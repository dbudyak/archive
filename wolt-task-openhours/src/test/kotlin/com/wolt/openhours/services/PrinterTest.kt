package com.wolt.openhours.services

import com.wolt.openhours.models.DayTimeRange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class PrinterTest {

    private val printer: PrinterService = PrinterService()

    @Test
    fun `check that model printed properly`() {
        val test = mapOf(
            DayOfWeek.TUESDAY to listOf(DayTimeRange(DayOfWeek.TUESDAY, 36000, 64800)),
            DayOfWeek.THURSDAY to listOf(DayTimeRange(DayOfWeek.THURSDAY, 37800, 64800)),
            DayOfWeek.FRIDAY to listOf(DayTimeRange(DayOfWeek.FRIDAY, 36000, 3600)),
            DayOfWeek.SATURDAY to listOf(DayTimeRange(DayOfWeek.SATURDAY, 36000, 3600)),
            DayOfWeek.SUNDAY to listOf(DayTimeRange(DayOfWeek.SUNDAY, 43200, 75600)),
        )

        val expected = """
            Monday: Closed
            Tuesday: 10 AM - 6 PM
            Wednesday: Closed
            Thursday: 10:30 AM - 6 PM
            Friday: 10 AM - 1 AM
            Saturday: 10 AM - 1 AM
            Sunday: 12 PM - 9 PM
        """.trimIndent()

        assertThat(printer.renderAsString(test), equalTo(expected))
    }

    @Test
    fun `check that all is closed`() {
        val test = mapOf<DayOfWeek, List<DayTimeRange>>()
        val expected = """
            Monday: Closed
            Tuesday: Closed
            Wednesday: Closed
            Thursday: Closed
            Friday: Closed
            Saturday: Closed
            Sunday: Closed
        """.trimIndent()

        assertThat(printer.renderAsString(test), equalTo(expected))
    }

}