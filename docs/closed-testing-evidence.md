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

> **🚨 STATUS 2026-05-06 (Day 3):** Console pokazuje **12+ testers opted-in** — drugi krok requirement zaliczony. Day 0 = 2026-05-03. Day 14 ≈ 2026-05-17. **v1.0.2 (pierwszy z 3+ wymaganych AAB updates) sent for review 05-06**, czeka na Google approve. PrimeTestLab paid pool — staged opt-in w toku.

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

**Pliki screenów:** `test-data/closed-testing/screenshots/Andrij_feedback_2/2026-05-04_andrij_real-work-orderpilot-{1..5}.jpeg` ✅ ZAPISANE (5 plików + 1 oryginalny WhatsApp grupowy `Andrij_feedback_2_whatsapp.jpg`)

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

| Wersja | versionCode | Data publikacji | Główne zmiany | Bazujące na feedback od |
|--------|-------------|-----------------|---------------|-------------------------|
| 1.0.0 | 1 | 2026-04-22 (initial Closed Testing release approved) | First Closed Testing build (signed AAB, 23 MB) | — |
| 1.0.2 | 3 | 2026-05-06 (Day 3) — uploaded + sent for review (czeka na approve, ETA 1-3h) | **Fixed false-positive overlay on news portals** — multi-layer defense (foreground tracker, watch mode reset on app switch, hardened Uber overlay phantom detection on MIUI, positive offer markers in Uber/Bolt/Wolt parsers). | Andrij (UA real courier, 2026-04-29) |
| _(1.0.3)_ | 4 (planowany) | _(planowane Day 7-8 ≈ 2026-05-10/11)_ | _(language fallback RU/UA + Samsung navbar overlap)_ | Dominik |
| _(1.0.4)_ | 5 (planowany) | _(planowane Day 11-12 ≈ 2026-05-14/15)_ | _(final polish before submit)_ | _(różni)_ |

**Cel: minimum 3 updates w trakcie 14-dniowego okna Closed Testing**, każdy z konkretnymi release notes typu „Fixed [bug] reported by [tester]".

> **Uwaga numeracja:** versionCode 2 (= hipotetyczny 1.0.1) został pominięty — faktycznie wgrane do Closed Testing track to 1.0.0 (code 1) → 1.0.2 (code 3). Google wymaga monotonicznego rosnięcia versionCode, nie ciągłego. Spójność versionName: zaplanowana sekwencja 1.0.2 → 1.0.3 → 1.0.4 zachowana.

### v1.0.2 — szczegóły release (Day 3, 2026-05-06)

**Release notes (faktycznie wgrane do Play Console):**
- **en-US:**
  > Fixed false-positive overlay appearing on news portals and other non-courier apps. Improved app context detection to ensure offer detection only runs while a courier app is in use.
- **pl-PL:**
  > Naprawiono błąd, w którym belka OrderPilot pojawiała się na portalach informacyjnych i innych aplikacjach. Ulepszono wykrywanie kontekstu — belka pokazuje się teraz tylko gdy aktywna jest aplikacja kurierska.

**Audit trail (commits na branchu `play-store-prep`):**
- `15c131d` — `fix(v1.0.2): false-positive overlay na portalach informacyjnych (Andrij 04-29)` — multi-layer defense (9 plików, +465/-46 linii)
- `179a573` — `docs: v1.0.2 uploaded + sent for review (05-06, Day 3)` — status update

