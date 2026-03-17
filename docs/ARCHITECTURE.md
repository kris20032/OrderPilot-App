# CourierAssist — Architektura Techniczna v1

**Data:** 2026-03-17
**Status:** Zatwierdzona (aktualizacja 2026-03-17)

---

## Kontekst

POC udowodnił że pipeline działa (AccessibilityService → MediaProjection → OCR → Parser → Overlay). Teraz projektujemy produkcyjną architekturę od zera — czysta, modularna, przygotowana na v2 (Wolt/Glovo, billing, historia).

## Decyzje

- Jeden moduł Gradle `:app` z pakietami (nie multi-module)
- Manual DI (ServiceLocator) — zero zewnętrznych bibliotek DI
- Min SDK 26, Target SDK latest stable
- Kotlin, ViewBinding + XML, SharedPreferences
- Coroutines dla operacji async (screenshot, OCR)

---

## Struktura pakietów

```
com.courierassist.app/
├── di/           ← ServiceLocator, CourierAssistApp, AppLog (manual DI, logging)
├── domain/       ← modele danych (czysta logika, zero Androida)
├── engine/       ← OfferAnalyzer, OfferFilter
├── parser/       ← OcrOfferParser (OCR), OfferParser (accessibility tree), Uber/Wolt/Glovo/Bolt parsery, ParserRegistry
├── capture/      ← ScreenCaptureService, PopupCropper
├── ocr/          ← OcrEngine (ML Kit wrapper)
├── pipeline/     ← PipelineOrchestrator (łączy capture→ocr→parse→analyze→overlay)
├── service/      ← CourierAccessibilityService, EventThrottler, AccessibilityTextCollector
├── overlay/      ← OverlayManager, SystemOverlayManager (multi-slot), OverlayViewFactory, OverlayAutoHider
├── settings/     ← SettingsRepository, modele ustawień
├── billing/      ← FeatureGate (stub v1)
└── ui/           ← MainActivity, SettingsActivity, SetupActivity, LocaleHelper
```

---

## Warstwa: `domain/`

Czyste data classes i enums — zero zależności Android.

```kotlin
enum class Platform { UBER, WOLT, GLOVO, BOLT }

enum class ProfitLevel { GREEN, YELLOW, RED }

data class Offer(
    val platform: Platform,
    val amount: Double,          // kwota w lokalnej walucie
    val estimatedMinutes: Int,
    val distanceKm: Double?,     // nullable — nie zawsze dostępne
    val currency: String = "zł"
)

data class AnalysisResult(
    val offer: Offer,
    val zlPerHour: Double,
    val zlPerKm: Double?,        // null gdy brak dystansu
    val level: ProfitLevel
)

enum class MetricType { ZL_PER_HOUR, ZL_PER_KM, DISTANCE, TIME, AMOUNT }

enum class AppLanguage { PL, UK, EN }

enum class ThemeMode { AUTO, LIGHT, DARK }
```

---

## Warstwa: `settings/`

### Model ustawień

```kotlin
data class ThresholdConfig(
    val greenMinZlPerHour: Double = 40.0,
    val yellowMinZlPerHour: Double = 32.0,
    val greenMinZlPerKm: Double = 5.0,    // TBD — wartości domyślne do ustalenia
    val yellowMinZlPerKm: Double = 3.5
)

data class DisplayConfig(
    val visibleMetrics: Set<MetricType> = setOf(MetricType.ZL_PER_HOUR),
    val themeMode: ThemeMode = ThemeMode.AUTO
)

data class FilterConfig(
    val minDistanceKm: Double? = null,     // null = wyłączony
    val maxDistanceKm: Double? = null
)

data class PlatformSettings(
    val thresholds: ThresholdConfig? = null,  // null = użyj globalnych
    val filters: FilterConfig? = null
)

data class AppSettings(
    val language: AppLanguage = AppLanguage.PL,
    val display: DisplayConfig = DisplayConfig(),
    val globalThresholds: ThresholdConfig = ThresholdConfig(),
    val globalFilters: FilterConfig = FilterConfig(),
    val platformOverrides: Map<Platform, PlatformSettings> = emptyMap()
) {
    /** Zwraca progi dla danej platformy (override lub globalne) */
    fun thresholdsFor(platform: Platform): ThresholdConfig =
        platformOverrides[platform]?.thresholds ?: globalThresholds

    fun filtersFor(platform: Platform): FilterConfig =
        platformOverrides[platform]?.filters ?: globalFilters
}
```

