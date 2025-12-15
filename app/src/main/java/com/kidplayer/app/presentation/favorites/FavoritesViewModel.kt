package com.kidplayer.app.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.usecase.GetFavoritesUseCase
import com.kidplayer.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Favorites screen
 * Manages user's favorite videos
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    /**
     * Load favorites
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getFavoritesUseCase()
                .catch { e ->
                    Timber.e(e, "Error loading favorites")
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "Failed to load favorites",
                            isLoading = false
                        )
                    }
                }
                .collect { favorites ->
                    _uiState.update {
                        it.copy(
                            favorites = favorites,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite(mediaItemId: String) {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(mediaItemId)
            if (result.isFailure) {
                Timber.e(result.exceptionOrNull(), "Error toggling favorite")
                _uiState.update {
                    it.copy(error = "Failed to update favorite")
                }
            }
        }
    }

    /**
     * Dismiss error
     */
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
