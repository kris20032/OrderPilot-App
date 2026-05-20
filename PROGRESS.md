# OrderPilot — Status Postępu

**Ostatnia aktualizacja:** 2026-05-20 — IARC Live Rating Notice otrzymany (rating wiekowy LIVE na Google Play, Global Rating ID `6ef6cf91-410e-8191-8de0-3f365b7a6a7e`). Sygnał że proces review postępuje — NIE oznacza że apka jest LIVE. Wciąż czekamy na AAB review decision.
**Obecny etap:** **Production release SUBMITTED — czekamy na AAB review.** Timeline: Application for Production submitted 2026-05-16 11:03 AM → APPROVED 2026-05-17 17:32 (1-day turnaround) → Production release created 2026-05-18 22:48 (versionCode 6, v1.0.5, 177 krajów, release notes PL+EN skopiowane z Closed Testing v1.0.5) → IARC rating LIVE 2026-05-20. 3 zmiany wysłane do sprawdzenia: (1) wdrożenie 6 (1.0.5), (2) dodaj 176 krajów, (3) dodaj resztę świata. Pre-checks Google ~13 min, potem pełne AAB review ~1-3 dni. **Po approve → auto-publish → LIVE w Google Play.** Następny krok dla Krzysztofa: monitor email `krzychu.brzezi@gmail.com` + Console Inbox 1× dziennie.
**Aktywne branche:** `fix/v1.0.5-uber-popup-background` (bieżący — v1.0.5 LIVE), `play-store-prep` (parent), `feature/production-app` (synced), `main` (synced)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| **High** | **Rekrutacja testerów Closed Testing** — 11/12 opted-in (po cleanup zombie 04-30), buffer cel: 14-16 z ratio engagement ≥85% | W TOKU |
| **High** | **PrimeTestLab ticket** — czeka na odpowiedź managera (pool 120 vs 25, staged rollout, instructions delivery, geo distribution, refund process) | WYSŁANE 04-30 |
| **High** | **Znajomy taty (non-courier)** — dodać jutro do listy testerów (Tata zapyta) | TODO 05-01 |
| **High** | **3+ AAB updates podczas 14-day clock** — v1.0.2 ✅ LIVE 05-06, v1.0.3 ✅ LIVE 05-09 (Dominik confirmed 05-11), v1.0.4 ✅ LIVE 05-13 11:20 AM (#37 decimal + #38 combined thresholds, Marcin), v1.0.5 ✅ LIVE 05-13 20:39 (same-day hotfix Marcin Uber popup-over-other-app, regresja v1.0.2 Layer 2) | ✅ 4/3 DONE |
| **High** | **Application form prep** — 4 Fix Cards paste-ready w `docs/closed-testing-evidence.md` sekcja 5 (Q1.1-Q3.2 + cytaty Andrij/Dominik/Marcin + audit trail) | ✅ DRAFT GOTOWY 05-15, finalizacja Day 14 |
| **High** | ~~Store listing — feature graphic 1024×500 finalna wersja~~ | ✅ DONE (zweryfikowane w Console 05-12) |
| **High** | ~~Store listing — pełny opis PL + EN (max 4000 zn)~~ | ✅ DONE PL (75/80 short, 1564/4000 long, non-affiliation + financial disclaimer). EN skip do v1.1 (świadoma decyzja 05-12) |
| **High** | Weryfikacja Glovo na Xiaomi — tata nie zalogowany | Czeka na test |
| **Medium** | Merge `play-store-prep` → `feature/production-app` → `main` po zatwierdzeniu Production | Po Production |
| **Medium** | Profile social media + strategia promocji | Po Production (decyzja 04-29: nie ekspozycja przed launch) |
| **Low** | Crash na starszym telefonie (brat) — SettingsActivity | Nie odtworzony po reinstalacji (03-25), monitorowane |

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `play-store-prep` | Play Store release — wszystkie Batche + Phase 4 + signed AAB | **BIEŻĄCY** |
| `feature/production-app` | Główny branch produkcyjny (stable) | Synced z main (04-16) |
| `main` | Stabilna baza | Synced (04-16) |
| `polishing` | Splash screen (04-19, nie mergowany do main osobno) | Czeka na Production merge |
| `feature/app-icon-refresh` | Ikona A1 (zawarta w play-store-prep) | Czeka na Production merge |

**Zachowane nie-merged (nieaktywne):** `feature/fake-uber-driver` (testing tool), `feature/glovo-parser`, `fix/parser-false-positives-bolt-watch`, `claude/hardcore-darwin` (docs audyt)

> Merge flow po Production: `play-store-prep` → `feature/production-app` → `main`

---

## Co dalej — Roadmap

### Faza 1–1.6 — ZAKOŃCZONA (03-04 — 04-16)
Wszystkie implementacje, fixy, parsery, drag handle, język RU, audyty niezawodności.

### Faza 2: Play Store Closed Testing — W TOKU (04-22+)
- ✅ Signed AAB zbudowany (04-21)
- ✅ Dev Account + package name zarejestrowane (04-21/22)
- ✅ Privacy Policy na GitHub Pages (04-22)
- ✅ Release przesłany do review (04-22)
- ✅ Closed Testing zatwierdzone przez Google (przed 04-28)
- ✅ Pierwsi real testerzy active: Tata (Xiaomi), Andrij (UA multi-platform), Dominik
- ✅ Bug ammunition zebrane (Andrij: false-positive na portalach, Dominik: RU/UA lang + Samsung navbar)
- ✅ Cleanup zombies (04-30) — Pavlyshy wywalony, lista odśwież, 11/12 opted-in
- ✅ PrimeTestLab Enterprise zamówiony (04-30) — 25 testers + Approval Guarantee
- 🟡 **Czeka:** odpowiedź managera PrimeTestLab (ticket 04-30)
- 🟡 **Plan:** clock startuje 1-2 maja → Day 14 ≈ 15-16 maja → submit Production → review 3-7 dni → Production live ~20-23 maja
- ⏳ 3+ AAB updates podczas 14-day okna
- ⏳ Apply for Production

### Faza 3: Production + promocja
- Merge do main
- Social media + marketing do kurierów
- Iteracja na podstawie feedbacku testerów

---

## Ostatnie zmiany (od 04-19)

| Data | Zmiana |
|------|--------|
| 05-20 | **IARC Live Rating Notice** — email od IARC Content Ratings (`noreply@globalratings.com`) potwierdza że rating wiekowy OrderPilota jest LIVE na Google Play. Global Rating ID: `6ef6cf91-410e-8191-8de0-3f365b7a6a7e` (zachowany do przyszłych storefrontów — Amazon, Galaxy Store). NIE oznacza że apka LIVE — to tylko rating widoczny publicznie. Prawdopodobnie regeneracja przy okazji Production submit (177 krajów vs Closed Testing PL-only). Sygnał że pipeline review Google się rusza |
| 05-16 | **Day 14 — Application for Production SUBMITTED 11:03 AM** — Google Play Console „Apply for production access" wypełniony i wysłany. 8 odpowiedzi (3 sekcje: Closed test / About app / Production readiness), wszystkie pod 300/300 limit. Q3.1 wzmocnione „confirmed" attribution dla v1.0.3 (Dominik 05-11), v1.0.4 + v1.0.5 (Marcin 05-16 10:42 WhatsApp „It works ok now, thanks 😂") = 3/4 closed-loop fixes. Q2.3 install range: 0-10K (konserwatywnie realistic). Q1.2 recruitment ease: „Neither difficult or easy". Całość ~30 min od otwarcia formularza do submit. Console mówi „usually 7 days or less" — email decyzji na konto owner. NIE wgrywać v1.0.6 / nie rotować testerów podczas review. Po approve: D5 Production track release create + upload v1.0.5 AAB + paste release notes z `PRODUCTION_SUBMIT_CHECKLIST.md` D3.5 |
| 05-13 | **Day 10 — v1.0.4 build + upload (one day ahead of plan)** — AAB 24 MB wygenerowany (`OrderPilot/app/release/app-release.aab`, build 6m 9s). Pierwszy build padł po 27 min na `Failed to create MD5 hash for file` w `app/build/intermediates/` (przyczyna: Desktop jest synchronizowany z iCloud Drive + iCloud storage FULL → blokowanie plików). Fix: `./gradlew clean` (58s, z JAVA_HOME ustawionym na Android Studio JBR) → drugi build SUCCESS. AAB uploaded do Closed Testing → Alpha jako "1.0.4 - threshold fixes", release notes PL paste z `PRODUCTION_SUBMIT_CHECKLIST.md` D3, walidacja Console: version `5 (1.0.4)`, Target SDK 35, API 26+, 2 non-blocking warnings (R8 mapping + native debug symbols — same co v1.0.2/v1.0.3). Save → Publishing overview, quick checks ~14 min, czeka na Send for review |
| 05-13 | **Day 10 — v1.0.4 version bump** (`versionCode 4→5`, `versionName "1.0.3"→"1.0.4"` w `OrderPilot/app/build.gradle.kts`, commit `8e4686e`). Kod (commit `f58ec8c`) gotowy od Day 9. Play Console dashboard potwierdza 12 testerów opted-in 10/14 days continuous |
| 05-12 | **Play Console verification Day 9** — Default store listing zweryfikowany pole-po-polu i Save (czeka w Publishing overview do Day 14). Wpisany Video URL `youtube.com/watch?v=riSLy3qiySA` (Play Console NIE akceptuje Shorts URLs → konwersja z `/shorts/<id>` → `/watch?v=<id>`). Privacy Policy + Data Deletion URLs zweryfikowane w incognito. Status checklisty: C2 ✅, C4 ✅ (non-affiliation + financial disclaimer obecne), C8 ✅ (8 screens), C9 ✅, F1 ✅, F2 ✅ (Data safety = "doesn't collect" → URL field nie wymagany), F4 ✅. EN translation świadomie pominięta dla v1.0 (skip do v1.1 post-Production, redukcja rejection surface). NIE klikamy „Send for review" do Day 14 — strategia jednego pakietu (store listing + v1.0.4 AAB razem). `PRODUCTION_SUBMIT_CHECKLIST.md` zaktualizowany |
| 05-05 | **v1.0.2 — Andrij news portals fix** (multi-layer defense). Layer 1: strict foreground tracker (`lastForegroundPackage` z `TYPE_WINDOW_STATE_CHANGED`) + cross-check z `rootInActiveWindow`, wpięty jako guard w `processViaScreenshot`/`processViaAccessibilityTree` + przed `pipeline.process()` we wszystkich 3 call sites. Layer 2: wzmocnione `hasUberOverlayWithContent()` — wymóg patternu oferty (kwota+czas ≤120 znaków) lub markeru Ubera, zamiast samej obecności tekstu (zamyka MIUI phantom-overlay). Layer 3: watch mode reset — cancel `uberWatchJob`/`boltWatchJob` przy `WINDOW_STATE_CHANGED` z packagem spoza `watchedPackages`, plus dodatkowy guard w Uber watch loop. Layer 4: positive markers (10-15 fraz multi-language: PL/EN/UA/RU) wymagane w `UberOcrParser`/`BoltFoodOcrParser`/`WoltOcrParser` — news portal nie zawiera „Łącznie"/„Odbiór za"/„Bolt"/„Akceptuj". `versionCode=3, versionName="1.0.2"`. `future_polish_fixes.md` #36 zamknięty, `closed-testing-evidence.md` sekcja 4+5 zaktualizowane (Application Form ammunition gotowa). Pliki: `OrderPilotAccessibilityService.kt`, `OcrOfferParser.kt`, `UberOcrParser.kt`, `BoltFoodOcrParser.kt`, `WoltOcrParser.kt` |
| 04-30 | **Closed Testing operations day** — cleanup zombie (Pavlyshy wywalony), counter 11/12 opted-in, PrimeTestLab Enterprise $19.99 zamówiony (25 testers + Approval Guarantee), pool CSV 120 emaili pobrany, ticket do managera wysłany (pool vs package, staged rollout, instructions, geo, refund process). Tracker `test-data/closed-testing/testers_tracker.xlsx` + RECOMMENDATIONS.md utworzone. Strategia: bundle paid+brat+znajomy taty + odpowiedzi POCZEKAJ → clock auto-startuje przy 12+ |
| 04-29 | **Andrij real engagement** — UA real kurier zgłosił bug (false-positive overlay na portalach informacyjnych) + statystyki dnia (5h57m online, 9 zleceń) + general feedback. Bug do `future_polish_fixes.md`. Kandydat #1 na fix w v1.0.2 (pierwszy z 3+ wymaganych AAB updates). `docs/closed-testing-evidence.md` utworzony jako Application Form ammunition |
| 04-29 | **Dominik bugi** — UI language fallback RU/UA nie działa + Samsung nav bar zakrywa „Zapisz ustawienia" w Settings. Oba w `future_polish_fixes.md`, planowane v1.0.3 |
| 04-29 | **Recruitment events** — wizyty w Forum Gdańsk (Lucky, Ivan Black, Kuba, Andrew, Ivan UA, Artur, Gonzalo, Pavlyshy=zombie). Tata zaoferował znajomego non-couriera jako dodatkowy tester. Decision: skip stary telefon brata (same household IP risk > value) |
| 04-22 | **Closed Testing in review** — package `com.orderpilot.app` zarejestrowany, AAB + screenshots przesłane do Play Console |
| 04-22 | **GitHub Pages** — `kris20032.github.io/OrderPilot-App/legal/` działa (PP PL+EN, Data Deletion) |
| 04-21 | **Keystore** — `orderpilot-release.jks` (SHA256 `AC:2D:E9:...:0D:96`, ważny do 2053), backup Desktop+iPhone |
| 04-21 | **Signed AAB** — `app-release.aab` 23 MB, build SUCCESS |
| 04-21 | **Batch 4** — `signingConfigs` w `build.gradle.kts`, `keystore.properties.template` |
| 04-20 | **Phase 4 (PP)** — Privacy Policy + Data Deletion HTML, Data Safety form, Permissions Declarations, GitHub Pages setup |
| 04-20 | **Path A** — PP link w Settings, About + disclaimers, wording „monitoring"→„wykrywanie zleceń" |
| 04-20 | **Batch 3** — DisclosureActivity (KD4 Prominent Disclosure), DisclosureRepository, consent flag, flow gate w MainActivity |
| 04-20 | **Batch 2** — audyty: allowBackup, ScreenCapture guard, Logcat ring buffer, SaveLogs |
| 04-19 | **Batch 1** — manifest/config/strings play-store quick wins (exported flags, query intents, backup rules) |
| 04-19 | **Ikona A1 + Splash** — Arrow-Up Reticle (orange #F07830, navy #0D1B2A), Android 12+ SplashScreen API, adaptive icon |

---

## Keystore — dane techniczne

- Plik: `OrderPilot/keystore/orderpilot-release.jks` (poza git — `.gitignore`)
- Alias: `orderpilot` | Algo: RSA 2048 / SHA384withRSA | Ważny do: 2053-09-05
- SHA256: `AC:2D:E9:20:42:F0:59:BA:10:84:E0:63:2E:C8:EF:21:9F:E7:54:7C:69:A1:CC:3B:16:57:50:55:C8:13:0D:96`
- Backup: Desktop Mac + iPhone Files

---

<details>
<summary>Archiwum — ukończone zadania (do 04-19)</summary>

## Faza POC — ZAKOŃCZONA (2026-02-24 — 2026-02-27)
- Android Studio + JDK 17, FakeUberDriver, POC belka na telefonie

## EPIC 1–14 — UKOŃCZONE (2026-03-04 — 2026-03-05)
Fundament → Domain → Settings → Engine → Parser → Capture → OCR → Overlay → Pipeline → Service → UI → Billing → Testy E2E.

## Faza 1: Stabilizacja — ZAKOŃCZONA (03-04 — 03-27)
Testy produkcyjne u taty, Bolt 4/4, Wolt fix, Glovo fixy, multi-overlay, setup wizard per producent.

## Faza 1.5: Fixy z testów — ZAKOŃCZONA (03-29 — 04-10)
Universal extractAmount(), spaced retries Uber, Samsung TYPE_WINDOWS_CHANGED, audyt kodu v1+v2, state refactor MonitoringController, merge do main.

## Faza 1.6: Ulepszenia przed beta — ZAKOŃCZONA (04-10 — 04-16)
Skip MediaProjection API 30+, język rosyjski (values-ru, parsery RU, overlay ч/км/мин), audyt niezawodności (BootReceiver, watchdog race guard, health-check 2500ms), 6 fixów z testów (units per waluta, OCR relaxed, gotówka 💵, drag handle, startup flow grace period 30s), merge do main (04-16), hook auto-branch CLAUDE.md.

## Polishing — ZAKOŃCZONA (04-16 — 04-19)
#24 × center-vertical, #25 PLN przy EN, #26 Ikona A1 Arrow-Up Reticle, Splash screen Android 12+.

## Play Store prep — ZAKOŃCZONA (04-19 — 04-22)
Gap analysis, plan implementacyjny, Batch 1-4, Path A (PP link+disclaimers+wording), Phase 4 (PP hosting), keystore, signed AAB, Closed Testing release.

</details>