### SettingsRepository

```kotlin
interface SettingsRepository {
    fun load(): AppSettings
    fun save(settings: AppSettings)
    fun addListener(listener: (AppSettings) -> Unit)
    fun removeListener(listener: (AppSettings) -> Unit)
}

class SharedPrefsSettingsRepository(context: Context) : SettingsRepository {
    // Serializacja AppSettings do SharedPreferences jako JSON string (Gson/kotlinx.serialization)
    // Listeners powiadamiane przy każdym save()
}
```

**Schemat SharedPreferences:** Jeden klucz `"app_settings"` z JSON stringiem całego `AppSettings`. Proste, atomowe, łatwe do debugowania.

---

## Warstwa: `engine/`

```kotlin
class OfferAnalyzer {
    fun analyze(offer: Offer, thresholds: ThresholdConfig): AnalysisResult {
        val zlPerHour = offer.amount / (offer.estimatedMinutes / 60.0)
        val zlPerKm = offer.distanceKm?.let { offer.amount / it }
        val level = when {
            zlPerHour >= thresholds.greenMinZlPerHour -> ProfitLevel.GREEN
            zlPerHour >= thresholds.yellowMinZlPerHour -> ProfitLevel.YELLOW
            else -> ProfitLevel.RED
        }
        return AnalysisResult(offer, zlPerHour, zlPerKm, level)
    }
}

class OfferFilter {
    /** Zwraca true jeśli oferta przechodzi filtry (dystans w zakresie) */
    fun passes(offer: Offer, filters: FilterConfig): Boolean {
        val dist = offer.distanceKm ?: return true  // brak dystansu = przepuść
        if (filters.minDistanceKm != null && dist < filters.minDistanceKm) return false
        if (filters.maxDistanceKm != null && dist > filters.maxDistanceKm) return false
        return true
    }
}
```

---

## Warstwa: `parser/`

### Dwa interfejsy parserów

Projekt ma dwie ścieżki parsowania — OCR (screenshot → ML Kit → tekst) i accessibility tree (bezpośredni odczyt UI). Każda ścieżka ma swój interfejs:

```kotlin
// Ścieżka 1: OCR — dla platform z React Native / WebView (Uber, Wolt)
interface OcrOfferParser {
    val platform: Platform
    val supportedPackages: Set<String>
    fun parse(ocrLines: List<String>, language: AppLanguage): Offer?
}

// Ścieżka 2: Accessibility tree — dla platform z natywnym UI (Glovo, Bolt)
interface OfferParser {
    val platform: Platform
    val supportedPackages: Set<String>
    fun canHandle(packageName: String): Boolean
    fun parse(rootNode: AccessibilityNodeInfo): Offer?
}
```

**Uwaga:** `GlovoOcrParser` i `BoltFoodOcrParser` implementują `OcrOfferParser` mimo że parsują tekst z accessibility tree (nie z OCR). Tekst jest zbierany przez `AccessibilityTextCollector` i przekazywany jako `ocrLines`. Jest to uproszczenie — oba interfejsy używają tego samego formatu wejścia (lista stringów).

### Implementacja — UberOcrParser

