package com.kidplayer.app.presentation.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.kidplayer.app.data.local.ScreenTimeManager
import com.kidplayer.app.data.local.dao.MediaItemDao
import com.kidplayer.app.data.network.NetworkMonitor
import com.kidplayer.app.data.network.NetworkState
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.usecase.GetAutoplaySettingUseCase
import com.kidplayer.app.domain.usecase.GetMediaItemUseCase
import com.kidplayer.app.domain.usecase.GetMediaItemsUseCase
import com.kidplayer.app.domain.usecase.GetNextVideoUseCase
import com.kidplayer.app.domain.usecase.GetStreamingUrlUseCase
import com.kidplayer.app.domain.usecase.ReportPlaybackProgressUseCase
import com.kidplayer.app.domain.usecase.ReportPlaybackStartedUseCase
import com.kidplayer.app.domain.usecase.StopPlaybackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.net.Uri
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for the Player screen
 * Manages ExoPlayer lifecycle, playback state, and progress tracking
 */
@UnstableApi
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val player: ExoPlayer,
    private val getStreamingUrlUseCase: GetStreamingUrlUseCase,
    private val getMediaItemUseCase: GetMediaItemUseCase,
    private val reportPlaybackStartedUseCase: ReportPlaybackStartedUseCase,
    private val reportPlaybackProgressUseCase: ReportPlaybackProgressUseCase,
    private val stopPlaybackUseCase: StopPlaybackUseCase,
    private val getMediaItemsUseCase: GetMediaItemsUseCase,
    private val getNextVideoUseCase: GetNextVideoUseCase,
    private val getAutoplaySettingUseCase: GetAutoplaySettingUseCase,
    private val mediaItemDao: MediaItemDao,
    private val screenTimeManager: ScreenTimeManager,
    private val verifyParentPinUseCase: com.kidplayer.app.domain.usecase.VerifyParentPinUseCase,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val videoId: String = checkNotNull(savedStateHandle.get<String>("videoId"))

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var progressTrackingJob: Job? = null
    private var positionUpdateJob: Job? = null
    private var screenTimeCheckJob: Job? = null
    private var autoplayCountdownJob: Job? = null

    // Player listener for state changes
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            Timber.d("Playback state changed: $playbackState")
            when (playbackState) {
                Player.STATE_IDLE -> {
                    _uiState.update { it.copy(isBuffering = false) }
                }
                Player.STATE_BUFFERING -> {
                    _uiState.update { it.copy(isBuffering = true) }
                }
                Player.STATE_READY -> {
                    _uiState.update {
                        it.copy(
                            isBuffering = false,
                            playerReady = true,
                            duration = player.duration.coerceAtLeast(0L)
                        )
                    }
                    startPositionUpdates()
                    logAvailableTracks()

                    // Ensure audio track is properly selected
                    val hasAudio = AudioTrackUtil.ensureAudioTrackSelected(player)
                    if (!hasAudio) {
                        Timber.w("Audio diagnostic: ${AudioTrackUtil.diagnoseAudioIssues(player)}")
                    }

                    // Seek to resume position if available and not already resumed
                    val currentState = _uiState.value
                    if (currentState.resumePositionMs > 0 && !currentState.hasResumedPlayback) {
                        Timber.d("Resuming playback from ${currentState.resumePositionMs}ms")
                        player.seekTo(currentState.resumePositionMs)
                        _uiState.update { it.copy(hasResumedPlayback = true) }
                    }
                }
                Player.STATE_ENDED -> {
                    _uiState.update { it.copy(isPlaying = false) }
                    onPlaybackEnded()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            Timber.d("Is playing changed: $isPlaying")
            _uiState.update { it.copy(isPlaying = isPlaying) }

            if (isPlaying) {
                startProgressTracking()
                startPositionUpdates()
                viewModelScope.launch {
                    screenTimeManager.startTracking()
                }
            } else {
                stopProgressTracking()
                viewModelScope.launch {
                    screenTimeManager.stopTracking()
                }
            }
        }

        override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
            Timber.d("Tracks changed - Total groups: ${tracks.groups.size}")
            logAvailableTracks()

            // Verify audio track is selected
            val hasAudioTrack = tracks.groups.any { group ->
                group.type == androidx.media3.common.C.TRACK_TYPE_AUDIO
            }

            if (!hasAudioTrack) {
                Timber.w("WARNING: No audio tracks available in this media")
            }

            // Log selected audio tracks
            tracks.groups.forEach { group ->
                if (group.type == androidx.media3.common.C.TRACK_TYPE_AUDIO) {
                    for (i in 0 until group.length) {
                        if (group.isTrackSelected(i)) {
                            val format = group.getTrackFormat(i)
                            Timber.d("Selected audio track: ${format.sampleMimeType}, " +
                                    "channels: ${format.channelCount}, " +
                                    "sampleRate: ${format.sampleRate}")
                        }
                    }
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            val errorDetails = buildString {
                append("Player error: ${error.message}")
                append("\nError code: ${error.errorCode}")
                error.cause?.let { cause ->
                    append("\nCause: ${cause.message}")
                    append("\nCause class: ${cause.javaClass.simpleName}")
                }
            }
            Timber.e(error, errorDetails)

            // User-friendly error message
            val userMessage = when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                    "Video file not found. The download may be corrupted."
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                    "Network connection failed. Check your internet connection."
                PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED,
                PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED ->
                    "This video format is not supported."
                else -> "Playback error: ${error.message}"
            }

            _uiState.update {
                it.copy(
                    error = userMessage,
                    isLoading = false,
                    isBuffering = false
                )
            }
        }
    }

    init {
        Timber.d("PlayerViewModel initialized for video: $videoId")
        player.addListener(playerListener)
        loadVideo()
        loadRecommendedVideos()
        startScreenTimeTracking()
        observeNetworkState()
    }

    /**
     * Observe network state changes and reload recommendations when connectivity changes
     */
    private fun observeNetworkState() {
        viewModelScope.launch {
            networkMonitor.networkState.collect { state ->
                val isOffline = state == NetworkState.OFFLINE
                val wasOffline = _uiState.value.isNetworkOffline

                _uiState.update { it.copy(isNetworkOffline = isOffline) }

                // Reload recommendations if network state changed
                if (isOffline != wasOffline) {
                    Timber.d("Network state changed: offline=$isOffline, reloading recommendations")
                    loadRecommendedVideos()
                }
            }
        }
    }

    /**
     * Check if device is currently offline
     */
    private fun isOffline(): Boolean {
        return !networkMonitor.isOnline()
    }

    /**
     * Load video metadata and streaming URL
     * Checks for local download first, falls back to streaming
     * Fetches fresh data from Jellyfin to get current resume position
     */
    private fun loadVideo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val isCurrentlyOffline = isOffline()
            Timber.d("=== Loading video: $videoId ===")
            Timber.d("Network status: offline=$isCurrentlyOffline")

            // First, check if video is downloaded locally
            val mediaItemEntity = mediaItemDao.getMediaItemByIdOnly(videoId)

            if (mediaItemEntity == null) {
                Timber.e("Media item not found in database for ID: $videoId")
                _uiState.update {
                    it.copy(
                        error = "Video not found in database",
                        isLoading = false
                    )
                }
                return@launch
            }

            Timber.d("Media item found: title=${mediaItemEntity.title}, isDownloaded=${mediaItemEntity.isDownloaded}")
            Timber.d("localFilePath=${mediaItemEntity.localFilePath}")
            Timber.d("jellyfinItemId=${mediaItemEntity.jellyfinItemId}")

            val localFilePath = mediaItemEntity.localFilePath
            val hasLocalFile = mediaItemEntity.isDownloaded && !localFilePath.isNullOrEmpty()

            // Verify local file actually exists on disk
            val localFileExists = if (hasLocalFile && localFilePath != null) {
                val file = File(localFilePath)
                val exists = file.exists()
                val size = if (exists) file.length() else 0L
                Timber.d("Local file check: exists=$exists, size=$size bytes, path=$localFilePath")
                exists && size > 0
            } else {
                Timber.d("No local file to check (hasLocalFile=$hasLocalFile)")
                false
            }

            val isOfflineAvailable = hasLocalFile && localFileExists
            Timber.d("Offline playback available: $isOfflineAvailable (hasLocalFile=$hasLocalFile, localFileExists=$localFileExists)")

            // Get resume position - fetch fresh data from Jellyfin API for accurate position
            // Skip API call if offline - use cached position
            var resumePositionMs = 0L
            if (!isCurrentlyOffline) {
                when (val mediaItemResult = getMediaItemUseCase(videoId)) {
                    is Result.Success -> {
                        val freshMediaItem = mediaItemResult.data
                        resumePositionMs = freshMediaItem.getResumePositionMs()
                    }
                    is Result.Error -> {
                        // Fall back to cached position if API fails
                        val cachedPositionTicks = mediaItemEntity?.playbackPositionTicks ?: 0L
                        resumePositionMs = cachedPositionTicks / 10_000
                        Timber.w("Failed to get fresh media item data, using cached position")
                    }
                    is Result.Loading -> {
                        // Shouldn't happen, but use cached position
                        val cachedPositionTicks = mediaItemEntity?.playbackPositionTicks ?: 0L
                        resumePositionMs = cachedPositionTicks / 10_000
                    }
                }
            } else {
                // Offline - use cached position from database
                val cachedPositionTicks = mediaItemEntity?.playbackPositionTicks ?: 0L
                resumePositionMs = cachedPositionTicks / 10_000
                Timber.d("Offline mode: using cached resume position ${resumePositionMs}ms")
            }

            val videoUrl = if (isOfflineAvailable) {
                // Play from local file
                Timber.d("Playing from local file: $localFilePath")

                _uiState.update {
                    it.copy(
                        isOfflinePlayback = true
                    )
                }

                // Use Uri.fromFile for proper file URI handling (handles special characters in path)
                val fileUri = Uri.fromFile(File(localFilePath!!))
                Timber.d("File URI: $fileUri")
                fileUri.toString()
            } else if (isCurrentlyOffline) {
                // Offline but no local file available
                Timber.e("Cannot play: offline and no downloaded file available")
                _uiState.update {
                    it.copy(
                        error = "This video is not available offline. Please download it first or connect to the internet.",
                        isLoading = false
                    )
                }
                return@launch
            } else {
                // Stream from Jellyfin
                Timber.d("Streaming from Jellyfin server")

                when (val result = getStreamingUrlUseCase(videoId)) {
                    is Result.Success -> {
                        result.data
                    }
                    is Result.Error -> {
                        Timber.e("Error getting streaming URL: ${result.message}")
                        _uiState.update {
                            it.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        return@launch
                    }
                    is Result.Loading -> {
                        return@launch
                    }
                }
            }

            Timber.d("Video URL: $videoUrl, Resume position: ${resumePositionMs}ms")

            _uiState.update {
                it.copy(
                    streamingUrl = videoUrl,
                    isLoading = false,
                    resumePositionMs = resumePositionMs
                )
            }

            // Prepare media item and start playback
            val mediaItem = MediaItem.fromUri(videoUrl)
            player.setMediaItem(mediaItem)
            player.prepare()

            // Report playback started to Jellyfin (only when online - required for progress tracking)
            if (!isCurrentlyOffline) {
                reportPlaybackStartedUseCase(videoId)
            }

            // Auto-play: Start playback immediately when media is ready
            player.play()

            // Log resume position for debugging
            if (resumePositionMs > 0) {
                Timber.d("Will resume playback from position: ${resumePositionMs}ms")
            }
        }
    }

    /**
     * Load recommended videos for the recommendation row
     * Strategy:
     * - For episodes: Show next episodes from same series
     * - For movies/standalone: Show videos from same library
     * - Exclude current video
     * - Max 8 recommendations
     * - When offline: Only show downloaded videos
     */
    private fun loadRecommendedVideos() {
        viewModelScope.launch {
            val offlineMode = isOffline()
            Timber.d("Loading recommendations, offline mode: $offlineMode")

            when (val result = getMediaItemsUseCase(null)) {
                is Result.Success -> {
                    val allItems = result.data
                    val currentItem = allItems.find { it.id == videoId }

                    if (currentItem == null) {
                        Timber.w("Current video not found for recommendations")
                        return@launch
                    }

                    val recommendations = buildRecommendationsList(
                        currentItem = currentItem,
                        allItems = allItems,
                        offlineOnly = offlineMode
                    )

                    _uiState.update {
                        it.copy(
                            mediaItem = currentItem,
                            recommendedVideos = recommendations
                        )
                    }

                    Timber.d("Loaded ${recommendations.size} recommended videos for ${currentItem.title} (offline=$offlineMode)")
                }
                is Result.Error -> {
                    Timber.e("Failed to load recommendations: ${result.message}")
                }
                is Result.Loading -> {
                    // Loading state, nothing to do
                }
            }
        }
    }

    /**
     * Build list of recommended videos
     * Returns up to 8 videos for the recommendation row
     * @param offlineOnly When true, only include downloaded videos
     */
    private fun buildRecommendationsList(
        currentItem: com.kidplayer.app.domain.model.MediaItem,
        allItems: List<com.kidplayer.app.domain.model.MediaItem>,
        offlineOnly: Boolean = false
    ): List<com.kidplayer.app.domain.model.MediaItem> {
        // Filter to downloaded-only if in offline mode
        val availableItems = if (offlineOnly) {
            allItems.filter { it.isDownloaded }
        } else {
            allItems
        }

        if (offlineOnly) {
            Timber.d("Offline mode: filtering to ${availableItems.size} downloaded videos")
        }

        val recommendations = mutableListOf<com.kidplayer.app.domain.model.MediaItem>()

        // Strategy 1: If it's an episode, get next episodes in series
        val seriesName = currentItem.seriesName
        val seasonNumber = currentItem.seasonNumber
        val episodeNumber = currentItem.episodeNumber

        if (seriesName != null && seasonNumber != null && episodeNumber != null) {
            // Get next episodes from current season
            val nextEpisodes = availableItems
                .filter {
                    it.seriesName == seriesName &&
                    it.seasonNumber == seasonNumber &&
                    it.episodeNumber != null &&
                    it.episodeNumber > episodeNumber &&
                    it.id != currentItem.id
                }
                .sortedBy { it.episodeNumber }
                .take(5)

            recommendations.addAll(nextEpisodes)

            // If we need more, get episodes from next season
            if (recommendations.size < 5) {
                val nextSeasonEpisodes = availableItems
                    .filter {
                        it.seriesName == seriesName &&
                        it.seasonNumber != null &&
                        it.seasonNumber > seasonNumber &&
                        it.id != currentItem.id &&
                        !recommendations.contains(it)
                    }
                    .sortedWith(
                        compareBy<com.kidplayer.app.domain.model.MediaItem> { it.seasonNumber }
                            .thenBy { it.episodeNumber }
                    )
                    .take(5 - recommendations.size)

                recommendations.addAll(nextSeasonEpisodes)
            }
        }

        // Strategy 2: Fill remaining slots with videos from same library (shuffled for variety)
        if (recommendations.size < MAX_RECOMMENDATIONS) {
            val sameLibraryVideos = availableItems
                .filter {
                    it.libraryId == currentItem.libraryId &&
                    it.id != currentItem.id &&
                    !recommendations.contains(it)
                }
                .shuffled()
                .take(MAX_RECOMMENDATIONS - recommendations.size)

            recommendations.addAll(sameLibraryVideos)
        }

        return recommendations.take(MAX_RECOMMENDATIONS)
    }

    /**
     * Toggle play/pause
     * Blocked if time limit is reached
     */
    fun togglePlayPause() {
        // Check if time limit is reached - if so, don't allow playback
        if (_uiState.value.isTimeLimitReached) {
            Timber.d("Playback blocked - time limit reached")
            return
        }

        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    /**
     * Play video
     * Blocked if time limit is reached
     */
    fun play() {
        // Check if time limit is reached - if so, don't allow playback
        if (_uiState.value.isTimeLimitReached) {
            Timber.d("Playback blocked - time limit reached")
            return
        }

        player.play()
    }

    /**
     * Pause video
     */
    fun pause() {
        player.pause()
    }

    /**
     * Seek to specific position
     * @param positionMs Position in milliseconds
     */
    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _uiState.update { it.copy(currentPosition = positionMs) }
    }

    /**
     * Seek forward by 10 seconds
     */
    fun seekForward() {
        val newPosition = (player.currentPosition + 10_000).coerceAtMost(player.duration)
        player.seekTo(newPosition)
    }

    /**
     * Seek backward by 10 seconds
     */
    fun seekBackward() {
        val newPosition = (player.currentPosition - 10_000).coerceAtLeast(0)
        player.seekTo(newPosition)
    }

    /**
     * Start tracking and reporting playback progress to Jellyfin
     * Reports progress every 10 seconds
     */
    private fun startProgressTracking() {
        if (progressTrackingJob?.isActive == true) return

        progressTrackingJob = viewModelScope.launch {
            while (isActive && player.isPlaying) {
                delay(10_000) // Report every 10 seconds

                val position = player.currentPosition
                Timber.d("Reporting playback progress: ${position}ms")

                reportPlaybackProgressUseCase(
                    itemId = videoId,
                    positionMs = position,
                    isPaused = !player.isPlaying
                )
            }
        }
    }

    /**
     * Stop progress tracking
     */
    private fun stopProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = null
    }

    /**
     * Start updating current position in UI state
     * Updates every 500ms for smooth progress bar
     */
    private fun startPositionUpdates() {
        if (positionUpdateJob?.isActive == true) return

        positionUpdateJob = viewModelScope.launch {
            while (isActive) {
                if (player.playbackState != Player.STATE_IDLE) {
                    _uiState.update {
                        it.copy(
                            currentPosition = player.currentPosition,
                            duration = player.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(500) // Update UI every 500ms
            }
        }
    }

    /**
     * Start tracking screen time and checking for limits
     * Updates remaining time every 30 seconds
     */
    private fun startScreenTimeTracking() {
        screenTimeCheckJob = viewModelScope.launch {
            while (isActive) {
                val config = screenTimeManager.getScreenTimeConfig()

                _uiState.update {
                    it.copy(
                        screenTimeRemaining = if (config.isEnabled) config.remainingMinutes else null,
                        isTimeLimitReached = config.isLimitReached,
                        timeLimitDailyMinutes = config.dailyLimitMinutes,
                        timeLimitUsedMinutes = config.usedTodayMinutes
                    )
                }

                if (config.isEnabled && config.isLimitReached) {
                    // Time limit reached - pause playback and stop screen time tracking
                    Timber.d("Screen time limit reached, stopping playback")
                    player.pause()
                    screenTimeManager.stopTracking()
                }

                delay(30_000) // Check every 30 seconds
            }
        }
    }

    /**
     * Handle playback ended
     * Checks autoplay setting and starts countdown if enabled
     * When offline, only considers downloaded videos for autoplay
     */
    private fun onPlaybackEnded() {
        viewModelScope.launch {
            // Report final position
            stopPlaybackUseCase(videoId, player.currentPosition)

            Timber.d("Playback ended for video: $videoId")

            // Check if autoplay is enabled
            val autoplayEnabled = getAutoplaySettingUseCase()
            Timber.d("Autoplay enabled: $autoplayEnabled")

            if (!autoplayEnabled) {
                Timber.d("Autoplay disabled, not loading next video")
                return@launch
            }

            // Get next video (only downloaded videos if offline)
            val offlineMode = isOffline()
            Timber.d("Getting next video, offline mode: $offlineMode")

            when (val result = getNextVideoUseCase(videoId, offlineOnly = offlineMode)) {
                is Result.Success -> {
                    val nextVideo = result.data
                    if (nextVideo != null) {
                        Timber.d("Next video found: ${nextVideo.title} (downloaded: ${nextVideo.isDownloaded})")
                        _uiState.update {
                            it.copy(
                                nextMediaItem = nextVideo,
                                autoplayCountdown = AUTOPLAY_COUNTDOWN_SECONDS
                            )
                        }
                        startAutoplayCountdown()
                    } else {
                        Timber.d("No next video available${if (offlineMode) " (offline mode - no downloaded videos)" else ""}")
                    }
                }
                is Result.Error -> {
                    Timber.e("Error getting next video: ${result.message}")
                }
                is Result.Loading -> {
                    // Loading state, nothing to do
                }
            }
        }
    }

    /**
     * Start autoplay countdown timer
     * Decrements counter every second and plays next video when it reaches 0
     */
    private fun startAutoplayCountdown() {
        autoplayCountdownJob?.cancel()
        autoplayCountdownJob = viewModelScope.launch {
            var countdown = AUTOPLAY_COUNTDOWN_SECONDS
            while (countdown > 0 && isActive) {
                delay(1000)
                countdown--
                _uiState.update { it.copy(autoplayCountdown = countdown) }
                Timber.d("Autoplay countdown: $countdown")
            }

            // Countdown finished, play next video
            if (countdown == 0 && isActive) {
                val nextVideo = _uiState.value.nextMediaItem
                if (nextVideo != null) {
                    Timber.d("Autoplay countdown finished, triggering playNext")
                    playNextVideo()
                }
            }
        }
    }

    /**
     * Cancel autoplay countdown
     * Called when user clicks "Cancel" button
     */
    fun cancelAutoplay() {
        Timber.d("Autoplay cancelled by user")
        autoplayCountdownJob?.cancel()
        _uiState.update {
            it.copy(
                autoplayCountdown = 0,
                nextMediaItem = null
            )
        }
    }

    /**
     * Play next video immediately
     * Called when user clicks "Play Now" button
     */
    fun playNow() {
        Timber.d("Play now clicked")
        autoplayCountdownJob?.cancel()
        playNextVideo()
    }

    /**
     * Trigger navigation to next video
     * This will be handled by the PlayerScreen to navigate
     */
    private fun playNextVideo() {
        val nextVideo = _uiState.value.nextMediaItem
        if (nextVideo != null) {
            // Update state to indicate we want to play next video
            // The PlayerScreen will observe this and handle navigation
            _uiState.update {
                it.copy(
                    autoplayCountdown = -1 // Special value to trigger navigation
                )
            }
        }
    }

    /**
     * Report current playback state to Jellyfin and stop playback
     * Should be called when user navigates away
     */
    fun onStop() {
        viewModelScope.launch {
            if (player.currentPosition > 0) {
                // Report final position
                stopPlaybackUseCase(videoId, player.currentPosition)
            }
            // Stop screen time tracking
            screenTimeManager.stopTracking()
        }
    }

    /**
     * Get the ExoPlayer instance for the VideoPlayer composable
     */
    fun getPlayer(): ExoPlayer = player

    /**
     * Clear error state
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Retry loading after error
     */
    fun retry() {
        loadVideo()
    }

    /**
     * Acknowledge that navigation to next video has been handled
     * Called by PlayerScreen after navigation
     */
    fun onNextVideoNavigated() {
        _uiState.update {
            it.copy(
                autoplayCountdown = 0,
                nextMediaItem = null
            )
        }
    }

    /**
     * Extend screen time by adding minutes
     * Called when parent unlocks and grants additional time
     */
    fun extendScreenTime(additionalMinutes: Int) {
        viewModelScope.launch {
            screenTimeManager.extendScreenTime(additionalMinutes)
            Timber.d("Screen time extended by $additionalMinutes minutes")

            // Update UI state to reflect new limit
            val config = screenTimeManager.getScreenTimeConfig()
            _uiState.update {
                it.copy(
                    screenTimeRemaining = if (config.isEnabled) config.remainingMinutes else null,
                    isTimeLimitReached = config.isLimitReached
                )
            }
        }
    }

    /**
     * Reset daily screen time counter
     * Called when parent unlocks and resets the limit
     */
    fun resetScreenTimeDaily() {
        viewModelScope.launch {
            screenTimeManager.resetDailyCounter()
            Timber.d("Daily screen time counter reset by parent")

            // Update UI state to reflect reset
            val config = screenTimeManager.getScreenTimeConfig()
            _uiState.update {
                it.copy(
                    screenTimeRemaining = if (config.isEnabled) config.remainingMinutes else null,
                    isTimeLimitReached = config.isLimitReached,
                    timeLimitDailyMinutes = config.dailyLimitMinutes,
                    timeLimitUsedMinutes = config.usedTodayMinutes
                )
            }
        }
    }

    /**
     * Verify parent PIN for unlocking time limit
     * Called when parent enters PIN to manage screen time
     */
    fun verifyParentPin(pin: String) {
        viewModelScope.launch {
            when (val result = verifyParentPinUseCase(pin)) {
                is com.kidplayer.app.domain.model.PinVerificationResult.Success -> {
                    Timber.d("PIN verification successful")
                    _uiState.update {
                        it.copy(pinVerificationError = null)
                    }
                }
                is com.kidplayer.app.domain.model.PinVerificationResult.Failure -> {
                    Timber.d("PIN verification failed")
                    _uiState.update {
                        it.copy(pinVerificationError = "Incorrect PIN. Please try again.")
                    }
                }
                is com.kidplayer.app.domain.model.PinVerificationResult.NotSet -> {
                    Timber.d("PIN not set")
                    _uiState.update {
                        it.copy(pinVerificationError = "No PIN set. Please set a PIN in settings.")
                    }
                }
            }
        }
    }

    /**
     * Clear PIN verification error
     */
    fun clearPinError() {
        _uiState.update {
            it.copy(pinVerificationError = null)
        }
    }

    /**
     * Log all available tracks for debugging purposes
     * Helps identify audio track availability issues
     */
    private fun logAvailableTracks() {
        try {
            val tracks = player.currentTracks
            Timber.d("=== Available Tracks ===")

            if (tracks.isEmpty) {
                Timber.w("No tracks available in the media")
                return
            }

            tracks.groups.forEachIndexed { groupIndex, group ->
                val trackType = when (group.type) {
                    androidx.media3.common.C.TRACK_TYPE_VIDEO -> "VIDEO"
                    androidx.media3.common.C.TRACK_TYPE_AUDIO -> "AUDIO"
                    androidx.media3.common.C.TRACK_TYPE_TEXT -> "TEXT"
                    else -> "UNKNOWN"
                }

                Timber.d("Track Group $groupIndex: Type=$trackType, Tracks=${group.length}")

                for (i in 0 until group.length) {
                    val format = group.getTrackFormat(i)
                    val isSelected = group.isTrackSelected(i)

                    when (group.type) {
                        androidx.media3.common.C.TRACK_TYPE_AUDIO -> {
                            Timber.d("  Audio Track $i: " +
                                    "Codec=${format.sampleMimeType}, " +
                                    "Channels=${format.channelCount}, " +
                                    "SampleRate=${format.sampleRate}, " +
                                    "Bitrate=${format.bitrate}, " +
                                    "Language=${format.language ?: "unknown"}, " +
                                    "Selected=$isSelected")
                        }
                        androidx.media3.common.C.TRACK_TYPE_VIDEO -> {
                            Timber.d("  Video Track $i: " +
                                    "Codec=${format.sampleMimeType}, " +
                                    "Resolution=${format.width}x${format.height}, " +
                                    "FPS=${format.frameRate}, " +
                                    "Selected=$isSelected")
                        }
                    }
                }
            }
            Timber.d("========================")
        } catch (e: Exception) {
            Timber.e(e, "Error logging available tracks")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("PlayerViewModel cleared")

        // Stop tracking jobs
        stopProgressTracking()
        positionUpdateJob?.cancel()
        screenTimeCheckJob?.cancel()
        autoplayCountdownJob?.cancel()

        // Stop screen time tracking
        viewModelScope.launch {
            screenTimeManager.stopTracking()
        }

        // Remove listener and release player
        player.removeListener(playerListener)
        player.release()
    }

    companion object {
        private const val AUTOPLAY_COUNTDOWN_SECONDS = 5
        private const val MAX_RECOMMENDATIONS = 8
    }
}

