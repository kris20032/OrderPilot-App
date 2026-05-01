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

### Aktywnie testują (engagement potwierdzony)

| # | Imię | Email/Kontakt | Platforma kuriera | Opt-in date | Notatki |
|---|------|---------------|---------------------|---------------|---------|
| 1 | Tata (Krzysztof) | marcamper@... | Uber Eats (Xiaomi) | ~2026-03-16 | Real kurier, używa od marca, baza historycznego feedbacku |
| 2 | Andrij | alcotresher@... | Bolt Food + inne | 2026-04-?? | Real kurier UA, multi-platform, aktywny feedback (5h57m / 9 zleceń 04-29) |
| 3 | Dominik | dominanb19@... | TBD | 2026-04-?? | Rodzina, zgłosił bug RU/UA + Samsung nav bar |
| 4 | Lucky | luckydhami781@... | Real kurier (EN) | 2026-04-30 | Zainstalował + włączył (Accessibility ON), odpisał na DM 04-30, czeka feedback |
| 5 | Ivan Black | shawaivan4@... | Real kurier (EN) | 2026-04-?? | Self-aware ("So for now I am a Zombie haha, I will use the app"), czeka aż pójdzie do pracy |
| 6 | Wujek Krzysiek | sklep@scrappasja.pl | non-courier (rodzina) | 2026-04-?? | Rodzina, gwarantowany engagement |
| 7 | Dziadek | kazimierzdamecki@... | non-courier (rodzina) | 2026-04-?? | Rodzina, przypominać osobiście |
| 8 | Grzegorz | cr7fc5@... | non-courier (znajomy) | 2026-04-?? | Znajomy, przypominać raz na pare dni |

### POCZEKAJ — TBD engagement (deadline 2-3 dni)

| # | Imię | Email | Kontakt | Notatki |
|---|------|-------|---------|---------|
| 9 | Andrew | andrew.onischuk@... | SMS | Z pierwszej rekrutacji MC. Przestraszył się, obiecał włączać raz na pare dni |
| 10 | Kuba | jakummroz2004@... | WhatsApp | Czeka na feedback czy udało się zainstalować |
| 11 | Ivan UA | khomichivan@... | Telegram | Country issue (Polska podobno OK), kraje teraz Select all |
| 12 | Artur (?) | g72308013@... | Telegram | Białoruś w Google Play, kraje teraz Select all |
| 13 | Gonzalo (developer EN) | gonzalogarcia01914@... | email | Miał wysłać tel, deadline +24h |

### Do dodania (pending)

| # | Imię | Email | Kategoria | Status |
|---|------|-------|-----------|--------|
| 14 | Brat | TBD (jego Gmail z nowego telefonu) | rodzina | Add gdy clock gotów (mobile data, nie home WiFi) |
| 15 | Znajomy taty | TBD (jutro przez tatę) | znajomy non-courier | Tata pyta jutro, dodać po potwierdzeniu |
| 16-22 | Paid (PrimeTestLab) | 5-7 z dostarczonego pool 120 | paid | Po odpowiedzi managera (ticket 04-30) |

### Usunięci z listy / zrezygnowali

| # | Imię | Powód | Data |
|---|------|-------|------|
| 1 | Andrii Pavlyshy (?) | Zombie — opted-in ale brak engagement, skasowane przed clock startem | 2026-04-30 |
| 2 | Begenchbayramov | Deleted (poprzednia czystka) | 2026-04-?? |
| 3 | Impelandrew | Deleted (poprzednia czystka) | 2026-04-?? |
| 4 | Saizent13 | Deleted (poprzednia czystka) | 2026-04-?? |

---

## 2. Feedback od testerów (z cytatami)

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
| _(1.0.2)_ | _(planowane)_ | _(np. fix overlay news portals — Andrij)_ | Andrij |
| _(1.0.3)_ | _(planowane)_ | _(kolejne fixy z `future_polish_fixes.md`)_ | _(różni)_ |
| _(1.0.4)_ | _(planowane)_ | _(final polish before submit)_ | _(różni)_ |

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
**Mapping (do wypełnienia po updates):**
- v1.0.2 — Fixed [X] reported by [tester] on [date]
- v1.0.3 — Fixed [Y] reported by [tester] on [date]
- v1.0.4 — Fixed [Z] reported by [tester] on [date]

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

### Pre-clock (do zrobienia przed startem 14-day okna ~1-2 maja)
- [ ] Dodać Andrija screen do `test-data/closed-testing/screenshots/2026-04-29_andrij_news-portals-bug.png`
- [ ] Dodać bug Andrija do `future_polish_fixes.md` (false-positive na portalach informacyjnych)
- [ ] Czekać na odpowiedź PrimeTestLab managera (ticket 04-30)
- [ ] Tata pyta znajomego o tester (jutro 05-01)
- [ ] Po managerze: bundle add — paid (5-7 z 120 pool) + brat + znajomy taty + odpowiedzi POCZEKAJ

### W trakcie 14-day okna
- [ ] Codzienny snapshot Play Console Statistics (screen co kilka dni)
- [ ] WA grupa daily ping co 2-3 dni (kto reaguje = engaged)
- [ ] **Day 2-3:** Publish v1.0.2 — fix Andrij news portals false-positive
- [ ] **Day 7-8:** Publish v1.0.3 — fix RU/UA UI lang + Samsung navbar (Dominik)
- [ ] **Day 11-12:** Publish v1.0.4 — final polish

### Milestones
- [ ] **Day 7:** review tej tabeli, sprawdź czy materiał na Production form rośnie
- [ ] **Day 14:** finalizacja materiału na Application Form (Section 5), submit Production
- [ ] **Po submit:** review 3-7 dni, monitor email od Google
- [ ] **Po Production approval:** merge `play-store-prep` → `feature/production-app` → `main`
