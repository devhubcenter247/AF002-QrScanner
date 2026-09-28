package com.azg.alf_002_qrscan.utils

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.azg.alf_002_qrscan.R
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat

enum class BarcodeType(
    val minLength: Int,
    val format: Int,
    val zxingFormat: BarcodeFormat,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val categoryRes: Int = R.string.category_general_purpose
) {
    AZTEC(1, Barcode.FORMAT_AZTEC, BarcodeFormat.AZTEC, R.string.aztec, R.drawable.ic_barcode_aztec, R.string.category_2d_code),
    PDF_417(1, Barcode.FORMAT_PDF417, BarcodeFormat.PDF_417, R.string.pdf_417, R.drawable.ic_barcode_pdf417, R.string.category_2d_code),
    DATA_MATRIX(1, Barcode.FORMAT_DATA_MATRIX, BarcodeFormat.DATA_MATRIX, R.string.data_matrix, R.drawable.ic_barcode_datamatrix, R.string.category_2d_code),
    EAN_8(7, Barcode.FORMAT_EAN_8, BarcodeFormat.EAN_8, R.string.ean_8, R.drawable.ic_barcode_ean8, R.string.category_product_code),
    EAN_13(12, Barcode.FORMAT_EAN_13, BarcodeFormat.EAN_13, R.string.ean_13, R.drawable.ic_barcode_ean13, R.string.category_product_code),
    UPC_A(11, Barcode.FORMAT_UPC_A, BarcodeFormat.UPC_A, R.string.upc_a, R.drawable.ic_barcode_upca, R.string.category_product_code),
    UPC_E(7, Barcode.FORMAT_UPC_E, BarcodeFormat.UPC_E, R.string.upc_e, R.drawable.ic_barcode_upce, R.string.category_product_code),
    CODE_39(1, Barcode.FORMAT_CODE_39, BarcodeFormat.CODE_39, R.string.code_39, R.drawable.ic_barcode_code39, R.string.category_general_purpose),
    CODE_93(1, Barcode.FORMAT_CODE_93, BarcodeFormat.CODE_93, R.string.code_93, R.drawable.ic_barcode_code93, R.string.category_general_purpose),
    CODE_128(1, Barcode.FORMAT_CODE_128, BarcodeFormat.CODE_128, R.string.code_128, R.drawable.ic_barcode_code128, R.string.category_general_purpose),
    CODABAR(4, Barcode.FORMAT_CODABAR, BarcodeFormat.CODABAR, R.string.codabar, R.drawable.ic_barcode_codabar, R.string.category_general_purpose),
    ITF(2, Barcode.FORMAT_ITF, BarcodeFormat.ITF, R.string.itf, R.drawable.ic_barcode_itf, R.string.category_logistics),
    OTHER(1, Barcode.FORMAT_CODE_128, BarcodeFormat.CODE_128, R.string.product, R.drawable.ic_barcode_other, R.string.category_general_purpose);

    companion object {
        fun fromFormat(format: Int): BarcodeType {
            return entries.find { it.format == format } ?: OTHER
        }

        fun fromMinLength(minLength: Int): BarcodeType {
            return entries.find { kotlin.math.abs(it.minLength - minLength) <= 1 } ?: OTHER
        }

        fun getStringRes(type: BarcodeType): Int {
            return type.titleRes
        }
    }
}

