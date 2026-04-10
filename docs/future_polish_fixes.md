# OrderPilot — Poprawki i pomysły na przyszłość

> **⚠️ JEDYNE ŹRÓDŁO PRAWDY** dla drobnych bugów, usprawnień i pomysłów odkrytych podczas testów.
>
> **Lokalizacja:** `docs/future_polish_fixes.md` (ten plik, w repo). **NIGDZIE INDZIEJ.**
>
> **Dla AI:** Nie twórz równoległych plików o podobnej treści (np. `memory/future_polish_fixes.md`, `docs/TODO.md`, `polish_fixes.md`). Jeśli MEMORY.md lub inny plik md wspomina o tym dokumencie, powinien **linkować do tej ścieżki**, nigdy nie duplikować zawartości. Wcześniej istniał duplikat w auto-memory — usunięty 2026-04-08, bo dwa pliki = drift po 2-3 sesjach.
>
> **Użycie:** przy pytaniach „co robimy?" / „co można poprawić?" → odwołaj się do tego pliku.
>
> Ostatnia aktualizacja: 2026-04-08

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

### 10. Usunięcie MediaProjection — uproszczenie UX
- **Problem:** takeScreenshot() (API 30+) daje te same wyniki co MediaProjection, ale nie wymaga dialogu ze zgodą.
- **Fix:** Usunąć ScreenCaptureService i cały flow MediaProjection, zostawić tylko takeScreenshot().
- **Ryzyko:** Średnie — spory kawałek kodu do usunięcia. Sprawdzić czy tata/brat mają Android 11+.
- **Status:** Do rozważenia po stabilizacji.

---

### 11. ~~Setup wizard + battery optimization~~ ✅ DONE (2026-03-25)
- Zaimplementowany: karty per producent (Samsung/Xiaomi/Huawei/Oppo/OnePlus), toast hints, domyślny język z system locale.

---

### 12. Test service restart po adb kill (diagnostyka stabilności)
- **Co zrobić:** Podłączyć telefon USB, `adb shell am force-stop com.orderpilot.app`, sprawdzić czy AccessibilityService się restartuje.
- **Dlaczego:** OEM-y (Xiaomi MIUI, Huawei EMUI, Samsung OneUI) agresywnie zabijają serwisy w tle. Jeśli serwis nie wstaje po killu — użytkownik traci monitoring bez świadomości.
- **Kiedy:** Przy kolejnym teście u taty (Samsung + Xiaomi).
- **Status:** Do przetestowania.

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

### 16. Dodanie języka rosyjskiego
- **Problem:** Apka obsługuje PL/EN/UK, ale brakuje rosyjskiego — potencjalni użytkownicy-kurierzy w RU/BY/KZ.
- **Co zrobić:** Dodać `AppLanguage.RU`, `strings.xml` w `values-ru/`, rival markers RU, waluty RUB/BYN/KZT.
- **Status:** Do zrobienia.
