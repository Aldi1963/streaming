package com.sonzaix.streaming.data.repository

import com.sonzaix.streaming.core.database.FavoriteDao
import com.sonzaix.streaming.data.local.FavoriteEntity
import com.sonzaix.streaming.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoriteDao
) : FavoriteRepository {
    override fun getAll(): Flow<List<FavoriteEntity>> = dao.getAll()
    override fun isFavorite(id: String, provider: String): Flow<Boolean> = dao.isFavorite(id, provider)
    override suspend fun add(entity: FavoriteEntity) = dao.insert(entity)
    override suspend fun remove(id: String, provider: String) = dao.delete(id, provider)
    override suspend fun removeAll() = dao.deleteAll()
}
