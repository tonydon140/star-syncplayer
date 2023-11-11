package top.tonydon.syncplayer.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

object JSONUtils {
    private val MAPPER: ObjectMapper =  ObjectMapper()

    init {
        MAPPER.registerModule(JavaTimeModule())
    }

    fun <T> parse(json: String?, clazz: Class<T>?): T {
        return try {
            MAPPER.readValue(json, clazz)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    fun toJSONStr(obj: Any?): String {
        return try {
            MAPPER.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }
}
