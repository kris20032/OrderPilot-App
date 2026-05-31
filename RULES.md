# Zasady pracy nad OrderPilot

## 1. Jak wracać do projektu po restarcie

**Trigger phrases** — jak mi przypomnieć o projekcie:
- "wróćmy do pracy nad aplikacją dla kurierów"
- "projekt OrderPilot"
- "aplikacja dla kurierów"
- "Glovo/UberEats/Wolt app"

**Co robię po usłyszeniu:** wykonuję **SEKWENCJĘ STARTOWĄ z `CLAUDE.md`** (gdzie jestem → branch → `todo.md` + ogon `PROGRESS.md` → kontrola higieny → weryfikacja plików z zadania). Nie powielam tu kroków — `CLAUDE.md` jest jedynym źródłem sekwencji startowej. Po wczytaniu informuję: „Stan: [aktualny etap z `todo.md`]. Kontynuujemy?"

---

## 2. Podział pracy: Opus 4.6 vs Sonnet 4.6

### Przed każdym krokiem:
**Informuję Cię:** "Teraz rekomenduje zmianę na [Opus/Sonnet], daj znać jak to zrobisz i będziemy kontynuować"

### Kiedy używać którego modelu:

**Opus 4.6** - skomplikowane zadania wymagające głębokiej analizy:
- Debugowanie nieoczywistych bugów (crashe, race conditions, pipeline)
- Decyzje architektoniczne i refaktoring wielu plików
- Analiza logów i diagnoza problemów z MediaProjection/AccessibilityService
- Zadania gdzie pierwsze podejście może być złe i trzeba rozważyć wiele opcji

**Sonnet 4.6** - implementacja według jasnej specyfikacji:
- Nowe funkcje UI (XML, layouty, Activity)
- Bugfixy z jasno zidentyfikowaną przyczyną
- Zmiany w jednym lub kilku plikach
- Aktualizacje dokumentacji i konfiguracji

---

## 3. Pliki pamięci — gdzie co trafia

> Pełny model ról plików i progi higieny są w `CLAUDE.md` → „Model plików pamięci". **Nie powielamy ich tutaj**
> (jedna informacja = jedno miejsce). Ten plik trzyma wyłącznie szczegóły referencyjne, których nie ma w `CLAUDE.md`
> (git workflow, build/test, keystore, struktura repo).

---

## 3b. Sesja porządkowa — przegląd spójności

**Co to jest:** Przegląd spójności całego projektu (nie kodu — patrz niżej). AI robi to i informuje użytkownika.

