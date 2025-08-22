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
                        content = mockMalformedSearchResponse,
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
    fun `search should handle malformed API response correctly`() = runTest {
        val query = "kotlin podcast"

        val result = searchApiService.search(query)

        assertNotNull(result)
        assertEquals(2, result.sections.size)

        val firstSection = result.sections[0]
        assertEquals("Y", firstSection.name)
        assertEquals("adipisicing reprehenderit anim", firstSection.type)
        assertEquals("in Lorem nostrud", firstSection.contentType)
        assertEquals("aliqua pariatur", firstSection.order)
        assertEquals(3, firstSection.content.size)

        val firstContent = firstSection.content[0]
        assertEquals("7f2fe780-5f64-45ed-bfb7-8917fa1cf42f", firstContent.podcastId)
        assertEquals("Licensed Steel Chips", firstContent.name)
        assertEquals("12", firstContent.episodeCount)
        assertEquals("82952", firstContent.duration)
        assertEquals("minim", firstContent.score)
    }

    @Test
    fun `search should send correct query parameter`() = runTest {
        val expectedQuery = "android development"

        mockEngine = MockEngine { request ->
            val queryParam = request.url.parameters["query"]
            assertEquals(expectedQuery, queryParam)

            respond(
                content = mockMalformedSearchResponse,
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

        searchApiService.search(expectedQuery)
    }

    @Test
    fun `search should use correct endpoint and method`() = runTest {
        mockEngine = MockEngine { request ->
            assertEquals("https", request.url.protocol.name)
            assertEquals("mock.apidog.com", request.url.host)
            assertEquals("/m1/735111-711675-default/search", request.url.encodedPath)
            assertEquals(HttpMethod.Get, request.method)

            respond(
                content = mockMalformedSearchResponse,
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

    @Test
    fun `search should handle empty sections response`() = runTest {
        val emptyResponse = """{"sections": []}"""

        mockEngine = MockEngine { request ->
            respond(
                content = emptyResponse,
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
        assertTrue(result.sections.isEmpty())
    }

    private companion object {
        val mockMalformedSearchResponse = """
            {
                "sections": [
                    {
                        "name": "Y",
                        "type": "adipisicing reprehenderit anim",
                        "content_type": "in Lorem nostrud",
                        "order": "aliqua pariatur",
                        "content": [
                            {
                                "podcast_id": "7f2fe780-5f64-45ed-bfb7-8917fa1cf42f",
                                "name": "Licensed Steel Chips",
                                "description": "The slim & simple Maple Gaming Keyboard from Dev Byte comes with a sleek body and 7- Color RGB LED Back-lighting for smart functionality",
                                "avatar_url": "https://avatars.githubusercontent.com/u/8032461",
                                "episode_count": "12",
                                "duration": "82952",
                                "language": "in dolore laborum",
                                "priority": "ullamco nisi esse sint ipsum",
                                "popularityScore": "magna in sint enim non",
                                "score": "minim"
                            },
                            {
                                "podcast_id": "42c3cc6e-fec1-4cc2-86ed-2730c89be3a0",
                                "name": "Incredible Bronze Hat",
                                "description": "The slim & simple Maple Gaming Keyboard from Dev Byte comes with a sleek body and 7- Color RGB LED Back-lighting for smart functionality",
                                "avatar_url": "https://avatars.githubusercontent.com/u/40003149",
                                "episode_count": "99",
                                "duration": "43805",
                                "language": "culpa enim et non fugiat",
                                "priority": "fugiat enim",
                                "popularityScore": "fugiat",
                                "score": "Ut id officia"
                            },
                            {
                                "podcast_id": "215b3eb6-2dc7-4728-bccb-0915805057b0",
                                "name": "Intelligent Cotton Chips",
                                "description": "Andy shoes are designed to keeping in mind durability as well as trends, the most stylish range of shoes & sandals",
                                "avatar_url": "https://avatars.githubusercontent.com/u/72057587",
                                "episode_count": "83",
                                "duration": "27782",
                                "language": "proident Duis enim aute",
                                "priority": "aliquip ad non occaecat in",
                                "popularityScore": "elit incididunt qui in laborum",
                                "score": "minim Duis"
                            }
                        ]
                    },
                    {
                        "name": "i",
                        "type": "commodo eu Duis",
                        "content_type": "Excepteur mollit ad pariatur in",
                        "order": "qui eu",
                        "content": [
                            {
                                "podcast_id": "9a2d4b00-cf18-4099-826d-2d6399221d4b",
                                "name": "Oriental Steel Sausages",
                                "description": "The beautiful range of Apple Naturalé that has an exciting mix of natural ingredients. With the Goodness of 100% Natural Ingredients",
                                "avatar_url": "https://avatars.githubusercontent.com/u/57486917",
                                "episode_count": "29",
                                "duration": "46668",
                                "language": "quis sunt dolor veniam Duis",
                                "priority": "voluptate sed cupidatat sint",
                                "popularityScore": "laboris incididunt",
                                "score": "adipisicing Excepteur dolore"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()
    }
}
