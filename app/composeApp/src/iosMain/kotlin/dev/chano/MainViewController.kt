package dev.chano

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(apiBaseUrl: String, controller: AppController) = ComposeUIViewController {
    App(apiBaseUrl = apiBaseUrl, controller = controller, nativeRefreshControl = true)
}
