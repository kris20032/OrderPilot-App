# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-15
**Obecny etap:** Bolt Food parser wdrożony — do przetestowania na prawdziwych zleceniach. Glovo parser przetestowany i działa. Ustawienia per platforma wdrożone.
**Aktywny branch:** `feature/bolt-parser` (bazuje na `feature/glovo-parser`)

---

## Faza POC — ZAKOŃCZONA

- [x] Etap 0: Android Studio + JDK 17 zainstalowane — 2026-02-24
- [x] Dokumentacja: RULES.md, README.md, ARCHITECTURE.md, PLAN.md — 2026-02-25
- [x] FakeUberDriver: aplikacja testowa symulująca popup Uber — 2026-02-26
- [x] **POC MediaProjection + OCR: belka pojawia się na telefonie przy popupie FakeUberDriver** — 2026-02-27

POC udowodnił że pipeline działa. Kod POC zostaje na `main` jako punkt odniesienia — NIE kontynuujemy go.

---

## Właściwa aplikacja — EPIC 1–14 UKOŃCZONE

| # | Etap | Status |
|---|------|--------|
| ✅ | Architektura (`docs/ARCHITECTURE.md`) | Gotowa (2026-02-27) |
| ✅ | Plan implementacji (`docs/PLAN.md` v2) | Zatwierdzony (2026-03-03) |
| ✅ | EPIC 1: Fundament (Gradle, Manifest, DI, Logger) | Ukończony (2026-03-04) |
| ✅ | EPIC 2: Domain (modele danych) | Ukończony (2026-03-04) |
| ✅ | EPIC 3: Settings (ustawienia + repo) | Ukończony (2026-03-04) |
| ✅ | EPIC 4: Engine (analiza + filtrowanie) | Ukończony (2026-03-04) |
| ✅ | EPIC 5: Parser (OCR parser Uber) | Ukończony (2026-03-04) |
| ✅ | EPIC 6: Capture (MediaProjection) | Ukończony (2026-03-04) |
| ✅ | EPIC 7: OCR (ML Kit wrapper) | Ukończony (2026-03-04) |
| ✅ | EPIC 8: Overlay (belka) | Ukończony (2026-03-04) |
| ✅ | EPIC 9: Pipeline (orkiestracja) | Ukończony (2026-03-04) |
| ✅ | EPIC 10: Service (AccessibilityService) | Ukończony (2026-03-04) |
| ✅ | EPIC 11: UI — MainActivity | Ukończony (2026-03-04) |
| ✅ | EPIC 12: UI — SettingsActivity | Ukończony (2026-03-04) |
| ✅ | EPIC 13: Billing stub + weryfikacja DI | Ukończony (2026-03-04) |
| ✅ | EPIC 14: Testy E2E + polish | Ukończony (2026-03-05) |

Pełny plan: `docs/PLAN.md` (14 epiców, 40 tasków)

---

## Bugfixy i zadania po testach na fizycznym telefonie

Ojciec testował aplikację na fizycznym telefonie (2026-03-06/07). Zgłosił 5 zadań Jira (KAN-11 do KAN-15) i 1 krytyczny bug (wygaszanie ekranu).

### Ukończone (2026-03-08)

| Zadanie | Opis | Efekt | Data | Branch |
|---------|------|-------|------|--------|
| Fix krytyczny: wygaszanie ekranu | App przestawała działać po wygaszeniu ekranu (ScreenCaptureService ginął) | WakeLock trzyma serwis przy życiu. Flaga `isProjectionLost` + powiadomienie pozwala wznowić bez restartu app | 2026-03-08 | `fix/screen-off-survival` → merged |
| Optymalizacja latencji pipeline | Belka pojawiała się z opóźnieniem ~2s | firstShotDelay 300→100ms, captureDelay 200→100ms, cooldown 5s→3s. Wynik: ~1.3s (bottleneck: ML Kit OCR ~700ms) | 2026-03-08 | `fix/screen-off-survival` → merged |
| KAN-14: odświeżanie belki | Belka nie aktualizowała się przy nowym zamówieniu gdy poprzednia była widoczna | Usunięto guard `isShowing()` z PipelineOrchestrator — `show()` już wywołuje `hide()` wewnętrznie | 2026-03-08 | `fix/kan-14-overlay-refresh` → merged |
| Bug: START + accessibility | START wymagał 2 kliknięć (race condition onResume/onActivityResult) | `pendingStart` flaga blokuje nadpisanie stanu przez onResume | 2026-03-08 | `fix/start-button-race-condition` → merged |
| KAN-12 | Dark mode — belka nie wyświetla się poprawnie | `forceDarkAllowed="false"` + jawny `setTextColor(Color.WHITE)` | 2026-03-08 | `feature/production-app` |
| KAN-11 | Dialog MediaProjection mylący | Toast wyjaśniający przed dialogiem | 2026-03-08 | `feature/production-app` |
| KAN-13 + KAN-15 | Suwaki przezroczystości i czasu wyświetlania | `overlayOpacity` i `displayTimeSeconds` w DisplayConfig | 2026-03-08 | `feature/production-app` |

