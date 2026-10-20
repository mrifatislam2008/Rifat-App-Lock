package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FilterType
import com.example.data.model.LockType
import com.example.data.model.RelockPolicy
import com.example.data.model.SecurityQuestion
import com.example.data.preferences.AppPreferences
import com.example.data.security.SecurityUtils
import com.example.ui.common.AppLanguage
import com.example.ui.common.AppStrings
import com.example.ui.common.toPatternString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Rifat App Locker", appName)
    }

    @Test
    fun `verify developer name resource and string mappings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val devName = context.getString(R.string.developer_name)
        assertEquals("Molla Mohammad Rifat Osama", devName)
        assertEquals("Molla Mohammad Rifat Osama", AppStrings.get("developer_name", AppLanguage.ENGLISH))
        assertEquals("মোল্লা মোহাম্মদ রিফাত ওসামা", AppStrings.get("developer_name", AppLanguage.BENGALI))
    }

    @Test
    fun `verify version 2_0_0 string in AppStrings`() {
        val verEn = AppStrings.get("version_code", AppLanguage.ENGLISH)
        val verBn = AppStrings.get("version_code", AppLanguage.BENGALI)
        assertEquals("2.0.0", verEn)
        assertEquals("২.০.০", verBn)
    }

    @Test
    fun `verify security salted hashing and verification`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val security = SecurityUtils(context)

        val salt = security.generateSalt()
        val password = "SecretPassword123"
        val hash = security.hashCredential(password, salt)

        // Verify correct password
        assertTrue(security.verifyCredential(password, salt, hash))

        // Verify wrong password fails
        assertFalse(security.verifyCredential("WrongPassword", salt, hash))
        assertFalse(security.verifyCredential("", salt, hash))
    }

    @Test
    fun `verify pattern string hashing and verification`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val security = SecurityUtils(context)

        val salt = security.generateSalt()
        val patternList = listOf(0, 1, 2, 5, 8)
        val pattern = patternList.toPatternString()
        assertEquals("0-1-2-5-8", pattern)

        val hash = security.hashCredential(pattern, salt)

        assertTrue(security.verifyCredential("0-1-2-5-8", salt, hash))
        assertFalse(security.verifyCredential("0-1-2-5", salt, hash))
    }

    @Test
    fun `verify recovery answer hashing and case insensitivity`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val security = SecurityUtils(context)

        val salt = security.generateSalt()
        val answer = "Dhaka"
        val hash = security.hashSecurityAnswer(answer, salt)

        assertTrue(security.verifySecurityAnswer("dhaka", salt, hash))
        assertTrue(security.verifySecurityAnswer("DHAKA ", salt, hash))
        assertTrue(security.verifySecurityAnswer("  Dhaka  ", salt, hash))
        assertFalse(security.verifySecurityAnswer("Chittagong", salt, hash))
    }

    @Test
    fun `verify app preferences locking and multi app selection`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        prefs.clearAllLockedPackages()
        assertEquals(0, prefs.lockedPackages.size)

        prefs.addLockedPackage("com.whatsapp")
        prefs.addLockedPackage("com.facebook.katana")
        assertTrue(prefs.lockedPackages.contains("com.whatsapp"))
        assertTrue(prefs.lockedPackages.contains("com.facebook.katana"))
        assertEquals(2, prefs.lockedPackages.size)

        prefs.removeLockedPackage("com.whatsapp")
        assertFalse(prefs.lockedPackages.contains("com.whatsapp"))
        assertTrue(prefs.lockedPackages.contains("com.facebook.katana"))

        prefs.clearAllLockedPackages()
        assertEquals(0, prefs.lockedPackages.size)
    }

    @Test
    fun `verify biometric preferences and relock policy`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        prefs.isFingerprintEnabled = true
        assertTrue(prefs.isFingerprintEnabled)
        prefs.isFingerprintEnabled = false
        assertFalse(prefs.isFingerprintEnabled)

        prefs.isFaceUnlockEnabled = true
        assertTrue(prefs.isFaceUnlockEnabled)
        prefs.isFaceUnlockEnabled = false
        assertFalse(prefs.isFaceUnlockEnabled)

        prefs.relockPolicy = RelockPolicy.ONE_MINUTE
        assertEquals(RelockPolicy.ONE_MINUTE, prefs.relockPolicy)

        prefs.relockPolicy = RelockPolicy.IMMEDIATELY
        assertEquals(RelockPolicy.IMMEDIATELY, prefs.relockPolicy)
    }

    @Test
    fun `verify developer WhatsApp configuration preference`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        prefs.developerWhatsAppNumber = "+880123456789"
        assertEquals("+880123456789", prefs.developerWhatsAppNumber)
    }

    @Test
    fun `verify preset security questions count`() {
        val questions = SecurityQuestion.PRESET_QUESTIONS
        assertTrue(questions.size >= 5)
        assertEquals(1, questions[0].id)
    }
}
