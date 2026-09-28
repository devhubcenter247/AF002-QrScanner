package com.azg.alf_002_qrscan.model

import androidx.annotation.StringRes
import com.azg.alf_002_qrscan.R
import com.create.custom.qr.QrErrorCorrectionLevel

/**
 * Simplified, user-facing view of [QrErrorCorrectionLevel] — instead of exposing the raw
 * Low/Medium/Quartile/High spec levels, the create/edit UI only offers two choices.
 */
enum class QrQuality(
    @StringRes val label: Int,
    val errorCorrectionLevel: QrErrorCorrectionLevel
) {
    STANDARD(R.string.qr_quality_standard, QrErrorCorrectionLevel.Medium),
    HIGH(R.string.qr_quality_high, QrErrorCorrectionLevel.High)
}
