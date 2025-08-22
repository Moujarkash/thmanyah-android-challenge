package com.mod.thmanyah_android_challenge.data.mapper

import com.mod.thmanyah_android_challenge.data.dto.SearchResponseDto
import com.mod.thmanyah_android_challenge.data.dto.SearchResultDto
import com.mod.thmanyah_android_challenge.domain.model.SearchResponse
import com.mod.thmanyah_android_challenge.domain.model.SearchResult

fun SearchResponseDto.toDomain(): SearchResponse {
    return SearchResponse(
        results = results.map { it.toDomain() },
        total = total,
        page = page,
        totalPages = totalPages
    )
}

fun SearchResultDto.toDomain(): SearchResult {
    return SearchResult(
        id = id,
        name = name,
        description = description,
        avatarUrl = avatarUrl,
        type = type,
        authorName = authorName,
        podcastName = podcastName,
        duration = duration,
        episodeCount = episodeCount,
        score = score
    )
}