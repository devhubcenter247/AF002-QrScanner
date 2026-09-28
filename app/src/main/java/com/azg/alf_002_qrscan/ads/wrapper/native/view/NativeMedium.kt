package com.azg.alf_002_qrscan.ads.wrapper.native.view

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azg.alf_002_qrscan.style.PhonePreview
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingStart
import com.azg.alf_002_qrscan.style.rememberShimmerState
import com.azg.alf_002_qrscan.style.shimmer
import com.azg.alf_002_qrscan.style.white
import com.lib.ads.application.ads.ads.wrapper.ApNativeAd
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdBodyView
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdMediaView
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdView
import com.sdp.ssp.android.Sdp


@Composable
fun ShimmerNativeFullScreenAd(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Box(
        modifier = modifier
            .fillMaxSize()
            .shimmer(state = shimmerState, visible = isShowShimmer)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(white)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.Sdp, bottom = 10.Sdp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(4.8f)
                    .padding(start = 16.Sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerIconCircle()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.Sdp)
                ) {
                    ShimmerHeadlineRow()
                    ShimmerBodyBox()
                }
            }
            Box(
                modifier = Modifier
                    .weight(2.2f)
                    .padding(horizontal = 16.Sdp)
                    .height(40.dp)
                    .background(lightGrey, RoundedCornerShape(28.dp))
            )
        }
    }
}

@Composable
fun NativeFullScreenAd(
    nativeAdView: ApNativeAd,
    isMetaLow: Boolean = false,
    modifier: Modifier = Modifier
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            NativeAdMediaView(
                modifier = Modifier.matchParentSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.Sdp, bottom = 10.Sdp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(4.8f)
                        .padding(start = 16.Sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        NativeBodyText()
                    }
                }
                NativeCtaButton(
                    modifier = Modifier
                        .weight(2.25f)
                        .padding(horizontal = 16.Sdp),
                    isMetaLow = isMetaLow
                )
            }
        }

    }
}


@Composable
fun NativeMediumCtaTop(
    nativeAdView: ApNativeAd,
    isMeta: Boolean = false,
    isMetaLow: Boolean = false,
    modifier: Modifier = Modifier
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .paddingBottom(10.Sdp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMeta && isMetaLow) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.Sdp)
                        .paddingStart(16.Sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeBodyText(Modifier.weight(1f))
                    NativeCtaButton(
                        modifier = Modifier
                            .fillMaxWidth(0.38f)
                            .padding(horizontal = 16.Sdp),
                        isMetaLow = true
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 16.Sdp)
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        MetaTextSponsor()
                    }
                }
                NativeAdMediaView(
                    modifier = Modifier
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                        .wrapContentWidth()
                        .height(120.dp)
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp),
                )
            } else if (isMeta && !isMetaLow) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.Sdp)
                        .paddingStart(16.Sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeBodyText(Modifier.weight(1f))
                    NativeCtaButton(
                        modifier = Modifier
                            .fillMaxWidth(0.38f)
                            .padding(horizontal = 16.Sdp),
                        isMetaLow = false
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 16.Sdp)
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        MetaTextSponsor()
                    }
                }
                NativeAdMediaView(
                    modifier = Modifier
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                        .wrapContentWidth()
                        .height(120.dp)
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp)
                )
            } else {
                NativeCtaButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 14.Sdp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 16.Sdp)
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        NativeBodyText()
                    }
                }
                NativeAdMediaView(
                    modifier = Modifier
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                        .wrapContentWidth()
                        .height(120.dp)
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp),
                )
            }
        }
    }
}

