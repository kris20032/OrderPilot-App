# Zasady pracy nad CourierAssist

## 1. Jak wracać do projektu po restarcie

**Trigger phrases** - jak mnie przypomnieć o projekcie:
- "wróćmy do pracy nad aplikacją dla kurierów"
- "projekt CourierAssist"
- "aplikacja dla kurierów"
- "Glovo/UberEats/Wolt app"

**Co robię po usłyszeniu trigger phrase:**
1. Czytam `PROGRESS.md` - sprawdzam obecny etap
2. Czytam `PLAN.md` - sprawdzam co jest do zrobienia
3. Informuję Cię: "Jesteśmy na Kroku X: [nazwa]. Ostatnia aktualizacja: [data]. Kontynuujemy?"

---

## 2. Podział pracy: Opus 4.6 vs Sonnet 4.5

### Przed każdym krokiem:
**Informuję Cię:** "Teraz rekomenduje zmianę na [Opus/Sonnet], daj znać jak to zrobisz i będziemy kontynuować"

### Kiedy używać którego modelu:

**🎯 Opus 4.6** - skomplikowane zadania:
- Krok 2: MediaProjection (VirtualDisplay, ImageReader, edge cases)
- Krok 3: OCR (ML Kit integration, preprocessing, optymalizacja)
- Krok 5: Parsery (regex engineering na prawdziwych danych OCR)
- Krok 7: Integracja end-to-end (łączenie komponentów, debugowanie)
- Debugowanie trudnych problemów
- Decyzje architektoniczne

**⚡ Sonnet 4.5** - implementacja według spec:
- Krok 0: Instalacja środowiska (powtarzalne kroki)
- Krok 1: Szkielet projektu (boilerplate, uprawnienia)
- Krok 4: Overlay layout (XML + prosty serwis)
- Krok 6: Kalkulator opłacalności (matematyka)
- Krok 8: Build scripts, dystrybucja APK
- UI screens (Settings, History)
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

## 4. Git workflow - ZASADY DLA WSZYSTKICH ⚠️

**🚨 PRZECZYTAJ TO PRZED PRACĄ - DOTYCZY WSZYSTKICH 🚨**

### Zespół:
- **Krzysztof** - główna implementacja (może używać Claude/innego AI)
- **Tata** - testowanie na prawdziwych zleceniach (może używać AI lub nie)
- **Łukasz (przyjaciel)** - wsparcie (może używać Copilot/Antigravity/inne AI lub nie)

**Podział zadań:** Sprawdź `PROGRESS.md` sekcja "🔄 W trakcie" - tam jest aktualny podział.

---

### 🤖 JEŚLI PRACUJESZ Z AI (Claude / Copilot / Antigravity / inne)

**AI ASYSTENT - PRZECZYTAJ I ZASTOSUJ:**

#### 🔴 NA POCZĄTKU KAŻDEJ SESJI (PRZED JAKĄKOLWIEK PRACĄ):

**MUSISZ:**
1. Uruchomić `git pull` - pobrać najnowszą wersję z GitHub
2. Poczekać na wynik i sprawdzić czy są konflikty
3. Jeśli są konflikty → rozwiązać NAJPIERW, potem pracować
4. Przeczytać `PROGRESS.md` - sprawdzić co inni robią
5. Dopiero potem rozpocząć pracę

**Poinformuj użytkownika:**
> "⚠️ UWAGA: Pobieram najnowszą wersję z GitHub (`git pull`)..."
>
> [wynik git pull]
>
> "✅ Kod zaktualizowany. Sprawdzam PROGRESS.md - kto co robi..."

#### 🟢 PODCZAS PRACY:

- Commituj po każdym logicznym kroku (nie czekaj do końca!)
- Format: `git commit -m "Krok X: Opis zmiany"`
- Przykład: `git commit -m "Krok 3: Dodano OcrEngine + preprocessing"`

#### 🔴 NA KOŃCU KAŻDEJ SESJI (PO ZAKOŃCZENIU PRACY):

**MUSISZ:**
1. `git add .` - dodać wszystkie zmiany
2. `git commit -m "Krok X ukończony: [opis]"` - commit końcowy
3. `git push` - wypchnąć na GitHub
4. Zaktualizować `PROGRESS.md` - oznacz co zrobione
5. Commit + push `PROGRESS.md`

**Poinformuj użytkownika:**
> "✅ Krok X ukończony. Wysyłam zmiany na GitHub..."
>
> `git add .`
> `git commit -m "..."`
> `git push`
>
> "✅ Kod wysłany. Reszta zespołu może pobrać (`git pull`)."

#### 🚨 PRZYPOMINAJ UŻYTKOWNIKOWI:

**AI powinien przypominać:**
- "⚠️ Zanim zacznę pracę, wykonuję `git pull`"
- "⚠️ Po zakończeniu kroku, robię `git push`"
- **Nawet jeśli użytkownik o tym nie wspomni!**

---

