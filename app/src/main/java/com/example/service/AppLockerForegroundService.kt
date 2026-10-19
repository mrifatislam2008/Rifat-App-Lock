package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.RifatAppLockerApplication
import com.example.ui.lock.LockOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppLockerForegroundService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private var screenReceiver: BroadcastReceiver? = null
    private var isScreenOn = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        registerScreenStateReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        startUsageStatsWatcher()
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, RifatAppLockerApplication.CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun registerScreenStateReceiver() {
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        isScreenOn = false
                        val app = application as? RifatAppLockerApplication
                        app?.appRepository?.onScreenOff()
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        isScreenOn = true
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)
    }

    private fun startUsageStatsWatcher() {
        serviceScope.launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            val app = application as? RifatAppLockerApplication ?: return@launch

            while (isActive) {
                if (isScreenOn && app.appPreferences.isAppLockerEnabled) {
                    // If accessibility service is enabled, let it handle window events with zero delay.
                    // Otherwise, usage stats polling handles it.
                    if (!ServiceHelper.isAccessibilityServiceEnabled(this@AppLockerForegroundService) &&
                        usageStatsManager != null &&
                        ServiceHelper.hasUsageStatsPermission(this@AppLockerForegroundService)
                    ) {
                        checkTopAppUsageStats(usageStatsManager, app)
                    }
                }
                delay(600)
            }
        }
    }

    private fun checkTopAppUsageStats(usageStatsManager: UsageStatsManager, app: RifatAppLockerApplication) {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 2500

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()
        var topPackage: String? = null

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                topPackage = event.packageName
            }
        }

        if (topPackage != null && topPackage != packageName && topPackage != "com.android.systemui") {
            app.appRepository.onForegroundPackageChanged(topPackage)

            if (app.appRepository.isAppLocked(topPackage) &&
                !app.appRepository.isPackageTemporarilyUnlocked(topPackage)
            ) {
                val lockIntent = Intent(this, LockOverlayActivity::class.java).apply {
                    putExtra(LockOverlayActivity.EXTRA_PACKAGE_NAME, topPackage)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                }
                startActivity(lockIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        try {
            screenReceiver?.let { unregisterReceiver(it) }
        } catch (_: Exception) {}
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
