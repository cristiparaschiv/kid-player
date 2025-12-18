package com.kidplayer.app.presentation.player.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidplayer.app.domain.model.MediaItem
import com.kidplayer.app.ui.theme.Dimensions
import kotlinx.coroutines.delay

/**
 * Kid-friendly video player controls
 * Features large touch targets, simple interface, and auto-hide behavior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    currentPosition: String,
    duration: String,
    progress: Float,
    onPlayPauseClick: () -> Unit,
    onSeekBackClick: () -> Unit,
    onSeekForwardClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onBackClick: () -> Unit,
    currentVideoId: String = "",
    recommendedVideos: List<MediaItem> = emptyList(),
    onRecommendationSelect: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    autoHide: Boolean = true
) {
    var isVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Auto-hide controls after 5 seconds of inactivity (extended for kids)
    LaunchedEffect(lastInteractionTime, isPlaying, autoHide) {
        if (autoHide && isPlaying) {
            delay(5000)
            if (System.currentTimeMillis() - lastInteractionTime >= 5000) {
                isVisible = false
            }
        }
    }

    // Show controls on tap
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isVisible = !isVisible
                lastInteractionTime = System.currentTimeMillis()
            }
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Semi-transparent overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )

                // Back button (top-left)
                IconButton(
                    onClick = {
                        onBackClick()
                        lastInteractionTime = System.currentTimeMillis()
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Center controls with bouncy animations
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seek backward button with bouncy animation
                    BouncyControlButton(
                        onClick = {
                            onSeekBackClick()
                            lastInteractionTime = System.currentTimeMillis()
                        },
                        size = 80.dp,
                        backgroundColor = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Seek back 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Play/Pause button with bouncy animation (large and prominent)
                    BouncyControlButton(
                        onClick = {
                            onPlayPauseClick()
                            lastInteractionTime = System.currentTimeMillis()
                        },
                        size = 120.dp,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        scaleOnPress = 0.88f
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    // Seek forward button with bouncy animation
                    BouncyControlButton(
                        onClick = {
                            onSeekForwardClick()
                            lastInteractionTime = System.currentTimeMillis()
                        },
                        size = 80.dp,
                        backgroundColor = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Seek forward 10 seconds",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Bottom controls (recommendations + progress bar and time)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(
                                topStart = Dimensions.dialogCornerRadius,
                                topEnd = Dimensions.dialogCornerRadius
                            )
                        )
                ) {
                    // Video recommendation row (shows if recommendations available)
                    if (recommendedVideos.isNotEmpty()) {
                        VideoRecommendationRow(
                            recommendations = recommendedVideos,
                            currentVideoId = currentVideoId,
                            onVideoSelect = { videoId ->
                                onRecommendationSelect(videoId)
                                lastInteractionTime = System.currentTimeMillis()
                            }
                        )

                        // Divider between recommendations and controls
                        Divider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            thickness = 1.dp,
                            color = Color.White.copy(alpha = 0.2f)
                        )
                    }

                    // Progress bar and time controls
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        // Progress bar
                        Slider(
                            value = progress,
                            onValueChange = { newValue ->
                                onSeek(newValue)
                                lastInteractionTime = System.currentTimeMillis()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp), // Large touch target for kids
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentPosition,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = duration,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Bouncy control button with spring animation on press
 * Provides playful, tactile feedback for kid-friendly controls
 */
@Composable
private fun BouncyControlButton(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    backgroundColor: Color,
    scaleOnPress: Float = 0.92f,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleOnPress else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bouncy_control_scale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(backgroundColor)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        val up = waitForUpOrCancellation()
                        isPressed = false
                        if (up != null) {
                            onClick()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
