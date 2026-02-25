# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-02-26
**Obecny etap:** TASK 5.1.1 ukończone — przejście do TASK 6.1.1
**Cel:** Beta APK do testów na telefonie z Androidem

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
| 8 | TASK 6.1.1 — Integracja | ⏳ Następny |
| 9 | TASK 7.1.1 — START/STOP UI | |

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

---

## W trakcie

**Krzysztof:** TASK 6.1.1 — Integracja
**Tata:** —
**Łukasz:** —

---

## Problemy / Notatki

- Zdecydowano: AccessibilityService zamiast MediaProjection+OCR (lżejsze, szybsze, dokładniejsze)
- Package zmieniony: `com.courierassist` → `com.courierassist.app`
- UI zmieniony: ViewBinding + XML (zamiast Jetpack Compose)
- Pierwsza platforma: Uber (potem Wolt, Glovo)

---

## Struktura repo

```
CourierAssist-App/
├── .gitignore
├── README.md
├── RULES.md
├── PROGRESS.md      ← ten plik
├── docs/
│   ├── ARCHITECTURE.md
│   ├── PLAN.md
│   └── TASKS.md
└── testing/
    ├── glovo/
    ├── ubereats/
    └── wolt/
```

---

## Następna akcja

**TERAZ:** TASK 6.1.1 — Integracja (Opus)
