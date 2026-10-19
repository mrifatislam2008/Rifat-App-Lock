package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.RifatAppLockerApplication
import com.example.service.ServiceHelper

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val app = context.applicationContext as? RifatAppLockerApplication ?: return
            if (app.appPreferences.isAppLockerEnabled) {
                ServiceHelper.startMonitoringService(context)
            }
        }
    }
}
