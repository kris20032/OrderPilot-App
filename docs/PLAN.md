# CourierAssist — Plan Implementacji Produkcyjnej v2

**Data:** 2026-03-03
**Status:** Zatwierdzony — gotowy do implementacji
**Branch:** `feature/production-app`

---

## Kontekst

POC udowodnił że pipeline działa (AccessibilityService → MediaProjection → OCR → Parser → Overlay). Teraz budujemy produkcyjną aplikację **od zera** na nowym branchu, bazując na architekturze z `docs/ARCHITECTURE.md`. Istniejący kod POC zostaje na `main` jako referencja — nie modyfikujemy go, nie kopiujemy z niego.

**Scope v1:** Uber tylko + interfejsy gotowe na Wolt/Glovo. Proste ustawienia (progi zł/h, język, metryki na belce). Unit testy razem z kodem dla warstw z czystą logiką.

---

## Zasady implementacji

### Git workflow
- **Branch:** `feature/production-app` (jeden branch na całość)
- **Commity:** per task, format: `[EPIC X] TASK X.Y: krótki opis`
- **Merge do main:** po EPIC 14 (E2E testy przechodzą, pipeline działa na telefonie)
- Commity tylko na prośbę użytkownika (zgodnie z RULES.md)

### ServiceLocator (DI) — rośnie z epicami
- ServiceLocator **NIE** jest jednorazowo wypełniany na końcu
- Po każdym epicu dodającym nowe klasy → od razu rejestrujemy je w `ServiceLocator.init()`
- Dzięki temu projekt kompiluje się i jest spójny po każdym epicu

### Podział testów
- **Unit testy (JVM):** `src/test/` — parser, engine, settings, EventThrottler. Szybkie, bez telefonu, odpalane w Android Studio
- **Testy UI (emulator):** MainActivity, SettingsActivity — czy się wyświetla, czy przyciski działają
- **Testy E2E (fizyczny telefon):** cały pipeline ze screenshotem — emulator nie obsługuje MediaProjection

### Działanie apki w tle
- Użytkownik otwiera apkę → klika START → przechodzi do Uber Driver lub dowolnej innej apki
- CourierAssist działa w tle (AccessibilityService + ScreenCaptureService z notyfikacją)
- Belka pojawia się automatycznie przy zleceniach — niezależnie od tego jaka apka jest na wierzchu
- Działa dopóki użytkownik nie wróci i nie kliknie STOP, lub nie wyłączy apki (force stop/swipe)
- Jeśli Android zabije ScreenCaptureService (brak RAM) → pipeline się zatrzymuje bez crasha, przy powrocie do MainActivity użytkownik widzi status i może ponownie kliknąć START

### Logowanie
- Każda warstwa loguje kluczowe eventy przez prosty `AppLog` wrapper (tag per warstwa)
- Logcat = jedyne narzędzie diagnostyczne na telefonie — bez logów debugowanie jest niemożliwe

---

## EPIC 1: Fundament projektu
> Gradle, Manifest, Application class, ServiceLocator, Logger — szkielet bez logiki.
> **Model: Sonnet**

### TASK 1.1 — Konfiguracja Gradle
- Wyczyścić `app/build.gradle.kts` — minSdk 26, targetSdk 35
- Dependencies: `kotlinx-coroutines-android`, `kotlinx-serialization-json`, `com.google.mlkit:text-recognition`
- Test dependencies: `junit`, `kotlinx-coroutines-test` w `testImplementation`
- Włączyć ViewBinding
- Dodać `kotlin("plugin.serialization")` do plugins

