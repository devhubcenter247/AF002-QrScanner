package com.azg.alf_002_qrscan.style

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event {
    var state by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            state = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return state
}

@Composable
fun OnLifecycleEvent(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onCreate: (() -> Unit)? = null,
    onStart: (() -> Unit)? = null,
    onResume: (() -> Unit)? = null,
    onPause: (() -> Unit)? = null,
    onStop: (() -> Unit)? = null,
    onDestroy: (() -> Unit)? = null,
) {
    val callbacks = rememberUpdatedState(
        mapOf(
            Lifecycle.Event.ON_CREATE to onCreate,
            Lifecycle.Event.ON_START to onStart,
            Lifecycle.Event.ON_RESUME to onResume,
            Lifecycle.Event.ON_PAUSE to onPause,
            Lifecycle.Event.ON_STOP to onStop,
            Lifecycle.Event.ON_DESTROY to onDestroy,
        )
    )
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            callbacks.value[event]?.invoke()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
fun rememberCurrentLifecycleState(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Lifecycle.State {
    var state by remember { mutableStateOf(lifecycleOwner.lifecycle.currentState) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, _ ->
            state = source.lifecycle.currentState
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return state
}

@Composable
fun rememberIsResumed(): Boolean =
    rememberCurrentLifecycleState() == Lifecycle.State.RESUMED

@Composable
fun rememberIsVisible(): Boolean =
    rememberCurrentLifecycleState().isAtLeast(Lifecycle.State.STARTED)
@Composable
fun OnReturnedFromBackground(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onReturn: () -> Unit
) {
    val currentOnReturn by rememberUpdatedState(onReturn)
    var hasPaused by remember { mutableStateOf(false) }
    OnLifecycleEvent(
        lifecycleOwner = lifecycleOwner,
        onPause = { hasPaused = true },
        onResume = {
            if (hasPaused) {
                currentOnReturn()
                hasPaused = false
            }
        }
    )
}
@Composable
fun rememberActivity(): Activity? {
    val context = LocalContext.current
    return remember(context) { context.findActivity() }
}
@Composable
fun rememberComponentActivity(): ComponentActivity? {
    val context = LocalContext.current
    return remember(context) { context.findActivity() as? ComponentActivity }
}
@Composable
fun rememberWindow() = rememberActivity()?.window

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun rememberIsDarkTheme(): Boolean {
    val context = LocalContext.current
    val uiMode = context.resources.configuration.uiMode
    return (uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
}
@SuppressLint("LocalContextConfigurationRead")
@Composable
fun rememberIsLandscape(): Boolean {
    val context = LocalContext.current
    return context.resources.configuration.orientation ==
            android.content.res.Configuration.ORIENTATION_LANDSCAPE
}