```kotlin
class UberOcrParser : OcrOfferParser {
    override val platform = Platform.UBER
    override val supportedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")

    // Regex per język
    private val regexSets = mapOf(
        AppLanguage.PL to RegexSet(
            amount = """(\d+[.,]\d+)\s*zł""".toRegex(),
            time = """(\d+)\s*min""".toRegex(),
            distance = """\((\d+[.,]\d+)\s*km\)""".toRegex()
        ),
        AppLanguage.UK to RegexSet(
            amount = """(\d+[.,]\d+)\s*грн""".toRegex(),
            time = """(\d+)\s*хв""".toRegex(),
            distance = """\((\d+[.,]\d+)\s*км\)""".toRegex()
        ),
        AppLanguage.EN to RegexSet(
            amount = """(\d+[.,]\d+)\s*(?:zł|PLN)""".toRegex(),
            time = """(\d+)\s*min""".toRegex(),
            distance = """\((\d+[.,]\d+)\s*km\)""".toRegex()
        )
    )

    override fun parse(ocrLines: List<String>, language: AppLanguage): Offer? {
        val regex = regexSets[language] ?: regexSets[AppLanguage.PL]!!
        val text = ocrLines.joinToString(" ")
        val amount = regex.amount.find(text)?.groupValues?.get(1)?.toDoubleLocale() ?: return null
        val minutes = regex.time.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: return null
        val distance = regex.distance.find(text)?.groupValues?.get(1)?.toDoubleLocale()
        return Offer(Platform.UBER, amount, minutes, distance)
    }

    private data class RegexSet(val amount: Regex, val time: Regex, val distance: Regex)
}
```

### Implementacja — WoltOcrParser

```kotlin
class WoltOcrParser : OcrOfferParser {
    override val platform = Platform.WOLT
    override val supportedPackages = setOf("com.wolt.courierapp")

    // Regex dla Wolta (PL/UK/EN)
    // Kwota: "13 zł" / "13,50 zł" / "13.50 zł"
    // Czas: "26 min"
    // Dystans: "2,7 km" / "2.7 km"

    override fun parse(ocrLines: List<String>, language: AppLanguage): Offer? { ... }
}
```

### ParserRegistry

```kotlin
class ParserRegistry(private val parsers: List<OcrOfferParser>) {
    fun getParser(packageName: String): OcrOfferParser? =
        parsers.firstOrNull { packageName in it.supportedPackages }

    fun getAllWatchedPackages(): Set<String> =
        parsers.flatMap { it.supportedPackages }.toSet()
}
```

Dodanie nowej platformy = nowa klasa implementująca `OcrOfferParser` + rejestracja w `ParserRegistry`.

Aktualne parsery: `UberOcrParser`, `WoltOcrParser`, `GlovoOcrParser`, `BoltFoodOcrParser`.

---

## Warstwa: `capture/`

### ScreenCaptureService

ForegroundService z typem `mediaProjection`. Singleton przez `instance`.

```kotlin
class ScreenCaptureService : Service() {
    companion object {
        @Volatile var instance: ScreenCaptureService? = null

        fun startCapture(context: Context, resultCode: Int, data: Intent) { ... }
        fun stopCapture(context: Context) { ... }
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    fun isReady(): Boolean = imageReader != null

    /** Robi screenshot — suspend, bo czekamy na ImageReader */
    suspend fun capture(): Bitmap? = withContext(Dispatchers.IO) {
        delay(200) // czekamy na renderowanie
        val image = imageReader?.acquireLatestImage() ?: return@withContext null
        // konwersja Image → Bitmap (z obsługą row padding)
        image.close()
        bitmap
    }
}
```

### PopupCropper

```kotlin
class PopupCropper(private val cropStartRatio: Float = 0.40f) {
    fun crop(fullScreenBitmap: Bitmap): Bitmap {
        val startY = (fullScreenBitmap.height * cropStartRatio).toInt()
        return Bitmap.createBitmap(fullScreenBitmap, 0, startY,
            fullScreenBitmap.width, fullScreenBitmap.height - startY)
    }
}
```

---

## Warstwa: `ocr/`

```kotlin
class OcrEngine {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognize(bitmap: Bitmap): List<String> = suspendCancellableCoroutine { cont ->
        recognizer.process(InputImage.fromBitmap(bitmap, 0))
            .addOnSuccessListener { result ->
                cont.resume(result.textBlocks.flatMap { it.lines }.map { it.text })
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

    fun close() = recognizer.close()
}
```

---

## Warstwa: `pipeline/`

**Nowy pakiet** — orkiestruje cały przepływ. Wyciąga logikę z AccessibilityService.

