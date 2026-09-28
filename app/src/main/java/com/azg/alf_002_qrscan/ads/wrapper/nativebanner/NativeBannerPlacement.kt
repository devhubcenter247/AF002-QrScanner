package com.azg.alf_002_qrscan.ads.wrapper.nativebanner

import com.azg.alf_002_qrscan.app.remote.remoteConfig

sealed class NativeBannerPlacement {
    abstract val listId: List<String>
    abstract val canShowAds: Boolean
    open val timeReloadBanner: Long = 0L

    data object BannerAll : NativeBannerPlacement() {
        override val listId get() = remoteConfig.nativeBannerConfig.listAds.filter { it.enableAd }.map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeBannerConfig.enable
        override val timeReloadBanner get() = remoteConfig.nativeBannerConfig.timeReloadBanner
    }
}