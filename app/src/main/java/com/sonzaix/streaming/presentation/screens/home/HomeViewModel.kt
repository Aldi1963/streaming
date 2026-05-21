package com.sonzaix.streaming.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.datastore.SettingsDataStore
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.data.local.HistoryEntity
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.model.Provider
import com.sonzaix.streaming.domain.usecase.GetHomeUseCase
import com.sonzaix.streaming.domain.usecase.GetLatestUseCase
import com.sonzaix.streaming.domain.usecase.GetPopularUseCase
import com.sonzaix.streaming.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val selectedProvider: Provider = Provider.MELOLO,
    val isLoading: Boolean = false,
    val heroDrama: DramaItem? = null,
    val latestDramas: List<DramaItem> = emptyList(),
    val popularDramas: List<DramaItem> = emptyList(),
    val historyList: List<HistoryEntity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val getHomeUseCase: GetHomeUseCase,
    private val getLatestUseCase: GetLatestUseCase,
    private val getPopularUseCase: GetPopularUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsDataStore.provider,
                settingsDataStore.language,
                historyRepository.getRecent()
            ) { providerId, lang, history ->
                Triple(Provider.fromId(providerId), lang, history)
            }.collect { (provider, lang, history) ->
                _uiState.update { it.copy(selectedProvider = provider, historyList = history) }
                loadData(provider, lang)
            }
        }
    }

    fun selectProvider(provider: Provider) {
        viewModelScope.launch {
            settingsDataStore.setProvider(provider.id)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val provider = _uiState.value.selectedProvider
            val lang = settingsDataStore.language.first()
            loadData(provider, lang)
        }
    }

    private suspend fun loadData(provider: Provider, lang: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            val homeResult = getHomeUseCase(provider.id, lang)
            val latestResult = getLatestUseCase(provider.id, 1, lang)
            val popularResult = getPopularUseCase(provider.id, 1, lang)

            var hero: DramaItem? = null
            var latest = emptyList<DramaItem>()
            var popular = emptyList<DramaItem>()
            var errorMsg: String? = null

            when (homeResult) {
                is NetworkResult.Success -> {
                    val list = homeResult.data
                    if (list.isNotEmpty()) {
                        hero = list.first()
                        latest = list
                    }
                }
                is NetworkResult.Error -> {
                    errorMsg = homeResult.message
                }
                else -> {}
            }

            when (latestResult) {
                is NetworkResult.Success -> {
                    val list = latestResult.data
                    if (list.isNotEmpty()) {
                        if (latest.isEmpty()) latest = list
                        if (hero == null) hero = list.first()
                    }
                }
                is NetworkResult.Error -> {
                    if (errorMsg == null) errorMsg = latestResult.message
                }
                else -> {}
            }

            when (popularResult) {
                is NetworkResult.Success -> {
                    popular = popularResult.data
                }
                is NetworkResult.Error -> {
                    if (errorMsg == null) errorMsg = popularResult.message
                }
                else -> {}
            }

            if (latest.isNotEmpty() || popular.isNotEmpty()) {
                errorMsg = null
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    heroDrama = hero,
                    latestDramas = latest,
                    popularDramas = popular,
                    error = errorMsg
                )
            }

        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message ?: "Terjadi kesalahan") }
        }
    }
}
