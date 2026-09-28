package com.azg.alf_002_qrscan.model

import android.content.Context
import android.content.Intent
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class EventModel(
    var eventName: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var location: String = "",
    var description: String = "",
) : Serializable {
    enum class EventStatus {
        CONFIRMED, TENTATIVE, CANCELLED
    }

    fun toQrString(): String {
        return buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:${escapeField(eventName)}")
            appendLine("DTSTART:${formatDateTime(startTime)}")
            appendLine("DTEND:${formatDateTime(endTime)}")
            if (location.isNotBlank()) appendLine("LOCATION:${escapeField(location)}")
            if (description.isNotBlank()) appendLine("DESCRIPTION:${escapeField(description)}")
            appendLine("END:VEVENT")
        }
    }

    fun toFullICalendar(): String {
        return buildString {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//YourApp//Event Generator//EN")
            appendLine("CALSCALE:GREGORIAN")
            appendLine("METHOD:PUBLISH")
            appendLine(toQrString())
            appendLine("END:VCALENDAR")
        }
    }

    fun isValid(): Boolean {
        return eventName.isNotBlank() && startTime.isNotBlank() && isDateTimeValid(startTime) && isDateTimeValid(
            endTime
        )
    }

    fun getDurationInMinutes(): Long {
        return try {
            val start = parseDateTime(startTime)
            val end = parseDateTime(endTime)
            (end.time - start.time) / (1000 * 60)
        } catch (e: Exception) {
            0L
        }
    }

    fun isAllDayEvent(): Boolean {
        return startTime.length == 8 && endTime.length == 8 // YYYYMMDD format
    }

    private fun escapeField(field: String): String {
        return field.replace("\n", "\\n")
            .replace(",", "\\,")
            .replace(";", "\\;")
    }

    private fun formatDateTime(dateTime: String): String {
        // Convert from readable format to iCal format (YYYYMMDDTHHmmss)
        return try {
            if (dateTime.length == 16) { // "yyyy-MM-dd HH:mm" format
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = sdf.parse(dateTime)
                val outputFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
                outputFormat.format(date)
            } else {
                dateTime // Assume already in correct format
            }
        } catch (e: Exception) {
            dateTime
        }
    }

    fun parseDateTime(dateTime: String): Date {
        return try {
            if (dateTime.contains('T')) {
                val format = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.getDefault())
                format.parse(dateTime)
            } else {
                val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                format.parse(dateTime)
            }
        } catch (e: Exception) {
            Date()
        }
    }

    private fun isDateTimeValid(dateTime: String): Boolean {
        return try {
            parseDateTime(dateTime)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private fun getCurrentTimestamp(): String {
            val format = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            return format.format(Date())
        }

        fun fromQRCodeString(qrCodeString: String): EventModel? {
            return try {
                val lines = qrCodeString.split("\n")
                val event = EventModel()

                for (line in lines) {
                    when {
                        line.startsWith("SUMMARY:") -> event.eventName =
                            unescapeField(line.substringAfter("SUMMARY:"))
                        line.startsWith("DTSTART:") -> {
                            val value = line.substringAfter("DTSTART:")
                            event.startTime =
                                if (value.contains(';')) value.substringAfterLast(':') else value
                        }
                        line.startsWith("DTEND:") -> {
                            val value = line.substringAfter("DTEND:")
                            event.endTime =
                                if (value.contains(';')) value.substringAfterLast(':') else value
                        }
                        line.startsWith("LOCATION:") -> event.location =
                            unescapeField(line.substringAfter("LOCATION:"))
                        line.startsWith("DESCRIPTION:") -> event.description =
                            unescapeField(line.substringAfter("DESCRIPTION:"))
                    }
                }

                if (event.isValid()) event else null
            } catch (e: Exception) {
                null
            }
        }

        fun createQuickEvent(
            name: String,
            start: Date,
            end: Date,
            location: String = ""
        ): EventModel {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return EventModel(
                eventName = name,
                startTime = format.format(start),
                endTime = format.format(end),
                location = location
            )
        }

        fun createAllDayEvent(name: String, date: Date, location: String = ""): EventModel {
            val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            return EventModel(
                eventName = name,
                startTime = format.format(date),
                endTime = format.format(date),
                location = location
            )
        }

        private fun unescapeField(field: String): String {
            return field.replace("\\n", "\n")
                .replace("\\,", ",")
                .replace("\\;", ";")
        }
    }
}
// Extension functions for List<EventModel>
fun List<EventModel>.filterUpcoming(): List<EventModel> {
    val now = Date()
    return this.filter { event ->
        try {
            event.parseDateTime(event.startTime).after(now)
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Adds [event] to the device calendar. Unlike [EventModel.parseDateTime] (which expects the
 * iCal `yyyyMMdd'T'HHmmss` format found in scanned VEVENT content), this parses the
 * "dd/MM/yyyy HH:mm" format that the create-flow date picker (`InputEventScreen`) stores.
 */
fun addEventToCalendar(context: Context, event: EventModel) {
    try {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val startTimeMs = format.parse(event.startTime)?.time ?: System.currentTimeMillis()
        val endTimeMs = format.parse(event.endTime)?.time ?: startTimeMs
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = android.provider.CalendarContract.Events.CONTENT_URI
            putExtra(android.provider.CalendarContract.Events.TITLE, event.eventName)
            putExtra(android.provider.CalendarContract.Events.DESCRIPTION, event.description)
            putExtra(android.provider.CalendarContract.Events.EVENT_LOCATION, event.location)
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeMs)
            putExtra(android.provider.CalendarContract.EXTRA_EVENT_END_TIME, endTimeMs)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun formatInstantToString(instant: Instant): String {
    return instant.atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun parseDateTimeFromString(dateTimeString: String): Instant? {
    return try {
        if (dateTimeString.isEmpty()) null
        else LocalDateTime.parse(dateTimeString).atZone(ZoneId.systemDefault()).toInstant()
    } catch (e: Exception) {
        null
    }
}

fun formatDateTimeForDisplay(dateTimeString: String): String {
    return try {
        if (dateTimeString.isEmpty()) ""
        else {
            val instant = parseDateTimeFromString(dateTimeString)
            instant?.atZone(ZoneId.systemDefault())
                ?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"))
                ?: ""
        }
    } catch (e: Exception) {
        dateTimeString
    }
}

fun List<EventModel>.sortByDate(): List<EventModel> {
    return this.sortedBy { event ->
        try {
            event.parseDateTime(event.startTime).time
        } catch (e: Exception) {
            0L
        }
    }
}

