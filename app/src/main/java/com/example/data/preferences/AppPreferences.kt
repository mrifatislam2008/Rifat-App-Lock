package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.LockType
import com.example.data.model.RelockPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("rifat_app_locker_secure_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_APP_LOCKER_ENABLED = "key_app_locker_enabled"
        private const val KEY_SETUP_COMPLETED = "key_setup_completed"
        private const val KEY_LOCK_TYPE = "key_lock_type"
        private const val KEY_CREDENTIAL_HASH = "key_credential_hash"
        private const val KEY_CREDENTIAL_SALT = "key_credential_salt"
        private const val KEY_SECURITY_Q_ID = "key_security_q_id"
        private const val KEY_SECURITY_A_HASH = "key_security_a_hash"
        private const val KEY_SECURITY_A_SALT = "key_security_a_salt"
        private const val KEY_LOCKED_PACKAGES = "key_locked_packages"
        private const val KEY_RELOCK_POLICY = "key_relock_policy"
        private const val KEY_LOCK_SELF_APP = "key_lock_self_app"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_FAILED_ATTEMPTS = "key_failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "key_lockout_until"
        private const val KEY_FINGERPRINT_ENABLED = "key_fingerprint_enabled"
        private const val KEY_FACE_UNLOCK_ENABLED = "key_face_unlock_enabled"
        private const val KEY_DEVELOPER_WHATSAPP_NUMBER = "key_developer_whatsapp_number"
    }

    private val _isAppLockerEnabledFlow = MutableStateFlow(isAppLockerEnabled)
    val isAppLockerEnabledFlow: StateFlow<Boolean> = _isAppLockerEnabledFlow.asStateFlow()

    private val _lockedPackagesFlow = MutableStateFlow(lockedPackages)
    val lockedPackagesFlow: StateFlow<Set<String>> = _lockedPackagesFlow.asStateFlow()

    private val _languageFlow = MutableStateFlow(language)
    val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    var isAppLockerEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCKER_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_APP_LOCKER_ENABLED, value).apply()
            _isAppLockerEnabledFlow.value = value
        }

    var isSetupCompleted: Boolean
        get() = prefs.getBoolean(KEY_SETUP_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_SETUP_COMPLETED, value).apply()

    var lockType: LockType
        get() {
            val raw = prefs.getString(KEY_LOCK_TYPE, LockType.PASSWORD.name) ?: LockType.PASSWORD.name
            return try {
                LockType.valueOf(raw)
            } catch (e: Exception) {
                LockType.PASSWORD
            }
        }
        set(value) = prefs.edit().putString(KEY_LOCK_TYPE, value.name).apply()

    var credentialHash: String
        get() = prefs.getString(KEY_CREDENTIAL_HASH, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CREDENTIAL_HASH, value).apply()

    var credentialSalt: String
        get() = prefs.getString(KEY_CREDENTIAL_SALT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CREDENTIAL_SALT, value).apply()

    var securityQuestionId: Int
        get() = prefs.getInt(KEY_SECURITY_Q_ID, 1)
        set(value) = prefs.edit().putInt(KEY_SECURITY_Q_ID, value).apply()

    var securityAnswerHash: String
        get() = prefs.getString(KEY_SECURITY_A_HASH, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SECURITY_A_HASH, value).apply()

    var securityAnswerSalt: String
        get() = prefs.getString(KEY_SECURITY_A_SALT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SECURITY_A_SALT, value).apply()

    var lockedPackages: Set<String>
        get() = prefs.getStringSet(KEY_LOCKED_PACKAGES, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_LOCKED_PACKAGES, HashSet(value)).apply()
            _lockedPackagesFlow.value = value
        }

    var relockPolicy: RelockPolicy
        get() {
            val raw = prefs.getString(KEY_RELOCK_POLICY, RelockPolicy.IMMEDIATELY.name)
            return try {
                RelockPolicy.valueOf(raw ?: RelockPolicy.IMMEDIATELY.name)
            } catch (e: Exception) {
                RelockPolicy.IMMEDIATELY
            }
        }
        set(value) = prefs.edit().putString(KEY_RELOCK_POLICY, value.name).apply()

    var lockSelfApp: Boolean
        get() = prefs.getBoolean(KEY_LOCK_SELF_APP, false)
        set(value) = prefs.edit().putBoolean(KEY_LOCK_SELF_APP, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE, value).apply()
            _languageFlow.value = value
        }

    var failedAttempts: Int
        get() = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        set(value) = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, value).apply()

    var lockoutUntilTimestamp: Long
        get() = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        set(value) = prefs.edit().putLong(KEY_LOCKOUT_UNTIL, value).apply()

    var isFingerprintEnabled: Boolean
        get() = prefs.getBoolean(KEY_FINGERPRINT_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_FINGERPRINT_ENABLED, value).apply()

    var isFaceUnlockEnabled: Boolean
        get() = prefs.getBoolean(KEY_FACE_UNLOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_FACE_UNLOCK_ENABLED, value).apply()

    var developerWhatsAppNumber: String
        get() = prefs.getString(KEY_DEVELOPER_WHATSAPP_NUMBER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DEVELOPER_WHATSAPP_NUMBER, value.trim()).apply()

    fun addLockedPackage(pkg: String) {
        val current = HashSet(lockedPackages)
        current.add(pkg)
        lockedPackages = current
    }

    fun removeLockedPackage(pkg: String) {
        val current = HashSet(lockedPackages)
        current.remove(pkg)
        lockedPackages = current
    }

    fun addAllLockedPackages(pkgs: Collection<String>) {
        val current = HashSet(lockedPackages)
        current.addAll(pkgs)
        lockedPackages = current
    }

    fun clearAllLockedPackages() {
        lockedPackages = emptySet()
    }
}
