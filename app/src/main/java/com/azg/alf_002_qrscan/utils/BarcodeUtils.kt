package com.azg.alf_002_qrscan.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

object BarcodeUtils {

    /**
     * Create barcode bitmap from data and format
     */
    suspend fun createBarcode(data: String, format: BarcodeFormat): Bitmap? {
        return try {
            val width = when (format) {
                BarcodeFormat.QR_CODE, BarcodeFormat.AZTEC, BarcodeFormat.DATA_MATRIX -> 512
                else -> 600
            }
            val height = when (format) {
                BarcodeFormat.QR_CODE, BarcodeFormat.AZTEC, BarcodeFormat.DATA_MATRIX -> 512
                else -> 300
            }
            val hints = if (format in setOf(
                    BarcodeFormat.QR_CODE,
                    BarcodeFormat.AZTEC,
                    BarcodeFormat.DATA_MATRIX,
                    BarcodeFormat.PDF_417
                )
            ) {
                mapOf(EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name())
            } else {
                null
            }
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                data,
                format,
                width,
                height,
                hints
            )
            bitMatrixToBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Convert BitMatrix to Bitmap
     */
    private fun bitMatrixToBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) {
                    android.graphics.Color.BLACK
                } else {
                    android.graphics.Color.WHITE
                }
            }
        }
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    /**
     * Calculate EAN-8 checksum digit
     * @param data 7-digit string
     * @return checksum character
     */
    fun calculateEAN8Checksum(data: String): Char {
        if (data.length != 7 || !data.all { it.isDigit() }) {
            throw IllegalArgumentException("EAN-8 requires exactly 7 digits")
        }
        var sum = 0
        for (i in data.indices) {
            val digit = data[i].digitToInt()
            sum += if (i % 2 == 0) digit * 3 else digit
        }
        val checkDigit = (10 - (sum % 10)) % 10
        return checkDigit.digitToChar()
    }

    /**
     * Validate EAN-8 code (8 digits with valid checksum)
     */
    fun isValidEAN8(code: String): Boolean {
        if (code.length != 8 || !code.all { it.isDigit() }) return false
        val data = code.substring(0, 7)
        val expectedChecksum = calculateEAN8Checksum(data)
        return code[7] == expectedChecksum
    }

    /**
     * Calculate EAN-13 checksum digit
     * @param input 12-digit string
     * @return checksum character
     */
    fun calculateEAN13Checksum(input: String): Char {
        if (input.length != 12 || !input.all { it.isDigit() }) {
            throw IllegalArgumentException("EAN-13 requires exactly 12 digits")
        }
        var sum = 0
        for (i in input.indices) {
            val digit = input[i].digitToInt()
            sum += if (i % 2 == 0) digit else digit * 3
        }
        val checkDigit = (10 - (sum % 10)) % 10
        return checkDigit.digitToChar()
    }

    /**
     * Validate EAN-13 code (13 digits with valid checksum)
     */
    fun isValidEAN13(code: String): Boolean {
        if (code.length != 13 || !code.all { it.isDigit() }) return false
        val data = code.substring(0, 12)
        val expectedChecksum = calculateEAN13Checksum(data)
        return code[12] == expectedChecksum
    }

    /**
     * Calculate UPC-A checksum digit
     * @param input 11-digit string
     * @return checksum character
     */
    fun calculateUPCAChecksum(input: String): Char {
        if (input.length != 11 || !input.all { it.isDigit() }) {
            throw IllegalArgumentException("UPC-A requires exactly 11 digits")
        }
        var sum = 0
        for (i in input.indices) {
            val digit = input[i].digitToInt()
            sum += if (i % 2 == 0) digit * 3 else digit
        }
        val checkDigit = (10 - (sum % 10)) % 10
        return checkDigit.digitToChar()
    }

    /**
     * Validate UPC-A code (12 digits with valid checksum)
     */
    fun isValidUPCA(code: String): Boolean {
        if (code.length != 12 || !code.all { it.isDigit() }) return false
        val data = code.substring(0, 11)
        val expectedChecksum = calculateUPCAChecksum(data)
        return code[11] == expectedChecksum
    }

    /**
     * Calculate UPC-E checksum digit
     * @param input 7-digit string
     * @return checksum character
     */
    fun calculateUPCEChecksum(input: String): Char {
        if (input.length != 7 || !input.all { it.isDigit() }) {
            throw IllegalArgumentException("UPC-E requires exactly 7 digits")
        }
        // UPC-E checksum is calculated after expanding to UPC-A
        val expanded = expandUPCEtoUPCA(input)
        return calculateUPCAChecksum(expanded)
    }

    /**
     * Expand UPC-E to UPC-A (11 digits, without checksum)
     */
    private fun expandUPCEtoUPCA(upce: String): String {
        val lastDigit = upce[6].digitToInt()
        val manufacturer: String
        val product: String

        when (lastDigit) {
            0, 1, 2 -> {
                manufacturer = upce.substring(1, 3) + lastDigit + "00"
                product = "00" + upce.substring(3, 6)
            }
            3 -> {
                manufacturer = upce.substring(1, 4) + "00"
                product = "000" + upce.substring(4, 6)
            }
            4 -> {
                manufacturer = upce.substring(1, 5) + "0"
                product = "0000" + upce[5]
            }
            else -> {
                manufacturer = upce.substring(1, 6)
                product = "0000" + lastDigit
            }
        }
        return upce[0] + manufacturer + product
    }

    /**
     * Validate input for specific barcode type
     * @return Pair<Boolean, String?> - (isValid, errorMessage)
     */
    fun validateBarcodeInput(input: String, barcodeType: BarcodeType): Pair<Boolean, String?> {
        if (input.isEmpty()) {
            return Pair(false, "Input cannot be empty")
        }

        return when (barcodeType) {
            BarcodeType.EAN_8 -> {
                when {
                    input.length < 7 -> Pair(false, "EAN-8 requires 7 digits (checksum will be added)")
                    input.length == 7 && input.all { it.isDigit() } -> Pair(true, null)
                    input.length == 8 && input.all { it.isDigit() } -> {
                        if (isValidEAN8(input)) Pair(true, null)
                        else Pair(false, "Invalid EAN-8 checksum")
                    }
                    !input.all { it.isDigit() } -> Pair(false, "EAN-8 requires only digits")
                    else -> Pair(false, "EAN-8 requires 7 or 8 digits")
                }
            }
            BarcodeType.EAN_13 -> {
                when {
                    input.length < 12 -> Pair(false, "EAN-13 requires 12 digits (checksum will be added)")
                    input.length == 12 && input.all { it.isDigit() } -> Pair(true, null)
                    input.length == 13 && input.all { it.isDigit() } -> {
                        if (isValidEAN13(input)) Pair(true, null)
                        else Pair(false, "Invalid EAN-13 checksum")
                    }
                    !input.all { it.isDigit() } -> Pair(false, "EAN-13 requires only digits")
                    else -> Pair(false, "EAN-13 requires 12 or 13 digits")
                }
            }
            BarcodeType.UPC_A -> {
                when {
                    input.length < 11 -> Pair(false, "UPC-A requires 11 digits (checksum will be added)")
                    input.length == 11 && input.all { it.isDigit() } -> Pair(true, null)
                    input.length == 12 && input.all { it.isDigit() } -> {
                        if (isValidUPCA(input)) Pair(true, null)
                        else Pair(false, "Invalid UPC-A checksum")
                    }
                    !input.all { it.isDigit() } -> Pair(false, "UPC-A requires only digits")
                    else -> Pair(false, "UPC-A requires 11 or 12 digits")
                }
            }
            BarcodeType.UPC_E -> {
                when {
                    input.length < 7 -> Pair(false, "UPC-E requires 7 digits (checksum will be added)")
                    input.length in 7..8 && input.all { it.isDigit() } -> Pair(true, null)
                    !input.all { it.isDigit() } -> Pair(false, "UPC-E requires only digits")
                    else -> Pair(false, "UPC-E requires 7 or 8 digits")
                }
            }
            BarcodeType.ITF -> {
                when {
                    input.length < 2 -> Pair(false, "ITF requires at least 2 digits")
                    input.length % 2 != 0 -> Pair(false, "ITF requires even number of digits")
                    !input.all { it.isDigit() } -> Pair(false, "ITF requires only digits")
                    else -> Pair(true, null)
                }
            }
            BarcodeType.CODABAR -> {
                val validChars = "0123456789-\$:/.+ABCDTN*E"
                when {
                    input.length < 4 -> Pair(false, "Codabar requires at least 4 characters")
                    !input.all { it.uppercaseChar() in validChars } ->
                        Pair(false, "Codabar allows: 0-9, -, \$, :, /, ., +, A-D")
                    else -> Pair(true, null)
                }
            }
            BarcodeType.CODE_39 -> {
                val validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. \$/+%"
                when {
                    input.isEmpty() -> Pair(false, "Code 39 requires at least 1 character")
                    !input.uppercase().all { it in validChars } ->
                        Pair(false, "Code 39 allows: A-Z, 0-9, -, ., space, \$, /, +, %")
                    else -> Pair(true, null)
                }
            }
            BarcodeType.CODE_93 -> {
                val validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. \$/+%"
                when {
                    input.isEmpty() -> Pair(false, "Code 93 requires at least 1 character")
                    !input.uppercase().all { it in validChars } ->
                        Pair(false, "Code 93 allows: A-Z, 0-9, -, ., space, \$, /, +, %")
                    else -> Pair(true, null)
                }
            }
            BarcodeType.CODE_128 -> {
                when {
                    input.isEmpty() -> Pair(false, "Code 128 requires at least 1 character")
                    else -> Pair(true, null)
                }
            }
            BarcodeType.AZTEC, BarcodeType.PDF_417, BarcodeType.DATA_MATRIX -> {
                when {
                    input.isEmpty() -> Pair(false, "Content cannot be empty")
                    else -> Pair(true, null)
                }
            }
            BarcodeType.OTHER -> Pair(true, null)
        }
    }

    /**
     * Prepare barcode value with checksum if needed
     */
    fun prepareBarcodeValue(input: String, barcodeType: BarcodeType): String {
        return when (barcodeType) {
            BarcodeType.EAN_8 -> {
                if (input.length == 7) {
                    input + calculateEAN8Checksum(input)
                } else {
                    input
                }
            }
            BarcodeType.EAN_13 -> {
                if (input.length == 12) {
                    input + calculateEAN13Checksum(input)
                } else {
                    input
                }
            }
            BarcodeType.UPC_A -> {
                if (input.length == 11) {
                    input + calculateUPCAChecksum(input)
                } else {
                    input
                }
            }
            BarcodeType.UPC_E -> {
                if (input.length == 7) {
                    input + calculateUPCEChecksum(input)
                } else {
                    input
                }
            }
            else -> input
        }
    }
}
