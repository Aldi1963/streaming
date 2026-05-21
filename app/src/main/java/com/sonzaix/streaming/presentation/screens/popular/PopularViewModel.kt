package com.sonzaix.streaming.presentation.screens.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.datastore.SettingsDataStore
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.usecase.GetPopularUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PopularUiState(
    val isLoading: Boolean = false,
    val dramas: List<DramaItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val getPopularUseCase: GetPopularUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularUiState())
    val uiState: StateFlow<PopularUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsDataStore.provider,
                settingsDataStore.language
            ) { provider, lang ->
                Pair(provider, lang)
            }.collect { (provider, lang) ->
                loadPopularDramas(provider, lang)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val provider = settingsDataStore.provider.first()
            val lang = settingsDataStore.language.first()
            loadPopularDramas(provider, lang)
        }
    }

    private suspend fun loadPopularDramas(provider: String, lang: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        when (val result = getPopularUseCase(provider, 1, lang)) {
            is NetworkResult.Success -> {
                _uiState.update { it.copy(isLoading = false, dramas = result.data) }
            }
            is NetworkResult.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
            else -> {}
        }
    }
}
