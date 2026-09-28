package com.azg.alf_002_qrscan.model

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLDecoder
import java.net.URLEncoder

data class MessageModel(
    var phone: String = "",
    var message: String = "",
    var subject: String = "",
)  {
    fun isValid(): Boolean {
        return phone.isNotBlank() && message.isNotBlank() && phone.isValidPhoneNumber()
    }

    fun toSmsUri(): String {
        return buildString {
            append("sms:")
            val params = mutableListOf<String>()
            if (message.isNotBlank()) {
                params.add("body=${URLEncoder.encode(message, "UTF-8")}")
            }
            if (subject.isNotBlank()) {
                params.add("subject=${URLEncoder.encode(subject, "UTF-8")}")
            }

            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
    }

    fun toShareableText(): String {
        return buildString {
            append("To: $phone\n")

            if (subject.isNotBlank()) {
                append("Subject: $subject\n")
            }

            append("Message: $message")
        }
    }

    fun toQrString(): String {
        return buildString {
            append("MESSAGE:")
            append("PHONE=${URLEncoder.encode(phone, "UTF-8")};")
            append("MESSAGE=${URLEncoder.encode(message, "UTF-8")};")
            if (subject.isNotBlank()) {
                append("SUBJECT=${URLEncoder.encode(subject, "UTF-8")};")
            }
        }
    }

    fun getMessagePreview(maxLength: Int = 50): String {
        return if (message.length <= maxLength) {
            message
        } else {
            message.take(maxLength) + "..."
        }
    }


    companion object {
        fun parseSmsString(smsString: String): MessageModel {
            return try {
                val upper = smsString.uppercase()
                when {
                    upper.startsWith("MESSAGE:") -> parseFromQRString(smsString)
                    upper.startsWith("SMS:") || upper.startsWith("SMSTO:") ||
                    upper.startsWith("MMS:") -> parseFromUri(smsString)
                    else -> parseLegacyFormat(smsString)
                }
            } catch (e: Exception) {
                MessageModel()
            }
        }

        private fun parseFromQRString(qrString: String): MessageModel {
            val model = MessageModel()
            val content = qrString.removePrefix("MESSAGE:")
            val params = content.split(";")

            params.forEach { param ->
                when {
                    param.startsWith("PHONE=") -> model.phone =
                        URLDecoder.decode(param.removePrefix("PHONE="), "UTF-8")
                    param.startsWith("MESSAGE=") -> model.message =
                        URLDecoder.decode(param.removePrefix("MESSAGE="), "UTF-8")
                    param.startsWith("SUBJECT=") -> model.subject =
                        URLDecoder.decode(param.removePrefix("SUBJECT="), "UTF-8")
                }
            }

            return model
        }

        private fun parseFromUri(uriString: String): MessageModel {
            val model = MessageModel()
            val uri = android.net.Uri.parse(uriString)
            val phoneNumbers = uri.schemeSpecificPart.substringBefore("?").split(",")
            if (phoneNumbers.isNotEmpty()) {
                model.phone = phoneNumbers[0]
            }
            model.message = uri.getQueryParameter("body") ?: ""
            model.subject = uri.getQueryParameter("subject") ?: ""
            return model
        }

        private fun parseLegacyFormat(smsString: String): MessageModel {
            val model = MessageModel()
            if (smsString.startsWith("sms:")) {
                val content = smsString.removePrefix("sms:").trim()
                val parts = content.split(":", limit = 2)
                if (parts.size == 2) {
                    model.phone = parts[0].trim()
                    model.message = parts[1].trim()
                }
            }
            return model
        }
    }
}
// Extension functions for phone number validation
fun String.isValidPhoneNumber(): Boolean {
    val cleaned = this.replace("[^\\d+]".toRegex(), "")
    return cleaned.length in 7..15 && cleaned.all { it.isDigit() || it == '+' }
}
// Utility object for message operations
object MessageUtils {
    fun shareMessage(context: Context, message: MessageModel) {
        val shareText = message.toShareableText()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Message")
        }
        context.startActivity(Intent.createChooser(intent, "Share Message"))
    }

    fun sendMessage(context: Context, message: MessageModel) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.toSmsUri()))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to SMS
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(message.toSmsUri()))
            context.startActivity(fallbackIntent)
        }
    }

    fun copyToClipboard(context: Context, message: MessageModel, label: String = "Message") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, message.toShareableText())
        clipboard.setPrimaryClip(clip)
    }
}