package com.kidplayer.app.presentation.games

import androidx.lifecycle.ViewModel
import com.kidplayer.app.R
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
class GamesViewModel @Inject constructor(
    val musicManager: GameMusicManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    private fun loadGames() {
        val games = listOf(
            GameInfo(
                id = "memory",
                nameResId = R.string.game_memory_name,
                descriptionResId = R.string.game_memory_desc,
                route = "games/memory",
                backgroundColor = 0xFF6C63FF, // Purple
                iconType = GameIconType.MEMORY,
                isAvailable = true
            ),
            GameInfo(
                id = "tictactoe",
                nameResId = R.string.game_tictactoe_name,
                descriptionResId = R.string.game_tictactoe_desc,
                route = "games/tictactoe",
                backgroundColor = 0xFF00BFA5, // Teal
                iconType = GameIconType.TICTACTOE,
                isAvailable = true
            ),
            GameInfo(
                id = "puzzle",
                nameResId = R.string.game_puzzle_name,
                descriptionResId = R.string.game_puzzle_desc,
                route = "games/puzzle",
                backgroundColor = 0xFFFF6B6B, // Coral
                iconType = GameIconType.PUZZLE,
                isAvailable = true
            ),
            GameInfo(
                id = "sliding",
                nameResId = R.string.game_sliding_name,
                descriptionResId = R.string.game_sliding_desc,
                route = "games/sliding",
                backgroundColor = 0xFF42A5F5, // Blue
                iconType = GameIconType.SLIDING,
                isAvailable = true
            ),
            GameInfo(
                id = "gridpuzzle",
                nameResId = R.string.game_gridpuzzle_name,
                descriptionResId = R.string.game_gridpuzzle_desc,
                route = "games/gridpuzzle",
                backgroundColor = 0xFFAB47BC, // Purple
                iconType = GameIconType.GRIDPUZZLE,
                isAvailable = true
            ),
            GameInfo(
                id = "match3",
                nameResId = R.string.game_match3_name,
                descriptionResId = R.string.game_match3_desc,
                route = "games/match3",
                backgroundColor = 0xFFFFB347, // Orange
                iconType = GameIconType.MATCH3,
                isAvailable = true
            ),
            GameInfo(
                id = "coloring",
                nameResId = R.string.game_coloring_name,
                descriptionResId = R.string.game_coloring_desc,
                route = "games/coloring",
                backgroundColor = 0xFF77DD77, // Pastel Green
                iconType = GameIconType.COLORING,
                isAvailable = true
            ),
            GameInfo(
                id = "pattern",
                nameResId = R.string.game_pattern_name,
                descriptionResId = R.string.game_pattern_desc,
                route = "games/pattern",
                backgroundColor = 0xFF9575CD, // Light Purple
                iconType = GameIconType.PATTERN,
                isAvailable = true
            ),
            GameInfo(
                id = "colormix",
                nameResId = R.string.game_colormix_name,
                descriptionResId = R.string.game_colormix_desc,
                route = "games/colormix",
                backgroundColor = 0xFFEC407A, // Pink
                iconType = GameIconType.COLORMIX,
                isAvailable = true
            ),
            GameInfo(
                id = "lettermatch",
                nameResId = R.string.game_lettermatch_name,
                descriptionResId = R.string.game_lettermatch_desc,
                route = "games/lettermatch",
                backgroundColor = 0xFF26A69A, // Teal
                iconType = GameIconType.LETTERMATCH,
                isAvailable = true
            ),
            GameInfo(
                id = "maze",
                nameResId = R.string.game_maze_name,
                descriptionResId = R.string.game_maze_desc,
                route = "games/maze",
                backgroundColor = 0xFF5C6BC0, // Indigo
                iconType = GameIconType.MAZE,
                isAvailable = true
            ),
            GameInfo(
                id = "dots",
                nameResId = R.string.game_dots_name,
                descriptionResId = R.string.game_dots_desc,
                route = "games/dots",
                backgroundColor = 0xFFFFCA28, // Amber
                iconType = GameIconType.DOTS,
                isAvailable = true
            ),
            GameInfo(
                id = "addition",
                nameResId = R.string.game_addition_name,
                descriptionResId = R.string.game_addition_desc,
                route = "games/addition",
                backgroundColor = 0xFF4CAF50, // Green
                iconType = GameIconType.ADDITION,
                isAvailable = true
            ),
            GameInfo(
                id = "subtraction",
                nameResId = R.string.game_subtraction_name,
                descriptionResId = R.string.game_subtraction_desc,
                route = "games/subtraction",
                backgroundColor = 0xFFFF7043, // Deep Orange
                iconType = GameIconType.SUBTRACTION,
                isAvailable = true
            ),
            GameInfo(
                id = "numberbonds",
                nameResId = R.string.game_numberbonds_name,
                descriptionResId = R.string.game_numberbonds_desc,
                route = "games/numberbonds",
                backgroundColor = 0xFF2196F3, // Blue
                iconType = GameIconType.NUMBERBONDS,
                isAvailable = true
            ),
            GameInfo(
                id = "compare",
                nameResId = R.string.game_compare_name,
                descriptionResId = R.string.game_compare_desc,
                route = "games/compare",
                backgroundColor = 0xFFAB47BC, // Purple
                iconType = GameIconType.COMPARE,
                isAvailable = true
            ),
            GameInfo(
                id = "oddoneout",
                nameResId = R.string.game_oddoneout_name,
                descriptionResId = R.string.game_oddoneout_desc,
                route = "games/oddoneout",
                backgroundColor = 0xFFFF5722, // Deep Orange
                iconType = GameIconType.ODDONEOUT,
                isAvailable = true
            ),
            GameInfo(
                id = "sudoku",
                nameResId = R.string.game_sudoku_name,
                descriptionResId = R.string.game_sudoku_desc,
                route = "games/sudoku",
                backgroundColor = 0xFF7C4DFF, // Deep Purple
                iconType = GameIconType.SUDOKU,
                isAvailable = true
            ),
            GameInfo(
                id = "ballsort",
                nameResId = R.string.game_ballsort_name,
                descriptionResId = R.string.game_ballsort_desc,
                route = "games/ballsort",
                backgroundColor = 0xFF00ACC1, // Cyan
                iconType = GameIconType.BALLSORT,
                isAvailable = true
            ),
            GameInfo(
                id = "hangman",
                nameResId = R.string.game_hangman_name,
                descriptionResId = R.string.game_hangman_desc,
                route = "games/hangman",
                backgroundColor = 0xFF8D6E63, // Brown
                iconType = GameIconType.HANGMAN,
                isAvailable = true
            ),
            GameInfo(
                id = "crossword",
                nameResId = R.string.game_crossword_name,
                descriptionResId = R.string.game_crossword_desc,
                route = "games/crossword",
                backgroundColor = 0xFF5C6BC0, // Indigo
                iconType = GameIconType.CROSSWORD,
                isAvailable = true
            ),
            GameInfo(
                id = "counting",
                nameResId = R.string.game_counting_name,
                descriptionResId = R.string.game_counting_desc,
                route = "games/counting",
                backgroundColor = 0xFF66BB6A, // Green
                iconType = GameIconType.COUNTING,
                isAvailable = true
            ),
            GameInfo(
                id = "shapes",
                nameResId = R.string.game_shapes_name,
                descriptionResId = R.string.game_shapes_desc,
                route = "games/shapes",
                backgroundColor = 0xFFE91E63, // Pink
                iconType = GameIconType.SHAPES,
                isAvailable = true
            ),
            GameInfo(
                id = "spelling",
                nameResId = R.string.game_spelling_name,
                descriptionResId = R.string.game_spelling_desc,
                route = "games/spelling",
                backgroundColor = 0xFFFFB300, // Amber
                iconType = GameIconType.SPELLING,
                isAvailable = true
            ),
            GameInfo(
                id = "wordsearch",
                nameResId = R.string.game_wordsearch_name,
                descriptionResId = R.string.game_wordsearch_desc,
                route = "games/wordsearch",
                backgroundColor = 0xFF29B6F6, // Light Blue
                iconType = GameIconType.WORDSEARCH,
                isAvailable = true
            ),
            GameInfo(
                id = "spotdiff",
                nameResId = R.string.game_spotdiff_name,
                descriptionResId = R.string.game_spotdiff_desc,
                route = "games/spotdiff",
                backgroundColor = 0xFF8E24AA, // Purple
                iconType = GameIconType.SPOTDIFF,
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