### 👥 JEŚLI PRACUJESZ BEZ AI (ręcznie)

**PRZED rozpoczęciem pracy:**
```bash
cd ~/Desktop/CourierAssist  # lub gdzie masz projekt
git pull
```

**Przeczytaj:**
- `PROGRESS.md` - sprawdź co inni robią (unikniesz konfliktów)

**PODCZAS pracy:**
- Commituj często: `git commit -am "Opis zmiany"`

**PO zakończeniu pracy:**
```bash
git add .
git commit -m "Krok X: Opis co zrobiłeś"
git push
```

**Zaktualizuj PROGRESS.md:**
- Oznacz co zrobiłeś jako ukończone
- Zapisz i wypchnij: `git add PROGRESS.md && git commit -m "Update progress" && git push`

---

### 🔧 Dla wszystkich (AI + ludzie):

### Zasady commitów:

**Kiedy commitować:**
- Po zakończeniu każdego kroku z PLAN.md
- Po naprawieniu buga
- Po dodaniu nowej funkcjonalności
- Przed końcem sesji (ZAWSZE!)

**Format commit message:**
```
Krok X: Krótki opis (max 50 znaków)

Opcjonalnie dłuższy opis:
- Co zostało dodane
- Co zostało zmienione
- Jakie problemy rozwiązano
```

**Przykłady:**
```bash
git commit -m "Krok 0: Zainstalowano Android Studio + JDK 17"
git commit -m "Krok 2: Dodano ScreenCaptureManager z detekcją zmian"
git commit -m "Krok 5: GlovoParser - regex dla kwoty i dystansu"
git commit -m "Fix: Poprawiono overlay self-capture issue"
```

### Podział pracy (kto co robi):

**⚠️ Status:** TBD - do ustalenia przez zespół

**Aktualny podział zadań:** Sprawdź `PROGRESS.md` sekcja "🔄 W trakcie"

**Przykładowy podział (może się zmienić):**
- **Krzysztof:** Główna implementacja (capture, OCR, parsery, overlay)
- **Tata:** Testowanie na prawdziwych zleceniach + feedback
- **Łukasz:** UI, settings, helper functions

**Zasada:** Przed rozpoczęciem pracy sprawdź `PROGRESS.md` - tam jest info kto czym się zajmuje teraz.

### Unikanie konfliktów:

**PROGRESS.md - każdy edytuje swoją sekcję:**
```markdown
## 🔄 W trakcie

**Krzysztof:** Krok 3 - OCR implementation
**Tata:** Testowanie Kroku 2
**Łukasz:** Wolny
```

**Różne pliki = zero konfliktów:**
- Ustalcie kto nad którymi plikami pracuje (sprawdź `PROGRESS.md`)
- Przykład: jeden robi `capture/`, drugi `ui/`, trzeci testuje
- Jeśli pracujecie nad różnymi plikami = zero konfliktów!

### Jeśli wystąpi konflikt:

```bash
git pull --rebase
# Rozwiąż konflikty w plikach
git add .
git rebase --continue
git push
```

**AI Asystent:** Jeśli konflikt → zatrzymaj się i poinformuj użytkownika.

### GitHub repo info:

**Nazwa:** `CourierAssist-App`
**Typ:** Private
**Collaborators:** tata + przyjaciel (dodani w Settings)
**Branch:** `main`

---

**🚨 KLUCZOWA ZASADA DLA WSZYSTKICH AI:**

```
PRZED PRACĄ → `git pull`
PO PRACY → `git push`

ZAWSZE. BEZ WYJĄTKÓW. NAWET JEŚLI UŻYTKOWNIK NIE WSPOMNI.
```

**Dotyczy:** Claude, Copilot, Antigravity, Cursor, i wszystkich innych AI asystentów.

---

## 5. Komunikacja podczas pracy

**Zasada: Zwięźle, ale zrozumiale**

### Na początku kroku:
> "Krok X: [nazwa kroku]. Tworzę [co tworzę]. Model: [Opus/Sonnet]"

### Podczas implementacji:
> "Dodaję [plik/funkcjonalność] - [1 zdanie co robi]"

### Po skończeniu:
> "✅ Krok X gotowy. Test: [jak zweryfikować]. Następny krok: [co dalej]"

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
- `PLAN.md` - pełny plan 8 kroków
- `PROGRESS.md` - tracking statusu (ZAWSZE czytam po restarcie)
- `RULES.md` - ten plik (ZAWSZE czytam po restarcie)

---

## Workflow po restarcie Claude Code

1. User pisze trigger phrase (np. "wróćmy do aplikacji dla kurierów")
2. Czytam `PROGRESS.md` + `RULES.md`
3. Informuję: "Jesteśmy na Kroku X, ostatnia aktualizacja [data]. Kontynuujemy?"
4. User potwierdza
5. Sprawdzam jaki model jest potrzebny, informuję o zmianie jeśli trzeba
6. Zaczynam pracę

---

**Ostatnia aktualizacja:** 2026-02-24
