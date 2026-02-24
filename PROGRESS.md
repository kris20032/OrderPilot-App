# CourierAssist - Status Postępu

**Ostatnia aktualizacja**: 2026-02-24
**Obecny etap**: Setup - ustalanie zasad pracy
**Folder projektu**: `/Users/krzysztof/Desktop/CourierAssist/`
**Cel**: Beta APK do testów na telefonie z Androidem

---

## 📊 Ogólny status: 0% → Beta

```
[ ] Krok 0: Środowisko
[ ] Krok 1: Szkielet + uprawnienia
[ ] Krok 2: MediaProjection capture
[ ] Krok 3: OCR (ML Kit)
[ ] Krok 4: Overlay
[ ] Krok 5: Parsery (Glovo → UberEats → Wolt)
[ ] Krok 6: Analiza opłacalności
[ ] Krok 7: Integracja end-to-end
[ ] Krok 8: Dystrybucja APK
```

---

## ✅ Ukończone

- [x] **Plan architektury** (Opus 4.6) - 2026-02-24
- [x] **Setup lokalnego folderu projektu** - 2026-02-24
- [x] **RULES.md** - zasady współpracy + Git workflow - 2026-02-24
- [x] **README.md** - wprowadzenie dla nowych członków zespołu - 2026-02-24
- [x] **PLAN.md** - zaktualizowany o info dla nowych - 2026-02-24

---

## 🔄 W trakcie

**Krzysztof:** Gotowy do Kroku 0 (może używać Claude)
**Tata:** - (może używać AI lub pracować ręcznie)
**Łukasz:** - (może używać Copilot/Antigravity/inne lub ręcznie)

**Uwaga:** Podział zadań do ustalenia - sprawdzajcie tę sekcję przed pracą!

---

## 📋 Kolejne kroki

**Krok 0: Instalacja środowiska** ⚡ Sonnet
- Zainstalować Android Studio
- Zainstalować JDK 17
- Skonfigurować zmienne środowiskowe
- Podłączyć telefon Android
- Test: `adb devices`

---

## ⚠️ Problemy / Notatki

- **iCloud Drive issue**: Zdecydowano użyć lokalnego folderu `~/Desktop/CourierAssist/` zamiast iCloud Drive z powodu problemów z synchronizacją

---

## 📁 Struktura projektu

```
~/Desktop/CourierAssist/
├── PLAN.md               ✅ Pełny plan 8 kroków
├── PROGRESS.md           ✅ Ten plik - tracking statusu
├── RULES.md              ⏳ Zasady pracy (do stworzenia)
├── CourierAssist/        ⏳ Projekt Android Studio (Krok 1)
├── docs/                 ⏳ Dokumentacja techniczna
└── testing/              ⏳ Screenshoty testowe
    ├── glovo/
    ├── ubereats/
    └── wolt/
```

---

## 🎯 Następna akcja

**TERAZ**: Ustalić zasady pracy z Claude Code przed implementacją.

**POTEM**: Krok 0 - Instalacja środowiska (Sonnet 4.5)
