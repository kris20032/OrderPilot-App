# Plan: Screenshot + OCR on-device (zamiennik parsowania drzewa UI)

**Data:** 2026-02-26
**Problem:** Popup zlecenia Uber Driver nie eksponuje tekstu przez AccessibilityNodeInfo. Drzewo UI jest puste lub nieczytelne — Uber prawdopodobnie używa custom renderowania (Canvas/Compose).
**Rozwiązanie:** Zrobić screenshot ekranu → wyciąć obszar popupu → rozpoznać tekst (OCR) → sparsować regex-ami.

---

## Dlaczego ta droga?

| Podejście | Zalety | Wady |
|-----------|--------|------|
| **AccessibilityService + parsowanie drzewa UI** | Szybkie, lekkie | ❌ Uber nie eksponuje tekstu |
| **MediaProjection + OCR** | Działa na API 26+ | Wymaga osobnej zgody użytkownika, ciągłe nagrywanie ekranu |
| **AccessibilityService.takeScreenshot() + OCR** | ✅ Najlepsze: screenshot na żądanie, brak nagrywania, jedno API | Wymaga API 30+ (Android 11) |

**Decyzja:** `AccessibilityService.takeScreenshot()` + **Google ML Kit Text Recognition (on-device)**

### Dlaczego ML Kit?
- **Darmowy**, on-device, zero internetu
- **Szybki:** 50–200ms na typowym telefonie
- **Mały model:** ~5 MB (bundled z APK) lub ~2 MB (download on demand)
- **Świetna obsługa łacińskich znaków** — polski (ź, ó, ą, ł), cyfry, symbole walut
- **Oficjalnie wspierany przez Google** — stabilne API, regularne aktualizacje
- Rozpoznaje tekst z screenshotów bez pre-processingu (wysoki kontrast popupu Uber to idealne warunki)

### MinSdk: 26 → 30?
`AccessibilityService.takeScreenshot()` wymaga **API 30** (Android 11). Opcje:
1. **Podnieść minSdk do 30** — upraszcza kod, eliminuje fallback. ~95% aktywnych telefonów Androida powyżej API 30.
2. **Zostawić minSdk 26 + MediaProjection fallback** — więcej kodu, ale większa kompatybilność.

**Rekomendacja:** Podnieść minSdk do 30. Kurier używający Uber Driver w 2026 roku prawie na pewno ma Android 11+. Jeśli telefon Taty ma starszego Androida — dodamy fallback MediaProjection jako osobne zadanie.

---

## Architektura nowego pipeline'u

```
AccessibilityEvent (TYPE_WINDOW_STATE_CHANGED)
    │
    ▼
CourierAccessibilityService
    │  wykrywa pakiet: com.ubercab.driver
    │  wykrywa typ okna: popup zlecenia
    │
    ▼
ScreenCaptureManager.capture()          ← NOWY
    │  AccessibilityService.takeScreenshot()
    │  zwraca: Bitmap
    │
    ▼
PopupCropper.crop(bitmap)               ← NOWY
    │  wycina dolną ~55% ekranu (popup z ofertą)
    │  zwraca: Bitmap (sam popup)
    │
    ▼
OcrEngine.recognize(croppedBitmap)       ← NOWY
    │  ML Kit TextRecognizer (on-device, Latin)
    │  zwraca: List<String> (rozpoznane linie tekstu)
    │
    ▼
UberOcrParser.parse(lines)              ← NOWY (zamiast obecnego UberParser)
    │  regex: kwota, czas, dystans, adres pickup/dropoff
    │  zwraca: Offer?
    │
    ▼
OfferAnalyzer.analyze(offer)            ← BEZ ZMIAN
    │
    ▼
SystemOverlayManager.show(result, offer) ← BEZ ZMIAN
```

### Co się zmienia vs. obecna architektura

| Komponent | Teraz | Po zmianach |
|-----------|-------|-------------|
| Trigger parsowania | Odczyt drzewa UI z AccessibilityNodeInfo | Screenshot + OCR |
| UberParser | Zbiera tekst z AccessibilityNodeInfo | UberOcrParser: parsuje tekst z OCR |
| OfferParser interface | `parse(rootNode: AccessibilityNodeInfo)` | `parse(ocrLines: List<String>)` lub nowy interfejs `OcrOfferParser` |
| Nowe zależności | brak | Google ML Kit Text Recognition |
| Screenshot | brak | ScreenCaptureManager |

