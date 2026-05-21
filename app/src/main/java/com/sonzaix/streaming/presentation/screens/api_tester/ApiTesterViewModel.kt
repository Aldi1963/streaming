package com.sonzaix.streaming.presentation.screens.api_tester

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QueryParam(val key: String, val value: String)

data class ApiTesterUiState(
    val provider: String = "melolo",
    val endpoint: String = "home",
    val queryParams: List<QueryParam> = listOf(QueryParam("lang", "id")),
    val isLoading: Boolean = false,
    val responseBody: String? = null,
    val responseCode: Int? = null,
    val latencyMs: Long? = null,
    val error: String? = null,
    val requestUrl: String? = null
)

@HiltViewModel
class ApiTesterViewModel @Inject constructor(
    private val dramaRepository: DramaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiTesterUiState())
    val uiState: StateFlow<ApiTesterUiState> = _uiState.asStateFlow()

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun setProvider(provider: String) {
        _uiState.update { it.copy(provider = provider) }
    }

    fun setEndpoint(endpoint: String) {
        _uiState.update { it.copy(endpoint = endpoint) }
    }

    fun addQueryParam(key: String, value: String) {
        _uiState.update { state ->
            val updated = state.queryParams.toMutableList().apply { add(QueryParam(key, value)) }
            state.copy(queryParams = updated)
        }
    }

    fun removeQueryParam(index: Int) {
        _uiState.update { state ->
            val updated = state.queryParams.toMutableList().apply { removeAt(index) }
            state.copy(queryParams = updated)
        }
    }

    fun updateQueryParam(index: Int, key: String, value: String) {
        _uiState.update { state ->
            val updated = state.queryParams.toMutableList().apply {
                this[index] = QueryParam(key, value)
            }
            state.copy(queryParams = updated)
        }
    }

    fun sendRequest() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, responseBody = null, responseCode = null, latencyMs = null, error = null, requestUrl = null) }

        val queryMap = state.queryParams.associate { it.key to it.value }
        
        // Construct visual request URL for information
        val queryStr = state.queryParams.joinToString("&") { "${it.key}=${it.value}" }
        val constructedUrl = "https://api.sonzaix.indevs.in/${state.provider}/${state.endpoint}" + 
                if (queryStr.isNotEmpty()) "?$queryStr" else ""

        _uiState.update { it.copy(requestUrl = constructedUrl) }

        val startTime = System.currentTimeMillis()

        viewModelScope.launch {
            val response = dramaRepository.getGeneric(state.provider, state.endpoint, queryMap)
            val endTime = System.currentTimeMillis()
            val latency = endTime - startTime

            when (response) {
                is NetworkResult.Success -> {
                    val prettyJson = try {
                        gson.toJson(response.data)
                    } catch (e: Exception) {
                        response.data.toString()
                    }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            responseBody = prettyJson,
                            responseCode = 200,
                            latencyMs = latency
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = response.message,
                            responseCode = response.code ?: 400,
                            latencyMs = latency
                        )
                    }
                }
                else -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Gagal memuat data",
                            latencyMs = latency
                        )
                    }
                }
            }
        }
    }
}
