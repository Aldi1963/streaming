package com.sonzaix.streaming.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sonzaix.streaming.domain.model.DramaItem

@Composable
fun DramaCard(
    drama: DramaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
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

                if (!drama.language.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = drama.language.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (drama.episodeCount != null && drama.episodeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        androidx.compose.ui.graphics.Color.Transparent,
                                        androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${drama.episodeCount} Ep",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = drama.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (!drama.watchCount.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "👀 ${drama.watchCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
