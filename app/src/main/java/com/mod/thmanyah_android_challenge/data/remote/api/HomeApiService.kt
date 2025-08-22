package com.mod.thmanyah_android_challenge.data.remote.api

import com.mod.thmanyah_android_challenge.data.dto.HomeSectionsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class HomeApiService(
    private val httpClient: HttpClient
) {
    private companion object {
        const val BASE_URL = "https://api-v2-b2sit6oh3a-uc.a.run.app"
    }

    suspend fun getHomeSections(page: Int = 1): HomeSectionsResponseDto {
        return httpClient.get("$BASE_URL/home_sections") {
            parameter("page", page)
        }.body()
    }
}