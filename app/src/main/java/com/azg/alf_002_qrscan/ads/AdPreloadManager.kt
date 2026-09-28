package com.azg.alf_002_qrscan.ads

import android.content.Context
import android.util.Log
import com.azg.alf_002_qrscan.ads.model.AdSplashConfig
import com.azg.alf_002_qrscan.ads.model.SplashAdConfig
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.ads.wrapper.ApAdError
import com.lib.ads.application.ads.helper.adnative.NativeAds
import com.lib.ads.application.ads.helper.adnative.PreloadBufferState
import com.lib.ads.application.ads.manager.InterstitialAdManager
import kotlinx.coroutines.flow.StateFlow

object AdPreloadManager {
    fun preloadAdConfig(context: Context, nativePlacement: NativePlacement, buffer: Int = 1) {
        if(!nativePlacement.canShowAds) return
        NativeAds.preload(
            context = context,
            tag = nativePlacement.tag,
            adUnitIds = nativePlacement.listId,
            bufferSize = buffer,
        )

    }

    fun getNativePreload(nativePlacement: NativePlacement): StateFlow<PreloadBufferState> {
        return NativeAds.stateFlow(nativePlacement.tag)
    }

    fun getAdCountPreload(nativePlacement: NativePlacement) =
        NativeAds.available(nativePlacement.tag)

    // inter splash

    fun preloadInterSplashConfig(
        context: Context, adSplashConfig: AdSplashConfig, onInterReady: () -> Unit = {},
        onNextAction: () -> Unit = {}
    ) {
        val adUnitConfig = adSplashConfig.listAds.filter { it.enableAd }.map { it.adUnit }

        InterstitialAdManager.loadSplashListAds(
            context = context,
            listId = adUnitConfig,
            enabled = adSplashConfig.enable,
            timeOut = adSplashConfig.totalTimeout,
            timeDelay = 0,
            adCallback = object : AdsCallback() {
                override fun onAdSplashReady() {
                    super.onAdSplashReady()
                    Log.d("ta", "loadInterSplash - onAdSplashReady")
                    onInterReady()
                }

                override fun onAdFailedToLoad(adError: ApAdError?) {
                    super.onAdFailedToLoad(adError)
                    Log.e("zzzzzz", "loadInterSplash - onAdFailedToLoad: ${adError?.message}")
                    onNextAction()
                }

                override fun onAdFailedToShow(adError: ApAdError?) {
                    super.onAdFailedToShow(adError)
                    Log.e("zzzzzz", "loadInterSplash - onAdFailedToShow: ${adError?.message}")
                    onNextAction()
                }
            }
        )
    }
}