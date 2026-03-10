# Instrukcja: Fix mrugania belki + status Inactive po screen off

**Branch:** `feature/accessibility-fallback`
**Priorytet:** WYSOKI — bez tego apka pokazuje Inactive i belka mruga

---

## Kontekst

Accessibility fallback (dodany wcześniej) działa — po wygaszeniu ekranu AccessibilityService
czyta tekst z drzewa UI Ubera i parsuje go. **Potwierdzone logami z telefonu.**

Dwa problemy:

1. **Mruganie belki** — brak deduplikacji wyników. Co ~1.6s ten sam wynik → `overlayManager.show()` → mruganie.
   PipelineOrchestrator ma `lastResult` ochronę — ale accessibility fallback jej nie ma.

2. **Status "Inactive"** — `MainActivity.onResume()` wykrywa `isProjectionLost == true` → `isRunning = false`.
   Ale accessibility fallback nadal działa.

---

## Zmiana 1: Deduplikacja wyników w CourierAccessibilityService

**Plik:** `CourierAssist/app/src/main/java/com/courierassist/app/service/CourierAccessibilityService.kt`

Dodaj pola instancyjne (w klasie, nie companion):
```kotlin
@Volatile private var lastResult: AnalysisResult? = null
@Volatile private var lastResultTime = 0L
private val resultExpiryMs = 60_000L
```

Import: `com.courierassist.app.domain.AnalysisResult`

W `processViaAccessibility()`, **PO** `val result = ...` a **PRZED** `scope.launch(Dispatchers.Main)`:
```kotlin
// Deduplikacja — nie pokazuj belki jeśli ten sam wynik
val now = System.currentTimeMillis()
if (now - lastResultTime > resultExpiryMs) lastResult = null
if (result == lastResult) {
    AppLog.d(AppLog.TAG_SERVICE, "Accessibility: same result, skipping")
    return
}
lastResult = result
lastResultTime = now
```

**UWAGA:** `AnalysisResult` jest `data class` więc `equals()` działa poprawnie.

---

## Zmiana 2: Flaga isUserStopped

**Plik:** `CourierAssist/app/src/main/java/com/courierassist/app/service/CourierAccessibilityService.kt`

W `companion object` dodaj:
```kotlin
@Volatile
var isUserStopped = false
```

Na początku `onAccessibilityEvent()`, zaraz po sprawdzeniu pakietów:
```kotlin
if (isUserStopped) return
```

---

## Zmiana 3: MainActivity.onResume() — nie ustawiaj Inactive

**Plik:** `CourierAssist/app/src/main/java/com/courierassist/app/ui/MainActivity.kt`

### Zmień `onResume()`:

**BYŁO:**
```kotlin
override fun onResume() {
    super.onResume()
    if (ScreenCaptureService.isProjectionLost) {
        ScreenCaptureService.stopCapture(this)
        isRunning = false
        pendingStart = false
        updateUi()
        Toast.makeText(this, "Nagrywanie ekranu zostało przerwane. Kliknij Start żeby wznowić.", Toast.LENGTH_LONG).show()
    } else if (!pendingStart) {
        isRunning = ScreenCaptureService.instance != null
        updateUi()
    }
    updateAccessibilityHint()
}
```

**MA BYĆ:**
```kotlin
override fun onResume() {
    super.onResume()
    if (ScreenCaptureService.isProjectionLost) {
        ScreenCaptureService.stopCapture(this)
        pendingStart = false
        // Accessibility fallback nadal działa — nie ustawiaj Inactive
        if (!CourierAccessibilityService.isConnected) {
            isRunning = false
            Toast.makeText(this, getString(R.string.toast_projection_lost), Toast.LENGTH_LONG).show()
        }
        updateUi()
    } else if (!pendingStart) {
        isRunning = ScreenCaptureService.instance != null || CourierAccessibilityService.isConnected
        updateUi()
    }
    updateAccessibilityHint()
}
```

### Zmień `startCapture()` — reset flagi:

Na początku `startCapture()` dodaj:
```kotlin
CourierAccessibilityService.isUserStopped = false
```

### Zmień `stopCapture()` — ustaw flagę:

W `stopCapture()` dodaj:
```kotlin
CourierAccessibilityService.isUserStopped = true
```

---

## Zmiana 4: Stringi (opcjonalnie)

Zamień hardcoded toast na string resource. Dodaj do `values/strings.xml`:
```xml
<string name="toast_projection_lost">Nagrywanie ekranu zostało przerwane. Kliknij Start żeby wznowić.</string>
```

I odpowiedniki w `values-en/strings.xml` i `values-uk/strings.xml`.

Jeśli pomijasz — zostaw hardcoded string w onResume (jak jest teraz).

---

## Czego NIE zmieniać

- `AccessibilityTextCollector` — bez zmian
- `PipelineOrchestrator` — bez zmian (ma swoją deduplikację)
- `ScreenCaptureService` — bez zmian
- `EventThrottler` — bez zmian
- Layout XML — bez zmian

---

## Weryfikacja (na telefonie z FakeUberApp)

1. Start → belka działa bez mrugania (OCR pipeline, deduplikacja w PipelineOrchestrator)
2. Wygaś ekran ~10s → odblokuj → wróć do apki
3. **Oczekiwane:** status nadal "Aktywna", brak toasta "kliknij Start"
4. Otwórz FakeUberApp → zlecenie → belka pojawia się **raz bez mrugania**
5. Sprawdź logi: `adb logcat -s CA_Service` — powinno być:
   - `Accessibility text (X chars)` — parsowanie działa
   - `Accessibility: same result, skipping` — deduplikacja działa
6. Kliknij Stop → UI: Inactive → accessibility fallback przestaje parsować
7. Kliknij Start → dialog MediaProjection → zielona, pełna jakość OCR