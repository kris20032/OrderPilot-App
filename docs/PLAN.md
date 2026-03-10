# Plan: Dual-mode + Setup Wizard + Kompatybilność Android 11–16

**Data:** 2026-03-10
**Status:** Do implementacji
**Branch:** `feature/ui-redesign`

---

## Kontekst problemu
- Telefon brata: Android 11 (One UI 3.1) — wszystko działa OK
- Telefon taty: Android 16 (One UI 8.0, SM-S911B) — problemy:
  1. Po wygaszeniu ekranu MediaProjection ginie, app przechodzi w Inactive
  2. Dialog MediaProjection — rozwiązane (`createConfigForDefaultDisplay`)

## Kluczowe odkrycie: Reverse-engineering RideHelper (2026-03-10)

Analiza konkurencyjnej apki `com.malansoft.ridehelper` (targetSdk=36, Play Store) ujawniła:

**Dual-mode architecture:**
- **Ścieżka 1 (primary):** MediaProjection → VirtualDisplay → ImageReader → ML Kit OCR
- **Ścieżka 2 (fallback):** AccessibilityService → `getRootInActiveWindow()` → recursive `collectText(node)` → RideParser

Gdy MediaProjection ginie po screen off, AccessibilityService przejmuje.
AccessibilityService czyta tekst z drzewa UI **bez OCR, bez MediaProjection, bez consent**.
Szczegóły: `.claude/projects/.../memory/ridehelper-reverse-engineering.md`

---

## Plan implementacji

### PRIORYTET 1: Dual-mode Accessibility Text Fallback

**Cel:** Gdy MediaProjection nie działa (screen off, brak consent), CourierAccessibilityService czyta tekst bezpośrednio z drzewa UI i parsuje go.

#### Krok 1.1: AccessibilityTextCollector (nowa klasa)
**Nowy plik:** `service/AccessibilityTextCollector.kt`

```kotlin
object AccessibilityTextCollector {
    fun collectText(root: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        traverseNode(root, sb)
        return sb.toString()
    }

    private fun traverseNode(node: AccessibilityNodeInfo, sb: StringBuilder) {
        node.text?.let { if (it.isNotBlank()) sb.append(it).append('\n') }
        node.contentDescription?.let { if (it.isNotBlank()) sb.append(it).append('\n') }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                traverseNode(child, sb)
                child.recycle()
            }
        }
    }
}
```

#### Krok 1.2: Rozbudowa CourierAccessibilityService
**Plik:** `service/CourierAccessibilityService.kt`

W `onAccessibilityEvent()` — dodać **drugą ścieżkę** (obok istniejącego triggera pipeline):

```kotlin
// Istniejąca ścieżka: trigger MediaProjection pipeline
if (isMediaProjectionActive) {
    pipelineOrchestrator.trigger()
}

// NOWA ścieżka: accessibility text fallback
if (!isMediaProjectionActive || isProjectionLost) {
    val root = rootInActiveWindow ?: return
    val text = AccessibilityTextCollector.collectText(root)
    root.recycle()
    // Parsuj tekst i pokaż overlay
    processAccessibilityText(text, packageName)
}
```

**Ważne:**
- Filtruj pakiety: `com.ubercab.driver`, `com.ubercab`, itp. (jak RideHelper)
- Throttle: 250ms–500ms między przetwarzaniami (jak RideHelper ma 250ms)
- Użyj istniejących parserów (`UberOcrParser` lub prosty regex na surowym tekście)

#### Krok 1.3: Flaga stanu MediaProjection
**Plik:** `capture/ScreenCaptureService.kt`

- Expose `isProjectionActive: Boolean` (companion object lub broadcast)
- Gdy projection ginie → broadcast `ACTION_PROJECTION_LOST`
- CourierAccessibilityService nasłuchuje i przełącza tryb

#### Krok 1.4: Adaptacja parsera
**Plik:** `parser/UberOcrParser.kt` (lub nowa klasa `AccessibilityTextParser`)

Tekst z drzewa accessibility ma **inny format** niż OCR:
- OCR: ciągły tekst z rozpoznawania obrazu, może mieć błędy
- Accessibility: czysty tekst z UI widgetów, linia po linii

Parser musi obsłużyć oba formaty. Najprościej: użyć tego samego regex co w OCR parser (bo tekst zawiera te same dane — kwotę, czas, dystans).

#### Krok 1.5: Testy na telefonie
1. Uruchom apkę → Start → MediaProjection aktywna → belka działa (jak teraz)
2. Wygaś ekran → odblokuj → MediaProjection ginie → accessibility fallback przejmuje
3. Popup Ubera pojawia się → accessibility czyta tekst → belka się wyświetla
4. Porównaj wyniki: OCR vs accessibility text — czy parser daje te same wyniki?

---

### PRIORYTET 2: Setup Wizard

**Cel:** Ekran konfiguracji uprawnień przy pierwszym uruchomieniu.

#### Krok 2.1: SetupActivity
**Pliki:** `ui/SetupActivity.kt` (NOWY), `res/layout/activity_setup.xml` (NOWY)

Lista uprawnień z przyciskami i statusami ✓/✗:
1. Wyświetlanie nad innymi aplikacjami → `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`
2. Usługa dostępności → `Settings.ACTION_ACCESSIBILITY_SETTINGS`
3. Działanie w tle (battery) → `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
4. Samsung: usypianie apek (tylko Samsung) → `com.samsung.android.sm.ACTION_OPEN_CHECKABLE_LISTACTIVITY`

`onResume()` sprawdza statusy, "Kontynuuj" aktywny gdy 1-3 granted.

#### Krok 2.2: Integracja z MainActivity
- Przy starcie sprawdzić czy uprawnienia nadane → jeśli nie → SetupActivity
- Po setup → powrót do MainActivity

#### Krok 2.3: Tłumaczenia
Nowe stringi w `values/strings.xml`, `values-uk/strings.xml`, `values-en/strings.xml`

---

### PRIORYTET 3: Ochrona serwisu

#### Krok 3.1: ScreenCaptureService hardening
- `START_REDELIVER_INTENT` zamiast `START_NOT_STICKY`
- `FOREGROUND_SERVICE_IMMEDIATE` na notyfikacji (API 31+)

#### Krok 3.2: Manifest permissions
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`

---

## Podsumowanie zmian

| Plik | Zmiana |
|------|--------|
| `AccessibilityTextCollector.kt` (NOWY) | Zbieranie tekstu z drzewa AccessibilityNodeInfo |
| `CourierAccessibilityService.kt` | Dual-mode: trigger pipeline + accessibility text fallback |
| `ScreenCaptureService.kt` | Broadcast projection lost, START_REDELIVER_INTENT |
| `UberOcrParser.kt` | Obsługa tekstu z accessibility (ten sam regex) |
| `SetupActivity.kt` (NOWY) | Setup wizard |
| `activity_setup.xml` (NOWY) | Layout setup wizard |
| `MainActivity.kt` | Redirect do setup |
| `AndroidManifest.xml` | Permissions + SetupActivity |
| `strings.xml` (3 pliki) | Stringi setup wizard PL/UK/EN |

---

## Kolejność implementacji

1. **Dual-mode** (Priorytet 1) — rozwiązuje główny problem screen off
2. **Setup wizard** (Priorytet 2) — UX uprawnień
3. **Hardening** (Priorytet 3) — dodatkowa ochrona serwisu
4. **Testy na telefonie taty** — weryfikacja

---

## Poprzedni plan (historyczny)

Plan implementacji 14 epiców (40 tasków) — wszystkie ukończone 2026-03-05.
Szczegóły w `PROGRESS.md`.
