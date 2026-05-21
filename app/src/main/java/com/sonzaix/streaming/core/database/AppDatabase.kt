package com.sonzaix.streaming.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sonzaix.streaming.data.local.FavoriteEntity
import com.sonzaix.streaming.data.local.HistoryEntity

@Database(entities = [FavoriteEntity::class, HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao
}
