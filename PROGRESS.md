# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-17
**Obecny etap:** Testy produkcyjne u taty. Glovo/Uber fixy wdrożone. Multi-overlay (2 belki naraz) gotowe na branchu `feature/multi-overlay`. Bolt Food parser do weryfikacji.
**Aktywne branche:** `feature/bolt-parser` (fixy), `feature/multi-overlay` (2 belki naraz)

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
5. **Zlecenia z gotówką — "ODBIERZ 65,41 zł"** — przy zleceniach gotówkowych Glovo pokazuje kwotę do odebrania od klienta (dużo wyższą niż wynagrodzenie). Parser filtruje kwoty poprzedzone słowem "ODBIERZ" i bierze tylko wynagrodzenie kuriera.
6. **Partial offer (1 dystans)** — jeśli tree załadował tylko pickup bez delivery, parser zwraca null i czeka na kolejny event z pełnymi danymi. Zapobiega pokazywaniu zawyżonego zł/km.

### Testy do przeprowadzenia

| Test | Opis | Status |
|------|------|--------|
| Glovo po fixach (gotówka + partial + suma dystansów) | Sprawdzić czy belka prawidłowo liczy zł/km przy gotówce i wielopunktowych zleceniach | ⏳ Czeka na build |
| Uber — ekran statystyk | Sprawdzić czy belka NIE pojawia się na ekranie Podsumowanie/Statystyki | ⏳ Czeka na build |
| Bolt Food | Testy na prawdziwych zleceniach | ⏳ Czeka na zlecenie |
| Ring buffer logów | Kliknięcie "Zapisz logi" → plik w Downloads z wpisami CA_* | ✅ Działa (tata zapisał logi) |
| Uber — regresja | Sprawdzić czy Uber nadal działa po zmianach (hourRegex, zaokrąglanie progu) | ✅ Działa |
| Wolt — regresja | Sprawdzić czy Wolt nadal działa | ⏳ Czeka na zlecenie |
| Świeże uruchomienie = Inactive | Po otwarciu apki stan Inactive, kliknięcie START → Active | ⏳ Czeka na build |
| Ustawienia per platforma | Tabs w SettingsActivity, zapis/odczyt progów per platforma | ⏳ Czeka na build |
| Crash na starszym telefonie | SettingsActivity crash — do zbadania po ustabilizowaniu | ⏳ Niski priorytet |

### Poprawki 2026-03-16/17

| Zadanie | Opis | Commit |
|---------|------|--------|
| Fix: partial offer Glovo | Parser zwraca null przy 1 dystansie — czeka na pełne dane (pickup + delivery) zamiast pokazywać zawyżony zł/km | `7b744f9` |
| Fix: gotówka Glovo | Parser ignoruje "ODBIERZ X zł" (kwota klienta) — bierze tylko wynagrodzenie kuriera | `fb15ac9` |
| Fix: Glovo sumuje WSZYSTKIE dystanse | Zmiana z 2 najmniejszych na sumę wszystkich dystansów — obsługuje wielopunktowe zlecenia (2 restauracje + 2 dostawy) | `05fdf73` |
| Fix: Uber odrzuca ekran statystyk | Parser odrzuca oferty z czasem > 180 min — zapobiega fałszywym belkom na ekranie "Podsumowanie" (324 zł / 2575 min) | `9a01794` |
| Fix: domyślne ustawienia | Po instalacji: 30s belka (zamiast 40s) + wszystkie metryki widoczne (zamiast tylko zł/h) | `86fccd4` |

### Wyniki testów Glovo (2026-03-16/17)

| Zlecenie | Wynik | Uwagi |
|----------|-------|-------|
| 18,29 zł / Pizzeria 105 | ✅ Poprawne po scrollu | Accessibility tree nie miał delivery od razu — po fix partial offer parser czekał na pełne dane |
| 25,38 zł / TARGOWA + Kebab King (3 dystanse) | Belka: 13,7 zł/km 39 zł 2,8 km | ⚠️ Wziął gotówkę (39 zł) zamiast wynagrodzenia (25,38 zł) — fix ODBIERZ jeszcze nie był na tym buildzie. Dystanse: parser wziął 2 najmniejsze zamiast wszystkich — naprawione (sumuje wszystkie). |
| 12,54 zł / Biedronka (gotówka 65,41 zł) | Belka: 40,9 zł/km 65,41 zł 1,6 km | ❌ Parser wziął gotówkę klienta — naprawione (filtr ODBIERZ) |

### Wynik testu Uber — ekran statystyk (2026-03-16)

| Problem | Co się stało | Fix |
|---------|-------------|-----|
| Ekran "Podsumowanie" w Uber | Parser odczytał 324,93 zł + 42 godz. 55 min jako zlecenie → belka 8 zł/h RED | Filtr: `minutes > 180` → odrzuć (ekran statystyk, nie zlecenie) |

### Kluczowe odkrycia (2026-03-16/17)

