package com.mod.thmanyah_android_challenge.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.AudioArticle
import com.mod.thmanyah_android_challenge.domain.model.AudioBook
import com.mod.thmanyah_android_challenge.domain.model.ContentItem
import com.mod.thmanyah_android_challenge.domain.model.Episode
import com.mod.thmanyah_android_challenge.domain.model.HomeSection
import com.mod.thmanyah_android_challenge.domain.model.Podcast
import com.mod.thmanyah_android_challenge.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeSections()
    }

    fun loadHomeSections(page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getHomeSections(page).let { result ->
                when (result) {
                    is Result.Success -> {
                        val mergedSections = if (page == 1) {
                            // First page - use sections as is
                            result.data.sections
                        } else {
                            // Subsequent pages - merge content within existing sections
                            mergeSectionsContent(_uiState.value.sections, result.data.sections)
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            sections = mergedSections,
                            currentPage = page,
                            totalPages = result.data.pagination.totalPages,
                            hasNextPage = result.data.pagination.nextPage != null,
                            nextPageNumber = parsePageFromUrl(result.data.pagination.nextPage)
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Unknown error occurred"
                        )
                    }

                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun mergeSectionsContent(
        existingSections: List<HomeSection>,
        newSections: List<HomeSection>
    ): List<HomeSection> {
        existingSections.associateBy { it.name }

        val newSectionsMap = newSections.associateBy { it.name }

        return existingSections.map { existingSection ->
            val newSection = newSectionsMap[existingSection.name]
            if (newSection != null) {
                val mergedContent = mergeContentItems(existingSection.content, newSection.content)
                existingSection.copy(content = mergedContent)
            } else {
                existingSection
            }
        }
    }

    private fun mergeContentItems(
        existingContent: List<ContentItem>,
        newContent: List<ContentItem>
    ): List<ContentItem> {
        val existingIds = existingContent.map { getContentItemId(it) }.toSet()

        val newUniqueContent = newContent.filter { newItem ->
            getContentItemId(newItem) !in existingIds
        }

        return existingContent + newUniqueContent
    }

    private fun getContentItemId(contentItem: ContentItem): String {
        return when (contentItem) {
            is Podcast -> contentItem.podcastId
            is Episode -> contentItem.episodeId
            is AudioBook -> contentItem.audiobookId
            is AudioArticle -> contentItem.articleId
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (!currentState.isLoadingMore && currentState.hasNextPage) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoadingMore = true, error = null)

                repository.getHomeSections(currentState.currentPage + 1).let { result ->
                    when (result) {
                        is Result.Success -> {
                            val mergedSections =
                                mergeSectionsContent(_uiState.value.sections, result.data.sections)

                            _uiState.value = _uiState.value.copy(
                                isLoadingMore = false,
                                sections = mergedSections,
                                currentPage = currentState.currentPage + 1,
                                totalPages = result.data.pagination.totalPages,
                                hasNextPage = result.data.pagination.nextPage != null,
                                nextPageNumber = parsePageFromUrl(result.data.pagination.nextPage)
                            )
                        }

                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoadingMore = false,
                                error = result.exception.message ?: "Failed to load more content"
                            )
                        }

                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoadingMore = true)
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        loadHomeSections()
    }

    private fun parsePageFromUrl(nextPageUrl: String?): Int? {
        return try {
            nextPageUrl?.let { url ->
                val pageParam = url.substringAfter("page=", "")
                    .substringBefore("&") // In case there are other parameters
                if (pageParam.isNotEmpty() && pageParam != url) {
                    pageParam.toIntOrNull()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error parsing page from URL: ${e.message}", e)
            null
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val sections: List<HomeSection> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val nextPageNumber: Int? = null,
    val hasNextPage: Boolean = false,
    val isLoadingMore: Boolean = false
)