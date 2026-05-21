package com.sonzaix.streaming.presentation.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.data.local.HistoryEntity
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.model.EpisodeItem
import com.sonzaix.streaming.domain.model.StreamSource
import com.sonzaix.streaming.domain.usecase.GetDetailUseCase
import com.sonzaix.streaming.domain.usecase.GetStreamUseCase
import com.sonzaix.streaming.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isLoading: Boolean = false,
    val streamSource: StreamSource? = null,
    val drama: DramaItem? = null,
    val episodes: List<EpisodeItem> = emptyList(),
    val currentEpisodeId: String = "",
    val currentEpisodeNumber: Int = 1,
    val initialPosition: Long = 0L,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getStreamUseCase: GetStreamUseCase,
    private val getDetailUseCase: GetDetailUseCase,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val provider: String = savedStateHandle.get<String>("provider") ?: ""
    val dramaId: String = savedStateHandle.get<String>("dramaId") ?: ""
    val initialEpisodeId: String = savedStateHandle.get<String>("episodeId") ?: ""
    val initialEpisodeNumber: Int = savedStateHandle.get<Int>("episodeNumber") ?: 1

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            currentEpisodeId = initialEpisodeId,
            currentEpisodeNumber = initialEpisodeNumber
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        loadDramaAndStream()
    }

    fun loadDramaAndStream() {
        val currentEpId = _uiState.value.currentEpisodeId
        val currentEpNum = _uiState.value.currentEpisodeNumber

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val history = historyRepository.getEntry(dramaId, currentEpId, provider)
            val startPos = history?.lastPosition ?: 0L

            _uiState.update { it.copy(initialPosition = startPos) }

            val streamResult = getStreamUseCase(provider, dramaId, currentEpId, currentEpNum)
            val detailResult = if (_uiState.value.drama == null) {
                getDetailUseCase(provider, dramaId)
            } else {
                NetworkResult.Success(Pair(_uiState.value.drama!!, _uiState.value.episodes))
            }

            var stream: StreamSource? = null
            var errorMsg: String? = null

            when (streamResult) {
                is NetworkResult.Success -> {
                    stream = streamResult.data
                }
                is NetworkResult.Error -> {
                    errorMsg = streamResult.message
                }
                else -> {}
            }

            if (detailResult is NetworkResult.Success) {
                val (drama, episodes) = detailResult.data
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        drama = drama,
                        episodes = episodes,
                        streamSource = stream,
                        error = errorMsg
                    )
                }
            } else if (detailResult is NetworkResult.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        streamSource = stream,
                        error = errorMsg ?: detailResult.message
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        streamSource = stream,
                        error = errorMsg
                    )
                }
            }
        }
    }

    fun saveWatchProgress(position: Long, duration: Long) {
        val drama = _uiState.value.drama ?: return
        val currentEpId = _uiState.value.currentEpisodeId
        val currentEpNum = _uiState.value.currentEpisodeNumber

        viewModelScope.launch {
            val entity = HistoryEntity(
                id = "${provider}_${dramaId}_${currentEpId}",
                dramaId = dramaId,
                episodeId = currentEpId,
                provider = provider,
                title = drama.title,
                thumbnail = drama.thumbnail,
                episodeNumber = currentEpNum,
                lastPosition = position,
                duration = duration,
                lastWatchedAt = System.currentTimeMillis()
            )
            historyRepository.save(entity)
        }
    }

    fun playNextEpisode(): Boolean {
        val episodes = _uiState.value.episodes
        val currentNum = _uiState.value.currentEpisodeNumber
        val nextEpisode = episodes.find { it.episodeNumber == currentNum + 1 }

        if (nextEpisode != null) {
            _uiState.update {
                it.copy(
                    currentEpisodeId = nextEpisode.id,
                    currentEpisodeNumber = nextEpisode.episodeNumber,
                    initialPosition = 0L,
                    streamSource = null
                )
            }
            loadDramaAndStream()
            return true
        }
        return false
    }

    fun selectEpisode(episode: EpisodeItem) {
        _uiState.update {
            it.copy(
                currentEpisodeId = episode.id,
                currentEpisodeNumber = episode.episodeNumber,
                initialPosition = 0L,
                streamSource = null
            )
        }
        loadDramaAndStream()
    }
}
