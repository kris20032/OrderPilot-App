# Zasady pracy nad CourierAssist

## 1. Jak wracać do projektu po restarcie

**Trigger phrases** - jak mnie przypomnieć o projekcie:
- "wróćmy do pracy nad aplikacją dla kurierów"
- "projekt CourierAssist"
- "aplikacja dla kurierów"
- "Glovo/UberEats/Wolt app"

**Co robię po usłyszeniu trigger phrase:**
1. Czytam `PROGRESS.md` - sprawdzam obecny etap
2. Czytam `docs/PLAN.md` + `docs/TASKS.md` - sprawdzam co jest do zrobienia
3. Informuję Cię: "Jesteśmy na TASK X.X.X: [nazwa]. Ostatnia aktualizacja: [data]. Kontynuujemy?"

---

## 2. Podział pracy: Opus 4.6 vs Sonnet 4.5

### Przed każdym krokiem:
**Informuję Cię:** "Teraz rekomenduje zmianę na [Opus/Sonnet], daj znać jak to zrobisz i będziemy kontynuować"

### Kiedy używać którego modelu:

**Opus 4.6** - skomplikowane zadania:
- TASK 2.1.1: AccessibilityService (konfiguracja, edge cases)
- TASK 4.2.1: UberParser (parsowanie drzewa UI, regex, multi-język)
- TASK 6.1.1: Integracja end-to-end (pipeline, debugowanie)
- Debugowanie trudnych problemów
- Decyzje architektoniczne

**Sonnet** - implementacja według spec:
- TASK 1.1.1: Inicjalizacja projektu (boilerplate)
- TASK 3.1.1: Modele domenowe (data class, enum)
- TASK 3.2.1: OfferAnalyzer (matematyka)
- TASK 5.1.1: Overlay (XML + WindowManager)
- TASK 7.1.1: START/STOP UI
- Testy jednostkowe

---

## 3. Aktualizacja PROGRESS.md

**Kiedy aktualizuję:**
- Na początku każdego kroku (status: 🔄 W trakcie)
- Po zakończeniu kroku (status: ✅ Ukończone)
- Gdy napotkam problem (sekcja: ⚠️ Problemy / Notatki)

**Co aktualizuję:**
- Data ostatniej aktualizacji
- Obecny etap
- Checklisty ([ ] → [x])
- Notatki o problemach/decyzjach

**Cel:** Sam muszę wiedzieć gdzie jesteśmy bez pytania Cię.

---

## 4. Git workflow — GitHub Flow ⚠️

**🚨 PRZECZYTAJ TO PRZED PRACĄ - DOTYCZY WSZYSTKICH 🚨**

### Zasada nadrzędna: `main` jest zawsze stabilny

- `main` = kod który się buduje i nie crashuje. Nigdy nie commituj bezpośrednio na main nowej funkcji ani niezweryfikowanego fixu.
- Każda zmiana = osobny branch → testuj → merge do main gdy działa.

### Zespół:
- **Krzysztof** - główna implementacja (używa Claude)
- **Tata** - testowanie na prawdziwych zleceniach
- **Łukasz** - wsparcie

**Aktualny podział zadań:** `PROGRESS.md` sekcja "Aktywne branche"

---

### Nazewnictwo branchy:

```
feature/opis-funkcji     ← nowa funkcja
fix/opis-buga            ← naprawa błędu
test/opis-eksperymentu   ← eksperymenty, nigdy nie trafia do main
```

**Przykłady:**
```
fix/accessibility-windows
feature/fake-uber-driver
feature/wolt-parser
fix/overlay-not-showing
```

---

### Workflow każdej sesji (AI + ludzie):

#### PRZED pracą:
```bash
git pull                    # pobierz najnowszą wersję
git status                  # sprawdź na jakim branchu jesteś
```
Przeczytaj `PROGRESS.md` → sprawdź aktywne branche i kto co robi.

#### ZACZYNAJĄC nowe zadanie:
```bash
git checkout -b fix/opis    # stwórz nowy branch
# lub
git checkout -b feature/opis
```

#### PODCZAS pracy:
```bash
git commit -m "opis zmiany" # commituj po każdym logicznym kroku
```

#### PO zakończeniu (gdy działa):
```bash
git push -u origin nazwa-brancha   # wypchnij branch na GitHub
# Następnie merge do main przez GitHub PR lub lokalnie:
git checkout main
git merge nazwa-brancha
git push
```
Zaktualizuj `PROGRESS.md` — oznacz branch jako ukończony.

---

### Wyjątek: kiedy można commitować bezpośrednio na main:
- Aktualizacja dokumentacji (`PROGRESS.md`, `RULES.md`, `docs/`)
- Drobne poprawki (literówka, komentarz)

### Jeśli wystąpi konflikt:
```bash
git pull --rebase
# Rozwiąż konflikty
git add .
git rebase --continue
git push
```
**AI:** Jeśli konflikt → zatrzymaj się i poinformuj użytkownika.

### GitHub repo:
**Nazwa:** `CourierAssist-App` | **Typ:** Private | **Branch główny:** `main`

---

