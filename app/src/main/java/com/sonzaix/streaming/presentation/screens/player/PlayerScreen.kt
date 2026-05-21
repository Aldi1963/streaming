package com.sonzaix.streaming.presentation.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.sonzaix.streaming.presentation.components.EmptyView
import com.sonzaix.streaming.presentation.components.ErrorView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        @Suppress("DEPRECATION")
        activity?.window?.decorView?.systemUiVisibility = (
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        
        onDispose {
            activity?.requestedOrientation = originalOrientation
            @Suppress("DEPRECATION")
            activity?.window?.decorView?.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        val hasNext = viewModel.playNextEpisode()
                        if (!hasNext) {
                            Toast.makeText(context, "Semua episode selesai", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    player.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Continue playback if was playing
                }
                Lifecycle.Event.ON_DESTROY -> {
                    player.release()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    LaunchedEffect(player) {
        while (isActive) {
            if (player.isPlaying) {
                val pos = player.currentPosition
                val dur = player.duration
                if (dur > 0) {
                    viewModel.saveWatchProgress(pos, dur)
                }
            }
            delay(5000)
        }
    }

    LaunchedEffect(uiState.streamSource) {
        val source = uiState.streamSource
        if (source != null) {
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            val headers = source.headers ?: emptyMap()
            val userAgent = headers["User-Agent"] ?: "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36"
            httpDataSourceFactory.setUserAgent(userAgent)
            
            val defaultHeaders = mutableMapOf<String, String>()
            headers.forEach { (k, v) ->
                if (k != "User-Agent") {
                    defaultHeaders[k] = v
                }
            }
            if (defaultHeaders.isNotEmpty()) {
                httpDataSourceFactory.setDefaultRequestProperties(defaultHeaders)
            }

            val mediaItem = MediaItem.fromUri(source.url)
            val mediaSource = DefaultMediaSourceFactory(context)
                .setDataSourceFactory(httpDataSourceFactory)
                .createMediaSource(mediaItem)
            
            player.setMediaSource(mediaSource)
            player.prepare()
            if (uiState.initialPosition > 0L) {
                player.seekTo(uiState.initialPosition)
            }
            player.playWhenReady = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (uiState.error != null && uiState.streamSource == null) {
            ErrorView(
                message = uiState.error ?: "Video gagal dimuat.",
                onRetry = { viewModel.loadDramaAndStream() },
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (uiState.streamSource == null) {
            EmptyView(
                message = "Link video tidak tersedia atau gagal dimuat.",
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = true
                        this.player = player
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}
