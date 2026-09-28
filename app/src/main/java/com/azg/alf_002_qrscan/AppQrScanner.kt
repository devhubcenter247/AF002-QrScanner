package com.azg.alf_002_qrscan

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.azg.alf_002_qrscan.app.LocateManager
import com.azg.alf_002_qrscan.app.dataModule
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.azg.alf_002_qrscan.app.viewModelModule
import com.azg.alf_002_qrscan.ui.splash.SplashActivity
import com.facebook.appevents.iap.InAppPurchaseUtils
import com.lib.ads.application.ads.admob.AppOpenManager
import com.lib.ads.application.ads.application.AdsMultiDexApplication
import com.lib.ads.application.ads.billing.AppPurchase
import com.lib.ads.application.ads.billing.PurchaseItem
import com.lib.ads.application.ads.config.AdSdkConfig
import com.lib.ads.application.ads.config.AdjustConfig
import com.lib.ads.application.ads.config.AdsConsentConfig
import com.lib.ads.application.ads.manager.AdSdkInitializer
import com.lib.ads.application.ads.manager.AdsManger
import com.lib.ads.application.ads.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import kotlin.time.Duration.Companion.milliseconds

class AppQrScanner : AdsMultiDexApplication(), Application.ActivityLifecycleCallbacks {
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    companion object {

        private const val APPSFLYER_TOKEN = "APPSFLYER_TOKEN"
        private const val ADJUST_TOKEN = "ADJUST_TOKEN"
        private const val EVENT_PURCHASE_ADJUST = "EVENT_PURCHASE_ADJUST"
        private const val EVENT_AD_IMPRESSION_VALUE_ADJUST = "EVENT_AD_IMPRESSION_VALUE_ADJUST"
        private const val EVENT_AD_IMPRESSION_ADJUST = "EVENT_AD_IMPRESSION_ADJUST"
        private const val TAG = "MainApplication"

        const val PRODUCT_DISCOUNT_FM = "monthly-sub-dis-fm"
        const val PRODUCT_MONTHLY_SUB = "qrscan_monthly_prd_1"

        // Bạn có thể bổ sung thêm class name nếu SDK khác dùng activity riêng
        private val AD_ACTIVITY_NAMES = setOf(
            // Google
            "AdActivity",
            // TikTok/Pangle
            "TTAppOpenAdActivity",
            "TTInterstitialActivity",
            "TTInterstitialExpressActivity",
            "TTFullScreenExpressVideoActivity",
            "TTFullScreenVideoActivity",
            "TTAdActivity",
            // Facebook
            "AudienceNetworkActivity",
            // Mintegral / MBridge
            "MBCommonActivity",
            "MBInterstitialVideoActivity",
            // Vungle / IronSource / ... nếu có thì add tiếp
        )

        // Những màn hình “nguồn” vừa chuyển sang ads (vd splash)
        private val SOURCE_ACTIVITY_NAMES = setOf(
            "SplashActivity",
            "NativeSplashActivity",
            "NativeFullSplashInterActivity",
            "LanguageLoadingActivity",
            "OnboardingActivity",
            "LanguageApplyActivity"
        )

        @SuppressLint("StaticFieldLeak")
        private var currentAdActivity: Activity? = null

        private var isFromSource = false
        private var jobActivityClose: Job? = null

        private val appScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

        private fun killAdActivity() {
            val delaySkip = remoteConfig.skipInterConfig
            if (!delaySkip.enable || delaySkip.timeOutMS <= 0) return
            currentAdActivity?.let { activity ->
                val activityName = activity::class.java.simpleName
                if (activityName in AD_ACTIVITY_NAMES) {
                    appScope.launch {
                        delay(delaySkip.timeOutMS.milliseconds)
                        safeFinishAd()
                    }
                }
            }
        }

        private fun safeFinishAd() {
            currentAdActivity?.let { activity ->
                if (!activity.isFinishing && !activity.isDestroyed) {
                    activity.finish()
                }
            }
            currentAdActivity = null
            isFromSource = false
        }

        fun cancelKillAd() {
            jobActivityClose?.cancel()
            currentAdActivity = null
            isFromSource = false
        }
    }


    override fun onCreate() {
        super.onCreate()
        LocateManager.initDeviceLocate()
        startKoin {
            androidLogger()
            androidContext(this@AppQrScanner)
            modules(
                listOf(
                    dataModule,
                    viewModelModule,
                )
            )
        }
        initBilling()
        initAds()
        registerActivityLifecycleCallbacks(this)
    }

    private fun initAds() {
        AppLogger.isEnabled = true
        AdsManger.showMessageForTester = false
        val environment = if (BuildConfig.build_debug) {
            AdSdkConfig.ENVIRONMENT_DEVELOP
        } else {
            AdSdkConfig.ENVIRONMENT_PRODUCTION
        }
        adSdkConfig = AdSdkConfig(this, environment)
        val adsConsentConfig = AdsConsentConfig(true).apply {
            isEnableDebug = BuildConfig.build_debug
            testDevice = "45FEEED09CF782946B3842A3C7BF7064"
            isResetData = BuildConfig.build_debug
        }
        adSdkConfig
        adSdkConfig.adsConsentConfig = adsConsentConfig
        val adjustConfig = AdjustConfig(true, ADJUST_TOKEN).apply {
            eventAdImpressionValue = EVENT_AD_IMPRESSION_VALUE_ADJUST
            eventNamePurchase = EVENT_PURCHASE_ADJUST
            eventAdImpression = EVENT_AD_IMPRESSION_ADJUST
        }

        adSdkConfig.adjustConfig = adjustConfig
        listTestDevice.addAll(
            listOf(
                "EC25F576DA9B6CE74778B268CB87E431",
                "FFA7C428D2398D78B45C850591A4528C"
            )
        )
        adSdkConfig.listDeviceTest = listTestDevice
        adSdkConfig.intervalInterstitialAd = 0
        AdSdkInitializer.init(this, adSdkConfig)
        AdsManger.disableAdResumeWhenClickAds = true
        AdsManger.openActivityAfterShowInterAds = true
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
    }

    override fun onActivityStarted(p0: Activity) {
        ++activityReferences
        isActivityChangingConfigurations = false
    }

    override fun onActivityResumed(p0: Activity) {
        val activityName = p0::class.java.simpleName
        if (activityName in SOURCE_ACTIVITY_NAMES) {
            isFromSource = true
        }
        if (activityName in AD_ACTIVITY_NAMES && isFromSource) {
            currentAdActivity = p0
            killAdActivity()
            isFromSource = false
        }
    }

    override fun onActivityStopped(p0: Activity) {
        isActivityChangingConfigurations = p0.isChangingConfigurations
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityDestroyed(p0: Activity) {
        val activityName = p0::class.java.simpleName
        if (activityName in AD_ACTIVITY_NAMES) {
            cancelKillAd()
        }
    }

    override fun onActivityPaused(p0: Activity) {
        val activityName = p0::class.java.simpleName
        if (activityName in AD_ACTIVITY_NAMES) {
            cancelKillAd()
        }
    }

    private fun initBilling() {

        AppPurchase.getInstance().initBilling(
            this, listOf(
                PurchaseItem(
                    PRODUCT_MONTHLY_SUB, AppPurchase.TYPE_IAP.PURCHASE, PRODUCT_MONTHLY_SUB
                )
            )
        );
    }


}