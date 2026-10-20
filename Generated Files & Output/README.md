# Rifat App Locker — Version 2.0.0 Release Documentation

**Rifat App Locker** is a high-performance, enterprise-grade, native Android application designed and engineered by **Molla Mohammad Rifat Osama**. It provides robust, on-device application locking using PIN/Password, 3×3 Pattern, and Biometrics (Fingerprint and Face Unlock) backed by native Android Accessibility Services and Usage Access monitoring.

---

## 📌 Application Identity

- **Application Name**: Rifat App Locker
- **Developer Name**: Molla Mohammad Rifat Osama
- **Application ID**: `com.rifat.applocker`
- **Version Name**: `2.0.0`
- **Version Code**: `2`
- **Platform**: Android (Min SDK: 24, Target SDK: 36)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Target / Compatibility Device**: TECNO Spark Go 2024 (BG6), Android 13, HiOS

---

## 📂 Output Artifacts Directory Structure

```
Generated Files & Output/
├── Rifat-App-Locker-v2.0.0.apk           (24.7 MB Debug APK)
├── Rifat-App-Locker-v2.0.0-Source.zip    (Complete source code archive)
└── README.md                             (This release documentation)
```

- **Compiled APK Location**:
  `Generated Files & Output/Rifat-App-Locker-v2.0.0.apk`
- **Source Code Archive**:
  `Generated Files & Output/Rifat-App-Locker-v2.0.0-Source.zip`

---

## 🚀 What's New in Version 2.0.0

### 1. Clickable Dashboard Statistics Cards
- **Total Apps**: Tapping the card opens the Manage Apps screen displaying all compatible installed applications.
- **Locked Apps**: Tapping navigates directly to the Manage Apps screen pre-filtered to show only currently locked applications.
- **Unlocked Apps**: Tapping navigates directly to show only unlocked applications.
- **Enforcement Health Banner**: If the required accessibility or usage access permissions are not yet granted, the dashboard clearly displays "Setup Required (Not Enforced)" with a direct setup prompt, rather than falsely claiming selected apps are active.

### 2. Complete Manage Apps Screen
- Live display of installed apps with real app icons, labels, package names, and protection badges.
- Search filter for instantly locating apps by name or package identifier.
- Filter chips: `All`, `Locked`, and `Unlocked` with live synchronized counts.
- Bulk actions: `Select All Compatible Apps` (Lock All) and `Deselect All` (Unlock All).
- Safe self-protection: Excludes `com.rifat.applocker` from accidental self-lockouts unless explicitly activated in Security Settings.
- Persistent lock states stored reliably in local application storage.

### 3. Actual App-Lock Enforcement Engine
- Zero-latency window transition detection using `AccessibilityService` (`TYPE_WINDOW_STATE_CHANGED`).
- Dual foreground fallback monitoring using `UsageStatsManager` and `AppLockerForegroundService`.
- Configurable relock policies:
  - **Immediately on Exit**: Relocks as soon as the user exits the app.
  - **When Screen is Locked**: Keeps unlocked until device screen turns off.
  - **After 1 Minute**: Grants a 60-second grace period.
- Intercepts hardware and gesture back navigation on `LockOverlayActivity` to safely redirect to the Android launcher Home screen.

### 4. High-Visibility 3×3 Pattern Grid (FIXED)
- **High-contrast outer rings and solid inner dots**: Clearly visible in both light (white card) and dark themes.
- Touch drag tracking with smooth line rendering, glowing visited nodes, and dynamic pointer connection.
- High-contrast highlight for the active node and clear error/success animations.

### 5. Optional Biometric Authentication
- **Fingerprint Unlock**: Optional AndroidX Biometric integration using official system prompts.
- **Face Unlock**: Optional Android biometric face detection support.
- **Biometric Fallback**: Primary PIN/Password or Pattern remains mandatory; biometrics serve as a seamless accelerator.

### 6. Emergency Recovery & WhatsApp Developer Support
- **Security Question & Answer Verification**: Reset or bypass locked screens with salted cryptographic answer hashing (case-insensitive).
- **Contact Developer on WhatsApp**:
  - Direct link opening `https://wa.me/<DEVELOPER_WHATSAPP_NUMBER>` with pre-filled support message.
  - Prefers official WhatsApp client with fallback to browser.
  - Never displays developer phone number in the public UI.
  - Configurable in Security Settings and Recovery dialog.

### 7. Developer Attribution & Telegram Channels
- Author: **Molla Mohammad Rifat Osama**
- Clean contact cards displaying only:
  - **Telegram icon** + **"Telegram Channel"** -> opens `https://t.me/RiFAT_TECH_BD`
  - **Telegram icon** + **"Telegram Profile"** -> opens `https://t.me/Molla_Mohammad_Rifat_Osama`
- Prefers native Telegram app if installed.
- No raw URLs displayed in UI.
- No artificial verification badges.
- Includes Privacy Policy and Open-Source Apache 2.0 license disclosures.

---

## 🛠️ Build and Compilation Instructions

### Prerequisites
- JDK 17 or higher
- Android SDK 36 (Compile SDK) / Android SDK 24 (Min SDK)
- Gradle 8.x / AGP 9.1.1

### Build the APK
```bash
gradle :app:assembleDebug
```
Output artifact generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Run Local JVM Unit & Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```
All unit tests, including security hashing, pattern string serialization, recovery questions, preferences, and bilingual strings pass with 100% success.

---

## 📱 Installation & Setup Guide

1. **Install the APK**:
   Copy `Generated Files & Output/Rifat-App-Locker-v2.0.0.apk` to your Android device and install it.
2. **Setup Credentials**:
   On first launch, choose **Password/PIN** or **3×3 Pattern**, confirm your code, and answer a recovery security question.
3. **Grant Required Android Permissions**:
   - **Accessibility Service**: Enable `Rifat App Locker Protection` in `Settings > Accessibility`.
   - **Usage Access**: Enable Rifat App Locker in `Settings > Apps > Special app access > Usage access`.
   - **Display Over Other Apps**: Grant overlay permission.
   - **Battery Optimization / Auto-Start** (TECNO / HiOS / MIUI): Tap "Open Auto-Start Manager" in Troubleshooting to whitelist Rifat App Locker.
4. **Lock Apps**:
   In the **Manage Apps** screen, toggle any app you wish to guard. Launch the target app from your home screen to verify instant lock overlay protection!

---

## 🔒 Privacy & Zero-Cloud Guarantee

Rifat App Locker operates **100% strictly on-device**. No cloud telemetry, no analytics, no external servers, and zero data leaves your phone. All passwords, patterns, and recovery answers are protected using salted SHA-256 cryptographic hashing within local sandboxed preferences.
