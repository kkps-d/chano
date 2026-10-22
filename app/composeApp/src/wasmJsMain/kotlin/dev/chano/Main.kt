@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package dev.chano

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

// Override window.CHANO_API_URL in index.html when the API has a separate origin.
@JsFun("() => globalThis.CHANO_API_URL ?? (location.hostname === 'localhost' || location.hostname === '127.0.0.1' ? 'http://localhost:3000' : location.origin)")
private external fun apiBaseUrl(): String

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport { App(apiBaseUrl = apiBaseUrl()) }
}
