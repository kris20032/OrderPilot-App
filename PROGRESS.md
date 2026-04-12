# OrderPilot — Status Postępu

**Ostatnia aktualizacja:** 2026-04-12
**Obecny etap:** Oznaczenie gotówki na belce (💵) zaimplementowane (04-12) — branch `fix/phantom-overlay-guard`, czeka na build + test.
**Aktywne branche:** `fix/phantom-overlay-guard` (bieżący), `feature/production-app` (stable base), `main` (zsynchronizowany)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| ~~**High**~~ | ~~Build + test `fix/state-refactor` u taty~~ | ✅ 2 dni bez błędów (04-09/10) |
| ~~**High**~~ | ~~Merge `fix/state-refactor` → `feature/production-app` → `main`~~ | ✅ Zmergowany (04-10) |
| ~~**High**~~ | ~~Fix phantom overlay Xiaomi — false positive belka w Google Maps~~ | ✅ 2 zmiany (04-10), czeka na test produkcyjny |
| ~~**High**~~ | ~~Skip MediaProjection na API 30+ — uproszczony start~~ | ✅ Zaimplementowane (04-10), build OK |
| ~~**High**~~ | ~~Język rosyjski (UI + parsery + rival markers)~~ | ✅ Zaimplementowany (04-11), czeka na build |
| ~~**High**~~ | ~~Audyt niezawodności + 4 HIGH fixy (boot/watchdog/delay/notif)~~ | ✅ Zaimplementowane (04-11), czeka na test |
| **High** | Weryfikacja Glovo na Xiaomi — tata nie zalogowany | Czeka na test |
| **High** | Budowanie APK release do dystrybucji beta | Następny krok |
| Medium | Crash na starszym telefonie (brat) — SettingsActivity | Nie odtworzony po reinstalacji (03-25), monitorowane |

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `fix/phantom-overlay-guard` | Fix false positive — phantom overlay Xiaomi | **BIEŻĄCY** — gotowy do testu |
| `feature/production-app` | Główny branch produkcyjny (stable) | Base branch |
| `main` | Stabilna baza — zsynchronizowany z production | Aktualny |
| ~~`fix/state-refactor`~~ | ~~MonitoringController refactor + defensive fixy~~ | ✅ Zmergowany (04-10) |

> Workflow: nowy branch → testuj → merge do `feature/production-app`

---

## Co dalej — Roadmap

### Faza 1: Stabilizacja — ZAKOŃCZONA (03-27)
1. ~~Testy produkcyjne u taty~~ ✅ Zaliczone (03-27, Xiaomi)
2. ~~Bolt Food — retest~~ ✅ 4/4 zlecenia (03-26)
3. ~~Setup wizard per producent~~ ✅ Gotowe (03-25)
4. ~~Merge `feature/xiaomi-testing` → `feature/production-app`~~ ✅ (03-27)

### Faza 1.5: Fixy z dalszych testów — ZAKOŃCZONA (04-10)
5. ~~Universal extractAmount()~~ ✅ (03-29)
6. ~~Uber adaptive polling~~ → spaced retries + watch mode ✅ (03-30)
7. ~~Samsung missed events~~ → TYPE_WINDOWS_CHANGED + false trigger filter ✅ (03-31)
8. ~~Audyt kodu v1+v2~~ → 15 fixów (memory leaki, crash guards, thread safety, porządki) ✅ (03-31)
9. ~~State refactor + defensive fixy + timeouty~~ ✅ (04-08/10)
10. ~~Merge `fix/state-refactor` → `feature/production-app` → `main`~~ ✅ (04-10)

### Faza 1.6: Ulepszenia przed beta (teraz)
11. ~~Skip MediaProjection na API 30+~~ ✅ (04-10) — takeScreenshot() jedyna ścieżka, brak dialogu, brak foreground service
12. ~~Język rosyjski~~ ✅ (04-11) — AppLanguage.RU, values-ru/strings.xml, rival markers RU we wszystkich parserach, filtr gotówkowy RU w Glovo, overlay units ч/км/мин
13. ~~Audyt niezawodności + HIGH fixy~~ ✅ (04-11) — BootReceiver, watchdog race guard, health-check 2500ms, notification permission UI hint
14. ~~6 fixów z testów produkcyjnych~~ ✅ (04-12) — overlay units per waluta (nie per język UI), OCR digit normalization relaxed, guard statisticsScreen, setup battery button, overlay × kółko, wizard toast "Zainstalowane aplikacje"
15. ~~Oznaczenie gotówki na belce~~ ✅ (04-12) — `isCash` w Offer, detekcja Glovo (per-amount + fallback), generyczne markery Wolt/Bolt, 💵 emoji na belce

