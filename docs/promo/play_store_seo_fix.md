# Play Store SEO Fix — Issue #1

> **Cel:** Aplikacja niewidoczna w Google Play gdy user wpisuje „order pilot" (z spacją). Fix przez optymalizację Short description + Full description, BEZ zmiany App name (mniej ryzyka, prościej).
>
> **Czas:** 5-10 min wklejania przez Claude w panelu Chrome + 24-48h Google review.
>
> **Status:** ⏳ TODO 2026-05-23

---

## Strategia

App name pozostaje **„OrderPilot"** (jedno słowo, jak teraz). Optymalizujemy 2 pola które mają drugi i trzeci najwyższy weight w Google Play search:

| Pole | Aktualnie | Po fixie | Główna zmiana |
|------|-----------|----------|---------------|
| **App name** (30 chars) | OrderPilot | OrderPilot | bez zmian |
| **Short description** (80 chars) | ~75/80 (nieznana dokładna treść) | **66/80, eksplicit „Order Pilot"** | ⭐ frazę „Order Pilot" wprowadzić |
| **Full description** (4000 chars) | ~1564/4000 | **~2000/4000, „Order Pilot" 4-5× w naturalnym kontekście** | ⭐ pierwsze 250 chars MUSZĄ mieć „Order Pilot" |

**Spodziewany efekt:** wpisanie „order pilot" w Google Play **znajduje apkę w top 5** po 24-48h od review. Wpisanie „orderpilot" (jak teraz) nadal działa.

---

## Gotowe teksty do wklejenia

### 📋 Short description (PL) — 66/80 znaków

```
Order Pilot dla kurierów: PLN/h zleceń Uber, Wolt, Glovo, Bolt Food
```

**Czemu ten:**
- Zaczyna od „**Order Pilot**" (max keyword weight)
- „**kurierów**" — główne keyword targetowe
- „**PLN/h**" — unique value
- Pełne nazwy platform — „**Uber, Wolt, Glovo, Bolt Food**" (każda jest osobnym search query)

---

### 📋 Full description (PL) — ~2000/4000 znaków

```
Order Pilot to darmowa aplikacja dla kurierów Uber, Wolt, Glovo i Bolt Food. OrderPilot wyświetla PLN/h każdego zlecenia w czasie rzeczywistym — zielona, żółta lub czerwona belka nad ekranem aplikacji kuriera mówi czy oferta jest opłacalna.

JAK DZIAŁA
Aplikacja wykrywa nowe oferty zleceń z apek kurierskich (Uber Driver, Wolt Courier, Glovo Courier, Bolt Food Courier), wylicza opłacalność (PLN/h, PLN/km) na podstawie Twoich własnych progów i wyświetla wynik jako kolorowa belka:
🟢 zielona = bierz
🟡 żółta = zależy od dystansu
🔴 czerwona = odrzuć

Order Pilot pokazuje obok kwotę, dystans, czas — wszystko czytelnie w jednym miejscu, abyś mógł zdecydować w 5 sekund.

DLA KOGO
- Kurierzy Uber Eats (aplikacja Uber Driver)
- Kurierzy Wolt
- Kurierzy Glovo
- Kurierzy Bolt Food

Wszystkie 4 platformy obsługiwane, możesz mieć włączone jednocześnie.

PRYWATNOŚĆ
- Wszystko działa lokalnie na Twoim telefonie
- Zero połączeń z internetem (możesz wyłączyć dane mobilne — apka nadal działa)
- Zero kont, zero loginów, zero rejestracji
- Zero śledzenia, zero reklam, zero analytics
- Order Pilot nie wysyła żadnych danych nigdzie

BEZPIECZEŃSTWO DLA TWOJEGO KONTA
Aplikacja działa przez Android Accessibility Service (czyta tylko ekran), nie używa API platform kurierskich, więc nie ma ryzyka bana ani naruszenia regulaminu Uber, Wolt, Glovo czy Bolt.

JĘZYKI
Order Pilot dostępny w 4 językach: polski, angielski, rosyjski, ukraiński.

ZASTRZEŻENIA
OrderPilot nie jest afiliowany z, sponsorowany przez ani powiązany z firmami Uber, Wolt, Glovo czy Bolt. To niezależne narzędzie stworzone przez kuriera dla kurierów.

Wyliczenia opłacalności (PLN/h) są szacunkowe i mają charakter informacyjny — nie stanowią porady finansowej. Każdy kurier sam podejmuje decyzję o akceptacji zlecenia.

UPRAWNIENIA
- Accessibility Service: wymagane do odczytu ofert z aplikacji kurierskich
- Wyświetlanie nad innymi aplikacjami: wymagane do pokazania kolorowej belki
- Foreground Service: wymagane do działania w tle podczas pracy
- Brak dostępu do kontaktów, lokalizacji, kamery, mikrofonu

WYMAGANIA
Android 8.0 (API 26) lub nowszy.
```