---

## Szczegółowy plan zadań

### ETAP A: Przygotowanie (1 sesja)

#### TASK A.1 — Podnieść minSdk do 30
- `build.gradle.kts`: `minSdk = 30`
- Upewnić się, że build przechodzi
- **Trudność:** 1/5

#### TASK A.2 — Dodać zależność ML Kit Text Recognition
- `build.gradle.kts`:
  ```kotlin
  implementation("com.google.mlkit:text-recognition:16.0.1")
  ```
  Wersja bundled (model w APK, zero downloadu w runtime):
  ```kotlin
  implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")
  ```
- **Rekomendacja:** Użyć wersji bundled (`com.google.mlkit:text-recognition`) — działa offline od razu po instalacji, ważne dla kuriera w trasie.
- **Trudność:** 1/5

#### TASK A.3 — Dodać flagę `canTakeScreenshot` do accessibility_config.xml
- `accessibility_config.xml`: dodać `android:canTakeScreenshot="true"` (wymagane od API 33)
- **Trudność:** 1/5

---

### ETAP B: Screenshot (1 sesja)

#### TASK B.1 — ScreenCaptureManager
**Plik:** `capture/ScreenCaptureManager.kt`

```kotlin
class ScreenCaptureManager(private val service: AccessibilityService) {

    fun capture(callback: (Bitmap?) -> Unit) {
        service.takeScreenshot(
            Display.DEFAULT_DISPLAY,
            service.mainExecutor,
            object : AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(result: AccessibilityService.ScreenshotResult) {
                    val hwBitmap = Bitmap.wrapHardwareBuffer(
                        result.hardwareBuffer, result.colorSpace
                    )
                    val swBitmap = hwBitmap?.copy(Bitmap.Config.ARGB_8888, false)
                    result.hardwareBuffer.close()
                    callback(swBitmap)
                }
                override fun onFailure(errorCode: Int) {
                    callback(null)
                }
            }
        )
    }
}
```

- Robienie screenshot na żądanie, nie ciągle
- Zwraca Bitmap w software mode (wymagane przez ML Kit)
- **Trudność:** 2/5

#### TASK B.2 — PopupCropper
**Plik:** `capture/PopupCropper.kt`

Na podstawie załączonego screenshota — popup zajmuje dolną ~55% ekranu. Popup ma ciemne tło z zaokrąglonymi rogami, białym tekstem.

```kotlin
class PopupCropper {
    fun crop(fullScreenBitmap: Bitmap): Bitmap {
        val startY = (fullScreenBitmap.height * 0.40).toInt()
        val height = fullScreenBitmap.height - startY
        return Bitmap.createBitmap(
            fullScreenBitmap, 0, startY,
            fullScreenBitmap.width, height
        )
    }
}
```

**Opcja zaawansowana (v2):** Wykrywanie krawędzi popupu dynamicznie (szukanie ciemnego prostokąta z zaokrąglonymi rogami). Na start — stały procent wystarczy.

- **Trudność:** 1/5

---

### ETAP C: OCR Engine (1 sesja)

#### TASK C.1 — OcrEngine
**Plik:** `ocr/OcrEngine.kt`

```kotlin
class OcrEngine {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun recognize(bitmap: Bitmap, callback: (List<String>) -> Unit) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val lines = visionText.textBlocks.flatMap { block ->
                    block.lines.map { it.text }
                }
                callback(lines)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
```

- ML Kit Latin script — obsługuje PL, EN, UKR (łaciński alfabet)
- Zero konfiguracji — działa od razu
- Callback-based (asynchroniczne), ale bardzo szybkie (~100ms)
- **Trudność:** 2/5

---

### ETAP D: Nowy parser OCR (1 sesja)

#### TASK D.1 — UberOcrParser
**Plik:** `parser/UberOcrParser.kt`

Parsuje tekst z OCR zamiast z drzewa UI. Logika regex zostaje prawie identyczna jak w obecnym `UberParser`.

Na podstawie załączonego screenshota, OCR zwróci linie typu:
```
"Dostawa"
"14,64 zł"
"Łącznie 26 min (5.6 km)"
"Panie Janie Pizza Morena"
"Dąbrówki & Królowej Jadwigi, Gdańsk"
"Akceptuj"
```

