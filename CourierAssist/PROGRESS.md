# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-26
**Obecny etap:** 4 krytyczne bugi naprawione po testach na Xiaomi (Wolt guard, isUserStopped, Glovo cross-contamination, Uber retry). Czeka na retest u taty.
**Aktywne branche:** `feature/xiaomi-testing` (fixy z testów Xiaomi), `feature/multi-overlay` (tip development)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| **High** | Fix: WoltOcrParser guard blokował 100% ofert Wolta | ✅ Naprawione (03-26) — usunięty guard z frazami Uber |
| **High** | Fix: isUserStopped nie resetował się po MIUI kill | ✅ Naprawione (03-26) — reset w onResume + usunięte z ScreenCaptureService |
| **High** | Fix: GlovoOcrParser parsował popup Ubera jako partial Glovo | ✅ Naprawione (03-26) — guard "Łącznie" |
| **High** | Fix: Uber brak retry po nieudanym screenshot | ✅ Naprawione (03-26) — retry 3s dla Ubera |
| **High** | Retest na Xiaomi — weryfikacja 4 fixów | Czeka na build + test u taty |
| **High** | Bolt Food — testy na prawdziwych zleceniach | ✅ 4/4 OK na Xiaomi (03-26) |
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

## Ostatnie zmiany (2026-03-14 — 2026-03-26)

| Data | Zmiana |
|------|--------|
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
