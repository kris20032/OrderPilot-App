# 01 · Logo Decision — OrderPilot

**Status:** ✅ FINAL DESIGN LOCKED → 🚀 IMPLEMENTED + VERIFIED ON DEVICE
**Wariant finalny:** A1 — Arrow-Up Reticle (Final Balanced, post-QA)
**Data decyzji:** 2026-04-19
**Data implementacji:** 2026-04-19
**Weryfikacja na urządzeniu:** 2026-04-19 (Samsung, dark wallpaper + OLED tiles)
**Commit produkcyjny:** `c9d692d` (merged do `polishing`)
**Aktywny branch:** `polishing`

---

## 1. Tło projektu

OrderPilot to narzędzie dla kurierów gig economy (Uber, Wolt, Glovo, Bolt Food) — w real-time analizuje zlecenia i wyświetla overlay z wyliczonym `zł/h` oraz wskaźnikiem GREEN/YELLOW/RED. Użytkownik = kurier, kontekst = decyzja w 1-2 sekundy.

**Pozycjonowanie logo:**
- TOOL dla profesjonalisty, nie konsumencki app
- „Wygląda jak HUD / system wspomagania" — nie jak ładna apka
- Musi wyróżniać się na launcherze kuriera (obok Uber / Wolt / Glovo / Bolt)

**Rynki:** PL / EN (docelowo uniwersalny branding, bez odniesień lokalnych).

**Nazwa:** `OrderPilot` — decyzja kierunkowa: nie zmieniamy, budujemy pod nią branding.

---

## 2. Proces decyzyjny (krok po kroku)

1. **Analiza trendów i konkurencji** — mapa kolorów zajętych (zielony Bolt, cyan Wolt, żółty Glovo, czarny Uber). Wolny teren: pomarańcz, fiolet, navy.
2. **5 koncepcji kierunkowych:**
   - A — Celownik / HUD
   - B — Światło / Sygnał (3 kolory belki)
   - C — Monogram „P" z kierunkiem
   - D — Belka (self-reference do overlay)
3. **Wybór koncepcji A (HUD/celownik).** Uzasadnienie: najlepszy mapping na „decyzja w sekundę" + wolna nisza kolorystyczna (granat + pomarańcz).
4. **5 wariantów w koncepcji A** (V1 Reticle, V2 Compact Target, V3 Arrow-Up, V4 Horizon Tick, V5 Radar Ping).
5. **Wybór V3 — Arrow-Up Reticle.** Semantyka (kierunek = zysk/wzrost) + najlepsza skalowalność do 24dp.
6. **3 refined sub-warianty V3:** V3-A (balans), V3-B (minimal), V3-C (bold).
7. **Wybór V3-A.** Balans między czytelnością a charakterem.
8. **3 final sub-warianty A:** A1 (Final Balanced), A2 (Clean/Minimal), A3 (Bold/Strong).
9. **Wybór A1.** Decyzja finalna (patrz sekcja 4).
10. **QA pass** — korekty optyczne i border radius (patrz `02_qa_fixes.md`).
11. **FINAL DESIGN LOCKED.**

---

## 3. A1 vs A2 vs A3 — porównanie

| Kryterium | A1 (wybrany) | A2 | A3 |
|---|---|---|---|
| Ring stroke | **56** | 60 | 52 |
| Shaft w × h | **36 × 328** | 32 × 324 | 42 × 336 |
| Head w × h | **124 × 128** | 112 × 112 | 148 × 140 |
| Tip y | **184** (pre-QA) / **192** (final) | 204 | 176 |
| Breakthrough tip nad ringiem | 30 px (final) | ~0 (contained) | 54 px |
| Boczne ticki | **44 × 24** | brak | 48 × 26 |
| Charakter | balans / HUD decision tool | clean / SaaS / Linear-like | bold / aggressive tool |

---

## 4. Dlaczego A1 wygrał — 3 argumenty UX/UI

