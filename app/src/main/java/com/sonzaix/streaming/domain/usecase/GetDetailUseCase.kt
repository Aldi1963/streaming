package com.sonzaix.streaming.domain.usecase

import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.model.EpisodeItem
import com.sonzaix.streaming.domain.repository.DramaRepository
import javax.inject.Inject

class GetDetailUseCase @Inject constructor(private val repo: DramaRepository) {
    suspend operator fun invoke(provider: String, dramaId: String): NetworkResult<Pair<DramaItem, List<EpisodeItem>>> =
        repo.getDetail(provider, dramaId)
}
