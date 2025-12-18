package com.kidplayer.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Standardized dimension tokens for Kid Player app
 * Following Material Design 3 and WCAG accessibility guidelines
 * Optimized for children ages 4-10
 */
object Dimensions {

    // TOUCH TARGETS - Following WCAG AAA standard (minimum 44dp, recommended 48-56dp for kids)
    val touchTargetMin = 48.dp // Absolute minimum (WCAG AAA)
    val touchTargetRecommended = 56.dp // Recommended for children
    val touchTargetLarge = 64.dp // Extra large for primary actions
    val touchTargetExtraLarge = 80.dp // Navigation bar height for kids

    // INTERACTIVE COMPONENT SIZES
    val iconButtonSize = 56.dp // IconButton minimum size
    val iconButtonLarge = 64.dp // Large icon button for primary actions
    val navigationBarHeight = 80.dp // Bottom navigation bar
    val searchFieldHeight = 64.dp // Search text field height

    // ICON SIZES
    val iconSmall = 20.dp
    val iconMedium = 24.dp
    val iconLarge = 28.dp
    val iconExtraLarge = 32.dp // Navigation bar icons
    val iconHuge = 40.dp // Primary action icons

    // SPACING - Material Design 3 spacing scale
    val spacingXxs = 2.dp
    val spacingXs = 4.dp
    val spacingS = 8.dp
    val spacingM = 12.dp
    val spacingL = 16.dp
    val spacingXl = 20.dp
    val spacingXxl = 24.dp
    val spacingXxxl = 32.dp
    val spacingHuge = 40.dp

    // GRID SPACING
    val gridSpacingPhone = 16.dp
    val gridSpacingTablet = 24.dp

    // PROGRESS INDICATORS
    val progressBarHeightSmall = 4.dp
    val progressBarHeightMedium = 6.dp
    val progressBarHeightLarge = 8.dp

    // CARD PROPERTIES
    val cardElevation = 4.dp
    val cardCornerRadius = 20.dp // Increased for more playful look
    val cardCornerRadiusSmall = 12.dp // For compact cards
    val chipCornerRadius = 12.dp // Increased for rounder chips
    val buttonCornerRadius = 16.dp // Rounded buttons for kid-friendly UI
    val dialogCornerRadius = 24.dp // Extra rounded dialogs

    // PADDING
    val paddingXs = 4.dp
    val paddingS = 8.dp
    val paddingM = 12.dp
    val paddingL = 16.dp
    val paddingXl = 24.dp
    val paddingXxl = 32.dp

    // SCREEN BREAKPOINTS
    val tabletMinWidth = 600.dp
    val desktopMinWidth = 840.dp
}
