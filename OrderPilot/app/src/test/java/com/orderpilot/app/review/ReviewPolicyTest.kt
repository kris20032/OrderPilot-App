package com.orderpilot.app.review

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewPolicyTest {

    @Test
    fun `nie pyta przed trzecim dniem uzycia`() {
        assertFalse(ReviewPolicy.shouldAsk(daysUsed = 0, alreadyAsked = false))
        assertFalse(ReviewPolicy.shouldAsk(daysUsed = 1, alreadyAsked = false))
        assertFalse(ReviewPolicy.shouldAsk(daysUsed = 2, alreadyAsked = false))
    }

    @Test
    fun `pyta od trzeciego dnia uzycia`() {
        assertTrue(ReviewPolicy.shouldAsk(daysUsed = 3, alreadyAsked = false))
        assertTrue(ReviewPolicy.shouldAsk(daysUsed = 10, alreadyAsked = false))
    }

    @Test
    fun `nigdy nie pyta drugi raz`() {
        assertFalse(ReviewPolicy.shouldAsk(daysUsed = 3, alreadyAsked = true))
        assertFalse(ReviewPolicy.shouldAsk(daysUsed = 100, alreadyAsked = true))
    }
}
