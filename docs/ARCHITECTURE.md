# Architektura Projektu CourierAssist

## Podział na warstwy

```
com.courierassist.app
│
├── ui/
├── service/
├── capture/                  ← NOWE (Screenshot + Crop)
├── ocr/                      ← NOWE (ML Kit wrapper)
├── parser/
│     ├── UberOcrParser         ← NOWE (regex na OCR output)
│     ├── UberParser            ← STARY (deprecated, zachowany)
│     ├── WoltParser
│     └── GlovoParser
├── domain/
├── engine/
├── overlay/
├── settings/
└── billing/
```

Separacja odpowiedzialności = łatwe dodanie Wolta i Glovo.

---

## 1. WARSTWA DOMAIN (czysta logika biznesowa)

Tu nie ma Androida. Sama matematyka.

### Offer.kt
```kotlin
data class Offer(
    val platform: Platform,
    val amount: Double,
    val estimatedMinutes: Int,
    val distanceKm: Double? = null   // opcjonalne, z OCR
)
```

### Platform.kt
```kotlin
enum class Platform {
    UBER,
    WOLT,
    GLOVO
}
```

### AnalysisResult.kt
```kotlin
data class AnalysisResult(
    val zlPerHour: Double,
    val level: ProfitLevel
)
```

### ProfitLevel.kt
```kotlin
enum class ProfitLevel {
    GREEN,
    YELLOW,
    RED
}
```

---

## 2. WARSTWA SETTINGS

Progi konfigurowalne, ale z defaultami.

### ThresholdConfig.kt
```kotlin
data class ThresholdConfig(
    val greenMin: Double = 40.0,
    val yellowMin: Double = 32.0
)
```

### SettingsRepository.kt (interface)
```kotlin
interface SettingsRepository {
    fun getThresholds(): ThresholdConfig
}
```

### SharedPrefsSettingsRepository.kt
Implementacja przez SharedPreferences.

---

## 3. SILNIK ANALITYCZNY (ENGINE)

### OfferAnalyzer.kt
```kotlin
class OfferAnalyzer(
    private val settingsRepository: SettingsRepository
) {
    fun analyze(offer: Offer): AnalysisResult {
        val thresholds = settingsRepository.getThresholds()
        val hours = offer.estimatedMinutes / 60.0
        val zlPerHour = offer.amount / hours
        val level = when {
            zlPerHour >= thresholds.greenMin -> ProfitLevel.GREEN
            zlPerHour >= thresholds.yellowMin -> ProfitLevel.YELLOW
            else -> ProfitLevel.RED
        }
        return AnalysisResult(zlPerHour, level)
    }
}
```

Czyste. Testowalne. Bez Androida.

---

## 4. PARSERY (klucz do skalowania)

Każda platforma ma swój parser.

### OfferParser.kt (interface)
```kotlin
interface OfferParser {
    fun canHandle(packageName: String): Boolean
    fun parse(rootNode: AccessibilityNodeInfo): Offer?
}
```

### UberOcrParser.kt (aktywny — OCR pipeline)
Parsuje tekst zwrócony przez ML Kit. Szuka:
- kwoty: `(\d+[.,]\d+)\s*zł` → `14.64`
- czasu: `(\d+)\s*min` → `26`
- dystansu: `\((\d+[.,]\d+)\s*km\)` → `5.6`
- przycisku accept: `akceptuj` / `accept` / `прийняти`

Zwraca `Offer` albo `null` (jeśli brak kwoty/czasu lub przycisku accept).

### UberParser.kt (deprecated — parsowanie drzewa UI)
Zachowany w kodzie, ale nie używany. Uber Driver nie eksponuje tekstu
przez `AccessibilityNodeInfo` (Canvas/Compose rendering) — dlatego przeszliśmy na OCR.

### WoltParser.kt (ETAP 2)
### GlovoParser.kt (ETAP 3)

### ParserRegistry.kt
```kotlin
class ParserRegistry(
    private val parsers: List<OfferParser>
) {
    fun getParser(packageName: String): OfferParser? {
        return parsers.firstOrNull { it.canHandle(packageName) }
    }
}
```

Dodanie nowej platformy = dopisanie klasy i rejestracja.

---

## 4b. CAPTURE LAYER (Screenshot + Crop)

### ScreenCaptureManager.kt
Robi screenshot całego ekranu na żądanie przez `AccessibilityService.takeScreenshot()`.
- Wymaga API 30 (Android 11)
- Konwertuje hardware bitmap → software bitmap (ARGB_8888) wymagany przez ML Kit
- Throttle 1s — nie robi screenshota częściej niż raz na sekundę

### PopupCropper.kt
Wycina dolne 60% screenshota — tam gdzie pojawia się popup oferty Uber.
- `CROP_START_RATIO = 0.40` (start od 40% wysokości ekranu)
- Statyczny crop — wystarczy dla typowego layout popupu Uber
- Przyszłościowo: dynamiczne wykrywanie krawędzi

