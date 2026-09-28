package com.azg.alf_002_qrscan.ads.wrapper.native.view

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azg.alf_002_qrscan.style.PhonePreview
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.rememberShimmerState
import com.azg.alf_002_qrscan.style.shimmer
import com.azg.alf_002_qrscan.style.white
import com.lib.ads.application.ads.ads.wrapper.ApNativeAd
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdBodyView
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdMediaView
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdView
import com.sdp.ssp.android.Sdp


@Composable
fun NativeMediaLeftCtaBot(
    nativeAdView: ApNativeAd,
    isMeta: Boolean = false,
    isMetaLow: Boolean = false,
    modifier: Modifier = Modifier,
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .paddingBottom(16.Sdp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                NativeAdMediaView(
                    modifier = Modifier. padding(1.Sdp)
                        .size(120.dp),
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.Sdp)
                        .padding(top = 10.Sdp)
                ) {
                    NativeHeadlineRowSmall()
                    if (isMeta) {
                        MetaTextSponsor()
                    }
                    NativeAdBodyView(
                        modifier = Modifier
                            .padding(top = 8.Sdp)
                            .weight(1f, fill = false)
                    ) { body ->
                        Text(
                            text = body,
                            style = TextStyle(color = NativeAdColors.body, fontSize = 11.sp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            NativeCtaButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 16.Sdp),
                isMetaLow = isMetaLow
            )
        }
    }
}

@Composable
fun NativeMediaLeftCtaTop(
    nativeAdView: ApNativeAd,
    isMeta: Boolean = false,
    isMetaLow: Boolean = false,
    modifier: Modifier = Modifier
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NativeCtaButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 16.Sdp),
                isMetaLow = isMetaLow
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingTop(16.Sdp)
            ) {
                NativeAdMediaView(
                    modifier =Modifier.  padding(1.Sdp)
                        .size(120.dp),
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.Sdp)
                        .padding(top = 10.Sdp)
                ) {
                    NativeHeadlineRowSmall()
                    if (isMeta) {
                        MetaTextSponsor()
                    }
                    NativeAdBodyView(
                        modifier = Modifier
                            .padding(top = 8.Sdp)
                            .weight(1f, fill = false)
                    ) { body ->
                        Text(
                            text = body,
                            style = TextStyle(color = NativeAdColors.body, fontSize = 11.sp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NativeMediumMediaLeftCtaRight(
    nativeAdView: ApNativeAd,
    isMeta: Boolean = false,
    isMetaLow: Boolean = false,
    modifier: Modifier = Modifier
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier.wrapContentHeight()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.Sdp)
        ) {
            NativeAdMediaView(
                modifier = Modifier.size(142.dp),
                scaleType = ImageView.ScaleType.CENTER_CROP
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.Sdp)
                    .wrapContentHeight()
                    .height(142.dp)
                    .paddingTop(3.dp)
            ) {
                NativeHeadlineRow()
                NativeBodyText()
                if (isMeta) {
                    MetaTextSponsor()
                }
                SpaceF()
                NativeCtaButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.Sdp, bottom = 4.Sdp)
                        .height(36.Sdp),
                    isMetaLow = isMetaLow
                )
            }
        }
    }
}

@Composable
fun ShimmerMediaLeftComposeView(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
            .padding(bottom = 16.Sdp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier. padding(1.Sdp)
                    .size(120.dp)
                    .background(NativeAdColors.shimmer)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.Sdp)
                    .padding(top = 10.Sdp)
            ) {
                ShimmerHeadlineRowSmall()
                ShimmerBodyBox()
            }
        }
        ShimmerCtaCircleButton(
            modifier = Modifier
                .padding(horizontal = 16.Sdp)
                .padding(top = 16.Sdp)
        )
    }
}

@Composable
fun ShimmerMediaLeftCtaTopView(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
    ) {
        ShimmerCtaCircleButton(
            modifier = Modifier
                .padding(horizontal = 16.Sdp)
                .padding(top = 16.Sdp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .paddingTop(16.Sdp)
        ) {
            Box(
                modifier = Modifier. padding(1.Sdp)
                    .size(120.dp)
                    .background(NativeAdColors.shimmer)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.Sdp)
                    .padding(top = 10.Sdp)
            ) {
                ShimmerHeadlineRowSmall()
                ShimmerBodyBox()
            }
        }
    }
}

@Composable
fun ShimmerMediaLeftCtaRightView(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.Sdp)
        ) {
            Box(
                modifier = Modifier
                    .size(142.dp)
                    .background(NativeAdColors.shimmer)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(142.dp)
                    .padding(horizontal = 10.Sdp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.Sdp)
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(NativeAdColors.shimmer)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.Sdp)
                        .background(NativeAdColors.shimmer, RoundedCornerShape(5.dp))
                        .size(24.dp, 14.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 4.Sdp)
                        .fillMaxWidth()
                        .weight(1f)
                        .background(NativeAdColors.shimmer)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.Sdp)
                        .height(32.Sdp)
                        .background(NativeAdColors.shimmer, RoundedCornerShape(8.Sdp))
                )
            }
        }
    }
}

@PhonePreview
@Composable
fun NativeMediaLeftCtaBotPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGrey)
    ) {
        NativeMediaLeftCtaBot(nativeAdView = ApNativeAd(), modifier = Modifier.background(
            white
        ))
        SpaceH(16.Sdp)
        NativeMediaLeftCtaTop(nativeAdView = ApNativeAd(), modifier = Modifier.background(
            white
        ))
        SpaceH(16.Sdp)
        NativeMediumMediaLeftCtaRight(
            nativeAdView = ApNativeAd(),
            modifier = Modifier.background(white)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ShimmerMediaLeftComposePreview() {
    ShimmerMediaLeftComposeView(isShowShimmer = true)
}

@Preview(showBackground = true)
@Composable
fun ShimmerMediaLeftCtaTopPreview() {
    ShimmerMediaLeftCtaTopView(isShowShimmer = true)
}

@Preview(showBackground = true)
@Composable
fun ShimmerMediaLeftCtaRightPreview() {
    ShimmerMediaLeftCtaRightView(isShowShimmer = true)
}
