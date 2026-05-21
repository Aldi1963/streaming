package com.sonzaix.streaming.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sonzaix.streaming.domain.model.DramaItem

@Composable
fun DramaGrid(
    dramas: List<DramaItem>,
    onDramaClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(dramas, key = { "${it.provider}_${it.id}" }) { drama ->
            DramaCard(
                drama = drama,
                onClick = { onDramaClick(drama.provider, drama.id) }
            )
        }
    }
}
