package com.kidplayer.app.presentation.games.spelling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.data.local.LanguageManager
import com.kidplayer.app.domain.reward.GameDifficulty
import com.kidplayer.app.domain.reward.RewardManager
import com.kidplayer.app.presentation.games.common.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpellingViewModel @Inject constructor(
    private val rewardManager: RewardManager,
    private val languageManager: LanguageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpellingUiState())
    val uiState: StateFlow<SpellingUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0

    init {
        viewModelScope.launch {
            rewardManager.initialize()
        }
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        val isRomanian = languageManager.isRomanian()
        val firstWord = SpellingWords.getRandomWord(1)
        val word = firstWord.getWord(isRomanian)
        _uiState.update {
            SpellingUiState(
                round = 1,
                score = 0,
                level = 1,
                currentWord = firstWord,
                isRomanian = isRomanian,
                letterTiles = SpellingGenerator.scrambleLetters(word),
                placedLetters = List(word.length) { null },
                gameState = GameState.Playing(score = 0)
            )
        }
    }

    fun onLetterTileClick(tile: LetterTile) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.wordComplete) return

        if (tile.isPlaced) {
            // Remove letter from placed position
            removeLetter(tile)
        } else {
            // Place letter in next empty slot
            placeLetter(tile)
        }
    }

    private fun placeLetter(tile: LetterTile) {
        val currentState = _uiState.value
        val nextEmptyIndex = currentState.placedLetters.indexOfFirst { it == null }
        if (nextEmptyIndex == -1) return

        val updatedTiles = currentState.letterTiles.map {
            if (it.id == tile.id) it.copy(isPlaced = true, placedIndex = nextEmptyIndex)
            else it
        }

        val updatedPlaced = currentState.placedLetters.toMutableList()
        updatedPlaced[nextEmptyIndex] = tile

        _uiState.update {
            it.copy(
                letterTiles = updatedTiles,
                placedLetters = updatedPlaced
            )
        }

        // Check if word is complete
        checkWordComplete()
    }

    private fun removeLetter(tile: LetterTile) {
        val currentState = _uiState.value

        val updatedTiles = currentState.letterTiles.map {
            if (it.id == tile.id) it.copy(isPlaced = false, placedIndex = -1)
            else it
        }

        val updatedPlaced = currentState.placedLetters.toMutableList()
        updatedPlaced[tile.placedIndex] = null

        _uiState.update {
            it.copy(
                letterTiles = updatedTiles,
                placedLetters = updatedPlaced
            )
        }
    }

    private fun checkWordComplete() {
        val currentState = _uiState.value
        if (currentState.placedLetters.any { it == null }) return

        val spelledWord = currentState.placedLetters.mapNotNull { it?.letter }.joinToString("")
        val correctWord = currentState.currentWord?.getWord(currentState.isRomanian) ?: return
        val isCorrect = spelledWord == correctWord

        val newScore = if (isCorrect) {
            currentState.score + (SpellingConfig.POINTS_CORRECT_LETTER * correctWord.length) + SpellingConfig.BONUS_COMPLETE_WORD
        } else {
            currentState.score
        }

        val newCorrectCount = if (isCorrect) currentState.correctCount + 1 else currentState.correctCount

        _uiState.update {
            it.copy(
                wordComplete = true,
                isCorrect = isCorrect,
                score = newScore,
                correctCount = newCorrectCount,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Move to next word after delay
        viewModelScope.launch {
            delay(1500)
            if (isCorrect) {
                nextWord()
            } else {
                // Reset current word for retry
                resetCurrentWord()
            }
        }
    }

    private fun resetCurrentWord() {
        val currentState = _uiState.value
        val spellingWord = currentState.currentWord ?: return
        val word = spellingWord.getWord(currentState.isRomanian)

        _uiState.update {
            it.copy(
                letterTiles = SpellingGenerator.scrambleLetters(word),
                placedLetters = List(word.length) { null },
                wordComplete = false,
                isCorrect = false
            )
        }
    }

    private fun nextWord() {
        val currentState = _uiState.value
        val nextRound = currentState.round + 1
        val isRomanian = currentState.isRomanian

        if (nextRound > SpellingConfig.TOTAL_ROUNDS) {
            handleGameComplete()
        } else {
            // Increase level every 3 rounds
            val newLevel = (nextRound - 1) / 3 + 1
            val nextSpellingWord = SpellingWords.getRandomWord(minOf(newLevel, 3))
            val word = nextSpellingWord.getWord(isRomanian)

            _uiState.update {
                it.copy(
                    round = nextRound,
                    level = minOf(newLevel, 3),
                    currentWord = nextSpellingWord,
                    letterTiles = SpellingGenerator.scrambleLetters(word),
                    placedLetters = List(word.length) { null },
                    wordComplete = false,
                    isCorrect = false
                )
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val percentage = currentState.correctCount.toFloat() / SpellingConfig.TOTAL_ROUNDS
        val stars = when {
            percentage >= 0.9f -> 3
            percentage >= 0.7f -> 2
            else -> 1
        }

        // Award stars based on difficulty
        viewModelScope.launch {
            val difficulty = when (currentState.level) {
                1 -> GameDifficulty.EASY
                2 -> GameDifficulty.MEDIUM
                else -> GameDifficulty.HARD
            }
            rewardManager.awardStarsForCompletion(difficulty, true)
        }

        _uiState.update {
            it.copy(
                gameState = GameState.Completed(
                    won = true,
                    score = currentState.score,
                    stars = stars,
                    timeElapsedMs = timeElapsed
                )
            )
        }
    }

    fun pauseGame() {
        _uiState.update { it.copy(gameState = GameState.Paused) }
    }

    fun resumeGame() {
        val currentState = _uiState.value
        _uiState.update {
            it.copy(gameState = GameState.Playing(score = currentState.score))
        }
    }
}

data class SpellingUiState(
    val round: Int = 1,
    val score: Int = 0,
    val level: Int = 1,
    val correctCount: Int = 0,
    val currentWord: SpellingWord? = null,
    val isRomanian: Boolean = false,
    val letterTiles: List<LetterTile> = emptyList(),
    val placedLetters: List<LetterTile?> = emptyList(),
    val wordComplete: Boolean = false,
    val isCorrect: Boolean = false,
    val gameState: GameState = GameState.Loading
)