### TASK 1.2 — AndroidManifest.xml
- Uprawnienia: `SYSTEM_ALERT_WINDOW`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PROJECTION`
- Deklaracja: `CourierAssistApp` (application), `MainActivity`, `SettingsActivity`
- Deklaracja: `ScreenCaptureService` (foregroundServiceType="mediaProjection")
- Deklaracja: `CourierAccessibilityService` + meta-data z `accessibility_config.xml`

### TASK 1.3 — Notification Channel
- Stworzyć notification channel `"courier_assist_service"` w `CourierAssistApp.onCreate()`
- Nazwa: "CourierAssist nasłuchuje"
- Priorytet: LOW (nie irytuje użytkownika, ale utrzymuje ForegroundService przy życiu)
- Ikona: domyślna (na razie)
- **Bez tego ScreenCaptureService nie ruszy na Android 8+**

### TASK 1.4 — Struktura pakietów
- Stworzyć katalogi: `di/`, `domain/`, `engine/`, `parser/`, `capture/`, `ocr/`, `pipeline/`, `service/`, `overlay/`, `settings/`, `billing/`, `ui/`
- Stworzyć katalog testów: `src/test/java/com/courierassist/app/` z odpowiednimi podkatalogami

### TASK 1.5 — AppLog (logging wrapper)
- `di/AppLog.kt` — object z metodami `d(tag, msg)`, `w(tag, msg)`, `e(tag, msg, throwable?)`
- Wrapper na `android.util.Log` — w przyszłości łatwo podmienić na Timber/crashlytics
- Stałe tagi per warstwa: `TAG_PIPELINE`, `TAG_CAPTURE`, `TAG_OCR`, `TAG_PARSER`, `TAG_OVERLAY`, `TAG_SERVICE`

### TASK 1.6 — CourierAssistApp + ServiceLocator (stub)
- `di/CourierAssistApp.kt` — Application class, wywołuje `ServiceLocator.init()` + tworzy notification channel
- `di/ServiceLocator.kt` — object z `lateinit var` dla wszystkich zależności, `init()` na razie puste (rośnie z kolejnymi epicami)

**Weryfikacja:** Projekt buduje się w Android Studio bez błędów.

---

## EPIC 2: Domain — modele danych
> Czyste data classes i enums, zero zależności Android.
> **Model: Sonnet**

### TASK 2.1 — Enums
- `domain/Platform.kt` — `enum class Platform { UBER, WOLT, GLOVO, BOLT }`
- `domain/ProfitLevel.kt` — `enum class ProfitLevel { GREEN, YELLOW, RED }`
- `domain/MetricType.kt` — `enum class MetricType { ZL_PER_HOUR, ZL_PER_KM, DISTANCE, TIME, AMOUNT }`
- `domain/AppLanguage.kt` — `enum class AppLanguage { PL, UK, EN }`
- `domain/ThemeMode.kt` — `enum class ThemeMode { AUTO, LIGHT, DARK }`

### TASK 2.2 — Data classes
- `domain/Offer.kt` — `data class Offer(platform, amount, estimatedMinutes, distanceKm?, currency)`
- `domain/AnalysisResult.kt` — `data class AnalysisResult(offer, zlPerHour, zlPerKm?, level)`

**DI:** Domain nie wymaga rejestracji w ServiceLocator (czyste data classes).

**Weryfikacja:** Kompiluje się. Czysta logika, brak importów Android.

---

## EPIC 3: Settings — ustawienia i persystencja
> Modele ustawień + SharedPreferences repository.
> **Model: Sonnet**

### TASK 3.1 — Modele ustawień
- `settings/AppSettings.kt` — zawiera:
  - `ThresholdConfig` (greenMinZlPerHour=40, yellowMinZlPerHour=32)
  - `DisplayConfig` (visibleMetrics, themeMode)
  - `FilterConfig` (minDistanceKm?, maxDistanceKm?)
  - `PlatformSettings` (thresholds?, filters?)
  - `AppSettings` (language, display, globalThresholds, globalFilters, platformOverrides)
  - Metody: `thresholdsFor(platform)`, `filtersFor(platform)`
- Adnotacje `@Serializable` (kotlinx.serialization)

### TASK 3.2 — SettingsRepository
- `settings/SettingsRepository.kt` — interfejs: `load()`, `save()`, `addListener()`, `removeListener()`
- `settings/SharedPrefsSettingsRepository.kt` — implementacja z jednym kluczem JSON w SharedPreferences

### TASK 3.3 — Unit testy Settings (JVM)
- `src/test/` — **nie** `androidTest/`
- Test `AppSettings.thresholdsFor()` — globalne vs platform override
- Test `AppSettings.filtersFor()` — globalne vs platform override
- Test serializacji/deserializacji AppSettings (JSON round-trip)

**DI:** Zarejestrować `settingsRepository` w `ServiceLocator.init()`.

**Weryfikacja:** Unit testy przechodzą (`./gradlew test`).

---

## EPIC 4: Engine — analiza i filtrowanie ofert
> Logika biznesowa: obliczanie zł/h, klasyfikacja, filtrowanie.
> **Model: Sonnet**

### TASK 4.1 — OfferAnalyzer
- `engine/OfferAnalyzer.kt`
- Oblicza `zlPerHour = amount / (estimatedMinutes / 60.0)`
- Oblicza `zlPerKm = amount / distanceKm` (nullable)
- Klasyfikuje: GREEN/YELLOW/RED na podstawie ThresholdConfig
- Ochrona przed dzieleniem przez 0 (estimatedMinutes = 0)

### TASK 4.2 — OfferFilter
- `engine/OfferFilter.kt`
- `passes(offer, filters): Boolean` — sprawdza dystans w zakresie min/max
- Brak dystansu = przepuść (return true)

### TASK 4.3 — Unit testy Engine (JVM)
- OfferAnalyzer: normalna oferta, oferta bez dystansu, edge case 0 minut, progi graniczne (dokładnie 40 zł/h = GREEN)
- OfferFilter: dystans w zakresie, poza zakresem, brak dystansu, brak filtrów

**DI:** Zarejestrować `offerAnalyzer` i `offerFilter` w `ServiceLocator.init()`.

**Weryfikacja:** Wszystkie unit testy przechodzą.

---

## EPIC 5: Parser — rozpoznawanie ofert z OCR
> Interfejs parsera + implementacja Uber + registry.
> **Model: Opus** (regex, multi-język, edge cases)

### TASK 5.1 — Interfejs OcrOfferParser
- `parser/OcrOfferParser.kt` — interfejs z: `platform`, `supportedPackages`, `parse(ocrLines, language)`

### TASK 5.2 — UberOcrParser
- `parser/UberOcrParser.kt`
- Regex per język (PL, UK, EN) — kwota, czas, dystans
- `supportedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")`
- Helper: `toDoubleLocale()` (obsługa przecinka i kropki)
- Logowanie: `AppLog.d(TAG_PARSER, "Parsed offer: ...")` przy sukcesie, `AppLog.w` przy failure

