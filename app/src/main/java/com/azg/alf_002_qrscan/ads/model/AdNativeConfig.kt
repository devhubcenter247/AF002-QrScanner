package com.azg.alf_002_qrscan.ads.model

import com.google.gson.annotations.SerializedName


data class AdNativeConfig(
    @SerializedName("type_layout")
    var layout: String,
    @SerializedName("list_ads")
    val listAds: List<AdUnitConfig>,
    @SerializedName("enable")
     var enable: Boolean,
)  {
    companion object {

        val nativeLanguage = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/7648519756"
                ),
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/3351481866"
                )
            )
        )
        val nativeLanguageDup = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/2264331139"
                ), AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/6070985251"
                ),
            )
        )
        val nativeOb = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumMediaLeftCtaBottom.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/1741737946"
                ),AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/6363358241"
                ),
            )
        )
        val nativeObFscr = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.Other.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/3073286820"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/4836960481"
                ),
            )
        )
        val nativeSmallAll = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeSmallCtaBottom.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/7676439913"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/8115574605"
                ),
            )
        )
        val nativeMediumAll = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumCtaBottom.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = false,
                    adUnit = "ca-app-pub-1713129912546517/7676439913"
                ),
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/8115574605"
                ),
            )
        )
        val nativeBanner = AdNativeConfig(
            enable = true,
            layout = LayoutNativeType.NativeMediumCtaBottom.type,
            listAds = listOf(
                AdUnitConfig(
                    enableAd = true,
                    adUnit = "ca-app-pub-1713129912546517/8644840995"
                ),
            )
        )
    }

    fun isNativeSmall() = this.layout.contains("native_small")
    fun isNativeLeftMedia() = this.layout.contains("media_left")
    override fun toString(): String {
        val adsSummary = listAds
            .mapIndexed { index, ad ->
                "[$index] ${ad}"
            }
            .joinToString(separator = ", ")

        return "AdNativeConfig(" +
                "enable=$enable, " +
                "layout='$layout', " +
                "nativeSmall=${isNativeSmall()}, " +
                "leftMedia=${isNativeLeftMedia()}, " +
                "listAdsSize=${listAds.size}, " +
                "listAds=[$adsSummary]" +
                ")"
    }
}

sealed class LayoutNativeType(val type: String) {
    data object NativeSmallCtaBottom : LayoutNativeType("native_small_cta_bottom")
    data object NativeSmallCtaTop : LayoutNativeType("native_small_cta_top")
    data object NativeSmallCtaRight : LayoutNativeType("native_small_cta_right")
    data object NativeMediumCtaBottom : LayoutNativeType("native_medium_cta_bottom")
    data object NativeMediumCtaTop : LayoutNativeType("native_medium_cta_top")
    data object MediumCtaRightBottom : LayoutNativeType("medium_cta_right_bottom")
    data object MediumCtaRightTop : LayoutNativeType("medium_cta_right_top")
    data object NativeMediumMediaLeftCtaBottom : LayoutNativeType("native_medium_media_left_cta_bottom")
    data object NativeMediumMediaLeftCtaRight : LayoutNativeType("native_medium_media_left_cta_right")
    data object NativeMediumMediaLeftCtaTop : LayoutNativeType("native_medium_media_left_cta_top")
    data object NativeMediumCtaBotRight : LayoutNativeType("native_medium_cta_bot_right")
    data object NativeMediumCtaTopRight : LayoutNativeType("native_medium_cta_top_right")
    data object Other : LayoutNativeType("other")
}
