package com.mod.thmanyah_android_challenge.data.mapper

import com.mod.thmanyah_android_challenge.data.dto.SearchResponseDto
import com.mod.thmanyah_android_challenge.data.dto.SearchResultDto
import org.junit.Assert.*
import org.junit.Test

class SearchMapperTest {
    @Test
    fun `test SearchResponseDto to Domain mapping`() {
        val dto = SearchResponseDto(
            results = listOf(
                SearchResultDto(
                    id = "result1",
                    name = "Search Result 1",
                    description = "Result Description",
                    avatarUrl = "https://test.com/result.jpg",
                    type = "podcast",
                    authorName = "Author Name",
                    podcastName = null,
                    duration = 1800L,
                    episodeCount = 25,
                    score = 88.5
                )
            ),
            total = 100,
            page = 1,
            totalPages = 10
        )

        val domain = dto.toDomain()

        assertEquals(100, domain.total)
        assertEquals(1, domain.page)
        assertEquals(10, domain.totalPages)
        assertEquals(1, domain.results.size)

        val result = domain.results[0]
        assertEquals("result1", result.id)
        assertEquals("Search Result 1", result.name)
        assertEquals("Result Description", result.description)
        assertEquals("https://test.com/result.jpg", result.avatarUrl)
        assertEquals("podcast", result.type)
        assertEquals("Author Name", result.authorName)
        assertNull(result.podcastName)
        assertEquals(1800L, result.duration)
        assertEquals(25, result.episodeCount)
    }
}