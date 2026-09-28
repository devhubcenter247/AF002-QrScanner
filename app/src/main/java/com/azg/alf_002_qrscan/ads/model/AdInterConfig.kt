package com.b269.ai.chatmate.ads.model

import com.azg.alf_002_qrscan.ads.model.AdUnitConfig
import com.google.gson.annotations.SerializedName

data class AdInterConfig(
    @SerializedName("time_interval_ms")
    val timeInterval: Long,
    @SerializedName("time_steps")
    val timeSteps: List<Int>,
    @SerializedName("list_ads")
    val listAds: List<AdUnitConfig>,
    @SerializedName("enable")
     var enable: Boolean,
)  {
    companion object {
        val adInterConfig = AdInterConfig(
            enable = true,
            timeInterval = 30000,
            timeSteps = emptyList(),
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/9533849432"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/5977645207"
                )
            )
        )
    }
}