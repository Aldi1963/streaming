package com.sonzaix.streaming.domain.repository

import com.sonzaix.streaming.data.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAll(): Flow<List<FavoriteEntity>>
    fun isFavorite(id: String, provider: String): Flow<Boolean>
    suspend fun add(entity: FavoriteEntity)
    suspend fun remove(id: String, provider: String)
    suspend fun removeAll()
}