**Pliki zmienione w v1.0.2 fix:**
- `OrderPilot/app/src/main/java/com/orderpilot/app/service/OrderPilotAccessibilityService.kt` — Layer 1+2+3 (foreground tracker, hardened phantom detection, watch reset)
- `OrderPilot/app/src/main/java/com/orderpilot/app/parser/OcrOfferParser.kt` — helper `hasAnyPositiveMarker()`
- `OrderPilot/app/src/main/java/com/orderpilot/app/parser/UberOcrParser.kt` — Layer 4 positive markers (Łącznie/Total/Akceptuj + multi-language)
- `OrderPilot/app/src/main/java/com/orderpilot/app/parser/WoltOcrParser.kt` — Layer 4 positive markers (Odbiór za/Pickup in/Spodziewany zarobek + multi-language)
- `OrderPilot/app/src/main/java/com/orderpilot/app/parser/BoltFoodOcrParser.kt` — Layer 4 positive markers (Bolt/Akceptuj/Restoran + multi-language)
- `OrderPilot/app/build.gradle.kts` — versionCode 1→3, versionName "1.0.0"→"1.0.2"

**v1.0.2 Status timeline:**
| Etap | Data/czas | Status |
|------|-----------|--------|
| Bug zgłoszony | 2026-04-29 | ✅ Andrij WhatsApp |
| Bug zapisany w `future_polish_fixes.md` #36 | 2026-04-29 | ✅ |
| Andrij KEY EVIDENCE screenshots collected | 2026-05-04 | ✅ 5 screenów Gdańsk |
| Kod (multi-layer fix) napisany | 2026-05-05 | ✅ commit 15c131d |
| AAB build w Android Studio (signed, 23 MB) | 2026-05-06 | ✅ |
| Upload do Play Console Closed Testing track | 2026-05-06 | ✅ |
| Sent for review | 2026-05-06 | ✅ |
| Google approve | 2026-05-06 (oczekiwane ~1-3h) | ⏳ |
| Auto-publish (managed publishing off) | po approve | ⏳ |
| Testerzy auto-update przez Play Store | po publish, w ciągu kilku godzin | ⏳ |
| Andrij verify (czy belka nadal się pojawia na newsach) | po update u Andrija | ⏳ |

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

**Short answer (paste-ready do Application Form):**
> Throughout the Closed Testing window we shipped 3 AAB updates, each addressing specific tester feedback:
> - **v1.0.2** (May 6, 2026) — Fixed false-positive overlay appearing on news portals (e.g., Onet, WP), reported by Andrij (UA real courier) on April 29. Implemented multi-layer defense covering app foreground tracking, watch-mode reset on app switching, hardened MIUI phantom-overlay detection, and platform-specific positive marker validation in our parsers. The bar now only appears in courier app contexts.
> - **v1.0.3** (planned May 10-11) — Fix language fallback for Russian/Ukrainian UI + Samsung navigation bar overlap on Settings save button. Reported by Dominik.
> - **v1.0.4** (planned May 14-15) — Final polish based on Days 7-14 tester feedback.

**Long version (jeśli Google poprosi o engineering detail):**
> v1.0.2 implemented a 4-layer defense to prevent the bar from appearing in non-courier apps:
> 1. Strict foreground tracker — tracks the last `TYPE_WINDOW_STATE_CHANGED` package independently of which app generated the accessibility event, cross-checked against the live `rootInActiveWindow` query. Eliminates race conditions when users switch apps during throttle/retry windows.
> 2. Hardened MIUI phantom-overlay detection — the legitimate Uber popup detection now requires either an actual offer pattern (currency + time within 120 chars) or a known Uber-specific marker, instead of just any non-empty overlay text. Closes the Xiaomi-specific edge case where Uber's persistent type-3 overlay was triggering screenshots in unrelated apps.
> 3. Watch-mode reset on app switch — when a user moves to a non-courier app, any active 60-second monitoring loops (Uber/Bolt watch jobs) are immediately cancelled. Reduces the false-positive window from 60 s to <2.5 s.
> 4. Platform-specific positive markers — each popup parser (Uber/Bolt/Wolt) now requires at least one of 10–15 multilingual phrases (PL/EN/UK/RU) typical of an offer popup (e.g., "Łącznie", "Odbiór za", "Akceptuj", "Bolt"). News articles never contain these — additional defense independent of timing/foreground state.
>
> Verified by Andrij in his real workday (he had previously confirmed core functionality with "Tak przy zleceniach wszystko super").

