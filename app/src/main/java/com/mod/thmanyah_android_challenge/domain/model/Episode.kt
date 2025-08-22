package com.mod.thmanyah_android_challenge.domain.model

data class Episode(
    val episodeId: String,
    override val name: String,
    val seasonNumber: Int?,
    val episodeType: String, // "full", "trailer"
    val podcastName: String,
    val authorName: String,
    override val description: String,
    val number: Int?,
    val duration: Int,
    override val avatarUrl: String,
    val separatedAudioUrl: String?,
    val audioUrl: String,
    val releaseDate: String,
    val podcastId: String,
    val podcastPopularityScore: Int?,
    val podcastPriority: Int?,
    override val score: Double?
) : ContentItem()