Regexy do wyciągnięcia:
- **Kwota:** `(\d+[.,]\d+)\s*zł` → 14.64
- **Czas:** `(\d+)\s*min` → 26
- **Dystans:** `\((\d+[.,]\d+)\s*km\)` → 5.6 *(nowe pole — bonus)*
- **Pickup:** linia po ikonie (•) lub linia nad adresem dostawy
- **Dropoff:** linia z adresem (kończy się na nazwie miasta)
- **Przycisk Accept:** `"Akceptuj"` / `"Accept"` / `"Прийняти"`

```kotlin
class UberOcrParser {
    fun parse(ocrLines: List<String>): Offer? {
        var amount: Double? = null
        var minutes: Int? = null
        var distanceKm: Double? = null
        var hasAccept = false

        for (line in ocrLines) {
            // kwota
            if (amount == null) {
                AMOUNT_REGEX.find(line)?.let {
                    amount = it.groupValues[1].replace(",", ".").toDoubleOrNull()
                }
            }
            // czas
            if (minutes == null) {
                TIME_REGEX.find(line)?.let {
                    minutes = it.groupValues[1].toIntOrNull()
                }
            }
            // dystans
            if (distanceKm == null) {
                DISTANCE_REGEX.find(line)?.let {
                    distanceKm = it.groupValues[1].replace(",", ".").toDoubleOrNull()
                }
            }
            // accept
            if (ACCEPT_TEXTS.any { line.lowercase().contains(it) }) {
                hasAccept = true
            }
        }

        if (amount == null || minutes == null || !hasAccept) return null
        return Offer(Platform.UBER, amount!!, minutes!!, distanceKm)
    }
}
```

- **Trudność:** 3/5

#### TASK D.2 — Rozszerzyć model Offer o dystans
```kotlin
data class Offer(
    val platform: Platform,
    val amount: Double,
    val estimatedMinutes: Int,
    val distanceKm: Double? = null  // nowe, opcjonalne
)
```

- **Trudność:** 1/5

#### TASK D.3 — Rozszerzyć overlay o dystans
- Belka: "14.64 zł | 26 min | 5.6 km | 33.8 zł/h"
- **Trudność:** 1/5

---

### ETAP E: Integracja nowego pipeline'u (1 sesja)

#### TASK E.1 — Nowy pipeline w CourierAccessibilityService

Zmienić `processEvent()`:
```
1. Wykryj event od com.ubercab.driver (TYPE_WINDOW_STATE_CHANGED)
2. ScreenCaptureManager.capture() → Bitmap
3. PopupCropper.crop(bitmap) → croppedBitmap
4. OcrEngine.recognize(croppedBitmap) → List<String>
5. UberOcrParser.parse(lines) → Offer?
6. OfferAnalyzer.analyze(offer) → AnalysisResult
7. SystemOverlayManager.show(result, offer)
```

- Callback chain: capture → crop → recognize → parse → show
- Debounce: zachować 300ms (albo zwiększyć do 500ms — screenshot + OCR trwa ~200-300ms)
- Fallback: jeśli OCR nie wykryje tekstu, loguj warning i nie pokazuj overlay
- Guard: nie rób screenshota częściej niż co 1s (throttle)

- **Trudność:** 4/5

#### TASK E.2 — Usunąć stary flow z AccessibilityNodeInfo
- `UberParser` → deprecate lub usunąć
- `OfferParser` interface → zachować (przyszłe platformy mogą mieć czytelne drzewo UI)
- **Trudność:** 1/5

---

### ETAP F: Testowanie (1-2 sesje)

#### TASK F.1 — Test z hardcoded screenshot
- Wbudować testowy screenshot (załączony obraz) w app
- Sprawdzić: crop → OCR → parser → overlay
- Bez potrzeby telefonu Taty
- **Trudność:** 2/5

#### TASK F.2 — Test na telefonie Taty
- Build APK → zainstaluj → czekaj na zlecenie
- Logcat: "OCR lines: [...]", "Offer detected: ..."
- **Trudność:** 2/5

#### TASK F.3 — Edge cases
- Zlecenie z inną kwotą (np. 8,50 zł, 45,00 zł)
- Polski / angielski UI Ubera
- Różne rozdzielczości ekranu (crop %)
- Ciemny/jasny motyw (Uber ma ciemny)
- Co gdy OCR źle odczyta "," jako "." lub odwrotnie → regex musi obsłużyć oba

---

## Szacowany czas

