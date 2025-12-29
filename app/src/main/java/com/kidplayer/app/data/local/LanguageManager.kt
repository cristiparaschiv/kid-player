package com.kidplayer.app.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supported languages in the app
 */
enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    ROMANIAN("ro", "Romanian", "Română");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}

/**
 * Manages app language settings with reactive state
 * Similar to ScreenTimeManager pattern
 */
@Singleton
class LanguageManager @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        // Load initial language setting
        scope.launch {
            loadLanguage()
        }
    }

    /**
     * Load language from preferences
     */
    private suspend fun loadLanguage() {
        try {
            val languageCode = securePreferences.getAppLanguage()
            _currentLanguage.value = AppLanguage.fromCode(languageCode)
            Timber.d("Language loaded: ${_currentLanguage.value}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load language, using default")
            _currentLanguage.value = AppLanguage.ENGLISH
        }
    }

    /**
     * Get current language synchronously (for initial app setup)
     */
    fun getCurrentLanguageSync(): AppLanguage {
        return try {
            AppLanguage.fromCode(securePreferences.getAppLanguageSync())
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    }

    /**
     * Set app language
     */
    suspend fun setLanguage(language: AppLanguage) {
        try {
            securePreferences.saveAppLanguage(language.code)
            _currentLanguage.value = language
            Timber.d("Language set to: $language")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save language")
        }
    }

    /**
     * Get current language code
     */
    fun getLanguageCode(): String = _currentLanguage.value.code

    /**
     * Check if current language is Romanian
     */
    fun isRomanian(): Boolean = _currentLanguage.value == AppLanguage.ROMANIAN
}