---

## 4c. OCR ENGINE (ML Kit)

### OcrEngine.kt
Wrapper na Google ML Kit Text Recognition (on-device, Latin script).
- Bundled w APK (`com.google.mlkit:text-recognition:16.0.1`) — ~5 MB, działa offline
- Asynchroniczny, czas ~100–200ms na typowym telefonie
- Zwraca `List<String>` (linie tekstu z wszystkich bloków)
- Loguje raw OCR output do Logcat: `OCR lines: [...]`

---

## 5. ACCESSIBILITY SERVICE (rdzeń aplikacji)

### CourierAccessibilityService.kt

Odpowiedzialność:
- nasłuchiwanie zmian UI (tylko pakiet `com.ubercab.driver`)
- triggerowanie pipeline'u OCR przy każdym `TYPE_WINDOW_STATE_CHANGED`
- debounce 500ms (screenshot + OCR trwa ~200–300ms)
- pokazanie/chowanie overlay

**Aktualny pipeline (OCR):**
```
AccessibilityEvent (Uber, TYPE_WINDOW_STATE_CHANGED)
    ↓ debounce 500ms
ScreenCaptureManager.capture()       ← AccessibilityService.takeScreenshot()
    ↓ Bitmap (ARGB_8888)
PopupCropper.crop(bitmap)             ← dolne 60% ekranu
    ↓ Bitmap (popup)
OcrEngine.recognize(croppedBitmap)    ← ML Kit Text Recognition (on-device, Latin)
    ↓ List<String>
UberOcrParser.parse(lines)            ← regex: kwota / czas / dystans / accept
    ↓ Offer?
OfferAnalyzer.analyze(offer)          ← bez zmian
    ↓ AnalysisResult
SystemOverlayManager.show(...)        ← belka: "34 zł/h | 14.64 zł | 26 min | 5.6 km"
```

**Wymagania API:**
- `takeScreenshot()` — API 30+ (Android 11), minSdk=30
- `@RequiresApi(Build.VERSION_CODES.R)` na klasie serwisu
- `canTakeScreenshot="true"` w accessibility_config.xml (wymagane od API 33)

---

## 6. OVERLAY SYSTEM

Oddzielony całkowicie od serwisu.

### OverlayManager.kt (interface)
```kotlin
interface OverlayManager {
    fun show(result: AnalysisResult, offer: Offer)
    fun hide()
}
```

### SystemOverlayManager.kt
Używa:
- `TYPE_APPLICATION_OVERLAY`
- `FLAG_NOT_TOUCHABLE`
- `FLAG_NOT_FOCUSABLE`

**Wygląd:**
- Pozycja: góra ekranu
- Tło: kolor zależny od ProfitLevel (zielony/żółty/czerwony, 80% opacity)
- Kliknięcia przechodzą na aplikację pod spodem
- Format: `★ 42 zł/h | 14.64 zł | 26 min | 5.6 km` (dystans opcjonalny — jeśli OCR wykrył)

**Logika znikania (kiedy `hide()`):**
- Przyciski „Akceptuj / Odrzuć" znikną z ekranu
- Wykryto zmianę ekranu (inny widok w aplikacji kurierskiej)
- Aplikacja kurierska przeszła do innego widoku (np. mapa nawigacji)
- Debounce 300ms

### OverlayViewFactory.kt
Tworzy widok zależnie od `ProfitLevel`.

---

## 7. DETEKCJA „AKTYWNEJ OFERTY"

### OfferVisibilityDetector.kt

Sprawdza:
- czy są przyciski „Accept" / „Reject"
- czy widoczny jest layout oferty

Zwraca `Boolean`. Parser działa tylko jeśli `true`.

---

## 8. UI (USTAWIENIA)

### MainActivity.kt
- włącznik statusu
- link do Accessibility
- podgląd progów
- przyszłościowo: billing

### SettingsActivity.kt
- edycja progów
- przezroczystość overlay
- opcja włączenia/wyłączenia platform

---

## 9. PRZYGOTOWANIE POD SUBSKRYPCJĘ

Na razie billing wyłączony, ale architektura gotowa.

### FeatureGate.kt (interface)
```kotlin
interface FeatureGate {
    fun isPro(): Boolean
}
```

W darmowej wersji:
- tylko Uber
- brak statystyk

W Pro:
- Wolt
- Glovo
- custom presety
- przyszłościowo historia

### BillingManager.kt (Google Play Billing v6)
Na razie stub.

---

## Dlaczego ta architektura jest dobra?

- parsery są niezależne
- logika biznesowa testowalna
- łatwe dodanie nowych platform
- overlay oddzielony
- przygotowane pod subskrypcję
- minimalne zużycie RAM
- działa na Android 8+
