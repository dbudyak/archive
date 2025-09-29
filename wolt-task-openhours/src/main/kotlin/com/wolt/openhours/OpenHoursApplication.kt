package com.wolt.openhours

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [JacksonAutoConfiguration::class])
class OpeningHoursApplication

fun main(args: Array<String>) {
    runApplication<OpeningHoursApplication>(*args)
}