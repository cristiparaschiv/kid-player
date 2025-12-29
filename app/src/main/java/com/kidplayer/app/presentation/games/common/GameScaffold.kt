package com.kidplayer.app.presentation.games.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // Still needed for overlays
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kidplayer.app.R
import com.kidplayer.app.presentation.components.StarCelebration
import com.kidplayer.app.presentation.components.StarDisplay
import com.kidplayer.app.presentation.components.rememberHapticFeedback
import com.kidplayer.app.presentation.util.bouncyClickable
import com.kidplayer.app.ui.theme.Dimensions

/**
 * Common scaffold wrapper for all games
 * Provides consistent header, pause/restart controls, and completion dialog
 */
@Composable
fun GameScaffold(
    gameName: String,
    gameState: GameState,
    onBackClick: () -> Unit,
    onPauseClick: () -> Unit,
    onRestartClick: () -> Unit,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier,
    showScore: Boolean = true,
    totalStars: Int = 0,
    starsEarned: Int = 0,
    onStarCelebrationComplete: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = rememberHapticFeedback()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Cartoon background image
        Image(
            painter = painterResource(id = R.drawable.cartoon_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Game content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp) // Leave room for header
        ) {
            content()
        }

        // Header bar
        GameHeader(
            gameName = gameName,
            score = when (gameState) {
                is GameState.Playing -> gameState.score
                is GameState.Completed -> gameState.score
                else -> 0
            },
            showScore = showScore && gameState is GameState.Playing,
            totalStars = totalStars,
            onBackClick = {
                haptic.performLight()
                onBackClick()
            },
            onPauseClick = {
                haptic.performLight()
                onPauseClick()
            },
            onRestartClick = {
                haptic.performMedium()
                onRestartClick()
            }
        )

        // Star celebration overlay
        AnimatedVisibility(
            visible = starsEarned > 0,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            StarCelebration(
                starsEarned = starsEarned,
                onDismiss = onStarCelebrationComplete
            )
        }

        // Pause overlay
        AnimatedVisibility(
            visible = gameState == GameState.Paused,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            PauseOverlay(
                onResumeClick = {
                    haptic.performLight()
                    onResumeClick()
                },
                onRestartClick = {
                    haptic.performMedium()
                    onRestartClick()
                },
                onExitClick = {
                    haptic.performLight()
                    onBackClick()
                }
            )
        }

        // Completion overlay
        AnimatedVisibility(
            visible = gameState is GameState.Completed,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            if (gameState is GameState.Completed) {
                CompletionOverlay(
                    won = gameState.won,
                    score = gameState.score,
                    stars = gameState.stars,
                    onPlayAgainClick = {
                        haptic.performMedium()
                        onRestartClick()
                    },
                    onExitClick = {
                        haptic.performLight()
                        onBackClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun GameHeader(
    gameName: String,
    score: Int,
    showScore: Boolean,
    totalStars: Int,
    onBackClick: () -> Unit,
    onPauseClick: () -> Unit,
    onRestartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
    ) {
        // Star display row at the very top
        if (totalStars > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                StarDisplay(totalStars = totalStars)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (totalStars > 0) 8.dp else 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.game_back_to_games_desc),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Game name and score
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = gameName.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (showScore) {
                    Text(
                        text = stringResource(R.string.game_score_label, score),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRestartClick,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.game_restart_desc),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = stringResource(R.string.game_pause_desc),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PauseOverlay(
    onResumeClick: () -> Unit,
    onRestartClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(Dimensions.dialogCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_paused),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onResumeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimensions.buttonCornerRadius)
                ) {
                    Text(
                        text = stringResource(R.string.game_resume),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onRestartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimensions.buttonCornerRadius)
                ) {
                    Text(
                        text = stringResource(R.string.game_restart),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                TextButton(
                    onClick = onExitClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.game_exit),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletionOverlay(
    won: Boolean,
    score: Int,
    stars: Int,
    onPlayAgainClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(Dimensions.dialogCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = stringResource(if (won) R.string.game_great_job else R.string.game_good_try),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (won) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )

                // Stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        StarIcon(
                            filled = index < stars,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Score
                Text(
                    text = stringResource(R.string.game_score_label, score),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onPlayAgainClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimensions.buttonCornerRadius)
                ) {
                    Text(
                        text = stringResource(R.string.game_play_again),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onExitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimensions.buttonCornerRadius)
                ) {
                    Text(
                        text = stringResource(R.string.game_back_to_games),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun StarIcon(
    filled: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (filled) {
        Color(0xFFFFD700) // Gold
    } else {
        Color(0xFFE0E0E0) // Gray
    }

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val outerRadius = size.minDimension / 2
        val innerRadius = outerRadius * 0.4f

        val path = androidx.compose.ui.graphics.Path()
        val angleStep = Math.PI / 5

        for (i in 0 until 10) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = i * angleStep - Math.PI / 2
            val x = centerX + (radius * kotlin.math.cos(angle)).toFloat()
            val y = centerY + (radius * kotlin.math.sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        drawPath(path, color)
    }
}
