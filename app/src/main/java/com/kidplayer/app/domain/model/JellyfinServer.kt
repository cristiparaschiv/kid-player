package com.kidplayer.app.domain.model

/**
 * Represents a configured Jellyfin server with authentication credentials
 */
data class JellyfinServer(
    val url: String,
    val username: String,
    val userId: String,
    val authToken: String
) {
    /**
     * Returns the base URL formatted for API calls
     * Ensures the URL ends without a trailing slash
     */
    fun getFormattedUrl(): String {
        return url.trimEnd('/')
    }

    /**
     * Returns true if the server configuration is valid
     */
    fun isValid(): Boolean {
        return url.isNotBlank() &&
                username.isNotBlank() &&
                userId.isNotBlank() &&
                authToken.isNotBlank()
    }
}
