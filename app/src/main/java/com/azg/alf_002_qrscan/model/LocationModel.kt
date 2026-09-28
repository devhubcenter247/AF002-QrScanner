package com.azg.alf_002_qrscan.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class LocationModel(
    var latitude: String = "",
    var longitude: String = "",
    var query: String = "",
    var label: String = ""
) : Serializable {
    fun isValid(): Boolean {
        return try {
            val lat = latitude.toDouble()
            val lon = longitude.toDouble()
            lat in -90.0..90.0 && lon in -180.0..180.0
        } catch (e: Exception) {
            false
        }
    }

    fun hasCoordinates(): Boolean {
        return latitude.isNotBlank() && longitude.isNotBlank()
    }

    fun hasQuery(): Boolean {
        return query.isNotBlank()
    }
    fun toQrString(): String {
        return buildString {
            append("geo:$latitude,$longitude")
            val params = mutableListOf<String>()
            if (query.isNotBlank()) {
                params.add("q=${Uri.encode(query)}")
            }
            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
    }
    override fun toString(): String {
        return buildString {
            append("geo:$latitude,$longitude")
            val params = mutableListOf<String>()
            if (query.isNotBlank()) {
                params.add("q=${Uri.encode(query)}")
            }
            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
    }

    fun toGoogleMapsUri(): String {
        return if (hasCoordinates()) {
            "https://maps.google.com/?q=$latitude,$longitude"
        } else if (hasQuery()) {
            "https://maps.google.com/?q=${Uri.encode(query)}"
        } else {
            "https://maps.google.com/"
        }
    }

    fun toAppleMapsUri(): String {
        return if (hasCoordinates()) {
            "https://maps.apple.com/?ll=$latitude,$longitude"
        } else if (hasQuery()) {
            "https://maps.apple.com/?q=${Uri.encode(query)}"
        } else {
            "https://maps.apple.com/"
        }
    }

    fun toOpenStreetMapUri(): String {
        return if (hasCoordinates()) {
            "https://www.openstreetmap.org/?mlat=$latitude&mlon=$longitude"
        } else if (hasQuery()) {
            "https://www.openstreetmap.org/search?query=${Uri.encode(query)}"
        } else {
            "https://www.openstreetmap.org/"
        }
    }

    fun getCoordinates(): Pair<Double, Double>? {
        return try {
            Pair(latitude.toDouble(), longitude.toDouble())
        } catch (e: Exception) {
            null
        }
    }

    fun toDMS(): String {
        return try {
            val lat = latitude.toDouble()
            val lon = longitude.toDouble()
            "${convertToDMS(lat, "N", "S")} ${convertToDMS(lon, "E", "W")}"
        } catch (e: Exception) {
            "Invalid coordinates"
        }
    }

    fun toIntentUri(): String {
        return if (hasCoordinates()) {
            "geo:$latitude,$longitude?q=${Uri.encode(query)}"
        } else {
            "geo:0,0?q=${Uri.encode(query)}"
        }
    }

    fun toShareableText(): String {
        return buildString {
            if (label.isNotBlank()) {
                append("$label\n")
            }
            if (hasCoordinates()) {
                append("Coordinates: $latitude, $longitude\n")
            }
            if (hasQuery()) {
                append("Location: $query\n")
            }
        }.trim()
    }

    companion object {
        fun fromString(geoUri: String): LocationModel? {
            if (!geoUri.startsWith("geo:", ignoreCase = true)) return null
            try {
                val cleanUri = geoUri.removePrefix("geo:").removePrefix("GEO:")
                val parts = cleanUri.split("?")
                val coordinatePart = parts[0]
                val queryPart = if (parts.size > 1) parts[1] else ""
                val coordinates = coordinatePart.split(",")
                if (coordinates.size < 2) return null
                val latitude = coordinates[0]
                val longitude = coordinates[1]
                var query = ""
                var label = ""

                if (queryPart.isNotBlank()) {
                    val params = queryPart.split("&")
                    params.forEach { param ->
                        when {
                            param.startsWith("q=") -> query = Uri.decode(param.removePrefix("q="))
                            param.startsWith("label=") -> label =
                                Uri.decode(param.removePrefix("label="))
                        }
                    }
                }

                return LocationModel(
                    latitude = latitude,
                    longitude = longitude,
                    query = query,
                    label = label
                )
            } catch (e: Exception) {
                return null
            }
        }

        fun fromCoordinates(lat: Double, lon: Double, label: String = ""): LocationModel {
            return LocationModel(
                latitude = lat.toString(),
                longitude = lon.toString(),
                label = label
            )
        }

        fun fromQuery(query: String, label: String = ""): LocationModel {
            return LocationModel(
                query = query,
                label = label
            )
        }

        private fun calculateHaversineDistance(
            lat1: Double,
            lon1: Double,
            lat2: Double,
            lon2: Double
        ): Double {
            val R = 6371000.0 // Earth radius in meters
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return R * c
        }

        private fun convertToDMS(coord: Double, positiveDir: String, negativeDir: String): String {
            val direction = if (coord >= 0) positiveDir else negativeDir
            val absCoord = abs(coord)
            val degrees = absCoord.toInt()
            val minutes = ((absCoord - degrees) * 60).toInt()
            val seconds = ((absCoord - degrees - minutes / 60.0) * 3600)

            return String.format("%d°%d'%.1f\"%s", degrees, minutes, seconds, direction)
        }
    }
}

fun List<LocationModel>.filterValid(): List<LocationModel> {
    return this.filter { it.isValid() }
}

object LocationUtils {
    fun createGeoIntent(location: LocationModel): String {
        return location.toIntentUri()
    }

    fun shareLocation(context: Context, location: LocationModel) {
        val shareText = location.toShareableText()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_SUBJECT, "Location")
        }
        context.startActivity(Intent.createChooser(intent, "Share Location"))
    }

    fun openInMaps(context: Context, location: LocationModel) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(location.toGoogleMapsUri()))
        context.startActivity(intent)
    }
}