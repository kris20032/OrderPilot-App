# CourierAssist - Asystent Kuriera

Aplikacja na Androida dla kurierów jedzenia (Glovo, UberEats, Wolt) - pomaga szybko ocenić czy zlecenie jest opłacalne.

## 🚀 Dla nowych członków zespołu

**ZANIM ZACZNIESZ PRACĘ:**

1. **Sklonuj repo:**
   ```bash
   git clone https://github.com/[username]/CourierAssist-App.git
   cd CourierAssist-App
   ```

2. **PRZECZYTAJ W KOLEJNOŚCI:**
   - 📋 [`RULES.md`](RULES.md) - **NAJWAŻNIEJSZE** - zasady współpracy i Git workflow
   - 📊 [`PROGRESS.md`](PROGRESS.md) - obecny status projektu
   - 📝 [`PLAN.md`](PLAN.md) - pełny plan implementacji (8 kroków)

3. **Zainstaluj środowisko:**
   - Android Studio
   - JDK 17
   - Zobacz: `PLAN.md` Krok 0

4. **Przed każdą pracą:**
   ```bash
   git pull  # Pobierz najnowszą wersję!
   ```

5. **Po zakończeniu pracy:**
   ```bash
   git add .
   git commit -m "Opis zmiany"
   git push
   ```

---

## 👥 Zespół

- **Krzysztof (+ Claude Code)** - główna implementacja (MediaProjection, OCR, parsery)
- **Tata** - testowanie na prawdziwych zleceniach kurierskich
- **Przyjaciel** - UI, settings, helper functions

---

## 🎯 Co robi aplikacja?

1. **Nagrywa ekran** podczas używania Glovo/UberEats/Wolt
2. **Rozpoznaje tekst** (OCR) z propozycji nowych zleceń
3. **Oblicza opłacalność** (PLN/h, PLN/km, dystans, dolot)
4. **Pokazuje kolorową belkę** na górze ekranu:
   - 🟢 Zielony = opłacalne (≥25 PLN/h)
   - 🟡 Żółty = średnio (18-25 PLN/h)
   - 🔴 Czerwony = nieopłacalne (<18 PLN/h)

**Aplikacja NIE łączy się z API kurierskimi** - tylko czyta ekran = zero ryzyka bana.

---

## 📁 Struktura projektu

```
CourierAssist/
├── README.md           ← Ten plik - start tutaj
├── RULES.md            ← Zasady współpracy (PRZECZYTAJ!)
├── PROGRESS.md         ← Status projektu
├── PLAN.md             ← Plan implementacji (8 kroków)
├── CourierAssist/      ← Projekt Android Studio
├── docs/               ← Dokumentacja techniczna
└── testing/            ← Screenshoty testowe
```

---

## 🛠️ Stack technologiczny

- **Język:** Kotlin
- **Min SDK:** Android 8.0 (API 26)
- **Screen capture:** MediaProjection API
- **OCR:** Google ML Kit (on-device)
- **Overlay:** WindowManager
- **UI:** Jetpack Compose
- **Build:** Gradle KTS

---

## 📊 Status projektu

Sprawdź [`PROGRESS.md`](PROGRESS.md) - tam jest aktualny status i co kto robi.

---

## ⚠️ Zasady Git

**KRYTYCZNE - przeczytaj [`RULES.md`](RULES.md) sekcja 4!**

Skrót:
- `git pull` przed pracą
- `git push` po pracy
- Format commita: `"Krok X: Opis"`
- Każdy pracuje na innych plikach = zero konfliktów

---

## 🎯 Następne kroki

1. Przeczytaj `RULES.md`
2. Sprawdź `PROGRESS.md` - co jest do zrobienia
3. Zainstaluj środowisko (Krok 0 w `PLAN.md`)
4. Daj znać w zespole że zaczynasz!

---

## 📞 Pytania?

Zapytaj w zespole lub sprawdź:
- `RULES.md` - zasady współpracy
- `PROGRESS.md` - obecny status
- `PLAN.md` - szczegółowy plan
