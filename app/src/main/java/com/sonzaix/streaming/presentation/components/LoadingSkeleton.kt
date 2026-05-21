package com.sonzaix.streaming.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray.copy(alpha = alpha))
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = alpha))
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(5) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray.copy(alpha = alpha))
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Gray.copy(alpha = alpha))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = alpha))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = alpha))
                )
            }
        }
    }
}

@Composable
fun GridSkeleton(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gridSkeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(6) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Gray.copy(alpha = alpha))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = alpha))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = alpha))
                )
            }
        }
    }
}
