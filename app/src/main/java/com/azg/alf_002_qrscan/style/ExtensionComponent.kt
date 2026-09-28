package com.azg.alf_002_qrscan.style

import android.annotation.SuppressLint
import android.graphics.RuntimeShader
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.material3.ripple
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.sdp.ssp.android.Sdp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BoxParentContent(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    onBack: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    BackHandler(onBack = {
        softwareKeyboardController?.hide()
        onBack()
    })
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = contentAlignment,
        propagateMinConstraints = propagateMinConstraints,
        content = content
    )
}

@Composable
fun BoxScope.onClick(enable: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .onClick(enable, onClick)
    )
}

@Composable
fun RowParentContent(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onBack: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    BackHandler(onBack = {
        softwareKeyboardController?.hide()
        onBack()
    })
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}

@Composable
fun ColumnParentContent(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    BackHandler(onBack = {
        softwareKeyboardController?.hide()
        onBack()
    })
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
fun ConstraintParentContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    content: @Composable ConstraintLayoutScope.() -> Unit
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    BackHandler(onBack = {
        softwareKeyboardController?.hide()
        onBack()
    })
    ConstraintLayout(
        modifier = modifier.fillMaxSize(), content = content
    )
}

@Composable
fun HorScrollView(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val scrollState = rememberScrollState()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        if (keyboardHeight > 0) {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }
    Box(
        modifier
            .fillMaxSize()
            .horizontalScroll(scrollState), content = content
    )
}

@Composable
fun VerScrollView(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val scrollState = rememberScrollState()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        if (keyboardHeight > 0) {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        content()
    }
}

@Stable
fun Modifier.borderWhite(
    width: Dp = 1.5.dp,
    alpha: Float = 20f,
    shape: Shape = CircleShape,
    color: Color = white,
): Modifier = this.border(width, color.opacity(alpha), shape)

@Stable
fun Modifier.onClick(enable: Boolean = true, onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enable,
        onClick = onClick
    )
}

fun Modifier.onClickRipple(
    enabled: Boolean = true, onClick: () -> Unit
): Modifier = clickable(
    interactionSource = null, indication = ripple(),     // có phản hồi chạm
    enabled = enabled, onClick = onClick
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.onLongClick(onLongClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    this.combinedClickable(
        onClick = {}, onLongClick = {
            onLongClick()
        }, indication = null, interactionSource = interactionSource
    )
}

enum class GradientType {
    Linear, Horizontal, Vertical, Radial, Sweep
}

fun Modifier.gradientContent(
    colors: List<Color> = listOf(Color(0xFFFFFFFF), Color(0xFF000000)),
    type: GradientType = GradientType.Linear
): Modifier =
    this.then(
        Modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithCache {
                val gradient = when (type) {
                    GradientType.Linear -> Brush.linearGradient(
                        colors = colors,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )

                    GradientType.Horizontal -> Brush.horizontalGradient(colors)
                    GradientType.Vertical -> Brush.verticalGradient(colors)
                    GradientType.Radial -> Brush.radialGradient(
                        colors = colors,
                        center = Offset(size.width / 2, size.height / 2),
                        radius = size.minDimension / 2
                    )

                    GradientType.Sweep -> Brush.sweepGradient(
                        colors = colors, center = Offset(size.width / 2, size.height / 2)
                    )
                }

                onDrawWithContent {
                    drawContent()
                    drawRect(brush = gradient, blendMode = BlendMode.SrcIn)
                }
            })

@Composable
fun Modifier.backgroundResource(
    @DrawableRes id: Int,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
    alpha: Float = 1f
): Modifier = this.paint(
    painter = painterResource(id), contentScale = contentScale, alignment = alignment, alpha = alpha
)

@Composable
fun Modifier.dashedBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp = 0.dp,
    dashLength: Float = 10f,
    gapLength: Float = 10f
): Modifier = this.then(
    Modifier.drawBehind {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        )
        val width = size.width
        val height = size.height
        val corner = cornerRadius.toPx()

        drawRoundRect(
            color = color,
            size = Size(width, height),
            style = stroke,
            cornerRadius = CornerRadius(corner, corner)
        )
    })

@Stable
class ShimmerState internal constructor(private val animatedProgress: State<Float>) {
    val progress: Float get() = animatedProgress.value
}

@Composable
fun rememberShimmerState(durationMillis: Int = 1200): ShimmerState {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val anim = transition.animateFloat(
        initialValue = -1f, targetValue = 2f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "offset"
    )
    return remember { ShimmerState(anim) }
}

