package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val results: List<SearchResultDto>,
    val total: Int,
    val page: Int,
    @SerialName("total_pages") val totalPages: Int
)
