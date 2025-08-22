package com.mod.thmanyah_android_challenge.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.HomeSection
import com.mod.thmanyah_android_challenge.domain.repository.SearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        _searchQuery
            .debounce(200L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() && it.trim().length >= 2 } // Minimum 2 characters
            .onEach { query ->
                performSearch(query.trim())
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            clearResults()
        }
    }

    private fun clearResults() {
        _uiState.value = SearchUiState()
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            searchRepository.search(query).let { result ->
                when (result) {
                    is Result.Success -> {
                        val totalResults = result.data.sections.sumOf { it.content.size }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            sections = result.data.sections.filter { it.content.isNotEmpty() }, // Filter empty sections
                            totalResults = totalResults,
                            searchPerformed = true
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Search failed",
                            searchPerformed = true
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    else -> {}
                }
            }
        }
    }

    fun retry() {
        val currentQuery = _searchQuery.value.trim()
        if (currentQuery.isNotBlank() && currentQuery.length >= 2) {
            performSearch(currentQuery)
        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val sections: List<HomeSection> = emptyList(),
    val error: String? = null,
    val totalResults: Int = 0,
    val searchPerformed: Boolean = false
)