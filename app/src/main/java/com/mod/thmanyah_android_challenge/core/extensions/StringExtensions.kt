package com.mod.thmanyah_android_challenge.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

fun String.removeHtmlTags(): String {
    return this.replace(Regex("<[^>]*>"), "").trim()
}

@Composable
fun String.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        append(this@toAnnotatedString.removeHtmlTags())
    }
}