### Faza 2: Przygotowanie do beta testów
8. Budowanie APK release (signed) do dystrybucji
9. Glovo — weryfikacja na Xiaomi (tata nie był zalogowany)

### Faza 3: Beta testy u zewnętrznych kurierów
10. Znaleźć 3-5 kurierów na mieście (mix platform + modeli telefonów)
11. Instalacja apki + konfiguracja na miejscu
12. Zbieranie feedbacku przez WhatsApp/Telegram

---

## Ostatnie zmiany

| Data | Zmiana |
|------|--------|
| 04-12 | **Oznaczenie gotówki na belce** — `isCash: Boolean` w Offer, detekcja: Glovo (per-amount prefix + containsCashMarkers fallback), Wolt/Bolt (generyczne markery PL/EN/UK/RU). 💵 emoji na końcu belki. isSameAsPrevious uwzględnia isCash. Testy: 4 w GlovoOcrParserTest + 10 w OcrOfferParserTest. |
| 04-12 | **6 fixów z testów produkcyjnych** — (1) Overlay units zależą od waluty zlecenia nie języka UI (zł→h/km/min zawsze), (2) OCR digit normalization relaxed (Z→7, O→0, L→1 fix PLN1Z.28), (3) Guard statisticsScreenMarkers w UberOcrParser, (4) Setup: przycisk "Zezwól na działanie w tle" + ukrywanie po zaliczeniu, (5) Overlay × jako kółko w odcieniu belki, (6) Wizard toast z krokiem "Zainstalowane aplikacje". |
| 04-11 | **Audyt niezawodności** — 7 problemów zidentyfikowanych (4 HIGH, 3 MEDIUM). 4 HIGH fixy: BootReceiver (wznowienie po reboot), watchdog race guard (initialize() w doWork()), health-check delay 500→2500ms (false stop po Doze), notification permission UI hint (persistent banner gdy denied). |
| 04-11 | **Język rosyjski** — AppLanguage.RU, values-ru/strings.xml (109 stringów), rival markers RU we wszystkich 4 parserach (synonimy per concept), filtr gotówkowy RU w GlovoOcrParser (prefix "наличн"), overlay units ч/км/мин. Punkt #10 (usunięcie MediaProjection) zamknięty jako nieaktualny. Punkt #17 (wskaźnik akceptacji) dodany do future_polish_fixes. |
| 04-10 | **Skip MediaProjection na API 30+** — na Android 11+ apka pomija dialog MediaProjection i foreground service. Screenshoty przez AccessibilityService.takeScreenshot(). Consecutive failure counter (alert po 10 porażkach) jako safety net. future_polish_fixes: punkt 9 resolved, punkt 7 z uwagą o ryzyku. |
| 04-10 | **Fix phantom overlay Xiaomi** — watch mode guard: `hasUberOverlayWindow()` → `hasUberOverlayWithContent()` (linia 481). Phantom overlay Xiaomi (type=3, pusty) nie przepuszcza już periodic screenshot bez realnego popupu. Bonus: fallback amount regex `\d+` → `\d{1,2}` po separatorze — blokuje GPS coords jako false kwoty. |
| 04-10 | **Merge `fix/state-refactor` → `feature/production-app` → `main`** — 2 dni testów produkcyjnych (tata) bez błędów. Defensive fixy: OCR timeout 5s, pipeline timeout 10s, health-check AccessibilityService w MainActivity (toast gdy OEM kill), odrzucanie ambiguous 3-cyfrowych kwot w parserze. |
| 04-09 | **Fix retry loop — isActive() guard** — audyt concurrency i STOP logiki. Dodano `if (!MonitoringController.isActive()) break` w obu retry pętlach (throttler callback + WINDOWS_CHANGED). Po STOP retries przerywają się natychmiast zamiast robić zbędne screenshoty przez 2.4s. |
| 04-09 | **Audyt crash & lifecycle** — 3 subagenty + ręczna weryfikacja. Większość "critical" to false positives. 3 defensive fixy: WakeLock timeout 4h, MonitoringController.start() po potwierdzeniu MediaProjection, CopyOnWriteArrayList w listeners. App bezpieczna do release (crash-wise). |
| 04-08 | **3 bugi taty + fix logowania** — OcrOfferParser, UberOcrParser, PipelineOrchestrator, MainActivity, AppLog |
| 04-08 | **State refactor (MonitoringController)** — zastąpienie `@Volatile isUserStopped` jednym persystowanym source of truth. `MonitoringController` object z `start()/stop()/isActive()`. 3-warstwowa gwarancja Stop. `onTaskRemoved()` NIE zmienia stanu. Waluta dynamiczna z `Offer.currency`. |
| 04-04 | **Rename CourierAssist → OrderPilot** — package `com.orderpilot.app`, klasy (OrderPilotApp, OrderPilotAccessibilityService), moduł `OrderPilot/`, SharedPrefs key, log tagi CA_→OP_, strings.xml (3 locale), themes.xml, docs, memory files. Zero pozostałości. |
| 04-03 | **Parser false positives** — usunięty regex `z` z CUR, fix backtracking AMOUNT_FALLBACK_REGEX, guard historyScreenMarkers w UberOcrParser, normalizeOcrDigits() przed parsowaniem czasu |
| 04-03 | **Bolt watch mode** — tree-based periodic read co 2.5s (Bolt nie generuje accessibility eventów przy popupie oferty) |
| 03-31 | Audyt kodu v2: synchronized w ScreenCaptureService, @Volatile na uberWatchJob, PopupCropper bounds check, OfferDuplicateChecker (wspólna logika), named constants (RETRY_DELAY_MS itp.) |
| 03-31 | Audyt kodu v1: 5x try-finally (bitmap/node recycle), pipeline.isInitialized guard, image.planes guard, Glovo distance cap 20→50km, @Volatile na EventThrottler |
| 03-31 | Samsung fix: TYPE_WINDOWS_CHANGED (event systemowy) + overlay detection po typie okna + false trigger filter (CONTENT_CHANGED bez overlay = skip) + watch mode 60s |
| 03-30 | Uber: spaced retries (delay 600ms, 4 retries pokrywające 0-2400ms) — fix errorCode=3 od back-to-back. Watch mode: periodic screenshot co 2.5s gdy Uber aktywny (safety net na opóźnione eventy). Wolt: 2 spaced retries (proaktywna ochrona). Flaga isRetrying zapobiega kolizji watch mode + retries. |
| 03-30 | Diagnostyka Samsung: getWindows() logging + OCR normalizacja l/I/|→1 obok cyfr |
| 03-29 | Uber: adaptive back-to-back polling (7 prób w ~2.9s zamiast 1 retry po 3s) — ZASTĄPIONE przez spaced retries 03-30 |
| 03-29 | Diagnostyka: debug screenshoty do Downloads + logi retry z retryIndex, screenOn, bitmap size, cropY |
| 03-29 | Universal extractAmount() w OcrOfferParser — 3-krokowy fallback (LICZBA+WALUTA, WALUTA+LICZBA, luźna liczba), obsługa PLN/zł/грн/₴ |
| 03-26 | Fix: WoltOcrParser — usunięto guard fraz Uber-specyficznych (blokował 100% ofert Wolta po polsku) |
| 03-26 | Fix: isUserStopped — reset po MIUI kill (monitoring wznawia się poprawnie) |
| 03-26 | Fix: GlovoOcrParser — guard przed przechwyceniem eventów Ubera |
| 03-26 | Fix: Uber retry — 3s opóźnienie po nieudanym screenshocie |
| 03-25 | Setup wizard v2: karty per producent (Samsung/Xiaomi/Huawei/Oppo/OnePlus), toast hints (skrócone — "Znajdź OrderPilot i włącz przełącznik"), domyślny język z system locale |
| 03-25 | Test na Xiaomi z FakeUberApp: belka działa, wizard Xiaomi OK, task-removed OK, toast hints OK (pushowano na GitHub) |
| 03-25 | Fix: MIUI fałszywie zatrzymywał monitoring po Home — zamiana ActivityLifecycleCallbacks na onTaskRemoved() w serwisach |
| 03-25 | Fix: OCR ukraiński — Latin lookalikes (rpH/XB) we wszystkich parserach, distance regex poluzowany, logowanie linii OCR |
| 03-25 | Hardening: thread-safe overlay slots (synchronized), OCR recycled-bitmap guard, optymalizacja screenshotów (eliminacja podwójnej alokacji) |
| 03-24 | Fix: WoltOcrParser odrzuca frazy Uber ("Spodziewany zarobek", "Szacowany", "Dostawa od") — zapobiega parsowaniu overlaya Ubera jako zlecenia Wolt |
| 03-24 | Fix: cross-platform duplicate check łapie duplikaty od pierwszego parsowania (usunięto guard clause) |
| 03-24 | Fix: Uber eventy exempt z foreground check — popup overlay widoczny nad każdą apką |
| 03-24 | Fix: foreground check tylko dla rival platform — nie blokuje Uber overlaya nad launcherem |
| 03-24 | Fix: foreground check przed screenshotem — Wolt w tle nie parsuje popupu Ubera jako swojego zlecenia |
| 03-24 | Fix: wyrzucenie apki z "ostatnich" zatrzymuje monitoring i chowa belki (ActivityLifecycleCallbacks) |
| 03-22 | Refactor: per-platform lastResult (ConcurrentHashMap) w serwisie i PipelineOrchestrator + cross-platform duplicate check w obu ścieżkach |
| 03-22 | Fix: Multi-overlay — cross-platform duplicate check z tolerancją (±1 min, ±0.5 km), dynamiczna wysokość slotów, stabilna pozycja przy update (bez slot swap) |
| 03-22 | Fix: Bolt Food — dodano prawdziwy pakiet `com.bolt.deliverycourier` do supportedPackages |
| 03-21 | Hardening: ConcurrentHashMap w OverlayAutoHider, cont.isActive w OcrEngine, maxDepth w TextCollector, crash logger do Downloads |
| 03-21 | Fix: Glovo parser filtruje "ZAPŁAĆ X zł" na ekranie oferty z gotówką + warianty wielojęzyczne (PL/UK/EN) |
| 03-20 | Fix: Glovo parser filtruje kwoty "zapłać gotówką partnerowi" + guard "Potwierdź odbiór" |
| 03-17 | Multi-overlay: max 2 belki naraz, etykiety platform, osobne timery, przycisk × per belka |
| 03-17 | Domyślne ustawienia: 30s belka, wszystkie metryki widoczne |
| 03-17 | Docs: pełna aktualizacja dokumentacji (sesja spójności) |
| 03-16/17 | Fix: Glovo partial offer, gotówka (ODBIERZ), sumowanie WSZYSTKICH dystansów |
| 03-16 | Fix: Uber odrzuca ekran statystyk (> 180 min) |
| 03-15 | BoltFoodOcrParser — gotowy, czeka na test |
| 03-14/15 | GlovoOcrParser v2 + sanitizeAmount + ring buffer logów + ustawienia per platforma |

