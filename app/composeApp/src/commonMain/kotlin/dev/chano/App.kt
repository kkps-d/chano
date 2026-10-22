package dev.chano

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException

private sealed interface ConnectionState {
    data object Loading : ConnectionState
    data class Connected(val info: ServerInfo) : ConnectionState
    data object Unavailable : ConnectionState
}

@Composable
fun App(
    apiBaseUrl: String,
    controller: AppController = remember { AppController() },
    colorScheme: ColorScheme? = null,
    nativeRefreshControl: Boolean = false,
) {
    val client = remember { createHttpClient() }
    DisposableEffect(client) { onDispose { client.close() } }
    val api = remember(apiBaseUrl, client) { ApiClient(apiBaseUrl, client) }
    var connection by remember { mutableStateOf<ConnectionState>(ConnectionState.Loading) }

    LaunchedEffect(api, controller.refreshGeneration) {
        connection = ConnectionState.Loading
        connection = try {
            ConnectionState.Connected(api.info())
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            ConnectionState.Unavailable
        }
    }

    ChanoTheme(colorScheme) {
        val colors = MaterialTheme.colorScheme
        Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(colors.primaryContainer, colors.background)),
                ).safeDrawingPadding(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    Modifier.widthIn(max = 620.dp).fillMaxWidth()
                        .verticalScroll(rememberScrollState()).padding(28.dp)
                        .padding(bottom = if (nativeRefreshControl) 88.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Text("Chano", style = MaterialTheme.typography.displayLarge)
                    Text("One app. Every day, everywhere.", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "A shared foundation for Android, iOS, desktop, and web.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Card(shape = RoundedCornerShape(32.dp)) {
                        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Your connection", style = MaterialTheme.typography.titleLarge)
                            when (val state = connection) {
                                ConnectionState.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                    Text("Connecting to your backend…")
                                }
                                is ConnectionState.Connected -> {
                                    Text(state.info.message, style = MaterialTheme.typography.bodyLarge)
                                    Text("${state.info.name} · v${state.info.version}")
                                }
                                ConnectionState.Unavailable -> Text(
                                    "Backend unavailable. Start the server and check your API address, then try again.",
                                )
                            }
                            if (!nativeRefreshControl) {
                                Button(
                                    onClick = controller::refresh,
                                    enabled = connection != ConnectionState.Loading,
                                ) { Text("Refresh connection") }
                            }
                        }
                    }
                }
            }
        }
    }
}
