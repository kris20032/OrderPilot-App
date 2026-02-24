# Plan: CourierAssist - Asystent Kuriera (Android)

---

## ⚠️ ZANIM ZACZNIESZ - PRZECZYTAJ TO! ⚠️

**Jesteś nową osobą w projekcie?**

1. **Przeczytaj `RULES.md`** - zasady współpracy, Git workflow, podział zadań
2. **Przeczytaj `PROGRESS.md`** - gdzie jesteśmy, co jest w trakcie, co zrobione
3. **ZAWSZE przed pracą:** `git pull` (pobierz najnowszą wersję)
4. **ZAWSZE po pracy:** `git add . && git commit -m "opis" && git push`

**Zespół:**
- Krzysztof (z Claude) - główna implementacja
- Tata - testowanie na prawdziwych zleceniach
- Przyjaciel - UI, settings, wsparcie

**Pytania?** Sprawdź `RULES.md` lub zapytaj w zespole.

---

## Kontekst
Aplikacja na Androida dla kurierów jedzenia (Glovo, UberEats, Wolt) - analogiczna do "RideHelper Asystent TAXI" ale dla food delivery. Aplikacja nagrywa ekran, rozpoznaje nowe zlecenia i wyświetla nakładkę (overlay) z oceną opłacalności: zielony/żółty/czerwony + dane: PLN/h, PLN/km, dystans, dolot.

## Stack technologiczny
- **Język**: Kotlin (natywny Android - wymagany dla MediaProjection API i overlay)
- **Min SDK**: 26 (Android 8.0)
- **UI główne**: Jetpack Compose
- **Overlay**: WindowManager + XML layout
- **OCR**: Google ML Kit Text Recognition (on-device, darmowe, bez internetu)
- **Screen capture**: MediaProjection API
- **Build**: Gradle KTS
- **DI**: Hilt

## Krok 0: Instalacja środowiska na macOS ⚡ Sonnet

Zainstalować:
1. **Android Studio** - `brew install --cask android-studio`
2. **JDK 17** - `brew install openjdk@17`
3. **Zmienne środowiskowe** w `~/.zshrc`:
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   export ANDROID_HOME=$HOME/Library/Android/sdk
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   ```
4. Uruchomić Android Studio, zainstalować SDK (API 26+, Build Tools, Platform Tools)
5. Podłączyć telefon Androida USB, włączyć Opcje Deweloperskie + Debugowanie USB
6. Sprawdzić: `adb devices`

**Test**: `adb devices` pokazuje podłączony telefon.

## Krok 1: Szkielet projektu + uprawnienia ⚡ Sonnet

Stworzyć nowy projekt w Android Studio (Empty Activity, Kotlin, Gradle KTS, package `com.courierassist`), dodać uprawnienia w `AndroidManifest.xml`:
- `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_MEDIA_PROJECTION`
- `SYSTEM_ALERT_WINDOW`
- `POST_NOTIFICATIONS`

Stworzyć `MainActivity` z przyciskiem Start/Stop i flow uprawnień. Stworzyć `ScreenCaptureService` jako foreground service z powiadomieniem.

**Test**: Aplikacja instaluje się na telefonie, prosi o uprawnienia, pokazuje notyfikację.

## Krok 2: Przechwytywanie ekranu (MediaProjection) 🎯 Opus

Zaimplementować `ScreenCaptureManager` - co 1.5s robi screenshot przez `VirtualDisplay` + `ImageReader`. Dodać detekcję zmian ekranu (porównanie pikseli) - jeśli ekran się nie zmienił, pomijamy OCR.

**Test**: Zrzuty ekranu zapisują się do pliku, widoczne przez `adb`.

## Krok 3: OCR (ML Kit) 🎯 Opus

Dodać zależność `com.google.mlkit:text-recognition`, zaimplementować `OcrEngine` - wrapper na ML Kit. Dodać preprocessing bitmap (grayscale, kontrast). Pipeline: screenshot → crop do interesującego regionu → preprocess → OCR → tekst.

**Test**: W logcat widać rozpoznany tekst z ekranu telefonu.

## Krok 4: Overlay (nakładka) ⚡ Sonnet

Zaimplementować `OverlayService` - małą belkę na górze ekranu (48dp, alpha 0.85). Layout XML z verdict (PLN/h) + szczegóły (PLN/km, dystans, czas) + przycisk zamknij. Kolory: zielony `#4CAF50`, żółty `#FF9800`, czerwony `#F44336`.

