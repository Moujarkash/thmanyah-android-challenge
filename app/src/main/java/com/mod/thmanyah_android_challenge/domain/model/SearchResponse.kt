package com.mod.thmanyah_android_challenge.domain.model

data class SearchResponse(
    val results: List<SearchResult>,
    val total: Int,
    val page: Int,
    val totalPages: Int
)
