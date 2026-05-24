# OrderPilot — Launch Promotion Plan

> **Status:** v1.0.5 LIVE w Google Play od 2026-05-23 (177 krajów). FB page `OrderPilot` utworzona ale 0 followers / 0 postów / 0 organic users. Apka jest niewidoczna.
>
> **Cel tej fazy:** pierwsze 50-500 organic installów + pierwsze recenzje, w 2-4 tygodnie.
>
> **Single source of truth dla całej promocji.** Wszystkie drafty, listy grup, szablony — w tym katalogu (`docs/promo/`). Status aktualizować po każdym kroku.

---

## Kontekst (nie zmieniać — pisane raz na początku)

- **Apka:** OrderPilot v1.0.5, package `com.orderpilot.app`
- **Link:** https://play.google.com/store/apps/details?id=com.orderpilot.app
- **Demo wideo:** https://www.youtube.com/watch?v=riSLy3qiySA
- **Cena:** $0 (zero monetyzacji w v1)
- **Target:** kurierzy Uber/Wolt/Glovo/Bolt Food w PL + UA kurierzy w PL (sekundarnie)
- **Unique value:** real-time PLN/h overlay zielony/żółty/czerwony, zero network, zero tracking, działa nad apkami kurierskimi przez Accessibility (nie API → zero ryzyka bana)

### Real tester quotes (potwierdzone, można cytować)
- **Marcin (PL, Samsung):** „It works ok now, thanks 😂" (2026-05-16 WhatsApp, po fix v1.0.4 + v1.0.5)
- **Dominik (PL, Samsung):** „spoko, wszystko co zglaszalem juz jest git" (2026-05-11 SMS, po fix v1.0.3)
- **Andrij (UA, multi-platform PL kurier):** 5h57m online / 9 zleceń jeden dzień (04-29 statystyki) + zgłosił bug news portals → fix v1.0.2

### Anti-patterns (NIE rób)
- ❌ **Nie spamuj** grup tym samym postem 5 razy → ban + szkoda dla brandu
- ❌ **Nie cytuj PrimeTestLab pool** jako social proof (~40 farmowych installów, fałszywy sygnał)
- ❌ **Nie używaj agresywnego CTA** („MUSISZ pobrać!", „ZAROBISZ WIĘCEJ" itp.) — kurierzy są wyczuleni na scam
- ❌ **Nie wgrywaj v1.0.6** podczas pierwszych 2 tygodni jeśli nie ma critical bug — Production track jest świeży, każdy review cycle to ryzyko
- ❌ **Nie pisz po angielsku do PL grup** — natywny PL action, even jeśli grupa jest mixed UA/PL

---

## Plan (kolejność wykonania)

### Etap 1 — Materiały gotowe do publikacji ✅ DONE 2026-05-23
- [x] [3 drafty postów na profil FB OrderPilot](fb_profile_posts.md) — launch announcement, tester quote, demo+CTA
- [x] [Lista 8-12 grup FB kurierów PL/UA + szablon postu do grup](fb_groups_outreach.md) — subtelne, anty-spam

### Etap 1.5 — BLOCKING ISSUES do rozwiązania PRZED massową publikacją (priorytet)

> **Te 2 rzeczy zmniejszają ROI promocji jeśli nie zostaną naprawione zanim ludzie zaczną klikać linki z postów.** Jeden post na profilu OK (Post #1), ale outreach do grup ma sens dopiero po fixie.

#### Issue #1 — Play Store SEO (apka nieznajdowalna w Sklepie) 🔴 BLOCKING

> ⭐ **Gotowy do wykonania:** zobacz [`play_store_seo_fix.md`](play_store_seo_fix.md) — full SEO copy (Short + Full description) + prompt dla Claude w panelu Chrome do automatycznej edycji w Console.

**Problem (zgłoszone 2026-05-23 przez Krzysztofa):**
Wpisanie „**order pilot**" (z spacją) w Google Play **nie wyświetla apki**. Tylko „orderpilot" (razem) działa. Większość użytkowników intuicyjnie wpisze ze spacją (jak każdą dwuwyrazową nazwę). Strata 50-80% organic search traffic.

**Root cause (hipoteza, do weryfikacji):**
- App name w Console = `OrderPilot` (jedno słowo, bez spacji)
- Google Play search tokenizer NIE rozdziela CamelCase automatycznie na 2 tokeny
- Short description (75/80 chars) + Full description (1564/4000) prawdopodobnie nie mają frazy „Order Pilot" ani „order pilot" jako konkretnej frazy w pierwszych 250 znakach (gdzie keyword weight najwyższy)

**Fix (kolejność, do wykonania):**
1. **Sprawdź obecny stan:** Console → Default store listing → przeczytaj App name, Short description, pierwsze 250 znaków Full description. Czy gdzieś jest fraza „Order Pilot" (z spacją)?
2. **Zmień App name** (max 30 chars) — opcje:
   - „OrderPilot - Kurier zł/h" (24 chars, dorzuca keyword „Kurier")
   - „OrderPilot · Order Pilot" (24 chars, eksplicit dla SEO obu form) — **rekomendowane**
   - „OrderPilot — kurier app" (23 chars)
