# Plan: Setup Wizard + Testy produkcyjne

**Data:** 2026-03-12 (aktualizacja)
**Status:** Testy produkcyjne w toku — Uber ✅, Wolt jasny motyw ✅, Wolt dark mode 🔄
**Branch aktywny:** `feature/wolt-parser`

## Co zostało zrobione

- takeScreenshot() fallback ✅ działa na prawdziwym Uberze (2026-03-11)
- WoltOcrParser ✅ gotowy i zweryfikowany na jednym zleceniu (jasny motyw, 2026-03-12)
- Logi diagnostyczne ✅ dodane do EventThrottler, PipelineOrchestrator, WoltOcrParser, CourierAccessibilityService

Szczegóły: `PROGRESS.md`

---

## Aktywne ścieżki rozwoju

### Branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/wolt-parser` | Wsparcie platformy Wolt — parser + testy produkcyjne | 🔄 Testy w toku, czeka na merge |
| `fix/uber-feedback-MMDD` | Bugfixy po testach ojca | ⏳ Otwieramy po zebraniu feedbacku |

---

## Otwarte zadania

| Problem | Rozwiązanie | Priorytet |
|---------|-------------|-----------|
| Wolt dark mode | Ojciec testuje samodzielnie 2026-03-12 | High |
| Merge feature/wolt-parser | Po potwierdzeniu dark mode + kilka zleceń | High |
| Brak battery optimization | Setup wizard + `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | High |
| Samsung agresywne usypianie | Setup wizard z instrukcją "Never sleeping apps" | High |
| SetupActivity jest (ale nie testowana) | Podpiąć do MainActivity + przetestować workflow | Medium |
| Bug: Uber popup nad Woltem | Fallback czyta Wolta w tle → parser null | Medium |

---

## Setup Wizard — plan implementacji

### Krok 1: SetupActivity
**Pliki:** `ui/SetupActivity.kt` (jest w repo), `res/layout/activity_setup.xml`

Lista uprawnień z przyciskami i statusami ✓/✗:
1. Wyświetlanie nad innymi aplikacjami → `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`
2. Usługa dostępności → `Settings.ACTION_ACCESSIBILITY_SETTINGS`
3. Działanie w tle (battery) → `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
4. Samsung: usypianie apek (tylko Samsung) → `com.samsung.android.sm.ACTION_OPEN_CHECKABLE_LISTACTIVITY`

`onResume()` sprawdza statusy, "Kontynuuj" aktywny gdy 1–3 granted.

### Krok 2: Integracja z MainActivity
- Przy starcie sprawdzić czy uprawnienia nadane → jeśli nie → SetupActivity
- Po setup → powrót do MainActivity

### Krok 3: Tłumaczenia
Nowe stringi w `values/strings.xml`, `values-uk/strings.xml`, `values-en/strings.xml`