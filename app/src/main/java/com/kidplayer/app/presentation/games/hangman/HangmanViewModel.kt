package com.kidplayer.app.presentation.games.hangman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class HangmanViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HangmanUiState())
    val uiState: StateFlow<HangmanUiState> = _uiState.asStateFlow()

    private var gameStartTime: Long = 0
    private var usedWords: MutableSet<String> = mutableSetOf()

    init {
        startNewGame()
    }

    fun startNewGame() {
        gameStartTime = System.currentTimeMillis()
        usedWords.clear()
        startRound(1, 0)
    }

    private fun startRound(round: Int, currentScore: Int) {
        // Use round number directly for progressive difficulty
        // Round 1: 3-letter, Round 2: 3-4 letter, ... Round 8: 7-8 letter
        val level = round

        // Get a word we haven't used yet
        var wordWithHint = HangmanWords.getRandomWord(level)
        var attempts = 0
        while (usedWords.contains(wordWithHint.word) && attempts < 50) {
            wordWithHint = HangmanWords.getRandomWord(level)
            attempts++
        }
        usedWords.add(wordWithHint.word)

        _uiState.update {
            HangmanUiState(
                round = round,
                score = currentScore,
                currentPuzzle = HangmanPuzzle(wordWithHint),
                wordsGuessed = it.wordsGuessed,
                gameState = GameState.Playing(score = currentScore)
            )
        }
    }

    fun guessLetter(letter: Char) {
        val currentState = _uiState.value
        if (currentState.gameState !is GameState.Playing) return
        if (currentState.showResult) return

        val puzzle = currentState.currentPuzzle ?: return

        if (puzzle.guessedLetters.contains(letter)) return

        val newPuzzle = puzzle.guessLetter(letter)
        val isCorrectGuess = puzzle.word.contains(letter)

        // Calculate score change
        val scoreChange = if (isCorrectGuess) {
            // Count occurrences of the letter
            puzzle.word.count { it == letter } * HangmanConfig.POINTS_PER_LETTER
        } else {
            HangmanConfig.POINTS_WRONG_GUESS
        }

        val newScore = maxOf(0, currentState.score + scoreChange)

        _uiState.update {
            it.copy(
                currentPuzzle = newPuzzle,
                score = newScore,
                gameState = GameState.Playing(score = newScore)
            )
        }

        // Check if round is over
        if (newPuzzle.isGameOver) {
            handleRoundComplete(newPuzzle.isWon)
        }
    }

    private fun handleRoundComplete(won: Boolean) {
        val currentState = _uiState.value

        val bonusScore = if (won) HangmanConfig.POINTS_WIN_BONUS else 0
        val newScore = currentState.score + bonusScore
        val newWordsGuessed = if (won) currentState.wordsGuessed + 1 else currentState.wordsGuessed

        _uiState.update {
            it.copy(
                score = newScore,
                wordsGuessed = newWordsGuessed,
                showResult = true,
                gameState = GameState.Playing(score = newScore)
            )
        }

        viewModelScope.launch {
            delay(2000)

            val nextRound = currentState.round + 1
            if (nextRound > HangmanConfig.TOTAL_ROUNDS) {
                handleGameComplete()
            } else {
                startRound(nextRound, newScore)
            }
        }
    }

    private fun handleGameComplete() {
        val currentState = _uiState.value
        val timeElapsed = System.currentTimeMillis() - gameStartTime

        val percentage = currentState.wordsGuessed.toFloat() / HangmanConfig.TOTAL_ROUNDS
        val stars = when {
            percentage >= 0.9f -> 3
            percentage >= 0.7f -> 2
            else -> 1
        }

        _uiState.update {
            it.copy(
                gameState = GameState.Completed(
                    won = currentState.wordsGuessed > HangmanConfig.TOTAL_ROUNDS / 2,
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

data class HangmanUiState(
    val round: Int = 1,
    val score: Int = 0,
    val wordsGuessed: Int = 0,
    val currentPuzzle: HangmanPuzzle? = null,
    val showResult: Boolean = false,
    val gameState: GameState = GameState.Loading
)
