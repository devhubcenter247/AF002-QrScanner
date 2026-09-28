package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName

data class AdBannerConfig(
    @SerializedName("is_collapsible")
    val isCollapsible: Boolean,
    @SerializedName("list_ads")
    val listAds: List<AdUnitConfig>,
    @SerializedName("time_reload_banner")
    val timeReloadBanner : Long = 0,
    @SerializedName("enable")
    var enable: Boolean,
)  {
    companion object {
        val adBannerAllConfig = AdBannerConfig(
            enable = true,
            isCollapsible = true,
            timeReloadBanner = 15000,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = " ca-app-pub-1713129912546517/8559180702"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/4277154913"
                )
            )
        )
        val adBannerSplashConfig = AdBannerConfig(
            enable = true,
            isCollapsible = true,
            timeReloadBanner = 15000,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = " ca-app-pub-1713129912546517/8559180702"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/1494174940"
                )
            )
        )


    }
}

data class AdNativeBannerConfig(
    @SerializedName("list_ads")
    val listAds: List<AdUnitConfig>,
    @SerializedName("timeReloadMs")
    val timeReloadBanner : Long = 0,
    @SerializedName("enable")
    var enable: Boolean,
)  {
    companion object {
        val adNativeBannerConfig = AdNativeBannerConfig(
            enable = true,
            timeReloadBanner = 15000,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/8644840995"
                )
            )
        )
    }
}