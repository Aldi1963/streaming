package com.sonzaix.streaming.data.local

import androidx.room.Entity

@Entity(tableName = "history", primaryKeys = ["dramaId", "episodeId", "provider"])
data class HistoryEntity(
    val id: String = "",
    val dramaId: String,
    val episodeId: String,
    val provider: String,
    val title: String,
    val thumbnail: String? = null,
    val episodeNumber: Int = 0,
    val lastPosition: Long = 0L,
    val duration: Long = 0L,
    val lastWatchedAt: Long = System.currentTimeMillis()
)
