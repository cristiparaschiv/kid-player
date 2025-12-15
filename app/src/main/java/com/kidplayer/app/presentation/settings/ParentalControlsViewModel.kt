package com.kidplayer.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.model.AccessSchedule
import com.kidplayer.app.domain.model.ContentFiltering
import com.kidplayer.app.domain.model.ParentalControls
import com.kidplayer.app.domain.model.PinVerificationResult
import com.kidplayer.app.domain.model.ScreenTimeConfig
import com.kidplayer.app.domain.usecase.GetParentalControlsUseCase
import com.kidplayer.app.domain.usecase.SetParentPinUseCase
import com.kidplayer.app.domain.usecase.UpdateAccessScheduleUseCase
import com.kidplayer.app.domain.usecase.UpdateScreenTimeConfigUseCase
import com.kidplayer.app.domain.usecase.VerifyParentPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing parental controls and settings
 */
@HiltViewModel
class ParentalControlsViewModel @Inject constructor(
    private val getParentalControls: GetParentalControlsUseCase,
    private val setParentPin: SetParentPinUseCase,
    private val verifyParentPin: VerifyParentPinUseCase,
    private val updateScreenTimeConfigUseCase: UpdateScreenTimeConfigUseCase,
    private val updateAccessScheduleUseCase: UpdateAccessScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentalControlsUiState())
    val uiState: StateFlow<ParentalControlsUiState> = _uiState.asStateFlow()

    init {
        loadParentalControls()
    }

    /**
     * Load parental controls configuration
     */
    private fun loadParentalControls() {
        viewModelScope.launch {
            getParentalControls()
                .catch { e ->
                    Timber.e(e, "Error loading parental controls")
                    _uiState.update { it.copy(error = "Failed to load settings") }
                }
                .collectLatest { controls ->
                    Timber.d("Flow emitted: isPinSet=${controls.isPinSet}, screenTimeEnabled=${controls.screenTimeConfig.isEnabled}")
                    _uiState.update { currentState ->
                        // If we already have isPinSet=true locally (from setup), preserve it
                        val mergedControls = if (currentState.parentalControls?.isPinSet == true && !controls.isPinSet) {
                            controls.copy(isPinSet = true)
                        } else {
                            controls
                        }
                        currentState.copy(
                            parentalControls = mergedControls,
                            error = null
                        )
                    }
                }
        }
    }

    /**
     * Show PIN entry dialog
     */
    fun showPinDialog(mode: PinDialogMode) {
        _uiState.update {
            it.copy(
                pinDialogState = PinDialogState(
                    isVisible = true,
                    mode = mode,
                    isError = false,
                    errorMessage = null
                )
            )
        }
    }

    /**
     * Hide PIN entry dialog
     */
    fun dismissPinDialog() {
        _uiState.update {
            it.copy(
                pinDialogState = PinDialogState(isVisible = false)
            )
        }
    }

    /**
     * Handle PIN entry
     */
    fun onPinEntered(pin: String) {
        val currentState = _uiState.value
        val mode = currentState.pinDialogState.mode

        when (mode) {
            PinDialogMode.SETUP -> handlePinSetup(pin)
            PinDialogMode.VERIFY -> handlePinVerification(pin)
            PinDialogMode.CHANGE -> handlePinChange(pin)
        }
    }

    /**
     * Handle PIN setup (first time)
     */
    private fun handlePinSetup(pin: String) {
        viewModelScope.launch {
            val result = setParentPin(pin)
            if (result.isSuccess) {
                Timber.d("PIN setup successful")
                _uiState.update {
                    // Ensure we have parentalControls, create default if null
                    val updatedControls = it.parentalControls?.copy(isPinSet = true)
                        ?: ParentalControls(
                            isPinSet = true,
                            screenTimeConfig = ScreenTimeConfig(
                                isEnabled = false,
                                dailyLimitMinutes = 60,
                                usedTodayMinutes = 0,
                                lastResetDate = java.time.LocalDate.now().toString()
                            ),
                            accessSchedule = null,
                            contentFiltering = ContentFiltering(isEnabled = false)
                        )
                    it.copy(
                        isAuthenticated = true,
                        pinDialogState = PinDialogState(isVisible = false),
                        parentalControls = updatedControls
                    )
                }
            } else {
                Timber.e("PIN setup failed: ${result.exceptionOrNull()?.message}")
                _uiState.update {
                    it.copy(
                        pinDialogState = it.pinDialogState.copy(
                            isError = true,
                            errorMessage = "Failed to set PIN. Please try again."
                        )
                    )
                }
            }
        }
    }

    /**
     * Handle PIN verification
     */
    private fun handlePinVerification(pin: String) {
        viewModelScope.launch {
            when (val result = verifyParentPin(pin)) {
                is PinVerificationResult.Success -> {
                    Timber.d("PIN verification successful")
                    _uiState.update {
                        // Ensure we have parentalControls, create default if null
                        val updatedControls = it.parentalControls?.copy(isPinSet = true)
                            ?: ParentalControls(
                                isPinSet = true,
                                screenTimeConfig = ScreenTimeConfig(
                                    isEnabled = false,
                                    dailyLimitMinutes = 60,
                                    usedTodayMinutes = 0,
                                    lastResetDate = java.time.LocalDate.now().toString()
                                ),
                                accessSchedule = null,
                                contentFiltering = ContentFiltering(isEnabled = false)
                            )
                        it.copy(
                            isAuthenticated = true,
                            pinDialogState = PinDialogState(isVisible = false),
                            parentalControls = updatedControls
                        )
                    }
                }
                is PinVerificationResult.Failure -> {
                    Timber.d("PIN verification failed")
                    _uiState.update {
                        it.copy(
                            pinDialogState = it.pinDialogState.copy(
                                isError = true,
                                errorMessage = "Incorrect PIN. Please try again."
                            )
                        )
                    }
                }
                is PinVerificationResult.NotSet -> {
                    Timber.d("PIN not set, showing setup")
                    showPinDialog(PinDialogMode.SETUP)
                }
            }
        }
    }

    /**
     * Handle PIN change
     */
    private fun handlePinChange(pin: String) {
        handlePinSetup(pin) // Same logic as setup
    }

    /**
     * Update screen time limit enabled status
     */
    fun updateScreenTimeLimitEnabled(enabled: Boolean) {
        Timber.d("updateScreenTimeLimitEnabled called with: $enabled")
        viewModelScope.launch {
            val currentControls = _uiState.value.parentalControls ?: return@launch
            val currentConfig = currentControls.screenTimeConfig
            val updatedConfig = currentConfig.copy(isEnabled = enabled)

            // Update UI immediately for responsive feel
            _uiState.update { state ->
                state.copy(
                    parentalControls = currentControls.copy(screenTimeConfig = updatedConfig)
                )
            }

            updateScreenTimeConfigUseCase(updatedConfig)
                .onSuccess {
                    Timber.d("Screen time limit enabled: $enabled")
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to update screen time limit")
                    // Revert on failure
                    _uiState.update { state ->
                        state.copy(
                            parentalControls = currentControls,
                            error = "Failed to update settings"
                        )
                    }
                }
        }
    }

    /**
     * Update daily screen time limit
     */
    fun updateDailyTimeLimit(minutes: Int) {
        viewModelScope.launch {
            val currentConfig = _uiState.value.parentalControls?.screenTimeConfig ?: return@launch
            val updatedConfig = currentConfig.copy(dailyLimitMinutes = minutes)

            updateScreenTimeConfigUseCase(updatedConfig)
                .onSuccess {
                    Timber.d("Daily time limit updated: $minutes minutes")
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to update daily time limit")
                    _uiState.update { it.copy(error = "Failed to update settings") }
                }
        }
    }

    /**
     * Update access schedule
     */
    fun updateAccessSchedule(schedule: AccessSchedule?) {
        Timber.d("updateAccessSchedule called with: $schedule")
        viewModelScope.launch {
            val currentControls = _uiState.value.parentalControls ?: return@launch

            // Update UI immediately for responsive feel
            _uiState.update { state ->
                state.copy(
                    parentalControls = currentControls.copy(accessSchedule = schedule)
                )
            }

            updateAccessScheduleUseCase.invoke(schedule)
                .onSuccess {
                    Timber.d("Access schedule updated: $schedule")
                }
                .onFailure { e ->
                    Timber.e(e, "Failed to update access schedule")
                    // Revert on failure
                    _uiState.update { state ->
                        state.copy(
                            parentalControls = currentControls,
                            error = "Failed to update settings"
                        )
                    }
                }
        }
    }

    /**
     * Logout (clear authentication)
     */
    fun logout() {
        _uiState.update { it.copy(isAuthenticated = false) }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI state for parental controls screen
 */
data class ParentalControlsUiState(
    val parentalControls: ParentalControls? = null,
    val isAuthenticated: Boolean = false,
    val pinDialogState: PinDialogState = PinDialogState(),
    val error: String? = null,
    val isLoading: Boolean = false
)

/**
 * PIN dialog state
 */
data class PinDialogState(
    val isVisible: Boolean = false,
    val mode: PinDialogMode = PinDialogMode.VERIFY,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

/**
 * PIN dialog mode
 */
enum class PinDialogMode {
    SETUP,   // First-time PIN setup
    VERIFY,  // Verify existing PIN
    CHANGE   // Change existing PIN
}
