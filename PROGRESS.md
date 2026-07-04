# OrderPilot — Log sesji (PROGRESS)

> **Czym jest ten plik:** chronologiczny log „co się działo" — pamięć między sesjami. Stan bieżący i otwarte
> zadania są w `todo.md`; trwałe decyzje w `DECISIONS.md`; długa lista pomysłów w `docs/future_polish_fixes.md`.
>
> **Reguły (ważne — chronią plik przed gniciem):**
> - **Newest-first:** najnowszy wpis na GÓRZE (zaraz pod tym nagłówkiem). Nowa sesja czyta po prostu górę pliku.
> - **Insert-only:** dodajesz nowy blok, **nie edytujesz i nie przepływasz** starych wpisów (czysty diff w gicie).
> - **Ziarno = sesja/decyzja, nie commit.** Jeśli wpis dałoby się odtworzyć z `git log` — nie pisz go. Tu zapisujemy
>   *dlaczego* i *co z tego wynika*, nie listę zmian plików.
> - **Archiwizacja:** gdy plik przekroczy ~150 linii, najstarsze wpisy → `PROGRESS_ARCHIVE.md` (sprawdzane w sekwencji startowej).
>
> **Szablon wpisu:**
> ```
> ## RRRR-MM-DD — krótki tytuł
> - **Co:** …
> - **Decyzje:** …  (opcjonalnie; trwałe decyzje dubluj do DECISIONS.md)
> - **Co dalej:** …
> ```

---

