# OrderPilot — Decyzje architektoniczne (ADR-lite)

> **Po co ten plik:** trwałe decyzje, które kształtują projekt i bywają później kwestionowane
> („dlaczego tak zrobiliśmy?", „czy możemy zmienić X?"). Gdy rozmowa dotyczy takiej decyzji — czytaj tutaj.
>
> **Czym różni się od `PROGRESS.md`:** PROGRESS to log sesji, **niemutowalny** (nie edytujesz starych wpisów).
> Tutaj **status jest EDYTOWALNY** — decyzja cofnięta = zmień pole `Status` na `❌ Cofnięta (RRRR-MM-DD)`
> i dopisz powód w `Aktualizacje`. **Nie usuwaj** wpisu i nie zostawiaj sprzecznych zapisów w logu.
>
> **Ten plik NIE jest archiwizowany po wieku.** Decyzja z miesiąca 1 musi być widoczna w miesiącu 12.
>
> **Granica:** tu są decyzje (kierunek + uzasadnienie). Szczegóły implementacyjne pojedynczych bugów
> i pomysłów → `docs/future_polish_fixes.md`. Nowa decyzja = nowy numer `Dn`.

---

## D1 — Play Store: `isAccessibilityTool=false` (track „Alternative Use")
- **Status:** ✅ Aktywna
- **Decyzja:** OrderPilot zgłaszany na Play Store jako narzędzie „Alternative Use" accessibility, z `isAccessibilityTool=false` w manifeście — nie jako klasyczne accessibility tool.
- **Dlaczego:** świadomy wybór ścieżki review/positioning; uniknięcie pełnego accessibility-tool review przy zachowaniu zgodności (prominent disclosure + runtime uzasadnienie).
- **Konsekwencje / ryzyka:** Android 16 wprowadza `accessibilityDataSensitive` — apki z `isAccessibilityTool=false` nie odczytają oznaczonych widoków → potencjalny blocker pipeline'u Glovo/Bolt (tree-read). Monitorowane.
- **Powiązania:** `docs/play-store/play_store_strategy.md`, `docs/future_polish_fixes.md` #33.

## D2 — Battery optimization = twardy wymóg setupu (Opcja A)
- **Status:** ✅ Zaimplementowana (2026-04-20)
- **Data decyzji:** 2026-04-19
- **Decyzja:** battery optimization exemption zostaje hard requirementem setupu; po usunięciu restricted permission `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` wołamy `ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS` (lista apek) + Toast z hintem w 4 językach.
- **Dlaczego:** Opcja B (soft requirement) odrzucona — degradacja niezawodności na Xiaomi/Samsung, userzy pominą krok. Opcja C (revert permission) odrzucona — Play traktuje to jako restricted permission, ryzyko rejection.
- **Powiązania:** `docs/future_polish_fixes.md` #27, `docs/play-store/02_implementation_plan.md` Task 2.11, `01_analysis_v2.md` Risk #7.

## D3 — Zero-network / brak telemetrii (positioning)
- **Status:** 🟡 Aktywna, W PRZEGLĄDZIE
- **Decyzja:** apka nie wysyła nic do sieci („wszystko lokalnie, zero kont, zero chmury") — element positioningu na Play Store.
- **W przeglądzie:** czy wprowadzić telemetrię opt-in/crash reporting po Production. Trzy warianty (A status quo / B opt-in PostHog / C tylko crash reporting) — **decyzja niepodjęta**, śledzona jako otwarte pytanie w `todo.md`.
- **Dlaczego trzymamy zero-network:** spójność z obietnicą prywatności w opisie sklepu; złamanie wymaga przepisania opisu + privacy policy, żeby nie wyglądało hipokrycko.
- **Powiązania:** `docs/play-store/play_store_strategy.md`, `docs/future_polish_fixes.md` #35.

## D4 — Progi koloru belki: AND-semantics (gorszy próg wygrywa)
- **Status:** ✅ Zaimplementowana (v1.0.4, 2026-05-12)
- **Decyzja:** kolor belki = `worstOf(levelFromHour, levelFromKm)` w głównej gałęzi `OfferAnalyzer.analyze()`. Brak/zero dystansu → decyduje tylko PLN/h. Glovo (brak czasu) → tylko PLN/km.
- **Dlaczego:** UX pokazuje progi PLN/h i PLN/km jako globalne → user oczekuje, że działają łącznie. Wcześniej kolor liczył się wyłącznie z PLN/h (km tylko w fallbacku Glovo) → mismatch oczekiwań (repro Marcin).
- **Powiązania:** `docs/future_polish_fixes.md` #38.

## D5 — MediaProjection zostaje jako fallback (API < 30)
- **Status:** ✅ Aktywna
- **Data decyzji:** 2026-04-11
- **Decyzja:** API 30+ używa `AccessibilityService.takeScreenshot()`; kod MediaProjection NIE jest usuwany — zostaje fallbackiem dla API < 30.
- **Dlaczego:** usunięcie MediaProjection to refaktor bez korzyści dla użytkownika; fallback kosztuje niewiele, a zabezpiecza starsze API.
- **Powiązania:** `docs/future_polish_fixes.md` #10.

## D6 — EN store listing pominięty w v1.0 (defer → v1.1)
- **Status:** ✅ Aktywna
- **Decyzja:** świadomy skip tłumaczenia EN w listingu sklepowym dla v1.0; planowane na v1.1 po Production.
- **Dlaczego:** redukcja rejection surface przy Production submit (mniej pól = mniej powodów do odrzucenia).
- **Powiązania:** PROGRESS log 2026-05-12, `todo.md` Plan v1.1.

## D7 — `main` chroniony: kod tylko przez branch, bezpośrednio tylko dokumentacja
- **Status:** ✅ Aktywna
- **Data decyzji:** 2026-02-27
- **Decyzja:** od momentu gdy `main` zawiera działający POC, ZAKAZ commitowania kodu `app/` bezpośrednio na `main`; każda zmiana kodu = osobny branch (`feature/…`, `fix/…`, `test/…`). Na `main` bezpośrednio trafia tylko dokumentacja i drobne poprawki.
- **Dlaczego:** `main` ma pozostać zawsze stabilny (buduje się, nie crashuje); ochrona POC przed regresją.
- **Powiązania:** `RULES.md` §4 (Git workflow).

## D8 — Parser nie zakłada, że popup eksponuje tekst przez accessibility tree
- **Status:** ✅ Aktywna zasada
- **Decyzja:** detekcja overlay Ubera opiera się na samym istnieniu okna overlay (`hasUberOverlayWindow`), a filtrowanie false-positive na pozytywnych markerach treści w OCR (Layer 4) — NIE na założeniu, że okno popupu wystawia widoczny tekst.
- **Dlaczego:** Uber Driver (React Native) zwraca `text len=0` w oknie overlay na większości urządzeń (Samsung). Hardening v1.0.2 (Layer 2 `hasUberOverlayWithContent`) złamał to założenie → 7-dniowa regresja (belka nie pojawiała się nad inną apką), naprawiona w v1.0.5. Lekcja: belt-and-suspenders na błędnym założeniu szkodzi.
- **Powiązania:** `docs/future_polish_fixes.md` #39, auto-memory `feedback_avoid_belt_suspenders.md`.