### 4.1 Czytelność w 24dp
A1 zachowuje czytelność wszystkich trzech elementów (ring, strzała, ticki) w małych rozmiarach. A2 traci ticki (nie ma ich wcale → brak metafory „radar/celownik"), A3 robi się „ciasna" (gruba strzałka + bliskie ticki zlewają się optycznie z ringiem).

### 4.2 Wyróżnienie na launcherze
W teście kontekstowym (obok Uber / Wolt / Glovo / Bolt na 56×56 dp):
- A1 — ostra sylwetka pionowej strzały wybija się obok symetrycznych ikon platform, bez bycia agresywną.
- A2 — za chude elementy, subtelność przegrywa z bold-ikon (Uber „U", Wolt „★").
- A3 — mocne, ale zbliża się do „przebodźcowania".

### 4.3 Balans symbolu
A1 ma wizualny balans ring ↔ strzała ~50/50 (ring stroke 56, shaft 36 — obwód ringa kompensuje cieńszą linię strzały). A2 jest ring-heavy (strzała znika), A3 jest arrow-heavy (ring przegrywa). A1 = złoty środek.

**Dodatkowy argument:** boczne ticki w A1 są **kluczowe dla metafory „Pilot"** — bez nich logo traci charakter radaru/celownika i staje się zwykłą strzałką w kółku.

---

## 5. Zasady wizualne (design system logo)

### 5.1 Canvas
- Master: `1024 × 1024` px
- Corner radius: `224` (iOS squircle-equivalent)
- Safe zone Android adaptive: centralne `640 × 640` (główne elementy w środku, ticki mogą wystawać)

### 5.2 Paleta (final)
| Rola | Hex | Zastosowanie |
|---|---|---|
| Background | `#0A1220` | Tło squircle, splashscreen, dark UI base |
| Primary accent | `#FF6B2C` | Wszystkie elementy logo, primary UI accent |
| Monochrome | `#000000` / `#FFFFFF` | Android themed icons, notifications |
| (reserved) | `#12192B` | Fallback bg jeśli testy pokażą kontrast issue na OLED |

### 5.3 Elementy logo i ich znaczenie
| Element | Geometria | Znaczenie |
|---|---|---|
| Ring | obręcz celownika HUD | precyzja, targetowanie, „reticle" |
| Head (strzałka) | ostry tip w kierunku up | wzrost, zysk, dobra decyzja |
| Shaft | pionowa belka solid | kierunek, kontynuacja, stabilność |
| Ticks (L/R) | boczne znaczniki osi | radar / decision tool, skanowanie opcji |
| Tip breakthrough | tip wystaje 30px ponad ring | „przebicie się powyżej średniej" — strzałka wychodzi z reticle w kierunku celu |

### 5.4 Proporcje (relative)
- Ring stroke / ring radius: `56 / 260 = 21.5%`
- Shaft width / ring radius: `36 / 260 = 13.8%`
- Head w : h = `124 : 128` ≈ `0.97 : 1` (prawie kwadratowa, ostra)
- Shaft radius: `6 / 36 = 17%` (spójne z tickami)
- Tick radius: `4 / 24 = 17%`

---

## 6. Finalne parametry (post-QA, A1 production)

Wszystkie wartości na canvas 1024×1024.

```
Ring:        circle  cx=512 cy=512 r=260 stroke=56 fill=none   #FF6B2C
Head:        polygon 512,192  574,320  450,320                 #FF6B2C
Shaft:       rect    x=494 y=320 w=36 h=328 rx=6               #FF6B2C
Tick (L):    rect    x=180 y=500 w=44 h=24  rx=4               #FF6B2C
Tick (R):    rect    x=800 y=500 w=44 h=24  rx=4               #FF6B2C
Background:  rect    1024×1024 rx=224                          #0A1220
```

**Obliczenia pomocnicze:**
- Outer ring edge: y = 512 − (260 + 28) = 222 (top), y = 802 (bottom)
- Inner ring edge: y = 512 − (260 − 28) = 280 (top), y = 744 (bottom)
- Tip y = 192 → breakthrough 30px ponad outer edge
- Arrow COM y = 401 → 111px powyżej środka ringa (akceptowalne top-heavy, zgodne z intencją kierunku)

---

## 7. Finalny SVG (copy-paste-ready)

### Color (app icon master)

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
  <rect width="1024" height="1024" rx="224" fill="#0A1220"/>
  <g fill="#FF6B2C">
    <circle cx="512" cy="512" r="260" fill="none" stroke="#FF6B2C" stroke-width="56"/>
    <rect x="180" y="500" width="44" height="24" rx="4"/>
    <rect x="800" y="500" width="44" height="24" rx="4"/>
    <polygon points="512,192 574,320 450,320"/>
    <rect x="494" y="320" width="36" height="328" rx="6"/>
  </g>
</svg>
```

### Monochrome (Android themed icon, notifications)

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
  <g fill="#000000">
    <circle cx="512" cy="512" r="260" fill="none" stroke="#000000" stroke-width="56"/>
    <rect x="180" y="500" width="44" height="24" rx="4"/>
    <rect x="800" y="500" width="44" height="24" rx="4"/>
    <polygon points="512,192 574,320 450,320"/>
    <rect x="494" y="320" width="36" height="328" rx="6"/>
  </g>
</svg>
```

---

## 8. Co NIE wchodzi do scope'u tej decyzji

- Wordmark / horizontal lockup (landing page, social media) — **do zaprojektowania osobno** w etapie marketing
- Simplified 24dp glyph (status bar, notifications) — **do zaprojektowania osobno** (patrz `02_qa_fixes.md` sekcja 3)
- Animacja ikony (splash, launch) — **nice-to-have**, nie blocker
- Alternatywne kolory (#6366F1 indigo, #F97316 orange, #FF8A3D soft) — odrzucone, zostajemy przy `#FF6B2C`

---

## 9. Status

🔒 **FINAL DESIGN LOCKED** — nie zmieniamy koncepcji ani parametrów bez eksplicytnej decyzji.

Następny krok: `03_implementation_plan.md`.
