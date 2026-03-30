# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-30
**Obecny etap:** Uber timing fix (spaced retries + watch mode) na branchu `fix-formaty`. Czeka na test u taty.
**Aktywne branche:** `feature/production-app` (główny), `fix-formaty` (fixy kwot + Uber timing), `feature/multi-overlay` (tip development)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| **High** | Uber belka — spaced retries + watch mode (fix timing) | Czeka na test u taty (03-30) |
| **High** | Merge `fix-formaty` → `feature/xiaomi-testing` → `feature/production-app` | Po teście |
| **High** | Budowanie APK release do dystrybucji beta | Następny krok |
| **High** | Znalezienie 3-5 kurierów beta testerów | W toku |
| **High** | Weryfikacja Glovo na Xiaomi — tata nie zalogowany podczas testów | Czeka na test |
| Medium | Uber — belka pokazała 2 metryki zamiast 5 | Czekamy na logi + screeny od taty |
| Medium | Crash na starszym telefonie (brat) — SettingsActivity | Nie odtworzony po reinstalacji (03-25), monitorowane |
| Low | Mruganie belki Uber jasny→ciemny | Monitorowane |

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/production-app` | Główny branch produkcyjny | Aktywny |
| `fix-formaty` | Universal amount regex + Uber spaced retries + watch mode | Czeka na test (03-30) |
| `feature/xiaomi-testing` | 4 bugi z testów Xiaomi (Wolt/Uber/isUserStopped/Glovo) | Gotowy do merge (03-27) |
| `feature/multi-overlay` | Multi-overlay + wszystkie fixy | Tip development |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu |

> **Archiwalne (zmergowane, do usunięcia):** `feature/setup-wizard-v2`, `feature/bolt-parser`, `feature/glovo-parser`, `feature/wolt-parser`, `feature/accessibility-fallback`, `feature/ui-redesign`, `feature/xiaomi-testing`, `fix/foreground-check`, `fix/screen-off-survival`
> **Stale remote:** `remotes/origin/feature/ocr`, `remotes/origin/feature/setup-wizard-v2`

> Workflow: nowy branch → testuj → merge do `feature/production-app`

---

## Co dalej — Roadmap

### Faza 1: Stabilizacja — ZAKOŃCZONA (03-27)
1. ~~Testy produkcyjne u taty~~ ✅ Zaliczone (03-27, Xiaomi)
2. ~~Bolt Food — retest~~ ✅ 4/4 zlecenia (03-26)
3. ~~Setup wizard per producent~~ ✅ Gotowe (03-25)
4. ~~Merge `feature/xiaomi-testing` → `feature/production-app`~~ ✅ (03-27)

### Faza 1.5: Fixy z dalszych testów (teraz)
5. ~~Universal extractAmount()~~ ✅ (03-29)
6. ~~Uber adaptive polling~~ → zamienione na spaced retries + watch mode (03-30) ← **czeka na test**
7. Merge `fix-formaty` → `feature/production-app` — po teście

### Faza 2: Przygotowanie do beta testów
8. Budowanie APK release (signed) do dystrybucji
9. Glovo — weryfikacja na Xiaomi (tata nie był zalogowany)

### Faza 3: Beta testy u zewnętrznych kurierów
10. Znaleźć 3-5 kurierów na mieście (mix platform + modeli telefonów)
11. Instalacja apki + konfiguracja na miejscu
12. Zbieranie feedbacku przez WhatsApp/Telegram

---

## Ostatnie zmiany (2026-03-14 — 2026-03-29)

| Data | Zmiana |
|------|--------|
| 03-30 | Uber: spaced retries (delay 600ms, 4 retries pokrywające 0-2400ms) — fix errorCode=3 od back-to-back. Watch mode: periodic screenshot co 2.5s gdy Uber aktywny (safety net na opóźnione eventy) |
| 03-30 | Diagnostyka Samsung: getWindows() logging + OCR normalizacja l/I/|→1 obok cyfr |
| 03-29 | Uber: adaptive back-to-back polling (7 prób w ~2.9s zamiast 1 retry po 3s) — ZASTĄPIONE przez spaced retries 03-30 |
| 03-29 | Diagnostyka: debug screenshoty do Downloads + logi retry z retryIndex, screenOn, bitmap size, cropY |
| 03-29 | Universal extractAmount() w OcrOfferParser — 3-krokowy fallback (LICZBA+WALUTA, WALUTA+LICZBA, luźna liczba), obsługa PLN/zł/грн/₴ |
| 03-26 | Fix: WoltOcrParser — usunięto guard fraz Uber-specyficznych (blokował 100% ofert Wolta po polsku) |
| 03-26 | Fix: isUserStopped — reset po MIUI kill (monitoring wznawia się poprawnie) |
| 03-26 | Fix: GlovoOcrParser — guard przed przechwyceniem eventów Ubera |
| 03-26 | Fix: Uber retry — 3s opóźnienie po nieudanym screenshocie |
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
