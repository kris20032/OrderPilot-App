# CourierAssist — Specyfikacja Produktu v1

**Data:** 2026-02-27
**Status:** Zatwierdzona

---

## Cel

Aplikacja dla kurierów (rower, skuter, auto) która **natychmiast analizuje oferty zleceń** z aplikacji kurierskich i wyświetla ocenę na belce overlay — żeby kurier jednym rzutem oka wiedział czy zlecenie się opłaca.

---

## Użytkownicy

- Kurierzy w Polsce i Ukrainie (rower, skuter, auto)
- Docelowo Google Play, ale v1 = dystrybucja APK / zamknięta beta
- Bez monetyzacji w v1, architektura przygotowana na przyszły freemium
- Telefon zazwyczaj zamontowany w uchwycie (kierownica roweru, deska rozdzielcza auta, skuter) — pionowo

---

## Główny problem

Kurier jedzie (rowerem!) i nie ma czasu ani możliwości liczyć zł/h w głowie. Popup zlecenia pojawia się na ~15 sekund, trzeba szybko decydować. Często trzeba się zatrzymać żeby ocenić czy zlecenie się opłaca. Nasza belka daje natychmiastową ocenę — zielony/żółty/czerwony + metryki — bez potrzeby zatrzymywania się.

---

## Obsługiwane platformy kurierskie

- **Uber Eats / Uber Driver** — OCR (MediaProjection / takeScreenshot fallback)
- **Wolt** — OCR (MediaProjection / takeScreenshot fallback)
- **Glovo** — accessibility tree (natywne UI)
- **Bolt Food** — accessibility tree (natywne UI)
- Kurier często ma włączonych kilka aplikacji jednocześnie i bierze najlepszą ofertę
- **Multi-overlay:** max 2 belki jednocześnie z różnych platform do porównania

---

## Pipeline (sprawdzony w POC)

```
Popup zlecenia pojawia się (TYPE_APPLICATION_OVERLAY)
    → AccessibilityService wykrywa event
    → ScreenCaptureService robi screenshot (MediaProjection)
    → PopupCropper przycina region popupu
    → ML Kit OCR rozpoznaje tekst
    → Parser wyciąga: kwotę, czas, dystans
    → Analyzer liczy zł/h, zł/km
    → Overlay wyświetla belkę z oceną
```

Aplikacja nasłuchuje pasywnie na zdarzenia — zero pollingu. Aktywuje się tylko gdy pojawi się popup zlecenia.

---

## Belka (overlay)

| Cecha | v1 | Przyszłość |
|-------|-----|------------|
| **Pozycja** | Góra ekranu (stała) | Konfigurowalna pozycja |
| **Zawartość** | Konfigurowalna — użytkownik wybiera metryki | Więcej metryk |
| **Kolor** | 3 poziomy (zielony/żółty/czerwony) z konfigurowalnymi progami | Więcej poziomów |
| **Multi-overlay** | Max 2 belki naraz z różnych platform, etykieta platformy | — |
| **Znikanie** | Osobne timery per platforma, przycisk × na każdej belce | — |
| **Animacje** | Brak (ale kod przygotowany na dodanie) | Slide in/out, fade |
| **Dźwięk/wibracja** | Brak | Opcjonalne w ustawieniach |

### Metryki do wyświetlenia na belce (użytkownik wybiera):
- zł/h (stawka godzinowa)
- zł/km (stawka za kilometr)
- Dystans (km)
- Czas (min)
- Kwota (zł)

---

## Ustawienia

**Filozofia: proste na pierwszy rzut oka, głębokie dla zaawansowanych.**

Użytkownik pobiera aplikację → odpala → od razu działa na domyślnych ustawieniach. Jak chce spersonalizować — wchodzi głębiej.

### Ustawienia v1:

| Ustawienie | Opis | Domyślne |
|------------|------|----------|
| **Progi zł/h** | Kiedy zielony, żółty, czerwony | 40/32 zł/h |
| **Progi zł/km** | Analogicznie per km | TBD |
| **Co wyświetlać na belce** | Checkboxy: zł/h, zł/km, dystans, czas, kwota | zł/h + kolor |
| **Min/max dystans** | Ignoruj oferty poza zakresem | Wyłączone |
| **Tryb nocny/dzienny** | Automatyczny z systemu + ręczny override | Auto |
| **Język** | PL (domyślny), UK, EN | PL |
| **Ustawienia per platforma** | Domyślnie globalne, z możliwością override | Globalne |

