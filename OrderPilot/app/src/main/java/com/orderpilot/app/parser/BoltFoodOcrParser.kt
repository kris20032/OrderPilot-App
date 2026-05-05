package com.orderpilot.app.parser

import com.orderpilot.app.di.AppLog
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform

class BoltFoodOcrParser : OcrOfferParser {

    override val platform = Platform.BOLT
    override val supportedPackages = setOf(
        "com.bolt.deliverycourier",
        "com.bolt.courier",
        "com.bolt.food.courier",
        "ee.mtakso.courier"
    )

    // Czas: "27 min", "27min", "27 хв"
    private val timeRegex = Regex("""(\d+)[\s\u00A0]*(?:min|хв|XB|мін|мин)""", RegexOption.IGNORE_CASE)
    private val hourRegex = Regex("""(\d+)[\s\u00A0]*(?:godz|год|hr|hour)""", RegexOption.IGNORE_CASE)

    // Dystans: "2.2 km", "2,2 km", "5 km"
    private val distanceRegex = Regex("""(\d+(?:[.,]\d+)?)[\s\u00A0]*(?:km|км)""", RegexOption.IGNORE_CASE)

    // Frazy specyficzne dla Ubera/Wolta — Bolt nigdy ich nie używa.
    private val rivalPlatformMarkers = listOf(
        // Uber PL/EN/UK
        "Łącznie", "Lacznie", "Загалом",
        "Dostawa ·", "Delivery ·",
        "Jesteś w trybie online", "You're online", "Ви онлайн",
        "Includes expected tip",
        // Uber RU
        "Итого", "Всего", "Вы онлайн", "Вы в сети", "Включая чаевые",
        // Wolt PL/EN/UK
        "Odbiór za", "Pickup in", "Забери через",
        "Spodziewany zarobek", "Expected earnings", "Estimated earnings", "Очікуваний заробіток",
        // Wolt RU
        "Заберите через", "Забрать через", "Ожидаемый заработок", "Приблизительный заработок"
    )

    // Layer 4: Positive markers — co najmniej jeden MUSI być w tekście. Bolt Food popup
    // ma typowo brand "Bolt", przyciski "Akceptuj"/"Decline" oraz nazwę restauracji.
    // Markery dobrane szeroko (multi-language) żeby nie blokować real offers.
    // Słabsze niż Uber/Wolt bo Bolt Food popup ma mniej charakterystyczny format,
    // ale w połączeniu z foreground guardem (Layer 1) i watch resetem (Layer 3)
    // daje wystarczające pokrycie.
    private val positiveOfferMarkers = listOf(
        // Brand
        "Bolt", "BOLT", "Бoлт", "Болт",
        // Akcja na popupie
        "Akceptuj", "Accept", "Прийняти", "Принять",
        "Odrzuć", "Decline", "Reject", "Відхилити", "Отклонить",
        // Restoran/Restaurant — Bolt Food zawsze pokazuje nazwę z kontekstem
        "Restoran", "Restaurant", "Restauracja", "Ресторан",
        // Post-akceptacji confirm screens (overlay zostaje)
        "Potwierdź odbiór", "Confirm pickup",
        "Підтвердити отримання", "Подтвердить получение", "Подтвердите получение",
        "Show map", "Pokaż mapę", "Показати карту", "Показать карту",
        // Idle/working state często widoczny pod popupem
        "Looking for orders", "Szukam zamówień", "Ищу заказы",
        "Go offline", "Выйти из сети"
    )

    override fun parse(ocrLines: List<String>): Offer? {
        val text = ocrLines.joinToString(" ")
        AppLog.d(AppLog.TAG_PARSER, "Bolt OCR: lines=${ocrLines.size} textLen=${text.length}")

        // Guard: odrzuć tekst z UI innej platformy kurierskiej
        rivalPlatformMarkers.firstOrNull { text.contains(it, ignoreCase = true) }?.let { marker ->
            AppLog.d(AppLog.TAG_PARSER, "Bolt: skipping — rival platform text detected ('$marker')")
            return null
        }

        // Layer 4: Positive marker check — wymóg co najmniej jednego markera popupu Bolt Food.
        // Chroni przed sytuacją gdy screenshot z apki news / social / innej apki przeszedł
        // foreground guard (Layer 1) ale tekst i tak by się sparsował przez luźny amount regex.
        if (!OcrOfferParser.hasAnyPositiveMarker(text, positiveOfferMarkers)) {
            AppLog.d(AppLog.TAG_PARSER, "Bolt: skipping — no positive offer marker found | textLen=${text.length}")
            return null
        }

        // Detekcja gotówki (generyczne markery — do weryfikacji z prawdziwymi zleceniami)
        val isCash = OcrOfferParser.containsCashMarkers(text)
        if (isCash) AppLog.d(AppLog.TAG_PARSER, "Bolt: cash order detected")

        // Szukamy WSZYSTKICH kwot i bierzemy NAJWIĘKSZĄ (przycisk ma kwotę zlecenia)
        val amounts = OcrOfferParser.findAllAmounts(text)
            .mapNotNull { match ->
                val raw = match.groupValues[1]
                OcrOfferParser.sanitizeAmount(raw, raw.toDoubleLocale() ?: return@mapNotNull null)
            }
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Bolt: amounts found = $amounts")

        val amount = amounts.maxOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Bolt: no amount found | textLen=${text.length}")
            return null
        }

        // Czas: szukamy wszystkich, bierzemy NAJWIĘKSZY (suma na przycisku)
        val allMinutes = timeRegex.findAll(text)
            .mapNotNull { it.groupValues[1].toIntOrNull() }
            .toList()
        val hours = hourRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val minutes = (allMinutes.maxOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Bolt: no time found | textLen=${text.length}")
            return null
        }) + hours * 60

        // Dystans: szukamy wszystkich, bierzemy NAJWIĘKSZY (suma na przycisku)
        val allDistances = distanceRegex.findAll(text)
            .mapNotNull { it.groupValues[1].toDoubleLocale() }
            .filter { it > 0 }
            .toList()

        AppLog.d(AppLog.TAG_PARSER, "Bolt: times=$allMinutes hours=$hours distances=$allDistances")

        val distance = allDistances.maxOrNull()

        val offer = Offer(Platform.BOLT, amount, minutes, distance, OcrOfferParser.detectCurrency(text), isCash = isCash)
        AppLog.d(AppLog.TAG_PARSER, "Bolt parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}
