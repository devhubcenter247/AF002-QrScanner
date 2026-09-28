package com.azg.alf_002_qrscan.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import java.io.File

data class ContactModel(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val company: String = "",
    val jobTitle: String = "",
    val address: String = "",
    val note: String = ""
) {
    fun toQrString(): String {
        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            if (name.isNotBlank()) {
                appendLine("FN:$name")
                // Try to split name for structured name field
                val nameParts = name.trim().split("\\s+".toRegex())
                if (nameParts.size >= 2) {
                    val familyName = nameParts.last()
                    val givenName = nameParts.dropLast(1).joinToString(" ")
                    appendLine("N:$familyName;$givenName;;;")
                } else {
                    appendLine("N:;$name;;;")
                }
            }
            if (phone.isNotBlank()) {
                val phones = phone.split(",").map { it.trim() }
                phones.forEach { phone ->
                    if (phone.isNotBlank()) {
                        appendLine("TEL;TYPE=CELL:$phone")
                    }
                }
            }
            if (email.isNotBlank()) {
                val emails = email.split(",").map { it.trim() }
                emails.forEach { email ->
                    if (email.isNotBlank()) {
                        appendLine("EMAIL;TYPE=INTERNET:$email")
                    }
                }
            }

            if (company.isNotBlank()) appendLine("ORG:$company")
            if (jobTitle.isNotBlank()) appendLine("TITLE:$jobTitle")
            // Format address properly
            if (address.isNotBlank()) {
                appendLine("ADR;TYPE=WORK:;;$address;;;;")
            }

            if (note.isNotBlank()) appendLine("NOTE:${note.replace("\n", "\\n")}")

            appendLine("END:VCARD")
        }.trim()
    }

    fun isValid(): Boolean {
        return name.isNotBlank() || phone.isNotBlank() || email.isNotBlank()
    }
}
fun getContactFromUri(context: Context, uri: Uri): ContactModel {
    val contentResolver = context.contentResolver

    var id: String? = null
    var name = ""
    var phone = ""
    var email = ""

    // Lấy id + name
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            id = cursor.getString(
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            )
            name = cursor.getString(
                cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
            ) ?: ""
        }
    }

    if (id != null) {
        // Phone
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(id),
            null
        )?.use { pCur ->
            if (pCur.moveToFirst()) {
                phone = pCur.getString(
                    pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                ) ?: ""
            }
        }

        // Email
        contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf(id),
            null
        )?.use { eCur ->
            if (eCur.moveToFirst()) {
                email = eCur.getString(
                    eCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS)
                ) ?: ""
            }
        }
    }

    return ContactModel(
        name = name,
        phone = phone,
        email = email
    )
}

fun String.toContactInfo(): ContactModel {
    val lines = this.split("\n")
    var name = ""
    var phone = ""
    var email = ""
    var company = ""
    var jobTitle = ""
    var address = ""
    var note = ""

    for (line in lines) {
        when {
            line.startsWith("FN:") -> name = line.substringAfter("FN:").trim()
            line.startsWith("N:") -> {
                if (name.isBlank()) {
                    val nameParts = line.substringAfter("N:").split(";")
                    if (nameParts.size >= 2) {
                        val familyName = nameParts[0].trim()
                        val givenName = nameParts[1].trim()
                        name = if (familyName.isNotBlank() && givenName.isNotBlank()) {
                            "$givenName $familyName"
                        } else givenName.ifBlank {
                            familyName
                        }
                    }
                }
            }
            line.startsWith("TEL") -> {
                val phoneNumber = line.substringAfter(":").trim()
                if (phone.isBlank()) {
                    phone = phoneNumber
                } else {
                    phone += ", $phoneNumber"
                }
            }
            line.startsWith("EMAIL") -> {
                val emailAddress = line.substringAfter(":").trim()
                if (email.isBlank()) {
                    email = emailAddress
                } else {
                    email += ", $emailAddress"
                }
            }
            line.startsWith("ORG:") -> company = line.substringAfter("ORG:").trim()
            line.startsWith("TITLE:") -> jobTitle = line.substringAfter("TITLE:").trim()
            line.startsWith("ADR:") -> {
                val addrParts = line.substringAfter("ADR:").split(";")
                if (addrParts.size >= 3) {
                    address = addrParts[2].trim()
                }
            }
            line.startsWith("NOTE:") -> note =
                line.substringAfter("NOTE:").replace("\\n", "\n").trim()
        }
    }

    return ContactModel(
        name = name,
        phone = phone,
        email = email,
        company = company,
        jobTitle = jobTitle,
        address = address,
        note = note
    )
}

fun insertContact(context: Context, contact: ContactModel) {
    try {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            if (contact.name.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
            }
            if (contact.phone.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.PHONE, contact.phone)
            }
            if (contact.email.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
            }
            if (contact.company.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.COMPANY, contact.company)
            }
            if (contact.jobTitle.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.jobTitle)
            }
            if (contact.address.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.POSTAL, contact.address)
            }
            if (contact.note.isNotBlank()) {
                putExtra(ContactsContract.Intents.Insert.NOTES, contact.note)
            }
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        saveContactAsVcf(context, contact)
    }
}

fun saveContactAsVcf(context: Context, contact: ContactModel) {
    try {
        val vcfContent = contact.toQrString()
        val fileName = "contact_${System.currentTimeMillis()}.vcf"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/x-vcard"
            putExtra(Intent.EXTRA_TITLE, fileName)
            putExtra(Intent.EXTRA_TEXT, vcfContent)
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        shareContactAsText(context, contact)
    }
}

fun shareContactAsText(context: Context, contact: ContactModel) {
    val shareText = buildString {
        if (contact.name.isNotBlank()) appendLine("Name: ${contact.name}")
        if (contact.phone.isNotBlank()) appendLine("Phone: ${contact.phone}")
        if (contact.email.isNotBlank()) appendLine("Email: ${contact.email}")
        if (contact.company.isNotBlank()) appendLine("Company: ${contact.company}")
        if (contact.jobTitle.isNotBlank()) appendLine("Job Title: ${contact.jobTitle}")
        if (contact.address.isNotBlank()) appendLine("Address: ${contact.address}")
        if (contact.note.isNotBlank()) appendLine("Note: ${contact.note}")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "Contact: ${contact.name}")
    }

    context.startActivity(Intent.createChooser(intent, "Share Contact"))
}

fun String.formatPhoneNumber(): String {
    return this.replace("[^\\d+]".toRegex(), "") // Remove all non-digit characters except +
}

fun ContactModel.saveToVcfFile(
    context: Context,
    fileName: String = "contact_${
        name.replace(
            "\\s+".toRegex(),
            "_"
        )
    }.vcf"
): File? {
    return try {
        val vcfContent = this.toQrString()
        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeText(vcfContent)
        file
    } catch (e: Exception) {
        null
    }
}