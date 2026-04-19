# OrderPilot — Instrukcje dla Claude Code

## 🚨 AKTYWNY BRANCH (source of truth)

**Obecny branch roboczy: `polishing`**
**Ostatnia aktualizacja tego wpisu: 2026-04-19**

> ℹ️ Te dwie linijki są **auto-aktualizowane** przez hook `.git/hooks/post-checkout` przy każdym `git checkout <branch>`. Nie edytuj ich ręcznie — po prostu przełącz branch i wpis się zmieni. Jeśli hook nie działa (np. po świeżym klonie repo), uruchom `./scripts/install-hooks.sh` żeby go zainstalować — źródło hooka jest w `scripts/git-hooks/post-checkout`, bo katalog `.git/hooks/` nie jest wersjonowany przez git.

Jeśli zaczynasz sesję i aktywny branch tu wymieniony **nie zgadza się** z tym na czym jesteś → **STOP, nie pracuj, zapytaj użytkownika**. Nie próbuj „naprawiać" przez switch — może być powód dla którego jesteś gdzie indziej (np. worktree `claude/*` z auto-halucynacji systemu).

## 🚨 PROTOKÓŁ STARTOWY SESJI (OBOWIĄZKOWY)

**Przed JAKĄKOLWIEK pracą nad kodem w nowej sesji**, wykonaj TE kroki po kolei:

1. **Sprawdź gdzie jesteś fizycznie:**
   ```
   Bash: pwd && git worktree list
   ```
   Zobacz czy pracujesz w głównym repo czy w którymś worktree pod `.claude/worktrees/`.

2. **Sprawdź branch i ostatni commit:**
   ```
   Bash: git branch --show-current && git log -1 --oneline
   ```

3. **Porównaj z „AKTYWNY BRANCH" powyżej.**
   - ✅ Jeśli się zgadza → możesz pracować.
   - ❌ Jeśli się nie zgadza → **STOP. Powiedz użytkownikowi:**
     > „Jestem na branchu `X` ale CLAUDE.md mówi że aktywny to `Y`. Nie wiem czy powinienem przełączyć czy coś jest nie tak z konfiguracją. Co robimy?"

4. **Weryfikuj istnienie kluczowych plików z zadania.**
   Jeśli użytkownik mówi „zaudytuj refaktor X", zanim zaczniesz cokolwiek (a szczególnie zanim delegujesz do subagenta), zrób `Glob` na plikach o których mowa. Jeśli ich NIE MA na Twoim branchu — to znak że jesteś w złym miejscu. **STOP i zapytaj.**

## 🚨 ANTI-HALUCYNACJA PRZY SUBAGENTACH

Subagenty (Agent tool, szczególnie Explore) potrafią **zhalucynować cały raport** — wymyślić nazwy plików, klas, numery linii. To się już stało w tym projekcie i spaliło godzinę pracy użytkownika.

**Zasady pracy z subagentami w tym projekcie:**

1. **Przed delegowaniem audytu/eksploracji** — zweryfikuj samodzielnie (Glob/Grep) że pliki o których piszesz w prompcie **istnieją**. Jeśli prosisz subagenta „przeanalizuj MonitoringController" — najpierw sam sprawdź że `MonitoringController.kt` istnieje.

2. **Nie ufaj raportom subagentów bez weryfikacji.** Gdy subagent zwraca konkretne numery linii (`X.kt:42`) — spot-check przynajmniej 2-3 cytowania przez Read. Jeśli pierwsze cytowanie nie pasuje do rzeczywistości, **odrzuć cały raport** i zacznij od zera czytając kod sam.

3. **Dla małych zadań (< 5 plików) nie używaj subagenta w ogóle.** Read + Grep są szybsze i nie halucynują.

## Ogólne zasady pracy (z RULES.md / CLAUDE.md użytkownika)

- Kod piszemy w VSCode + Claude Code. **Build i run tylko Krzysztof w Android Studio / adb.** Nigdy nie uruchamiaj aplikacji sam.
- Język projektu: Kotlin, UI XML + ViewBinding (bez Compose).
- Package: `com.orderpilot.app`. Module root: `OrderPilot/`.
- Nowe zadanie = nowy branch `fix/...` lub `feature/...` → merge do aktywnego branchu roboczego (patrz wyżej).
- PROGRESS.md aktualizować po każdym ukończonym zadaniu, przed commitem.
- Avoid over-engineering. Proste rozwiązania. Nie refaktoruj tego o co nikt nie prosił.

## Kluczowe pliki projektu

- `docs/PLAN.md` — plan implementacji v2 (14 epiców)
- `docs/ARCHITECTURE.md` — architektura modułowa
- `docs/PRODUCT_SPEC.md` — specyfikacja produktu
- `PROGRESS.md` — status aktualnych zadań
- `RULES.md` — zasady współpracy
- `docs/future_polish_fixes.md` — **jedyne** źródło prawdy dla drobnych bugów, usprawnień i pomysłów na przyszłość. Przy pytaniach „co robimy?" / „co można poprawić?" → odwołaj się tu. NIE twórz duplikatów tego pliku.

Więcej kontekstu (historia fixów, feedback, odkrycia) znajdziesz w auto-memory:
`/Users/krzysztof/.claude/projects/-Users-krzysztof-Desktop-OrderPilot/memory/MEMORY.md`
