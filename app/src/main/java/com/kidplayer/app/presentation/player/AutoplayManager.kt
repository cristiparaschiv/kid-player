package com.kidplayer.app.presentation.player

import com.kidplayer.app.domain.model.AutoplayConfig
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.domain.model.Result
import com.kidplayer.app.domain.usecase.GetNextVideoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for autoplay functionality
 * Handles countdown and next video selection
 */
@Singleton
class AutoplayManager @Inject constructor(
    private val getNextVideoUseCase: GetNextVideoUseCase
) {
    private val _autoplayState = MutableStateFlow<AutoplayState>(AutoplayState.Idle)
    val autoplayState: StateFlow<AutoplayState> = _autoplayState.asStateFlow()

    /**
     * Start autoplay countdown
     */
    suspend fun startAutoplayCountdown(
        currentVideoId: String,
        config: AutoplayConfig
    ) {
        if (!config.enabled) {
            Timber.d("Autoplay is disabled")
            _autoplayState.value = AutoplayState.Idle
            return
        }

        Timber.d("Starting autoplay countdown")

        // Get next video
        when (val result = getNextVideoUseCase(currentVideoId)) {
            is Result.Success -> {
                val nextVideo = result.data
                if (nextVideo != null) {
                    _autoplayState.value = AutoplayState.Countdown(
                        nextVideo = nextVideo,
                        secondsRemaining = config.countdownSeconds
                    )
                } else {
                    Timber.d("No next video found")
                    _autoplayState.value = AutoplayState.Idle
                }
            }
            is Result.Error -> {
                Timber.e("Error getting next video: ${result.message}")
                _autoplayState.value = AutoplayState.Idle
            }
            is Result.Loading -> {
                // Should not happen
            }
        }
    }

    /**
     * Update countdown timer
     */
    fun updateCountdown(secondsRemaining: Int) {
        val current = _autoplayState.value
        if (current is AutoplayState.Countdown) {
            _autoplayState.value = current.copy(secondsRemaining = secondsRemaining)
        }
    }

    /**
     * Cancel autoplay
     */
    fun cancelAutoplay() {
        Timber.d("Autoplay cancelled")
        _autoplayState.value = AutoplayState.Idle
    }

    /**
     * Confirm autoplay (skip countdown)
     */
    fun confirmAutoplay() {
        val current = _autoplayState.value
        if (current is AutoplayState.Countdown) {
            Timber.d("Autoplay confirmed, playing: ${current.nextVideo.title}")
            _autoplayState.value = AutoplayState.PlayNext(current.nextVideo.id)
        }
    }

    /**
     * Reset to idle state
     */
    fun reset() {
        _autoplayState.value = AutoplayState.Idle
    }
}

/**
 * Autoplay state
 */
sealed class AutoplayState {
    object Idle : AutoplayState()

    data class Countdown(
        val nextVideo: MediaItem,
        val secondsRemaining: Int
    ) : AutoplayState()

    data class PlayNext(
        val videoId: String
    ) : AutoplayState()
}
