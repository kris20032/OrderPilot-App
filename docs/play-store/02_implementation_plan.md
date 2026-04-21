# 02 · ORDERPILOT — IMPLEMENTATION PLAN (PLAY STORE RELEASE v1)

## Status

- **Status:** DRAFT v1
- **Data:** 2026-04-19
- **Branch:** `play-store-prep`
- **Bazuje na:** `01_analysis_v2.md` (FINAL)
- **Reguła:** każdy task ma odnośnik do M*/F*/V*/KD*/H* z analizy. Bez nowych funkcji, bez nowych architektur.

---

## GOAL

Doprowadzić OrderPilot do **pierwszego skutecznego submission na Closed Testing track w Google Play Console** (z możliwością promocji do Production po 14 dniach + 12 testerów).

---

## CURRENT STATE

### Co już istnieje w kodzie (na podstawie analizy v2)

- Pełny manifest z permisjami i FGS types (H1, H2, `OrderPilot/app/src/main/AndroidManifest.xml:5-12,43-55`)
- AccessibilityService poprawnie zadeklarowany (H3, manifest:65-75)
- `accessibility_config.xml` z `canTakeScreenshot`, `canRetrieveWindowContent`, `notificationTimeout`, `description` (H4)
- `accessibility_service_description` w 4 językach PL/EN/UK/RU (H5, **za krótki — F1**)
- `SetupActivity` z OEM onboarding (Samsung/Xiaomi/Huawei/Oppo/OnePlus) (H6)
- Ikona A1 Arrow-Up Reticle (adaptive + themed + monochrome + webp) (H7)
- Splash screen (Android 12+ SplashScreen API) zweryfikowany (H8)
- **Zero network code** — brak INTERNET, brak Firebase/Analytics/Crashlytics/retrofit/okhttp (H9, KD5)
- Target SDK 35, min SDK 26, namespace, applicationId, backup rules XML (H10)

### Co działa end-to-end (potwierdzone produkcyjnie)

- Pipeline Uber/Wolt (API 30+): AccessibilityService → EventThrottler → takeScreenshot → OCR → Parser → Overlay
- Pipeline Glovo/Bolt: accessibility tree → Parser → Overlay
- Multi-overlay max 2 sloty z duplicate check
- Drag handle, persystowana pozycja Y, close button
- Watchdog + health-check z 30s grace period
- BootReceiver, MIUI fix (onTaskRemoved)
- 4 języki UI + parsery (PL/EN/UK/RU)

### Co jest niekompletne

- ASSUMPTION: `versionName="1.0"` (Play preferuje 3-segment, F8)
- ASSUMPTION: `allowBackup="true"` bez audytu zawartości backupu (F3)
- ASSUMPTION: brak audytu logów pod kątem user data (F6, V4)
- ASSUMPTION: save-logs feature może zawierać user data (F7, V5)
- ASSUMPTION: kod może używać `getInstalledPackages` bez `<queries>` (V2 → M16)
- Brak DisclosureActivity (M1, F2)
- Brak consent flag (M5)
- Brak in-app PP linku (M4)
- Brak signed AAB / keystore (M3)
- Brak Privacy Policy / Data Deletion URL (M2, M25)
- Brak store assets, video, listing copy (M7, M8, M9)
- Brak Dev Account (M19)

---

## BLOCKERS (P0 — MUST FIX BEFORE ANY SUBMIT)

Każdy z poniższych blokuje fizyczne złożenie wniosku. Kolejność niżej (Phases) opisuje **kiedy** zaadresować, nie priorytet — wszystkie są P0.

### B1 — Brak Prominent Disclosure Screen (M1, F2, KD4)

- **Co blokuje:** policy „Alternative use of accessibility" wymaga full-screen disclosure PRZED system dialogiem accessibility. Bez tego → instant reject + Risk #1 escalation.
- **Gdzie:** brak pliku — do utworzenia `ui/DisclosureActivity.kt` + `res/layout/activity_disclosure.xml`. Wpięcie w `MainActivity.kt:57` flow (przed redirect do SetupActivity).
- **Jak naprawić:** Phase 3, Task 3.1.

### B2 — Brak Privacy Policy + Data Deletion URL (M2, M25)

- **Co blokuje:** Play Console wymaga obu URL przed submitem (hard required field). Dodatkowo PP musi zawierać explicit ML Kit disclosure (V1).
- **Gdzie:** brak — do napisania + hostingu (GitHub Pages w `docs/legal/`, M29).
- **Jak naprawić:** Phase 4, Tasks 4.1-4.3.

### B3 — Brak signed AAB (M3, V13)

- **Co blokuje:** Play Console przyjmuje wyłącznie signed AAB (nie APK, nie unsigned).
- **Gdzie:** `OrderPilot/app/build.gradle.kts` brak `signingConfigs` block; brak keystore.
- **Jak naprawić:** Phase 1, Tasks 1.1-1.3.

### B4 — Brak Developer Account (M19, V9)

- **Co blokuje:** wszystko. Bez konta nie ma Console.
- **Gdzie:** play.google.com/console/signup
- **Jak naprawić:** Phase 0, Task 0.1.

### B5 — AccessibilityService description za krótki (F1, H5)

- **Co blokuje:** policy wymaga że description = pełne uzasadnienie pokazywane userowi w System Settings; spójne z disclosure (KD1, F9). Krótki opis = reject.
- **Gdzie:** `OrderPilot/app/src/main/res/values/strings.xml:3` + `values-en/uk/ru/strings.xml:3`.
- **Jak naprawić:** Phase 2, Task 2.4.

### B6 — Brak `<queries>` w manifeście (M16, V2)

- **Co blokuje:** Android 11+ wymaga `<queries>` jeśli kod sprawdza zainstalowane apki. Bez tego apka „nie widzi" Uber/Wolt/Glovo/Bolt → core feature broken po update target SDK ≥30. Również Play scan flaguje `QUERY_ALL_PACKAGES`.
- **Gdzie:** `OrderPilot/app/src/main/AndroidManifest.xml` — brak `<queries>` block.
- **Jak naprawić:** Phase 2, Task 2.1 (po V2).

### B7 — AD_ID permission auto-injected (M20)

