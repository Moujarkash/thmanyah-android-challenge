package com.mod.thmanyah_android_challenge.domain.model

data class HomeSection(
    val name: String,
    val type: String, // "square", "2_lines_grid", "big_square", "queue"
    val contentType: String, // "podcast", "episode", "audio_book", "audio_article"
    val order: Int,
    val content: List<ContentItem>
)
