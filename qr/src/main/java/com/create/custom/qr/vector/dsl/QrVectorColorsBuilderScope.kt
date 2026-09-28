package com.create.custom.qr.vector.dsl

import com.create.custom.qr.vector.style.IQrVectorColors
import com.create.custom.qr.vector.style.QrVectorColor


sealed interface QrVectorColorsBuilderScope : IQrVectorColors {
    override var ball: QrVectorColor
    override var dark: QrVectorColor
    override var frame: QrVectorColor
    override var light: QrVectorColor
}

