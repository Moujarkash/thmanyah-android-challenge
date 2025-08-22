package com.mod.thmanyah_android_challenge.domain.repository

import com.mod.thmanyah_android_challenge.core.util.Result
import com.mod.thmanyah_android_challenge.domain.model.SearchResponse

interface SearchRepository {
    suspend fun search(query: String, page: Int = 1): Result<SearchResponse>
}