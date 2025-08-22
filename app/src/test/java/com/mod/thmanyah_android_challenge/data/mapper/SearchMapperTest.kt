package com.mod.thmanyah_android_challenge.data.mapper
import com.mod.thmanyah_android_challenge.data.dto.*
import com.mod.thmanyah_android_challenge.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class SearchMapperTest {
    @Test
    fun `SearchResponseDto toDomain should handle malformed data correctly`() {
        val malformedDto = SearchResponseDto(
            sections = listOf(
                SearchSectionDto(
                    name = "Test Section",
                    type = "square",
                    contentType = "podcast",
                    order = "not_a_number", // Malformed order
                    content = listOf(
                        SearchContentItemDto(
                            name = "Test Podcast",
                            description = "Test Description",
                            avatarUrl = "https://test.com/image.jpg",
                            podcastId = "podcast_1",
                            episodeCount = "invalid_number",
                            duration = "also_invalid",
                            language = "Lorem ipsum dolor",
                            priority = "high_priority",
                            popularityScore = "very_popular",
                            score = "excellent"
                        )
                    )
                )
            )
        )

        val domain = malformedDto.toDomain()

        assertEquals(1, domain.sections.size)

        val section = domain.sections[0]
        assertEquals("Test Section", section.name)
        assertEquals("square", section.type)
        assertEquals("podcast", section.contentType)
        assertEquals(1, section.order)

        assertEquals(1, section.content.size)
        val podcast = section.content[0] as Podcast
        assertEquals("Test Podcast", podcast.name)
        assertEquals("podcast_1", podcast.podcastId)
        assertEquals(0, podcast.episodeCount)
        assertEquals(0L, podcast.duration)
        assertEquals("en", podcast.language)
        assertNull(podcast.priority)
        assertNull(podcast.popularityScore)
        assertNull(podcast.score)
    }

    @Test
    fun `SearchContentItemDto should parse valid numeric strings correctly`() {
        val validDto = SearchContentItemDto(
            name = "Valid Podcast",
            description = "Valid Description",
            avatarUrl = "https://test.com/image.jpg",
            podcastId = "podcast_1",
            episodeCount = "25",
            duration = "3600",
            language = "en",
            priority = "5",
            popularityScore = "9",
            score = "95.5"
        )

        val podcast = validDto.toDomain("podcast") as Podcast

        assertEquals("Valid Podcast", podcast.name)
        assertEquals("podcast_1", podcast.podcastId)
        assertEquals(25, podcast.episodeCount)
        assertEquals(3600L, podcast.duration)
        assertEquals("en", podcast.language)
        assertEquals(5, podcast.priority)
        assertEquals(9, podcast.popularityScore)
    }

    @Test
    fun `SearchContentItemDto should handle mixed valid and invalid data`() {
        val mixedDto = SearchContentItemDto(
            name = "Mixed Data Podcast",
            description = "Mixed Description",
            avatarUrl = "https://test.com/image.jpg",
            podcastId = "podcast_1",
            episodeCount = "50",
            duration = "invalid_duration",
            language = "es",
            priority = "high",
            popularityScore = "8",
            score = "not_a_score"
        )

        val podcast = mixedDto.toDomain("podcast") as Podcast

        assertEquals("Mixed Data Podcast", podcast.name)
        assertEquals(50, podcast.episodeCount)
        assertEquals(0L, podcast.duration)
        assertEquals("es", podcast.language)
        assertNull(podcast.priority)
        assertEquals(8, podcast.popularityScore)
        assertNull(podcast.score)
    }

    @Test
    fun `SearchContentItemDto should create different content types based on contentType`() {
        val episodeDto = SearchContentItemDto(
            name = "Test Episode",
            description = "Episode Description",
            avatarUrl = "https://test.com/episode.jpg",
            episodeId = "episode_1",
            podcastName = "Host Podcast",
            authorName = "Episode Author",
            duration = "1800"
        )

        val audiobookDto = SearchContentItemDto(
            name = "Test AudioBook",
            description = "Book Description",
            avatarUrl = "https://test.com/book.jpg",
            audiobookId = "book_1",
            authorName = "Book Author",
            duration = "7200"
        )

        // When
        val episode = episodeDto.toDomain("episode") as Episode
        val audiobook = audiobookDto.toDomain("audio_book") as AudioBook

        // Then
        assertEquals("Test Episode", episode.name)
        assertEquals("episode_1", episode.episodeId)
        assertEquals("Host Podcast", episode.podcastName)
        assertEquals("Episode Author", episode.authorName)
        assertEquals(1800, episode.duration)

        assertEquals("Test AudioBook", audiobook.name)
        assertEquals("book_1", audiobook.audiobookId)
        assertEquals("Book Author", audiobook.authorName)
        assertEquals(7200L, audiobook.duration)
    }

    @Test
    fun `SearchContentItemDto should generate fallback IDs when missing`() {
        val dtoWithoutId = SearchContentItemDto(
            name = "No ID Podcast",
            description = "Description",
            avatarUrl = "https://test.com/image.jpg"
        )

        val podcast = dtoWithoutId.toDomain("podcast") as Podcast

        assertEquals("No ID Podcast", podcast.name)
        assertTrue("Should generate fallback ID", podcast.podcastId.startsWith("podcast_"))
        assertTrue("Should contain timestamp", podcast.podcastId.length > 10)
    }

    @Test
    fun `SearchSectionDto should filter out Lorem ipsum language correctly`() {
        val loremLanguages = listOf(
            "Lorem ipsum dolor",
            "in dolore laborum",
            "ipsum consectetur",
            "LOREM IPSUM",
            "very long language name that exceeds reasonable limits"
        )

        loremLanguages.forEach { loremLang ->
            val dto = SearchContentItemDto(
                name = "Test",
                description = "Test",
                avatarUrl = "https://test.com/test.jpg",
                language = loremLang
            )

            val podcast = dto.toDomain("podcast") as Podcast

            assertEquals("Should default to 'en' for Lorem text: $loremLang", "en", podcast.language)
        }
    }

    @Test
    fun `SearchSectionDto should keep valid languages`() {
        val validLanguages = listOf("en", "es", "fr", "de", "ar", "zh")

        validLanguages.forEach { validLang ->
            val dto = SearchContentItemDto(
                name = "Test",
                description = "Test",
                avatarUrl = "https://test.com/test.jpg",
                language = validLang
            )

            val podcast = dto.toDomain("podcast") as Podcast

            assertEquals("Should keep valid language: $validLang", validLang, podcast.language)
        }
    }
}