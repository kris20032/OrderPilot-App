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
|  |  |  |

---

## Linki

- [Drafty postów na profil OrderPilot](fb_profile_posts.md)
- [Outreach do grup FB + szablony](fb_groups_outreach.md)
- [Google Play listing](https://play.google.com/store/apps/details?id=com.orderpilot.app)
- [Demo wideo YouTube](https://www.youtube.com/watch?v=riSLy3qiySA)
- [GitHub Release v1.0.5](https://github.com/kris20032/OrderPilot-App/releases/tag/v1.0.5)
- [Privacy Policy](https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html)
