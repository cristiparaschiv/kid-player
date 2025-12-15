package com.kidplayer.app.domain.usecase

import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import javax.inject.Inject

/**
 * Use case to test connection to a Jellyfin server
 * Validates server URL and checks if server is reachable
 */
class ConnectToServerUseCase @Inject constructor(
    private val repository: JellyfinRepository
) {
    /**
     * Test connection to a Jellyfin server
     *
     * @param serverUrl The server URL to test
     * @return Result containing success message or error
     */
    suspend operator fun invoke(serverUrl: String): Result<String> {
        // Validate server URL
        if (serverUrl.isBlank()) {
            return Result.Error("Server URL cannot be empty")
        }

        val trimmedUrl = serverUrl.trim()

        // Check if URL starts with http or https
        if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            return Result.Error("Server URL must start with http:// or https://")
        }

        // Check for basic URL format
        if (!isValidUrl(trimmedUrl)) {
            return Result.Error("Invalid server URL format")
        }

        // Test connection
        return repository.testServerConnection(trimmedUrl)
    }

    /**
     * Basic URL validation
     */
    private fun isValidUrl(url: String): Boolean {
        return try {
            val regex = Regex("^https?://[^\\s/$.?#].[^\\s]*$")
            regex.matches(url)
        } catch (e: Exception) {
            false
        }
    }
}
