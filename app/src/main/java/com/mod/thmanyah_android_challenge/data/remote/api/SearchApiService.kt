package com.mod.thmanyah_android_challenge.data.remote.api

import com.mod.thmanyah_android_challenge.data.dto.SearchResponseDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SearchApiService(
    private val httpClient: HttpClient
) {
    private companion object {
        const val SEARCH_URL = "https://mock.apidog.com/m1/735111-711675-default/search"
    }

    suspend fun search(query: String): SearchResponseDto {
        return httpClient.get(SEARCH_URL) {
            parameter("query", query)
        }.body()
    }
}