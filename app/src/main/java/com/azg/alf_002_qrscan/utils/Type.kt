package com.azg.alf_002_qrscan.utils

import androidx.annotation.StringRes
import com.azg.alf_002_qrscan.R
import com.create.custom.qr.QrType

@StringRes
fun getTitleByType(type: QrType): Int =
    when (type) {
        QrType.Email -> R.string.email
        QrType.Contact -> R.string.contact
        QrType.Website -> R.string.url
        QrType.Message -> R.string.message
        QrType.Phone -> R.string.phone
        QrType.Location -> R.string.location
        QrType.Event -> R.string.event
        QrType.Wifi -> R.string.wifi
        QrType.Barcode -> R.string.barcode
        else -> R.string.text
    }