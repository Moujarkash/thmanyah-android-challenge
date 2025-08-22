package com.mod.thmanyah_android_challenge.domain.repository

import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.HomeSectionsResponse

interface HomeRepository {
    suspend fun getHomeSections(page: Int = 1): Result<HomeSectionsResponse>
}