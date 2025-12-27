package com.kidplayer.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidplayer.app.R
import kotlinx.coroutines.delay

/**
 * Animated star display showing the player's total stars
 * Bounces when stars are earned
 */
@Composable
fun StarDisplay(
    totalStars: Int,
    modifier: Modifier = Modifier
) {
    var previousStars by remember { mutableStateOf(totalStars) }
    var isAnimating by remember { mutableStateOf(false) }

    // Trigger animation when stars increase
    LaunchedEffect(totalStars) {
        if (totalStars > previousStars) {
            isAnimating = true
            delay(600)
            isAnimating = false
        }
        previousStars = totalStars
    }

    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "starScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFFD700), // Gold
                        Color(0xFFFFA500)  // Orange
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_star_filled),
                contentDescription = "Stars",
                modifier = Modifier.size(24.dp)
            )

            AnimatedContent(
                targetState = totalStars,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) +
                            scaleIn(initialScale = 0.8f, animationSpec = tween(300)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(150)) +
                                    scaleOut(targetScale = 1.2f, animationSpec = tween(150))
                        )
                },
                label = "starCount"
            ) { count ->
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Star celebration overlay shown when stars are earned
 */
@Composable
fun StarCelebration(
    starsEarned: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCelebration by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500)
        showCelebration = false
        delay(300)
        onDismiss()
    }

    val scale by animateFloatAsState(
        targetValue = if (showCelebration) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "celebrationScale"
    )

    if (scale > 0.01f) {
        Box(
            modifier = modifier
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.95f),
                            Color(0xFFFFA500).copy(alpha = 0.9f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(starsEarned.coerceAtMost(5)) { index ->
                    var starVisible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(index * 200L)
                        starVisible = true
                    }

                    val starScale by animateFloatAsState(
                        targetValue = if (starVisible) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "singleStarScale$index"
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_star_filled),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(starScale)
                    )
                }

                Text(
                    text = "+$starsEarned",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
