package com.orderpilot.app.pipeline

import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfferDuplicateCheckerTest {

    private fun offer(
        platform: Platform,
        amount: Double,
        minutes: Int = 0,
        km: Double? = null
    ) = Offer(platform = platform, amount = amount, estimatedMinutes = minutes, distanceKm = km)

    // ─── Klasyczne duplikaty (3 wymiary znane) ───

    @Test
    fun `ta sama oferta na dwoch platformach to duplikat`() {
        val bolt = offer(Platform.BOLT, 21.50, 22, 7.2)
        val active = mapOf(Platform.UBER to offer(Platform.UBER, 21.30, 24, 7.0))
        assertTrue(OfferDuplicateChecker.isCrossPlatformDuplicate(bolt, active))
    }

    @Test
    fun `zupelnie inna oferta nie jest duplikatem`() {
        val bolt = offer(Platform.BOLT, 35.00, 40, 15.0)
        val active = mapOf(Platform.UBER to offer(Platform.UBER, 21.30, 24, 7.0))
        assertFalse(OfferDuplicateChecker.isCrossPlatformDuplicate(bolt, active))
    }

    @Test
    fun `ta sama platforma nigdy nie jest cross-duplikatem`() {
        val uber = offer(Platform.UBER, 21.30, 24, 7.0)
        val active = mapOf(Platform.UBER to offer(Platform.UBER, 21.30, 24, 7.0))
        assertFalse(OfferDuplicateChecker.isCrossPlatformDuplicate(uber, active))
    }

    @Test
    fun `dwa z trzech wymiarow wystarcza przy znanym czasie (szum OCR)`() {
        // Kwota i czas zgodne, dystans rozjechany o 1 km (inny parser) → wciąż duplikat
        val wolt = offer(Platform.WOLT, 18.00, 20, 6.0)
        val active = mapOf(Platform.UBER to offer(Platform.UBER, 18.20, 21, 7.0))
        assertTrue(OfferDuplicateChecker.isCrossPlatformDuplicate(wolt, active))
    }

    // ─── M9: Glovo (czas nieznany, min=0) ───

    @Test
    fun `glovo bez czasu nie tlumi oferty o przypadkowo zblizonej kwocie`() {
        // Scenariusz z audytu M9: aktywna belka Glovo (min=0), wpada realnie INNY Bolt.
        // Kwota różni się o 0.4 zł (stare okno 0.5 by złapało), dystans zgodny →
        // przy nieznanym czasie okno zawężone do 0.3 → NIE duplikat.
        val bolt = offer(Platform.BOLT, 20.40, 22, 5.2)
        val active = mapOf(Platform.GLOVO to offer(Platform.GLOVO, 20.00, 0, 5.0))
        assertFalse(OfferDuplicateChecker.isCrossPlatformDuplicate(bolt, active))
    }

    @Test
    fun `glovo bez czasu nadal lapie prawdziwy duplikat kwota plus dystans`() {
        val bolt = offer(Platform.BOLT, 20.10, 22, 5.1)
        val active = mapOf(Platform.GLOVO to offer(Platform.GLOVO, 20.00, 0, 5.0))
        assertTrue(OfferDuplicateChecker.isCrossPlatformDuplicate(bolt, active))
    }

    @Test
    fun `nieznany czas nie liczy sie jako zgodny wymiar`() {
        // Oba min=0 → czas nieznany; zgodna tylko kwota, brak dystansu → NIE duplikat
        // (stary kod: abs(0-0)<=5 = true → czas "pasował" mimo że nieznany).
        val glovo = offer(Platform.GLOVO, 20.10, 0, null)
        val active = mapOf(Platform.BOLT to offer(Platform.BOLT, 20.00, 0, null))
        assertFalse(OfferDuplicateChecker.isCrossPlatformDuplicate(glovo, active))
    }

    @Test
    fun `brak dystansu po jednej stronie nie liczy sie jako zgodnosc`() {
        // Zgodna kwota + czas, dystans nieporównywalny → 2 z 3 → duplikat (jak dotąd)
        val wolt = offer(Platform.WOLT, 18.00, 20, null)
        val active = mapOf(Platform.UBER to offer(Platform.UBER, 18.20, 21, 7.0))
        assertTrue(OfferDuplicateChecker.isCrossPlatformDuplicate(wolt, active))
    }
}
