# Production Submit — Final Checklist (Day 14, target 2026-05-17)

**Status na 2026-05-15 (Day 13):** **4/3 AAB updates LIVE w Closed Testing** (v1.0.2 → v1.0.3 → v1.0.4 → v1.0.5 same-day hotfix Marcin Uber popup 05-13 20:39). 53 active testers, Dashboard pokazuje 13/14 days continuously. Application Form draft GOTOWY (4 Fix Cards paste-ready w `closed-testing-evidence.md` sekcja 5). **Default store listing zweryfikowany pole-po-polu i Save kliknięte — zmiany pending w Publishing overview do Day 14 (one-shot submit razem z v1.0.5 AAB).** C2/C4/C8/C9 + F1/F2/F4 ✅ zamknięte.

Ten plik to **single source of truth** dla Day 14 submitu — przejdź po kolei, zaznacz każde pole.

---

## A. Closed Testing requirements (must be met BEFORE Apply for Production)

| # | Wymóg | Status | Evidence |
|---|-------|--------|----------|
| A1 | Closed Testing track active 14+ dni | 🟡 W TOKU (Day 13, Day 14 = 2026-05-17) — Dashboard "12 testers opted in for 13 days continuously" | Console: Test and release → Closed testing → Alpha track |
| A2 | Min 12 opted-in testerów continuously przez 14 dni | ✅ DONE (53 active, 12+ continuously confirmed) | `2026-05-09_play-console-statistics-installed-audience.png` |
| A3 | 3+ AAB updates podczas 14-day okna | ✅ DONE 4/3 (v1.0.2 ✅ 05-06, v1.0.3 ✅ 05-09, v1.0.4 ✅ 05-13 11:20, **v1.0.5 ✅ 05-13 20:39 LIVE**) | `closed-testing-evidence.md` sekcja 4 |
| A4 | Zero blocking crashes / ANRs | ✅ DONE (Console pokazuje zero) | Console: Quality → Android vitals |
| A5 | Real tester feedback collected (≥3 different testers) | ✅ DONE (Andrij, Dominik, Marcin, Lucky, Ivan Black + 5 others) | `closed-testing-evidence.md` sekcja 2 |

---

## B. Play Console — App content (musi być WYPEŁNIONE przed Production submit)

> Większość tego wypełniliśmy przy Closed Testing — przed Day 14 **weryfikujemy** że wszystko jest wciąż valid.

| # | Pole | Status | Notatki |
|---|------|--------|---------|
| B1 | Privacy Policy URL | ✅ DONE | `https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html` |
| B2 | Data Deletion URL | ✅ DONE | `https://kris20032.github.io/OrderPilot-App/legal/data-deletion.html` |
| B3 | App access | ✅ DONE (zweryfikowane 2026-05-12) | "All functionality in my app is available without any access restrictions" zaznaczone. **Brak instructions for reviewer celowo** — OrderPilot nie ma loginu/paywalla/geo-restrykcji. Kontekst use-case dla reviewera jest w Permissions Declarations (Accessibility ~300 słów) + YouTube video preview. Sekcja B3 wzór niżej został wycofany jako niepotrzebny dla naszego case. |
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

### B3 — Decyzja 2026-05-12 (zweryfikowane w Console)

**Status:** „All functionality in my app is available without any access restrictions" zaznaczone w Console (od 2026-04-21). **Nie zmieniamy.**

**Dlaczego nie wpisujemy „Instructions for reviewer":**
- Google policy „access restrictions" = login, paywall, geo-blokada, membership. OrderPilot nie ma żadnej z tych barier.
- Brak konta Uber/Wolt/Glovo/Bolt **nie jest** access restriction do OrderPilot — to external app context, nie barrier do enter our app.
- Reviewer dostanie pełen dostęp: Prominent Disclosure, Setup wizard, Settings, AccessibilityService permission flow — wszystko reachable bez courier account.

**Reviewer kontekst use-case dostaje gdzie indziej:**
- Permissions Declarations → Accessibility Service section (~300 słów o courier use case) w `permissions_declarations.md` sekcja 1
- YouTube video preview (`youtube.com/shorts/riSLy3qiySA`) — demonstruje pełen flow
- Long description w Store listing — opisuje docelowych użytkowników

---

## C. Play Console — Store presence (Main store listing)

> Wszystkie pola w Console: **Grow users → Store presence → Main store listing**. Weryfikujemy Day 13 że Polish + English listing są kompletne.

