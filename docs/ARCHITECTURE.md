# Architektura Projektu CourierAssist

## Podział na warstwy

```
com.courierassist.app
│
├── ui/
├── service/
├── parser/
│     ├── UberParser
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
    val estimatedMinutes: Int
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

### UberParser.kt (ETAP 1)
Zawiera:
- rozpoznanie przycisków „Accept"
- parsowanie kwoty
- parsowanie czasu

Zwraca `Offer` albo `null`.

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

## 5. ACCESSIBILITY SERVICE (rdzeń aplikacji)

### CourierAccessibilityService.kt

Odpowiedzialność:
- nasłuchiwanie zmian UI
- wykrywanie czy to Uber/Wolt/Glovo
- wywołanie parsera
- wywołanie analizy
- pokazanie overlay

```kotlin
override fun onAccessibilityEvent(event: AccessibilityEvent) {
    val packageName = event.packageName?.toString() ?: return
    val parser = parserRegistry.getParser(packageName) ?: return
    val root = rootInActiveWindow ?: return
    val offer = parser.parse(root) ?: return
    val result = offerAnalyzer.analyze(offer)
    overlayManager.show(result, offer)
}
```

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
- Tło: czarne 70% opacity (domyślnie, konfigurowalne)
- Kliknięcia przechodzą na aplikację pod spodem
- Format: `🟢 42 zł/h | 35 zł | 50 min` lub `🔴 28 zł/h – NIEOPŁACALNE`

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
