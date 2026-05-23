# GitHub Pages — instrukcja dla Krzysztofa

Ten dokument to krok-po-kroku jak włączyć GitHub Pages na repo `OrderPilot-App`, żeby pliki z folderu `legal/` były dostępne publicznie pod URL:

```
https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html
```

Ten dokładny URL jest już wkompilowany w kod apki (stałą `OrderPilotApp.PRIVACY_POLICY_URL`), więc **zachowaj nazewnictwo**.

---

## Wymaganie wstępne

- Pliki `legal/privacy-policy.html`, `legal/privacy-policy-en.html`, `legal/data-deletion.html`, `legal/index.html` są już w repo w folderze `legal/` na branchu `play-store-prep`.
- Zanim włączysz Pages, branch `play-store-prep` musi zostać **zmergowany do `main`** (albo zmień Pages source na `play-store-prep`, ale docelowo lepiej `main`).

---

## Krok 1 — Merge `play-store-prep` → `main` (jeśli jeszcze nie)

Zobaczysz jak będziemy gotowi do Closed Testing. Na razie: **pomiń**, możesz włączyć Pages bezpośrednio z `play-store-prep` jako tymczasowy source do testów.

---

## Krok 2 — Włącz GitHub Pages

1. Otwórz repo w przeglądarce: https://github.com/kris20032/OrderPilot-App
2. Kliknij zakładkę **Settings** (ostatnia z prawej w menu u góry repo).
3. W menu bocznym wybierz **Pages** (sekcja „Code and automation").
4. W polu **Source** wybierz **Deploy from a branch**.
5. W polu **Branch** wybierz:
   - Jeśli `play-store-prep` jeszcze nie zmergowany do `main` → wybierz **`play-store-prep`** + folder **`/ (root)`** → **Save**
   - Jeśli już po merge → wybierz **`main`** + folder **`/ (root)`** → **Save**
6. Poczekaj 1-3 minuty. Odśwież stronę.
7. Na górze sekcji Pages powinien pojawić się zielony checkmark i tekst:
   ```
   Your site is live at https://kris20032.github.io/OrderPilot-App/
   ```

---

## Krok 3 — Weryfikacja że działa

Otwórz w przeglądarce (w oknie incognito — żeby nie mylił cache):

| URL | Co powinno się pokazać |
|---|---|
| `https://kris20032.github.io/OrderPilot-App/legal/` | Landing z 3 kartami (PL PP, EN PP, Data Deletion) |
| `https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html` | Pełna polityka prywatności po polsku |
| `https://kris20032.github.io/OrderPilot-App/legal/privacy-policy-en.html` | Privacy Policy po angielsku |
| `https://kris20032.github.io/OrderPilot-App/legal/data-deletion.html` | Instrukcje usunięcia danych (PL + EN) |

Jeśli zobaczysz 404 — poczekaj 2-3 min i odśwież (GitHub Pages cache czasem ma opóźnienie przy pierwszym deployu).

---

## Krok 4 — Sprawdź link z apki

Zainstaluj najnowszy build apki na telefonie i:
1. Wejdź do **Settings** w apce.
2. Scrolluj do sekcji „O aplikacji".
3. Kliknij link **„Polityka Prywatności"**.
4. Powinien otworzyć przeglądarkę z polską wersją PP.

Jeśli toast „Nie można otworzyć linku" → coś z URL jest nie tak. Sprawdź czy URL w kodzie (`OrderPilotApp.PRIVACY_POLICY_URL` w `OrderPilotApp.kt`) zgadza się z żywym URL.

---

## Krok 5 — Custom domain (opcjonalne, na przyszłość)

Jeśli kiedyś kupisz domenę (np. `orderpilot.app`), możesz skonfigurować custom domain w Pages. Na razie `kris20032.github.io` wystarcza — Google akceptuje GitHub Pages jako hosting PP.

---

## Troubleshooting

### „Your site is live" ale 404 dla `/legal/privacy-policy.html`
- Sprawdź czy plik faktycznie istnieje w repo: https://github.com/kris20032/OrderPilot-App/blob/main/legal/privacy-policy.html
- Jeśli nie — musisz zrobić push (albo merge branch który zawiera `legal/`).

### „Pages build failure" email z GitHuba
- Zwykle problem z jakimś Jekyll markdown-processing. My używamy czystego HTML, więc niemożliwe, ale jeśli zobaczysz — dodaj plik `.nojekyll` do roota repo (pusty plik, wyłącza Jekyll processing).

### HTTPS nie działa / certyfikat
- GitHub Pages auto-provisioning HTTPS. Poczekaj 10-60 min po pierwszym deployu. Jeśli dalej problem — w sekcji Pages zaznacz „Enforce HTTPS".

---

## Co dalej po live deployment

1. ✅ Test URL w przeglądarce (krok 3)
2. ✅ Test w apce (krok 4)
3. Następny krok: Phase 5 — store assets (screenshoty, ikona, opis)

---

**Data utworzenia:** 2026-04-21
