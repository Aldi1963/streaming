package com.sonzaix.streaming.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.network.ConnectivityObserver
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.usecase.CheckApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashUiState {
    object Loading : SplashUiState
    object Success : SplashUiState
    data class Error(val message: String) : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkApiUseCase: CheckApiUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkHealth()
    }

    fun checkHealth() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading
            connectivityObserver.isConnected.first().let { connected ->
                if (!connected) {
                    _uiState.value = SplashUiState.Error("Tidak ada koneksi internet.")
                    return@launch
                }
            }

            when (val result = checkApiUseCase()) {
                is NetworkResult.Success -> {
                    _uiState.value = SplashUiState.Success
                }
                is NetworkResult.Error -> {
                    _uiState.value = SplashUiState.Error("Server sedang tidak tersedia.")
                }
                is NetworkResult.Loading -> {
                    _uiState.value = SplashUiState.Loading
                }
            }
        }
    }
}
