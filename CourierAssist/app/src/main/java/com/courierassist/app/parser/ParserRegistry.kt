package com.courierassist.app.parser

class ParserRegistry(
    private val parsers: List<OfferParser>
) {
    fun getParser(packageName: String): OfferParser? {
        return parsers.firstOrNull { it.canHandle(packageName) }
    }
}
