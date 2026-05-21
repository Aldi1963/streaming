package com.sonzaix.streaming.domain.usecase

import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.repository.DramaRepository
import javax.inject.Inject

class GetPopularUseCase @Inject constructor(private val repo: DramaRepository) {
    suspend operator fun invoke(provider: String, page: Int = 1, lang: String = "id"): NetworkResult<List<DramaItem>> =
        repo.getPopular(provider, page, lang)
}
