package com.courierassist.app.parser

import com.courierassist.app.domain.AppLanguage
import com.courierassist.app.domain.Offer
import com.courierassist.app.domain.Platform

interface OcrOfferParser {
    val platform: Platform
    val supportedPackages: Set<String>
    fun parse(ocrLines: List<String>, language: AppLanguage): Offer?
}