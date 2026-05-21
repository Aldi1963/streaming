package com.sonzaix.streaming.domain.usecase

import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.repository.DramaRepository
import javax.inject.Inject

class CheckApiUseCase @Inject constructor(private val repo: DramaRepository) {
    suspend operator fun invoke(): NetworkResult<Boolean> = repo.checkApiStatus()
}
