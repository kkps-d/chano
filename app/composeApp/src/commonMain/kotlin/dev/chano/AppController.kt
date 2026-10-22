package dev.chano

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

// The SwiftUI host can trigger the same refresh as the shared Compose button.
class AppController {
    internal var refreshGeneration by mutableIntStateOf(0)
        private set

    fun refresh() { refreshGeneration++ }
}
