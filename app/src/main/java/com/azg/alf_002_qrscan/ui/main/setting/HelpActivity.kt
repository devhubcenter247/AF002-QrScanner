package com.azg.alf_002_qrscan.ui.main.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerSmallCtaBotView
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.VerScrollView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.SimpleBackTopBar
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

class HelpActivity : BaseActivity() {
    override fun initialize() = Unit

    @Composable
    override fun BoxScope.ContentView() {
        val colorTheme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)

        BoxParentContent(
            onBack = { finish() },
            modifier = Modifier
                .fillMaxSize()
                .background(colorTheme.bgColor)
                .statusBarPadding()
        ) {
            HelpScreen(
                colorTheme = colorTheme,
                onBack = { finish() }
            )
        }
    }
}

private data class FaqItem(
    val question: Int,
    val answer: Int
)

private val faqItems = listOf(
    FaqItem(R.string.faq_scan_q, R.string.faq_scan_a),
    FaqItem(R.string.faq_create_q, R.string.faq_create_a),
    FaqItem(R.string.faq_customize_q, R.string.faq_customize_a),
    FaqItem(R.string.faq_gallery_q, R.string.faq_gallery_a),
    FaqItem(R.string.faq_history_q, R.string.faq_history_a),
    FaqItem(R.string.faq_favorite_q, R.string.faq_favorite_a),
    FaqItem(R.string.faq_permission_q, R.string.faq_permission_a),
)

@Composable
fun HelpScreen(
    colorTheme: ColorTheme,
    onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SimpleBackTopBar(title = R.string.help, onBack = onBack)


        SpaceH(16)

        TextView(
            text = stringResource(R.string.help_subtitle),
            font = R.font.rb_400,
            color = gray,
            textSize = 13.Ssp,
            modifier = Modifier.paddingHorizontal(12.Sdp).fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        SpaceH(16)

        Box(modifier = Modifier.fillMaxWidth().weight(1f).paddingHorizontal(12.Sdp)){
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(white, shape = RoundedCornerShape(20.Sdp))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.Sdp)
            ) {
                faqItems.forEachIndexed { index, item ->
                    FaqRow(item = item, colorTheme = colorTheme)
                    if (index != faqItems.lastIndex) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.Sdp)
                                .background(gray.copy(alpha = 0.12f))
                        )
                    }
                }
            }
        }



        SpaceH(24)

        NativeAdCard(
            holder = rememberNativeAdPreload(
                tag = NativePlacement.NativeMediumAll.tag,
                enabled = NativePlacement.NativeMediumAll.canShowAds,
                fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
                autoReloadOnResume = true,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.Sdp))
                .background(white),
            loading = { isLoading ->
                if (LocalInspectionMode.current) {
                    ShimmerSmallCtaBotView(isShowShimmer = true)
                } else {
                    NativeShimmerByLayout(
                        NativePlacement.NativeMediumAll,
                        isLoading,
                        Modifier.fillMaxWidth()
                    )
                }
            },
            nativeView = { ad ->
                NativeAdByLayout(NativePlacement.NativeMediumAll, ad)
            },
        )
    }
}

@Composable
private fun FaqRow(item: FaqItem, colorTheme: ColorTheme) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .onClick { expanded = !expanded }
            .padding(vertical = 14.Sdp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextView(
                text = stringResource(item.question),
                font = R.font.rb_500,
                textSize = 13.Ssp,
                color = black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            IconView(
                item = if (expanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down,
                tint = colorTheme.buttonColor,
                icSize = 16
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(Modifier.fillMaxWidth()) {
                SpaceH(8)
                TextView(
                    text = stringResource(item.answer),
                    font = R.font.rb_400,
                    textSize = 12.Ssp,
                    color = gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HelpScreenPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(ColorTheme.BLUE.bgColor)
    ) {
        HelpScreen(
            colorTheme = ColorTheme.BLUE,
            onBack = {}
        )
    }
}
