# 03 · Implementation Plan — Logo → App Integration

**Status:** 🟢 READY FOR IMPLEMENTATION
**Input:** A1 Final (post-QA) — patrz `01_logo_decision.md` sekcja 6-7
**Kontekst QA:** `02_qa_fixes.md`
**Target branch:** `polishing` (lub nowy `feature/app-icon-refresh`)

---

## Pipeline wysokopoziomowy

```
[1] SVG w Figma
     ↓
[2] Asset generation (Android Studio Image Asset)
     ↓
[3] Device testing (light/dark/launcher comparison)
     ↓
[4] Code integration (res/mipmap, res/drawable)
     ↓
[5] Build → APK → beta release
```

---

## Krok 1 — Export final SVG do Figmy

### Przygotowanie

1. Utwórz Figma file `OrderPilot Branding`
2. Frame `1024 × 1024`, corner radius `224`, fill `#0A1220`

### Import SVG

Wklej do Figma kod SVG z `01_logo_decision.md` sekcja 7 (wersja Color).

### Components do zapisania

| Component | Rozmiar | Kolor | Use case |
|---|---|---|---|
| `Logo/A1/Color` | 1024×1024 | `#FF6B2C` on `#0A1220` | App icon master, store listing |
| `Logo/A1/Monochrome-Black` | 1024×1024 | `#000000` transparent | Light theme UI, Android themed icon (light) |
| `Logo/A1/Monochrome-White` | 1024×1024 | `#FFFFFF` transparent | Dark theme UI, Android themed icon (dark) |
| `Logo/A1/No-Background` | 1024×1024 | `#FF6B2C` transparent | Landing page hero, social media overlay |

### Export settings

- Format: `SVG` (master) + `PNG @ 1x, 2x, 3x`
- Naming convention: `op_icon_1024.svg`, `op_icon_1024@2x.png`, etc.

---

## Krok 2 — Generowanie assetów Android

### 2.1 Android Adaptive Icon (Android 8+)

**Narzędzie:** Android Studio → File → New → Image Asset → Launcher Icons (Adaptive and Legacy)

**Parametry:**
- Foreground: PNG 432×432 (z safe zone 264 centered) — strzała + ring + ticki
- Background: solid color `#0A1220` (lub drawable z squircle bg — zależy od launcher default)
- Legacy icon: PNG 512×512 z full squircle design (fallback dla Android < 8)
- Round icon: wygenerowane automatycznie
- Google Play Store icon: PNG 512×512

**Output:**
- `res/mipmap-mdpi/ic_launcher.webp` (48×48)
- `res/mipmap-hdpi/ic_launcher.webp` (72×72)
- `res/mipmap-xhdpi/ic_launcher.webp` (96×96)
- `res/mipmap-xxhdpi/ic_launcher.webp` (144×144)
- `res/mipmap-xxxhdpi/ic_launcher.webp` (192×192)
- `res/mipmap-anydpi-v26/ic_launcher.xml` (adaptive)
- `res/mipmap-anydpi-v26/ic_launcher_round.xml`
- `res/drawable/ic_launcher_foreground.xml` (vector)
- `res/values/ic_launcher_background.xml` (`<color name="ic_launcher_background">#0A1220</color>`)

### 2.2 Android Themed Icon (Android 13+)

**Nowy requirement:** Android 13+ wspiera „themed icons" — monochrome glyph który system koloruje pod wybraną tapetę.

**Parametry:**
- Monochrome PNG 432×432, safe zone 264 centered, pełny biały (`#FFFFFF`) na transparent
- `res/drawable/ic_launcher_monochrome.xml` — vector z single path (Boolean Union all shapes)
- Update `res/mipmap-anydpi-v26/ic_launcher.xml` o `<monochrome>` tag

### 2.3 Notification icon (small icon, 24dp)

**⚠️ UWAGA:** patrz `02_qa_fixes.md` sekcja 3 — master A1 w 24dp traci ticki i shaft.

**Rekomendacja:** zaprojektować osobny simplified glyph (tylko ring + arrow, grubsze linie) i zapisać jako:
- `res/drawable/ic_notification_24.xml` (vector, monochrome, pojedynczy path)

**Priorytet:** Medium — nie blocker na MVP icon refresh, blocker jeśli dodajemy custom notifications. Aktualnie aplikacja ma foreground service notification z default icon — można odłożyć do osobnego taska.

---

## Krok 3 — Testy na urządzeniach

### 3.1 Light mode — check list

- [ ] Ikona widoczna na jasnej tapecie
- [ ] Ring i strzała mają pełny kontrast
- [ ] Squircle tło wyraźnie oddzielone od tapety
- [ ] Proporcje wyglądają balansowo (nie top-heavy po shift o 8px)

### 3.2 Dark mode — check list

- [ ] Typowa dark wallpaper (domyślna Android/iOS) — kontrast OK
- [ ] Pure black OLED wallpaper — czy squircle zlewa się? (patrz `02_qa_fixes.md` sekcja 4)
- [ ] Color wallpaper (zdjęcie) — ikonka nie ginie