### TASK 5.3 — ParserRegistry
- `parser/ParserRegistry.kt`
- `getParser(packageName): OcrOfferParser?` — szuka parsera po supportedPackages
- `getAllWatchedPackages(): Set<String>` — zbiera wszystkie supportedPackages (dla AccessibilityService)

### TASK 5.4 — Unit testy Parser (JVM)
- UberOcrParser: tekst PL ("34,58 zł", "9 min", "(2,5 km)"), tekst UK (грн, хв, км), tekst EN
- Edge cases: brak dystansu, złe formatowanie, puste linie, brak "Akceptuj"
- ParserRegistry: znaleziony parser, nieznany pakiet = null, getAllWatchedPackages

**DI:** Zarejestrować `parserRegistry` (z `UberOcrParser`) w `ServiceLocator.init()`.

**Weryfikacja:** Wszystkie unit testy przechodzą. Parser poprawnie parsuje przykłady z POC.

---

## EPIC 6: Capture — screenshot przez MediaProjection
> ForegroundService + PopupCropper.
> **Model: Opus** (MediaProjection API, lifecycle, edge cases)

### TASK 6.1 — ScreenCaptureService
- `capture/ScreenCaptureService.kt` — ForegroundService
- Companion: `startCapture(context, resultCode, data)`, `stopCapture(context)`
- `@Volatile var instance` — singleton pattern
- `isReady(): Boolean`
- `suspend fun capture(): Bitmap?` — ImageReader → Bitmap (z obsługą row padding)
- Używa notification channel z TASK 1.3 ("CourierAssist nasłuchuje")
- Callback na utratę MediaProjection → `isReady = false`, log warning
- **Android 14+ (API 34):** MediaProjection wymaga explicit user consent **przy każdym uruchomieniu sesji** — uwzględnić w flow (MainActivity musi requestować consent za każdym razem gdy startuje capture)
- Logowanie: `AppLog.d(TAG_CAPTURE, ...)` przy starcie/stopie/błędach

### TASK 6.2 — PopupCropper
- `capture/PopupCropper.kt`
- `crop(fullScreenBitmap): Bitmap` — przycina od `cropStartRatio` (default 0.40) w dół

**DI:** Zarejestrować `popupCropper` w `ServiceLocator.init()`. ScreenCaptureService rejestruje się sam przez `instance`.

**Weryfikacja:** Buduje się. Testowanie manualne na fizycznym telefonie w EPIC 14.

---