**🚨 KLUCZOWE DLA AI:**
```
1. git pull na początku sesji
2. Nowe zadanie = nowy branch
3. Merge do main tylko gdy działa
4. git push na końcu sesji
```

---

## 5. Komunikacja podczas pracy

**Zasada: Zwięźle, ale zrozumiale**

### Na początku kroku:
> "TASK X.X.X: [nazwa]. Tworzę [co tworzę]. Model: [Opus/Sonnet]"

### Podczas implementacji:
> "Dodaję [plik/funkcjonalność] - [1 zdanie co robi]"

### Po skończeniu:
> "TASK X.X.X gotowy. Test: [jak zweryfikować]. Następny: [co dalej]"

### Przy problemie:
> "⚠️ Problem: [krótki opis]. Propozycja: [rozwiązanie]. OK?"

**Nie:** Długie wyjaśnienia, szczegóły techniczne (chyba że pytasz)
**Tak:** Konkret, akcja, weryfikacja

---

## 6. Testowanie i budowanie APK

**Status:** TBD - ustalimy jak dotrzemy do odpowiedniego etapu

**Dla AI:** Jak dotrzemy do momentu testowania, zapytaj użytkownika jak chce to zorganizować.

---

## 7. Środowisko pracy — VSCode vs Android Studio

**Podział narzędzi:**

- **VSCode + Claude Code** — służy wyłącznie do pisania i edycji kodu wspólnie z AI (Claude). Tu tworzymy pliki, omawiamy architekturę, generujemy implementacje.
- **Android Studio** — docelowe środowisko do budowania, kompilowania i uruchamiania aplikacji na telefonie. Każdy wygenerowany kod jest otwierany, budowany i testowany w Android Studio.

**Dla AI asystenta:**
- Kod piszemy w VSCode (przez Claude Code)
- Nigdy nie zakładaj że AI może bezpośrednio uruchomić aplikację na urządzeniu
- Instrukcje uruchomienia zawsze formułuj jako kroki do wykonania ręcznie w Android Studio lub przez `adb`
- Build i instalacja APK = zadanie użytkownika w Android Studio, nie AI

---

## Lokalizacja projektu

**Folder:** `/Users/krzysztof/Desktop/CourierAssist/`

**Kluczowe pliki:**
- `docs/PLAN.md` - plan etapów implementacji
- `docs/ARCHITECTURE.md` - architektura modułowa
- `PROGRESS.md` - tracking statusu (ZAWSZE czytam po restarcie)
- `RULES.md` - ten plik (ZAWSZE czytam po restarcie)

---

## 8. Gdzie co tworzyć — ZASADY STRUKTURY REPO

**Obowiązuje wszystkich (AI + ludzie). Przed dodaniem pliku sprawdź tę sekcję.**

### Repo root (`/CourierAssist-App/`)
Tylko pliki nawigacyjne — nic więcej:
- `README.md` — wstęp, jak zacząć
- `RULES.md` — zasady (ten plik)
- `PROGRESS.md` — status kroków

### `docs/`
Cała dokumentacja projektu:
- `PLAN.md` — plan etapów
- `ARCHITECTURE.md` — architektura modułowa
- `TASKS.md` — backlog zadań (po dodaniu)
- Inne pliki `.md` z decyzjami technicznymi

### `testing/`
Wyłącznie screenshoty i dane testowe z aplikacji kurierskich:
```
testing/
├── glovo/
├── ubereats/
└── wolt/
```

### Projekt Android (`app/` po stworzeniu)
```
app/src/main/java/com/courierassist/app/
├── domain/      ← Offer, Platform, AnalysisResult, ProfitLevel
├── engine/      ← OfferAnalyzer
├── parser/      ← OfferParser, ParserRegistry, UberParser, WoltParser, GlovoParser
├── service/     ← CourierAccessibilityService, OfferVisibilityDetector
├── overlay/     ← OverlayManager, SystemOverlayManager, OverlayViewFactory
├── settings/    ← ThresholdConfig, SettingsRepository, SharedPrefsSettingsRepository
├── billing/     ← FeatureGate, BillingManager
└── ui/          ← MainActivity, SettingsActivity

app/src/main/res/
├── xml/         ← accessibility_service_config.xml
├── layout/      ← pliki XML layoutów overlay
└── values/      ← kolory, stringi, style
```

**Zasada:** każdy nowy plik Kotlin trafia do odpowiedniej warstwy. Nie tworzyć plików bezpośrednio w `com.courierassist.app/` — zawsze w podfolderze warstwy.

---

## Workflow po restarcie Claude Code

1. User pisze trigger phrase (np. "wróćmy do aplikacji dla kurierów")
2. Czytam `PROGRESS.md` + `RULES.md` + `docs/ARCHITECTURE.md`
3. Informuję: "Jesteśmy na TASK X.X.X, ostatnia aktualizacja [data]. Kontynuujemy?"
4. User potwierdza
5. Sprawdzam jaki model jest potrzebny, informuję o zmianie jeśli trzeba
6. Zaczynam pracę

---

**Ostatnia aktualizacja:** 2026-02-26
