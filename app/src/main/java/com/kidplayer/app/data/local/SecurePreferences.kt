package com.kidplayer.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage for sensitive data using Android's EncryptedSharedPreferences
 * All data is encrypted using AES256-GCM encryption backed by Android Keystore
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save server URL
     * Uses commit() for synchronous write to ensure data is persisted
     */
    suspend fun saveServerUrl(url: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_SERVER_URL, url).commit()
    }

    /**
     * Get server URL
     */
    suspend fun getServerUrl(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_SERVER_URL, null)
    }

    /**
     * Save authentication token
     * Uses commit() for synchronous write to ensure data is persisted
     */
    suspend fun saveAuthToken(token: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_AUTH_TOKEN, token).commit()
    }

    /**
     * Get authentication token
     */
    suspend fun getAuthToken(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Save user ID
     * Uses commit() for synchronous write to ensure data is persisted
     */
    suspend fun saveUserId(userId: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_USER_ID, userId).commit()
    }

    /**
     * Get user ID
     */
    suspend fun getUserId(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_USER_ID, null)
    }

    /**
     * Save username
     * Uses commit() for synchronous write to ensure data is persisted
     */
    suspend fun saveUsername(username: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_USERNAME, username).commit()
    }

    /**
     * Get username
     */
    suspend fun getUsername(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_USERNAME, null)
    }

    /**
     * Save parent PIN (hashed)
     */
    suspend fun saveParentPin(hashedPin: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_PARENT_PIN, hashedPin).apply()
    }

    /**
     * Get parent PIN (hashed)
     */
    suspend fun getParentPin(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_PARENT_PIN, null)
    }

    /**
     * Check if server is configured
     */
    suspend fun isServerConfigured(): Boolean = withContext(Dispatchers.IO) {
        !getServerUrl().isNullOrBlank() &&
                !getAuthToken().isNullOrBlank() &&
                !getUserId().isNullOrBlank()
    }

    /**
     * Clear all stored credentials
     * Used when logging out or switching servers
     * Uses commit() to ensure credentials are cleared immediately
     */
    suspend fun clearCredentials() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit()
            .remove(KEY_SERVER_URL)
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .commit()
    }

    /**
     * Clear all data including parent PIN
     * Uses commit() to ensure all data is cleared immediately
     */
    suspend fun clearAll() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().clear().commit()
    }

    // ===== Playback Settings =====

    /**
     * Save autoplay enabled status
     * Default: true (autoplay enabled by default)
     */
    suspend fun saveAutoplayEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putBoolean(KEY_AUTOPLAY_ENABLED, enabled).apply()
    }

    /**
     * Get autoplay enabled status
     * Returns true by default if not set
     */
    suspend fun getAutoplayEnabled(): Boolean = withContext(Dispatchers.IO) {
        encryptedPrefs.getBoolean(KEY_AUTOPLAY_ENABLED, true) // Default: enabled
    }

    // ===== Parental Controls - Screen Time =====

    /**
     * Save screen time limit enabled status
     */
    suspend fun saveScreenTimeLimitEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putBoolean(KEY_SCREEN_TIME_ENABLED, enabled).apply()
    }

    /**
     * Get screen time limit enabled status
     */
    suspend fun getScreenTimeLimitEnabled(): Boolean? = withContext(Dispatchers.IO) {
        if (!encryptedPrefs.contains(KEY_SCREEN_TIME_ENABLED)) null
        else encryptedPrefs.getBoolean(KEY_SCREEN_TIME_ENABLED, false)
    }

    /**
     * Save daily screen time limit in minutes
     */
    suspend fun saveScreenTimeDailyLimit(minutes: Int) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putInt(KEY_SCREEN_TIME_DAILY_LIMIT, minutes).apply()
    }

    /**
     * Get daily screen time limit in minutes
     */
    suspend fun getScreenTimeDailyLimit(): Int? = withContext(Dispatchers.IO) {
        if (!encryptedPrefs.contains(KEY_SCREEN_TIME_DAILY_LIMIT)) null
        else encryptedPrefs.getInt(KEY_SCREEN_TIME_DAILY_LIMIT, 60)
    }

    /**
     * Save screen time used today in minutes
     */
    suspend fun saveScreenTimeUsedToday(minutes: Int) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putInt(KEY_SCREEN_TIME_USED_TODAY, minutes).apply()
    }

    /**
     * Get screen time used today in minutes
     */
    suspend fun getScreenTimeUsedToday(): Int? = withContext(Dispatchers.IO) {
        if (!encryptedPrefs.contains(KEY_SCREEN_TIME_USED_TODAY)) null
        else encryptedPrefs.getInt(KEY_SCREEN_TIME_USED_TODAY, 0)
    }

    /**
     * Save last reset date for screen time counter
     */
    suspend fun saveScreenTimeLastReset(date: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_SCREEN_TIME_LAST_RESET, date).apply()
    }

    /**
     * Get last reset date for screen time counter
     */
    suspend fun getScreenTimeLastReset(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_SCREEN_TIME_LAST_RESET, null)
    }

    // ===== Parental Controls - Access Schedule =====

    /**
     * Save access schedule enabled status
     */
    suspend fun saveAccessScheduleEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putBoolean(KEY_ACCESS_SCHEDULE_ENABLED, enabled).apply()
    }

    /**
     * Get access schedule enabled status
     */
    suspend fun getAccessScheduleEnabled(): Boolean? = withContext(Dispatchers.IO) {
        if (!encryptedPrefs.contains(KEY_ACCESS_SCHEDULE_ENABLED)) null
        else encryptedPrefs.getBoolean(KEY_ACCESS_SCHEDULE_ENABLED, false)
    }

    /**
     * Save access schedule start time (format: "HH:mm")
     */
    suspend fun saveAccessScheduleStartTime(time: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_ACCESS_SCHEDULE_START, time).apply()
    }

    /**
     * Get access schedule start time
     */
    suspend fun getAccessScheduleStartTime(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_ACCESS_SCHEDULE_START, null)
    }

    /**
     * Save access schedule end time (format: "HH:mm")
     */
    suspend fun saveAccessScheduleEndTime(time: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_ACCESS_SCHEDULE_END, time).apply()
    }

    /**
     * Get access schedule end time
     */
    suspend fun getAccessScheduleEndTime(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_ACCESS_SCHEDULE_END, null)
    }

    companion object {
        private const val PREFS_NAME = "kidplayer_secure_prefs"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_PARENT_PIN = "parent_pin"

        // Playback settings keys
        private const val KEY_AUTOPLAY_ENABLED = "autoplay_enabled"

        // Screen time keys
        private const val KEY_SCREEN_TIME_ENABLED = "screen_time_enabled"
        private const val KEY_SCREEN_TIME_DAILY_LIMIT = "screen_time_daily_limit"
        private const val KEY_SCREEN_TIME_USED_TODAY = "screen_time_used_today"
        private const val KEY_SCREEN_TIME_LAST_RESET = "screen_time_last_reset"

        // Access schedule keys
        private const val KEY_ACCESS_SCHEDULE_ENABLED = "access_schedule_enabled"
        private const val KEY_ACCESS_SCHEDULE_START = "access_schedule_start"
        private const val KEY_ACCESS_SCHEDULE_END = "access_schedule_end"
    }
}
