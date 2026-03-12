# Plan: Setup Wizard + Testy produkcyjne

**Data:** 2026-03-11 (aktualizacja)
**Status:** takeScreenshot fallback ✅ DZIAŁA — czekamy na wyniki całodniowych testów 2026-03-12
**Branch:** `feature/production-app`

## Co zostało zrobione (2026-03-11)

takeScreenshot() z AccessibilityService działa jako fallback po screen off na prawdziwym Uberze.
Szczegóły: `PROGRESS.md` → sekcja "takeScreenshot fallback — UKOŃCZONE 2026-03-11"

---

## Aktywne ścieżki rozwoju

### Równoległe branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/wolt-support` | Wsparcie platformy Wolt — nowy parser + detekcja pakietu | 🔄 Do rozpoczęcia |
| `fix/uber-feedback-MMDD` | Bugfixy po testach ojca (2026-03-12) | ⏳ Czekamy na feedback |

- **Wolt:** nowy branch `feature/wolt-support` — niezależny od testów Ubera, można zacząć równolegle
- **Uber fixy:** branch otworzymy po otrzymaniu feedbacku od ojca z testów 2026-03-12

---

## Otwarte zadania

| Problem | Rozwiązanie | Priorytet |
|---------|-------------|-----------|
| Całodniowe testy na prawdziwym Uberze | Ojciec testuje 2026-03-12 — zbieramy feedback | High |
| Brak battery optimization | Setup wizard + `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | High |
| Samsung agresywne usypianie | Setup wizard z instrukcją "Never sleeping apps" | High |
| SetupActivity jest (ale nie testowana) | Podpiąć do MainActivity + przetestować workflow | Medium |
| Potencjalne bugfixy po testach | Nieznane — zależy od wyników 2026-03-12 | TBD |

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