### 3.3 Launcher comparison (kluczowy test!)

Zrzut ekranu launchera z folderem kurierskim:
- Uber (czarny)
- Uber Eats (zielony + czarny)
- Wolt (cyan)
- Glovo (żółty)
- Bolt (zielony)
- Bolt Food (zielony + widelec)
- **OrderPilot (navy + pomarańcz)** ← nasza ikona

**Kryterium:** OrderPilot wyróżnia się wizualnie, nie zlewa się z żadną platformą.

**Jeśli nie wyróżnia się wystarczająco:**
- Zwiększ saturację pomarańczu o 5-10%
- Spróbuj alternatywnego odcienia (odrzucone wcześniej: `#F97316`, `#FF7A1A`)
- **Nie zmieniaj koncepcji** — tylko subtle color tweak

### 3.4 Urządzenia testowe

| Urządzenie | Właściciel | Wallpaper typowy | Priorytet |
|---|---|---|---|
| Xiaomi (tata) | tata | Photo / colorful | **High** (production user) |
| Samsung | Krzysztof | Dark solid | **High** (dev) |
| Pixel | - | Default | Medium (jeśli dostępny) |

### 3.5 Screenshoty do zrobienia

- Light mode launcher — folder kurierski
- Dark mode launcher — folder kurierski
- Ikona solo @ 1x, 2x, 3x zoom (Android accessibility)
- Status bar (jeśli zrobimy notification icon)

---

## Krok 4 — Integracja do aplikacji

### 4.1 Pliki do podmiany

```
OrderPilot/app/src/main/res/
├── mipmap-mdpi/ic_launcher.webp          ← NEW
├── mipmap-mdpi/ic_launcher_round.webp    ← NEW
├── mipmap-hdpi/ic_launcher.webp          ← NEW
├── mipmap-hdpi/ic_launcher_round.webp    ← NEW
├── mipmap-xhdpi/ic_launcher.webp         ← NEW
├── mipmap-xhdpi/ic_launcher_round.webp   ← NEW
├── mipmap-xxhdpi/ic_launcher.webp        ← NEW
├── mipmap-xxhdpi/ic_launcher_round.webp  ← NEW
├── mipmap-xxxhdpi/ic_launcher.webp       ← NEW
├── mipmap-xxxhdpi/ic_launcher_round.webp ← NEW
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml                   ← UPDATE (dodać monochrome)
│   └── ic_launcher_round.xml             ← UPDATE
├── drawable/
│   ├── ic_launcher_foreground.xml        ← NEW (vector)
│   └── ic_launcher_monochrome.xml        ← NEW (vector, Android 13+)
└── values/
    └── ic_launcher_background.xml        ← UPDATE (#0A1220)
```

### 4.2 Inne miejsca użycia logo w aplikacji

| Miejsce | Plik | Zmiana |
|---|---|---|
| Splash screen | `res/drawable/ic_splash.xml` (lub Android 12+ splash API w `themes.xml`) | Podmienić na nową ikonę |
| About screen | `res/layout/activity_about.xml` (jeśli istnieje) | ImageView src podmienić |
| SetupActivity header | `res/layout/activity_setup.xml` | Jeśli jest logo w headerze, podmienić |
| MainActivity header | `res/layout/activity_main.xml` | Jeśli jest logo, podmienić |
| Notifications small icon | `res/drawable/ic_notification_24.xml` | NEW (simplified 24dp glyph) |

**TODO przed implementacją:** zrobić `Grep` po `ic_launcher`, `R.mipmap.ic_launcher`, `R.drawable.ic_splash` żeby znaleźć wszystkie punkty użycia.

### 4.3 Splash screen (Android 12+)

W `themes.xml`:

```xml
<style name="Theme.OrderPilot.Splash" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/ic_launcher_background</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
    <item name="postSplashScreenTheme">@style/Theme.OrderPilot</item>
</style>
```

---

## Krok 5 — Deployment

1. Branch: `feature/app-icon-refresh` (z `polishing`)
2. Commit:
   - `feat(branding): new app icon (A1 arrow-up reticle) + adaptive + themed`
   - `feat(branding): splash screen z nowym logo`
   - (opcjonalnie) `feat(branding): simplified 24dp notification glyph`
3. Update `PROGRESS.md`
4. Build signed APK
5. Test na urządzeniach (Krok 3)
6. Merge do `polishing` → merge do `main`
7. Upload do Play Store Internal Testing
8. Update Play Store listing:
   - App icon (512×512 PNG)
   - Feature graphic (1024×500 — z logo + wordmark + CTA)
   - Screenshots (jeśli logo jest widoczne)

---

## Ryzyka

### R1 — 24dp readability (Medium)
**Opis:** Master A1 w 24dp traci ticki i shaft (sub-pixel, patrz `02_qa_fixes.md` sekcja 3).
**Mitigation:** osobny simplified glyph (ring + arrow, grubsze linie, bez tickków).
**Kiedy blocker:** gdy dodajemy custom notifications z logo. Obecnie nie — odłożone.
**Action:** zaprojektować simplified glyph przy pierwszej potrzebie.

