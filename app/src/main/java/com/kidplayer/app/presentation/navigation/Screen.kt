package com.kidplayer.app.presentation.navigation

/**
 * Sealed class representing all navigation routes in the app
 * Phase 6: Added Home, Search, and Favorites screens
 * Persistent Login: Added Splash screen
 */
sealed class Screen(val route: String) {
    /**
     * Splash screen with auto-login
     * Validates saved session and navigates to Home or Setup
     */
    object Splash : Screen("splash")

    /**
     * Initial setup/onboarding screen for connecting to Jellyfin server
     */
    object Setup : Screen("setup")

    /**
     * Home screen with personalized content (Continue Watching, Recently Added, etc.)
     * Phase 6: New main screen
     */
    object Home : Screen("home")

    /**
     * Browse screen showing all videos in library grid
     */
    object Browse : Screen("browse")

    /**
     * Downloaded screen showing downloaded videos
     * Phase 6: New screen for offline content
     */
    object Downloaded : Screen("downloaded")

    /**
     * Search screen for finding videos
     * Phase 6: New screen
     */
    object Search : Screen("search")

    /**
     * Favorites screen showing favorited videos
     * Phase 6: New screen
     */
    object Favorites : Screen("favorites")

    /**
     * Games hub screen showing available mini-games
     */
    object Games : Screen("games")

    /**
     * Individual game screens
     * Route includes gameId parameter: "games/{gameId}"
     */
    object Game : Screen("games/{gameId}") {
        fun createRoute(gameId: String) = "games/$gameId"
    }

    /**
     * Video player screen
     * Route includes videoId parameter: "player/{videoId}"
     */
    object Player : Screen("player/{videoId}") {
        fun createRoute(videoId: String) = "player/$videoId"
    }

    /**
     * Parent settings screen (PIN-protected)
     */
    object Settings : Screen("settings")

    /**
     * Time limit reached screen
     * Route includes limitMinutes parameter: "time_limit/{limitMinutes}"
     */
    object TimeLimitReached : Screen("time_limit/{limitMinutes}") {
        fun createRoute(limitMinutes: Int) = "time_limit/$limitMinutes"
    }
}