7. **Accessibility tree nie zawsze widzi delivery od razu** — przy niektórych popupach Glovo (zależy od rozmiaru) delivery distance pojawia się w drzewie z opóźnieniem (API Glovo ładuje asynchronicznie). Parser czeka na min. 2 dystanse.
8. **Glovo może mieć 3+ dystanse** — zlecenia wielopunktowe (2 restauracje + 2 dostawy). Parser sumuje WSZYSTKIE dystanse.
9. **"ODBIERZ X zł" = gotówka klienta** — dużo wyższa niż wynagrodzenie kuriera. Parser filtruje kwoty z prefixem "ODBIERZ".
10. **Uber ekran statystyk wygląda jak zlecenie** — ma kwotę (324,93 zł) i czas (42 godz. 55 min). Parser odrzuca > 180 min.

### Multi-overlay — 2 belki naraz (branch `feature/multi-overlay`, 2026-03-17)

| Zadanie | Opis | Status |
|---------|------|--------|
| SystemOverlayManager | Multi-slot: max 2 belki, najnowsza na górze, pozycjonowanie slotów | ✅ |
| OverlayAutoHider | Osobne timery per platforma (mapa `hideJobs`) | ✅ |
| OverlayViewFactory | Etykieta platformy (UBER/WOLT/GLOVO/BOLT) gdy 2 belki naraz | ✅ |
| OverlayManager interface | Nowe metody: `hideByPlatform(platform)`, `overlayCount()` | ✅ |
| CourierAccessibilityService | Przekazanie `offer.platform` do `onOverlayShown()` (tree + screenshot path) | ✅ |
| PipelineOrchestrator | Przekazanie `result.offer.platform` do `onOverlayShown()` | ✅ |
| Testy na telefonie | Do przetestowania na prawdziwych zleceniach z 2 platform | ⏳ Czeka na build |

**Logika multi-overlay:**
- 1 belka → bez etykiety (jak dotychczas)
- 2 belki z różnych platform → obie z etykietą (UBER/WOLT/GLOVO/BOLT)
- 3. zlecenie → najstarsza belka znika, nowa na górze
- Przycisk × ukrywa konkretną belkę, druga zostaje (bez etykiety)
- Osobne timery — każda belka znika niezależnie po swoim czasie

### Otwarte zadania

| Problem | Rozwiązanie | Status | Priorytet |
|---------|-------------|--------|-----------|
| Multi-overlay — testy | Tata testuje 2 belki naraz z różnych platform | ⏳ Czeka na build | High |
| Bolt Food parser — weryfikacja | Tata testuje na prawdziwych zleceniach Bolt Food | ⏳ Czeka na zlecenie | High |
| Glovo — weryfikacja po fixach | Tata testuje: gotówka, partial offer, sumowanie dystansów | ⏳ Czeka na build | High |
| Crash na starszym telefonie (brat) | SettingsActivity crash — do zbadania | ⏳ Do zbadania | Medium |
| Mruganie belki Uber jasny→ciemny | Deduplikacja lastResult — monitorujemy | Monitorowane | Low |
| UI/UX polish | Dopracowanie wyglądu aplikacji — lepsze layouty, kolory, animacje, ogólna estetyka | Do implementacji | Medium |
| Przesuwana belka + mini overlay | Belka draggable (użytkownik ustawia pozycję) + mały stały overlay do szybkiego dostosowania (np. przesunięcie, ustawienia) | Do implementacji | Medium |
| Setup wizard + battery optimization | Kreator pierwszego uruchomienia + `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` + instrukcja Samsung. Dostępny też z poziomu ustawień (sprawdzenie statusu uprawnień w dowolnym momencie) | Do implementacji | Medium |

---

## Aktywne branche

| Branch | Cel | Status | Last Commit |
|--------|-----|--------|-------------|
| `feature/multi-overlay` | 2 belki naraz z różnych platform | ✅ Aktywny na GitHub | `6cf4419` (2026-03-17) |
| `feature/bolt-parser` | Bolt Food parser + wszystkie fixy | ✅ Na GitHub | `86fccd4` (2026-03-17) |
| `feature/glovo-parser` | Glovo parser + poprawki pipeline + ustawienia per platforma | ✅ Na GitHub | `73226e0` (2026-03-17) |
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

1. **Multi-overlay testy** — zbudować APK z `feature/multi-overlay`, przetestować 2 belki naraz
2. **Weryfikacja Glovo po fixach** — gotówka, wielopunktowe zlecenia, partial offer
3. **Weryfikacja Uber** — ekran statystyk nie powinien triggerować belki
4. **Bolt Food parser** — testy na prawdziwych zleceniach
5. **Crash na starszym telefonie** — zbadać po ustabilizowaniu
6. **UI/UX polish** — dopracowanie wyglądu (MainActivity, SettingsActivity, belka, ogólna estetyka)
7. **Setup wizard** — kreator pierwszego uruchomienia z uprawnieniami + battery optimization
8. **Merge feature/multi-overlay → feature/production-app** — po potwierdzeniu stabilności

Plan w `docs/PLAN.md`.
