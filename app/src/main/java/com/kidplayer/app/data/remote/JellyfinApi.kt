package com.kidplayer.app.data.remote

import com.kidplayer.app.data.remote.dto.AuthRequest
import com.kidplayer.app.data.remote.dto.AuthResponse
import com.kidplayer.app.data.remote.dto.ItemsResponse
import com.kidplayer.app.data.remote.dto.LibrariesResponse
import com.kidplayer.app.data.remote.dto.ServerInfoResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Retrofit interface for Jellyfin API endpoints
 *
 * All API endpoints follow Jellyfin's REST API structure
 * Base URL will be configured dynamically based on user's server
 */
interface JellyfinApi {

    /**
     * Get server system information
     * Used to verify server connectivity and compatibility
     */
    @GET("System/Info/Public")
    suspend fun getServerInfo(): Response<ServerInfoResponse>

    /**
     * Authenticate user with username and password
     * Returns access token and user information
     *
     * Jellyfin requires X-Emby-Authorization header even for authentication
     */
    @POST("Users/AuthenticateByName")
    suspend fun authenticate(
        @Header("X-Emby-Authorization") authHeader: String,
        @Body request: AuthRequest
    ): Response<AuthResponse>

    /**
     * Get all media libraries/folders available to the user
     * Requires authentication token
     *
     * Note: Using /Users/{userId}/Views instead of /Library/MediaFolders
     * because MediaFolders is an admin-level endpoint that returns 403 for regular users.
     * Views returns user-scoped libraries respecting access permissions.
     */
    @GET("Users/{userId}/Views")
    suspend fun getLibraries(
        @Path("userId") userId: String,
        @Header("X-Emby-Token") authToken: String
    ): Response<LibrariesResponse>

    /**
     * Get user's playlists
     * Returns all playlists created by or accessible to the user
     *
     * @param userId User ID obtained from authentication
     * @param includeItemTypes Filter to only return Playlist items
     * @param recursive Search recursively
     * @param sortBy Field to sort by
     * @param enableUserData Include UserData
     * @param authToken Authentication token
     */
    @GET("Users/{userId}/Items")
    suspend fun getPlaylists(
        @Path("userId") userId: String,
        @Query("includeItemTypes") includeItemTypes: String = "Playlist",
        @Query("recursive") recursive: Boolean = true,
        @Query("sortBy") sortBy: String = "SortName",
        @Query("sortOrder") sortOrder: String = "Ascending",
        @Query("enableUserData") enableUserData: Boolean = true,
        @Header("X-Emby-Token") authToken: String
    ): Response<ItemsResponse>

    /**
     * Get library items (videos, movies, episodes)
     * Can be filtered by parent library ID
     *
     * @param userId User ID obtained from authentication
     * @param parentId Optional parent library ID to filter results
     * @param includeItemTypes Types of items to include (e.g., "Movie,Episode")
     * @param recursive Whether to recursively search subdirectories
     * @param sortBy Field to sort by
     * @param sortOrder Sort direction (Ascending/Descending)
     * @param startIndex Pagination start index
     * @param limit Number of items to return
     * @param enableUserData Include UserData (playback position, played status)
     * @param authToken Authentication token
     */
    @GET("Users/{userId}/Items")
    suspend fun getLibraryItems(
        @Path("userId") userId: String,
        @Query("parentId") parentId: String? = null,
        @Query("includeItemTypes") includeItemTypes: String = "Movie,Episode",
        @Query("recursive") recursive: Boolean = true,
        @Query("sortBy") sortBy: String = "SortName",
        @Query("sortOrder") sortOrder: String = "Ascending",
        @Query("startIndex") startIndex: Int = 0,
        @Query("limit") limit: Int = 100,
        @Query("enableUserData") enableUserData: Boolean = true,
        @Header("X-Emby-Token") authToken: String
    ): Response<ItemsResponse>

    /**
     * Get a specific item by ID
     * Uses the Items endpoint with ids filter to ensure consistent ItemsResponse format
     * and proper UserData inclusion
     *
     * @param userId User ID
     * @param ids Comma-separated item IDs to fetch
     * @param enableUserData Include UserData (playback position, played status)
     * @param authToken Authentication token
     */
    @GET("Users/{userId}/Items")
    suspend fun getItem(
        @Path("userId") userId: String,
        @Query("ids") ids: String,
        @Query("enableUserData") enableUserData: Boolean = true,
        @Header("X-Emby-Token") authToken: String
    ): Response<ItemsResponse>

    /**
     * Get latest media items
     * Returns recently added content
     *
     * @param userId User ID
     * @param parentId Optional parent library ID
     * @param limit Number of items to return
     * @param enableUserData Include UserData (playback position, played status)
     * @param authToken Authentication token
     */
    @GET("Users/{userId}/Items/Latest")
    suspend fun getLatestItems(
        @Path("userId") userId: String,
        @Query("parentId") parentId: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("enableUserData") enableUserData: Boolean = true,
        @Header("X-Emby-Token") authToken: String
    ): Response<ItemsResponse>

    /**
     * Report playback started
     * Must be called when playback begins to create a session
     * Required before Progress/Stopped calls will save position
     *
     * @param request PlaybackStartInfo with item ID and initial state
     * @param authToken Authentication token
     */
    @POST("Sessions/Playing")
    suspend fun reportPlaybackStarted(
        @Body request: com.kidplayer.app.data.remote.dto.PlaybackStartInfo,
        @Header("X-Emby-Token") authToken: String
    ): Response<Unit>

    /**
     * Report playback progress
     * Used to track watched status and resume position
     *
     * @param request PlaybackProgressInfo with position and state
     * @param authToken Authentication token
     */
    @POST("Sessions/Playing/Progress")
    suspend fun reportPlaybackProgress(
        @Body request: com.kidplayer.app.data.remote.dto.PlaybackProgressInfo,
        @Header("X-Emby-Token") authToken: String
    ): Response<Unit>

    /**
     * Report playback stopped
     * Called when video playback ends
     *
     * @param request PlaybackStopInfo with final position
     * @param authToken Authentication token
     */
    @POST("Sessions/Playing/Stopped")
    suspend fun reportPlaybackStopped(
        @Body request: com.kidplayer.app.data.remote.dto.PlaybackStopInfo,
        @Header("X-Emby-Token") authToken: String
    ): Response<Unit>

    /**
     * Download video file
     * Used for offline playback
     *
     * @param itemId Item ID to download
     * @param authToken Authentication token
     * @return Response body containing video file stream
     */
    @Streaming
    @GET("Items/{itemId}/Download")
    suspend fun downloadVideo(
        @Path("itemId") itemId: String,
        @Header("X-Emby-Token") authToken: String
    ): Response<ResponseBody>
}
