package com.azg.alf_002_qrscan.ui.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.azg.alf_002_qrscan.BuildConfig
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.AdPreloadManager
import com.azg.alf_002_qrscan.ads.wrapper.banner.BannerPlacement
import com.azg.alf_002_qrscan.ads.wrapper.interstitial.InterstitialAdConfig
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.isInternetAvailable
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.borderTop
import com.azg.alf_002_qrscan.style.getScreenWidthInDp
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.language.LanguageActivity
import com.azg.alf_002_qrscan.ui.language.LanguageScreenType
import com.azg.alf_002_qrscan.ui.main.MainActivity
import com.azg.alf_002_qrscan.widget.mainColor
import com.lib.ads.application.ads.admob.AdsConsentManager
import com.lib.ads.application.ads.admob.AppOpenManager
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.billing.AppPurchase
import com.lib.ads.application.ads.helper.BannerCollapseGravity
import com.lib.ads.application.ads.helper.banner.BannerAdCard
import com.lib.ads.application.ads.helper.banner.BannerAdConfig
import com.lib.ads.application.ads.helper.banner.BannerAdHolder
import com.lib.ads.application.ads.helper.banner.DefaultBannerAdLoading
import com.lib.ads.application.ads.helper.banner.createBannerAdHolder
import com.lib.ads.application.ads.manager.InterstitialAdManager
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private var isPause = false
    private var isAdOpenSplashShow = false
    private var hasNavigatedNext = false

    override fun initialize() {
        lifecycleScope.launch {
            if (isInternetAvailable()) {
                awaitAll(
                    async { remoteConfig.setupRemoteConfig(BuildConfig.DEBUG) },
                    async { requestUmp() },
                )

                if (remoteConfig.adEnable) {
                    initAds()
                } else {
                    delay(3000.milliseconds)
                    goNextPage()
                }
            } else {
                delay(3000.milliseconds)
                goNextPage()
            }
        }
    }

    private var bannerAdConfig by mutableStateOf<BannerAdHolder?>(null)

    @Composable
    override fun BoxScope.ContentView() {
        val colorTheme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)
        ColumnParentContent(onBack = {}, modifier = Modifier.fillMaxSize()) {
            SplashScreen(
                Modifier
                    .background(colorTheme.bgColor)
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarPadding(), theme = colorTheme
            )
            bannerAdConfig?.let {
                BannerAdCard(
                    holder = it,
                    modifier = Modifier
                        .background(white)
                        .borderTop(1.dp, lightGrey),
                    loading = {
                        DefaultBannerAdLoading()
                    }
                )
            }

        }
    }

    private var isAcceptUmp by mutableStateOf(false)

    private fun initAds() {
        if (AppPurchase.getInstance().getInitBillingFinish()) {
            goNextPage()
        } else {
            AppPurchase.getInstance().setBillingListener({
                lifecycleScope.launch(Dispatchers.Main) {
                    if (!AppPurchase.getInstance()
                            .isPurchased() && remoteConfig.adEnable && isInternetAvailable() && isAcceptUmp
                    ) {
                        bannerAdConfig = createBannerAdHolder(
                            activity = this@SplashActivity,
                            enabled = remoteConfig.bannerSplashConfig.enable,
                            config = BannerAdConfig.waterfall(
                                adUnitIds = BannerPlacement.BannerSplash.listId,
                                collapsibleGravity = BannerCollapseGravity.None,
                                useInline = true,
                                canShowAds = BannerPlacement.BannerSplash.canShowAds,
                                canReloadAds = false,
                                timeReloadMs = BannerPlacement.BannerSplash.timeReloadBanner
                            ),
                            scope = lifecycleScope,
                            lifecycleOwner = this@SplashActivity
                        )
                        bannerAdConfig?.request()
                        if (!AppPreferences.isFinishFirstFlow) {
                            preloadNativeForLanguage()
                        }
                        loadInterSplash()
                        setupOpenAds()
                    } else {
                        lifecycleScope.launch {
                            delay(3000.milliseconds)
                            goNextPage()
                        }
                    }
                }
            }, 7000)
        }


    }

    private fun setupOpenAds() {
        val config = remoteConfig.appOpenAdConfig
        if (!config.enable) return
        val listEnableAds = config.listAds.filter { it.enableAd }.map { it.adUnit }
        AppOpenManager.getInstance().init(this.application, listEnableAds)
    }

    fun preloadNativeForLanguage() {
        AdPreloadManager.preloadAdConfig(
            this@SplashActivity.applicationContext,
            NativePlacement.Language1
        )
        AdPreloadManager.preloadAdConfig(
            this@SplashActivity.applicationContext,
            NativePlacement.Language2
        )
    }

    private fun loadInterSplash() {
        AdPreloadManager.preloadInterSplashConfig(
            this@SplashActivity,
            remoteConfig.interSplashConfig,
            onInterReady = {
                InterstitialAdManager.onShowSplash(
                    this@SplashActivity,
                    enabled = remoteConfig.interSplashConfig.enable,
                    object : AdsCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            InterstitialAdConfig.updateTimeShowInterAll()
                            goNextPage()
                        }
                    })
            }) {
            InterstitialAdConfig.updateTimeShowInterAll()
            goNextPage()
        }
    }

    private suspend fun requestUmp() {
        val consentManager = AdsConsentManager(this@SplashActivity)
        consentManager.requestUMP()
        isAcceptUmp = consentManager.getCanRequestAd()
        if (!isAcceptUmp) finish()
    }

    override fun isFinishFirstFlow() = AppPreferences.isFinishFirstFlow
    fun goNextPage() {
        if (hasNavigatedNext) return
        hasNavigatedNext = true

        if (!AppPreferences.isFinishFirstFlow) {
            LanguageActivity.startLanguage(this@SplashActivity, LanguageScreenType.Language1)
            finish()
        } else {
            launchActivity<MainActivity>()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        if (isAdOpenSplashShow) return
        InterstitialAdManager.onCheckShowSplashWhenFail(this, object : AdsCallback() {
            override fun onNextAction() {
                super.onNextAction()
                goNextPage()
            }
        }, 800)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }
}

@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL, showBackground = true)
@Composable
fun SplashPreview() {
    AppViewTheme {
        SplashScreen(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        )
    }
}

@Composable
fun SplashScreen(modifier: Modifier, theme: ColorTheme = ColorTheme.BLUE) {
    Column(
        modifier.background(color = theme.bgColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpaceH(h = getScreenWidthInDp() * 0.35f)

        Image(
            painter = painterResource(R.drawable.img_logo_splash),
            contentDescription = "Logo Desc",
            contentScale = ContentScale.Companion.Crop,
            modifier = Modifier.Companion
                .size(120.dp)
                .clip(RoundedCornerShape(12.Sdp))
        )

        SpaceH(h = 12.Sdp)
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = FontFamily(Font(R.font.rb_600)),
            color = mainColor,
            textAlign = TextAlign.Center,
            fontSize = 18.Ssp
        )


        Spacer(Modifier.Companion.weight(1f))
        TextView(
            text = stringResource(R.string.this_action_may_be_constant_adverting),
            modifier = Modifier.Companion.padding(bottom = 12.dp),
            textSize = 11.Ssp,
            color = theme.buttonColor,
            font = R.font.rb_400
        )
        LinearProgressIndicator(
            modifier = Modifier.height(6.dp),
            color = theme.buttonColor,
            trackColor = theme.buttonColor.opacity(35f),
            strokeCap = StrokeCap.Companion.Round
        )
        SpaceH(h = 32.dp)
    }
}
