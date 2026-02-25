# Plan implementacji — CourierAssist

**Stack:** Kotlin, ViewBinding + XML, AccessibilityService, WindowManager overlay
**Min SDK:** 26 (Android 8.0)
**Package:** `com.courierassist.app`
**Zadania:** szczegóły w `docs/TASKS.md`
**Architektura:** szczegóły w `docs/ARCHITECTURE.md`

---

## Etapy implementacji

### Etap 0: Środowisko ✅
- Android Studio + JDK 17 zainstalowane
- Zmienne środowiskowe skonfigurowane

### Etap 1: Szkielet projektu + AccessibilityService 🎯 Opus
Stworzyć nowy projekt Android Studio (Empty Activity, Kotlin, Gradle KTS).

**AndroidManifest.xml:**
- `SYSTEM_ALERT_WINDOW` — overlay
- `POST_NOTIFICATIONS` — powiadomienie foreground
- `<service>` dla `CourierAccessibilityService` z konfiguracją accessibility

**Struktura pakietów:**
```
com.courierassist.app/
├── domain/
├── engine/
├── parser/
├── service/
├── overlay/
├── settings/
├── billing/
└── ui/
```

**Pliki do stworzenia (szkielety):**
- `domain/` — Offer, Platform, AnalysisResult, ProfitLevel
- `engine/` — OfferAnalyzer
- `parser/` — OfferParser (interface), ParserRegistry, UberParser (szkielet)
- `service/` — CourierAccessibilityService, OfferVisibilityDetector
- `overlay/` — OverlayManager (interface), SystemOverlayManager
- `settings/` — ThresholdConfig, SettingsRepository (interface), SharedPrefsSettingsRepository
- `billing/` — FeatureGate (interface), BillingManager (stub)
- `ui/` — MainActivity (status + link do Accessibility settings)
- `res/xml/accessibility_service_config.xml`

**Test:** Aplikacja instaluje się, w Ustawienia → Dostępność widać "CourierAssist"

---

### Etap 2: UberParser — parsowanie realnych danych 🎯 Opus
1. Uruchomić app na telefonie taty z włączonym logowaniem
2. Otworzyć UberEats, poczekać na zlecenie
3. Z logcat odczytać strukturę `AccessibilityNodeInfo`
4. Napisać `UberParser` z dopasowanymi selektorami
5. `OfferVisibilityDetector` — wykrywa przyciski Accept/Reject

**Test:** Logcat pokazuje sparsowaną ofertę (kwota + czas) przy zleceniu UberEats

---

### Etap 3: Overlay ⚡ Sonnet
Zaimplementować `SystemOverlayManager` — belka 48dp na górze ekranu (alpha 0.85).

Kolory:
- GREEN `#4CAF50` — ≥ 40 PLN/h
- YELLOW `#FF9800` — ≥ 32 PLN/h
- RED `#F44336` — < 32 PLN/h

Dane na belce: PLN/h + kwota + czas + przycisk zamknij
Debounce 300ms, `hide()` gdy oferta znika

**Test:** Hardcoded dane — kolorowa belka pojawia się nad innymi aplikacjami

---

### Etap 4: Integracja pipeline 🎯 Opus
Połączyć: `onAccessibilityEvent` → `OfferVisibilityDetector` → `UberParser` → `OfferAnalyzer` → `OverlayManager`

**Test:** Otworzyć UberEats z realnym zleceniem → belka pojawia się z danymi

---

### Etap 5: WoltParser + GlovoParser 🎯 Opus
Ten sam schemat co Etap 2, dla Wolt i Glovo.
`FeatureGate.isPro()` — Wolt i Glovo tylko w wersji Pro (na razie zawsze `true` dla testów)

---

### Etap 6: Settings UI + ThresholdConfig ⚡ Sonnet
- `SettingsActivity` — edycja progów GREEN/YELLOW
- `SharedPrefsSettingsRepository` — zapis ustawień
- `MainActivity` — podgląd aktualnych progów

---

### Etap 7: Billing (Google Play Billing v6) 🎯 Opus
Zaimplementować `BillingManager`, podpiąć `FeatureGate`.
Darmowa: tylko Uber. Pro: Wolt + Glovo + custom progi.

---

### Etap 8: Dystrybucja beta APK ⚡ Sonnet
`./gradlew assembleDebug` → APK do testów
Opcjonalnie: Firebase App Distribution

---

## Legenda modeli
- 🎯 **Opus 4.6** — skomplikowane: parsery, integracja, billing
- ⚡ **Sonnet** — boilerplate, UI, build scripts
