# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-02-25
**Obecny etap:** Dokumentacja gotowa — przejście do TASK 1.1.1
**Cel:** Beta APK do testów na telefonie z Androidem

---

## Status zadań

| # | Task | Status |
|---|------|--------|
| 1 | TASK 1.1.1 — Inicjalizacja projektu | ⏳ Następny |
| 2 | TASK 3.1.1 — Modele domenowe | |
| 3 | TASK 3.2.1 — OfferAnalyzer | |
| 4 | TASK 2.1.1 — AccessibilityService | |
| 5 | TASK 4.1.1 — OfferParser interface | |
| 6 | TASK 4.2.1 — UberParser | |
| 7 | TASK 5.1.1 — Overlay | |
| 8 | TASK 6.1.1 — Integracja | |
| 9 | TASK 7.1.1 — START/STOP UI | |

Szczegóły zadań: [`docs/TASKS.md`](docs/TASKS.md)

---

## Ukończone

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md, TASKS.md — 2026-02-25
- [x] Architektura: zdecydowano AccessibilityService (zamiast MediaProjection+OCR) — 2026-02-25

---

## W trakcie

**Krzysztof:** Gotowy do TASK 1.1.1 — Inicjalizacja projektu
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

**TERAZ:** TASK 1.1.1 — Inicjalizacja projektu Android (Kotlin, ViewBinding, minSdk 26)
