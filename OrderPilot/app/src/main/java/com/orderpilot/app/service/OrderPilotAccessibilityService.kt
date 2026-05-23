package com.orderpilot.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.PowerManager
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import androidx.annotation.RequiresApi
import com.orderpilot.app.capture.ScreenCaptureService
import com.orderpilot.app.di.AppLog
import com.orderpilot.app.di.MonitoringController
import com.orderpilot.app.di.ServiceLocator
import com.orderpilot.app.domain.AnalysisResult
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import com.orderpilot.app.pipeline.OfferDuplicateChecker
import com.orderpilot.app.pipeline.PipelineOrchestrator
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class OrderPilotAccessibilityService : AccessibilityService() {

    private lateinit var pipeline: PipelineOrchestrator
    private val throttlers = mutableMapOf<String, EventThrottler>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val lastResults = ConcurrentHashMap<Platform, AnalysisResult>()
    private val lastResultTimes = ConcurrentHashMap<Platform, Long>()
    private val resultExpiryMs = 60_000L
    private var lastWindowDiagTime = 0L
    @Volatile private var lastUberEventTime = 0L
    @Volatile private var lastBoltEventTime = 0L
    @Volatile private var isRetrying = false
    @Volatile private var uberWatchJob: Job? = null
    @Volatile private var boltWatchJob: Job? = null
    @Volatile private var hadUberOverlayWindow = false
    @Volatile private var consecutiveScreenshotFailures = 0
    private var screenshotAlertShown = false

    // Layer 1: Foreground tracker — aktualizowany przy KAŻDYM TYPE_WINDOW_STATE_CHANGED
    // (niezależnie czy package jest w watchedPackages). Trzymamy ostatni package,
    // żeby pipeline mógł zweryfikować że apka której parser ma być użyty
    // nadal jest foreground w momencie capture (po throttle/retry/watch delay).
    // Nie polegamy wyłącznie na rootInActiveWindow bo na MIUI bywa stale po app switch.
    @Volatile private var lastForegroundPackage: String? = null
    @Volatile private var lastForegroundChangeTs = 0L

    override fun onServiceConnected() {
        pipeline = ServiceLocator.pipelineOrchestrator
        isConnected = true
        serviceInstance = this
        AppLog.d(AppLog.TAG_SERVICE, "AccessibilityService connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (!::pipeline.isInitialized) return // event przed onServiceConnected()
        if (!MonitoringController.isActive()) return

        // TYPE_WINDOWS_CHANGED = event SYSTEMOWY (WindowManager) — nowe okno pojawiło się/zniknęło.
        // packageName jest null bo pochodzi z systemu, nie z aplikacji.
        // Sprawdzamy getWindows() czy jest overlay Ubera → łapie popupy nad Samsung launcher
        // gdzie TYPE_WINDOW_STATE_CHANGED nie przychodzi.
        if (event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            handleWindowsChanged()
            return
        }

        val pkg = event.packageName?.toString() ?: return

        // Layer 1: Foreground tracker — aktualizujemy PRZED watchedPackages filter.
        // TYPE_WINDOW_STATE_CHANGED z dowolnej apki jest sygnałem zmiany foregroundu.
        // Pomija systemowe overlay (input method, status bar) — tylko package z prawdziwą activity.
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val isSystemUi = pkg == "com.android.systemui" ||
                pkg.startsWith("com.google.android.inputmethod") ||
                pkg.contains(".inputmethod") ||
                pkg == "android"
            if (!isSystemUi) {
                lastForegroundPackage = pkg
                lastForegroundChangeTs = System.currentTimeMillis()
                // Layer 3: Watch mode reset — gdy user przeszedł do apki spoza watchedPackages,
                // przerywamy aktywne watch jobs żeby nie strzelały screenshotów do innej apki
                // przez kolejne 60s. Zamyka okno false-positive z 60s do <2.5s (do następnego ticka).
                if (pkg !in watchedPackages) {
                    if (uberWatchJob?.isActive == true || boltWatchJob?.isActive == true) {
                        AppLog.d(AppLog.TAG_SERVICE, "Foreground switched to non-courier app ($pkg) — cancelling watch jobs")
                        uberWatchJob?.cancel()
                        boltWatchJob?.cancel()
                        lastUberEventTime = 0L
                        lastBoltEventTime = 0L
                    }
                }
            }
        }

        if (pkg !in watchedPackages) return
        AppLog.d(AppLog.TAG_SERVICE, "RAW event: pkg=$pkg type=${event.eventType}")
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        AppLog.d(AppLog.TAG_SERVICE, "Event from $pkg")

        // Uber watch mode: aktualizuj timestamp i uruchom periodic check
        if (pkg == "com.ubercab.driver") {
            lastUberEventTime = System.currentTimeMillis()
            startUberWatchIfNeeded()
        }

        val throttler = throttlers.getOrPut(pkg) { EventThrottler() }

        // Bolt watch mode: aktualizuj timestamp i uruchom periodic tree check
        if (pkg == "com.bolt.deliverycourier") {
            lastBoltEventTime = System.currentTimeMillis()
            startBoltWatchIfNeeded()
        }

        // Glovo/Bolt: accessibility tree first (natywne UI — tekst widoczny w drzewie)
        val platform = ServiceLocator.parserRegistry.getParser(pkg)?.platform
        if (platform == com.orderpilot.app.domain.Platform.GLOVO || platform == com.orderpilot.app.domain.Platform.BOLT) {
            throttler.onEvent(scope) { processViaAccessibilityTree(pkg) }
            return
        }

        // Screenshot pipeline (Uber/Wolt) — ochrona przed cross-contamination.
        // takeScreenshot() robi zrzut CAŁEGO ekranu, więc jeśli np. Wolt generuje eventy
        // w tle a na ekranie jest popup Ubera — WoltOcrParser sparsuje go jako ofertę Wolt.
        //
        // Uber pokazuje popupy jako overlay NAD KAŻDĄ apką (launcher, Wolt, bank itp.)
        // więc eventów z Ubera NIGDY nie skipujemy — popup jest zawsze widoczny.
        // Skipujemy TYLKO eventy z Wolta gdy foreground to inna apka kurierska,
        // bo Wolt generuje "szum" (eventy 2048) bez widocznego popupu.
        if (isRivalInForeground(pkg)) {
            AppLog.d(AppLog.TAG_SERVICE, "Skipping screenshot for $pkg — rival platform in foreground")
            return
        }

        // Uber false trigger reduction: jeśli event to CONTENT_CHANGED (mapa/UI scroll)
        // i Uber NIE jest foreground i nie ma overlay popupu → skip screenshot.
        // Gdy Uber JEST foreground: popup jest WEWNĄTRZ okna apki (type=1, nie overlay),
        // więc hasUberOverlayWindow() zwraca false — musimy przepuścić event do throttlera.
        // Throttler ogranicza do 1 screenshot/1.6s, parser odfiltruje mapę (zwróci null).
        if (pkg == "com.ubercab.driver" && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (!isUberForeground() && !hasUberOverlayWindow()) {
                AppLog.d(AppLog.TAG_SERVICE, "Uber: CONTENT_CHANGED but no overlay window and not foreground — skipping")
                return
            }
        }

        // Primary: MediaProjection pipeline (jeśli aktywna) + Fallback: takeScreenshot (API 30+)
        throttler.onEvent(scope) {
            if (!MonitoringController.isActive()) return@onEvent
            // Context validation: rival mógł wejść na foreground podczas throttle delay (100ms)
            if (isRivalInForeground(pkg)) {
                AppLog.d(AppLog.TAG_SERVICE, "$pkg: rival in foreground after throttle — aborting pipeline")
                return@onEvent
            }

            // Diagnostyka: loguj okna Ubera gdy user jest w apce (throttle 10s)
            if (pkg == "com.ubercab.driver") {
                logUberWindowDiagnostics()
            }

            // Layer 1: foreground guard przed MediaProjection path.
            // processViaScreenshot ma własny guard wewnątrz; tu chronimy alternatywną ścieżkę.
            if (!isForegroundOfPackage(pkg)) {
                AppLog.d(AppLog.TAG_SERVICE, "$pkg: foreground mismatch after throttle — aborting pipeline")
                return@onEvent
            }

            if (isMediaProjectionAvailable()) {
                pipeline.process(pkg)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                processViaScreenshot(pkg)
            }

            // Spaced retries dla platform screenshot-owych (Uber, Wolt).
            // Popup może nie być wyrenderowany w momencie pierwszego screenshota.
            // Uber (React Native): 50-2500ms na rendering → 4 retries.
            // Wolt (natywne UI): szybszy rendering → 2 retries (safety net).
            // Retry co 600ms unika errorCode=3 (Android rate-limit na takeScreenshot).
            // Early exit: jeśli belka się pokazała → stop (bez marnowania zasobów).
            val plat = ServiceLocator.parserRegistry.getParser(pkg)?.platform
            if ((plat == Platform.UBER || plat == Platform.WOLT) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val maxRetries = if (plat == Platform.UBER) UBER_MAX_RETRIES else WOLT_MAX_RETRIES
                isRetrying = true
                try {
                    for (i in 1..maxRetries) {
                        delay(RETRY_DELAY_MS)
                        if (!MonitoringController.isActive()) break
                        if (ServiceLocator.overlayManager.getActiveOffers()[plat] != null) {
                            AppLog.d(AppLog.TAG_SERVICE, "${plat.name}: overlay shown after $i retries — stopping")
                            break
                        }
                        // Context validation: rival mógł wejść na foreground między retryami
                        if (isRivalInForeground(pkg)) {
                            AppLog.d(AppLog.TAG_SERVICE, "${plat.name}: rival in foreground during retry $i — stopping")
                            break
                        }
                        // Uber: sprawdź czy overlay okno nadal istnieje (user mógł przełączyć apkę)
                        if (plat == Platform.UBER && !hasUberOverlayWindow()) {
                            AppLog.d(AppLog.TAG_SERVICE, "UBER: overlay window gone during retries — stopping")
                            break
                        }
                        AppLog.d(AppLog.TAG_SERVICE, "${plat.name}: spaced retry $i/$maxRetries (T+${i * RETRY_DELAY_MS}ms)")
                        processViaScreenshot(pkg, retryIndex = i)
                    }
                } finally {
                    isRetrying = false
                }
            }
        }
    }

    /**
     * TYPE_WINDOWS_CHANGED handler — event systemowy (WindowManager).
     * Sprawdza getWindows() czy pojawiło się okno overlay Ubera.
     * Łapie popupy nad Samsung launcher, gdzie TYPE_WINDOW_STATE_CHANGED nie przychodzi.
     */
    private fun handleWindowsChanged() {
        if (isRetrying) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        // State transition: triggeruj TYLKO gdy overlay jest NOWY (nie było go wcześniej).
        // Na Xiaomi Uber trzyma stały pusty overlay (type=3) — bez tego checku
        // każdy WINDOWS_CHANGED (np. od Wolta) triggerowałby fałszywy screenshot.
        // Na Samsungu overlay pojawia się TYLKO z popupem → transition false→true → trigger.
        val hasOverlay = hasUberOverlayWindow()
        val isNewOverlay = !hadUberOverlayWindow && hasOverlay
        hadUberOverlayWindow = hasOverlay // aktualizuj ZAWSZE — śledzi zniknięcie overlay
        if (!isNewOverlay) return

        // Jeśli belka Ubera już widoczna — nie robimy nic
        if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) return

        AppLog.d(AppLog.TAG_SERVICE, "WINDOWS_CHANGED: detected Uber overlay window — triggering screenshot")
        lastUberEventTime = System.currentTimeMillis()
        startUberWatchIfNeeded()

        val throttler = throttlers.getOrPut("com.ubercab.driver") { EventThrottler() }
        throttler.onEvent(scope) {
            if (!MonitoringController.isActive()) return@onEvent
            logUberWindowDiagnostics()
            // Layer 1: foreground guard przed MediaProjection path.
            // Uber popup overlay nad inną apką jest legalny — isForegroundOfPackage
            // przepuści go gdy hasUberOverlayWithContent() (Layer 2) potwierdzi prawdziwą treść.
            if (!isForegroundOfPackage("com.ubercab.driver")) {
                AppLog.d(AppLog.TAG_SERVICE, "WINDOWS_CHANGED: foreground mismatch — aborting pipeline")
                return@onEvent
            }
            if (isMediaProjectionAvailable()) {
                pipeline.process("com.ubercab.driver")
            } else {
                processViaScreenshot("com.ubercab.driver")
            }

            // Spaced retries — popup React Native może potrzebować czasu na rendering
            isRetrying = true
            try {
                for (i in 1..UBER_MAX_RETRIES) {
                    delay(RETRY_DELAY_MS)
                    if (!MonitoringController.isActive()) break
                    if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) {
                        AppLog.d(AppLog.TAG_SERVICE, "UBER: overlay shown after $i retries (WINDOWS_CHANGED) — stopping")
                        break
                    }
                    if (!hasUberOverlayWindow()) {
                        AppLog.d(AppLog.TAG_SERVICE, "UBER: overlay window gone during retries (WINDOWS_CHANGED) — stopping")
                        break
                    }
                    // Od retry 2: jeśli rival courier na ekranie i overlay Ubera pusty → phantom overlay, stop
                    if (i >= 2 && isRivalCourierInForeground() && !hasUberOverlayWithContent()) {
                        AppLog.d(AppLog.TAG_SERVICE, "UBER: rival foreground + phantom overlay — stopping retries at $i")
                        break
                    }
                    AppLog.d(AppLog.TAG_SERVICE, "UBER: spaced retry $i/$UBER_MAX_RETRIES via WINDOWS_CHANGED (T+${i * RETRY_DELAY_MS}ms)")
                    processViaScreenshot("com.ubercab.driver", retryIndex = i)
                }
            } finally {
                isRetrying = false
            }
        }
    }

    /**
     * Sprawdza czy na ekranie jest overlay okno Ubera (popup zlecenia).
     * Z logów wiemy:
     *   - Mapa (bez popupu): Window[1] type=1 pkg=com.ubercab.driver (type=1 = APPLICATION)
     *   - Popup nad WhatsApp: Window[1] type=3 pkg=com.ubercab.driver (type=3 = overlay)
     * Popup zlecenia = okno Ubera z type != TYPE_APPLICATION (1).
     * Gdy popup jest nad inną apką, główne okno Ubera nie jest widoczne,
     * więc liczyć okna >= 2 nie działa — trzeba sprawdzać typ.
     */
    /**
     * Sprawdza czy Uber jest aktywną (foreground) apką na ekranie.
     * Gdy Uber jest foreground, popup zlecenia jest renderowany WEWNĄTRZ okna apki (type=1),
     * nie jako osobne overlay okno (type=3) — więc hasUberOverlayWindow() zwraca false.
     */
    private fun isUberForeground(): Boolean {
        return try {
            rootInActiveWindow?.packageName?.toString() == "com.ubercab.driver"
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Uniwersalny context validation: czy inna apka kurierska jest na foreground?
     * Fail-open: null (nieznany foreground) → false → screenshot przechodzi.
     * Uber exempt: jego popup jest overlay nad każdą apką — foreground to zawsze inna apka.
     */
    private fun isRivalInForeground(pkg: String): Boolean {
        if (pkg == "com.ubercab.driver") return false
        val activePackage = try {
            rootInActiveWindow?.packageName?.toString()
        } catch (e: Exception) { null }
        return activePackage != null && activePackage != pkg && activePackage in watchedPackages
    }

    /**
     * Layer 1: Strict foreground guard — kombinacja trackera (z TYPE_WINDOW_STATE_CHANGED)
     * i live'owego rootInActiveWindow. Zwraca true jeśli AKTUALNY foreground to pkg.
     *
     * Wyjątek dla Ubera: popup oferty Ubera renderowany jest jako overlay nad inną apką
     * (popup OVER WhatsApp / launcher / cokolwiek), więc Uber nie musi być foreground —
     * wystarczy że istnieje overlay window Ubera (type != APPLICATION). Wcześniej
     * wymagaliśmy też że tekst overlay zawiera markery oferty (hasUberOverlayWithContent),
     * ale Uber Driver (React Native) na większości urządzeń NIE eksponuje tekstu popupu
     * przez accessibility tree (`Window Uber text: len=0` w logach Marcin/Samsung 2026-05-13).
     * Layer 4 (positive markers w UberOcrParser — "Łącznie"/"Akceptuj"/"Доставка") sam
     * odfiltruje portal newsowy/social/inne false-positive po OCR, więc warstwa tekstowa
     * w `hasUberOverlayWithContent` była zbędna a powodowała regresję od v1.0.2.
     *
     * Tracker (lastForegroundPackage) jest ground truth gdy nie jest stale (< 5s temu).
     * rootInActiveWindow użyte jako sanity check — jeśli oba się zgadzają = trust.
     */
    private fun isForegroundOfPackage(pkg: String): Boolean {
        // Uber popup overlay edge case — overlay window wystarczy, OCR+parser markery decydują
        if (pkg == "com.ubercab.driver" && hasUberOverlayWindow()) return true

        val tracker = lastForegroundPackage
        val live = try {
            rootInActiveWindow?.packageName?.toString()
        } catch (e: Exception) { null }

        // Najmocniejsze potwierdzenie: oba źródła zgodne
        if (tracker == pkg && live == pkg) return true

        // Świeży tracker (< 5s) sam wystarczy gdy live'a nie ma (race / null)
        val trackerAgeMs = System.currentTimeMillis() - lastForegroundChangeTs
        if (tracker == pkg && trackerAgeMs < 5_000L && live == null) return true

        // Live tylko (np. tracker nie złapał WINDOW_STATE_CHANGED) — akceptujemy
        if (live == pkg) return true

        return false
    }

    private fun hasUberOverlayWindow(): Boolean {
        return try {
            windows?.any { w ->
                val root = w.root
                val pkg = try { root?.packageName?.toString() } finally { root?.recycle() }
                pkg == "com.ubercab.driver" && w.type != AccessibilityWindowInfo.TYPE_APPLICATION
            } ?: false
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "hasUberOverlayWindow error: ${e.message}")
            false
        }
    }

    /**
     * Layer 2: Wzmocniony detektor real-popup vs phantom overlay.
     * Phantom overlay (Xiaomi) = type=3 z pustym lub niezwiązanym tekstem (mapy, toast, accessibility labels).
     * Prawdziwy popup = ma jednocześnie kwotę (zł/€/UAH) ORAZ jednostkę czasu (min/хв)
     * w bliskiej odległości znaków (typowy rozkład popupu oferty).
     * Alternatywnie: zawiera markery typowe dla popupu Ubera (Łącznie/Total/Akceptuj/Accept).
     *
     * Ten guard zapobiega fałszywym screenshotom triggerowanym przez phantom overlay
     * Xiaomi gdy user jest na innej apce (np. portal newsowy).
     */
    private fun hasUberOverlayWithContent(): Boolean {
        return try {
            windows?.any { w ->
                val root = w.root
                try {
                    val pkg = root?.packageName?.toString()
                    if (pkg != "com.ubercab.driver" || w.type == AccessibilityWindowInfo.TYPE_APPLICATION) return@any false
                    val text = root?.let { AccessibilityTextCollector.collectText(it) } ?: ""
                    if (text.isBlank() || text.length < 8) return@any false
                    overlayTextLooksLikeOffer(text)
                } finally {
                    root?.recycle()
                }
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Layer 2 helper: czy tekst overlay wygląda na popup oferty Ubera.
     * Akceptuje (1) kwotę walutową w pobliżu jednostki czasu lub (2) konkretny marker Ubera.
     */
    private fun overlayTextLooksLikeOffer(text: String): Boolean {
        // (1) Marker UI Ubera typowy dla popupu oferty (PL/EN/UA/RU)
        val uberOfferMarkers = listOf(
            "Łącznie", "Lacznie", "Akceptuj", "Accept",
            "Total", "Загалом", "Прийняти",
            "Итого", "Принять", "Доставка"
        )
        if (uberOfferMarkers.any { text.contains(it, ignoreCase = true) }) return true

        // (2) Wzorzec popupu: kwota waluty w pobliżu (≤120 znaków) jednostki czasu
        val moneyRegex = Regex("""\d+[.,]?\d*\s*(?:zł|PLN|€|EUR|UAH|грн|₴)""", RegexOption.IGNORE_CASE)
        val timeRegex = Regex("""\d+\s*(?:min|хв|мин)""", RegexOption.IGNORE_CASE)
        val moneyMatch = moneyRegex.find(text) ?: return false
        val timeMatch = timeRegex.find(text) ?: return false
        val distance = kotlin.math.abs(moneyMatch.range.first - timeMatch.range.first)
        return distance <= 120
    }

    /**
     * Sprawdza czy foreground to inna apka kurierska (nie Uber).
     * Używane w watch mode i retries, żeby nie screenshotować popupu rivala.
     */
    private fun isRivalCourierInForeground(): Boolean {
        val activePackage = try {
            rootInActiveWindow?.packageName?.toString()
        } catch (e: Exception) { null }
        return activePackage != null && activePackage != "com.ubercab.driver" && activePackage in watchedPackages
    }

    /**
     * Per-platform duplicate check: porównuje nowy wynik z ostatnim wynikiem
     * DLA TEJ SAMEJ platformy. Zapobiega niepotrzebnym aktualizacjom belki.
     */
    private fun isSameAsPrevious(platform: Platform, result: AnalysisResult): Boolean {
        val now = System.currentTimeMillis()
        val lastTime = lastResultTimes[platform] ?: 0L
        if (now - lastTime > resultExpiryMs) lastResults.remove(platform)

        val prev = lastResults[platform]
        if (prev != null &&
            result.level == prev.level &&
            result.offer.amount == prev.offer.amount &&
            result.offer.estimatedMinutes == prev.offer.estimatedMinutes &&
            result.offer.distanceKm == prev.offer.distanceKm &&
            result.offer.isCash == prev.offer.isCash) return true

        lastResults[platform] = result
        lastResultTimes[platform] = now
        return false
    }

    private suspend fun processViaAccessibilityTree(packageName: String) {
        try {
            // Layer 1: foreground guard — odrzuć jeśli foreground zmienił się podczas throttle/watch.
            // Dla tree pipeline (Glovo/Bolt) to krytyczne, bo rootInActiveWindow zwraca okno
            // AKTUALNEJ apki, nie tej która wygenerowała event. Bez guardu BoltFoodOcrParser
            // może czytać tekst Onetu/WP gdy user przełączył apkę po evencie.
            if (!isForegroundOfPackage(packageName)) {
                AppLog.d(AppLog.TAG_SERVICE, "Tree: aborting $packageName — foreground mismatch (foreground=$lastForegroundPackage)")
                return
            }

            val root = rootInActiveWindow ?: run {
                AppLog.w(AppLog.TAG_SERVICE, "Tree: rootInActiveWindow null, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }
            // Drugi guard: rootInActiveWindow może zwrócić okno innej apki niż packageName
            // (race między event a accessibility query). Sprawdzamy package na samym root.
            val rootPkg = try { root.packageName?.toString() } catch (_: Exception) { null }
            if (rootPkg != null && rootPkg != packageName) {
                AppLog.d(AppLog.TAG_SERVICE, "Tree: aborting $packageName — rootInActiveWindow has $rootPkg")
                root.recycle()
                return
            }
            val text: String
            try {
                text = AccessibilityTextCollector.collectText(root)
            } finally {
                root.recycle()
            }
            AppLog.d(AppLog.TAG_SERVICE, "Tree text ($packageName): len=${text.length}")

            val lines = text.lines().filter { it.isNotBlank() }
            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
            val offer = parser.parse(lines)

            if (offer == null) {
                AppLog.d(AppLog.TAG_SERVICE, "Tree: parse failed for $packageName, falling back to screenshot")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) processViaScreenshot(packageName)
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            // Cross-platform duplicate check
            if (OfferDuplicateChecker.isCrossPlatformDuplicate(offer, ServiceLocator.overlayManager.getActiveOffers())) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Tree result ($packageName): zlPerHour=${result.zlPerHour} zlPerKm=${result.zlPerKm} → ${result.level}")

            if (isSameAsPrevious(offer.platform, result)) return

            // Overlay guard — ostatnia bramka, blokuje belkę jeśli user kliknął Stop w trakcie
            if (!MonitoringController.isActive()) return

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L, offer.platform)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Tree error ($packageName): ${e.message}")
        }
    }

    private fun isMediaProjectionAvailable(): Boolean {
        val service = ScreenCaptureService.instance ?: return false
        return service.isReady() && !ScreenCaptureService.isProjectionLost
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun processViaScreenshot(packageName: String, retryIndex: Int = 0) {
        // Layer 1: foreground guard — odrzuć jeśli foreground zmienił się podczas
        // throttle (100ms) / retry loop (do 2.5s) / watch mode (60s).
        // takeScreenshot() robi zrzut CAŁEGO ekranu — bez tego guard'u parser dla packageName
        // dostanie tekst z apki na której user jest TERAZ (np. portal newsowy → false-positive).
        // Wyjątek (w isForegroundOfPackage): Uber popup overlay nad inną apką jest legalny,
        // pod warunkiem że overlay zawiera prawdziwą treść oferty (Layer 2).
        if (!isForegroundOfPackage(packageName)) {
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot: aborting $packageName (retry=$retryIndex) — foreground mismatch (foreground=$lastForegroundPackage)")
            return
        }
        var bitmap: Bitmap? = null
        var croppedBitmap: Bitmap? = null
        try {
            val screenOn = (getSystemService(POWER_SERVICE) as? PowerManager)?.isInteractive ?: true
            AppLog.d(AppLog.TAG_SERVICE, "Taking screenshot via AccessibilityService API (retry=$retryIndex, screenOn=$screenOn)")
            bitmap = withTimeoutOrNull(SCREENSHOT_TIMEOUT_MS) { takeScreenshotSuspend() } ?: run {
                consecutiveScreenshotFailures++
                AppLog.w(AppLog.TAG_SERVICE, "Screenshot returned null or timed out (retry=$retryIndex, consecutive failures: $consecutiveScreenshotFailures)")
                if (consecutiveScreenshotFailures >= SCREENSHOT_FAILURE_ALERT_THRESHOLD && !screenshotAlertShown) {
                    screenshotAlertShown = true
                    AppLog.w(AppLog.TAG_SERVICE, "ALERT: $consecutiveScreenshotFailures consecutive screenshot failures — notifying user")
                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                        android.widget.Toast.makeText(applicationContext, "OrderPilot: zrzuty ekranu nie działają — sprawdź uprawnienia", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
                return
            }

            // Screenshot success — reset failure counter
            consecutiveScreenshotFailures = 0
            screenshotAlertShown = false

            AppLog.d(AppLog.TAG_SERVICE, "Screenshot bitmap: ${bitmap.width}x${bitmap.height} (retry=$retryIndex)")

            // Crop dolne 60% — belka jest na górze, popup zlecenia na dole
            val startY = (bitmap.height * CROP_TOP_RATIO).toInt()
            croppedBitmap = Bitmap.createBitmap(bitmap, 0, startY, bitmap.width, bitmap.height - startY)

            val lines = ServiceLocator.ocrEngine.recognize(croppedBitmap)
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot OCR: ${lines.size} lines (retry=$retryIndex)")

            val parser = ServiceLocator.parserRegistry.getParser(packageName) ?: return
            val offer = parser.parse(lines)

            if (offer == null) {
                AppLog.d(AppLog.TAG_SERVICE, "Screenshot: parser returned null (retry=$retryIndex)")
                return
            }

            val settings = ServiceLocator.settingsRepository.load()
            if (!ServiceLocator.offerFilter.passes(offer, settings.filtersFor(offer.platform))) return

            // Cross-platform duplicate check
            if (OfferDuplicateChecker.isCrossPlatformDuplicate(offer, ServiceLocator.overlayManager.getActiveOffers())) return

            val result = ServiceLocator.offerAnalyzer.analyze(offer, settings.thresholdsFor(offer.platform))
            AppLog.d(AppLog.TAG_SERVICE, "Screenshot result: ${result.zlPerHour} zł/h → ${result.level} (retry=$retryIndex)")

            if (isSameAsPrevious(offer.platform, result)) return

            // Overlay guard — ostatnia bramka, blokuje belkę jeśli user kliknął Stop w trakcie
            if (!MonitoringController.isActive()) return

            scope.launch(Dispatchers.Main) {
                ServiceLocator.overlayManager.show(result, settings.display, settings.language)
                ServiceLocator.overlayAutoHider.onOverlayShown(scope, settings.displayTimeFor(offer.platform) * 1000L, offer.platform)
            }
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Screenshot fallback error (retry=$retryIndex): ${e.message}")
        } finally {
            bitmap?.recycle()
            croppedBitmap?.recycle()
        }
    }

    /**
     * Uber watch mode — safety net na wypadek opóźnionych/brakujących eventów.
     * Gdy Uber generuje eventy, uruchamiamy periodic check co 2.5s.
     * Jeśli belka NIE jest widoczna → robimy screenshot.
     * Zatrzymuje się po 60s bez eventów Ubera.
     */
    private fun startUberWatchIfNeeded() {
        if (uberWatchJob?.isActive == true) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

        uberWatchJob = scope.launch {
            AppLog.d(AppLog.TAG_SERVICE, "Uber watch: started")
            while (isActive && MonitoringController.isActive()) {
                delay(WATCH_INTERVAL_MS)
                val sinceLastEvent = System.currentTimeMillis() - lastUberEventTime
                if (sinceLastEvent > WATCH_TIMEOUT_MS) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: no events for ${sinceLastEvent / 1000}s, stopping")
                    break
                }
                // Jeśli retries właśnie lecą — nie robimy dodatkowego screenshota (unika kolizji)
                if (isRetrying) continue
                // Jeśli belka Ubera jest już widoczna — nie robimy screenshota
                if (ServiceLocator.overlayManager.getActiveOffers()[Platform.UBER] != null) continue
                // Jeśli Uber nie jest foreground i nie ma overlay okna — nie ma co screenshotować.
                // Gdy Uber jest foreground, popup jest wewnątrz okna apki (nie overlay) — screenshotuj.
                // Wcześniej (v1.0.2-1.0.4) wymagaliśmy hasUberOverlayWithContent (tekstu w overlay)
                // ale RN popup nie eksponuje tekstu przez accessibility → watch dead-end na Samsungu.
                // Layer 4 (parser positive markers) odsiewa false-positive po OCR.
                if (!isUberForeground() && !hasUberOverlayWindow()) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: no overlay window and not foreground — skipping screenshot")
                    continue
                }
                // Rival courier na ekranie + overlay Ubera pusty = phantom → nie screenshotuj rivala
                // (text-based content check tu zostaje — rivala wyróżniamy konkretnym foregroundem)
                if (isRivalCourierInForeground() && !hasUberOverlayWithContent()) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: rival courier foreground + phantom overlay — skipping screenshot")
                    continue
                }
                // Layer 1: foreground guard — bez tego watch mode mógłby strzelać przez 60s
                // do innej apki (jeśli user przeszedł na portal newsowy a Uber ma phantom overlay).
                // Layer 2 (wzmocniony hasUberOverlayWithContent) i Layer 3 (cancel watch on app switch)
                // już dużo łapią; ten guard to dodatkowa warstwa zanim screenshot poleci.
                if (!isForegroundOfPackage("com.ubercab.driver")) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: foreground mismatch — skipping tick")
                    continue
                }
                // Jeśli MediaProjection aktywna — użyj pipeline zamiast screenshot
                if (isMediaProjectionAvailable()) {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: periodic check via pipeline")
                    pipeline.process("com.ubercab.driver")
                } else {
                    AppLog.d(AppLog.TAG_SERVICE, "Uber watch: periodic check via screenshot")
                    processViaScreenshot("com.ubercab.driver", retryIndex = -1)
                }
            }
            AppLog.d(AppLog.TAG_SERVICE, "Uber watch: stopped")
        }
    }

    /**
     * Bolt watch mode — safety net na brakujące accessibility eventy.
     * Bolt Food nie generuje eventów gdy popup oferty się pojawia (~13s ciszy w testach).
     * Periodic tree read co 2.5s jest lekki (~10ms) i łapie ofertę nawet bez eventów.
     * Zatrzymuje się po 60s bez eventów Bolta.
     */
    private fun startBoltWatchIfNeeded() {
        if (boltWatchJob?.isActive == true) return

        boltWatchJob = scope.launch {
            AppLog.d(AppLog.TAG_SERVICE, "Bolt watch: started")
            while (isActive && MonitoringController.isActive()) {
                delay(WATCH_INTERVAL_MS)
                val sinceLastEvent = System.currentTimeMillis() - lastBoltEventTime
                if (sinceLastEvent > WATCH_TIMEOUT_MS) {
                    AppLog.d(AppLog.TAG_SERVICE, "Bolt watch: no events for ${sinceLastEvent / 1000}s, stopping")
                    break
                }
                // Jeśli belka Bolta jest już widoczna — nie sprawdzamy
                if (ServiceLocator.overlayManager.getActiveOffers()[Platform.BOLT] != null) continue
                // Jeśli Bolt nie jest foreground — nie sprawdzamy drzewa
                val activePackage = try {
                    rootInActiveWindow?.packageName?.toString()
                } catch (e: Exception) { null }
                if (activePackage != "com.bolt.deliverycourier") continue

                AppLog.d(AppLog.TAG_SERVICE, "Bolt watch: periodic tree check")
                processViaAccessibilityTree("com.bolt.deliverycourier")
            }
            AppLog.d(AppLog.TAG_SERVICE, "Bolt watch: stopped")
        }
    }

    /**
     * Diagnostyka okien Ubera — loguje wszystkie okna na ekranie i ich tekst.
     * Throttle: max raz na 10 sekund żeby nie zalewać logów.
     * Cel: sprawdzić czy popup zlecenia Ubera jest osobnym oknem
     * i czy accessibility tree eksponuje tekst popupu.
     */
    private fun logUberWindowDiagnostics() {
        val now = System.currentTimeMillis()
        if (now - lastWindowDiagTime < DIAG_THROTTLE_MS) return
        lastWindowDiagTime = now

        try {
            val allWindows = windows ?: return
            AppLog.d(AppLog.TAG_SERVICE, "=== Uber Window Diagnostics (${allWindows.size} windows) ===")
            for ((i, w) in allWindows.withIndex()) {
                val root = w.root
                try {
                    val pkg = root?.packageName?.toString() ?: "null"
                    AppLog.d(AppLog.TAG_SERVICE, "  Window[$i]: type=${w.type}, layer=${w.layer}, pkg=$pkg")
                    if (root != null && pkg == "com.ubercab.driver") {
                        val text = AccessibilityTextCollector.collectText(root)
                        AppLog.d(AppLog.TAG_SERVICE, "  Window[$i] Uber text: len=${text.length}")
                    }
                } finally {
                    root?.recycle()
                }
            }
            AppLog.d(AppLog.TAG_SERVICE, "=== End Window Diagnostics ===")
        } catch (e: Exception) {
            AppLog.w(AppLog.TAG_SERVICE, "Window diagnostics error: ${e.message}")
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun takeScreenshotSuspend(): Bitmap? = suspendCancellableCoroutine { cont ->
        takeScreenshot(
            Display.DEFAULT_DISPLAY,
            mainExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        ?.copy(Bitmap.Config.ARGB_8888, false)
                    screenshot.hardwareBuffer.close()
                    cont.resume(bitmap)
                }
                override fun onFailure(errorCode: Int) {
                    AppLog.w(AppLog.TAG_SERVICE, "takeScreenshot failed: errorCode=$errorCode")
                    cont.resume(null)
                }
            }
        )
    }

    override fun onInterrupt() {}

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Swipe z recents NIE jest Stop — nie zmieniamy MonitoringState.
        // Cancelujemy aktywne joby i chowamy belki, ale monitoring pozostaje aktywny
        // (nowe eventy będą przetwarzane, bo AccessibilityService żyje niezależnie od task).
        uberWatchJob?.cancel()
        boltWatchJob?.cancel()
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            try { ServiceLocator.overlayManager.hide() } catch (_: Exception) {}
        }
        AppLog.d(AppLog.TAG_SERVICE, "App removed from recents — cancelled watch jobs, hidden overlays (state unchanged)")
    }

    override fun onDestroy() {
        isConnected = false
        if (serviceInstance === this) serviceInstance = null
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        @Volatile
        var isConnected = false
            private set

        @Volatile
        private var serviceInstance: OrderPilotAccessibilityService? = null

        /** Cancelling watch jobów wołane z UI po kliknięciu Stop — hard stop bez opóźnień. */
        fun cancelActiveJobs() {
            val instance = serviceInstance ?: return
            instance.uberWatchJob?.cancel()
            instance.boltWatchJob?.cancel()
            AppLog.d(AppLog.TAG_SERVICE, "Watch jobs cancelled by user stop")
        }

        private val watchedPackages: Set<String>
            get() = ServiceLocator.parserRegistry.getAllWatchedPackages()

        // Timing constants
        private const val RETRY_DELAY_MS = 600L        // odstęp między retryami — unika errorCode=3 (Android rate-limit)
        private const val WATCH_INTERVAL_MS = 2500L    // periodic check co 2.5s
        private const val WATCH_TIMEOUT_MS = 60_000L   // watch mode wyłącza się po 60s bez eventów
        private const val SCREENSHOT_TIMEOUT_MS = 3000L // timeout na takeScreenshot()
        private const val DIAG_THROTTLE_MS = 10_000L   // diagnostyka okien max raz na 10s
        private const val UBER_MAX_RETRIES = 4         // Uber React Native: wolny rendering
        private const val WOLT_MAX_RETRIES = 2          // Wolt natywne UI: szybki rendering
        private const val CROP_TOP_RATIO = 0.4f         // crop dolne 60% screenshota
        private const val SCREENSHOT_FAILURE_ALERT_THRESHOLD = 10  // alert po 10 porażkach z rzędu
    }
}
