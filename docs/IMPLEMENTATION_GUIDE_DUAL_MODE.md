# Instrukcja implementacji: Dual-mode Accessibility Text Fallback

**Cel:** Gdy MediaProjection nie działa (screen off, brak consent), CourierAccessibilityService
czyta tekst bezpośrednio z drzewa UI i parsuje go — bez OCR, bez screenshot.

**Branch:** `feature/ui-redesign`
**Priorytet:** NAJWYŻSZY — rozwiązuje problem utraty serwisu po wygaszeniu ekranu

---

## Kontekst techniczny

Reverse-engineering konkurencyjnej apki (RideHelper) ujawnił dual-mode:
- **Primary:** MediaProjection → screenshot → OCR → parser → overlay
- **Fallback:** AccessibilityService → `getRootInActiveWindow()` → `collectText(node)` → parser → overlay

Nasz `CourierAccessibilityService` już nasłuchuje eventów z Ubera i triggeruje pipeline.
Trzeba dodać **drugą ścieżkę** (accessibility text) która działa gdy MediaProjection jest niedostępna.

### Istniejący pipeline (NIE zmieniaj):
```
AccessibilityEvent → EventThrottler → PipelineOrchestrator.process()
  → ScreenCaptureService.capture() → PopupCropper → OcrEngine → UberOcrParser → OfferAnalyzer → Overlay
```

### Nowa ścieżka fallback (DO DODANIA):
```
AccessibilityEvent → EventThrottler → CourierAccessibilityService.processViaAccessibility()
  → getRootInActiveWindow() → collectText(node) → UberOcrParser.parse() → OfferAnalyzer → Overlay
```

---

## Krok 1: AccessibilityTextCollector (NOWY PLIK)

**Plik:** `CourierAssist/app/src/main/java/com/courierassist/app/service/AccessibilityTextCollector.kt`

```kotlin
package com.courierassist.app.service

import android.view.accessibility.AccessibilityNodeInfo
import com.courierassist.app.di.AppLog

object AccessibilityTextCollector {

    fun collectText(root: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        traverseNode(root, sb)
        return sb.toString()
    }

    private fun traverseNode(node: AccessibilityNodeInfo, sb: StringBuilder) {
        node.text?.let { text ->
            if (text.isNotBlank()) sb.append(text).append('\n')
        }
        node.contentDescription?.let { desc ->
            if (desc.isNotBlank()) sb.append(desc).append('\n')
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                traverseNode(child, sb)
                child.recycle()
            }
        }
    }
}
```

**Ważne:** `child.recycle()` po użyciu — to wymagane przez Android API.

---

## Krok 2: Rozbudowa CourierAccessibilityService

**Plik:** `CourierAssist/app/src/main/java/com/courierassist/app/service/CourierAccessibilityService.kt`

### Zmiany:

1. Dodaj import:
   - `AccessibilityTextCollector`
   - `ScreenCaptureService` (do sprawdzenia `isProjectionLost`)
   - `ServiceLocator` (do dostępu do parsera, analyzera, overlay)

2. W `onAccessibilityEvent()` — zmień logikę:

```kotlin
override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    val pkg = event?.packageName?.toString() ?: return
    if (pkg !in watchedPackages) return
    if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
        event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

    AppLog.d(AppLog.TAG_SERVICE, "Event from $pkg")

    // Primary: MediaProjection pipeline (jeśli aktywna)
    if (isMediaProjectionAvailable()) {
        throttler.onEvent(scope) { pipeline.process(pkg) }
        return
    }

    // Fallback: Accessibility text parsing (gdy MediaProjection niedostępna)
    throttler.onEvent(scope) { processViaAccessibility(pkg) }
}
```

3. Dodaj `isMediaProjectionAvailable()`:

```kotlin
private fun isMediaProjectionAvailable(): Boolean {
    val service = ScreenCaptureService.instance ?: return false
    return service.isReady() && !ScreenCaptureService.isProjectionLost
}
```

4. Dodaj `processViaAccessibility()`:

