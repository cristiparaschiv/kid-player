package com.kidplayer.app.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription

/**
 * BackgroundWrapper - A reusable composable for adding subtle background images to screens
 *
 * Designed for Kid Player app (ages 4-10) to add visual interest without distracting
 * from the main content. The background image is rendered with low opacity to maintain
 * readability and focus on interactive elements.
 *
 * Features:
 * - Configurable opacity (default 0.12 for subtle effect)
 * - ContentScale.Crop to cover entire screen
 * - Fallback to solid background color if no image provided
 * - Accessible - decorative images hidden from screen readers
 *
 * @param backgroundImageRes Drawable resource ID for the background image (optional)
 * @param backgroundAlpha Opacity of the background image (0.0 to 1.0, default 0.12)
 * @param fallbackColor Solid color to use when no background image is provided
 * @param content The main screen content to be rendered on top of the background
 *
 * Usage:
 * ```
 * BackgroundWrapper(
 *     backgroundImageRes = R.drawable.cartoon_background
 * ) {
 *     // Your screen content here
 *     Column { ... }
 * }
 * ```
 */
@Composable
fun BackgroundWrapper(
    @DrawableRes backgroundImageRes: Int? = null,
    backgroundAlpha: Float = 0.12f,
    fallbackColor: Color? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fallbackColor ?: MaterialTheme.colorScheme.background)
    ) {
        // Background layer - image or solid color
        if (backgroundImageRes != null) {
            Image(
                painter = painterResource(id = backgroundImageRes),
                contentDescription = null, // Decorative image, hidden from accessibility
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(backgroundAlpha.coerceIn(0f, 1f))
                    .semantics {
                        // Explicitly mark as decorative for screen readers
                        contentDescription = ""
                    },
                contentScale = ContentScale.Crop
            )
        }

        // Content layer - your screen UI
        content()
    }
}

/**
 * Preset background wrapper with standard kid-friendly opacity
 * Perfect for most screens in the app
 */
@Composable
fun KidFriendlyBackgroundWrapper(
    @DrawableRes backgroundImageRes: Int? = null,
    content: @Composable () -> Unit
) {
    BackgroundWrapper(
        backgroundImageRes = backgroundImageRes,
        backgroundAlpha = 0.52f, // Subtle enough to not distract young users
        fallbackColor = MaterialTheme.colorScheme.background,
        content = content
    )
}

/**
 * Extra subtle background wrapper for screens with lots of content
 * Use this for busy screens like HomeScreen or BrowseScreen
 */
@Composable
fun ExtraSubtleBackgroundWrapper(
    @DrawableRes backgroundImageRes: Int? = null,
    content: @Composable () -> Unit
) {
    BackgroundWrapper(
        backgroundImageRes = backgroundImageRes,
        backgroundAlpha = 0.52f, // Even more subtle for content-heavy screens
        fallbackColor = MaterialTheme.colorScheme.background,
        content = content
    )
}
