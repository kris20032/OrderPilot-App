# OrderPilot — Poprawki i pomysły na przyszłość

> **⚠️ JEDYNE ŹRÓDŁO PRAWDY** dla drobnych bugów, usprawnień i pomysłów odkrytych podczas testów.
>
> **Lokalizacja:** `docs/future_polish_fixes.md` (ten plik, w repo). **NIGDZIE INDZIEJ.**
>
> **Dla AI:** Nie twórz równoległych plików o podobnej treści (np. `memory/future_polish_fixes.md`, `docs/TODO.md`, `polish_fixes.md`). Jeśli MEMORY.md lub inny plik md wspomina o tym dokumencie, powinien **linkować do tej ścieżki**, nigdy nie duplikować zawartości. Wcześniej istniał duplikat w auto-memory — usunięty 2026-04-08, bo dwa pliki = drift po 2-3 sesjach.
>
> **Użycie:** przy pytaniach „co robimy?" / „co można poprawić?" → odwołaj się do tego pliku.
>
> Ostatnia aktualizacja: 2026-04-16

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
