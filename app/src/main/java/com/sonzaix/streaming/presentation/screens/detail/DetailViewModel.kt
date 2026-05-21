package com.sonzaix.streaming.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.data.local.FavoriteEntity
import com.sonzaix.streaming.data.local.HistoryEntity
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.model.EpisodeItem
import com.sonzaix.streaming.domain.usecase.GetDetailUseCase
import com.sonzaix.streaming.domain.repository.FavoriteRepository
import com.sonzaix.streaming.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = false,
    val drama: DramaItem? = null,
    val episodes: List<EpisodeItem> = emptyList(),
    val isFavorite: Boolean = false,
    val lastWatchedEpisode: HistoryEntity? = null,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetailUseCase: GetDetailUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val provider: String = savedStateHandle.get<String>("provider") ?: ""
    val dramaId: String = savedStateHandle.get<String>("dramaId") ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        observeFavorite()
        observeHistory()
    }

    fun loadDetail() {
        if (provider.isBlank() || dramaId.isBlank()) {
            _uiState.update { it.copy(error = "Parameter tidak valid") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = getDetailUseCase(provider, dramaId)) {
                is NetworkResult.Success -> {
                    val (drama, episodes) = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            drama = drama,
                            episodes = episodes
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            favoriteRepository.isFavorite(dramaId, provider).collect { isFav ->
                _uiState.update { it.copy(isFavorite = isFav) }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            historyRepository.getAll().collect { historyList ->
                val matchingHistory = historyList.find { it.dramaId == dramaId && it.provider == provider }
                _uiState.update { it.copy(lastWatchedEpisode = matchingHistory) }
            }
        }
    }

    fun toggleFavorite() {
        val drama = _uiState.value.drama ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoriteRepository.remove(dramaId, provider)
            } else {
                val tagsString = drama.tags.joinToString(",")
                val favoriteEntity = FavoriteEntity(
                    id = drama.id,
                    provider = drama.provider,
                    title = drama.title,
                    description = drama.description,
                    thumbnail = drama.thumbnail,
                    episodeCount = drama.episodeCount,
                    watchCount = drama.watchCount,
                    tags = tagsString,
                    createdAt = System.currentTimeMillis()
                )
                favoriteRepository.add(favoriteEntity)
            }
        }
    }
}
