package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName

data class AdUnitConfig(
    @SerializedName("enable_ad")
    var enableAd: Boolean,
    @SerializedName("adunit")
    val adUnit: String,
)

data class SplashAdConfig(
    @SerializedName("enable_ad")
    var enableAd: Boolean,
    @SerializedName("timeout_ms")
    val timeout: Long,
    @SerializedName("adunit")
    val adUnit: String,
)