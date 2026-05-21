package com.sonzaix.streaming.presentation.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.data.local.FavoriteEntity
import com.sonzaix.streaming.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUiState(
    val favorites: List<FavoriteEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState(isLoading = true))
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteRepository.getAll()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { list ->
                    _uiState.update { it.copy(favorites = list, isLoading = false) }
                }
        }
    }

    fun removeFavorite(id: String, provider: String) {
        viewModelScope.launch {
            favoriteRepository.remove(id, provider)
        }
    }
}