```kotlin
private fun processViaAccessibility(packageName: String) {
    val root = rootInActiveWindow ?: run {
        AppLog.w(AppLog.TAG_SERVICE, "rootInActiveWindow null")
        return
    }
    try {
        val text = AccessibilityTextCollector.collectText(root)
        root.recycle()

        if (text.isBlank()) return
        AppLog.d(AppLog.TAG_SERVICE, "Accessibility text (${text.length} chars)")

        // Użyj istniejącego parsera — tekst z accessibility ma te same dane co OCR
        val lines = text.split("\n").filter { it.isNotBlank() }
        val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
        val offer = parser.parse(lines) ?: run {
            AppLog.d(AppLog.TAG_SERVICE, "Accessibility: parser returned null")
            return
        }

        val settings = ServiceLocator.settingsRepository.load()
        if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

        val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
        AppLog.d(AppLog.TAG_SERVICE, "Accessibility result: ${result.zlPerHour} zł/h → ${result.level}")

        // Pokaż overlay na main thread
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Main) {
            ServiceLocator.overlayManager.show(result, settings.display, settings.language)
            ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.display.displayTimeSeconds * 1000L)
        }
    } catch (e: Exception) {
        AppLog.w(AppLog.TAG_SERVICE, "Accessibility fallback error: ${e.message}")
    }
}
```

**UWAGA o `runBlocking`:** Jest OK tutaj bo to jest na coroutine scope (`throttler.onEvent` uruchamia na `Dispatchers.Default`), a `show()` wymaga Main thread. Alternatywnie użyj `withContext(Dispatchers.Main)` w ramach coroutine.

Lepsze podejście (jeśli throttler uruchamia w coroutine):
```kotlin
// Zamień runBlocking na:
scope.launch(Dispatchers.Main) {
    ServiceLocator.overlayManager.show(result, settings.display, settings.language)
    ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.display.displayTimeSeconds * 1000L)
}
```

---

## Krok 3: Expose w ServiceLocator

**Plik:** `CourierAssist/app/src/main/java/com/courierassist/app/di/ServiceLocator.kt`

Upewnij się że `parserRegistry`, `offerFilter`, `offerAnalyzer`, `overlayManager`, `overlayAutoHider`
są dostępne publicznie (prawdopodobnie już są, bo PipelineOrchestrator ich używa).

---

## Krok 4: Accessibility Service XML config

**Plik:** `CourierAssist/app/src/main/res/xml/accessibility_service_config.xml` (lub jak się nazywa)

Upewnij się że config zawiera:
```xml
android:canRetrieveWindowContent="true"
```
To jest WYMAGANE żeby `getRootInActiveWindow()` zwracał drzewo UI.

---

## Krok 5: Testy

1. **MediaProjection aktywna:** Uruchom apkę → Start → sprawdź że OCR pipeline działa jak dotychczas
2. **MediaProjection ginie:** Wygaś ekran → odblokuj → sprawdź w logach:
   - `"Accessibility text (X chars)"` — powinno się pojawić
   - `"Accessibility result: XX zł/h"` — powinno się pojawić
3. **Popup Ubera po screen off:** Otwórz Ubera → niech pojawi się zlecenie → sprawdź czy belka się pokazuje
4. **Bez MediaProjection w ogóle:** Nie klikaj Start → sprawdź czy accessibility fallback sam z siebie parsuje

---

## Czego NIE zmieniać

- `PipelineOrchestrator` — zostaje bez zmian
- `ScreenCaptureService` — zostaje bez zmian
- `UberOcrParser` — zostaje bez zmian (ten sam regex działa na tekst z accessibility)
- `EventThrottler` — zostaje bez zmian
- `OverlayManager` — zostaje bez zmian

---

## Podsumowanie plików do zmiany/utworzenia

| Plik | Akcja |
|------|-------|
| `service/AccessibilityTextCollector.kt` | NOWY — zbieranie tekstu z drzewa UI |
| `service/CourierAccessibilityService.kt` | ZMIANA — dodać dual-mode logic |
| `di/ServiceLocator.kt` | SPRAWDZIĆ — czy expose'uje potrzebne zależności |
| `res/xml/accessibility_service_config.xml` | SPRAWDZIĆ — `canRetrieveWindowContent="true"` |
