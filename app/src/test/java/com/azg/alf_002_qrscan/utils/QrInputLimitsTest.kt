package com.azg.alf_002_qrscan.utils

import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.decoder.Decoder
import com.google.zxing.qrcode.encoder.Encoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrInputLimitsTest {

    @Test
    fun limitToQrContent_keepsInputWithinUtf8ByteLimit() {
        val limited = "a".repeat(MAX_QR_CONTENT_BYTES + 50).limitToQrContent()

        assertEquals(MAX_QR_CONTENT_BYTES, limited.toByteArray(Charsets.UTF_8).size)
        assertTrue(limited.isWithinQrContentLimit())
    }

    @Test
    fun limitToQrContent_doesNotSplitEmojiSurrogatePair() {
        val input = "😀".repeat(MAX_QR_CONTENT_BYTES / 4 + 1)
        val limited = input.limitToQrContent()

        assertEquals(MAX_QR_CONTENT_BYTES, limited.toByteArray(Charsets.UTF_8).size)
        assertEquals(MAX_QR_CONTENT_BYTES / 4, limited.codePointCount(0, limited.length))
        assertFalse(Character.isHighSurrogate(limited.last()))
    }

    @Test
    fun isWithinQrContentLimit_countsUtf8BytesInsteadOfCharacters() {
        val input = "😀".repeat(MAX_QR_CONTENT_BYTES / 4 + 1)

        assertTrue(input.length < MAX_QR_CONTENT_BYTES * 2)
        assertFalse(input.isWithinQrContentLimit())
    }

    @Test
    fun maxContent_fitsHighErrorCorrectionWithUtf8Encoding() {
        val hints = mapOf(EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name())

        Encoder.encode(
            "😀".repeat(MAX_QR_CONTENT_BYTES / 4),
            ErrorCorrectionLevel.H,
            hints
        )
    }

    @Test
    fun emoji_roundTripsThroughQrAsUtf8() {
        val content = "Hello 👋😀 QR tiếng Việt"
        val hints = mapOf(EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name())
        val encoded = Encoder.encode(content, ErrorCorrectionLevel.H, hints)
        val byteMatrix = encoded.matrix
        val bitMatrix = BitMatrix(byteMatrix.width, byteMatrix.height)
        for (y in 0 until byteMatrix.height) {
            for (x in 0 until byteMatrix.width) {
                if (byteMatrix[x, y].toInt() == 1) bitMatrix.set(x, y)
            }
        }

        assertEquals(content, Decoder().decode(bitMatrix).text)
    }

    @Test
    fun decodeLegacyQrUnicodeEscapes_decodesOldWifiPayload() {
        val legacy = "\\u0057\\u0049\\u0046\\u0049\\u003A\\u0053\\u003A\\u0031" +
                "\\u0032\\u0034\\u003B\\u0054\\u003A\\u0057\\u0050\\u0041\\u0033" +
                "\\u003B\\u0050\\u003A\\u0031\\u0032\\u0033\\u0034\\u0035\\u0036"

        assertEquals("WIFI:S:124;T:WPA3;P:123456", legacy.decodeLegacyQrUnicodeEscapes())
    }

    @Test
    fun decodeLegacyQrUnicodeEscapes_preservesNormalBackslashText() {
        val normalText = "Use \\u0057 as an example"

        assertEquals(normalText, normalText.decodeLegacyQrUnicodeEscapes())
    }
}
