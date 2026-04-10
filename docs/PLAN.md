# Plan przyszłych feature'ów

**Ostatnia aktualizacja:** 2026-03-17
**Bieżący status i otwarte zadania:** `PROGRESS.md`

---

## Setup Wizard + Battery Optimization

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
- Przycisk "Sprawdź uprawnienia" w SettingsActivity → otwiera wizard ponownie

### Krok 3: Tłumaczenia
Nowe stringi w `values/strings.xml`, `values-uk/strings.xml`, `values-en/strings.xml`

---

## UI/UX Polish
- Dopracowanie wyglądu MainActivity, SettingsActivity, belki
- Lepsze layouty, kolory, animacje, ogólna estetyka

## Przesuwana belka + mini overlay
- Belka draggable (użytkownik ustawia pozycję)
- Mały stały overlay do szybkiego dostosowania (przesunięcie, ustawienia)

## Do rozważenia (nie pilne)
- Usunięcie MediaProjection — takeScreenshot() daje te same wyniki, prostszy UX (brak dialogu)
- Powiadomienie "wróć do aplikacji" — jeśli użytkownik nie korzysta z OrderPilot przez X dni, wysłać push notification zachęcający do powrotu (wzór: RideHelper "Zarabiaj więcej!")
