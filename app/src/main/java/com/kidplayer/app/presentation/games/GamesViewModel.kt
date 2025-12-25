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
                isAvailable = true
            ),
            GameInfo(
                id = "coloring",
                name = "Coloring Book",
                description = "Color fun pictures",
                route = "games/coloring",
                backgroundColor = 0xFF77DD77, // Pastel Green
                iconType = GameIconType.COLORING,
                isAvailable = true
            ),
            GameInfo(
                id = "pattern",
                name = "Pattern",
                description = "Complete the pattern",
                route = "games/pattern",
                backgroundColor = 0xFF9575CD, // Light Purple
                iconType = GameIconType.PATTERN,
                isAvailable = true
            ),
            GameInfo(
                id = "colormix",
                name = "Color Mix",
                description = "Mix colors together",
                route = "games/colormix",
                backgroundColor = 0xFFEC407A, // Pink
                iconType = GameIconType.COLORMIX,
                isAvailable = true
            ),
            GameInfo(
                id = "lettermatch",
                name = "Letter Match",
                description = "Match letters & pictures",
                route = "games/lettermatch",
                backgroundColor = 0xFF26A69A, // Teal
                iconType = GameIconType.LETTERMATCH,
                isAvailable = true
            ),
            GameInfo(
                id = "maze",
                name = "Maze Runner",
                description = "Find your way out",
                route = "games/maze",
                backgroundColor = 0xFF5C6BC0, // Indigo
                iconType = GameIconType.MAZE,
                isAvailable = true
            ),
            GameInfo(
                id = "dots",
                name = "Connect Dots",
                description = "Connect the dots",
                route = "games/dots",
                backgroundColor = 0xFFFFCA28, // Amber
                iconType = GameIconType.DOTS,
                isAvailable = true
            ),
            GameInfo(
                id = "addition",
                name = "Addition Adventure",
                description = "Learn to add numbers",
                route = "games/addition",
                backgroundColor = 0xFF4CAF50, // Green
                iconType = GameIconType.ADDITION,
                isAvailable = true
            ),
            GameInfo(
                id = "subtraction",
                name = "Subtraction Safari",
                description = "Learn to subtract",
                route = "games/subtraction",
                backgroundColor = 0xFFFF7043, // Deep Orange
                iconType = GameIconType.SUBTRACTION,
                isAvailable = true
            ),
            GameInfo(
                id = "numberbonds",
                name = "Number Bonds",
                description = "Numbers that go together",
                route = "games/numberbonds",
                backgroundColor = 0xFF2196F3, // Blue
                iconType = GameIconType.NUMBERBONDS,
                isAvailable = true
            ),
            GameInfo(
                id = "compare",
                name = "Greater or Less",
                description = "Compare numbers",
                route = "games/compare",
                backgroundColor = 0xFFAB47BC, // Purple
                iconType = GameIconType.COMPARE,
                isAvailable = true
            ),
            GameInfo(
                id = "oddoneout",
                name = "Odd One Out",
                description = "Find what's different",
                route = "games/oddoneout",
                backgroundColor = 0xFFFF5722, // Deep Orange
                iconType = GameIconType.ODDONEOUT,
                isAvailable = true
            ),
            GameInfo(
                id = "sudoku",
                name = "Picture Sudoku",
                description = "Fill the puzzle grid",
                route = "games/sudoku",
                backgroundColor = 0xFF7C4DFF, // Deep Purple
                iconType = GameIconType.SUDOKU,
                isAvailable = true
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
