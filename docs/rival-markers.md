# CourierAssist — Rival Platform Markers (Audyt)

**Data audytu:** 2026-04-01

---

## Obecny stan markerow w kodzie

Kazdy parser OCR ma liste `rivalPlatformMarkers` — stringi z UI innych platform, ktore powoduja odrzucenie (return null). To defense-in-depth przeciw cross-contamination (np. screenshot Ubera trafia do WoltOcrParser).

---

## Markery per parser

### UberOcrParser (rival = Wolt + Bolt)

| Marker | Jezyk | Platforma |
|--------|-------|-----------|
| "Odbior za" | PL | Wolt |
| "Pickup in" | EN | Wolt |
| "Забери через" | UK | Wolt |
| "Spodziewany zarobek" | PL | Wolt |
| "Expected earnings" | EN | Wolt |
| "Очікуваний заробіток" | UK | Wolt |
| "Dostawa od" | PL | Wolt |
| "Delivery from" | EN | Wolt |
| "Доставка від" | UK | Wolt |
| "Potwierdzd odbior" | PL | Bolt |
| "Confirm pickup" | EN | Bolt |
| "Decline" | EN | Bolt |
| "Show map" | EN | Bolt |
| "Looking for orders" | EN | Bolt |
| "Go offline" | EN | Bolt |

**Luki:**
- Brak Bolt UK markerow (ukrainski UI Bolta)
- Bolt PL markery tylko "Potwierdzd odbior" — brak "Odrzuc", "Pokaz mape", "Szukam zamowien", "Przejdz offline"

### WoltOcrParser (rival = Uber + Bolt)

| Marker | Jezyk | Platforma |
|--------|-------|-----------|
| "Lacznie" | PL | Uber |
| "Lacznie" | PL (OCR) | Uber |
| "Загалом" | UK | Uber |
| "Dostawa ·" | PL | Uber |
| "Delivery ·" | EN | Uber |
| "Jestes w trybie online" | PL | Uber |
| "You're online" | EN | Uber |
| "Decline" | EN | Bolt |
| "Show map" | EN | Bolt |
| "Looking for orders" | EN | Bolt |
| "Go offline" | EN | Bolt |

**Luki:**
- **Brak Uber EN** — "Total" (angielski odpowiednik "Lacznie")
- Brak Bolt PL/UK markerow

### GlovoOcrParser (rival = Uber + wlasne guardy)

| Marker | Jezyk | Kontekst |
|--------|-------|----------|
| "Lacznie" | PL | Guard Uber |
| "Lacznie" | PL (OCR) | Guard Uber |
| "Загалом" | UK | Guard Uber |
| "Potwierdzd odbior" | PL | Guard: ekran szczegolow (nie oferta) |
| "Potwierdz odbior" | PL (OCR) | Guard: ekran szczegolow |
| "Підтвердити отримання" | UK | Guard: ekran szczegolow |
| "Confirm pickup" | EN | Guard: ekran szczegolow |

**Luki:**
- **Brak Uber EN** — "Total"
- Brak guardow na Bolt/Wolt (ale Glovo uzywa accessibility tree, nie OCR screenshot, wiec ryzyko nizsze)

### BoltFoodOcrParser (rival = Uber + Wolt)

| Marker | Jezyk | Platforma |
|--------|-------|-----------|
| "Lacznie" | PL | Uber |
| "Lacznie" | PL (OCR) | Uber |
| "Загалом" | UK | Uber |
| "Dostawa ·" | PL | Uber |
| "Delivery ·" | EN | Uber |
| "Jestes w trybie online" | PL | Uber |
| "You're online" | EN | Uber |
| "Odbior za" | PL | Wolt |
| "Pickup in" | EN | Wolt |
| "Забери через" | UK | Wolt |
| "Spodziewany zarobek" | PL | Wolt |
| "Expected earnings" | EN | Wolt |
| "Очікуваний заробіток" | UK | Wolt |

**Luki:**
- **Brak Uber EN** — "Total"

---

## Duplikacja markerow

Te same stringi powtarzaja sie w wielu parserach:

| String | Wystepuje w |
|--------|-------------|
| "Lacznie" / "Загалом" | WoltOcrParser, GlovoOcrParser, BoltFoodOcrParser |
| "Odbior za" / "Pickup in" / "Забери через" | UberOcrParser, BoltFoodOcrParser |
| "Spodziewany zarobek" / "Expected earnings" | UberOcrParser, BoltFoodOcrParser |
| "Decline" / "Show map" / "Go offline" | UberOcrParser, WoltOcrParser |

**Problem:** Dodanie nowego markera wymaga edycji 2-4 plikow. Latwo o rozsynchronizowanie.

**Propozycja:** Centralny obiekt `PlatformMarkers` w `parser/` z metoda `rivalsOf(platform)`.

---

## GlovoOcrParser — filtry gotowkowe (cash amount)

Glovo ma osobne filtry na kwoty gotowkowe (nie sa wynagrodzeniem kuriera):

| Marker | Jezyk | Kontekst |
|--------|-------|----------|
| "ODBIERZ" | PL | Przycisk odbioru gotowki |
| "gotowka partnerowi" | PL | Info o platnosci |
| "gotowka u partnera" | PL | Info o platnosci |
| "gotowk" | PL | Skrocony match |
| "ZAPLAC" | PL | Przycisk zaplaty |
| "Zaplac" | PL (OCR) | Bez znakow diakrytycznych |
| "reszte za" | PL | Reszta |
| "reszt" | PL | Skrocony match |
| "СПЛАТИТИ" | UK | Zaplatd |
| "ОПЛАТИТИ" | UK | Oplacd |
| "готівкою" | UK | Gotowka |
| "решту" | UK | Reszta |
| "PAY " | EN | Przycisk pay |
| "cash to partner" | EN | Info |
| "change for" | EN | Reszta |
| "COLLECT" | EN | Przycisk odbioru |

**Status:** Pelne pokrycie PL/EN/UK. Nie wymaga zmian.

---

## Podsumowanie luk

| Luka | Parsery | Ryzyko | Priorytet |
|------|---------|--------|-----------|
| Brak Uber EN "Total" | Wolt, Glovo, Bolt | Srednie — EN Uber popup nie odfiltrowany | Wysoki |
| Brak Bolt PL idle markerow | Uber, Wolt | Niskie — Bolt idle to nie oferta | Niski |
| Brak Bolt UK markerow | Uber, Wolt | Niskie — malo UK Bolt userow | Niski |
| Duplikacja w 4 parserach | Wszystkie | Maintenance risk | Sredni |

**Uwaga:** Foreground check + retry context validation to glowne guardy. Rival markers to defense-in-depth (3. warstwa ochrony). Nawet brak markera rzadko prowadzi do false positive — parser i tak musi znalezc kwote + czas + dystans w formacie specyficznym dla platformy.
