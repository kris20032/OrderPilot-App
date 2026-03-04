package com.courierassist.app.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventThrottlerTest {

    // EventThrottler.onEvent() używa scope.launch { delay(...) }.
    // runTest używa StandardTestDispatcher — launch jest lazy, wymaga
    // advanceTimeBy żeby faktycznie wykonać zawieszone coroutiny.
    // cooldownMs=0: System.currentTimeMillis() - lastTriggerTime zawsze > 0,
    // więc cooldown nie blokuje kolejnych eventów.

    @Test
    fun `first event triggers action after firstShotDelay`() = runTest {
        val throttler = EventThrottler(firstShotDelayMs = 300, cooldownMs = 0)
        var count = 0

        throttler.onEvent(backgroundScope) { count++ }
        assertEquals(0, count)

        advanceTimeBy(301)
        assertEquals(1, count)
    }

    @Test
    fun `action is not called before delay elapses`() = runTest {
        val throttler = EventThrottler(firstShotDelayMs = 300, cooldownMs = 0)
        var called = false

        throttler.onEvent(backgroundScope) { called = true }
        advanceTimeBy(299)
        assertFalse(called)

        advanceTimeBy(2)
        assertTrue(called)
    }

    @Test
    fun `multiple events before first shot collapse to one action`() = runTest {
        val throttler = EventThrottler(firstShotDelayMs = 300, cooldownMs = 0)
        var count = 0

        throttler.onEvent(backgroundScope) { count++ }
        throttler.onEvent(backgroundScope) { count++ }
        throttler.onEvent(backgroundScope) { count++ }

        advanceTimeBy(301)
        assertEquals(1, count)
    }

    @Test
    fun `second event after first shot completes is accepted`() = runTest {
        val throttler = EventThrottler(firstShotDelayMs = 300, cooldownMs = 0)
        var count = 0

        throttler.onEvent(backgroundScope) { count++ }
        advanceTimeBy(301)
        assertEquals(1, count)

        throttler.onEvent(backgroundScope) { count++ }
        advanceTimeBy(301)
        assertEquals(2, count)
    }

    @Test
    fun `action receives correct sequence of calls`() = runTest {
        val throttler = EventThrottler(firstShotDelayMs = 100, cooldownMs = 0)
        val calls = mutableListOf<Int>()

        throttler.onEvent(backgroundScope) { calls.add(1) }
        advanceTimeBy(101)
        throttler.onEvent(backgroundScope) { calls.add(2) }
        advanceTimeBy(101)

        assertEquals(listOf(1, 2), calls)
    }
}