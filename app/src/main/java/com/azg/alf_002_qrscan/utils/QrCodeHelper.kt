package com.azg.alf_002_qrscan.utils

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Patterns
import androidx.core.content.FileProvider
import com.azg.alf_002_qrscan.BuildConfig
import com.azg.alf_002_qrscan.model.ResultScan
import com.create.custom.qr.QrType
import com.create.custom.qr.vector.QrCodeDrawable
import com.create.custom.qr.vector.createQrVectorOptions
import com.create.custom.qr.vector.style.QrVectorColor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLEncoder
import java.util.regex.Pattern

object QrCodeHelper {
    fun generateQrCode(
        input: String,
        onSuccess: (Drawable) -> Unit
    ) {
        val qrVectorOptions = createQrVectorOptions {
            padding = 0.125f
            background {
                color = QrVectorColor.Solid(Color.WHITE)
            }
            colors {
                dark = QrVectorColor.Solid(Color.BLACK)
            }
        }
        val drawable = QrCodeDrawable(
            data = { input },
            options = qrVectorOptions,
            charset = Charsets.UTF_8
        )
        onSuccess(drawable)
    }

    fun getContentGenerate(
        input: String,
        type: QrType
    ): String {
        return when (type) {
            QrType.Website -> {
                input
            }
            QrType.Wifi -> {
                input
            }
            QrType.Email -> {
                input
            }
            QrType.Contact -> {
                input
            }
            QrType.Text -> {
                input
            }
            QrType.Message -> {
                input
            }
            QrType.Location -> {
                input
            }
            QrType.Event -> {
                input
            }
            else -> input
        }
    }

    fun getResultValue(rawValue: String): ResultScan {
        return if (isWebsite(rawValue)) {
            ResultScan(QrType.Website, rawValue, listOf(rawValue))
        } else if (isContact(rawValue)) {
            getContact(rawValue)
        } else if (isEmail(rawValue)) {
            getEmail(rawValue)
        } else if (rawValue.lowercase().contains("barcode:")) {
            val result = rawValue.replace("barcode:", "").replace("barcode%3A", "")
            ResultScan(QrType.Barcode, result, listOf(result))
        } else if (rawValue.lowercase().contains("wifi:")) {
            getWifi(rawValue)
        } else if (rawValue.lowercase().contains("smsto:")) {
            getMessage(rawValue)
        } else if (rawValue.lowercase().contains("geo:")) {
            getLocation(rawValue)
        } else if (rawValue.lowercase().contains("begin:vevent")) {
            getEvent(rawValue)
        } else {
            ResultScan(QrType.Text, rawValue, listOf(rawValue))
        }
    }

    private fun isWebsite(input: String): Boolean {
        return input.lowercase().startsWith("http://") || input.lowercase().startsWith("https://")
    }

    private fun isContact(input: String): Boolean {
        return input.lowercase().contains("begin:vcard") || input.lowercase().contains("mecard")
    }

    private fun isEmail(input: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches()
                || input.lowercase().contains("mailto:")
                || input.lowercase().contains("matmsg:")
    }

