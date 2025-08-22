package com.mod.thmanyah_android_challenge.core.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeExtensionsTest {

    @Test
    fun `test formatDuration for seconds only`() {
        assertEquals("0:30", 30L.formatDuration())
        assertEquals("0:05", 5L.formatDuration())
    }

    @Test
    fun `test formatDuration for minutes and seconds`() {
        assertEquals("1:30", 90L.formatDuration())
        assertEquals("5:00", 300L.formatDuration())
        assertEquals("12:45", 765L.formatDuration())
    }

    @Test
    fun `test formatDuration for hours, minutes and seconds`() {
        assertEquals("1:00:00", 3600L.formatDuration())
        assertEquals("1:30:45", 5445L.formatDuration())
        assertEquals("2:15:30", 8130L.formatDuration())
    }

    @Test
    fun `test formatDuration for Int values`() {
        assertEquals("1:30", 90.formatDuration())
        assertEquals("1:00:00", 3600.formatDuration())
    }
}