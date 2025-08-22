package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationDto(
    @SerialName("next_page") val nextPage: String?,
    @SerialName("total_pages") val totalPages: Int
)
