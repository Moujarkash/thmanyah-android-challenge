package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val type: String, // "podcast", "episode", "audio_book", "audio_article"
    @SerialName("author_name") val authorName: String? = null,
    @SerialName("podcast_name") val podcastName: String? = null,
    val duration: Long? = null,
    @SerialName("episode_count") val episodeCount: Int? = null,
    val score: Double? = null
)
