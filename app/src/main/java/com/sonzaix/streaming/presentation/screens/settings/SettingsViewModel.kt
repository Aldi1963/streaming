package com.sonzaix.streaming.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.datastore.SettingsDataStore
import com.sonzaix.streaming.domain.repository.FavoriteRepository
import com.sonzaix.streaming.domain.repository.HistoryRepository
import com.sonzaix.streaming.domain.repository.DramaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val provider: String = "melolo",
    val language: String = "id",
    val themeMode: String = "dark",
    val apiHealthy: Boolean? = null,
    val isCheckingApi: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository,
    private val dramaRepository: DramaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsDataStore.provider,
                settingsDataStore.language,
                settingsDataStore.themeMode
            ) { provider, language, themeMode ->
                SettingsUiState(
                    provider = provider,
                    language = language,
                    themeMode = themeMode
                )
            }.collect { state ->
                _uiState.update { 
                    state.copy(
                        apiHealthy = it.apiHealthy,
                        isCheckingApi = it.isCheckingApi
                    )
                }
            }
        }
        checkApiStatus()
    }

    fun setProvider(provider: String) {
        viewModelScope.launch {
            settingsDataStore.setProvider(provider)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(language)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.setThemeMode(mode)
        }
    }

    fun checkApiStatus() {
        _uiState.update { it.copy(isCheckingApi = true) }
        viewModelScope.launch {
            val result = dramaRepository.checkApiStatus()
            val isHealthy = result is com.sonzaix.streaming.core.network.NetworkResult.Success
            _uiState.update { it.copy(isCheckingApi = false, apiHealthy = isHealthy) }
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            favoriteRepository.removeAll()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}
