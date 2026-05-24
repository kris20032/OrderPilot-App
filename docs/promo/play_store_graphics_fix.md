# Play Store Graphics Fix — Issue #2

> **Cel:** Naprawić visualną stronę listing'u w Sklepie Play. Aktualnie: **brak feature graphic** (Google podstawia placeholder), 5 screenshotów w **złej kolejności** (najsłabszy pierwszy).
>
> **Status:** ⏳ TODO 2026-05-23
>
> **Czas:** Część A (reorder screenshotów) 5 min + Część B (nowy feature graphic) 1-2h DIY / 24-48h outsourcing.

---

## Diagnostyka — co jest na Sklepie (stan 2026-05-23 23:50)

Pobrane przez `curl` z `play.google.com/store/apps/details?id=com.orderpilot.app&hl=pl&gl=PL`:

### 🔴 Feature graphic — **NIE JEST USTAWIONY**

URL który Google wystawia (`play-lh.googleusercontent.com/KUnvVSJ...`) zwraca **WebP 48×48 z szarą ikoną chmury** = placeholder „brak obrazka". To znaczy że pole „Feature graphic" w Console jest puste albo upload się nie zapisał, mimo że PROGRESS.md odnotował to jako DONE 05-12. **Trzeba zweryfikować w Console i uploadować.**

Wpływ:
- Na desktopie Sklepu — Google ukrywa banner, używa trailera YT zamiast
- Na mobile Sklepie — pokazuje placeholder/blank space nad ikoną apki
- W „Google Play Editorial" featured sections — apka **nie kwalifikuje się** bez feature graphic
- Reklamy „Promoted" na Google Search/YouTube — wymagają feature graphic

### 🟡 Screenshoty — 5 sztuk, ale złe ułożenie

| # | Co pokazuje | URL hash | Ocena | Rekomendacja |
|---|-------------|----------|-------|--------------|
| 1 | Ekran główny: „Active · Stop · Zapisz logi" | `LZIThLaw` | 🔴 słaby — wygląda biurokratycznie | **przenieść na koniec albo usunąć** |
| 2 | Zielona belka nad ofertą Uber „13,7 zł/km · 39 zł" | `SacA-xhN` | 🟢 **świetny** — core value w 1s | **pozycja 2** (po reorderze) |
| 3 | Settings → toggles + przezroczystość belki | `Sv_WR_HM` | 🟡 przeciętny | pozycja 4 |
| 4 | Mapa + 2 belki UBER 43 zł/h + BOLT 25 zł/h | `UPShTM5s` | 🟢 **rewelacyjny** — multi-platform use case | **pozycja 1** (hero shot) |
| 5 | Notyfikacja systemowa „Monitoring aktywny" | `dk5aUQsL` | 🟡 przeciętny — pokazuje że żyje w tle | pozycja 5 |

### 🟢 Ikona apki — OK

