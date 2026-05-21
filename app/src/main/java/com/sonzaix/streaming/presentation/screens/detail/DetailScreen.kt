package com.sonzaix.streaming.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sonzaix.streaming.presentation.components.EmptyView
import com.sonzaix.streaming.presentation.components.EpisodeGrid
import com.sonzaix.streaming.presentation.components.ErrorView
import com.sonzaix.streaming.presentation.components.SectionTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onEpisodeClick: (String, String, String, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Drama") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.drama != null) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
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
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.error != null) {
                ErrorView(
                    message = uiState.error ?: "Terjadi kesalahan",
                    onRetry = { viewModel.loadDetail() }
                )
            } else if (uiState.drama == null) {
                EmptyView(
                    message = "Detail drama tidak ditemukan.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val drama = uiState.drama!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(drama.thumbnail)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.5f))
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.4f),
                                                MaterialTheme.colorScheme.background
                                            )
                                        )
                                    )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .aspectRatio(2f / 3f)
                                        .clip(RoundedCornerShape(8.dp)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = drama.title,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Provider: ${drama.provider.uppercase()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    if (!drama.watchCount.isNullOrBlank()) {
                                        Text(
                                            text = "👀 ${drama.watchCount} Ditonton",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val lastWatched = uiState.lastWatchedEpisode
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            if (lastWatched != null) {
                                Button(
                                    onClick = {
                                        onEpisodeClick(
                                            lastWatched.provider,
                                            lastWatched.dramaId,
                                            lastWatched.episodeId,
                                            lastWatched.episodeNumber
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Lanjutkan Tonton (Ep ${lastWatched.episodeNumber})")
                                }
                            } else if (uiState.episodes.isNotEmpty()) {
                                val firstEp = uiState.episodes.first()
                                Button(
                                    onClick = {
                                        onEpisodeClick(
                                            firstEp.provider,
                                            firstEp.dramaId,
                                            firstEp.id,
                                            firstEp.episodeNumber
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Mulai Menonton")
                                }
                            }
                        }
                    }

                    if (!drama.description.isNullOrBlank()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Sinopsis",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = drama.description!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (drama.tags.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Genre",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    drama.tags.forEach { tag ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(tag) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Daftar Episode",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (uiState.episodes.isEmpty()) {
                                EmptyView(message = "Episode tidak tersedia.", modifier = Modifier.height(100.dp))
                            } else {
                                EpisodeGrid(
                                    episodes = uiState.episodes,
                                    onEpisodeClick = { ep ->
                                        onEpisodeClick(ep.provider, ep.dramaId, ep.id, ep.episodeNumber)
                                    },
                                    activeEpisodeId = uiState.lastWatchedEpisode?.episodeId
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
