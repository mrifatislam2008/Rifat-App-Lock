# Rifat App Locker

**Rifat App Locker** is a native, high-performance, enterprise-grade Android application built by **Molla Mohammad Rifat Osama**. It protects apps on device with PIN/Password and an interactive 3×3 Pattern Lock using native Accessibility and Usage Stats foreground detection.

---

## 🛡️ Core Highlights

- **Native App Locking Engine**: Uses `AccessibilityService` (`TYPE_WINDOW_STATE_CHANGED`) for instant window-level detection without polling lag, paired with `UsageStatsManager` and `ForegroundService` dual monitoring.
- **Bi-Method Security**:
  - **Password / PIN**: Alphanumeric or numeric credential with salted SHA-256 cryptographic verification.
  - **3×3 Pattern Grid**: Canvas-based interactive 3×3 pattern grid with line drag physics, dynamic glow, and tactile haptic feedback.
  - **Safe Recovery**: Security question and answer verification to reset credentials without accidental lockouts.
- **Manage Locked Apps Screen**:
  - Live query of installed applications with real app icons, names, and package names.
  - Search apps by title or package name.
  - Quick filter chips: `All`, `Locked`, `Unlocked`.
  - Bulk actions: `Select All Compatible Apps` and `Deselect All`.
  - Instant toggle switch on each app card with visual feedback.
  - Excludes Rifat App Locker from accidental self-lock (unless explicitly enabled).
- **Dashboard**:
  - Real-time stat counters: Total Apps, Locked Apps, Unlocked Apps.
  - Master Protection Switch (Enable / Pause Locker).
  - System permission health checker.
- **Device & OEM Compatibility**:
  - Designed for Android 13 and tested on TECNO Spark Go 2024 (model BG6, HiOS).
  - Guided instructions for OEM background restrictions, Auto-start, and Recents tray locking.
- **Bilingual Interface**: Full native English and Bengali (বাংলা) language support with instant in-app switching.
- **100% On-Device Privacy**: Zero analytics, zero cloud syncing, zero remote data transmission.

---

## 👨‍💻 Developer Information

- **Developer Name**: Molla Mohammad Rifat Osama
- **Role**: Lead Developer & Android Security Architect
- **Telegram Channel**: [https://t.me/RiFAT_TECH_BD](https://t.me/RiFAT_TECH_BD)
- **Telegram ID / Profile**: [@Molla_Mohammad_Rifat_Osama](https://t.me/Molla_Mohammad_Rifat_Osama) (Direct: [https://t.me/Molla_Mohammad_Rifat_Osama](https://t.me/Molla_Mohammad_Rifat_Osama))

---

## 📦 Build Artifacts & Output Paths

- **Debug APK Location**:
  `app/build/outputs/apk/debug/app-debug.apk`
- **Full Project Source ZIP**:
  `rifat-app-locker-project.zip`

---

## 🔑 Permissions & Justifications

| Permission | Purpose |
|---|---|
| `QUERY_ALL_PACKAGES` | Allows querying installed user applications so they can be listed for locking. |
| `PACKAGE_USAGE_STATS` | Used by the background monitoring engine to verify foreground application states. |
| `SYSTEM_ALERT_WINDOW` | Enables drawing the secure lock screen overlay over protected applications. |
| `BIND_ACCESSIBILITY_SERVICE` | Enables zero-delay real-time window transition events (`TYPE_WINDOW_STATE_CHANGED`). |
| `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_SPECIAL_USE` | Keeps the protection service persistent and prevents background termination. |
| `POST_NOTIFICATIONS` | Displays the mandatory persistent service notification on Android 13+. |
| `RECEIVE_BOOT_COMPLETED` | Restores protection automatically when the device powers on or restarts. |
| `VIBRATE` | Provides tactile feedback when nodes are selected in the pattern grid or PIN entry. |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Whitelists the app against aggressive OEM battery killers (HiOS, MIUI, etc.). |

---

## 📱 How to Install and Test on Android Phone

1. **Copy the APK**:
   Transfer `app/build/outputs/apk/debug/app-debug.apk` to your phone via USB or download it directly.
2. **Install**:
   Open the file in your device's file manager and tap **Install** (allow "Install from Unknown Sources" if prompted).
3. **First-Launch Onboarding**:
   - Tap **Get Started** on the Welcome screen.
   - Choose your preferred Lock Method: **Password** or **Pattern**.
   - Create and confirm your credential.
   - Choose a Security Recovery Question and provide an answer.
4. **Grant Permissions**:
   - Enable **Accessibility Service** (`Rifat App Locker Protection`).
   - Enable **Usage Access** in Android Settings.
   - Enable **Display Over Other Apps**.
   - (For TECNO / HiOS / MIUI devices): Tap **Open Auto-Start Manager** and toggle on Auto-Start.
5. **Lock Apps**:
   - Navigate to **Manage Apps**.
   - Tap the switch next to any app (e.g. Chrome, WhatsApp, Gallery).
   - Launch the protected app from your phone's home screen. The Rifat App Locker screen will instantly intercept and require authentication!
