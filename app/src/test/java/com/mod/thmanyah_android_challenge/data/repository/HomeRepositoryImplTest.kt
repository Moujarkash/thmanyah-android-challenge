package com.mod.thmanyah_android_challenge.data.repository

import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.data.dto.ContentItemDto
import com.mod.thmanyah_android_challenge.data.dto.HomeSectionDto
import com.mod.thmanyah_android_challenge.data.dto.HomeSectionsResponseDto
import com.mod.thmanyah_android_challenge.data.dto.PaginationDto
import com.mod.thmanyah_android_challenge.data.remote.api.HomeApiService
import com.mod.thmanyah_android_challenge.domain.model.AudioBook
import com.mod.thmanyah_android_challenge.domain.model.Episode
import com.mod.thmanyah_android_challenge.domain.model.Podcast
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.exceptions.base.MockitoException
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class HomeRepositoryImplTest {
    @Mock
    private lateinit var mockApiService: HomeApiService

    private lateinit var repository: HomeRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = HomeRepositoryImpl(mockApiService)
    }

    @Test
    fun `getHomeSections should return Success with mapped data when API call succeeds`() = runTest {
        val mockDto = createMockHomeSectionsResponseDto()
        whenever(mockApiService.getHomeSections(1)).thenReturn(mockDto)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success

        assertEquals(2, successResult.data.sections.size)

        val firstSection = successResult.data.sections[0]
        assertEquals("Top Podcasts", firstSection.name)
        assertEquals("square", firstSection.type)
        assertEquals("podcast", firstSection.contentType)
        assertEquals(1, firstSection.order)
        assertEquals(2, firstSection.content.size)

        val firstPodcast = firstSection.content[0] as Podcast
        assertEquals("Test Podcast 1", firstPodcast.name)
        assertEquals("Test Description 1", firstPodcast.description)
        assertEquals("https://test.com/image1.jpg", firstPodcast.avatarUrl)
        assertEquals("podcast_1", firstPodcast.podcastId)
        assertEquals(50, firstPodcast.episodeCount)
        assertEquals(3600L, firstPodcast.duration)
        assertEquals("en", firstPodcast.language)
        assertEquals(5, firstPodcast.priority)
        assertEquals(9, firstPodcast.popularityScore)

        val secondSection = successResult.data.sections[1]
        assertEquals("Trending Episodes", secondSection.name)
        assertEquals("2_lines_grid", secondSection.type)
        assertEquals("episode", secondSection.contentType)
        assertEquals(2, secondSection.order)
        assertEquals(1, secondSection.content.size)

        val firstEpisode = secondSection.content[0] as Episode
        assertEquals("Episode 1", firstEpisode.name)
        assertEquals("Episode Description 1", firstEpisode.description)
        assertEquals("https://test.com/episode1.jpg", firstEpisode.avatarUrl)
        assertEquals("ep_1", firstEpisode.episodeId)
        assertEquals(1, firstEpisode.seasonNumber)
        assertEquals("full", firstEpisode.episodeType)
        assertEquals("Test Podcast Show", firstEpisode.podcastName)
        assertEquals("Test Author", firstEpisode.authorName)
        assertEquals(1, firstEpisode.number)
        assertEquals(1800, firstEpisode.duration)
        assertEquals("https://test.com/audio1.mp3", firstEpisode.audioUrl)
        assertEquals("2024-01-01T00:00:00Z", firstEpisode.releaseDate)
        assertEquals("podcast_1", firstEpisode.podcastId)
        assertEquals(8, firstEpisode.podcastPopularityScore)
        assertEquals(4, firstEpisode.podcastPriority)

        assertEquals("/home_sections?page=2", successResult.data.pagination.nextPage)
        assertEquals(5, successResult.data.pagination.totalPages)

        verify(mockApiService).getHomeSections(1)
    }

    @Test
    fun `getHomeSections should pass correct page parameter to API service`() = runTest {
        val expectedPage = 3
        val mockDto = createMockHomeSectionsResponseDto()
        whenever(mockApiService.getHomeSections(expectedPage)).thenReturn(mockDto)

        repository.getHomeSections(expectedPage)

        verify(mockApiService).getHomeSections(expectedPage)
    }

    @Test
    fun `getHomeSections should use default page 1 when no page specified`() = runTest {
        val mockDto = createMockHomeSectionsResponseDto()
        whenever(mockApiService.getHomeSections(1)).thenReturn(mockDto)

        repository.getHomeSections()

        verify(mockApiService).getHomeSections(1)
    }

    @Test
    fun `getHomeSections should return Error when API throws IOException`() = runTest {
        val exception = MockitoException("Network connection failed")
        whenever(mockApiService.getHomeSections(1)).thenThrow(exception)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Network connection failed", errorResult.exception.message)
    }

    @Test
    fun `getHomeSections should return Error when API throws SocketTimeoutException`() = runTest {
        val exception = MockitoException("Request timeout")
        whenever(mockApiService.getHomeSections(1)).thenThrow(exception)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Request timeout", errorResult.exception.message)
    }

    @Test
    fun `getHomeSections should return Error when API throws UnknownHostException`() = runTest {
        val exception = MockitoException("Unable to resolve host")
        whenever(mockApiService.getHomeSections(1)).thenThrow(exception)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Unable to resolve host", errorResult.exception.message)
    }

    @Test
    fun `getHomeSections should return Error when API throws generic Exception`() = runTest {
        val exception = RuntimeException("Unexpected error occurred")
        whenever(mockApiService.getHomeSections(1)).thenThrow(exception)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Error)
        val errorResult = result as Result.Error
        assertEquals(exception, errorResult.exception)
        assertEquals("Unexpected error occurred", errorResult.exception.message)
    }

    @Test
    fun `getHomeSections should handle empty sections response correctly`() = runTest {
        val emptyDto = HomeSectionsResponseDto(
            sections = emptyList(),
            pagination = PaginationDto(null, 1)
        )
        whenever(mockApiService.getHomeSections(1)).thenReturn(emptyDto)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertTrue(successResult.data.sections.isEmpty())
        assertNull(successResult.data.pagination.nextPage)
        assertEquals(1, successResult.data.pagination.totalPages)
    }

    @Test
    fun `getHomeSections should handle sections with empty content correctly`() = runTest {
        val dtoWithEmptyContent = HomeSectionsResponseDto(
            sections = listOf(
                HomeSectionDto(
                    name = "Empty Section",
                    type = "square",
                    contentType = "podcast",
                    order = 1,
                    content = emptyList()
                )
            ),
            pagination = PaginationDto(null, 1)
        )
        whenever(mockApiService.getHomeSections(1)).thenReturn(dtoWithEmptyContent)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data.sections.size)
        assertEquals("Empty Section", successResult.data.sections[0].name)
        assertTrue(successResult.data.sections[0].content.isEmpty())
    }

    @Test
    fun `getHomeSections should handle mixed content types in sections correctly`() = runTest {
        val mixedContentDto = HomeSectionsResponseDto(
            sections = listOf(
                HomeSectionDto(
                    name = "Mixed Content Section",
                    type = "square",
                    contentType = "audio_book",
                    order = 1,
                    content = listOf(
                        ContentItemDto(
                            name = "Test AudioBook",
                            description = "Book Description",
                            avatarUrl = "https://test.com/book.jpg",
                            score = 95.0,
                            audiobookId = "book_1",
                            authorName = "Book Author",
                            duration = 7200L,
                            language = "en",
                            releaseDate = "2023-12-01T00:00:00Z"
                        )
                    )
                )
            ),
            pagination = PaginationDto(null, 1)
        )
        whenever(mockApiService.getHomeSections(1)).thenReturn(mixedContentDto)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data.sections.size)

        val section = successResult.data.sections[0]
        assertEquals("Mixed Content Section", section.name)
        assertEquals("audio_book", section.contentType)
        assertEquals(1, section.content.size)

        val audioBook = section.content[0] as AudioBook
        assertEquals("Test AudioBook", audioBook.name)
        assertEquals("Book Author", audioBook.authorName)
        assertEquals("book_1", audioBook.audiobookId)
        assertEquals(7200L, audioBook.duration)
        assertEquals("en", audioBook.language)
        assertEquals("2023-12-01T00:00:00Z", audioBook.releaseDate)
    }

    @Test
    fun `getHomeSections should handle null optional fields correctly`() = runTest {
        val dtoWithNullFields = HomeSectionsResponseDto(
            sections = listOf(
                HomeSectionDto(
                    name = "Section With Nulls",
                    type = "square",
                    contentType = "podcast",
                    order = 1,
                    content = listOf(
                        ContentItemDto(
                            name = "Podcast With Nulls",
                            description = "Description",
                            avatarUrl = "https://test.com/image.jpg",
                            score = null,
                            podcastId = "podcast_1",
                            episodeCount = null,
                            duration = null,
                            language = null,
                            priority = null,
                            popularityScore = null
                        )
                    )
                )
            ),
            pagination = PaginationDto(null, 1)
        )
        whenever(mockApiService.getHomeSections(1)).thenReturn(dtoWithNullFields)

        val result = repository.getHomeSections(1)

        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        val podcast = successResult.data.sections[0].content[0] as Podcast

        assertEquals("Podcast With Nulls", podcast.name)
        assertEquals("podcast_1", podcast.podcastId)
        assertEquals(0, podcast.episodeCount) // Should default to 0
        assertEquals(0L, podcast.duration) // Should default to 0
        assertEquals("", podcast.language) // Should default to empty string
        assertNull(podcast.priority) // Should remain null
        assertNull(podcast.popularityScore) // Should remain null
        assertNull(podcast.score) // Should remain null
    }

    private fun createMockHomeSectionsResponseDto(): HomeSectionsResponseDto {
        return HomeSectionsResponseDto(
            sections = listOf(
                HomeSectionDto(
                    name = "Top Podcasts",
                    type = "square",
                    contentType = "podcast",
                    order = 1,
                    content = listOf(
                        ContentItemDto(
                            name = "Test Podcast 1",
                            description = "Test Description 1",
                            avatarUrl = "https://test.com/image1.jpg",
                            score = 95.5,
                            podcastId = "podcast_1",
                            episodeCount = 50,
                            duration = 3600L,
                            language = "en",
                            priority = 5,
                            popularityScore = 9
                        ),
                        ContentItemDto(
                            name = "Test Podcast 2",
                            description = "Test Description 2",
                            avatarUrl = "https://test.com/image2.jpg",
                            score = 88.0,
                            podcastId = "podcast_2",
                            episodeCount = 30,
                            duration = 2400L,
                            language = "ar",
                            priority = 3,
                            popularityScore = 7
                        )
                    )
                ),
                HomeSectionDto(
                    name = "Trending Episodes",
                    type = "2_lines_grid",
                    contentType = "episode",
                    order = 2,
                    content = listOf(
                        ContentItemDto(
                            name = "Episode 1",
                            description = "Episode Description 1",
                            avatarUrl = "https://test.com/episode1.jpg",
                            score = 92.3,
                            episodeId = "ep_1",
                            seasonNumber = 1,
                            episodeType = "full",
                            podcastName = "Test Podcast Show",
                            authorName = "Test Author",
                            number = 1,
                            duration = 1800L,
                            audioUrl = "https://test.com/audio1.mp3",
                            releaseDate = "2024-01-01T00:00:00Z",
                            podcastId = "podcast_1",
                            podcastPopularityScore = 8,
                            podcastPriority = 4
                        )
                    )
                )
            ),
            pagination = PaginationDto(
                nextPage = "/home_sections?page=2",
                totalPages = 5
            )
        )
    }
}