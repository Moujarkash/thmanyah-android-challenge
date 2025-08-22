package com.mod.thmanyah_android_challenge.domain.model

data class AudioArticle(
    val articleId: String,
    override val name: String,
    val authorName: String,
    override val description: String,
    override val avatarUrl: String,
    val duration: Long,
    val releaseDate: String,
    override val score: Double?
) : ContentItem()