### R2 — Dark wallpaper contrast (Low)
**Opis:** `#0A1220` zlewa się z pure black OLED wallpaperami.
**Mitigation:** pomarańczowe elementy trzymają identity samodzielnie.
**Fallback:** bump bg do `#12192B` (+3 shade) jeśli testy pokażą problem.
**Action:** test na Xiaomi/Samsung w Kroku 3; decyzja po testach.

### R3 — Android adaptive icon masking (Low)
**Opis:** niektóre launchery wymuszają okrągłą maskę — ticki mogą być obcięte na krawędzi safe zone.
**Mitigation:** ring + arrow w safe zone 640×640, tworzą identity samodzielnie. Ticki = dekoracja.
**Action:** akceptujemy. Screenshot z launchera pokaże skalę problemu.

### R4 — Play Store icon review (Low)
**Opis:** Play Store policy odrzuca ikony zbyt podobne do innych apek.
**Mitigation:** A1 jest unikalny w kategorii (navy + orange + pionowa strzała nie występuje u żadnego konkurenta).
**Action:** brak.

### R5 — Regresja z istniejącym brandem (Low)
**Opis:** apka jest już w użyciu u taty (produkcja). Zmiana ikonki = zmiana rozpoznawalności.
**Mitigation:** tata rozpoznaje aplikację głównie przez overlay, nie przez ikonkę. Bezproblemowe.
**Action:** brak. Informacja dla taty przy update.

---

## Decyzje otwarte

### D1 — Simplified 24dp glyph
**Pytanie:** projektować teraz czy później?
**Rekomendacja:** **później** (przy pierwszej potrzebie — custom notification).
**Uzasadnienie:** nie jest blockerem dla MVP icon refresh; foreground service notification używa default icon i to jest akceptowalne.

### D2 — Wordmark / horizontal lockup
**Pytanie:** projektować wordmark (tekstowy) dla landing page / social media?
**Rekomendacja:** **osobny task** po zamknięciu app icon.
**Uzasadnienie:** inna skala problemu (typografia, tracking, kerning) — warto zrobić spójnie z resztą branding system.

### D3 — Animacja ikony (splash / launch)
**Pytanie:** animowany splash (pulse arrow / rotate ring)?
**Rekomendacja:** **odłóż** — nice-to-have, nie blocker MVP.

### D4 — Alternatywne kolory dla różnych trybów
**Pytanie:** inny pomarańcz dla Light vs Dark mode?
**Rekomendacja:** **nie** — `#FF6B2C` działa w obu, consistency wygrywa.

---

## Checklist pre-implementation

- [ ] Figma file `OrderPilot Branding` utworzony
- [ ] SVG A1 zaimportowany, zapisany jako Component
- [ ] Android Studio otwarty na projekcie
- [ ] Git: branch `feature/app-icon-refresh` z `polishing`
- [ ] Backup aktualnej ikony (git log identyfikuje commit)
- [ ] Xiaomi (tata) dostępny do testu — umówić datę
- [ ] Czas rezerwowy: ~2h na generację + integrację + test + commit

---

## MASTER SUMMARY

**Co zrobiliśmy:**
OrderPilot to narzędzie dla kurierów gig economy (decision-support tool z overlay zł/h + GREEN/YELLOW/RED). W sesji projektowej 2026-04-19 zaprojektowaliśmy nowe logo aplikacji w konwencji HUD / celownik, które pozycjonuje produkt jako profesjonalne narzędzie, a nie konsumencki app.

**Proces:** 5 koncepcji → wybór „HUD/celownik" → 5 wariantów kierunkowych → wybór „Arrow-Up Reticle" (V3) → 3 refinement passes (A/B/C → A1/A2/A3) → wybór **A1 jako finalnego**.

**Finalna forma:** squircle tło `#0A1220` (deep navy) + pomarańcz `#FF6B2C` (HUD accent) + ring celownika + pionowa strzała w górę przebijająca ring + 2 boczne ticki (L/R) jako markery radaru.

**Symbolika:** ring = precyzja / reticle, strzała = wzrost / zysk / dobra decyzja, ticki = skanowanie opcji, tip breakthrough = „przebicie się powyżej średniej".

**QA:** zastosowano 2 korekty: (1) shift strzałki o 8px w dół — kompromis między optycznym balansem a zachowaniem breakthrough; (2) shaft border radius `8 → 6` — konsystencja z tickami na poziomie 17% relative. Zidentyfikowano 2 ryzyka nie wymagające fixu w masterze: 24dp sub-pixel issue (rozwiązanie: osobny simplified glyph) + dark OLED contrast (rozwiązanie: identity trzymają pomarańczowe elementy).

**Rezultat:** gotowy produkcyjny SVG (color + monochrome) zoptymalizowany pod Android adaptive icon + themed icon (Android 13+), dokumentacja kompletna, design LOCKED.

**NEXT STEP = IMPLEMENTATION INTO APP** → asset generation (Android Studio Image Asset) + device tests (Xiaomi / Samsung) + integracja do `res/mipmap` + `res/drawable` + splash screen + signed APK + Play Store beta.
