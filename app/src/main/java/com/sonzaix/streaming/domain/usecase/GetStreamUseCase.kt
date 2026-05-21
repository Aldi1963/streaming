package com.sonzaix.streaming.domain.usecase

import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.StreamSource
import com.sonzaix.streaming.domain.repository.DramaRepository
import javax.inject.Inject

class GetStreamUseCase @Inject constructor(private val repo: DramaRepository) {
    suspend operator fun invoke(provider: String, dramaId: String, episodeId: String, episodeNumber: Int): NetworkResult<StreamSource> =
        repo.getStream(provider, dramaId, episodeId, episodeNumber)
}
