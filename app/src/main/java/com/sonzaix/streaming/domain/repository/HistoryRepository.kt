package com.sonzaix.streaming.domain.repository

import com.sonzaix.streaming.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAll(): Flow<List<HistoryEntity>>
    fun getRecent(): Flow<List<HistoryEntity>>
    suspend fun getEntry(dramaId: String, episodeId: String, provider: String): HistoryEntity?
    suspend fun save(entity: HistoryEntity)
    suspend fun deleteAll()
}
