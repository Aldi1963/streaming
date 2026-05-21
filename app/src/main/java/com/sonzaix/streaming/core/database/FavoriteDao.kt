package com.sonzaix.streaming.core.database

import androidx.room.*
import com.sonzaix.streaming.data.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id AND provider = :provider)")
    fun isFavorite(id: String, provider: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id AND provider = :provider")
    suspend fun delete(id: String, provider: String)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()
}
