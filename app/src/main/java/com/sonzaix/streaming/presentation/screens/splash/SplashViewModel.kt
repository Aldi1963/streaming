package com.sonzaix.streaming.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonzaix.streaming.core.network.ConnectivityObserver
import com.sonzaix.streaming.core.network.NetworkResult
import com.sonzaix.streaming.domain.usecase.CheckApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private var retryCount = 0
    private val maxRetries = 3

    init {
        checkHealth()
    }

    fun checkHealth() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading

            // Beri waktu sebentar agar network siap (fix race condition di Android 15)
            delay(500)

            val isConnected = connectivityObserver.isConnected
                .first()

            if (!isConnected) {
                // Coba tunggu sebentar, kadang Android 15 lambat report network ready
                delay(1500)
                val retryConnected = connectivityObserver.isConnected.first()
                if (!retryConnected) {
                    _uiState.value = SplashUiState.Error("Tidak ada koneksi internet.")
                    return@launch
                }
            }

            when (val result = checkApiUseCase()) {
                is NetworkResult.Success -> {
                    retryCount = 0
                    _uiState.value = SplashUiState.Success
                }
                is NetworkResult.Error -> {
                    if (retryCount < maxRetries) {
                        retryCount++
                        delay(2000L * retryCount)
                        checkHealth()
                    } else {
                        retryCount = 0
                        _uiState.value = SplashUiState.Error("Server sedang tidak tersedia. Pastikan koneksi internet stabil.")
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.value = SplashUiState.Loading
                }
            }
        }
    }
}