- **Co blokuje:** Android 13+ auto-dodaje `com.google.android.gms.permission.AD_ID`. Brak explicit removal = Data Safety mismatch (Google traktuje to jako „collects AdID").
- **Gdzie:** `OrderPilot/app/src/main/AndroidManifest.xml`.
- **Jak naprawić:** Phase 2, Task 2.2.

### B8 — Closed Testing minimum nie spełniony (M15, V10, KD6, Risk #6)

- **Co blokuje:** nowe Dev Accounts wymagają 12-20 testerów × 14 dni przed Production. Przy submit do Closed Testing samym sobie to nie blokuje, ale **promocja do Prod jest zablokowana** dopóki nie spełnione.
- **Gdzie:** organizacja zewnętrzna — Phase 6.
- **Jak naprawić:** Phase 0, Task 0.3 (rozpocząć rekrutację) + Phase 6.

### B9 — Brak Permissions Declarations (M6)

- **Co blokuje:** 4 sensitive permissions (Accessibility, MediaProjection, SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE_SPECIAL_USE) wymagają osobnych deklaracji w Console. Bez nich → reject.
- **Gdzie:** Play Console > App content > Permissions declaration.
- **Jak naprawić:** Phase 4, Task 4.5 (treść) + Phase 7 (wprowadzenie do Console).

### B10 — Brak Data Safety form (M10)

- **Co blokuje:** hard required przed submitem. Mismatch z kodem = Risk #5.
- **Gdzie:** Play Console > App content > Data safety.
- **Jak naprawić:** Phase 4, Task 4.4 + Phase 7.

### B11 — Brak store listing assets (M7, M8, M9, M23, M24)

- **Co blokuje:** ikona 512×512, feature graphic 1024×500, min 2 screenshoty, short + long description, video są required.
- **Gdzie:** brak.
- **Jak naprawić:** Phase 5.

### B12 — Brak Content Rating + Target Audience (M11, M12, M14)

- **Co blokuje:** required fields w Console.
- **Jak naprawić:** Phase 7.

### B13 — Logcat / Save logs mogą zawierać user data (F6, F7, V4, V5, Risk #5)

- **Co blokuje:** jeśli loguje offer text / OCR output → Data Safety „no data collected" jest kłamstwem → reject + risk account-level action.
- **Gdzie:** wszystkie call sites `Log.*` / `AppLog.*`; implementacja `btn_save_logs`.
- **Jak naprawić:** Phase 2, Tasks 2.7-2.8.

---

## IMPLEMENTATION PHASES (ORDERED EXECUTION)

Numeracja Phase/Task liniowa — wykonywać top-down. Phase'y mogą się częściowo nakładać tylko gdy explicitly oznaczone „PARALLEL OK".

---

### Phase 0 — Pre-work (organizacja, blokuje wszystko)

**Cel:** odblokować możliwość fizycznego submitu i startu 14-day timera.

#### Task 0.1 — Założyć Google Play Developer Account
- **Akcja:** play.google.com/console/signup; $25 jednorazowo; identity verification (paszport/dowód).
- **Decyzja przed:** indywidualne vs firmowe (V9). ASSUMPTION: indywidualne (szybsze, mniej dokumentów).
- **Konsekwencja decyzji:** indywidualne = Twoje imię publicznie widoczne jako developer name (Google policy od 2023).
- **Ref:** M19, V9.
- **Definition of done:** dostęp do Play Console, tab „Create app" widoczny.

#### Task 0.2 — Weryfikacja nazwy + package
- **Akcja:** play.google.com/store search „OrderPilot"; UPRP/EUIPO trademark search; próba `Create app` z `com.orderpilot.app` w Console (potwierdza V8).
- **Ref:** V7, V8, V22, M31.
- **Definition of done:** apka utworzona w Console, package potwierdzony wolny, brak konfliktu trademark.
- **Plan B jeśli konflikt:** alternatywne nazwy do wyboru — zapisać 3 zapasowe.

#### Task 0.3 — Rozpoczęcie rekrutacji testerów (PARALLEL z całą resztą)
- **Akcja:** komunikacja z tatą + 3-5 obecnych testerów; lista zapasowa przez lokalne grupy FB kurierskie; cel 20 (12 minimum + 8 zapasu, V10/V16).
- **Wymóg:** każdy tester musi mieć Google account bez restriction sub/family (V17).
- **Ref:** M15, V10, V16, V17, KD6, Risk #6.
- **Definition of done:** spreadsheet z 20 emailami, każdy potwierdził udział.
- **WAŻNE:** ta task musi startować już teraz, bo jej deliverable jest potrzebny dopiero w Phase 6, ale rekrutacja zajmuje czas.

#### Task 0.4 — Decyzja Play App Signing vs self-managed
- **Akcja:** decyzja przed Phase 1.
- **ASSUMPTION:** Play App Signing (Google trzyma signing key, my upload key — wymienialny).
- **Ref:** V13, M3.
- **Definition of done:** zapisana decyzja w `docs/play-store/decisions.md` (nowy plik, jeden-liner).

---

### Phase 1 — Build & Signing

**Cel:** generować signed AAB lokalnie, voidproof keystore.

#### Task 1.1 — Wygenerowanie upload keystore ✅ WYKONANE 2026-04-21
- **Zrobione:** `keytool -genkeypair` przez Android Studio JDK. Keystore w `OrderPilot/keystore/orderpilot-release.jks` (folder w `.gitignore`), alias `orderpilot`, RSA 2048, SHA384withRSA, ważny do 2053-09-05.
- **Odstępstwo od planu:** lokalizacja `OrderPilot/keystore/` (w repo path, ale gitignored) zamiast `~/keystores/` — prościej dla gradle path resolution.

#### Task 1.2 — Backup keystore (CRITICAL) ⚠️ CZĘŚCIOWO WYKONANE 2026-04-21
- **Zrobione:** lokalna kopia w `~/Documents/OrderPilot-Keystore-Backup/` (keystore + properties + README z instrukcjami i SHA256).
- **TODO USER:** upload do chmury (iCloud / Google Drive / Dropbox) + pendrive. Hasło zapamiętane.
- **Ref:** M30, V12.

#### Task 1.3 — `signingConfigs` w gradle ✅ WYKONANE 2026-04-21
- **Zrobione:** `OrderPilot/app/build.gradle.kts` + `keystore.properties` (gitignored) + `keystore.properties.template` (w git). Commity `2777495` + lint fix `ad6edaa` (FullBackupContent).
- **Odstępstwo od planu:** credentials w `keystore.properties` (per-project) zamiast `~/.gradle/gradle.properties` (global) — lepsze izolowanie projektu, prostszy setup dla future-me.

#### Task 1.4 — Bump versionName
- **Plik:** `OrderPilot/app/build.gradle.kts:15`.
- **Co zmienić:** `versionName = "1.0.0"` (z `"1.0"`).
- **Ref:** F8, M32.
- **Reason:** Play standard 3-segment; ułatwia future bumps.

#### Task 1.5 — Decyzja R8/ProGuard
- **ASSUMPTION:** zostawić `isMinifyEnabled = false` na v1.0 (większy AAB ~kilka MB więcej, NIE blocker; włączenie wymaga reguł dla MLKit + kotlinx-serialization + ViewBinding co dodaje ryzyko regression).
- **Ref:** M26.
- **Risk if missing:** brak — non-blocker.
- **Decision criterion:** włączyć tylko jeśli AAB > 50 MB lub Console flaguje size.

#### Task 1.6 — Test build AAB lokalnie ✅ WYKONANE 2026-04-21
- **Zrobione:** Generate Signed App Bundle w Android Studio → `OrderPilot/app/release/app-release.aab` (23 MB). Build successful po fix lint `FullBackupContent` (commit `ad6edaa`).
- **Path:** w praktyce plik wylądował w `app/release/` nie `app/build/outputs/bundle/release/`.

#### Task 1.7 — Test instalacji AAB przez bundletool
- **Akcja użytkownika:** `bundletool build-apks --bundle=...aab --output=...apks --mode=universal --ks=...jks --ks-key-alias=...`; `bundletool install-apks --apks=...apks` na real device.
- **Walidacja:** fresh install flow działa (apka się uruchamia, accessibility się binduje).
- **Ref:** Definition of Ready „bundletool fresh install flow".

#### Task 1.8 — Dependency compat check pod targetSDK 35
- **Akcja:** `./gradlew dependencies` + manualna lista; weryfikacja że `androidx.work`, `core-splashscreen`, `mlkit:text-recognition`, `material` są API 35-aware.
- **Ref:** V15.
- **Risk if missing:** runtime crash na Android 14/15 device.

---

### Phase 2 — Android Permissions & Manifest

**Cel:** manifest 100% Play-compliant; accessibility config zawężony; logi czyste; build-time guard przeciwko AdID.

PARALLEL OK z Phase 1 (różne pliki).

#### Task 2.1 — Dodać `<queries>` block (V2 — WYKONANE 2026-04-19)
- **Krok A (V2 result):** Grep `getInstalledPackages|queryIntentActivities|resolveActivity` w `OrderPilot/` zwrócił **ZERO matches**. Kod NIE używa PackageManager API do wykrywania apek; detection idzie wyłącznie przez `AccessibilityEvent.packageName`.
- **Decyzja:** `<queries>` block formalnie ZBĘDNY (M16 nie aplikuje się do current code). Defensywnie dodać i tak — koszt zerowy, mitygacja jeśli przyszły kod doda PackageManager call.
- **Krok B (defensive):** dodać do `OrderPilot/app/src/main/AndroidManifest.xml` przed `<application>` **finalną listę z Task 2.0** (patrz niżej):
  ```xml
  <queries>
      <package android:name="com.ubercab.driver" />
      <package android:name="com.ubercab.eats" />
      <package android:name="com.wolt.courierapp" />
      <package android:name="com.glovo.courier" />
      <package android:name="com.logistics.rider.glovo" />
      <package android:name="com.bolt.deliverycourier" />
      <package android:name="com.bolt.courier" />
      <package android:name="com.bolt.food.courier" />
  </queries>
  ```
- **Reason:** package visibility post Android 11 + future-proofing.
- **Ref:** M16, V2 (NEGATIVE — defensive include).

#### Task 2.2 — Usunąć AD_ID permission
- **Plik:** `OrderPilot/app/src/main/AndroidManifest.xml`.
- **Co dodać:** `<uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove"/>` w sekcji uses-permission.
- **Reason:** Android 13+ auto-injection; bez removal Data Safety = mismatch.
- **Ref:** M20, Risk #5.
- **Risk if missing:** Data Safety reject.

#### Task 2.3 — Decyzja `allowBackup` (F3, V20 — WYKONANE 2026-04-20)
- **Decyzja:** `allowBackup="true"` zostawić, ale z dedykowanymi regułami include/exclude.
- **Rationale (V20 audit):**
  - `order_pilot_settings.xml` (current SharedPrefs) — zawiera tylko user preferences (thresholds, theme, language, Y position, monitoring_state flag). Monitoring_state jest nieszkodliwy: bez systemowych grantów (accessibility, overlay, battery) wizard i tak startuje od zera na nowym urządzeniu. → **include**.
  - `order_pilot_disclosure.xml` (Phase 3 dedykowany plik) — zarezerwowany na `disclosureAcceptedVersion` i inne consent flagi. KD5 Play Store wymaga świeżego accept disclosure po restore na nowym urządzeniu. → **exclude**.
- **Implementacja:**
  - `backup_rules.xml` (API 23+): `<full-backup-content>` z `<include>` settings + `<exclude>` disclosure
  - `data_extraction_rules.xml` (API 31+): `<cloud-backup>` + `<device-transfer>` z tą samą parą reguł
- **Pliki:** `res/xml/backup_rules.xml`, `res/xml/data_extraction_rules.xml`
- **Ref:** F3, V20, KD5.

#### Task 2.4 — Rozszerzyć `accessibility_service_description` (F1, F4)
- **Pliki:** `res/values/strings.xml:3`, `res/values-en/strings.xml:3`, `res/values-uk/strings.xml:3`, `res/values-ru/strings.xml:3`.
- **Wording (PL, źródłowy):**
  > „OrderPilot odczytuje zawartość ekranu aplikacji kurierskich (Uber Driver, Wolt Courier, Glovo Courier, Bolt Food), aby wykryć nowe zlecenia i wyświetlić na nakładce wyliczoną stawkę godzinową (zł/h). Dane z ekranu są analizowane wyłącznie na Twoim urządzeniu i nie są nigdzie wysyłane ani zapisywane. Możesz wyłączyć usługę w dowolnym momencie."
- **Wording EN/UK/RU:** ekwiwalenty z zachowaniem tych samych 4 elementów: (1) co czyta, (2) z których apek, (3) po co (zł/h overlay), (4) zero network + cancel anytime.
- **Reason:** policy wymóg (pokazywane w System Settings przed accept). Spójność z F9 + Permissions Declaration + Disclosure.
- **Ref:** F1, F4, KD1, Risk #1.
- **Risk if missing:** policy reject (vague description).

#### Task 2.0 — Source-of-truth: package names (KOREKTA 3, V3 — WYKONANE 2026-04-19)

**Wykonano grep `com.(ubercab|wolt|glovo|bolt)` + `supportedPackages` + `courierPackages` w `OrderPilot/`. Znaleziono ROZBIEŻNOŚCI z planem v1 + między modułami w samym kodzie.**

**Finalna lista package names (superset z runtime kodu — single source of truth):**

| # | Platform | Package name | Źródło w kodzie |
|---|----------|--------------|-----------------|
| 1 | Uber Driver | `com.ubercab.driver` | `UberParser.kt:13`, `UberOcrParser.kt:10`, `OrderPilotAccessibilityService.kt:660` (courierPackages) |
| 2 | Uber Eats Courier | `com.ubercab.eats` | `UberOcrParser.kt:10` ⚠️ **NIE w courierPackages** |
| 3 | Wolt Courier | `com.wolt.courierapp` | `WoltOcrParser.kt:10`, `OrderPilotAccessibilityService.kt:661` (courierPackages) |
| 4 | Glovo Courier (current PL) | `com.logistics.rider.glovo` | `GlovoOcrParser.kt:10`, `OrderPilotAccessibilityService.kt:662` (courierPackages) |
| 5 | Glovo Courier (legacy) | `com.glovo.courier` | `GlovoOcrParser.kt:10` ⚠️ **NIE w courierPackages** |
| 6 | Bolt Food (potwierdzone prod 03-22) | `com.bolt.deliverycourier` | `BoltFoodOcrParser.kt:11`, `OrderPilotAccessibilityService.kt:663` (courierPackages) |
| 7 | Bolt Courier (alt) | `com.bolt.courier` | `BoltFoodOcrParser.kt:12` ⚠️ **NIE w courierPackages** |
| 8 | Bolt Food Courier (alt) | `com.bolt.food.courier` | `BoltFoodOcrParser.kt:13` ⚠️ **NIE w courierPackages** |

**Rozbieżności z planem v1 (do FIX):**
- ❌ Plan miał `com.wolt.courier` → poprawnie **`com.wolt.courierapp`**
- ❌ Plan miał tylko `com.glovo.courier` → kod ma 2 warianty
- ❌ Plan miał tylko `com.bolt.deliverycourier` → kod ma 3 warianty
- ❌ Plan brakował `com.ubercab.eats`

**Wewnętrzna niespójność w samym kodzie (do FIX, P0 dla Play review):**
- `ParserRegistry` (przez `supportedPackages`) akceptuje **8 packages**.
- `courierPackages` w `OrderPilotAccessibilityService.kt:659-664` ma tylko **4 packages** (subset).
- Rezultat: jeśli user ma alternatywny pakiet (np. `com.bolt.courier`), parser by go obsłużył ALE foreground guard w AccService go odrzuci → broken detection.
- **Fix:** Task 2.0a poniżej.

#### Task 2.0a — Usunąć duplikat `courierPackages`, użyć `watchedPackages` (CODE FIX, MINIMAL)

**Decyzja architektoniczna:** SYNC z `ParserRegistry`. Wzorzec już istnieje w tym samym pliku — `watchedPackages` (AccService.kt:644-645) deleguje do `ServiceLocator.parserRegistry.getAllWatchedPackages()`. `courierPackages` to dead-code duplicate (subset z 4 packages zamiast 8 z registry).

**Plik:** `OrderPilot/app/src/main/java/com/orderpilot/app/service/OrderPilotAccessibilityService.kt`

**Zmiany (3 lokalizacje, 1 commit):**
1. **Linia 280** (`isRivalInForeground`): `activePackage in courierPackages` → `activePackage in watchedPackages`
2. **Linia 326** (`isRivalCourierInForeground`): `activePackage in courierPackages` → `activePackage in watchedPackages`
3. **Linie 658-664**: usunąć cały blok (komentarz + `private val courierPackages = setOf(...)`).

**Reason:** single source of truth = `ParserRegistry` (construct-once w `ServiceLocator.kt:37`). Dodanie kolejnego parsera w przyszłości = automatyczna propagacja do all guards.

**Side effect (pozytywny):** cross-contamination guard zacznie poprawnie filtrować również `com.bolt.courier`, `com.bolt.food.courier`, `com.glovo.courier`, `com.ubercab.eats` (obecnie ignorowane przez guard).

**Risk if missing:** user z alternatywnym pakietem nie dostanie overlay → user-facing bug + potencjalna negatywna recenzja Play Store. Plus rozbieżność deklaracji `accessibility_config packageNames` (Task 2.5, 8 packages) z runtime guardem (4 packages) = wewnętrzna niespójność widoczna dla audytora.

**Brak race/lateinit risk:** `watchedPackages` jest już używane w hot path (linia 74, każdy event). Gdyby ServiceLocator nie był gotowy = wszystko by crashowało.

**Ref:** V3 finding, ParserRegistry.kt:8-9, AccService.kt:644-645.

#### Task 2.5 — Dodać `packageNames` constraint w accessibility_config (F5, M28)
- **Plik:** `OrderPilot/app/src/main/res/xml/accessibility_config.xml`.
- **Krok A:** użyć finalnej listy z Task 2.0 (8 packages).
- **Krok B:** dodać atrybut:
  ```
  android:packageNames="com.ubercab.driver,com.ubercab.eats,com.wolt.courierapp,com.glovo.courier,com.logistics.rider.glovo,com.bolt.deliverycourier,com.bolt.courier,com.bolt.food.courier"
  ```
- **Reason:** mniejsza powierzchnia ataku → łatwiej obronić w review; spójna z `ParserRegistry`.
- **Risk if missing:** reviewer może argumentować że apka „spies on all apps".
- **Ref:** F5, M28, V3.

#### Task 2.6 — Audit `accessibilityFlags` (F12)
- **Plik:** `OrderPilot/app/src/main/res/xml/accessibility_config.xml:5`.
- **Co sprawdzić:** czy `flagRetrieveInteractiveWindows` jest realnie używany (grep `getWindows()` / `flagRetrieveInteractiveWindows` w kodzie).
- **Decision:** jeśli nie używany → usunąć (mniej sensitive flags = mniejsza scrutiny). Jeśli używany (np. dla Uber popup detection) → zostawić + udokumentować w Permissions Declaration dlaczego.
- **Ref:** F12.

#### Task 2.7 — Logcat audit (F6, V4 — WYKONANE 2026-04-20)
- **Znaleziono 2 kategorie leakage:**
  - **Cat 1 — pełny dump OCR:** `BoltFoodOcrParser` / `GlovoOcrParser` / `WoltOcrParser` logowały `"OCR: $text"` przy wejściu; `OcrEngine` logował każdą linię w forEachIndexed.
  - **Cat 2 — częściowy dump (`text.take(200)`):** 3 fallback site w każdym z 4 parserów OCR + 3 site w `OrderPilotAccessibilityService` (tree text, screenshot OCR preview, Uber windows).
- **Naprawa:** wszystkie leaki zastąpione metadanymi (`textLen=${text.length}`, `lines=${ocrLines.size}`, `retry=$retryIndex`). Liczbowe logi (kwoty, dystanse, czasy, offer summary) zostają — `Offer` data class ma tylko pola numeric/enum/boolean (bez textual content).
- **Weryfikacja:** `grep -E 'AppLog\.[dwe].*\$(text|line|preview)'` = zero wyników; `grep 'text\.take\('` = zero wyników.
- **Ref:** F6, V4, Risk #5.

#### Task 2.8 — Save logs feature audit (F7, V5 — WYKONANE 2026-04-20)
- **Lokalizacja:** `MainActivity.saveLogs()` → pisze `AppLog.getBufferedLogs()` do pliku.
- **Wynik audytu:** buffer jest populated wyłącznie przez `AppLog.d/w/e` call-sites, które zostały wyczyszczone w ramach Task 2.7. Czyli save-logs automatycznie dziedziczy czysty stan — **żadnych dodatkowych zmian nie trzeba**.
- **Ref:** F7, V5, Risk #5; pokrewne: Task 2.7.

#### Task 2.9 — Spójność wordingu `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` (F9)
- **Plik:** `OrderPilot/app/src/main/AndroidManifest.xml:52-54`.
- **Akcja:** wording w manifeście MUSI być dosłownie ten sam co w Permissions Declaration dla FOREGROUND_SERVICE_SPECIAL_USE (Phase 4, Task 4.5d).
- **ASSUMPTION current text:** „Keeps order monitoring process alive to prevent OEM battery optimization from killing the accessibility service" — można zostawić, ale skopiować dosłownie do Permissions Declaration.
- **Ref:** F9, KD6.
- **Risk if missing:** mismatch flagowany przez review.

#### Task 2.10 — `ScreenCaptureService` foreground guard (F11, V6 — WYKONANE 2026-04-20)
- **Znaleziono:** `capture()` nie miał własnego filtra per-pakiet — polegał wyłącznie na tym, że `PipelineOrchestrator` jest jedynym callerem i już filtruje. OK w praktyce, ale MediaProjection token sam w sobie = capture dowolnego ekranu.
- **Naprawa (defensive, V6):** `ScreenCaptureService.capture(packageName: String)` — nowy argument. Funkcja odmawia i zwraca `null` jeśli `packageName !in ServiceLocator.parserRegistry.getAllWatchedPackages()`. `PipelineOrchestrator.processInternal` przekazuje `packageName` (już dostępny z event callbacka).
- **Single source of truth:** `ParserRegistry.getAllWatchedPackages()` — ten sam zestaw co `watchedPackages` w AccService (Task 2.0a).
- **Ref:** F11, V6, KD2, Risk #2.

#### Task 2.11 — Decyzja `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` (V11, Risk #7)
- **Status (2026-04-19):** **ZREALIZOWANE jako Plan B** — permission usunięty w Batch 1 (preemptywnie pod Play policy, nie czekamy na reject).
- **Decision tree (historyczny):**
  - ~~Plan A (prefer): zostawić permission, uzasadnić w Permissions Declaration „accessibility service requires continuous operation for core functionality".~~
  - **Plan B (wybrany):** usunąć permission + zaakceptować gorszą reliability na Xiaomi/Huawei (kompensacja przez manualny flow w Setup wizard).
- **⚠️ Regresja (2026-04-19):** Usunięcie permission złamało Setup wizard — `Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` (SetupActivity.kt:337) nie pokazuje dialogu i **nie rzuca wyjątku** → fallback w try/catch się nie aktywuje → `batteryOk=false` → Continue szary → **hard block setupu**.
- **Decyzja fixa (2026-04-20, Opcja A):** w `SetupActivity.requestBatteryOptimizationExemption()` usunąć `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` i od razu wołać `Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)` (lista wszystkich apek) + pokazać `Toast` z hintem „Znajdź OrderPilot na liście i wybierz 'Bez ograniczeń'" (i18n PL/EN/UK/RU). Battery pozostaje hard requirement w `isSetupComplete()` — user dokończy ręcznie.
- **Status fixa:** ✅ ZAIMPLEMENTOWANE (2026-04-20). Pełne uzasadnienie (dlaczego A, nie B-soft-requirement ani C-revert) w `docs/future_polish_fixes.md` #27.
- **Ref:** V11, Risk #7; `docs/future_polish_fixes.md` #27.

---

### Phase 3 — Core UX Compliance (Disclosure)

**Cel:** disclosure flow zgodny z KD4; consent persistowany; wpiety przed accessibility grant.

#### Task 3.1 — Implementacja `DisclosureActivity` (M1, F2, KD4 — WYKONANE 2026-04-20)
- **Utworzone:**
  - `OrderPilot/app/src/main/java/com/orderpilot/app/ui/DisclosureActivity.kt`
  - `OrderPilot/app/src/main/res/layout/activity_disclosure.xml`
- **Manifest:** `<activity android:name=".ui.DisclosureActivity" android:exported="false" />` dodany przed SetupActivity.
- **Layout:** NestedScrollView + MaterialCardView z body (~350 słów, 8 elementów KD4: identyfikacja w 1. zdaniu, lista 5 target apek, 4 punkty danych czytanych, cel zł/h, 4× NIE-robione (no server / no disk / no share / no ads), retention ~30s, cancel anytime, PP link). Oba przyciski filled primary tej samej klasy — zero dark pattern.
- **Logika:** Accept → `DisclosureRepository.markAccepted()` → MainActivity → `finish()`. Cancel / back button → `finishAffinity()`.
- **Ref:** M1, F2, KD4, Risk #1.

#### Task 3.2 — Consent flag (M5 — WYKONANE 2026-04-20)
- **Decyzja architektoniczna:** zamiast rozszerzać `SharedPrefsSettingsRepository` (który używa `order_pilot_settings.xml` — plik w auto-backup), dedykowany `DisclosureRepository` używa **osobnego SharedPrefs** `order_pilot_disclosure.xml`.
- **Rationale:** backup_rules.xml (Task 2.3) **exclude-uje** `order_pilot_disclosure.xml` — KD5 wymaga świeżego consent po restore. Gdyby flag był w tym samym pliku co user settings, trzeba by było albo exclude-ować cały plik ustawień (tracimy backup preferencji), albo robić custom `BackupAgent`. Osobny plik = najprostszy split.
- **API:** `DisclosureRepository.isAccepted()`, `markAccepted()`, `acceptedVersion()`, `CURRENT_DISCLOSURE_VERSION = 1`. Zarejestrowany w `ServiceLocator.disclosureRepository`.
- **Ref:** M5, KD4, KD5 (backup split).

#### Task 3.3 — Wpięcie Disclosure w start flow (F2 — WYKONANE 2026-04-20)
- **MainActivity.onCreate** (po `installSplashScreen()` + `super.onCreate`): gate na `!disclosureRepository.isAccepted()` → redirect do `DisclosureActivity`. Gate jest PRZED `SetupActivity.isSetupComplete(this)` check — zgodnie z KD4 element 8 (disclosure przed grant).
- **Flow:** cold start fresh install → splash → MainActivity (gate) → DisclosureActivity → Accept → MainActivity (gate passes) → SetupActivity (setup not complete).
- **Ref:** F2, KD4, V14.

#### Task 3.4 — Stringi disclosure (M13 — WYKONANE 2026-04-20)
- **Stringi dodane w 4 locales:** PL (`values/`), EN (`values-en/`), UK (`values-uk/`), RU (`values-ru/`).
- **Klucze:** `disclosure_title`, `disclosure_subtitle`, `disclosure_body`, `disclosure_button_accept`, `disclosure_button_cancel`, `disclosure_pp_link`, `toast_pp_error`.
- **Body zawiera 8 elementów KD4:** identyfikacja w 1. zdaniu, lista 5 target apek, 4 punkty danych (kwota/czas/km/cash), cel (zł/h overlay), 4× NIE-robione, retention ~30s, cancel anytime, PP link pointer.
- **Const PP URL:** `OrderPilotApp.PRIVACY_POLICY_URL` placeholder (`https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html`) — TODO Phase 4 Task 4.3 po realnym hostingu.
- **Ref:** M13, KD4.

#### Task 3.5 — Manualny test flow (V14)
- **Akcja użytkownika:** fresh install na Android Studio device. Verify: splash → DisclosureActivity (NIE SetupActivity) → klick „Anuluj" zamyka apkę / klick „Rozumiem" → SetupActivity → grant → MainActivity.
- **Ref:** V14, Definition of Ready.

#### Task 3.6 — In-app PP link w SettingsActivity (M4) ✅ WYKONANE 2026-04-20
- **Plik:** `OrderPilot/app/src/main/java/com/orderpilot/app/ui/SettingsActivity.kt` + odpowiedni layout.
- **Co dodać:** sekcja „Polityka prywatności" / „Privacy policy" → click → `Intent(ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))`.
- **Reason:** policy wymaga że PP jest dostępny z apki po grant (M4).
- **Ref:** M4, KD5.
- **Implementacja:** dodana karta „O aplikacji" w `activity_settings.xml` między Language card a save button; klikalny TextView `tv_pp_link` → `openPrivacyPolicy()` w `SettingsActivity.kt` (Intent ACTION_VIEW + fallback toast `toast_pp_error`). URL = `OrderPilotApp.PRIVACY_POLICY_URL` (placeholder do Phase 4 Task 4.3).

#### Task 3.7 — Settings → About z disclaimers (M21, M22) ✅ WYKONANE 2026-04-20
- **Plik:** `OrderPilot/app/src/main/java/com/orderpilot/app/ui/SettingsActivity.kt` (lub nowa sekcja About).
- **Co dodać:**
  - String `about_non_affiliation`: „OrderPilot is not affiliated with, endorsed by, or sponsored by Uber, Wolt, Glovo, or Bolt." + PL equivalent
  - String `about_financial_disclaimer`: „Earnings estimates shown by OrderPilot are computational only and do not constitute financial advice." + PL equivalent
- **Reason:** Risk #3 (trademark) + Risk #4 (deceptive) mitigation.
- **Ref:** M21, M22, Risk #3, Risk #4.
- **Implementacja:** stringi `settings_about_header`, `settings_pp_link`, `about_non_affiliation`, `about_financial_disclaimer` dodane w 4 locale (values, values-en, values-uk, values-ru). Disclaimery wyświetlone w tej samej karcie co PP link (textSize 12sp, on_surface_variant).

#### Task 3.8 — Wording reframing „monitoring" (F10, P1) ✅ WYKONANE 2026-04-20
- **Pliki:** `res/values*/strings.xml` — `hint_notifications_disabled`, `notif_monitoring_*`, `toast_*`.
- **Co zmienić:** „monitoring" / „monitorowanie" → „offer detection" / „wykrywanie zleceń" / „active" / „aktywny".
- **Reason:** „monitoring" niesie konotację surveillance (Risk #4 mitigation).
- **Ref:** F10, Risk #4.
- **Priority:** P1, ale low-effort — robić w tej fazie.
- **Implementacja:** w 4 locale zmienione user-facing stringi: `status_subtitle_running`, `toast_notification_denied`, `notif_monitoring_title`, `notif_watchdog_title`, `notif_watchdog_text`, `hint_notifications_disabled`. PL: „Wykrywanie zleceń"; EN: „Offer detection"; UK: „Виявлення замовлень"; RU: „Обнаружение заказов". Dodatkowo hardcoded kanał notyfikacji watchdoga w `ServiceWatchdog.kt` („Monitoring zatrzymany" → „Wykrywanie zleceń zatrzymane"). Wewnętrzne klasy (`MonitoringController`, `MonitoringForegroundService`) NIE zmieniane — to technical names, nie user-facing.

---

### Phase 4 — Data Safety & Privacy

**Cel:** kompletna treść Privacy Policy + Data Deletion + Permissions Declarations + Data Safety mapping. Hosting URL.

#### Task 4.1 — Treść Privacy Policy (M2, KD5)
- **Plik:** `docs/legal/privacy-policy.md` (nowy).
- **Struktura (M2):**
  1. Introduction + contact email
  2. What data is accessed (screen content of 4 named apps, OCR processing, accessibility events)
  3. How data is used (in-memory analysis → zł/h calculation → overlay display)
  4. What is NOT collected (explicit list: no personal info, no location, no contacts, no device IDs, no account info, no analytics events sent off-device)
  5. Data retention (max ~30s in memory, then GC; nothing persisted)
  6. Third-party SDKs: **ML Kit Text Recognition (on-device, bundled, no network)** + AndroidX libraries + kotlinx-serialization (offline-only)
  7. Children's privacy: 18+ only, app not directed at minors, no COPPA scope
  8. User rights (GDPR): nothing to access/modify/delete because nothing collected; uninstall = full removal
  9. Changes to this policy
  10. Effective date
  11. Contact email
- **Languages:** EN (canonical) + PL (translation).
- **Ref:** M2, V1, KD5, Risk #5.

#### Task 4.2 — Treść Data Deletion Policy (M25)
- **Plik:** `docs/legal/data-deletion.md` (nowy).
- **Treść:** krótka strona — „OrderPilot does not store any user data on external servers. To delete all local data, uninstall the application via your device settings. No further action is required from you or from us."
- **Ref:** M25.

#### Task 4.3 — Hosting (M2, M25, M29)
- **Decyzja:** GitHub Pages z `docs/` w repo (ASSUMPTION KD5 / M29).
- **Akcja:**
  - Włączyć GitHub Pages w settings repo (source = `main` / `/docs`)
  - URL: `https://<gh-user>.github.io/OrderPilot/legal/privacy-policy.html` + `/legal/data-deletion.html`
  - ASSUMPTION: konwersja MD → HTML przez Jekyll auto-build (default)
- **Ref:** M2, M25, M29.
- **Definition of done:** oba URL żyją + zwracają 200, treść widoczna.

#### Task 4.4 — Wypełnienie Data Safety form (M10, V19, Risk #5)
- **Lokacja:** Play Console > App content > Data safety.
- **Mapping:**
  - Data collection: **No data collected** (każda kategoria: Personal info, Financial, Health, Messages, Photos, Audio files, Files & docs, Calendar, Contacts, App activity, Web browsing, App info & performance, Device or other IDs)
  - Data sharing: **No data shared**
  - Security practices:
    - Data encrypted in transit: N/A (no transit)
    - Users can request data deletion: YES → link Data Deletion URL
    - Independent security review: NO
  - Data types collected by Google automatically: explicit acknowledgment Android Vitals (crash, ANR, battery — V19) — w Console jest osobne pole na to
  - Third-party SDKs: ML Kit Text Recognition (bundled, on-device, no data sent)
- **Reason:** mismatch z kodem = reject (Risk #5).
- **Ref:** M10, V19, Risk #5.

#### Task 4.5 — Treść 4 Permissions Declarations (M6, F9)
- **Plik:** `docs/play-store/permissions-declarations.md` (nowy, źródło dla copy-paste do Console).
- **Każda deklaracja zawiera (a) co, (b) po co, (c) co jeśli user odmówi, (d) czemu alternatywne API nie wystarczają:**

  **4.5a — AccessibilityService:**
  - Wording oparty na narracji KD1.
  - Explicit: React Native (Uber/Wolt) blokuje dostęp przez tree → screenshot fallback
  - Explicit: Glovo/Bolt eksponują tekst → tree read (battery-friendly)
  - Cancel = core feature niedostępne; user może uninstall
  - Alternative API: brak — Uber/Wolt nie publikują driver-side public API
  - Ref: M6, KD1, Risk #1.

  **4.5b — MediaProjection:**
  - Wording oparty na KD2.
  - Explicit: single screenshot per offer event, foreground-checked (tylko 4 target apps), discarded immediately, never stored, never displayed
  - Why not alternative: accessibility tree pusty dla React Native UI
  - Cancel = Uber/Wolt support disabled; Glovo/Bolt nadal działają
  - Ref: M6, KD2, Risk #2.

  **4.5c — SYSTEM_ALERT_WINDOW:**
  - Wording oparty na KD3.
  - Explicit: 60dp height bar, OrderPilot branded, non-interactive z underlying app, dismissible
  - Cancel = brak zł/h overlay; user może otwierać apkę dla danych (drastycznie gorszy UX)
  - Ref: M6, KD3, Risk #4.

  **4.5d — FOREGROUND_SERVICE_SPECIAL_USE:**
  - Wording **DOSŁOWNIE TEN SAM** co `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` w manifeście (F9 spójność).
  - Explicit subtype: keep accessibility service alive against OEM battery optimization
  - Why not other FGS types: nie dataSync, nie location, nie phoneCall, nie connectedDevice, nie health → najlepiej fit specialUse
  - Ref: M6, F9.

- **Risk if missing:** wszystkie 4 permissions = reject (M6).

#### Task 4.6 — Decyzja `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` declaration (V11, Risk #7)
- **Akcja:** dodać do `permissions-declarations.md` 5. sekcję jeśli zostawiamy permission. Jeśli reject → drop permission + Plan B (Task 2.11 Plan B).
- **Ref:** V11, Risk #7.

---

### Phase 5 — Store Assets

**Cel:** wszystkie assets visible na liście Play Store + video demo.

PARALLEL OK z Phase 4 (różne deliverables).

#### Task 5.1 — Ikona 512×512 (M8)
- **Akcja:** export z istniejącego adaptive icon vector (A1 Arrow-Up Reticle, H7) w 512×512 PNG (no transparency, no rounded corners — Play sam dodaje).
- **Lokacja:** `docs/play-store/assets/icon-512.png`.
- **Ref:** M8, H7.

#### Task 5.2 — Feature graphic 1024×500 (M8)
- **Akcja:** prosty graphic z logo A1 + tagline „PLN/h overlay for delivery couriers" / „Stawka zł/h dla kurierów". Czarny background. Brak logo platform konkurencji (Risk #3).
- **Lokacja:** `docs/play-store/assets/feature-graphic-1024x500.png`.
- **Ref:** M8, M23, Risk #3.

#### Task 5.3 — Screenshoty (M8, M23)
- **Min 4-6 per locale (PL + EN), exact screens:**
  1. Home (apka aktywna, status „aktywne wykrywanie")
  2. Overlay in action — **na neutralnym tle** (czarny wallpaper z generic delivery icons, NIE prawdziwe UI Uber/Wolt — Risk #3)
  3. Settings (theme, language, About z disclaimers)
  4. Disclosure screen (proof prominent disclosure flow)
  5. Setup wizard (OEM-specific permissions)
  6. Stop state (apka unactive)
- **CRITICAL constraint (M23):** żaden screenshot nie pokazuje logo/UI Uber/Wolt/Glovo/Bolt.
- **Lokacja:** `docs/play-store/assets/screenshots/<locale>/0X-<name>.png`.
- **Ref:** M8, M23, Risk #3.

#### Task 5.4 — Video demo 45-75s (M7, KD7)
- **Wymagania (KD7):**
  - Real device (Samsung/Pixel — masz Xiaomi taty, ale do video lepiej user own Pixel-equivalent jeśli dostępny)
  - First 5s: clear statement what app does (text overlay + voice/captions EN)
  - Pełen first-run flow: install → DisclosureActivity → „I understand" → grant accessibility → overlay appears na real delivery offer
  - OrderPilot branding visible w każdym kadrze gdzie overlay
  - Stop button shown
  - Captions EN (reviewer non-native EN-friendly)
- **Czerwone flagi do uniknięcia (KD7):** emulator, brak disclosure, overlay bez branding, overlay zasłaniający Accept button, video <15s lub >2min.
- **Hosting:** YouTube unlisted upload, link saved → Permissions Declaration (M6).
- **Ref:** M7, KD7.

#### Task 5.5 — Short description PL + EN (M9)
- **Plik:** `docs/play-store/listing/short-description.md`.
- **Limit:** 80 znaków per locale.
- **Treść (proposal):**
  - PL: „Stawka zł/h dla zleceń z Uber, Wolt, Glovo, Bolt — analiza on-device."
  - EN: „PLN/h overlay for Uber, Wolt, Glovo, Bolt offers — fully on-device."
- **Ref:** M9.

#### Task 5.6 — Long description PL + EN (M9, M21, M22)
- **Plik:** `docs/play-store/listing/long-description.md`.
- **Limit:** 1500-3000 chars (NIE max 4000 — dłuższe = bardziej spam-flag).
- **Mandatory elements:**
  - Co robi (zł/h analyzer)
  - Jak działa skrótowo (accessibility + screenshot for RN apps)
  - Privacy emphasis (on-device, no network, no data sent)
  - Compatibility list (Uber Driver, Wolt Courier, Glovo Courier, Bolt Food)
  - **Non-affiliation disclaimer** (M21): „OrderPilot is not affiliated with, endorsed by, or sponsored by Uber, Wolt, Glovo, or Bolt."
  - **Financial disclaimer** (M22): „Earnings estimates are computational only and do not constitute financial advice."
  - Permissions explanation summary
- **Ref:** M9, M21, M22, Risk #3, Risk #4.

#### Task 5.7 — Release Notes v1.0.0 (M24)
- **Plik:** `docs/play-store/release-notes/v1.0.0-pl.txt` + `v1.0.0-en.txt`.
- **Limit:** 500 chars per locale.
- **Template:**
  - PL: „Pierwsza wersja OrderPilot. Automatyczna analiza ofert z Uber, Wolt, Glovo, Bolt — wyliczenie zł/h dla każdego nowego zlecenia. Wszystko działa wyłącznie na Twoim urządzeniu — żadne dane nie są wysyłane."
  - EN: „First release of OrderPilot. Automatic offer analysis for Uber, Wolt, Glovo, Bolt — PLN/h calculation for each new order. Fully on-device, no data leaves your phone."
- **Ref:** M24, M33.

---

### Phase 6 — Testing Requirements (Closed Testing)

**Cel:** spełnić Google requirement (12-20 testers × 14 dni) → odblokowanie Production submit.

PARALLEL OK z Phase 4-5 (rekrutacja testerów już w Phase 0).

#### Task 6.1 — Setup Closed Testing track w Console (M15)
- **Lokacja:** Play Console > Testing > Closed testing > Create track.
- **Akcja:** create track „beta-1", upload AAB z Phase 1.
- **Ref:** M15, KD6.

#### Task 6.2 — Dodanie testerów (M15, V10, V16, V17)
- **Akcja:** create email list w Console > Testing > Closed testing > Testers > Create email list. Wkleić 20 emaili z Task 0.3.
- **Walidacja przed:** każdy email = active Google account, no family/sub restriction (V17).
- **Cel:** min 12 (V10 buffer), realne 20 (z resserve na drop-off).
- **Ref:** M15, V10, V16, V17, KD6, Risk #6.

#### Task 6.3 — Opt-in link + instrukcje (M15)
- **Akcja:** Console generuje opt-in URL po assign listy do tracka.
- **Komunikacja:** wysłać każdemu testerowi (a) opt-in link, (b) instrukcje („otwórz link → Become a tester → poczekaj 1-3h → install z Play Store"), (c) prośba o feedback (channel: WhatsApp / FB grupa / Google Form).
- **Ref:** M15.

#### Task 6.4 — Rollout strategy
- **Pierwsze 3 dni:** 5 testers (najbardziej aktywnych — tata + 4 najlepszych) — szybki feedback core flow
- **Dni 4-7:** kolejnych 10 testers (rozszerzenie pokrycia OEM)
- **Dni 8-14:** finalna 5 (resserve)
- **Reason:** żeby major bug w core flow nie zepsuł doświadczenia wszystkim 20 naraz.
- **Ref:** KD6, Risk #6.

#### Task 6.5 — Timer 14 dni (KD6, V10)
- **Start:** moment gdy pierwszy tester ZAINSTALUJE (nie tylko opt-in!) — Console pokazuje countdown.
- **Wymóg:** przez całe 14 dni utrzymać min 12 active testers (Console liczy „active").
- **Ref:** KD6, Risk #6.

#### Task 6.6 — Feedback collection + iteracja
- **Akcja:** zbierać bugi → fix na branch `play-store-prep` lub fork → bump versionCode → upload nowy AAB do Closed Testing.
- **WAŻNE:** każdy upload nowej wersji NIE resetuje 14-day timera — timer dotyczy track lifetime.
- **Ref:** KD6.

#### Task 6.7 — GO/NO-GO check przed Production submit
- **Kryteria GO (Definition of Ready):**
  - 14 dni minęło
  - Min 12 testers zainstalowało i używa (Console > Active testers)
  - Brak negative feedback blokującego core functionality
- **NO-GO triggers:**
  - <12 active testers → wydłużyć rekrutację
  - Crash rate > 2% w Vitals → fix → re-test
  - Policy „Alternative use of accessibility" zmieniła się (V24) → re-evaluate disclosure wording
- **Ref:** Definition of Ready (`01_analysis_v2.md`).

---

### Phase 7 — Final Submission Checklist

**Cel:** all-green pre-submit, klick „Submit for review" do Closed Testing (a po 14 dniach do Production).

#### Task 7.1 — Play Console: App content fields
- App category = Productivity (M14)
- Target audience = 18+ (M12)
- Content Rating questionnaire — wypełnić jako Utility/Productivity, no ads, no violence, no gambling, no UGC, no location → expected PEGI 3 / IARC 3+ (M11)
- Privacy Policy URL (z Task 4.3)
- Data Safety form (z Task 4.4)
- Permissions declarations × 4-5 (z Task 4.5/4.6)
- Government apps: NO
- News apps: NO
- COVID-19 apps: NO
- **App access** → wybrać „All functionality available without restrictions" + **Instructions for reviewer** (KOREKTA 1, P0):
  > „This app requires an active courier account on Uber Driver, Wolt Courier, Glovo Courier, or Bolt Food to demonstrate offer-detection functionality. Reviewers without such accounts will see only setup and home screens. Please refer to the video demo (linked in Permissions Declarations) for full functionality demonstration on a real device with active courier account."

  **Reason:** Google reviewer NIE ma kurierskich kont na platformach. Bez tej notatki manual test = apka „nic nie robi" → reject „app does nothing". Eliminuje #1 najbardziej prawdopodobny non-policy reject.

#### Task 7.2 — Play Console: Store listing
- Short description PL + EN (Task 5.5)
- Long description PL + EN (Task 5.6)
- Ikona 512 (Task 5.1)
- Feature graphic (Task 5.2)
- Screenshots (Task 5.3)
- Video URL — YouTube unlisted (Task 5.4)
- Email kontaktowy
- Website (PP URL or main page)

#### Task 7.3 — Play Console: App access
- Pole „App access" → wybrać „All functionality is available without restrictions" (free app, no login).

#### Task 7.4 — Pre-submit code/build verification
- Debug artifacts scan (V26): grep `Build.SERIAL\|getDeviceId\|ANDROID_ID\|getMac` w kodzie. Każdy match → review (zwykle false positive bo unused; jeśli używane → remove lub justify).
- AAB analyzer (V25): otworzyć `app-release.aab` w Android Studio APK Analyzer; weryfikacja: brak `BuildConfig.DEBUG = true`, brak debug symbols, brak test keys.
- Graceful degradation test (V18): user wyłącza accessibility w trakcie sesji — apka nie crashuje (test ręczny przez user).
- **Pre-launch Report check (KOREKTA 2, P1):** po pierwszym uploadzie do Closed Testing → Console > Testing > Pre-launch report → poczekać 5-10 min → sprawdzić raport.
  - **Co Google robi:** automatycznie odpala AAB na fizycznych Pixelach (~3-5 device/API combos), klika randomowo przez UI, zbiera crashe + screenshoty.
  - **Co spodziewane:** bot przejdzie Disclosure → SetupActivity → kliknie permission buttons → grant accessibility nigdy się nie powiedzie (bot nie nadaje grant) → MainActivity z status „setup needed".
  - **Co MUSI być zielone:** zero crashy. Acceptable: warnings o brakujących permissions / no target apps installed.
  - **Co fixujemy:** każdy crash w Pre-launch Report = fix przed promocją do Production. Najczęstszy crash = `MainActivity.onResume()` zakładający że accessibility jest aktywne.
  - **Nie blokuje submitu do Closed Testing**, ale blokuje promocję do Production jeśli crash.

#### Task 7.5 — Pre-submit policy refresh (V24)
- Akcja: support.google.com/googleplay/android-developer/answer/10964491 — sprawdzić że policy „Use of the Accessibility API" nie zmieniła się od daty FINAL analizy (2026-04-19).
- Decyzja: jeśli zmieniła → re-evaluate disclosure wording / permission declarations PRZED submitem.

#### Task 7.6 — Precedens check (V21)
- Akcja: Play Store search „Gridwise", „Para", „Stride", „RideHelper", „Indeed Flex", „Rydar", „Everlance" — sprawdzić że żadna nie została usunięta w ostatnich 30 dniach.
- Decyzja: jeśli któraś usunięta → research powodu (Google „[name] removed from play store") → re-evaluate strategy.

#### Task 7.7 — Klick „Review release" → „Start rollout to Closed Testing"
- Konsekwencja: AAB idzie do Google review (zwykle kilka godzin do kilku dni); po accept testers mogą instalować; 14-day timer startuje gdy pierwszy zainstaluje.
- Reviewer może odrzucić — w tym wypadku → Phase 8 (poza tym planem; iteracja na podstawie feedback).

#### Task 7.8 — Po 14 dniach: promocja do Production
- Pre-condition: GO criteria z Task 6.7 spełnione.
- Akcja: Console > Production > Create release → wybrać AAB z Closed Testing → Release Notes (Task 5.7) → Review → Start rollout.
- Rollout staged: ASSUMPTION 20% → monitor 3 dni → 50% → 3 dni → 100% (mitygacja Risk #4 jeśli problem masowy ujawni się dopiero w skali).

---

## DETAILED TASKS (CODE-LEVEL)

> ⚠️ **UWAGA KRYTYCZNA:** spójność `accessibility_service_description` (System Settings) ↔ DisclosureActivity body ↔ Permissions Declarations text ↔ store long description ↔ Privacy Policy „what data is accessed". Wszystkie 5 wymienia: **te same 4 apki**, **te same dane** (screen text), **ten sam cel** (zł/h overlay), **ten sam disclaimer** (no network, on-device only). Google reviewer porównuje tekst — najmniejsza rozbieżność = reject + Risk #1 escalation.

### CodeTask C1 — `AndroidManifest.xml` → `<queries>`
- **File:** `OrderPilot/app/src/main/AndroidManifest.xml`
- **Change:** add `<queries>` block before `<application>` (z 4 packages)
- **Reason:** package visibility for courier apps (Android 11+)
- **Risk if missing:** app cannot detect delivery apps → broken core
- **Ref:** Task 2.1, M16, V2

### CodeTask C2 — `AndroidManifest.xml` → AD_ID remove
- **File:** `OrderPilot/app/src/main/AndroidManifest.xml`
- **Change:** add `<uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove"/>`
- **Reason:** prevent auto-injection on Android 13+
- **Risk if missing:** Data Safety mismatch → reject
- **Ref:** Task 2.2, M20

### CodeTask C3 — `AndroidManifest.xml` → DisclosureActivity declaration
- **File:** `OrderPilot/app/src/main/AndroidManifest.xml`
- **Change:** add `<activity android:name=".ui.DisclosureActivity" android:exported="false" />`
- **Reason:** new activity needs declaration
- **Risk if missing:** ActivityNotFoundException
- **Ref:** Task 3.1, M1

### CodeTask C4 — `AndroidManifest.xml` → allowBackup decision
- **File:** `OrderPilot/app/src/main/AndroidManifest.xml:16` (and/or `res/xml/backup_rules.xml`)
- **Change:** depending on V20 audit — `allowBackup="false"` OR add excludes for offer cache/logs
- **Reason:** prevent backing up sensitive cache
- **Risk if missing:** Data Safety mismatch on cloud backup
- **Ref:** Task 2.3, F3, V20

### CodeTask C5 — `accessibility_config.xml` → packageNames
- **File:** `OrderPilot/app/src/main/res/xml/accessibility_config.xml`
- **Change:** add `android:packageNames="com.ubercab.driver,com.wolt.courier,com.glovo.courier,com.bolt.deliverycourier"`
- **Reason:** narrow attack surface, easier to defend in review
- **Risk if missing:** reviewer flag „can spy on all apps"
- **Ref:** Task 2.5, F5, M28, V3

### CodeTask C6 — `accessibility_config.xml` → audit accessibilityFlags
- **File:** `OrderPilot/app/src/main/res/xml/accessibility_config.xml:5`
- **Change:** remove `flagRetrieveInteractiveWindows` if not used in code; otherwise document usage
- **Reason:** minimize sensitive flags
- **Risk if missing:** unnecessary scrutiny
- **Ref:** Task 2.6, F12

### CodeTask C7 — `strings.xml` (×4 locales) → expanded `accessibility_service_description`
- **Files:** `res/values/strings.xml:3`, `res/values-en/strings.xml:3`, `res/values-uk/strings.xml:3`, `res/values-ru/strings.xml:3`
- **Change:** rewrite ~200-400 chars zgodnie z F1 wording (4 elements: what reads / from where / for what / no network)
- **Reason:** policy requires meaningful description; consistency with Disclosure
- **Risk if missing:** policy reject (vague description)
- **Ref:** Task 2.4, F1, F4, KD1

### CodeTask C8 — `strings.xml` (PL + EN) → disclosure strings
- **Files:** `res/values/strings.xml`, `res/values-en/strings.xml`
- **Change:** add 8+ strings per Task 3.4
- **Reason:** populate DisclosureActivity layout
- **Risk if missing:** crash / hardcoded text
- **Ref:** Task 3.4, M13, KD4

### CodeTask C9 — `strings.xml` → reframe „monitoring"
- **Files:** `res/values*/strings.xml` (search „monitoring" / „monitorowanie")
- **Change:** reword toneword to „offer detection" / „active"
- **Reason:** reduce surveillance connotation
- **Risk if missing:** P1 (Risk #4 mitigation)
- **Ref:** Task 3.8, F10

### CodeTask C10 — Create `ui/DisclosureActivity.kt`
- **File:** `OrderPilot/app/src/main/java/com/orderpilot/app/ui/DisclosureActivity.kt` (new)
- **Change:** Activity z layout `activity_disclosure.xml`, dwa buttony (accept → set flag + start SetupActivity / cancel → finishAffinity), PP link
- **Reason:** policy requires prominent disclosure
- **Risk if missing:** instant policy reject
- **Ref:** Task 3.1, M1, F2, KD4, Risk #1

### CodeTask C11 — Create `res/layout/activity_disclosure.xml`
- **File:** `OrderPilot/app/src/main/res/layout/activity_disclosure.xml` (new)
- **Change:** full-screen layout, title, body (scroll if needed), 2 equal buttons, PP link
- **Reason:** UI for DisclosureActivity
- **Risk if missing:** activity has no view
- **Ref:** Task 3.1, M1

### CodeTask C12 — Edit `SettingsRepository` + impl → `disclosureAcceptedVersion`
- **Files:** `settings/SettingsRepository.kt` (interface) + `settings/SharedPrefsSettingsRepository.kt` (impl)
- **Change:** add `var disclosureAcceptedVersion: Int` (default 0); const `CURRENT_DISCLOSURE_VERSION = 1`
- **Reason:** persist consent + allow future re-prompt
- **Risk if missing:** disclosure shown every launch (broken UX)
- **Ref:** Task 3.2, M5

### CodeTask C13 — Edit `MainActivity.kt:57` → wire Disclosure into start flow
- **File:** `OrderPilot/app/src/main/java/com/orderpilot/app/ui/MainActivity.kt` (around line 57, post-splash)
- **Change:** if `disclosureAcceptedVersion < CURRENT_DISCLOSURE_VERSION` → start DisclosureActivity + finish
- **Reason:** policy timing requirement (Disclosure BEFORE accessibility grant)
- **Risk if missing:** disclosure post-grant = policy violation
- **Ref:** Task 3.3, F2, KD4

### CodeTask C14 — Edit `SettingsActivity.kt` → PP link
- **File:** `OrderPilot/app/src/main/java/com/orderpilot/app/ui/SettingsActivity.kt`
- **Change:** add „Privacy policy" entry → opens `Intent(ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))`
- **Reason:** PP must be reachable from app post-grant
- **Risk if missing:** M4 not satisfied → reject
- **Ref:** Task 3.6, M4

### CodeTask C15 — Edit `SettingsActivity.kt` → About section z disclaimers
- **File:** `OrderPilot/app/src/main/java/com/orderpilot/app/ui/SettingsActivity.kt`
- **Change:** add About section z `about_non_affiliation` + `about_financial_disclaimer` strings
- **Reason:** Risk #3 + Risk #4 mitigation
- **Risk if missing:** trademark complaint risk
- **Ref:** Task 3.7, M21, M22

### CodeTask C16 — Logcat audit (cross-cutting)
- **Files:** all `*.kt` files with `Log.*` / `AppLog.*` calls
- **Change:** remove any log emitting offer text / OCR output / accessibility tree content
- **Reason:** match Data Safety „no data collected"
- **Risk if missing:** Risk #5 (Data Safety mismatch)
- **Ref:** Task 2.7, F6, V4

### CodeTask C17 — Save logs feature audit
- **File:** TBD (probably `SettingsActivity.kt` or `MainActivity.kt` — locate `btn_save_logs`)
- **Change:** redact log file content to structural-only OR remove feature
- **Reason:** match Data Safety
- **Risk if missing:** Risk #5
- **Ref:** Task 2.8, F7, V5

### CodeTask C18 — `ScreenCaptureService` foreground guard
- **File:** `OrderPilot/app/src/main/java/com/orderpilot/app/capture/ScreenCaptureService.kt`
- **Change:** ensure (a) `startForeground` only after MediaProjection result, (b) `takeScreenshot` only when foreground package ∈ TARGET_PACKAGES
- **Reason:** Risk #2 mitigation + crash prevention on Android 10+
- **Risk if missing:** crash + reviewer flag „captures any screen"
- **Ref:** Task 2.10, F11, V6

### CodeTask C19 — `build.gradle.kts` → versionName + signingConfigs
- **File:** `OrderPilot/app/build.gradle.kts:14-15`
- **Change:** `versionName = "1.0.0"` + add `signingConfigs.release` block
- **Reason:** Play standard + signed AAB requirement
- **Risk if missing:** Console rejects upload (unsigned) / non-standard versionName
- **Ref:** Tasks 1.3-1.4, M3, F8

### CodeTask C20 — Add `PRIVACY_POLICY_URL` constant
- **File:** `OrderPilot/app/src/main/java/com/orderpilot/app/di/OrderPilotApp.kt` (or BuildConfig field)
- **Change:** `const val PRIVACY_POLICY_URL = "https://<gh-user>.github.io/OrderPilot/legal/privacy-policy.html"`
- **Reason:** referenced by DisclosureActivity + SettingsActivity
- **Risk if missing:** hardcoded link or null reference
- **Ref:** Tasks 3.4, 3.6

---

## RISK-BASED WARNINGS

### Top 5 risks (z analizy 7-pozycyjnej, top by severity × likelihood)

#### W1 — Policy „Alternative use of accessibility" reject (Risk #1)
- **Real consequence:** policy-level reject; potencjalnie zero-recovery (apka nigdy nie wejdzie na Play). Worst case: account-level flag jeśli traktowane jako repeat offender przy resubmissions.
- **Mitigation:** narracja KD1 dosłownie spójna w 5 miejscach (System Settings desc, DisclosureActivity, Permissions Declaration, store description, Privacy Policy); evidence że Uber/Wolt nie publikują driver API; precedens RideHelper/Gridwise/Para żyje (V21).

#### W2 — MediaProjection „unnecessary" (Risk #2)
- **Real consequence:** reject z „remove MediaProjection or justify"; jeśli nie obronimy → cut Uber/Wolt support z v1.0 (60-70% value lost).
- **Mitigation:** w Permissions Declaration (4.5b) explicit React Native technical reason; w video demo pokazać empty accessibility tree dla Uber popup; foreground guard (C18); single-screenshot-per-event policy widoczna w kodzie.

#### W3 — Data Safety mismatch (Risk #5)
- **Real consequence:** reject z konkretnym feedback; przy powtarzaniu account-level action; legally binding declaration → post-release user może zgłosić = app removal + investigation.
- **Mitigation:** logcat audit (C16) + save logs audit (C17) + AD_ID removal (C2) + ML Kit explicit w PP/DataSafety + Android Vitals explicit acknowledgment.

#### W4 — Trademark / platform complaint (Risk #3)
- **Real consequence:** krótkoterminowe = store listing reject (fixable: zmień screenshot); długoterminowe = app removal po complaint Uber/Wolt/Glovo/Bolt (worst case).
- **Mitigation:** screenshots BEZ logo platform (M23, Task 5.3); brand mentions tylko jako fact („compatible with"); non-affiliation disclaimer w About + store (M21); nie używać platform marks w icon/feature graphic.

#### W5 — Closed Testing gating (Risk #6)
- **Real consequence:** Production submit zablokowany do czasu spełnienia 12+ × 14 dni. Delay multi-week.
- **Mitigation:** rekrutacja w Phase 0 (Task 0.3) z buforem 8 (cel 20 vs minimum 12); rollout strategy z Task 6.4 (5/10/5); rezerwa testerów na drop-off.

### Pozostałe 2 risks (do śledzenia)

- **Risk #4 (Deceptive overlay):** mitigated przez OrderPilot branding w overlay + non-interactive z underlying app + video proof.
- **Risk #7 (REQUEST_IGNORE_BATTERY_OPTIMIZATIONS):** Plan B przygotowany (Task 2.11) — możliwy fallback drop permission.

---

## MINIMAL PATH TO FIRST SUBMISSION

Najkrótsza ścieżka do kliknięcia **„Start rollout to Closed Testing"** w Console.

### Critical path (must-have do submit):

1. **Phase 0 — w całości** (account + name verification + tester recruitment start). 4 tasks.
2. **Phase 1 — Tasks 1.1, 1.2, 1.3, 1.4, 1.6, 1.7.** (Task 1.5 R8 i 1.8 dependency check NICE-to-have, można zostawić.)
3. **Phase 2 — Tasks 2.1, 2.2, 2.3, 2.4, 2.5, 2.7, 2.8, 2.9, 2.10.** (Task 2.6 flagi — P1, optional. Task 2.11 keep current state, decyzja w response do feedback.)
4. **Phase 3 — Tasks 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7.** (Task 3.8 monitoring rewording — P1, można później.)
5. **Phase 4 — Tasks 4.1, 4.2, 4.3, 4.4, 4.5.** (Task 4.6 = Plan B, robione tylko jeśli reject.)
6. **Phase 5 — Tasks 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7.** Wszystkie required.
7. **Phase 6 — Tasks 6.1, 6.2, 6.3.** (Task 6.4 rollout strategy + 6.5 timer + 6.6 iteration + 6.7 GO check = w trakcie 14 dni, nie blokują samego submitu do Closed Testing.)
8. **Phase 7 — Tasks 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7.** Submit. (Task 7.8 = Production submit, zostaje na później.)

### Co MUSI być zielone, żeby kliknąć „Submit for review":

- Signed AAB upload (Task 1.6 + 1.7)
- Manifest fixes done (C1-C7)
- DisclosureActivity działa fresh-install (Task 3.5 / V14)
- Logs audited (C16-C17)
- PP + Data Deletion URL żyją (Task 4.3)
- Data Safety form filled (Task 4.4)
- 4 Permissions Declarations w Console z wordingiem (Task 4.5 → Task 7.1)
- Store assets uploaded (Tasks 5.1-5.6 → Task 7.2)
- Video link w Permissions Declaration (Task 5.4)
- Content Rating + Target Audience (Tasks 7.1)
- App category = Productivity (Task 7.1)

### Co można odłożyć po pierwszym review:

- R8/ProGuard (Task 1.5) — tylko jeśli AAB > 50 MB
- Task 2.6 audit accessibilityFlags (P1)
- Task 3.8 reframe „monitoring" (P1, polish)
- UK/RU store listing (M33) — fallback do EN OK na v1.0
- Release Notes automation (M34) — manualne na v1.0
- 3-segment versionName ALREADY w critical path (F8 / Task 1.4)
- Glovo dynamic time / FOLDABLE / Huawei (z `future_polish_fixes.md`) — out of scope

---

## OUTPUT RULES — WHAT THIS DOC IS NOT

- Nie powtarza analizy v1/v2 (tam są tabele HAVE/MISSING/NEEDS FIX/VERIFY z numerami)
- Nie tłumaczy „dlaczego policy" — to jest w `01_analysis_v2.md` (sekcje KD + Risks)
- Nie dodaje nowych funkcji apki
- Nie zmienia architektury
- Nie planuje nic poza Closed Testing → Production v1.0.0

---

## NEXT STEP

Pierwsza akcja użytkownika: **Phase 0 Task 0.1** (założyć Dev Account + $25 + identity verification). Wszystko inne czeka.

Druga równoległa: **Phase 0 Task 0.3** (rozpocząć rekrutację 20 testerów — to zajmuje czas niezależnie od reszty pracy).
