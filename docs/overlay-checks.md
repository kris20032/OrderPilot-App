# CourierAssist — Overlay Checks & Retry Logic (Audyt lokalizacyjny)

**Data audytu:** 2026-04-01

---

## Podsumowanie

Logika overlay detection, watch mode i retry w CourierAccessibilityService jest **w pelni language-independent**. Opiera sie na:
- Package names (Java identifiers — brak lokalizacji)
- Window types (Android int constants — TYPE_APPLICATION = 1, TYPE_APPLICATION_OVERLAY = 3)
- Numeric timings (600ms, 2500ms, 60s)

Zmiana jezyka urzadzenia/aplikacji **nie wplywa** na te mechanizmy.

---

## 1. Overlay Window Detection

### `hasUberOverlayWindow()` — sprawdza typ okna
```
windows.filter { it.type != AccessibilityWindowInfo.TYPE_APPLICATION }
```
- Szuka okien Ubera ktore NIE sa typu APPLICATION (czyli overlay)
- **Language-independent** — typy okien to int constants

### Phantom Overlay (Xiaomi)
- Uber na Xiaomi trzyma staly pusty overlay (type=3)
- Fix: state transition `hadUberOverlayWindow` — trigger TYLKO na nowy overlay
- **Language-independent** — logika oparta na boolean flag

### `isUberForeground()` — bypass dla foreground popup
```
rootInActiveWindow?.packageName == "com.ubercab.driver"
```
- Gdy Uber jest foreground, popup jest wewnatrz okna apki (type=1), nie overlay (type=3)
- **Language-independent** — porownuje package name

---

## 2. Foreground & Rival Checks

### `isRivalInForeground(pkg: String)`
```kotlin
val activePackage = rootInActiveWindow?.packageName?.toString()
return activePackage != null && activePackage != pkg && activePackage in courierPackages
```
- Sprawdza czy inna apka kurierska jest na pierwszym planie
- **Language-independent** — porownuje package names

### `courierPackages` set
```kotlin
setOf(
    "com.ubercab.driver",
    "com.wolt.courierapp",
    "com.logistics.rider.glovo",
    "com.bolt.deliverycourier"
)
```
- Java package names — **nigdy nie sa lokalizowane**

---

## 3. Watch Mode

### Trigger
- Startuje gdy Uber generuje eventy i belka nie jest widoczna
- Periodic screenshot co 2500ms
- Timeout 60s bez nowych eventow

### Guard
- Skipuje jesli rival courier jest foreground
- Skipuje jesli `isRetrying` flag aktywna (unika kolizji screenshotow)
- **Language-independent** — timing + boolean flags

---

## 4. Retry Logic

### Spaced retries
- Uber: 4 retries z delay 600ms miedzy nimi
- Wolt: 2 retries (safety net)
- Glovo/Bolt: brak retry (accessibility tree, nie OCR)

### Context validation per retry
```kotlin
if (isRivalInForeground(pkg)) break  // rival stala sie foreground
if (platform == Platform.UBER && !hasUberOverlayWindow()) break  // overlay zniknal
```
- **Language-independent** — package names + window types

---

## 5. EventThrottler

- First shot delay: 100ms
- Cooldown: 1.5s
- Callback z context validation (rival foreground check)
- **Language-independent** — timing + lambda callback

---

## 6. OfferDuplicateChecker

### Porownanie ofert
```kotlin
amountDiff <= 0.5   // ±0.5 zl
timeDiff <= 5       // ±5 min
distDiff <= 0.5     // ±0.5 km
// Wymaga 2 z 3 kryteriow
```
- **Czysto numeryczne** — brak stringow, brak lokalizacji
- Cross-platform: porownuje oferty z roznych platform (np. Uber vs Wolt)

---

## 7. PopupCropper

### Crop ratio
```kotlin
CROP_START_RATIO = 0.30  // Crop top 30%, keep bottom 70%
```
- Procentowy crop — niezalezny od jezyka
- Popup kurierski zwykle zajmuje dolna czesc ekranu
- **Language-independent**

---

## 8. PipelineOrchestrator

### Routing
```kotlin
val parser = parserRegistry.getParser(packageName) ?: return
```
- Wybor parsera na podstawie package name — **language-independent**

### Per-platform result cache
```kotlin
ConcurrentHashMap<Platform, AnalysisResult>
```
- Cache per Platform enum — **language-independent**

---

## 9. TYPE_WINDOWS_CHANGED vs TYPE_CONTENT_CHANGED

### Samsung launcher fix
- Samsung launcher dropuje `TYPE_WINDOW_STATE_CHANGED` z overlay
- Fix: `TYPE_WINDOWS_CHANGED` — event systemowy (WindowManager), Samsung nie moze go zablokowac
- `getWindows()` sprawdza ile okien Ubera jest na ekranie (1=mapa, 2+=popup)
- **Language-independent** — event types + window counts

### False trigger filter
- `CONTENT_CHANGED` bez overlay okna → skip screenshot
- Zapobiega zbednym screenshotom przy scrollowaniu mapy
- **Language-independent**

---

## Podsumowanie

| Komponent | Language-dependent? | Uwagi |
|-----------|-------------------|-------|
| Overlay window detection | NIE | Window types = int constants |
| Foreground checks | NIE | Package names |
| Watch mode | NIE | Timing + flags |
| Retry logic | NIE | Timing + context validation |
| EventThrottler | NIE | Timing |
| OfferDuplicateChecker | NIE | Numeric comparison |
| PopupCropper | NIE | Percentage crop |
| PipelineOrchestrator | NIE | Package name routing |
| WINDOWS_CHANGED handling | NIE | Event types + window counts |

**Wniosek:** Cala warstwa overlay/retry/detection jest odporna na zmiane jezyka. Jedyne language-dependent elementy to parsery OCR (rival markers + offer parsing) — udokumentowane w `rival-markers.md`.
