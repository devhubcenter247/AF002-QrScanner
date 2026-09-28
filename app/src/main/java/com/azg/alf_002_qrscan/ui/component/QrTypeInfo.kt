package com.azg.alf_002_qrscan.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.azg.alf_002_qrscan.R
import com.create.custom.qr.QrType

/**
 * Canonical QR-type -> icon/color/label mapping, shared by History/Favorite rows, the
 * Create Result screen, and the Scan Result screen.
 */
@Immutable
data class QrTypeInfo(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val accentColor: Color
)

private val qrTypeInfoMap = mapOf(
    QrType.Website to QrTypeInfo(R.drawable.ic_create_url, R.string.url, Color(0xFFEF4444)),
    QrType.Wifi to QrTypeInfo(R.drawable.ic_create_wifi, R.string.wifi, Color(0xFFF59E0B)),
    QrType.Email to QrTypeInfo(R.drawable.ic_create_email, R.string.email, Color(0xFF2196F3)),
    QrType.Contact to QrTypeInfo(R.drawable.ic_create_contact, R.string.contact, Color(0xFF0B61F5)),
    QrType.Phone to QrTypeInfo(R.drawable.ic_create_phone, R.string.phone, Color(0xFF22C55E)),
    QrType.Message to QrTypeInfo(R.drawable.ic_create_message, R.string.sms, Color(0xFFAC00F5)),
    QrType.Location to QrTypeInfo(R.drawable.ic_create_location, R.string.location, Color(0xFF109F00)),
    QrType.Event to QrTypeInfo(R.drawable.ic_create_calendar, R.string.event, Color(0xFFF97316)),
    QrType.Barcode to QrTypeInfo(R.drawable.ic_barcode_scan, R.string.barcode, Color(0xFF6B7280)),
    QrType.Address to QrTypeInfo(R.drawable.ic_create_location, R.string.location, Color(0xFF109F00))
)

private val defaultQrTypeInfo = QrTypeInfo(R.drawable.ic_create_text, R.string.text, Color(0xFF8B5CF6))

fun getQrTypeInfo(type: QrType): QrTypeInfo = qrTypeInfoMap[type] ?: defaultQrTypeInfo
