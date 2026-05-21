package com.sonzaix.streaming

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sonzaix.streaming.presentation.navigation.AppNavGraph
import com.sonzaix.streaming.presentation.theme.SonzaiXTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SonzaiXTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph()
                }
            }
        }
    }
}
