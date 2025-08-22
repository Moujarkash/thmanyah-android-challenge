package com.mod.thmanyah_android_challenge.data.remote.api

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SearchApiServiceTest {
    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient
    private lateinit var searchApiService: SearchApiService

    @Before
    fun setup() {
        mockEngine = MockEngine { request ->
            when (request.url.rawSegments.last()) {
                "search" -> {
                    respond(
                        content = mockSearchResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> {
                    respond(
                        content = "",
                        status = HttpStatusCode.NotFound
                    )
                }
            }
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        searchApiService = SearchApiService(httpClient)
    }

    @Test
    fun `search should return correct response with query parameter`() = runTest {
        val query = "test podcast"

        mockEngine = MockEngine { request ->
            // Verify query parameter
            val queryParam = request.url.parameters["query"]
            assertEquals(query, queryParam)

            respond(
                content = mockSearchResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        searchApiService = SearchApiService(httpClient)

        val result = searchApiService.search(query)

        assertNotNull(result)
        assertEquals(2, result.results.size)
        assertEquals(2, result.total)
        assertEquals(1, result.page)
        assertEquals(1, result.totalPages)

        val firstResult = result.results[0]
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
    }

    @Test
    fun `search should use correct URL and method`() = runTest {
        mockEngine = MockEngine { request ->
            // Verify URL components
            assertEquals("https", request.url.protocol.name)
            assertEquals("mock.apidog.com", request.url.host)
            assertEquals("/m1/735111-711675-default/search", request.url.encodedPath)
            assertEquals(HttpMethod.Get, request.method)

            respond(
                content = mockSearchResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        searchApiService = SearchApiService(httpClient)

        searchApiService.search("test")
    }

    @Test
    fun `search should handle empty results`() = runTest {
        val emptySearchResponse = """
            {
                "results": [],
                "total": 0,
                "page": 1,
                "total_pages": 1
            }
        """.trimIndent()

        mockEngine = MockEngine { request ->
            respond(
                content = emptySearchResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        searchApiService = SearchApiService(httpClient)

        val result = searchApiService.search("nonexistent")

        assertNotNull(result)
        assertTrue(result.results.isEmpty())
        assertEquals(0, result.total)
        assertEquals(1, result.page)
        assertEquals(1, result.totalPages)
    }

    @Test(expected = Exception::class)
    fun `search should throw exception on server error`() = runTest {
        mockEngine = MockEngine { request ->
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        searchApiService = SearchApiService(httpClient)

        searchApiService.search("test")
    }

    private companion object {
        val mockSearchResponse = """
            {
                "results": [
                    {
                        "id": "podcast_1",
                        "name": "Test Podcast",
                        "description": "A test podcast description",
                        "avatar_url": "https://test.com/podcast.jpg",
                        "type": "podcast",
                        "author_name": null,
                        "podcast_name": null,
                        "duration": 1800,
                        "episode_count": 25,
                        "score": 95.0
                    },
                    {
                        "id": "episode_1",
                        "name": "Test Episode",
                        "description": "A test episode description",
                        "avatar_url": "https://test.com/episode.jpg",
                        "type": "episode",
                        "author_name": "Episode Author",
                        "podcast_name": "Host Podcast",
                        "duration": 1200,
                        "episode_count": null,
                        "score": 88.5
                    }
                ],
                "total": 2,
                "page": 1,
                "total_pages": 1
            }
        """.trimIndent()
    }
}