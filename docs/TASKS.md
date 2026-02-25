# Lista zadań — CourierAssist

**Package:** `com.courierassist.app`
**UI:** ViewBinding + XML (bez Compose)

---

## Kolejność realizacji

| # | Task | Trudność | Model | Zależności |
|---|------|----------|-------|-----------|
| 1 | TASK 1.1.1 — Inicjalizacja projektu | 1/5 | Sonnet | brak |
| 2 | TASK 3.1.1 — Modele domenowe | 1/5 | Sonnet | Projekt |
| 3 | TASK 3.2.1 — OfferAnalyzer | 2/5 | Sonnet | Domain |
| 4 | TASK 2.1.1 — AccessibilityService | 3/5 | Opus | Projekt |
| 5 | TASK 4.1.1 — OfferParser interface | 1/5 | Sonnet | Domain |
| 6 | TASK 4.2.1 — UberParser | 5/5 | Opus | Accessibility + Domain |
| 7 | TASK 2.4.1 — OfferVisibilityDetector | 2/5 | Sonnet | Parser + Domain |
| 8 | TASK 5.1.1 — Overlay | 4/5 | Sonnet | Accessibility |
| 9 | TASK 6.1.1 — Integracja | 5/5 | Opus | Parser + Overlay + Analyzer |
| 10 | TASK 7.1.1 — START/STOP UI | 3/5 | Sonnet | Accessibility |

---

## Zależności

```
Analyzer → Domain
Parser → Domain
Accessibility → Parser
Overlay → Domain
Integracja → wszystko
```

---

## EPIC 1 — FOUNDATION & CORE ARCHITECTURE

### STORY 1.1 — Utworzenie projektu Android

#### TASK 1.1.1 — Inicjalizacja projektu

**Opis:**
Utworzyć nowy projekt Android:
- Nazwa: CourierAssist
- Package: `com.courierassist.app`
- minSdk: 26
- targetSdk: latest stable
- Kotlin
- Empty Activity
- ViewBinding włączony
- Bez Compose
- Bez Hilt (na start ręczne DI)

**Checklist:**
- [ ] Projekt się kompiluje
- [ ] MainActivity uruchamia się poprawnie
- [ ] minSdk = 26
- [ ] Brak błędów builda

**Trudność:** 1/5 | **Kolejność:** 1

---

## EPIC 2 — ACCESSIBILITY SERVICE

### STORY 2.1 — Konfiguracja AccessibilityService

#### TASK 2.1.1 — Utworzenie CourierAccessibilityService

**Opis:**
Stworzyć AccessibilityService:
- Nasłuchuje `TYPE_WINDOW_CONTENT_CHANGED`
- Filtruje pakiet Uber Driver (placeholder string)
- Zarejestrowany w AndroidManifest.xml
- `accessibility_config.xml`
- Target Android 8+
- Bez logiki biznesowej wewnątrz

**Checklist:**
- [ ] Service zarejestrowany w manifest
- [ ] accessibility_config.xml poprawny
- [ ] Działa po włączeniu w ustawieniach systemowych
- [ ] Nasłuchuje TYPE_WINDOW_CONTENT_CHANGED

**Trudność:** 3/5 | **Kolejność:** 4

---

### STORY 2.4 — Detekcja widoczności oferty

#### TASK 2.4.1 — OfferVisibilityDetector

**Plik:** `parser/OfferVisibilityDetector.kt`

**Opis:**
- Metoda: `isOfferVisible(rootNode: AccessibilityNodeInfo): Boolean`
- Zwraca `true` tylko gdy w UI jest przycisk Accept/Akceptuj ORAZ layout oferty
- Obsługa PL/EN/UKR
- Parser działa tylko jeśli zwraca `true`

**Checklist:**
- [ ] Działa dla PL/EN/UKR
- [ ] Nie blokuje głównej pętli Accessibility
- [ ] Odseparowana od parsera
- [ ] Zwraca false gdy brak przycisku Accept

**Trudność:** 2/5 | **Kolejność:** 7

---

## EPIC 3 — DOMAIN & ANALYSIS ENGINE

### STORY 3.1 — Modele domenowe

