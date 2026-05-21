package com.sonzaix.streaming.domain.repository

import com.google.gson.JsonElement
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.model.EpisodeItem
import com.sonzaix.streaming.domain.model.StreamSource

interface DramaRepository {
    suspend fun checkApiStatus(): NetworkResult<Boolean>
    suspend fun getHome(provider: String, lang: String = "id"): NetworkResult<List<DramaItem>>
    suspend fun getLatest(provider: String, page: Int = 1, lang: String = "id"): NetworkResult<List<DramaItem>>
    suspend fun getPopular(provider: String, page: Int = 1, lang: String = "id"): NetworkResult<List<DramaItem>>
    suspend fun searchDrama(provider: String, query: String, page: Int = 1, lang: String = "id"): NetworkResult<List<DramaItem>>
    suspend fun getDetail(provider: String, dramaId: String): NetworkResult<Pair<DramaItem, List<EpisodeItem>>>
    suspend fun getStream(provider: String, dramaId: String, episodeId: String, episodeNumber: Int): NetworkResult<StreamSource>
    suspend fun getGeneric(provider: String, endpoint: String, queries: Map<String, String> = emptyMap()): NetworkResult<JsonElement>
}
