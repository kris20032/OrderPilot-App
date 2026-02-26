# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-02-26
**Obecny etap:** Architektura OCR — pipeline zaimplementowany, gotowy do testu na telefonie
**Cel:** ✅ Pipeline Screenshot + ML Kit OCR wdrożony. Następny krok: zbudować APK i przetestować na telefonie ojca.

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
| 10 | TASK A.1 — minSdk → 30 | ✅ Ukończone |
| 11 | TASK A.2 — Zależność ML Kit bundled | ✅ Ukończone |
| 12 | TASK A.3 — accessibility_config.xml: canTakeScreenshot | ✅ Ukończone |
| 13 | TASK B.1 — ScreenCaptureManager | ✅ Ukończone |
| 14 | TASK B.2 — PopupCropper | ✅ Ukończone |
| 15 | TASK C.1 — OcrEngine (ML Kit wrapper) | ✅ Ukończone |
| 16 | TASK D.1 — UberOcrParser | ✅ Ukończone |
| 17 | TASK D.2 — Offer +distanceKm | ✅ Ukończone |
| 18 | TASK D.3 — Overlay +dystans | ✅ Ukończone |
| 19 | TASK E.1 — Nowy pipeline w CourierAccessibilityService | ✅ Ukończone |
| 20 | TASK E.2 — Usunąć stary UberParser flow | ✅ Ukończone |
| 21 | TASK F.1 — Test z hardcoded screenshot | 📋 Opcjonalne |
| 22 | TASK F.2 — Test na telefonie Taty | 🔜 Następny krok |

Szczegóły zadań: [`docs/TASKS.md`](docs/TASKS.md) | Szczegóły OCR: [`docs/PLAN_OCR.md`](docs/PLAN_OCR.md)

---

## Ukończone

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md, TASKS.md — 2026-02-25
- [x] Architektura: zdecydowano AccessibilityService (zamiast MediaProjection+OCR) — 2026-02-25
- [x] TASK 1.1.1: Inicjalizacja projektu Android (Kotlin, ViewBinding, minSdk 26) — 2026-02-26
- [x] TASK 3.1.1: Modele domenowe (Offer, Platform, AnalysisResult, ProfitLevel) — 2026-02-26
- [x] TASK 3.2.1: OfferAnalyzer (GREEN/YELLOW/RED, safe division) — 2026-02-26
- [x] TASK 2.1.1: CourierAccessibilityService (Uber Driver filter, manifest, config) — 2026-02-26
- [x] TASK 4.1.1: OfferParser interface (canHandle, parse) — 2026-02-26
- [x] TASK 4.2.1: UberParser + ParserRegistry (PL/EN/UKR, regex) — 2026-02-26
- [x] TASK 5.1.1: SystemOverlayManager (TYPE_APPLICATION_OVERLAY, kolory, layout) — 2026-02-26
- [x] TASK 6.1.1: Integracja pipeline (event→parser→analyzer→overlay, debounce 300ms) — 2026-02-26
- [x] TASK 7.1.1: MainActivity START/STOP UI (ViewBinding, SharedPreferences) — 2026-02-26
- [x] Diagnoza: popup Uber Driver nie eksponuje tekstu przez AccessibilityNodeInfo (Canvas/Compose rendering) — 2026-02-26
- [x] Decyzja architektoniczna: Screenshot + ML Kit OCR on-device (zamiast parsowania drzewa UI) — 2026-02-26
- [x] Decyzja: minSdk → 30 (Android 11), ML Kit bundled, implementacja przez osobnych agentów — 2026-02-26
- [x] TASK A.1: minSdk podniesiony do 30 — 2026-02-26
- [x] TASK A.2: Zależność ML Kit bundled dodana (`com.google.mlkit:text-recognition:16.0.1`) — 2026-02-26
- [x] TASK A.3: `canTakeScreenshot="true"` w accessibility_config.xml — 2026-02-26
- [x] TASK B.1: `capture/ScreenCaptureManager.kt` — screenshot on-demand, throttle 1s, software bitmap — 2026-02-26
- [x] TASK B.2: `capture/PopupCropper.kt` — wycina dolne 60% ekranu (popup Uber) — 2026-02-26
- [x] TASK C.1: `ocr/OcrEngine.kt` — ML Kit Latin wrapper, callback-based — 2026-02-26
- [x] TASK D.1: `parser/UberOcrParser.kt` — regex na kwotę/czas/dystans/accept PL+EN+UA — 2026-02-26
- [x] TASK D.2: `domain/Offer.kt` — dodane pole `distanceKm: Double? = null` — 2026-02-26
- [x] TASK D.3: `overlay/SystemOverlayManager.kt` — belka pokazuje dystans jeśli dostępny — 2026-02-26
- [x] TASK E.1+E.2: `CourierAccessibilityService` przepisany na nowy pipeline OCR, stary flow z AccessibilityNodeInfo usunięty — 2026-02-26

