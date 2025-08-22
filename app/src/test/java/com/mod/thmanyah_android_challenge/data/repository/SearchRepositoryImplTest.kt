package com.mod.thmanyah_android_challenge.data.repository
import android.util.Log
import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.data.dto.*
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
    fun `search should return Success with mapped sections when API call succeeds`() = runTest {
        val query = "kotlin podcast"
        val mockResponse = createMockSearchResponse()
        whenever(mockSearchApiService.search(query)).thenReturn(mockResponse)

        val result = repository.search(query)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success

        assertEquals(2, successResult.data.sections.size)

        val firstSection = successResult.data.sections[0]
        assertEquals("Y", firstSection.name)
        assertEquals("adipisicing reprehenderit anim", firstSection.type)
        assertEquals("in Lorem nostrud", firstSection.contentType)
        assertEquals(1, firstSection.order) // Parsed from malformed string
        assertEquals(3, firstSection.content.size)

        val firstContent = firstSection.content[0]
        assertTrue("Should be a Podcast", firstContent is com.mod.thmanyah_android_challenge.domain.model.Podcast)

        val podcast = firstContent as com.mod.thmanyah_android_challenge.domain.model.Podcast
        assertEquals("Licensed Steel Chips", podcast.name)
        assertEquals("7f2fe780-5f64-45ed-bfb7-8917fa1cf42f", podcast.podcastId)
        assertEquals(12, podcast.episodeCount)
        assertEquals(82952L, podcast.duration)
        assertEquals("en", podcast.language)

        verify(mockSearchApiService).search(query)
    }

    @Test
    fun `search should return Error when API throws IOException`() = runTest {
        val query = "test"
        val exception = MockKException("Network connection failed")
        whenever(mockSearchApiService.search(query)).thenThrow(exception)

        val result = repository.search(query)

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

        val result = repository.search(query)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Search request timeout", errorResult.exception.message)
    }

    @Test
    fun `search should pass correct query to API service`() = runTest {
        val specificQuery = "android development tutorials"
        val mockResponse = createMockSearchResponse()
        whenever(mockSearchApiService.search(specificQuery)).thenReturn(mockResponse)

        repository.search(specificQuery)

        verify(mockSearchApiService).search(specificQuery)
    }

    @Test
    fun `search should handle sections with all malformed content`() = runTest {
        val query = "malformed"
        val malformedResponse = SearchResponseDto(
            sections = listOf(
                SearchSectionDto(
                    name = "Malformed Section",
                    type = "square",
                    contentType = "podcast",
                    order = "invalid_order",
                    content = listOf(
                        SearchContentItemDto(
                            name = "Completely Malformed",
                            description = "Description",
                            avatarUrl = "https://test.com/malformed.jpg",
                            episodeCount = "not_a_number",
                            duration = "also_not_a_number",
                            language = "Lorem ipsum dolor sit amet",
                            score = "excellent_but_not_numeric"
                        )
                    )
                )
            )
        )
        whenever(mockSearchApiService.search(query)).thenReturn(malformedResponse)

        val result = repository.search(query)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success

        assertEquals(1, successResult.data.sections.size)
        val section = successResult.data.sections[0]
        assertEquals("Malformed Section", section.name)
        assertEquals(1, section.order)

        val podcast = section.content[0] as com.mod.thmanyah_android_challenge.domain.model.Podcast
        assertEquals("Completely Malformed", podcast.name)
        assertEquals(0, podcast.episodeCount)
        assertEquals(0L, podcast.duration)
        assertEquals("en", podcast.language)
        assertNull(podcast.score)
    }

    private fun createMockSearchResponse(): SearchResponseDto {
        return SearchResponseDto(
            sections = listOf(
                SearchSectionDto(
                    name = "Y",
                    type = "adipisicing reprehenderit anim",
                    contentType = "in Lorem nostrud",
                    order = "aliqua pariatur",
                    content = listOf(
                        SearchContentItemDto(
                            name = "Licensed Steel Chips",
                            description = "The slim & simple Maple Gaming Keyboard",
                            avatarUrl = "https://avatars.githubusercontent.com/u/8032461",
                            podcastId = "7f2fe780-5f64-45ed-bfb7-8917fa1cf42f",
                            episodeCount = "12",
                            duration = "82952",
                            language = "in dolore laborum",
                            priority = "ullamco nisi esse sint ipsum",
                            popularityScore = "magna in sint enim non",
                            score = "minim"
                        ),
                        SearchContentItemDto(
                            name = "Incredible Bronze Hat",
                            description = "Another test podcast",
                            avatarUrl = "https://avatars.githubusercontent.com/u/40003149",
                            podcastId = "42c3cc6e-fec1-4cc2-86ed-2730c89be3a0",
                            episodeCount = "99",
                            duration = "43805",
                            language = "culpa enim et non fugiat",
                            priority = "fugiat enim",
                            popularityScore = "fugiat",
                            score = "Ut id officia"
                        ),
                        SearchContentItemDto(
                            name = "Intelligent Cotton Chips",
                            description = "Third test podcast",
                            avatarUrl = "https://avatars.githubusercontent.com/u/72057587",
                            podcastId = "215b3eb6-2dc7-4728-bccb-0915805057b0",
                            episodeCount = "83",
                            duration = "27782",
                            language = "proident Duis enim aute",
                            priority = "aliquip ad non occaecat in",
                            popularityScore = "elit incididunt qui in laborum",
                            score = "minim Duis"
                        )
                    )
                ),
                SearchSectionDto(
                    name = "i",
                    type = "commodo eu Duis",
                    contentType = "Excepteur mollit ad pariatur in",
                    order = "qui eu",
                    content = listOf(
                        SearchContentItemDto(
                            name = "Oriental Steel Sausages",
                            description = "Fourth test podcast",
                            avatarUrl = "https://avatars.githubusercontent.com/u/57486917",
                            podcastId = "9a2d4b00-cf18-4099-826d-2d6399221d4b",
                            episodeCount = "29",
                            duration = "46668",
                            language = "quis sunt dolor veniam Duis",
                            priority = "voluptate sed cupidatat sint",
                            popularityScore = "laboris incididunt",
                            score = "adipisicing Excepteur dolore"
                        )
                    )
                )
            )
        )
    }
}