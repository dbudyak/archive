package com.wolt.openhours.services

import com.wolt.openhours.exceptions.ValidationException
import com.wolt.openhours.models.DayTimeRange
import com.wolt.openhours.models.TimeChunk
import com.wolt.openhours.models.TimeChunkType.CLOSE
import com.wolt.openhours.models.TimeChunkType.OPEN
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.DayOfWeek
import java.time.DayOfWeek.*

class ConverterTest {

    private val conv: ConverterService = ConverterService(ValidatorService())
    private val weekTemplate: Map<DayOfWeek, List<TimeChunk>> = DayOfWeek.values().associateWith { emptyList() }

    @Test
    fun `check that schema model converted properly`() {
        val test = mapOf(
            MONDAY to emptyList(),
            TUESDAY to listOf(TimeChunk(OPEN, 36000), TimeChunk(CLOSE, 64800)),
            WEDNESDAY to emptyList(),
            THURSDAY to listOf(TimeChunk(OPEN, 37800), TimeChunk(CLOSE, 64800)),
            FRIDAY to listOf(TimeChunk(OPEN, 36000)),
            SATURDAY to listOf(TimeChunk(CLOSE, 3600), TimeChunk(OPEN, 36000)),
            SUNDAY to listOf(TimeChunk(CLOSE, 3600), TimeChunk(OPEN, 43200), TimeChunk(CLOSE, 75600))
        )

        val expected = mapOf(
            TUESDAY to listOf(DayTimeRange(TUESDAY, 36000, 64800)),
            THURSDAY to listOf(DayTimeRange(THURSDAY, 37800, 64800)),
            FRIDAY to listOf(DayTimeRange(FRIDAY, 36000, 3600)),
            SATURDAY to listOf(DayTimeRange(SATURDAY, 36000, 3600)),
            SUNDAY to listOf(DayTimeRange(SUNDAY, 43200, 75600)),
        )

        assertThat(conv.convert(test), equalTo(expected))
    }

    @Test
    fun `check random day order`() {
        val test = mapOf(
            WEDNESDAY to emptyList(),
            TUESDAY to listOf(TimeChunk(OPEN, 36000), TimeChunk(CLOSE, 64800)),
            THURSDAY to listOf(TimeChunk(OPEN, 37800), TimeChunk(CLOSE, 64800)),
            FRIDAY to listOf(TimeChunk(OPEN, 36000)),
            SUNDAY to listOf(TimeChunk(CLOSE, 3600), TimeChunk(OPEN, 43200), TimeChunk(CLOSE, 75600)),
            SATURDAY to listOf(TimeChunk(CLOSE, 3600), TimeChunk(OPEN, 36000)),
            MONDAY to emptyList()
        )

        val expected = mapOf(
            TUESDAY to listOf(DayTimeRange(TUESDAY, 36000, 64800)),
            THURSDAY to listOf(DayTimeRange(THURSDAY, 37800, 64800)),
            FRIDAY to listOf(DayTimeRange(FRIDAY, 36000, 3600)),
            SATURDAY to listOf(DayTimeRange(SATURDAY, 36000, 3600)),
            SUNDAY to listOf(DayTimeRange(SUNDAY, 43200, 75600)),
        )

        assertThat(conv.convert(test), equalTo(expected))
    }

    @Test
    fun `check that sunday closes on monday`() {
        val test = weekTemplate.plus(
            mapOf(
                MONDAY to listOf(TimeChunk(CLOSE, 3600)),
                SUNDAY to listOf(TimeChunk(OPEN, 43200))
            )
        )

        val expected = mapOf(
            SUNDAY to listOf(DayTimeRange(SUNDAY, 43200, 3600)),
        )

        assertThat(conv.convert(test), equalTo(expected))
    }


    @Test
    fun `check always closed`() {
        val test = weekTemplate
        val expected = mapOf<DayOfWeek, List<DayTimeRange>>()

        assertThat(conv.convert(test), equalTo(expected))
    }

    @Test
    fun `check missing days`() {
        val test = mapOf<DayOfWeek, List<TimeChunk>>().plus(SUNDAY to emptyList())

        assertThrows<ValidationException> { conv.convert(test) }
    }

    @Test
    fun `check invalid time order`() {
        val test = weekTemplate.plus(
            mapOf(
                TUESDAY to listOf(TimeChunk(OPEN, 3600), TimeChunk(OPEN, 36000)),
            )
        )
        assertThrows<ValidationException> { conv.convert(test) }
    }

    @Test
    fun `check invalid order for sunday-monday range`() {
        val test = weekTemplate.plus(
            mapOf(
                SUNDAY to listOf(TimeChunk(CLOSE, 36000)),
                MONDAY to listOf(TimeChunk(CLOSE, 3600)),
            )
        )
        assertThrows<ValidationException> { conv.convert(test) }
    }

    @Test
    fun `check missing time chunks`() {
        val test = weekTemplate.plus(
            mapOf(
                TUESDAY to listOf(TimeChunk(OPEN, 3600), TimeChunk(CLOSE, 36001)),
                WEDNESDAY to listOf(TimeChunk(OPEN, 36000))
            )
        )
        assertThrows<ValidationException> { conv.convert(test) }
    }

    @Test
    fun `check missing time chunks on sunday`() {
        val test = weekTemplate.plus(
            mapOf(
                MONDAY to listOf(TimeChunk(OPEN, 3600), TimeChunk(OPEN, 36001)),
                SUNDAY to listOf(TimeChunk(OPEN, 36000))
            )
        )
        assertThrows<ValidationException> { conv.convert(test) }
    }
}