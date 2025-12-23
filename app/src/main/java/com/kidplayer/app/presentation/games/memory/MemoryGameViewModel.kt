package com.kidplayer.app.presentation.games.memory

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.presentation.games.common.Difficulty
import com.kidplayer.app.presentation.games.common.GameConfig
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Memory Match game
 * Handles card flipping, matching logic, and game state
 */
@HiltViewModel
class MemoryGameViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MemoryGameUiState())
    val uiState: StateFlow<MemoryGameUiState> = _uiState.asStateFlow()

    private var flipBackJob: Job? = null
    private var gameStartTime: Long = 0

    init {
        startNewGame()
    }

    /**
     * Start a new game with current difficulty
     */
    fun startNewGame(difficulty: Difficulty = _uiState.value.config.difficulty) {
        flipBackJob?.cancel()

        val pairCount = when (difficulty) {
            Difficulty.EASY -> 3    // 6 cards (3 pairs)
            Difficulty.MEDIUM -> 6  // 12 cards (6 pairs)
            Difficulty.HARD -> 8    // 16 cards (8 pairs)
        }

        // Select random symbols for this game
        val selectedSymbols = CardSymbol.entries.shuffled().take(pairCount)

        // Create pairs and shuffle
        val cards = selectedSymbols.flatMapIndexed { index, symbol ->
            listOf(
                MemoryCardState(id = index * 2, symbol = symbol),
                MemoryCardState(id = index * 2 + 1, symbol = symbol)
            )
        }.shuffled()

        gameStartTime = System.currentTimeMillis()

        _uiState.update {
            it.copy(
                cards = cards,
                gameState = GameState.Playing(),
                config = it.config.copy(difficulty = difficulty),
                firstFlippedIndex = null,
                secondFlippedIndex = null,
                matchedPairs = 0,
                moves = 0
            )
        }
    }

    /**
     * Handle card tap
     */
    fun onCardClick(index: Int) {
        val currentState = _uiState.value
        val card = currentState.cards.getOrNull(index) ?: return

        // Ignore if game not playing, card already face up, or card already matched
        if (currentState.gameState !is GameState.Playing) return
        if (card.isFaceUp || card.isMatched) return

        // Ignore if two cards already flipped and waiting
        if (currentState.firstFlippedIndex != null && currentState.secondFlippedIndex != null) return

        // Flip the card
        val updatedCards = currentState.cards.toMutableList()
        updatedCards[index] = card.copy(isFaceUp = true)

        if (currentState.firstFlippedIndex == null) {
            // First card flipped
            _uiState.update {
                it.copy(
                    cards = updatedCards,
                    firstFlippedIndex = index
                )
            }
        } else {
            // Second card flipped
            val firstIndex = currentState.firstFlippedIndex
            val firstCard = currentState.cards[firstIndex]
            val newMoves = currentState.moves + 1

            _uiState.update {
                it.copy(
                    cards = updatedCards,
                    secondFlippedIndex = index,
                    moves = newMoves,
                    gameState = GameState.Playing(
                        score = it.matchedPairs * 100,
                        moves = newMoves
                    )
                )
            }

            // Check for match
            if (firstCard.symbol == card.symbol) {
                // Match found!
                viewModelScope.launch {
                    delay(500) // Brief delay to show the match
                    handleMatch(firstIndex, index)
                }
            } else {
                // No match - flip back after delay
                flipBackJob = viewModelScope.launch {
                    delay(1200) // Let player see both cards
                    flipCardsBack(firstIndex, index)
                }
            }
        }
    }

    private fun handleMatch(firstIndex: Int, secondIndex: Int) {
        val currentState = _uiState.value
        val updatedCards = currentState.cards.toMutableList()

        updatedCards[firstIndex] = updatedCards[firstIndex].copy(isMatched = true)
        updatedCards[secondIndex] = updatedCards[secondIndex].copy(isMatched = true)

        val newMatchedPairs = currentState.matchedPairs + 1
        val totalPairs = currentState.cards.size / 2

        _uiState.update {
            it.copy(
                cards = updatedCards,
                firstFlippedIndex = null,
                secondFlippedIndex = null,
                matchedPairs = newMatchedPairs
            )
        }

        // Check for game completion
        if (newMatchedPairs == totalPairs) {
            handleGameComplete()
        }
    }

    private fun flipCardsBack(firstIndex: Int, secondIndex: Int) {
        val currentState = _uiState.value
        val updatedCards = currentState.cards.toMutableList()

        updatedCards[firstIndex] = updatedCards[firstIndex].copy(isFaceUp = false)
        updatedCards[secondIndex] = updatedCards[secondIndex].copy(isFaceUp = false)

        _uiState.update {
            it.copy(
                cards = updatedCards,
                firstFlippedIndex = null,
                secondFlippedIndex = null
            )
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime
        val totalPairs = currentState.cards.size / 2

        // Calculate stars based on moves
        // Perfect: moves = pairs (one try per pair)
        // Good: moves <= pairs * 1.5
        // Okay: moves <= pairs * 2
        val stars = when {
            currentState.moves <= totalPairs -> 3
            currentState.moves <= totalPairs * 1.5 -> 2
            else -> 1
        }

        // Score: base + time bonus + efficiency bonus
        val baseScore = totalPairs * 100
        val efficiencyBonus = ((totalPairs.toFloat() / currentState.moves) * 200).toInt()
        val timeBonus = maxOf(0, 500 - (timeElapsed / 1000).toInt())
        val totalScore = baseScore + efficiencyBonus + timeBonus

        _uiState.update {
            it.copy(
                gameState = GameState.Completed(
                    won = true,
                    score = totalScore,
                    stars = stars,
                    moves = currentState.moves,
                    timeElapsedMs = timeElapsed
                )
            )
        }
    }

    /**
     * Pause the game
     */
    fun pauseGame() {
        flipBackJob?.cancel()
        _uiState.update {
            it.copy(gameState = GameState.Paused)
        }
    }

    /**
     * Resume the game
     */
    fun resumeGame() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(
                gameState = GameState.Playing(
                    score = currentState.matchedPairs * 100,
                    moves = currentState.moves
                )
            )
        }
    }

    /**
     * Change difficulty
     */
    fun setDifficulty(difficulty: Difficulty) {
        startNewGame(difficulty)
    }

    override fun onCleared() {
        super.onCleared()
        flipBackJob?.cancel()
    }
}

