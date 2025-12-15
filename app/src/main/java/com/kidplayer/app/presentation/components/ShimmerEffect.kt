package com.kidplayer.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect modifier that can be applied to any composable
 * Creates an animated gradient that moves across the component
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 1000f, translateAnim - 1000f),
            end = Offset(translateAnim, translateAnim)
        )
    )
}

/**
 * Simple pulsing shimmer effect using alpha animation
 * More performant than gradient shimmer for many items
 */
fun Modifier.pulseShimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulse_shimmer")

    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    background(Color.Gray.copy(alpha = alpha))
}

/**
 * Video card shimmer placeholder
 * Displays while video thumbnails are loading
 */
@Composable
fun VideoCardShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(16f / 9f)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmerEffect()
        )
    }
}

/**
 * Title text shimmer placeholder
 */
@Composable
fun TitleShimmer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
    )
}

/**
 * Small text shimmer placeholder
 */
@Composable
fun TextShimmer(
    modifier: Modifier = Modifier,
    widthFraction: Float = 0.5f
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect()
    )
}

/**
 * Circular shimmer placeholder (for avatars, icons, etc.)
 */
@Composable
fun CircleShimmer(
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .shimmerEffect()
    )
}
