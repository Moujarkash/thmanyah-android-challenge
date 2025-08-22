package com.mod.thmanyah_android_challenge.core.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `test removeHtmlTags removes basic HTML tags`() {
        val input = "<p>This is a <strong>test</strong> string with <em>HTML</em> tags.</p>"
        val expected = "This is a test string with HTML tags."
        assertEquals(expected, input.removeHtmlTags())
    }

    @Test
    fun `test removeHtmlTags removes complex HTML`() {
        val input = "<div class='content'><h1>Title</h1><p>Content with <a href='#'>link</a></p></div>"
        val expected = "TitleContent with link"
        assertEquals(expected, input.removeHtmlTags())
    }

    @Test
    fun `test removeHtmlTags handles string without HTML`() {
        val input = "This is a plain text string"
        val expected = "This is a plain text string"
        assertEquals(expected, input.removeHtmlTags())
    }

    @Test
    fun `test removeHtmlTags handles empty string`() {
        val input = ""
        val expected = ""
        assertEquals(expected, input.removeHtmlTags())
    }

    @Test
    fun `test removeHtmlTags handles BR tags and line breaks`() {
        val input = "Line 1<br>Line 2<br/>Line 3"
        val result = input.removeHtmlTags()
        // Should remove BR tags but preserve the text
        assertEquals("Line 1Line 2Line 3", result)
    }
}