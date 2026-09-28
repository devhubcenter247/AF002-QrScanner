package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName

data class AdRewardConfig(
    @SerializedName("list_ads")
    val listAds: List<AdUnitConfig>,
    @SerializedName("enable")
     var enable: Boolean,
)  {
    companion object {
        val rewardedEdit= AdRewardConfig(
            enable = true,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/7161585583"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/8331046030"
                ),
            )
        )
    }
}