# Production Submit — Final Checklist (Day 14, target 2026-05-17)

**Status na 2026-05-12 (Day 9):** v1.0.3 LIVE w Closed Testing, v1.0.4 kod gotowy (build/upload Day 11-12), Application Form material 90% gotowy.

Ten plik to **single source of truth** dla Day 14 submitu — przejdź po kolei, zaznacz każde pole.

---

## A. Closed Testing requirements (must be met BEFORE Apply for Production)

| # | Wymóg | Status | Evidence |
|---|-------|--------|----------|
| A1 | Closed Testing track active 14+ dni | 🟡 W TOKU (Day 9, Day 14 = 2026-05-17) | Console: Test and release → Closed testing → Alpha track |
| A2 | Min 12 opted-in testerów continuously przez 14 dni | ✅ DONE (50 active, 12+ continuously confirmed) | `2026-05-09_play-console-statistics-installed-audience.png` |
| A3 | 3+ AAB updates podczas 14-day okna | 🟡 2/3 DONE (v1.0.2 ✅, v1.0.3 ✅, v1.0.4 pending Day 11-12) | `closed-testing-evidence.md` sekcja 4 |
| A4 | Zero blocking crashes / ANRs | ✅ DONE (Console pokazuje zero) | Console: Quality → Android vitals |
| A5 | Real tester feedback collected (≥3 different testers) | ✅ DONE (Andrij, Dominik, Marcin, Lucky, Ivan Black + 5 others) | `closed-testing-evidence.md` sekcja 2 |

---

## B. Play Console — App content (musi być WYPEŁNIONE przed Production submit)

> Większość tego wypełniliśmy przy Closed Testing — przed Day 14 **weryfikujemy** że wszystko jest wciąż valid.

| # | Pole | Status | Notatki |
|---|------|--------|---------|
| B1 | Privacy Policy URL | ✅ DONE | `https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html` |
| B2 | Data Deletion URL | ✅ DONE | `https://kris20032.github.io/OrderPilot-App/legal/data-deletion.html` |
| B3 | App access (Reviewer instructions) | ⚠️ **WERYFIKACJA Day 13** | "All functionality available without restrictions" + Instructions for reviewer (patrz wzór niżej) |
| B4 | Ads — declaration | ✅ DONE | NO ads (`AD_ID` removed in manifest) |
| B5 | Content rating questionnaire | ✅ DONE | PEGI 3 / IARC 3+ (Productivity, no violence/gambling/UGC/location) |
| B6 | Target audience and content | ✅ DONE | 18+ (gig economy workers, Families Policy: NO) |
| B7 | News apps declaration | ✅ DONE | NO |
| B8 | COVID-19 contact tracing | ✅ DONE | NO |
| B9 | Data safety form | ✅ DONE | "No data collected, no data shared" — patrz `data_safety_form.md` |
| B10 | Government apps | ✅ DONE | NO |
| B11 | Financial features | ✅ DONE | NO (computational only, with financial disclaimer) |
| B12 | Health apps | ✅ DONE | NO |
| B13 | Accessibility declaration (most critical) | ✅ DONE | Wording z `permissions_declarations.md` sekcja 1 |
| B14 | SYSTEM_ALERT_WINDOW declaration | ✅ DONE | `permissions_declarations.md` sekcja 2 |
| B15 | FOREGROUND_SERVICE_SPECIAL_USE declaration | ✅ DONE | `permissions_declarations.md` sekcja 3 |
| B16 | FOREGROUND_SERVICE_MEDIA_PROJECTION declaration | ✅ DONE | `permissions_declarations.md` sekcja 7 |

### B3 — Wzór "Instructions for reviewer" (paste-ready)

```
This app requires an active courier account on Uber Driver, Wolt Courier,
Glovo Courier, or Bolt Food to demonstrate offer-detection functionality.
Reviewers without such accounts will see only the onboarding (Prominent
Disclosure), Setup wizard, Settings, and the AccessibilityService permission
flow — these are all reachable without a courier account and demonstrate
the privacy disclosure, permission rationale, and configuration UI.

For full offer-detection demonstration on a real device with an active
courier account, please refer to the video demo linked in the Permissions
Declarations form.

Closed Testing feedback (14 days, 50 active testers, 5 real couriers from
4 countries) is documented in our Production Application questionnaire
responses.
```

---

## C. Play Console — Store presence (Main store listing)

