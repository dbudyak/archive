package com.wolt.openhours.services

import com.wolt.openhours.models.DayTimeChunk
import com.wolt.openhours.models.DayTimeRange
import com.wolt.openhours.models.TimeChunk
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.util.concurrent.TimeUnit

@Service
class ConverterService(val validator: ValidatorService) {

    fun convert(body: Map<DayOfWeek, List<TimeChunk>>): Map<DayOfWeek, List<DayTimeRange>> {
        validator.validateMissingDays(body.keys.toList())

        if (body.values.all { it.isEmpty() }) return mapOf()

        val sortedTimeChunks = body
            .flatMap { it.value.map { chunk -> DayTimeChunk(it.key, chunk.type, chunk.value) } }
            .sortedBy { (it.day.value - 1) * TimeUnit.DAYS.toSeconds(1) + it.value }
            .also { validator.validate(it) }

        return sortedTimeChunks
            .zipWithNext { current, next ->
                when {
                    validator.isValidTimeRange(current, next) -> DayTimeRange(current.day, current.value, next.value)
                    validator.isNextSundayEndsOnMonday(next, sortedTimeChunks) -> DayTimeRange(
                        next.day,
                        next.value,
                        sortedTimeChunks.first().value
                    )
                    else -> {
                        validator.validateRange(sortedTimeChunks, current, next)
                        return@zipWithNext null
                    }
                }
            }
            .filterNotNull()
            .groupBy { it.day }
    }
}