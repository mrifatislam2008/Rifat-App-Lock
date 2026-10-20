# Rifat App Locker — Version 2.0.0

**Rifat App Locker** is a native, high-performance, enterprise-grade Android application built by **Molla Mohammad Rifat Osama**. It protects apps on device with PIN/Password, 3×3 Pattern, and optional Biometrics (Fingerprint and Face Unlock) backed by real-time Accessibility Service and Usage Access monitoring.

---

## 🛡️ Version 2.0.0 Core Highlights

- **Application Identity**:
  - Name: `Rifat App Locker`
  - Developer: `Molla Mohammad Rifat Osama`
  - Application ID: `com.rifat.applocker`
  - Version: `2.0.0` (VersionCode: `2`)
  - Target / Compatibility Device: TECNO Spark Go 2024 (BG6), Android 13, HiOS
- **Clickable Dashboard**:
  - `Total Apps`, `Locked Apps`, and `Unlocked Apps` cards are interactive and navigate directly to pre-filtered lists.
  - Honest enforcement status indicating when accessibility/usage permissions are required.
- **Complete Manage Apps Screen**:
  - Live query of installed applications with real app icons, names, and package names.
  - Search apps by title or package name.
  - Quick filter chips: `All`, `Locked`, `Unlocked`.
  - Bulk actions: `Select All Compatible Apps` (Lock All) and `Deselect All` (Unlock All).
  - Safe self-protection against accidental lockouts.
- **Fixed Pattern Grid Visibility**:
  - High-contrast outer rings and solid inner dots clearly visible on light/white card backgrounds as well as dark themes.
  - Smooth drag tracking, glowing visited nodes, and dynamic pointer connection line.
- **Optional Biometric Authentication**:
  - Independent toggles for **Fingerprint Unlock** and **Face Unlock** using AndroidX Biometrics.
  - Primary PIN/Password or Pattern remains mandatory; biometrics serve as quick unlock.
- **Emergency Recovery & WhatsApp Support**:
  - Salted cryptographic security question & answer recovery.
  - "Contact Developer on WhatsApp" button with international formatting (`wa.me`) without exposing phone number in UI.
- **Developer Attribution & Telegram**:
  - Two dedicated contact cards: `Telegram Channel` (`https://t.me/RiFAT_TECH_BD`) and `Telegram Profile` (`https://t.me/Molla_Mohammad_Rifat_Osama`).
  - Privacy policy and Open-source Apache 2.0 license disclosures.
- **100% On-Device Privacy**:
  - Zero cloud syncing, zero remote tracking, zero telemetry.

---

## 📦 Deliverables & Artifacts

All final release files are organized in the root directory:

```
Generated Files & Output/
├── Rifat-App-Locker-v2.0.0.apk           (24.7 MB Signed Debug APK)
├── Rifat-App-Locker-v2.0.0-Source.zip    (Complete source code archive)
└── README.md                             (Release documentation)
```

---

## 🔑 Permissions & Justifications

| Permission | Purpose |
|---|---|
| `QUERY_ALL_PACKAGES` | Allows querying installed user applications so they can be listed for locking. |
| `PACKAGE_USAGE_STATS` | Used by background monitoring engine to verify foreground application states. |
| `SYSTEM_ALERT_WINDOW` | Enables drawing secure lock screen overlay over protected applications. |
| `BIND_ACCESSIBILITY_SERVICE` | Enables zero-delay real-time window transition events (`TYPE_WINDOW_STATE_CHANGED`). |
| `USE_BIOMETRIC` & `USE_FINGERPRINT` | Powers optional biometric fingerprint and face unlock via AndroidX Biometrics. |
| `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_SPECIAL_USE` | Keeps protection service persistent and prevents background termination. |
| `POST_NOTIFICATIONS` | Displays mandatory persistent service notification on Android 13+. |
| `RECEIVE_BOOT_COMPLETED` | Restores protection automatically when device powers on or restarts. |
| `VIBRATE` | Provides tactile feedback when nodes are selected in pattern grid or PIN entry. |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Whitelists app against aggressive OEM battery killers (HiOS, MIUI, etc.). |

---

## 🛠️ Build Commands

```bash
# Assemble Debug APK
gradle :app:assembleDebug

# Run Unit & Robolectric Tests
gradle :app:testDebugUnitTest
```
