package com.sonzaix.streaming.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.datastore.SettingsDataStore
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.domain.usecase.SearchDramaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val dramas: List<DramaItem> = emptyList(),
    val isLoading: Boolean = false,
    val isMoreLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val endReached: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchDramaUseCase: SearchDramaUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .collect { q ->
                    if (q.isBlank()) {
                        _uiState.update { it.copy(query = q, dramas = emptyList(), error = null, endReached = true) }
                    } else {
                        _uiState.update { it.copy(query = q, page = 1, endReached = false, dramas = emptyList()) }
                        search(q, 1)
                    }
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        _queryFlow.value = newQuery
    }

    fun searchNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isMoreLoading || currentState.endReached || currentState.query.isBlank()) return

        val nextPage = currentState.page + 1
        _uiState.update { it.copy(isMoreLoading = true) }

        viewModelScope.launch {
            val provider = settingsDataStore.provider.first()
            val lang = settingsDataStore.language.first()

            when (val result = searchDramaUseCase(provider, currentState.query, nextPage, lang)) {
                is NetworkResult.Success -> {
                    val newDramas = result.data
                    if (newDramas.isEmpty()) {
                        _uiState.update { it.copy(isMoreLoading = false, endReached = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isMoreLoading = false,
                                dramas = currentState.dramas + newDramas,
                                page = nextPage
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isMoreLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun retry() {
        val q = _uiState.value.query
        val page = _uiState.value.page
        if (q.isNotBlank()) {
            search(q, page)
        }
    }

    private fun search(q: String, page: Int) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val provider = settingsDataStore.provider.first()
            val lang = settingsDataStore.language.first()

            when (val result = searchDramaUseCase(provider, q, page, lang)) {
                is NetworkResult.Success -> {
                    val list = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dramas = list,
                            page = page,
                            endReached = list.isEmpty()
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
}
