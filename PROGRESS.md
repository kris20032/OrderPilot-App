# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-04
**Obecny etap:** Implementacja produkcyjna — EPIC 4 ukończony
**Cel następny:** EPIC 5 — Parser (OCR parser Uber)

---

## Faza POC — ZAKOŃCZONA

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md, TASKS.md — 2026-02-25
- [x] FakeUberDriver: aplikacja testowa symulująca popup Uber — 2026-02-26
- [x] **POC MediaProjection + OCR: belka pojawia się na telefonie przy popupie FakeUberDriver** — 2026-02-27

POC udowodnił że pipeline działa. Kod POC zostaje na `main` jako punkt odniesienia — NIE kontynuujemy go.

---

## Właściwa aplikacja — produkcja

| # | Etap | Status |
|---|------|--------|
| ✅ | Architektura (`docs/ARCHITECTURE.md`) | Gotowa (2026-02-27) |
| ✅ | Plan implementacji (`docs/PLAN.md` v2) | Zatwierdzony (2026-03-03) |
| ✅ | EPIC 1: Fundament (Gradle, Manifest, DI, Logger) | Ukończony (2026-03-04) |
| ✅ | EPIC 2: Domain (modele danych) | Ukończony (2026-03-04) |
| ✅ | EPIC 3: Settings (ustawienia + repo) | Ukończony (2026-03-04) |
| ✅ | EPIC 4: Engine (analiza + filtrowanie) | Ukończony (2026-03-04) |
| — | EPIC 5: Parser (OCR parser Uber) | Nie zaczęty |
| — | EPIC 6: Capture (MediaProjection) | Nie zaczęty |
| — | EPIC 7: OCR (ML Kit wrapper) | Nie zaczęty |
| — | EPIC 8: Overlay (belka) | Nie zaczęty |
| — | EPIC 9: Pipeline (orkiestracja) | Nie zaczęty |
| — | EPIC 10: Service (AccessibilityService) | Nie zaczęty |
| — | EPIC 11: UI — MainActivity | Nie zaczęty |
| — | EPIC 12: UI — SettingsActivity | Nie zaczęty |
| — | EPIC 13: Billing stub + weryfikacja DI | Nie zaczęty |
| — | EPIC 14: Testy E2E + polish | Nie zaczęty |

Pełny plan: `docs/PLAN.md` (14 epiców, 40 tasków)

---

## Aktywne branche

| Branch | Cel | Status | Kto |
|--------|-----|--------|-----|
| `main` | Stabilna baza z działającym POC | ✅ Zablokowany (tylko docs) | — |
| `feature/production-app` | Produkcyjna aplikacja (14 epiców) | 🔄 W trakcie (EPIC 1-4 ✅) | Krzysztof |

> Nowe zadanie = nowy branch. Od teraz ŻADNYCH zmian bezpośrednio na main.

## Archiwalne branche (referencja, nie rozwijane)

| Branch | Cel | Notatka |
|--------|-----|---------|
| `feature/fake-uber-driver` | Aplikacja testowa FakeUberDriver | Gotowa, używana do testów E2E |
| `lukasz` | POC: MediaProjection + OCR pipeline | Zmerge'owany do main |
| `feature/ocr` | Eksperymenty z OCR (remote only) | Stary, prawdopodobnie wchłonięty przez lukasz |

---

## Co zostało udowodnione (POC — branch lukasz)

### Architektura pipeline OCR która działa:

```
Uber Driver popup pojawia się (TYPE_APPLICATION_OVERLAY)
    ↓
AccessibilityService wykrywa event (TYPE_WINDOW_CONTENT_CHANGED)
    ↓
ScreenCaptureService (ForegroundService z typem mediaProjection)
    robi screenshot przez MediaProjection API
    → widzi WSZYSTKO na ekranie, w tym overlaye innych aplikacji
    ↓
PopupCropper — przycina dolne 60% ekranu (tam gdzie jest popup)
    ↓
ML Kit OCR (TextRecognition) — rozpoznaje tekst z bitmapy
    ↓
UberOcrParser — regex wyciąga: kwotę (zł), czas (min), dystans (km)
    ↓
OfferAnalyzer — liczy zł/h = kwota / (minuty/60)
    ↓
SystemOverlayManager — pokazuje belkę na górze ekranu z kolorem:
    GREEN (≥40 zł/h), YELLOW (≥32 zł/h), RED (<32 zł/h)
```

### Kluczowe odkrycia techniczne:

1. **`takeScreenshot()` (AccessibilityService API) nie widzi overlayów innych aplikacji** — Android celowo to blokuje dla bezpieczeństwa. Uber Driver popup jest `TYPE_APPLICATION_OVERLAY`, więc screenshot był pusty.

2. **MediaProjection API rozwiązuje ten problem** — robi screenshot identyczny z fizycznym przyciskiem, widzi wszystko. Wymaga jednorazowej zgody użytkownika ("Czy pozwolić nagrywać ekran?").

3. **MediaProjection wymaga ForegroundService z typem `mediaProjection`** (Android API 34+) — AccessibilityService nie wystarczy. Dlatego stworzono `ScreenCaptureService`.

4. **Emulator nie obsługuje `VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR`** — na emulatorze MediaProjection zwraca pusty/szary obraz. Na fizycznym telefonie działa poprawnie.

5. **Timing screenshota jest krytyczny** — debounce od ostatniego eventu powodował screenshot PO zniknięciu popupu (popup generuje eventy co ~100ms, debounce 1500ms = screenshot 1500ms po ostatnim evencie = popup już zniknął). Fix: screenshot przy PIERWSZYM evencie (300ms delay), potem cooldown 5s.

6. **OCR dokładnie odczytuje tekst polskiego Ubera** — kwoty z przecinkiem ("34,58 zł"), czas, dystans w nawiasach "(9,1 km)", "AKCEPTUJ".

---

## Co NIE jest jeszcze gotowe / znane problemy

| Problem | Priorytet |
|---------|-----------|
| Belka nie znika po zniknięciu popupu | Wysoki |
| Nie testowane na prawdziwej aplikacji Uber Driver | Wysoki |
| Brak automatycznego odświeżenia (kolejne zlecenia po cooldown 5s) | Średni |
| Brak obsługi różnych rozmiarów ekranu w PopupCropper | Średni |
| ScreenCaptureService nie restartuje się po wyłączeniu/włączeniu | Średni |
| Brak obsługi Wolt, Glovo | Niski |
| Brak ustawień progów zł/h | Niski |
| Brak trybu nocnego/dziennego w overlau | Niski |

---

## Następna akcja

EPIC 5 — Parser: `UberOcrParser` + unit testy (regex dla PL/UK/EN).

