package com.orderpilot.app.ui.setup

/**
 * Czysta (bez Androida) logika przepływu wizarda konfiguracji — testowalna jednostkowo.
 *
 * Wizard v2: jeden krok = jeden ekran, auto-przejście gdy uprawnienie nadane.
 * Kolejność kroków uprawnień jest stała; kroki już spełnione są pomijane przy wejściu.
 *
 * Krok OEM (wskazówki producenta: autostart, usypianie) jest NIEBLOKUJĄCY:
 * pokazuje się raz na końcu przebiegu wizarda (po nadaniu uprawnień), nie wchodzi
 * do bramki isComplete i jest zawsze osiągalny ponownie z ekranu podsumowania.
 */
enum class SetupStep {
    WELCOME,        // podgląd belki, zero uprawnień
    OVERLAY,        // SYSTEM_ALERT_WINDOW
    ACCESSIBILITY,  // usługa dostępności
    NOTIFICATIONS,  // POST_NOTIFICATIONS (tylko API 33+)
    BATTERY,        // wyłączenie optymalizacji baterii
    OEM,            // wskazówki producenta (Xiaomi/Vivo/…) — nieblokujące
    DONE            // podsumowanie
}

data class SetupStatus(
    val overlayOk: Boolean,
    val accessibilityOk: Boolean,
    val notificationsOk: Boolean,
    val batteryOk: Boolean,
    /** false na API < 33 — krok NOTIFICATIONS wtedy nie istnieje. */
    val notificationsSupported: Boolean,
    /** true gdy dla tego telefonu mamy wskazówki producenta (Samsung/Xiaomi/Vivo/… lub generyczne). */
    val oemApplicable: Boolean,
    /** true gdy ekran powitalny był już pokazany (nie męczymy przy powrotach). */
    val welcomeSeen: Boolean
)

object SetupFlow {

    /** Kroki WYMAGANE (bramkujące), w kolejności wizarda. */
    fun permissionSteps(status: SetupStatus): List<SetupStep> = buildList {
        add(SetupStep.OVERLAY)
        add(SetupStep.ACCESSIBILITY)
        if (status.notificationsSupported) add(SetupStep.NOTIFICATIONS)
        add(SetupStep.BATTERY)
    }

    /** Kroki pokazywane w liczniku postępu: wymagane + (opcjonalnie) OEM na końcu. */
    fun wizardSteps(status: SetupStatus): List<SetupStep> =
        permissionSteps(status) + if (status.oemApplicable) listOf(SetupStep.OEM) else emptyList()

    fun isStepSatisfied(step: SetupStep, status: SetupStatus): Boolean = when (step) {
        SetupStep.WELCOME -> status.welcomeSeen
        SetupStep.OVERLAY -> status.overlayOk
        SetupStep.ACCESSIBILITY -> status.accessibilityOk
        SetupStep.NOTIFICATIONS -> status.notificationsOk
        SetupStep.BATTERY -> status.batteryOk
        SetupStep.OEM -> true   // nieweryfikowalny systemowo — nie blokuje
        SetupStep.DONE -> true
    }

    /** Bramka „setup zakończony" — WYŁĄCZNIE uprawnienia (OEM nie blokuje). */
    fun isComplete(status: SetupStatus): Boolean =
        permissionSteps(status).all { isStepSatisfied(it, status) }

    /** Pierwszy niespełniony krok uprawnień albo null gdy wszystko nadane. */
    fun firstUnsatisfied(status: SetupStatus): SetupStep? =
        permissionSteps(status).firstOrNull { !isStepSatisfied(it, status) }

    /**
     * Krok, od którego wizard startuje przy wejściu:
     * - komplet uprawnień → od razu podsumowanie (wejście z „Sprawdź ustawienia"),
     * - pierwszy raz → ekran powitalny z podglądem belki,
     * - powrót w trakcie → pierwszy brakujący krok (bez powtarzania powitania).
     */
    fun initialStep(status: SetupStatus): SetupStep = when {
        isComplete(status) -> SetupStep.DONE
        !status.welcomeSeen -> SetupStep.WELCOME
        else -> firstUnsatisfied(status) ?: SetupStep.DONE
    }

    /**
     * Następny krok po [current]:
     * - kolejny NIESPEŁNIONY krok uprawnień dalszy w kolejności niż [current],
     * - gdy uprawnienia skończone: ekran OEM (jeśli dotyczy i nie jesteśmy właśnie na nim),
     * - w ostateczności DONE.
     * Z WELCOME idziemy do pierwszego niespełnionego.
     */
    fun nextStep(current: SetupStep, status: SetupStatus): SetupStep {
        val steps = permissionSteps(status)
        val startIndex = if (current == SetupStep.WELCOME) 0 else steps.indexOf(current) + 1
        val nextPermission = steps.drop(startIndex.coerceAtLeast(0))
            .firstOrNull { !isStepSatisfied(it, status) }
        if (nextPermission != null) return nextPermission
        if (status.oemApplicable && current != SetupStep.OEM && current != SetupStep.DONE) return SetupStep.OEM
        return SetupStep.DONE
    }

    /**
     * Etykieta postępu: (numer 1-based, liczba kroków) w ramach [wizardSteps].
     * Dla WELCOME/DONE — null (nagłówek postępu ukryty).
     */
    fun progress(current: SetupStep, status: SetupStatus): Pair<Int, Int>? {
        val steps = wizardSteps(status)
        val index = steps.indexOf(current)
        if (index < 0) return null
        return (index + 1) to steps.size
    }
}
