# OrderPilot — Stan bieżący i otwarte zadania

> **To jest JEDYNE źródło prawdy o stanie „teraz".** Pytanie „gdzie jesteśmy / co robimy?" → czytaj ten plik.
> `CLAUDE.md` nie trzyma statusu, `PROGRESS.md` to historia. Zamknięte zadanie → przenieś jeden ślad do
> `PROGRESS.md` i **usuń stąd** (todo nie jest cmentarzem). Długa lista pomysłów/drobnych bugów → `docs/future_polish_fixes.md`.

## Stan teraz
**🧊 PROJEKT ZAMROŻONY PRODUKTOWO (decyzja Krzysztofa 2026-08-31 — patrz `DECISIONS.md` D9).**
Nie promujemy, nie rozwijamy, nie kasujemy. Apka zostaje w sklepie jako wizytówka sprzedażowa Impulseo.

**🚀 v1.1.0 (versionCode 7) LIVE w Google Play** od 2026-08-31, 100% użytkowników, 177 krajów.
Zawiera: wizard v2.1, redesign całej apki, sprint niezawodności #1+#2, In-App Review, „Poleć kumplowi",
`targetSdk 36` (wymóg Google od 31.08.2026). Wszystko zmergowane do `main`.

**Powód zamrożenia — twarde dane z 31.08.2026:** 13 instalacji, ale **~1 aktywny użytkownik dziennie**,
a po 21.08 zero otwarć. To rodzina i płatni testerzy, nie kurierzy. Brak konta sprzedawcy = brak monetyzacji.

**JEDYNE, co robimy dalej (raz w roku, ~godzina):** Google co roku ok. 31 sierpnia wymaga podbicia
`targetSdk` na poprzednią wersję Androida, inaczej nie da się wypuścić aktualizacji. Przepis:
podbij `compileSdk`/`targetSdk` w `OrderPilot/app/build.gradle.kts` → `./gradlew :app:testDebugUnitTest`
→ `./gradlew :app:bundleRelease` → `.venv-play/bin/python tools/wydaj.py --aab <plik> --wydaj`
(`JAVA_HOME` = JBR z Android Studio; szczegóły i pułapki: pamięć `reference_play_api_wydawanie`).

**⛔ Czego NIE robimy:** promocji w grupach kurierskich, podmiany listingu, screenshotów v1.1, ASO,
backlogu z audytu 2026-06-28 (M14/M17, `docs/future_polish_fixes.md`), testów na fizycznym telefonie.
Wszystko poniżej zostaje jako ARCHIWALNY kontekst — nie jest listą zadań.

---

> 📈 **PLAN ROZWOJU / GROWTH (deep research 2026-06-28):** pełny plan „jak sprawić, by kurierzy pobierali i używali apki" →
> **`docs/promo/PLAN_ROZWOJU_DEEP_RESEARCH_2026-06-28.md`** (diagnoza + ASO gotowiec + listy grup/twórców + roadmapa + plan 4 tyg).
> TL;DR: problem = dystrybucja, nie produkt. Najpierw niezawodność odczytu, potem promocja w społecznościach kurierów. Zostać przy PL (nie Hiszpania). Pamięć: `project_orderpilot_growth_plan`. ⛔ **NIEAKTUALNE od 2026-08-31 (D9):** to już NIE jest „następny krok" — plan leży na półce, promocji nie robimy.

> 🔍 **AUDYT KODU 2026-06-28:** wieloagentowy audyt całego kodu → **69 znalezisk** (3 high, 17 medium, 39 low, 10 nit; brak „critical"). Pełna lista: **`docs/AUDYT-2026-06-28.md`**.
> **Sprint #1 ZROBIONY w kodzie (branch `fix/audit-2026-06-28-batch1`)** — czeka na build+test Krzysztofa + merge. Naprawione: H1 crash pipeline, M2/M3 persystencja języka (#28), H3 czerwone testy parserów, M1 diagnostyka logów, M5 czas belki, L20 wyciek bitmapy, L21/L31/L32/L33 drobne.

> 🧙 **WIZARD v2 + SPRINT #2 (2026-07-04, branch `feat/wizard-v2`, zbudowany NA sprincie #1):**
> **(1) Wizard konfiguracji v2** — krokowy (1 krok = 1 ekran), podgląd PRAWDZIWEJ belki na powitaniu, auto-wykrycie nadania uprawnienia → ✓ → auto-przejście, demo belki na żywo po nadaniu overlay, deep-link do dostępności z podświetleniem usługi, POST_NOTIFICATIONS jako wymagany krok (M12), ekran OEM nieblokujący (Samsung/Xiaomi/Huawei/OPPO/**Vivo nowość M13**/OnePlus + karta generyczna), OPPO autostart naprawiony (M16), hint językowy OCR dla UI UA/RU (H2 Droga 0). Logika kroków: `ui/setup/SetupFlow.kt` + testy. Tłumaczenia PL/EN/UK/RU.
> **(2) Sprint niezawodności #2** — M10 (timestamp startu persystowany; watchdog bez fałszywych alertów po reboocie), M11 (FGS start z tła w try/catch + powiadomienie zamiast crasha), M15 (race capture na API<30), M4 (Xiaomi crop 0.30 na głównej ścieżce takeScreenshot), M6 (onDestroy chowa belki), L1/#29 (insety MainActivity).
> Build + testy jednostkowe (148) zielone lokalnie. **Merge-plan: `main` ← `fix/audit-2026-06-28-batch1` ← `feat/wizard-v2` (jedna linia, jeden test na telefonie).**

## Otwarte zadania

| Priorytet | Zadanie | Status |
|-----------|---------|--------|
| 🔴 **High** | **WYDANIE v1.1.0** — gałąź `feat/wizard-v2` = komplet: sprint #1 + wizard v2.1 + sprint #2 + redesign całej apki + fixy M8/M9 + In-App Review + „Poleć kumplowi" + bump vc7. Ścieżka: test na telefonie (świeża instalacja → wizard; Start → belka; „Poleć kumplowi") → merge → podpisany AAB → Play Console (notki+screenshoty GOTOWE: `docs/play-store/RELEASE-NOTES-1.1.0.md` + `screenshots-v1.1/`). 159 testów + emulator zielone (2026-07-04) | Kod+materiały GOTOWE, wydanie TODO (Krzysztof) |
| ⏸️ **PARK** | **Audyt H2** — OCR cyrylicy UA/RU. ✅ Analiza 28.06: ML Kit NIE MA modelu cyrylicy; psuje się głównie jednostka czasu (хв/мин) + bramka. 3 drogi: 0=podpowiedź setup (tania), A=łatki OCR na ML Kit (wymaga REALNYCH logów z urządzenia UA — bez nich nie ruszać), B=Tesseract on-device (armata). **DECYZJA 28.06: na razie NIE robimy nic** (Krzysztof). Odblok Drogi A: log OCR od Andrija (przycisk „Zapisz logi" działa po fixie M1). Pełne: `docs/AUDYT-2026-06-28.md` H2 | ⏸️ PARK (decyzja: nie ruszać) |
| 🟢 ~~Low~~ | ~~**Audyt M7** — Bolt fałszywy GREEN z gotówki~~ → **ZWERYFIKOWANE 28.06 (research): bezprzedmiotowy dla PL** — Bolt Food w Polsce wycofał dostawy gotówkowe (kurierpedia.pl), scenariusz nie zachodzi; dla UA moot (OCR ślepy na cyrylicę = H2). NIE ruszać parsera. Wracać tylko gdyby Bolt PL wrócił do gotówki | ZAMKNIĘTE (moot) |
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
