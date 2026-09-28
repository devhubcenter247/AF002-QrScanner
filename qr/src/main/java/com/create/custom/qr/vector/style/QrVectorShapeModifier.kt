package com.create.custom.qr.vector.style

import android.graphics.Path
import com.create.custom.qr.style.Neighbors

fun interface QrVectorShapeModifier {

    /**
     * Apply shape to a path
     *
     * @return The same instance if possible
     * */
    fun Path.shape(size : Float, neighbors: Neighbors)

}
