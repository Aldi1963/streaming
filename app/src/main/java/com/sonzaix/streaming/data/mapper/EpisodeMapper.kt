package com.sonzaix.streaming.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sonzaix.streaming.core.utils.JsonUtils.getIntField
import com.sonzaix.streaming.core.utils.JsonUtils.getStringField
import com.sonzaix.streaming.domain.model.EpisodeItem

object EpisodeMapper {

    fun mapList(json: JsonElement, dramaId: String, provider: String): List<EpisodeItem> {
        val array = findEpisodeArray(json) ?: return emptyList()
        return array.mapIndexedNotNull { index, el ->
            if (!el.isJsonObject) return@mapIndexedNotNull null
            mapItem(el.asJsonObject, dramaId, provider, index + 1)
        }
    }

    private fun findEpisodeArray(json: JsonElement): com.google.gson.JsonArray? {
        if (json.isJsonArray) return json.asJsonArray
        if (!json.isJsonObject) return null
        val obj = json.asJsonObject
        for (key in listOf("episodes", "episode_list", "data", "list", "items")) {
            val el = obj.get(key)
            if (el != null && el.isJsonArray) return el.asJsonArray
        }
        val data = obj.get("data")
        if (data != null && data.isJsonObject) {
            val dataObj = data.asJsonObject
            for (key in listOf("episodes", "episode_list", "list", "items")) {
                val el = dataObj.get(key)
                if (el != null && el.isJsonArray) return el.asJsonArray
            }
        }
        return null
    }

    private fun mapItem(obj: JsonObject, dramaId: String, provider: String, fallbackNumber: Int): EpisodeItem {
        val id = obj.getStringField("episode_id", "id", "ep_id") ?: "${dramaId}_$fallbackNumber"
        val number = obj.getIntField("episode_number", "episode", "ep_num", "number") ?: fallbackNumber
        val title = obj.getStringField("title", "name", "episode_name") ?: "Episode $number"
        return EpisodeItem(
            id = id,
            dramaId = dramaId,
            provider = provider,
            title = title,
            episodeNumber = number,
            thumbnail = obj.getStringField("thumb_url", "thumbnail", "cover", "image"),
            streamUrl = obj.getStringField("stream_url", "url", "video_url", "play_url"),
            rawJson = obj.toString()
        )
    }

    fun generateFallbackEpisodes(dramaId: String, provider: String, count: Int): List<EpisodeItem> {
        return (1..count).map { num ->
            EpisodeItem(
                id = "${dramaId}_$num",
                dramaId = dramaId,
                provider = provider,
                title = "Episode $num",
                episodeNumber = num
            )
        }
    }
}
