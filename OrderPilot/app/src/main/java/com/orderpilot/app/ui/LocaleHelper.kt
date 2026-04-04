package com.orderpilot.app.ui

import android.content.Context
import android.content.res.Configuration
import com.orderpilot.app.domain.AppLanguage
import java.util.Locale

object LocaleHelper {

    fun wrap(context: Context, language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.PL -> Locale("pl")
            AppLanguage.UK -> Locale("uk")
            AppLanguage.EN -> Locale("en")
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}