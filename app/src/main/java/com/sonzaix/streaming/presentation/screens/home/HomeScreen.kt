package com.sonzaix.streaming.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sonzaix.streaming.data.local.HistoryEntity
import com.sonzaix.streaming.domain.model.DramaItem
import com.sonzaix.streaming.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDramaClick: (String, String) -> Unit,
    onHistoryClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SonzaiX",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        )
                        ProviderSelector(
                            selectedProvider = uiState.selectedProvider,
                            onProviderSelected = { viewModel.selectProvider(it) }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
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
            if (uiState.isLoading && uiState.latestDramas.isEmpty() && uiState.popularDramas.isEmpty()) {
                LoadingSkeleton()
            } else if (uiState.error != null && uiState.latestDramas.isEmpty() && uiState.popularDramas.isEmpty()) {
                ErrorView(
                    message = uiState.error ?: "Terjadi kesalahan",
                    onRetry = { viewModel.refresh() }
                )
            } else if (uiState.latestDramas.isEmpty() && uiState.popularDramas.isEmpty()) {
                EmptyView(
                    message = "Data tidak ditemukan. Silakan ganti provider atau coba lagi.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Banner
                    uiState.heroDrama?.let { hero ->
                        item {
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                HeroBanner(
                                    drama = hero,
                                    onWatchClick = { onDramaClick(hero.provider, hero.id) }
                                )
                            }
                        }
                    }

                    // Continue Watching
                    if (uiState.historyList.isNotEmpty()) {
                        item {
                            Column {
                                SectionTitle(title = "Lanjutkan Menonton")
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(uiState.historyList, key = { "${it.provider}_${it.dramaId}_${it.episodeId}" }) { history ->
                                        ContinueWatchingCard(
                                            history = history,
                                            onClick = {
                                                onDramaClick(history.provider, history.dramaId)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Latest Dramas
                    if (uiState.latestDramas.isNotEmpty()) {
                        item {
                            Column {
                                SectionTitle(title = "Drama Terbaru")
                                DramaHorizontalList(
                                    dramas = uiState.latestDramas,
                                    onDramaClick = onDramaClick
                                )
                            }
                        }
                    }

                    // Popular Dramas
                    if (uiState.popularDramas.isNotEmpty()) {
                        item {
                            Column {
                                SectionTitle(title = "Drama Populer")
                                DramaHorizontalList(
                                    dramas = uiState.popularDramas,
                                    onDramaClick = onDramaClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroBanner(
    drama: DramaItem,
    onWatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(drama.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = drama.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = drama.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!drama.description.isNullOrBlank()) {
                Text(
                    text = drama.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tonton Sekarang", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    history: HistoryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(history.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = history.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (history.duration > 0) {
                    val progress = history.lastPosition.toFloat() / history.duration.toFloat()
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = history.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Episode ${history.episodeNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
