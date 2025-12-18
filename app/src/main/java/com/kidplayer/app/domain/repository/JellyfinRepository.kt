package com.kidplayer.app.domain.repository

import com.kidplayer.app.domain.model.JellyfinServer
import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.PaginatedResult
import com.kidplayer.app.domain.model.Result

/**
 * Repository interface for Jellyfin server operations
 * This is the contract between the domain and data layers
 *
 * All operations return Result<T> for consistent error handling
 */
interface JellyfinRepository {

    /**
     * Test connection to Jellyfin server
     * Verifies server is reachable and compatible
     *
     * @param serverUrl Full server URL (e.g., "https://jellyfin.example.com")
     * @return Result containing server info or error
     */
    suspend fun testServerConnection(serverUrl: String): Result<String>

    /**
     * Authenticate user with Jellyfin server
     *
     * @param serverUrl Server URL
     * @param username Username
     * @param password Password
     * @return Result containing server configuration or error
     */
    suspend fun authenticate(
        serverUrl: String,
        username: String,
        password: String
    ): Result<JellyfinServer>

    /**
     * Get all media libraries available to the authenticated user
     *
     * @return Result containing list of libraries or error
     */
    suspend fun getLibraries(): Result<List<Library>>

    /**
     * Get media items from a specific library or all libraries
     *
     * @param libraryId Optional library ID to filter by
     * @param limit Maximum number of items to return
     * @param startIndex Pagination start index
     * @return Result containing list of media items or error
     */
    suspend fun getMediaItems(
        libraryId: String? = null,
        limit: Int = 100,
        startIndex: Int = 0
    ): Result<List<MediaItem>>

    /**
     * Get media items with pagination metadata
     *
     * @param libraryId Optional library ID to filter by
     * @param limit Maximum number of items to return
     * @param startIndex Pagination start index
     * @return Result containing paginated media items with metadata
     */
    suspend fun getMediaItemsPaginated(
        libraryId: String? = null,
        limit: Int = 100,
        startIndex: Int = 0
    ): Result<PaginatedResult<MediaItem>>

    /**
     * Get latest/recently added media items
     *
     * @param libraryId Optional library ID to filter by
     * @param limit Maximum number of items to return
     * @return Result containing list of media items or error
     */
    suspend fun getLatestMediaItems(
        libraryId: String? = null,
        limit: Int = 20
    ): Result<List<MediaItem>>

    /**
     * Get a specific media item by ID
     *
     * @param itemId Item ID
     * @return Result containing media item or error
     */
    suspend fun getMediaItem(itemId: String): Result<MediaItem>

    /**
     * Get streaming URL for a media item
     *
     * @param itemId Item ID
     * @return Result containing streaming URL or error
     */
    suspend fun getStreamingUrl(itemId: String): Result<String>

    /**
     * Save server configuration to secure storage
     *
     * @param server Server configuration to save
     */
    suspend fun saveServerConfig(server: JellyfinServer)

    /**
     * Get saved server configuration from secure storage
     *
     * @return JellyfinServer if exists, null otherwise
     */
    suspend fun getServerConfig(): JellyfinServer?

    /**
     * Check if server is configured and user is authenticated
     *
     * @return true if server is configured
     */
    suspend fun isServerConfigured(): Boolean

    /**
     * Clear server configuration and logout
     */
    suspend fun clearServerConfig()

    /**
     * Validate current session by testing if saved credentials are still valid
     * Makes a lightweight API call to verify authentication
     *
     * @return Result indicating if session is valid
     */
    suspend fun validateSession(): Result<Unit>

    /**
     * Report playback started to Jellyfin
     * Must be called when playback begins to create a session
     *
     * @param itemId Item ID
     */
    suspend fun reportPlaybackStarted(itemId: String): Result<Unit>

    /**
     * Report playback progress to Jellyfin
     * Used to track watched status and resume position
     *
     * @param itemId Item ID
     * @param positionTicks Playback position in ticks
     * @param isPaused Whether playback is paused
     */
    suspend fun reportPlaybackProgress(
        itemId: String,
        positionTicks: Long,
        isPaused: Boolean = false
    ): Result<Unit>

    /**
     * Report playback stopped to Jellyfin
     *
     * @param itemId Item ID
     * @param positionTicks Final playback position in ticks
     */
    suspend fun reportPlaybackStopped(
        itemId: String,
        positionTicks: Long
    ): Result<Unit>
}
