package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeSectionDto(
    val name: String,
    val type: String,
    @SerialName("content_type") val contentType: String,
    val order: Int,
    val content: List<ContentItemDto>
)
