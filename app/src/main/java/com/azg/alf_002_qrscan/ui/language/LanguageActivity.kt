package com.azg.alf_002_qrscan.ui.language

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.base.getData
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.LanguageItem
import com.azg.alf_002_qrscan.ui.onboarding.IntroActivity
import com.azg.alf_002_qrscan.widget.mainColor
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.createNativeAdPreloadHolder
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

abstract class LanguageActivity : BaseActivity() {
    companion object {
        private const val SCREEN_OFFSET = "screen_offset"
        private const val LANG_SCREEN_TYPE = "lang_screen_type"
        private const val SELECTED_LANGUAGE = "selected_language"
        private const val SCROLL_INDEX = "scroll_index"
        private const val SCROLL_OFFSET = "scroll_offset"
        val listLanguages = getLanguageArray().toList()

        fun startLanguage(
            context: Context,
            screenType: LanguageScreenType,
            screenOffset: Int = 0,
            selectedLanguage: LanguageItem? = null,
            scrollIndex: Int = 0,
            scrollOffset: Int = 0,
        ) {
            val clazz = if (screenType == LanguageScreenType.Language1) {
                Language1Activity::class.java
            } else Language2Activity::class.java
            val intent = Intent(context, clazz)
            intent.putExtra(SCREEN_OFFSET, screenOffset)
            intent.putExtra(LANG_SCREEN_TYPE, screenType)
            intent.putExtra(SELECTED_LANGUAGE, selectedLanguage)
            intent.putExtra(SCROLL_INDEX, scrollIndex)
            intent.putExtra(SCROLL_OFFSET, scrollOffset)
            val options = ActivityOptions.makeCustomAnimation(
                context,
                0,
                0
            )

            context.startActivity(intent, options.toBundle())
        }
    }

    private val screenType: LanguageScreenType by lazy {
        getData<LanguageScreenType>(LANG_SCREEN_TYPE) ?: LanguageScreenType.Language1
    }

    open fun nativePlacement(): NativePlacement = NativePlacement.Language1
    val nativeAdHolder: NativeAdPreloadHolder by lazy {
        createNativeAdPreloadHolder(
            context = this@LanguageActivity,
            enabled = nativePlacement().canShowAds,
            scope = lifecycleScope,
            tag = nativePlacement().tag,
            lifecycleOwner = this@LanguageActivity,
            autoReloadOnResume = false
        )
    }

    override fun initialize() {
        nativeAdHolder.request()
    }

    override fun isFinishFirstFlow() = false

    @Composable
    override fun BoxScope.ContentView() {
        val theme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)
        var currentLanguage by rememberSaveable {
            mutableStateOf(getData<LanguageItem>(SELECTED_LANGUAGE))
        }
        val lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = getData(SCROLL_INDEX, 0),
            initialFirstVisibleItemScrollOffset = getData(SCROLL_OFFSET, 0)
        )
        BoxParentContent(onBack = { finish() }) {
            Column(Modifier.fillMaxSize()) {
                LanguageScreen(
                    languages = listLanguages,
                    selected = currentLanguage,
                    lazyListState = lazyListState,
                    nativeAd = {
                        NativeAdCard(
                            holder = nativeAdHolder,
                            modifier = Modifier.fillMaxWidth(),
                            loading = { isLoading ->
                                NativeShimmerByLayout(
                                    nativePlacement(),
                                    isLoading,
                                    Modifier.fillMaxWidth()
                                )
                            },
                            nativeView = { ad ->
                                NativeAdByLayout(nativePlacement(), ad)
                            },
                        )
                    },
                    theme = theme,
                    modifier = Modifier
                        .background(theme.bgColor)
                        .fillMaxWidth()
                        .statusBarPadding()
                        .weight(1f),
                    onSelect = {
                        currentLanguage = it
                        if (screenType is LanguageScreenType.Language1) {
                            startLanguage(
                                context = this@LanguageActivity,
                                screenType = LanguageScreenType.Language2,
                                selectedLanguage = it,
                                scrollIndex = lazyListState.firstVisibleItemIndex,
                                scrollOffset = lazyListState.firstVisibleItemScrollOffset
                            )
                            finish()
                        } else {
                            nativeAdHolder.reload()
                        }

                    },
                    onConfirm = {
                        val code = currentLanguage?.code ?: "en"
                        AppPreferences.setLocale(code)
                        launchActivity<IntroActivity>()
                    }
                )
            }
        }
    }
}

@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL, showBackground = true)
@Composable
fun GreetingPreview() {
    val listLanguages = getLanguageArray()
    AppViewTheme {
        LanguageScreen(
            languages = listLanguages,
            selected = listLanguages[0],
            modifier =
                Modifier
                    .paddingTop(6.Sdp)
                    .background(white),
            onSelect = {},
            onConfirm = {},
        )
    }
}

@Composable
fun LanguageScreen(
    languages: List<LanguageItem>,
    selected: LanguageItem?,
    nativeAd: @Composable () -> Unit = {},
    theme: ColorTheme = ColorTheme.BLUE,
    lazyListState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    onSelect: (LanguageItem) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(modifier.background(theme.bgColor)) {
        Row(
            Modifier.padding(horizontal = 12.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextView(
                text = R.string.language,
                font = R.font.rb_600,
                textSize = 16.Ssp,
                color = black
            )
            Spacer(Modifier.weight(1f))
            IconView(
                item = if (selected != null) Icons.Rounded.Done else Icons.Default.Done,
                icSize = 32,
                tint = if (selected != null) mainColor else Color.Transparent,
                contentDescription = stringResource(R.string.cd_next),
            ) {
                if (selected != null) onConfirm()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.Sdp)
                .paddingTop(12.Sdp),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            this.items(
                items = languages,
                key = { it.code }
            ) { item ->
                LanguageItem(currentLanguage = selected, theme = theme, item = item) { chosen ->
                    onSelect(chosen)
                }
            }
        }
        nativeAd()
    }
}