### takeScreenshot fallback — UKOŃCZONE 2026-03-11

Szczegóły: sekcja archiwalna w historii git.

### Dual-mode accessibility fallback — UKOŃCZONE 2026-03-10

Szczegóły: sekcja archiwalna w historii git.

### WoltOcrParser — UKOŃCZONE 2026-03-13

| Zadanie | Opis | Status | Branch |
|---------|------|--------|--------|
| WoltOcrParser.kt | Parser Wolta: zakres czasu (MAX), dystans bez nawiasów | ✅ 15/15 testów | `feature/wolt-parser` |
| Testy na telefonie | 13 zł / 26 min / 2.7 km → 30 zł/h → RED, belka OK | ✅ Zweryfikowane | `feature/wolt-parser` |
| Merge do production-app | Zmerge'owany | ✅ 2026-03-13 | `feature/wolt-parser` |

---

## Sesja 2026-03-14/15: GlovoOcrParser + poprawki

### Zmiany wdrożone na `feature/glovo-parser`

| Zadanie | Opis | Commit |
|---------|------|--------|
| Korekta kwot OCR | `sanitizeAmount()`: 4+ cyfry bez separatora → /100 (1720→17.20). Sanity: <3 zł odrzuć, >150 zł warning. Wszystkie parsery używają tej funkcji. | `0ad50e6` |
| Przycisk "Zapisz logi" | Przycisk w MainActivity — zapisuje logi z ring buffera do Downloads. | `0ad50e6` |
| GlovoOcrParser v1 | Szkielet parsera Glovo: kwota + dystanse, partial offers (przed scrollem), per-platform throttler. | `8debce4` |
| Fix: zaokrąglanie progu zł/h | OfferAnalyzer porównuje zaokrągloną wartość z progiem (33.86→34 ≥ 34 = YELLOW, nie RED). | `50dc991` |
| Fix: zawijanie tekstu belki | `maxLines=2` + non-breaking spaces — tekst się zawija zamiast ucinać, `16 min` nie rozdziela się między linie. | `50dc991` |
| Fix: ring buffer logów | AppLog buforuje 500 wpisów w pamięci. `saveLogs()` pisze z bufora zamiast z `logcat` (który nie działał na Android 13+). | `6e93281` |
| Fix: GlovoOcrParser v2 | Największa kwota zł (eliminuje fałszywe odczyty z mapy Google) + dwa najmniejsze dystanse < 20 km (pickup + delivery). | `6e93281` |
| Fix: obsługa godzin | `hourRegex` w Uber i Wolt parserach: "1 godz. 3 min" → 63 min (zamiast 3 min). | `8690989` |
| Fix: świeże uruchomienie = Inactive | Po otwarciu apki (nie powrót z tła) stan = Inactive, wymaga kliknięcia START. | `781c5fc` |
| Ustawienia per platforma | TabLayout: Global / Uber / Wolt / Glovo / Bolt — osobne progi zł/h, zł/km i czas belki per platforma. Puste pola = dziedziczą z Global. | `ae34862` |

### BoltFoodOcrParser — wdrożony 2026-03-15

| Zadanie | Opis | Status | Branch |
|---------|------|--------|--------|
| BoltFoodOcrParser.kt | Parser Bolt Food: kwota (max), czas (max = suma z przycisku), dystans (max = suma). Obsługuje format przycisku `2.2 km, 27 min, 8,22 zł`. | ✅ Gotowy | `feature/bolt-parser` |
| ServiceLocator | BoltFoodOcrParser zarejestrowany w ParserRegistry | ✅ | `feature/bolt-parser` |
| CourierAccessibilityService | Bolt dodany do ścieżki accessibility tree (obok Glovo) | ✅ | `feature/bolt-parser` |
| Pakiety: `com.bolt.courier`, `com.bolt.food.courier`, `ee.mtakso.courier` | Obsługiwane warianty pakietów Bolt | ✅ | `feature/bolt-parser` |
| Testy na telefonie | Do przetestowania na prawdziwych zleceniach Bolt Food | ⏳ Czeka | - |

**Logika parsera:**
- Bolt Food pokazuje pickup i delivery osobno: `~0.9 km, ~16 min` + `~1.3 km, ~12 min`
- Na przycisku akceptuj jest suma: `2.2 km, 27 min, 8,22 zł`
- Parser bierze: **największą kwotę** (suma z przycisku), **największy czas** (suma), **największy dystans** (suma)
- Accessibility tree widzi cały UI → parser od razu ma dane z przycisku

### Wyniki testów Glovo na prawdziwych zleceniach (2026-03-15)

