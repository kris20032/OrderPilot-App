package com.courierassist.app.domain

import java.util.Locale

enum class AppLanguage {
    PL, UK, EN;

    companion object {
        fun fromSystemLocale(): AppLanguage {
            return when (Locale.getDefault().language) {
                "uk" -> UK
                "en" -> EN
                else -> PL
            }
        }
    }
}