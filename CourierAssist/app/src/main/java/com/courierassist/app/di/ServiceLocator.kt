package com.courierassist.app.di

import android.content.Context

/**
 * Manual DI — rośnie z kolejnymi epicami.
 * Po każdym epicu dodającym nowe klasy rejestrujemy je tutaj.
 */
object ServiceLocator {

    fun init(context: Context) {
        // EPIC 1: fundament — brak zależności do zarejestrowania
        // EPIC 3: settingsRepository = SharedPrefsSettingsRepository(context)
        // EPIC 4: offerAnalyzer, offerFilter
        // EPIC 5: parserRegistry
        // EPIC 6: popupCropper
        // EPIC 7: ocrEngine
        // EPIC 8: overlayManager, overlayAutoHider
        // EPIC 9: pipelineOrchestrator
    }
}
