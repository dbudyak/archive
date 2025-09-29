package com.wolt.openhours.services

import com.wolt.openhours.models.DayTimeRange
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Service
class PrinterService {

    companion object {
        val formatterMinutes: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")
            .withLocale(Locale.US)
            .withZone(ZoneOffset.UTC)
        val formatterHours: DateTimeFormatter = DateTimeFormatter.ofPattern("h a")
            .withLocale(Locale.US)
            .withZone(ZoneOffset.UTC)
    }

    val weekTemplate: Map<DayOfWeek, List<DayTimeRange>> = DayOfWeek.values().associateWith { listOf() }

    fun renderAsString(data: Map<DayOfWeek, List<DayTimeRange>>): String =
        weekTemplate.plus(data).entries
            .joinToString(separator = "\n") { "${renderDay(it.key)}: " + renderRanges(it.value) }

    fun renderDay(day: DayOfWeek): String = day.getDisplayName(TextStyle.FULL, Locale.US)

    fun renderRanges(ranges: List<DayTimeRange>): String =
        if (ranges.isNotEmpty()) ranges.joinToString(separator = ", ") { range ->
            "${format(range.open)} - ${format(range.close)}"
        } else "Closed"

    fun format(dateTime: LocalDateTime): String =
        if (dateTime.minute != 0) formatterMinutes.format(dateTime) else formatterHours.format(dateTime)


}