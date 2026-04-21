# Play Console — Data Safety form

Ten dokument mówi CO zaznaczyć w Google Play Console → **App content → Data safety**.

Formularz jest w 2 sekcjach:
1. **Data collection and sharing** — czy zbierasz / udostępniasz dane
2. **Data security practices** — jak chronisz dane

Nasza sytuacja jest prosta: **NIE zbieramy niczego**. Wypełnienie zajmie ~5 min.

---

## Sekcja 1 — Data collection and sharing

### Pytanie 1: „Does your app collect or share any of the required user data types?"
**Odpowiedź: NO**

Uzasadnienie: aplikacja nie ma uprawnienia INTERNET, nie wysyła żadnych danych na serwery zewnętrzne, nie używa analytics / tracking SDK.

> ⚠️ **Uwaga:** Google definiuje „collection" jako „transmitting data off the device". My NIC nie wysyłamy → zaznaczamy NO.
>
> Nie chodzi o to czy apka widzi dane (bo widzi tekst oferty przez accessibility), tylko czy **transmitowane** są poza urządzenie. Accessibility-only reading = NIE jest collection wg Google.

### Pytanie 2: „Is all of the user data collected by your app encrypted in transit?"
**Odpowiedź: YES** (trivially — nic nie jest w tranzycie, więc wszystko „in transit" jest zaszyfrowane, bo nie istnieje).

> To standardowe pytanie które i tak musi dostać YES dla każdej apki od 2022.

### Pytanie 3: „Do you provide a way for users to request that their data be deleted?"
**Odpowiedź: YES**

URL do wpisania: `https://kris20032.github.io/OrderPilot-App/legal/data-deletion.html`

---

## Sekcja 2 — Data types (pomijana, bo w sekcji 1 powiedzieliśmy NO)

Jeśli Play Console mimo to poprosi o wypełnienie listy „data types", wypełnij:

- ❌ Location — NIE
- ❌ Personal info (name, email, ID) — NIE
- ❌ Financial info — NIE
- ❌ Health & fitness — NIE
- ❌ Messages — NIE
- ❌ Photos & videos — NIE
- ❌ Audio — NIE
- ❌ Files & docs — NIE
- ❌ Calendar — NIE
- ❌ Contacts — NIE
- ❌ App activity — NIE
- ❌ Web browsing — NIE
- ❌ App info and performance — NIE (brak Crashlytics / Analytics)
- ❌ Device or other IDs — NIE (AD_ID explicite usunięty, brak innych identyfikatorów)

**Wszystko NIE.**

---

## Sekcja 3 — Security practices

### „Is your data encrypted in transit?"
**YES** — nie ma tranzytu, więc domyślnie.

### „Do you follow Families Policy?"
Odpowiedź zależy od tego czy targetujemy dzieci. **NO** — aplikacja dla kurierów zawodowych (dorośli).

### „Has your app been independently validated against a global security standard?"
**NO** — brak oficjalnej certyfikacji (SOC 2, ISO 27001). Niezaznaczenie tego NIE jest problemem — opcjonalne pytanie dla enterprises.

---

## Po wypełnieniu

Po submit formularza Play Console pokazuje podgląd „Data safety" section tak jak zobaczy go użytkownik na stronie aplikacji. Powinno wyglądać tak:

> **No data collected**
> The developer says this app doesn't collect any user data.
>
> **No data shared with third parties**
> The developer says this app doesn't share user data with other companies or organizations.

To jest **idealny** wynik. Bardzo mało apek na Play Store może to pokazać.

---

## Jak to się ma do Privacy Policy

Data Safety Form to **streszczenie** dla użytkowników Play Store (tabelka pokazywana na karcie apki). Privacy Policy to **pełen dokument prawny**. Oba muszą być spójne.

Spójność u nas:
- Data Safety: „No data collected" ↔ PP: „Aplikacja nie zbiera żadnych danych"
- Data Safety: „No data shared" ↔ PP: „Dane nie opuszczają urządzenia"
- Data Deletion URL: wskazany w Data Safety + zlinkowany z PP

---

**Data utworzenia dokumentu:** 2026-04-21
**Wersja:** 1.0