Arrow-Up Reticle (orange #F07830 + navy #0D1B2A), 512×512, czytelna w small format. **Nie ruszać.**

### 🟢 Trailer YouTube — OK

`youtube.com/watch?v=riSLy3qiySA` poprawnie zlinkowany.

---

## Część A — Reorder screenshotów (5 min, ⚡ szybka wygrana)

Pierwsze 2 screenshoty są widoczne **bez scrolla** w Sklepie. Aktualnie pierwszy widoczny = screen 1 (ekran „Active · Stop") = nie pokazuje wartości. Trzeba na pierwszej pozycji mieć najmocniejszy.

**Nowa kolejność:**
1. **Screen 4** (mapa + 2 belki UBER/BOLT) — hero shot, pokazuje multi-platform w 1 sek
2. **Screen 2** (zielona belka nad ofertą Uber) — core value
3. **Screen 3** (settings) — pokazuje customizację
4. **Screen 5** (notyfikacja) — pokazuje że działa w tle
5. **Screen 1** (ekran główny) — żółty/słaby, na koniec albo wywal

### 🤖 Prompt dla Claude w panelu Chrome (Część A)

```
Cześć, potrzebuję pomocy ze zmianą kolejności screenshotów w Google Play Console.

KROK 1:
Otwórz https://play.google.com/console
Wybierz aplikację OrderPilot (com.orderpilot.app).

KROK 2:
W lewym menu: Grow → Store presence → Main store listing
Przewiń do sekcji "Phone screenshots" (Zrzuty ekranu telefonu).

KROK 3:
Aktualnie powinno tam być 5 screenshotów. Opisz mi w jakiej są
kolejności (numer 1, 2, 3, 4, 5 od lewej do prawej) — opisz co
każdy pokazuje (np. "1 = ekran główny z przyciskiem Stop",
"2 = mapa z dwiema belkami", itd.).

KROK 4 (poczekaj na moje potwierdzenie zanim zaczniesz):
Po opisaniu kolejności, czekam na Krzysztofa żeby potwierdził którą
docelową kolejność chcemy. Idealnie:
- Pozycja 1: mapa z 2 belkami UBER+BOLT (hero shot, pokazuje multi-platform)
- Pozycja 2: zielona belka nad ofertą Uber "13,7 zł/km · 39 zł" (core value)
- Pozycja 3: settings z toggles
- Pozycja 4: notyfikacja systemowa "Monitoring aktywny"
- Pozycja 5: ekran główny "Active · Stop · Zapisz logi"

KROK 5 (po potwierdzeniu Krzysztofa):
Przeciągnij screenshoty w nowy porządek (Console pozwala drag&drop
miniaturek). Sprawdź wizualnie czy układ się zgadza.

KROK 6:
Kliknij Save (Zapisz) na dole strony.

KROK 7:
Wróć do Publishing overview → "Send changes for review" (Wyślij
zmiany do sprawdzenia). Poczekaj na moje potwierdzenie zanim
naciśniesz Send.

UWAGA: NIE usuwaj żadnego screenshota, tylko zmieniaj kolejność.
Nie ruszaj feature graphic, ikony, opisu, video.
```

---

## Część B — Nowy feature graphic (1-2h DIY w Canva)

### Spec techniczny
- **Wymiary:** 1024 × 500 px (dokładnie, no rounding)
- **Format:** PNG lub JPG
- **Max rozmiar pliku:** 1 MB (Console limit)
- **Bez tekstu blisko krawędzi:** Google przycina ~20px z każdej strony w niektórych widokach — keep important content w środkowej części 980×460

### Layout — proposal

```
┌───────────────────────────────────────────────────────────────┐
│ [navy background #0D1B2A]                                     │
│                                                               │
│   [LOGO]            [PHONE MOCKUP                             │
│    Arrow-Up         ekran apki Uber Driver                    │
│    Reticle          z dwiema belkami OrderPilot:              │
│    orange           🟢 UBER 43 zł/h                           │
│    200×200          🔴 BOLT 25 zł/h                           │
│                     widoczna mapa, nawigacja                  │
│   OrderPilot        portrait, ~280×500 px]                    │
│   ────────────                                                │
│   PLN/h każdego                                               │
│   zlecenia kuriera                                            │
│                                                               │
│   Uber · Wolt · Glovo · Bolt   [orange #F07830]              │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

**Brand tokens:**
- Background navy: `#0D1B2A`
- Accent orange: `#F07830`
- Tekst biały: `#FFFFFF` (Title), `#E0E0E0` (subtitle)
- Font: sans-serif bold (np. Inter, Roboto, Montserrat)

**Hierarchia treści (max 5 słów na element):**
- Title: **OrderPilot** (bold, ~64pt)
- Subtitle: **Order Pilot** (regular, ~32pt) — eksplicit dla SEO, żeby fraza była też w grafice (Google OCR feature graphics)
- Tagline: **PLN/h każdego zlecenia kuriera** (regular, ~28pt)
- Platforms row: **Uber · Wolt · Glovo · Bolt** (medium, orange, ~24pt)

### Assets do reuse

1. **Logo apki** — masz lokalnie:
   - W repo: `OrderPilot/app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp` (i mniejsze rozdzielczości)
   - Z Sklepu: `https://play-lh.googleusercontent.com/VK2rN4CZqg0ApslMyMq5WHJHe7yrbQ_ywFyaYTlV-GKuTSga9Pt9AEVlsa0PmzvUe6-Uyh54MYV0bTHOZSeEiw=w512-h512`
2. **Phone mockup** — Canva ma za darmo („Search elements: phone mockup android")
3. **Screenshot do wstawienia w mockup** — najlepszy ten z mapy + 2 belek:
   - URL: `https://play-lh.googleusercontent.com/UPShTM5s0-8IM3hpgtF8fqpElocsaTpRMIcdeTX65PfumycSUvIKSZv4H-16jCY9MeHNHhkjRYIFj-eO-yF9DA=w1052-h592-rw`
   - Lub zrób fresh screenshot z apki w akcji na swoim telefonie

### Step-by-step Canva (DIY, ~1h)

1. Wejdź na **canva.com**, zaloguj się
2. Top right → **Create a design** → **Custom size** → **1024 × 500 px** → Create
3. **Background:** kliknij plain background tool → kolor → wpisz `#0D1B2A` → Apply
4. **Logo:**
   - Uploads → upload `ic_launcher.webp` z repo (lub `store_icon.png` z `/tmp/`)
   - Przeciągnij na canvas, skala do ~200×200
   - Pozycjonowanie: lewa krawędź ~80px, vertically center
5. **Title „OrderPilot":**
   - Text → Add a heading → wpisz „OrderPilot"
   - Font: Inter Bold / Montserrat Bold / Roboto Bold, size 64pt, kolor `#FFFFFF`
   - Pozycja: pod logo (lub obok), left-aligned z logo
6. **Subtitle „Order Pilot":**
   - Text → wpisz „Order Pilot"
   - Font: ten sam, regular, size 32pt, kolor `#E0E0E0`
   - Pozycja: pod „OrderPilot", same left alignment
7. **Tagline:**
   - Text → wpisz „PLN/h każdego zlecenia kuriera"
   - Font: regular, size 28pt, kolor `#FFFFFF`
   - Pozycja: pod „Order Pilot"
8. **Platforms row:**
   - Text → wpisz „Uber · Wolt · Glovo · Bolt"
   - Font: medium, size 24pt, kolor `#F07830` (orange)
   - Pozycja: pod tagline
9. **Phone mockup + screenshot:**
   - Elements → search „android phone mockup" → wybierz prosty czarny frame portrait
   - Przeciągnij na prawą stronę canvasa, skala do ~280×500
   - W Effects/Frame opcji wstaw screenshot z mapy+2 belkami (upload z Sklepu URL lub `/tmp/store_screen4.png`)
   - Wycentruj prawą stronę (right edge ~80px margin)
10. **Quality check:**
    - Zoom do 100% — czytelność tekstu OK?
    - Pierwszych 100px od lewej i prawej krawędzi — czy nie ma kluczowego contentu? (Google może to przyciąć)
    - Phone mockup nie zachodzi na text?
11. **Eksport:**
    - Share → Download → PNG → Quality: standard
    - Sprawdź rozmiar pliku — jeśli >1MB, zmień na JPG quality 85%

### Alternatywa — outsourcing Fiverr

Search „Google Play feature graphic design" → cena $10-30, turnaround 24-48h. W brief'ie podaj:
- Wymiary 1024×500 PNG
- Brand colors: navy `#0D1B2A`, orange `#F07830`
- Logo (załącz `ic_launcher.webp`)
- Screenshot do mockupa (załącz `store_screen4.png`)
- Tekst: „OrderPilot" + „Order Pilot" + „PLN/h każdego zlecenia kuriera" + „Uber · Wolt · Glovo · Bolt"
- Style: clean, modern, kurierski/professional, NIE corpo, NIE „startup hype"

### 🤖 Prompt dla Claude w panelu Chrome (Część B — gdy masz już PNG)

```
Cześć, potrzebuję wgrać nowy feature graphic do Google Play Console.

KROK 1:
Otwórz https://play.google.com/console
Wybierz aplikację OrderPilot (com.orderpilot.app).

KROK 2:
W lewym menu: Grow → Store presence → Main store listing
Przewiń do sekcji "Feature graphic" (Grafika wyróżniająca).

KROK 3:
Powiedz mi co jest tam teraz:
- Czy jest pusto (placeholder Google z chmurką)?
- Czy jest jakaś grafika 1024×500?
- Jeśli jest — opisz co przedstawia.

KROK 4 (poczekaj na potwierdzenie Krzysztofa):
Jeśli jest pusto LUB Krzysztof potwierdza wymianę:
- Kliknij upload area
- Wybierz plik [Krzysztof powie ścieżkę, np. /Users/krzysztof/Desktop/feature_graphic.png]
- Poczekaj na upload + preview
- Zweryfikuj wymiary 1024×500
- Sprawdź czy preview wygląda OK

KROK 5:
Kliknij Save.

KROK 6:
Wróć do Publishing overview → "Send changes for review".
Poczekaj na potwierdzenie Krzysztofa zanim klikniesz Send.

UWAGA: NIE ruszaj App name, opisu, screenshotów, ikony, video.
Tylko feature graphic.
```

---

## Status

| Krok | Status | Data |
|------|--------|------|
| Diagnostyka — feature graphic missing | ✅ DONE | 2026-05-23 |
| Diagnostyka — screenshoty w złej kolejności | ✅ DONE | 2026-05-23 |
| Plan reorderu screenshotów + prompt Claude | ✅ DONE | 2026-05-23 |
| Plan feature graphic + brief + Canva tutorial | ✅ DONE | 2026-05-23 |
| Część A: User uruchamia reorder prompt | ⏳ TODO | — |
| Część B: User generuje feature graphic (Canva lub Fiverr) | ⏳ TODO | — |
| Część B: User uruchamia upload prompt | ⏳ TODO | — |
| Google approve (24-48h) | ⏳ TODO | — |

Po wykonaniu — zaktualizuj status w [`LAUNCH_PLAN.md`](LAUNCH_PLAN.md) Etap 1.5 Issue #2 → ✅ DONE.

---

## Lokalne kopie obrazków z analizy (do reuse)

W `/tmp/`:
- `store_icon.png` — ikona apki (512×512)
- `store_screen1.png` — ekran główny (najsłabszy)
- `store_screen2.png` — zielona belka nad ofertą Uber (świetny)
- `store_screen3.png` — settings
- `store_screen4.png` — **mapa + 2 belki** (najlepszy, kandydat na phone mockup w feature graphic)
- `store_screen5.png` — notyfikacja systemowa

Jeśli chcesz lepsze rozdzielczości — kontaktuj Krzysztofa po świeże screenshoty z aktualnej apki na jego telefonie.
