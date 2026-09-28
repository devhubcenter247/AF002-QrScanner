package com.azg.alf_002_qrscan.ads.wrapper.banner

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.lib.ads.application.ads.helper.banner.BannerAdConfig
import com.lib.ads.application.ads.helper.banner.createBannerAdHolder
import com.lib.ads.application.ads.helper.banner.rememberBannerAd
import kotlinx.coroutines.CoroutineScope


sealed class BannerPlacement {

    abstract val listId: List<String>
    abstract val canShowAds: Boolean
    abstract val isCollapsible: Boolean
    open val timeReloadBanner: Long = 0L

    data object BannerAll : BannerPlacement() {
        override val listId
            get() = remoteConfig.bannerAllConfig.listAds.filter { it.enableAd }.map { it.adUnit }
        override val canShowAds get() = remoteConfig.bannerAllConfig.enable
        override val isCollapsible get() = remoteConfig.bannerAllConfig.isCollapsible
        override val timeReloadBanner get() = remoteConfig.bannerAllConfig.timeReloadBanner
    }

    data object BannerSplash : BannerPlacement() {
        override val listId
            get() = remoteConfig.bannerSplashConfig.listAds.filter { it.enableAd }.map { it.adUnit }
        override val canShowAds get() = remoteConfig.bannerSplashConfig.enable
        override val isCollapsible get() = remoteConfig.bannerSplashConfig.isCollapsible
        override val timeReloadBanner get() = remoteConfig.bannerSplashConfig.timeReloadBanner
    }
}


@Composable
fun rememberBannerAdHolder(
    placement: BannerPlacement,
    useInline: Boolean = true,
    canShowAds: Boolean = true,
) = rememberBannerAd(
    config = BannerAdConfig.waterfall(
        adUnitIds = placement.listId,
        collapsibleGravity = if (placement.isCollapsible) "bottom" else "",
        useInline = useInline,
        canShowAds = placement.canShowAds,
        canReloadAds = canShowAds,
        timeReloadMs = placement.timeReloadBanner
    )
)


fun createBannerAdConfig(
    activity: ComponentActivity,
    placement: BannerPlacement,
    lifecycleScope: CoroutineScope = activity.lifecycleScope,
    lifecycleOwner: LifecycleOwner = activity,
    useInline: Boolean = true,
    canShowAds: Boolean = true,
) = createBannerAdHolder(
    activity = activity,
    config = BannerAdConfig.waterfall(
        adUnitIds = placement.listId,
        collapsibleGravity = if (placement.isCollapsible) "bottom" else "",
        useInline = useInline,
        canShowAds = placement.canShowAds,
        canReloadAds = canShowAds,
        timeReloadMs = placement.timeReloadBanner
    ),
    scope = lifecycleScope,
    lifecycleOwner = lifecycleOwner
)