@Composable
fun NativeMediumCtaBottom(
    nativeAdView: ApNativeAd,
    isMeta: Boolean = false,
    isMetaLow: Boolean = false,
    modifier: Modifier = Modifier,
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .paddingBottom(16.Sdp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMeta && isMetaLow) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 16.Sdp)
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        MetaTextSponsor()
                    }
                }
                NativeAdMediaView(
                    modifier = Modifier
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                        .wrapContentWidth()
                        .height(120.dp)
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .paddingStart(16.Sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeBodyText(Modifier.weight(1f))
                    NativeCtaButton(
                        modifier = Modifier
                            .fillMaxWidth(0.38f)
                            .padding(horizontal = 16.Sdp)
                            .padding(top = 10.Sdp), isMetaLow = true
                    )
                }

            } else if (isMeta && !isMetaLow) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 16.Sdp)
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        NativeBodyText()
                    }
                }
                NativeAdMediaView(
                    modifier = Modifier
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                        .wrapContentWidth()
                        .height(120.dp)
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp),
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .paddingStart(16.Sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeBodyText(Modifier.weight(1f))
                    NativeCtaButton(
                        modifier = Modifier
                            .fillMaxWidth(0.38f)
                            .padding(horizontal = 16.Sdp)
                            .padding(top = 10.Sdp), isMetaLow = false
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 16.Sdp)
                ) {
                    NativeIconContent()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.Sdp)
                    ) {
                        NativeHeadlineRow()
                        NativeBodyText()
                    }
                }
                NativeAdMediaView(
                    modifier = Modifier
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                        .wrapContentWidth()
                        .height(120.dp)
                        .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp),
                )
                NativeCtaButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.Sdp)
                        .padding(top = 10.Sdp)
                )
            }

        }
    }
}

@Composable
fun NativeMediumCtaBotRight(
    nativeAdView: ApNativeAd, isMetaLow: Boolean = false, modifier: Modifier = Modifier
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .paddingBottom(10.Sdp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 16.Sdp), verticalAlignment = Alignment.CenterVertically
            ) {
                NativeHeadlineRow()
            }
            NativeAdMediaView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 10.Sdp), scaleType = ImageView.ScaleType.CENTER_CROP
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 10.Sdp), verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(4.8f), verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeIconContent()
                    NativeAdBodyView(
                        modifier = Modifier.padding(horizontal = 8.Sdp)
                    ) { body ->
                        Text(
                            text = body,
                            style = TextStyle(color = NativeAdColors.body, fontSize = 11.sp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                NativeCtaButton(
                    modifier = Modifier
                        .weight(2.2f)
                        .padding(start = 8.Sdp), isMetaLow = isMetaLow
                )
            }
        }
    }
}

@Composable
fun NativeMediumCtaTopRight(
    nativeAdView: ApNativeAd, isMetaLow: Boolean = false, modifier: Modifier = Modifier
) {
    NativeAdView(nativeAd = nativeAdView, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .paddingBottom(10.Sdp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 10.Sdp), verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(4.8f), verticalAlignment = Alignment.CenterVertically
                ) {
                    NativeIconContent()
                    NativeAdBodyView(
                        modifier = Modifier.padding(horizontal = 8.Sdp)
                    ) { body ->
                        Text(
                            text = body,
                            style = TextStyle(color = NativeAdColors.body, fontSize = 11.sp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                NativeCtaButton(
                    modifier = Modifier
                        .weight(2.2f)
                        .padding(start = 8.Sdp), isMetaLow = isMetaLow
                )
            }
            NativeAdMediaView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 10.Sdp), scaleType = ImageView.ScaleType.CENTER_CROP
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.Sdp)
                    .padding(top = 10.Sdp), verticalAlignment = Alignment.CenterVertically
            ) {
                NativeHeadlineRow()
            }
        }
    }
}

@Composable
fun ShimmerMediumCtaBotRightView(
    isShowShimmer: Boolean,
    modifier: Modifier = Modifier
) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
            .padding(bottom = 10.Sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.Sdp)
                .padding(top = 16.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(NativeAdColors.shimmer, RoundedCornerShape(5.dp))
                    .size(24.dp, 14.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 10.Sdp)
                    .weight(1f)
                    .height(14.dp)
                    .background(NativeAdColors.shimmer)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp)
                .background(NativeAdColors.shimmer)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(4.8f), verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerIconCircle()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.Sdp)
                        .weight(1f)
                        .height(24.dp)
                        .background(NativeAdColors.shimmer)
                )
            }
            Box(
                modifier = Modifier
                    .weight(2.2f)
                    .padding(start = 8.Sdp)
                    .height(40.dp)
                    .background(NativeAdColors.shimmer, RoundedCornerShape(8.Sdp))
            )
        }
    }
}