## EPIC 7: OCR — wrapper ML Kit
> Minimalna warstwa nad ML Kit TextRecognition.
> **Model: Sonnet**

### TASK 7.1 — OcrEngine
- `ocr/OcrEngine.kt`
- `suspend fun recognize(bitmap): List<String>` — suspendCancellableCoroutine
- `fun close()` — zamyka recognizer
- Zwraca listę linii tekstu (textBlocks → lines → text)
- Logowanie: `AppLog.d(TAG_OCR, "Recognized ${lines.size} lines")`, log pierwszych kilka linii dla debug

**DI:** Zarejestrować `ocrEngine` w `ServiceLocator.init()`.

**Weryfikacja:** Buduje się. Test manualny na fizycznym telefonie.

---

## EPIC 8: Overlay — belka wynikowa
> Wyświetlanie kolorowej belki z wynikami na ekranie.
> **Model: Sonnet**

### TASK 8.1 — Layout XML
- `res/layout/overlay_offer.xml` — prosty layout: TextView z paddingiem, tło ustawiane programowo

### TASK 8.2 — OverlayManager + SystemOverlayManager
- `overlay/OverlayManager.kt` — interfejs: `show(result, displayConfig)`, `hide()`, `isShowing()`
- `overlay/SystemOverlayManager.kt` — implementacja z WindowManager, TYPE_APPLICATION_OVERLAY, Gravity.TOP
- Logowanie: `AppLog.d(TAG_OVERLAY, "Showing overlay: ...")`, `"Hiding overlay"`

### TASK 8.3 — OverlayViewFactory
- `overlay/OverlayViewFactory.kt` — object
- Buduje tekst z wybranych metryk (zł/h, zł/km, kwota, czas, dystans)
- Ustawia kolor tła: GREEN=zielony, YELLOW=pomarańczowy, RED=czerwony (semi-transparent)

### TASK 8.4 — OverlayAutoHider
- `overlay/OverlayAutoHider.kt`
- `onOverlayShown(scope)` — schedule hide po 15s (czas popupu Uber)
- `hideNow(scope)` — natychmiastowe ukrycie

**DI:** Zarejestrować `overlayManager` i `overlayAutoHider` w `ServiceLocator.init()`.

**Weryfikacja:** Buduje się. Test manualny — belka pojawia się i znika.

---

## EPIC 9: Pipeline — orkiestracja
> Łączy wszystkie warstwy w jeden przepływ.
> **Model: Opus** (koordynacja coroutines, error handling)

### TASK 9.1 — PipelineOrchestrator
- `pipeline/PipelineOrchestrator.kt`
- `fun process(packageName)` — launch coroutine:
  1. capture screenshot → `AppLog.d(TAG_PIPELINE, "Screenshot captured")`
  2. crop
  3. OCR recognize
  4. find parser → parse
  5. filter
  6. analyze
  7. show overlay (Main dispatcher) + trigger OverlayAutoHider
- `fun cancel()` — scope.cancel()
- Każdy krok null-safe — return na failure bez crasha, z logiem `AppLog.w` wyjaśniającym gdzie pipeline się zatrzymał
- Recycle bitmap po użyciu

**DI:** Zarejestrować `pipelineOrchestrator` w `ServiceLocator.init()`.

**Weryfikacja:** Buduje się. Integracja testowana w EPIC 14.

---

## EPIC 10: Service — AccessibilityService + throttling
> Rdzeń nasłuchiwania eventów z aplikacji kurierskich.
> **Model: Opus** (AccessibilityService lifecycle, edge cases)

### TASK 10.1 — EventThrottler
- `service/EventThrottler.kt`
- First-shot delay: 300ms (screenshot przy pierwszym evencie, nie czekamy na koniec serii)
- Cooldown: 5000ms (ignoruj kolejne eventy po triggerze)
- Coroutine-based
- Logowanie: `AppLog.d(TAG_SERVICE, "Event throttled")` / `"Event triggered"`

### TASK 10.2 — CourierAccessibilityService
- `service/CourierAccessibilityService.kt`
- `onServiceConnected()` — pobiera pipeline z ServiceLocator
- `onAccessibilityEvent()` — filtruje po pakiecie i typie eventu, throttle → pipeline.process()
- `onDestroy()` — scope.cancel()
- watchedPackages z `ParserRegistry.getAllWatchedPackages()` (nie hardcoded)
- Logowanie: `AppLog.d(TAG_SERVICE, "Event from $pkg")` (tylko watchedPackages)