---

## Wyniki testów

### Glovo (2026-03-15 — 2026-03-17)
| Zlecenie | Wynik | Uwagi |
|----------|-------|-------|
| 18,15 zł / Starbucks | ✅ | Max kwota (18,15 > 4,71), dystans 1,26+0,78=2,0 km |
| 7,50 zł / Pasibus | ✅ | Oba dystanse od razu (tree widzi spoza ekranu) |
| 18,29 zł / Pizzeria 105 | ✅ | Po fix partial offer — parser czekał na pełne dane |
| 25,38 zł / TARGOWA+Kebab (3 dyst.) | ⚠️→✅ | Wziął gotówkę 39 zł. Naprawione (filtr ODBIERZ). |
| 12,54 zł / Biedronka (gotówka) | ❌→✅ | Wziął 65,41 zł klienta. Naprawione (filtr ODBIERZ). |
| 31,50 zł / Kebab Lamh (zapłać gotówką) | ❌→✅ | Naprawione (filtr gotówkowy + guard). |
| 11,32 zł / Apteczka Zdrowia (ZAPŁAĆ gotówką) | ❌→✅ | Naprawione (filtr "ZAPŁAĆ" + warianty PL/UK/EN). |

### Uber (2026-03-16 — 2026-03-22)
| Problem | Wynik |
|---------|-------|
| Ekran statystyk (324 zł / 2575 min) | ✅ Naprawione — filtr > 180 min |
| Regresja po zmianach | ✅ Działa |
| Belka 2 metryki zamiast 5 | ⏳ Czekamy na logi |

