package com.azg.alf_002_qrscan.model

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import kotlinx.serialization.json.Json

data class EmailModel(
    var email: String = "",
    var subject: String = "",
    var body: String = "",
    var cc: List<String> = emptyList()
) {
    fun isValid(): Boolean {
        return email.isValidEmail()
    }

    fun hasRecipients(): Boolean {
        return email.isValidEmail() || cc.any { it.isValidEmail() }
    }

    fun getAllRecipients(): List<String> {
        return listOf(email) + cc
    }

    fun getValidRecipients(): List<String> {
        return getAllRecipients().filter { it.isValidEmail() }
    }
}

fun String.fromQrData(): EmailModel? {
    return try {
        if (!this.startsWith("mailto:", ignoreCase = true)) {
            return null
        }
        val uri = this.toUri()
        val email = uri.schemeSpecificPart.substringBefore("?")
        val subject = uri.getQueryParameter("subject") ?: ""
        val body = uri.getQueryParameter("body") ?: ""
        val cc = uri.getQueryParameter("cc")?.split(",")?.map { it.trim() } ?: emptyList()

        EmailModel(
            email = email,
            subject = subject,
            body = body,
            cc = cc,
        )
    } catch (e: Exception) {
        null
    }
}
// Enhanced QR string generation
fun EmailModel.toQrString(): String {
    return buildString {
        append("mailto:$email")
        val params = mutableListOf<String>()
        if (subject.isNotBlank()) {
            params.add("subject=${Uri.encode(subject)}")
        }
        if (body.isNotBlank()) {
            params.add("body=${Uri.encode(body)}")
        }
        if (cc.isNotEmpty()) {
            params.add("cc=${Uri.encode(cc.joinToString(","))}")
        }

        if (params.isNotEmpty()) {
            append("?${params.joinToString("&")}")
        }
    }
}
// Email validation
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
// Email intent creation
fun EmailModel.toIntent(): Intent {
    return Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(this@toIntent.toQrString())
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}
// Share email via intent
fun EmailModel.shareViaEmail(context: Context) {
    try {
        val intent = this.toIntent()
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to generic send action
            createFallbackEmailIntent(context)
        }
    } catch (e: Exception) {
        createFallbackEmailIntent(context)
    }
}

private fun EmailModel.createFallbackEmailIntent(context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        if (cc.isNotEmpty()) {
            putExtra(Intent.EXTRA_CC, cc.toTypedArray())
        }
    }

    context.startActivity(Intent.createChooser(intent, "Send email via..."))
}
// Copy email data to clipboard
fun EmailModel.copyToClipboard(context: Context, label: String = "Email Data") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, this.toQrString())
    clipboard.setPrimaryClip(clip)
}
// Create deep link for sharing
fun EmailModel.toDeepLink(): String {
    val json = Json.encodeToString(this)
    val encoded = Base64.encodeToString(json.toByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
    return "yourapp://email?data=$encoded"
}
// Parse from deep link
fun String.fromDeepLink(): EmailModel? {
    return try {
        if (!this.startsWith("yourapp://email")) return null
        val uri = this.toUri()
        val data = uri.getQueryParameter("data") ?: return null
        val json = String(Base64.decode(data, Base64.URL_SAFE or Base64.NO_WRAP))
        Json.decodeFromString<EmailModel>(json)
    } catch (e: Exception) {
        null
    }
}

fun EmailModel.validate(): EmailValidationResult {
    return when {
        !hasRecipients() -> EmailValidationResult.NO_RECIPIENTS
        getValidRecipients().isEmpty() -> EmailValidationResult.INVALID_RECIPIENTS
        subject.length > 200 -> EmailValidationResult.SUBJECT_TOO_LONG
        body.length > 10000 -> EmailValidationResult.BODY_TOO_LONG
        else -> EmailValidationResult.VALID
    }
}

enum class EmailValidationResult {
    VALID, NO_RECIPIENTS, INVALID_RECIPIENTS, SUBJECT_TOO_LONG, BODY_TOO_LONG
}
// Extension functions for List<Email>
fun List<EmailModel>.filterValid(): List<EmailModel> {
    return this.filter { it.hasRecipients() }
}

fun List<EmailModel>.findByRecipient(email: String): List<EmailModel> {
    return this.filter {
        it.getAllRecipients().any { recipient -> recipient.equals(email, ignoreCase = true) }
    }
}

fun List<EmailModel>.groupByDomain(): Map<String, List<EmailModel>> {
    return this.groupBy { it.email.substringAfter("@") }
}
