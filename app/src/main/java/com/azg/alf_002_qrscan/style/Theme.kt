package com.azg.alf_002_qrscan.style

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.ui.component.ColorTheme

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun ColorTheme.toColorScheme(): ColorScheme = lightColorScheme(
    primary = buttonColor,
    onPrimary = colorText,
    secondary = buttonColor,
    onSecondary = colorText,
    background = bgColor,
    onBackground = black,
    surface = white,
    onSurface = black,
)

@Composable
fun AppViewTheme(
    lightStatusBar: Boolean = false,
    content: @Composable () -> Unit,
) {
    val currentColorTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) {AppPreferences.themeApp}
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = remember(view) { view.context.findActivity()?.window }
        if (window != null) {
            val insetsController = remember(window, view) {
                WindowCompat.getInsetsController(window, view)
            }
            LaunchedEffect(window, view) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            LaunchedEffect(lightStatusBar) {
                insetsController.isAppearanceLightStatusBars = lightStatusBar
            }
        }
    }
    MaterialTheme(colorScheme = currentColorTheme.toColorScheme(), content = content)
}
