package com.example.ui.common

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    BENGALI("bn", "বাংলা")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

object AppStrings {
    fun get(key: String, lang: AppLanguage): String {
        val entry = stringMap[key] ?: return key
        return if (lang == AppLanguage.BENGALI) entry.second else entry.first
    }

    private val stringMap = mapOf(
        "app_name" to Pair("Rifat App Locker", "রিফাত অ্যাপ লকার"),
        "dashboard" to Pair("Dashboard", "ড্যাশবোর্ড"),
        "manage_apps" to Pair("Manage Apps", "অ্যাপ ব্যবস্থাপনা"),
        "security_settings" to Pair("Security Settings", "নিরাপত্তা সেটিংস"),
        "developer_info" to Pair("Developer Info", "ডেভেলপার তথ্য"),
        "troubleshooting" to Pair("Troubleshooting & Privacy", "সমস্যা সমাধান ও প্রাইভেসী"),
        "protection_active" to Pair("Protection Active", "সুরক্ষা সক্রিয় রয়েছে"),
        "protection_paused" to Pair("Protection Paused", "সুরক্ষা সাময়িকভাবে বন্ধ"),
        "enable_protection" to Pair("Enable App Locker", "অ্যাপ লকার সক্রিয় করুন"),
        "disable_protection" to Pair("Disable App Locker", "অ্যাপ লকার বন্ধ করুন"),
        "total_apps" to Pair("Total Apps", "সর্বমোট অ্যাপ"),
        "locked_apps" to Pair("Locked Apps", "সুরক্ষিত অ্যাপ"),
        "unlocked_apps" to Pair("Unlocked Apps", "আনলকড অ্যাপ"),
        "security_health" to Pair("Security Health", "নিরাপত্তা অবস্থা"),
        "permissions_needed" to Pair("Permissions Needed", "অনুমতি প্রয়োজন"),
        "all_systems_go" to Pair("All Systems Operational", "সব সিস্টেম প্রস্তুত"),
        "grant_permissions" to Pair("Set Up Permissions", "অনুমতি প্রদান করুন"),
        "quick_lock_all" to Pair("Lock All Apps", "সকল অ্যাপ লক করুন"),
        "unlock_all" to Pair("Unlock All Apps", "সকল অ্যাপ আনলক করুন"),
        "search_hint" to Pair("Search installed apps...", "ইনস্টল করা অ্যাপ খুঁজুন..."),
        "filter_all" to Pair("All", "সব"),
        "filter_locked" to Pair("Locked", "লক করা"),
        "filter_unlocked" to Pair("Unlocked", "আনলকড"),
        "password" to Pair("Password / PIN", "পাসওয়ার্ড / পিন"),
        "pattern" to Pair("Pattern", "প্যাটার্ন"),
        "enter_password" to Pair("Enter Password", "পাসওয়ার্ড দিন"),
        "draw_pattern" to Pair("Draw Pattern", "প্যাটার্ন আঁকুন"),
        "confirm_password" to Pair("Confirm Password", "পাসওয়ার্ড নিশ্চিত করুন"),
        "confirm_pattern" to Pair("Confirm Pattern", "প্যাটার্ন নিশ্চিত করুন"),
        "change_password" to Pair("Change Password", "পাসওয়ার্ড পরিবর্তন করুন"),
        "change_pattern" to Pair("Change Pattern", "প্যাটার্ন পরিবর্তন করুন"),
        "lock_type" to Pair("Lock Method", "লক পদ্ধতি"),
        "relock_policy" to Pair("Relock Timeout", "রি-লক করার সময়"),
        "lock_self_app" to Pair("Lock Rifat App Locker", "রিফাত অ্যাপ লকার নিজেই লক করুন"),
        "security_question" to Pair("Security Recovery Question", "রিকভারি সিকিউরিটি প্রশ্ন"),
        "security_answer" to Pair("Recovery Answer", "রিকভারি উত্তর"),
        "forgot_credential" to Pair("Forgot Password / Pattern?", "পাসওয়ার্ড বা প্যাটার্ন ভুলে গেছেন?"),
        "reset_credential" to Pair("Reset Security Credential", "নিরাপত্তা কোড রিসেট করুন"),
        "wrong_credential" to Pair("Incorrect credential. Please try again.", "ভুল তথ্য। আবার চেষ্টা করুন।"),
        "credential_mismatch" to Pair("Credentials do not match. Try again.", "পাসওয়ার্ড মেলেনি। আবার চেষ্টা করুন।"),
        "pattern_min_dots" to Pair("Connect at least 4 dots", "কমপক্ষে ৪টি বিন্দু সংযোগ করুন"),
        "save_success" to Pair("Settings updated successfully", "সেটিংস সফলভাবে সংরক্ষিত হয়েছে"),
        "app_locked_msg" to Pair("is now locked", "সুরক্ষিত করা হয়েছে"),
        "app_unlocked_msg" to Pair("is now unlocked", "আনলক করা হয়েছে"),
        "developer_name_title" to Pair("Lead Developer & Architect", "প্রধান ডেভেলপার ও স্থপতি"),
        "developer_name" to Pair("Molla Mohammad Rifat Osama", "মোল্লা মোহাম্মদ রিফাত ওসামা"),
        "telegram_channel" to Pair("Telegram Channel", "টেলিগ্রাম চ্যানেল"),
        "telegram_profile" to Pair("Telegram Profile", "টেলিগ্রাম প্রোফাইল"),
        "telegram_channel_sub" to Pair("Join for security updates & releases", "নিরাপত্তা আপডেট ও খবরের জন্য যুক্ত হন"),
        "telegram_profile_sub" to Pair("Direct contact with developer", "ডেভেলপারের সাথে সরাসরি যোগাযোগ করুন"),
        "version_title" to Pair("App Version", "অ্যাপ সংস্করণ"),
        "version_code" to Pair("1.0.0 (Production Build)", "১.০.০ (প্রোডাকশন সংস্করণ)"),
        "tecno_hios_note" to Pair("TECNO / HiOS / MIUI Optimization", "TECNO / HiOS / MIUI এর জন্য পরামর্শ"),
        "tecno_hios_desc" to Pair(
            "On devices like TECNO Spark Go 2024 (HiOS) or Xiaomi (MIUI), the system kills background services aggressively. Enable 'Auto-start' and lock Rifat App Locker in the recent apps tray for 100% uninterrupted protection.",
            "TECNO Spark Go 2024 (HiOS) বা শাওমির মতো ডিভাইসে ব্যাকগ্রাউন্ড সার্ভিস স্বয়ংক্রিয়ভাবে বন্ধ হয়ে যেতে পারে। নিরবচ্ছিন্ন সুরক্ষার জন্য 'Auto-start' চালু করুন এবং রিসেন্ট অ্যাপস থেকে অ্যাপটি লক করে রাখুন।"
        ),
        "zero_cloud_title" to Pair("100% Offline & Private", "সম্পূর্ণ অফলাইন এবং সুরক্ষিত"),
        "zero_cloud_desc" to Pair(
            "Your passwords, patterns, answers, and installed apps never leave your device. No analytics, no cloud telemetry, and zero tracking.",
            "আপনার পাসওয়ার্ড, প্যাটার্ন বা ব্যক্তিগত তথ্য কখনো ফোনের বাইরে যায় না। কোনো প্রকার ক্লাউড ট্র্যাকিং বা তথ্য সংগ্রহ করা হয় না।"
        ),
        "open_autostart" to Pair("Open Auto-Start Manager", "অটো-স্টার্ট ম্যানেজার খুলুন"),
        "ignore_battery" to Pair("Disable Battery Optimization", "ব্যাটারি অপ্টিমাইজেশন বন্ধ করুন"),
        "accessibility_perm" to Pair("Accessibility Service", "এক্সেসিবিলিটি সার্ভিস"),
        "accessibility_perm_desc" to Pair("Instantly detects when a protected app is brought to foreground.", "সুরক্ষিত অ্যাপ খুললেই সাথে সাথে লক স্ক্রিন প্রদর্শন করে।"),
        "usage_stats_perm" to Pair("Usage Access Permission", "ইউসেজ এক্সেস অনুমতি"),
        "usage_stats_perm_desc" to Pair("Allows Android to monitor foreground application states.", "অ্যান্ড্রয়েড সিস্টেমে চলমান অ্যাপ শনাক্ত করতে সহায়তা করে।"),
        "overlay_perm" to Pair("Display Over Other Apps", "অন্যান্য অ্যাপের উপর প্রদর্শনের অনুমতি"),
        "overlay_perm_desc" to Pair("Required to display the security lock screen over protected apps.", "সুরক্ষিত অ্যাপ খোলার সময় উপরে সিকিউরিটি পর্দা আঁকতে প্রয়োজন।"),
        "granted" to Pair("Granted", "অনুমোদিত"),
        "not_granted" to Pair("Not Granted", "প্রয়োজন"),
        "welcome_title" to Pair("Shield Your Privacy", "আপনার গোপনীয়তা রক্ষা করুন"),
        "welcome_subtitle" to Pair("Enterprise-grade on-device security by Molla Mohammad Rifat Osama", "মোল্লা মোহাম্মদ রিফাত ওসামার তৈরি শক্তিশালী নিরাপত্তা অ্যাপ্লিকেশন"),
        "get_started" to Pair("Get Started", "শুরু করুন"),
        "setup_lock" to Pair("Setup Protection", "লক সেটআপ করুন"),
        "unlock" to Pair("Unlock", "আনলক করুন")
    )
}