```kotlin
class PipelineOrchestrator(
    private val captureService: () -> ScreenCaptureService?,  // lazy — może nie istnieć
    private val popupCropper: PopupCropper,
    private val ocrEngine: OcrEngine,
    private val parserRegistry: ParserRegistry,
    private val offerAnalyzer: OfferAnalyzer,
    private val offerFilter: OfferFilter,
    private val overlayManager: OverlayManager,
    private val settingsRepository: SettingsRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun process(packageName: String) {
        scope.launch {
            val capture = captureService() ?: return@launch
            if (!capture.isReady()) return@launch

            val screenshot = capture.capture() ?: return@launch
            val cropped = popupCropper.crop(screenshot)
            screenshot.recycle()

            val ocrLines = ocrEngine.recognize(cropped)
            cropped.recycle()
            if (ocrLines.isEmpty()) return@launch

            val settings = settingsRepository.load()
            val parser = parserRegistry.getParser(packageName) ?: return@launch
            val offer = parser.parse(ocrLines, settings.language) ?: return@launch

            if (!offerFilter.passes(offer, settings.filtersFor(offer.platform))) return@launch

            val result = offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))

            withContext(Dispatchers.Main) {
                overlayManager.show(result, settings.display)
            }
        }
    }

    fun cancel() { scope.cancel() }
}
```

---

## Warstwa: `service/`

### EventThrottler

```kotlin
class EventThrottler(
    private val firstShotDelayMs: Long = 100L,
    private val cooldownMs: Long = 1500L
) {
    private var lastTriggerTime = 0L
    private var pendingJob: Job? = null

    fun onEvent(scope: CoroutineScope, action: () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastTriggerTime < cooldownMs) return  // w cooldownie
        if (pendingJob?.isActive == true) return          // już czekamy

        pendingJob = scope.launch {
            delay(firstShotDelayMs)
            lastTriggerTime = System.currentTimeMillis()
            action()
        }
    }
}
```

### CourierAccessibilityService

```kotlin
class CourierAccessibilityService : AccessibilityService() {
    private lateinit var pipeline: PipelineOrchestrator
    private lateinit var throttler: EventThrottler
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Pakiety do nasłuchiwania — z ParserRegistry
    private val watchedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")

    override fun onServiceConnected() {
        val sl = ServiceLocator.instance
        pipeline = sl.pipelineOrchestrator
        throttler = EventThrottler()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val pkg = event?.packageName?.toString() ?: return
        if (pkg !in watchedPackages) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        throttler.onEvent(scope) {
            pipeline.process(pkg)
        }
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
```

### OverlayAutoHider (auto-hide belki — per platforma)

```kotlin
class OverlayAutoHider(
    private val overlayManager: OverlayManager,
    private val onHidden: () -> Unit = {}
) {
    private val hideJobs = mutableMapOf<Platform, Job>()

    fun onOverlayShown(scope: CoroutineScope, hideDelayMs: Long = 15_000L, platform: Platform) {
        hideJobs[platform]?.cancel()
        hideJobs[platform] = scope.launch {
            delay(hideDelayMs)
            withContext(Dispatchers.Main) { overlayManager.hideByPlatform(platform) }
            hideJobs.remove(platform)
            onHidden()
        }
    }

    fun hideNow(scope: CoroutineScope) {
        hideJobs.values.forEach { it.cancel() }
        hideJobs.clear()
        scope.launch(Dispatchers.Main) {
            overlayManager.hide()
            onHidden()
        }
    }
}
```

---

## Warstwa: `overlay/`

### OverlayManager

```kotlin
interface OverlayManager {
    fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage)
    fun hide()
    fun hideByPlatform(platform: Platform)
    fun isShowing(): Boolean
    fun overlayCount(): Int
}
```

### SystemOverlayManager (multi-slot: max 2 belki)

