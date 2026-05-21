package com.sonzaix.streaming.data.repository

import com.google.gson.JsonElement
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.core.network.SonzaiApiService
import com.sonzaix.streaming.data.mapper.DramaMapper
import com.sonzaix.streaming.data.mapper.EpisodeMapper
import com.sonzaix.streaming.data.mapper.StreamMapper
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.model.EpisodeItem
import com.sonzaix.streaming.domain.model.StreamSource
import com.sonzaix.streaming.domain.repository.DramaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DramaRepositoryImpl @Inject constructor(
    private val api: SonzaiApiService
) : DramaRepository {

    override suspend fun checkApiStatus(): NetworkResult<Boolean> = safeCall {
        val response = api.checkApi()
        if (response.isSuccessful) NetworkResult.Success(true)
        else NetworkResult.Error("Server tidak tersedia", response.code())
    }

    override suspend fun getHome(provider: String, lang: String): NetworkResult<List<DramaItem>> = safeCall {
        val response = api.getHome(provider, mapOf("lang" to lang))
        mapDramaResponse(response, provider)
    }

    override suspend fun getLatest(provider: String, page: Int, lang: String): NetworkResult<List<DramaItem>> = safeCall {
        val response = api.getNew(provider, mapOf("page" to page.toString(), "lang" to lang))
        mapDramaResponse(response, provider)
    }

    override suspend fun getPopular(provider: String, page: Int, lang: String): NetworkResult<List<DramaItem>> = safeCall {
        val response = api.getPopular(provider, mapOf("page" to page.toString(), "lang" to lang))
        mapDramaResponse(response, provider)
    }

    override suspend fun searchDrama(provider: String, query: String, page: Int, lang: String): NetworkResult<List<DramaItem>> = safeCall {
        val queries = mapOf(
            "q" to query,
            "query" to query,
            "page" to page.toString(),
            "lang" to lang
        )
        val response = api.search(provider, queries)
        mapDramaResponse(response, provider)
    }

    override suspend fun getDetail(provider: String, dramaId: String): NetworkResult<Pair<DramaItem, List<EpisodeItem>>> = safeCall {
        val paramSets = listOf(
            mapOf("id" to dramaId),
            mapOf("drama_id" to dramaId),
            mapOf("book_id" to dramaId),
            mapOf("dramaId" to dramaId)
        )
        for (params in paramSets) {
            try {
                val response = api.getDetail(provider, params)
                if (response.isSuccessful && response.body() != null) {
                    val json = response.body()!!
                    val dramas = DramaMapper.mapList(json, provider)
                    val drama = dramas.firstOrNull() ?: DramaMapper.mapItem(
                        if (json.isJsonObject) json.asJsonObject else continue, provider
                    ) ?: continue

                    val episodes = EpisodeMapper.mapList(json, dramaId, provider).ifEmpty {
                        drama.episodeCount?.let { EpisodeMapper.generateFallbackEpisodes(dramaId, provider, it) } ?: emptyList()
                    }
                    return@safeCall NetworkResult.Success(Pair(drama, episodes))
                }
            } catch (_: Exception) { continue }
        }
        NetworkResult.Error("Detail tidak ditemukan")
    }

    override suspend fun getStream(provider: String, dramaId: String, episodeId: String, episodeNumber: Int): NetworkResult<StreamSource> = safeCall {
        val paramSets = mutableListOf(
            mapOf("id" to episodeId),
            mapOf("episode_id" to episodeId),
            mapOf("drama_id" to dramaId, "episode" to episodeNumber.toString()),
            mapOf("drama_id" to dramaId, "episode_id" to episodeId)
        )
        if (provider == "dramanova") {
            try {
                val r = api.dramanovaPlay(mapOf("id" to episodeId))
                if (r.isSuccessful && r.body() != null) {
                    StreamMapper.map(r.body()!!)?.let { return@safeCall NetworkResult.Success(it) }
                }
            } catch (_: Exception) {}
        }
        for (params in paramSets) {
            try {
                val response = api.getStream(provider, params)
                if (response.isSuccessful && response.body() != null) {
                    StreamMapper.map(response.body()!!)?.let { return@safeCall NetworkResult.Success(it) }
                }
            } catch (_: Exception) { continue }
        }
        NetworkResult.Error("Link video tidak tersedia atau gagal dimuat.")
    }

    override suspend fun getGeneric(provider: String, endpoint: String, queries: Map<String, String>): NetworkResult<JsonElement> = safeCall {
        val response = api.getGeneric(provider, endpoint, queries)
        if (response.isSuccessful && response.body() != null) NetworkResult.Success(response.body()!!)
        else NetworkResult.Error("Request gagal", response.code())
    }

    private fun mapDramaResponse(response: retrofit2.Response<JsonElement>, provider: String): NetworkResult<List<DramaItem>> {
        if (!response.isSuccessful) return NetworkResult.Error("Server error", response.code())
        val body = response.body() ?: return NetworkResult.Error("Data kosong")
        val list = DramaMapper.mapList(body, provider)
        return NetworkResult.Success(list)
    }

    private suspend fun <T> safeCall(block: suspend () -> NetworkResult<T>): NetworkResult<T> {
        return try {
            block()
        } catch (e: java.net.SocketTimeoutException) {
            NetworkResult.Error("Koneksi terlalu lama, coba lagi.")
        } catch (e: java.net.UnknownHostException) {
            NetworkResult.Error("Tidak ada koneksi internet.")
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Terjadi kesalahan")
        }
    }
}