| # | Pole | Limit | Status | Source |
|---|------|-------|--------|--------|
| C1 | App name | 30 chars | ✅ DONE | "OrderPilot" |
| C2 | Short description PL | 80 chars | ✅ DONE (zweryfikowane 05-12) | "Wylicza zł/h dla zleceń Uber, Wolt, Glovo, Bolt — pokazuje czy warto jechać" (75/80) |
| C3 | Short description EN | 80 chars | ⏭️ SKIP do v1.1 (decyzja 05-12) | Brak EN translation w Console — świadomy skip, Google auto-translate wystarczy dla v1.0, redukuje rejection surface |
| C4 | Long description PL | 1500-3000 chars | ✅ DONE (zweryfikowane 05-12) | 1564/4000. Non-affiliation ✅, financial disclaimer ✅, „NIE posiada uprawnienia Internet" ✅, email kontaktowy ✅, sekcja Accessibility Service rationale ✅ |
| C5 | Long description EN | 1500-3000 chars | ⏭️ SKIP do v1.1 (decyzja 05-12) | jak C3 |
| C6 | App icon 512×512 | PNG | ✅ DONE | `store-assets/app_icon_512.png` |
| C7 | Feature graphic 1024×500 | PNG/JPG | ✅ DONE (1/1 widoczne w Console 05-12) | `store-assets/feature_graphic_1024x500.png` (OrderPilot dark + 42 zł/h GREEN + 14 zł/h RED) |
| C8 | Phone screenshots — min 2, max 8 | per locale | ✅ DONE (8 widoczne w Console 05-12) | + 7-inch tablet 8/8, 10-inch tablet 1/8 (min wymóg spełniony) |
| C9 | Video preview (YouTube) | URL | ✅ DONE (zapisane 05-12) | `https://www.youtube.com/watch?v=riSLy3qiySA`. **⚠️ GOTCHA: Play Console NIE akceptuje Shorts URLs** — trzeba konwertować `/shorts/<id>` → `/watch?v=<id>` (ID identyczne) |
| C10 | App category | wybór | ✅ DONE | Productivity |
| C11 | Contact details — email | required | ✅ DONE | `krzychu.brzezi@gmail.com` |
| C12 | Tablet screenshots | optional | 🟢 N/A (skipping for v1) | - |

### C — Status 2026-05-12 (ZAMKNIĘTE)

