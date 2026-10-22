package dev.chano.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import dev.chano.App
import dev.chano.AppController

class MainActivity : ComponentActivity() {
    private val controller = AppController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Dynamic color requires Android 12; older devices use the shared palette.
            val colors = if (android.os.Build.VERSION.SDK_INT >= 31) {
                if (isSystemInDarkTheme()) dynamicDarkColorScheme(this)
                else dynamicLightColorScheme(this)
            } else null
            App(apiBaseUrl = BuildConfig.API_BASE_URL, controller = controller, colorScheme = colors)
        }
    }
}
