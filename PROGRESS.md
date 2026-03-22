# CourierAssist — Status Postępu

**Ostatnia aktualizacja:** 2026-03-22
**Obecny etap:** Testy produkcyjne. Wszystkie parsery gotowe. Multi-overlay gotowy. Bolt Food fix — zły package name naprawiony.
**Aktywne branche:** `feature/multi-overlay` (tip development), `feature/bolt-parser` (ancestor)

---

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| High | Multi-overlay — testy 2 belek naraz z różnych platform | Czeka na build |
| High | Glovo — weryfikacja fixów (gotówka, partial offer, suma dystansów, "zapłać gotówką") | Czeka na build |
| High | Bolt Food — testy na prawdziwych zleceniach | Fix: dodano com.bolt.deliverycourier do supportedPackages (03-22) |
| High | Merge `feature/multi-overlay` → `feature/production-app` | Po potwierdzeniu stabilności |
| Medium | Crash na starszym telefonie (brat) — SettingsActivity | Do zbadania |
| Medium | UI/UX polish — layouty, kolory, animacje | Do implementacji |
| Medium | Setup wizard + battery optimization | Plan w `docs/PLAN.md` |
| Medium | Przesuwana belka + mini overlay | Do implementacji |
| Low | Mruganie belki Uber jasny→ciemny | Monitorowane |

---

## Aktywne branche

| Branch | Cel | Status |
|--------|-----|--------|
| `feature/multi-overlay` | 2 belki naraz + wszystkie fixy | **Aktualny tip development** (12 ahead of production-app) |
| `feature/bolt-parser` | Bolt Food parser + fixy Glovo/Uber | Na GitHub (ancestor multi-overlay) |
| `feature/production-app` | Główny branch produkcyjny | Na GitHub (37 ahead of main) |
| `main` | Stabilna baza z POC | Zablokowany na zmiany kodu |

> Workflow: nowy branch → testuj → merge do `feature/production-app`

---

## Co dalej — Priorytet

1. Zbudować APK z `feature/multi-overlay`, przetestować
2. Weryfikacja Glovo po fixach (gotówka, wielopunktowe, partial offer)
3. Weryfikacja Uber (ekran statystyk nie triggeruje belki)
4. Bolt Food na prawdziwych zleceniach
5. Crash na starszym telefonie — zbadać
6. UI/UX polish
7. Setup wizard
8. Merge multi-overlay → production-app

---

## Ostatnie zmiany (2026-03-14 — 2026-03-21)

| Data | Zmiana |
|------|--------|
| 03-22 | Fix: Multi-overlay — cross-platform duplicate check (belki nie mieszają danych), dynamiczna wysokość slotów (bez overlapping), stabilna pozycja przy update (bez slot swap) |
| 03-22 | Fix: Bolt Food — dodano prawdziwy pakiet `com.bolt.deliverycourier` do supportedPackages (belka nie działała bo parser miał złe nazwy pakietów) |
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

## Wyniki testów (2026-03-15 — 2026-03-17)

### Glovo
| Zlecenie | Wynik | Uwagi |
|----------|-------|-------|
| 18,15 zł / Starbucks | ✅ | Max kwota (18,15 > 4,71), dystans 1,26+0,78=2,0 km |
| 7,50 zł / Pasibus | ✅ | Oba dystanse od razu (tree widzi spoza ekranu) |
| 18,29 zł / Pizzeria 105 | ✅ | Po fix partial offer — parser czekał na pełne dane |
| 25,38 zł / TARGOWA+Kebab (3 dyst.) | ⚠️ | Build bez fixu ODBIERZ — wziął gotówkę 39 zł. Naprawione. |
| 12,54 zł / Biedronka (gotówka) | ❌→✅ | Wziął 65,41 zł klienta. Naprawione (filtr ODBIERZ). |
| 31,50 zł / Kebab Lamh (zapłać gotówką) | ❌→✅ | Belka wzięła 31,50 zł (gotówka w restauracji) zamiast 10,74 zł (wynagrodzenie). Naprawione (filtr gotówkowy + guard). |
| 11,32 zł / Apteczka Zdrowia (ZAPŁAĆ gotówką) | ❌ | Belka wzięła 43,99 zł (przycisk "ZAPŁAĆ") zamiast 11,32 zł. Naprawione (filtr "ZAPŁAĆ" + warianty PL/UK/EN). |

### Uber
| Problem | Wynik |
|---------|-------|
| Ekran statystyk (324 zł / 2575 min) | ✅ Naprawione — filtr > 180 min |
| Regresja po zmianach | ✅ Działa |

### Wolt
- Zweryfikowany na telefonie (13 zł / 26 min / 2.7 km → 30 zł/h → RED) ✅

### Bolt Food
- Parser gotowy, czeka na prawdziwe zlecenie ⏳
- 03-22: Zlecenie przyszło ale belka nie zadziałała — pakiet `com.bolt.deliverycourier` nie był w supportedPackages. Naprawione.

---

<details>
<summary>📋 Archiwum — ukończone etapy</summary>

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

## Archiwalne branche

| Branch | Status |
|--------|--------|
| `feature/glovo-parser` | Fixy zduplikowane na bolt-parser/multi-overlay |
| `feature/wolt-parser` | Merged do production-app 2026-03-13 |
| `feature/accessibility-fallback` | Merged 2026-03-10 |
| `feature/ui-redesign` | Merged 2026-03-09 |
| `fix/screen-off-survival` | Merged 2026-03-08 |
| `feature/fake-uber-driver` | Gotowa |
| `lukasz` | Merged do main |

</details>
