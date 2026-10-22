package dev.chano

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Chano",
        state = rememberWindowState(width = 960.dp, height = 720.dp),
    ) {
        App(apiBaseUrl = System.getenv("CHANO_API_URL") ?: "http://localhost:3000")
    }
}
