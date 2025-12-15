package com.kidplayer.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.usecase.ValidateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Splash Screen
 * Handles auto-login logic by validating saved session
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val validateSessionUseCase: ValidateSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        validateSession()
    }

    /**
     * Validate existing session or navigate to setup
     */
    private fun validateSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isValidating = true) }

            // Small delay for smooth UX (allows splash screen to be visible)
            delay(500)

            when (val result = validateSessionUseCase()) {
                is Result.Success -> {
                    Timber.d("Auto-login successful")
                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            validationResult = ValidationResult.SUCCESS
                        )
                    }
                }
                is Result.Error -> {
                    Timber.d("Auto-login failed: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            validationResult = ValidationResult.FAILED,
                            errorMessage = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Keep validating
                }
            }
        }
    }

    /**
     * Reset validation state
     * Called after navigation to prevent re-triggering
     */
    fun resetValidationState() {
        _uiState.update { it.copy(validationResult = ValidationResult.PENDING) }
    }
}

/**
 * UI State for Splash Screen
 */
data class SplashUiState(
    val isValidating: Boolean = true,
    val validationResult: ValidationResult = ValidationResult.PENDING,
    val errorMessage: String? = null
)

/**
 * Validation result states
 */
enum class ValidationResult {
    PENDING,    // Initial state, validation in progress
    SUCCESS,    // Session is valid, navigate to Home
    FAILED      // Session is invalid or missing, navigate to Setup
}
