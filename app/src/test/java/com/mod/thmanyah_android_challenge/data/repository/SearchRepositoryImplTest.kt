package com.mod.thmanyah_android_challenge.data.repository

import android.util.Log
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.data.dto.SearchResponseDto
import com.mod.thmanyah_android_challenge.data.dto.SearchResultDto
import com.mod.thmanyah_android_challenge.data.remote.api.SearchApiService
import io.mockk.MockKException
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SearchRepositoryImplTest {
    @Mock
    private lateinit var mockSearchApiService: SearchApiService

    private lateinit var repository: SearchRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = SearchRepositoryImpl(mockSearchApiService)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any<Throwable>()) } returns 0
    }

    @Test
    fun `search should return Success with mapped data when API call succeeds`() = runTest {
        val query = "test podcast"
        val page = 1
        val mockDto = createMockSearchResponseDto()
        whenever(mockSearchApiService.search(query)).thenReturn(mockDto)

        val result = repository.search(query, page)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success

        assertEquals(2, successResult.data.results.size)
        assertEquals(2, successResult.data.total)
        assertEquals(1, successResult.data.page)
        assertEquals(1, successResult.data.totalPages)

        val firstResult = successResult.data.results[0]
        assertEquals("podcast_1", firstResult.id)
        assertEquals("Test Podcast", firstResult.name)
        assertEquals("A test podcast description", firstResult.description)
        assertEquals("https://test.com/podcast.jpg", firstResult.avatarUrl)
        assertEquals("podcast", firstResult.type)
        assertNull(firstResult.authorName)
        assertNull(firstResult.podcastName)
        assertEquals(1800L, firstResult.duration)
        assertEquals(25, firstResult.episodeCount)
        assertEquals(95.0, firstResult.score)

        val secondResult = successResult.data.results[1]
        assertEquals("episode_1", secondResult.id)
        assertEquals("Test Episode", secondResult.name)
        assertEquals("A test episode description", secondResult.description)
        assertEquals("https://test.com/episode.jpg", secondResult.avatarUrl)
        assertEquals("episode", secondResult.type)
        assertEquals("Episode Author", secondResult.authorName)
        assertEquals("Host Podcast", secondResult.podcastName)
        assertEquals(1200L, secondResult.duration)
        assertNull(secondResult.episodeCount)
        assertEquals(88.5, secondResult.score)

        verify(mockSearchApiService).search(query)
    }

    @Test
    fun `search should pass correct query to API service`() = runTest {
        val query = "specific search query"
        val mockDto = createMockSearchResponseDto()
        whenever(mockSearchApiService.search(query)).thenReturn(mockDto)

        repository.search(query, 1)

        verify(mockSearchApiService).search(query)
    }

    @Test
    fun `search should return Error when API throws IOException`() = runTest {
        val query = "test"
        val exception = MockKException("Network connection failed")
        whenever(mockSearchApiService.search(query)).thenThrow(exception)

        val result = repository.search(query, 1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Network connection failed", errorResult.exception.message)
    }

    @Test
    fun `search should return Error when API throws SocketTimeoutException`() = runTest {
        val query = "test"
        val exception = MockKException("Search request timeout")
        whenever(mockSearchApiService.search(query)).thenThrow(exception)

        val result = repository.search(query, 1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Search request timeout", errorResult.exception.message)
    }

    @Test
    fun `search should handle empty search results correctly`() = runTest {
        val query = "nonexistent"
        val emptyDto = SearchResponseDto(
            results = emptyList(),
            total = 0,
            page = 1,
            totalPages = 1
        )
        whenever(mockSearchApiService.search(query)).thenReturn(emptyDto)

        val result = repository.search(query, 1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertTrue(successResult.data.results.isEmpty())
        assertEquals(0, successResult.data.total)
        assertEquals(1, successResult.data.page)
        assertEquals(1, successResult.data.totalPages)
    }

    @Test
    fun `search should handle results with null optional fields correctly`() = runTest {
        val query = "test"
        val dtoWithNulls = SearchResponseDto(
            results = listOf(
                SearchResultDto(
                    id = "result_1",
                    name = "Result with nulls",
                    description = "Description",
                    avatarUrl = "https://test.com/image.jpg",
                    type = "podcast",
                    authorName = null,
                    podcastName = null,
                    duration = null,
                    episodeCount = null,
                    score = null
                )
            ),
            total = 1,
            page = 1,
            totalPages = 1
        )
        whenever(mockSearchApiService.search(query)).thenReturn(dtoWithNulls)

        val result = repository.search(query, 1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        val searchResult = successResult.data.results[0]

        assertEquals("result_1", searchResult.id)
        assertEquals("Result with nulls", searchResult.name)
        assertEquals("podcast", searchResult.type)
        assertNull(searchResult.authorName)
        assertNull(searchResult.podcastName)
        assertNull(searchResult.duration)
        assertNull(searchResult.episodeCount)
        assertNull(searchResult.score)
    }

    @Test
    fun `search should return Error for generic exceptions`() = runTest {
        val query = "test"
        val exception = RuntimeException("Unexpected search error")
        whenever(mockSearchApiService.search(query)).thenThrow(exception)

        val result = repository.search(query, 1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Unexpected search error", errorResult.exception.message)
    }

    private fun createMockSearchResponseDto(): SearchResponseDto {
        return SearchResponseDto(
            results = listOf(
                SearchResultDto(
                    id = "podcast_1",
                    name = "Test Podcast",
                    description = "A test podcast description",
                    avatarUrl = "https://test.com/podcast.jpg",
                    type = "podcast",
                    authorName = null,
                    podcastName = null,
                    duration = 1800L,
                    episodeCount = 25,
                    score = 95.0
                ),
                SearchResultDto(
                    id = "episode_1",
                    name = "Test Episode",
                    description = "A test episode description",
                    avatarUrl = "https://test.com/episode.jpg",
                    type = "episode",
                    authorName = "Episode Author",
                    podcastName = "Host Podcast",
                    duration = 1200L,
                    episodeCount = null,
                    score = 88.5
                )
            ),
            total = 2,
            page = 1,
            totalPages = 1
        )
    }
}