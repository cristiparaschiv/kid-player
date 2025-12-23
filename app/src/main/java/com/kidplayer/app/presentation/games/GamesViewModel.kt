package com.kidplayer.app.presentation.games

import androidx.lifecycle.ViewModel
import com.kidplayer.app.presentation.games.common.GameIconType
import com.kidplayer.app.presentation.games.common.GameInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for Games selection screen
 * Manages available games list and navigation
 */
@HiltViewModel
class GamesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        val games = listOf(
            GameInfo(
                id = "memory",
                name = "Memory Match",
                description = "Find matching pairs",
                route = "games/memory",
                backgroundColor = 0xFF6C63FF, // Purple
                iconType = GameIconType.MEMORY,
                isAvailable = true
            ),
            GameInfo(
                id = "tictactoe",
                name = "Tic-Tac-Toe",
                description = "Beat the computer",
                route = "games/tictactoe",
                backgroundColor = 0xFF00BFA5, // Teal
                iconType = GameIconType.TICTACTOE,
                isAvailable = true
            ),
            GameInfo(
                id = "puzzle",
                name = "Shape Puzzle",
                description = "Match the shapes",
                route = "games/puzzle",
                backgroundColor = 0xFFFF6B6B, // Coral
                iconType = GameIconType.PUZZLE,
                isAvailable = true
            ),
            GameInfo(
                id = "sliding",
                name = "Sliding Puzzle",
                description = "Slide tiles to solve",
                route = "games/sliding",
                backgroundColor = 0xFF42A5F5, // Blue
                iconType = GameIconType.SLIDING,
                isAvailable = true
            ),
            GameInfo(
                id = "gridpuzzle",
                name = "Picture Puzzle",
                description = "Swap pieces to complete",
                route = "games/gridpuzzle",
                backgroundColor = 0xFFAB47BC, // Purple
                iconType = GameIconType.GRIDPUZZLE,
                isAvailable = true
            ),
            GameInfo(
                id = "match3",
                name = "Match 3",
                description = "Swap & match tiles",
                route = "games/match3",
                backgroundColor = 0xFFFFB347, // Orange
                iconType = GameIconType.MATCH3,
                isAvailable = false // Coming soon
            ),
            GameInfo(
                id = "coloring",
                name = "Coloring Book",
                description = "Color fun pictures",
                route = "games/coloring",
                backgroundColor = 0xFF77DD77, // Pastel Green
                iconType = GameIconType.COLORING,
                isAvailable = false // Coming soon
            )
        )

        _uiState.value = GamesUiState(
            games = games,
            isLoading = false
        )
    }
}

/**
 * UI State for Games screen
 */
data class GamesUiState(
    val games: List<GameInfo> = emptyList(),
    val isLoading: Boolean = true
)
