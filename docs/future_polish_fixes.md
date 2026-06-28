# OrderPilot — Poprawki i pomysły na przyszłość

> **⚠️ JEDYNE ŹRÓDŁO PRAWDY** dla drobnych bugów, usprawnień i pomysłów odkrytych podczas testów.
>
> **Lokalizacja:** `docs/future_polish_fixes.md` (ten plik, w repo). **NIGDZIE INDZIEJ.**
>
> **Dla AI:** Nie twórz równoległych plików o podobnej treści (np. `memory/future_polish_fixes.md`, `docs/TODO.md`, `polish_fixes.md`). Jeśli MEMORY.md lub inny plik md wspomina o tym dokumencie, powinien **linkować do tej ścieżki**, nigdy nie duplikować zawartości. Wcześniej istniał duplikat w auto-memory — usunięty 2026-04-08, bo dwa pliki = drift po 2-3 sesjach.
>
> **Użycie:** przy pytaniach „co robimy?" / „co można poprawić?" → odwołaj się do tego pliku.
>
> Ostatnia aktualizacja: 2026-04-28 (dodane #33/#34/#35 — Android 16 accessibility + auto-demo + telemetria, YT research v2)

---

### 1. ~~Domyślny czas belki~~ ✅ DONE (2026-03-17)
- Zmieniono na 30s (nie 40s — feedback od użytkownika). Wszystkie metryki domyślnie widoczne.

---

### 2. ~~Belka — miganie + łamanie tekstu + resetowanie timera~~ ✅ ROZWIĄZANE (04-04: potwierdzone)
- **Co się działo (1):** Tekst czasu łamie się w złym miejscu — np. `25` w jednej linii i `min` w drugiej.
- **Co się działo (2):** Parser wywołuje `showOverlay` przy każdym screenshocie (~co 1.6s) dopóki popup jest widoczny → belka miga i nie znika (>30s).
- **Fix (2) — zaimplementowany:** `isSameAsPrevious()` w PipelineOrchestrator porównuje amount/minutes/distance/level — identyczny wynik = skip overlay update. Działa od ~03-22.
- **Fix (1) — do zrobienia jeśli problem wróci:** `\u00A0` (non-breaking space) między liczbą a `min`.
- **Status (04-04):** Sprawdzone — `isSameAsPrevious()` blokuje odświeżanie belki przy identycznym wyniku parsera. Zmiana motywu Ubera (jasny→ciemny) nie zmienia parsowanych wartości (amount/minutes/distance), więc belka NIE powinna mrugać. Tata nie zgłaszał migania od wdrożenia. Zamykamy — wracamy tylko jeśli problem się pojawi.

---

### 3. ~~Przycisk zamknięcia belki (×)~~ ✅ DONE (wcześniej)
- Zaimplementowany. Teraz `hideByPlatform(platform)` — ukrywa konkretną belkę z multi-overlay.

---

### 4. ~~Collision Wolt+Uber — parser Wolta parsuje screenshot Ubera~~ ✅ DONE (2026-03-14)
- **Fix wdrożony:** `ParserRegistry` matchuje parser po `supportedPackages` z packageName eventu accessibility. Każdy parser dostaje tylko swoje screenshoty.

---

### 5. ~~Czas wyświetlania belki per platforma~~ ✅ DONE (wcześniej)
- `displayTimeFor(platform)` w `AppSettings` + `PlatformSettings.displayTimeSeconds`. `OverlayAutoHider` dostaje czas per platforma.

---

### 6. ~~Throttler per platforma~~ ✅ DONE (wcześniej)
- `throttlers = mutableMapOf<String, EventThrottler>()` — osobny throttler per `packageName`.

---

### 7. Glovo — szacunkowy czas zamówienia na podstawie km (zł/h)
- **Problem:** Glovo nie pokazuje szacunkowego czasu dostawy, więc nie da się wyliczyć zł/h.
- **Pomysł:** Obliczyć szacunkowy czas na podstawie dystansu (km) i typu pojazdu użytkownika.
- **Wymagane zmiany:**
  - Setup wizard: pole wyboru pojazdu (rower / rower elektryczny / skuter / auto)
  - Engine: tabela prędkości per typ pojazdu → dystans / prędkość = szacunkowy czas
  - GlovoOcrParser/OfferAnalyzer: przekazywanie dystansu → wyliczanie zł/h
- **Uwaga:** Ryzykowne — szacunkowe prędkości (rower vs auto, centrum vs obrzeża) mogą dawać niedokładne zł/h. Brak czasu oczekiwania w restauracji (5-15 min) dodatkowo zaburza wynik. Może wprowadzić więcej problemów niż korzyści (utrata zaufania do apki). Wymaga dokładnego przemyślenia mechanizmu.
- **Status:** Pomysł — odłożony, wymaga lepszego podejścia.

---

### 8. ~~Jeden mechanizm sprawdzania duplikatów~~ ✅ DONE (2026-03-31)
- **Fix:** `OfferDuplicateChecker` — wyciągnięcie zduplikowanej logiki do wspólnego obiektu (audyt kodu v2).

---

### 9. ~~Lepsza diagnostyka błędów OCR~~ ✅ ROZWIĄZANE (2026-04-10)
- OCR timeout 5s loguje osobno "timed out" vs "failed" vs pusta lista — diagnostyka wystarczająca.

---

### 10. ~~Usunięcie MediaProjection~~ ✅ NIEAKTUALNE (2026-04-11)
- API 30+ automatycznie pomija MediaProjection i używa `takeScreenshot()` — zaimplementowane 04-10.
- Kod MediaProjection zostaje jako fallback dla API <30. Usuwanie go to refaktoring bez korzyści dla użytkownika.

---

### 11. ~~Setup wizard + battery optimization~~ ✅ DONE (2026-03-25)
- Zaimplementowany: karty per producent (Samsung/Xiaomi/Huawei/Oppo/OnePlus), toast hints, domyślny język z system locale.

---

### 12. ~~Test service restart po adb kill~~ ✅ PRZETESTOWANE (2026-04-11)
- **Wynik (Samsung OneUI):** `adb shell am force-stop` zabija AccessibilityService. Samsung **NIE restartuje** serwisu automatycznie. Serwis znika z "Bound services", nie trafia do "Crashed services". Przy ponownym otwarciu apki wizard wykrywa brak usługi — user musi ręcznie włączyć ponownie.
- **Wniosek:** Jeśli OEM zabije apkę w tle → monitoring umiera po cichu. User nie wie.
- **Do zrobienia w przyszłości:**
  - Persistent notification ("OrderPilot monitoruje") — gdy zniknie, user widzi że coś nie tak.
  - Self-health check: `BOOT_COMPLETED` receiver + periodic alive-ping → powiadomienie "monitoring zatrzymany, kliknij żeby przywrócić".
  - ~~Xiaomi (MIUI) — do przetestowania osobno~~ ✅ Przetestowane 04-11: identyczny wynik jak Samsung — serwis nie wstaje po force-stop.
- **Status:** Zdiagnozowane, fix odłożony.

---

### 13. Huawei/EMUI — kompatybilność takeScreenshot()
- **Ryzyko:** Huawei EMUI może blokować `AccessibilityService.takeScreenshot()` (brak dokumentacji, nieznany OEM skin).
- **Co zrobić:** Jeśli zdobędziemy telefon Huawei — przetestować czy screenshot pipeline w ogóle działa. Fallback: MediaProjection (już zaimplementowany).
- **Status:** Brak telefonu do testów — monitorowane.

---

### 14. ~~PopupCropper crop ratio~~ ✅ DONE (2026-04-01)
- `CROP_START_RATIO` z 0.40 na 0.30. Monitorować czy OCR nie parsuje śmieci z górnej części ekranu.

---

### 15. Foldable / tablet aspect ratio
- **Ryzyko:** Na foldable (Samsung Fold, Motorola Razr) i tabletach aspect ratio jest niestandardowy. PopupCropper zakłada stały % — popup może być wyżej/niżej.
- **Co zrobić:** Jeśli zdobędziemy foldable — przetestować. Ewentualnie: dynamiczny crop na podstawie wykrytego popup boundary (ML Kit bounding box).
- **Status:** Brak telefonu — monitorowane na przyszłość.

---

### 16. ~~Dodanie języka rosyjskiego~~ ✅ ZAIMPLEMENTOWANE (2026-04-11)
- AppLanguage.RU, values-ru/strings.xml (109 stringów), rival markers RU w 4 parserach, filtr gotówkowy RU, overlay ч/км/мин.
- Waluty (RUB/BYN/KZT) NIE dodane — osobne zadanie na przyszłość.

---

### 17. Wskaźnik akceptacji kursów — ochrona przed banem
- **Problem:** Kurierzy używający OrderPilot mogą odrzucać zbyt wiele zleceń (bo widzą że zł/h jest niskie), co grozi obniżeniem acceptance rate i potencjalnym banem/depriorytetyzacją na platformie.
- **Pomysł:** Wyświetlać użytkownikowi jego bieżący wskaźnik akceptacji (np. "Akceptacja: 73% — uwaga, poniżej 80%"), żeby świadomie decydował kiedy odrzucić a kiedy przyjąć słabsze zlecenie.
- **Wymagane do analizy:**
  - Skąd brać dane? Platformy raczej nie eksponują acceptance rate w accessibility tree ani na ekranie oferty. Możliwe źródła: ekran statystyk (OCR/tree read), ręczne wpisanie przez usera, zliczanie accept/reject w apce.
  - Zliczanie lokalne (OrderPilot liczy ile belek user widział vs ile zaakceptował) — najprostsze, ale niedokładne (nie wie czy user naprawdę przyjął/odrzucił).
  - Progi alarmowe per platforma — każda platforma ma inne zasady (Uber vs Glovo vs Bolt vs Wolt).
  - UX: gdzie wyświetlać? Belka? Osobny widget? Ekran ustawień?
- **Wstępny kierunek:** Hybryda — zliczanie lokalne (ile ofert widzianych w sesji) + oportunistyczny odczyt z ekranu statystyk platformy (gdy user wejdzie w ustawienia/statystyki → screenshot + OCR).
- **Status:** Pomysł — wymaga analizy przed implementacją.

---

### 18. Fałszywy parse ze screenshotów w galerii / innych apkach
- **Problem:** Gdy użytkownik przegląda galerię zdjęć (lub inną apkę) na której widać stary screenshot zlecenia z belką OrderPilot, a w tym momencie Uber trzyma pusty overlay (znany problem Xiaomi — persistent type=3 overlay), nasz pipeline robi screenshot → OCR czyta stary screenshot z galerii → parser parsuje go jako nowe zlecenie → wyświetla belkę z błędnymi danymi.
- **Zaobserwowane:** 2026-04-11, Xiaomi taty. Dwa przypadki:
  1. Galeria zdjęć ze starym zleceniem + pusty persistent overlay Xiaomi → OCR odczytał 79 linii śmieci → fałszywy RED parse. Poprawiony ~4s później gdy przyszło prawdziwe zlecenie.
  2. Przeglądanie ustawień Ubera (bez prawdziwego zlecenia) → belka się pojawiła na podstawie tekstu z ekranu ustawień.
- **Potencjalne fixy:**
  - Guard na liczbę linii OCR: normalny popup Ubera ma ~10-15 linii. Jeśli OCR zwraca >30-40 linii → prawdopodobnie screenshot złapał zły ekran → odrzuć i retry.
  - Guard na foreground app: jeśli na pierwszym planie jest galeria/menedżer plików → skip screenshot.
  - Guard na treść OCR: jeśli tekst zawiera nazwy plików (`.png`, `.txt`, `.jpg`) → odrzuć.
- **Status:** Znany, niski priorytet. Na etapie testów wręcz przydatne (można testować pipeline ze starych screenshotów gdy nie ma nowych zleceń).

---

### 19. ~~Większy krzyżyk zamknięcia belki (przycisk jak na Uberze)~~ ✅ DONE (2026-04-12)
- Przycisk × jako kółko w odcieniu belki (większy, lepiej widoczny). Zaimplementowany w overlay_offer.xml + OverlayViewFactory.

---

### 20. Możliwość przesuwania belki (drag)
- **Problem:** Belka zasłania przycisk odrzucenia/anulacji zlecenia na platformie. Użytkownik musi najpierw zamknąć belkę żeby odrzucić zlecenie — to wkurzające i problematyczne, szczególnie przy krótkim timerze na decyzję.
- **Propozycja taty:** Belka powinna być przesuwalna (drag & drop) żeby można ją przesunąć w inne miejsce ekranu bez zamykania.
- **Potencjalne podejścia:**
  - `OnTouchListener` z `ACTION_MOVE` na overlay WindowManager → aktualizacja `layoutParams.y`
  - Zapamiętywanie ostatniej pozycji w SharedPreferences żeby belka pojawiała się tam gdzie user ją ostatnio zostawił
  - Alternatywa: swipe-to-dismiss (przesunięcie w bok zamyka belkę) — prostsze ale nie rozwiązuje problemu zasłaniania
- **Priorytet:** Wysoki — bezpośrednio wpływa na UX i decyzyjność kuriera.
- **Status:** Do zrobienia.

---

### 21. ~~Oznaczenie zlecenia gotówkowego na belce~~ ✅ DONE (2026-04-12)
- `isCash: Boolean` w Offer. Glovo: per-amount prefix detekcja + containsCashMarkers() fallback. Wolt/Bolt: generyczne markery PL/EN/UK/RU (do weryfikacji z prawdziwymi zleceniami). 💵 emoji na końcu belki. Testy jednostkowe.

---

### 23. ~~Wizard — poprawić instrukcje dla AccessibilityService~~ ✅ DONE (2026-04-12)
- Toast z krokiem "Zainstalowane aplikacje" dodany w SetupActivity. User widzi podpowiedź gdzie szukać OrderPilot.

---

### 24. ~~Pozycjonowanie przycisku × na belce~~ ✅ DONE (2026-04-16)
- `overlay_offer.xml`: `layout_gravity="center_vertical|end"` zamiast `top|end`, usunięty `layout_marginTop="4dp"`. `marginEnd="4dp"` zostaje. Przycisk × wycentrowany w pionie przy prawej krawędzi belki.

---

### 25. ~~Waluta PLN zamiast "zł" przy lokaleu EN~~ ✅ DONE (2026-04-16)
- `OverlayViewFactory.labels()`: override — gdy `language == AppLanguage.EN && currency == "zł"` → `displayCurrency = "PLN"`. Dotyczy tylko wyświetlania na belce (currency, currencyPerHour, currencyPerKm). Parsery dalej matchują `zł` na wejściu. Inne lokale (PL/UK/RU) + inne waluty (₴/₽/€) bez zmian.

---

### 26. Docelowa ikona aplikacji (branch `polishing`, 2026-04-16)
- **Problem:** Aktualna ikona apki to placeholder/domyślna z Android Studio. Przed wypuszczeniem bety na Play Store potrzebujemy czegoś własnego.
- **Co zrobić:**
  - Zaprojektować ikonę reprezentującą OrderPilot (pilot kursów? lupa + samochód? logo?).
  - Wyprodukować warianty: `ic_launcher` (classic), `ic_launcher_round`, `ic_launcher_foreground` (adaptive icon), `ic_launcher_background`. Dla Play Store: 512x512 PNG.
  - Sprawdzić czy notyfikacyjna ikona (`ic_notification.xml`, monochrome) jest zgodna z nowym brandingiem.
- **Uwaga:** To zadanie design-first. Etap 1: ustalenie kierunku wizualnego z userem. Etap 2: produkcja zasobów. Można też rozważyć zlecenie projektantowi.
- **Priorytet:** Średni — potrzebne przed publikacją bety na Play Store.
- **Status:** Do zrobienia w branchu `polishing` (lub osobnym `feature/branding`).

---

### 27. ~~Setup wizard — przycisk „Allow background activity" nie działa po usunięciu `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`~~ ✅ DONE (2026-04-20, Opcja A)
- **Problem:** Po usunięciu permission `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` w Batch 1 Play Store prep (Task 2.11, AndroidManifest.xml), kliknięcie „Allow background activity" w Setup wizard nie pokazuje **nic** — system nie otwiera dialogu i **nie rzuca wyjątku**, więc fallback w `try/catch` (`SetupActivity.kt:340-344`) się nie aktywuje.
- **Skutek:** `isIgnoringBatteryOptimizations(packageName)` zostaje `false` → `isSetupComplete()` zwraca `false` (linia 440-453) → przycisk **Continue zostaje szary** → user nie może przejść dalej w setupie. **Hard block całego setup flow.**
- **Root cause:** `Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` (linia 337) wymaga deklaracji permission w manifeście. Bez niej intent nie pokazuje dialogu i nie rzuca catchable exception (zachowanie różni się między wersjami Androida).
- **DECYZJA (2026-04-19): Opcja A — bezpieczna pod Play Store policy.**
  - W `SetupActivity.requestBatteryOptimizationExemption()` (linia 336-345) **usunąć** próbę `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` i od razu wołać `Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)` (lista wszystkich apek).
  - Dodać `Toast` z hintem (i18n we wszystkich 4 stringach: PL/EN/UK/RU): „Znajdź OrderPilot na liście i wybierz 'Bez ograniczeń'" / „Find OrderPilot in the list and select 'Don't optimize'" / etc.
  - Pozostawić `isSetupComplete()` bez zmian (battery dalej hard requirement) — user dokończy ręcznie i `onResume()` odświeży stan.
- **Dlaczego Opcja A, nie B/C:**
  - **Opcja B (battery jako soft requirement):** odrzucone — degradacja niezawodności na Xiaomi/Samsung; userzy pominą krok i zacznie się dzwonienie „nie działa po wyłączeniu ekranu".
  - **Opcja C (revert permission):** odrzucone — Play policy traktuje `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` jako restricted permission; ryzyko rejection w Play Console review (Risk #7 z `01_analysis_v2.md`). Zgodne z rekomendacją w `02_implementation_plan.md` Task 2.11.
- **Status:** ✅ ZAIMPLEMENTOWANE (2026-04-20).
- **Zmiany:**
  - `SetupActivity.requestBatteryOptimizationExemption()` — usunięty `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`, wołamy od razu `ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS` przez `safeStartActivity` (z fallbackiem do `ACTION_SETTINGS`). `Toast` z nowym hintem.
  - Nowy string `toast_hint_battery_optimization` w 4 lokalach (PL/EN/UK/RU).
- **Powiązane:** `docs/play-store/02_implementation_plan.md` Task 2.11; `docs/play-store/01_analysis_v2.md` Risk #7.

---

### 28. Język aplikacji — rosyjski/ukraiński nie zmienia UI (zgłoszone 2026-04-28, **re-reported 2026-05-07** przez Dominika)
- **Problem:** Po zmianie języka w ustawieniach aplikacji na rosyjski lub ukraiński UI aplikacji **nie zmienia języka** — pozostaje w domyślnym (PL/EN).
- **🆕 2026-05-07 update (po testach v1.0.2):** Dominik dostarczył screeny + cytaty: „klikam na język angielski, zapisuje i działa" / „natomiast jak wybieram ukraiński i zapisuje to zmienia się na język polski". Czyli wybór UA/RU **resetuje do PL po Save** (nie tylko UI nie tłumaczy się — sam zapis preferencji nie utrzymuje się).
- **Niesprawdzone:** Czy belka (overlay) zmienia język. Możliwe że strings dla overlay są poprawnie tłumaczone, a problem dotyczy tylko UI ustawień/głównego ekranu.
- **Możliwe przyczyny do sprawdzenia:**
  - `LocaleHelper` (ui/LocaleHelper.kt) — czy wspiera RU/UK locales tak samo jak PL/EN
  - `SharedPrefsSettingsRepository` — czy `AppLanguage.UK` / `AppLanguage.RU` są poprawnie persistowane (może mapowanie enum→string fallbackuje do PL?)
  - Brak resource folderów `values-ru/` i `values-uk/` (lub niepełne tłumaczenia → fallback do default)
  - `AppLanguage` enum w domain — czy ma RU/UK warianty (✅ kod ma `AppLanguage.UK` i `AppLanguage.RU`)
  - `attachBaseContext` w MainActivity / SettingsActivity — czy LocaleHelper.wrap jest aplikowany przy wszystkich activity
- **Test:** zmienić na RU/UK → kliknąć Save → reopen Settings → sprawdzić które radio jest zaznaczone (powinno być RU/UK, według Dominika wraca PL).
- **Priorytet:** **Podniesiony do Średniego** — Dominik to drugi tester-driven fix po Andriju → mocny case study do Production Application Form. Naprawić w v1.0.3.
- **Status:** Planowany fix w v1.0.3 (Day 7-8 ≈ 2026-05-10/11).
- **Powiązane:** `test-data/closed-testing/screenshots/dominik feedback/feedback_2026-05-07.md` + `docs/closed-testing-evidence.md` sekcja Dominika.

---

### 29. Przycisk „Zapisz ustawienia" zakryty przez Samsung navigation bar (zgłoszone 2026-04-28, **re-reported 2026-05-07** przez Dominika)
- **Problem:** Na telefonie Samsung Dominika dolny przycisk „Zapisz ustawienia" (i prawdopodobnie inne dolne przyciski w setup wizard typu Continue) jest **częściowo zakryty** przez systemowy pasek nawigacji Samsung (gesture/3-button bar).
- **🆕 2026-05-07 update (po testach v1.0.2):** Dominik podał cytat: „jest to denerwujące przy codziennym użytkowaniu" / „jest tylko część przycisku dostępna do kliknięcia" + screen pokazujący overlap. Bug jest stały, nie jednorazowy.
- **Skutek:** User nie widzi że przycisk istnieje albo nie może go kliknąć (touch trafia w nav bar zamiast w przycisk).
- **Możliwe przyczyny:**
  - Brak `android:fitsSystemWindows="true"` lub `WindowInsets` handling w layoucie ustawień
  - Layout root nie respektuje `systemBars()` insets — przycisk na dole bez padding bottom = systemBars insets
  - Edge-to-edge nie jest poprawnie obsłużony (Android 15+ wymusza edge-to-edge dla apek z targetSdk 35+)
- **Fix do zrobienia:**
  - Dodać `WindowInsetsCompat` listener w SettingsActivity / SetupActivity który ustawia `paddingBottom` = `systemBars().bottom`
  - Albo `android:fitsSystemWindows="true"` na root layoutu jeśli to wystarczy
  - Sprawdzić target Sdk — jeśli 35+, edge-to-edge jest obowiązkowy
- **Priorytet:** Średni — to jest realny UX bloker dla testerów na Samsungu. Trzeba naprawić zanim Production.
- **Status:** Planowany fix w v1.0.3 (Day 7-8 ≈ 2026-05-10/11).
- **Reproducible on:** Samsung (Dominik), prawdopodobnie też inne urządzenia z dolnym pasem nawigacji.
- **Powiązane:** `test-data/closed-testing/screenshots/dominik feedback/feedback_2026-05-07.md` + `docs/closed-testing-evidence.md` sekcja Dominika.

---

### 30. Pre-permission screen przed systemowym dialogiem accessibility (onboarding UX)
- **Inspiracja:** [Mobin — onboarding analysis 1000+ apps](https://www.youtube.com/watch?v=jqoFP9QapXI&t=480s) @ 8:00 — „A lot of apps show a custom screen before the notification pop-up. Apparently, it improves accept rates significantly."
- **Problem:** Systemowy dialog Androida o uprawnieniach accessibility brzmi alarmująco („App can read your screen, perform actions, observe your typing"). Dla nietechnicznego usera (np. starszy kurier) wygląda jak malware → odmawia. To prawdopodobnie nasz #1 dropoff w onboardingu (do zweryfikowania na danych z Closed Testing).
- **Pomysł:** Przed `Settings.ACTION_ACCESSIBILITY_SETTINGS` pokazać własny ekran-pomost który tłumaczy DLACZEGO i ZAPEWNIA o prywatności.
- **Treść ekranu (draft):**
  - Tytuł: „Następny ekran zapyta o uprawnienie systemowe"
  - Body: „OrderPilot potrzebuje dostępu accessibility, żeby widzieć ofertę z Ubera/Wolta/Glovo/Bolta i policzyć zł/h. To jedyny sposób — nie ma API."
  - Reassurance bullets: „🔒 Wszystko zostaje na Twoim telefonie. Zero internetu. Zero kont. Zero danych do nas."
  - CTA: „Otwórz ustawienia systemu" → `Settings.ACTION_ACCESSIBILITY_SETTINGS`
- **Wymagane zmiany:** Nowy fragment/screen w `SetupActivity` przed wywołaniem accessibility intent. Strings PL/EN/UK/RU.
- **Korzyść dodatkowa:** Google Play przy `isAccessibilityTool=false` (decyzja w `play_store_strategy.md`) wymaga jasnego uzasadnienia w runtime — pre-prompt podwójnie się opłaca (lepszy accept rate + zgodność z polityką).
- **Priorytet:** Średni — wartościowy ale nie blocker. Zweryfikować najpierw po feedbacku z Closed Testing czy faktycznie userzy odpadają na tym kroku.
- **Status:** Pomysł — odłożony do feedbacku z Closed Testing.

---

### 31. Demo overlay bar w setupie (aha-moment przed permissions)
- **Inspiracja:** [Mobin — onboarding analysis 1000+ apps](https://www.youtube.com/watch?v=jqoFP9QapXI&t=92s) @ 1:32 — „Alma goes one step further. It lets you try the core experience before you sign up. I rarely see apps with AI features who lets you try it out before signing up an account."
- **Problem:** User w setupie nie wie co dostanie zanim odda dostęp. Decyduje na ślepo.
- **Pomysł:** Pokazać **fake belkę z przykładową ofertą** w setupie, żeby user zobaczył wartość zanim przebrnie przez permissions.
- **Realizacja:**
  - Statyczny mock layout w setupie: poziomy „pasek" w stylu prawdziwego overlaya, np. `Uber • 18,50 zł • 4,2 km • 28 zł/h • GREEN`
  - Krótki tekst: „Tak będzie wyglądać OrderPilot na Twoim ekranie nad apką kurierską — pokazujemy zł/h zanim klikniesz Akceptuj."
  - Można dodać 2-3 warianty (Glovo z gotówką 💵, Wolt RED nieopłacalne) jako mini-karuzelę żeby pokazać różne stany.
- **Wymagane zmiany:** Nowy ekran w SetupActivity (lub karta na pierwszym ekranie głównym). Reuse `OverlayViewFactory` z dummy `Offer` i `AnalysisResult` żeby spójne wizualnie z prawdziwą belką. Strings i18n.
- **Priorytet:** Średni — silny psychological driver, ale wymaga design pracy żeby wyglądało ładnie.
- **Status:** Pomysł — odłożony, do rozważenia po sprawdzeniu obecnego flow setupu.

---

### 32. Persistent setup checklist na MainActivity (zamiast guided tour)
- **Inspiracja:** [Mobin — onboarding analysis 1000+ apps](https://www.youtube.com/watch?v=jqoFP9QapXI&t=455s) @ 7:35 — „when Mural replaced pop-ups and banners with a clear six-step checklist, it drove a 10% relative increase in one week retention. Checklist stick around even after the user dismisses the initial flow."
- **Problem:** Setup ma kilka kroków (accessibility, overlay, battery optimization, wybór platform). Jeśli user zacznie i nie skończy → wraca do MainActivity i nie wie co jeszcze do zrobienia. Aktualnie polegamy na `isSetupComplete()` redirect do SetupActivity, ale to all-or-nothing.
- **Pomysł:** Widoczny checklist na MainActivity dopóki któryś krok niedokończony.
- **Realizacja:**
  - Card / sekcja u góry MainActivity widoczna tylko gdy `!isSetupComplete()`:
    ```
    Konfiguracja OrderPilota
    ✅ Accessibility włączone
    ✅ Overlay nad innymi apkami
    ⏳ Battery optimization — kliknij żeby otworzyć
    ⏳ Wybierz platformy (Uber, Glovo, Wolt, Bolt)
    ```
  - Każdy nieukończony krok = klikalny → redirect do odpowiedniego intent / sekcji ustawień
  - Po zakończeniu wszystkich kroków → karta znika
- **Wymagane zmiany:**
  - Nowy view (CardView lub LinearLayout) w `activity_main.xml`
  - Logika w MainActivity: sprawdzanie statusu każdego kroku (już istnieje rozproszona w SetupActivity → wyciągnąć do `SetupChecklist` helpera w `ui/`)
  - Strings i18n
- **Korzyść:** User który dropnie setup w połowie ma drogę powrotną. Nie musimy go zmuszać do całego SetupActivity flow.
- **Priorytet:** Niski — current redirect do SetupActivity działa. To jest „nice to have" a nie blocker.
- **Status:** Pomysł — odłożony, niski priorytet.

---

### 33. ⚠️ Android 16 — `accessibilityDataSensitive` flag może zepsuć pipeline Glovo/Bolt
- **Inspiracja:** [Android Developers — Accessibility Service Abuse](https://www.youtube.com/watch?v=GAv5-OAjle4&t=97s) @ 1:37 — „The accessibility data sensitive flag allows you to explicitly mark a view or composable as containing sensitive data" + @ 1:53 — „genuine accessibility apps can continue to provide their service by setting the `isAccessibilityTool` flagged to true in their manifest" + @ 2:11 — „Google Play Protect will also take action when it detects that a non-accessibility app has falsely declared that it is an accessibility tool."
- **Problem:** Android 16 wprowadza nową flagę `accessibilityDataSensitive` którą **inne apki** (Glovo, Bolt, Uber, Wolt) mogą ustawić na widokach z wrażliwymi danymi (cena, dystans, dane klienta). Apki z `isAccessibilityTool=false` w manifeście **nie mogą czytać** widoków oznaczonych tą flagą. Mamy `isAccessibilityTool=false` (decyzja w `play_store_strategy.md` — Alternative Use track na Play Store).
- **Skutek dla pipeline:**
  - **Glovo/Bolt (accessibility tree read)** — jeśli któraś z apek włączy flagę na popupie oferty → `OfferParser` przestaje widzieć tekst → **pipeline kompletnie martwy** na Androidzie 16+.
  - **Uber/Wolt (OCR przez `takeScreenshot()`)** — prawdopodobnie przetrwa, bo `takeScreenshot()` czyta display buffer (pixele), nie view tree. Ale to **nie jest zweryfikowane**, możliwe że Google rozszerzy ochronę.
- **Co zrobić (monitoring task):**
  1. Po wyjściu Android 16 stable (~Q3 2026) zainstalować i przetestować każdą z 4 apek kurierskich na świeżym buildzie.
  2. Sprawdzić w logach czy `OfferParser`/`AccessibilityTextCollector` nadal odbierają tekst z popupów Glovo/Bolt.
  3. Sprawdzić czy `takeScreenshot()` nadal działa dla Uber/Wolt (czy nie zwraca pustego/zaczernionego obrazu).
- **Decyzja architektoniczna jeśli flag zostanie włączony:**
  - **Opcja A:** Przełączyć `isAccessibilityTool=true` → przejść Play Store accessibility review (`play_store_strategy.md` → revisit).
  - **Opcja B:** Migrować Glovo/Bolt na pipeline OCR (jak Uber/Wolt). Większy koszt CPU/baterii, ale niezależne od flagi.
  - **Opcja C:** Hybryda — A dla pipeline który zostanie zablokowany, B jako fallback.
- **Priorytet:** Wysoki (potencjalny architectural blocker), ale **nie pilny** — Android 16 jeszcze nie stable, zero apek kurierskich na razie nie używa flagi (na 2026-04-28).
- **Status:** Monitoring — sprawdzić ponownie po wyjściu Android 16 stable.

---

### 34. Auto-trigger demo overlay raz po setupie (rozszerzenie #31)
- **Inspiracja:** [Android Developers — Improve User Onboarding for Google Play](https://www.youtube.com/watch?v=fK5OLEP0DdE&t=38s) @ 0:38 — „80% of people installing top performing apps make this decision \[to keep using or churn\] within the first 10 minutes of use."
- **Problem:** Realny kurier po dokończeniu setupu odpala apkę kurierską i czeka 30+ minut na pierwsze prawdziwe zlecenie. W tych 30 minutach **nie ma żadnego sygnału** że OrderPilot działa. 80% userów decyduje czy zostaje w pierwszych 10 minutach → mamy duży risk dropu zanim zobaczą wartość.
- **Pomysł:** Po pierwszym successful setup (`isSetupComplete() == true` + flag `hasShownDemo == false` w SharedPrefs) **automatycznie odpalić** demo overlay z #31 raz przez 5-8s, z disclaimerem „To jest demo — Twoje zlecenia będą wyglądać tak samo."
- **Różnica vs #31:** #31 to statyczny mock w SetupActivity (user widzi i klika dalej). #34 to **prawdziwy overlay** wyświetlony przez `OverlayManager.show()` — user widzi mechanikę pojawiania się belki, nie tylko obrazek.
- **Wymagane zmiany:**
  - Flag w `AppSettings`: `hasShownPostSetupDemo: Boolean = false`
  - W `MainActivity.onResume()` (lub na końcu SetupActivity flow): jeśli `isSetupComplete() && !hasShownPostSetupDemo` → wywołać `OverlayManager.showDemo(Platform.UBER, dummyOffer)` + ustawić flag na `true`
  - Dummy `Offer` + `AnalysisResult` (np. 18,50 zł / 4,2 km / 28 zł/h / GREEN)
  - Toast/banner przed overlayem: „Tak będzie wyglądać OrderPilot. Pokazujemy demo raz."
  - Auto-hide po 5-8s (dłużej niż domyślne 30s, bo to demo a nie prawdziwa decyzja)
- **Edge case:** Co jeśli user akurat ma uruchomioną apkę kurierską i przyjdzie prawdziwe zlecenie? Demo nie powinno blokować prawdziwego pipeline. Rozwiązanie: demo używa osobnego slotu w `SystemOverlayManager` lub jest skipped jeśli pipeline w trakcie pracy.
- **Priorytet:** Średni — wzmacnia #31, razem dają silniejszy aha-moment.
- **Status:** Pomysł — implementować razem z #31 (lub jako follow-up).

---

### 35. Telemetria opt-in (PostHog/podobne) — decyzja architektoniczna
- **Inspiracja:** [Chris Raroque — Things I ALWAYS Do Before Launching](https://www.youtube.com/watch?v=MnF-zJhyUtE&t=88s) @ 1:28 — „in a few months, you're going to deeply wish that you installed it earlier... It takes like max 30 minutes and you lose nothing by doing it" + @ 1:47 — „the main reason to set up analytics is you want to see why users are churning very early so you can make product decisions."
- **Problem:** Mamy 2/12 opted-in testerów do Closed Testing, idziemy do Production. **Nie mamy żadnej telemetrii** — nie wiemy:
  - Czy OCR pipeline w ogóle wykrywa oferty (false negative — apka działa ale nic nie pokazuje)
  - Czy parser zwraca sensowne wyniki (false positive — pokazuje belkę z błędnymi liczbami)
  - Time-to-first-offer (kluczowy retention metric — 80% decyzji w 10 min, patrz #34)
  - Crash rate w pipeline / które urządzenia/OEM są problematyczne
  - Session length, daily active users, retention curve
- **Konflikt z `play_store_strategy.md`:** Aktualna decyzja = **zero network** (część positioning na Play Store: „wszystko lokalnie, prywatność, brak chmury"). Telemetria łamie tę zasadę.
- **Możliwe podejścia:**
  - **A) Status quo** — żadnej telemetrii, lecimy ślepo, debugujemy przez ręczne raporty od testerów. Najprostsze, najbezpieczniejsze pod kątem Play policy, najgorsze pod kątem product development.
  - **B) Opt-in telemetria z explicit consent w setupie** — osobny ekran w SetupActivity „Pomóż nam ulepszyć OrderPilot — dane anonimowe, możesz wyłączyć" → toggle w `AppSettings`. Tylko jeśli user zgodzi się — wysyłamy events do PostHog/podobnego (self-hosted lub free tier).
  - **C) Tylko crash reporting (Sentry/Firebase Crashlytics)** — kompromis. Nie tracking użycia, tylko crashe + ANR. Mniej kontrowersyjne pod kątem prywatności.
- **Co tracking jeśli B/C:**
  - `setup_completed` (które kroki, jakie OEM, jakie platformy wybrane)
  - `pipeline_first_offer_detected` (time od `setup_completed`)
  - `pipeline_offer_parsed` (per platform + parser version)
  - `pipeline_error` (typ + stack)
  - `overlay_dismissed_by_user` vs `overlay_auto_hidden`
- **Wymagane decyzje:**
  - Czy łamiemy „zero network"? Jeśli tak — jak to opisać w opisie sklepu i privacy policy żeby nie wyglądało hipokrycko.
  - Self-hosted PostHog vs free tier vs Sentry-only?
  - Backend / koszty / RODO compliance (gdzie hostujemy serwer EU?)
- **Priorytet:** Wysoki strategicznie (bez tego trudno iterować po Production), ale wymaga przemyślenia konfliktu z brand/positioning. **NIE robimy w Closed Testing** — najpierw decyzja architektoniczna z userem.
- **Status:** Decyzja do podjęcia — nie implementować dopóki nie ustalone podejście (A/B/C).

---

### 36. Belka false-positive na portalach informacyjnych (zgłoszone 2026-04-29 przez Andrija)
- **Zgłaszający:** Andrij (UA real kurier, multi-platform, aktywny tester Closed Testing)
- **Cytat dosłowny:** „Podobne rzeczy pokazuje także na różnych portalach informacyjnych"
- **Kontekst:** Andrij używa apki podczas pracy (5h 57min online, 9 zleceń dnia 2026-04-29). Potwierdził że **przy zleceniach wszystko działa super** — problem dotyczy false positive: belka pojawia się na innych aplikacjach (portale newsowe typu Onet, WP, Interia, możliwe że też Facebook / Twitter / inne apki z UI elementami przypominającymi ofertę).
- **Prawdopodobna przyczyna:**
  - `ParserRegistry` matchuje parser po `supportedPackages` ✅ — więc parser sam się nie odpala
  - **ALE** OCR pipeline może być triggerowany szerzej (na każdy screenshot z aktywnej apki?) — jeśli accessibility event z `pl.onet.app` / `pl.wp.app` triggerują screenshot + OCR, to zwracane teksty mogą fałszywie matchować pattern oferty (cyfry + km + min)
  - Inna hipoteza: keep-alive overlay z innej platformy (Uber/Wolt) nie chowa się gdy user przechodzi do innej apki
- **Co sprawdzić:**
  - Lista `supportedPackages` w `ParserRegistry` — czy każdy parser jest ścisły?
  - Czy `OrderPilotAccessibilityService` filtruje package names przed odpaleniem screenshot/OCR?
  - Logi z urządzenia Andrija (jeśli da się wziąć) — który package triggeruje overlay
- **Priorytet:** **WYSOKI** — pierwszy real bug zgłoszony przez aktywnego Closed Testing testera. Dobra ammunition do AAB update v1.0.X (jednego z 3+ wymaganych przez Google).
- **Tracking:** Pełny kontekst + cytat w `docs/closed-testing-evidence.md` sekcja 2 (Andrij).
- **Status:** ✅ **NAPRAWIONE w v1.0.2** (2026-05-05). Multi-layer defense:
  - **Layer 1** — strict foreground tracker (`lastForegroundPackage` z `TYPE_WINDOW_STATE_CHANGED`) + cross-check z `rootInActiveWindow`, wpięty jako guard w `processViaScreenshot`, `processViaAccessibilityTree` oraz przed `pipeline.process()` we wszystkich 3 call sites. Real popup overlay Ubera (nad inną apką) przepuszczany przez wzmocnioną `hasUberOverlayWithContent()`.
  - **Layer 2** — `hasUberOverlayWithContent()` wymaga teraz markerów oferty (kwota+czas w odległości ≤120 znaków LUB konkretne frazy Ubera typu „Łącznie"/„Akceptuj"), nie samej obecności tekstu. Zamyka phantom-overlay edge case na MIUI.
  - **Layer 3** — watch mode reset: gdy `TYPE_WINDOW_STATE_CHANGED` z packagem spoza `watchedPackages`, cancel `uberWatchJob`/`boltWatchJob` + zerowanie `lastUberEventTime`/`lastBoltEventTime`. Plus dodatkowy guard wewnątrz Uber watch loop (skip tick gdy foreground != Uber).
  - **Layer 4** — positive markers w `UberOcrParser`/`BoltFoodOcrParser`/`WoltOcrParser`. Każdy popup parser wymaga teraz co najmniej 1 z ~10-15 fraz typowych dla popupu (multi-language: PL/EN/UA/RU). News portal nie zawiera „Łącznie"/„Odbiór za"/„Bolt"/„Akceptuj" — odrzucany niezależnie od foreground/timing.
  - Pliki: `OrderPilotAccessibilityService.kt`, `OcrOfferParser.kt`, `UberOcrParser.kt`, `BoltFoodOcrParser.kt`, `WoltOcrParser.kt`.

---

### 37. Progi PLN/km i PLN/h — wprowadzanie wartości dziesiętnych (zgłoszone 2026-05-10 przez Marcina)
- **Zgłaszający:** Marcin (Closed Testing tester, WhatsApp grupa „Beta testerzy courier assist")
- **Kontekst:** ekran ustawień progów kolorów belki (GREEN/YELLOW/RED dla PLN/km i PLN/h)
- **Bug 1 — PLN/km, locale PL:**
  - Klawiatura blokuje przecinek (`,`)
  - Kropka (`.`) wpisuje się i po pierwszym zapisie wartość pokazuje się jako przecinek (OK)
  - **ALE** gdy user zmieni cokolwiek innego w ustawieniach i ponownie zapisze (bez dotykania pola z dziesiętną), wartość traci ułamek: `2.5` → `2`. Powtarzalne.
- **Bug 2 — PLN/km, locale EN:** kropka działa stabilnie (nie zamienia się na przecinek, nie znika po re-save). Czyli **bug 1 jest locale-dependent** (PL).
- **Bug 3 — PLN/h:** w ogóle nie da się wprowadzić wartości dziesiętnej (ani przecinek, ani kropka). Tylko liczba całkowita.
- **Prawdopodobna przyczyna:**
  - `inputType="numberDecimal"` akceptuje tylko `.` niezależnie od locale → przecinek zablokowany przez klawiaturę
  - Parsing/format używa `NumberFormat.getInstance(locale)` które w PL oczekuje `,` → przy re-render po zapisie liczba `2.5` parsuje jako `2` (kropka jest separatorem grupującym w PL, nie dziesiętnym)
  - PLN/h vs PLN/km może mieć inny inputType / inny TextWatcher → stąd całkowity blok dziesiętnych dla PLN/h
- **Co sprawdzić / fix proposal:**
  - Settings input fields dla progów → ustawić `inputType="numberDecimal"` dla obu (PLN/km **i** PLN/h)
  - Custom DigitsKeyListener akceptujący `,` i `.` (lub force `.` w UI niezależnie od locale, normalizacja przy zapisie)
  - Parser wartości: `text.replace(",", ".").toDoubleOrNull()` przed zapisem do prefs
  - Format przy odczycie: użyć `String.format(Locale.US, "%.2f", value)` lub jawnie kontrolować locale wyświetlania
- **Materiały:** `test-data/closed-testing/screenshots/marcin feedback/2026-05-10_marcin_decimal-threshold-bug.jpg` + video `2026-05-10_marcin_decimal-threshold-bug.mp4` (0:55, reprodukcja na żywo)
- **Priorytet:** ~~WYSOKI~~ → ZAMKNIĘTE.
- **Status:** ✅ **NAPRAWIONE w v1.0.4** (commit `f58ec8c`, 2026-05-12). `SettingsActivity.kt` + `OfferAnalyzer.kt` — locale-aware decimal input (normalizacja `,`→`.`) + combined threshold logic (AND-semantics). Build wgrany 2026-05-13, **opublikowany przez Google 2026-05-13** (Play Console: „App update published"). Dobra ammunition do Application Form (3. iteracyjny AAB update w Closed Testing window).

### 38. Progi koloru PLN/h i PLN/km powinny działać jako AND (zgłoszone 2026-05-11 przez Marcina)
- **Zgłaszający:** Marcin (Closed Testing tester, WhatsApp grupa „Beta testerzy courier assist", 6:12 PM)
- **Kontekst:** ekran ustawień progów (Settings → Color thresholds) ma cztery pola: `Green from PLN/h`, `Yellow from PLN/h`, `Green from PLN/km`, `Yellow from PLN/km`. User naturalnie zakłada, że oba progi działają **łącznie**.
- **Bug — aktualne zachowanie:**
  - Repro przykład Marcina: oferta 25.61 zł / 45 min / 20.0 km = **34 PLN/h** + **1.3 PLN/km**
  - Ustawienia: Green/Yellow PLN/h = 40/34, Green/Yellow PLN/km = 3/2
  - Belka pokazuje **żółty/pomarańczowy** (bo 34 PLN/h = próg yellow)
  - Marcin oczekuje **RED**, bo PLN/km (1.3) jest **poniżej** progu yellow (2)
- **Przyczyna (kod):** `OfferAnalyzer.kt:26-30` — w głównej gałęzi (`estimatedMinutes > 0`) kolor liczony **wyłącznie** z PLN/h. Progi PLN/km z `ThresholdConfig` używane tylko w fallbacku dla Glovo (gdy brak czasu). Komentarz w `AppSettings.kt:13` to potwierdza: `// dla Glovo (brak czasu)`. UX pokazuje pola jako globalne, kod traktuje je per-platforma → mismatch oczekiwań.
- **Fix proposal:**
  - W głównej gałęzi `OfferAnalyzer.analyze()` policzyć osobno `levelFromHour` i `levelFromKm`
  - Finalny `level = min(levelFromHour, levelFromKm)` (gorszy wygrywa, AND-semantics) — czyli zamiana GREEN→YELLOW→RED na ordinal i `min`
  - **Edge — brak dystansu** (`distanceKm == null` lub `0`): pominąć `levelFromKm`, decyduje tylko `levelFromHour` (nie psujemy ofert z samym czasem; ~10 linijek zmian)
  - Glovo (`estimatedMinutes <= 0`) bez zmian — tam i tak decyduje tylko PLN/km
- **Materiały:** `test-data/closed-testing/screenshots/marcin feedback/2026-05-11_marcin_combined-thresholds-bug.jpg`
- **Priorytet:** ~~WYSOKI~~ → ZAMKNIĘTE.
- **Status:** ✅ **NAPRAWIONE w v1.0.4** (commit `f58ec8c`, 2026-05-12). `OfferAnalyzer.analyze()` refactor: `worstOf(levelFromHour, levelFromKm)` w głównej gałęzi, edge cases pokryte (brak/zero dystansu → fallback do PLN/h, Glovo path bez zmian). 7 nowych unit testów (Marcin repro + 6 edge cases) — wszystkie 19/19 testów PASSED. Build wgrany 2026-05-13, **opublikowany przez Google 2026-05-13 11:20 AM** (Play Console: „App update published"). Drugi z dwóch fixów Marcina w v1.0.4.

### 39. Belka Ubera nie pojawia się gdy popup nad inną apką (zgłoszone 2026-05-13 przez Marcina)
- **Zgłaszający:** Marcin (Closed Testing tester, WhatsApp grupa „Beta testerzy courier assist", rano 2026-05-13)
- **Kontekst:** Marcin często trzyma Wolt na pierwszym planie czekając na oferty Uber Driver w tle. Popup oferty Uber wyświetla się jako system overlay nad foreground app (Wolt / home screen) — belka OrderPilot powinna pojawić się niezależnie od tego która apka jest foreground.
- **Bug — aktualne zachowanie (v1.0.2-v1.0.4):**
  - Popup Uber widoczny na ekranie ale belka OrderPilot nie pojawia się
  - Belka pojawia się TYLKO gdy user jest w pełni w Uber Driver foreground
  - Regresja od v1.0.2 (multi-layer defense Layer 2 `hasUberOverlayWithContent` introduced dla fixu Andrij news portals)
- **Przyczyna (z accessibility logu Marcin 2026-05-13, 1999 linii):**
  - `Window[2]: type=3, pkg=com.ubercab.driver, text len=0` — Uber Driver to React Native, popup window nie eksponuje tekstu przez accessibility tree na Samsungu (i większości urządzeń)
  - v1.0.2 Layer 2 wymagał że overlay window EKSPONUJE widoczny tekst (`hasUberOverlayWithContent`) → na Samsungu `text len=0` → check fail
  - `isForegroundOfPackage("com.ubercab.driver")` zwracał false → fallback do trackera → tracker=launcher/Wolt → false → pipeline aborted przez foreground guard ("foreground mismatch after throttle — aborting pipeline")
- **Fix:**
  - `OrderPilotAccessibilityService.kt:isForegroundOfPackage` — zamiana `hasUberOverlayWithContent` na `hasUberOverlayWindow` (samo istnienie overlay window wystarczy; powrót do pre-v1.0.2 logiki dla popup-over-other-app path)
  - `OrderPilotAccessibilityService.kt:635` (watch mode) — ta sama zamiana dla symmetry, inaczej safety-net loop dead-end na RN Uber Driver
- **Safety dla regresji Andrija news portals:**
  - Layer 4 (`UberOcrParser.positiveOfferMarkers`) NIEZMIENIONE — wymaga "Łącznie"/"Total"/"Akceptuj"/"Доставка"/"Загалом"/"Принять" w tekście OCR (multi-language PL/EN/UK/RU)
  - Portal newsowy / social / inne apki nie zawierają tych markerów → parser zwraca null niezależnie od foreground/timing
  - Layer 3 (watch mode reset on app switch) + Layer 1 (foreground tracker dla Wolt/Glovo/Bolt) bez zmian
- **Materiały:** `test-data/closed-testing/logs/2026-05-13_marcin_uber-popup-over-other-app_accessibility-log.txt` (1999 linii)
- **Priorytet:** ~~KRYTYCZNY~~ → ZAMKNIĘTE.
- **Status:** ✅ **NAPRAWIONE w v1.0.5** (commit `e17860c` na branchu `fix/v1.0.5-uber-popup-background`, 2026-05-13 19:41). Same-day hotfix — 9h od bug report rano do LIVE wieczorem. versionCode 5→6, versionName 1.0.4→1.0.5. **LIVE w Closed Testing od 2026-05-13 20:39** (Play Console: „1.0.5 - Uber popup fix", Google auto-approved within minutes). Czwarty iteracyjny AAB update w 14-day Closed Testing window (4/3 minimum DONE). Lesson learned: belt-and-suspenders Layer 2 hardening v1.0.2 założył że Uber popup zawsze eksponuje tekst → assumption violated by RN Uber Driver na większości urządzeń → 7-day regression. Zob. `feedback_avoid_belt_suspenders.md` w memory.

---

## 🔍 AUDYT KODU 2026-06-28 (69 znalezisk) → pełna lista: `docs/AUDYT-2026-06-28.md`

> Wieloagentowy audyt całego kodu (27 agentów, niezależna weryfikacja każdego znaleziska). **69 realnych znalezisk** (3 high, 17 medium, 39 low, 10 nit; brak „critical"). **Pełna, ponumerowana lista z plikami/liniami/fixami jest w `docs/AUDYT-2026-06-28.md`** — tu tylko skrót i status sprintu #1, żeby nie duplikować (konwencja repo).

### ✅ Naprawione w sprincie #1 (branch `fix/audit-2026-06-28-batch1`, 2026-06-28) — czeka na build+test Krzysztofa
- **H1** crash całej apki z pipeline (brak `CoroutineExceptionHandler`) — `PipelineOrchestrator.kt`. + **L20** wyciek bitmapy screenshotu (try/catch wokół crop).
- **M2+M3+L39** persystencja ustawień/języka (część #28): wspólny `SettingsJson { encodeDefaults; ignoreUnknownKeys; coerceInputValues }` w `AppSettings.kt` + użycie w `SharedPrefsSettingsRepository`; testy w `AppSettingsTest`. **Uwaga #28:** część UI (`setApplicationLocales`) była już naprawiona; ta zmiana domyka PERSYSTENCJĘ (objaw „reset do PL po Save").
- **H3** czerwone testy parserów Uber/Wolt (bramka Layer-4 z commitu `15c131d` bez aktualizacji fixtur) — dodane realne markery popupu do fixtur + jawne testy bramki.
- **M1** logi crashy + „Zapisz logi" pisały do publicznego `Downloads` (martwe na targetSdk 35) → katalog prywatny apki (`getExternalFilesDir`/`filesDir`).
- **M5** czas belki per platforma gubiony na ścieżce MediaProjection → `displayTimeFor(platform)`.
- **L21** rosyjska jednostka minut `мін`→`мин`. **L31** `contentDescription` zębatki → `@string/settings_open`. **L32** nazwy kanałów powiadomień → `getString`. **L33** Stop chowa belkę natychmiast (`ServiceLocator.overlayManager.hide()`).

### 🔴 Najważniejsze OTWARTE (do decyzji/kolejnych sprintów) — szczegóły w `docs/AUDYT-2026-06-28.md`
- **H2 ⏸️ PARK (decyzja 28.06: na razie nie ruszać)** — Latin-only ML Kit OCR vs cyrylica UA/RU. Analiza 28.06: ML Kit NIE MA modelu cyrylicy (potwierdzone); psuje się głównie jednostka czasu (хв/мин) → brak belki. 3 drogi: 0=podpowiedź w setupie (tania, zero ryzyka), A=łatki translit+bramka agnostyczna pisma na ML Kit (**wymaga realnych logów OCR z urządzenia UA** — bez nich nie ruszać, ryzyko #36), B=Tesseract on-device ukr/rus (armata, +waga). Chmurowe OCR odrzucone (zero-network). Odblok A: log OCR od Andrija (przycisk „Zapisz logi" działa po M1). Pełne: `docs/AUDYT-2026-06-28.md` H2. `OcrEngine.kt`, parsery.
- ~~**M7** Bolt fałszywy GREEN z gotówki~~ → **ZWERYFIKOWANE 28.06 (research): bezprzedmiotowy dla PL.** Bolt Food w Polsce wycofał dostawy gotówkowe (kurierpedia.pl) → scenariusz nie zachodzi; dla UA moot (OCR ślepy na cyrylicę = H2). NIE ruszać parsera Bolt (ślepy fix = ryzyko). Wracać tylko gdyby Bolt PL wrócił do gotówki. `BoltFoodOcrParser.kt`.
- **#29 (insety)** naprawione w 3 Activity, ale POMINIĘTE w `MainActivity` (L1). **#33** ryzyko `accessibilityDataSensitive` od API 34 (Android 14), nie 16 (korekta L35).
- **Niezawodność/przeżywalność:** M10 (fałszywy status po reboocie), M11 (crash przy restarcie FGS z tła), M12 (odmowa POST_NOTIFICATIONS wycisza watchdoga), M13 (Vivo bez karty setupu), M16 (Oppo zła instrukcja autostartu).
- **Dokładność:** M4 (Xiaomi crop 0.40 na głównej ścieżce), M9 (Glovo dedup tłumi oferty), M14 (Glovo sumuje za dużo km). **Inne:** L6 (martwy `UberParser`), L8 (Wolt bez normalizacji OCR), L30 (tymczasowy przycisk „Zapisz logi" na produkcji — zostawiony, bo po M1 działa; do decyzji czy schować).
- Pełna lista LOW/NIT i sekcja „pewne vs do potwierdzenia testem na urządzeniu" → `docs/AUDYT-2026-06-28.md`.
