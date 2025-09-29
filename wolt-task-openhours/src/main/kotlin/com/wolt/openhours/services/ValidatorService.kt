package com.wolt.openhours.services

import com.wolt.openhours.exceptions.ValidationException
import com.wolt.openhours.models.DayTimeChunk
import org.springframework.stereotype.Service
import java.time.DayOfWeek

@Service
class ValidatorService {

    fun validateMissingDays(data: List<DayOfWeek>) {
        if (!DayOfWeek.values().contentEquals(data.sorted().toTypedArray())) {
            throw ValidationException("Missing days of the week: " + DayOfWeek.values().toList().minus(data.toSet()))
        }
    }

    fun validateRange(sortedTimeChunks: List<DayTimeChunk>, current: DayTimeChunk, next: DayTimeChunk) {
        if (current.type == next.type) {
            throw ValidationException("Invalid order of open and close time on ${current.day}: ${current.type} (${current.value}) - ${next.type} (${next.value})")
        }
        if (next == sortedTimeChunks.last() && next.isOpen()) {
            throw ValidationException("Invalid unclosed range on ${next.day}: ${next.type} (${next.value}) ")
        }
    }

    fun validate(sortedDayTimeChunks: List<DayTimeChunk>) {
        if (sortedDayTimeChunks.size % 2 != 0) {
            throw ValidationException("Invalid size of time chunks: it's odd.")
        }

        val first = sortedDayTimeChunks.first()
        val last = sortedDayTimeChunks.last()
        if (first.day == DayOfWeek.MONDAY && last.day == DayOfWeek.SUNDAY)
            if (first.isOpen() && last.isOpen() || !first.isOpen() && !last.isOpen())
                throw ValidationException("Invalid range for sunday: ${first.type} (${first.value}) - ${last.type} (${last.value})")
    }

    fun isValidTimeRange(current: DayTimeChunk, next: DayTimeChunk) = current.isOpen() && !next.isOpen()

    fun isNextSundayEndsOnMonday(next: DayTimeChunk, sortedChunks: List<DayTimeChunk>) =
        next == sortedChunks.last() && sortedChunks.last().isOpen() && next.day == DayOfWeek.SUNDAY
                && !sortedChunks.first().isOpen() && sortedChunks.first().day == DayOfWeek.MONDAY

}