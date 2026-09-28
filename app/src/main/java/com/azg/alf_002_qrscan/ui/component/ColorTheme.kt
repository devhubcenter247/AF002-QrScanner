package com.azg.alf_002_qrscan.ui.component

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.azg.alf_002_qrscan.R

enum class ColorTheme(var bgColor: Color, var buttonColor: Color, var colorText: Color) {
    BLUE(Color(0xFFECF3F9), Color(0xFF0071F6), Color(0xFFFFFFFF)),
    PINK(Color(0xFFF7EDF5), Color(0xFFF81AC7), Color(0xFF000000)),
    GREEN(Color(0xFFF0F6EF), Color(0xFF27BE0E), Color(0xFF000000)),
    DENIM(Color(0xFFEEF0F6), Color(0xFF151E3D), Color(0xFFFFFFFF))
}
