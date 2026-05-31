# OrderPilot — Archiwum logu (PROGRESS_ARCHIVE)

> Zamrożone, stare wpisy wyniesione z `PROGRESS.md`, żeby aktywny log był krótki i tani do czytania.
> Czytany **rzadko** — tylko gdy trzeba odtworzyć „jak tu doszliśmy". Tu kolejność **oldest-first** (narracja od początku).
> Aktywny log: `PROGRESS.md`. Stan bieżący: `todo.md`. Decyzje: `DECISIONS.md`.

---

## Fazy projektu (skrót historyczny)

### Faza POC — ZAKOŃCZONA (2026-02-24 — 2026-02-27)
Android Studio + JDK 17, FakeUberDriver, POC belka na telefonie.

### EPIC 1–14 — UKOŃCZONE (2026-03-04 — 2026-03-05)
Fundament → Domain → Settings → Engine → Parser → Capture → OCR → Overlay → Pipeline → Service → UI → Billing → Testy E2E.

### Faza 1: Stabilizacja — ZAKOŃCZONA (03-04 — 03-27)
Testy produkcyjne u taty, Bolt 4/4, Wolt fix, Glovo fixy, multi-overlay, setup wizard per producent.

### Faza 1.5: Fixy z testów — ZAKOŃCZONA (03-29 — 04-10)
Universal extractAmount(), spaced retries Uber, Samsung TYPE_WINDOWS_CHANGED, audyt kodu v1+v2, state refactor MonitoringController, merge do main.

### Faza 1.6: Ulepszenia przed beta — ZAKOŃCZONA (04-10 — 04-16)
Skip MediaProjection API 30+, język rosyjski (values-ru, parsery RU, overlay ч/км/мин), audyt niezawodności (BootReceiver, watchdog race guard, health-check 2500ms), 6 fixów z testów, merge do main (04-16), hook auto-branch CLAUDE.md.

### Polishing — ZAKOŃCZONA (04-16 — 04-19)
#24 × center-vertical, #25 PLN przy EN, #26 Ikona A1 Arrow-Up Reticle, Splash screen Android 12+.

### Play Store prep — ZAKOŃCZONA (04-19 — 04-22)
Gap analysis, plan implementacyjny, Batch 1–4, Path A (PP link+disclaimers+wording), Phase 4 (PP hosting), keystore, signed AAB, Closed Testing release.

### Faza 2: Play Store Closed Testing — ZAKOŃCZONA (04-22 — 05-16)
Signed AAB (04-21), Dev Account + package (04-22), Privacy Policy na GitHub Pages, Closed Testing approved. Real testerzy + bug ammunition: Andrij (news portals false-positive → v1.0.2), Dominik (RU/UA lang + Samsung navbar → v1.0.3), Marcin (decimal + thresholds → v1.0.4, Uber popup → v1.0.5). Cleanup zombies (04-30), PrimeTestLab Enterprise (~40 installów), 4 AAB updates podczas okna, 14-day clock COMPLETED (Day 0 = 05-03, Day 14 = 05-16), 53 active testers.

---

## Log szczegółowy — kwiecień 2026 (04-19 → 04-30)

### 2026-04-19 — Batch 1 + Ikona A1 + Splash
Batch 1: manifest/config/strings play-store quick wins (exported flags, query intents, backup rules). Ikona A1 Arrow-Up Reticle (orange #F07830, navy #0D1B2A), Android 12+ SplashScreen API, adaptive icon.

### 2026-04-20 — Batch 2/3 + Path A + Phase 4
Batch 2: audyty (allowBackup, ScreenCapture guard, Logcat ring buffer, SaveLogs). Batch 3: DisclosureActivity (Prominent Disclosure), consent flag, flow gate w MainActivity. Path A: PP link w Settings, About + disclaimers, wording „monitoring"→„wykrywanie zleceń". Phase 4: Privacy Policy + Data Deletion HTML, Data Safety form, Permissions Declarations, GitHub Pages.

### 2026-04-21 — Signed AAB + Batch 4 + keystore
Batch 4: `signingConfigs` w `build.gradle.kts`, `keystore.properties.template`. Keystore `orderpilot-release.jks` utworzony (dane techniczne → `RULES.md`). Signed `app-release.aab` 23 MB, build SUCCESS.

### 2026-04-22 — Closed Testing in review + GitHub Pages
Package `com.orderpilot.app` zarejestrowany, AAB + screenshots przesłane do Play Console. GitHub Pages `kris20032.github.io/OrderPilot-App/legal/` działa (PP PL+EN, Data Deletion).

### 2026-04-29 — Real engagement + bugi testerów + recruitment
Andrij (UA real kurier): bug false-positive na portalach informacyjnych + statystyki dnia (5h57m, 9 zleceń) → `docs/closed-testing-evidence.md`, kandydat #1 na v1.0.2. Dominik: UI language fallback RU/UA + Samsung nav bar zakrywa „Zapisz ustawienia" → v1.0.3. Recruitment events: wizyty w Forum Gdańsk (kilku testerów + zombie). Decision: skip stary telefon brata (same household IP risk > value).

### 2026-04-30 — Closed Testing operations day
Cleanup zombie (Pavlyshy wywalony), counter 11/12 opted-in, PrimeTestLab Enterprise $19.99 zamówiony (25 testers + Approval Guarantee), pool CSV 120 emaili, ticket do managera. Tracker `test-data/closed-testing/testers_tracker.xlsx` + RECOMMENDATIONS.md. Strategia: bundle paid+brat+znajomy taty, clock auto-startuje przy 12+.
