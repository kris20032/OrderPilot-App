# Phase 4 — Privacy & Policy content (notes)

Ten dokument opisuje WHAT + WHY dla decyzji podjętych podczas Phase 4. Jeśli coś zmieniasz w treści PP / Data Deletion / Data Safety — zacznij od przeczytania tych notatek, żeby nie złamać intencji.

---

## Stan faktyczny (audyt wykonany 2026-04-21)

**Konkluzja: apka nie zbiera NICZEGO. Wszystko on-device, zero transmisji.**

Dowody:
1. **Brak permission INTERNET** w `AndroidManifest.xml` — technicznie niemożliwa komunikacja sieciowa.
2. **Brak permission ACCESS_NETWORK_STATE** — nawet nie może sprawdzić czy jest online.
3. **AD_ID explicite usunięty** (`tools:node="remove"`) — świadoma decyzja nie-trackingu.
4. **Dependencies:** tylko androidx.*, kotlinx, ML Kit Text Recognition. Zero Firebase, Analytics, Crashlytics, OkHttp, Retrofit.
5. **Accessibility filter po packageNames** — serwis aktywny WYŁĄCZNIE na 4 apkach kurierskich (Uber, Wolt, Glovo, Bolt Food).

---

## Decyzje

### 1. Ton: „człowieczy TL;DR + legal footer" (Opcja B)
- User wybrał po prezentacji 2 opcji (A = formalno-prawny, B = przyjazny).
- PP zaczyna się TL;DR („nie zbieramy niczego"), przeplatane przykładami technicznymi (weryfikacja przez `adb shell`), kończy sekcją RODO.
- Ryzyko: super-strict reviewer może uznać TL;DR za niepoważny. Mitigacja: legal section poniżej jest pełnowymiarowa, RODO-compliant.

### 2. Kontakt: tylko email (bez adresu fizycznego)
- User świadomie wybrał prywatny email `krzychu.brzezi@gmail.com`.
- Bez adresu zamieszkania (user indywidualny, publiczny URL = nie chce doxxingu).
- RODO: podanie kanału kontaktowego wystarcza (email), adres nie jest wymogiem dla osoby fizycznej.

### 3. Status prawny: osoba fizyczna
- Bez JDG, bez spółki.
- PP: „Krzysztof Brzezinski, osoba fizyczna (niezarejestrowana działalność gospodarcza)".
- Jeśli kiedyś założysz JDG/spółkę — zmień Administratora we wszystkich plikach HTML.

### 4. Języki: PL + EN
- 2 osobne pliki HTML (cleaner niż in-page JS toggle, lepsze SEO).
- UK + RU mogą dojść w przyszłości (apka ma 4 locale), ale MVP: PL + EN wystarczy. Play Store listing można zrobić w wielu językach bez osobnego PP per język.

### 5. Hosting: GitHub Pages
- Darmowe, SSL automatyczny, wystarcza dla indywidualnego dewelopera.
- URL hardcoded w kodzie (`OrderPilotApp.PRIVACY_POLICY_URL`) — zgadza się z faktycznym URL (audyt 2026-04-21).
- Gdyby kiedyś zmienić hosting (custom domain) — update URL w kodzie + redirect ze starego.

### 6. URL structure
- `/legal/index.html` — landing (3 karty)
- `/legal/privacy-policy.html` — PL (główny, bo primary market = PL)
- `/legal/privacy-policy-en.html` — EN
- `/legal/data-deletion.html` — PL + EN na jednej stronie (prosty content, nie warto dzielić)

### 7. Data Safety form
- Wszystko NO w Data Collection section — bo faktycznie nic nie zbieramy.
- Data Deletion URL podany.
- Oczekiwany widok na Play Store: „No data collected" + „No data shared with third parties" = idealnie.

### 8. Accessibility declaration — najwyższe ryzyko rejection
- Treść ~300 słów pokrywa:
  - Core functionality argumentation (courier 5-10s decision window)
  - Scope limitation (packageNames filter na 4 apki)
  - Prominent Disclosure (już zaimplementowany w Batch 3)
  - Data handling (on-device, no transmission)
  - Link do PP
- Gotowa na przewidywane pytania review (non-core, alternative mechanism, missing disclosure).

---

## Czego NIE dało się uniknąć

- **ML Kit Text Recognition** — używamy Google'owej biblioteki. Mimo że gwarantuje on-device processing, formalnie to jest „third-party SDK". W PP wzmianka: „przetwarzanie on-device zgodnie z dokumentacją Google". Review nie powinno tego zakwestionować, bo brak INTERNET permission = nie może wysyłać.
- **Google Play itself** — Play Store zbiera dane o instalacji apki. To poza naszą kontrolą. PP wspomina z linkiem do polityki Google.

---

## Co zostaje dla usera (Phase 4 tasks)

1. **Włączyć GitHub Pages** (instrukcja: `GITHUB_PAGES_SETUP.md`)
2. **Zweryfikować URL działa** (3 strony)
3. **Wypełnić Data Safety form** w Play Console przy upload AAB (checklist: `data_safety_form.md`)
4. **Wkleić Accessibility Declaration** w Play Console (tekst: `permissions_declarations.md`, sekcja 1)

---

## Jak zmieniać w przyszłości

- **Aktualizacja PP:** edytuj `legal/*.html`, zwiększ wersję w stopce („Wersja polityki: 1.0" → „1.1"), zaktualizuj datę, push → auto-deploy przez GitHub Pages.
- **Istotne zmiany PP** (np. zaczynasz zbierać dane): zmień Data Safety form w Play Console, wspomnij w release notes apki.
- **Zmiana emaila:** update we wszystkich 4 plikach HTML + w memory (`user_developer_profile.md`).

---

**Data utworzenia:** 2026-04-21
**Wersja dokumentu:** 1.0
