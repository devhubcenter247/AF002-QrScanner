package com.azg.alf_002_qrscan.ui.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeComposeFullView
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerMediaLeftComposeView
import com.azg.alf_002_qrscan.app.isInternetAvailable
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingEnd
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.main.MainActivity
import com.lib.ads.application.ads.admob.AppOpenManager
import com.lib.ads.application.ads.ads.AdsCallback
import com.lib.ads.application.ads.ads.wrapper.ApAdError
import com.lib.ads.application.ads.billing.AppPurchase
import com.lib.ads.application.ads.helper.AdOptionVisibility
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.createNativeAdPreloadHolder
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class IntroActivity : BaseActivity() {
    override fun initialize() = Unit
    private var requestedNativeAdSlideIndex: Int? = null

    val nativeAdHolder: NativeAdPreloadHolder by lazy {
        createNativeAdPreloadHolder(
            context = this@IntroActivity,
            enabled = NativePlacement.Onboarding.canShowAds,
            scope = lifecycleScope,
            tag = NativePlacement.Onboarding.tag,
            adOptionVisibility = AdOptionVisibility.INVISIBLE,
            fallbackAdUnitIds = NativePlacement.Onboarding.listId,
            lifecycleOwner = this,
            autoReloadOnResume = true
        )
    }
    val nativeFullAdHolder: NativeAdPreloadHolder by lazy {
        createNativeAdPreloadHolder(
            context = this@IntroActivity,
            enabled = NativePlacement.Onboarding.canShowAds,
            scope = lifecycleScope,
            tag = NativePlacement.OnboardingFull.tag,
            fallbackAdUnitIds = NativePlacement.OnboardingFull.listId,
            lifecycleOwner = this,
            autoReloadOnResume = true
        )
    }

    private fun isShowNativeFullScreen1(): Boolean {
        return remoteConfig.nativeObFscrConfig.enable
                && isInternetAvailable()
                && !AppPurchase.getInstance().isPurchased()
    }

    @Composable
    override fun BoxScope.ContentView() {
        val listIntroModel by remember {
            mutableStateOf(
                setupOnboardingPage(
                    isShowNativeFullScreen1()
                )
            )
        }
        val pagerState = rememberPagerState(initialPage = 0) { listIntroModel.size }
        val scope = rememberCoroutineScope()
        BoxParentContent(onBack = {
            val pre = pagerState.currentPage
            if (pre == 0) {
                finishAffinity()
            } else {

                val target = nearestSlideIndex(listIntroModel, pre, -1)
                scope.launch { pagerState.animateScrollToPage(target) }
            }
        }) {
            IntroScreen(
                pagerState = pagerState,
                nativeAdHolder = nativeAdHolder to NativePlacement.Onboarding,
                nativeFullAdHolder = nativeFullAdHolder to NativePlacement.OnboardingFull,
                scope = scope,
                items = listIntroModel,
                modifier = Modifier.background(white),
                nextScreen = {
                    launchActivity<MainActivity>()
                    finish()
                },
                checkLoadAds = { page ->
                    checkLoadAds(page)
                },
            )
        }
    }

    private fun checkLoadAds(page: OnboardingPage) {
        when (page) {
            is OnboardingPage.FullAd -> {
                AppOpenManager.getInstance().disableAppResume()
                cancelAds()
            }

            is OnboardingPage.Slide -> {
                if (remoteConfig.appOpenAdConfig.enable) {
                    AppOpenManager.getInstance().enableAppResume()
                }

                if (isInternetAvailable() && !AppPurchase.getInstance()
                        .isPurchased()
                ) requestAds(page.index) else cancelAds()
            }
        }
    }

    private fun requestAds(slideIndex: Int) {
        if (requestedNativeAdSlideIndex == slideIndex) return

        if (requestedNativeAdSlideIndex == null) {
            nativeAdHolder.request()
        } else {
            nativeAdHolder.reload()
        }
        requestedNativeAdSlideIndex = slideIndex
    }

    private fun cancelAds() {
        nativeAdHolder.cancel()
        requestedNativeAdSlideIndex = null
    }
}

fun nearestSlideIndex(items: List<OnboardingPage>, fromIndex: Int, direction: Int): Int {
    var t = fromIndex + direction
    while (t in items.indices && items[t] !is OnboardingPage.Slide) t += direction
    if (t !in items.indices) {

        t = fromIndex - direction
        while (t in items.indices && items[t] !is OnboardingPage.Slide) t -= direction
    }
    return t.coerceIn(0, items.lastIndex)
}

