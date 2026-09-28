package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName

data class AdSplashConfig(
    @SerializedName("total_timeout_ms")
    val totalTimeout: Long,
    @SerializedName("list_ads")
    val listAds: List<SplashAdConfig>,
    @SerializedName("enable")
    var enable: Boolean,
) {
    companion object {
        val adSplashConfig = AdSplashConfig(
            enable = true,
            totalTimeout = 30000,
            listAds = listOf(
                SplashAdConfig(
                    enableAd = true,
                    timeout = 30000,
                    adUnit = "ca-app-pub-1713129912546517/1846931109"
                ),
                SplashAdConfig(
                    enableAd = true,
                    timeout = 30000,
                    adUnit = "ca-app-pub-1713129912546517/9230405216"
                ),
            )
        )
    }
}