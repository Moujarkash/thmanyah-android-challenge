package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchContentItemDto(
    val name: String,
    val description: String,
    @SerialName("avatar_url") val avatarUrl: String,

    @SerialName("podcast_id") val podcastId: String? = null,
    @SerialName("episode_count") val episodeCount: String? = null,
    val duration: String? = null,
    val language: String? = null,
    val priority: String? = null,
    @SerialName("popularityScore") val popularityScore: String? = null,
    val score: String? = null,

    @SerialName("episode_id") val episodeId: String? = null,
    @SerialName("season_number") val seasonNumber: String? = null,
    @SerialName("episode_type") val episodeType: String? = null,
    @SerialName("podcast_name") val podcastName: String? = null,
    @SerialName("author_name") val authorName: String? = null,
    val number: String? = null,
    @SerialName("separated_audio_url") val separatedAudioUrl: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,

    @SerialName("audiobook_id") val audiobookId: String? = null,

    @SerialName("article_id") val articleId: String? = null
)