**Trigger (realny, nie „licznik"):** AI nie ma trwałego licznika zadań między sesjami — **nie udawaj, że liczysz**. Sesję porządkową odpalasz, gdy:
- **hook `pre-commit` ostrzega** o przekroczeniu progu higieny (`CLAUDE.md` 120 / `todo.md` 80 / `PROGRESS.md` 150), **lub**
- na starcie sesji widzisz, że `todo.md` / `PROGRESS.md` / `DECISIONS.md` się rozjeżdżają, **lub**
- użytkownik o to prosi.

**Co AI sprawdza i ewentualnie poprawia:**
1. **Higiena rozmiaru:** `bash scripts/memory-check.sh` (progi 120/80/150). Przekroczone → posprzątaj (archiwizacja `PROGRESS.md` → `PROGRESS_ARCHIVE.md`, czyszczenie zamkniętych zadań z `todo.md`).
2. `todo.md` — czy stan, otwarte zadania, branche i priorytety są spójne z rzeczywistością; czy nie ma zamkniętych zadań, które powinny zejść do `PROGRESS.md`.
3. `PROGRESS.md` / `DECISIONS.md` — czy log i decyzje nie rozjeżdżają się z `todo.md` (np. coś oznaczone DONE w logu, a wciąż TODO w todo).
4. `docs/future_polish_fixes.md` — czy `todo.md` nie zaczął kopiować wpisów zamiast linkować #N.
5. Branche — czy są przestarzałe do odnotowania w `todo.md` „Aktywne branche".

**Czego AI NIE robi w sesji porządkowej:**
- Nie czyta każdego pliku kodu
- Nie zmienia kodu aplikacji
- Nie sprawdza czy kod się kompiluje

**Jak AI informuje użytkownika:**
> "Hook zgłosił przekroczenie progu / widzę rozjazd w `todo.md`. Robię przegląd projektu — zaraz raport."
Po sprawdzeniu: krótki raport co poprawiono (lub "wszystko OK, nic do zmiany").

**Commit po sesji:** Jeśli były zmiany — jeden commit z opisem `docs: sesja porządkowa`.

---

## 4. Git workflow — GitHub Flow ⚠️

**🚨 PRZECZYTAJ TO PRZED PRACĄ - DOTYCZY WSZYSTKICH 🚨**

### Zasada nadrzędna: `main` jest zawsze stabilny

- `main` = kod który się buduje i nie crashuje. Nigdy nie commituj bezpośrednio na main nowej funkcji ani niezweryfikowanego fixu.
- Każda zmiana = osobny branch → testuj → merge do main gdy działa.

### 🔒 Ochrona POC na `main`

**Od 2026-02-27 `main` zawiera działający POC (MediaProjection + OCR + belka).**

- **ZAKAZ** modyfikowania kodu w `app/` bezpośrednio na `main`
- **KAŻDE** ulepszenie, fix, eksperyment = nowy branch (`feature/...`, `fix/...`, `test/...`)
- Do `main` trafia tylko dokumentacja (`PROGRESS.md`, `RULES.md`, `docs/`)
- Jeśli AI lub człowiek chce zmienić kod = STOP, stwórz branch, dopiero wtedy koduj

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
**Nazwa:** `OrderPilot-App` | **Typ:** Private | **Branch główny:** `main`

---

**🚨 KLUCZOWE DLA AI:**
```
1. git pull na początku sesji
2. Nowe zadanie = nowy branch
3. Merge do main tylko gdy działa
4. git push na końcu sesji
```

---

## 4b. Analiza bugów — ZASADA OBOWIĄZKOWA DLA AI

**Przy każdym zgłoszonym bugu AI MUSI przed zaproponowaniem fixa:**

1. **Sprawdzić szerszy kontekst** — czy ten bug to objaw głębszego problemu architektonicznego?
2. **Poszukać powiązanych bugów** — gdzie jeszcze ten sam wzorzec może powodować problemy?
3. **Zaproponować szerszą analizę** — jeśli bug wskazuje na systemowy problem (np. rozproszony stan, brak source of truth), zgłoś to użytkownikowi PRZED pisaniem fixa.

**Dlaczego:** Doświadczenie z bugami stanu aplikacji (04-08) — 3 bugi zgłoszone przez testera okazały się symptomem 6 powiązanych problemów. Wąski fix naprawiłby 3, zostawiając 3 ukryte.

**Jak informować użytkownika:**
> "Ten bug wygląda na objaw szerszego problemu z [X]. Chcesz żebym najpierw zrobił audyt [X] zanim napiszę fix?"

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

## 6. Wymagania wydajnościowe

**Priorytet: szybkość działania + minimalne zużycie zasobów telefonu.**

- **Czas reakcji:** od pojawienia się popupu do wyświetlenia belki — jak najkrócej (cel <1s)
- **Zużycie baterii:** minimalne. Nie robić nic gdy nie ma aktywnych zleceń. Zero pollingu.
- **RAM:** minimalne alokacje w hot path (pipeline screenshot → OCR → overlay)
- **CPU:** unikać zbędnych operacji — nie przetwarzaj gdy nie musisz
- **Przy każdym TASK:** rozważ wpływ na wydajność. Jeśli istnieje prostsza/szybsza alternatywa — wybierz ją.
- **Nie optymalizuj przedwcześnie** — ale też nie pisz kodu który z natury jest wolny gdy szybsza wersja jest równie prosta.

---

## 7. Testowanie i budowanie APK

**Workflow testów (ustalony 2026-03):**

1. **AI pisze kod** w VSCode via Claude Code — edycja plików, commity, push
2. **Krzysztof buduje APK** w Android Studio — Build → Run na telefonie lub debug APK
3. **APK instalowane na telefonie taty** — via USB / adb / bezpośrednio
4. **Tata testuje na prawdziwych zleceniach** — Uber, Wolt, Glovo, Bolt Food
5. **Raportowanie wyników** — tata opisuje co działa / nie działa, ewentualnie zapisuje logi

**Logi debugowe:**
- Aplikacja ma ring buffer (500 wpisów) w pamięci
- Przycisk "Zapisz logi" na ekranie głównym → plik w Downloads
- AI analizuje logi po otrzymaniu od użytkownika

**Dla AI:**
- Nigdy nie buduj ani nie uruchamiaj aplikacji — daj instrukcje do Android Studio
- Po fixie podaj co przetestować i jak zweryfikować poprawkę
- Jeśli potrzebne logi — poproś użytkownika o kliknięcie "Zapisz logi" i przesłanie pliku

---

## 8. Środowisko pracy — VSCode vs Android Studio

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

**Folder:** `/Users/krzysztof/Desktop/OrderPilot/`. Pełna mapa plików pamięci → `CLAUDE.md` („Model plików pamięci"), nie powielamy jej tu.

---

## 9. Gdzie co tworzyć — ZASADY STRUKTURY REPO

**Obowiązuje wszystkich (AI + ludzie). Przed dodaniem pliku sprawdź tę sekcję.**

### Repo root (`/OrderPilot-App/`)
Tylko pliki nawigacyjne — nic więcej:
- `README.md` — wstęp, jak zacząć
- `RULES.md` — zasady (ten plik)
- `PROGRESS.md` — status kroków

### `docs/`
Cała dokumentacja projektu:
- `PLAN.md` — plan implementacji produkcyjnej (14 epiców, 40 tasków)
- `ARCHITECTURE.md` — architektura modułowa
- `PRODUCT_SPEC.md` — specyfikacja produktu
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
app/src/main/java/com/orderpilot/app/
├── di/          ← ServiceLocator, OrderPilotApp, AppLog
├── domain/      ← Offer, Platform, AnalysisResult, ProfitLevel, MetricType, AppLanguage, ThemeMode
├── engine/      ← OfferAnalyzer, OfferFilter
├── parser/      ← OcrOfferParser, OfferParser, UberOcrParser, UberParser, WoltOcrParser, GlovoOcrParser, BoltFoodOcrParser, ParserRegistry
├── capture/     ← ScreenCaptureService, PopupCropper
├── ocr/         ← OcrEngine
├── pipeline/    ← PipelineOrchestrator
├── service/     ← OrderPilotAccessibilityService, EventThrottler, AccessibilityTextCollector
├── overlay/     ← OverlayManager, SystemOverlayManager, OverlayViewFactory, OverlayAutoHider
├── settings/    ← AppSettings, SettingsRepository, SharedPrefsSettingsRepository
├── billing/     ← FeatureGate
└── ui/          ← MainActivity, SettingsActivity, SetupActivity, LocaleHelper

app/src/test/java/com/orderpilot/app/
├── engine/      ← OfferAnalyzerTest, OfferFilterTest
├── parser/      ← UberOcrParserTest, WoltOcrParserTest, GlovoOcrParserTest
├── settings/    ← AppSettingsTest
└── service/     ← EventThrottlerTest

app/src/main/res/
├── xml/         ← accessibility_config.xml
├── layout/      ← activity_main, activity_settings, activity_setup, overlay_offer
└── values/      ← kolory, stringi, style, themes
```

**Zasada:** każdy nowy plik Kotlin trafia do odpowiedniej warstwy. Nie tworzyć plików bezpośrednio w `com.orderpilot.app/` — zawsze w podfolderze warstwy.

---

## 10. Release / keystore

**Sygnowanie release AAB/APK.** Dane techniczne (przeniesione z `PROGRESS.md` 2026-05-31):

- **Keystore:** `OrderPilot/keystore/orderpilot-release.jks` — **gitignored**, NIGDY nie commituj do repo.
- **Alias:** `orderpilot`
- **Klucz:** RSA 2048, `SHA384withRSA`, ważny do **2053-09-05**.
- **SHA-256:** `AC:2D:E9:20:42:F0:59:BA:10:84:E0:63:2E:C8:EF:21:9F:E7:54:7C:69:A1:CC:3B:16:57:50:55:C8:13:0D:96`
- **Backup:** Desktop Mac + iPhone (Files).
- **Konfiguracja:** `signingConfigs` w `build.gradle.kts`, hasła w `keystore.properties` (gitignored; szablon `keystore.properties.template`).

> ⚠️ Utrata keystore = brak możliwości aktualizacji apki w Google Play (wymuszony nowy package). Pilnuj backupu.

---

## Workflow po restarcie Claude Code

Sekwencja startowa jest w `CLAUDE.md` („SEKWENCJA STARTOWA") — nie powielamy jej tu. Skrót: wczytaj `todo.md` + ogon `PROGRESS.md`, sprawdź higienę, zweryfikuj branch, poinformuj o stanie i (jeśli trzeba) rekomendowanym modelu (§2), zacznij pracę.

---

**Ostatnia aktualizacja:** 2026-05-31
