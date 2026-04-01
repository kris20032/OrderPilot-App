# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-04-01
**Obecny etap:** Fix cross-contamination retry (Wolt retry parsował popup Bolta) + uniwersalny context validation dla screenshot pipeline. Czeka na build + test.
**Aktywne branche:** `fix-formaty` (Samsung fix + Xiaomi phantom overlay fix + audyt + retry context validation), `feature/multi-overlay` (tip development)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| **High** | Fix: Uber popup nad Samsung launcher — brak accessibility eventów | ✅ Naprawione (03-31) — TYPE_WINDOWS_CHANGED + getWindows() overlay detection |
| **High** | Fix: Overlay detection — typ okna zamiast liczby okien | ✅ Naprawione (03-31) — popup=type 3, mapa=type 1 |
| **High** | Fix: False triggers — 7+ screenshotów przy przeglądaniu mapy Ubera | ✅ Naprawione (03-31) — filtr CONTENT_CHANGED bez overlay okna |
| **High** | Fix: Watch mode screenshotował po obsłużonym popupie | ✅ Naprawione (03-31) — hasUberOverlayWindow() check |
| **High** | Fix: Retries screenshotowały po przełączeniu apki | ✅ Naprawione (03-31) — overlay check w pętli retries |
| **High** | Watch mode rozszerzony: 60s timeout (było 15s) | ✅ Naprawione (03-31) |
| **High** | Audyt kodu: memory leaki (5x try-finally) | ✅ Naprawione (03-31) — bitmap/node recycle w finally |
| **High** | Audyt kodu: crash guards | ✅ Naprawione (03-31) — pipeline.isInitialized + image.planes check |
| **High** | Audyt kodu: Glovo distance cap 20→50km | ✅ Naprawione (03-31) — dalekie zlecenia nie odrzucane |
| **High** | Audyt kodu: EventThrottler @Volatile | ✅ Naprawione (03-31) — thread safety |
| Medium | Hardening: synchronized ScreenCaptureService | ✅ Naprawione (03-31) — ochrona przed podwójnym setup |
| Medium | Hardening: @Volatile uberWatchJob | ✅ Naprawione (03-31) — thread safety |
| Medium | Hardening: PopupCropper bounds check | ✅ Naprawione (03-31) — ochrona przed crashem |
| Medium | Refactor: OfferDuplicateChecker | ✅ Naprawione (03-31) — usunięcie duplikacji kodu |
| Low | Refactor: named constants | ✅ Naprawione (03-31) — 8 magicznych liczb → czytelne stałe |
| **High** | Fix: Uber popup niewykrywany gdy Uber jest foreground | ✅ Naprawione (03-31) — isUberForeground() bypass dla CONTENT_CHANGED + watch mode |
| **High** | Fix: Fałszywa belka Ubera na Xiaomi (persistent overlay) | ✅ Naprawione (04-01) — state transition w handleWindowsChanged() + guard w UberOcrParser |
| **High** | Fix: Fałszywa belka Wolta przy zleceniu Bolta (retry cross-contamination) | ✅ Naprawione (04-01) — uniwersalny `isRivalInForeground()` w throttler callback + retry loop |
| **Medium** | Defense in depth: guardy w WoltOcrParser i BoltFoodOcrParser | ✅ Naprawione (04-01) — odrzucają tekst rival platform |
| **High** | Build + test Samsung fix + Xiaomi fix u taty | Czeka na build w Android Studio |
| **High** | Merge do `feature/production-app` | Po potwierdzeniu stabilności |
| Medium | Crash na starszym telefonie (brat) — SettingsActivity | Nie odtworzony po reinstalacji (03-25), monitorowane |
| Low | Mruganie belki Uber jasny→ciemny | Monitorowane |

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/xiaomi-testing` | Fixy z testów Xiaomi (4 bugi) | **Aktywny** — czeka na retest |
| `feature/multi-overlay` | Multi-overlay + wszystkie fixy | Tip development |
| `feature/production-app` | Główny branch produkcyjny | Na GitHub (37 ahead of main) |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu |

> **Archiwalne:** `feature/bolt-parser` (ancestor multi-overlay), `feature/glovo-parser`, `feature/wolt-parser`, `feature/accessibility-fallback`, `feature/ui-redesign`, `fix/screen-off-survival`

> Workflow: nowy branch → testuj → merge do `feature/production-app`

---

## Co dalej — Roadmap

### Faza 1: Stabilizacja (teraz)
1. Testy produkcyjne u taty — czekamy na potwierdzenie stabilności
2. Bolt Food — retest na prawdziwym zleceniu
3. Glovo — weryfikacja fixów gotówkowych
4. Merge `feature/multi-overlay` → `feature/production-app`

### Faza 2: Przygotowanie do beta testów
5. ~~Setup wizard per producent~~ ✅ Gotowe (03-25)
6. Przygotowanie APK do dystrybucji

### Faza 3: Beta testy u zewnętrznych kurierów
7. Znaleźć 3-5 kurierów na mieście (mix platform + modeli telefonów)
8. Instalacja apki + konfiguracja na miejscu
9. Zbieranie feedbacku przez WhatsApp/Telegram

---

## Ostatnie zmiany (2026-03-14 — 2026-03-31)

| Data | Zmiana |
|------|--------|
| 04-01 | **PopupCropper crop ratio 40%→30%** — na Xiaomi popup zaczynał się od 36% ekranu, margines tylko 4%. Zmiana na 30% daje 6% marginesu, lepiej pokrywa różne aspect ratio |
| 04-01 | **Fix: Retry cross-contamination** — Wolt retry robił screenshot gdy Bolt był na ekranie → WoltOcrParser parsował popup Bolta jako Wolt. Fix: `isRivalInForeground()` helper, sprawdzany w throttler callback (po 100ms delay) i w każdym retry przed screenshotem. Uber exempt (overlay nad wszystkim). |
| 04-01 | **Defense in depth: guardy parserów** — WoltOcrParser + BoltFoodOcrParser odrzucają tekst z frazami rival platform (Uber/Bolt/Wolt), analogicznie do UberOcrParser |
| 04-01 | **Fix: Fałszywa belka Ubera na Xiaomi** — Uber trzyma stały pusty overlay (type=3) na Xiaomi. WINDOWS_CHANGED triggerował fałszywy screenshot → UberOcrParser parsował popup Wolta/Bolta jako Uber. Fix: state transition (trigger TYLKO na nowy overlay) + guard w UberOcrParser (odrzuca frazy Wolta/Bolta) |
| 03-31 | **Fix: Uber foreground popup** — gdy Uber jest na pierwszym planie, popup jest wewnątrz okna apki (type=1, nie overlay). `isUberForeground()` bypass przepuszcza eventy do throttlera bez wymogu overlay window |
| 03-31 | **Audyt kodu v2** — 6 dodatkowych fixów: synchronized w ScreenCaptureService, @Volatile uberWatchJob, PopupCropper bounds check, OfferDuplicateChecker (usunięcie duplikacji kodu), named constants (8 magicznych liczb → stałe) |
| 03-31 | **Audyt kodu v1** — 9 fixów z przeglądu codebase: memory leaki (5x try-finally na bitmap/node recycle), crash guard (pipeline.isInitialized + image.planes.isEmpty), Glovo distance cap 20→50km, @Volatile na EventThrottler |
| 03-31 | **Fix: Samsung missed events** — dodano `typeWindowsChanged` do accessibility config. Nowy handler `handleWindowsChanged()` sprawdza `getWindows()` czy pojawił się overlay Ubera. Łapie popupy nad Samsung launcher gdzie `TYPE_WINDOW_STATE_CHANGED` nie przychodził |
| 03-31 | **Fix: Overlay detection** — sprawdzanie typu okna (popup=type 3, mapa=type 1) zamiast liczby okien. Z logów: popup nad WhatsApp = 1 okno Ubera (type=3), nie 2 |
| 03-31 | **Fix: False triggers** — filtr `CONTENT_CHANGED` z Ubera: jeśli brak overlay okna (type!=1) → skip screenshot. Eliminuje 7+ bezcelowych screenshotów mapy |
| 03-31 | **Fix: Watch mode** — `hasUberOverlayWindow()` check przed screenshotem. Bez tego: ~20 bezcelowych screenshotów WhatsApp/launchera po obsłużeniu popupu |
| 03-31 | **Fix: Retries** — overlay check w pętli retries. Z logów: tata przełączył apkę, retry 3-4 screenshotowały launcher/WhatsApp zamiast Ubera |
| 03-31 | **Watch mode 60s** — rozszerzony timeout z 15s do 60s, interwał 2.5s bez zmian. Safety net na missed/opóźnione eventy |
| 03-26 | **Fix: WoltOcrParser** — usunięty guard z frazami Uber ("Spodziewany zarobek", "Szacowany", "Dostawa od") który blokował 100% ofert Wolta (4 zlecenia zfailowane) |
| 03-26 | **Fix: isUserStopped** — MIUI zabijał ScreenCaptureService ustawiając flagę, monitoring stawał. Reset w MainActivity.onResume() + usunięte z ScreenCaptureService.onTaskRemoved() |
| 03-26 | **Fix: GlovoOcrParser** — guard "Łącznie"/"Lacznie"/"Загалом" odrzuca tekst popup Ubera (zapobieganie cross-contamination) |
| 03-26 | **Fix: Uber retry** — 3s retry po nieudanym screenshot (Uber generuje mało eventów, ~15s przerwy) |
| 03-25 | Setup wizard v2: karty per producent (Samsung/Xiaomi/Huawei/Oppo/OnePlus), toast hints (skrócone — "Znajdź CourierAssist i włącz przełącznik"), domyślny język z system locale |
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

### Wolt (2026-03-13 — 2026-03-26)
- Zweryfikowany na telefonie (13 zł / 26 min / 2.7 km → 30 zł/h → RED) ✅
- **03-26 Xiaomi:** 4 zlecenia — belka NIGDY nie pojawiła się. Przyczyna: guard "Spodziewany zarobek"/"Szacowany"/"Dostawa od" blokował prawdziwe oferty Wolta (Wolt używa tych samych fraz). **Naprawione** — guard usunięty.
- **03-26 Xiaomi:** monitoring martwy po ~1h — MIUI zabił ScreenCaptureService, isUserStopped=true nie resetowało się. **Naprawione** — reset w onResume.

### Multi-overlay (2026-03-22)
| Problem | Wynik |
|---------|-------|
| Cross-contamination (Wolt bar dostał dane Uber) | ❌→✅ Fix: cross-platform duplicate check z tolerancją |
| Belki nachodzą na siebie | ❌→✅ Fix: dynamiczna wysokość (view.height po layout) |
| Slot swap (Uber z góry na dół) | ❌→✅ Fix: position = existingIndex zamiast 0 |

### Uber (2026-03-26 Xiaomi)
| Problem | Wynik |
|---------|-------|
| Belka nie pojawiła się — screenshot widział Glovo dialog zamiast Ubera | Naprawione: GlovoOcrParser guard + Uber retry 3s |
| 15s przerwy między eventami — popup może zniknąć | Naprawione: retry po 3s jeśli brak belki |

### Uber Samsung (2026-03-31)
| Problem | Wynik |
|---------|-------|
| Popup nad WhatsApp (13,86 zł) | Belka OK — eventy accessibility przyszły normalnie |
| Popup nad Samsung launcher (14,98 zł) | Belka NIE — Samsung nie wysłał eventów accessibility. **Fix:** TYPE_WINDOWS_CHANGED + getWindows() overlay detection |
| Popup gdy Uber jest foreground (13,63 zł) | Belka NIE przez 16s — popup wewnątrz okna apki (type=1, nie overlay). **Fix:** isUberForeground() bypass |
| False triggers — przeglądanie mapy (22:03) | 7+ screenshotów na nic. **Fix:** filtr CONTENT_CHANGED bez overlay okna |

### Xiaomi — testy 2026-04-01
| Problem | Wynik |
|---------|-------|
| Samsung: belka działa prawidłowo | ✅ Kilka zleceń z Ubera i innych apek — belka za każdym razem OK |
| Xiaomi: Wolt zlecenie → fałszywa belka Ubera | ❌→✅ Persistent overlay Ubera (type=3) triggerował screenshot. Fix: state transition + parser guard |
| Xiaomi: Bolt zlecenie → fałszywa belka Ubera | ❌→✅ Ten sam problem. Fix jw. |
| Xiaomi: Uber zlecenie → belka prawidłowa | ✅ Uber działa poprawnie przez CONTENT_CHANGED |
| Xiaomi: Bolt zlecenie (16,18 zł) → fałszywa belka Wolta | ❌→✅ Wolt retry robił screenshot gdy Bolt był na ekranie. Fix: isRivalInForeground() w throttler callback + retry loop |

### Bolt Food (2026-03-22 — 2026-03-26)
- ~~Zlecenie przyszło, belka nie zadziałała — pakiet `com.bolt.deliverycourier` nie był w supportedPackages.~~ Naprawione.
- **03-26 Xiaomi:** 4/4 zlecenia — belka pojawiła się od razu prawidłowo ✅

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
