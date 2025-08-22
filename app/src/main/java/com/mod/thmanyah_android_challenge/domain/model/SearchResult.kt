package com.mod.thmanyah_android_challenge.domain.model

data class SearchResult(
    val id: String,
    val name: String,
    val description: String,
    val avatarUrl: String,
    val type: String,
    val authorName: String?,
    val podcastName: String?,
    val duration: Long?,
    val episodeCount: Int?,
    val score: Double?
)
