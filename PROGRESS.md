# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-10
**Obecny etap:** Dual-mode accessibility fallback ✅ UKOŃCZONY. Następny: Setup Wizard + testy na prawdziwym Uberze
**Aktywny branch:** `feature/production-app`

---

## Faza POC — ZAKOŃCZONA

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md — 2026-02-25
- [x] FakeUberDriver: aplikacja testowa symulująca popup Uber — 2026-02-26
- [x] **POC MediaProjection + OCR: belka pojawia się na telefonie przy popupie FakeUberDriver** — 2026-02-27

POC udowodnił że pipeline działa. Kod POC zostaje na `main` jako punkt odniesienia — NIE kontynuujemy go.

---

## Właściwa aplikacja — EPIC 1–14 UKOŃCZONE

| # | Etap | Status |
|---|------|--------|
| ✅ | Architektura (`docs/ARCHITECTURE.md`) | Gotowa (2026-02-27) |
| ✅ | Plan implementacji (`docs/PLAN.md` v2) | Zatwierdzony (2026-03-03) |
| ✅ | EPIC 1: Fundament (Gradle, Manifest, DI, Logger) | Ukończony (2026-03-04) |
| ✅ | EPIC 2: Domain (modele danych) | Ukończony (2026-03-04) |
| ✅ | EPIC 3: Settings (ustawienia + repo) | Ukończony (2026-03-04) |
| ✅ | EPIC 4: Engine (analiza + filtrowanie) | Ukończony (2026-03-04) |
| ✅ | EPIC 5: Parser (OCR parser Uber) | Ukończony (2026-03-04) |
| ✅ | EPIC 6: Capture (MediaProjection) | Ukończony (2026-03-04) |
| ✅ | EPIC 7: OCR (ML Kit wrapper) | Ukończony (2026-03-04) |
| ✅ | EPIC 8: Overlay (belka) | Ukończony (2026-03-04) |
| ✅ | EPIC 9: Pipeline (orkiestracja) | Ukończony (2026-03-04) |
| ✅ | EPIC 10: Service (AccessibilityService) | Ukończony (2026-03-04) |
| ✅ | EPIC 11: UI — MainActivity | Ukończony (2026-03-04) |
| ✅ | EPIC 12: UI — SettingsActivity | Ukończony (2026-03-04) |
| ✅ | EPIC 13: Billing stub + weryfikacja DI | Ukończony (2026-03-04) |
| ✅ | EPIC 14: Testy E2E + polish | Ukończony (2026-03-05) |

Pełny plan: `docs/PLAN.md` (14 epiców, 40 tasków)

---

## Bugfixy i zadania po testach na fizycznym telefonie

Ojciec testował aplikację na fizycznym telefonie (2026-03-06/07). Zgłosił 5 zadań Jira (KAN-11 do KAN-15) i 1 krytyczny bug (wygaszanie ekranu).

### Ukończone

| Zadanie | Opis | Efekt | Data | Branch |
|---------|------|-------|------|--------|
| Fix krytyczny: wygaszanie ekranu | App przestawała działać po wygaszeniu ekranu (ScreenCaptureService ginął) | WakeLock trzyma serwis przy życiu. Flaga `isProjectionLost` + powiadomienie pozwala wznowić bez restartu app | 2026-03-08 | `fix/screen-off-survival` → merged |
| Optymalizacja latencji pipeline | Belka pojawiała się z opóźnieniem ~2s | firstShotDelay 300→100ms, captureDelay 200→100ms, cooldown 5s→3s. Wynik: ~1.3s (bottleneck: ML Kit OCR ~700ms) | 2026-03-08 | `fix/screen-off-survival` → merged |
| KAN-14: odświeżanie belki | Belka nie aktualizowała się przy nowym zamówieniu gdy poprzednia była widoczna | Usunięto guard `isShowing()` z PipelineOrchestrator — `show()` już wywołuje `hide()` wewnętrznie | 2026-03-08 | `fix/kan-14-overlay-refresh` → merged |
| Bug: START + accessibility | START wymagał 2 kliknięć (race condition onResume/onActivityResult). Toggle OFF/ON po reinstalacji — ograniczenie Androida przy sideloadingu | `pendingStart` flaga blokuje nadpisanie stanu przez onResume. Flaga `isConnected` wykrywa stan "enabled ale nie connected" z celowym toastem | 2026-03-08 | `fix/start-button-race-condition` + `fix/accessibility-detection` → merged |
| KAN-12 | Dark mode — belka nie wyświetla się poprawnie w trybie ciemnym | `forceDarkAllowed="false"` na overlay + jawny `setTextColor(Color.WHITE)` | 2026-03-08 | `feature/production-app` |
| KAN-11 | Dialog MediaProjection mylący dla użytkownika | Toast wyjaśniający przed dialogiem: "Zezwól na nagrywanie ekranu — to pozwala analizować oferty" | 2026-03-08 | `feature/production-app` |
| KAN-13 + KAN-15 | Suwaki przezroczystości i czasu wyświetlania belki | `overlayOpacity` (0-100%) i `displayTimeSeconds` (5-60s) w DisplayConfig. Suwaki w SettingsActivity. Opacity → `view.alpha`, czas → dynamiczny `hideDelayMs` | 2026-03-08 | `feature/production-app` (niescommitowane) |

