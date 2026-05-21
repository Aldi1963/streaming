package com.sonzaix.streaming.presentation.screens.popular

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonzaix.streaming.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularScreen(
    onDramaClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PopularViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular & Trending") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.dramas.isEmpty()) {
                GridSkeleton()
            } else if (uiState.error != null && uiState.dramas.isEmpty()) {
                ErrorView(
                    message = uiState.error ?: "Terjadi kesalahan",
                    onRetry = { viewModel.refresh() }
                )
            } else if (uiState.dramas.isEmpty()) {
                EmptyView(
                    message = "Data tidak ditemukan.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                DramaGrid(
                    dramas = uiState.dramas,
                    onDramaClick = onDramaClick
                )
            }
        }
    }
}
