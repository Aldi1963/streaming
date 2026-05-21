package com.sonzaix.streaming.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.data.local.HistoryEntity
import com.sonzaix.streaming.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val historyItems: List<HistoryEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            historyRepository.getAll()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { list ->
                    _uiState.update { it.copy(historyItems = list, isLoading = false) }
                }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}
