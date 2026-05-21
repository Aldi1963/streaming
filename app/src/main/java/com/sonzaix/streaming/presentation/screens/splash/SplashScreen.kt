package com.sonzaix.streaming.presentation.screens.splash

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonzaix.streaming.presentation.components.ErrorView

@Composable
fun SplashScreen(
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Success) {
            onNavigateHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color(0xFF0C101A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is SplashUiState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SonzaiX",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "STREAMING",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 4.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            is SplashUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.checkHealth() }
                )
            }
            is SplashUiState.Success -> {
                // Navigating
            }
        }
    }
}
