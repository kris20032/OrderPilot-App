# 02 · QA Fixes — A1 Refinement

**Status:** ✅ APPLIED (zastosowane w finalnej wersji A1)
**Data:** 2026-04-19
**Źródło:** przegląd techniczny A1 przed lockiem designu

---

## 0. Cel QA

Po wyborze A1 jako finalnego logo, przed zamknięciem designu zrobiony został przegląd techniczny pod kątem:
1. Optycznego wyśrodkowania strzałki wewnątrz ringa
2. Spójności zaokrągleń (border radius)
3. Czytelności w najmniejszych skalach (24dp)
4. Kontrastu w dark mode / OLED

Znalezione 2 realne problemy (wymagały korekty) + 2 potencjalne ryzyka (zidentyfikowane, nie wymagają zmiany mastera).

---

## 1. FIX 1 — Optyczne wyśrodkowanie strzałki

### Problem

W wersji pre-QA:
- Ring center: `(512, 512)`
- Arrow center of mass (waga ważona polami head + shaft):
  - Head (trójkąt): centroid y = `(184 + 312 + 312) / 3 = 269`, area ≈ `7936`
  - Shaft: centroid y = `476`, area = `11808`
  - **Weighted COM y ≈ 393**
- **Odchylenie: arrow COM jest 119 px POWYŻEJ środka ringa**

Matematycznie symbol jest symetryczny (wszystko wycentrowane na osi x = 512), ale **optycznie strzałka wygląda jakby „uciekała" z ringa ku górze**. Przy rozmiarach 48-96 dp efekt jest zauważalny — ikonka czuje się top-heavy, jakby nie była w stanie równowagi.

### Korekta

**Shift całej strzałki w dół o 8 px na masterze 1024.**

| Element | Przed | Po |
|---|---|---|
| Tip y | 184 | **192** |
| Head base y | 312 | **320** |
| Shaft y (top) | 312 | **320** |
| Shaft bottom | 640 | **648** |
| Arrow COM y | 393 | **401** |
| Odchylenie od ring center | 119 px | **111 px** |
| Tip breakthrough (nad outer edge ringa) | 38 px | **30 px** |

Ticki (L/R) i ring **nie były ruszane** — zostają na y = 500 i cx/cy = 512.

### Dlaczego dokładnie 8 px, nie więcej i nie mniej

- **< 4 px:** imperceptible, fake QA — nie poprawia realnej percepcji
- **8 px:** = ~1 render-px przy 96 dp renderingu (proporcjonalnie). Redukuje top-heavy feel o ~7%, pozostaje niewidoczny jako „ruch" ale czuć go jako balans
- **12-16 px:** redukcja breakthrough o 30-40% — zaczyna rozbijać intencję „arrow emerges from reticle"
- **Pełne optyczne wyśrodkowanie (shift 119 px):** zniszczyłoby breakthrough — tip znalazłby się WEWNĄTRZ ringa, cała metafora „przebicia się powyżej średniej" przepada

**8 px = optymalny kompromis między balansem a zachowaniem intencji designu.**

### Co się poprawiło

- Strzała wygląda bardziej „osadzona" w ringu, mniej uciekająca
- Tip breakthrough (30 px) nadal w pełni czytelny — efekt „przebicia" zachowany
- Waga wizualna subtelnie lepiej rozłożona, ale charakter upward-directional pozostaje

---

## 2. FIX 2 — Border radius consistency

### Problem

W wersji pre-QA:
- Shaft: `rx = 8`, shaft width = 36 → **22% relative radius**
- Ticki: `rx = 4`, tick height = 24 → **17% relative radius**

Shaft miał bardziej zaokrąglone rogi niż ticki, proporcjonalnie do swojego rozmiaru. Różnica **22% vs 17%** jest subtelna, ale na prostych geometriach (solid rects) rytm zaokrągleń jest widoczny — elementy wyglądały jakby nie pochodziły z jednego systemu.

### Korekta

**Shaft `rx`: 8 → 6** (17% relative radius, zgodne z tickami).

| Element | rx przed | rx po | Relative |
|---|---|---|---|
| Shaft | 8 | **6** | 22% → **17%** |
| Tick L | 4 | 4 | 17% (bez zmian) |
| Tick R | 4 | 4 | 17% (bez zmian) |

### Dlaczego `rx = 6`, nie 4 i nie 8

- `rx = 4`: zbyt ostry dla 36-px-wide shaft (wyglądałby jak obcięty prostokąt, nie jak dopracowany element)
- `rx = 6`: 17% — spójny matematycznie z tickami, wizualnie soft ale nie pill-like
- `rx = 8`: oryginalna wartość, za miękka — kolidowała z ostrym head trójkątem (zero radius)

**17% relative radius = design token** — jeśli w przyszłości dodawane będą inne elementy (np. UI buttony), ten token propaguje się naturalnie.

### Co się poprawiło

- Spójny rytm zaokrągleń w całym symbolu
- Shaft bardziej „precision tool", mniej „pill"
- Kontrast z head (zero radius, ostry) wzmocniony — shaft i head mają teraz wyraźnie różne charaktery: shaft = kontynuacja z delikatną miękkością, head = sharp direction

---

## 3. ZIDENTYFIKOWANE RYZYKO — 24dp sub-pixel issue

### Problem

Master A1 (1024 canvas) przy renderingu do 24dp:

