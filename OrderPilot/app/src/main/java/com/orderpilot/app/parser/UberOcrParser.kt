package com.orderpilot.app.parser

import com.orderpilot.app.di.AppLog
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform

class UberOcrParser : OcrOfferParser {

    override val platform = Platform.UBER
    override val supportedPackages = setOf("com.ubercab.driver", "com.ubercab.eats")

    // Czas: "14 min", "14min", "14 хв"
    private val timeRegex = Regex("""(\d+)[\s\u00A0]*(?:min|хв|XB|мин)""", RegexOption.IGNORE_CASE)
    private val hourRegex = Regex("""(\d+)[\s\u00A0]*(?:godz|год|hr|hour)""", RegexOption.IGNORE_CASE)

    // Dystans: "(1.0 km)", "1,5 km" — wymaga spacji/nawias/minus przed liczbą
    private val distanceRegex = Regex("""[\s\u00A0(-](\d+[.,]\d+)[\s\u00A0]*(?:km|км)""", RegexOption.IGNORE_CASE)

    // Frazy specyficzne dla Wolta/Bolta — Uber nigdy ich nie używa.
    // Defense in depth: nawet jeśli screenshot z popupem innej platformy
    // dotrze do tego parsera, zwróci null zamiast fałszywej oferty.
    private val rivalPlatformMarkers = listOf(
        // Wolt PL/EN/UK
        "Odbiór za", "Pickup in", "Забери через",
        "Spodziewany zarobek", "Expected earnings", "Estimated earnings", "Очікуваний заробіток",
        "Dostawa od", "Delivery from", "Доставка від",
        // Wolt RU
        "Заберите через", "Забрать через",
        "Ожидаемый заработок", "Приблизительный заработок",
        "Доставка от",
        // Bolt PL/EN
        "Potwierdź odbiór", "Confirm pickup",
        "Decline", "Show map", "Looking for orders", "Go offline",
        // Bolt RU
        "Подтвердить получение", "Подтвердите получение",
        "Отклонить", "Показать карту", "Ищу заказы", "Выйти из сети"
    )

    // Frazy z ekranu historii/szczegółów przejazdu — nigdy nie pojawiają się na popupie oferty.
    // Popup oferty ma "Łącznie X min (Y km)", historia ma etykiety "Czas trwania", "Odległość".
    private val historyScreenMarkers = listOf(
        // PL
        "Czas trwania", "Odległość", "Twój przychód",
        // EN
        "Trip duration", "Your earnings", "Distance", "Trip fare",
        // UK
        "Тривалість", "Відстань", "Ваш заробіток",
        // RU
        "Продолжительность", "Длительность", "Время поездки",
        "Расстояние", "Ваш заработок", "Заработок", "Доход", "Стоимость поездки"
    )

    override fun parse(ocrLines: List<String>): Offer? {
        val text = OcrOfferParser.normalizeOcrDigits(ocrLines.joinToString(" "))

        // Guard: odrzuć tekst z UI innej platformy kurierskiej
        rivalPlatformMarkers.firstOrNull { text.contains(it, ignoreCase = true) }?.let { marker ->
            AppLog.d(AppLog.TAG_PARSER, "Uber: skipping — rival platform text detected ('$marker')")
            return null
        }

        // Guard: odrzuć ekran historii/szczegółów przejazdu
        historyScreenMarkers.firstOrNull { text.contains(it, ignoreCase = true) }?.let { marker ->
            AppLog.d(AppLog.TAG_PARSER, "Uber: skipping — history screen detected ('$marker')")
            return null
        }

        // Kwota — wspólna logika z fallbackiem
        val (rawAmount, parsedAmount) = OcrOfferParser.extractAmount(text) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Uber: no amount found | text=${text.take(200)}")
            return null
        }
        val amount = OcrOfferParser.sanitizeAmount(rawAmount, parsedAmount) ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Uber: amount rejected by sanitize")
            return null
        }

        val mins = timeRegex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: run {
            AppLog.w(AppLog.TAG_PARSER, "Uber: no time found | text=${text.take(200)}")
            return null
        }
        val hourMatch = hourRegex.find(text)
        val hours = hourMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
        if (hours > 0) {
            AppLog.d(AppLog.TAG_PARSER, "Uber: hour match '${hourMatch?.value}' → ${hours}h ${mins}min")
        }
        val minutes = hours * 60 + mins

        // Prawdziwe zlecenie Uber ma max ~120 min. Powyżej 180 min = ekran statystyk/zestawienia
        if (minutes > 180) {
            AppLog.d(AppLog.TAG_PARSER, "Uber: rejecting — time ${minutes} min too high (statistics screen?)")
            return null
        }

        val distance = distanceRegex.find(text)?.groupValues?.get(1)?.toDoubleLocale()
        val detectedCurrency = OcrOfferParser.detectCurrency(text)

        val offer = Offer(Platform.UBER, amount, minutes, distance, detectedCurrency)
        AppLog.d(AppLog.TAG_PARSER, "Parsed offer: $offer")
        return offer
    }

    private fun String.toDoubleLocale(): Double? = replace(",", ".").toDoubleOrNull()
}
