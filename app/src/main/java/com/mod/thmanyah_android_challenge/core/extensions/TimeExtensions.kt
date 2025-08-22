package com.mod.thmanyah_android_challenge.core.extensions

import java.util.Locale
import kotlin.time.Duration.Companion.seconds

fun Long.formatDuration(): String {
    val duration = this.seconds
    val hours = duration.inWholeHours
    val minutes = (duration.inWholeMinutes % 60)
    val secs = (duration.inWholeSeconds % 60)

    return when {
        hours > 0 -> String.format(Locale.US, "%d:%02d:%02d", hours, minutes, secs)
        else -> String.format(Locale.US,"%d:%02d", minutes, secs)
    }
}

fun Int.formatDuration(): String = this.toLong().formatDuration()