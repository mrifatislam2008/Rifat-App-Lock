package com.example.data.model

import android.graphics.drawable.Drawable

enum class LockType {
    PASSWORD,
    PATTERN
}

enum class RelockPolicy(val titleEn: String, val titleBn: String) {
    IMMEDIATELY("Immediately on Exit", "অ্যাপ বন্ধের সাথে সাথে"),
    SCREEN_OFF("When Screen is Locked", "স্ক্রিন লক হলে"),
    ONE_MINUTE("After 1 Minute", "১ মিনিট পর")
}

enum class FilterType {
    ALL,
    LOCKED,
    UNLOCKED
}

data class AppInfo(
    val packageName: String,
    val appName: String,
    val isLocked: Boolean = false,
    val isSystemApp: Boolean = false,
    val versionName: String = ""
)

data class DashboardStats(
    val totalApps: Int = 0,
    val lockedApps: Int = 0,
    val unlockedApps: Int = 0,
    val isProtectionEnabled: Boolean = true,
    val isAccessibilityGranted: Boolean = false,
    val isUsageStatsGranted: Boolean = false,
    val isOverlayGranted: Boolean = false,
    val isBatteryOptimizationIgnored: Boolean = false
)

data class SecurityQuestion(
    val id: Int,
    val questionEn: String,
    val questionBn: String
) {
    companion object {
        val PRESET_QUESTIONS = listOf(
            SecurityQuestion(1, "What is your childhood nickname?", "আপনার শৈশবের ডাকনাম কী?"),
            SecurityQuestion(2, "What was the name of your first school?", "আপনার প্রথম স্কুলের নাম কী ছিল?"),
            SecurityQuestion(3, "What city were you born in?", "আপনি কোন শহরে জন্মগ্রহণ করেছেন?"),
            SecurityQuestion(4, "What is your favorite sport or hobby?", "আপনার প্রিয় খেলা বা শখ কী?"),
            SecurityQuestion(5, "What is your mother's maiden name?", "আপনার মায়ের পারিবারিক নাম কী?")
        )
    }
}
