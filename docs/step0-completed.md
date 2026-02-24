# Krok 0: Instalacja środowiska - UKOŃCZONE

**Data:** 2026-02-24
**Model:** Sonnet 4.5

## ✅ Co zainstalowano:

1. **Android Studio**
   - Lokalizacja: `/Applications/Android Studio.app`
   - Komenda CLI: `studio`

2. **JDK 17**
   - Zainstalowano przez Homebrew
   - Lokalizacja: `/opt/homebrew/opt/openjdk@17`

3. **Zmienne środowiskowe**
   - Dodano do `~/.zshrc`:
     ```bash
     export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null)
     export ANDROID_HOME=$HOME/Library/Android/sdk
     export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
     ```

## 📝 Następne kroki (ręczne):

### 1. Pierwsze uruchomienie Android Studio

```bash
open -a "Android Studio"
```

Lub kliknij ikonę Android Studio w Applications.

### 2. Setup Wizard:

1. **Welcome** → `Next`
2. **Install Type** → `Standard` → `Next`
3. **Select UI Theme** → wybierz (Darcula lub Light) → `Next`
4. **Verify Settings** → sprawdź czy pobiera:
   - Android SDK
   - Android SDK Platform (API 26+)
   - Android SDK Build-Tools
   - Android SDK Platform-Tools
   - Android Emulator (opcjonalnie)
5. Kliknij `Finish` → poczekaj na instalację (może potrwać 10-15 min)

### 3. Konfiguracja JDK w Android Studio:

Po instalacji SDK:
1. `File` → `Project Structure` (lub ⌘+;)
2. `SDK Location`
3. `JDK location` → kliknij folder → wybierz:
   ```
   /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
   ```
4. `Apply` → `OK`

### 4. Podłączenie telefonu Android:

**Na telefonie:**
1. `Settings` → `About phone`
2. Kliknij `Build number` 7 razy → "You are now a developer!"
3. Wróć → `Developer options`
4. Włącz `USB debugging`
5. Podłącz USB do Maca

**Na Macu:**
```bash
adb devices
```

Powinno pokazać:
```
List of devices attached
XXXXXXXXXX    device
```

Jeśli `unauthorized` → zaakceptuj dialog na telefonie.

### 5. Weryfikacja:

```bash
# Sprawdź Android SDK
ls $ANDROID_HOME/platform-tools

# Sprawdź adb
adb version

# Sprawdź telefon
adb devices
```

## ⚠️ Problemy / Rozwiązania:

**Problem:** `adb: command not found`
**Rozwiązanie:**
```bash
source ~/.zshrc
# lub zamknij i otwórz terminal ponownie
```

**Problem:** Telefon pokazuje `unauthorized`
**Rozwiązanie:**
- Odłącz USB
- Na telefonie: `Developer options` → `Revoke USB debugging authorizations`
- Podłącz ponownie → zaakceptuj dialog

**Problem:** Android Studio nie widzi SDK
**Rozwiązanie:**
- `File` → `Settings` → `Appearance & Behavior` → `System Settings` → `Android SDK`
- Sprawdź czy SDK Location = `$HOME/Library/Android/sdk`

---

**Status:** Krok 0 ukończony ✅
**Następny krok:** Krok 1 - Szkielet projektu + uprawnienia (Sonnet 4.5)
