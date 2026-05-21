package com.sonzaix.streaming.domain.usecase

import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.repository.DramaRepository
import javax.inject.Inject

class GetHomeUseCase @Inject constructor(private val repo: DramaRepository) {
    suspend operator fun invoke(provider: String, lang: String = "id"): NetworkResult<List<DramaItem>> =
        repo.getHome(provider, lang)
}
