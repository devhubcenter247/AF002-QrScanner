package com.azg.alf_002_qrscan.ads.wrapper.native

import com.azg.alf_002_qrscan.ads.model.AdNativeConfig
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.lib.ads.application.ads.helper.AdOptionVisibility


sealed class NativePlacement {
    val tag: String = this::class.java.name
    abstract val listId: List<String>
    abstract val canShowAds: Boolean
    open val adVisibility: AdOptionVisibility = AdOptionVisibility.GONE
    abstract val nativeConfig: AdNativeConfig


    data object Language1 : NativePlacement() {
        override val listId
            get() = remoteConfig.nativeLanguageConfig.listAds.filter { it.enableAd }
                .map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeLanguageConfig.enable
        override val nativeConfig get() = remoteConfig.nativeLanguageConfig
    }

    data object Language2 : NativePlacement() {
        override val listId
            get() = remoteConfig.nativeLanguageDupConfig.listAds.filter { it.enableAd }
                .map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeLanguageDupConfig.enable
        override val nativeConfig get() = remoteConfig.nativeLanguageDupConfig
    }

    data object Onboarding : NativePlacement() {
        override val listId
            get() = remoteConfig.nativeObConfig.listAds.filter { it.enableAd }.map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeObConfig.enable
        override val adVisibility = AdOptionVisibility.INVISIBLE
        override val nativeConfig get() = remoteConfig.nativeObConfig
    }

    data object OnboardingFull : NativePlacement() {
        override val listId
            get() = remoteConfig.nativeObFscrConfig.listAds.filter { it.enableAd }
                .map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeObFscrConfig.enable
        override val nativeConfig get() = remoteConfig.nativeObFscrConfig
    }

    data object NativeMediumAll : NativePlacement() {
        override val listId
            get() = remoteConfig.nativeMediumAllConfig.listAds.filter { it.enableAd }
                .map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeMediumAllConfig.enable
        override val nativeConfig get() = remoteConfig.nativeMediumAllConfig
    }

    data object NativeSmallAll : NativePlacement() {
        override val listId
            get() = remoteConfig.nativeSmallAllConfig.listAds.filter { it.enableAd }
                .map { it.adUnit }
        override val canShowAds get() = remoteConfig.nativeSmallAllConfig.enable
        override val nativeConfig get() = remoteConfig.nativeSmallAllConfig
    }

    override fun toString(): String {
        return "NativePlacement(listId=$listId, canShowAds=$canShowAds, adVisibility=$adVisibility, layout:${nativeConfig.layout}), Config: $nativeConfig"
    }

    companion object {

    }
}