```kotlin
class SystemOverlayManager(private val context: Context) : OverlayManager {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private data class OverlaySlot(
        val view: View,
        val platform: Platform,
        val result: AnalysisResult,
        val displayConfig: DisplayConfig,
        val language: AppLanguage,
        val createdAt: Long
    )

    private val slots = mutableListOf<OverlaySlot>()  // max 2

    override fun show(result: AnalysisResult, displayConfig: DisplayConfig, language: AppLanguage) {
        val platform = result.offer.platform
        // Ta sama platforma → zastąp belkę
        // 1 belka innej platformy → dodaj drugą z etykietami
        // 2 belki → usuń najstarszą, dodaj nową na górze
        // 0 belek → dodaj normalnie
    }

    override fun hideByPlatform(platform: Platform) {
        // Usuwa belkę danej platformy, przebudowuje pozostałą (bez etykiety)
    }

    override fun hide() { /* Usuwa wszystkie belki */ }
    override fun isShowing() = slots.isNotEmpty()
    override fun overlayCount() = slots.size
}
```

**Pozycjonowanie:** Każda belka to osobne okno WindowManager. Górna: y=48dp, dolna: y=48dp+60dp+4dp.
**Etykieta platformy:** Widoczna tylko gdy 2 belki naraz (UBER/WOLT/GLOVO/BOLT prepended do tekstu).
```

### OverlayViewFactory

```kotlin
object OverlayViewFactory {
    fun create(context: Context, result: AnalysisResult, config: DisplayConfig,
               language: AppLanguage, platformLabel: String? = null): View {
        // Buduj tekst na podstawie wybranych metryk + wielojęzyczność
        val parts = mutableListOf<String>()
        // ... metryki z non-breaking spaces (zł/h, zł/km, kwota, czas, dystans)

        // Etykieta platformy — widoczna tylko gdy 2 belki naraz
        if (platformLabel != null) parts.add(0, platformLabel)

        // Partial offer → "↓" zachęca do scrollu
        if (result.offer.isPartial) parts += "↓"

        textView.text = parts.joinToString(" | ")

        // Kolor tła z konfigurowalaną przezroczystością
        val alpha = (config.overlayOpacity / 100f * 255).toInt()
        val bgColor = when (result.level) {
            ProfitLevel.GREEN  -> Color.argb(alpha, 0x4C, 0xAF, 0x50)
            ProfitLevel.YELLOW -> Color.argb(alpha, 0xFF, 0x98, 0x00)
            ProfitLevel.RED    -> Color.argb(alpha, 0xF4, 0x43, 0x36)
        }
    }
}
```

---

## Warstwa: `di/`

```kotlin
object ServiceLocator {
    lateinit var instance: ServiceLocator private set

    lateinit var settingsRepository: SettingsRepository
    lateinit var ocrEngine: OcrEngine
    lateinit var parserRegistry: ParserRegistry
    lateinit var offerAnalyzer: OfferAnalyzer
    lateinit var offerFilter: OfferFilter
    lateinit var overlayManager: OverlayManager
    lateinit var overlayAutoHider: OverlayAutoHider
    lateinit var popupCropper: PopupCropper
    lateinit var pipelineOrchestrator: PipelineOrchestrator

    fun init(context: Context) {
        instance = this
        settingsRepository = SharedPrefsSettingsRepository(context)
        ocrEngine = OcrEngine()
        parserRegistry = ParserRegistry(listOf(UberOcrParser(), WoltOcrParser(), GlovoOcrParser(), BoltFoodOcrParser()))
        offerAnalyzer = OfferAnalyzer()
        offerFilter = OfferFilter()
        overlayManager = SystemOverlayManager(context)
        overlayAutoHider = OverlayAutoHider(overlayManager)
        popupCropper = PopupCropper()
        pipelineOrchestrator = PipelineOrchestrator(
            captureService = { ScreenCaptureService.instance },
            popupCropper = popupCropper,
            ocrEngine = ocrEngine,
            parserRegistry = parserRegistry,
            offerAnalyzer = offerAnalyzer,
            offerFilter = offerFilter,
            overlayManager = overlayManager,
            settingsRepository = settingsRepository
        )
    }
}
```

### CourierAssistApp (Application)

```kotlin
class CourierAssistApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(applicationContext)
    }
}
```

---

## Warstwa: `billing/`

Stub na v2.

```kotlin
object FeatureGate {
    fun isPro(): Boolean = true  // v1: wszystko odblokowane

