@file:Suppress("DEPRECATION")

package com.azg.alf_002_qrscan.model

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.Serializable

data class WifiModel(
    var name: String = "",
    var pass: String = "",
    var security: String = ""
) : Serializable {

    enum class SecurityType {
        NONE, WEP, WPA, WPA2, WPA3, UNKNOWN
    }

    fun isValid(): Boolean {
        return name.isNotBlank() && when (getSecurityType()) {
            SecurityType.NONE -> true
            SecurityType.WEP -> pass.length in 5..13
            SecurityType.WPA, SecurityType.WPA2, SecurityType.WPA3 -> pass.length >= 8
            else -> false
        }
    }

    fun getSecurityType(): SecurityType {
        return when (security.uppercase()) {
            "WEP" -> SecurityType.WEP
            "WPA" -> SecurityType.WPA
            "WPA2" -> SecurityType.WPA2
            "WPA3" -> SecurityType.WPA3
            "NOPASS", "OPEN" -> SecurityType.NONE
            else -> SecurityType.UNKNOWN
        }
    }

    fun toQrString(): String {
        return buildString {
            append("WIFI:")
            append("S:${escapeField(name)};")
            append("T:${getSecurityType().name};")
            if (pass.isNotBlank()) {
                append("P:${escapeField(pass)};")
            }
        }.removeSuffix(";")
    }

    fun toShareableText(): String {
        return buildString {
            append("WiFi Network: $name\n")
            append("Security: ${getSecurityType().name}\n")
            if (pass.isNotBlank()) {
                append("Password: $pass\n")
            }
        }.trim()
    }

    fun getSignalStrength(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSignalStrengthApi29Plus(context)
        } else {
            getSignalStrengthLegacy(context)
        }
    }

    @Suppress("DEPRECATION")
    private fun getSignalStrengthLegacy(context: Context): Int {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return if (wifiInfo.ssid?.removeSurrounding("\"") == name) {
            WifiManager.calculateSignalLevel(wifiInfo.rssi, 5)
        } else {
            -1
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSignalStrengthApi29Plus(context: Context): Int {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfos = wifiManager.scanResults
        val networkInfo = wifiInfos.find { it.SSID == name }
        return networkInfo?.let { WifiManager.calculateSignalLevel(it.level, 5) } ?: -1
    }

    fun isConnected(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid?.removeSurrounding("\"") == name
    }

    private fun escapeField(field: String): String {
        return field.replace(";", "\\;")
            .replace(":", "\\:")
            .replace("\\", "\\\\")
    }

    companion object {
        fun parseWifiString(wifiString: String): WifiModel? {
            if (!wifiString.startsWith("WIFI:", ignoreCase = true)) return null

            try {
                val model = WifiModel()
                val content = wifiString.removePrefix("WIFI:").removePrefix("wifi:")
                val params = content.split(";")

                params.forEach { param ->
                    when {
                        param.startsWith("S:") -> model.name = unescapeField(param.removePrefix("S:"))
                        param.startsWith("P:") -> model.pass = unescapeField(param.removePrefix("P:"))
                        param.startsWith("T:") -> model.security = param.removePrefix("T:")
                    }
                }

                return if (model.name.isNotBlank()) model else null
            } catch (e: Exception) {
                return null
            }
        }

        fun createOpenNetwork(name: String, isHidden: Boolean = false): WifiModel {
            return WifiModel(
                name = name,
                security = "NOPASS",
            )
        }

        fun createWPA2Network(name: String, password: String, isHidden: Boolean = false): WifiModel {
            return WifiModel(
                name = name,
                pass = password,
                security = "WPA2",
            )
        }

        fun createWPA3Network(name: String, password: String, isHidden: Boolean = false): WifiModel {
            return WifiModel(
                name = name,
                pass = password,
                security = "WPA3")
        }

        private fun unescapeField(field: String): String {
            return field.replace("\\;", ";")
                .replace("\\:", ":")
                .replace("\\\\", "\\")
        }
    }
}

// Enhanced WiFi connection functions
fun connectToWifi(context: Context, wifiModel: WifiModel): Boolean {
    return if (wifiModel.isValid()) {
        connectToWifi(context, wifiModel.name, wifiModel.pass, wifiModel.getSecurityType())
    } else {
        false
    }
}

fun connectToWifi(context: Context, ssid: String, password: String, securityType: WifiModel.SecurityType): Boolean {
    return try {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> connectToWifiApi29Plus(context, ssid, password, securityType)
            else -> connectToWifiLegacy(context, ssid, password, securityType)
        }
    } catch (e: Exception) {
        false
    }
}