### TASK 10.3 — accessibility_config.xml
- `res/xml/accessibility_config.xml`
- eventTypes: windowContentChanged, windowStateChanged
- packageNames: com.ubercab.driver, com.ubercab.eats
- feedbackType: generic
- flags: flagReportViewIds

### TASK 10.4 — Unit test EventThrottler (JVM)
- Test: pierwszy event → action po 300ms
- Test: drugi event w cooldownie → ignorowany
- Test: event po cooldownie → action znowu

**Weryfikacja:** Testy przechodzą. Buduje się.

---

## EPIC 11: UI — MainActivity
> Ekran główny: START/STOP, wskaźniki uprawnień, nawigacja do ustawień.
> **Model: Sonnet**

### TASK 11.1 — Layout activity_main.xml
- Duży przycisk START/STOP (toggle)
- 3 wskaźniki statusu: Accessibility ✓/✗, MediaProjection ✓/✗, Overlay ✓/✗
- Ikona gear → SettingsActivity
- Pole informacyjne (porady)

### TASK 11.2 — MainActivity.kt
- Sprawdzanie uprawnień w `onResume()` (aktualizuje wskaźniki przy każdym powrocie do apki):
  - `Settings.canDrawOverlays()` → overlay
  - `AccessibilityManager.getEnabledAccessibilityServiceList()` → accessibility
  - `ScreenCaptureService.instance?.isReady()` → mediaProjection
- **START flow:**
  1. Sprawdź uprawnienia — jeśli brakuje → prowadzi krok po kroku (overlay → accessibility → mediaProjection)
  2. Requestuj MediaProjection consent (`registerForActivityResult`) — **za każdym razem** (Android 14+ wymaga)
  3. Uruchom ScreenCaptureService z resultCode/data z MediaProjection
  4. Zapisz isEnabled=true
- **STOP flow:**
  1. Zatrzymaj ScreenCaptureService
  2. Ukryj overlay
  3. Zapisz isEnabled=false
- **onResume po powrocie z tła:** sprawdź czy serwisy żyją, zaktualizuj wskaźniki. Jeśli ScreenCaptureService umarł (Android zabił) → pokaż info że trzeba ponownie kliknąć START

### TASK 11.3 — Resources
- `res/values/strings.xml` — stringi PL (domyślne)
- `res/values/colors.xml` — kolory UI + GREEN/YELLOW/RED
- `res/values/themes.xml` — theme jasny
- `res/values-night/themes.xml` — theme ciemny (na razie auto)

**Weryfikacja:** Apka uruchamia się na emulatorze. Przycisk START/STOP działa, wskaźniki uprawnień poprawne. Flow uprawnień krok po kroku.

---

## EPIC 12: UI — SettingsActivity (proste v1)
> Podstawowe ustawienia: progi zł/h, język, metryki na belce.
> **Model: Sonnet**

### TASK 12.1 — Layout activity_settings.xml
- Sekcja: Progi zł/h — 2 pola (green, yellow) z EditText/NumberPicker
- Sekcja: Co wyświetlać na belce — checkboxy (MetricType: zł/h, zł/km, kwota, czas, dystans)
- Sekcja: Język — radio (PL/UK/EN)
- Przycisk Zapisz / auto-save

### TASK 12.2 — SettingsActivity.kt
- Ładuje AppSettings z SettingsRepository
- Wyświetla aktualne wartości
- Zapisuje zmiany do SettingsRepository
- Walidacja: yellowMin < greenMin

**Weryfikacja:** Ustawienia zapisują się na emulatorze. Zmiana progów wpływa na kolor belki (E2E na telefonie w EPIC 14).

---

## EPIC 13: Billing stub + weryfikacja DI
> Ostatnie brakujące elementy + sprawdzenie że ServiceLocator jest kompletny.
> **Model: Sonnet**

### TASK 13.1 — Billing stub
- `billing/FeatureGate.kt` — `isPro() = true`, `canUsePlatform() = true` (v1: wszystko odblokowane)

### TASK 13.2 — Weryfikacja ServiceLocator
- Przejrzeć `ServiceLocator.init()` — czy wszystkie zależności są zarejestrowane w prawidłowej kolejności
- Upewnić się że nie ma żadnych `lateinit` które nie zostały zainicjalizowane
- Upewnić się że projekt buduje się czysto bez warningów

