package com.orderpilot.app.ui.setup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupFlowTest {

    private fun status(
        overlay: Boolean = false,
        accessibility: Boolean = false,
        notifications: Boolean = false,
        battery: Boolean = false,
        notificationsSupported: Boolean = true,
        oemApplicable: Boolean = false,
        welcomeSeen: Boolean = false
    ) = SetupStatus(
        overlayOk = overlay,
        accessibilityOk = accessibility,
        notificationsOk = notifications,
        batteryOk = battery,
        notificationsSupported = notificationsSupported,
        oemApplicable = oemApplicable,
        welcomeSeen = welcomeSeen
    )

    // ─── permissionSteps ───

    @Test
    fun `api 33 plus ma 4 kroki uprawnien w stalej kolejnosci`() {
        assertEquals(
            listOf(SetupStep.OVERLAY, SetupStep.ACCESSIBILITY, SetupStep.NOTIFICATIONS, SetupStep.BATTERY),
            SetupFlow.permissionSteps(status())
        )
    }

    @Test
    fun `api ponizej 33 pomija krok powiadomien`() {
        assertEquals(
            listOf(SetupStep.OVERLAY, SetupStep.ACCESSIBILITY, SetupStep.BATTERY),
            SetupFlow.permissionSteps(status(notificationsSupported = false))
        )
    }

    // ─── isComplete ───

    @Test
    fun `komplet uprawnien oznacza setup zakonczony`() {
        assertTrue(SetupFlow.isComplete(status(overlay = true, accessibility = true, notifications = true, battery = true)))
    }

    @Test
    fun `brak powiadomien na api 33 blokuje komplet`() {
        assertFalse(SetupFlow.isComplete(status(overlay = true, accessibility = true, notifications = false, battery = true)))
    }

    @Test
    fun `brak powiadomien na starym api nie blokuje kompletu`() {
        assertTrue(
            SetupFlow.isComplete(
                status(overlay = true, accessibility = true, notifications = false, battery = true, notificationsSupported = false)
            )
        )
    }

    // ─── initialStep ───

    @Test
    fun `pierwszy start zaczyna od powitania`() {
        assertEquals(SetupStep.WELCOME, SetupFlow.initialStep(status()))
    }

    @Test
    fun `powrot w trakcie pomija powitanie i idzie do pierwszego brakujacego`() {
        assertEquals(
            SetupStep.ACCESSIBILITY,
            SetupFlow.initialStep(status(overlay = true, welcomeSeen = true))
        )
    }

    @Test
    fun `wejscie z kompletem uprawnien laduje na podsumowaniu`() {
        assertEquals(
            SetupStep.DONE,
            SetupFlow.initialStep(status(overlay = true, accessibility = true, notifications = true, battery = true, welcomeSeen = true))
        )
    }

    @Test
    fun `komplet uprawnien bez widzianego powitania tez idzie na podsumowanie`() {
        // Scenariusz: użytkownik nadał wszystko np. po reinstalacji — nie ma sensu wracać do powitania.
        assertEquals(
            SetupStep.DONE,
            SetupFlow.initialStep(status(overlay = true, accessibility = true, notifications = true, battery = true))
        )
    }

    // ─── nextStep (auto-przejścia) ───

    @Test
    fun `z powitania przechodzi do pierwszego brakujacego kroku`() {
        assertEquals(SetupStep.OVERLAY, SetupFlow.nextStep(SetupStep.WELCOME, status()))
        assertEquals(
            SetupStep.NOTIFICATIONS,
            SetupFlow.nextStep(SetupStep.WELCOME, status(overlay = true, accessibility = true))
        )
    }

    @Test
    fun `po overlay pomija juz nadane kroki`() {
        // Accessibility nadane wcześniej (np. reinstalacja) → z OVERLAY od razu do NOTIFICATIONS
        assertEquals(
            SetupStep.NOTIFICATIONS,
            SetupFlow.nextStep(SetupStep.OVERLAY, status(overlay = true, accessibility = true))
        )
    }

    @Test
    fun `ostatni brakujacy krok prowadzi do done`() {
        assertEquals(
            SetupStep.DONE,
            SetupFlow.nextStep(SetupStep.BATTERY, status(overlay = true, accessibility = true, notifications = true, battery = true))
        )
    }

    @Test
    fun `next nie cofa sie do wczesniejszych krokow`() {
        // Użytkownik jest na BATTERY, a OVERLAY nagle cofnięte (np. odebrał w ustawieniach)
        // → nextStep NIE wraca; pilnuje tego bramka isComplete przy wyjściu.
        assertEquals(
            SetupStep.DONE,
            SetupFlow.nextStep(SetupStep.BATTERY, status(accessibility = true, notifications = true, battery = true))
        )
    }

    // ─── krok OEM (nieblokujący) ───

    @Test
    fun `po ostatnim uprawnieniu wchodzi ekran oem gdy dotyczy`() {
        val s = status(overlay = true, accessibility = true, notifications = true, battery = true, oemApplicable = true)
        assertEquals(SetupStep.OEM, SetupFlow.nextStep(SetupStep.BATTERY, s))
        assertEquals(SetupStep.DONE, SetupFlow.nextStep(SetupStep.OEM, s))
    }

    @Test
    fun `oem nie blokuje kompletu ani nie pojawia sie przy wejsciu`() {
        val s = status(overlay = true, accessibility = true, notifications = true, battery = true, oemApplicable = true)
        assertTrue(SetupFlow.isComplete(s))
        assertEquals(SetupStep.DONE, SetupFlow.initialStep(s))
    }

    @Test
    fun `oem liczony w postepie jako ostatni krok`() {
        val s = status(oemApplicable = true)
        assertEquals(5, SetupFlow.wizardSteps(s).size)
        assertEquals(5 to 5, SetupFlow.progress(SetupStep.OEM, s))
        assertEquals(4 to 5, SetupFlow.progress(SetupStep.BATTERY, s))
    }

    // ─── progress ───

    @Test
    fun `postep liczy kroki uprawnien 1-based`() {
        assertEquals(1 to 4, SetupFlow.progress(SetupStep.OVERLAY, status()))
        assertEquals(4 to 4, SetupFlow.progress(SetupStep.BATTERY, status()))
        assertEquals(3 to 3, SetupFlow.progress(SetupStep.BATTERY, status(notificationsSupported = false)))
    }

    @Test
    fun `powitanie i podsumowanie nie maja etykiety postepu`() {
        assertNull(SetupFlow.progress(SetupStep.WELCOME, status()))
        assertNull(SetupFlow.progress(SetupStep.DONE, status()))
    }

    @Test
    fun `firstUnsatisfied wskazuje kolejny brak w kolejnosci wizarda`() {
        assertEquals(SetupStep.OVERLAY, SetupFlow.firstUnsatisfied(status()))
        assertEquals(SetupStep.BATTERY, SetupFlow.firstUnsatisfied(status(overlay = true, accessibility = true, notifications = true)))
        assertNull(SetupFlow.firstUnsatisfied(status(overlay = true, accessibility = true, notifications = true, battery = true)))
    }
}