@Suppress("DEPRECATION")
private fun connectToWifiLegacy(context: Context, ssid: String, password: String, securityType: WifiModel.SecurityType): Boolean {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val wifiConfig = WifiConfiguration().apply {
        SSID = "\"$ssid\""
        when (securityType) {
            WifiModel.SecurityType.NONE -> {
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            }
            WifiModel.SecurityType.WEP -> {
                wepKeys[0] = "\"$password\""
                wepTxKeyIndex = 0
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104)
            }
            WifiModel.SecurityType.WPA -> {
                preSharedKey = "\"$password\""
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            }
            WifiModel.SecurityType.WPA2 -> {
                preSharedKey = "\"$password\""
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            }
            else -> {
                // Default to WPA2 for unknown types
                preSharedKey = "\"$password\""
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            }
        }
    }

    val netId = wifiManager.addNetwork(wifiConfig)
    return if (netId != -1) {
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()
        true
    } else {
        false
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun connectToWifiApi29Plus(context: Context, ssid: String, password: String, securityType: WifiModel.SecurityType): Boolean {
    return try {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val suggestion = when (securityType) {
            WifiModel.SecurityType.NONE -> {
                WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .build()
            }
            WifiModel.SecurityType.WPA2 -> {
                WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build()
            }
            WifiModel.SecurityType.WPA3 -> {
                WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa3Passphrase(password)
                    .build()
            }
            else -> {
                // Fallback to WPA2 for other security types
                WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build()
            }
        }

        val suggestionsList = listOf(suggestion)
        val status = wifiManager.addNetworkSuggestions(suggestionsList)

        status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS
    } catch (e: Exception) {
        false
    }
}

// WiFi utility functions
object WifiUtils {
    fun scanForNetworks(context: Context): List<WifiModel> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scanNetworksApi29Plus(context)
        } else {
            scanNetworksLegacy(context)
        }
    }

    @Suppress("DEPRECATION")
    private fun scanNetworksLegacy(context: Context): List<WifiModel> {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        val results = wifiManager.scanResults
        return results.map { scanResult ->
            WifiModel(
                name = scanResult.SSID,
                security = getSecurityTypeFromCapabilities(scanResult.capabilities),
            )
        }.distinctBy { it.name }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun scanNetworksApi29Plus(context: Context): List<WifiModel> {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val results = wifiManager.scanResults
        return results.map { scanResult ->
            WifiModel(
                name = scanResult.SSID,
                security = getSecurityTypeFromCapabilities(scanResult.capabilities),
            )
        }.distinctBy { it.name }
    }

    private fun getSecurityTypeFromCapabilities(capabilities: String): String {
        return when {
            capabilities.contains("WPA3") -> "WPA3"
            capabilities.contains("WPA2") -> "WPA2"
            capabilities.contains("WPA") -> "WPA"
            capabilities.contains("WEP") -> "WEP"
            else -> "NOPASS"
        }
    }

    fun isWifiEnabled(context: Context): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

    fun toggleWifi(context: Context, enable: Boolean) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = enable
    }

    fun getCurrentNetwork(context: Context): WifiModel? {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return if (wifiInfo.ssid != null && wifiInfo.ssid != "<unknown ssid>") {
            WifiModel(
                name = wifiInfo.ssid.removeSurrounding("\""),
            )
        } else {
            null
        }
    }
}

// Extension functions for List<WifiModel>
fun List<WifiModel>.filterOpenNetworks(): List<WifiModel> {
    return this.filter { it.getSecurityType() == WifiModel.SecurityType.NONE }
}

fun List<WifiModel>.filterSecureNetworks(): List<WifiModel> {
    return this.filter { it.getSecurityType() != WifiModel.SecurityType.NONE }
}

fun List<WifiModel>.sortBySignalStrength(context: Context): List<WifiModel> {
    return this.sortedByDescending { it.getSignalStrength(context) }
}