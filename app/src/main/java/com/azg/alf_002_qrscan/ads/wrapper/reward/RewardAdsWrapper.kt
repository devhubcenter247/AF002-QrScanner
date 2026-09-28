package com.azg.alf_002_qrscan.ads.wrapper.reward

import android.app.Activity
import android.content.Context
import com.azg.alf_002_qrscan.app.isInternetAvailable
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.ads.wrapper.ApAdError
import com.lib.ads.application.ads.ads.wrapper.ApRewardAd
import com.lib.ads.application.ads.ads.wrapper.ApRewardItem
import com.lib.ads.application.ads.billing.AppPurchase
import com.lib.ads.application.ads.manager.RewardAdManager


object RewardAdConfig {
    private val rewardEdit = RewardAdsWrapper(RewardPlacement.RewardEdit)

    fun loadRewardChat(activity: Activity) {
        rewardEdit.preload(activity)
    }
    fun showRewardEdit(
        activity: Activity?,
        onRewarded: () -> Unit,
        onAdNotReady: () -> Unit = {},
    ) {
        activity?.let {
            rewardEdit.showAd(it, onNextAction = onRewarded, onAdNotReady = onAdNotReady)
        } ?: onRewarded()
    }
}


class RewardAdsWrapper(
    private val config: RewardPlacement,
) {
    private var isRequestAds = false
    private var rewardAd: ApRewardAd? = null

    private fun isAdsReady(): Boolean {
        return rewardAd?.isReady() == true
    }

    fun preload(activity: Context) {
        if (!isRequestAds
            && !isAdsReady()
            && activity.isInternetAvailable()
            && config.canShowAds
            && !AppPurchase.getInstance().isPurchased()
        ) {
            isRequestAds = true
            RewardAdManager.loadRewardAdList(
                context = activity,
                listId = config.listId,
                adsCallback = object : AdsCallback() {
                    override fun onRewardAdLoaded(apRewardAd: ApRewardAd?) {
                        super.onRewardAdLoaded(apRewardAd)
                        rewardAd = apRewardAd
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
        onNextAction: () -> Unit = {},
        onAdNotReady: () -> Unit = {},
    ) {
        if (AppPurchase.getInstance().isPurchased()) {
            onNextAction()
            return
        }
        if (isAdsReady()) {
            RewardAdManager.showRewardAd(
                activity = activity,
                apRewardAd = rewardAd!!,
                object : AdsCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        rewardAd = null
                    }

                    override fun onUserEarnedReward(rewardItem: ApRewardItem) {
                        super.onUserEarnedReward(rewardItem)
                        rewardAd?.clean()
                        onNextAction()
                    }

                    override fun onAdFailedToShow(adError: ApAdError?) {
                        super.onAdFailedToShow(adError)
                        onNextAction()
                    }
                }
            )
        } else {
            if (config.canShowAds) {
                onAdNotReady()
            } else {
                onNextAction()
            }
        }
    }
}

sealed class RewardPlacement {
    abstract val listId: List<String>
    abstract val canShowAds: Boolean

    data object RewardEdit : RewardPlacement() {
        override val listId
            get() = remoteConfig.rewarEditConfig.listAds
                .filter { it.enableAd }
                .map { it.adUnit }
        override val canShowAds
            get() = remoteConfig.rewarEditConfig.enable
    }
}