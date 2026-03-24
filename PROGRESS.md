# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-24
**Obecny etap:** Testy produkcyjne. Foreground check + task-removed detection dodane (03-24). Czekamy na build + testy u taty.
**Aktywne branche:** `feature/multi-overlay` (tip development, 22 commitów ahead of production-app)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| **High** | Zbudować APK z `feature/multi-overlay` i przetestować | Czeka na build |
| **High** | Bolt Food — testy na prawdziwych zleceniach | Fix package name gotowy (03-22), czeka na test |
| **High** | Multi-overlay — testy 2 belek naraz | Fixy gotowe (03-22), czeka na test |
| **High** | Glovo — weryfikacja fixów (gotówka, partial offer, suma dystansów) | Czeka na test |
| **High** | Merge `feature/multi-overlay` → `feature/production-app` | Po potwierdzeniu stabilności |
| Medium | Uber — belka pokazała 2 metryki zamiast 5 | Czekamy na logi + screeny od taty |
| Medium | Crash na starszym telefonie (brat) — SettingsActivity | Do zbadania |
| Medium | Setup wizard + battery optimization | Plan w `docs/PLAN.md` |
| Low | Mruganie belki Uber jasny→ciemny | Monitorowane |

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/multi-overlay` | Multi-overlay + wszystkie fixy | **Aktualny tip development** (16 ahead of production-app) |
| `feature/production-app` | Główny branch produkcyjny | Na GitHub (37 ahead of main) |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu |

> **Archiwalne:** `feature/bolt-parser` (ancestor multi-overlay), `feature/glovo-parser`, `feature/wolt-parser`, `feature/accessibility-fallback`, `feature/ui-redesign`, `fix/screen-off-survival`

> Workflow: nowy branch → testuj → merge do `feature/production-app`

---

## Co dalej — Priorytet

1. Zbudować APK z `feature/multi-overlay`, przetestować
2. Bolt Food — test na prawdziwych zleceniach (fix package name)
3. Multi-overlay — test 2 belek naraz (cross-contamination fix)
4. Glovo — weryfikacja po fixach gotówkowych
5. Upomnieć się u taty o logi Uber (2 metryki)
6. Crash na starszym telefonie — zbadać
7. Merge multi-overlay → production-app

---

## Ostatnie zmiany (2026-03-14 — 2026-03-24)

| Data | Zmiana |
|------|--------|
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
