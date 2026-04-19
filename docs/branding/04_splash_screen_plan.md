# 04 · Splash Screen Plan — Android 12+ SplashScreen API

**Status:** 🟡 ZAIMPLEMENTOWANY ale **SPLASH NIE WIDOCZNY** przy cold start — do zbadania w kolejnej sesji
**Zależy od:** ✅ Logo A1 zaimplementowane (`c9d692d`)
**Branch:** `feature/splash-screen` (utworzony z `polishing`, nie zmergowany)
**Data implementacji:** 2026-04-19

---

## ⚠️ Status po pierwszym teście (2026-04-19)

**Problem:** Aplikacja otwiera się za szybko — splash nie jest widoczny wcale. Nawet próba z debugowym `setKeepOnScreenCondition` trzymającym splash 2s nie pomogła — użytkownik dalej go nie widział.

**Hipotezy do sprawdzenia w kolejnej sesji:**
1. **Gradle sync / build cache** — może APK nie został przebudowany po dodaniu dependency/theme (spróbować `Build → Clean Project` + `Rebuild`)
2. **Theme inheritance** — `Theme.SplashScreen` może nie być dostępny bez prawidłowego parentu; sprawdzić czy `androidx.core:core-splashscreen:1.0.1` faktycznie zadziałał (dostępność `@style/Theme.SplashScreen` w merged resources)
3. **MainActivity theme override** — może jakiś inny theme override (np. w `attachBaseContext` lub gdzieś indziej) nadpisuje splash theme przed system handoff
4. **Warm start vs cold start** — splash pokazuje się TYLKO przy cold start (app killed, process not in memory). Jeśli aplikacja była w recents, to warm start = brak splasha. Test: force-stop z Settings → Apps, potem launch.
5. **OEM quirk (Samsung)** — niektóre Samsung skórki (OneUI) mają własne override splash behavior, zwłaszcza na launcher z „clean speed animation"
6. **installSplashScreen() timing** — upewnić się że wywołanie jest naprawdę pierwsze w `onCreate`, przed innymi side-effectami (np. `attachBaseContext` już tworzy LocaleHelper → może być problem z ordering)

**Następny krok:** Przed merge — zdiagnozować czemu splash nie jest widoczny. Opcje debug:
- Dodać `AppLog.d` tuż po `installSplashScreen()` żeby potwierdzić że linia się wykonuje
- Sprawdzić w `adb logcat | grep SplashScreen` podczas cold startu
- Zajrzeć do `merged_manifest.xml` w build output żeby potwierdzić że theme się zaaplikował

---

---

## Cel

Podmienić domyślny biały/czarny splash (window background) na **dedykowany splash ekran z logo A1** używając Android 12+ SplashScreen API (z compat shim dla API 26-30 via `androidx.core:core-splashscreen`).

**Zasada:** minimalny setup. Żadnych animacji niestandardowych, żadnego custom splash screen layoutu, żadnego opóźnienia ponad default.

---

## Jak wykorzystujemy logo A1