3. **Short description** (max 80 chars) — wzbogać o „order pilot" jako frazę + „kurier":
   - Stary: (nieznany, sprawdź)
   - Propozycja: „Order Pilot dla kurierów Uber, Wolt, Glovo, Bolt — zł/h każdego zlecenia" (75 chars)
4. **Full description** — w pierwszych 250 znakach (above-the-fold w Sklepie) wpleć „Order Pilot" 2-3x jako naturalną frazę. Plus zmień strukturę żeby pierwsze zdanie zawierało: „Order Pilot", „kurier", „Uber Wolt Glovo Bolt", „PLN/h", „opłacalność zleceń".
5. **Submit do Google review** — Default store listing update wymaga review ~24-48h (krótki review, nie pełen AAB). Apka pozostaje LIVE w międzyczasie.
6. **Po 48h** — wpisz „order pilot" w Google Play z 2-3 różnych telefonów (najlepiej różne konta Google) — apka powinna być w top 5.

**Side effect:** Nie zmieniamy package name (`com.orderpilot.app`) ani versionCode (zostaje 6). To tylko update store listing.

**Czas:** 30-60 min (research + edycja + submit). Wynik widoczny po 24-48h.

**Status:** 🔴 TODO — **przed Etapem 3 (outreach do grup)**. Postu #1 na profil można wrzucić wcześniej (kierowane traffic z linka, nie z search).

#### Issue #2 — Grafiki FB w niskiej jakości + brak showcase belki 🟡 BLOCKING (visual quality)

**Problem (zgłoszone 2026-05-23 przez Krzysztofa):**
Aktualna cover graphic na FB profilu OrderPilot ma:
- Niską rozdzielczość / pixelizację (nie wygląda profesjonalnie)
- Pokazuje **abstrakcyjny** screenshot belki (42 zł/h / 14 zł/h) na czarnym tle, nie **realny use case** (belka NAD aplikacją kuriera Uber/Wolt/Glovo)

**Wpływ na konwersję:** kurier przychodzi z grupy, widzi „beznadziejną" grafikę → zniechęcenie zanim przeczyta opis → brak install.

**Rekomendacja (do wykonania):**