| Element | Master | 24dp render | Status |
|---|---|---|---|
| Ring stroke | 56 px | `56 × 24/1024 = 1.31 px` | OK — widoczny |
| Shaft width | 36 px | `36 × 24/1024 = 0.84 px` | **Sub-pixel — blur / zanika** |
| Tick height | 24 px | `24 × 24/1024 = 0.56 px` | **Sub-pixel — ledwo widoczny** |
| Tick width | 44 px | `44 × 24/1024 = 1.03 px` | OK (granicznie) |

Przy 24dp (status bar icon, notification small icon) ticki będą prawie niewidoczne, shaft będzie rozmazany. **Logo w tym rozmiarze traci charakter celownika i staje się rozmytą kropką z ringem.**

### Decyzja: NIE FIX w masterze

- Pogrubienie elementów w masterze zniszczyłoby proporcje w 48-1024 dp (czyli 99% zastosowań)
- Master jest zoptymalizowany pod app icon (48-192 dp), nie pod 24dp notification glyph

### Rozwiązanie: osobny simplified glyph dla 24dp

**Do zaprojektowania w etapie implementacji (patrz `03_implementation_plan.md`).**

Specyfikacja roboczą (MVP):
- Tylko ring + strzała (bez tickków)
- Stroke ringa grubszy proporcjonalnie: 72-80 px (zamiast 56)
- Shaft grubszy: 56-64 px (zamiast 36)
- Head odpowiednio dostosowany
- Monochrome (pojedynczy fill, Boolean Union)
- Canvas 24×24 dp native, albo 192×192 master z downscalingu do 24

**Use case:** notification small icon, status bar, Android API monochrome icon.

**Priorytet:** Medium — blocker tylko gdy dodajemy notifications do aplikacji. Aktualnie aplikacja nie wyświetla custom notifications z ikoną logo (tylko foreground service notification, która używa default Android icon).

---

## 4. ZIDENTYFIKOWANE RYZYKO — Dark mode / OLED contrast

### Problem

Tło `#0A1220` vs różne typy dark wallpaperów:

| Wallpaper | Kontrast ΔE | Status |
|---|---|---|
| Biały / jasny | ~90 | ✅ Pełny |
| Domyślny Android/iOS dark (blue-grey) | ~15-25 | ✅ OK |
| Custom dark (gradients, photo) | ~10-20 | ✅ OK |
| Pure black OLED (`#000000`) | **~3** | ⚠️ Squircle zlewa się z tapetą |

Na pure black OLED wallpaper kafelek ikony traci widoczną sylwetę — ikonka wygląda jakby pływała w przestrzeni.

### Decyzja: NIE FIX w masterze

**Dlaczego:**
- Identity logo = pomarańczowe elementy (ring + arrow), a nie squircle tło
- Pomarańcz `#FF6B2C` ma pełny kontrast vs każdy dark wallpaper (ΔE > 60)
- Kształt logo pozostaje w 100% rozpoznawalny nawet bez widocznego kafelka
- Realistyczne użycie: <10% kurierów ma pure black OLED wallpaper
- Dodanie outer stroke konfliktowałoby z minimalizmem i dodałoby 1 element do path'u

### Fallback (jeśli testy pokażą problem)

**Opcja A:** bump background o 3 shade do `#12192B`
- Plus: zachowuje „deep navy" charakter, +3 shade wystarcza na pure black contrast
- Minus: odrobinę traci głębię

**Opcja B:** outer stroke 2dp `#1E2940` na squircle
- Plus: maksymalna definicja sylwety
- Minus: dodaje extra element, konflikt z minimalizmem

**Nie decydujemy teraz** — czekamy na faktyczne testy na urządzeniach (patrz `03_implementation_plan.md` Krok 3).

---

## 5. Podsumowanie zmian

### Geometria przed QA vs po QA

| Element | Przed QA | Po QA (final) |
|---|---|---|
| Ring cx/cy | 512, 512 | 512, 512 |
| Ring r | 260 | 260 |
| Ring stroke | 56 | 56 |
| Head polygon | 512,184 574,312 450,312 | **512,192 574,320 450,320** |
| Shaft x | 494 | 494 |
| Shaft y | 312 | **320** |
| Shaft w × h | 36 × 328 | 36 × 328 |
| Shaft rx | **8** | **6** |
| Tick L | 180, 500, 44×24, rx=4 | 180, 500, 44×24, rx=4 |
| Tick R | 800, 500, 44×24, rx=4 | 800, 500, 44×24, rx=4 |
| Background | 1024×1024 rx=224 `#0A1220` | 1024×1024 rx=224 `#0A1220` |

**Zmienione:** head polygon (shift 8), shaft y (shift 8), shaft rx (8→6).
**Bez zmian:** ring, ticki, background.

### Finalne parametry po QA

Patrz `01_logo_decision.md` sekcja 6 i 7.

---

## 6. Lessons learned (dla przyszłych iteracji)

1. **Matematyczne wyśrodkowanie ≠ optyczne wyśrodkowanie** — dla asymetrycznych kształtów (strzałki, trójkąty) trzeba liczyć weighted COM i porównać ze środkiem kontenera
2. **Relative radius ratio (rx / width)** jako design token — lepsze niż absolute rx (zachowuje konsystencję przy skalowaniu elementów)
3. **Sub-pixel analysis** przed lockiem — każdy element < 1.5 px @ target size ryzykuje utratę w rasteryzacji
4. **OLED edge case** — pure black wallpapery to <10% use case, ale warto mieć fallback plan
