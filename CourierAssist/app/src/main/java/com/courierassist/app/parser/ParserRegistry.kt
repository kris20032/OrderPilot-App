package com.courierassist.app.parser

class ParserRegistry(private val parsers: List<OcrOfferParser>) {

    fun getParser(packageName: String): OcrOfferParser? =
        parsers.firstOrNull { packageName in it.supportedPackages }

    fun getAllWatchedPackages(): Set<String> =
        parsers.flatMap { it.supportedPackages }.toSet()
}