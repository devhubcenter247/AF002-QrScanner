package com.azg.alf_002_qrscan.utils

import java.nio.charset.StandardCharsets

const val MAX_QR_CONTENT_BYTES = 1_000
private val LEGACY_UNICODE_QR_PATTERN = Regex("^(?:\\\\u[0-9A-Fa-f]{4})+$")

fun String.isWithinQrContentLimit(): Boolean =
    toByteArray(StandardCharsets.UTF_8).size <= MAX_QR_CONTENT_BYTES

fun String.limitToQrContent(): String {
    if (isWithinQrContentLimit()) return this

    var byteCount = 0
    var endIndex = 0
    while (endIndex < length) {
        val codePoint = Character.codePointAt(this, endIndex)
        val character = String(Character.toChars(codePoint))
        val characterBytes = character.toByteArray(StandardCharsets.UTF_8).size
        if (byteCount + characterBytes > MAX_QR_CONTENT_BYTES) break
        byteCount += characterBytes
        endIndex += Character.charCount(codePoint)
    }
    return substring(0, endIndex)
}

/** Decodes QR data produced by old app versions that escaped every UTF-16 code unit. */
fun String.decodeLegacyQrUnicodeEscapes(): String {
    if (!LEGACY_UNICODE_QR_PATTERN.matches(this)) return this

    return chunked(6).joinToString(separator = "") { escapedCodeUnit ->
        escapedCodeUnit.substring(2).toInt(16).toChar().toString()
    }
}