| Zlecenie | Popup | Belka | Wynik |
|----------|-------|-------|-------|
| 18,15 zł / Starbucks | 18,15 zł + Dodatkowo 4,71 zł, pickup 1,26 km, delivery 0,78 km | `8,9 zł/km \| 18,15 zł \| 2,0 km` → GREEN | ✅ Poprawne. Parser wziął max kwotę (18,15 > 4,71). Dystans: 1,26 + 0,78 = 2,04 ≈ 2,0 km. |
| 9,71 zł / KFC (przed fixem v2) | 9,71 zł, pickup 0,28 km | Belka skakała 3 razy (17,3→0,9→3,1 zł/km) | ❌ Parser łapał fałszywe dane z mapy Google. Naprawione w v2. |
| 7,50 zł / Pasibus | 7,50 zł, pickup 0,16 km, delivery 0,83 km | `7,6 zł/km \| 7,50 zł \| 1,0 km` → GREEN | ✅ Od razu widzi oba dystanse (accessibility tree zwraca dane spoza widocznego ekranu). |

### Kluczowe odkrycia (Glovo)

1. **Accessibility tree widzi dane spoza ekranu** — `getRootInActiveWindow()` zwraca CAŁE drzewo UI, nie tylko widoczną część. Parser od razu ma pickup + delivery bez czekania na scroll.
2. **Mapa Google wbudowana w Glovo emituje tekst do accessibility tree** — nazwy ulic, dystanse, numery dróg. Fix: parser bierze największą kwotę zł i dwa najmniejsze dystanse.
3. **Glovo nie podaje czasu** — analiza po zł/km zamiast zł/h. Osobne progi w ustawieniach.
4. **"Dodatkowo X zł"** — napiwek/bonus jest wliczony w kwotę główną. Parser ignoruje mniejszą kwotę (bierze max).

### Testy do przeprowadzenia

| Test | Opis | Status |
|------|------|--------|
| Glovo po fixie v2 | Sprawdzić czy belka nie skacze po naprawie parsera (największa kwota + najmniejsze dystanse) | ⏳ Czeka na build |
| Ring buffer logów | Kliknięcie "Zapisz logi" → plik w Downloads z wpisami CA_* | ⏳ Czeka na build |
| Uber — regresja | Sprawdzić czy Uber nadal działa po zmianach (hourRegex, zaokrąglanie progu) | ⏳ Czeka na build |
| Wolt — regresja | Sprawdzić czy Wolt nadal działa | ⏳ Czeka |
| Świeże uruchomienie = Inactive | Po otwarciu apki stan Inactive, kliknięcie START → Active | ⏳ Czeka na build |
| Ustawienia per platforma | Tabs w SettingsActivity, zapis/odczyt progów per platforma | ⏳ Czeka na build |
| Crash na starszym telefonie | SettingsActivity crash — do zbadania po ustabilizowaniu | ⏳ Niski priorytet |

### Otwarte zadania

| Problem | Rozwiązanie | Status | Priorytet |
|---------|-------------|--------|-----------|
| Bolt Food parser — weryfikacja | Tata testuje na prawdziwych zleceniach Bolt Food | ⏳ Czeka na build | High |
| Glovo po fixie v2 — weryfikacja | Tata testuje po przebudowie z najnowszego glovo-parser | ⏳ Czeka | High |
| Crash na starszym telefonie (brat) | SettingsActivity crash — do zbadania | ⏳ Do zbadania | Medium |
| Mruganie belki Uber jasny→ciemny | Deduplikacja lastResult — monitorujemy | Monitorowane | Low |
| Brak battery optimization | Setup wizard | Do implementacji | Low |

---

## Aktywne branche

| Branch | Cel | Status | Last Commit |
|--------|-----|--------|-------------|
| `feature/bolt-parser` | Bolt Food parser | ✅ Aktywny na GitHub | (2026-03-15) |
| `feature/glovo-parser` | Glovo parser + poprawki pipeline + ustawienia per platforma | ✅ Na GitHub | `c7f7bab` (2026-03-15) |
| `feature/production-app` | Główny branch produkcyjny | ✅ Na GitHub | 8a9109c (2026-03-10) |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu | 285c209 |

> Workflow: nowe zadanie → nowy branch `fix/...` lub `feature/...` → testuj na telefonie → merge do `feature/production-app`

## Archiwalne branche

| Branch | Cel | Notatka |
|--------|-----|---------|
| `feature/wolt-parser` | WoltOcrParser | Zmerge'owany do production-app 2026-03-13 |
| `feature/accessibility-fallback` | Dual-mode fallback | Zmerge'owany do production-app 2026-03-10 |
| `feature/ui-redesign` | UI redesign + wielojęzyczność | Zmerge'owany 2026-03-09 |
| `fix/screen-off-survival` | Fix wygaszania + latencja | Zmerge'owany 2026-03-08 |
| `feature/fake-uber-driver` | Aplikacja testowa | Gotowa |
| `lukasz` | POC pipeline | Zmerge'owany do main |

---

## Co dalej — Priorytet

1. **Bolt Food parser** — nowy branch, screen od użytkownika → parser → testy
2. **Weryfikacja Glovo v2 + ustawień per platforma** — tata buduje najnowszy APK i testuje
3. **Crash na starszym telefonie** — zbadać po ustabilizowaniu
4. **Merge feature/glovo-parser → feature/production-app** — po potwierdzeniu stabilności

Plan w `docs/PLAN.md`.
