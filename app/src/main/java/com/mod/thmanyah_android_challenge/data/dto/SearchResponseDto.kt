package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val sections: List<SearchSectionDto>
)
