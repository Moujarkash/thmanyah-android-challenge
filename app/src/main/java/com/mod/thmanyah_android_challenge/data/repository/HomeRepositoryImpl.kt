package com.mod.thmanyah_android_challenge.data.repository

import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.data.mapper.toDomain
import com.mod.thmanyah_android_challenge.data.remote.api.HomeApiService
import com.mod.thmanyah_android_challenge.domain.model.HomeSectionsResponse
import com.mod.thmanyah_android_challenge.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val apiService: HomeApiService
) : HomeRepository {

    override suspend fun getHomeSections(page: Int): Result<HomeSectionsResponse> {
        return try {
            val response = apiService.getHomeSections(page)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}