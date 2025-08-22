package com.mod.thmanyah_android_challenge.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.*
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
import org.mockito.kotlin.*

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
    fun `initial state should be empty and not searched`() = runTest {
        val searchQuery = viewModel.searchQuery.first()
        val uiState = viewModel.uiState.first()

        assertEquals("", searchQuery)
        assertFalse(uiState.isLoading)
        assertTrue(uiState.sections.isEmpty())
        assertEquals(0, uiState.totalResults)
        assertFalse(uiState.searchPerformed)
        assertNull(uiState.error)
    }

    @Test
    fun `updateSearchQuery should update search query`() = runTest {
        viewModel.updateSearchQuery("kotlin")

        val searchQuery = viewModel.searchQuery.first()
        assertEquals("kotlin", searchQuery)
    }

    @Test
    fun `updateSearchQuery with blank should clear results`() = runTest {
        viewModel.updateSearchQuery("test")

        viewModel.updateSearchQuery("")

        val uiState = viewModel.uiState.first()
        assertTrue(uiState.sections.isEmpty())
        assertEquals(0, uiState.totalResults)
        assertFalse(uiState.searchPerformed)
    }

    @Test
    fun `search should be debounced by 200ms and require minimum 2 characters`() = runTest {
        val mockSections = listOf(createTestSection("Test Section", 2))
        val mockResponse = SearchResponse(sections = mockSections)
        whenever(searchRepository.search("ko")).thenReturn(Result.Success(mockResponse))

        viewModel.updateSearchQuery("k")
        delay(250)

        verify(searchRepository, never()).search(any(), any())

        viewModel.updateSearchQuery("ko")
        delay(250)

        verify(searchRepository).search("ko")
        val uiState = viewModel.uiState.first()
        assertEquals(1, uiState.sections.size)
        assertEquals(2, uiState.totalResults)
        assertTrue(uiState.searchPerformed)
    }

    @Test
    fun `search should handle successful response with multiple sections`() = runTest {
        val mockSections = listOf(
            createTestSection("Podcasts", 3),
            createTestSection("Episodes", 2)
        )
        val mockResponse = SearchResponse(sections = mockSections)
        whenever(searchRepository.search("android")).thenReturn(Result.Success(mockResponse))

        viewModel.updateSearchQuery("android")
        delay(250)

        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertEquals(2, uiState.sections.size)
        assertEquals(5, uiState.totalResults) // 3 + 2 items
        assertTrue(uiState.searchPerformed)
        assertNull(uiState.error)

        assertEquals("Podcasts", uiState.sections[0].name)
        assertEquals("Episodes", uiState.sections[1].name)
    }

    @Test
    fun `search should filter out empty sections`() = runTest {
        val mockSections = listOf(
            createTestSection("Non-empty Section", 2),
            createTestSection("Empty Section", 0), // Empty section
            createTestSection("Another Non-empty", 1)
        )
        val mockResponse = SearchResponse(sections = mockSections)
        whenever(searchRepository.search("filter")).thenReturn(Result.Success(mockResponse))

        viewModel.updateSearchQuery("filter")
        delay(250)

        val uiState = viewModel.uiState.first()
        assertEquals(2, uiState.sections.size)
        assertEquals(3, uiState.totalResults)

        assertEquals("Non-empty Section", uiState.sections[0].name)
        assertEquals("Another Non-empty", uiState.sections[1].name)
    }

    @Test
    fun `search should handle error response`() = runTest {
        val errorMessage = "Search service unavailable"
        whenever(searchRepository.search("error")).thenReturn(Result.Error(Exception(errorMessage)))

        viewModel.updateSearchQuery("error")
        delay(250)

        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertTrue(uiState.sections.isEmpty())
        assertEquals(0, uiState.totalResults)
        assertTrue(uiState.searchPerformed)
        assertEquals(errorMessage, uiState.error)
    }

    @Test
    fun `search should handle empty results correctly`() = runTest {
        val emptyResponse = SearchResponse(sections = emptyList())
        whenever(searchRepository.search("empty")).thenReturn(Result.Success(emptyResponse))

        viewModel.updateSearchQuery("empty")
        delay(250)

        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertTrue(uiState.sections.isEmpty())
        assertEquals(0, uiState.totalResults)
        assertTrue(uiState.searchPerformed)
        assertNull(uiState.error)
    }

    @Test
    fun `debouncing should cancel previous search when new query typed quickly`() = runTest {
        val firstResponse = SearchResponse(sections = listOf(createTestSection("First", 1)))
        val secondResponse = SearchResponse(sections = listOf(createTestSection("Second", 2)))

        whenever(searchRepository.search("fi")).thenReturn(Result.Success(firstResponse))
        whenever(searchRepository.search("first")).thenReturn(Result.Success(secondResponse))

        viewModel.updateSearchQuery("fi")
        delay(100) // Less than 200ms debounce
        viewModel.updateSearchQuery("first")
        delay(250) // Wait for debounce

        verify(searchRepository, never()).search("fi") // Should be cancelled
        verify(searchRepository).search("first") // Should be performed

        val uiState = viewModel.uiState.first()
        assertEquals("Second", uiState.sections[0].name)
        assertEquals(2, uiState.totalResults)
    }

    @Test
    fun `retry should perform search again with current query`() = runTest {
        val errorMessage = "Network error"
        val successResponse = SearchResponse(sections = listOf(createTestSection("Retry Success", 1)))

        whenever(searchRepository.search("retry"))
            .thenReturn(Result.Error(Exception(errorMessage)))
            .thenReturn(Result.Success(successResponse))

        viewModel.updateSearchQuery("retry")
        delay(250)

        var uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertTrue(uiState.sections.isEmpty())

        viewModel.retry()

        uiState = viewModel.uiState.first()
        assertNull(uiState.error)
        assertEquals(1, uiState.sections.size)
        assertEquals("Retry Success", uiState.sections[0].name)

        verify(searchRepository, times(2)).search("retry")
    }

    @Test
    fun `retry should not perform search if query is blank`() = runTest {
        viewModel.retry()

        verify(searchRepository, never()).search(any(), any())
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.searchPerformed)
    }

    @Test
    fun `retry should not perform search if query is less than 2 characters`() = runTest {
        viewModel.updateSearchQuery("a")

        viewModel.retry()

        verify(searchRepository, never()).search(any(),any())
    }

    @Test
    fun `search should trim whitespace from query`() = runTest {
        val mockResponse = SearchResponse(sections = listOf(createTestSection("Trimmed", 1)))
        whenever(searchRepository.search("kotlin")).thenReturn(Result.Success(mockResponse))

        viewModel.updateSearchQuery("  kotlin  ")
        delay(250)

        verify(searchRepository).search("kotlin")
    }

    @Test
    fun `multiple rapid query changes should only execute final search`() = runTest {
        val finalResponse = SearchResponse(sections = listOf(createTestSection("Final", 1)))
        whenever(searchRepository.search("final")).thenReturn(Result.Success(finalResponse))

        viewModel.updateSearchQuery("f")
        delay(50)
        viewModel.updateSearchQuery("fi")
        delay(50)
        viewModel.updateSearchQuery("fin")
        delay(50)
        viewModel.updateSearchQuery("fina")
        delay(50)
        viewModel.updateSearchQuery("final")
        delay(250) // Wait for final debounce

        verify(searchRepository, times(1)).search("final")
        verify(searchRepository, never()).search("f")
        verify(searchRepository, never()).search("fi")
        verify(searchRepository, never()).search("fin")
        verify(searchRepository, never()).search("fina")

        val uiState = viewModel.uiState.first()
        assertEquals("Final", uiState.sections[0].name)
    }

    private fun createTestSection(name: String, contentCount: Int): HomeSection {
        val content = (1..contentCount).map { index ->
            Podcast(
                podcastId = "${name.lowercase()}_$index",
                name = "$name Podcast $index",
                description = "Description for $name $index",
                avatarUrl = "https://test.com/${name.lowercase()}_$index.jpg",
                episodeCount = 10 + index,
                duration = 3600L,
                language = "en",
                priority = index,
                popularityScore = 5 + index,
                score = 90.0 + index
            )
        }

        return HomeSection(
            name = name,
            type = "square",
            contentType = "podcast",
            order = 1,
            content = content
        )
    }
}