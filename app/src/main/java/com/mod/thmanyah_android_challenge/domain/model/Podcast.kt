package com.mod.thmanyah_android_challenge.domain.model

data class Podcast(
    val podcastId: String,
    override val name: String,
    override val description: String,
    override val avatarUrl: String,
    val episodeCount: Int,
    val duration: Long,
    val language: String,
    val priority: Int?,
    val popularityScore: Int?,
    override val score: Double?
) : ContentItem()
