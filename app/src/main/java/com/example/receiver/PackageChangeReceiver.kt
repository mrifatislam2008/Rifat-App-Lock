package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.RifatAppLockerApplication

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val app = context.applicationContext as? RifatAppLockerApplication ?: return

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // If an app was uninstalled, also clean it from locked list
                if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
                    val uri = intent.data
                    val pkg = uri?.schemeSpecificPart
                    if (pkg != null) {
                        app.appPreferences.removeLockedPackage(pkg)
                    }
                }
                // Reload apps list
                app.appRepository.loadInstalledApps()
            }
        }
    }
}
