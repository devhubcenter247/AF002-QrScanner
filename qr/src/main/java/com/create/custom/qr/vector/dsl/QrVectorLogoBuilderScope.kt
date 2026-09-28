package com.create.custom.qr.vector.dsl

import android.graphics.drawable.Drawable
import com.create.custom.qr.style.BitmapScale
import com.create.custom.qr.vector.style.IQRVectorLogo
import com.create.custom.qr.vector.style.QrVectorColor
import com.create.custom.qr.vector.style.QrVectorLogoPadding
import com.create.custom.qr.vector.style.QrVectorLogoShape

sealed interface QrVectorLogoBuilderScope : IQRVectorLogo {

    override var drawable: Drawable?
    override var size : Float
    override var padding : QrVectorLogoPadding
    override var shape: QrVectorLogoShape
    override var scale: BitmapScale
    override var backgroundColor : QrVectorColor
}