**Frazy SEO w tekście:**
- „Order Pilot" — 5× (linie 1, 6, 9, 14, 26)
- „OrderPilot" — 2× (linie 1, 25 — w disclaimer)
- „kurier/kurierów/kurierzy" — 7×
- „Uber" — 6×, „Wolt" — 5×, „Glovo" — 5×, „Bolt" — 5×
- „PLN/h" — 3×
- „opłacalność/opłacalny" — 2×
- „darmowa" — 1× (pierwsze zdanie, search keyword)

---

## 🤖 Prompt dla Claude w panelu Chrome (skopiuj i wklej do panelu po prawej)

> **Jak użyć:** Otwórz panel Claude w Chrome (po prawej stronie), upewnij się że jesteś zalogowany na Google Play Console (`play.google.com/console`) w jakiejś karcie, wklej poniższy prompt:

```
Cześć, potrzebuję pomocy z edycją store listing w Google Play Console.

KROK 1:
Otwórz nową kartę i wejdź na: https://play.google.com/console

KROK 2:
Z listy aplikacji wybierz aplikację o nazwie "OrderPilot" (package com.orderpilot.app).

KROK 3:
W lewym menu nawiguj do: Grow → Store presence → Main store listing
(Może być też: Wzrost → Obecność w sklepie → Główna strona aplikacji w polskiej wersji UI.)

KROK 4:
Znajdź pole "Short description" (Krótki opis) — ma licznik 0/80 znaków.
ZASTĄP cały aktualny tekst tym:

Order Pilot dla kurierów: PLN/h zleceń Uber, Wolt, Glovo, Bolt Food

KROK 5:
Znajdź pole "Full description" (Pełny opis) — ma licznik 0/4000 znaków.
ZASTĄP cały aktualny tekst tym:

Order Pilot to darmowa aplikacja dla kurierów Uber, Wolt, Glovo i Bolt Food. OrderPilot wyświetla PLN/h każdego zlecenia w czasie rzeczywistym — zielona, żółta lub czerwona belka nad ekranem aplikacji kuriera mówi czy oferta jest opłacalna.

JAK DZIAŁA
Aplikacja wykrywa nowe oferty zleceń z apek kurierskich (Uber Driver, Wolt Courier, Glovo Courier, Bolt Food Courier), wylicza opłacalność (PLN/h, PLN/km) na podstawie Twoich własnych progów i wyświetla wynik jako kolorowa belka:
🟢 zielona = bierz
🟡 żółta = zależy od dystansu
🔴 czerwona = odrzuć

Order Pilot pokazuje obok kwotę, dystans, czas — wszystko czytelnie w jednym miejscu, abyś mógł zdecydować w 5 sekund.

DLA KOGO
- Kurierzy Uber Eats (aplikacja Uber Driver)
- Kurierzy Wolt
- Kurierzy Glovo
- Kurierzy Bolt Food

Wszystkie 4 platformy obsługiwane, możesz mieć włączone jednocześnie.

PRYWATNOŚĆ
- Wszystko działa lokalnie na Twoim telefonie
- Zero połączeń z internetem (możesz wyłączyć dane mobilne — apka nadal działa)
- Zero kont, zero loginów, zero rejestracji
- Zero śledzenia, zero reklam, zero analytics
- Order Pilot nie wysyła żadnych danych nigdzie

BEZPIECZEŃSTWO DLA TWOJEGO KONTA
Aplikacja działa przez Android Accessibility Service (czyta tylko ekran), nie używa API platform kurierskich, więc nie ma ryzyka bana ani naruszenia regulaminu Uber, Wolt, Glovo czy Bolt.

JĘZYKI
Order Pilot dostępny w 4 językach: polski, angielski, rosyjski, ukraiński.

ZASTRZEŻENIA
OrderPilot nie jest afiliowany z, sponsorowany przez ani powiązany z firmami Uber, Wolt, Glovo czy Bolt. To niezależne narzędzie stworzone przez kuriera dla kurierów.

Wyliczenia opłacalności (PLN/h) są szacunkowe i mają charakter informacyjny — nie stanowią porady finansowej. Każdy kurier sam podejmuje decyzję o akceptacji zlecenia.

UPRAWNIENIA
- Accessibility Service: wymagane do odczytu ofert z aplikacji kurierskich
- Wyświetlanie nad innymi aplikacjami: wymagane do pokazania kolorowej belki
- Foreground Service: wymagane do działania w tle podczas pracy
- Brak dostępu do kontaktów, lokalizacji, kamery, mikrofonu

WYMAGANIA
Android 8.0 (API 26) lub nowszy.

KROK 6:
Sprawdź czy liczniki znaków pokazują w normie:
- Short description: powinien pokazać 66/80
- Full description: powinien pokazać ok 2000/4000

KROK 7:
Kliknij przycisk "Save" (Zapisz) na dole strony.

KROK 8:
Po zapisaniu wróć do "Publishing overview" (Przegląd publikacji) w lewym menu.
Powinien być banner mówiący o zmianach do wysłania ("X changes pending review" / "X zmian do sprawdzenia").
Kliknij "Send changes for review" (Wyślij zmiany do sprawdzenia).

KROK 9:
Zrób screenshot potwierdzenia że zmiany są w review i podaj mi link do zmienionej store listing.

UWAGA: NIE zmieniaj App name, ikony, screenshotów, feature graphic ani żadnych innych pól.
Tylko Short description i Full description.

Jeśli pojawi się 2FA, CAPTCHA lub coś nieoczywistego — zatrzymaj się i daj znać Krzysztofowi żeby potwierdził.
```

