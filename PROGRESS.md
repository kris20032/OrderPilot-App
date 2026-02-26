# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-02-27
**Obecny etap:** Proof of Concept działa — belka pokazuje się na telefonie przy popupie FakeUberDriver
**Cel następny:** Przepisanie aplikacji od zera na nowym branchu — czysta, produkcyjna wersja

---

## Status zadań

| # | Task | Status |
|---|------|--------|
| 1 | TASK 1.1.1 — Inicjalizacja projektu | ✅ Ukończone |
| 2 | TASK 3.1.1 — Modele domenowe | ✅ Ukończone |
| 3 | TASK 3.2.1 — OfferAnalyzer | ✅ Ukończone |
| 4 | TASK 2.1.1 — AccessibilityService | ✅ Ukończone |
| 5 | TASK 4.1.1 — OfferParser interface | ✅ Ukończone |
| 6 | TASK 4.2.1 — UberParser | ✅ Ukończone |
| 7 | TASK 5.1.1 — Overlay | ✅ Ukończone |
| 8 | TASK 6.1.1 — Integracja | ✅ Ukończone |
| 9 | TASK 7.1.1 — START/STOP UI | ✅ Ukończone |
| 10 | POC — MediaProjection + OCR pipeline | ✅ Działa na FakeUberDriver |

---

## Ukończone

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md, TASKS.md — 2026-02-25
- [x] TASK 1.1.1 — TASK 7.1.1: Pełny pipeline AccessibilityService — 2026-02-26
- [x] FakeUberDriver: aplikacja testowa symulująca popup Uber (osobny projekt, branch feature/fake-uber-driver) — 2026-02-26
- [x] **POC MediaProjection + OCR: belka pojawia się na telefonie przy popupie FakeUberDriver** — 2026-02-27

---

## Aktywne branche

| Branch | Cel | Status | Kto |
|--------|-----|--------|-----|
| `main` | Stabilna baza z działającym POC | ✅ Aktualny | — |
| `lukasz` | Gałąź robocza Łukasza (MediaProjection + OCR) | ✅ Zmergowana do main | Łukasz |
| `feature/fake-uber-driver` | Aplikacja testowa FakeUberDriver | ✅ Gotowa | — |

> Nowe zadanie = nowy branch. Od teraz ŻADNYCH zmian bezpośrednio na main.

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

## Plan następnego etapu

**Nowy branch od zera** — czysta, produkcyjna wersja aplikacji:

1. Przepisać `CourierAccessibilityService` — dodać wykrywanie zniknięcia popupu (chować belkę gdy popup znika)
2. Produkcyjny `ScreenCaptureService` — lepsze zarządzanie cyklem życia, restart po utracie uprawnień
3. Przetestować na prawdziwej aplikacji Uber Driver (telefon Taty z aktywnym kontem kuriera)
4. Dopasować `PopupCropper` do rzeczywistego layoutu Ubera
5. Dopasować `UberOcrParser` do rzeczywistego formatu tekstu Ubera
6. Dodać chowanie belki (timeout lub wykrywanie zniknięcia popupu)

---

## Następna akcja

1. Zainstalować obie aplikacje na telefonie Taty (prawdziwy kurier Uber)
2. Przetestować z prawdziwym zleceniem Uber Driver
3. Zebrać logi i debug_crop.png — zobaczyć jak wygląda prawdziwy popup
4. Na podstawie testów: nowy branch z poprawkami

