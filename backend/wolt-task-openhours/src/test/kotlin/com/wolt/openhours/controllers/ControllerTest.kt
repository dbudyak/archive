package com.wolt.openhours.controllers

import com.wolt.openhours.models.TimeChunk
import com.wolt.openhours.models.TimeChunkType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.web.client.DefaultResponseErrorHandler
import java.net.URI
import kotlin.collections.set


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerTest {

    @LocalServerPort
    var port = 0

    @Test
    fun `check that response is OK`() {
        val body = mutableMapOf<String, List<TimeChunk>>()
        body["monday"] = listOf()
        body["tuesday"] = listOf()
        body["wednesday"] = listOf()
        body["thursday"] = listOf()
        body["friday"] = listOf(
            TimeChunk(TimeChunkType.OPEN, 0),
            TimeChunk(TimeChunkType.CLOSE, 60)
        )
        body["saturday"] = listOf()
        body["sunday"] = listOf()

        val response = request(body)

        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(
            response.body, equalTo(
                """
            Monday: Closed
            Tuesday: Closed
            Wednesday: Closed
            Thursday: Closed
            Friday: 12 AM - 12:01 AM
            Saturday: Closed
            Sunday: Closed
        """.trimIndent()
            )
        )
    }

    @Test
    fun `check when various case`() {
        val body = """
            {"MondAy": [],"tuesday": [],"Wednesday": [],"thursday": [],
            "friday": [{"typE": "close","value": 60},{"type": "Open","Value": 0}],"saturday": [],"sunday": []}
        """.trimIndent()

        val response = request(body)

        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(
            response.body, equalTo(
                """
            Monday: Closed
            Tuesday: Closed
            Wednesday: Closed
            Thursday: Closed
            Friday: 12 AM - 12:01 AM
            Saturday: Closed
            Sunday: Closed
        """.trimIndent()
            )
        )
    }

    @Test
    fun `check bad request on missing day`() {
        val body = mutableMapOf<String, List<TimeChunk>>()
        body["monday"] = listOf()
        body["tuesday"] = listOf()
        body["wednesday"] = listOf()
        body["thursday"] = listOf()
        body["saturday"] = listOf()
        body["sunday"] = listOf()

        assertThat(request(body).statusCode, equalTo(HttpStatus.BAD_REQUEST))
    }

    @Test
    fun `check bad request on invalid day`() {
        val body = mutableMapOf<String, List<TimeChunk>>()
        body["monday"] = listOf()
        body["tuesday"] = listOf()
        body["wednesday"] = listOf()
        body["thursday"] = listOf()
        body["friyay"] = listOf()
        body["saturday"] = listOf()
        body["sunday"] = listOf()

        assertThat(request(body).statusCode, equalTo(HttpStatus.BAD_REQUEST))
    }

    private fun <T> request(body: T): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        return RestTemplateBuilder()
            .errorHandler(object: DefaultResponseErrorHandler() { //avoid HttpClientErrorException in tests
                override fun hasError(statusCode: HttpStatus): Boolean {
                    return false
                }
            })
            .build().postForEntity(
            URI.create("http://localhost:$port/restaurant/schedule/format"),
            HttpEntity(body, headers),
            String::class.java
        )
    }
}