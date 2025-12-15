package com.kidplayer.app.data.local

import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for secure PIN hashing and verification
 * Uses SHA-256 hashing with salt for secure PIN storage
 */
@Singleton
class PinManager @Inject constructor() {

    /**
     * Hash a PIN with salt for secure storage
     * @param pin 4-digit PIN code
     * @return Base64-encoded string containing salt and hash (format: "salt:hash")
     */
    fun hashPin(pin: String): String {
        require(pin.length == 4 && pin.all { it.isDigit() }) {
            "PIN must be exactly 4 digits"
        }

        // Generate random salt
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        // Hash PIN with salt
        val hash = hashWithSalt(pin, salt)

        // Encode salt and hash as base64 strings separated by ":"
        val saltBase64 = salt.toBase64()
        val hashBase64 = hash.toBase64()

        return "$saltBase64:$hashBase64"
    }

    /**
     * Verify a PIN against a stored hash
     * @param pin PIN to verify
     * @param storedHash Stored hash in format "salt:hash"
     * @return true if PIN matches, false otherwise
     */
    fun verifyPin(pin: String, storedHash: String): Boolean {
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            return false
        }

        return try {
            // Split stored hash into salt and hash
            val parts = storedHash.split(":")
            if (parts.size != 2) return false

            val salt = parts[0].fromBase64()
            val expectedHash = parts[1].fromBase64()

            // Hash provided PIN with stored salt
            val actualHash = hashWithSalt(pin, salt)

            // Compare hashes using constant-time comparison to prevent timing attacks
            constantTimeEquals(expectedHash, actualHash)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Validate PIN format
     * @param pin PIN to validate
     * @return true if PIN is valid (4 digits), false otherwise
     */
    fun isValidPinFormat(pin: String): Boolean {
        return pin.length == 4 && pin.all { it.isDigit() }
    }

    /**
     * Hash PIN with salt using SHA-256
     */
    private fun hashWithSalt(pin: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(pin.toByteArray(Charsets.UTF_8))
        return digest.digest()
    }

    /**
     * Constant-time byte array comparison to prevent timing attacks
     */
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false

        var result = 0
        for (i in a.indices) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }

    /**
     * Convert byte array to Base64 string
     */
    private fun ByteArray.toBase64(): String {
        return android.util.Base64.encodeToString(this, android.util.Base64.NO_WRAP)
    }

    /**
     * Convert Base64 string to byte array
     */
    private fun String.fromBase64(): ByteArray {
        return android.util.Base64.decode(this, android.util.Base64.NO_WRAP)
    }

    companion object {
        private const val SALT_LENGTH = 16 // 16 bytes = 128 bits
    }
}
