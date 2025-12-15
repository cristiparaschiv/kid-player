package com.kidplayer.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidplayer.app.domain.usecase.AddSearchHistoryUseCase
import com.kidplayer.app.domain.usecase.GetSearchHistoryUseCase
import com.kidplayer.app.domain.usecase.SearchMediaItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Search screen
 * Manages search operations and history
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMediaItemsUseCase: SearchMediaItemsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSearchHistory()
    }

    /**
     * Load search history
     */
    private fun loadSearchHistory() {
        viewModelScope.launch {
            getSearchHistoryUseCase()
                .collect { history ->
                    _uiState.update { it.copy(searchHistory = history) }
                }
        }
    }

    /**
     * Update search query with debouncing
     */
    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        // Cancel previous search
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    searchResults = emptyList(),
                    hasSearched = false,
                    error = null
                )
            }
            return
        }

        // Debounce search
        searchJob = viewModelScope.launch {
            delay(500) // Wait 500ms before searching
            performSearch(query)
        }
    }

    /**
     * Perform search immediately
     */
    fun onSearch(query: String = _uiState.value.query) {
        if (query.isBlank()) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            performSearch(query)
            // Add to history
            addSearchHistoryUseCase(query)
        }
    }

    /**
     * Perform the actual search operation
     */
    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isSearching = true, error = null) }

        val result = searchMediaItemsUseCase(query)
        result.onSuccess { searchResults ->
            _uiState.update {
                it.copy(
                    searchResults = searchResults,
                    isSearching = false,
                    hasSearched = true
                )
            }
        }.onFailure { exception ->
            Timber.e(exception, "Search failed")
            _uiState.update {
                it.copy(
                    error = exception.message ?: "Search failed",
                    isSearching = false,
                    hasSearched = true
                )
            }
        }
    }

    /**
     * Select a query from history
     */
    fun onHistoryItemClick(query: String) {
        _uiState.update { it.copy(query = query) }
        onSearch(query)
    }

    /**
     * Clear search
     */
    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update {
            SearchUiState(searchHistory = it.searchHistory)
        }
    }
}
