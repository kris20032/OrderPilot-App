package com.courierassist.app.parser

import com.courierassist.app.di.AppLog
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

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
        // zł, zl (OCR gubi ł), zt (OCR zamienia ł→t), z (OCR gubi ł całkiem),
        // PLN (angielski), грн (ukraiński), rpH (OCR czyta грн jako łacinkę), ₴ (symbol hrywny)
        private const val CUR = """(?:zł|zl|zt|z(?=\s|\d|$)|PLN|грн|rpH|₴)"""

        // Główny regex: LICZBA + WALUTA (np. "7,86 zł", "7.86 PLN", "7,86zl")
        val AMOUNT_SUFFIX_REGEX = Regex("""(\d+(?:[.,]\d+)?)${WS}*${CUR}""", RegexOption.IGNORE_CASE)

        // Odwrócony: WALUTA + LICZBA (np. "PLN7.86", "PLN 7.86", "zł7,86")
        val AMOUNT_PREFIX_REGEX = Regex("""${CUR}${WS}*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)

        // Fallback: luźna liczba dziesiętna (np. "7.86") — NOT obok min/km/хв/км
        // Wymaga separatora dziesiętnego żeby odfiltrować losowe liczby całkowite
        val AMOUNT_FALLBACK_REGEX = Regex("""(?<!\d)(\d+[.,]\d+)(?!\s*(?:min|хв|XB|km|км|mi))""", RegexOption.IGNORE_CASE)

        // Regex do detekcji waluty (do wyświetlenia w belce)
        val CURRENCY_DETECT_REGEX = Regex("""(zł|zl|zt|PLN|грн|rpH|₴)""", RegexOption.IGNORE_CASE)

        /**
         * Normalizuje typowe błędy OCR w cyfrach:
         * - 'l' (lowercase L), 'I' (uppercase i), '|' (pipe) obok cyfr → '1'
         * OCR często myli te znaki z cyfrą 1 (np. "l8,64 zł" zamiast "18,64 zł").
         */
        fun normalizeOcrDigits(text: String): String {
            return text
                .replace(Regex("""[lI|](?=\d)"""), "1")   // l8 → 18, I8 → 18
                .replace(Regex("""(?<=\d)[lI|](?=\d)"""), "1") // 1l0 → 110
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
         */
        fun detectCurrency(text: String): String {
            val raw = CURRENCY_DETECT_REGEX.find(text)?.groupValues?.get(1) ?: return "zł"
            return when {
                raw.equals("rpH", ignoreCase = true) -> "грн"
                raw.equals("zl", ignoreCase = true) -> "zł"
                raw.equals("zt", ignoreCase = true) -> "zł"
                raw.equals("₴", ignoreCase = true) -> "грн"
                else -> raw
            }
        }

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
                    // 3 cyfry bez separatora — nie wiemy czy np. 850=8.50 czy 85.0
                    // Zostawiamy i logujemy, zbieramy dane z produkcji
                    AppLog.w(AppLog.TAG_PARSER, "Amount 3-digit no separator: $rawMatch — leaving as-is")
                    parsed
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