| Etap | Opis | Czas |
|------|------|------|
| A | Przygotowanie (minSdk, ML Kit, config) | 30 min |
| B | Screenshot + crop | 1h |
| C | OCR Engine | 1h |
| D | Nowy parser + model | 1.5h |
| E | Integracja pipeline | 2h |
| F | Testowanie | 2-3h |
| | **Razem** | **~8-9h** |

---

## Zależności nowych pakietów

```
com.courierassist.app
├── capture/                    ← NOWY
│   ├── ScreenCaptureManager    ← robi screenshot
│   └── PopupCropper            ← wycina popup z pełnego screena
├── ocr/                        ← NOWY
│   └── OcrEngine               ← ML Kit wrapper
├── parser/
│   ├── UberOcrParser           ← NOWY (regex na tekście z OCR)
│   ├── UberParser              ← STARY (do usunięcia/deprecation)
│   ├── OfferParser             ← interfejs (zachować)
│   └── ParserRegistry          ← aktualizacja
├── domain/
│   └── Offer                   ← +distanceKm
├── engine/
│   └── OfferAnalyzer           ← bez zmian
├── overlay/
│   └── SystemOverlayManager    ← +dystans w belce
└── service/
    └── CourierAccessibilityService ← nowy pipeline
```

---

## Ryzyka i mitygacja

| Ryzyko | Prawdopodobieństwo | Mitygacja |
|--------|-------------------|-----------|
| OCR źle odczyta kwotę | Niskie (biały tekst na czarnym tle = idealny kontrast) | Fuzzy matching w regex, logowanie raw OCR output |
| `takeScreenshot()` zwróci null | Niskie | Retry 1x po 200ms, potem log error |
| Screenshot trwa >500ms | Niskie na nowoczesnych telefonach | Throttle: max 1 screenshot/s |
| Uber zmieni layout popupu | Średnie (długoterminowo) | Regex jest elastyczny, nie zależy od pozycji pikseli |
| ML Kit model zbyt duży | Niskie (~5MB) | Wersja bundled jest małą ceną za offline |
| minSdk 30 wyklucza telefon Taty | Niskie (2026) | Sprawdzić wersję Androida; fallback MediaProjection jeśli trzeba |

---

## Podjęte decyzje (2026-02-26)

| # | Decyzja | Wybór | Uzasadnienie |
|---|---------|-------|--------------|
| 1 | **minSdk** | **30 (Android 11)** | `takeScreenshot()` wymaga API 30; w 2026 roku ~95% telefonów powyżej API 30; upraszcza kod, eliminuje fallback MediaProjection |
| 2 | **ML Kit wariant** | **Bundled** (`com.google.mlkit:text-recognition`) | Działa offline od razu po instalacji — kluczowe dla kuriera w trasie; +~5MB APK to akceptowalny koszt |
| 3 | **Implementacja** | **Osobni agenci** per etap | Każdy etap A–F implementuje osobny agent; plan w `docs/PLAN_OCR.md` jest specyfikacją wejściową |

---

## Notatki techniczne

### AccessibilityService.takeScreenshot() — API
```kotlin
// Dostępne od API 30 (Android 11)
// Wymaga: accessibility_config.xml → canTakeScreenshot="true" (od API 33)
service.takeScreenshot(
    Display.DEFAULT_DISPLAY,
    executor,
    callback
)
```

### ML Kit Text Recognition — API
```kotlin
// Gradle
implementation("com.google.mlkit:text-recognition:16.0.1")

// Użycie
val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
val image = InputImage.fromBitmap(bitmap, 0)
recognizer.process(image)
    .addOnSuccessListener { text: Text ->
        text.textBlocks.forEach { block ->
            block.lines.forEach { line ->
                println(line.text)  // "14,64 zł"
            }
        }
    }
```

### Jak wygląda popup (z załączonego screenshota)
```
┌─────────────────────────────────┐
│  🍴 Dostawa              ✕     │
│                                 │
│  14,64 zł                       │
│                                 │
│  🕐 Łącznie 26 min (5.6 km)   │
│                                 │
│  ● Panie Janie Pizza Morena    │
│  ┃ Dąbrówki & Królowej         │
│  ┃ Jadwigi, Gdańsk             │
│                                 │
│  ┌─────────────────────────┐   │
│  │       Akceptuj          │   │
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

OCR powinien bez problemu rozpoznać: kwotę, czas, dystans, adresy, przycisk. Biały tekst na ciemnym tle to optymalny scenariusz dla OCR.
