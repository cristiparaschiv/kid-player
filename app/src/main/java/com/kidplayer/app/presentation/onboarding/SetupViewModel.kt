package com.kidplayer.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.usecase.AuthenticateUseCase
import com.kidplayer.app.domain.usecase.ConnectToServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Setup/Onboarding screen
 * Manages Jellyfin server connection and authentication
 */
@HiltViewModel
class SetupViewModel @Inject constructor(
    private val connectToServerUseCase: ConnectToServerUseCase,
    private val authenticateUseCase: AuthenticateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    /**
     * Update server URL
     */
    fun onServerUrlChanged(url: String) {
        _uiState.update { it.copy(serverUrl = url, errorMessage = null) }
    }

    /**
     * Update username
     */
    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    /**
     * Update password
     */
    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    /**
     * Test connection to server
     * This is an optional step before authentication
     */
    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = connectToServerUseCase(_uiState.value.serverUrl)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            connectionSuccess = true,
                            successMessage = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            connectionSuccess = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    /**
     * Authenticate with Jellyfin server
     * This will save credentials and navigate to browse screen on success
     */
    fun authenticate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val state = _uiState.value

            when (val result = authenticateUseCase(
                serverUrl = state.serverUrl,
                username = state.username,
                password = state.password
            )) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            successMessage = "Successfully connected to server!"
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Reset authentication state
     * Used after successful navigation to clear the flag
     */
    fun resetAuthenticationState() {
        _uiState.update { it.copy(isAuthenticated = false) }
    }
}

/**
 * UI state for the Setup screen
 */
data class SetupUiState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val connectionSuccess: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    /**
     * Check if the connect button should be enabled
     */
    val canConnect: Boolean
        get() = serverUrl.isNotBlank() && !isLoading

    /**
     * Check if the authenticate button should be enabled
     * Note: Password can be empty (some servers like demo.jellyfin.org allow it)
     */
    val canAuthenticate: Boolean
        get() = serverUrl.isNotBlank() &&
                username.isNotBlank() &&
                !isLoading
}
