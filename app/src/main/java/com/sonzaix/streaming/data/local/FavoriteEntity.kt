package com.sonzaix.streaming.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", primaryKeys = ["id", "provider"])
data class FavoriteEntity(
    val id: String,
    val provider: String,
    val title: String,
    val description: String? = null,
    val thumbnail: String? = null,
    val episodeCount: Int? = null,
    val watchCount: String? = null,
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
