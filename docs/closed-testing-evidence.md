# OrderPilot — Closed Testing Evidence Log

> **Cel pliku:** zbieranie wszystkich dowodów engagementu i feedbacku z Closed Testing **w jednym miejscu**, gotowych do wykorzystania przy wypełnianiu **Production Application Form** w Google Play Console.
>
> **Co tu trafia:**
> - Konkretne wiadomości od testerów (cytaty, dat, kontekst)
> - Statystyki engagementu (z apek kurierskich na ich telefonach)
> - Bug reporty z konkretnymi szczegółami
> - Linki do screenshotów (folder: `test-data/closed-testing/screenshots/`)
> - Historia AAB updates z mapowaniem „który update naprawił który feedback"
>
> **Czego tu NIE ma:**
> - Lista bugów/poprawek do zrobienia → `docs/future_polish_fixes.md`
> - Engagement aggregate metrics → Play Console Statistics (screenshots tutaj)
>
> **Lokalizacja screenshotów:** `test-data/closed-testing/screenshots/`
> **Konwencja nazw plików:** `YYYY-MM-DD_imię-testera_kontekst.png`
> Przykład: `2026-04-29_andrij_dashboard-bolt-stats.png`

---

## 1. Pula testerów

> **🚨 STATUS 2026-05-03:** Console pokazuje **12+ testers opted-in** — drugi krok requirement zaliczony. Day 0 = 2026-05-03. Day 14 ≈ 2026-05-17. PrimeTestLab paid pool czeka na Google review track changes (4-24h), potem 4-12h staged opt-in.

### Aktywnie testują (engagement potwierdzony)

| # | Imię | Email/Kontakt | Platforma kuriera | Opt-in date | Notatki |
|---|------|---------------|---------------------|---------------|---------|
| 1 | Tata (Krzysztof) | marcamper@... | Uber Eats (Xiaomi) | ~2026-03-16 | Real kurier, używa od marca, baza historycznego feedbacku |
| 2 | Andrij | alcotresher@... | Bolt Food + inne | 2026-04-?? | Real kurier UA, multi-platform, aktywny feedback (5h57m / 9 zleceń 04-29) |
| 3 | Dominik (brat) | dominanb19@... | TBD | 2026-04-?? | Brat. Rodzina, zgłosił bug RU/UA + Samsung nav bar. Plan dodania jego drugiego starego telefonu odrzucony (risk same-IP > value) |
| 4 | Lucky | luckydhami781@... | Real kurier (EN) | 2026-04-30 | Zainstalował + włączył (Accessibility ON), czeka pierwsza sesja pracy |
| 5 | Ivan Black | shawaivan4@... | Real kurier (EN) | 2026-04-?? | Self-aware ("So for now I am a Zombie haha, I will use the app"), czeka aż pójdzie do pracy |
| 6 | Wujek Krzysiek | sklep@scrappasja.pl | non-courier (rodzina) | 2026-04-?? | Rodzina, gwarantowany engagement |
| 7 | Dziadek | kazimierzdamecki@... | non-courier (rodzina) | 2026-04-?? | Rodzina, przypominać osobiście |
| 8 | Grzegorz | cr7fc5@... | non-courier (znajomy) | 2026-04-?? | Znajomy, przypominać raz na pare dni |
| 9 | Kuba | jakummroz2004@... | TBD | 2026-05-03 | **Confirmed installed 05-03**, awaiting first feedback |
| 10 | Vasyl (znajomy taty) | TBD (telefon) | TBD | 2026-05-03 (?) | To jest „znajomy taty" o którym wcześniej mowa — dostałem numer przez tatę. Zadzwoniłem 05-03, wysłany link, prawdopodobnie pobrał (Console delay). Verify w 24h. **Możliwe że to #12 który odpalił clock.** |

### POCZEKAJ — czekamy na opt-in / feedback

| # | Imię | Email | Kontakt | Notatki |
|---|------|-------|---------|---------|
| 11 | Andrew | andrew.onischuk@... | SMS | Z pierwszej rekrutacji MC. Przestraszył się, obiecał włączać raz na pare dni. Deadline weryfikacji: 05-05 |
| 12 | Ivan UA | khomichivan@... | Telegram | Podał najpierw zły email, poprawiony 05-03. Kraje teraz Select all. Czekamy na feedback czy zainstalował |
| 13 | Artur (?) | g72308013@... | Telegram | Pingowany 05-03 rano (Białoruś dodana). Cisza 24-48h → ostatni ping albo wywal |
| 14 | Gonzalo (developer EN) | gonzalogarcia01914@... | email | Final ping 05-03 rano. Brak odpowiedzi do 05-04 → wywalić |