/**
 * UI state for Memory game
 */
data class MemoryGameUiState(
    val cards: List<MemoryCardState> = emptyList(),
    val gameState: GameState = GameState.Loading,
    val config: GameConfig = GameConfig(),
    val firstFlippedIndex: Int? = null,
    val secondFlippedIndex: Int? = null,
    val matchedPairs: Int = 0,
    val moves: Int = 0
)

/**
 * State for individual memory card
 */
data class MemoryCardState(
    val id: Int,
    val symbol: CardSymbol,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

/**
 * Card symbols with associated colors
 */
enum class CardSymbol(val color: Color) {
    STAR(Color(0xFFFFD700)),       // Gold
    HEART(Color(0xFFE91E63)),      // Pink
    CIRCLE(Color(0xFF2196F3)),     // Blue
    SQUARE(Color(0xFF4CAF50)),     // Green
    TRIANGLE(Color(0xFFFF9800)),   // Orange
    DIAMOND(Color(0xFF9C27B0)),    // Purple
    MOON(Color(0xFF3F51B5)),       // Indigo
    SUN(Color(0xFFFFC107)),        // Amber
    FLOWER(Color(0xFFE91E63)),     // Pink
    BUTTERFLY(Color(0xFF00BCD4)),  // Cyan
    FISH(Color(0xFF03A9F4)),       // Light Blue
    BIRD(Color(0xFF8BC34A))        // Light Green
}
