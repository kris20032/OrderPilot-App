# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-08
**Obecny etap:** Wszystkie KAN-y zaimplementowane — oczekiwanie na testy na telefonie
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

### Otwarte zadania

Brak — wszystkie zadania KAN-11 do KAN-15 zaimplementowane. Oczekiwanie na testy na fizycznym telefonie.

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/production-app` | Główny branch produkcyjny — tu trafia wszystko co działa | Aktywny |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu (tylko dokumentacja) |

> Workflow: nowe zadanie → nowy branch `fix/...` lub `feature/...` → testuj na telefonie → merge do `feature/production-app`

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
EventThrottler: czeka 100ms (firstShot), potem cooldown 3s
    ↓
ScreenCaptureService robi screenshot przez MediaProjection API
    → widzi WSZYSTKO na ekranie, w tym overlaye innych aplikacji
    ↓
PopupCropper przycina dolne 60% ekranu (tam gdzie jest popup)
    ↓
ML Kit OCR rozpoznaje tekst z bitmapy (~700ms)
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
1. `takeScreenshot()` z AccessibilityService nie widzi overlayów innych app — stąd MediaProjection
2. MediaProjection wymaga jednorazowej zgody użytkownika przy każdym uruchomieniu (Android 14+)
3. Na emulatorze MediaProjection daje pusty obraz — testy tylko na fizycznym telefonie
4. ML Kit OCR (~700ms) to bottleneck pipeline — trudny do obejścia bez zmiany silnika
5. Po reinstalacji APK AccessibilityService wymaga ręcznego toggle OFF→ON (znane ograniczenie Androida)

---

## Co dalej

Wszystkie zaplanowane zadania ukończone. Następny krok: testy na fizycznym telefonie przez tatę, zbieranie nowych bugów/feature requestów.
