package com.create.custom.qr.vector.dsl

import com.create.custom.qr.vector.style.IQrVectorShapes
import com.create.custom.qr.vector.style.QrVectorBallShape
import com.create.custom.qr.vector.style.QrVectorFrameShape
import com.create.custom.qr.vector.style.QrVectorPixelShape

sealed interface QrVectorShapesBuilderScope : IQrVectorShapes {
    override var darkPixel: QrVectorPixelShape
    override var lightPixel: QrVectorPixelShape
    override var ball: QrVectorBallShape
    override var frame: QrVectorFrameShape
}

