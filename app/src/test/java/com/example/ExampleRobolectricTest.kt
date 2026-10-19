package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.preferences.AppPreferences
import com.example.data.security.SecurityUtils
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
        val pattern = "0-1-2-5-8"
        val hash = security.hashCredential(pattern, salt)

        assertTrue(security.verifyCredential("0-1-2-5-8", salt, hash))
        assertFalse(security.verifyCredential("0-1-2-5", salt, hash))
    }

    @Test
    fun `verify app preferences locking toggle`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = AppPreferences(context)

        prefs.clearAllLockedPackages()
        assertEquals(0, prefs.lockedPackages.size)

        prefs.addLockedPackage("com.whatsapp")
        assertTrue(prefs.lockedPackages.contains("com.whatsapp"))

        prefs.removeLockedPackage("com.whatsapp")
        assertFalse(prefs.lockedPackages.contains("com.whatsapp"))
    }
}
