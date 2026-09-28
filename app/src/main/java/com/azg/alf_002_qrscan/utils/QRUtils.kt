package com.azg.alf_002_qrscan.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.AZTEC
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.CODE_128
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.CODE_39
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.CODE_93
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.DATA_MATRIX
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.EAN_13
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.EAN_8
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.ITF
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.PDF_417
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.QR_CODE
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.UPC_A
import com.azg.alf_002_qrscan.utils.QRUtils.FormatUtils.UPC_E
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.CALENDAR_EVENT
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.CONTACT_INFO
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.DRIVER_LICENSE
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.EMAIL
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.GEO
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.ISBN
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.PHONE
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.SMS
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.TEXT
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.UNKNOWN
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.URL
import com.azg.alf_002_qrscan.utils.QRUtils.TypeUtils.WIFI
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.graphics.createBitmap
import com.azg.alf_002_qrscan.model.ContactModel
import com.azg.alf_002_qrscan.model.EmailModel
import com.azg.alf_002_qrscan.model.EventModel
import com.azg.alf_002_qrscan.model.LocationModel
import com.azg.alf_002_qrscan.model.WifiModel
import com.azg.alf_002_qrscan.model.MessageModel
import com.azg.alf_002_qrscan.model.fromQrData
import com.azg.alf_002_qrscan.model.toContactInfo
import com.create.custom.qr.QrType
import com.google.zxing.EncodeHintType
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.math.min

object QRUtils {
    const val RESULT_TYPE = "RESULT_TYPE"
    const val BARCODE_MODEL = "BARCODE_MODEL"
    const val BATCH_LIST = "BATCH_LIST"

    object ResultTypeUtils {
        const val SCAN = "SCAN"
        const val CREATE = "CREATE"
        const val HISTORY = "HISTORY"
    }

    object TypeUtils {
        const val TEXT = "TEXT"
        const val URL = "URL"
        const val CONTACT_INFO = "CONTACT_INFO"
        const val EMAIL = "EMAIL"
        const val PHONE = "PHONE"
        const val SMS = "SMS"
        const val WIFI = "WIFI"
        const val GEO = "GEO"
        const val CALENDAR_EVENT = "CALENDAR_EVENT"
        const val DRIVER_LICENSE = "DRIVER_LICENSE"
        const val ISBN = "ISBN"
        const val UNKNOWN = "UNKNOWN"
    }

    object FormatUtils {
        const val QR_CODE = "QR_CODE"
        const val CODE_128 = "CODE_128"
        const val CODE_39 = "CODE_39"
        const val CODE_93 = "CODE_93"
        const val CODABAR = "CODABAR"
        const val DATA_MATRIX = "DATA_MATRIX"
        const val EAN_13 = "EAN_13"
        const val EAN_8 = "EAN_8"
        const val ITF = "ITF"
        const val PDF_417 = "PDF_417"
        const val AZTEC = "AZTEC"
        const val UPC_A = "UPC_A"
        const val UPC_E = "UPC_E"
    }

    fun formatNameToBarcodeFormat(formatName: String): BarcodeFormat =
        when (formatName.uppercase()) {
            QR_CODE -> BarcodeFormat.QR_CODE
            AZTEC -> BarcodeFormat.AZTEC
            CODE_128 -> BarcodeFormat.CODE_128
            CODE_39 -> BarcodeFormat.CODE_39
            CODE_93 -> BarcodeFormat.CODE_93
            DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            EAN_13 -> BarcodeFormat.EAN_13
            EAN_8 -> BarcodeFormat.EAN_8
            ITF -> BarcodeFormat.ITF
            PDF_417 -> BarcodeFormat.PDF_417
            UPC_A -> BarcodeFormat.UPC_A
            UPC_E -> BarcodeFormat.UPC_E
            else -> BarcodeFormat.QR_CODE
        }

    fun formatBarcode(barcode: Barcode): String =
        when (barcode.format) {
            Barcode.FORMAT_QR_CODE -> QR_CODE
            Barcode.FORMAT_AZTEC -> AZTEC
            Barcode.FORMAT_CODE_128 -> CODE_128
            Barcode.FORMAT_CODE_39 -> CODE_39
            Barcode.FORMAT_CODE_93 -> CODE_93
            Barcode.FORMAT_DATA_MATRIX -> DATA_MATRIX
            Barcode.FORMAT_EAN_13 -> EAN_13
            Barcode.FORMAT_EAN_8 -> EAN_8
            Barcode.FORMAT_ITF -> ITF
            Barcode.FORMAT_PDF417 -> PDF_417
            Barcode.FORMAT_UPC_A -> UPC_A
            Barcode.FORMAT_UPC_E -> UPC_E
            else -> UNKNOWN
        }

