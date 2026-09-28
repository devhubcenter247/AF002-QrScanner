package com.azg.alf_002_qrscan.app.remote

import com.app.inappupdate.RemoteUpdateConfig
import com.app.inappupdate.UpdateMode
import com.azg.alf_002_qrscan.ads.model.AdBannerConfig
import com.azg.alf_002_qrscan.ads.model.AdNativeBannerConfig
import com.azg.alf_002_qrscan.ads.model.AdNativeConfig
import com.azg.alf_002_qrscan.ads.model.AdRewardConfig
import com.azg.alf_002_qrscan.ads.model.AdSplashConfig
import com.azg.alf_002_qrscan.ads.model.AppOpenAdConfig
import com.azg.alf_002_qrscan.ads.model.SkipInterSplash
import com.b269.ai.chatmate.ads.model.AdInterConfig
import org.koin.core.context.GlobalContext

val remoteConfig get() = RemoteConfig.Instance
class RemoteConfig : RemoteConfigBase() {

    companion object {
        val Instance: RemoteConfig by lazy { RemoteConfig() }
    }

    private val defaultTime = listOf(
        8 to 0,
        12 to 0,
        15 to 0,
        19 to 0,
        20 to 0,
        21 to 0,
    )
    private val configuredAdEnable by remote("ad_enable", true)
    val adEnable: Boolean
        get() = GlobalContext.getOrNull() != null && configuredAdEnable
    private var updateMode: UpdateMode by remote("update_mode", UpdateMode.SMOOTH)

    val metaCtrLow by remote("meta_ctr_low", false)

    val appOpenAdConfig by remote("app_open_config", AppOpenAdConfig.appOpenAdConfig) { enable = adEnable && enable }
    val nativeLanguageConfig by remote("native_lang_config", AdNativeConfig.nativeLanguage) { enable = adEnable && enable }
    val nativeLanguageDupConfig by remote("native_lang_dup_config", AdNativeConfig.nativeLanguageDup) { enable = adEnable && enable }
    val nativeObConfig by remote("native_ob_config", AdNativeConfig.nativeOb) { enable = adEnable && enable }
    val nativeObFscrConfig by remote("native_ob_full_config", AdNativeConfig.nativeObFscr) { enable = adEnable && enable }
    val nativeSmallAllConfig by remote("native_small_all_config", AdNativeConfig.nativeSmallAll) { enable = adEnable && enable }
    val nativeMediumAllConfig by remote("native_medium_config", AdNativeConfig.nativeMediumAll) { enable = adEnable && enable }
    val nativeBannerConfig by remote("native_banner_config", AdNativeBannerConfig.adNativeBannerConfig) { enable = adEnable && enable }
    val skipInterConfig by remote("skip_inter_config", SkipInterSplash.defaultData)
    val interSplashConfig by remote("inter_slash_config", AdSplashConfig.adSplashConfig) { enable = adEnable && enable }
    val interAllConfig by remote("inter_all_config", AdInterConfig.adInterConfig) { enable = adEnable && enable }
    val rewarEditConfig by remote("reward_edit_config", AdRewardConfig.rewardedEdit) { enable= adEnable && enable }
    val bannerAllConfig by remote("banner_all_config", AdBannerConfig.adBannerAllConfig) { enable = adEnable && enable }
    val bannerSplashConfig by remote("banner_spalsh_config", AdBannerConfig.adBannerSplashConfig) { enable = adEnable && enable }
    fun buildUpdateConfig(
        defaultTitle: String,
        defaultMessage: String,
        defaultUpdateText: String,
        defaultDismissText: String,
    ) = RemoteUpdateConfig(
        updateMode        = updateMode,
        dialogTitle       = defaultTitle,
        dialogMessage     = defaultMessage,
        updateButtonText  = defaultUpdateText,
        dismissButtonText = defaultDismissText,
    )
    private fun notiTimesRemoteConfig(
        rawValue: String,
        default: List<Pair<Int, Int>> = defaultTime,
    ): List<Pair<Int, Int>> {
        if (rawValue.isBlank()) return default
        val result = rawValue.split(",")
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { timeStr ->
                val parts = timeStr.split(":")
                if (parts.size != 2) return@mapNotNull null
                val hour = parts[0].toIntOrNull() ?: return@mapNotNull null
                val minute = parts[1].toIntOrNull() ?: return@mapNotNull null
                if (hour !in 0..23 || minute !in 0..59) return@mapNotNull null
                hour to minute
            }
            .distinct()
            .sortedWith(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second })
            .toList()
        return result.ifEmpty { default }
    }
}
