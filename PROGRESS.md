# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-02-27
**Obecny etap:** POC ukończony — zaczynamy właściwą aplikację od zera
**Cel następny:** Ustalenie planu i architektury dla produkcyjnej wersji → nowy branch

---

## Faza POC — ZAKOŃCZONA

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md, TASKS.md — 2026-02-25
- [x] FakeUberDriver: aplikacja testowa symulująca popup Uber — 2026-02-26
- [x] **POC MediaProjection + OCR: belka pojawia się na telefonie przy popupie FakeUberDriver** — 2026-02-27

POC udowodnił że pipeline działa. Kod POC zostaje na `main` jako punkt odniesienia — NIE kontynuujemy go.

---

## Właściwa aplikacja — DO ZROBIENIA

Plan i architektura: do ustalenia (sesja po POC).

| # | Task | Status |
|---|------|--------|
| — | Plan właściwej aplikacji | Do ustalenia |
| — | Architektura właściwej aplikacji | Do ustalenia |
| — | Implementacja | Nie zaczęta |

---

## Aktywne branche

| Branch | Cel | Status | Kto |
|--------|-----|--------|-----|
| `main` | Stabilna baza z działającym POC | ✅ Zablokowany (tylko docs) | — |
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

## Następna akcja

Ustalić plan i architekturę właściwej aplikacji → stworzyć nowy branch → zacząć implementację od zera.