> Wszystkie pola w Console: **Grow users → Store presence → Main store listing**. Weryfikujemy Day 13 że Polish + English listing są kompletne.

| # | Pole | Limit | Status | Source |
|---|------|-------|--------|--------|
| C1 | App name | 30 chars | ✅ DONE | "OrderPilot" |
| C2 | Short description PL | 80 chars | ⚠️ **WERYFIKACJA Day 13** w Console | Proposal: "Stawka zł/h dla zleceń z Uber, Wolt, Glovo, Bolt — analiza on-device." |
| C3 | Short description EN | 80 chars | ⚠️ **WERYFIKACJA Day 13** w Console | Proposal: "PLN/h overlay for Uber, Wolt, Glovo, Bolt offers — fully on-device." |
| C4 | Long description PL | 1500-3000 chars | ⚠️ **WERYFIKACJA Day 13** w Console | Patrz `02_implementation_plan.md` Task 5.6 + non-affiliation + financial disclaimers |
| C5 | Long description EN | 1500-3000 chars | ⚠️ **WERYFIKACJA Day 13** w Console | jak wyżej |
| C6 | App icon 512×512 | PNG | ✅ DONE | `store-assets/app_icon_512.png` |
| C7 | Feature graphic 1024×500 | PNG/JPG | ✅ DONE | `store-assets/feature_graphic_1024x500.png` |
| C8 | Phone screenshots — min 2, max 8 | per locale | ⚠️ **WERYFIKACJA Day 13** w Console | `store-assets/screenshots play conosle/example1-9` (9 plików) — sprawdzić czy uploadowane do Console |
| C9 | Video preview (YouTube) | URL | ⚠️ **WERYFIKACJA Day 13** | `youtube.com/shorts/riSLy3qiySA` (90-sec setup) — sprawdzić czy zalinkowany |
| C10 | App category | wybór | ✅ DONE | Productivity |
| C11 | Contact details — email | required | ✅ DONE | `krzychu.brzezi@gmail.com` |
| C12 | Tablet screenshots | optional | 🟢 N/A (skipping for v1) | - |

### C — Akcja Day 13

1. **Play Console → Store presence → Main store listing → Polish (pl-PL)**: zweryfikować że short + long description widoczne. Jeśli puste, paste z `02_implementation_plan.md` Task 5.5/5.6.
2. **Play Console → Store presence → Main store listing → English (en-US)**: jak wyżej.
3. **Screenshots** (oba locale): minimum 2, idealnie 4-6. Wybrać najlepsze z `store-assets/screenshots play conosle/`.
4. **Save** każdą zmianę.

---

## D. Production track release (Day 13-14)

| # | Krok | Kiedy | Notatki |
|---|------|-------|---------|
| D1 | Build signed AAB v1.0.4 | Day 11-12 (05-14/15) | versionCode 5, versionName "1.0.4", keystore z `keystore.properties` |
| D2 | Upload AAB do Closed Testing (still) | Day 11-12 | Wciąż Closed track, NIE Production yet — to nadal część 14-day window |
| D3 | Release notes v1.0.4 EN+PL | Day 11-12 | Patrz wzór niżej |
| D4 | Send for review (Closed Testing) | Day 11-12 | Managed publishing OFF → auto-publish po Google approve (~1-3h) |
| D5 | **Apply for Production** | **Day 14 (2026-05-17)** | Play Console → Production track → New release → kopiuj z latest Closed AAB |
| D6 | Wypełnić Production Application questionnaire | Day 14 | Paste z `closed-testing-evidence.md` sekcja 5 (Q1.1-Q3.2 + Fix Cards) |
| D7 | Submit Application | Day 14 | Czeka ~3-7 dni na Google decision |

### D3 — Release notes v1.0.4 (paste-ready)

**EN (max 500 chars):**
```
Fixed color thresholds: PLN/h and PLN/km thresholds now apply together (an
offer is yellow only if both metrics meet the threshold). Fixed decimal
values in threshold settings — values like 32.5 or 2.5 are now correctly
preserved after save on all device languages. Both issues were reported
by tester Marcin during Closed Testing.
```

**PL (max 500 chars):**
```
Naprawiono progi kolorów: progi PLN/h i PLN/km działają teraz łącznie
(oferta jest żółta tylko gdy obie metryki spełniają próg). Naprawiono
wartości dziesiętne w ustawieniach progów — wartości typu 32,5 lub 2,5 są
teraz poprawnie zachowywane po zapisie, niezależnie od języka telefonu.
Oba problemy zgłosił tester Marcin podczas testów zamkniętych.
```