- **Ikona na splashu:** `@drawable/ic_launcher_foreground` — gotowy vector A1 (ring + strzała + ticki), viewport 1024×1024
- **Tło splashu:** `@color/logo_background` = `#0A1220`
- **Brak tekstu** (nazwa „OrderPilot" nie jest potrzebna — ikona już zawiera identity)
- **Brak animacji** (static icon — Android 12+ nadal robi subtle „reveal" animation systemowo)

**Dlaczego reuse `ic_launcher_foreground`:**
- Zero duplikacji — ten sam asset co adaptive icon foreground
- Safe zone 62.5% (nasz) vs 66.67% (Android splash spec) → ring + strzała gwarantowanie widoczne, ticki mogą być lekko przycięte na części urządzeń (akceptowalne — identity trzyma ring+strzała)
- Jeśli testy pokażą że ticki przeszkadzają → zamień na `@drawable/ic_launcher_monochrome` (bez tickków) jako splash icon. Trivia zmiana jednego atrybutu w themes.xml.

---

## Pliki do zmiany

### 1. `app/build.gradle.kts` (lub `.gradle`) — dodaj dependency

```kotlin
dependencies {
    implementation("androidx.core:core-splashscreen:1.0.1")
    // ... existing deps
}
```

**Dlaczego:** core-splashscreen zapewnia forward compatibility — SplashScreen API działa od API 21+, natywnie od API 31. Min SDK OrderPilot = 26 (Android 8), więc potrzebujemy shima.

### 2. `res/values/themes.xml` — dodaj splash theme

Dodać na końcu (przed `</resources>`):

```xml
<!-- Splash screen theme — Android 12+ SplashScreen API -->
<style name="Theme.OrderPilot.Splash" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/logo_background</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
    <item name="postSplashScreenTheme">@style/Theme.OrderPilot</item>
</style>
```

**3 atrybuty (to cała konfiguracja):**
- `windowSplashScreenBackground` — tło pełnoekranowe (navy)
- `windowSplashScreenAnimatedIcon` — ikona na środku (A1 foreground)
- `postSplashScreenTheme` — theme na który apka przełącza się po splashu (nasz main theme)

### 3. `res/values-night/themes.xml` — nic nie zmieniamy

Night theme dziedziczy po Base.Theme.OrderPilot który już wymusza light mode (`parent="Theme.Material3.Light.NoActionBar"`). Splash jest agnostyczny — `logo_background` to fixed navy, niezależnie od trybu. **Brak zmian.**

### 4. `AndroidManifest.xml` — zmień theme MainActivity

```xml
<activity
    android:name=".ui.MainActivity"
    android:exported="true"
    android:theme="@style/Theme.OrderPilot.Splash">    <!-- DODAJ tę linię -->
    ...
</activity>
```

**Dlaczego tylko MainActivity:** splash pokazuje się tylko przy cold start. Launcher activity = MainActivity. SetupActivity i SettingsActivity nie mają intent-filter LAUNCHER, więc pozostają bez zmian.

### 5. `MainActivity.kt` — dodaj `installSplashScreen()`

```kotlin
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()    // <-- DODAJ PRZED super.onCreate()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ... reszta bez zmian
    }
}
```

**Kolejność krytyczna:** `installSplashScreen()` MUSI być przed `super.onCreate()` — inaczej splash się nie wyświetli.

---

## Co NIE robimy (świadomie, no over-engineering)

- ❌ **Animated icon** — AnimatedVectorDrawable dla splash. Pomijamy. Static icon wystarcza, animacja zwiększa złożoność bez ROI.
- ❌ **Custom exit animation** — `setOnExitAnimationListener` do custom fade-out. Pomijamy. Default animation jest OK.
- ❌ **Opóźnianie splashu** — `setKeepOnScreenCondition` do trzymania splashu dłużej (np. na ładowanie zasobów). Pomijamy — splash powinien być krótki, jeśli app potrzebuje czasu na init, to inne miejsce do optymalizacji.
- ❌ **Osobny splash layout XML** — pozostajemy na system-drawn splash. Custom layout = bypass SplashScreen API = stara szkoła, nie robimy.
- ❌ **Branding string (nazwa apki pod ikoną)** — można via `windowSplashScreenBrandingImage`. Pomijamy — zbędne.

---

## Ryzyka

### R1 — Ticki przycięte przez splash icon bounds
**Opis:** Splash icon area to 288dp canvas z 192dp safe zone (66.67%). Nasz A1 ma safe zone 62.5%, ticki w ~82% canvasu. Na niektórych urządzeniach (zwłaszcza z circular splash mask) ticki mogą być przycięte.
**Mitigation:** jeśli wystąpi, zamień `windowSplashScreenAnimatedIcon` na `@drawable/ic_launcher_monochrome` (bez tickków, tylko ring + arrow). Jedna linia zmiany.
**Severity:** Low — nawet bez tickków identity A1 jest zachowana.

### R2 — Kolor ikony na splashu
**Opis:** `ic_launcher_foreground` używa pomarańczu `#FF6B2C`. Na splashu z tłem `#0A1220` kontrast jest pełny — OK.
**Severity:** None.

### R3 — MainActivity startup impact
**Opis:** `installSplashScreen()` dodaje minimalny overhead (~1ms). Nie wpływa na rzeczywisty czas ładowania.
**Severity:** None.

### R4 — Compat shim vs native SplashScreen (API 26-30 vs 31+)
**Opis:** Na API 26-30 core-splashscreen używa shima, który może wyglądać nieco inaczej niż native API 31+ splash.
**Mitigation:** testujemy na urządzeniu produkcyjnym taty (Xiaomi, Android ? — trzeba sprawdzić) i u Krzysztofa (Samsung).
**Severity:** Low.

---

## Checklist implementacji

- [ ] Branch `feature/splash-screen` z `polishing`
- [ ] `build.gradle(.kts)`: dodać dependency `androidx.core:core-splashscreen:1.0.1`
- [ ] `themes.xml`: dodać `Theme.OrderPilot.Splash`
- [ ] `AndroidManifest.xml`: MainActivity `android:theme="@style/Theme.OrderPilot.Splash"`
- [ ] `MainActivity.kt`: `installSplashScreen()` przed `super.onCreate()`
- [ ] Sync Gradle + build
- [ ] Test na urządzeniu: cold start → zobacz splash
- [ ] Screenshot / video splashu → weryfikacja wizualna
- [ ] Commit: `feat(branding): splash screen z logo A1 (Android 12+ SplashScreen API)`
- [ ] Merge `feature/splash-screen` → `polishing`

---

## Przewidywany rezultat

**Cold start OrderPilot →**
1. Użytkownik kliknie ikonkę na launcherze
2. Splash: pełnoekranowe tło `#0A1220` + centered ikona A1 (ring + strzała + ticki w pomarańczu)
3. Android 12+ robi subtle „reveal" animation (ikona lekko pulsuje/pojawia się)
4. Po ~0.5-1s splash znika → MainActivity pokazuje się
5. Seamless przejście (tło MainActivity jasne — kontrastowy flash, ale akceptowalny)

**Opcjonalnie (jeśli chcemy smooth handoff):** MainActivity może mieć tło `#0A1220` w pierwszym frame'ie (cross-fade z splashu), potem fade-in normalnego UI. **Out of scope tego taska.**
