package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.JellyfinServer
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to authenticate with a Jellyfin server
 * Validates credentials and establishes an authenticated session
 */
class AuthenticateUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Authenticate user with Jellyfin server
     *
     * @param serverUrl Server URL
     * @param username Username
     * @param password Password
     * @return Result containing server configuration or error
     */
    suspend operator fun invoke(
        serverUrl: String,
        username: String,
        password: String
    ): Result<JellyfinServer> {
        // Validate inputs
        if (serverUrl.isBlank()) {
            return Result.Error("Server URL is required")
        }

        if (username.isBlank()) {
            return Result.Error("Username is required")
        }

        if (password.isBlank()) {
            return Result.Error("Password is required")
        }

        val trimmedUrl = serverUrl.trim()

        // Validate URL format
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            return Result.Error("Server URL must start with http:// or https://")
        }

        // Attempt authentication
        return repository.authenticate(
            serverUrl = trimmedUrl,
            username = username.trim(),
            password = password
        )
    }
}