### Hierarchia ustawień:
```
Globalne (domyślne)
  └── Per platforma (opcjonalne nadpisanie)
        np. Uber: progi 45/35 zł/h
            Glovo: progi 30/25 zł/h
```

---

## Wielojęzyczność

### Języki UI:
- **Polski** (domyślny)
- **Ukraiński**
- **Angielski**

Wyraźna flaga wyboru języka w ustawieniach.

### Języki OCR/Parser:
Zmiana języka wpływa na **dwie rzeczy**:
1. **UI aplikacji** — tłumaczenia strings.xml
2. **OCR/Parser** — parser musi rozumieć język platformy kurierskiej

Jeśli kurier ma Ubera po ukraińsku, parser musi rozpoznawać:
- Walutę: "грн" zamiast "zł"
- Czas: "хв" zamiast "min"
- Dystans: "км" zamiast "km"
- Przyciski: "Прийняти" zamiast "Akceptuj"

Każdy parser ma zestaw regex/słowników per język platformy.

---

## Ekran główny

- **Duży przycisk START/STOP** — włącza/wyłącza nasłuchiwanie
- **Status:** "Aktywny" / "Nieaktywny" + wskaźniki uprawnień:
  - Accessibility Service: ✓ / ✗
  - MediaProjection (nagrywanie ekranu): ✓ / ✗
  - Overlay permission: ✓ / ✗
- **Ikona zębatki** → ekran ustawień
- **Pole tekstowe** na dodatkowe informacje (np. porady, status, info o aktualizacji)

---

## Styl wizualny

- **Tryb ciemny + jasny** — automatyczne przełączanie z ustawień systemu + ręczny override
- Minimalistyczny, czytelny — kurier patrzy na ekran ułamek sekundy
- Material Design 3 jako baza

---

## Czego NIE ma w v1

| Funkcja | Kiedy |
|---------|-------|
| Historia zleceń / statystyki | v2+ |
| Monetyzacja / billing | v2+ |
| Wibracja / dźwięk przy zleceniu | v2+ |
| Zmiana pozycji belki | v2+ (kod przygotowany w v1) |
| Animacje belki | v2+ (kod przygotowany w v1) |

---

## Wymagania techniczne

- **Min SDK:** 26
- **Target SDK:** latest stable
- **Język:** Kotlin
- **UI:** ViewBinding + XML (bez Compose)
- **Architektura:** jeden moduł Gradle `:app` z pakietami warstw (di, domain, engine, parser, capture, ocr, pipeline, service, overlay, settings, billing, ui)
- **Wydajność:** <1s od popupu do belki, minimalne zużycie baterii/RAM/CPU
- **Uprawnienia:** Accessibility Service, MediaProjection, System Alert Window (overlay)

---

## Wymagania Google Play (przygotowanie)

Nawet jeśli v1 nie idzie na Play, architektura powinna być zgodna z:
- Polityką Accessibility Service (Google wymaga uzasadnienia)
- Polityką overlay (TYPE_APPLICATION_OVERLAY)
- Privacy policy
- Deklaracją uprawnień

---

## Decyzje architektoniczne (z POC + produkcji)

Sprawdzone i potwierdzone:
1. **Dual pipeline:** Uber/Wolt → OCR (MediaProjection lub takeScreenshot fallback), Glovo/Bolt → accessibility tree (natywne UI)
2. `AccessibilityService.takeScreenshot()` (API 30+) działa jako fallback bez MediaProjection — widzi React Native popupy
3. Glovo/Bolt mają natywne UI → `getRootInActiveWindow()` zwraca tekst → brak potrzeby OCR
4. Uber popup (React Native) = brak tekstu w accessibility tree → OCR konieczne
5. MediaProjection wymaga **ForegroundService z typem mediaProjection** (nie AccessibilityService)
6. Emulator nie obsługuje `VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR` — testowanie tylko na fizycznym urządzeniu
7. EventThrottler: per platforma, 100ms delay + 1.5s cooldown
8. ML Kit OCR poprawnie czyta polskie kwoty ("34,58 zł"), czas, dystans
9. Multi-overlay: max 2 belki naraz, osobne WindowManager okna, osobne timery per platforma