### Dual-mode accessibility fallback — UKOŃCZONE 2026-03-10

| Zadanie | Opis | Status | Branch |
|---------|------|--------|--------|
| AccessibilityTextCollector.kt | Klasa do rekurencyjnego zbierania tekstu z drzewa UI (`getRootInActiveWindow()`) | ✅ | feature/accessibility-fallback |
| CourierAccessibilityService: dual-mode logic | Jeśli MediaProjection niedostępna → fallback na text parsing z accessibility tree | ✅ | feature/accessibility-fallback |
| Deduplikacja wyników (fix mrugania) | `lastResult` + `lastResultTime` aby nie wyświetlać tego samego wyniku co 1.6s | ✅ | feature/accessibility-fallback |
| Fix statusu Inactive po screen off | `onResume()` sprawdza `CourierAccessibilityService.isConnected` zamiast ustawiać Inactive gdy accessibility działa | ✅ | feature/accessibility-fallback |
| Flaga isUserStopped | Stop button teraz faktycznie wyłącza accessibility fallback | ✅ | feature/accessibility-fallback |
| Toast strings (3 języki) | `toast_projection_lost` w `values/`, `values-en/`, `values-uk/` | ✅ | feature/accessibility-fallback |
| FakeUberApp: format dystansu | Zmieniono `"%.1f km"` → `"(%.1f km)"` aby matchował regex parsera | ✅ | feature/accessibility-fallback |

**Merge:** feature/accessibility-fallback → feature/production-app (commit 8a9109c) ✅

**Testowanie:** Ojciec testuje na Android 16 (SM-S911B). Po wygaszeniu ekranu — accessibility fallback parsuje tekst Ubera i wyświetla belkę bez MediaProjection.

### Otwarte zadania — Setup Wizard + Kompatybilność Android 16

| Problem | Rozwiązanie | Status | Priorytet |
|---------|-------------|--------|-----------|
| Brak battery optimization | Setup wizard + `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Do implementacji | High |
| Samsung agresywne usypianie | Setup wizard z instrukcją "Never sleeping apps" | Do implementacji | High |
| SetupActivity jest (ale nie testowana) | Podpiąć do MainActivity + przetestować workflow | Do testów | Medium |
| Test na prawdziwym Uberze | Czy accessibility fallback parsuje rzeczywiste zlecenia z Uber Driver | Do testów (jutro) | High |

Plan: `docs/PLAN.md`

---

## Aktywne branche

| Branch | Cel | Status | Last Commit |
|--------|-----|--------|-------------|
| `feature/production-app` | Główny branch produkcyjny — zawiera wszystkie bugfixy + dual-mode | ✅ Aktywny na GitHub | 8a9109c (2026-03-10) |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu (tylko dokumentacja) | 285c209 |

> Workflow: nowe zadanie → nowy branch `fix/...` lub `feature/...` → testuj na telefonie → merge do `feature/production-app`

## Workflow branche (w pracy)

| Branch | Cel | Status | Data |
|--------|-----|--------|------|
| `feature/accessibility-fallback` | Dual-mode: accessibility fallback + fixes (mruganie + Inactive) | ✅ Merged do production-app | 2026-03-10 |
| `feature/ui-redesign` | UI redesign + wielojęzyczność + bugfixy | ✅ Merged do production-app (pośrednio) | 2026-03-09 |

## Archiwalne branche

| Branch | Cel | Notatka |
|--------|-----|---------|
| `fix/screen-off-survival` | Fix wygaszania + optymalizacja latencji | Zmerge'owany do `feature/production-app` 2026-03-08 |
| `feature/fake-uber-driver` | Aplikacja testowa FakeUberDriver 2 | Gotowa, używana do testów E2E |
| `lukasz` | POC: MediaProjection + OCR pipeline | Zmerge'owany do `main` |

---

## Kluczowe odkrycia techniczne (dla nowych osób)

### Pipeline który działa:

```
Uber Driver popup pojawia się na ekranie
    ↓