**Weryfikacja:** Apka buduje się i startuje bez crashy. Żadnych `UninitializedPropertyAccessException`.

---

## EPIC 14: Testy E2E + polish
> Integracja end-to-end z FakeUberDriver, naprawianie bugów.
> **Model: Opus**

### TASK 14.1 — Test E2E na fizycznym telefonie
Instrukcje dla użytkownika:
1. Zainstaluj CourierAssist APK na telefonie
2. Zainstaluj FakeUberDriver na telefonie
3. Otwórz CourierAssist → START (nadaj wszystkie uprawnienia)
4. Przejdź do FakeUberDriver → wygeneruj popup

Scenariusze do sprawdzenia:
- [ ] Popup FakeUberDriver → belka pojawia się <1s z poprawnym kolorem
- [ ] Belka znika po ~15s
- [ ] Kolejny popup → nowa belka (po cooldown 5s)
- [ ] Zmiana ustawień (progi) → następna belka odzwierciedla nowe progi
- [ ] STOP → belka się nie pojawia przy kolejnym popupie
- [ ] Ponowne START → znowu działa
- [ ] Apka w tle (przejście do innej apki) → pipeline nadal działa
- [ ] Logcat: logi z każdej warstwy pipeline'u widoczne

### TASK 14.2 — Naprawa bugów z testów
- Fix znalezionych problemów z E2E
- Iteracja aż wszystkie scenariusze z TASK 14.1 przechodzą

### TASK 14.3 — Aktualizacja dokumentacji
- Zaktualizować `PROGRESS.md` — status każdego epicu
- Zaktualizować `RULES.md` sekcja 9 — dodać nowe pakiety (capture/, ocr/, pipeline/, di/)
- Zaktualizować `docs/PLAN.md` — oznaczyć taski jako ukończone

**Weryfikacja:** Pełny pipeline działa na telefonie. Wszystkie scenariusze z TASK 14.1 przechodzą. Dokumentacja aktualna.

---

## Podsumowanie

| Epic | Opis | Model | Tasków |
|------|------|-------|--------|
| 1 | Fundament (Gradle, Manifest, Logger, DI stub) | Sonnet | 6 |
| 2 | Domain (modele danych) | Sonnet | 2 |
| 3 | Settings (ustawienia + repo + testy) | Sonnet | 3 |
| 4 | Engine (analiza + filtrowanie + testy) | Sonnet | 3 |
| 5 | Parser (OCR parser Uber + testy) | **Opus** | 4 |
| 6 | Capture (MediaProjection + cropper) | **Opus** | 2 |
| 7 | OCR (ML Kit wrapper) | Sonnet | 1 |
| 8 | Overlay (belka + auto-hide) | Sonnet | 4 |
| 9 | Pipeline (orkiestracja) | **Opus** | 1 |
| 10 | Service (AccessibilityService + testy) | **Opus** | 4 |
| 11 | UI — MainActivity | Sonnet | 3 |
| 12 | UI — SettingsActivity | Sonnet | 2 |
| 13 | Billing stub + weryfikacja DI | Sonnet | 2 |
| 14 | Testy E2E + polish | **Opus** | 3 |
| **Razem** | | | **40 tasków** |

## Kolejność implementacji

Liniowa, od dołu (domain) do góry (UI), z E2E na końcu:
```
EPIC 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11 → 12 → 13 → 14
```

Każdy epic kończy się:
1. Kod kompiluje się
2. Nowe klasy zarejestrowane w ServiceLocator
3. Unit testy przechodzą (jeśli epic je zawiera)
4. Commit: `[EPIC X] TASK X.Y: opis`

## Weryfikacja końcowa (po EPIC 14)

1. **Unit testy (JVM)** przechodzą dla: Settings, Engine, Parser, EventThrottler
2. **Build** w Android Studio bez błędów i warningów
3. **E2E na fizycznym telefonie:** FakeUberDriver popup → belka <1s → auto-hide 15s
4. **Działanie w tle:** apka działa po przejściu do Uber Driver
5. **Ustawienia:** zmiana progów → następna belka odzwierciedla
6. **Uprawnienia:** wskaźniki w MainActivity poprawne, flow krok po kroku
7. **Logcat:** logi z każdej warstwy widoczne i sensowne
8. **Merge** `feature/production-app` → `main`
