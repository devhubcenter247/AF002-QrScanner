package com.create.custom.qr.vector.dsl

import android.graphics.drawable.Drawable
import com.create.custom.qr.style.BitmapScale
import com.create.custom.qr.vector.style.IQrVectorBackground
import com.create.custom.qr.vector.style.QrVectorColor

sealed interface QrVectorBackgroundBuilderScope : IQrVectorBackground {

    override var drawable: Drawable?
    override var scale: BitmapScale
    override var color: QrVectorColor
}
