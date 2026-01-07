package com.kidplayer.app.presentation.player

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.UnstableApi
import com.kidplayer.app.presentation.player.components.AutoplayOverlay
import com.kidplayer.app.presentation.player.components.BufferingIndicator
import com.kidplayer.app.presentation.player.components.CircularTimeIndicator
import com.kidplayer.app.presentation.player.components.PlayerControls
import com.kidplayer.app.presentation.player.components.PlayerError
import com.kidplayer.app.presentation.player.components.TimeLimitReachedOverlay
import com.kidplayer.app.presentation.player.components.VideoPlayer
import com.kidplayer.app.presentation.util.rememberSystemUiController

/**
 * Player Screen - Full-screen video playback with ExoPlayer
 * Features kid-friendly controls, buffering states, and error handling
 */
@UnstableApi
@Composable
fun PlayerScreen(
    onNavigateBack: () -> Unit,
    onPlayNext: (String) -> Unit = {},
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    // Enable full-screen immersive mode (hide system bars)
    DisposableEffect(Unit) {
        systemUiController?.setSystemBarsVisible(visible = false, isSticky = true)
        onDispose {
            // Restore system bars when leaving player screen
            systemUiController?.setSystemBarsVisible(visible = true)
        }
    }

    // Keep screen awake while video is playing
    DisposableEffect(uiState.isPlaying) {
        val window = (context as? Activity)?.window
        if (uiState.isPlaying) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Handle autoplay navigation
    LaunchedEffect(uiState.autoplayCountdown) {
        if (uiState.autoplayCountdown == -1 && uiState.nextMediaItem != null) {
            // Navigate to next video
            val nextVideoId = uiState.nextMediaItem!!.id
            viewModel.onNextVideoNavigated()
            onPlayNext(nextVideoId)
        }
    }

    // Report stop when navigating away
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onStop()
        }
    }

    // Pause playback when screen turns off or app goes to background
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    // Screen turned off or app went to background - pause playback
                    viewModel.pause()
                }
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            // Error state
            uiState.hasError() -> {
                PlayerError(
                    message = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.retry() },
                    onBack = onNavigateBack
                )
            }

            // Loading initial video
            uiState.isLoading -> {
                BufferingIndicator(
                    isBuffering = true,
                    message = "Loading video..."
                )
            }

            // Player ready - show video and controls
            uiState.playerReady -> {
                // Video player view
                VideoPlayer(
                    player = viewModel.getPlayer(),
                    modifier = Modifier.fillMaxSize()
                )

                // Buffering overlay (shown on top of video)
                if (uiState.isBuffering) {
                    BufferingIndicator(
                        isBuffering = true,
                        message = "Buffering..."
                    )
                }

                // Player controls overlay
                PlayerControls(
                    isPlaying = uiState.isPlaying,
                    currentPosition = uiState.getFormattedPosition(),
                    duration = uiState.getFormattedDuration(),
                    progress = uiState.getProgress(),
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onSeekBackClick = { viewModel.seekBackward() },
                    onSeekForwardClick = { viewModel.seekForward() },
                    onSeek = { progress ->
                        val newPosition = (progress * uiState.duration).toLong()
                        viewModel.seekTo(newPosition)
                    },
                    onBackClick = onNavigateBack,
                    currentVideoId = uiState.mediaItem?.id ?: "",
                    recommendedVideos = uiState.recommendedVideos,
                    onRecommendationSelect = { videoId ->
                        onPlayNext(videoId)
                    }
                )

                // Circular time remaining indicator (top right)
                if (uiState.screenTimeRemaining != null && uiState.screenTimeRemaining!! > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        CircularTimeIndicator(
                            isVisible = true,
                            remainingMinutes = uiState.screenTimeRemaining!!,
                            totalMinutes = uiState.timeLimitDailyMinutes ?: 60
                        )
                    }
                }

                // Autoplay overlay (shown when countdown is active)
                if (uiState.autoplayCountdown > 0 && uiState.nextMediaItem != null) {
                    AutoplayOverlay(
                        nextVideo = uiState.nextMediaItem!!,
                        secondsRemaining = uiState.autoplayCountdown,
                        onCancel = { viewModel.cancelAutoplay() },
                        onPlayNow = { viewModel.playNow() }
                    )
                }

                // Time limit reached overlay (blocks all playback)
                // This overlay takes precedence over all other UI elements
                TimeLimitReachedOverlay(
                    isVisible = uiState.isTimeLimitReached,
                    limitMinutes = uiState.timeLimitDailyMinutes,
                    currentUsedMinutes = uiState.timeLimitUsedMinutes,
                    onPinEntered = { pin ->
                        viewModel.verifyParentPin(pin)
                    },
                    onAddMinutes = { minutes ->
                        viewModel.extendScreenTime(minutes)
                    },
                    onResetDaily = {
                        viewModel.resetScreenTimeDaily()
                    },
                    pinError = uiState.pinVerificationError,
                    onDismissPinError = {
                        viewModel.clearPinError()
                    }
                )
            }
        }
    }
}