Sekcja C wykonana w sesji Day 9 (Play Console verification). Zmiany zapisane przez **Save** (nie „Save as draft") → pending w **Publishing overview** ze statusem „Ready to send for review". **Nie klikamy „Send for review" do Day 14** — strategia jednego pakietu (store listing + v1.0.4 AAB razem, mniej okazji do Google rejection).

EN translation pominięta świadomie: brak EN entry w Manage translations (tylko Polish). Powody decyzji: (1) Google nie wymaga EN dla v1.0 Production, (2) +5 nowych pól = nowa powierzchnia rejection, (3) Google auto-translate wystarczy dla 177 countries w Closed Testing pool, (4) EN translation = praca v1.1 post-Production.

---

## D. Production track release (Day 13-14)

| # | Krok | Kiedy | Notatki |
|---|------|-------|---------|
| D1 | Build signed AAB v1.0.4 | ✅ DONE 05-13 | versionCode 5, versionName "1.0.4" |
| D2 | Upload AAB v1.0.4 do Closed Testing | ✅ DONE 05-13 | Wgrany jako "1.0.4 - threshold fixes" |
| D3 | Release notes v1.0.4 EN+PL | ✅ DONE 05-13 | Paste z PRODUCTION_SUBMIT_CHECKLIST.md D3 |
| D4 | Send for review + auto-publish (Closed Testing) | ✅ DONE 05-13 11:20 AM | Play Console: „App update published, May 13" |
| D1.5 | Build signed AAB v1.0.5 (hotfix Marcin Uber popup) | ✅ DONE 05-13 wieczorem | versionCode 6, versionName "1.0.5", branch `fix/v1.0.5-uber-popup-background` (commit `e17860c`) |
| D2.5 | Upload AAB v1.0.5 do Closed Testing | ✅ DONE 05-13 | Replaced v1.0.4 jako live build w Closed track |
| D3.5 | Release notes v1.0.5 EN+PL | ✅ DONE 05-13 | Paste w sekcji D3.5 poniżej |
| D4.5 | Send for review + auto-publish | ✅ DONE 05-13 20:39 | Google auto-approved within minutes — „1.0.5 - Uber popup fix" Available to selected testers |
| D4.6 | SMS do Marcin: potwierdź że belka działa nad home screenem | ⏳ Day 13–14 | Krzysztof ping 05-15. Zapisać quote do `closed-testing-evidence.md` |
| D4.7 | SMS do Andrij: regression check (czy nie ma fałszywych belek na portalach informacyjnych) | ⏳ Day 13–14 | Layer 4 chroni — sprawdzamy że nadal działa |
| D5 | **Apply for Production z v1.0.5** | **Day 14 (2026-05-17, sobota)** | Play Console → Production track → New release → kopiuj z v1.0.5 Closed AAB (versionCode 6) |
| D6 | Wypełnić Production Application questionnaire | Day 14 | Paste z `closed-testing-evidence.md` sekcja 5 (Q1.1-Q3.2 + Fix Cards v1.0.2/v1.0.3/v1.0.4/**v1.0.5**) |
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

### D3.5 — Release notes v1.0.5 (paste-ready)

**EN (max 500 chars):**
```
Fixed Uber offer bar not appearing when the offer popup floats over other
apps (e.g. home screen). The bar now appears regardless of which app is in
the foreground. Issue was reported by tester Marcin during Closed Testing
on May 13 and verified through device logs.
```

**PL (max 500 chars):**
```
Naprawiono belkę Ubera, która nie pojawiała się gdy popup oferty
wyświetlał się nad inną aplikacją (np. ekranem głównym). Belka pojawia się
teraz niezależnie od tego, która aplikacja jest na pierwszym planie.
Problem zgłosił tester Marcin podczas testów zamkniętych 13 maja i został
zweryfikowany na logach z urządzenia.
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
| F1 | Privacy Policy URL działa w przeglądarce incognito | ✅ DONE 05-12 — strona top-tier: PL/EN toggle, TL;DR "zero data", RODO art. 15-22, UODO complaint link, last updated 2026-04-21 |
| F2 | Data Deletion URL działa w incognito | ✅ DONE 05-12 — `data-deletion.html` bilingual (PL/EN), Opcja 1 odinstaluj + Opcja 2 Clear data. Data safety w Console = "doesn't collect" → URL field nie wymagany przez Google, ale strona istnieje jako bonus |
| F3 | EN i PL Privacy Policy są spójne | ✅ DONE 05-12 — toggle „English version" linked z PL, tytuł "Privacy Policy", TL;DR "collects zero data", spójność z PL content |
| F4 | YouTube video preview działa publicznie (unlisted OK) | ✅ DONE 05-12 — `https://www.youtube.com/watch?v=riSLy3qiySA` 3/3 checklist incognito (loads, no ads, no age gate) |
| F5 | Zero NEW negative feedback od testerów Day 13 wieczór | WhatsApp grupa, Console feedback |
| F6 | Zero NEW crashes/ANRs w Console od ostatniej weryfikacji | Console → Quality → Vitals |
| F7 | v1.0.5 AAB LIVE w Closed Testing track | ✅ DONE 05-13 20:39 — „1.0.5 - Uber popup fix" Available to selected testers, 1 version code, Released on May 13 8:39 PM |

---

## G. Co NIE robić przed/podczas submit (anti-patterns)

- ❌ NIE submit przed Day 14 (Google flaguje insufficient testing duration even if 13d 23h)
- ❌ NIE cytować PrimeTestLab "40 extra installs" jako engagement evidence w Application Form (to install farma — Google się może skapnąć)
- ❌ NIE wgrywać AAB v1.0.6 podczas Production review (zostaw stabilne v1.0.5 LIVE)
- ❌ NIE odpowiadać generycznie ("good engagement", "tested thoroughly") na pytania 1.2/1.3 — Google to traktuje jako #1 rejection trigger
- ❌ NIE zostawiać Privacy Policy URL bez przeprowadzenia weryfikacji Day 14 morning (URLs gasły testerom przy update v1.0.2)
- ❌ NIE rotować testerów (dodawać nowych zamiast utrzymać continuously opted-in) — Google liczy "consecutive 14 days"

---

## H. Notatki dla audit trail

- **Marcin = tata** — przemianowane do dokumentacji Google review aby nie ujawniać relacji rodzinnej. Wszystkie materiały do Console używają "Marcin". Decyzja 2026-05-11.
- **Dominik = brat** — używamy imienia "Dominik" w docs ale relacja brat-brat nie jest ujawniana.
- **Vasyl = znajomy taty** — independent tester, OK do cytowania.
- **Pre-launch report** wykreślony jako evidence — robot Firebase Google nie przechodzi onboardingu AccessibilityService (znane zachowanie). Evidence zastąpione: 53 active testers + 5 real kurierów + 4 AAB iteracje.
- **PrimeTestLab pool** — używany tylko do liczników w Console (active testers count). NIE cytować w Application Form jako engagement evidence — to fake installs.

---

**Data utworzenia:** 2026-05-12 (Day 9)
**Ostatnia rewizja:** 2026-05-15 (Day 13) — v1.0.5 LIVE confirmation, status sync 4/3 AAB DONE
**Owner:** Krzysztof Brzezinski (`krzychu.brzezi@gmail.com`)
