package com.kidplayer.app.presentation.games.common

import androidx.annotation.StringRes

/**
 * Common game state representation for all mini-games
 * Provides a unified state model for game lifecycle
 */
sealed class GameState {
    /** Game is loading/initializing */
    object Loading : GameState()

    /** Game is ready to start, waiting for player */
    object Ready : GameState()

    /** Game is actively being played */
    data class Playing(
        val score: Int = 0,
        val moves: Int = 0,
        val timeElapsedMs: Long = 0
    ) : GameState()

    /** Game is paused */
    object Paused : GameState()

    /** Game has been completed */
    data class Completed(
        val won: Boolean,
        val score: Int,
        val stars: Int, // 1-3 stars based on performance
        val moves: Int = 0,
        val timeElapsedMs: Long = 0
    ) : GameState()
}

/**
 * Game difficulty levels appropriate for ages 5-7
 */
enum class Difficulty {
    EASY,   // Age 5 - Simplest configuration
    MEDIUM, // Age 6 - Moderate challenge
    HARD    // Age 7 - More complex
}

/**
 * Configuration options for games
 */
data class GameConfig(
    val difficulty: Difficulty = Difficulty.EASY,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val showTimer: Boolean = false
)

/**
 * Information about a game for the selection screen
 */
data class GameInfo(
    val id: String,
    @StringRes val nameResId: Int,
    @StringRes val descriptionResId: Int,
    val route: String,
    val backgroundColor: Long, // Color as Long for Canvas
    val iconType: GameIconType,
    val isAvailable: Boolean = true
)

/**
 * Types of game icons that can be drawn with Canvas
 */
enum class GameIconType {
    MEMORY,      // Grid of cards
    TICTACTOE,   // X and O
    PUZZLE,      // Puzzle piece shapes
    MATCH3,      // Colorful tiles
    COLORING,    // Paintbrush/palette
    SLIDING,     // Sliding puzzle grid
    GRIDPUZZLE,  // Picture puzzle grid
    PATTERN,     // Pattern shapes
    COLORMIX,    // Color blobs
    LETTERMATCH, // ABC letters
    MAZE,        // Maze path
    DOTS,        // Connect dots
    ADDITION,    // Plus sign with numbers
    SUBTRACTION, // Minus sign with animals
    NUMBERBONDS, // Number bond diagram
    COMPARE,     // Greater/less than
    ODDONEOUT,   // Magnifying glass
    SUDOKU,      // Grid puzzle
    BALLSORT,    // Colored balls in tubes
    HANGMAN,     // Hangman figure
    CROSSWORD,   // Crossword grid
    COUNTING,    // Numbers with objects
    SHAPES,      // Geometric shapes
    SPELLING,    // Letter tiles
    WORDSEARCH,  // Letter grid
    SPOTDIFF     // Two pictures
}
