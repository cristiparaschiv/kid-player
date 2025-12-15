package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.repository.JellyfinRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case to logout user and clear all stored credentials
 * Clears server configuration, auth tokens, and cached data
 */
class LogoutUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Logout user by clearing all stored credentials and cached data
     */
    suspend operator fun invoke() {
        try {
            Timber.d("Logging out user")
            repository.clearServerConfig()
            Timber.d("Logout successful")
        } catch (e: Exception) {
            Timber.e(e, "Error during logout")
            // Even if there's an error, we should clear credentials
            repository.clearServerConfig()
        }
    }
}
