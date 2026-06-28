# OrderPilot — Stan bieżący i otwarte zadania

> **To jest JEDYNE źródło prawdy o stanie „teraz".** Pytanie „gdzie jesteśmy / co robimy?" → czytaj ten plik.
> `CLAUDE.md` nie trzyma statusu, `PROGRESS.md` to historia. Zamknięte zadanie → przenieś jeden ślad do
> `PROGRESS.md` i **usuń stąd** (todo nie jest cmentarzem). Długa lista pomysłów/drobnych bugów → `docs/future_polish_fixes.md`.

## Stan teraz
**🚀 v1.0.5 LIVE w Google Play** (177 krajów) od 2026-05-23. Etap: **post-launch ops + promocja + planowanie v1.1.**
Merge sequence `fix/v1.0.5-uber-popup-background` → … → `main` + tag `v1.0.5` + GitHub Release: zakończone 2026-05-23 (per PROGRESS).

> 📈 **PLAN ROZWOJU / GROWTH (deep research 2026-06-28):** pełny plan „jak sprawić, by kurierzy pobierali i używali apki" →
> **`docs/promo/PLAN_ROZWOJU_DEEP_RESEARCH_2026-06-28.md`** (diagnoza + ASO gotowiec + listy grup/twórców + roadmapa + plan 4 tyg).
> TL;DR: problem = dystrybucja, nie produkt. Najpierw niezawodność odczytu, potem promocja w społecznościach kurierów. Zostać przy PL (nie Hiszpania). Pamięć: `project_orderpilot_growth_plan`. **Gdy Krzysztof pyta „następny krok w OrderPilot" — zacznij od tego pliku.**

> 🔍 **AUDYT KODU 2026-06-28:** wieloagentowy audyt całego kodu → **69 znalezisk** (3 high, 17 medium, 39 low, 10 nit; brak „critical"). Pełna lista: **`docs/AUDYT-2026-06-28.md`**.
> **Sprint #1 ZROBIONY w kodzie (branch `fix/audit-2026-06-28-batch1`)** — czeka na build+test Krzysztofa + merge. Naprawione: H1 crash pipeline, M2/M3 persystencja języka (#28), H3 czerwone testy parserów, M1 diagnostyka logów, M5 czas belki, L20 wyciek bitmapy, L21/L31/L32/L33 drobne.

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| 🔴 **High** | **Audyt: sprint #1** — zbudować + przetestować branch `fix/audit-2026-06-28-batch1` (`./gradlew testDebugUnitTest` — testy parserów znów zielone), potem merge do `main`. Szczegóły: `docs/AUDYT-2026-06-28.md` | Kod GOTOWY, build+test+merge TODO (Krzysztof) |
| 🔴 **High** | **Audyt H2** — Latin-only OCR vs cyrylica UA/RU (segment UA ślepy na ścieżce OCR). Decyzja biznesowa (ile inwestujemy w rynek UA) + implementacja. `docs/AUDYT-2026-06-28.md` H2 | Decyzja + build TODO |
| 🟡 **High** | **Audyt M7** — Bolt może pokazać fałszywy GREEN (nie filtruje kwoty gotówki). Potrzebny realny zrzut oferty Bolta z gotówką → potem fix jak w Glovo | Weryfikacja + fix TODO |
| 🔴 **High** | **Play Store SEO fix** — apka niewidoczna na frazę „order pilot" (ze spacją). Edycja: App name + Short description + pierwsze 250 znaków Full description, żeby „Order Pilot" było explicit. **BLOCKING przed outreachem do grup.** Szczegóły: `docs/promo/LAUNCH_PLAN.md` Etap 1.5 Issue #1 | TODO (30–60 min + 24–48h review) |
| 🟡 **High** | **Grafiki FB** — nowa cover 1640×924 + profile pic + 3–4 screeny do postów (obecna cover słaba, brak showcase belki). Szczegóły: `LAUNCH_PLAN.md` Etap 1.5 Issue #2 | Materiały TODO (1–2h DIY / 24–48h outsourcing) |
| 🟡 **High** | **Promocja launch — execution** — plan + drafty gotowe (`docs/promo/`: LAUNCH_PLAN.md + 3 drafty FB + 12 grup kurierów PL/UA + 3 szablony outreach). User publikuje 1–2 grupy/dzień przez tydzień | Materiały gotowe 05-23, execution TODO (user) |
| 🟢 **High** | **Monitoring Console** — codziennie: Statistics (instalacje/kraj), Crashes & ANRs (baseline zero), Ratings & Reviews (pierwsza recenzja), Vitals | DAILY |
| **Medium** | **Plan v1.1** (Q3 2026) — EN store listing (zob. `DECISIONS.md` D6), decyzja telemetria (D3 / future #35), Android 16 watch-out (D1 / future #33), nowe języki/waluty | TODO |
| **Medium** | **Reply do real testerów** — podziękowanie + invite do Production: Marcin, Andrij, Dominik | TODO |
| **Medium** | **Sesja porządkowa memory** — licznik 10/10 + LIVE bump; archiwizacja closed-testing memory (`closed_testing_strategy.md`, fragmenty MEMORY.md) | TODO |

## Otwarte pytania / decyzje w toku
- **Telemetria po Production?** Wariant A (status quo, zero-network) / B (opt-in PostHog) / C (tylko crash reporting). Niezdecydowane — łamie obietnicę zero-network. Pełny kontekst: `docs/future_polish_fixes.md` #35. Stan decyzji: `DECISIONS.md` D3. → gdy zapadnie, zaktualizuj D3 i usuń stąd.

## Monitorowane (real-world, niski priorytet)
- **Uber persistent overlay Xiaomi** — pusty overlay type=3 trzyma się na Xiaomi (na Samsungu OK). Polishing, obserwacja.
- **Crash starszy telefon (brat)** — SettingsActivity, nieodtworzony po reinstalacji (03-25). Obserwacja.

## Aktywne branche
> Po merge 05-23 (wg PROGRESS) bazą jest `main` z v1.0.5. Lokalny stan branchy zweryfikować gitem przy okazji (środowisko: Desktop+iCloud bywa wolne dla gita).

| Branch | Rola |
|--------|------|
| `main` | stabilna baza, v1.0.5 |
| `fix/v1.0.5-uber-popup-background` | bieżący working (zmergowany do main 05-23) |

**Zachowane nie-merged (eksperymenty):** `feature/fake-uber-driver`, `feature/glovo-parser`, `fix/parser-false-positives-bolt-watch`, `claude/hardcore-darwin`.