@Composable
fun ShimmerMediumCtaTopRightView(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
            .padding(bottom = 10.Sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(4.8f), verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerIconCircle()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.Sdp)
                        .weight(1f)
                        .height(24.dp)
                        .background(NativeAdColors.shimmer)
                )
            }
            Box(
                modifier = Modifier
                    .weight(2.2f)
                    .padding(start = 8.Sdp)
                    .height(40.dp)
                    .background(NativeAdColors.shimmer, RoundedCornerShape(8.Sdp))
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp)
                .background(NativeAdColors.shimmer)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(NativeAdColors.shimmer, RoundedCornerShape(5.dp))
                    .size(24.dp, 14.dp)
            )
            Box(
                modifier = Modifier
                    .padding(start = 10.Sdp)
                    .weight(1f)
                    .height(16.Sdp)
                    .background(NativeAdColors.shimmer)
            )
        }
    }
}

@Composable
fun ShimmerMediumCtaTopView(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
            .padding(bottom = 10.Sdp)
    ) {
        ShimmerCtaRoundedButton(
            modifier = Modifier
                .padding(horizontal = 16.Sdp)
                .padding(top = 14.Sdp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.Sdp)
                .padding(top = 16.Sdp)
        ) {
            ShimmerIconCircle()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.Sdp)
            ) {
                ShimmerHeadlineRow()
                ShimmerBodyBox()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp)
                .background(NativeAdColors.shimmer)
        )
    }
}

@Composable
fun ShimmerMediumCtaBottomView(isShowShimmer: Boolean, modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmerState(900)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shimmer(state = shimmerState, visible = isShowShimmer)
            .padding(bottom = 16.Sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.Sdp)
                .padding(top = 16.Sdp)
        ) {
            ShimmerIconCircle()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.Sdp)
            ) {
                ShimmerHeadlineRow()
                ShimmerBodyBox()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp)
                .background(NativeAdColors.shimmer)
        )
        ShimmerCtaRoundedButton(
            modifier = Modifier
                .padding(horizontal = 16.Sdp)
                .padding(top = 10.Sdp)
        )
    }
}

@PhonePreview
@Composable
fun NativeMediumCtaTopPreview() {
    Column(
        Modifier
            .fillMaxSize()
            .background(lightGrey)
    ) {
        NativeMediumCtaTop(
            nativeAdView = ApNativeAd(), modifier = Modifier.background(
                white
            )
        )
        SpaceH(16.Sdp)
        NativeMediumCtaTop(
            nativeAdView = ApNativeAd(),
            modifier = Modifier.background(white),
            isMetaLow = true,
            isMeta = true
        )
        SpaceH(16.Sdp)
        NativeMediumCtaTop(
            nativeAdView = ApNativeAd(),
            modifier = Modifier.background(white),
            isMetaLow = false,
            isMeta = true
        )
    }

}

@PhonePreview
@Composable
fun NativeMediumCtaBottomPreview() {
    Column(
        Modifier
            .fillMaxSize(1f)
            .background(lightGrey)
    ) {
        NativeMediumCtaBottom(
            nativeAdView = ApNativeAd(), modifier = Modifier.background(
                white
            )
        )
        SpaceH(16.Sdp)
        NativeMediumCtaBottom(
            nativeAdView = ApNativeAd(),
            modifier = Modifier.background(white),
            isMetaLow = true,
            isMeta = true
        )
        SpaceH(16.Sdp)
        NativeMediumCtaBottom(
            nativeAdView = ApNativeAd(),
            modifier = Modifier.background(white),
            isMetaLow = false,
            isMeta = true
        )
    }

}


@Preview(showBackground = true)
@Composable
fun ShimmerMediumCtaBotRightPreview() {
    ShimmerMediumCtaBotRightView(isShowShimmer = true)
}

@Preview(showBackground = true)
@Composable
fun ShimmerMediumCtaTopRightPreview() {
    ShimmerMediumCtaTopRightView(isShowShimmer = true)
}

@Preview(showBackground = true)
@Composable
fun ShimmerMediumCtaTopPreview() {
    ShimmerMediumCtaTopView(isShowShimmer = true)
}

@Preview(showBackground = true)
@Composable
fun ShimmerMediumCtaBottomPreview() {
    ShimmerMediumCtaBottomView(isShowShimmer = true)
}

@PhonePreview
@Composable
fun NativeFullScreenAdPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(white)
    ) {
        NativeFullScreenAd(nativeAdView = ApNativeAd())
    }
}

@PhonePreview
@Composable
fun ShimmerNativeFullScreenAdPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(white)
    ) {
        ShimmerNativeFullScreenAd(isShowShimmer = true)
    }
}