### Wolt (2026-03-13)
- Zweryfikowany na telefonie (13 zł / 26 min / 2.7 km → 30 zł/h → RED) ✅

### Multi-overlay (2026-03-22)
| Problem | Wynik |
|---------|-------|
| Cross-contamination (Wolt bar dostał dane Uber) | ❌→✅ Fix: cross-platform duplicate check z tolerancją |
| Belki nachodzą na siebie | ❌→✅ Fix: dynamiczna wysokość (view.height po layout) |
| Slot swap (Uber z góry na dół) | ❌→✅ Fix: position = existingIndex zamiast 0 |

### Bolt Food (2026-03-22)
- Zlecenie przyszło, belka nie zadziałała — pakiet `com.bolt.deliverycourier` nie był w supportedPackages. Naprawione. Czeka na retest.

### Xiaomi — testy produkcyjne (2026-03-26/27)
| Problem | Wynik |
|---------|-------|
| Bolt Food 4/4 zlecenia | ✅ Działa |
| Wolt 0/4 (guard Uber-specyficzny blokował po polsku) | ❌→✅ Naprawione (03-26), retested (03-27) |
| Uber — belka nie pojawiła się (GlovoOcrParser przechwycił eventy) | ❌→✅ Naprawione (03-26), retested (03-27) |
| isUserStopped martwy po MIUI kill (monitoring nie wznawia się) | ❌→✅ Naprawione (03-26), retested (03-27) |
| Glovo — nie testowane (tata nie zalogowany) | ⏳ Czeka na test |