    fun canUsePlatform(platform: Platform): Boolean = when (platform) {
        Platform.UBER -> true
        else -> isPro()
    }
}
```

---

## Warstwa: `ui/`

### MainActivity

- Duży przycisk START/STOP (toggle nasłuchiwania)
- 3 wskaźniki uprawnień:
  - Accessibility Service: sprawdza `AccessibilityManager.getEnabledAccessibilityServiceList()`
  - MediaProjection: sprawdza `ScreenCaptureService.instance?.isReady()`
  - Overlay: sprawdza `Settings.canDrawOverlays()`
- Gear icon → `SettingsActivity`
- Pole tekstowe na info/porady
- Przy START: jeśli brak uprawnień → prowadzi użytkownika krok po kroku

### SettingsActivity

- Sekcja: Progi zł/h (green, yellow) — sliders lub input fields
- Sekcja: Progi zł/km
- Sekcja: Co wyświetlać na belce — checkboxy (MetricType)
- Sekcja: Filtr dystansu (min/max) — opcjonalny
- Sekcja: Tryb ciemny/jasny — radio (Auto/Light/Dark)
- Sekcja: Język — radio (PL/UK/EN)
- Sekcja: Per platforma — expandable, override globalnych

---

## Diagram przepływu danych

### Ścieżka 1: Accessibility Tree (Glovo, Bolt)
```
[Popup Glovo/Bolt] ─── AccessibilityEvent ───→ [CourierAccessibilityService]
                                                         │
                                                  EventThrottler (per platforma)
                                                  (100ms delay, 1.5s cooldown)
                                                         │
                                                         ▼
                                              processViaAccessibilityTree()
                                                         │
                                              getRootInActiveWindow()
                                              AccessibilityTextCollector.collectText()
                                                         │
                                                         ▼
                                        [ParserRegistry → GlovoOcrParser / BoltFoodOcrParser]
                                               parse(lines) → Offer?
                                                         │
                                              OfferFilter → OfferAnalyzer
                                                         │
                                                         ▼
                                              [SystemOverlayManager] (max 2 belki)
                                              [OverlayAutoHider] (timer per platforma)
```

### Ścieżka 2: MediaProjection OCR (Uber, Wolt)
```
[Popup Uber/Wolt] ─── AccessibilityEvent ───→ [CourierAccessibilityService]
                                                         │
                                                  EventThrottler (per platforma)
                                                         │
                                                         ▼
                                              [PipelineOrchestrator.process()]
                                                         │
                                    ScreenCaptureService → PopupCropper → OcrEngine
                                                         │
                                                         ▼
                                        [ParserRegistry → UberOcrParser / WoltOcrParser]
                                               parse(lines) → Offer?
                                                         │
                                              OfferFilter → OfferAnalyzer
                                                         │
                                                         ▼
                                              [SystemOverlayManager] (max 2 belki)
                                              [OverlayAutoHider] (timer per platforma)