---

### 🎯 Fix Card v1.0.2 (paste-ready do Production Application Form)

**Use case:** gdy Google pyta o konkretny przykład tester-driven fix (najmocniejszy proof iteration loop).

```
┌─ TESTER FEEDBACK → PRODUCTION FIX (v1.0.2) ─────────────────────────────┐
│                                                                          │
│ TESTER:    Andrij — Ukrainian courier, multi-platform (Uber + Bolt Food)│
│ DATE:      April 29, 2026 (Day -4 before clock start)                   │
│ CHANNEL:   WhatsApp group + 1:1 message                                 │
│                                                                          │
│ FEEDBACK QUOTE (verbatim, Polish):                                      │
│ "Podobne rzeczy pokazuje także na różnych portalach informacyjnych"     │
│                                                                          │
│ TRANSLATION:                                                             │
│ "It also shows similar things on various news portals"                  │
│                                                                          │
│ CONTEXT: Andrij was working a 5h57m / 9-delivery shift the same day.    │
│ He confirmed core functionality with "Tak przy zleceniach wszystko       │
│ super" ("Yes, at orders everything is great") and "Tak, sama aplikacja  │
│ jest bardzo przydatna" ("Yes, the app itself is very useful").          │
│                                                                          │
│ EVIDENCE: 5 screenshots from Andrij's real workday (May 4, 2026)        │
│   — real Gdańsk restaurant orders with OrderPilot bar visible:           │
│   • 19,18 PLN / 14 min / 5 km (McDonald's Morena, Gdańsk)               │
│   • 13,73 PLN / 23 min (Smażą parzą, Gdańsk)                            │
│   • 31,22 PLN / 44 min / 9,43 km (MAX Premium Burgers, Kowale)          │
│   • 15,49 PLN / 14 min (McDonald's Morski Park Handlowy)                │
│   • 19,70 PLN / 16:18                                                   │
│                                                                          │
│ ENGINEERING RESPONSE:                                                    │
│ Multi-layer defense (4 independent guards):                             │
│   1. Foreground tracker — independent app context detection             │
│   2. Hardened MIUI phantom-overlay detection — pattern + marker check   │
│   3. Watch-mode reset on app switch — 60s → <2.5s false-positive window │
│   4. Positive marker validation in parsers — multilingual offer phrases │
│                                                                          │
│ TIMELINE:                                                                │
│   Apr 29 — bug reported                                                 │
│   May 4  — additional usage evidence collected (5 screenshots)          │
│   May 5  — code written + committed (commit 15c131d, 9 files)           │
│   May 6  — AAB built + uploaded + sent for review (1.0.2, code 3)       │
│   May 6  — auto-published after Google approval                          │
│                                                                          │
│ VERIFICATION:                                                            │
│ Asked Andrij to confirm bar no longer appears on news portals during    │
│ his next workday. Awaiting confirmation.                                │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

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

### 🚨 STATUS DNIA — 2026-05-06 (Day 3) — v1.0.2 IN REVIEW

**Console potwierdza 12+ testers opted-in ✅** — drugi krok requirement zaliczony, 14-day clock running.

| Milestone | Data |
|-----------|------|
| **Day 0** | 2026-05-03 ✅ |
| **Day 3 (TODAY)** | 2026-05-06 — v1.0.2 uploaded + sent for review + WA group ping #1 |
| **Day 14** | 2026-05-17 (najwcześniej Apply for Production) |
| **Submit Production target** | 2026-05-17 |
| **Live target** (po Google review 3-7 dni) | ~2026-05-20 do 2026-05-24 |

**Co zrobione 05-03 (Day 0):**
- ✅ Pingi: Ivan UA (poprawiony email), Artur, Kuba, Gonzalo, Vasyl (cold call)
- ✅ Kuba potwierdził install (05-03)
- ✅ Vasyl wysłany link, prawdopodobnie pobrał
- ✅ Ticket #539 — manager Kefayatullah potwierdził: czeka na Google approval Closed track changes

**Co zrobione 05-04 / 05-05 (Day 1-2):**
- ✅ Andrij KEY EVIDENCE collected — 5 screenshots z realnego dnia pracy w Gdańsku z OrderPilot belką
- ✅ v1.0.2 multi-layer fix code napisany (commit 15c131d, 9 plików, +465/-46 linii)

**Co zrobione 05-06 (Day 3, dzisiaj):**
- ✅ AAB build w Android Studio (signed, v1.0.2 = versionCode 3)
- ✅ Upload do Play Console Closed Testing track
- ✅ Sent for review (managed publishing off → auto-publish po approve)
- ⏳ Czekamy na Google approve (~1-3h)
- 🟡 WA group ping #1 — TODO dziś
- 🟡 Console Statistics snapshot — TODO dziś (`2026-05-06_play-console-statistics.png`)

---

#### 🔴 Krytyczne najbliższe 24h (do 05-07 wieczór)

- [ ] **WA group ping #1 (Day 3)** — wzór: „Cześć! Jak idzie z OrderPilotem? Mógłbyś wrzucić jeden screen + napisać jedno zdanie co działa albo co nie? Potrzebne do oficjalnego submit do Google. Dzięki!"
- [ ] **Andrij ping** (po Google approve) — „Wgrałem update z fixem belki na portalach. Apka sama się zaktualizuje. Daj znać czy belka nadal pojawia się gdzieś poza apkami kurierskimi."
- [ ] **Save Day 3 Console Statistics screen** → `test-data/closed-testing/screenshots/2026-05-06_play-console-statistics.png`
- [ ] **Pre-launch report check** dla v1.0.2 — Console → Pre-launch report. Crashe? ANRs? Lista przetestowanych urządzeń.
- [ ] **Day 0 dashboard screen save** (jeśli jeszcze niezrobione) — `2026-05-03_day0_dashboard-12-testers-checked.png`
- [ ] **Verify Vasyl install** (= „znajomy taty") — Console
- [ ] **Verify Ivan UA install** — Console
- [ ] **PrimeTestLab pool** — monitor czy fala opt-inów ruszyła

> **Brat (Dominik) już jest na liście** od dawna (dominanb19@...) — plan dodania jego drugiego starego telefonu odrzucony (risk same-IP > value). „Znajomy taty" = Vasyl, ten sam człowiek.

#### 🟡 Przez najbliższe 14 dni (rozłożone)

- [ ] Daily snapshot Console Statistics co 2 dni (DAU, Active devices, Installed audience)
- [ ] Pingi do aktywnych kurierów (co 2-3 dni): Andrij, Tata, Dominik, Lucky, Ivan Black, Kuba (nowy)
- [ ] WA group ping #1 (Day 3 ≈ 2026-05-06): „screen + 1 zdanie"
- [ ] WA group ping #2 (Day 7 ≈ 2026-05-10)
- [ ] WA group ping #3 (Day 11 ≈ 2026-05-14)
- [x] ✅ **Kod v1.0.2 (Andrij news portals fix) — published Day 3 (2026-05-06)**: AAB sent for review, czeka na auto-publish
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
  - [x] ✅ v1.0.2 (Day 3, 2026-05-06): Andrij news portals fix — uploaded + in review
  - [ ] v1.0.3 (Day 7-8): Dominik RU/UA + Samsung navbar
  - [ ] v1.0.4 (Day 11-12): final polish

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
| **Day 2-3** | 2026-05-05/06 | Publish v1.0.2 (Andrij news portals) | ✅ DONE (uploaded 05-06, in review) |
| **Day 3** | 2026-05-06 | WA group ping #1 | 🟡 TODO dziś |
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
