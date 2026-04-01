# CourierAssist — Locale-Sensitive Bugs

**Data audytu:** 2026-04-01

---

## 1. `.lowercase()` bez `Locale.ROOT`

Kotlin `.lowercase()` bez parametru uzywaja domyslnego locale JVM. Na urzadzeniach z Turkish locale (`tr_TR`) litera `I` zamienia sie na `ı` (dotless i) zamiast `i`. Moze zepsuc porownania stringow.

| Plik | Linia | Kod | Ryzyko |
|------|-------|-----|--------|
| `SetupActivity.kt` | 70 | `Build.MANUFACTURER.lowercase()` | "XIAOMI" → "xıaomı" ≠ "xiaomi" na Turkish locale |
| `MainActivity.kt` | 215 | `enabledServices.lowercase().contains("courierassist")` | "COURIERASSIST" → "courıerassıst" ≠ "courierassist" |

**Fix:** Zamienic na `.lowercase(Locale.ROOT)` + dodac `import java.util.Locale`.

**Praktyczne ryzyko:** Niskie — brak tureckich uzytkownikow. Ale fix jest trywialny.

---

## 2. `String.format()` bez explicit Locale

| Plik | Linia | Kod | Efekt |
|------|-------|-----|-------|
| `SettingsActivity.kt` | 278 | `String.format("%.1f", value)` | Na PL/UK locale: "4,5" zamiast "4.5" |

**Kontekst:** `formatThreshold()` formatuje progi zl/h do wyswietlenia w UI. Wartosc jest czysto wyswietlana (nie parsowana z powrotem), wiec przecinek jest poprawny dla PL/UK uzytkownikow.

**Fix opcjonalny:** `String.format(Locale.US, "%.1f", value)` jesli chcemy spojny format.

---

## 3. OverlayViewFactory `.format()` — locale-dependent decimal separator

| Plik | Linie | Kod |
|------|-------|-----|
| `OverlayViewFactory.kt` | 26 | `"%.0f".format(result.zlPerHour)` |
| `OverlayViewFactory.kt` | 28 | `"%.1f".format(result.zlPerKm)` |
| `OverlayViewFactory.kt` | 30 | `"%.2f".format(result.offer.amount)` |
| `OverlayViewFactory.kt` | 34 | `"%.1f".format(result.offer.distanceKm)` |

**Efekt:** Na PL locale: "34,50 zl/h" (z przecinkiem). Na EN locale: "34.50 PLN/h" (z kropka).

**Decyzja:** To jest **poprawne zachowanie** — PL/UK uzytkownicy oczekuja przecinka, EN oczekuje kropki. Default locale dziala prawidlowo. **Nie wymaga zmiany.**

---

## 4. Hardcoded currency `"zl"` w 3 parserach

Tylko `UberOcrParser` wywoluje `OcrOfferParser.detectCurrency(text)`. Pozostale 3 parsery hardcoduja walute:

| Parser | Linia | Kod |
|--------|-------|-----|
| `WoltOcrParser.kt` | 72 | `Offer(Platform.WOLT, amount, minutes, distance, "zł")` |
| `GlovoOcrParser.kt` | 108 | `currency = "zł"` |
| `BoltFoodOcrParser.kt` | 80 | `Offer(Platform.BOLT, amount, minutes, distance, "zł")` |

**Efekt:** Ukrainscy kurierzy widza "zl" nawet gdyby platforma pokazywala ceny w hrywniach.

**Praktyczne ryzyko:** Zerowe w Polsce (wszystko PLN). Ale fix jest trywialny: zamienic na `OcrOfferParser.detectCurrency(text)`.

---

## 5. `Locale.setDefault()` w LocaleHelper

| Plik | Linia | Kod |
|------|-------|-----|
| `LocaleHelper.kt` | 16 | `Locale.setDefault(locale)` |

**Kontekst:** Ustawia globalny default locale JVM. Wplywa na `String.format()`, `.lowercase()`, `SimpleDateFormat` we wszystkich watkach.

**Ryzyko:** Zmiana jezyka aplikacji zmienia tez default locale dla calego procesu, wlacznie z background threads. Moze powodowac niespojne formatowanie jesli watki nie ustawia locale explicite.

**Obecny wplyw:** Minimalny — background threads (OCR pipeline, retry) nie uzywaja locale-sensitive formatowania. Ale warto miec swiadomosc.

---

## Podsumowanie priorytetow

| Bug | Priorytet | Trudnosc |
|-----|-----------|----------|
| `.lowercase()` bez Locale.ROOT | Niski (brak Turkish users) | Trywialny |
| `String.format` w SettingsActivity | Kosmetyczny | Trywialny |
| Hardcoded currency w 3 parserach | Niski (Polska = PLN) | Trywialny |
| `Locale.setDefault()` side effects | Informacyjny | N/A |
| OverlayViewFactory `.format()` | Brak buga | N/A |
