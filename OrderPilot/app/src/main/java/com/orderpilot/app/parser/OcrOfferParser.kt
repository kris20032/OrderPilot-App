package com.orderpilot.app.parser

import com.orderpilot.app.di.AppLog
import com.orderpilot.app.domain.Offer
import com.orderpilot.app.domain.Platform

interface OcrOfferParser {
    val platform: Platform
    val supportedPackages: Set<String>
    fun parse(ocrLines: List<String>): Offer?

    companion object {
        private const val MIN_REALISTIC_AMOUNT = 3.0
        private const val MAX_REALISTIC_AMOUNT = 150.0

        // Whitespace: zwykła spacja + non-breaking space (apki używają \u00A0)
        private const val WS = """[\s\u00A0]"""

        // Symbole waluty PLN w różnych formatach OCR:
        // zł, zl (OCR gubi ł), zt (OCR zamienia ł→t),
        // PLN (angielski), грн (ukraiński), rpH (OCR czyta грн jako łacinkę), ₴ (symbol hrywny)
        // UWAGA: usunięto z(?=\s|\d|$) — matchowało końcówki polskich słów ("Nasz", "przez", "bez")
        private const val CUR = """(?:zł|zl|zt|PLN|грн|rpH|₴)"""

        // Główny regex: LICZBA + WALUTA (np. "7,86 zł", "7.86 PLN", "7,86zl")
        val AMOUNT_SUFFIX_REGEX = Regex("""(\d+(?:[.,]\d+)?)${WS}*${CUR}""", RegexOption.IGNORE_CASE)

        // Odwrócony: WALUTA + LICZBA (np. "PLN7.86", "PLN 7.86", "zł7,86")
        val AMOUNT_PREFIX_REGEX = Regex("""${CUR}${WS}*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

        // Fallback: luźna liczba dziesiętna (np. "7.86") — NOT obok min/km/хв/км
        // Wymaga separatora dziesiętnego żeby odfiltrować losowe liczby całkowite
        // \d* w lookahead zapobiega backtrackowi: "5.86 km" → match "5.8" + lookahead "6\s*km" → odrzucone
        val AMOUNT_FALLBACK_REGEX = Regex("""(?<!\d)(\d+[.,]\d{1,2})(?!\d*\s*(?:min|хв|XB|мін|мин|km|км|mi))""", RegexOption.IGNORE_CASE)

        // Regex do detekcji waluty (do wyświetlenia w belce)
        val CURRENCY_DETECT_REGEX = Regex("""(zł|zl|zt|PLN|грн|rpH|₴)""", RegexOption.IGNORE_CASE)

        /**
         * Normalizuje typowe błędy OCR w cyfrach.
         * Reguły aktywują się TYLKO gdy znak jest otoczony kontekstem liczbowym
         * (cyfra, kropka, przecinek) — bezpieczne, nie psuje normalnego tekstu.
         *
         * Zaobserwowane błędy OCR z produkcji:
         * - 'l/I/|' → '1': "PLN1L.31" (L zamiast 1), "l8,64 zł" (l zamiast 1)
         * - 'Z' → '7':    "PLN1Z.28" (Z zamiast 7) — font sans-serif Ubera
         * - 'O' → '0':    klasyczny OCR error
         */
        fun normalizeOcrDigits(text: String): String {
            return text
                // l/I/| → 1: przed cyfrą (l8 → 18) lub po cyfrze przed .,digit (1L.31 → 11.31)
                .replace(Regex("""[lI|](?=\d)"""), "1")
                .replace(Regex("""(?<=\d)[lI|](?=[.,\d])"""), "1")
                // Z/z → 7: po cyfrze przed .,digit (1Z.28 → 17.28) — OCR myli 7↔Z w sans-serif
                .replace(Regex("""(?<=\d)[Zz](?=[.,\d])"""), "7")
                // O/o → 0: otoczony cyframi/separatorami (1O.5 → 10.5, O8 → 08)
                .replace(Regex("""(?<=\d)[Oo](?=[.,\d])"""), "0")
                .replace(Regex("""[Oo](?=\d)"""), "0")
                // OCR klei "1" (jako "l") do poprzedniego słowa: "Łączniel godz" → "Łącznie 1 godz"
                .replace(Regex("""(?<=[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻіїєґІЇЄҐ])[lI|](?=\s*(?:godz|год|hr|hour))"""), " 1")
                .replace(Regex("""(?<=\s|^)[lI|](?=\s*(?:godz|год|hr|hour))"""), "1") // l godz → 1 godz
        }

        /**
         * Wyciąga kwotę z tekstu OCR — podejście hybrydowe:
         * 1. Szukaj LICZBA+WALUTA (np. "7,86 zł")
         * 2. Szukaj WALUTA+LICZBA (np. "PLN7.86")
         * 3. Fallback: luźna liczba dziesiętna (nie czas, nie dystans)
         *
         * @return para (rawMatch, parsedDouble) lub null
         */
        fun extractAmount(text: String): Pair<String, Double>? {
            val normalized = normalizeOcrDigits(text)
            // Krok 1: LICZBA + WALUTA
            AMOUNT_SUFFIX_REGEX.find(normalized)?.let { match ->
                val raw = match.groupValues[1]
                val parsed = raw.replace(",", ".").toDoubleOrNull()
                if (parsed != null) {
                    AppLog.d(AppLog.TAG_PARSER, "Amount found (suffix): $raw")
                    return Pair(raw, parsed)
                }
            }

            // Krok 2: WALUTA + LICZBA
            AMOUNT_PREFIX_REGEX.find(normalized)?.let { match ->
                val raw = match.groupValues[1]
                val parsed = raw.replace(",", ".").toDoubleOrNull()
                if (parsed != null) {
                    AppLog.d(AppLog.TAG_PARSER, "Amount found (prefix): $raw")
                    return Pair(raw, parsed)
                }
            }

            // Krok 3: Fallback — luźna liczba dziesiętna
            AMOUNT_FALLBACK_REGEX.find(normalized)?.let { match ->
                val raw = match.groupValues[1]
                val parsed = raw.replace(",", ".").toDoubleOrNull()
                if (parsed != null) {
                    AppLog.w(AppLog.TAG_PARSER, "Amount found (fallback, no currency): $raw")
                    return Pair(raw, parsed)
                }
            }

            return null
        }

        /**
         * Wyciąga WSZYSTKIE kwoty z tekstu OCR (dla parserów multi-amount jak Glovo/Bolt).
         * Zwraca listę MatchResult z grupą 1 = kwota.
         */
        fun findAllAmounts(text: String): List<MatchResult> {
            val normalized = normalizeOcrDigits(text)
            val results = mutableListOf<MatchResult>()
            // Zbierz z obu regexów (suffix + prefix)
            results.addAll(AMOUNT_SUFFIX_REGEX.findAll(normalized).toList())
            AMOUNT_PREFIX_REGEX.findAll(normalized).forEach { prefixMatch ->
                // Unikaj duplikatów — jeśli ta sama pozycja już znaleziona
                val alreadyFound = results.any { existing ->
                    existing.range.intersect(prefixMatch.range).isNotEmpty()
                }
                if (!alreadyFound) results.add(prefixMatch)
            }
            // Fallback jeśli nic nie znaleziono
            if (results.isEmpty()) {
                results.addAll(AMOUNT_FALLBACK_REGEX.findAll(normalized).toList())
            }
            return results
        }

        /**
         * Wykrywa walutę z tekstu OCR. Zwraca znormalizowany symbol.
         * Priorytet: PLN (zł/PLN) → UAH (грн/₴). Apka działa w PL, platformy płacą w PLN.
         * Ukraiński UI Glovo może mieć "грн" w etykietach ale kwoty są w zł.
         */
        private val PLN_REGEX = Regex("""(zł|zl|zt|PLN)""", RegexOption.IGNORE_CASE)
        private val UAH_REGEX = Regex("""(грн|rpH|₴)""", RegexOption.IGNORE_CASE)

        fun detectCurrency(text: String): String {
            if (PLN_REGEX.containsMatchIn(text)) return "zł"
            UAH_REGEX.find(text)?.let { return "грн" }
            return "zł"
        }

        /**
         * Generyczne markery gotówkowe — wykrywa czy tekst zawiera frazy
         * wskazujące na zlecenie cash-on-delivery.
         * Używane przez Wolt/Bolt (generyczny skan) i Glovo (fallback obok per-amount detekcji).
         * Markery dla Wolt/Bolt to najlepsze zgadywanie — do weryfikacji z prawdziwymi zleceniami.
         */
        private val CASH_MARKERS = listOf(
            // PL
            "gotówk", "gotowk", "płatność gotówką", "platnosc gotowka",
            // EN
            "cash payment", "pay in cash", "cash on delivery",
            // UK
            "готівк", "оплата готівкою",
            // RU
            "наличн", "оплата наличными"
        )

        fun containsCashMarkers(text: String): Boolean =
            CASH_MARKERS.any { text.contains(it, ignoreCase = true) }

        /**
         * Layer 4: Defense-in-depth helper. Sprawdza czy tekst zawiera co najmniej jeden
         * z platform-specific positive markers (frazy które ZAWSZE są na popupie oferty,
         * a NIGDY na portalach newsowych / social mediach / innych apkach).
         *
         * Używane jako warunek koniec pre-parse validacji w Uber/Bolt/Wolt parser.
         * Bez tego parser może sparsować losowy tekst news jako ofertę — kwoty + jednostki
         * czasu są wszędzie, ale konkretne frazy popupu kurierskiego — tylko na popupie.
         *
         * Markery są szerokie (10-15 fraz per platforma w 4 językach: PL/EN/UA/RU)
         * żeby nie generować false-negatives na real offers.
         */
        fun hasAnyPositiveMarker(text: String, markers: List<String>): Boolean =
            markers.any { text.contains(it, ignoreCase = true) }

        /**
         * Koryguje kwotę gdy OCR zgubił separator dziesiętny.
         * @param rawMatch dopasowany ciąg znaków z regex (np. "1720", "17,20")
         * @param parsed sparsowana wartość (np. 1720.0, 17.20)
         */
        fun sanitizeAmount(rawMatch: String, parsed: Double): Double? {
            val hasSeparator = rawMatch.contains(',') || rawMatch.contains('.')
            val corrected = when {
                hasSeparator -> parsed  // OCR widział separator — ufamy
                parsed >= 1000 -> {
                    // 4+ cyfry bez separatora: 1720 → 17.20
                    val c = parsed / 100.0
                    AppLog.w(AppLog.TAG_PARSER, "Amount corrected: $rawMatch → $c (div/100)")
                    c
                }
                parsed >= 100 -> {
                    // 3 cyfry bez separatora — 850 to raczej 8.50 niż 850 zł
                    // Odrzucamy — safe fail (brak belki), retry zrobi nowy screenshot
                    AppLog.w(AppLog.TAG_PARSER, "Amount 3-digit no separator: $rawMatch — rejecting (ambiguous)")
                    return null
                }
                else -> parsed  // < 100 — prawidłowa wartość
            }
            return when {
                corrected < MIN_REALISTIC_AMOUNT -> {
                    AppLog.w(AppLog.TAG_PARSER, "Amount $corrected below minimum — rejecting")
                    null
                }
                corrected > MAX_REALISTIC_AMOUNT ->  {
                    AppLog.w(AppLog.TAG_PARSER, "Amount $corrected above maximum — keeping (rare large order)")
                    corrected
                }
                else -> corrected
            }
        }
    }
}