    fun valueTypeBarcode(barcode: Barcode): String =
        when (barcode.valueType) {
            Barcode.TYPE_TEXT -> TEXT
            Barcode.TYPE_URL -> URL
            Barcode.TYPE_CONTACT_INFO -> CONTACT_INFO
            Barcode.TYPE_EMAIL -> EMAIL
            Barcode.TYPE_PHONE -> PHONE
            Barcode.TYPE_SMS -> SMS
            Barcode.TYPE_WIFI -> WIFI
            Barcode.TYPE_GEO -> GEO
            Barcode.TYPE_CALENDAR_EVENT -> CALENDAR_EVENT
            Barcode.TYPE_DRIVER_LICENSE -> DRIVER_LICENSE
            Barcode.TYPE_ISBN -> ISBN
            else -> UNKNOWN
        }

    suspend fun generateBarcode(
        content: String,
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        margin: Int = 1 // 0–1 cho viền mỏng
    ): Bitmap? = withContext(dispatcher) {
        try {
            val is2D = format == BarcodeFormat.QR_CODE
            val safeContent = content.take(1800)
            val width = if (is2D) 512 else 1024
            val height = if (is2D) 512 else 300
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, margin)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }
            val bitMatrix = MultiFormatWriter().encode(
                safeContent,
                format,
                width,
                height,
                hints
            )
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)

            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] =
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            createBitmap(w, h).apply {
                setPixels(pixels, 0, w, 0, 0, w, h)
            }
        } catch (e: Exception) {
            null
        }
    }

    enum class QrModuleShape {
        SQUARE,
        CIRCLE,
        ROUNDED
    }

    suspend fun generateStyledBarcode(
        content: String,
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        margin: Int = 1,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        moduleShape: QrModuleShape = QrModuleShape.SQUARE,
        centerIcon: Bitmap? = null,
        centerIconSizeFraction: Float = 0.2f, // 0.2 = icon ~20% cạnh ngắn
    ): Bitmap? = withContext(dispatcher) {
        try {
            val is2D = format == BarcodeFormat.QR_CODE
            val safeContent = content.take(1800)
            val width = if (is2D) 1024 else 1024   // cho đẹp hơn 512
            val height = if (is2D) 1024 else 300

            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, margin)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }

            val bitMatrix = MultiFormatWriter().encode(
                safeContent,
                format,
                width,
                height,
                hints
            )

            val w = bitMatrix.width
            val h = bitMatrix.height

            // Tạo bitmap + canvas để vẽ
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Vẽ background (có thể thay bằng gradient nếu muốn)
            canvas.drawColor(backgroundColor)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = foregroundColor
            }

            // Kích thước 1 module (1 ô) trong bitmap
            val moduleW = w.toFloat() / bitMatrix.width
            val moduleH = h.toFloat() / bitMatrix.height

            val radius = min(moduleW, moduleH) / 2f

            // Vẽ từng module
            for (y in 0 until bitMatrix.height) {
                for (x in 0 until bitMatrix.width) {
                    if (!bitMatrix[x, y]) continue

                    val left = x * moduleW
                    val top = y * moduleH
                    val right = left + moduleW
                    val bottom = top + moduleH

                    when (moduleShape) {
                        QrModuleShape.SQUARE -> {
                            canvas.drawRect(left, top, right, bottom, paint)
                        }

                        QrModuleShape.CIRCLE -> {
                            val cx = (left + right) / 2f
                            val cy = (top + bottom) / 2f
                            canvas.drawCircle(cx, cy, radius, paint)
                        }

                        QrModuleShape.ROUNDED -> {
                            // bo nhẹ cho ô
                            val rx = moduleW / 3f
                            val ry = moduleH / 3f
                            canvas.drawRoundRect(
                                left, top, right, bottom,
                                rx, ry,
                                paint
                            )
                        }
                    }
                }
            }

            // Thêm icon ở giữa (nếu có)
            centerIcon?.let { icon ->
                val minSide = min(w, h).toFloat()
                val targetSize = (minSide * centerIconSizeFraction).toInt().coerceAtLeast(10)

                val scaledIcon = Bitmap.createScaledBitmap(
                    icon,
                    targetSize,
                    targetSize,
                    true
                )

                val cx = w / 2f
                val cy = h / 2f
                val left = cx - scaledIcon.width / 2f
                val top = cy - scaledIcon.height / 2f

                // (Optional) vẽ nền trắng tròn/bo góc cho icon
                val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    style = Paint.Style.FILL
                }
                val padding = targetSize * 0.1f
                val bgLeft = left - padding
                val bgTop = top - padding
                val bgRight = left + scaledIcon.width + padding
                val bgBottom = top + scaledIcon.height + padding

                // Hình bo góc phía sau icon cho QR dễ đọc
                canvas.drawRoundRect(
                    bgLeft, bgTop, bgRight, bgBottom,
                    targetSize * 0.3f,
                    targetSize * 0.3f,
                    bgPaint
                )

                // Vẽ icon
                canvas.drawBitmap(scaledIcon, left, top, null)
            }

            bitmap
        } catch (e: Exception) {
            null
        }
    }

    // Parsing methods for scan results
    fun parsePhone(content: String): String {
        return content
            .removePrefix("tel:")
            .removePrefix("TEL:")
            .trim()
    }

    fun parseEmail(content: String): EmailModel {
        val trimmed = content.trim()
        val upper = trimmed.uppercase()

        return when {
            // mailto: format
            upper.startsWith("MAILTO:") -> {
                content.fromQrData() ?: EmailModel(email = trimmed.removePrefix("mailto:").removePrefix("MAILTO:").substringBefore("?"))
            }
            // MATMSG: format (used by some QR generators)
            // Format: MATMSG:TO:email;SUB:subject;BODY:body;;
            upper.startsWith("MATMSG:") -> {
                parseMatmsgEmail(trimmed)
            }
            // Plain email address
            isEmailPattern(trimmed) -> {
                EmailModel(email = trimmed)
            }
            else -> {
                EmailModel(email = trimmed)
            }
        }
    }

    private fun parseMatmsgEmail(content: String): EmailModel {
        var email = ""
        var subject = ""
        var body = ""

        val cleanContent = content.removePrefix("MATMSG:").removePrefix("matmsg:")
        val parts = cleanContent.split(";")

        for (part in parts) {
            when {
                part.uppercase().startsWith("TO:") -> {
                    email = part.substring(3).trim()
                }
                part.uppercase().startsWith("SUB:") -> {
                    subject = part.substring(4).trim()
                }
                part.uppercase().startsWith("BODY:") -> {
                    body = part.substring(5).trim()
                }
            }
        }

        return EmailModel(email = email, subject = subject, body = body)
    }

    fun parseSms(content: String): MessageModel {
        return MessageModel.parseSmsString(content)
    }

    fun parseContact(content: String): ContactModel {
        return content.toContactInfo()
    }

    fun parseWifi(content: String): WifiModel {
        return WifiModel.parseWifiString(content) ?: WifiModel(name = content)
    }

    fun parseLocation(content: String): LocationModel {
        return LocationModel.fromString(content) ?: LocationModel()
    }

    fun parseEvent(content: String): EventModel {
        return EventModel.fromQRCodeString(content) ?: EventModel(eventName = content)
    }

    /**
     * Auto-detect QrType based on content patterns
     */
    fun detectQrType(content: String): QrType {
        val trimmedContent = content.trim()
        val upperContent = trimmedContent.uppercase()

        return when {
            // WiFi detection (check first as it's most specific)
            upperContent.startsWith("WIFI:") -> QrType.Wifi

            // Contact detection (vCard)
            upperContent.startsWith("BEGIN:VCARD") ||
            upperContent.startsWith("MECARD:") -> QrType.Contact

            // Event/Calendar detection
            upperContent.startsWith("BEGIN:VEVENT") ||
            upperContent.startsWith("BEGIN:VCALENDAR") -> QrType.Event

            // Email detection (before URL as mailto: contains ://)
            upperContent.startsWith("MAILTO:") ||
            upperContent.startsWith("MATMSG:") -> QrType.Email

            // SMS/Message detection
            upperContent.startsWith("SMS:") ||
            upperContent.startsWith("SMSTO:") ||
            upperContent.startsWith("MESSAGE:") -> QrType.Message

            // Phone detection
            upperContent.startsWith("TEL:") -> QrType.Phone

            // Location/Geo detection
            upperContent.startsWith("GEO:") -> QrType.Location

            // Website/URL detection
            trimmedContent.startsWith("http://", ignoreCase = true) ||
            trimmedContent.startsWith("https://", ignoreCase = true) ||
            trimmedContent.startsWith("www.", ignoreCase = true) -> QrType.Website

            // Check for plain email pattern (user@domain.com)
            isEmailPattern(trimmedContent) -> QrType.Email

            // Check for URL pattern (domain.com/path)
            isValidUrl(trimmedContent) -> QrType.Website

            // Check for geo coordinates pattern
            isGeoPattern(trimmedContent) -> QrType.Location

            // Check for phone number pattern (only digits)
            isPhonePattern(trimmedContent) -> QrType.Phone

            // Default to Text
            else -> QrType.Text
        }
    }

    /**
     * Check if content looks like a valid URL
     */
    private fun isValidUrl(content: String): Boolean {
        val urlPattern = Regex(
            "^(https?://)?([a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}(/.*)?$",
            RegexOption.IGNORE_CASE
        )
        return urlPattern.matches(content)
    }

    /**
     * Check if content looks like an email address
     */
    private fun isEmailPattern(content: String): Boolean {
        val emailPattern = Regex(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return emailPattern.matches(content)
    }

    /**
     * Check if content looks like a phone number
     */
    private fun isPhonePattern(content: String): Boolean {
        val phonePattern = Regex(
            "^[+]?[0-9]{7,15}$"
        )
        val cleaned = content.replace(Regex("[\\s()-]"), "")
        return phonePattern.matches(cleaned)
    }

    /**
     * Check if content looks like geo coordinates
     */
    private fun isGeoPattern(content: String): Boolean {
        val geoPattern = Regex(
            "^-?\\d+\\.\\d+,\\s*-?\\d+\\.\\d+$"
        )
        return geoPattern.matches(content)
    }
}