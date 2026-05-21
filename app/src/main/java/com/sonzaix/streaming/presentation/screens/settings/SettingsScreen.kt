package com.sonzaix.streaming.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonzaix.streaming.domain.model.Provider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onApiTester: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearFavoritesDialog by remember { mutableStateOf(false) }
    
    var providerExpanded by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }

    // Dialogs
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Hapus Riwayat") },
            text = { Text("Apakah Anda yakin ingin menghapus semua riwayat tontonan?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showClearFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showClearFavoritesDialog = false },
            title = { Text("Hapus Favorit") },
            text = { Text("Apakah Anda yakin ingin menghapus semua drama favorit dari watchlist?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearFavorites()
                        showClearFavoritesDialog = false
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearFavoritesDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pengaturan") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Preferensi Streaming
            SettingsSection(title = "Preferensi Streaming") {
                // Provider Selector
                Box {
                    SettingsRow(
                        icon = Icons.Default.Tv,
                        title = "Provider Utama",
                        subtitle = Provider.fromId(uiState.provider).displayName,
                        onClick = { providerExpanded = true }
                    )
                    DropdownMenu(
                        expanded = providerExpanded,
                        onDismissRequest = { providerExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Provider.entries.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.displayName) },
                                onClick = {
                                    viewModel.setProvider(p.id)
                                    providerExpanded = false
                                }
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Language Selector
                Box {
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = "Bahasa Konten",
                        subtitle = if (uiState.language == "id") "Bahasa Indonesia" else "English",
                        onClick = { langExpanded = true }
                    )
                    DropdownMenu(
                        expanded = langExpanded,
                        onDismissRequest = { langExpanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bahasa Indonesia") },
                            onClick = {
                                viewModel.setLanguage("id")
                                langExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("English") },
                            onClick = {
                                viewModel.setLanguage("en")
                                langExpanded = false
                            }
                        )
                    }
                }
            }

            // Tampilan
            SettingsSection(title = "Tampilan") {
                SettingsRow(
                    icon = Icons.Default.DarkMode,
                    title = "Tema Aplikasi",
                    subtitle = if (uiState.themeMode == "dark") "Gelap (Premium)" else "Terang",
                    onClick = {
                        val nextTheme = if (uiState.themeMode == "dark") "light" else "dark"
                        viewModel.setThemeMode(nextTheme)
                    }
                )
            }

            // Sistem & Status
            SettingsSection(title = "Sistem & Status") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "API Status",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Status API Server",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = when (uiState.apiHealthy) {
                                    true -> "Terhubung"
                                    false -> "Gangguan Koneksi"
                                    null -> "Memeriksa..."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = when (uiState.apiHealthy) {
                                    true -> MaterialTheme.colorScheme.secondary
                                    false -> MaterialTheme.colorScheme.error
                                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    if (uiState.isCheckingApi) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(onClick = { viewModel.checkApiStatus() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh API Status",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                SettingsActionRow(
                    icon = Icons.Default.Delete,
                    title = "Hapus Riwayat Menonton",
                    description = "Menghapus semua jejak tontonan dan progres pemutaran.",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { showClearHistoryDialog = true }
                )

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                SettingsActionRow(
                    icon = Icons.Default.HeartBroken,
                    title = "Hapus Semua Favorit",
                    description = "Mengosongkan semua drama dari daftar favorit Anda.",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { showClearFavoritesDialog = true }
                )
            }

            // Developer Tools
            SettingsSection(title = "Developer Tools") {
                SettingsRow(
                    icon = Icons.Default.Code,
                    title = "API Tester",
                    subtitle = "Uji respons REST API secara langsung",
                    onClick = onApiTester
                )
            }

            // App Version Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SonzaiX Streaming v1.0.0-beta",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    description: String,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = tint
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
