package com.sonzaix.streaming.data.mapper

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.sonzaix.streaming.core.utils.JsonUtils
import com.sonzaix.streaming.core.utils.JsonUtils.getIntField
import com.sonzaix.streaming.core.utils.JsonUtils.getStringField
import com.sonzaix.streaming.core.utils.JsonUtils.getStringListField
import com.sonzaix.streaming.domain.model.DramaItem

object DramaMapper {

    fun mapList(json: JsonElement, provider: String): List<DramaItem> {
        val array = JsonUtils.findDramaList(json) ?: return emptyList()
        return array.mapNotNull { el ->
            if (!el.isJsonObject) return@mapNotNull null
            mapItem(el.asJsonObject, provider)
        }
    }

    fun mapItem(obj: JsonObject, provider: String): DramaItem? {
        val id = obj.getStringField("drama_id", "id", "book_id", "dramaId", "content_id") ?: return null
        val title = obj.getStringField("drama_name", "title", "name", "book_name") ?: "Untitled"
        return DramaItem(
            id = id,
            provider = provider,
            title = title,
            description = obj.getStringField("description", "desc", "synopsis", "summary"),
            thumbnail = obj.getStringField("thumb_url", "thumbnail", "poster", "cover", "image", "img"),
            banner = obj.getStringField("banner", "banner_url"),
            episodeCount = obj.getIntField("episode_count", "total_episode", "episodes_count", "totalEpisodes"),
            watchCount = obj.getStringField("watch_value", "views", "watch_count", "play_count"),
            language = obj.getStringField("language", "lang"),
            tags = obj.getStringListField("tags", "genres", "categories"),
            rawJson = obj.toString()
        )
    }
}
