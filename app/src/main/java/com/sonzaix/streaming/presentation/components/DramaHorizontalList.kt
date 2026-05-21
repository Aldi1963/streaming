package com.sonzaix.streaming.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonzaix.streaming.domain.model.DramaItem

@Composable
fun DramaHorizontalList(
    dramas: List<DramaItem>,
    onDramaClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dramas, key = { "${it.provider}_${it.id}" }) { drama ->
            DramaCard(
                drama = drama,
                onClick = { onDramaClick(drama.provider, drama.id) },
                modifier = Modifier.width(130.dp)
            )
        }
    }
}