    private fun getContact(input: String): ResultScan {
        var result = ""
        var name = ""
        var org = ""
        var title = ""
        var tel = ""
        var email = ""
        var adr = ""
        var note = ""
        var url = ""
        val listContent = mutableListOf<String>()
        try {
            var matcher = Pattern.compile("N:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                name = matcher.group(1) ?: ""
                if (input.lowercase().contains("mecard")) {
                    name = name.substringBefore(";")
                }
            }
            matcher = Pattern.compile("FN:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                name = matcher.group(1) ?: ""
                if (input.lowercase().contains("mecard")) {
                    name = name.substringBefore(";")
                }
            }
            matcher = Pattern.compile("ORG:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                org = matcher.group(1) ?: ""
                if (input.lowercase().contains("mecard")) {
                    org = org.substringBefore(";")
                }
            }
            matcher = Pattern.compile("TITLE:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                title = matcher.group(1) ?: ""
                if (input.lowercase().contains("mecard")) {
                    title = title.substringBefore(";")
                }
            }
            if (input.lowercase().contains("begin:vcard")) {
                matcher = Pattern.compile("TEL:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    tel = tel + "\n" + matcher.group(1)
                }
                matcher = Pattern.compile("EMAIL:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    email = matcher.group(1) ?: ""
                }
                matcher = Pattern.compile("ADR:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    adr = adr + "\n" + matcher.group(1)
                }
                if (adr.contains(";")) {
                    val adrArray = adr.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                    adr = ""
                    for (a in adrArray) {
                        adr = adr + a + "\n"
                    }
                }
                matcher = Pattern.compile("URL:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    url = url + "\n" + matcher.group(1)
                }
            } else {
                matcher = Pattern.compile("TEL:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    tel = matcher.group(1) ?: ""
                    tel = tel.substringBefore(";")
                }
                matcher = Pattern.compile("EMAIL:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    email = matcher.group(1) ?: ""
                    email = email.take(email.indexOf(";"))
                }
                matcher = Pattern.compile("ADR:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    adr = matcher.group(1) ?: ""
                    adr = adr.substringBefore(";")
                }
            }
            matcher = Pattern.compile("NOTE:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                note = matcher.group(1) ?: ""
                if (input.lowercase().contains("mecard")) {
                    note = note.substringBefore(";")
                }
            }

            listContent.add(name)
            listContent.add(org)
            listContent.add(title)
            listContent.add(tel.trim())
            listContent.add(email.trim())
            listContent.add(adr.trim())
            listContent.add(note)

            if (name.isNotEmpty()) {
                result = "$result<b>Name: </b>$name<br><br>"
            }
            if (title.isNotEmpty()) {
                result = "$result<b>Job title</b>: $title<br><br>"
            }
            if (org.isNotEmpty()) {
                result = "$result<b>Company:</b> $org<br><br>"
            }
            if (adr.replace("\n", "").trim { it <= ' ' }.isNotEmpty()) {
                result = "$result<b>Address</b>: $adr<br><br>"
            }
            if (tel.replace("\n", "").trim { it <= ' ' }.isNotEmpty()) {
                result = "$result<b>Phone:</b> $tel<br><br>"
            }
            if (email.isNotEmpty()) {
                result = "$result<b>Email:</b> $email<br><br>"
            }
            if (note.isNotEmpty()) {
                result = "$result<b>Note:</b> $note"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ResultScan(QrType.Contact, result, listContent)
    }

    private fun getEmail(input: String): ResultScan {
        val listContent = mutableListOf<String>()
        var result = input
        if (input.lowercase().contains("mailto:")) {
            listContent.clear()
            try {
                result = result.replace("mailto:", "")
                result = result.replace("MAILTO:", "")
                listContent.add(result)
            } catch (e: Exception) {
                e.printStackTrace()
                return ResultScan(QrType.Email, result, listContent)
            }
            return ResultScan(QrType.Email, result, listContent)
        } else if (result.lowercase().contains("matmsg:")) {
            listContent.clear()
            try {
                var email = ""
                var sub = ""
                var body = ""
                var matcher = Pattern.compile("to:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    email = matcher.group(1) ?: ""
                    email = email.substringBefore(";")
                }
                matcher = Pattern.compile("sub:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    sub = matcher.group(1) ?: ""
                    sub = sub.substringBefore(";")
                }
                result = result.replace("\n", " ")
                matcher = Pattern.compile("body:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
                while (matcher.find()) {
                    body = matcher.group(1) ?: ""
                    body = body.substringBefore(";")
                }
                result = ""
                listContent.add(email)
                if (email.isNotEmpty()) {
                    result = result + "E-mail: " + email + " "
                }
                listContent.add(sub)
                if (sub.isNotEmpty()) {
                    result = result + "Subject: " + sub + " "
                }
                listContent.add(body)
                if (body.isNotEmpty()) {
                    result = result + "Content: " + body + " "
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ResultScan(QrType.Email, result, listContent)
        } else {
            return ResultScan(QrType.Email, result, listContent)
        }
    }

    private fun getWifi(input: String): ResultScan {
        val listContent = mutableListOf<String>()
        var result = input
        try {
            var ssid = ""
            var type = ""
            var password = ""
            var hidden = ""
            var matcher = Pattern.compile("s:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                ssid = matcher.group(1) ?: ""
                ssid = ssid.substringBefore(";")
            }
            matcher = Pattern.compile("t:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                type = matcher.group(1) ?: ""
                type = type.substringBefore(";")
            }
            matcher = Pattern.compile("p:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                password = matcher.group(1) ?: ""
                password = password.substringBefore(";")
            }
            if (type == "") type = "nopass"
            matcher = Pattern.compile("h:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                hidden = matcher.group(1) ?: ""
                hidden = hidden.substringBefore(";")
            }
            if (type.trim() == "NONE") {
                listContent.add(ssid)
                listContent.add(type)
                listContent.add(hidden)
                result =
                    "<b>SSID:</b> $ssid<br><br><b>Sercurity:</b> $type<br><br><b>Hidden:</b> $hidden"
            } else {
                listContent.add(ssid)
                listContent.add(type)
                listContent.add(password)
                listContent.add(hidden)
                result =
                    "<b>SSID:</b> $ssid<br><br><b>Sercurity:</b> $type<br><br><b>Password:</b> $password<br><br><b>Hidden:</b> $hidden"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ResultScan(QrType.Wifi, result, listContent)
    }

    private fun getMessage(input: String): ResultScan {
        val listContent = mutableListOf<String>()
        var result = input
        try {
            val number: String
            var message = ""
            result = result.replace("SMSTO:", "")
            result = result.replace("smsto:", "")
            if (result.contains(":")) {
                val list = result.split(":")
                number = list[0]
                if (list.size > 1) {
                    for (i in 1 until list.size) {
                        message = if (message == "") list[i]
                        else message + ":" + list[i]
                    }
                }
                result = "<b>Phone:</b> $number<br><br><b>Content:</b> $message"
                listContent.add(number.trim())
                listContent.add(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ResultScan(QrType.Message, result, listContent)
    }

    private fun getLocation(input: String): ResultScan {
        val listContent = mutableListOf<String>()
        val latLon = input.replace("GEO:", "").replace("geo:", "")
        val lon = latLon.substringBefore(",")
        val lat = latLon.substring(latLon.indexOf(",") + 1)
        listContent.add(lon)
        listContent.add(lat)
        return ResultScan(QrType.Location, input, listContent)
    }

    private fun getEvent(input: String): ResultScan {
        val listContent = mutableListOf<String>()
        var titleEvent = ""
        var dateStart = ""
        var dateEnd = ""
        var isAllDay: String
        var content = ""
        var matcher = Pattern.compile("Y:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
        while (matcher.find()) {
            titleEvent = matcher.group(1) ?: ""
        }
        if (input.lowercase().contains("value=date")) {
            isAllDay = "true"
            matcher =
                Pattern.compile("DTSTART;VALUE=DATE:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                dateStart = matcher.group(1) ?: ""
            }
            matcher =
                Pattern.compile("DTEND;VALUE=DATE:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
        } else {
            isAllDay = "false"
            matcher = Pattern.compile("DTSTART:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
            while (matcher.find()) {
                dateStart = matcher.group(1) ?: ""
            }
            matcher = Pattern.compile("DTEND:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
        }
        while (matcher.find()) {
            dateEnd = matcher.group(1) ?: ""
        }
        matcher = Pattern.compile("DESCRIPTION:(.*)", Pattern.CASE_INSENSITIVE).matcher(input)
        while (matcher.find()) {
            content = matcher.group(1) ?: ""
        }
        listContent.add(titleEvent)
        listContent.add(dateStart)
        listContent.add(dateEnd)
        listContent.add(isAllDay)
        listContent.add(content)
        return ResultScan(QrType.Event, input, listContent)
    }

    fun searchOnWeb(context: Context, input: String) {
        try {
            val query = input.trim()
            if (query.isEmpty()) return

            if (query.startsWith("http://") || query.startsWith("https://")) {
                val textSearch = URLEncoder.encode(query, "UTF-8")
                val uri = Uri.parse("http://www.google.com/search?q=$textSearch")
                context.startActivity(Intent(Intent.ACTION_VIEW, uri).withNewTaskIfNeeded(context))
            } else {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                    .putExtra(SearchManager.QUERY, query)
                    .withNewTaskIfNeeded(context)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    val searchQuery = Uri.encode(query)
                    val fallbackIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/search?q=$searchQuery")
                    ).withNewTaskIfNeeded(context)
                    context.startActivity(fallbackIntent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Intent.withNewTaskIfNeeded(context: Context): Intent {
        return if (context is Activity) this else addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    private fun saveContactToFile(data: String, context: Context): Uri {
        val root = context.cacheDir.toString()
        val myDir = File("$root/qrcode")
        if (!myDir.exists()) myDir.mkdirs()
        val file = File(myDir, "contact.vcf")
        if (file.exists()) file.delete()
        try {
            file.createNewFile()
            val fos = FileOutputStream(file, true)
            fos.write(data.toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }
}