## 2026-07-04 — 🧙 Wizard konfiguracji v2 + sprint niezawodności #2 (Fable 5)
- **Co:** Przebudowa pierwszego uruchomienia (decyzja K.: „wizard w najlepszej możliwej formie, raz a porządnie"). Branch **`feat/wizard-v2`** (na `fix/audit-2026-06-28-batch1`). Wizard: 1 krok = 1 ekran, na powitaniu PRAWDZIWA belka (kod produkcyjny `OverlayViewFactory`, rotacja zielona/żółta/czerwona) ZANIM poprosimy o cokolwiek; auto-wykrycie nadania → ✓ → auto-przejście; po nadaniu overlay belka na żywo (moment „wow"); deep-link dostępności z podświetleniem usługi; POST_NOTIFICATIONS wymagany (M12); ekran OEM nieblokujący z kartą Vivo (M13) i naprawionym OPPO (M16) + generyczna; hint „apka kurierska po PL/EN" (H2 Droga 0); PL/EN/UK/RU. Logika kroków w czystym `SetupFlow` + 16 testów. Do tego sprint #2: M10 (persystencja timestampu startu → koniec fałszywych alertów watchdoga po reboocie), M11 (FGS z tła: try/catch + powiadomienie zamiast crasha), M15 (race capture), M4 (Xiaomi crop 0.30 na ścieżce takeScreenshot), M6 (onDestroy chowa belki), L1 (insety MainActivity).
- **Decyzje:** powiadomienia = wymagane w bramce setupu (bez nich watchdog ślepy — istota M12); kroki OEM nieblokujące (niewervyfikowalne systemowo), zawsze dostępne z podsumowania; bateria zostaje wymagana (niezawodność = priorytet #1 z planu rozwoju).
- **Weryfikacja:** build lokalny + 148 testów jednostkowych zielone (JDK z Android Studio, bez udziału K.).
- **Co dalej:** Krzysztof — jeden test na telefonie (świeża instalacja, przejście wizarda) + merge łańcucha `fix/audit-2026-06-28-batch1` → `feat/wizard-v2` → `main`; potem bump wersji i release do Play.

## 2026-06-28 — 🔍 Wieloagentowy audyt kodu + sprint naprawczy #1
- **Co:** Pełny statyczny audyt całego kodu apki (27 agentów: 13 podsystemów × recenzja + niezależna weryfikacja każdego znaleziska + synteza; 2,6 mln tokenów). **69 realnych znalezisk** po deduplikacji (3 high, 17 medium, 39 low, 10 nit; **brak „critical"**). Pełny raport utrwalony w repo: **`docs/AUDYT-2026-06-28.md`** (durable backlog; `future_polish_fixes.md` + `todo.md` linkują tam).
- **Naprawione w branchu `fix/audit-2026-06-28-batch1`** (sprint #1, bez build/test — to po stronie Krzysztofa): **H1** crash pipeline (brak `CoroutineExceptionHandler` → nieobsłużony wyjątek z capture/OCR zabijał proces) + **L20** wyciek bitmapy screenshotu; **M2+M3+L39** persystencja ustawień/języka — wspólny `SettingsJson { encodeDefaults; ignoreUnknownKeys; coerceInputValues }` (domyka PERSYSTENCJĘ #28; część UI `setApplicationLocales` była już ok); **H3** czerwone testy parserów Uber/Wolt (bramka Layer-4 z `15c131d` bez aktualizacji fixtur → dodane markery popupu + jawne testy bramki); **M1** logi crashy/„Zapisz logi" z publicznego Downloads → katalog prywatny apki (były martwe na targetSdk 35); **M5** czas belki per platforma na ścieżce MediaProjection; **L21/L31/L32/L33** drobne (мин, contentDescription/kanały i18n, Stop chowa belkę natychmiast).
- **Kluczowe weryfikacje znanych:** **#28** był naprawiony tylko w POŁOWIE (UI tak, persystencja nie — domknięte teraz). **#29** insety naprawione w 3 Activity, POMINIĘTE w MainActivity (L1, otwarte). **#33** ryzyko `accessibilityDataSensitive` od API 34 (Android 14), nie 16. R8/minify wyłączone → hipoteza „R8 obcina serializację" wykluczona. Źródło „pustego overlay type=3" na Xiaomi: keepAlive 1x1 w `MonitoringForegroundService`.
- **Co dalej:** Krzysztof: `./gradlew testDebugUnitTest` (parsery znów zielone) + build + merge `fix/audit-2026-06-28-batch1` → `main`. Otwarte grube tematy: **H2** Latin-only OCR vs cyrylica UA/RU (decyzja biznesowa o rynku UA), **M7** Bolt fałszywy GREEN (do potwierdzenia zrzutem). Reszta backlogu w `docs/AUDYT-2026-06-28.md`.

## 2026-05-23 — 🚀 Production LIVE w Google Play
- **Co:** Krzysztof potwierdził, że OrderPilot v1.0.5 jest dostępny w Sklepie Play (177 krajów, dowolny Android). Production track approved między 2026-05-20 (po IARC notice) a 05-23. PrimeTestLab zakończył 20-day testing cycle 05-21 (14 standard + 6 bonus). Pełen flow POC → LIVE w ~3 miesiące (02-24 → 05-23).
- **Co dalej:** merge git → sesja porządkowa → promocja → monitoring → plan v1.1 (zob. `todo.md`).

## 2026-05-23 — Git cleanup + GitHub Release + plan promocji
- **Co:** pełna sekwencja merge `fix/v1.0.5-uber-popup-background` → `play-store-prep` → `feature/production-app` → `main` zakończona, tag `v1.0.5` (annotated) pushnięty. GitHub Release v1.0.5 z release notes PL+EN i journey table. README zaktualizowany (Google Play + Release badge + LIVE notice). Utworzono `docs/promo/` jako single source of truth promocji: 3 drafty postów FB + 12 grup kurierów PL/UA + 3 szablony outreach z anti-spam zasadami.
- **Co dalej:** user wykonuje outreach 1–2 grupy/dzień; najpierw Play Store SEO fix (BLOCKING — `todo.md`).

## 2026-05-20 — IARC Live Rating Notice
- **Co:** email od IARC (`noreply@globalratings.com`) — rating wiekowy LIVE na Google Play. Global Rating ID `6ef6cf91-410e-8191-8de0-3f365b7a6a7e` (zachowany do przyszłych storefrontów: Amazon, Galaxy Store). NIE oznacza że apka LIVE — to tylko publiczny rating; sygnał, że pipeline review Google się rusza.

## 2026-05-16 — Application for Production SUBMITTED (Day 14)
- **Co:** formularz „Apply for production access" wysłany 11:03. 8 odpowiedzi (Closed test / About app / Production readiness), wszystkie < 300/300. Q3.1 wzmocnione „confirmed" attribution: v1.0.3 (Dominik 05-11), v1.0.4 + v1.0.5 (Marcin 05-16) = 3/4 closed-loop fixes. Approved 05-17 17:32 (1-day turnaround). Production release v1.0.5 utworzony 05-18 (versionCode 6).
- **Co dalej:** nie wgrywać v1.0.6 / nie rotować testerów podczas review.

## 2026-05-13 — v1.0.5 hotfix: belka Ubera nad inną apką (same-day)
- **Co:** regresja od v1.0.2 — belka pojawiała się tylko gdy Uber Driver na pierwszym planie. Przyczyna: Layer 2 `hasUberOverlayWithContent` wymagał tekstu w oknie overlay, ale Uber Driver (RN) daje `text len=0` na Samsungu → foreground guard ubijał pipeline. Fix: powrót do `hasUberOverlayWindow` (samo istnienie okna), Layer 4 markery OCR niezmienione (chronią regresję Andrija). 9h od bug report do LIVE w Closed Testing (20:39). 4. iteracyjny AAB update (4/3 minimum).
- **Decyzje:** D8 (parser nie zakłada ekspozycji tekstu) — zob. `DECISIONS.md`.

## 2026-05-13 — v1.0.4 build + upload (Day 10, dzień przed planem)
- **Co:** version bump `versionCode 4→5`, `versionName 1.0.3→1.0.4` (commit `8e4686e`; kod `f58ec8c` gotowy od Day 9). AAB 24 MB. Pierwszy build padł po 27 min na `Failed to create MD5 hash` (iCloud Drive FULL blokował pliki) → `./gradlew clean` → SUCCESS. Upload do Closed Testing, walidacja Console OK (2 non-blocking warnings jak w v1.0.2/3).

## 2026-05-12 — Play Console verification (Day 9)
- **Co:** Default store listing zweryfikowany pole-po-polu i Save. Video URL skonwertowany z Shorts → `watch?v=` (Console nie przyjmuje Shorts). PP + Data Deletion zweryfikowane w incognito. EN translation świadomie pominięta dla v1.0 (zob. `DECISIONS.md` D6). Strategia jednego pakietu: store listing + v1.0.4 AAB razem, „Send for review" dopiero Day 14.

## 2026-05-05 — v1.0.2: fix false-positive Andrij (news portals)
- **Co:** multi-layer defense przeciw belce na portalach newsowych. Layer 1: strict foreground tracker + cross-check `rootInActiveWindow`. Layer 2: `hasUberOverlayWithContent()` wymaga markerów oferty. Layer 3: watch mode reset przy zmianie apki. Layer 4: pozytywne markery (10–15 fraz PL/EN/UA/RU) w parserach Uber/Bolt/Wolt — news portal nie zawiera „Łącznie"/„Akceptuj". `versionCode=3`. Pliki: `OrderPilotAccessibilityService.kt`, `Ocr*Parser.kt`.
- **Co dalej:** (uwaga) Layer 2 okazał się zbyt agresywny → regresja naprawiona w v1.0.5 (zob. D8).

---

> **Starsze wpisy (2026-04-30 i wcześniej) + historia faz POC/EPIC/Stabilizacja/Play Store prep → [`PROGRESS_ARCHIVE.md`](PROGRESS_ARCHIVE.md).**
> Dane techniczne keystore przeniesione do `RULES.md` (sekcja „Release / keystore").