CourierAccessibilityService wykrywa event (TYPE_WINDOW_CONTENT_CHANGED)
    ↓
EventThrottler: czeka 100ms (firstShot), potem cooldown 1.5s
    ↓
ScreenCaptureService robi screenshot przez MediaProjection API
    → widzi WSZYSTKO na ekranie, w tym overlaye innych aplikacji
    ↓
PopupCropper przycina dolne 60% ekranu (tam gdzie jest popup)
    ↓
ML Kit OCR rozpoznaje tekst z bitmapy (~200-300ms)
    ↓
UberOcrParser (regex) wyciąga: kwotę (zł), czas (min), dystans (km)
    ↓
OfferAnalyzer liczy zł/h = kwota / (minuty/60)
    ↓
SystemOverlayManager pokazuje belkę na górze ekranu:
    GREEN (≥40 zł/h) | YELLOW (≥32 zł/h) | RED (<32 zł/h)
    Belka znika automatycznie po 5s (OverlayAutoHider)
```

### Ważne ograniczenia techniczne:
1. `takeScreenshot()` z AccessibilityService nie widzi overlayów innych app — stąd MediaProjection dla pełnego capture
2. **ALE** `getRootInActiveWindow()` + `collectText()` **widzi tekst z popupów Ubera** — bo popup to okno aktywnej aplikacji, nie overlay
3. MediaProjection wymaga jednorazowej zgody użytkownika przy każdym uruchomieniu (Android 14+)
4. **MediaProjection ginie po wygaszeniu ekranu** (Android 14+ policy) — nie da się obejść
5. Na emulatorze MediaProjection daje pusty obraz — testy tylko na fizycznym telefonie
6. ML Kit OCR (~200-300ms) — realna latencja ~350-420ms total
7. Po reinstalacji APK AccessibilityService wymaga ręcznego toggle OFF→ON (znane ograniczenie Androida)

---

## UI Redesign i bugfixy — 2026-03-09

| Zadanie | Opis | Status |
|---------|------|--------|
| Suwak czasu wyświetlania | Zakres 1–15s, krok 1s (było 5–60s) | ✅ |
| Przycisk Zapisz | Przyklejony do dołu ekranu, poza ScrollView | ✅ |
| Po zapisaniu | `finish()` natychmiast bez opóźnienia | ✅ |
| Parser wielojęzyczny | Uniwersalny regex PL/UK/EN jednocześnie — nie zależy od języka Ubera | ✅ |
| Belka wielojęzyczna | Tekst belki (zł/h, грн/год, PLN/h) według języka UI | ✅ |
| Tłumaczenia UI | `values-uk/` i `values-en/` — cała aplikacja po ukraińsku/angielsku | ✅ |
| Zmiana języka natychmiastowa | LocaleHelper + attachBaseContext, restart MainActivity przy zmianie języka | ✅ |
| Fix mrugania belki | `lastResult` nie kasowany po ukryciu — wygasa po 60s | ✅ |
| Cooldown 3s → 1.5s | Szybsza reakcja na nowe zlecenie | ✅ |

Branch: `feature/ui-redesign`

## Co dalej — Priorytet

1. **TEST na prawdziwym Uberze** (jutro) — czy accessibility fallback parsuje rzeczywiste zlecenia. Jeśli tak → prawie done.
2. **Setup wizard** — jeśli nie istnieje flow. SetupActivity.kt jest w repo ale wymaga testów + podpięcia do MainActivity.
3. **Ewentualne bugfixy** po testach z prawdziwym Uberem.

**Token budget:** ~20% weekly limit do 2026-03-15. Realistycznie: test Ubera + ewentualny bugfix + Setup wizard → 2-3 sesje.

Plan w `docs/PLAN.md`.