A. **Nowa cover graphic FB** (851×315 px desktop, 640×360 mobile — używaj 1640×924 px żeby działało na wszystkich urządzeniach):
   - **Lewa strona:** logo OrderPilot + tagline „Analiza opłacalności zleceń · Uber · Wolt · Glovo · Bolt"
   - **Prawa strona:** **realny screenshot** telefonu pokazujący belkę OrderPilot NAD aplikacją kuriera (np. Uber Driver z popupem oferty + zielona belka „42 zł/h · 8.20 zł · 2.3 km · 12 min" na górze). Mockup → pokazuje use case w 1 sekundzie.
   - **Jakość:** PNG 1640×924 (nie JPG, nie kompresować), eksport z Figma/Canva/Photoshop

B. **Nowe profile picture** (170×170 px, ale używaj 500×500 — FB resize):
   - Logo OrderPilot (Arrow-Up Reticle, navy #0D1B2A + orange #F07830) na **białym** lub **navy** tle (czytelność w small format)
   - Aktualnie jest na czarnym, sprawdzić czy navy nie wygląda lepiej

C. **Dodatkowe screenshoty do postów** (1080×1080 dla Insta-compatible, 1200×630 dla FB):
   - 3-4 screenshoty z apki w akcji (różne platformy: Uber zielony, Wolt żółty, Glovo czerwony, Bolt)
   - Można złożyć w mockup phone frame (Figma free templates: „iPhone mockup", „Android mockup")

**Czym to zrobić (jeśli sam):**
- **Canva** (free) — szablon „Facebook Cover" + drag&drop screenshot + tekst. 30-60 min.
- **Figma** (free) — więcej kontroli, więcej krzywej uczenia. 1-2h.
- **AI image generation** — NIE rekomendowane dla cover (wygląda generic, kurierzy wyczują AI od razu). OK dla tła abstrakcyjnego.

**Czym to zrobić (jeśli outsourcing):**
- Fiverr „Facebook cover design" — $10-30, 24-48h
- Polski freelancer (znajomy z grupy „Designerzy PL" na FB) — $20-50

**Status:** 🟡 TODO — **przed Postem #1 jeśli chcesz najlepszy first impression**, albo wrzuć Post #1 z obecną grafiką (lepiej coś niż nic) i wymień cover w trakcie tygodnia gdy nowa będzie gotowa.

**Czas:** 1-2h DIY (Canva) lub 24-48h outsourcing.

---

### Etap 2 — Profil OrderPilot bootstrap (Day 1-3, ty)
- [ ] Wrzucić **Post #1 (launch announcement)** na profil OrderPilot — pin do góry
- [ ] Wrzucić **Post #2 (tester quote — Marcin)** 24h po Post #1
- [ ] Wrzucić **Post #3 (demo wideo + jak działa)** 48h po Post #1
- [ ] Zaprosić rodzinę/znajomych do polubienia strony (Tata, Dominik, Krzysztof, Andrij jeśli ma FB) — pierwsze 5-10 followers żeby strona nie wyglądała na pustą

### Etap 3 — Outreach do grup FB kurierów (Day 3-7, ty)
- [ ] Otworzyć [`fb_groups_outreach.md`](fb_groups_outreach.md), skopiować listę 8-12 grup
- [ ] Dołączyć do każdej (zaakceptują w ciągu 24-48h, niektóre wymagają answer pytania weryfikacyjnego)
- [ ] **Rozłożyć posty w czasie** — 1-2 grupy dziennie przez 5-7 dni, NIE wszystkie na raz (Facebook spam detector)
- [ ] Dla każdej grupy użyć **szablonu A lub B** z [`fb_groups_outreach.md`](fb_groups_outreach.md) (różnić wording żeby algorytm nie traktował jako copy-paste spam)
- [ ] Monitor komentarze, odpowiadaj w 12-24h (engagement = boost algorytmu)

### Etap 4 — Monitoring + iteracja (Day 7+, ty)
- [ ] Codziennie Google Play Console → Statistics (instalacje per dzień)
- [ ] Codziennie Console → Crashes & ANRs (baseline zero)
- [ ] Codziennie Console → Ratings & Reviews (pierwsza recenzja → odpowiedź w 24h)
- [ ] Co 3-4 dni FB profil OrderPilot → reach/likes postów, dorzucić odpowiedzi
- [ ] **Jeśli pierwsze 50 installów w 7 dni** → kolejna fala (Reddit r/UberDrivers w EN, TikTok demo)
- [ ] **Jeśli <20 installów w 7 dni** → re-analiza, postować w innych grupach lub zmienić messaging

### Etap 5 — Rozszerzenie (Day 14+, opcjonalnie)
- [ ] Reddit r/UberDrivers, r/uberEats, r/doordash_drivers — adaptacja Post #3 na EN
- [ ] TikTok demo wideo (30-60s, screen recording z belką)
- [ ] Instagram Reels (skrót TikTok)
- [ ] Outreach do PL/UA YouTuberów kurierskich (review)

---

## Status updates (uzupełniaj na bieżąco)

| Data | Co zrobione | Wynik |
|------|-------------|-------|
| 2026-05-23 | Plan + drafty + lista grup gotowe | docs/promo/ utworzone |
| 2026-05-23 | Dodano Etap 1.5 (BLOCKING ISSUES): Play Store SEO + Grafiki FB | Issues #1 + #2 zapisane, do wykonania przed Etapem 3 |
|  |  |  |

---

## Linki

- [Drafty postów na profil OrderPilot](fb_profile_posts.md)
- [Outreach do grup FB + szablony](fb_groups_outreach.md)
- [Google Play listing](https://play.google.com/store/apps/details?id=com.orderpilot.app)
- [Demo wideo YouTube](https://www.youtube.com/watch?v=riSLy3qiySA)
- [GitHub Release v1.0.5](https://github.com/kris20032/OrderPilot-App/releases/tag/v1.0.5)
- [Privacy Policy](https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html)
