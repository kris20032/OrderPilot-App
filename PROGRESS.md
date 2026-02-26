# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-02-26
**Obecny etap:** Debugowanie — popup Uber Driver nie jest wykrywany przez AccessibilityService
**Cel:** Zdiagnozować i naprawić odczyt popupu zlecenia w Uber Driver.

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

Szczegóły zadań: [`docs/TASKS.md`](docs/TASKS.md)

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

---

## Aktywne branche

| Branch | Cel | Status | Kto |
|--------|-----|--------|-----|
| `main` | Stabilna baza | ✅ Aktualny | — |
| `fix/accessibility-windows` | Fix: getWindows() zamiast rootInActiveWindow | 🔄 Do przetestowania na telefonie | Krzysztof |
| `feature/fake-uber-driver` | Aplikacja testowa symulująca popup Uber | 📋 Zaplanowany | — |

> Nowe zadanie = nowy branch. Szczegóły: `RULES.md` sekcja 4.

---

## W trakcie

**Krzysztof:** Test brancha `fix/accessibility-windows` na telefonie Taty — czy popup zlecenia jest teraz wykrywany.
**Tata:** Oczekiwanie na nową wersję APK.
**Łukasz:** —

---

## Problemy / Notatki

- **Diagnoza (2026-02-26):** Popup zlecenia Uber Driver pojawia się w osobnym oknie systemu. `rootInActiveWindow` zwracało drzewo głównego okna (puste gdy popup aktywny). Fix: `getAllRootNodes()` skanuje wszystkie okna przez `getWindows()` + flaga `flagRetrieveInteractiveWindows`.
- **Wdrożono GitHub Flow** (2026-02-26): od teraz każda zmiana = osobny branch, merge do main tylko gdy działa.
- Zdecydowano: AccessibilityService zamiast MediaProjection+OCR
- Package: `com.courierassist.app`, UI: ViewBinding + XML
- Pierwsza platforma: Uber (potem Wolt, Glovo)

---

## Następna akcja

1. Build APK z brancha `fix/accessibility-windows` w Android Studio
2. Wyłącz i włącz CourierAssist w Settings → Accessibility (nowy config XML)
3. Test na telefonie Taty — poczekaj na zlecenie, sprawdź logcat
4. Jeśli działa → merge do main. Jeśli nie → kolejna diagnoza z logów.
