package com.sonzaix.streaming.data.repository

import com.sonzaix.streaming.domain.repository.DramaRepository
import com.sonzaix.streaming.domain.repository.FavoriteRepository
import com.sonzaix.streaming.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindDramaRepo(impl: DramaRepositoryImpl): DramaRepository
    @Binds @Singleton abstract fun bindFavoriteRepo(impl: FavoriteRepositoryImpl): FavoriteRepository
    @Binds @Singleton abstract fun bindHistoryRepo(impl: HistoryRepositoryImpl): HistoryRepository
}
