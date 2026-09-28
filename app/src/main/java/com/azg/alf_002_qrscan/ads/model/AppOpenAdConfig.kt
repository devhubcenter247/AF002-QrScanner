package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName


data class AppOpenAdConfig(
    @SerializedName("list_ads")
    val listAds: List<AdUnitConfig>,
    @SerializedName("enable")
     var enable: Boolean,
)  {
    companion object {
        val appOpenAdConfig = AppOpenAdConfig(
            enable = true,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/2615684927"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/6315407770"
                ),
            )
        )
    }
}