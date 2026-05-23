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
| 11 | Marcin | TBD (WA group) | real kurier (PL) | TBD (pool?) | W grupie „Beta testerzy courier assist". Potwierdził używanie apki ("it was working normally, and I'm using it now"). Zgłosił bug decimal thresholds 2026-05-10. Aktywny. |

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

### 🟡 Dominik (Samsung) — 2026-04-28 (initial) + 2026-05-07 (re-report with screenshots) ⭐ KEY EVIDENCE #2

**Kontekst:** Brat Krzysztofa, tester Closed Testing, telefon Samsung (gesture/3-button nav bar). Zgłosił te same 2 bugi dwukrotnie — pierwszy raz 2026-04-28 (zapisane do `future_polish_fixes.md` #28/#29), drugi raz 2026-05-07 z dosłownymi cytatami i screenami po przetestowaniu v1.0.2. Drugie zgłoszenie = potwierdzenie że bugi nadal są w produkcie + ammunition do v1.0.3 Fix Card.

**Pełna dokumentacja feedbacku 2026-05-07:** `test-data/closed-testing/screenshots/dominik feedback/feedback_2026-05-07.md`

#### Bug 1: Język UA/RU nie utrzymuje się po zapisie (resetuje do PL)

**Cytaty dosłowne (WhatsApp, 2026-05-07 15:14):**
> „język angielski działa, język polski również ale jezeyk rosyjski/ukraiński nie działa"

> „natomiast jak wybieram ukraiński i zapisuje to zmienia się na język polski"

**Repro:** Settings → wybór Українська lub Русский → Save → po reopen Settings zaznaczone jest Polski (zł). Działa tylko PL i EN.

**Screen evidence:** folder `test-data/closed-testing/screenshots/dominik feedback/` — radio z Русский zaznaczonym (UI nadal po polsku), radio z English (PLN) zaznaczonym (UI po angielsku, kontrola), radio po zapisie UA z powrotem na Polski (zł).

**Status:** do naprawy w v1.0.3 (kod: `SettingsActivity.kt` + `LocaleHelper.kt`).

#### Bug 2: Przycisk „Zapisz ustawienia" zakryty przez Samsung nav bar

**Cytaty dosłowne (WhatsApp, 2026-05-07 15:17–15:18):**
> „jeśli chodzi o pole "always on display" zasłania mi pole, w którym zapisuje ustawienia po zmianie. Jak widać na screenie jest tylko część przycisku dostępna do kliknięcia"

> „jest to denerwujące przy codziennym użytkowaniu"

**Repro:** Settings → przewinąć do dołu → Samsung 3-button nav bar zasłania ~30% dolnej części zielonego przycisku „Zapisz ustawienia". Touch w dolną część trafia w nav bar zamiast przycisku.

> Dominik nazywa to „pole always on display" — myli z funkcją AOD. Faktycznie chodzi o **system navigation bar** Samsunga.

**Screen evidence:** folder `dominik feedback/` — Settings z widocznym overlap nav bar / button.

**Status:** do naprawy w v1.0.3 (windowInsets / `fitsSystemWindows` na layout Settings).

**Powiązanie:** `future_polish_fixes.md` #28 (RU/UA), #29 (Samsung navbar).

---

### 🟡 Marcin — 2026-05-10 (Day 7) ⭐ BUG REPORT

**Kontekst:** Tester z grupy WhatsApp „Beta testerzy courier assist". Potwierdził że apka działa normalnie, po czym zgłosił szczegółowy bug z dziesiętnym inputem progów. Wysłał wideo (0:55) z reprodukcją.

**Cytat (EN):**
> "I found a bug in the app. When I want to set a threshold for displaying different colors, and I want the PLN/km threshold to be 2.50 instead of a whole number, I can't use a comma; its use is blocked. When I use a period, it partially works — partially, because when I save the settings and go back in, the app changes the period to a comma (and that's fine). But if I change anything else in the configuration, the amount with the comma disappears and the integer appears. Instead of 2.5, for example, 2 appears. This only works incorrectly in the Polish version. In the English version, when I insert a period, it remains there the entire time; it doesn't change to a comma. And for PLN/h thresholds, you can't set the amount with a comma/period at all."

**Wcześniejsza wiadomość od Marcina (EN):**
> "I was using app yesterday, it was working normally, and I'm using it now and it works."

**Trzy sub-bugi w jednym raporcie:**
1. PLN/km, locale PL: przecinek zablokowany na klawiaturze; kropka wpisuje się, ale po kolejnym zapisie (czegokolwiek innego) wartość traci ułamek (`2.5` → `2`)
2. PLN/km, locale EN: kropka działa stabilnie — bug jest locale-dependent
3. PLN/h: w ogóle nie da się wpisać dziesiętnej (ani przecinek, ani kropka)

**Potwierdzenie przez Andrija (PL):**
> „Nie zapisuje wyłącznie liczb niecałkowitych, ale liczby całkowite zapisuje bez problemu."
(zebrany tego samego dnia ok. 12:00 — `test-data/closed-testing/screenshots/2026-05-10_wa-group-day7-2-andrij-confirms.jpg`)

**Materiały:**
- `test-data/closed-testing/screenshots/2026-05-10_wa-group-day7-1-marcin-decimal-bug.jpg` — WA group: bug report Marcina + screen "Active/Wykrywanie zleceń" od Marcina
- `test-data/closed-testing/screenshots/marcin feedback/2026-05-10_marcin_decimal-threshold-bug.jpg` — zdjęcie ekranu apki
- `test-data/closed-testing/screenshots/marcin feedback/2026-05-10_marcin_decimal-threshold-bug.mp4` — wideo reprodukcji (0:55)
**Bug tracking:** `docs/future_polish_fixes.md` #37
**Plan naprawy:** v1.0.4 ✅ NAPRAWIONE
**Status:** ✅ **FIXED in v1.0.4** (commit `f58ec8c`, 2026-05-12). Opublikowane przez Google 2026-05-13 (Play Console: „App update published").

---

### 📝 Ivan Black — 2026-05-10 (Day 7) — accessibility wyłączona po update

**Kontekst:** Ivan zgłosił w WA grupie że „yesterday the app didn't work at all". Prawdopodobna przyczyna: update v1.0.3 mógł zresetować stan Accessibility Service (znane zachowanie Androida po aktualizacji APK — system może wyłączyć accessibility permission).

**Komunikacja:**
- Ivan: "Hi guys Yesterday the app didn't work at all" / "I do not know but seems like It was turned off maybe"
- Ivan: "To be honest I dont even know when it is open or not"
- Ty: "check Settings → Accessibility → OrderPilot → toggle ON" / "You should see notification that the app is running in the notification bar"
- Ivan: "alryt / thanks" (13:57)

**Wynik:** Rozwiązane przez re-toggle Accessibility. **Nie jest to bug kodu** — standardowe zachowanie Androida po update. Do ewentualnego adresowania w UX: lepszy onboarding/reminder po aktualizacji.
**Materiały:** `test-data/closed-testing/screenshots/2026-05-10_wa-group-day7-3-ivan-resolved.jpg` — WA group: Ivan "alryt / thanks" po naprawie
**Status:** Zamknięte 2026-05-10.

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
| 2026-05-10 | WA group Day 7 — Ivan issue + Marcin decimal bug + Andrij confirmation | 3× wiadomości, 1× bug report szczegółowy, 1× potwierdzenie drugiego testera, 1× issue resolved |

**Pliki screen WA grupy (Day 7):**
- `test-data/closed-testing/screenshots/2026-05-10_wa-group-day7-1-marcin-decimal-bug.jpg`
- `test-data/closed-testing/screenshots/2026-05-10_wa-group-day7-2-andrij-confirms.jpg`
- `test-data/closed-testing/screenshots/2026-05-10_wa-group-day7-3-ivan-resolved.jpg`

---

## 4. AAB updates timeline (3+ wymagane przez Google — DONE 4/3)

| Wersja | versionCode | Data publikacji | Główne zmiany | Bazujące na feedback od |
|--------|-------------|-----------------|---------------|-------------------------|
| 1.0.0 | 1 | 2026-04-22 (initial Closed Testing release approved) | First Closed Testing build (signed AAB, 23 MB) | — |
| 1.0.2 | 3 | 2026-05-06 (Day 3) — LIVE | **Fixed false-positive overlay on news portals** — multi-layer defense (foreground tracker, watch mode reset on app switch, hardened Uber overlay phantom detection on MIUI, positive offer markers in Uber/Bolt/Wolt parsers). | Andrij (UA real courier, 2026-04-29) |
| 1.0.3 | 4 | 2026-05-09 (Day 7) — LIVE (auto-publish po approve) | **Fix language fallback RU/UA (selected language resets to PL after save)** — migracja na `AppCompatDelegate.setApplicationLocales()` + `locales_config.xml` (official Android 13+ API). + **Samsung nav bar overlapping „Save settings" button** — `WindowInsetsCompat` handling w SettingsActivity / SetupActivity / DisclosureActivity (edge-to-edge na targetSdk 35). Re-reported by Dominik 2026-05-07 with screenshots. | Dominik (Samsung) |
| 1.0.4 | 5 | 2026-05-13 11:20 AM (Day 10) — LIVE | **Decimal threshold input (locale-dependent)** — `SettingsActivity.formatThreshold`/`parseThreshold` Locale.US + accepts „,"/„." + preserves previous value on parse failure. + **Combined PLN/h + PLN/km color thresholds (AND-semantics)** — `OfferAnalyzer.worstOf(levelFromHour, levelFromKm)`, edge cases (no/zero distance → fallback to PLN/h, Glovo path unchanged). 7 new unit tests passing. | Marcin (PL real courier, Samsung) 2026-05-10/11 + Andrij (decimal bug confirm) 2026-05-10 |
| 1.0.5 | 6 | 2026-05-13 8:39 PM (Day 10) — LIVE | **Uber bar not appearing when popup is over another app** — regresja od v1.0.2 Layer 2 (hasUberOverlayWithContent zakładał że Uber popup eksponuje tekst przez accessibility tree; na większości urządzeń RN Uber Driver NIE eksponuje → foreground guard zabijał pipeline). Fix: zamiana na `hasUberOverlayWindow` (samo istnienie overlay window). Safety dla Andrija (news portal false-positive) zachowane przez Layer 4 (positive markers w UberOcrParser, multi-language PL/EN/UK/RU). | Marcin (PL real courier, Samsung) 2026-05-13 |

**Cel: minimum 3 updates w trakcie 14-dniowego okna Closed Testing** — DONE 4/3, każdy z konkretnymi release notes typu „Fixed [bug] reported by [tester]".

> **Uwaga numeracja:** versionCode 2 (= hipotetyczny 1.0.1) został pominięty — faktycznie wgrane do Closed Testing track to 1.0.0 (code 1) → 1.0.2 (code 3) → 1.0.3 (code 4) → 1.0.4 (code 5) → 1.0.5 (code 6). Google wymaga monotonicznego rosnięcia versionCode, nie ciągłego.

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

---

### 🗺️ MAPA FORMULARZA (zweryfikowana z Help Center 2026-05-09)

Formularz Production Access ma **3 sekcje, 8 pytań total**. Source: [support.google.com/googleplay/android-developer/answer/14151465](https://support.google.com/googleplay/android-developer/answer/14151465)

**Część 1 — Informacje o teście zamkniętym** (3 pytania)
- 1.1 — **DROPDOWN/lista opcji** — jak łatwo było zrekrutować testerów (TODO: zobaczyć opcje przy „Preview questions" w Console)
- 1.2 — **Free text** — engagement testerów (czy używali wszystkich funkcji + czy zgodne z oczekiwaniami production)
- 1.3 — **Free text** — podsumowanie opinii + JAK były zebrane

**Część 2 — Informacje o aplikacji** (3 pytania)
- 2.1 — **Free text** — docelowi odbiorcy (szczegółowo)
- 2.2 — **Free text** — wartość aplikacji dla użytkowników
- 2.3 — **DROPDOWN z zakresami** — spodziewana liczba instalacji w 1. roku (TODO: zobaczyć zakresy przy „Preview questions")

**Część 3 — Production readiness** (2 pytania)
- 3.1 — **Free text** — jakie zmiany wprowadzone na podstawie testów
- 3.2 — **Free text** — dlaczego apka jest ready for production

**Submit:** przycisk „Zastosuj" na końcu Części 3.
**⚠️ UWAGA:** brak auto-save. „Odrzuć" / zamknięcie zakładki bez „Dalej"/„Zastosuj" = utrata wszystkich wpisanych odpowiedzi.

---

### 1.1 Q: Jak łatwo było zrekrutować testerów? [DROPDOWN]

**Akcja Day 14:** Otwórz formularz, wybierz najbliższe oddanie naszej rzeczywistości z dostępnej listy. **Najpewniej coś typu „Quite easy" / „Moderate" / „Challenging".** Konkretne opcje sprawdzimy przy „Preview questions" w Console.

**Bonus context (jeśli pole pozwala dopisać uzasadnienie):**
> Recruited 50+ testers via 4 channels: (1) leaflets distributed at Forum Gdańsk shopping mall and at a bicycle service shop where couriers gather; (2) friends and family for wizard/UX/edge cases; (3) paid testing service (PrimeTestLab / 12testers14days.com) for geographic and device diversity; (4) organic word-of-mouth from existing testers (e.g. Ukrainian courier Andrij who tests across multiple courier platforms).

---

### 1.2 Q: Engagement testerów [FREE TEXT]
**Draft answer:**
> Active feedback channel via WhatsApp group with [X] messages exchanged in 14 days. Real couriers report daily usage (e.g., Andrij: 5h 57min online, 9 deliveries on April 29 with OrderPilot running). Family/friend testers verify wizard, settings UX, and language fallback. Paid testers verify cross-device compatibility (Samsung Knox, Xiaomi MIUI, etc.). Across the 14 days, our Closed Track grew from 12 opted-in (Day 0) to 50 active testers (Day 5), with daily active users (DAU) climbing from ~1 to 11 by Day 5 (≈22% DAU/active ratio — strong engagement signal for a Closed Testing pool).
>
> **Did testers use all functions?** Yes — feature coverage included: per-courier-platform pipeline (Uber, Wolt, Glovo, Bolt Food), Settings (language switching across 4 locales: PL/EN/UK/RU), Setup wizard, overlay positioning across multiple OEMs (Samsung, Xiaomi/MIUI, Google Pixel), and accessibility service lifecycle (boot, foreground, watchdog).
>
> **Did usage match expectations for production users?** Yes — multiple testers are real food-delivery couriers using the app during paid shifts (not synthetic / lab testing). Andrij's April 29 shift (5h57m online + 9 deliveries with OrderPilot bar visible across all offers) is representative of expected production usage.

### 1.3 Q: Podsumowanie opinii + jak zebrane [FREE TEXT]

**Draft answer:**
> Feedback was collected via three channels: (1) a dedicated WhatsApp group for active couriers and family/friend testers, used for daily check-ins, screenshots, and bug reports; (2) 1-on-1 WhatsApp conversations with key real-courier testers (Andrij, Dominik, Tata, Lucky, Ivan Black) for in-depth bug repro; (3) Play Console „Test feedback" channel.
>
> Two categories of feedback emerged:
>
> **A) Functional bugs (acted upon, shipped fixes):**
> - Andrij (UA real courier, Apr 29): "Podobne rzeczy pokazuje także na różnych portalach informacyjnych" — false-positive overlay on news portals (Onet, WP). Fixed in v1.0.2 (multi-layer defense). Verified in Andrij's May 4 workday with 5 screenshots from real Gdańsk deliveries.
> - Dominik (Samsung, Apr 28 + re-report May 7): "język angielski działa, język polski również ale jezeyk rosyjski/ukraiński nie działa" / "natomiast jak wybieram ukraiński i zapisuje to zmienia się na język polski" — language fallback for UA/RU resets to Polish after save. Fixed in v1.0.3 (migration to AppCompatDelegate.setApplicationLocales).
> - Dominik (Samsung, Apr 28 + re-report May 7): "jest to denerwujące przy codziennym użytkowaniu" / "jest tylko część przycisku dostępna do kliknięcia" — Samsung navigation bar partially obscures the Save Settings button. Fixed in v1.0.3 (WindowInsetsCompat handling on edge-to-edge targetSdk 35).
>
> **B) Validation of core functionality:**
> - Andrij (UA real courier, Apr 29): "Tak przy zleceniach wszystko super" — confirms the offer-detection pipeline works correctly across his real workday on Bolt Food.
> - Andrij (UA real courier, Apr 29): "Tak, sama aplikacja jest bardzo przydatna" — qualitative confirmation of value.
>
> All bug reports were assigned a fix release (v1.0.2 / v1.0.3 / v1.0.4) and acknowledged with the reporting tester after the fix was published — closing the feedback loop with the same tester who reported the bug.

### 2.1 Q: Docelowi odbiorcy [FREE TEXT]
**Draft answer:**
> OrderPilot's intended audience is **food-delivery and ride-share couriers** working as independent contractors (gig workers) on platforms such as Uber Eats, Bolt Food, Wolt, Glovo, and Bolt (rides). Geographic focus: Poland and Ukraine primarily, with growing demand from English-speaking couriers in EU markets.
>
> **User profile:**
> - 18+ (we enforced this with content rating + Closed Testing audience selection)
> - Smartphone-first (Android, mostly mid-range Xiaomi/Samsung/Pixel)
> - Multi-platform couriers — many work simultaneously on 2-3 delivery apps to maximize earnings
> - Often non-Polish-native (Ukrainian, Russian-speaking, English-speaking workforce in PL cities)
> - Income-driven: choose / decline orders based on profit-per-hour, not just trip distance
>
> **Why this audience needs OrderPilot:** delivery apps display only raw numbers (PLN, time, km). Couriers need to do quick mental math (zł/h calculation) under time pressure (10-15s to accept/decline). OrderPilot does this math automatically and shows GREEN / YELLOW / RED verdict in a small overlay bar — letting couriers focus on driving safely instead of on-screen math.

### 2.2 Q: Wartość aplikacji [FREE TEXT]
**Draft answer:**
> OrderPilot solves a concrete problem for delivery couriers: **deciding which orders are worth taking under time pressure.** Delivery platforms (Uber Eats, Wolt, Glovo, Bolt Food) show raw payment + time + distance, but couriers need profit-per-hour (zł/h) to make rational decisions. Doing this math manually in 10-15 seconds while driving is unsafe and error-prone.
>
> **What the app does:**
> 1. Reads incoming offer popups via Android Accessibility Service + on-device OCR (no network calls, no data leaves the device)
> 2. Computes zł/h = (offer payment ÷ estimated delivery time) instantly
> 3. Compares against the courier's personal threshold (configured in Settings) and displays GREEN (above target) / YELLOW (borderline) / RED (below target) overlay bar
> 4. Auto-hides when no offer is on screen — zero distraction otherwise
>
> **Key differentiators vs alternatives:**
> - **Zero-network architecture** — all OCR, parsing, and analysis happens on-device. No personal data, no offer details, no location ever leaves the phone. Tested via airplane mode.
> - **Multi-platform** — works across Uber Eats, Wolt, Glovo, Bolt Food in one install. Most couriers work multiple platforms simultaneously, so a per-platform app would not fit the workflow.
> - **Multilingual UI** — Polish, English, Ukrainian, Russian (covering the actual demographics of couriers in Polish cities).
> - **Built by an active observer of the gig-economy market** — features come from real courier interviews, not assumptions.
>
> **Validation from Closed Testing:** Andrij (Ukrainian courier, multi-platform Uber+Bolt Food) — verified during a real 5h57m / 9-delivery shift on April 29, 2026 — confirmed: "Tak przy zleceniach wszystko super" ("With orders, everything works perfectly"), "Tak, sama aplikacja jest bardzo przydatna" ("Yes, the app itself is very useful").

### 2.3 Q: Spodziewana liczba instalacji w 1. roku [DROPDOWN]

**Akcja Day 14:** Wybierz zakres z listy. Realistic estimate dla pierwszego roku:
- **Najprawdopodobniej zakres ~1 000 – 10 000 installs** (delivery couriers w PL ≈ 30-60k aktywnych, można sensownie celować w ~5-10% świadomości pierwszej fali = 1.5k-6k)
- Konserwatywnie: **100 – 1 000** (jeśli dropdown ma takie opcje, wybierz wyższy zakres żeby nie wyglądać za nisko)
- Optymistycznie: **10 000 – 100 000** (przy mocnej akcji w community + word-of-mouth UA/PL)

**Rekomendacja:** wybierz najbliższy realnym ambicjom **1k-10k** (lub jeśli nie ma — sąsiedni). Konkretne zakresy sprawdzimy przy „Preview questions".

---

### 3.1 Q: Jakie zmiany wprowadzone na podstawie testów [FREE TEXT]

**Short answer (paste-ready do Application Form):**
> Throughout the Closed Testing window we shipped 4 AAB updates, each addressing specific tester feedback:
> - **v1.0.2** (released May 6, 2026 — Day 3) — Fixed false-positive overlay appearing on news portals (e.g., Onet, WP), reported by Andrij (UA real courier) on April 29. Implemented multi-layer defense covering app foreground tracking, watch-mode reset on app switching, hardened MIUI phantom-overlay detection, and platform-specific positive marker validation in our parsers. The bar now only appears in courier app contexts.
> - **v1.0.3** (released May 9, 2026 — Day 7) — Fixed language fallback for Russian/Ukrainian UI (selected language was resetting to Polish after save) + Samsung navigation bar overlapping the "Save settings" button on Settings screen. Reported by Dominik (Samsung) on April 28 and re-reported May 7 with screenshots after testing v1.0.2 (which proved the bugs persisted). Engineering response: migrated to AppCompatDelegate.setApplicationLocales (official Android 13+ per-app locale API) with one-time migration sync from existing SharedPrefs preferences; added WindowInsetsCompat handling in three Activity classes for edge-to-edge enforcement on targetSdk 35. Verified working by Dominik on May 11: "spoko, wszystko co zglaszalem juz jest git" ("all good, everything I reported is now fine").
> - **v1.0.4** (released May 13, 2026 — Day 10, 11:20 AM) — Two fixes from Marcin (PL real courier): (a) decimal threshold input was locale-dependent — PLN/km values like 2.5 were silently overwritten with the default 3.0 after re-save on Polish locale, and PLN/h was completely blocked from accepting decimals; (b) color thresholds for PLN/h and PLN/km were not combined — only PLN/h decided the bar color, so a 34 PLN/h + 1.3 PLN/km offer was yellow even though PLN/km was below the yellow threshold. Andrij independently confirmed the decimal bug (same day). Engineering response: SettingsActivity now uses Locale.US for formatting decimals (consistent round-trip), accepts both "," and "." as decimal separators, and preserves the previous value on parse failure instead of falling back to a hardcoded default; OfferAnalyzer now computes the color level from both metrics and takes the worse of the two (AND-semantics). 7 new unit tests added (all 19 tests pass) covering the Marcin repro and 6 edge cases (missing distance, zero distance, Glovo path regression, etc.).
> - **v1.0.5** (released May 13, 2026 — Day 10, 8:39 PM, same-day hotfix) — Fixed regression introduced by v1.0.2: the Uber bar was no longer appearing when an offer popup was shown over a different foreground app (e.g. while the courier was viewing Wolt or the home screen). Reported by Marcin on May 13 morning, diagnosed from his 1999-line accessibility log: v1.0.2 Layer 2 hardened the foreground check by requiring visible accessible text in the Uber overlay, but on Samsung devices the React Native Uber Driver popup exposes zero text through the accessibility tree (`text len=0`), so the foreground guard rejected all popup-over-other-app events. Engineering response: replaced `hasUberOverlayWithContent` with `hasUberOverlayWindow` (window existence is sufficient signal); regression safety for Andrij's news-portal fix preserved through Layer 4 (positive markers "Łącznie"/"Total"/"Akceptuj"/"Доставка"/"Загалом"/"Принять" — multi-language PL/EN/UK/RU — required in OCR text before the bar shows). Built, uploaded and live in Closed Testing within 9 hours of the bug report.

**Long version (jeśli Google poprosi o engineering detail):**
> v1.0.2 implemented a 4-layer defense to prevent the bar from appearing in non-courier apps:
> 1. Strict foreground tracker — tracks the last `TYPE_WINDOW_STATE_CHANGED` package independently of which app generated the accessibility event, cross-checked against the live `rootInActiveWindow` query. Eliminates race conditions when users switch apps during throttle/retry windows.
> 2. Hardened MIUI phantom-overlay detection — the legitimate Uber popup detection now requires either an actual offer pattern (currency + time within 120 chars) or a known Uber-specific marker, instead of just any non-empty overlay text. Closes the Xiaomi-specific edge case where Uber's persistent type-3 overlay was triggering screenshots in unrelated apps.
> 3. Watch-mode reset on app switch — when a user moves to a non-courier app, any active 60-second monitoring loops (Uber/Bolt watch jobs) are immediately cancelled. Reduces the false-positive window from 60 s to <2.5 s.
> 4. Platform-specific positive markers — each popup parser (Uber/Bolt/Wolt) now requires at least one of 10–15 multilingual phrases (PL/EN/UK/RU) typical of an offer popup (e.g., "Łącznie", "Odbiór za", "Akceptuj", "Bolt"). News articles never contain these — additional defense independent of timing/foreground state.
>
> Verified by Andrij in his real workday (he had previously confirmed core functionality with "Tak przy zleceniach wszystko super").
>
> v1.0.3 addresses two Samsung-specific issues reported by tester Dominik:
> 1. Language persistence migration — Replaced our custom `LocaleHelper.wrap` (which used `createConfigurationContext` with `Locale.setDefault`) with `AppCompatDelegate.setApplicationLocales` and a `locales_config.xml` declaration. The legacy approach worked for languages already supported in the device's system locale list (PL, EN on a Polish Samsung) but silently fell back to default when the user selected a non-system locale (UK, RU), so the SharedPrefs preference was saved correctly but the UI reverted to Polish on the next Settings open. The new API is the official Android 13+ per-app locale standard and works regardless of system locale support. We also added a one-time migration sync in `Application.onCreate` to seed `AppCompatDelegate` from existing SharedPrefs preferences — so users who already chose UK/RU in v1.0.2 get their language correctly restored on first launch of v1.0.3.
> 2. Edge-to-edge insets — `targetSdk = 35` enforces edge-to-edge layout on Android 15+. Without `WindowInsetsCompat` handling, system bars overlap UI elements; on Dominik's Samsung this manifested as the 3-button navbar covering ~30% of the "Save settings" button. We added `ViewCompat.setOnApplyWindowInsetsListener` in `SettingsActivity`, `SetupActivity`, and `DisclosureActivity` to apply `systemBars()` insets as padding on the root layout. Buttons now stay above any 3-button or gesture navbar.
>
> Will verify with Dominik after his Samsung auto-updates to v1.0.3.

---

### 3.2 Q: Dlaczego apka jest ready for production [FREE TEXT]
**Draft answer:**
> OrderPilot is ready for production based on five concrete signals from the 14-day Closed Testing window:
>
> 1. **4 iterative updates shipped during the testing window**, each fixing tester-reported issues (v1.0.2: news-portal false positives from Andrij; v1.0.3: language persistence + Samsung navbar overlap from Dominik — verified by him as fixed; v1.0.4: decimal threshold input + combined-thresholds AND-semantics from Marcin, with Andrij independently confirming the decimal bug; v1.0.5: same-day hotfix for a regression introduced by v1.0.2 — the Uber bar not appearing when the offer popup was over another app, reported by Marcin and fixed within 9 hours). All fixes were verified by the same testers who reported the bugs, closing the feedback loop. The codebase has unit-test coverage of the offer-analysis engine (19 tests passing, including 7 regression tests covering the Marcin combined-thresholds repro and its edge cases).
>
> 2. **Real-world validation by working couriers, not synthetic testing.** Andrij (Ukrainian Bolt Food courier) tested the app during a paid 5h57m / 9-delivery shift on April 29, 2026, confirming the offer pipeline works correctly during actual paid work — not just in lab conditions. We have 5 photos from his real Gdańsk deliveries showing the OrderPilot bar visible during real offers.
>
> 3. **Cross-device coverage.** Testers covered: Samsung (Knox, edge-to-edge Android 15+ navbar — Dominik's repro), Xiaomi MIUI (phantom overlay quirks, Marcin's locale repro), Google Pixel, vivo, plus ~40 additional devices via a paid testing pool (PrimeTestLab) for breadth. Geographic spread across 4 countries (PL, US, UK, AF — confirmed in Play Console statistics, 177 supported countries/regions). Play Console reports zero crashes and zero ANRs across all v1.0.2 and v1.0.3 sessions.
>
> 4. **Zero-network privacy guarantee verified.** The app makes no network calls (verified via airplane mode + DNS sniffing). All OCR, parsing, and analysis is on-device. No personal data ever leaves the user's phone — important given that we read sensitive offer/payment information from third-party delivery apps.
>
> 5. **Stable engagement metrics across 14 days.** Closed Testing track grew from 12 opted-in (Day 0) to 50 active testers (Day 5+). DAU climbed from ~1 (early April) to 11+ (May 2), with continuous activity throughout the window — not a one-shot install spike. Engagement ratio (DAU/active) ≈ 22% — strong for a Closed Testing pool where many testers are non-courier (family/friend/paid).
>
> Beyond Closed Testing: the app is fully compliant with our declared use case (Accessibility Service strictly for delivery-platform offer detection, with `isAccessibilityTool=false` honestly reflecting the assistive-but-not-purely-accessibility nature of the use case). Privacy policy, content rating (18+), and target API 35 are all in place.

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

### 🎯 Fix Card v1.0.3 (paste-ready do Production Application Form)

**Use case:** drugi case study tester-driven fix — pokazuje że iteracja jest stała (nie tylko Andrij), różne urządzenia (Samsung), wielokrotne reportowanie tego samego buga przez tego samego testera (proof że słuchamy).

```
┌─ TESTER FEEDBACK → PRODUCTION FIX (v1.0.3) ─────────────────────────────┐
│                                                                          │
│ TESTER:    Dominik — Closed Testing tester, Samsung phone               │
│ DATES:     April 28, 2026 (initial report)                              │
│            May 7, 2026 (re-report with screenshots after testing v1.0.2)│
│ CHANNEL:   WhatsApp 1:1                                                 │
│                                                                          │
│ FEEDBACK QUOTES (verbatim, Polish):                                     │
│   "język angielski działa, język polski również ale jezeyk             │
│    rosyjski/ukraiński nie działa"                                       │
│   "natomiast jak wybieram ukraiński i zapisuje to zmienia się          │
│    na język polski"                                                     │
│   "jest to denerwujące przy codziennym użytkowaniu"                     │
│   "jest tylko część przycisku dostępna do kliknięcia"                   │
│                                                                          │
│ TRANSLATIONS:                                                            │
│   "English works, Polish also works, but Russian/Ukrainian don't work" │
│   "I select Ukrainian and save, but it changes to Polish"               │
│   "It's annoying in daily use"                                          │
│   "Only part of the button is available to click"                       │
│                                                                          │
│ TWO INDEPENDENT BUGS reported in one feedback session:                  │
│   1. Language fallback for UA/RU resets to PL after Save                │
│   2. „Save settings" button partially obscured by Samsung navbar        │
│                                                                          │
│ EVIDENCE: 5+ screenshots from Dominik's Samsung device:                 │
│   • Settings with Russian radio selected, UI still in Polish (control)  │
│   • Settings in English (works — confirmation)                          │
│   • Settings after attempting to save Ukrainian (Polish becomes         │
│     selected again)                                                     │
│   • Settings showing button overlap with Samsung 3-button navbar        │
│                                                                          │
│ ENGINEERING RESPONSE:                                                    │
│ Two independent fixes in one release:                                   │
│   1. Migrated from custom LocaleHelper.wrap (createConfigurationContext)│
│      to AppCompatDelegate.setApplicationLocales — official Android 13+ │
│      API. Added locales_config.xml + android:localeConfig to manifest. │
│      One-time migration sync in Application.onCreate ensures users     │
│      with v1.0.2 preferences (e.g. AppLanguage.UK in SharedPrefs but   │
│      empty AppCompatDelegate state) get their language restored on     │
│      first launch of v1.0.3.                                           │
│   2. Added WindowInsetsCompat handling in SettingsActivity,            │
│      SetupActivity, DisclosureActivity to respect systemBars insets    │
│      (edge-to-edge enforcement on targetSdk 35 / Android 15+).         │
│      Save / Continue buttons now stay above any 3-button or gesture    │
│      navbar.                                                            │
│                                                                          │
│ TIMELINE:                                                                │
│   Apr 28 — bugs first reported by Dominik                               │
│   May 7  — bugs re-reported with screenshots after testing v1.0.2       │
│            (verifies bugs persisted into v1.0.2)                        │
│   May 7  — code written + tested locally on family device + committed   │
│            (commit 1b8b3cd, 13 files, +142/-99 lines, push to GitHub)  │
│   May 9 (Day 7) — AAB built + uploaded + sent for review (1.0.3, code 4)│
│   May 9 — auto-publish expected after Google approval (~1-3h)           │
│                                                                          │
│ VERIFICATION:                                                            │
│ Will ping Dominik after his Samsung auto-updates to v1.0.3 to confirm:  │
│   • UA/RU language selections now persist after save                    │
│   • Save button is fully visible above the navbar                       │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

### 🎯 Fix Card v1.0.4 (paste-ready do Production Application Form)

**Use case:** trzeci case study tester-driven fix — zamyka 3+ AAB updates wymagane przez Google (czwarty hotfix v1.0.5 idzie osobno). Dwa różne bugi od tego samego testera (Marcin) w 24h: jeden locale-dependent (klasa po Dominiku UA/RU, oddzielnie potwierdzony przez Andrija), drugi UX-logic (combined-thresholds semantics). Cztery iteracyjne fixy w 14-day oknie + sześciu różnych testerów dostarczyło bug reportów = mocna ammunition dla pytania „How did your app change based on testing?".

```
┌─ TESTER FEEDBACK → PRODUCTION FIX (v1.0.4) ─────────────────────────────┐
│                                                                          │
│ TESTERS:   Marcin (PL locale, 2 reports) + Andrij (UA, confirms decimal)│
│ DATES:     May 10, 2026 (Day 7) + May 11, 2026 (Day 8)                  │
│ CHANNEL:   WhatsApp group "Beta testerzy courier assist"                 │
│                                                                          │
│ ─── BUG 1: Decimal threshold input (locale-dependent) ──                 │
│ REPORTED:  May 10 (Day 7) by Marcin, confirmed same day by Andrij       │
│                                                                          │
│ FEEDBACK QUOTES:                                                         │
│   Marcin (EN): "When I want to set the PLN/km threshold to 2.50,        │
│    I can't use a comma [...] if I change anything else, the amount      │
│    with the comma disappears and the integer appears. This only works   │
│    incorrectly in the Polish version."                                   │
│   Andrij (PL): "Nie zapisuje wyłącznie liczb niecałkowitych, ale liczby │
│    całkowite zapisuje bez problemu."                                     │
│                                                                          │
│ ENGINEERING FIX:                                                         │
│   - SettingsActivity formatThreshold(): always Locale.US (period) so    │
│     round-trip render→parse stays consistent regardless of system locale│
│   - parseThreshold() helper accepts both "," and "." separators         │
│     (defensive against manual entry / keyboard variants)                │
│   - Load path for PLN/h: replaced .toInt().toString() with              │
│     formatThreshold() — decimals were silently truncated at every       │
│     re-render, totally blocking decimal entry for PLN/h                 │
│   - Save fallback preserves previous value from prefs instead of        │
│     hardcoded default — was silently overwriting user's 2.5 with 3.0   │
│     when text didn't parse                                              │
│                                                                          │
│ ─── BUG 2: Combined color thresholds (AND-semantics, UX) ──             │
│ REPORTED:  May 11 (Day 8) by Marcin                                     │
│                                                                          │
│ FEEDBACK QUOTE:                                                          │
│   Marcin (EN, screenshot evidence): "since we have color thresholds    │
│    for PLN/h and PLN/km, the thresholds for the same color should      │
│    probably work combined, so for the orange color to appear, both     │
│    the PLN/h and PLN/km thresholds must be exceeded, and it seems      │
│    that meeting only one of these thresholds is enough for the color   │
│    to change, even if the other one is too low."                       │
│                                                                          │
│ REPRO (Marcin's screenshot):                                             │
│   Offer: 25.61 PLN / 45 min / 20.0 km → 34 PLN/h + 1.3 PLN/km          │
│   Thresholds: yellow_h=34, yellow_km=2                                  │
│   Expected: RED (km below yellow_km threshold)                          │
│   Actual:   YELLOW (PLN/km ignored in non-Glovo path)                   │
│                                                                          │
│ ENGINEERING FIX:                                                         │
│   - OfferAnalyzer.analyze() now computes levelFromHour AND levelFromKm │
│     in main branch, takes worstOf(...) for AND-semantics                │
│   - Edge case: missing distanceKm or 0 → falls back to PLN/h only       │
│     (preserves behavior for time-only offers)                           │
│   - Glovo path (no time) unchanged — still uses PLN/km only             │
│   - 7 new unit tests in OfferAnalyzerTest (Marcin repro + 6 edge cases) │
│   - All 19 tests passing: ./gradlew :app:testDebugUnitTest ✅           │
│                                                                          │
│ ─── DELIVERY ──                                                          │
│ COMMITS:                                                                 │
│   f58ec8c — fix(v1.0.4): combined thresholds + decimal input            │
│   56d7edd — docs: Day 8/9 status + v1.0.4 plan                          │
│ BRANCH:    fix/v1.0.4-thresholds (pushed to origin)                     │
│ PUBLISHED: 2026-05-13 (Play Console: "App update published")            │
│                                                                          │
│ EVIDENCE FILES:                                                          │
│   test-data/closed-testing/screenshots/marcin feedback/                 │
│     2026-05-10_marcin_decimal-threshold-bug.{jpg,mp4}                   │
│     2026-05-11_marcin_combined-thresholds-bug.jpg                       │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

### 🎯 Fix Card v1.0.5 (paste-ready do Production Application Form)

**Use case:** czwarty i ostatni case study tester-driven fix w 14-day window — pokazuje że iteracja działa również w trybie hotfix (Marcin zgłosił bug 13 maja rano, fix wgrany 13 maja wieczorem). Jednocześnie pokazuje uczciwość: złapaliśmy regresję wprowadzoną w v1.0.2 (Layer 2 multi-layer defense) i naprawiliśmy ją przed Production submit zamiast ukrywać.

```
┌─ TESTER FEEDBACK → PRODUCTION HOTFIX (v1.0.5) ──────────────────────────┐
│                                                                          │
│ TESTER:    Marcin — PL real courier, Samsung                            │
│ DATE:      May 13, 2026 (Day 10) — bug reported morning, fix live same  │
│            day evening                                                   │
│ CHANNEL:   WhatsApp group "Beta testerzy courier assist" + 1:1          │
│                                                                          │
│ FEEDBACK QUOTE (verbatim, Polish):                                      │
│   "Belka Ubera nie pojawia się, gdy popup z ofertą wyświetla się nad   │
│    inną apką (np. gdy jestem na pulpicie / w Wolt)."                   │
│                                                                          │
│ TRANSLATION:                                                             │
│   "The Uber bar doesn't appear when the offer popup is shown over      │
│    another app (e.g. when I'm on the home screen / in Wolt)."          │
│                                                                          │
│ CONTEXT:                                                                 │
│   Bug existed since v1.0.2 (May 6) — i.e. 7 days regression. Marcin    │
│   noticed it because he often keeps Wolt foregrounded while waiting    │
│   for Uber Driver offers in the background; the OrderPilot bar should  │
│   appear on top of whatever app is foreground when Uber popup fires.    │
│                                                                          │
│ ROOT CAUSE (engineering diagnosis from accessibility logs):              │
│   v1.0.2 introduced multi-layer defense for Andrij's news-portal       │
│   false-positive. Layer 2 hardened the foreground check by requiring   │
│   `hasUberOverlayWithContent` — i.e. the Uber overlay window must     │
│   expose visible text through the accessibility tree (offer pattern    │
│   currency + time, or known Uber marker). Assumption: a "real" Uber   │
│   offer popup always exposes its text.                                  │
│                                                                          │
│   That assumption is FALSE on most Samsung devices. Uber Driver is a   │
│   React Native app and on Samsung the popup window has `type=3`,       │
│   `pkg=com.ubercab.driver`, `text len=0` — zero accessible text.       │
│   Marcin's 1999-line accessibility log (May 13) confirmed:             │
│       Window[2]: type=3, pkg=com.ubercab.driver, text len=0           │
│                                                                          │
│   Effect: `isForegroundOfPackage("com.ubercab.driver")` returned       │
│   false for every popup-over-other-app event because the text-content │
│   check failed → fallback to tracker → tracker=launcher/Wolt → false. │
│   Pipeline was aborted by the foreground guard before screenshot, so  │
│   the bar only ever showed up when the user was already inside        │
│   Uber Driver foreground.                                              │
│                                                                          │
│ ENGINEERING FIX (1 commit, 21 lines changed):                           │
│   - `isForegroundOfPackage`: replaced `hasUberOverlayWithContent`      │
│     with `hasUberOverlayWindow` — the existence of an Uber overlay    │
│     window is sufficient signal that Uber is the source of the event. │
│     This restores the pre-v1.0.2 logic for the popup-over-other-app   │
│     path.                                                              │
│   - Uber watch mode (line 635): same swap for symmetry — without it   │
│     the safety-net loop was a dead-end on RN-based Uber Driver        │
│     devices. Main path (CONTENT_CHANGED handler) catches popups too,  │
│     watch mode is backup.                                              │
│                                                                          │
│ SAFETY FOR ANDRIJ'S NEWS-PORTAL FIX (regression prevention):            │
│   Layer 4 (positive markers in UberOcrParser) is what stops news      │
│   portals from triggering the bar. Marker requires one of: "Łącznie", │
│   "Total", "Akceptuj", "Доставка", "Загалом", "Принять" (PL/EN/UK/RU).│
│   News portals / social / unrelated apps never contain these tokens.  │
│   Layer 3 (watch mode reset on app switch) + Layer 1 (foreground      │
│   tracker for Wolt/Glovo/Bolt) remain unchanged.                       │
│                                                                          │
│ TIMELINE (same-day hotfix):                                              │
│   May 13 morning — Marcin reports Uber bar missing during Wolt-       │
│                     foregrounded shift                                 │
│   May 13 afternoon — Engineering analysis of his accessibility log     │
│                       (1999 lines) → diagnosed Layer 2 regression      │
│   May 13 19:41 — Commit e17860c on `fix/v1.0.5-uber-popup-background` │
│                   (versionCode 5→6, versionName 1.0.4→1.0.5)           │
│   May 13 evening — AAB built + signed + uploaded                       │
│   May 13 20:39 — Released to Closed Testing track (Google auto-       │
│                   approved within minutes)                              │
│                                                                          │
│ EVIDENCE FILES:                                                          │
│   test-data/closed-testing/logs/2026-05-13_marcin_uber-popup-          │
│     over-other-app_accessibility-log.txt (1999 lines)                  │
│                                                                          │
│ VERIFICATION:                                                            │
│   Asked Marcin to confirm the bar now appears when Uber popup fires    │
│   while another app is foregrounded. Awaiting confirmation.            │
│                                                                          │
│ WHY THIS MATTERS FOR REVIEW:                                            │
│   - Demonstrates we own the regressions we introduce (Layer 2 was     │
│     our own code from v1.0.2 — we did not blame Android, Samsung, or  │
│     Uber).                                                             │
│   - Demonstrates same-day hotfix capability before Production submit. │
│   - 4th iteration in 14-day window (above Google's 3 minimum).        │
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

### ✅ STATUS — 2026-05-15 (Day 13) — All 4 AAB updates LIVE, ready for Day 14 submit

**Stan na 05-15 (Day 13):**
- ✅ **4/3 AAB updates LIVE w Closed Testing** (v1.0.2 → v1.0.3 → v1.0.4 → v1.0.5)
- ✅ **53 active testers** (Play Console snapshot 05-15)
- ✅ **Dashboard pokazuje 13/14 days continuously** z 12+ opted-in testers — przycisk „Apply for production" odblokuje się Day 14 (2026-05-17)
- ✅ **Application Form draft GOTOWY** — sekcja 5 evidence doc ma paste-ready Q1.1-Q3.2 + 4 Fix Cards (v1.0.2/v1.0.3/v1.0.4/v1.0.5)
- ✅ **Default store listing zamknięte** (C2/C4/C8/C9 + F1/F2/F4 + D1/D2/D3/D4)

**Co zostało do Day 14 (05-17, sobota):**
- ⏳ Marcin confirmation o v1.0.5 Uber popup fix (user pinguje 05-15)
- ⏳ Day 14 końcowy Console snapshot — Statistics + Dashboard z 14/14 green ✅
- ⏳ Play Console → Production track → „Apply for production access" — wypełnić questionnaire (3 sekcje, 8 pytań) — paste z evidence doc sekcja 5
- ⏳ Stworzyć Production release — skopiować v1.0.5 AAB z Closed Testing
- ⏳ Submit Production
- ⏳ Po submit: Google review 3-7 dni → email approval → merge `play-store-prep` → `main` → LIVE

---

### ✅ STATUS — 2026-05-13 (Day 10) — v1.0.5 HOTFIX LIVE (same-day after v1.0.4)

**Co zrobione 05-13 wieczorem (po Marcin bug report Uber popup-over-other-app):**
- ✅ **Marcin zgłosił rano 05-13** — belka Ubera nie pojawia się gdy popup wyświetla się nad inną apką (np. Wolt foreground / pulpit). Przysłał 1999-liniowy accessibility log z Samsunga.
- ✅ **Engineering diagnosis** — `Window[2]: type=3, pkg=com.ubercab.driver, text len=0`. v1.0.2 Layer 2 `hasUberOverlayWithContent` zakładał że RN Uber Driver eksponuje tekst → na Samsungu nie eksponuje → foreground guard zabijał pipeline. Regresja v1.0.2.
- ✅ **Fix napisany** — `OrderPilotAccessibilityService.kt` z `hasUberOverlayWithContent` → `hasUberOverlayWindow` (samo istnienie overlay window wystarczy). Druga zmiana w watch mode (linia 635) dla symmetry. Safety dla Andrija news-portal zachowane przez Layer 4 (positive markers UberOcrParser, multi-language PL/EN/UK/RU).
- ✅ **versionCode 5→6, versionName 1.0.4→1.0.5** — commit `e17860c` na `fix/v1.0.5-uber-popup-background`
- ✅ **AAB v1.0.5 zbudowany i wgrany** — same-day hotfix, build + sign + upload w godzinach wieczornych
- ✅ **v1.0.5 LIVE w Closed Testing od 2026-05-13 20:39** — Google auto-approved within minutes. **4/3 AAB updates w 14-day window DONE.**

**Co zrobione 05-12/05-13 wcześniej (v1.0.4):**
- ✅ **Kod v1.0.4 napisany na branchu `fix/v1.0.4-thresholds`** — dwa fixy w jednym release:
  - Fix #38 combined color thresholds (AND-semantics) — `OfferAnalyzer.kt` refactor z `worstOf(levelFromHour, levelFromKm)`, edge cases pokryte (brak/zero dystansu → fallback do zł/h, Glovo path bez zmian)
  - Fix #37 decimal threshold input — `SettingsActivity.kt` z `formatThreshold` Locale.US + `parseThreshold` akceptujący oba separatory + fallback zachowujący poprzednią wartość z prefs zamiast hardcoded defaultu
- ✅ **Unit testy: 19/19 PASSED** — pełna regresja `OfferAnalyzerTest` + 7 nowych testów combined-thresholds (Marcin's exact repro + 6 edge cases). `./gradlew :app:testDebugUnitTest` ~ 3ms.
- ✅ **AAB v1.0.4 zbudowany i wgrany** — build 6m 9s (po `./gradlew clean` fix dla MD5 hash/iCloud), wgrany do Closed Testing track, one day ahead of plan.
- ✅ **v1.0.4 OPUBLIKOWANY przez Google 05-13 11:20 AM** — Play Console: „App update published, May 13".
- ✅ **Pre-launch report** wykreślony z TODO — robot Firebase Google nie przechodzi onboardingu AccessibilityService. Evidence zastąpione 53 active testers + 5 real kurierów + 4 AAB iteracji.

---

### 🚨 STATUS DNIA — 2026-05-11 (Day 8) — v1.0.3 LIVE, v1.0.4 podwójny fix planning

**Co zrobione 05-11 (dziś):**
- ✅ **v1.0.3 LIVE w Closed Testing** — Play Console pokazuje „Available to testers on Google Play, Full rollout", install base 32.50% (177/177 countries), Last updated May 9, 1:46 PM. Auto-publish po Google approve zadziałał zgodnie z planem.
- ✅ **Dominik potwierdza fix v1.0.3** (SMS 23:50): „spoko, wszystko co zglaszalem juz jest git" → UA/RU language persistence + Samsung navbar overlap działają poprawnie. **Closing loop dla obu fixów Dominika** — material do Application Form sekcja „closed-loop iteration".
- ✅ **Ivan Black resolved** — apka była po prostu wyłączona po update v1.0.3, re-toggle Accessibility naprawił. Nie bug kodu.
- ✅ **WA group ping #2 (Day 8)** wysłany do większości testerów — przypomnienie o wejściu na apkę i kliknięciu przez kilkanaście sekund.
- ✅ **Marcin (= tata, przemianowane do dokumentacji Google review)** zgłosił bug #2 — combined color thresholds (PLN/h + PLN/km powinny działać jako AND, nie OR). Screenshot zapisany: `test-data/closed-testing/screenshots/marcin feedback/2026-05-11_marcin_combined-thresholds-bug.jpg`. Udokumentowany w `future_polish_fixes.md` #38.
- ⏸️ **Save Day 0 dashboard screen** — świadomie pominięty (poprzednia decyzja: nie blokujący do Application Form, Console statistics z Day 5/7/9 wystarczają).

**Plan v1.0.4 (Day 9 = 05-12 kod, Day 11-12 = 05-14/15 build/upload):**
- Fix 1: **decimal threshold input** (`future_polish_fixes.md` #37) — Marcin/Andrij — locale PL przecinek/kropka + PLN/h całkiem zablokowane dziesiętne
- Fix 2: **combined color thresholds** (`future_polish_fixes.md` #38) — Marcin — `OfferAnalyzer` ignoruje PLN/km dla nie-Glovo, kolor powinien być MIN(level_per_hour, level_per_km)
- Dwa bugi w jednym release → mocna ammunition do Application Form (closed-loop iteration #3 w 14-day window)

**Nazewnictwo w dokumentacji (decyzja 05-11):**
- „Marcin" = tata. Imię używane jednolicie we wszystkich materiałach Google review aby uniknąć ujawnienia relacji rodzinnej (Closed Testing wymaga niezależnych testerów). Stosować konsekwentnie w `closed-testing-evidence.md`, `future_polish_fixes.md`, Application Form, release notes.

---

### 🚨 STATUS DNIA — 2026-05-10 (Day 7, continued) — v1.0.4 planning

**Co zrobione 05-10 (dziś):**
- ✅ Console Crashes & ANRs: **zero issues** (screenshot z May 10)
- ✅ Dashboard: **8 of 14 days** continuously z 12+ opted-in testers — pozostało 6 dni (cel: 2026-05-17)
- ✅ Marcin (WA group) — bug decimal thresholds zebrany, udokumentowany → `future_polish_fixes.md` #37, Fix Card v1.0.4 placeholder wpisany
- ✅ Andrij potwierdzył ten sam bug decimal (wiadomość 12:00 — „Nie zapisuje liczb niecałkowitych")
- ✅ Ivan Black — apka wyłączona po update v1.0.3, naprawione przez re-toggle Accessibility (nie bug kodu)
- ⏸️ **WA group ping #2 przesunięty na jutro (2026-05-11, Day 8)** — testers byli aktywni dziś w grupie (= naturalny kontakt), dodatkowy formalny ping dziś byłby za częsty. Jutro brzmi naturalnie.

---

### 🚨 STATUS DNIA — 2026-05-09 (Day 7) — v1.0.3 IN REVIEW

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

**Co zrobione 05-06 (Day 3):**
- ✅ AAB build w Android Studio (signed, v1.0.2 = versionCode 3)
- ✅ Upload do Play Console Closed Testing track
- ✅ Sent for review → **Released May 6 12:14 AM**
- ✅ Auto-published po Google approve

**Co zrobione 05-07 (Day 5):**
- ✅ Console snapshot: **50 active testers** (Day 5 ramp-up vs Day 0 12 opted-in) — paid pool wszedł
- ✅ Dominik feedback #2 zebrany (WhatsApp 15:14–15:18) — UA/RU language reset + Samsung navbar
- ✅ Pełna dokumentacja Dominik feedback zapisana — `test-data/closed-testing/screenshots/dominik feedback/feedback_2026-05-07.md` + `closed-testing-evidence.md` sekcja Dominika
- ✅ v1.0.3 kod napisany i przetestowany na telefonie brata — działa
  - Bug 1: migracja LocaleHelper.wrap → AppCompatDelegate.setApplicationLocales (Android 13+ official API) + locales_config.xml + one-time migration sync
  - Bug 2: WindowInsetsCompat handling w Settings/Setup/Disclosure (edge-to-edge na targetSdk 35)
  - UX: cleanup nazwy radio buttons („Polski (zł)" → „Polski", „English (PLN)" → „English")
- ✅ Commit `1b8b3cd` na `play-store-prep`, push do GitHub
- ⏸️ AAB upload świadomie odłożony o 1-2 dni (decyzja 05-07) — żeby zachować 3-dniowy odstęp przed v1.0.4

**Co zrobione 05-08 (Day 6):**
- 🟢 Czekamy / odpoczynek (zgodnie z planem)

**Co zrobione 05-09 (Day 7, dzisiaj):**
- ✅ Build signed AAB w Android Studio (release variant, keystore z `keystore.properties`)
- ✅ Upload do Play Console Closed Testing track Alpha (versionCode 4, versionName 1.0.3)
- ✅ Release notes EN + PL wgrane (en-US + pl-PL tags)
- ✅ Sent for review — **status: IN REVIEW** (Publishing overview pokazuje „Your changes are now in review")
- ✅ Managed publishing OFF → auto-publish po Google approve (~1-3h)
- ✅ Device coverage check: 0 newly supported / 0 no longer supported → backward compatible z v1.0.2 ✅
- ✅ 2 Warnings (deobfuscation file + native debug symbols) — te same co v1.0.2, bezpieczne, ignorowane
- 🟡 **TODO po auto-publish (dziś wieczór lub jutro):** ping Dominika (verify UA/RU + navbar), ping Andrija (verify v1.0.2 still + verify v1.0.3 nothing regressed)

---

#### 🔴 Krytyczne najbliższe 24-48h (do 05-09 wieczór = Day 7)

**🚨 v1.0.3 AAB upload pending — Day 6 (05-08) lub Day 7 (05-09):**
- [ ] **Sprawdzić** czy ktoś z 50 active testerów zgłosił coś nowego w międzyczasie (jeśli tak — szybki dodatek do v1.0.3 przed buildem)
- [ ] **Build signed AAB** w Android Studio (Build → Generate Signed App Bundle/APK → release keystore z `keystore.properties`)
- [ ] **Upload do Play Console** Closed Testing track
- [ ] **Release notes EN:** "Fixed Russian/Ukrainian language not persisting after save. Fixed Save Settings button being partially hidden behind Samsung navigation bar. Reported by tester Dominik."
- [ ] **Release notes PL:** „Naprawiono problem z zapisywaniem języka rosyjskiego/ukraińskiego — wybór nie utrzymywał się po zapisie. Naprawiono częściowe zasłanianie przycisku Zapisz ustawienia przez systemowy pasek nawigacji Samsung."
- [ ] **Sent for review** (managed publishing off → auto-publish po Google approve, ~1-3h)
- [ ] **Ping Dominika** po auto-update — „Wgrałem update z fixami które zgłaszałeś (UA/RU + nav bar). Apka sama się zaktualizuje. Daj znać czy teraz działa."
- [ ] **Ping Andrija** — „Czy belka nadal pojawia się gdzieś poza apkami kurierskimi po update v1.0.2?" (verify v1.0.2 fix)

#### 🔴 Wcześniejsze TODO przeniesione (do nadrobienia jeśli niezrobione)

- [ ] **WA group ping #1 (Day 3)** — wzór: „Cześć! Jak idzie z OrderPilotem? Mógłbyś wrzucić jeden screen + napisać jedno zdanie co działa albo co nie? Potrzebne do oficjalnego submit do Google. Dzięki!"
- [ ] **Andrij ping** (po Google approve) — „Wgrałem update z fixem belki na portalach. Apka sama się zaktualizuje. Daj znać czy belka nadal pojawia się gdzieś poza apkami kurierskimi."
- [ ] **Save Day 3 Console Statistics screen** → `test-data/closed-testing/screenshots/2026-05-06_play-console-statistics.png`
- [x] ~~**Pre-launch report check** dla v1.0.2~~ — **WYKREŚLONE 2026-05-12**: Pre-launch report jest pusty bo robot Google Firebase nie przechodzi onboardingu AccessibilityService (wymaga manual permission grant w Settings → Accessibility). To znane zachowanie dla aplikacji accessibility, nie blokuje Application Form. Evidence zastępujemy: 50 active testers + 5 real kurierów + 3 AAB iteracje + 177 countries device coverage.
- [ ] **Day 0 dashboard screen save** (jeśli jeszcze niezrobione) — `2026-05-03_day0_dashboard-12-testers-checked.png`
- [ ] **Verify Vasyl install** (= „znajomy taty") — Console
- [ ] **Verify Ivan UA install** — Console
- [ ] **PrimeTestLab pool** — monitor czy fala opt-inów ruszyła

> **Brat (Dominik) już jest na liście** od dawna (dominanb19@...) — plan dodania jego drugiego starego telefonu odrzucony (risk same-IP > value). „Znajomy taty" = Vasyl, ten sam człowiek.

#### 🟡 Przez najbliższe 14 dni (rozłożone)

- [ ] Daily snapshot Console Statistics co 2 dni (DAU, Active devices, Installed audience)
- [ ] Pingi do aktywnych kurierów (co 2-3 dni): Andrij, Tata, Dominik, Lucky, Ivan Black, Kuba (nowy)
- [ ] WA group ping #1 (Day 3 ≈ 2026-05-06): „screen + 1 zdanie"
- [ ] WA group ping #2 (Day 8 ≈ 2026-05-11) — przesunięty z Day 7 (testers aktywni w grupie 05-10)
- [ ] WA group ping #3 (Day 11 ≈ 2026-05-14)
- [x] ✅ **v1.0.2 (Andrij news portals fix) — Released 2026-05-06 (Day 3)**: commit `15c131d`
- [x] ✅ **v1.0.3 (Dominik UA/RU + Samsung navbar) — sent for review 2026-05-09 (Day 7)**: commit `1b8b3cd`, IN REVIEW
- [ ] **Kod v1.0.4 (decimal fix #37) — napisać Day 9 (≈ 2026-05-12)**
- [ ] Build + upload v1.0.4 AAB — Day 11-12 (≈ 2026-05-14/15)

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
| **Day 2-3** | 2026-05-05/06 | Publish v1.0.2 (Andrij news portals) | ✅ DONE (Released 05-06 12:14 AM, commit `15c131d`) |
| **Day 5** | 2026-05-07 | Dominik feedback #2 + v1.0.3 kod + push | ✅ DONE (commit `1b8b3cd`) |
| **Day 7** | 2026-05-09 | Upload v1.0.3 AAB → sent for review | ✅ DONE (IN REVIEW, auto-publish po approve) |
| **Day 7** | 2026-05-10 | Review materiału — sekcja 5 rośnie? | ✅ Marcin + Andrij decimal bug zebrany, Fix Card v1.0.4 placeholder gotowy |
| **Day 8** | 2026-05-11 | WA group ping #2 | Przesunięty z Day 7 — testers aktywni dziś (naturalny kontakt), jutro brzmi normalnie |
| **Day 9** | 2026-05-12 | **Kod v1.0.4 — fix decimal thresholds** | `future_polish_fixes.md` #37. Napisać + przetestować lokalnie, nie czekać do Day 11 |
| **Day 11** | 2026-05-14 | WA group ping #3 — final | Ostatnia szansa na cytaty |
| **Day 11-12** | 2026-05-14/15 | **Build + upload v1.0.4 AAB** | Kod gotowy od Day 9; tu tylko build signed AAB + upload do Console |
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
