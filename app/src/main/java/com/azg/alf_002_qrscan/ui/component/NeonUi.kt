package com.azg.alf_002_qrscan.ui.component

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.azg.alf_002_qrscan.widget.grdEnd
import com.azg.alf_002_qrscan.widget.grdStart

@Stable
fun Modifier.neonState(
    neonEnabled: Boolean = true,
    alpha: Float = 0.72f,
    shape: Shape = CircleShape,
    colors: List<Color> = listOf(grdStart, grdEnd),
    outerRadius: Dp = 8.dp,
    outerSpread: Dp = 1.dp,
    innerRadius: Dp = 0.dp,
    innerSpread: Dp = 0.dp
) = if (!neonEnabled) this else this
    .dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = outerRadius,
            spread = outerSpread,
            brush = Brush.horizontalGradient(colors),
            alpha = alpha
        )
    )
    .dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = innerRadius,
            spread = innerSpread,
            color = colors.last(),
            alpha = alpha
        )
    )
