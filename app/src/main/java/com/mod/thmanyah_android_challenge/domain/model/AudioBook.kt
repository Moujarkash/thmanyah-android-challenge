package com.mod.thmanyah_android_challenge.domain.model

data class AudioBook(
    val audiobookId: String,
    override val name: String,
    val authorName: String,
    override val description: String,
    override val avatarUrl: String,
    val duration: Long,
    val language: String,
    val releaseDate: String,
    override val score: Double?
) : ContentItem()
