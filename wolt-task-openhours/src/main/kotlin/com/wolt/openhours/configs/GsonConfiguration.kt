package com.wolt.openhours.configs

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.wolt.openhours.exceptions.ValidationException
import com.wolt.openhours.models.TimeChunk
import com.wolt.openhours.models.TimeChunkType
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Type
import java.time.DayOfWeek

@Configuration
class GsonConfiguration {

    @Bean
    fun typeAdapterRegistration(): GsonBuilderCustomizer = GsonBuilderCustomizer { builder: GsonBuilder ->
        builder.registerTypeAdapter(DayOfWeek::class.java, DayOfWeekDeserializer())
        builder.registerTypeAdapter(TimeChunk::class.java, TimeChunkDeserializer())
    }

    class DayOfWeekDeserializer : JsonDeserializer<DayOfWeek> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DayOfWeek {
            val dayOfWeek = json?.asString?.uppercase()
            if (DayOfWeek.values().none { it.name == dayOfWeek }) throw ValidationException("Invalid day name: $dayOfWeek")
            return DayOfWeek.valueOf(dayOfWeek!!)
        }
    }

    class TimeChunkDeserializer : JsonDeserializer<TimeChunk> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): TimeChunk {
            val jsonTimeChunkMap = json.asJsonObject.entrySet().associate { it.key.lowercase() to it.value }

            if (!jsonTimeChunkMap.containsKey("type")) throw ValidationException("Invalid time chunk: missing type")
            val type = jsonTimeChunkMap["type"]?.asString?.uppercase()
            if (TimeChunkType.values().none { it.name == type }) throw ValidationException("Invalid time chunk type: ${type}")

            if (!jsonTimeChunkMap.containsKey("value")) throw ValidationException("Invalid time chunk: missing value")
            val value = jsonTimeChunkMap["value"]?.asInt
            if (value!! < 0) throw ValidationException("Invalid value $value")

            return TimeChunk(TimeChunkType.valueOf(type!!), value)
        }
    }
}