@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL, showBackground = true)
@Composable
fun IntroPreview() {
    AppViewTheme {
        val pagerState = rememberPagerState(initialPage = 1) { setupOnboardingPreviewPage().size }
        IntroScreen(
            pagerState = pagerState,
            scope = rememberCoroutineScope(),
            items = setupOnboardingPreviewPage(),
            theme = ColorTheme.BLUE,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    pagerState: PagerState,
    scope: CoroutineScope,
    nativeAdHolder: Pair<NativeAdPreloadHolder, NativePlacement>? = null,
    nativeFullAdHolder: Pair<NativeAdPreloadHolder, NativePlacement>? = null,
    theme: ColorTheme = ColorTheme.BLUE,
    modifier: Modifier = Modifier,
    items: List<OnboardingPage> = setupOnboardingPage(),
    checkLoadAds: (OnboardingPage) -> Unit = {},
    nextScreen: () -> Unit = {},
) {
    val page by remember(items) { derivedStateOf { pagerState.currentPage } }
    val onboardingModel by remember(items) {
        derivedStateOf { items[page.coerceIn(0, items.lastIndex)] }
    }
    val overscroll = rememberOverscrollEffect()
    var lastPage by remember { mutableIntStateOf(-1) }

    var navDirection by remember { mutableIntStateOf(0) }
    var isNavigating by remember { mutableStateOf(false) }

    var scrollJob by remember { mutableStateOf<Job?>(null) }
    val latestCheckLoadAds by rememberUpdatedState(checkLoadAds)
    val navigateTo: (Int) -> Unit = remember(pagerState, items, scope) {
        { targetPage ->
            if (targetPage in items.indices && targetPage != pagerState.currentPage) {
                scrollJob?.cancel()
                scrollJob = scope.launch {
                    isNavigating = true
                    try {
                        pagerState.animateScrollToPage(targetPage)
                    } finally {
                        isNavigating = false
                    }
                }
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { current ->
            if (current != lastPage) {
                if (lastPage != -1) navDirection = if (current > lastPage) 1 else -1
                lastPage = current
                latestCheckLoadAds(items[current])
            }
        }
    }

    ConstraintLayout(
        modifier.fillMaxSize()
    ) {
        val (swipe, _, content) = createRefs()
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = !isNavigating,
            modifier = Modifier
                .fillMaxSize()
                .overscroll(overscroll),
            beyondViewportPageCount = 0
        ) { index ->

            val isCurrentPage = pagerState.currentPage == index
            when (items[index]) {
                is OnboardingPage.FullAd -> {
                    NativeFullScreenView(
                        nativeFullAdHolder = nativeFullAdHolder?.first,
                        modifier = Modifier.fillMaxSize(1f)
                    ) {

                        if (isCurrentPage) {
                            val dir = if (navDirection == 0) 1 else navDirection
                            navigateTo(nearestSlideIndex(items, index, dir))
                        }
                    }
                }

                is OnboardingPage.Slide -> {
                    IntroParallaxPage(
                        index,
                        pagerState,
                        model = items[index] as OnboardingPage.Slide,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (onboardingModel is OnboardingPage.Slide) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                theme.bgColor.opacity(10f),
                                theme.bgColor,
                                theme.bgColor,
                                theme.bgColor,
                                theme.bgColor,
                                theme.bgColor,
                                theme.bgColor,
                                theme.bgColor
                            )
                        )
                    )
                    .paddingTop(16.Sdp)
                    .constrainAs(content) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            ) {
                TextView(
                    text = stringResource((onboardingModel as OnboardingPage.Slide).title),
                    font = R.font.rb_600,
                    textSize = 14.Ssp,
                    color = theme.buttonColor,
                    minLine = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingHorizontal(16.Sdp)
                )
                SpaceH(12.Sdp)
                TextView(
                    text = stringResource((onboardingModel as OnboardingPage.Slide).content),
                    font = R.font.rb_400,
                    textSize = 12.Ssp,
                    color = black,
                    textAlign = TextAlign.Center,
                    minLine = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingHorizontal(16.Sdp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingHorizontal(16.Sdp)
                        .paddingBottom(8.Sdp)
                ) {
                    DotIndicator(
                        pages = items,
                        currentPage = pagerState.currentPage,
                    )
                    SpaceF()
                    TextView(
                        text =
                            if (isLastSlide(items, page)) stringResource(R.string.continues)
                            else stringResource(R.string.next),
                        font = R.font.rb_500,
                        textSize = 12.Ssp,
                        color = theme.buttonColor,
                        modifier = Modifier.Companion
                            .padding(vertical = 6.Sdp, horizontal = 12.Sdp)

                    ) {
                        if (isLastSlide(items, page)) {
                            nextScreen()
                        } else {
                            val next = page + 1
                            if (next <= items.lastIndex) navigateTo(next)
                        }
                    }
                }

                NativeAdCard(
                    holder = nativeAdHolder?.first,
                    modifier = Modifier.fillMaxWidth(),
                    loading = { isLoading ->
                        if (LocalInspectionMode.current) {
                            ShimmerMediaLeftComposeView(false, Modifier.fillMaxWidth())
                        } else {
                            NativeShimmerByLayout(
                                nativeAdHolder!!.second,
                                isLoading,
                                Modifier.fillMaxWidth()
                            )
                        }
                    },
                    nativeView = { ad ->
                        NativeAdByLayout(nativeAdHolder!!.second, ad)
                    },
                )

            }
        }
    }
}

private fun isLastSlide(items: List<OnboardingPage>, page: Int): Boolean {
    if (page !in items.indices || items[page] !is OnboardingPage.Slide) return false
    for (i in (page + 1)..items.lastIndex) {
        if (items[i] is OnboardingPage.Slide) return false
    }
    return true
}

@Composable
fun NativeFullScreenView(
    nativeFullAdHolder: Native? = null,
    modifier: Modifier = Modifier,
    onNextPage: () -> Unit = {}
) {
    var isShowSkip by remember { mutableStateOf(false) }
    val adCallback by remember(nativeFullAdHolder) {
        mutableStateOf(object : NativeAdListener {
            override fun onAdImpression() {
                super.onAdImpression()
                isShowSkip = true
            }

            override fun onAdFailedToShow(adError: ApAdError?) {
                super.onAdFailedToShow(adError)
                onNextPage()
            }
        })
    }

    DisposableEffect(Unit) {
        nativeFullAdHolder?.request()
        nativeFullAdHolder?.registerAdCallback(adCallback)
        onDispose {
            nativeFullAdHolder?.unregisterAdCallback(adCallback)
            nativeFullAdHolder?.cancel()
        }
    }

    Box(modifier) {
        NativeComposeFullView(
            nativePreloadConfig = nativeFullAdHolder,
            modifier = Modifier.fillMaxSize()
        )

        if (isShowSkip) {
            TextView(
                text = stringResource(R.string.skip),
                color = Color.LightGray,
                modifier = Modifier
                    .paddingTop(32.Sdp)
                    .paddingEnd(16.Sdp)
                    .border(width = 1.Sdp, Color.LightGray, CircleShape)
                    .padding(horizontal = 16.Sdp, vertical = 4.Sdp)
                    .align(Alignment.TopEnd)
            ) {
                onNextPage()
            }
        }
    }
}


@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL, showBackground = true)
@Composable
fun NativeFullScreen2AdViewPreview() {
    AppViewTheme {
        NativeFullScreenView()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroParallaxPage(
    page: Int, pagerState: PagerState, model: OnboardingPage.Slide, modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Box(modifier.fillMaxSize()) {
        if (model.image != 0) {
            AsyncImage(
                model.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        val rawOffset =
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        translationX = rawOffset * 24f * density.density
                    }
            )
        }
    }
}


@Composable
fun DotIndicator(
    pages: List<OnboardingPage>,
    currentPage: Int = 0,
    theme: ColorTheme = ColorTheme.BLUE,
    modifier: Modifier = Modifier
) {
    val slideIndices = remember(pages) {
        pages.mapIndexedNotNull { idx, p ->
            if (p is OnboardingPage.Slide) idx else null
        }
    }

    Row(
        modifier = modifier.wrapContentSize()
    ) {
        slideIndices.forEach { slideIndex ->
            Row {
                val width by animateDpAsState(
                    targetValue = if (slideIndex == currentPage) 16.Sdp else 8.Sdp,
                    animationSpec = tween(220),
                    label = "indicator_width"
                )

                Box(
                    modifier = Modifier
                        .size(width = width, height = 8.Sdp)
                        .background(
                            Brush.linearGradient(listOf(theme.buttonColor, theme.buttonColor)),
                            shape = CircleShape
                        )
                )
                SpaceW(6.Sdp)
            }
        }
    }
}
