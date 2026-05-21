package com.sonzaix.streaming.data.mapper

import com.google.gson.JsonElement
import com.sonzaix.streaming.core.utils.JsonUtils.getStringField
import com.sonzaix.streaming.domain.model.StreamSource

object StreamMapper {

    fun map(json: JsonElement): StreamSource? {
        if (!json.isJsonObject) return tryFromString(json)
        val obj = json.asJsonObject

        // Try nested data object
        val data = obj.get("data")
        val target = if (data != null && data.isJsonObject) data.asJsonObject else obj

        val url = target.getStringField("stream_url", "url", "video_url", "play_url", "source", "src", "hls", "mp4")
            ?: return null

        val headers = mutableMapOf<String, String>()
        val headersEl = target.get("headers")
        if (headersEl != null && headersEl.isJsonObject) {
            headersEl.asJsonObject.entrySet().forEach { (k, v) ->
                try { headers[k] = v.asString } catch (_: Exception) {}
            }
        }

        return StreamSource(
            url = url,
            quality = target.getStringField("quality", "resolution"),
            type = target.getStringField("type", "format"),
            headers = headers.ifEmpty { null }
        )
    }

    private fun tryFromString(json: JsonElement): StreamSource? {
        return try {
            val str = json.asString
            if (str.startsWith("http")) StreamSource(url = str) else null
        } catch (_: Exception) { null }
    }
}
