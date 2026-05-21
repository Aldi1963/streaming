package com.sonzaix.streaming.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sonzaix.streaming.domain.model.EpisodeItem

@Composable
fun EpisodeGrid(
    episodes: List<EpisodeItem>,
    onEpisodeClick: (EpisodeItem) -> Unit,
    modifier: Modifier = Modifier,
    activeEpisodeId: String? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 64.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(episodes, key = { it.id }) { episode ->
            val isActive = episode.id == activeEpisodeId
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onEpisodeClick(episode) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = episode.episodeNumber.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