---

## Weryfikacja po 24-48h

Po tym jak Google zaakceptuje zmiany (email „Your store listing update is live"):

1. **Test na 2-3 telefonach** (różne konta Google jeśli możliwe):
   - Otwórz Google Play
   - Wpisz w search: **„order pilot"** (z spacją)
   - Aplikacja OrderPilot powinna być **w top 5 wyników**
2. **Test alternative search:**
   - „kurier zł/h" — powinna być w top 10
   - „kurier uber wolt" — powinna być w top 20
   - „orderpilot" (jak dotychczas) — nadal #1 (brand search)
3. **Console → Statistics → Acquisition reports** za 7 dni:
   - Zakładka „Search terms" — sprawdź jakimi frazami ludzie znajdują apkę
   - Powinien pojawić się „order pilot" jako jedna z top fraz

**Jeśli „order pilot" NIE znajduje apki po 48h:**
- Sprawdź czy edycja faktycznie zapisała się (Console → Main store listing → przeczytaj pierwsze 250 chars Full description, czy zawiera „Order Pilot")
- Eskalacja: zmień App name na „OrderPilot · Order Pilot" (24 chars) — mocniejszy sygnał dla Google

---

## Status

| Krok | Status | Data |
|------|--------|------|
| Tekst SEO copy gotowy | ✅ DONE | 2026-05-23 |
| Prompt dla Claude in Chrome gotowy | ✅ DONE | 2026-05-23 |
| User uruchomił prompt w panelu Claude | ✅ DONE | 2026-05-23 |
| Edycje zapisane w Console (Short 67/80, Full 2137/4000) | ✅ DONE | 2026-05-23 |
| Zmiany w „Zmiany w trakcie sprawdzania" (under review) | ✅ DONE | 2026-05-25 |
| Google approve (24-48h od submit) | ⏳ TODO | ~2026-05-26/27 |
| Weryfikacja „order pilot" search z innego telefonu | ⏳ TODO | po approve |

Po wykonaniu — zaktualizuj status w [`LAUNCH_PLAN.md`](LAUNCH_PLAN.md) Etap 1.5 Issue #1 → ✅ DONE.