#### TASK 3.1.1 — Utworzenie modeli domenowych

**Pliki:**
- `domain/Offer.kt`
- `domain/Platform.kt`
- `domain/AnalysisResult.kt`
- `domain/ProfitLevel.kt`

**Checklist:**
- [ ] Modele nie zależą od Android SDK
- [ ] Są w pakiecie `domain`
- [ ] Kod kompiluje się
- [ ] Są data class / enum

**Trudność:** 1/5 | **Kolejność:** 2

---

### STORY 3.2 — Silnik analizy

#### TASK 3.2.1 — Implementacja OfferAnalyzer

**Progi:**
- GREEN >= 40 PLN/h
- YELLOW >= 32 i < 40 PLN/h
- RED < 32 PLN/h

**Checklist:**
- [ ] Działa dla różnych danych
- [ ] Zwraca poprawny ProfitLevel
- [ ] Nie crashuje przy dzieleniu przez 0
- [ ] Czysty Kotlin (bez Android SDK)

**Trudność:** 2/5 | **Kolejność:** 3

---

## EPIC 4 — UBER PARSER

### STORY 4.1 — Parser interfejs

#### TASK 4.1.1 — OfferParser interface

**Plik:** `parser/OfferParser.kt`

```kotlin
interface OfferParser {
    fun canHandle(packageName: String): Boolean
    fun parse(rootNode: AccessibilityNodeInfo): Offer?
}
```

**Trudność:** 1/5 | **Kolejność:** 5

---

### STORY 4.2 — Implementacja UberParser

#### TASK 4.2.1 — UberParser

**Założenia:**
- Wyszukiwanie tekstów: "zł" / "PLN", "min", "Accept" / "Akceptuj"
- Regex do wyciągania liczb
- Obsługa PL/EN/UKR

**Checklist:**
- [ ] Zwraca Offer gdy dane kompletne
- [ ] Zwraca null gdy brak danych
- [ ] Obsługuje EN/PL/UKR

**Trudność:** 5/5 | **Kolejność:** 5

---

## EPIC 5 — OVERLAY SYSTEM

### STORY 5.1 — OverlayManager

#### TASK 5.1.1 — SystemOverlayManager

**Wymagania:**
- `TYPE_APPLICATION_OVERLAY`
- `NOT_TOUCHABLE`
- `NOT_FOCUSABLE`
- Dopasowanie do dark/light mode
- 70% opacity
- Pozycja: góra ekranu

**Checklist:**
- [ ] Overlay pojawia się nad innymi aplikacjami
- [ ] Kolory: GREEN #4CAF50, YELLOW #FF9800, RED #F44336
- [ ] Działa na Android 8+
- [ ] hide() prawidłowo ukrywa

**Trudność:** 4/5 | **Kolejność:** 6

---

## EPIC 6 — INTEGRATION

### STORY 6.1 — Połączenie parser + analyzer + overlay

#### TASK 6.1.1 — Integracja w AccessibilityService

**Pipeline:**
```
onAccessibilityEvent
  → detect Uber package
  → parse Offer
  → analyze (OfferAnalyzer)
  → show overlay
  → hide overlay if offer disappears
```

**Wymagania:**
- Debounce 300ms
- No crashes
- Defensive null checks

**Checklist:**
- [ ] Pipeline działa end-to-end
- [ ] Overlay pojawia się przy ofercie
- [ ] Overlay znika gdy oferta znika
- [ ] Brak crashów

**Trudność:** 5/5 | **Kolejność:** 7

---

## EPIC 7 — START / STOP CONTROL

### STORY 7.1 — UI sterowania

#### TASK 7.1.1 — MainActivity z przyciskiem START/STOP

**Wymagania:**
- Toggle button START/STOP
- Stan zapisany w SharedPreferences
- Service reaguje na stan
- UI: PL/EN/UKR via strings.xml

**Checklist:**
- [ ] Przycisk działa poprawnie
- [ ] Stan przeżywa restart aplikacji
- [ ] Service nie przetwarza eventów gdy STOP
- [ ] Wielojęzyczność

**Trudność:** 3/5 | **Kolejność:** 8