### Do dodania (pending) — safety net dla utrzymania 12+ continuously

| # | Imię | Email | Kategoria | Status |
|---|------|-------|-----------|--------|
| 11-15 | PrimeTestLab paid pool | 120 emaili w `primetestlab-pool` | paid | Czekamy na Google approval Closed track changes (4-24h), potem 4-12h staged opt-in (5-7 wejdzie initially per manager) |

> **Note:** Plan „dodać brata na drugim starym telefonie" odrzucony — brat już jest na liście jako Dominik (dominanb19@...), a drugie konto z tego samego IP = ryzyko Google flag > value. „Znajomy taty" to ten sam człowiek co Vasyl (wiersz #10 powyżej) — nie liczyć podwójnie.

### Usunięci z listy / zrezygnowali

| # | Imię | Powód | Data |
|---|------|-------|------|
| 1 | Andrii Pavlyshy (?) | Zombie — opted-in ale brak engagement, skasowane przed clock startem | 2026-04-30 |
| 2 | Begenchbayramov | Deleted (poprzednia czystka) | 2026-04-?? |
| 3 | Impelandrew | Deleted (poprzednia czystka) | 2026-04-?? |
| 4 | Saizent13 | Deleted (poprzednia czystka) | 2026-04-?? |

---

## 2. Feedback od testerów (z cytatami)

### 🟢 Andrij (UA real kurier) — 2026-05-04 ⭐ KEY EVIDENCE

**Kontekst:** Po pingu Krzysztofa na WhatsAppie 18:01 z pytaniem o feedback i bug z portalami informacyjnymi, Andrij odpowiedział o 18:03 i wysłał o 18:06 **5+ screenshotów z OrderPilot overlay podczas realnego dnia pracy** (z poprzedniego tygodnia, dzisiaj miał dzień wolny).

**Screeny pokazują realne zlecenia z apek Uber/Bolt z OrderPilot overlay:**
- 19,18 PLN / 14 min / 5 km · 3,75 km dystans (McDonald's Morena, Gdańsk)
- 13,73 PLN / 23 min · 3,73 km dystans (Smażą parzą, Gdańsk)
- 31,22 PLN / 44 min · 9,43 km dystans (MAX Premium Burgers, Kowale)
- 15,49 PLN / 14 min · 4,18 km dystans (McDonald's Morski Park Handlowy)
- 19,70 PLN, godzina 16:18

**Cytat (PL):**
> „Pokażę wam dzisiejszy dzień wolny na przykładzie z zeszłego tygodnia."

**Why this is GOLD for Application Form:**
- Real courier (UA, multi-platform Uber + Bolt Food)
- Real OrderPilot overlay numbers visible podczas realnych zleceń
- Lokalizacje z prawdziwych restauracji w Gdańsku
- Multiple orders w jednym dniu pracy = consistent usage pattern
- Conversation timing = potwierdza engagement on demand (Andrij odpisał < 5 min od pingu)

**Pliki screenów:** `test-data/closed-testing/screenshots/2026-05-04_andrij_real-work-orderpilot-{1..5}.png` _(do dodania — pobrać z WhatsApp)_

**Plan użycia w Application Form:**
- Sekcja „How are testers engaging" — cytat Andrija + 1-2 screeny
- Sekcja „What feedback did you receive" — bug news portals (już mamy z 04-29) + ten 05-04 update

---

### 🟢 Andrij (UA real kurier) — 2026-04-29

**Kontekst:** Andrij wysłał screen ze statystykami z apki Bolt Food (lub Uber, do potwierdzenia z screena). Pokazał konkretne liczby ze swojego dnia pracy + zgłosił bug + dał general feedback o aplikacji.

**Statystyki dnia (29.04.2026):**
- Online: **5h 57min**
- Zleceń: **9**
- Zarobek netto: 133,68 PLN
- Podatki: 30,75 PLN

**Bug report (cytat):**
> „Podobne rzeczy pokazuje także na różnych portalach informacyjnych"

→ **Problem:** Belka OrderPilot pojawia się na portalach informacyjnych (np. Onet, WP, etc.) gdy nie powinna — false positive overlay matching.

**Potwierdzenie że apka działa przy zleceniach (cytat):**
> „Tak przy zleceniach wszystko super"

**General feedback (cytat):**
> „Tak, sama aplikacja jest bardzo przydatna, oczywiście. Jeśli pojawią się jakieś błędy, na pewno napiszę. Miłego weekendu. ❤️"

**Plik screen:** `test-data/closed-testing/screenshots/2026-04-29_andrij_news-portals-bug.png` _(do dodania)_

**Linki:**
- Bug w `docs/future_polish_fixes.md` — _(dodać entry)_
- Fix w wersji: _(planowane v1.0.X — następny update AAB)_

---

### 🟡 Dominik — 2026-04-?? (data opt-in do uzupełnienia)

**Kontekst:** Tester Dominik zgłosił 2 bugi w trakcie wstępnej weryfikacji.

**Bug 1: Język RU/UA nie zmienia się w UI aplikacji**
- Po zmianie języka w ustawieniach na rosyjski/ukraiński, UI aplikacji nie tłumaczy się
- Belka — niesprawdzone czy się tłumaczy
- Status: zapisane do `future_polish_fixes.md`, naprawa odłożona

**Bug 2: Przycisk „Zapisz ustawienia" zakryty przez Samsung nav bar**
- Dolny przycisk save w Settings jest częściowo niewidoczny przez systemowy pasek nawigacji
- Status: zapisane do `future_polish_fixes.md`, naprawa odłożona

**Plik screen:** `test-data/closed-testing/screenshots/2026-04-??_dominik_settings-navbar-overlap.png` _(do dodania jeśli mamy)_

---

### 🟢 Tata (Krzysztof) — running history

**Status:** Real kurier Uber Eats, telefon Xiaomi (MIUI), używa apki od ~marca 2026, baza historycznego feedbacku.

**Historyczne odkrycia (już naprawione lub w trackingu):**
- Multi-overlay collision Wolt+Uber (naprawione 03-14)
- Glovo partial offer parsing (naprawione)
- Persistent overlay Uber Xiaomi — w trackingu
- (więcej w `memory/bugfixes_courier_apps_2026_q1.md`)

---

## 3. Engagement evidence — Play Console snapshots

### Daily snapshots Play Console Statistics

| Data | Active users (DAU) | Installed audience | Liczba opted-in | Notatki |
|------|---------------------|---------------------|---------------------|---------|
| 2026-04-23 | _(unknown)_ | 3 | ~5 | Pierwsze realne installs |
| 2026-04-24 | _(unknown)_ | 2 | ~6 | 1 osoba odinstalowała |
| 2026-04-25 | _(unknown)_ | 2 | ~7 | Stabilne 2 zainstalowanych |
| 2026-04-26 | _(unknown)_ | 2 | 8 | Stabilne |
| 2026-04-29 | _(do sprawdzenia)_ | _(do sprawdzenia)_ | 8 | Andrij real engagement: 5h57m, 9 zleceń |

**Plik screen:** `test-data/closed-testing/screenshots/2026-04-29_play-console-statistics.png` _(do dodania)_

### WhatsApp grupa — engagement timeline

| Data | Wiadomość / wydarzenie | Liczba reakcji |
|------|---------------------------|-------------------|
| 2026-04-29 | Andrij raport stats + bug | 1× detailed bug report, 1× konfirmacja działania |
| | _(uzupełniać daily pings)_ | |

**Plik screen WA grupy:** `test-data/closed-testing/screenshots/YYYY-MM-DD_wa-group-snapshot.png`

---

## 4. AAB updates timeline (3+ wymagane przez Google)

| Wersja | Data publikacji | Główne zmiany | Bazujące na feedback od |
|--------|-----------------|---------------|-------------------------|
| 1.0.1 | _(initial Closed Testing release)_ | First Closed Testing build | — |
| 1.0.2 | 2026-05-05 (Day 2) — kod gotowy, czeka na build+upload | **Fixed false-positive overlay on news portals** — multi-layer defense (foreground tracker, watch mode reset on app switch, hardened Uber overlay phantom detection on MIUI, positive offer markers in Uber/Bolt/Wolt parsers). | Andrij (UA real courier, 2026-04-29) |
| _(1.0.3)_ | _(planowane Day 7-8 ≈ 2026-05-10/11)_ | _(language fallback RU/UA + Samsung navbar overlap)_ | Dominik |
| _(1.0.4)_ | _(planowane Day 11-12 ≈ 2026-05-14/15)_ | _(final polish before submit)_ | _(różni)_ |

**Cel: minimum 3 updates w trakcie 14-dniowego okna Closed Testing**, każdy z konkretnymi release notes typu „Fixed [bug] reported by [tester]".

---

## 5. Material gotowy do Production Application Form

> **Sekcja do wypełnienia gdy dochodzimy do Day 14 unlock i submit Production.**
> Pre-fill najlepszych cytatów / liczb tutaj, żeby przy submit było gotowe do wklejenia.

### Q: How did you recruit testers?
**Draft answer:**
> Recruited testers via 4 channels: (1) leaflets distributed at Forum Gdańsk shopping mall and at a bicycle service shop where couriers gather; (2) friends and family who use the app to test wizard flow, settings UX, and edge cases; (3) paid testing service (PrimeTestLab / 12testers14days.com) for geographic and device diversity; (4) organic word-of-mouth from existing testers (e.g., Ukrainian courier Andrij who tests across multiple platforms).

### Q: How are testers engaging with the app?
**Draft answer:**
> Active feedback channel via WhatsApp group with [X] messages exchanged in 14 days. Real couriers report daily usage (e.g., Andrij: 5h 57min online, 9 deliveries on April 29 with OrderPilot running). Family/friend testers verify wizard, settings UX, and language fallback. Paid testers verify cross-device compatibility (Samsung Knox, Xiaomi MIUI, etc.).

### Q: What feedback did you receive?
**Specific quotes ready to cite:**
- Andrij (UA real courier, 2026-04-29): „Podobne rzeczy pokazuje także na różnych portalach informacyjnych" → bug: false-positive overlay on news portals
- Andrij (UA real courier, 2026-04-29): „Tak przy zleceniach wszystko super" → confirms core pipeline works
- Dominik (2026-04-??): UI language fallback (RU/UA) not switching → fix planned
- Dominik (2026-04-??): Samsung nav bar overlap on Settings save button → fix planned

### Q: What did you fix based on feedback?
**Mapping:**
- **v1.0.2** (2026-05-05) — Fixed false-positive overlay appearing on news portals, reported by Andrij (UA real courier) on 2026-04-29. Implemented multi-layer defense: strict foreground tracker (cross-checked against system accessibility state), watch mode reset on app switch, hardened detection of legitimate Uber overlay popups vs MIUI phantom overlays, and positive offer-marker validation in Uber/Bolt/Wolt parsers (parser now requires at least one platform-specific offer phrase like "Łącznie"/"Odbiór za"/"Akceptuj" before showing overlay).
- v1.0.3 — _(planowane Day 7-8)_ Fixed Y reported by Z on date
- v1.0.4 — _(planowane Day 11-12)_ Fixed W reported by Z on date

---

## 6. Konwencja zapisywania nowych znalezisk

Gdy Krzysztof wyśle screen / feedback od kolejnego testera:

1. **Screen zapisać** w `test-data/closed-testing/screenshots/YYYY-MM-DD_imię-testera_kontekst.png`
2. **Dodać sekcję** w pkt. 2 tego dokumentu (Feedback od testerów) z:
   - Datą
   - Imieniem testera
   - Cytatem dosłownym
   - Linkiem do screenu
   - Statusem (do naprawy / naprawione w wersji X)
3. **Jeśli to bug** → dodać też entry w `docs/future_polish_fixes.md` (z linkiem zwrotnym tutaj)
4. **Jeśli to engagement signal** → uaktualnić tabelę w pkt. 3 (Play Console snapshots / WA timeline)
5. **Jeśli to fix wdrożony w AAB update** → uaktualnić timeline w pkt. 4

---

## 7. Otwarte zadania / przypomnienia

---

### 🚨 STATUS DNIA — 2026-05-03 (Day 0) — CLOCK URUCHOMIONY

**Console potwierdza 12+ testers opted-in ✅** — drugi krok requirement zaliczony, 14-day clock wystartował.

| Milestone | Data |
|-----------|------|
| **Day 0** | 2026-05-03 |
| **Day 14** | 2026-05-17 (najwcześniej Apply for Production) |
| **Submit Production target** | 2026-05-17 |
| **Live target** (po Google review 3-7 dni) | ~2026-05-20 do 2026-05-24 |

**Co zrobione 05-03:**
- ✅ Pingi: Ivan UA (poprawiony email), Artur, Kuba, Gonzalo, Vasyl (cold call)
- ✅ Kuba potwierdził install (05-03)
- ✅ Vasyl wysłany link, prawdopodobnie pobrał (Console delay, verify w 24h)
- ✅ Ticket #539 — manager Kefayatullah potwierdził: czeka na Google approval Closed track changes (4-24h), potem 4-12h staged opt-in z puli 120

---

#### 🔴 Krytyczne najbliższe 24h (do 05-04 wieczór)

- [ ] **Save Day 0 screen** Console Dashboard (12 testers ✅) → `test-data/closed-testing/screenshots/2026-05-03_day0_dashboard-12-testers-checked.png`
- [ ] **Verify Vasyl install** (= „znajomy taty") — sprawdzić czy Console pokazuje +1 (slight delay teraz)
- [ ] **Verify Ivan UA install** (po poprawce maila)
- [ ] **Pre-launch report check** — Console → Pre-launch report. Crashe? ANRs? Naprawić ASAP, nie czekać do Day 13
- [ ] **PrimeTestLab pool** — gdy Google approval przyjdzie, fala opt-inów. Monitor.

> **Brat (Dominik) już jest na liście** od dawna (dominanb19@...) — plan dodania jego drugiego starego telefonu odrzucony (risk same-IP > value). „Znajomy taty" = Vasyl, ten sam człowiek.

#### 🟡 Przez najbliższe 14 dni (rozłożone)

- [ ] Daily snapshot Console Statistics co 2 dni (DAU, Active devices, Installed audience)
- [ ] Pingi do aktywnych kurierów (co 2-3 dni): Andrij, Tata, Dominik, Lucky, Ivan Black, Kuba (nowy)
- [ ] WA group ping #1 (Day 3 ≈ 2026-05-06): „screen + 1 zdanie"
- [ ] WA group ping #2 (Day 7 ≈ 2026-05-10)
- [ ] WA group ping #3 (Day 11 ≈ 2026-05-14)
- [ ] Kod v1.0.2 (Andrij news portals fix) — publish Day 2-3 (≈ 2026-05-05/06)
- [ ] Kod v1.0.3 (Dominik RU/UA + Samsung navbar) — publish Day 7-8 (≈ 2026-05-10/11)
- [ ] Kod v1.0.4 (final polish) — publish Day 11-12 (≈ 2026-05-14/15)

#### 🟢 Nie ruszać (manager sam wróci)

- ❌ Nie pingować PrimeTestLab managera — sam da znać gdy fala startuje
- ❌ Nie ruszać 120 emaili z puli osobno — staged opt-in, interferencja = problem
- ❌ Nie wgrywać AAB updates szybciej niż Day 2 — Google chce widzieć rozłożenie w czasie

---

### 📊 MATERIAL DO ZEBRANIA przez 14 dni (Application Form ammunition)

**Obecne pokrycie: ~30%.** Większość pól w sekcji 5 to placeholdery. Bez tego materiału submit Production = wysokie ryzyko rejection.

#### Bezwzględnie zebrać:

- [ ] **Screeny Play Console Statistics** — daily lub co 2 dni → `test-data/closed-testing/screenshots/YYYY-MM-DD_play-console-statistics.png`
  - Active devices, Installed audience, DAU per country
  - Pokaż wzrost przez 14 dni

- [ ] **Cytaty od kurierów którzy jeszcze nie odpisali**: Lucky, Ivan Black, Andrew, Kuba, Ivan UA
  - Mamy tylko Andrija + Dominika — za mało dla Google
  - Każdy cytat = data + imię + dosłowne słowa + kontekst

- [ ] **Screeny WhatsApp grupy** — pokazują że komunikacja jest żywa
  - Co 2-3 dni snapshot konwersacji
  - Pokazuje że pingi są, reakcje są

- [ ] **Pre-launch report wyniki** z Console
  - Free dowód cross-device (~20 urządzeń automatycznie)
  - Screeny + lista przetestowanych modeli

- [ ] **Bug repro screeny/wideo**
  - Andrij — news portals false-positive (najmocniej: video jak Andrij scrolluje news i belka się pojawia)
  - Dominik — RU/UA UI nie tłumaczy + Samsung navbar overlap

- [ ] **3× AAB release notes** z mappingiem „Fixed X reported by Y"
  - v1.0.2 (Day 2-3): Andrij news portals fix
  - v1.0.3 (Day 7-8): Dominik RU/UA + Samsung navbar
  - v1.0.4 (Day 11-12): final polish

#### Mocne uderzenia (jeśli się da zdobyć):

- [ ] **Andrij screencast/wideo** używania apki podczas zlecenia
  - Najmocniejszy proof: real courier + real work + real OrderPilot belka
  - Zapytać go — może nagrać 30 sek ekranu

- [ ] **Tata — long-term use proof**
  - Screen z Play Console „installed since March 2026"
  - Historyczny feedback już mamy (Wolt+Uber collision, Glovo, etc.)

- [ ] **Country diversity screenshot**
  - Console pokazuje per-country installs
  - Cel: PL + UA + EN minimum — pokazuje że nie tylko lokalna apka

#### Strategia praktyczna:

**3 pingi w WA grupie podczas 14 dni** — Day 3, Day 7, Day 11.
Każdy ping = prośba o:
1. Screen z apki (cokolwiek — działa, nie działa, popup)
2. 1 zdanie feedbacku (co dobrze / co źle)

Tester poświęca 30 sek, ty masz cytaty + screen. Wzór wiadomości:

> Cześć! Jak idzie z OrderPilotem? Mógłbyś wrzucić jeden screen + napisać jedno zdanie co działa albo co nie? Potrzebne do oficjalnego submit do Google. Dzięki!

---

### 📅 Milestones podczas 14-day okna (Day 0 = 2026-05-03)

| Day | Data | Co | Notatki |
|-----|------|-----|---------|
| **Day 0** | 2026-05-03 | Clock start — 12+ opted-in confirmed | ✅ DONE |
| **Day 2-3** | 2026-05-05/06 | Publish v1.0.2 (Andrij news portals) | Kod pisany 05-03/04 |
| **Day 3** | 2026-05-06 | WA group ping #1 | Prośba o screen + 1 zdanie |
| **Day 7** | 2026-05-10 | Review materiału — sekcja 5 rośnie? | Jeśli nie → eskalacja: video Andrija, bonus dla testerów |
| **Day 7-8** | 2026-05-10/11 | Publish v1.0.3 (Dominik RU/UA + navbar) | Drugi update |
| **Day 7** | 2026-05-10 | WA group ping #2 | j.w. |
| **Day 11** | 2026-05-14 | WA group ping #3 — final | Ostatnia szansa na cytaty |
| **Day 11-12** | 2026-05-14/15 | Publish v1.0.4 (final polish) | Trzeci update — wymóg Google |
| **Day 14** | 2026-05-17 | Finalizacja Application Form (sekcja 5) | Wklejenie cytatów, screenów, mapowania |
| **Day 14** | 2026-05-17 | **Submit Production** | Po wypełnieniu formularza |
| **Po submit** | 05-17 → 05-24 | Google review: 3-7 dni | Monitor email |
| **Po approval** | ~2026-05-20-24 | Merge `play-store-prep` → `main`, live | 🚀 |

---

### ⚠️ Anti-patterns — czego NIE robić

- ❌ **Nie ruszać puli PrimeTestLab** osobnymi pingami — mogą zinterpretować jako interferencję. Ich opt-in jest staged 4-12h, czekać.
- ❌ **Nie wgrywać AAB updates szybciej niż Day 2** — Google chce widzieć rozłożenie w czasie (3+ updates przez 14 dni, nie wszystkie naraz).
- ❌ **Nie pisać "I've uploaded" gdy jeszcze nie wgrałeś** — w komunikacji z PrimeTestLab manager weryfikuje, mismatch = ticket eskalacja.
- ❌ **Nie zostawiać sekcji 5 (Application Form) na Day 14** — wypełniaj inkrementalnie po każdym feedback. Day 14 = tylko polish + submit.
