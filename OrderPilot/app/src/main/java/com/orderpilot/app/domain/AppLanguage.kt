package com.orderpilot.app.domain

import java.util.Locale

enum class AppLanguage {
    PL, UK, EN, RU;

    companion object {
        fun fromSystemLocale(): AppLanguage {
            return when (Locale.getDefault().language) {
                "uk" -> UK
                "en" -> EN
                "ru" -> RU
                else -> PL
            }
        }
    }
}