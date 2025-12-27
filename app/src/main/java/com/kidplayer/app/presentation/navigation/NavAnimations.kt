package com.kidplayer.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry

/**
 * Playful navigation animations with bounce effects for kid-friendly transitions
 */
object NavAnimations {

    private const val ANIMATION_DURATION = 400
    private const val FADE_DURATION = 200

    /**
     * Enter transition: Slide in from right with bounce
     */
    val enterSlideLeft: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(FADE_DURATION))
    }

    /**
     * Exit transition: Slide out to left
     */
    val exitSlideLeft: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeOut(animationSpec = tween(FADE_DURATION))
    }

    /**
     * Pop enter transition: Slide in from left with bounce (for back navigation)
     */
    val popEnterSlideRight: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(FADE_DURATION))
    }

    /**
     * Pop exit transition: Slide out to right (for back navigation)
     */
    val popExitSlideRight: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeOut(animationSpec = tween(FADE_DURATION))
    }

    /**
     * Vertical slide up (for modals/overlays)
     */
    val enterSlideUp: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        androidx.compose.animation.slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(animationSpec = tween(FADE_DURATION))
    }

    /**
     * Vertical slide down (for dismissing modals)
     */
    val exitSlideDown: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        androidx.compose.animation.slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(ANIMATION_DURATION)
        ) + fadeOut(animationSpec = tween(FADE_DURATION))
    }
}
