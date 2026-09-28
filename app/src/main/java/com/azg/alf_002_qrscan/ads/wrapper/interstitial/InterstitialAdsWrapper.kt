package com.azg.alf_002_qrscan.ads.wrapper.interstitial

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.azg.alf_002_qrscan.app.isInternetAvailable
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.ads.wrapper.ApAdError
import com.lib.ads.application.ads.ads.wrapper.ApInterstitialAd
import com.lib.ads.application.ads.billing.AppPurchase
import com.lib.ads.application.ads.manager.InterstitialAdManager
import kotlinx.coroutines.Job


object InterstitialAdConfig {
    var isCloseInterSplash = MutableLiveData(false)
    private var interAll = InterstitialAdsWrapper(InterstitialPlacement.InterAll)
    fun resetInterAllConfig() {
        interAll.resetConfig()
    }

    fun updateTimeShowInterAll() {
        interAll.updateTimeShowInter()
    }

    fun loadInterAll(context: Context) {
        interAll.preloadAd(context)
    }

    fun showInterAll(activity: Activity?, onNextAction: () -> Unit) {
        activity?.let {
            interAll.showAd(activity, true, onNextAction)
        } ?: run { onNextAction() }
    }
}



class InterstitialAdsWrapper(
    private val config: InterstitialPlacement,
) {
    private var lastTimeInterstitialShow = 0L
    private var isRequestAds = false
    private var interAd: ApInterstitialAd? = null
    private var currentStepInter = 1
    private val showType by lazy {
        when {
            config.timeInterval > 0 -> InterShowType.Interval
            else -> InterShowType.None
        }
    }

    private fun isAdsReady(): Boolean {
        return interAd?.isReady() == true
    }

    fun resetConfig() {
        lastTimeInterstitialShow = 0
        currentStepInter = 1
    }

    fun updateTimeShowInter() {
        lastTimeInterstitialShow = System.currentTimeMillis()
    }

    fun preloadAd(context: Context) {
        if (!isRequestAds
            && !isAdsReady()
            && context.isInternetAvailable()
            && config.canShowAds
            && !AppPurchase.getInstance().isPurchased()
        ) {
            isRequestAds = true
            InterstitialAdManager.getInterstitialAdsList(
                context = context,
               listId =  config.listId,
                adListener = object : AdsCallback(){
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interAd = interstitialAd
                        isRequestAds = false
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        isRequestAds = false
                    }
                }
            )
        }
    }


    fun showAd(
        activity: Activity,
        shouldReloadAds: Boolean = false,
        onNextAction: () -> Unit = {},
        isCloseInter: (Boolean)-> Unit = {}
    ) {
        isCloseInter(false)
        if (AppPurchase.getInstance().isPurchased()) {
            onNextAction()
            return
        }

        if (canShowAdsInterval()) {
            InterstitialAdManager.forceShowInterstitial(
                activity =activity,
                mInterstitialAd = interAd,
                callback =
                object : AdsCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        onNextAction()

                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        lastTimeInterstitialShow = System.currentTimeMillis()
                        interAd = null
                        if (shouldReloadAds) {
                            preloadAd(activity)
                        }
                        isCloseInter(true)
                    }

                    override fun onAdFailedToShow(adError: ApAdError?) {
                        super.onAdFailedToShow(adError)
                        isCloseInter(true)
                    }

                    override fun onAdFailedToLoad(adError: ApAdError?) {
                        super.onAdFailedToLoad(adError)
                        isCloseInter(true)
                    }
                },
            )
        } else {
            isCloseInter(true)
            onNextAction()
        }
        currentStepInter++
    }

    private fun canShowAdsInterval(): Boolean {
        val timeInterval = System.currentTimeMillis() - lastTimeInterstitialShow
        return timeInterval > config.timeInterval && isAdsReady()
    }
}

sealed class InterShowType {
    data object Interval : InterShowType()
    data object None : InterShowType()
}

sealed class InterstitialPlacement {
    abstract val listId: List<String>
    abstract val canShowAds: Boolean
    abstract val timeInterval: Long
    abstract val timeSteps: List<Int>

    data object InterAll : InterstitialPlacement() {
        override val listId get() = remoteConfig.interAllConfig.listAds.filter { it.enableAd }.map { it.adUnit }
        override val canShowAds get() = remoteConfig.interAllConfig.enable
        override val timeInterval get() = remoteConfig.interAllConfig.timeInterval
        override val timeSteps get() = remoteConfig.interAllConfig.timeSteps
    }
}