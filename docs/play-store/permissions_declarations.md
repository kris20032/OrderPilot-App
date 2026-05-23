# Play Console — Permissions & Sensitive APIs Declarations

Przy upload AAB Google Play Console wykrywa „sensitive permissions" i wymaga **pisemnego uzasadnienia** dla każdego. Uzasadnienia wpisuje się po angielsku w formularzu „Declarations" w Play Console.

Ten dokument ma gotowe, przetestowane przez review teksty do skopiowania.

---

## 1. Accessibility Service (KRYTYCZNE — najwyższe ryzyko rejection)

**Gdzie w Play Console:** App content → Accessibility

**Pytanie Play Console:** „Tell us how your app uses Accessibility Service."

### Tekst do wklejenia (EN):

```
OrderPilot is a productivity tool for food and ride-share couriers (Uber,
Wolt, Glovo, Bolt Food). The app uses AccessibilityService SOLELY to
detect when an offer popup appears in one of these four courier apps and
to read the offer text (distance, time, amount) so the user can instantly
see the computed hourly rate (profitability).

This is an "alternative use" of Accessibility per Google policy:
- The user is a professional courier making real-time decisions (accept
  or reject a delivery offer) in a 5-10 second window while driving.
- Reading the offer text with a screen reader manually is not practical.
- The app provides visible accessibility benefits: large, color-coded
  overlay (green/yellow/red) that is easier to read than raw offer data,
  and auditory/visual feedback for drivers who may have difficulty with
  small on-screen text while working.

Scope limitation:
- The AccessibilityService is configured with a strict package name
  filter: it activates ONLY on the four courier apps listed above (see
  accessibility_config.xml, `android:packageNames`).
- On all other apps and system screens, the service is inactive.
- The service does not browse history, messages, contacts, or other
  accessibility data.

Prominent Disclosure:
- Before the user is prompted to grant Accessibility permission, the app
  shows a full-screen Prominent Disclosure activity explaining:
  - What accessibility is used for (reading courier offer text)
  - What it is NOT used for (no browsing history, no messages, no data
    transmission)
  - That all processing is on-device
  - That the user can revoke at any time via Settings
- The disclosure must be explicitly accepted before any permission is
  requested. Accepting only dismisses the dialog — the user is still
  taken to Android Settings to grant the permission.

Data handling:
- Offer text read via accessibility is analyzed locally (hourly rate
  computation), displayed in the overlay, and discarded.
- NO accessibility data is transmitted off the device. The app does not
  hold the `android.permission.INTERNET` permission (verifiable in the
  manifest), making transmission technically impossible.

Privacy Policy: https://kris20032.github.io/OrderPilot-App/legal/privacy-policy.html
```

**Długość:** ~300 słów. Review zazwyczaj wymaga 100-500 słów dla accessibility.

### Ryzyko rejection (high) — co przygotować na odpowiedź

Jeśli Google odrzuci, typowe powody:
1. „Not core functionality" — ODPOWIEDŹ: accessibility IS core (bez niej apka nie wykrywa ofert)
2. „No alternative mechanism" — ODPOWIEDŹ: MediaProjection na starszych API jest jedyną alternatywą, ale wymaga powtarzalnej zgody usera każdorazowo → UX nie do przyjęcia dla 5-second decision window. Accessibility jest jedynym workable mechanizmem dla API 30+.
3. „Missing prominent disclosure" — już mamy (Batch 3)

---

## 2. SYSTEM_ALERT_WINDOW (overlay nad innymi apkami)

**Gdzie:** Play Console wyłapie automatycznie przy review.

### Uzasadnienie (EN):

```
OrderPilot displays a small, semi-transparent overlay bar (1-2 lines,
~60dp high) above the courier app (Uber, Wolt, Glovo, Bolt Food) when an
offer is detected. The overlay shows: distance, time, amount, and the
computed hourly rate (zł/h) color-coded green (profitable), yellow
(marginal), or red (unprofitable).

This overlay is the core user experience — the courier needs to see the
computed profitability within the 5-10 second window during which the
offer is visible. Displaying it inside our own app would require the
user to switch apps (not possible during the offer countdown).

The overlay is:
- Only visible while one of the four supported courier apps is in the
  foreground
- Auto-hidden when the offer expires or the user switches apps
- Draggable vertically (user preference, persisted)
- Dismissable with an X button

SYSTEM_ALERT_WINDOW is the standard Android mechanism for this use case
(same as accessibility volume overlays, password managers like
Bitwarden, translation apps, etc.).
```

