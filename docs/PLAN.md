# Plan: Setup Wizard + Testy produkcyjne

**Data:** 2026-03-17 (aktualizacja)
**Status:** Wszystkie parsery gotowe (Uber/Wolt/Glovo/Bolt). Multi-overlay gotowy. Testy produkcyjne w toku.
**Aktywne branche:** `feature/multi-overlay`, `feature/bolt-parser`

## Co zostało zrobione

- takeScreenshot() fallback ✅ (2026-03-11)
- WoltOcrParser ✅ zweryfikowany (2026-03-13)
- GlovoOcrParser ✅ + fixy: gotówka, partial offer, sumowanie dystansów (2026-03-14/17)
- BoltFoodOcrParser ✅ gotowy, czeka na test (2026-03-15)
- UberOcrParser ✅ + filtr ekranu statystyk (2026-03-17)
- Multi-overlay ✅ — 2 belki naraz z różnych platform (2026-03-17)
- Domyślne ustawienia: 30s belka, wszystkie metryki widoczne (2026-03-17)

Szczegóły: `PROGRESS.md`

---

## Otwarte zadania

| Problem | Rozwiązanie | Priorytet |
|---------|-------------|-----------|
| Multi-overlay testy | Zbudować APK z `feature/multi-overlay`, przetestować 2 belki naraz | High |
| Glovo weryfikacja fixów | Gotówka (ODBIERZ), partial offer, sumowanie dystansów | High |
| Bolt Food testy | Testy na prawdziwych zleceniach | High |
| Merge do production-app | Po potwierdzeniu stabilności wszystkich zmian | High |
| Brak battery optimization | Setup wizard + `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Medium |
| Samsung agresywne usypianie | Setup wizard z instrukcją "Never sleeping apps" | Medium |
| UI/UX polish | Dopracowanie wyglądu — layouty, kolory, animacje, estetyka MainActivity/Settings/belka | Medium |
| Przesuwana belka + mini overlay | Belka draggable (użytkownik sam ustawia pozycję) + mały stały overlay do szybkiego dostosowania | Medium |
| Crash na starszym telefonie (brat) | SettingsActivity crash — do zbadania | Medium |
| Mruganie belki Uber jasny→ciemny | Deduplikacja lastResult — monitorowane | Low |

---

## Setup Wizard — plan implementacji

### Krok 1: SetupActivity
**Pliki:** `ui/SetupActivity.kt` (jest w repo), `res/layout/activity_setup.xml`

Lista uprawnień z przyciskami i statusami:
1. Wyświetlanie nad innymi aplikacjami → `Settings.ACTION_MANAGE_OVERLAY_PERMISSION`
2. Usługa dostępności → `Settings.ACTION_ACCESSIBILITY_SETTINGS`
3. Działanie w tle (battery) → `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
4. Samsung: usypianie apek (tylko Samsung) → `com.samsung.android.sm.ACTION_OPEN_CHECKABLE_LISTACTIVITY`

`onResume()` sprawdza statusy, "Kontynuuj" aktywny gdy 1–3 granted.

### Krok 2: Integracja z MainActivity + SettingsActivity
- Przy starcie sprawdzić czy uprawnienia nadane → jeśli nie → SetupActivity
- Po setup → powrót do MainActivity
- Przycisk "Sprawdź uprawnienia" w SettingsActivity → otwiera wizard ponownie (podgląd statusu uprawnień w dowolnym momencie)

### Krok 3: Tłumaczenia
Nowe stringi w `values/strings.xml`, `values-uk/strings.xml`, `values-en/strings.xml`