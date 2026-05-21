package com.sonzaix.streaming.presentation.screens.api_tester

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonzaix.streaming.domain.model.Provider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTesterScreen(
    modifier: Modifier = Modifier,
    viewModel: ApiTesterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var providerExpanded by remember { mutableStateOf(false) }
    var endpointExpanded by remember { mutableStateOf(false) }

    val endpoints = listOf("home", "new", "populer", "search", "detail", "stream", "languages")

    // Input state for adding new parameter
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Developer API Tester") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Configuration Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Konfigurasi Request",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Provider Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertizontally
                    ) {
                        Text("Provider:", fontWeight = FontWeight.SemiBold)
                        Box {
                            Button(onClick = { providerExpanded = true }) {
                                Text(Provider.fromId(uiState.provider).displayName)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
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
                    }

                    // Endpoint selector / custom input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertizontally
                    ) {
                        Text("Endpoint:", fontWeight = FontWeight.SemiBold)
                        Box {
                            Button(onClick = { endpointExpanded = true }) {
                                Text("/${uiState.endpoint}")
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = endpointExpanded,
                                onDismissRequest = { endpointExpanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                endpoints.forEach { ep ->
                                    DropdownMenuItem(
                                        text = { Text("/$ep") },
                                        onClick = {
                                            viewModel.setEndpoint(ep)
                                            endpointExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Custom endpoint text field
                    OutlinedTextField(
                        value = uiState.endpoint,
                        onValueChange = { viewModel.setEndpoint(it) },
                        label = { Text("Custom Endpoint Path") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Query Parameters Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Query Parameters",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Current parameters list
                    uiState.queryParams.forEachIndexed { index, param ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertizontally,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = param.key,
                                onValueChange = { viewModel.updateQueryParam(index, it, param.value) },
                                label = { Text("Key") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = param.value,
                                onValueChange = { viewModel.updateQueryParam(index, param.key, it) },
                                label = { Text("Value") },
                                modifier = Modifier.weight(1.2f)
                            )
                            IconButton(
                                onClick = { viewModel.removeQueryParam(index) },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Parameter",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Add new parameter row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertizontally,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newKey,
                            onValueChange = { newKey = it },
                            placeholder = { Text("Key Baru") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newValue,
                            onValueChange = { newValue = it },
                            placeholder = { Text("Value Baru") },
                            modifier = Modifier.weight(1.2f)
                        )
                        IconButton(
                            onClick = {
                                if (newKey.isNotBlank()) {
                                    viewModel.addQueryParam(newKey, newValue)
                                    newKey = ""
                                    newValue = ""
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Parameter",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // Run Button
            Button(
                onClick = { viewModel.sendRequest() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Kirim Request", fontWeight = FontWeight.Bold)
                }
            }

            // Results Section
            if (uiState.requestUrl != null || uiState.responseBody != null || uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Hasil Request",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // Request URL info
                        uiState.requestUrl?.let { url ->
                            Column {
                                Text("URL Request:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                SelectionContainer {
                                    Text(
                                        text = url,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }
                            }
                        }

                        // HTTP status and latency info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            uiState.responseCode?.let { code ->
                                Column {
                                    Text("Status Code:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = code.toString(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (code in 200..299) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            uiState.latencyMs?.let { latency ->
                                Column {
                                    Text("Latency:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "$latency ms",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                        // Response payload details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertizontally
                        ) {
                            Text("Response Body:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            
                            val responseText = uiState.responseBody ?: uiState.error
                            if (!responseText.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(responseText))
                                        Toast.makeText(context, "Respon disalin ke clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Salin JSON",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Main payload text box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            SelectionContainer {
                                when {
                                    uiState.responseBody != null -> {
                                        Text(
                                            text = uiState.responseBody!!,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    uiState.error != null -> {
                                        Text(
                                            text = "Error:\n${uiState.error}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = "Tidak ada respon",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