```

### Ścieżka 3: takeScreenshot fallback (API 30+, gdy MediaProjection niedostępna)
```
AccessibilityService.takeScreenshot() → crop dolne 60% → OcrEngine → Parser → Overlay
```

---

## Model wątków

| Operacja | Wątek | Dlaczego |
|----------|-------|----------|
| AccessibilityEvent callback | Main | Android wymaga |
| EventThrottler.delay() | Coroutine (Default) | Nie blokujemy main |
| ScreenCaptureService.capture() | Coroutine (IO) | Czekamy na ImageReader |
| PopupCropper.crop() | Coroutine (Default) | CPU-bound, szybkie |
| OcrEngine.recognize() | ML Kit thread pool | ML Kit zarządza |
| OfferAnalyzer + Filter | Coroutine (Default) | Czysta logika |
| OverlayManager.show() | Main | WindowManager wymaga |
| Settings load/save | Main (SharedPrefs) | Synchroniczne, szybkie |

---

## Obsługa błędów

| Awaria | Reakcja |
|--------|---------|
| ScreenCaptureService nie gotowy | `pipeline.process()` zwraca bez akcji |
| Screenshot null | return — czekamy na następny event |
| OCR zwraca pustą listę | return |
| Parser zwraca null | return — nie rozpoznaliśmy formatu |
| Oferta odfiltrowana | return — nie pokazujemy belki |
| MediaProjection utracony | Callback → ustawiamy isReady=false, MainActivity przy kolejnym START poprosi o ponowną zgodę |
| Overlay permission cofnięty | MainActivity wykrywa przy resume, pokazuje przycisk do ustawień |

Brak crashy — każdy krok pipeline'u jest null-safe i wrapped w coroutine try/catch.

---

## Uprawnienia — flow użytkownika

```
MainActivity.onCreate()
  ├── Sprawdź Overlay permission → ✗ → "Włącz rysowanie nad aplikacjami" → Settings
  ├── Sprawdź Accessibility Service → ✗ → "Włącz usługę dostępności" → Settings
  └── Sprawdź MediaProjection → ✗ → "Pozwól nagrywać ekran" → system dialog