---

## 3. FOREGROUND_SERVICE + FOREGROUND_SERVICE_SPECIAL_USE

**Play Console pytanie:** „Why does your app use a foreground service with type 'specialUse'?"

### Uzasadnienie (EN):

```
OrderPilot runs a foreground service during active offer monitoring in
order to:
1. Keep the AccessibilityService bound while the screen is on — without
   a foreground anchor, Android aggressively restricts accessibility
   services in the background, which would cause missed offers for the
   courier user.
2. Display the persistent "Offer detection active" notification (as
   required by foreground service policy).

Why 'specialUse' rather than a typed foreground service:
- The service does not fit any of the predefined types (camera,
  location, mediaPlayback, mediaProjection, phoneCall, connectedDevice,
  dataSync, health, remoteMessaging, systemExempted).
- Its purpose is ambient awareness of courier offer popups, which is
  not covered by any existing type.

On Android versions where MediaProjection is used (API 29 and below for
some courier apps), FOREGROUND_SERVICE_MEDIA_PROJECTION is declared as
an additional type on the same service.

Duration: the service runs only while the user has "Monitoring" enabled
in the app (togglable from the main screen). It stops automatically when
the user disables monitoring, and on device reboot (the user must
re-enable).
```

---

## 4. POST_NOTIFICATIONS

**Zwykle nie wymaga declaration** — standardowy permission na API 33+.

### Krótkie uzasadnienie (jeśli zapytają):

```
Used to display the persistent "Offer detection active" notification
required by Android foreground service policy, plus optional
notifications for accessibility rebind hints (when the OS unbinds the
service after app reinstall, prompting the user to toggle permission
off/on).
```

---

## 5. RECEIVE_BOOT_COMPLETED

### Uzasadnienie (EN):

```
If the user had enabled offer monitoring before rebooting the phone,
OrderPilot re-enables it after boot so they don't miss offers during
their first shift after restart. The service only starts if the user
had explicitly enabled monitoring before the reboot (checked via
persisted preference). On fresh install or after the user disabled
monitoring, the boot receiver does nothing.
```

---

## 6. WAKE_LOCK

**Zwykle automatyczny, bez declaration** — ale jeśli pytają:

```
Short-duration wake lock (< 1 second) held around takeScreenshot()
calls on API 30+ to prevent the screen from being dimmed mid-capture,
which would produce unreadable OCR input. No long-running wake locks.
```

---

## 7. FOREGROUND_SERVICE_MEDIA_PROJECTION

Używany tylko na API ≤29 gdy takeScreenshot() jest niedostępne.

### Uzasadnienie (EN):

```
On Android API 29 and below, AccessibilityService.takeScreenshot() is
not available. To perform OCR on courier offer popups that do not
expose text through accessibility (React Native apps), OrderPilot falls
back to MediaProjection for screen capture. The user grants
MediaProjection permission once in the setup wizard. Captured frames
are processed locally (ML Kit text recognition) and immediately
discarded — no screen contents are ever stored or transmitted.

On API 30+ this fallback is not used (AccessibilityService.takeScreenshot
is used instead), but the permission remains declared for backward
compatibility.
```

---

## Checklista przed submit

- [ ] Privacy Policy URL jest LIVE (testowany w przeglądarce incognito przed submit)
- [ ] Data Deletion URL jest LIVE
- [ ] Accessibility declaration wklejona (~300 słów)
- [ ] Screenshoty pokazują Prominent Disclosure (z Batch 3)
- [ ] Screenshoty pokazują overlay w działaniu (z Phase 5)
- [ ] Video preview pokazuje pełny flow: disclosure → permission grant → overlay (opcjonalne ale zalecane)

---

**Data utworzenia dokumentu:** 2026-04-21
**Wersja:** 1.0
