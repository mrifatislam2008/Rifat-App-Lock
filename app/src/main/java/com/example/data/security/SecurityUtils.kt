package com.example.data.security

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

class SecurityUtils(private val context: Context) {

    private val secureRandom = SecureRandom()

    fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        secureRandom.nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    fun hashCredential(input: String, salt: String): String {
        val combined = "$salt:$input"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    fun verifyCredential(input: String, salt: String, expectedHash: String): Boolean {
        if (input.isBlank() || salt.isBlank() || expectedHash.isBlank()) {
            return false
        }
        val computedHash = hashCredential(input, salt)
        return MessageDigest.isEqual(
            computedHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }

    fun hashSecurityAnswer(answer: String, salt: String): String {
        val cleanAnswer = answer.trim().lowercase()
        return hashCredential(cleanAnswer, salt)
    }

    fun verifySecurityAnswer(inputAnswer: String, salt: String, expectedHash: String): Boolean {
        val cleanAnswer = inputAnswer.trim().lowercase()
        return verifyCredential(cleanAnswer, salt, expectedHash)
    }
}
