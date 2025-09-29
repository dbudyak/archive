package com.wolt.openhours.controllers

import com.wolt.openhours.exceptions.ValidationException
import com.wolt.openhours.models.TimeChunk
import com.wolt.openhours.services.ConverterService
import com.wolt.openhours.services.PrinterService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek

@RestController
@RequestMapping("/restaurant")
class RestaurantController(val converter: ConverterService, val printer: PrinterService) {

    val logger: Logger = LoggerFactory.getLogger(RestaurantController::class.java)

    @PostMapping("/schedule/format")
    fun getHumanHours(@RequestBody body: Map<DayOfWeek, List<TimeChunk>>): String = printer.renderAsString(converter.convert(body))

    @ExceptionHandler(value = [ValidationException::class])
    fun handleValidationError(ex: ValidationException):ResponseEntity<String> {
        logger.error("Validation error: ${ex.message}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
    }
}