package com.sonzaix.streaming.presentation.screens.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonzaix.streaming.presentation.components.DramaGrid
import com.sonzaix.streaming.presentation.components.EmptyView
import com.sonzaix.streaming.presentation.components.LoadingSkeleton
import com.sonzaix.streaming.domain.model.DramaItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onDramaClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Favorit Saya",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingSkeleton(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.favorites.isEmpty() -> {
                    EmptyView(
                        message = "Belum ada drama favorit. Tambahkan drama ke favorit untuk melihatnya di sini."
                    )
                }
                else -> {
                    val dramas = uiState.favorites.map { entity ->
                        DramaItem(
                            id = entity.id,
                            provider = entity.provider,
                            title = entity.title,
                            description = entity.description,
                            thumbnail = entity.thumbnail,
                            episodeCount = entity.episodeCount,
                            watchCount = entity.watchCount,
                            tags = if (entity.tags.isBlank()) emptyList() else entity.tags.split(",")
                        )
                    }

                    DramaGrid(
                        dramas = dramas,
                        onDramaClick = onDramaClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
