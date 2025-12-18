package com.kidplayer.app.data.repository

import com.kidplayer.app.data.local.SecurePreferences
import com.kidplayer.app.data.local.dao.LibraryDao
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.local.mapper.EntityMapper
import com.kidplayer.app.data.network.NetworkMonitor
import com.kidplayer.app.data.remote.JellyfinApi
import com.kidplayer.app.data.remote.JellyfinApiProvider
import com.kidplayer.app.data.remote.dto.AuthRequest
import com.kidplayer.app.data.remote.mapper.JellyfinMapper
import com.kidplayer.app.domain.model.JellyfinServer
import com.kidplayer.app.domain.model.Library
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.PaginatedResult
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.repository.JellyfinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of JellyfinRepository
 * Handles all Jellyfin API calls and local storage with caching
 */
@Singleton
class JellyfinRepositoryImpl @Inject constructor(
    private val apiProvider: JellyfinApiProvider,
    private val securePreferences: SecurePreferences,
    private val mediaItemDao: MediaItemDao,
    private val libraryDao: LibraryDao,
    private val networkMonitor: NetworkMonitor
) : JellyfinRepository {

    override suspend fun testServerConnection(serverUrl: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val api = apiProvider.getApi(serverUrl)
                val response = api.getServerInfo()

                if (response.isSuccessful && response.body() != null) {
                    val serverInfo = response.body()!!
                    Result.Success("Connected to ${serverInfo.serverName} (v${serverInfo.version})")
                } else {
                    Result.Error("Unable to connect to server: ${response.code()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error testing server connection")
                Result.Error("Connection failed: ${e.message ?: "Unknown error"}")
            }
        }

    override suspend fun authenticate(
        serverUrl: String,
        username: String,
        password: String
    ): Result<JellyfinServer> = withContext(Dispatchers.IO) {
        try {
            val api = apiProvider.getApi(serverUrl)
            val authHeader = buildAuthHeader()
            val response = api.authenticate(
                authHeader = authHeader,
                request = AuthRequest(
                    username = username,
                    password = password
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                Timber.d("Authentication successful - AccessToken received: ${authResponse.accessToken.length} chars")

                val server = JellyfinMapper.mapAuthResponseToServer(
                    response = authResponse,
                    serverUrl = serverUrl,
                    username = username
                )

                Timber.d("Mapped server - UserId: ${server.userId}, Token length: ${server.authToken.length}")

                // Save to secure storage
                saveServerConfig(server)
                Timber.d("Server config saved to secure storage")

                Result.Success(server)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid username or password"
                    403 -> "Access forbidden"
                    404 -> "Server not found"
                    else -> "Authentication failed: ${response.code()}"
                }
                Result.Error(errorMessage)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error during authentication")
            Result.Error("Authentication failed: ${e.message ?: "Unknown error"}")
        }
    }

    override suspend fun getLibraries(): Result<List<Library>> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("Not authenticated")

            // Check network state before making API call
            if (!networkMonitor.isOnline()) {
                Timber.d("Device is offline, returning cached libraries for user: ${server.userId}")
                val cachedLibraries = libraryDao.getAllLibraries(server.userId).first()
                val libraries = EntityMapper.entityListToLibraryList(cachedLibraries)

                return@withContext if (libraries.isNotEmpty()) {
                    Result.Success(libraries)
                } else {
                    Result.Error("No cached libraries available. Please connect to the internet.")
                }
            }

            val api = apiProvider.getApi(server.url)
            val response = api.getLibraries(
                userId = server.userId,
                authToken = server.authToken
            )

            if (response.isSuccessful && response.body() != null) {
                val libraries = JellyfinMapper.mapLibraryDtoListToLibraryList(
                    response.body()!!.items
                )

                // Cache libraries locally with userId for user-scoped caching
                val libraryEntities = EntityMapper.libraryListToEntityList(libraries, server.userId)
                libraryDao.insertLibraries(libraryEntities)

                Result.Success(libraries)
            } else {
                // Try to return cached libraries if API call fails
                Timber.w("API call failed, returning cached libraries for user: ${server.userId}")
                val cachedLibraries = libraryDao.getAllLibraries(server.userId).first()
                val libraries = EntityMapper.entityListToLibraryList(cachedLibraries)

                if (libraries.isNotEmpty()) {
                    Result.Success(libraries)
                } else {
                    Result.Error("Failed to fetch libraries: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching libraries")

            // Try to return cached libraries on error
            try {
                val server = getServerConfig()
                if (server != null) {
                    val cachedLibraries = libraryDao.getAllLibraries(server.userId).first()
                    val libraries = EntityMapper.entityListToLibraryList(cachedLibraries)

                    if (libraries.isNotEmpty()) {
                        Result.Success(libraries)
                    } else {
                        Result.Error("Failed to fetch libraries: ${e.message ?: "Unknown error"}")
                    }
                } else {
                    Result.Error("Failed to fetch libraries: ${e.message ?: "Unknown error"}")
                }
            } catch (cacheError: Exception) {
                Result.Error("Failed to fetch libraries: ${e.message ?: "Unknown error"}")
            }
        }
    }

    override suspend fun getMediaItems(
        libraryId: String?,
        limit: Int,
        startIndex: Int
    ): Result<List<MediaItem>> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("Not authenticated")

            // Check network state before making API call
            if (!networkMonitor.isOnline()) {
                Timber.d("Device is offline, returning cached media items for user: ${server.userId}")
                val cachedItems = if (libraryId != null) {
                    mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                } else {
                    mediaItemDao.getAllMediaItems(server.userId).first()
                }
                val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)

                return@withContext if (mediaItems.isNotEmpty()) {
                    Result.Success(mediaItems)
                } else {
                    Result.Error("No cached videos available. Please connect to the internet.")
                }
            }

            Timber.d("Fetching media items - LibraryId: $libraryId, UserId: ${server.userId}, Token length: ${server.authToken.length}")

            val api = apiProvider.getApi(server.url)
            val response = api.getLibraryItems(
                userId = server.userId,
                parentId = libraryId,
                limit = limit,
                startIndex = startIndex,
                authToken = server.authToken
            )

            Timber.d("Media items response - Success: ${response.isSuccessful}, Code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val mediaItems = JellyfinMapper.mapMediaItemDtoListToMediaItemList(
                    dtoList = response.body()!!.items,
                    serverUrl = server.url,
                    authToken = server.authToken
                )

                // Cache media items locally with userId for user-scoped caching
                val mediaItemEntities = EntityMapper.mediaItemListToEntityList(
                    mediaItems = mediaItems,
                    userId = server.userId,
                    libraryId = libraryId
                )
                mediaItemDao.insertMediaItems(mediaItemEntities)

                Result.Success(mediaItems)
            } else {
                // Try to return cached items if API call fails
                Timber.w("API call failed, returning cached media items for user: ${server.userId}")
                val cachedItems = if (libraryId != null) {
                    mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                } else {
                    mediaItemDao.getAllMediaItems(server.userId).first()
                }
                val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)

                if (mediaItems.isNotEmpty()) {
                    Result.Success(mediaItems)
                } else {
                    Result.Error("Failed to fetch media items: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching media items")

            // Try to return cached items on error
            try {
                val server = getServerConfig()
                if (server != null) {
                    val cachedItems = if (libraryId != null) {
                        mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                    } else {
                        mediaItemDao.getAllMediaItems(server.userId).first()
                    }
                    val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)

                    if (mediaItems.isNotEmpty()) {
                        Result.Success(mediaItems)
                    } else {
                        Result.Error("Failed to fetch media items: ${e.message ?: "Unknown error"}")
                    }
                } else {
                    Result.Error("Failed to fetch media items: ${e.message ?: "Unknown error"}")
                }
            } catch (cacheError: Exception) {
                Result.Error("Failed to fetch media items: ${e.message ?: "Unknown error"}")
            }
        }
    }

    override suspend fun getMediaItemsPaginated(
        libraryId: String?,
        limit: Int,
        startIndex: Int
    ): Result<PaginatedResult<MediaItem>> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("Not authenticated")

            // Check network state before making API call
            if (!networkMonitor.isOnline()) {
                Timber.d("Device is offline, returning cached media items for user: ${server.userId}")
                val cachedItems = if (libraryId != null) {
                    mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                } else {
                    mediaItemDao.getAllMediaItems(server.userId).first()
                }
                val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)

                return@withContext if (mediaItems.isNotEmpty()) {
                    // Create paginated result from cached data
                    // Note: startIndex is ignored for cached data, we return all cached items
                    val paginatedResult = PaginatedResult.create(
                        items = mediaItems,
                        totalCount = mediaItems.size,
                        startIndex = 0
                    )
                    Timber.d("Returned ${mediaItems.size} cached items (offline mode)")
                    Result.Success(paginatedResult)
                } else {
                    Result.Error("No cached videos available. Please connect to the internet.")
                }
            }

            Timber.d("Fetching paginated media items - LibraryId: $libraryId, UserId: ${server.userId}, Token length: ${server.authToken.length}")

            val api = apiProvider.getApi(server.url)
            val response = api.getLibraryItems(
                userId = server.userId,
                parentId = libraryId,
                limit = limit,
                startIndex = startIndex,
                authToken = server.authToken
            )

            Timber.d("Paginated media items response - Success: ${response.isSuccessful}, Code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val mediaItems = JellyfinMapper.mapMediaItemDtoListToMediaItemList(
                    dtoList = responseBody.items,
                    serverUrl = server.url,
                    authToken = server.authToken
                )

                // Cache media items locally with userId for user-scoped caching
                val mediaItemEntities = EntityMapper.mediaItemListToEntityList(
                    mediaItems = mediaItems,
                    userId = server.userId,
                    libraryId = libraryId
                )
                mediaItemDao.insertMediaItems(mediaItemEntities)

                // Create paginated result with total count
                val paginatedResult = PaginatedResult.create(
                    items = mediaItems,
                    totalCount = responseBody.totalRecordCount,
                    startIndex = startIndex
                )

                Timber.d("Fetched ${mediaItems.size} items (${startIndex + 1}-${startIndex + mediaItems.size} of ${responseBody.totalRecordCount})")

                Result.Success(paginatedResult)
            } else {
                // Try to return cached items if API call fails
                Timber.w("API call failed, returning cached media items for user: ${server.userId}")
                val cachedItems = if (libraryId != null) {
                    mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                } else {
                    mediaItemDao.getAllMediaItems(server.userId).first()
                }
                val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)

                if (mediaItems.isNotEmpty()) {
                    val paginatedResult = PaginatedResult.create(
                        items = mediaItems,
                        totalCount = mediaItems.size,
                        startIndex = 0
                    )
                    Result.Success(paginatedResult)
                } else {
                    Result.Error("Failed to fetch media items: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching media items")

            // Try to return cached items on error
            try {
                val server = getServerConfig()
                if (server != null) {
                    val cachedItems = if (libraryId != null) {
                        mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                    } else {
                        mediaItemDao.getAllMediaItems(server.userId).first()
                    }
                    val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)

                    if (mediaItems.isNotEmpty()) {
                        val paginatedResult = PaginatedResult.create(
                            items = mediaItems,
                            totalCount = mediaItems.size,
                            startIndex = 0
                        )
                        Result.Success(paginatedResult)
                    } else {
                        Result.Error("Failed to fetch media items: ${e.message ?: "Unknown error"}")
                    }
                } else {
                    Result.Error("Failed to fetch media items: ${e.message ?: "Unknown error"}")
                }
            } catch (cacheError: Exception) {
                Result.Error("Failed to fetch media items: ${e.message ?: "Unknown error"}")
            }
        }
    }

    override suspend fun getLatestMediaItems(
        libraryId: String?,
        limit: Int
    ): Result<List<MediaItem>> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("Not authenticated")

            // Check network state before making API call
            if (!networkMonitor.isOnline()) {
                Timber.d("Device is offline, returning cached latest items for user: ${server.userId}")
                val cachedItems = if (libraryId != null) {
                    mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                } else {
                    mediaItemDao.getAllMediaItems(server.userId).first()
                }
                val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)
                    .take(limit) // Limit to requested amount

                return@withContext if (mediaItems.isNotEmpty()) {
                    Result.Success(mediaItems)
                } else {
                    Result.Error("No cached videos available. Please connect to the internet.")
                }
            }

            val api = apiProvider.getApi(server.url)
            val response = api.getLatestItems(
                userId = server.userId,
                parentId = libraryId,
                limit = limit,
                authToken = server.authToken
            )

            if (response.isSuccessful && response.body() != null) {
                val mediaItems = JellyfinMapper.mapMediaItemDtoListToMediaItemList(
                    dtoList = response.body()!!.items,
                    serverUrl = server.url,
                    authToken = server.authToken
                )
                Result.Success(mediaItems)
            } else {
                // Try to return cached items if API call fails
                Timber.w("API call failed, returning cached latest items for user: ${server.userId}")
                val cachedItems = if (libraryId != null) {
                    mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                } else {
                    mediaItemDao.getAllMediaItems(server.userId).first()
                }
                val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)
                    .take(limit)

                if (mediaItems.isNotEmpty()) {
                    Result.Success(mediaItems)
                } else {
                    Result.Error("Failed to fetch latest items: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching latest items")

            // Try to return cached items on error
            try {
                val server = getServerConfig()
                if (server != null) {
                    val cachedItems = if (libraryId != null) {
                        mediaItemDao.getMediaItemsByLibrary(server.userId, libraryId).first()
                    } else {
                        mediaItemDao.getAllMediaItems(server.userId).first()
                    }
                    val mediaItems = EntityMapper.entityListToMediaItemList(cachedItems)
                        .take(limit)

                    if (mediaItems.isNotEmpty()) {
                        Result.Success(mediaItems)
                    } else {
                        Result.Error("Failed to fetch latest items: ${e.message ?: "Unknown error"}")
                    }
                } else {
                    Result.Error("Failed to fetch latest items: ${e.message ?: "Unknown error"}")
                }
            } catch (cacheError: Exception) {
                Result.Error("Failed to fetch latest items: ${e.message ?: "Unknown error"}")
            }
        }
    }

    override suspend fun getMediaItem(itemId: String): Result<MediaItem> =
        withContext(Dispatchers.IO) {
            try {
                val server = getServerConfig()
                    ?: return@withContext Result.Error("Not authenticated")

                // Check network state before making API call
                if (!networkMonitor.isOnline()) {
                    Timber.d("Device is offline, returning cached media item for user: ${server.userId}")
                    val cachedItem = mediaItemDao.getMediaItemById(server.userId, itemId)

                    return@withContext if (cachedItem != null) {
                        val mediaItem = EntityMapper.entityToMediaItem(cachedItem)
                        Result.Success(mediaItem)
                    } else {
                        Result.Error("Item not available offline. Please connect to the internet.")
                    }
                }

                val api = apiProvider.getApi(server.url)
                val response = api.getItem(
                    userId = server.userId,
                    ids = itemId,
                    authToken = server.authToken
                )

                if (response.isSuccessful && response.body() != null) {
                    val items = response.body()!!.items
                    if (items.isNotEmpty()) {
                        val mediaItem = JellyfinMapper.mapMediaItemDtoToMediaItem(
                            dto = items.first(),
                            serverUrl = server.url,
                            authToken = server.authToken
                        )
                        Result.Success(mediaItem)
                    } else {
                        Result.Error("Item not found")
                    }
                } else {
                    // Try to return cached item if API call fails
                    Timber.w("API call failed, returning cached media item for user: ${server.userId}")
                    val cachedItem = mediaItemDao.getMediaItemById(server.userId, itemId)

                    if (cachedItem != null) {
                        val mediaItem = EntityMapper.entityToMediaItem(cachedItem)
                        Result.Success(mediaItem)
                    } else {
                        Result.Error("Failed to fetch item: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching media item")

                // Try to return cached item on error
                try {
                    val server = getServerConfig()
                    if (server != null) {
                        val cachedItem = mediaItemDao.getMediaItemById(server.userId, itemId)

                        if (cachedItem != null) {
                            val mediaItem = EntityMapper.entityToMediaItem(cachedItem)
                            Result.Success(mediaItem)
                        } else {
                            Result.Error("Failed to fetch item: ${e.message ?: "Unknown error"}")
                        }
                    } else {
                        Result.Error("Failed to fetch item: ${e.message ?: "Unknown error"}")
                    }
                } catch (cacheError: Exception) {
                    Result.Error("Failed to fetch item: ${e.message ?: "Unknown error"}")
                }
            }
        }

    override suspend fun getStreamingUrl(itemId: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val server = getServerConfig()
                    ?: return@withContext Result.Error("Not authenticated")

                val streamingUrl = JellyfinMapper.buildStreamingUrl(
                    serverUrl = server.url,
                    itemId = itemId,
                    authToken = server.authToken
                )

                Result.Success(streamingUrl)
            } catch (e: Exception) {
                Timber.e(e, "Error building streaming URL")
                Result.Error("Failed to build streaming URL: ${e.message ?: "Unknown error"}")
            }
        }

    override suspend fun saveServerConfig(server: JellyfinServer) {
        withContext(Dispatchers.IO) {
            securePreferences.saveServerUrl(server.url)
            securePreferences.saveAuthToken(server.authToken)
            securePreferences.saveUserId(server.userId)
            securePreferences.saveUsername(server.username)
        }
    }

    override suspend fun getServerConfig(): JellyfinServer? = withContext(Dispatchers.IO) {
        val url = securePreferences.getServerUrl()
        val token = securePreferences.getAuthToken()
        val userId = securePreferences.getUserId()
        val username = securePreferences.getUsername()

        Timber.d("Getting server config - URL: ${!url.isNullOrBlank()}, Token: ${!token.isNullOrBlank()}, UserId: ${!userId.isNullOrBlank()}, Username: ${!username.isNullOrBlank()}")

        if (!url.isNullOrBlank() && !token.isNullOrBlank() &&
            !userId.isNullOrBlank() && !username.isNullOrBlank()
        ) {
            Timber.d("Server config found - URL: $url, UserId: $userId, Username: $username, Token length: ${token.length}")
            JellyfinServer(
                url = url,
                username = username,
                userId = userId,
                authToken = token
            )
        } else {
            Timber.w("Incomplete server config - missing required fields")
            null
        }
    }

    override suspend fun isServerConfigured(): Boolean = withContext(Dispatchers.IO) {
        securePreferences.isServerConfigured()
    }

    override suspend fun clearServerConfig() {
        withContext(Dispatchers.IO) {
            securePreferences.clearCredentials()
            // Also clear cached data
            mediaItemDao.deleteAllMediaItems()
            libraryDao.deleteAllLibraries()
        }
    }

    override suspend fun validateSession(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("No saved session")

            Timber.d("Validating session for server: ${server.url}")
            Timber.d("Auth token present: ${server.authToken.isNotBlank()}")

            // Make a lightweight API call that requires authentication to verify the token is still valid
            // Using /Users/{userId}/Views endpoint which is user-scoped and requires valid authentication
            val api = apiProvider.getApi(server.url)
            val response = api.getLibraries(
                userId = server.userId,
                authToken = server.authToken
            )

            if (response.isSuccessful) {
                Timber.d("Session validation successful")
                Result.Success(Unit)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Authentication expired"
                    403 -> "Access forbidden"
                    else -> "Session validation failed: ${response.code()}"
                }
                Timber.w("Session validation failed: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error validating session")
            Result.Error("Network error: ${e.message ?: "Unable to reach server"}")
        }
    }

    override suspend fun reportPlaybackStarted(itemId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val server = getServerConfig()
                    ?: return@withContext Result.Error("Not authenticated")

                val api = apiProvider.getApi(server.url)
                val request = com.kidplayer.app.data.remote.dto.PlaybackStartInfo(
                    itemId = itemId,
                    canSeek = true,
                    isPaused = false,
                    isMuted = false,
                    positionTicks = 0L
                )
                val response = api.reportPlaybackStarted(
                    request = request,
                    authToken = server.authToken
                )

                if (response.isSuccessful) {
                    Result.Success(Unit)
                } else {
                    Result.Error("Failed to report playback started: ${response.code()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error reporting playback started")
                Result.Error("Failed to report playback started: ${e.message ?: "Unknown error"}")
            }
        }

    override suspend fun reportPlaybackProgress(
        itemId: String,
        positionTicks: Long,
        isPaused: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("Not authenticated")

            val api = apiProvider.getApi(server.url)
            val request = com.kidplayer.app.data.remote.dto.PlaybackProgressInfo(
                itemId = itemId,
                positionTicks = positionTicks,
                canSeek = true,
                isPaused = isPaused,
                isMuted = false
            )
            val response = api.reportPlaybackProgress(
                request = request,
                authToken = server.authToken
            )

            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Failed to report progress: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reporting playback progress")
            Result.Error("Failed to report progress: ${e.message ?: "Unknown error"}")
        }
    }

    override suspend fun reportPlaybackStopped(
        itemId: String,
        positionTicks: Long
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val server = getServerConfig()
                ?: return@withContext Result.Error("Not authenticated")

            val api = apiProvider.getApi(server.url)
            val request = com.kidplayer.app.data.remote.dto.PlaybackStopInfo(
                itemId = itemId,
                positionTicks = positionTicks
            )
            val response = api.reportPlaybackStopped(
                request = request,
                authToken = server.authToken
            )

            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Failed to report stopped: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error reporting playback stopped")
            Result.Error("Failed to report stopped: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Build the X-Emby-Authorization header required by Jellyfin API
     * This header is required even for authentication requests
     */
    private fun buildAuthHeader(): String {
        return "MediaBrowser Client=\"Kid Player\", Device=\"Android\", DeviceId=\"kidplayer-android\", Version=\"1.0.0\""
    }
}