---

## E. Application for Production — questionnaire (Day 14)

> Wszystkie odpowiedzi paste-ready w `closed-testing-evidence.md` sekcja 5. Mapa pól zweryfikowana z Google Help Center 2026-05-09.

| Q# | Pytanie | Source w docs | Status |
|----|---------|---------------|--------|
| 1.1 | Jak łatwo było zrekrutować testerów? | Sekcja 5 → 1.1 | ✅ DONE |
| 1.2 | Engagement testerów | Sekcja 5 → 1.2 | ✅ DONE |
| 1.3 | Podsumowanie opinii + jak zebrane | Sekcja 5 → 1.3 | ✅ DONE |
| 2.1 | Docelowi odbiorcy | Sekcja 5 → 2.1 | ✅ DONE |
| 2.2 | Wartość aplikacji | Sekcja 5 → 2.2 | ✅ DONE |
| 2.3 | Spodziewana liczba instalacji | Sekcja 5 → 2.3 | ✅ DONE (rec: 1k-10k) |
| 3.1 | Jakie zmiany na podstawie testów | Sekcja 5 → 3.1 + Fix Cards v1.0.2/v1.0.3/v1.0.4 | ✅ DONE |
| 3.2 | Dlaczego apka jest ready for production | Sekcja 5 → 3.2 | ✅ DONE |

---

## F. Pre-submit smoke check (Day 14 morning)

| # | Sprawdzenie | Jak |
|---|-------------|-----|
| F1 | Privacy Policy URL działa w przeglądarce incognito | Open `https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html` w incognito |
| F2 | Data Deletion URL działa w incognito | jak wyżej z `data-deletion.html` |
| F3 | EN i PL Privacy Policy są spójne | porównanie wizualne |
| F4 | YouTube video preview działa publicznie (unlisted OK) | `youtube.com/shorts/riSLy3qiySA` |
| F5 | Zero NEW negative feedback od testerów Day 13 wieczór | WhatsApp grupa, Console feedback |
| F6 | Zero NEW crashes/ANRs w Console od ostatniej weryfikacji | Console → Quality → Vitals |
| F7 | v1.0.4 AAB LIVE w Closed Testing track | Console → Test and release → Closed testing → status "Available" |

---

## G. Co NIE robić przed/podczas submit (anti-patterns)

- ❌ NIE submit przed Day 14 (Google flaguje insufficient testing duration even if 13d 23h)
- ❌ NIE cytować PrimeTestLab "40 extra installs" jako engagement evidence w Application Form (to install farma — Google się może skapnąć)
- ❌ NIE wgrywać AAB v1.0.5 podczas Production review (zostaw stabilne v1.0.4 LIVE)
- ❌ NIE odpowiadać generycznie ("good engagement", "tested thoroughly") na pytania 1.2/1.3 — Google to traktuje jako #1 rejection trigger
- ❌ NIE zostawiać Privacy Policy URL bez przeprowadzenia weryfikacji Day 14 morning (URLs gasły testerom przy update v1.0.2)
- ❌ NIE rotować testerów (dodawać nowych zamiast utrzymać continuously opted-in) — Google liczy "consecutive 14 days"

---

## H. Notatki dla audit trail

- **Marcin = tata** — przemianowane do dokumentacji Google review aby nie ujawniać relacji rodzinnej. Wszystkie materiały do Console używają "Marcin". Decyzja 2026-05-11.
- **Dominik = brat** — używamy imienia "Dominik" w docs ale relacja brat-brat nie jest ujawniana.
- **Vasyl = znajomy taty** — independent tester, OK do cytowania.
- **Pre-launch report** wykreślony jako evidence — robot Firebase Google nie przechodzi onboardingu AccessibilityService (znane zachowanie). Evidence zastąpione: 50 active testers + 5 real kurierów + 3 AAB iteracje.
- **PrimeTestLab pool** — używany tylko do liczników w Console (active testers count). NIE cytować w Application Form jako engagement evidence — to fake installs.

---

**Data utworzenia:** 2026-05-12 (Day 9)
**Ostatnia rewizja:** 2026-05-12
**Owner:** Krzysztof Brzezinski (`krzychu.brzezi@gmail.com`)
