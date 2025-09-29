package com.wolt.openhours.models

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

data class TimeChunk(val type: TimeChunkType, val value: Int)

data class DayTimeChunk(val day: DayOfWeek, val type: TimeChunkType, val value: Int) {
    fun isOpen():Boolean = type == TimeChunkType.OPEN
}

data class DayTimeRange(val day: DayOfWeek, val openSeconds: Int, val closeSeconds: Int) {
    val open: LocalDateTime = toLocalDateTime(openSeconds)
    val close: LocalDateTime = toLocalDateTime(closeSeconds)

    private fun toLocalDateTime(unixSeconds: Int): LocalDateTime =
        Instant.ofEpochSecond(unixSeconds.toLong()).atOffset(ZoneOffset.UTC).toLocalDateTime()
}

enum class TimeChunkType {
    OPEN, CLOSE
}