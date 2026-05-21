package com.sonzaix.streaming.presentation.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonzaix.streaming.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onDramaClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.searchNextPage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pencarian") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchInput(
                query = uiState.query,
                onQueryChange = { viewModel.onQueryChanged(it) }
            )

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading && uiState.dramas.isEmpty()) {
                    GridSkeleton()
                } else if (uiState.error != null && uiState.dramas.isEmpty()) {
                    ErrorView(
                        message = uiState.error ?: "Terjadi kesalahan",
                        onRetry = { viewModel.retry() }
                    )
                } else if (uiState.dramas.isEmpty() && uiState.query.isNotBlank()) {
                    EmptyView(
                        message = "Hasil pencarian kosong untuk \"${uiState.query}\"",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (uiState.dramas.isEmpty() && uiState.query.isBlank()) {
                    EmptyView(
                        message = "Ketik judul drama untuk mencari...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DramaGrid(
                            dramas = uiState.dramas,
                            onDramaClick = onDramaClick,
                            state = gridState,
                            modifier = Modifier.weight(1f)
                        )

                        if (uiState.isMoreLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
