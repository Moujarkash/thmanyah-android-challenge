package com.mod.thmanyah_android_challenge.domain.model

sealed class ContentItem {
    abstract val name: String
    abstract val description: String
    abstract val avatarUrl: String
    abstract val score: Double?
}