Wszystkie ✓ → przycisk START aktywny
START → zapisuje isEnabled=true → AccessibilityService zaczyna nasłuchiwać
STOP → isEnabled=false → AccessibilityService ignoruje eventy, belka hide
```

---

## Pliki do stworzenia (nowa aplikacja)

### Kotlin (~27 plików)

| Pakiet | Plik | Opis |
|--------|------|------|
| `di` | `ServiceLocator.kt` | Manual DI — inicjalizacja wszystkich zależności |
| `di` | `CourierAssistApp.kt` | Application class — wywołuje ServiceLocator.init() |
| `di` | `AppLog.kt` | Ring buffer logger (500 wpisów), zapis do Downloads |
| `domain` | `Platform.kt` | Enum platform |
| `domain` | `ProfitLevel.kt` | Enum GREEN/YELLOW/RED |
| `domain` | `Offer.kt` | Data class oferty |
| `domain` | `AnalysisResult.kt` | Wynik analizy |
| `domain` | `MetricType.kt` | Enum metryk do wyświetlania |
| `domain` | `AppLanguage.kt` | Enum języków |
| `domain` | `ThemeMode.kt` | Enum trybu ciemnego |
| `engine` | `OfferAnalyzer.kt` | Oblicza zł/h, zł/km, przypisuje ProfitLevel |
| `engine` | `OfferFilter.kt` | Filtruje oferty po dystansie |
| `parser` | `OcrOfferParser.kt` | Interfejs parsera OCR (Uber, Wolt) |
| `parser` | `OfferParser.kt` | Interfejs parsera accessibility tree |
| `parser` | `UberOcrParser.kt` | Parser OCR dla Uber |
| `parser` | `UberParser.kt` | Parser accessibility tree dla Uber (fallback) |
| `parser` | `WoltOcrParser.kt` | Parser OCR dla Wolt |
| `parser` | `GlovoOcrParser.kt` | Parser Glovo (accessibility tree → ocrLines) |
| `parser` | `BoltFoodOcrParser.kt` | Parser Bolt Food (accessibility tree → ocrLines) |
| `parser` | `ParserRegistry.kt` | Rejestr parserów — dispatch po packageName |
| `capture` | `ScreenCaptureService.kt` | ForegroundService MediaProjection |
| `capture` | `PopupCropper.kt` | Przycina bitmapę do regionu popupu |
| `ocr` | `OcrEngine.kt` | Wrapper ML Kit OCR |
| `pipeline` | `PipelineOrchestrator.kt` | Orkiestracja pipeline'u |
| `service` | `CourierAccessibilityService.kt` | AccessibilityService — nasłuchuje eventy |
| `service` | `EventThrottler.kt` | Throttling eventów (delay + cooldown, per platforma) |
| `service` | `AccessibilityTextCollector.kt` | Zbiera tekst z accessibility tree (traversal) |
| `overlay` | `OverlayManager.kt` | Interfejs + SystemOverlayManager |
| `overlay` | `OverlayViewFactory.kt` | Tworzy widok belki |
| `overlay` | `OverlayAutoHider.kt` | Auto-ukrywanie belki po timeout |
| `settings` | `AppSettings.kt` | Modele ustawień (ThresholdConfig, DisplayConfig, itd.) |
| `settings` | `SettingsRepository.kt` | Interfejs + SharedPrefsSettingsRepository |
| `billing` | `FeatureGate.kt` | Stub — v1 all unlocked |
| `ui` | `MainActivity.kt` | Ekran główny — START/STOP, uprawnienia |
| `ui` | `SettingsActivity.kt` | Ekran ustawień (tabs per platforma) |
| `ui` | `SetupActivity.kt` | Wizard uprawnień (overlay, accessibility, bateria, Samsung) |
| `ui` | `LocaleHelper.kt` | Helper do zmiany locale per AppLanguage |

### XML Resources

| Plik | Opis |
|------|------|
| `res/layout/activity_main.xml` | Layout główny — przycisk, statusy, gear |
| `res/layout/activity_settings.xml` | Layout ustawień |
| `res/layout/activity_setup.xml` | Layout wizarda uprawnień |
| `res/layout/overlay_offer.xml` | Layout belki overlay |
| `res/xml/accessibility_config.xml` | Konfiguracja AccessibilityService |
| `res/values/strings.xml` | Stringi PL (domyślne) |
| `res/values-uk/strings.xml` | Stringi UK |
| `res/values-en/strings.xml` | Stringi EN |
| `res/values/colors.xml` | Kolory (GREEN/YELLOW/RED + UI) |
| `res/values/themes.xml` | Tryb jasny |
| `res/values-night/themes.xml` | Tryb ciemny |

### Gradle / Manifest

| Plik | Zmiany |
|------|--------|
| `app/build.gradle.kts` | minSdk 26, dependencies (ML Kit, kotlinx-coroutines, kotlinx-serialization) |
| `AndroidManifest.xml` | Uprawnienia, services, activities, Application class |

---

## Weryfikacja

1. **Unit testy** (bez Androida):
   - `OfferAnalyzer` — progi, edge cases (0 minut, brak dystansu)
   - `OfferFilter` — filtr dystansu
   - `UberOcrParser` — regex PL/UK/EN, edge cases
   - `WoltOcrParser` — regex kwot, dystansów, godzin
   - `GlovoOcrParser` — partial offers, gotówka, sumowanie dystansów
   - `AppSettings.thresholdsFor()` — globalne vs override
   - `EventThrottler` — cooldown logic

2. **Testy na urządzeniu** (manualne, z FakeUberDriver):
   - Pipeline end-to-end: FakeUberDriver popup → belka pojawia się <1s
   - Belka znika po ~15s
   - Zmiana ustawień (progi, metryki) → następna belka odzwierciedla
   - START/STOP działa
   - Wskaźniki uprawnień poprawne
   - Dark/light mode belki

---

## Plan implementacji (kolejność)

1. **Projekt bazowy** — Gradle, Manifest, Application, ServiceLocator (Sonnet)
2. **Domain** — wszystkie modele danych (Sonnet)
3. **Settings** — AppSettings, SettingsRepository, SharedPrefs (Sonnet)
4. **Engine** — OfferAnalyzer, OfferFilter + unit testy (Sonnet)
5. **Parser** — OcrOfferParser, UberOcrParser, ParserRegistry + unit testy (Opus)
6. **Capture** — ScreenCaptureService, PopupCropper (Opus)
7. **OCR** — OcrEngine wrapper (Sonnet)
8. **Pipeline** — PipelineOrchestrator (Opus)
9. **Overlay** — SystemOverlayManager, OverlayViewFactory, OverlayAutoHider (Sonnet)
10. **Service** — CourierAccessibilityService, EventThrottler (Opus)
11. **UI — MainActivity** — uprawnienia, START/STOP (Sonnet)
12. **UI — SettingsActivity** (Sonnet)
13. **Integracja + testy E2E** — FakeUberDriver → pełny pipeline (Opus)