Ważne: overlay umieszczony na górze ekranu, OCR cropuje środek/dół - unikamy self-capture.

**Test**: Kolorowa belka pokazuje się nad innymi aplikacjami z hardcoded danymi.

## Krok 5: Parser zleceń - Glovo (pierwszy) 🎯 Opus

Stworzyć interfejs `OrderParser` + `OrderData` (data class z: courierApp, pickup, delivery, pay, distance, time). Zbudować `GlovoParser` z regexami.

Proces tworzenia parserów (to robimy my jako developerzy, nie użytkownik):
1. Na telefonie taty uruchomić aplikację z włączonym capture
2. Poczekać na realne zlecenie - aplikacja automatycznie przechwyci ekran
3. Z logów OCR zobaczyć co ML Kit zwraca
4. Napisać regexy dopasowane do prawdziwego outputu

Następnie: `UberEatsParser`, `WoltParser` - ten sam schemat.

**Ważne**: Aplikacja NIE łączy się z API Glovo/UberEats/Wolt. Nie ingeruje w aplikacje kurierskie. Tylko czyta ekran (MediaProjection) i rozpoznaje tekst (OCR) - zero ryzyka bana.

**Test**: Z automatycznie przechwyconego ekranu Glovo parsuje kwotę, dystans, czas.

## Krok 6: Analiza opłacalności ⚡ Sonnet

Zaimplementować `ProfitabilityAnalyzer` - oblicza PLN/h, PLN/km, verdict (GREEN/YELLOW/RED) na podstawie konfigurowalnych progów. Domyślne progi: GREEN ≥ 25 PLN/h, YELLOW ≥ 18 PLN/h, RED < 18 PLN/h.

## Krok 7: Integracja end-to-end 🎯 Opus

Połączyć pipeline: Capture → Change Detection → Crop → OCR → AppDetector → Parser → Analyzer → Overlay. Dodać ekran ustawień (progi, wybór aplikacji kurierskich).

**Test**: Otworzyć Glovo z prawdziwym zleceniem - belka się podświetla z danymi.

## Krok 8: Dystrybucja beta APK ⚡ Sonnet

`./gradlew assembleDebug` → APK do udostępnienia. Opcjonalnie Firebase App Distribution.

## Weryfikacja końcowa
1. `./gradlew installDebug` - aplikacja instaluje się na telefonie
2. Uruchomić aplikację, nadać uprawnienia, nacisnąć Start
3. Otworzyć Glovo/UberEats/Wolt, poczekać na zlecenie
4. Belka powinna się pojawić z kolorową oceną i danymi
5. Sprawdzić logcat: `adb logcat -s CourierAssist:V`

## Struktura projektu Android
```
com.courierassist/
├── App.kt, MainActivity.kt
├── capture/   (ScreenCaptureService, ScreenCaptureManager, ScreenshotProcessor)
├── ocr/       (OcrEngine, OcrResult)
├── parser/    (OrderParser, OrderData, AppDetector, GlovoParser, UberEatsParser, WoltParser)
├── analyzer/  (ProfitabilityAnalyzer, ThresholdConfig)
├── overlay/   (OverlayService, OverlayView)
├── data/      (Room DB, DataStore preferences)
└── ui/        (HomeScreen, SettingsScreen, HistoryScreen)
```

---

## Legenda modeli
- 🎯 **Opus 4.6**: Skomplikowane zadania wymagające głębokiego rozumowania (MediaProjection, OCR, parsery, integracja)
- ⚡ **Sonnet 4.5**: Implementacja według spec, boilerplate, UI layouts, build scripts
