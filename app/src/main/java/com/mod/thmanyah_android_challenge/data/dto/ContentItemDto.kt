package com.mod.thmanyah_android_challenge.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentItemDto(
    // Common fields
    val name: String,
    val description: String,
    @SerialName("avatar_url") val avatarUrl: String,
    val score: Double?,

    // Podcast fields
    @SerialName("podcast_id") val podcastId: String? = null,
    @SerialName("episode_count") val episodeCount: Int? = null,
    val duration: Long? = null,
    val language: String? = null,
    val priority: Int? = null,
    @SerialName("popularityScore") val popularityScore: Int? = null,

    // Episode fields
    @SerialName("episode_id") val episodeId: String? = null,
    @SerialName("season_number") val seasonNumber: Int? = null,
    @SerialName("episode_type") val episodeType: String? = null,
    @SerialName("podcast_name") val podcastName: String? = null,
    @SerialName("author_name") val authorName: String? = null,
    val number: Int? = null,
    @SerialName("separated_audio_url") val separatedAudioUrl: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("podcastPopularityScore") val podcastPopularityScore: Int? = null,
    @SerialName("podcastPriority") val podcastPriority: Int? = null,

    // AudioBook fields
    @SerialName("audiobook_id") val audiobookId: String? = null,

    // AudioArticle fields
    @SerialName("article_id") val articleId: String? = null
)
