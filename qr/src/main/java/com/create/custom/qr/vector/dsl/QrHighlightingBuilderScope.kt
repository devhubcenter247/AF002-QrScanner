package com.create.custom.qr.vector.dsl

import com.create.custom.qr.HighlightingType
import com.create.custom.qr.IAnchorsHighlighting

sealed interface QrHighlightingBuilderScope : IAnchorsHighlighting {
    override var cornerEyes: HighlightingType
    override var versionEyes: HighlightingType
    override var timingLines: HighlightingType
    override val alpha: Float
}
