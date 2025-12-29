package com.kidplayer.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.local.AppLanguage
import com.kidplayer.app.data.local.LanguageManager
import com.kidplayer.app.domain.model.ScreenTimeConfig
import com.kidplayer.app.domain.repository.JellyfinRepository
import com.kidplayer.app.domain.usecase.GetAutoplaySettingUseCase
import com.kidplayer.app.domain.usecase.GetParentalControlsUseCase
import com.kidplayer.app.domain.usecase.LogoutUseCase
import com.kidplayer.app.domain.usecase.UpdateAutoplaySettingUseCase
import com.kidplayer.app.domain.usecase.UpdateScreenTimeConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Settings screen
 * Manages app settings including autoplay, screen time, and other preferences
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAutoplaySettingUseCase: GetAutoplaySettingUseCase,
    private val updateAutoplaySettingUseCase: UpdateAutoplaySettingUseCase,
    private val getParentalControlsUseCase: GetParentalControlsUseCase,
    private val updateScreenTimeConfigUseCase: UpdateScreenTimeConfigUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val jellyfinRepository: JellyfinRepository,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // Store current screen time config for updates
    private var currentScreenTimeConfig: ScreenTimeConfig? = null

    init {
        loadSettings()
        observeLanguage()
    }

    /**
     * Observe language changes
     */
    private fun observeLanguage() {
        viewModelScope.launch {
            languageManager.currentLanguage.collect { language ->
                _uiState.update { it.copy(currentLanguage = language) }
            }
        }
    }

    /**
     * Change app language
     */
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            try {
                Timber.d("Changing language to: $language")
                languageManager.setLanguage(language)
            } catch (e: Exception) {
                Timber.e(e, "Error changing language")
                _uiState.update {
                    it.copy(error = "Failed to change language: ${e.message}")
                }
            }
        }
    }

    /**
     * Load all settings from preferences
     */
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Load autoplay setting
                val autoplayEnabled = getAutoplaySettingUseCase()
                Timber.d("Loaded autoplay setting: $autoplayEnabled")

                // Load parental controls (collect first emission from Flow)
                val parentalControls = getParentalControlsUseCase().first()
                currentScreenTimeConfig = parentalControls.screenTimeConfig

                // Load server info
                val serverConfig = jellyfinRepository.getServerConfig()
                val serverUrl = serverConfig?.url ?: "Not configured"
                val username = serverConfig?.username ?: "Not configured"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        autoplayEnabled = autoplayEnabled,
                        screenTimeEnabled = parentalControls.screenTimeConfig.isEnabled,
                        screenTimeDailyLimit = parentalControls.screenTimeConfig.dailyLimitMinutes,
                        serverUrl = serverUrl,
                        username = username
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading settings")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load settings: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Toggle autoplay setting
     */
    fun toggleAutoplay(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Toggling autoplay: $enabled")
                updateAutoplaySettingUseCase(enabled)
                _uiState.update { it.copy(autoplayEnabled = enabled) }
            } catch (e: Exception) {
                Timber.e(e, "Error updating autoplay setting")
                _uiState.update {
                    it.copy(error = "Failed to update autoplay: ${e.message}")
                }
            }
        }
    }

    /**
     * Toggle screen time limit
     */
    fun toggleScreenTime(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Toggling screen time: $enabled")
                val config = currentScreenTimeConfig?.copy(isEnabled = enabled)
                    ?: ScreenTimeConfig(
                        isEnabled = enabled,
                        dailyLimitMinutes = _uiState.value.screenTimeDailyLimit,
                        usedTodayMinutes = 0,
                        lastResetDate = LocalDate.now().toString()
                    )
                updateScreenTimeConfigUseCase(config)
                currentScreenTimeConfig = config
                _uiState.update { it.copy(screenTimeEnabled = enabled) }
            } catch (e: Exception) {
                Timber.e(e, "Error updating screen time setting")
                _uiState.update {
                    it.copy(error = "Failed to update screen time: ${e.message}")
                }
            }
        }
    }

    /**
     * Update screen time daily limit
     */
    fun updateScreenTimeLimit(minutes: Int) {
        viewModelScope.launch {
            try {
                Timber.d("Updating screen time limit: $minutes minutes")
                val config = currentScreenTimeConfig?.copy(dailyLimitMinutes = minutes)
                    ?: ScreenTimeConfig(
                        isEnabled = _uiState.value.screenTimeEnabled,
                        dailyLimitMinutes = minutes,
                        usedTodayMinutes = 0,
                        lastResetDate = LocalDate.now().toString()
                    )
                updateScreenTimeConfigUseCase(config)
                currentScreenTimeConfig = config
                _uiState.update { it.copy(screenTimeDailyLimit = minutes) }
            } catch (e: Exception) {
                Timber.e(e, "Error updating screen time limit")
                _uiState.update {
                    it.copy(error = "Failed to update screen time limit: ${e.message}")
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Logout user and clear all credentials
     * This will trigger navigation back to Setup screen
     */
    fun logout() {
        viewModelScope.launch {
            try {
                Timber.d("Initiating logout")
                _uiState.update { it.copy(isLoading = true) }

                logoutUseCase()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        shouldNavigateToSetup = true
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error during logout")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Logout failed: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Reset navigation flag after navigating to Setup
     */
    fun resetNavigationFlag() {
        _uiState.update { it.copy(shouldNavigateToSetup = false) }
    }
}

/**
 * UI State for Settings screen
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val autoplayEnabled: Boolean = true,
    val screenTimeEnabled: Boolean = false,
    val screenTimeDailyLimit: Int = 60,
    val serverUrl: String = "",
    val username: String = "",
    val shouldNavigateToSetup: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    val error: String? = null
) {
    fun hasError(): Boolean = error != null
}
