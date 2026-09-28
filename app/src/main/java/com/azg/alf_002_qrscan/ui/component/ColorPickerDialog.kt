package com.azg.alf_002_qrscan.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.base.AppDialog
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.white
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
      isShow: Boolean,
      initialColor: Color = Color.Red,
      theme: ColorTheme = ColorTheme.BLUE,
      onDismiss: () -> Unit,
      onColorSelected: (Color) -> Unit
) {
    if (!isShow) return

    val hsv = remember(initialColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(
            android.graphics.Color.argb(
                (initialColor.alpha * 255).toInt(),
                (initialColor.red * 255).toInt(),
                (initialColor.green * 255).toInt(),
                (initialColor.blue * 255).toInt()
            ),
            hsv
        )
        hsv
    }

    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }

    val currentColor = remember(hue, saturation, value) {
        Color.hsv(hue, saturation, value)
    }

    AppDialog(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.Sdp)
        ) {
            TextView(
                stringResource(R.string.color_picker),
                font = R.font.rb_600,
                textSize = 14.Ssp,
                color = black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SpaceH(12.Sdp)

            // Color preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.Sdp)
                    .clip(RoundedCornerShape(8.Sdp))
                    .background(currentColor)
                    .border(1.dp, lightGrey, RoundedCornerShape(6.Sdp))
            )

            SpaceH(12.Sdp)

            // Hue slider
            TextView(
                stringResource(R.string.hue),
                font = R.font.rb_500,
                textSize = 12.Ssp,
                color = black
            )
            SpaceH(4)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.Sdp)
                    .clip(RoundedCornerShape(6.Sdp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.hsv(0f, 1f, 1f),
                                Color.hsv(60f, 1f, 1f),
                                Color.hsv(120f, 1f, 1f),
                                Color.hsv(180f, 1f, 1f),
                                Color.hsv(240f, 1f, 1f),
                                Color.hsv(300f, 1f, 1f),
                                Color.hsv(360f, 1f, 1f)
                            )
                        )
                    )
            )
            Slider(
                value = hue,
                onValueChange = { hue = it },
                valueRange = 0f..360f,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(),
                thumb = {
                    Box(
                        modifier = Modifier.size(18.Sdp), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.Sdp)
                                .background(theme.buttonColor, CircleShape)
                        )
                    }
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        modifier = Modifier.height(4.Sdp),
                        thumbTrackGapSize = 0.dp,
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            activeTrackColor = theme.bgColor,
                            inactiveTrackColor = black.opacity(0.3f)
                        )
                    )
                })
            // Saturation slider
            TextView(
                stringResource(R.string.saturation),
                font = R.font.rb_500,
                textSize = 12.Ssp,
                color = black
            )
            SpaceH(4)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.Sdp)
                    .clip(RoundedCornerShape(6.Sdp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.hsv(hue, 0f, value),
                                Color.hsv(hue, 1f, value)
                            )
                        )
                    )
            )
            Slider(
                value = saturation,
                onValueChange = { saturation = it },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(),
                thumb = {
                    Box(
                        modifier = Modifier.size(18.Sdp), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.Sdp)
                                .background(theme.buttonColor, CircleShape)
                        )
                    }
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        modifier = Modifier.height(4.Sdp),
                        thumbTrackGapSize = 0.dp,
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            activeTrackColor = theme.bgColor,
                            inactiveTrackColor = black.opacity(0.3f)
                        )
                    )
                })
            // Value/Brightness slider
            TextView(
                stringResource(R.string.brightness),
                font = R.font.rb_500,
                textSize = 12.Ssp,
                color = black
            )
            SpaceH(4)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.Sdp)
                    .clip(RoundedCornerShape(6.Sdp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.hsv(hue, saturation, 0f),
                                Color.hsv(hue, saturation, 1f)
                            )
                        )
                    )
            )
            Slider(
                value = value,
                onValueChange = { value = it },
                valueRange = 0f..1f,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(),
                thumb = {
                    Box(
                        modifier = Modifier.size(18.Sdp), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.Sdp)
                                .background(theme.buttonColor, CircleShape)
                        )
                    }
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        modifier = Modifier.height(4.Sdp),
                        thumbTrackGapSize = 0.dp,
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            activeTrackColor = theme.bgColor,
                            inactiveTrackColor = black.opacity(0.3f)
                        )
                    )
                })
            SpaceH(12.Sdp)

            // Buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.Sdp)
            ) {
                TextView(
                    stringResource(R.string.cancel),
                    color = lightGrey,
                    font = R.font.rb_500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .border(1.dp, lightGrey, CircleShape)
                        .weight(1f)
                        .padding(12.Sdp)
                ) {
                    onDismiss()
                }
                TextView(
                    stringResource(R.string.ok),
                    color = white,
                    font = R.font.rb_500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(theme.buttonColor, CircleShape)
                        .weight(1f)
                        .padding(12.Sdp)
                ) {
                    onColorSelected(currentColor)
                    onDismiss()
                }
            }
        }
    }
}

@Composable
fun CustomColorItem(
      theme: ColorTheme,
      onClick: () -> Unit
) {
    Box(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )
                )
            )
            .border(1.dp, lightGrey, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(white, CircleShape)
        )
    }
}
