package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import com.example.data.model.AppInfo
import com.example.data.model.RelockPolicy
import com.example.data.preferences.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class AppRepository(
    private val context: Context,
    private val preferences: AppPreferences
) {
    private val packageManager: PackageManager = context.packageManager
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Temporary unlock session timestamps: packageName -> unlockTimestampMillis
    private val unlockedSessions = ConcurrentHashMap<String, Long>()
    @Volatile
    private var lastForegroundPackage: String? = null

    // Cache of App Icons by package name to keep Compose snappy
    private val iconCache = ConcurrentHashMap<String, Drawable>()

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        coroutineScope.launch {
            _isLoading.value = true
            try {
                val launchIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.queryIntentActivities(
                        launchIntent,
                        PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
                    )
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.queryIntentActivities(launchIntent, PackageManager.MATCH_ALL)
                }

                val lockedSet = preferences.lockedPackages
                val myPackage = context.packageName
                val allowSelfLock = preferences.lockSelfApp

                val appList = mutableListOf<AppInfo>()
                val seenPackages = HashSet<String>()

                for (info in resolveInfos) {
                    val pkg = info.activityInfo.packageName
                    if (seenPackages.contains(pkg)) continue
                    seenPackages.add(pkg)

                    // Skip self unless explicitly allowed
                    if (pkg == myPackage && !allowSelfLock) {
                        continue
                    }

                    val appName = try {
                        info.loadLabel(packageManager).toString()
                    } catch (e: Exception) {
                        pkg
                    }

                    val isSystem = try {
                        val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            packageManager.getApplicationInfo(pkg, PackageManager.ApplicationInfoFlags.of(0))
                        } else {
                            @Suppress("DEPRECATION")
                            packageManager.getApplicationInfo(pkg, 0)
                        }
                        (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    } catch (e: Exception) {
                        false
                    }

                    val versionName = try {
                        val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            packageManager.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
                        } else {
                            @Suppress("DEPRECATION")
                            packageManager.getPackageInfo(pkg, 0)
                        }
                        pInfo.versionName ?: ""
                    } catch (e: Exception) {
                        ""
                    }

                    // Preload icon into cache
                    try {
                        val icon = info.loadIcon(packageManager)
                        iconCache[pkg] = icon
                    } catch (_: Exception) {}

                    appList.add(
                        AppInfo(
                            packageName = pkg,
                            appName = appName,
                            isLocked = lockedSet.contains(pkg),
                            isSystemApp = isSystem,
                            versionName = versionName
                        )
                    )
                }

                // Sort alphabetically by app name
                appList.sortBy { it.appName.lowercase() }
                _installedApps.value = appList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAppIcon(packageName: String): Drawable? {
        iconCache[packageName]?.let { return it }
        return try {
            val icon = packageManager.getApplicationIcon(packageName)
            iconCache[packageName] = icon
            icon
        } catch (e: Exception) {
            null
        }
    }

    fun getAppName(packageName: String): String {
        return _installedApps.value.find { it.packageName == packageName }?.appName ?: try {
            val appInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getApplicationInfo(packageName, 0)
            }
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    fun toggleAppLock(packageName: String): Boolean {
        val currentlyLocked = preferences.lockedPackages.contains(packageName)
        val newLocked = !currentlyLocked
        if (newLocked) {
            preferences.addLockedPackage(packageName)
        } else {
            preferences.removeLockedPackage(packageName)
            unlockedSessions.remove(packageName)
        }
        updateAppInList(packageName, newLocked)
        return newLocked
    }

    fun lockAllApps() {
        val allPkgs = _installedApps.value.map { it.packageName }
        preferences.addAllLockedPackages(allPkgs)
        _installedApps.value = _installedApps.value.map { it.copy(isLocked = true) }
    }

    fun unlockAllApps() {
        preferences.clearAllLockedPackages()
        unlockedSessions.clear()
        _installedApps.value = _installedApps.value.map { it.copy(isLocked = false) }
    }

    private fun updateAppInList(packageName: String, isLocked: Boolean) {
        val current = _installedApps.value
        _installedApps.value = current.map { app ->
            if (app.packageName == packageName) app.copy(isLocked = isLocked) else app
        }
    }

    fun isAppLocked(packageName: String): Boolean {
        if (!preferences.isAppLockerEnabled) return false
        if (packageName == context.packageName && !preferences.lockSelfApp) return false
        return preferences.lockedPackages.contains(packageName)
    }

    fun markPackageTemporarilyUnlocked(packageName: String) {
        unlockedSessions[packageName] = System.currentTimeMillis()
        lastForegroundPackage = packageName
    }

    fun isPackageTemporarilyUnlocked(packageName: String): Boolean {
        val unlockTime = unlockedSessions[packageName] ?: return false
        val now = System.currentTimeMillis()
        return when (preferences.relockPolicy) {
            RelockPolicy.IMMEDIATELY -> {
                // Stays unlocked as long as user is currently using it
                lastForegroundPackage == packageName
            }
            RelockPolicy.ONE_MINUTE -> {
                now - unlockTime < 60_000L
            }
            RelockPolicy.SCREEN_OFF -> {
                true // Stays unlocked until screen off triggers clear
            }
        }
    }

    fun onForegroundPackageChanged(newPackage: String) {
        if (lastForegroundPackage != null && lastForegroundPackage != newPackage) {
            if (preferences.relockPolicy == RelockPolicy.IMMEDIATELY) {
                // Re-lock previous app
                unlockedSessions.remove(lastForegroundPackage)
            }
        }
        lastForegroundPackage = newPackage
    }

    fun onScreenOff() {
        if (preferences.relockPolicy != RelockPolicy.ONE_MINUTE) {
            unlockedSessions.clear()
        }
    }

    fun clearAllUnlockSessions() {
        unlockedSessions.clear()
        lastForegroundPackage = null
    }
}