---

## Aktywne branche

| Branch | Cel | Status | Kto |
|--------|-----|--------|-----|
| `main` | Stabilna baza | ✅ Aktualny | — |
| `fix/accessibility-windows` | Fix: getWindows() zamiast rootInActiveWindow | ⏸ Wstrzymany (zastąpiony przez OCR) | — |
| `feature/ocr-pipeline` | Nowy pipeline: Screenshot + ML Kit OCR | ✅ Zaimplementowany na main | — |
| `feature/fake-uber-driver` | Aplikacja testowa symulująca popup Uber | 📋 Zaplanowany | — |

> Nowe zadanie = nowy branch. Szczegóły: `RULES.md` sekcja 4.

---

## W trakcie

**Krzysztof:** —
**Tata:** —
**Łukasz:** Koordynacja agentów implementujących etapy A–F pipeline'u OCR.

---

## Problemy / Notatki

- **Diagnoza (2026-02-26):** Popup zlecenia Uber Driver pojawia się w osobnym oknie. `rootInActiveWindow` zwracało puste drzewo. Fix `getAllRootNodes()` (getWindows()) też nie pomógł — Uber nie eksponuje tekstu przez AccessibilityNodeInfo w ogóle (Canvas/Compose rendering).
- **Decyzja architektoniczna (2026-02-26):** Rezygnujemy z parsowania AccessibilityNodeInfo dla Ubera. Nowe podejście: `AccessibilityService.takeScreenshot()` + Google ML Kit Text Recognition (on-device, bundled). Szczegóły: `docs/PLAN_OCR.md`.
- **Podjęte decyzje (2026-02-26):** minSdk→30, ML Kit bundled (`com.google.mlkit:text-recognition:16.0.1`), implementacja przez osobnych agentów per etap.
- **Wdrożono GitHub Flow** (2026-02-26): od teraz każda zmiana = osobny branch, merge do main tylko gdy działa.
- Package: `com.courierassist.app`, UI: ViewBinding + XML
- Pierwsza platforma: Uber (potem Wolt, Glovo)

---

## Następna akcja

**Pipeline OCR zaimplementowany.** Następne kroki:

1. Zbudować APK: `cd CourierAssist && .\gradlew.bat assembleDebug`
2. Zainstalować APK na telefonie ojca: `adb install app/build/outputs/apk/debug/app-debug.apk`
3. Włączyć usługę w Ustawieniach > Ułatwienia dostępu > CourierAssist
4. Uruchomić Uber Driver i poczekać na zlecenie
5. Sprawdzić Logcat: `adb logcat -s CourierAssist` — szukać linii `OCR lines:` i `Offer detected:`

> Ewentualne problemy: jeśli `takeScreenshot()` zwraca null — sprawdzić czy serwis ma uprawnienie BIND_ACCESSIBILITY_SERVICE aktywne.
