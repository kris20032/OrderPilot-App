# 01 · Play Store Release — Gap Analysis (v2)

## Status

- **Status:** FINAL
- **Data:** 2026-04-19
- **Branch:** `play-store-prep`
- **Zastępuje:** `01_analysis.md` (v1) — v1 traktować jako draft, ten plik jest source of truth
- **Cel:** jednoznaczny stan rzeczy przed przygotowaniem planu implementacyjnego (`02_implementation_plan.md`)

## Strategia (FINAL)

- `android:isAccessibilityTool="false"` (lub brak atrybutu = default false) — **alternative use track** przez Permissions Declaration Form. Świadoma rezygnacja z `true` mimo że to by ułatwiło automated scan, bo `true` byłoby **deceptive declaration** w rozumieniu policy (apka nie jest narzędziem dla osób z niepełnosprawnościami — jest productivity tool dla kurierów) i grozi account-level action.
- **Zero network / on-device only processing** — brak `android.permission.INTERNET`, brak Firebase/Crashlytics/Analytics, brak retrofit/okhttp/HttpURLConnection. Jedyne „dane do Google" to auto-zbierane Android Vitals (ANR/crash), nie do wyłączenia dla Play-distributed apps — **must be disclosed** w Data Safety.
- **Target audience: 18+** (gig economy workers). Odcina compliance requirements dotyczące dzieci (Families Policy, COPPA). Pozwala deklaratywnie uniknąć Children's Privacy.
- **Play Store first release — no fallback distribution.** Nie traktujemy APK direct download / F-Droid / Huawei AppGallery / Samsung Galaxy Store jako planu B. Jedyna ścieżka: Play Store. To znaczy że każdy element analysis musi być twardo zaimplementowany (nie „można odpuścić bo rozdamy APK").
- **AAB + Google Play App Signing.** ASSUMPTION: wybieramy Play App Signing (Google trzyma signing key, my upload key). Wymaga weryfikacji w V13 — alternatywa to self-managed signing.
- **App category:** Productivity (ASSUMPTION). Lepsze niż Tools (Tools = system utility = większa scrutiny). Lepsze niż Business (Business zwykle dla aplikacji enterprise).
- **i18n store listing:** PL + EN na v1.0. UK/RU pokazują fallback do EN. Pełne tłumaczenie listingu dopiero po production release stabilnym.
- **App name:** „OrderPilot" (ASSUMPTION — do weryfikacji V7 czy nazwa nie jest zajęta).
- **Package name:** `com.orderpilot.app` (do weryfikacji V8 po założeniu Dev Account).

## Key Decisions (FINAL)

### KD1 — Accessibility Strategy: `isAccessibilityTool="false"` + Alternative Use track

**Decyzja:** `isAccessibilityTool="false"` + pełny Permissions Declaration Form + Prominent Disclosure + spójna narracja „workplace productivity tool for gig-economy delivery workers".

**Implikacje policy (Google „Use of the AccessibilityService API"):**
- Accessibility APIs „should only be used to help users with disabilities" — OrderPilot nie spełnia tego literalnie
- Alternative use wymaga: (a) prominent disclosure, (b) user consent, (c) accurate representation w store listing
- `isAccessibilityTool="true"` jest zastrzeżone dla apek realnie pomagających disabled users — użycie „na zapas" to misuse
- Wszystkie materiały (app description, store listing, in-app, video) MUSZĄ spójnie mówić o tym samym use case — reviewer porównuje i flaguje rozbieżności

**Narracja do obrony:**
> OrderPilot is a workplace productivity tool for food-delivery and ride-hailing couriers. AccessibilityService is used exclusively to read real-time offer content from four specified delivery platforms (Uber Driver, Wolt Courier, Glovo Courier, Bolt Food) for the purpose of computing per-hour rate (PLN/hour) and displaying it on a non-intrusive overlay. No accessibility-tree data is transmitted, logged persistently, or shared. Users must explicitly enable the service through system settings after seeing a full-screen prominent disclosure. Core productivity value cannot be delivered via alternative APIs because (a) Uber Driver and Wolt Courier use React Native — text is not exposed in accessibility tree, necessitating OCR via MediaProjection/screenshot, (b) Glovo Courier and Bolt Food expose offer text in accessibility tree, which is more efficient and battery-friendly than OCR.

**Ryzyko akceptacji (my own assessment, ASSUMPTION):**
- Runda 1: 30-50% acceptance
- Runda 2 (po feedback): 40-60%
- Runda 3+: rosnąco do 70%+
- Total 4-round probability: ~85-90%
- 10-15% ryzyko policy-level block (patrz Top Risk #1)

### KD2 — MediaProjection Usage

**Decyzja:** MediaProjection jako fallback kiedy accessibility tree nie zawiera danych (React Native UI w Uber/Wolt). Nie jako primary mechanism. Single screenshot → OCR → discard.

**Wymogi:**
- Foreground check przed screenshot (tylko gdy target app jest in foreground)
- Screenshot nigdy nie zapisywany na dysk
- Screenshot nigdy nie wyświetlany użytkownikowi
- User-initiated (system `startActivityForResult` dialog)
- System cast icon widoczny podczas aktywności
- `FOREGROUND_SERVICE_MEDIA_PROJECTION` (Android 14+ wymóg)

### KD3 — SYSTEM_ALERT_WINDOW Strategy

**Decyzja:** Overlay wyłącznie do wyświetlania zł/h bar. Mały (60dp height), **non-interactive z underlying app** (nie blokuje przycisków), pokazuje się tylko gdy nowa oferta wykryta. User może przenieść, ukryć, dismissować. Overlay tagged wyraźnym OrderPilot brandingiem, żeby nigdy nie było pomylone z underlying delivery app.

### KD4 — Prominent Disclosure Requirement (non-optional)

**Decyzja:** Pełnoekranowa `DisclosureActivity` pokazywana PRZED `SetupActivity` przy pierwszym uruchomieniu. Akceptacja persistowana jako flaga (`disclosureAcceptedVersion`). Cancel zamyka apkę.

**Mandatory elements disclosure (policy-driven):**
1. Identyfikacja apki w pierwszym zdaniu („OrderPilot…", nie „This app…")
2. Explicit lista danych które są czytane — nie „contents of your screen" tylko „text visible on the screen of Uber Driver, Wolt Courier, Glovo Courier, and Bolt Food apps"
3. Explicit cel — „to compute per-hour rate (PLN/hour) and display it as an overlay"
4. Eksplicytne stwierdzenie co się NIE dzieje — „This information is not transmitted off your device, is not stored permanently, and is not shared with anyone"
5. Czas życia danych — „Data from each offer is processed in memory and discarded when you accept/decline the offer"
6. Dwa przyciski z równorzędną prominencją: „I understand, continue" i „Cancel" (ten sam rozmiar, kolor, kontrast)
7. Cancel MUSI mieć sensowną konsekwencję (zamknięcie apki / wyjście), nie „ta sama apka działa dalej"
8. Timing: PRZED system dialog „włącz accessibility", nie w trakcie, nie po

### KD5 — Privacy Architecture (on-device only)

**Decyzja:** Zero danych opuszczających urządzenie (poza Android Vitals auto-collected by Play). Argument ten wykorzystujemy jako kluczowy atut w Data Safety + Privacy Policy + Permissions Declarations.

**Atut w materiałach:**
- Data Safety form: „No data collected, no data shared" w każdej kategorii
- Privacy Policy: „Since we do not collect any personal data, there is nothing to access, modify, or delete on our end. You may uninstall the app at any time to remove all local data."
- Permissions Declarations: „Data is processed in RAM, discarded after overlay display (max 30s), and never logged, transmitted, or shared"

### KD6 — Play Store Submission Strategy (Closed Testing required)

**Decyzja:** Ścieżka Closed Testing → Production, **nie** Internal Testing → Production. Powód: od 2023/2024 Google wymaga dla nowych developer accounts **minimum 12-20 testers przez 14 dni** na Closed Testing przed możliwością Production release (exact liczba testerów do weryfikacji V10 — zmieniała się).

**Implikacje:**
- Trzeba rekrutować testerów **zanim** AAB gotowy (minimum 12, docelowo 20 z zapasem)
- 14-dniowy timer startuje od momentu gdy pierwsi testerzy zainstalowali apkę
- Nie da się tego skrócić — hard requirement Google
- Planowanie wstecz: jeśli chcemy Production release w dniu X, Closed Testing musi być live w X-14 (minimum)

### KD7 — Video Demo Strategy

**Decyzja:** Video 45-75s (sweet spot), real device (nie emulator), real delivery app flow, YouTube unlisted upload linkowany z Permissions Declaration.

**Obowiązkowe elementy video:**
- Pierwsze 5s: clear statement what app does
- Pełen first-run flow: install → prominent disclosure → user tap „I understand" → grant accessibility → overlay appears
- Real device (Samsung/Pixel)
- Real third-party app na tle (Uber/Wolt) z prawdziwym lub dev-mode zleceniem
- Widoczny OrderPilot overlay z brandingiem
- Pokazanie Stop button / dezaktywacji
- Napisy po angielsku (reviewer może być non-native EN speaker)

**Czerwone flagi video:**
- Emulator zamiast real device
- Brak disclosure screen flow
- Overlay bez OrderPilot brandingu
- Overlay zasłaniający krytyczny UI underlying app (Accept button)
- Video <15s lub >2min

---

## ✅ HAVE (FINAL — 10 pozycji)

| # | Punkt | Gdzie |
|---|-------|-------|
| H1 | Wszystkie permisje zadeklarowane (SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE_*, POST_NOTIFICATIONS, WAKE_LOCK, RECEIVE_BOOT_COMPLETED, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) | `AndroidManifest.xml:5-12` |
| H2 | Foreground service types zgodnie z Android 14+ (`mediaProjection`, `specialUse` + `PROPERTY_SPECIAL_USE_FGS_SUBTYPE`) | `AndroidManifest.xml:43-55` |
| H3 | AccessibilityService z `BIND_ACCESSIBILITY_SERVICE` permission + poprawny intent-filter + meta-data | `AndroidManifest.xml:65-75` |
| H4 | `accessibility_config.xml` z event types, flags, `canTakeScreenshot`, `canRetrieveWindowContent`, `notificationTimeout`, `description` | `res/xml/accessibility_config.xml` |
| H5 | `accessibility_service_description` string w 4 językach (PL/EN/UK/RU) — krótki, wymaga rozszerzenia (F1/F5) | `res/values*/strings.xml:3` |
| H6 | SetupActivity z OEM-specific onboarding (Samsung/Xiaomi/Huawei/Oppo/OnePlus) + status checks + intent helpers | `ui/SetupActivity.kt` |
| H7 | Ikona A1 Arrow-Up Reticle — adaptive + themed + monochrome + webp dla mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi | `res/mipmap-*` + `res/drawable/ic_launcher_*` |
| H8 | Splash screen (Android 12+ SplashScreen API + compat shim) zweryfikowany na urządzeniu | `res/values/themes.xml` + `MainActivity.kt:57` |
| H9 | **Zero network code** — brak INTERNET permission, brak Firebase/Analytics/Crashlytics/retrofit/okhttp/HttpURLConnection (grep confirmed) | cała baza kodu |
| H10 | Target SDK 35 + min SDK 26 + namespace + applicationId + backup rules XML obecne | `build.gradle.kts` + `res/xml/backup_rules.xml` + `res/xml/data_extraction_rules.xml` |

---

## ❌ MISSING (FINAL)

### P0 — Blockery submission (bez tego nie da się nawet złożyć)

| # | Punkt | Kategoria |
|---|-------|-----------|
| M1 | **Prominent Disclosure Screen** — pełnoekranowa `DisclosureActivity` pokazywana przed `SetupActivity` przy pierwszym launch, z wordingiem zgodnym z KD4, dwoma równorzędnymi przyciskami, Cancel zamyka apkę | UI/Code |
| M2 | **Privacy Policy (pełna treść)** — hostowana na stabilnym URL. Struktura: (1) Introduction + contact, (2) What data accessed, (3) How data used, (4) What NOT collected, (5) Data retention, (6) Third-party SDKs (**ML Kit explicit**), (7) Children's privacy (18+), (8) User rights (GDPR), (9) Changes policy, (10) Date, (11) Contact email | Artefakt zewnętrzny |
| M3 | **Signed AAB** — upload keystore (z backup!) + signingConfig w gradle + secrets handling przez `~/.gradle/gradle.properties` nigdy w repo + decyzja Play App Signing vs self-managed | Build |
| M4 | **In-app Privacy Policy link** w `SettingsActivity` (po grant accessibility user MUSI mieć dostęp do PP) | UI/Code |
| M5 | **Consent tracking** w `SettingsRepository` — `disclosureAcceptedVersion: Int` (wersjonowany żeby móc re-promptować przy zmianie disclosure) | Code |
| M6 | **Permissions Declaration content** — 4 osobne wordingi (Accessibility, MediaProjection, SYSTEM_ALERT_WINDOW, FOREGROUND_SERVICE_SPECIAL_USE) z argumentami per KD1/KD2/KD3. Każde z (a) co, (b) po co, (c) co jeśli user odmówi, (d) czemu alternatywne API nie wystarczają | Artefakt zewnętrzny |
| M7 | **Video demo 45-75s** per KD7 — real device, full first-run flow, OrderPilot branding visible, YouTube unlisted upload | Artefakt zewnętrzny |
| M8 | **Store listing assets:** ikona 512×512 (eksport z vectora A1), feature graphic 1024×500, 4-6 screenshotów per locale (home/overlay-in-action/settings/disclosure/setup wizard/stop state) | Artefakt zewnętrzny |
| M9 | **Store listing copy** — short description (80 chars) + long description (1500-3000 chars, nie max) w PL + EN. Długi opis MUSI zawierać disclaimer o non-affiliation z Uber/Wolt/Glovo/Bolt i financial disclaimer | Artefakt zewnętrzny |
| M10 | **Data Safety form** — wypełnione „no data collected" w każdej kategorii; explicit disclosure Android Vitals (auto-collected, nie do wyłączenia); explicit wymienione ML Kit Text Recognition jako third-party SDK | Play Console |
| M11 | **Content Rating questionnaire** — Utility/Productivity, no ads, no violence, no gambling, no user-generated content, no location → PEGI 3 / IARC 3+ | Play Console |
| M12 | **Target audience + age declaration** — 18+, gig economy workers | Play Console |
| M13 | **Prominent disclosure strings** w `strings.xml` — minimum 8 nowych stringów (title, body ~300 słów, accept button, cancel button, PP link label, PP URL constant, data-type-list items, cancel confirmation) w PL + EN | Code/Resources |
| M14 | **App category decision + deklaracja w Console** — Productivity (rekomendacja, ASSUMPTION do potwierdzenia) | Play Console |
| M15 | **Closed Testing track setup** — 12-20+ testers z Google accounts, 14-day minimum przed możliwością Production submit. Rekrutacja testerów to osobne zadanie organizacyjne (lista emaili, opt-in link, instrukcje) | Play Console + Org |
| M16 | **`<queries>` declaration w AndroidManifest** — jeśli kod używa `getInstalledPackages()` / `queryIntentActivities()` / jakiegokolwiek sposobu wykrycia innych apek (weryfikacja V2). Musi zawierać exact package names: `com.ubercab.driver`, `com.wolt.courier`, `com.glovo.courier`, `com.bolt.deliverycourier` (pełna lista z kodu do weryfikacji) | Manifest |
| M17 | **Logcat audit** — żaden `Log.*`/`AppLog.*` nie loguje tekstu z accessibility tree / OCR / user offer content. Przejść 100% call sites (weryfikacja V4). Naruszenie = Data Safety mismatch | Code audit |
| M18 | **Log save feature audit** — `btn_save_logs` zapisuje do Downloads (strings.xml:106). Trzeba zweryfikować zawartość pliku (V5): jeśli zawiera accessibility/OCR content, albo usunąć feature, albo zredagować do generic info, albo dodać to do Data Safety jako user-initiated log export | Code audit |
| M19 | **Google Play Developer Account** — $25 jednorazowo + identity verification (paszport/dowód) + decyzja indywidualne vs firmowe (V9) | Play Console |
| M20 | **Advertising ID disabled** w manifeście: `<uses-permission android:name="com.google.android.gms.permission.AD_ID" tools:node="remove"/>` — od Android 13 wymóg jeśli nie używamy ads. Brak tego = Data Safety mismatch („collects AdID" domyślnie) | Manifest |
| M21 | **Disclaimer non-affiliation** z Uber/Wolt/Glovo/Bolt w (a) Settings → About, (b) long description w Play Store, (c) potencjalnie w splash screen lub disclosure | Code + Store listing |
| M22 | **Financial disclaimer** — „Earnings estimates only, not financial advice" w apce (About) + store listing. Mitigacja risk mis-classification jako „financial advice app" | Code + Store listing |
| M23 | **Screenshot guidelines compliance** — screenshoty w store listing NIE pokazują logo/UI Uber/Wolt/Glovo/Bolt. Zamiast tego: tylko OrderPilot belka na neutralnym tle (czarny wallpaper z delikatnymi generic ikonami platform, nie ich prawdziwe UI) | Artefakt zewnętrzny |
| M24 | **Release Notes / What's new** template (500 chars / locale). Pierwsza wersja: „Pierwsza wersja OrderPilot. Automatyczna analiza ofert z Uber, Wolt, Glovo, Bolt — wyliczenie zł/h dla każdego nowego zlecenia." (PL) + EN | Play Console |
| M25 | **Data Deletion Policy URL** — wymagane od Google (nawet jeśli brak danych). Minimal: strona wyjaśniająca że apka nie trzyma danych poza urządzeniem, uninstall wystarcza do deletion | Artefakt zewnętrzny |

### P1 — Ważne przed submission, ale nie hard-block

| # | Punkt | Kategoria |
|---|-------|-----------|
| M26 | **R8/ProGuard decyzja** — `isMinifyEnabled = false` obecnie. Opcje: (a) zostawić false na v1.0 (większy AAB, nie blocker), (b) włączyć true + dodać reguły dla MLKit, kotlinx-serialization, ViewBinding. Decyzja do podjęcia | Build |
| M27 | **ML Kit offline verification** — potwierdzić że używamy `com.google.mlkit:text-recognition` (bundled) nie `-latn` variant który pobiera z Play Services. Weryfikacja V1 | Code |
| M28 | **`accessibility_config.xml` packageNames constraint** — dodać `android:packageNames="com.ubercab.driver,com.wolt.courier,com.glovo.courier,com.bolt.deliverycourier"` (exact lista z kodu). Serwis będzie rcivil tylko te 4 apki = mniejsza powierzchnia ataku = better review | Config |
| M29 | **Domain / URL dla Privacy Policy + Data Deletion** — decyzja hosting: GitHub Pages (free, stable) / Cloudflare Pages / własna domena. ASSUMPTION: GitHub Pages z `docs/` w repo | Infra |
| M30 | **Keystore backup strategy** — min 2 lokalizacje (password manager + offline). Strata keystore = nie możesz aktualizować apki NIGDY. Weryfikacja V13 | Ops |
| M31 | **Brand / name research** — Play Store search „OrderPilot" + trademark database check (UPRP/EUIPO). Weryfikacja V7 | Legal |

### P2 — Post-launch polishing (opcjonalnie przed v1.0)

| # | Punkt | Kategoria |
|---|-------|-----------|
| M32 | **`versionName` 3-segment semver** — zmiana z `1.0` na `1.0.0` (Play standard) | Build |
| M33 | **UK/RU store listing translations** — na v1.0 fallback do EN, pełne tłumaczenia w v1.1+ | Store |
| M34 | **Changelog schema / versionCode bump automation** — ręczny bump per release plus PROGRESS.md update. Automatyzacja (script/gradle) opcjonalna | Ops |

---

## ⚠️ NEEDS FIX (FINAL)

### P0 — Must-fix przed submission

| # | Punkt | Gdzie | Co zrobić |
|---|-------|-------|-----------|
| F1 | `accessibility_service_description` za krótki (1 zdanie, ~60 znaków) | `res/values*/strings.xml:3` | Rozszerzyć do ~200-400 znaków, wording zgodny z KD1, w PL/EN/UK/RU. Przykład: „OrderPilot czyta zawartość ekranu aplikacji kurierskich (Uber Driver, Wolt Courier, Glovo Courier, Bolt Food), aby wykryć nowe zlecenia i wyświetlić na nakładce wyliczoną stawkę godzinową (zł/h). Dane z ekranu są analizowane wyłącznie na Twoim urządzeniu i nie są nigdzie wysyłane." |
| F2 | `SetupActivity` nie jest prominent disclosure — flow idzie od razu do permission buttons | `ui/SetupActivity.kt` + `MainActivity.kt` flow | Dodać `DisclosureActivity` PRZED `SetupActivity`. Zmiana flow: `SplashScreen → DisclosureActivity (if !accepted) → SetupActivity → MainActivity`. Konsumuje M5 consent flag |
| F3 | `allowBackup="true"` bez weryfikacji co się backupuje | `AndroidManifest.xml:16` + `res/xml/backup_rules.xml` + `res/xml/data_extraction_rules.xml` | Sprawdzić zawartość XML. Jeśli backup obejmuje SharedPrefs z historical offer cache/OCR/logs → dodać exclude. **Alternatywa prosta: `allowBackup="false"` jeśli nie mamy nic istotnego do zachowania** (thresholds/settings user może ustawić ponownie) |
| F4 | `accessibility_service_description` w 4 językach — każda locale musi mieć pełny rozszerzony opis (konsekwencja F1) | `res/values-en`, `res/values-uk`, `res/values-ru` | Równocześnie z F1 |
| F5 | `accessibility_config.xml` brak `packageNames` constraint | `res/xml/accessibility_config.xml` | Dodać atrybut `android:packageNames="..."` z exact lista 4 apek kurierskich. Konsumuje M28 |
| F6 | Kod loguje tekst z accessibility / OCR (RYZYKO, do weryfikacji V4) | Cała baza kodu — audit all `Log.*` / `AppLog.*` | Usunąć wszystkie logi zawierające user offer content. Zostawić tylko structural logs (event types, timings, errors). Bez weryfikacji V4 to jest ASSUMPTION że problem istnieje |
| F7 | Save logs feature zawiera user data (RYZYKO, do weryfikacji V5) | Prawdopodobnie `SettingsActivity.kt` lub `MainActivity.kt` button `btn_save_logs` | Zredagować plik logów do structural-only (bez offer text, bez screenshotów, bez OCR output). Albo usunąć feature. Bez V5 to ASSUMPTION |
| F8 | `versionName = "1.0"` zamiast `"1.0.0"` (Play standard 3-segment) | `build.gradle.kts:15` | Zmienić na `"1.0.0"` |

### P1 — Should-fix

| # | Punkt | Gdzie | Co zrobić |
|---|-------|-------|-----------|
| F9 | `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` value długi angielski opis w manifeście | `AndroidManifest.xml:52-54` | Upewnić się że wording w manifeście JEST DOKŁADNIE TAKI SAM jak w Permissions Declaration (spójność). Google compares text — rozbieżność = flag |
| F10 | Wordingi toastów i hintów używają „monitoring" — słowo potencjalnie problemne (surveillance connotation) | `res/values*/strings.xml` — `hint_notifications_disabled`, `notif_monitoring_*`, `toast_*` | Rozważyć zmiany na neutralne: „monitoring" → „offer detection" / „delivery offer tracking" / „active". Nie blocker, ale wpływa na wrażenie reviewera |
| F11 | `ScreenCaptureService` może być uruchamiany przed user grant MediaProjection | `capture/ScreenCaptureService.kt` | Zweryfikować że startForeground jest wołany dopiero po successful MediaProjection result. Crash/security violation inaczej na Android 10+ |
| F12 | `accessibilityFlags` — sprawdzić czy `flagRetrieveInteractiveWindows` jest realnie wykorzystywany | `res/xml/accessibility_config.xml:5` | Jeśli nie używany — usunąć. To dodatkowy sensitive flag który reviewer notuje |

### P2 — Nice-to-have

| # | Punkt | Gdzie | Co zrobić |
|---|-------|-------|-----------|
| F13 | Release Notes template + schemat wersjonowania w repo | `docs/play-store/release-notes/` | Template per locale, proces update przy każdym release |

---

## 🔎 VERIFY (FINAL)

### P0 — Blockujące plan implementacyjny

| # | Pytanie | Jak zweryfikować |
|---|---------|------------------|
| V1 | ML Kit Text Recognition — on-device bundled czy downloaded? | Grep `mlkit.*text` w `libs.versions.toml` + `build.gradle.kts`. Jeśli `com.google.mlkit:text-recognition` = bundled (100% offline). Jeśli `-latn` variant + Play Services — downloaded |
| V2 | Czy kod używa `getInstalledPackages()` / `queryIntentActivities()` / `PackageManager.resolveActivity` do wykrycia innych apek? | Grep `getInstalledPackages\|queryIntentActivities\|resolveActivity\|PackageManager` w kodzie. Jeśli tak → wymagane `<queries>` w manifeście (M16) |
| V3 | Accessibility config `packageNames` constraint — czy ograniczyć do 4 apek kurierskich? | Grep `packageName ==` w kodzie — znaleźć pełną listę package names na które reaguje service. Lista musi być exact (Uber Driver vs Uber Eats osobno jeśli oba) |
| V4 | Każdy `Log.*` / `AppLog.*` call site — czy loguje user data (offer text, OCR output, accessibility tree content)? | Grep `Log\.\|AppLog\.` w kodzie, audyt 100% call sites |
| V5 | Feature save logów do Downloads — co dokładnie zawiera plik? | Code review action `btn_save_logs` / `toast_logs_saved`. Odczytać kod generujący treść pliku |
| V6 | `ScreenCaptureService` — czy zbieramy screenshoty tylko z foreground delivery apps (4 target apps), czy z jakiegokolwiek foreground app? | Code review `ScreenCaptureService.kt` + `PopupCropper.kt` + trigger logic |
| V7 | Play Store nazwa „OrderPilot" — czy zajęta przez inną apkę (nawet nieaktywną)? | play.google.com/store + Play Store app search |
| V8 | Package `com.orderpilot.app` — czy wolny? | Weryfikacja po założeniu Dev Account (próba stworzenia apki). Przed tym nie da się potwierdzić |
| V9 | Developer account indywidualny vs firmowy — decyzja na podstawie: (a) monetyzacja (tax implications), (b) czy ujawnienie prawdziwego imienia jest OK (Google publikuje developer name publicznie od 2023), (c) czy masz firmę zarejestrowaną | User decision |
| V10 | Closed Testing — exact liczba testerów + exact dni wymagane | Sprawdzić aktualną Google policy (support.google.com) tuż przed planem. Zmieniała się: 12 vs 20, 14 dni twardo |
| V11 | `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` — Google restricted permission, wymaga uzasadnienia w Permissions Declaration + qualifying use case. Czy Productivity to qualifying use case? | Policy check + test submission. Alternatywa: usunąć permission + polegać tylko na `FOREGROUND_SERVICE_SPECIAL_USE` |
| V12 | Alternatywa storage dla keystore — min 2 lokalizacje (password manager + offline/cloud) | Decyzja + setup |
| V13 | Play App Signing vs self-managed — decyzja | Google recommends Play App Signing. Konsekwencja: upload keystore (mniej krytyczny, wymienialny) + Google trzyma signing key (dożywotni). Self-managed: Ty trzymasz oba — totalna strata = koniec apki |
| V14 | Onboarding flow test — czy pierwszy launch po fresh install pokazuje Disclosure PRZED Setup? | Test instrumentalny na fresh emulator/device po implementacji M1/F2 |
| V15 | Target SDK 35 + wszystkie dep compatible | Gradle build test + runtime test — `WorkManager`, `core-splashscreen`, `mlkit`, `material` wersje muszą być API 35-aware |

### P1 — Ważne przed submission

| # | Pytanie | Jak zweryfikować |
|---|---------|------------------|
| V16 | Beta testerzy — lista 12+ real Google accounts (Ty, tata, 3-5 obecnych test kurierów + rekrutacja przez lokalne grupy FB kurierskie) | Plan rekrutacji + komunikacja |
| V17 | Czy tata / testerzy mają konta z restrictions (sub-account, family account) — nie wszystkie konta mogą być testerami | Komunikacja z testerami przed invite |
| V18 | Stop/uninstall behavior — co się dzieje z accessibility grant gdy user disable accessibility? MainActivity reaguje graceful? | Test flow — reviewer może testować to celowo |
| V19 | Android Vitals auto-collection — exact lista co zbiera (ANR, crash, battery usage, etc.) | Play Console documentation check przy Data Safety form |
| V20 | `backup_rules.xml` + `data_extraction_rules.xml` — aktualna zawartość | Read tych plików |
| V21 | Inne apki dla kurierów na Play Store (precedensy) — lista live/removed: Gridwise, Para, Stride, RideHelper, Indeed Flex, Rydar, Everlance | Play Store research + Google „[app name] removed from play store" |
| V22 | Trademark search „OrderPilot" w UPRP / EUIPO | Database query |
| V23 | Domena `orderpilot.pl`, `orderpilot.com`, etc. — dostępność | WHOIS check |

### P2 — Edge cases

| # | Pytanie | Jak zweryfikować |
|---|---------|------------------|
| V24 | Policy „Alternative use of accessibility" — czy wersja policy na moment submit się nie zmieniła | Monitoring support.google.com/googleplay/android-developer/answer/10964491 raz w miesiącu |
| V25 | Build artifacts (AAB) — czy nie zawierają debug symbols / test keys / development artifacts | Analiza AAB przez `bundletool` + Android Studio APK Analyzer |
| V26 | Debug artifacts w kodzie — grep `Build.SERIAL\|getDeviceId\|ANDROID_ID\|getMac`. Google automated scan szuka tego | Grep + manual review |

---

## 🚨 Top Risks (Play Store Review)

### Risk #1 — Alternative Use of Accessibility nie spełnia policy bar

**Opis:** Google może stwierdzić że use case OrderPilot (optymalizacja przyjmowania zleceń) **nie jest legitimate alternative use** w sensie policy, nawet z prominent disclosure. Policy stanowi że accessibility ma być „last resort" gdy żadna inna integracja nie jest możliwa. Reviewer może argumentować że „couriers can calculate rates themselves / delivery apps provide this info natively / third-party integration should be with platform API".

**Realne konsekwencje:**
- **Policy-level reject** — nie ma technical fix, tylko rewrite narracji (i ewentualnie ograniczenie funkcjonalności)
- Potencjalnie nigdy nie do zaadresowania na Play Store
- Największe ze wszystkich ryzyk bo jedyne bez oczywistej drogi w górę

**Strategia obrony:**
- Dowód że platformy NIE udostępniają API dla kurierów (Uber nie ma public API dla driver offers — publikacja evidence w Permissions Declaration)
- Dowód precedensu — inne apki tej kategorii działają na Play Store (RideHelper, Para, Gridwise — testowanie czy żyją przed submit, V21)
- Argumentacja safety — kurier na rowerze/skuterze/aucie ma 5-15s na decyzję; OrderPilot redukuje distraction
- User benefit wyraźny — lepsze decyzje finansowe, zmniejszenie wypalenia, realne podniesienie earnings
- Precyzyjna narracja KD1 w każdym materiale submission

### Risk #2 — MediaProjection uznane za „unnecessary" lub „overly broad"

**Opis:** Reviewer czasem nie rozumie że accessibility tree nie zawiera React Native content (Uber Driver, Wolt Courier używają RN). Może argumentować „używacie accessibility = nie potrzebujecie screen capture". Jeśli uwierzy w to, policy mówi że **najmniej inwazyjna permisja** powinna być użyta — screenshot jest inwazyjniejszy niż accessibility, więc reject.

**Realne konsekwencje:**
- Reject z komentarzem „remove MediaProjection or justify why accessibility alone insufficient"
- Jeśli nie uda się obronić — trzeba usunąć Uber/Wolt support z v1.0 (drastyczne obcięcie funkcjonalności)

**Strategia obrony:**
- W Permissions Declaration **explicit** techniczne wyjaśnienie (React Native, Flutter embedded view — tekst nie w accessibility tree, mamy dowód)
- W video demo pokazać case gdzie accessibility tree jest pusty, a screenshot ma treść (reviewer zobaczy że to nie redundancy)
- Feature-flag MediaProjection jako optional runtime — user może używać apki tylko z accessibility (tracimy Uber/Wolt), ale pokazujemy że zbieramy tylko co potrzebne
- Foreground-check w kodzie — screenshot TYLKO gdy target delivery app jest foreground (V6)

### Risk #3 — Trademark / platform complaint (Uber/Wolt/Glovo/Bolt)

**Opis:** Screenshoty apki pokazujące UI Uber/Wolt/Glovo/Bolt na tle mogą być flagowane przez:
- Play Store policy na unauthorized use of third-party trademarks
- **Complaint ze strony samej platformy** — Uber ma pro-active policy przeciwko apkom które „hijack" driver flow. Platformy złożyły skargi w przeszłości na inne apki kurierskie.

**Realne konsekwencje:**
- Store listing reject z tytułu trademark — szybko naprawialny (zmiana screenshotów)
- Complaint ze strony Uber/Wolt/Glovo/Bolt po release = **app removal** — najgorszy scenariusz, apka usunięta z sklepu

**Strategia obrony:**
- Screenshoty pokazujące TYLKO naszą belkę na neutralnym tle (czarny wallpaper z delikatnymi generic ikonami platform, nie prawdziwe UI)
- Brand references w opisie tylko jako fact („compatible with Uber Driver, Wolt Courier, etc.") — nie „optimize Uber"
- Żadnych logo/marks platform konkurencji w assets
- Disclaimer „Not affiliated with Uber, Wolt, Glovo, or Bolt" (M21) w store description + Settings → About
- Financial disclaimer (M22)

### Risk #4 — Deceptive Behavior / Overlay misinterpretation

**Opis:** Apka która „monitors other apps" + wyświetla overlay jest blisko policy „Device and Network Abuse" + „Deceptive Behavior". Jeśli overlay może być wzięty za element platform competitor, Google może to odczytać jako deceptive.

**Realne konsekwencje:**
- Reject z tytułu „deceptive overlay" — apka może naśladować UI Ubera / Wolta
- Potencjalne escalation do account-level flag jeśli wielokrotnie

**Strategia obrony:**
- Overlay MUSI mieć wyraźny OrderPilot branding (logo A1 + nazwa widoczne w każdej instancji overlay)
- Overlay non-interactive z underlying app (nie blokuje przycisków accept/decline platform)
- Explicitly pokazać w video że OrderPilot **nie modyfikuje** zachowania monitored apps, tylko czyta
- Wording w Permissions Declaration: „OrderPilot does not interact with the delivery app. The user decides whether to accept or decline the offer"

### Risk #5 — Data Safety form mismatch z faktycznym kodem

**Opis:** Jeśli deklarujemy „no data collected" a kod loguje user data (logcat, log save feature, cache) — Google automated scan lub manual review może to znaleźć. Data Safety jest **legally binding** — mismatch to poważne naruszenie.

**Realne konsekwencje:**
- Reject z konkretnym feedbackiem „Data Safety declaration does not match app behavior"
- Przy powtarzającej się niezgodności: account-level action (developer reputation)
- Post-release: jeśli user zgłosi to Google, możliwe app removal + investigation

**Strategia obrony:**
- Pełen logcat audit (F6 + V4) — żaden log nie zawiera user data
- Save logs feature audit (F7 + V5) — zawartość pliku structural-only
- Android Vitals auto-collection explicit w Data Safety (jedyny legitimate data transfer do Google)
- ML Kit Text Recognition explicitly wymienione w Data Safety jako third-party on-device SDK
- AD_ID removal (M20) — brak AdID transmissions

### Risk #6 — Closed Testing requirement nie spełniony

**Opis:** Od 2023/2024 Google wymaga dla nowych developer accounts 12-20+ testers przez 14 dni na Closed Testing przed Production. Jeśli mamy tylko 3-5 testerów — nie będziemy mogli pushować do Production.

**Realne konsekwencje:**
- Production submit zablokowany do czasu spełnienia wymogu
- Delay release nawet o kilka tygodni (trzeba rekrutować)

**Strategia obrony:**
- Rekrutacja testerów ZANIM AAB gotowy (V16) — min 12, docelowo 20 z zapasem
- Lokalne grupy FB kurierskie jako kanał rekrutacji
- Dodawanie testerów z zapasem 3-5 „resserve" gdy niektórzy nie zainstalują

### Risk #7 — `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` nie kwalifikuje się

**Opis:** Google traktuje tę permisję jako restricted. Wymaga (a) uzasadnienia w Permissions Declaration, (b) qualifying use case. Lista qualifying use cases Google jest krótka — productivity/courier helper nie jest oczywisty.

**Realne konsekwencje:**
- Reject z „your app does not qualify for REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
- Workaround: usunąć permission, polegać tylko na `FOREGROUND_SERVICE_SPECIAL_USE` — straci część OEM compatibility (Xiaomi/Huawei kill apki częściej)

**Strategia obrony:**
- Permissions Declaration z argumentem „accessibility service requires continuous operation for core functionality"
- Plan B: usunąć permission jeśli review jest nieustępliwy; zaakceptować gorszą reliability na OEM z aggressive battery management

---

## 🎯 Definition of Ready for Submission

Minimalny stan do pierwszego submitu do Closed Testing track. Każdy element MUSI być zamknięty (nie in-progress, nie „prawie gotowe").

### Build & Signing

- [ ] Keystore wygenerowany + backup min 2 lokalizacje (M3 + V12)
- [ ] `signingConfig` w `build.gradle.kts` z credentials przez `~/.gradle/gradle.properties` (M3)
- [ ] Decyzja Play App Signing vs self-managed (V13)
- [ ] `versionCode=1`, `versionName="1.0.0"` (M3 + F8)
- [ ] AAB builds poprawnie lokalnie: `./gradlew bundleRelease`
- [ ] AAB zainstalowany + przetestowany na real device przez `bundletool` — fresh install flow działa

### Manifest & Resources

- [ ] `<queries>` declaration z 4 package names (M16, jeśli V2 potwierdzi użycie)
- [ ] AD_ID permission removed (M20)
- [ ] `allowBackup` decyzja: false OR wybiera explicitly co backupować (F3)
- [ ] `accessibility_config.xml` z `packageNames` constraint (F5 + M28)
- [ ] `accessibility_config.xml` bez zbędnych flags (F12)
- [ ] `accessibility_service_description` rozszerzony w 4 językach (F1 + F4)
- [ ] Wszystkie nowe disclosure strings w PL + EN (M13)
- [ ] `foregroundServiceType="mediaProjection"` + `PROPERTY_SPECIAL_USE_FGS_SUBTYPE` wording spójny z Permissions Declaration (F9)

### Code

- [ ] `DisclosureActivity` zaimplementowana + wpięta w flow przed SetupActivity (M1 + F2)
- [ ] `disclosureAcceptedVersion` consent flag w `SettingsRepository` (M5)
- [ ] In-app Privacy Policy link w Settings (M4)
- [ ] Settings → About z non-affiliation + financial disclaimer (M21 + M22)
- [ ] Logcat audit done + zero user data w logach (F6 + V4)
- [ ] Save logs feature audit done + plik bez user data (F7 + V5)
- [ ] Screen capture foreground check — tylko 4 target apps (F11 + V6)
- [ ] Onboarding flow test: fresh install → Disclosure → Setup → Main (V14)

### Privacy Policy & Data Deletion

- [ ] Privacy Policy tekst zgodny ze strukturą KD5 (M2)
- [ ] Third-party SDKs explicitly (ML Kit, AndroidX, kotlinx-serialization) (M2 + V1)
- [ ] Privacy Policy hosted na stabilnym URL (M2 + M29)
- [ ] Data Deletion Policy hosted na stabilnym URL (M25)
- [ ] Link do Privacy Policy w Play Console + w apce

### Store Listing Assets

- [ ] Ikona 512×512 (eksport z vectora A1) (M8)
- [ ] Feature graphic 1024×500 (M8)
- [ ] 4-6 screenshotów per locale (PL + EN) — bez logo platform konkurencji (M8 + M23)
- [ ] Short description (80 chars) PL + EN (M9)
- [ ] Long description (1500-3000 chars) PL + EN z non-affiliation + financial disclaimers (M9 + M21 + M22)
- [ ] Release notes template (M24)

### Video Demo

- [ ] Video 45-75s zgodnie z KD7 (M7)
- [ ] Real device, nie emulator (M7)
- [ ] Full first-run flow: install → disclosure → grant → overlay (M7)
- [ ] OrderPilot branding visible w każdym momencie (M7)
- [ ] YouTube unlisted upload + link zapisany do Permissions Declaration (M7)

### Play Console Setup

- [ ] Developer Account założony ($25) + identity verification (M19 + V9)
- [ ] Apka utworzona w Console, package `com.orderpilot.app` wolny (V8)
- [ ] Nazwa „OrderPilot" potwierdzona że nie zajęta (V7 + V22)
- [ ] App category = Productivity (M14)
- [ ] Target audience = 18+ (M12)
- [ ] Content Rating questionnaire wypełniony (M11)
- [ ] Data Safety form wypełniony „no data collected" + Android Vitals + ML Kit disclosed (M10 + V19)
- [ ] 4 osobne Permissions Declarations z wordingiem zgodnym z KD1/KD2/KD3 + F9 (M6)
- [ ] Privacy Policy URL + Data Deletion URL w Console
- [ ] Release Notes dla v1.0.0 (M24)

### Closed Testing

- [ ] Lista 12+ testers z Google accounts potwierdzonych (V10 + V16 + V17)
- [ ] Testers dodani do Closed Testing track w Console (M15)
- [ ] AAB uploaded do Closed Testing
- [ ] Opt-in link wysłany testerom + instrukcje
- [ ] Timer 14 dni startuje od instalacji przez pierwszego testera

### Pre-submit last-mile checks

- [ ] Fresh install test — disclosure pokazuje się, cancel zamyka apkę (V14)
- [ ] Debug artifacts scan — grep Build.SERIAL, getDeviceId, ANDROID_ID, getMac (V26)
- [ ] APK/AAB analyzer — brak debug symbols, test keys (V25)
- [ ] Policy refresh — sprawdzić że „Alternative use of accessibility" policy nie zmieniła się od naszej analizy (V24)
- [ ] Precedens check — Gridwise/Para/RideHelper żyją na Play Store (V21)
- [ ] Graceful degradation test — user wyłącza accessibility mid-session, apka nie crashuje (V18)

### Kryterium GO/NO-GO submitu

**GO tylko jeśli:**
- Wszystkie P0 MISSING zamknięte
- Wszystkie P0 NEEDS FIX zamknięte
- Wszystkie P0 VERIFY rozstrzygnięte
- Closed Testing track live od minimum 14 dni
- Minimum 12 testerów zainstalowało i używa (nie tylko dodani, realnie aktywni)
- Brak negative feedback od testerów blokującego core functionality

**NO-GO jeśli:**
- Którykolwiek P0 open
- Policy „Alternative use of accessibility" zmieniła się i nasza narracja jest niespójna
- Któryś z precedensów (RideHelper/Gridwise/Para) został usunięty z Play Store w ostatnich 30 dniach (sygnał że Google zmienił interpretację)

---

## Załącznik: Pliki przejrzane vs niesprawdzone

**Przejrzane (v1 + v2):**
- `OrderPilot/app/build.gradle.kts`
- `OrderPilot/app/src/main/AndroidManifest.xml`
- `OrderPilot/app/src/main/res/xml/accessibility_config.xml`
- `OrderPilot/app/src/main/res/values/strings.xml` + `values-en` + `values-uk` + `values-ru` (tylko accessibility_service_description)
- `OrderPilot/app/src/main/java/com/orderpilot/app/ui/SetupActivity.kt`
- `OrderPilot/app/proguard-rules.pro`
- Grep: brak INTERNET, Firebase, retrofit, okhttp, HttpURLConnection

**Niesprawdzone (do fazy implementacyjnej):**
- Pełny `MainActivity.kt`
- `OrderPilotAccessibilityService.kt` (potrzebne do Permissions Declaration wording)
- Pełny `libs.versions.toml`
- `SettingsActivity.kt` (gdzie wpiąć Privacy Policy link)
- `ScreenCaptureService.kt` + `PopupCropper.kt` (V6)
- Wszystkie parsery + engine
- `res/xml/backup_rules.xml` + `data_extraction_rules.xml` (V20)
- Wszystkie `AppLog.*` / `Log.*` call sites (V4)
- Save logs implementation (V5)
