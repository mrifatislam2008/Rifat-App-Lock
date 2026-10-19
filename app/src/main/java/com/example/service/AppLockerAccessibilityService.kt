package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.RifatAppLockerApplication
import com.example.ui.lock.LockOverlayActivity

class AppLockerAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val packageName = event.packageName?.toString() ?: return
        val app = application as? RifatAppLockerApplication ?: return

        // Ignore System UI, keyboards, and our own app unless self-lock enabled
        if (packageName == "com.android.systemui" || packageName == "android") {
            return
        }

        val repo = app.appRepository
        val prefs = app.appPreferences

        if (!prefs.isAppLockerEnabled) {
            return
        }

        if (packageName == this.packageName && !prefs.lockSelfApp) {
            return
        }

        // Notify repo of foreground change
        repo.onForegroundPackageChanged(packageName)

        // Check if package is locked
        if (repo.isAppLocked(packageName)) {
            // Check if temporarily unlocked
            if (!repo.isPackageTemporarilyUnlocked(packageName)) {
                // Intercept and launch Lock Screen
                val lockIntent = Intent(this, LockOverlayActivity::class.java).apply {
                    putExtra(LockOverlayActivity.EXTRA_PACKAGE_NAME, packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                }
                startActivity(lockIntent)
            }
        }
    }

    override fun onInterrupt() {
        // Required method by AccessibilityService
    }
}
