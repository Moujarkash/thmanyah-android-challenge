package com.mod.thmanyah_android_challenge.data.repository

import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.data.mapper.toDomain
import com.mod.thmanyah_android_challenge.data.remote.api.SearchApiService
import com.mod.thmanyah_android_challenge.domain.model.SearchResponse
import com.mod.thmanyah_android_challenge.domain.repository.SearchRepository

class SearchRepositoryImpl(
    private val searchApiService: SearchApiService
) : SearchRepository {

    override suspend fun search(query: String, page: Int): Result<SearchResponse> {
        return try {
            val response = searchApiService.search(query)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}