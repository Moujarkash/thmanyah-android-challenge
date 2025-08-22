package com.mod.thmanyah_android_challenge.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.SearchResponse
import com.mod.thmanyah_android_challenge.domain.model.SearchResult
import com.mod.thmanyah_android_challenge.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var searchRepository: SearchRepository

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(searchRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() = runTest {
        val searchQuery = viewModel.searchQuery.first()
        val uiState = viewModel.uiState.first()

        assertEquals("", searchQuery)
        assertFalse(uiState.isLoading)
        assertTrue(uiState.results.isEmpty())
        assertEquals(0, uiState.total)
        assertNull(uiState.error)
    }

    @Test
    fun `updateSearchQuery with blank should clear results`() = runTest {
        viewModel.updateSearchQuery("test")

        viewModel.updateSearchQuery("")

        val uiState = viewModel.uiState.first()
        assertTrue(uiState.results.isEmpty())
        assertEquals(0, uiState.total)
    }

    @Test
    fun `search should be debounced by 200ms`() = runTest {
        val mockResults = listOf(
            SearchResult(
                id = "1",
                name = "Test Result",
                description = "Description",
                avatarUrl = "url",
                type = "podcast",
                authorName = null,
                podcastName = null,
                duration = 1800L,
                episodeCount = 10,
                score = 95.0
            )
        )
        val mockResponse = SearchResponse(
            results = mockResults,
            total = 1,
            page = 1,
            totalPages = 1
        )
        whenever(searchRepository.search("test", 1)).thenReturn(Result.Success(mockResponse))

        viewModel.updateSearchQuery("test")

        delay(250)

        verify(searchRepository).search("test", 1)
        val uiState = viewModel.uiState.first()
        assertEquals(1, uiState.results.size)
        assertEquals("Test Result", uiState.results[0].name)
    }

    @Test
    fun `search should handle error result`() = runTest {
        val errorMessage = "Search failed"
        whenever(searchRepository.search("error", 1)).thenReturn(Result.Error(Exception(errorMessage)))

        viewModel.updateSearchQuery("error")
        delay(250)

        val uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertTrue(uiState.results.isEmpty())
    }

    @Test
    fun `loadMore should append results to existing ones`() = runTest {
        val firstPageResults = listOf(
            SearchResult("1", "Result 1", "Desc 1", "url1", "podcast", null, null, 1800L, 10, 95.0)
        )
        val secondPageResults = listOf(
            SearchResult("2", "Result 2", "Desc 2", "url2", "episode", null, "Podcast", 1200L, null, 88.0)
        )

        val firstResponse = SearchResponse(firstPageResults, 2, 1, 2)
        val secondResponse = SearchResponse(secondPageResults, 2, 2, 2)

        whenever(searchRepository.search("test", 1)).thenReturn(Result.Success(firstResponse))
        whenever(searchRepository.search("test", 2)).thenReturn(Result.Success(secondResponse))

        viewModel.updateSearchQuery("test")
        delay(250)
        viewModel.loadMore()

        val uiState = viewModel.uiState.first()
        assertEquals(2, uiState.results.size)
        assertEquals("Result 1", uiState.results[0].name)
        assertEquals("Result 2", uiState.results[1].name)
        assertEquals(2, uiState.currentPage)
        assertFalse(uiState.hasNextPage)
    }
}