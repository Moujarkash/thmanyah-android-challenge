package com.mod.thmanyah_android_challenge.data.mapper

import com.mod.thmanyah_android_challenge.data.dto.SearchResponseDto
import com.mod.thmanyah_android_challenge.domain.model.SearchResponse
import com.mod.thmanyah_android_challenge.data.dto.SearchSectionDto
import com.mod.thmanyah_android_challenge.data.dto.SearchContentItemDto
import com.mod.thmanyah_android_challenge.domain.model.*

fun SearchResponseDto.toDomain(): SearchResponse {
    return SearchResponse(
        sections = sections.map { it.toDomain() }
    )
}

fun SearchSectionDto.toDomain(): HomeSection {
    return HomeSection(
        name = name,
        type = type,
        contentType = contentType,
        order = parseIntSafely(order) ?: 1, // Default to 1 if parsing fails
        content = content.map { it.toDomain(contentType) }
    )
}

fun SearchContentItemDto.toDomain(contentType: String): ContentItem {
    return when (contentType.lowercase()) {
        "podcast" -> createPodcastFromSearchData(this)
        "episode" -> createEpisodeFromSearchData(this)
        "audio_book", "audiobook" -> createAudioBookFromSearchData(this)
        "audio_article", "audioarticle", "article" -> createAudioArticleFromSearchData(this)
        else -> createPodcastFromSearchData(this) // Default to podcast
    }
}

private fun createPodcastFromSearchData(dto: SearchContentItemDto): Podcast {
    return Podcast(
        podcastId = dto.podcastId ?: generateFallbackId("podcast"),
        name = dto.name,
        description = dto.description,
        avatarUrl = dto.avatarUrl,
        episodeCount = parseIntSafely(dto.episodeCount) ?: 0,
        duration = parseLongSafely(dto.duration) ?: 0L,
        language = cleanLanguageField(dto.language),
        priority = parseIntSafely(dto.priority),
        popularityScore = parseIntSafely(dto.popularityScore),
        score = parseDoubleSafely(dto.score)
    )
}

private fun createEpisodeFromSearchData(dto: SearchContentItemDto): Episode {
    return Episode(
        episodeId = dto.episodeId ?: generateFallbackId("episode"),
        name = dto.name,
        seasonNumber = parseIntSafely(dto.seasonNumber),
        episodeType = dto.episodeType ?: "full",
        podcastName = dto.podcastName ?: "Unknown Podcast",
        authorName = dto.authorName ?: "",
        description = dto.description,
        number = parseIntSafely(dto.number),
        duration = parseIntSafely(dto.duration) ?: 0,
        avatarUrl = dto.avatarUrl,
        separatedAudioUrl = dto.separatedAudioUrl,
        audioUrl = dto.audioUrl ?: "",
        releaseDate = dto.releaseDate ?: "",
        podcastId = dto.podcastId ?: "",
        podcastPopularityScore = parseIntSafely(dto.popularityScore),
        podcastPriority = parseIntSafely(dto.priority),
        score = parseDoubleSafely(dto.score)
    )
}

private fun createAudioBookFromSearchData(dto: SearchContentItemDto): AudioBook {
    return AudioBook(
        audiobookId = dto.audiobookId ?: generateFallbackId("audiobook"),
        name = dto.name,
        authorName = dto.authorName ?: "Unknown Author",
        description = dto.description,
        avatarUrl = dto.avatarUrl,
        duration = parseLongSafely(dto.duration) ?: 0L,
        language = cleanLanguageField(dto.language),
        releaseDate = dto.releaseDate ?: "",
        score = parseDoubleSafely(dto.score)
    )
}

private fun createAudioArticleFromSearchData(dto: SearchContentItemDto): AudioArticle {
    return AudioArticle(
        articleId = dto.articleId ?: generateFallbackId("article"),
        name = dto.name,
        authorName = dto.authorName ?: "Unknown Author",
        description = dto.description,
        avatarUrl = dto.avatarUrl,
        duration = parseLongSafely(dto.duration) ?: 0L,
        releaseDate = dto.releaseDate ?: "",
        score = parseDoubleSafely(dto.score)
    )
}

private fun parseIntSafely(value: String?): Int? {
    return try {
        value?.trim()?.toIntOrNull()
    } catch (_: Exception) {
        null
    }
}

private fun parseLongSafely(value: String?): Long? {
    return try {
        value?.trim()?.toLongOrNull()
    } catch (_: Exception) {
        null
    }
}

private fun parseDoubleSafely(value: String?): Double? {
    return try {
        value?.trim()?.toDoubleOrNull()
    } catch (_: Exception) {
        null
    }
}

private fun cleanLanguageField(language: String?): String {
    return language?.takeIf {
        it.isNotBlank() &&
                !it.contains("Lorem", ignoreCase = true) &&
                !it.contains("ipsum", ignoreCase = true) &&
                it.length <= 10 // Reasonable language code length
    } ?: "en"
}

private fun generateFallbackId(type: String): String {
    return "${type}_${System.currentTimeMillis()}_${(1000..9999).random()}"
}
