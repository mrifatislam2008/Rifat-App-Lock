package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.preferences.AppPreferences
import com.example.data.repository.AppRepository
import com.example.data.security.SecurityUtils
import com.example.service.ServiceHelper

class RifatAppLockerApplication : Application() {

    companion object {
        const val CHANNEL_ID = "rifat_app_locker_channel"
        lateinit var instance: RifatAppLockerApplication
            private set
    }

    lateinit var appPreferences: AppPreferences
        private set
    lateinit var appRepository: AppRepository
        private set
    lateinit var securityUtils: SecurityUtils
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        securityUtils = SecurityUtils(this)
        appPreferences = AppPreferences(this)
        appRepository = AppRepository(this, appPreferences)

        createNotificationChannel()

        // Check if service should be started on app init
        if (appPreferences.isAppLockerEnabled && ServiceHelper.hasUsageStatsPermission(this)) {
            ServiceHelper.startMonitoringService(this)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
