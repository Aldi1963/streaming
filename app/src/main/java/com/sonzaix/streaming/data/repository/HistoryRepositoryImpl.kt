package com.sonzaix.streaming.data.repository

import com.sonzaix.streaming.core.database.HistoryDao
import com.sonzaix.streaming.data.local.HistoryEntity
import com.sonzaix.streaming.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryDao
) : HistoryRepository {
    override fun getAll(): Flow<List<HistoryEntity>> = dao.getAll()
    override fun getRecent(): Flow<List<HistoryEntity>> = dao.getRecent()
    override suspend fun getEntry(dramaId: String, episodeId: String, provider: String): HistoryEntity? = dao.getEntry(dramaId, episodeId, provider)
    override suspend fun save(entity: HistoryEntity) = dao.insert(entity)
    override suspend fun deleteAll() = dao.deleteAll()
}
