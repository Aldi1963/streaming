package com.sonzaix.streaming.core.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

object JsonUtils {

    fun JsonElement?.asStringOrNull(): String? = try { this?.asString } catch (_: Exception) { null }

    fun JsonElement?.asIntOrNull(): Int? = try { this?.asInt } catch (_: Exception) { null }

    fun JsonObject.getStringField(vararg keys: String): String? {
        for (key in keys) {
            val value = get(key)?.asStringOrNull()
            if (!value.isNullOrBlank()) return value
        }
        return null
    }

    fun JsonObject.getIntField(vararg keys: String): Int? {
        for (key in keys) {
            val el = get(key) ?: continue
            el.asIntOrNull()?.let { return it }
            el.asStringOrNull()?.toIntOrNull()?.let { return it }
        }
        return null
    }

    fun JsonObject.getStringListField(vararg keys: String): List<String> {
        for (key in keys) {
            val el = get(key) ?: continue
            if (el.isJsonArray) {
                return el.asJsonArray.mapNotNull { it.asStringOrNull() }
            }
        }
        return emptyList()
    }

    fun findDramaList(json: JsonElement): JsonArray? {
        if (json.isJsonArray) return json.asJsonArray
        if (!json.isJsonObject) return null
        val obj = json.asJsonObject

        // Try data[0].books, data.books, data.items, data.list, data, results, books, items, list
        val dataEl = obj.get("data")
        if (dataEl != null) {
            if (dataEl.isJsonArray) {
                val arr = dataEl.asJsonArray
                if (arr.size() > 0 && arr[0].isJsonObject) {
                    val first = arr[0].asJsonObject
                    for (key in listOf("books", "items", "list")) {
                        val inner = first.get(key)
                        if (inner != null && inner.isJsonArray) return inner.asJsonArray
                    }
                }
                // data itself is array of dramas
                return arr
            }
            if (dataEl.isJsonObject) {
                val dataObj = dataEl.asJsonObject
                for (key in listOf("books", "items", "list")) {
                    val inner = dataObj.get(key)
                    if (inner != null && inner.isJsonArray) return inner.asJsonArray
                }
            }
        }

        for (key in listOf("results", "books", "items", "list")) {
            val el = obj.get(key)
            if (el != null && el.isJsonArray) return el.asJsonArray
        }
        return null
    }
}