---

<details>
<summary>Archiwum — ukończone etapy</summary>

## Faza POC — ZAKOŃCZONA (2026-02-24 — 2026-02-27)

- Android Studio + JDK 17 zainstalowane
- FakeUberDriver: aplikacja testowa symulująca popup Uber
- **POC MediaProjection + OCR: belka pojawia się na telefonie przy popupie FakeUberDriver**

## EPIC 1–14 — UKOŃCZONE (2026-03-04 — 2026-03-05)

Fundament → Domain → Settings → Engine → Parser → Capture → OCR → Overlay → Pipeline → Service → UI → Billing → Testy E2E. Pełny plan: `docs/PLAN.md`.

## Bugfixy po testach na telefonie (2026-03-06 — 2026-03-08)

| Zadanie | Efekt |
|---------|-------|
| Fix wygaszanie ekranu | WakeLock + `isProjectionLost` flaga + powiadomienie |
| Optymalizacja latencji | firstShotDelay 100ms, cooldown 3s. Wynik ~1.3s (bottleneck: ML Kit OCR ~700ms) |
| KAN-14 odświeżanie belki | Usunięto guard `isShowing()` |
| Bug START + accessibility | `pendingStart` flaga na race condition onResume/onActivityResult |
| KAN-12 dark mode | `forceDarkAllowed="false"` + jawny `setTextColor(WHITE)` |
| KAN-11 dialog MediaProjection | Toast wyjaśniający przed dialogiem |
| KAN-13+15 suwaki | `overlayOpacity` i `displayTimeSeconds` w DisplayConfig |

## Dual-mode accessibility fallback (2026-03-10)
## takeScreenshot fallback (2026-03-11)
## WoltOcrParser — zweryfikowany (2026-03-13)
## GlovoOcrParser + fixy (2026-03-14/17)
## BoltFoodOcrParser — gotowy (2026-03-15)
## Multi-overlay — 2 belki naraz (2026-03-17)

</details>
