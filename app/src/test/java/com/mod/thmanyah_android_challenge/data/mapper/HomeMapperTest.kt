package com.mod.thmanyah_android_challenge.data.mapper

import com.mod.thmanyah_android_challenge.data.dto.*
import com.mod.thmanyah_android_challenge.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class HomeMapperTest {

    @Test
    fun `test HomeSectionsResponseDto to Domain mapping`() {
        val dto = HomeSectionsResponseDto(
            sections = listOf(
                HomeSectionDto(
                    name = "Test Section",
                    type = "square",
                    contentType = "podcast",
                    order = 1,
                    content = listOf(
                        ContentItemDto(
                            name = "Test Podcast",
                            description = "Test Description",
                            avatarUrl = "https://test.com/image.jpg",
                            score = 100.0,
                            podcastId = "123",
                            episodeCount = 50,
                            duration = 3600,
                            language = "en"
                        )
                    )
                )
            ),
            pagination = PaginationDto(
                nextPage = "/page2",
                totalPages = 10
            )
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.sections.size)
        assertEquals("Test Section", domain.sections[0].name)
        assertEquals("square", domain.sections[0].type)
        assertEquals("podcast", domain.sections[0].contentType)
        assertEquals(1, domain.sections[0].order)
        assertEquals(1, domain.sections[0].content.size)

        val podcast = domain.sections[0].content[0] as Podcast
        assertEquals("Test Podcast", podcast.name)
        assertEquals("Test Description", podcast.description)
        assertEquals("https://test.com/image.jpg", podcast.avatarUrl)
        assertEquals("123", podcast.podcastId)
        assertEquals(50, podcast.episodeCount)
        assertEquals(3600L, podcast.duration)
        assertEquals("en", podcast.language)

        assertEquals("/page2", domain.pagination.nextPage)
        assertEquals(10, domain.pagination.totalPages)
    }

    @Test
    fun `test ContentItemDto to Episode mapping`() {
        val dto = ContentItemDto(
            name = "Test Episode",
            description = "Episode Description",
            avatarUrl = "https://test.com/episode.jpg",
            score = 85.5,
            episodeId = "ep123",
            seasonNumber = 1,
            episodeType = "full",
            podcastName = "Test Podcast",
            authorName = "Test Author",
            number = 5,
            duration = 1800L,
            separatedAudioUrl = "https://test.com/separated.mp3",
            audioUrl = "https://test.com/audio.mp3",
            releaseDate = "2024-01-01T00:00:00Z",
            podcastId = "pod123",
            podcastPopularityScore = 9,
            podcastPriority = 5
        )

        val episode = dto.toDomain("episode") as Episode

        assertEquals("Test Episode", episode.name)
        assertEquals("Episode Description", episode.description)
        assertEquals("https://test.com/episode.jpg", episode.avatarUrl)
        assertEquals("ep123", episode.episodeId)
        assertEquals(1, episode.seasonNumber)
        assertEquals("full", episode.episodeType)
        assertEquals("Test Podcast", episode.podcastName)
        assertEquals("Test Author", episode.authorName)
        assertEquals(5, episode.number)
        assertEquals(1800, episode.duration)
        assertEquals("https://test.com/separated.mp3", episode.separatedAudioUrl)
        assertEquals("https://test.com/audio.mp3", episode.audioUrl)
        assertEquals("2024-01-01T00:00:00Z", episode.releaseDate)
        assertEquals("pod123", episode.podcastId)
        assertEquals(9, episode.podcastPopularityScore)
        assertEquals(5, episode.podcastPriority)
    }

    @Test
    fun `test ContentItemDto to AudioBook mapping`() {
        val dto = ContentItemDto(
            name = "Test AudioBook",
            description = "Book Description",
            avatarUrl = "https://test.com/book.jpg",
            score = 95.0,
            audiobookId = "book123",
            authorName = "Book Author",
            duration = 7200L,
            language = "en",
            releaseDate = "2023-12-01T00:00:00Z"
        )

        val audioBook = dto.toDomain("audio_book") as AudioBook

        assertEquals("Test AudioBook", audioBook.name)
        assertEquals("Book Description", audioBook.description)
        assertEquals("https://test.com/book.jpg", audioBook.avatarUrl)
        assertEquals("book123", audioBook.audiobookId)
        assertEquals("Book Author", audioBook.authorName)
        assertEquals(7200L, audioBook.duration)
        assertEquals("en", audioBook.language)
        assertEquals("2023-12-01T00:00:00Z", audioBook.releaseDate)
    }
}