package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeSectionsResponseDto(
    val sections: List<HomeSectionDto>,
    val pagination: PaginationDto
)
