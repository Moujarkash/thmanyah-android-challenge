package com.mod.thmanyah_android_challenge.data.mapper

import com.mod.thmanyah_android_challenge.data.dto.*
import com.mod.thmanyah_android_challenge.domain.model.*

fun HomeSectionsResponseDto.toDomain(): HomeSectionsResponse {
    return HomeSectionsResponse(
        sections = sections.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}

fun HomeSectionDto.toDomain(): HomeSection {
    return HomeSection(
        name = name,
        type = type,
        contentType = contentType,
        order = order,
        content = content.map { it.toDomain(contentType) }
    )
}

fun PaginationDto.toDomain(): Pagination {
    return Pagination(
        nextPage = nextPage,
        totalPages = totalPages
    )
}

fun ContentItemDto.toDomain(contentType: String): ContentItem {
    return when (contentType) {
        "podcast" -> Podcast(
            podcastId = podcastId ?: "",
            name = name,
            description = description,
            avatarUrl = avatarUrl,
            episodeCount = episodeCount ?: 0,
            duration = duration ?: 0,
            language = language ?: "",
            priority = priority,
            popularityScore = popularityScore,
            score = score
        )
        "episode" -> Episode(
            episodeId = episodeId ?: "",
            name = name,
            seasonNumber = seasonNumber,
            episodeType = episodeType ?: "",
            podcastName = podcastName ?: "",
            authorName = authorName ?: "",
            description = description,
            number = number,
            duration = duration?.toInt() ?: 0,
            avatarUrl = avatarUrl,
            separatedAudioUrl = separatedAudioUrl,
            audioUrl = audioUrl ?: "",
            releaseDate = releaseDate ?: "",
            podcastId = podcastId ?: "",
            podcastPopularityScore = podcastPopularityScore,
            podcastPriority = podcastPriority,
            score = score
        )
        "audio_book" -> AudioBook(
            audiobookId = audiobookId ?: "",
            name = name,
            authorName = authorName ?: "",
            description = description,
            avatarUrl = avatarUrl,
            duration = duration ?: 0,
            language = language ?: "",
            releaseDate = releaseDate ?: "",
            score = score
        )
        "audio_article" -> AudioArticle(
            articleId = articleId ?: "",
            name = name,
            authorName = authorName ?: "",
            description = description,
            avatarUrl = avatarUrl,
            duration = duration ?: 0,
            releaseDate = releaseDate ?: "",
            score = score
        )
        else -> Podcast(
            podcastId = podcastId ?: "",
            name = name,
            description = description,
            avatarUrl = avatarUrl,
            episodeCount = episodeCount ?: 0,
            duration = duration ?: 0,
            language = language ?: "",
            priority = priority,
            popularityScore = popularityScore,
            score = score
        )
    }
}