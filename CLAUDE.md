# OrderPilot — Instrukcje dla Claude Code

> **Czym jest ten plik:** boot + zasady. Czytany na starcie KAŻDEJ sesji. Trzyma *jak pracować*, nie *co się dzieje*.
> Stan bieżący → `todo.md`. Historia → `PROGRESS.md`. Decyzje → `DECISIONS.md`. **Ten plik NIE trzyma statusu.**

## 🚨 AKTYWNY BRANCH (source of truth)

**Obecny branch roboczy: `fix/audit-2026-06-28-batch1`**
**Ostatnia aktualizacja tego wpisu: 2026-06-28**

> ℹ️ Te dwie linijki są **auto-aktualizowane** przez hook `.git/hooks/post-checkout` przy każdym `git checkout <branch>`. Nie edytuj ich ręcznie. Jeśli hook nie działa (np. świeży klon), uruchom `./scripts/install-hooks.sh` (źródło: `scripts/git-hooks/post-checkout`, bo `.git/hooks/` nie jest wersjonowany).

Jeśli aktywny branch tu wymieniony **nie zgadza się** z tym na czym jesteś → **STOP, nie pracuj, zapytaj**. Nie „naprawiaj" przez switch — może być powód (np. worktree `claude/*` z auto-halucynacji systemu).

## 🚨 SEKWENCJA STARTOWA (OBOWIĄZKOWA)

Przed JAKĄKOLWIEK pracą nad kodem, po kolei:

1. **Gdzie jestem fizycznie:** `pwd && git worktree list` — główne repo czy worktree pod `.claude/worktrees/`?
2. **Branch + ostatni commit:** `git branch --show-current && git log -1 --oneline`. Porównaj z „AKTYWNY BRANCH" wyżej. Nie zgadza się → **STOP, zapytaj.**
   > Środowisko: Desktop+iCloud bywa wolny dla gita — komendy potrafią wisieć. Jeśli wiszą, nazwę brancha masz w bloku „AKTYWNY BRANCH" wyżej; nie blokuj się.
3. **Wczytaj pamięć projektu** (w tej kolejności):
   - `todo.md` — **jedyne** źródło stanu „teraz" + otwarte zadania. **Zawsze.**
   - ogon `PROGRESS.md` (góra pliku = najnowsze) — co się działo ostatnio. **Zawsze.**
   - `DECISIONS.md` — **tylko gdy** temat dotyka architektury / „dlaczego tak?" / „czy możemy zmienić X?".
   - `PROGRESS_ARCHIVE.md` — **rzadko**, tylko gdy trzeba odtworzyć „jak tu doszliśmy".
4. **Kontrola higieny** (deterministycznie, jedną komendą):
   ```
   wc -l CLAUDE.md todo.md PROGRESS.md
   ```
   Jeśli próg przekroczony — posprzątaj ZANIM dołożysz treść:
   - `PROGRESS.md` > **150** → najstarsze wpisy do `PROGRESS_ARCHIVE.md`.
   - `todo.md` > **80** → zamknięte zadania usuń (ślad zostaje w `PROGRESS.md`); todo to nie cmentarz.
   - `CLAUDE.md` > **120** → szczegóły do `RULES.md` / `docs/`; tu zostają tylko boot + zasady.
5. **Zweryfikuj pliki z zadania.** Zanim cokolwiek zrobisz (zwł. zanim delegujesz subagentowi) — `Glob` na plikach z polecenia. Nie ma ich na Twoim branchu → jesteś w złym miejscu → **STOP, zapytaj.**

## Model plików pamięci (kto za co odpowiada)

| Plik | Rola | Reguła |
|------|------|--------|
| `CLAUDE.md` | boot + zasady (ten plik) | jak pracować; **bez statusu** |
| `todo.md` | stan „teraz" + otwarte zadania | **jedyne** źródło prawdy o „teraz" |
| `PROGRESS.md` | log sesji (newest-first, insert-only) | *dlaczego / co z tego wynika*; nie to co `git log` |
| `DECISIONS.md` | trwałe decyzje (ADR-lite) | status **edytowalny**; nie archiwizowany po wieku |
| `PROGRESS_ARCHIVE.md` | zamrożone stare wpisy (oldest-first) | czytany rzadko |
| `docs/future_polish_fixes.md` | **jedyny** backlog drobnych bugów/pomysłów (#N) | `todo.md` LINKUJE do #N, nie kopiuje |
| `RULES.md` | szczegółowe zasady (git, modele, struktura repo, testy) | rozwinięcie tego pliku |

Dokumentacja produktu: `docs/PLAN.md` (14 epiców), `docs/ARCHITECTURE.md`, `docs/PRODUCT_SPEC.md`.

**Nie dubluj.** Jedna informacja = jedno miejsce. Stan → tylko `todo.md`. Drobny backlog → tylko `future_polish_fixes.md`. Zamknięte zadanie: jeden ślad do `PROGRESS.md`, usuń z `todo.md`.

## 🚨 ANTI-HALUCYNACJA PRZY SUBAGENTACH

Subagenty (zwł. Explore) potrafią **zhalucynować cały raport** — wymyślić pliki, klasy, numery linii. To się już stało w tym projekcie i spaliło godzinę pracy użytkownika.

1. **Przed delegowaniem** zweryfikuj sam (Glob/Grep), że pliki z promptu **istnieją**.
2. **Nie ufaj raportom bez weryfikacji.** Numery linii (`X.kt:42`) — spot-check 2-3 cytowania przez Read. Pierwsze nie pasuje → **odrzuć cały raport**, czytaj kod sam.
3. **Małe zadania (< 5 plików) — bez subagenta.** Read + Grep są szybsze i nie halucynują.

## Ogólne zasady pracy

- Kod: VSCode + Claude Code. **Build i run tylko Krzysztof w Android Studio / adb.** Nigdy nie uruchamiaj aplikacji sam.
- Kotlin, UI XML + ViewBinding (bez Compose). Package `com.orderpilot.app`, module root `OrderPilot/`.
- Nowe zadanie = nowy branch `fix/...` lub `feature/...` (zob. `RULES.md` §4). `main` chroniony — kod tylko przez branch (`DECISIONS.md` D7).
- `PROGRESS.md` aktualizuj po ukończonym zadaniu, przed commitem (nowy wpis na GÓRZE, nie edytuj starych).
- Avoid over-engineering. Proste rozwiązania. Nie refaktoruj tego o co nikt nie prosił.
- Szczegóły (podział Opus/Sonnet, git workflow, struktura repo, testowanie) → `RULES.md`.

## Auto-memory (osobna warstwa)

Trwałe fakty o użytkowniku + 1 linia/projekt żyją w auto-memory, nie tutaj:
`/Users/krzysztof/.claude/projects/-Users-krzysztof-Desktop-OrderPilot/memory/MEMORY.md`
