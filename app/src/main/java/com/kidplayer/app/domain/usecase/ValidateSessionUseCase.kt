package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.JellyfinServer
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case to validate existing authentication session
 * Checks if saved credentials are still valid by making a test API call
 */
class ValidateSessionUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Validate current session by retrieving server config and testing connection
     *
     * @return Result containing JellyfinServer if session is valid, error otherwise
     */
    suspend operator fun invoke(): Result<JellyfinServer> {
        return try {
            // Get saved server configuration
            val savedServer = repository.getServerConfig()

            if (savedServer == null) {
                Timber.d("No saved server configuration found")
                return Result.Error("Not authenticated")
            }

            Timber.d("Found saved server config, validating session...")

            // Validate the session by attempting to fetch libraries
            // This confirms the auth token is still valid
            val validationResult = repository.validateSession()

            when (validationResult) {
                is Result.Success -> {
                    Timber.d("Session validation successful")
                    Result.Success(savedServer)
                }
                is Result.Error -> {
                    Timber.w("Session validation failed: ${validationResult.message}")
                    Result.Error(validationResult.message)
                }
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Timber.e(e, "Error validating session")
            Result.Error("Session validation failed: ${e.message ?: "Unknown error"}")
        }
    }
}
