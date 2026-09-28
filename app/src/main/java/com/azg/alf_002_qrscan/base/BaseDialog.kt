@file:Suppress("DEPRECATION")

package com.azg.alf_002_qrscan.base

import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.widthPercentDp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AppBottomSheet(
    onDismiss: () -> Unit,
    dismissBack: Boolean = true,
    dismissOutside: Boolean = true,
    onAppear: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (dismissBack) BackHandler { onDismiss() }
    LaunchedEffect(Unit) { onAppear?.invoke() }

    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 12.Sdp, topEnd = 12.Sdp),
                tonalElevation = 8.Sdp,
                modifier = Modifier.fillMaxWidth()
            ) { content() }
        }
    } else {
        Dialog(
            onDismissRequest = { if (dismissOutside || dismissBack) onDismiss() },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                dismissOnBackPress = dismissBack,
                dismissOnClickOutside = dismissOutside
            )
        ) {
            val view = LocalView.current
            val dialogWindow = (view.parent as? DialogWindowProvider)?.window

            LaunchedEffect(dialogWindow) {
                dialogWindow?.let { win ->
                    win.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    win.setDimAmount(0f)
                    win.setWindowAnimations(0)
                    win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    win.navigationBarColor = Color.Transparent.toArgb()
                    win.statusBarColor = Color.Transparent.toArgb()
                    WindowCompat.setDecorFitsSystemWindows(win, false)

                    val controller = WindowCompat.getInsetsController(win, win.decorView)
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    controller.hide(WindowInsetsCompat.Type.navigationBars())
                }
            }

            DisposableEffect(view) {
                ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                    val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
                    if (!imeVisible) {
                        dialogWindow?.let { win ->
                            WindowCompat.getInsetsController(win, win.decorView)
                                .hide(WindowInsetsCompat.Type.navigationBars())
                        }
                    }
                    insets
                }
                onDispose { }
            }

            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { visible = true }

            val overlayAlpha by animateFloatAsState(if (visible) 0.35f else 0f, tween(300))
            val offsetY = remember { Animatable(0f) }
            val scope = rememberCoroutineScope()
            val screenHeightPx = with(LocalDensity.current) {
                LocalConfiguration.current.screenHeightDp.dp.toPx()
            }
            val dismissInteraction = remember { MutableInteractionSource() }
            val sheetInteraction = remember { MutableInteractionSource() }

            val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)

            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = overlayAlpha))
                        .then(
                            if (dismissOutside) Modifier.clickable(
                                indication = null,
                                interactionSource = dismissInteraction
                            ) { onDismiss() } else Modifier
                        )
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .offset { IntOffset(0, offsetY.value.roundToInt() - imeBottomPx) }
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                scope.launch {
                                    offsetY.snapTo((offsetY.value + delta).coerceAtLeast(0f))
                                }
                            },
                            onDragStopped = { velocity ->
                                scope.launch {
                                    if (offsetY.value > screenHeightPx * 0.2f || velocity > 1000f) {
                                        offsetY.animateTo(screenHeightPx, tween(250))
                                        onDismiss()
                                    } else {
                                        offsetY.animateTo(
                                            0f,
                                            spring(
                                                Spring.DampingRatioMediumBouncy,
                                                Spring.StiffnessMedium
                                            )
                                        )
                                    }
                                }
                            }
                        )
                        .clickable(
                            indication = null,
                            interactionSource = sheetInteraction
                        ) {}
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 12.Sdp, topEnd = 12.Sdp),
                        tonalElevation = 8.Sdp,
                        modifier = Modifier.fillMaxWidth()
                    ) { content() }
                }
            }
        }
    }
}

@Composable
fun AppDialog(
    onDismiss: () -> Unit,
    dismissBack: Boolean = true,
    dismissOutsize: Boolean = true,
    onAppear: (() -> Unit)? = null,
    dialog: @Composable () -> Unit
) {
    LaunchedEffect(Unit) { onAppear?.invoke() }
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.Sdp),
                color = Color.White,
                tonalElevation = 8.Sdp,
                modifier = Modifier.padding(horizontal = widthPercentDp(5f))
            ) {
                dialog()
            }
        }
    } else {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = dismissBack,
                dismissOnClickOutside = dismissOutsize
            )
        ) {
            Surface(
                shape = RoundedCornerShape(12.Sdp),
                color = Color.White,
                tonalElevation = 8.Sdp,
                modifier = Modifier.padding(horizontal = widthPercentDp(5f))
            ) {
                dialog()
            }
        }
    }
}