@get:RequiresApi(33)
private val SHIMMER_AGSL_SRC = """
    uniform float2 resolution;
    uniform float  progress;
    uniform float4 baseColor;
    uniform float4 highColor;
    uniform float  band;
    uniform float  angleDx;
    uniform float  angleDy;

    half4 main(float2 coord) {
        float totalTravel = resolution.x + band * 2.0;
        float startX      = -band + progress * totalTravel;
        float2 start      = float2(startX * angleDx, startX * angleDy);
        float2 dir        = float2(angleDx, angleDy);
        float t           = dot(coord - start, dir) / band;
        float factor      = 1.0 - abs(clamp(t, 0.0, 1.0) - 0.5) * 2.0;
        return half4(mix(baseColor, highColor, factor));
    }
""".trimIndent()
@Stable
fun Modifier.shimmer(
    state: ShimmerState,
    visible: Boolean,
    alpha: Float = 1f,
    shape: Shape = RectangleShape,
    baseColor: Color = Color.LightGray.copy(alpha = 0f),
    highlightColor: Color = Color.LightGray.copy(alpha = 0.55f),
    angleDegrees: Float = 20f,
    bandWidthFraction: Float = 0.45f
): Modifier = this.then(
    if (!visible) Modifier
    else if (android.os.Build.VERSION.SDK_INT >= 33) {
        Modifier.shimmerAGSL(
            state,
            alpha,
            shape,
            baseColor,
            highlightColor,
            angleDegrees,
            bandWidthFraction
        )
    } else {
        Modifier.shimmerLegacy(
            state, alpha, shape, baseColor, highlightColor, angleDegrees, bandWidthFraction
        )
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@RequiresApi(33)
private fun Modifier.shimmerAGSL(
    state: ShimmerState,
    alpha: Float,
    shape: Shape,
    baseColor: Color,
    highlightColor: Color,
    angleDegrees: Float,
    bandWidthFraction: Float
): Modifier = Modifier.drawWithCache {
    if (size.minDimension <= 0f || alpha <= 0f) {
        onDrawWithContent { drawContent() }
    } else {
        val angleRad = Math.toRadians(angleDegrees.toDouble())
        val dx = cos(angleRad).toFloat()
        val dy = sin(angleRad).toFloat()
        val band = bandWidthFraction.coerceIn(0.1f, 0.5f) * size.width
        val runtimeShader = RuntimeShader(SHIMMER_AGSL_SRC).apply {
            setFloatUniform("resolution", size.width, size.height)
            setFloatUniform("band", band)
            setFloatUniform("angleDx", dx)
            setFloatUniform("angleDy", dy)
            setFloatUniform(
                "baseColor", baseColor.red, baseColor.green, baseColor.blue, baseColor.alpha * alpha
            )
            setFloatUniform(
                "highColor",
                highlightColor.red,
                highlightColor.green,
                highlightColor.blue,
                highlightColor.alpha * alpha
            )
        }

        val paint = Paint().apply {
            isAntiAlias = true
            shader = runtimeShader
        }

        val cachedPath = shape.toCachedPath(size, layoutDirection)

        onDrawWithContent {
            drawContent()
            runtimeShader.setFloatUniform("progress", state.progress)
            drawIntoCanvas { canvas ->
                if (cachedPath != null) canvas.drawPath(cachedPath, paint)
                else canvas.drawRect(0f, 0f, size.width, size.height, paint)
            }
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.shimmerLegacy(
    state: ShimmerState,
    alpha: Float,
    shape: Shape,
    baseColor: Color,
    highlightColor: Color,
    angleDegrees: Float,
    bandWidthFraction: Float
): Modifier = Modifier.drawWithCache {
    if (size.minDimension <= 0f || alpha <= 0f) {
        onDrawWithContent { drawContent() }
    } else {
        val angleRad = Math.toRadians(angleDegrees.toDouble())
        val dx = cos(angleRad).toFloat()
        val dy = sin(angleRad).toFloat()
        val band = bandWidthFraction.coerceIn(0.1f, 0.5f) * size.width
        val totalTravel = size.width + band * 2f
        val originOffset = -band

        val baseA = baseColor.copy(alpha = baseColor.alpha * alpha)
        val hiA = highlightColor.copy(alpha = highlightColor.alpha * alpha)
        val colors = listOf(baseA, hiA, baseA)

        val paint = Paint().apply { isAntiAlias = true }
        val cachedPath = shape.toCachedPath(size, layoutDirection)

        onDrawWithContent {
            drawContent()
            val startX = originOffset + state.progress * totalTravel
            val start = Offset(startX * dx, startX * dy)
            val end = Offset(start.x + band * dx, start.y + band * dy)
            paint.shader = LinearGradientShader(
                from = start, to = end, colors = colors, tileMode = TileMode.Clamp
            )
            drawIntoCanvas { canvas ->
                if (cachedPath != null) canvas.drawPath(cachedPath, paint)
                else canvas.drawRect(0f, 0f, size.width, size.height, paint)
            }
        }
    }
}

private fun Shape.toCachedPath(
    size: Size, layoutDirection: LayoutDirection
): Path? {
    return when (val outline =
        createOutline(size, layoutDirection, androidx.compose.ui.unit.Density(1f))) {
        is Outline.Generic -> outline.path
        is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        is Outline.Rectangle -> null
    }
}

@Stable
private inline fun Modifier.drawEdgeLine(
    width: Dp,
    color: Color?,
    brush: Brush?,
    crossinline computeStartEnd: DrawScope.(strokePx: Float) -> Pair<Offset, Offset>
): Modifier = drawWithContent {
    drawContent()
    val strokePx = width.toPx()
    if (strokePx <= 0f) return@drawWithContent
    val (start, end) = computeStartEnd(strokePx)
    when {
        brush != null -> drawLine(
            brush = brush, start = start, end = end, strokeWidth = strokePx
        )

        color != null && color != Color.Unspecified -> drawLine(
            color = color, start = start, end = end, strokeWidth = strokePx
        )
    }
}

@Stable
fun Modifier.borderTop(
    width: Dp = 1.5.dp, color: Color? = Color.Black, brush: Brush? = null
): Modifier = composed {
    this.then(
        Modifier.drawEdgeLine(width, color, brush) { strokePx ->
            val y = strokePx / 2f
            Offset(0f, y) to Offset(size.width, y)
        })
}

@Stable
fun Modifier.borderBottom(
    width: Dp = 1.5.dp, color: Color? = Color.Black, brush: Brush? = null
): Modifier = composed {
    this.then(
        Modifier.drawEdgeLine(width, color, brush) { strokePx ->
            val y = size.height - strokePx / 2f
            Offset(0f, y) to Offset(size.width, y)
        })
}

@Stable
fun Modifier.borderStart(
    width: Dp = 1.5.dp, color: Color? = Color.Black, brush: Brush? = null
): Modifier = composed {
    val layoutDir = LocalLayoutDirection.current
    this.then(
        Modifier.drawEdgeLine(width, color, brush) { strokePx ->
            val x =
                if (layoutDir == LayoutDirection.Ltr) strokePx / 2f else size.width - strokePx / 2f
            Offset(x, 0f) to Offset(x, size.height)
        })
}

@Stable
fun Modifier.borderEnd(
    width: Dp = 1.5.dp, color: Color? = Color.Black, brush: Brush? = null
): Modifier = composed {
    val layoutDir = LocalLayoutDirection.current
    this.then(
        Modifier.drawEdgeLine(width, color, brush) { strokePx ->
            val x =
                if (layoutDir == LayoutDirection.Ltr) size.width - strokePx / 2f else strokePx / 2f
            Offset(x, 0f) to Offset(x, size.height)
        })
}

@Stable
fun Modifier.borderHorizontal(
    width: Dp = 1.5.dp, color: Color? = Color.Black, brush: Brush? = null
): Modifier = this
    .borderTop(width, color, brush)
    .borderBottom(width, color, brush)

@Stable
fun Modifier.borderVertical(
    width: Dp = 1.5.dp, color: Color? = Color.Black, brush: Brush? = null
): Modifier = this
    .borderStart(width, color, brush)
    .borderEnd(width, color, brush)

@Composable
fun CustomSwitch(
    checked: Boolean,
    thumbOn: Color = Color.Blue,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.onClick { onCheckedChange(!checked) }) {
        val width = if (constraints.hasBoundedWidth) maxWidth else 52.dp
        val height = if (constraints.hasBoundedHeight) maxHeight else 28.dp

        val thumbPadding = height * 0.1f
        val thumbSize = height - (thumbPadding * 2)

        val thumbPosition by animateDpAsState(
            targetValue = if (checked) width - thumbSize - thumbPadding else thumbPadding,
            animationSpec = tween(durationMillis = 300),
            label = ""
        )

        Box(
            modifier = Modifier
                .size(width, height)
                .clip(CircleShape)
                .background(if (checked) thumbOn else Color(0xFFB5B5B5)),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .offset(x = thumbPosition)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun Modifier.onAppear(block: () -> Unit): Modifier {
    LaunchedEffect(Unit) { block() }
    return this
}

@Composable
fun Modifier.onDisappear(block: () -> Unit): Modifier {
    DisposableEffect(Unit) { onDispose { block() } }
    return this
}

@Composable
fun IconView(
    item: Any,
    tint: Color? = null,
    icSize: Int = 24,
    mirrorForRtl: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onclick: (() -> Unit)? = null
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val iconModifier = Modifier
        .size(icSize.Sdp)
        .then(if (mirrorForRtl && isRtl) Modifier.graphicsLayer { scaleX = -1f } else Modifier)
    Box(modifier = modifier.then(if (onclick != null) Modifier.onClick { onclick() } else Modifier),
        contentAlignment = Alignment.Center) {
        when (item) {
            is ImageVector -> Icon(
                item,
                contentDescription = contentDescription,
                tint = tint ?: Color.Unspecified,
                modifier = iconModifier
            )

            is Painter -> Icon(
                painter = item,
                contentDescription = contentDescription,
                modifier = iconModifier,
                tint = tint ?: Color.Unspecified
            )

            is Int -> Icon(
                painter = painterResource(item),
                contentDescription = contentDescription,
                modifier = iconModifier,
                tint = tint ?: Color.Unspecified
            )
        }
    }
}
