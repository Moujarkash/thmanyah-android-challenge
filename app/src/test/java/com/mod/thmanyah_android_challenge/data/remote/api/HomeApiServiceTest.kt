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

class HomeApiServiceTest {
    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient
    private lateinit var homeApiService: HomeApiService

    @Before
    fun setup() {
        mockEngine = MockEngine { request ->
            when (request.url.rawSegments.last()) {
                "home_sections" -> {
                    respond(
                        content = mockHomeSectionsResponse,
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

        homeApiService = HomeApiService(httpClient)
    }

    @Test
    fun `getHomeSections should return correct response for default page`() = runTest {
        val result = homeApiService.getHomeSections()

        assertNotNull(result)
        assertEquals(2, result.sections.size)

        val firstSection = result.sections[0]
        assertEquals("Top Podcasts", firstSection.name)
        assertEquals("square", firstSection.type)
        assertEquals("podcast", firstSection.contentType)
        assertEquals(1, firstSection.order)
        assertEquals(2, firstSection.content.size)

        val firstContent = firstSection.content[0]
        assertEquals("Test Podcast 1", firstContent.name)
        assertEquals("Test Description 1", firstContent.description)
        assertEquals("https://test.com/image1.jpg", firstContent.avatarUrl)
        assertEquals("podcast_1", firstContent.podcastId)
        assertEquals(50, firstContent.episodeCount)
        assertEquals(3600L, firstContent.duration)
        assertEquals("en", firstContent.language)
        assertEquals(5, firstContent.priority)
        assertEquals(9, firstContent.popularityScore)
        assertEquals(95.5, firstContent.score)

        assertEquals("/home_sections?page=2", result.pagination.nextPage)
        assertEquals(5, result.pagination.totalPages)
    }

    @Test
    fun `getHomeSections should include correct page parameter in request`() = runTest {
        val expectedPage = 3

        mockEngine = MockEngine { request ->
            val pageParam = request.url.parameters["page"]
            assertEquals(expectedPage.toString(), pageParam)

            respond(
                content = mockHomeSectionsResponse,
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

        homeApiService = HomeApiService(httpClient)

        homeApiService.getHomeSections(expectedPage)
    }

    @Test
    fun `getHomeSections should use correct base URL and endpoint`() = runTest {
        mockEngine = MockEngine { request ->
            assertEquals("https", request.url.protocol.name)
            assertEquals("api-v2-b2sit6oh3a-uc.a.run.app", request.url.host)
            assertEquals("/home_sections", request.url.encodedPath)
            assertEquals(HttpMethod.Get, request.method)

            respond(
                content = mockHomeSectionsResponse,
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

        homeApiService = HomeApiService(httpClient)

        homeApiService.getHomeSections()
    }

    @Test(expected = Exception::class)
    fun `getHomeSections should throw exception when server returns error`() = runTest {
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

        homeApiService = HomeApiService(httpClient)

        homeApiService.getHomeSections()
    }

    @Test(expected = Exception::class)
    fun `getHomeSections should throw exception when response is malformed JSON`() = runTest {
        mockEngine = MockEngine { request ->
            respond(
                content = "{ invalid json }",
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

        homeApiService = HomeApiService(httpClient)

        homeApiService.getHomeSections()
    }

    @Test
    fun `getHomeSections should handle empty sections response`() = runTest {
        val emptySectionsResponse = """
            {
                "sections": [],
                "pagination": {
                    "next_page": null,
                    "total_pages": 1
                }
            }
        """.trimIndent()

        mockEngine = MockEngine { request ->
            respond(
                content = emptySectionsResponse,
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

        homeApiService = HomeApiService(httpClient)

        val result = homeApiService.getHomeSections()

        assertNotNull(result)
        assertTrue(result.sections.isEmpty())
        assertNull(result.pagination.nextPage)
        assertEquals(1, result.pagination.totalPages)
    }

    @Test
    fun `getHomeSections should handle sections with empty content`() = runTest {
        val emptySectionContentResponse = """
            {
                "sections": [
                    {
                        "name": "Empty Section",
                        "type": "square",
                        "content_type": "podcast",
                        "order": 1,
                        "content": []
                    }
                ],
                "pagination": {
                    "next_page": null,
                    "total_pages": 1
                }
            }
        """.trimIndent()

        mockEngine = MockEngine { request ->
            respond(
                content = emptySectionContentResponse,
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

        homeApiService = HomeApiService(httpClient)

        val result = homeApiService.getHomeSections()

        assertNotNull(result)
        assertEquals(1, result.sections.size)
        assertEquals("Empty Section", result.sections[0].name)
        assertTrue(result.sections[0].content.isEmpty())
    }

    private companion object {
        val mockHomeSectionsResponse = """
            {
                "sections": [
                    {
                        "name": "Top Podcasts",
                        "type": "square",
                        "content_type": "podcast",
                        "order": 1,
                        "content": [
                            {
                                "name": "Test Podcast 1",
                                "description": "Test Description 1",
                                "avatar_url": "https://test.com/image1.jpg",
                                "podcast_id": "podcast_1",
                                "episode_count": 50,
                                "duration": 3600,
                                "language": "en",
                                "priority": 5,
                                "popularityScore": 9,
                                "score": 95.5
                            },
                            {
                                "name": "Test Podcast 2",
                                "description": "Test Description 2",
                                "avatar_url": "https://test.com/image2.jpg",
                                "podcast_id": "podcast_2",
                                "episode_count": 30,
                                "duration": 2400,
                                "language": "ar",
                                "priority": 3,
                                "popularityScore": 7,
                                "score": 88.0
                            }
                        ]
                    },
                    {
                        "name": "Trending Episodes",
                        "type": "2_lines_grid",
                        "content_type": "episode",
                        "order": 2,
                        "content": [
                            {
                                "name": "Episode 1",
                                "description": "Episode Description 1",
                                "avatar_url": "https://test.com/episode1.jpg",
                                "episode_id": "ep_1",
                                "season_number": 1,
                                "episode_type": "full",
                                "podcast_name": "Test Podcast Show",
                                "author_name": "Test Author",
                                "number": 1,
                                "duration": 1800,
                                "audio_url": "https://test.com/audio1.mp3",
                                "release_date": "2024-01-01T00:00:00Z",
                                "podcast_id": "podcast_1",
                                "podcastPopularityScore": 8,
                                "podcastPriority": 4,
                                "score": 92.3
                            }
                        ]
                    }
                ],
                "pagination": {
                    "next_page": "/home_sections?page=2",
                    "total_pages": 5
                }
            }
        """.trimIndent()
    }
}