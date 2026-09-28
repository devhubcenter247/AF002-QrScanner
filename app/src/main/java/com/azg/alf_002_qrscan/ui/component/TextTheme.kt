package com.azg.alf_002_qrscan.ui.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.azg.alf_002_qrscan.R
import com.sdp.ssp.android.Ssp

val headerScreen = TextStyle(
    color = Color.Black,
    fontSize = 16.Ssp,
    fontFamily = FontFamily(Font(R.font.rb_600)),
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    ),
)
