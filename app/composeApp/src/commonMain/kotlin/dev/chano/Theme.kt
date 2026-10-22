package dev.chano

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ChanoTheme(colorScheme: ColorScheme? = null, content: @Composable () -> Unit) {
    val colors = colorScheme ?: if (isSystemInDarkTheme()) {
        darkColorScheme(primary = Color(0xFFB8C7FF), secondary = Color(0xFFB6D5CF))
    } else {
        lightColorScheme(primary = Color(0xFF3855A6), secondary = Color(0xFF38675E))
    }
    MaterialExpressiveTheme(
        colorScheme = colors,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}
