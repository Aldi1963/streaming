package com.sonzaix.streaming.core.database

import androidx.room.*
import com.sonzaix.streaming.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY lastWatchedAt DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history ORDER BY lastWatchedAt DESC LIMIT 10")
    fun getRecent(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE dramaId = :dramaId AND episodeId = :episodeId AND provider = :provider LIMIT 1")
    suspend fun getEntry(dramaId: String, episodeId: String, provider: String): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
