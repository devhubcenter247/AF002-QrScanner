package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerMediumCtaTopView
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.InputValue
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.ui.main.create.CreateFormHeader
import com.azg.alf_002_qrscan.ui.main.create.FormField
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputPhone", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewPhone() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.Sdp)
        ) {
            InputQrPhoneContent(
                valueInput = "",
                onValueChange = {},
                theme = ColorTheme.BLUE
            )
        }
    }
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputText", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewText() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrTextContent(
                valueInput = "",
                onValueChange = {}
            )
        }
    }
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputText", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewUrl() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrUrlContent(
                valueInput = "",
                onValueChange = {}
            )
        }
    }
}
@Composable
fun ColumnScope.InputQrUrl(
    viewModel: CreateViewModel = koinViewModel(),
) {
    val nativeAdHolder = rememberNativeAdPreload(
        tag = NativePlacement.NativeMediumAll.tag,
        enabled = NativePlacement.NativeMediumAll.canShowAds,
        fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        nativeAdHolder.request()
    }
    val valueInput by viewModel.createQrUrl.collectAsStateWithLifecycle("")

    InputQrUrlContent(
        valueInput = valueInput,
        onValueChange = { viewModel.setInputUrl(it) },
        nativeAdHolder = nativeAdHolder
    )
}

private val urlAccentColor = Color(0xFFEF4444)

@Composable
fun ColumnScope.InputQrUrlContent(
    valueInput: String,
    onValueChange: (String) -> Unit,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    val colorTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp}
    CreateFormHeader(
        icon = R.drawable.ic_create_url,
        title = stringResource(R.string.url),
        subtitle = stringResource(R.string.url_subtitle),
        accentColor = urlAccentColor
    )
    TextView(
        stringResource(R.string.url), modifier = Modifier
            .paddingTop(12.Sdp)
            .fillMaxWidth(),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )
    Box(
        Modifier
            .paddingTop(6.Sdp)
            .fillMaxWidth()
            .wrapContentHeight()
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(14.Sdp)
            )
            .background(white, RoundedCornerShape(14.Sdp))
            .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
            .padding(4.Sdp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            IconView(
                item = R.drawable.ic_form_link,
                tint = colorTheme.buttonColor,
                icSize = 18,
                modifier = Modifier
                    .paddingTop(10.Sdp)
                    .paddingHorizontal(8.Sdp)
            )
            InputValue(
                valueInput,
                hint = stringResource(R.string.enter_url),
                hintColor = lightGrey,
                textAlignment = TextAlign.Justify,
                boxModifier = Modifier
                    .weight(1f)
                    .paddingVertical(2.Sdp)
                    .wrapContentHeight()
                    .defaultMinSize(minHeight = 120.Sdp),
            ) {
                if (it.length > 1500) {
                    return@InputValue
                }
                onValueChange(it)
            }
        }
        if (valueInput.isNotEmpty()) {
            Image(
                imageVector = Icons.Default.Close,
                modifier = Modifier
                    .size(18.Sdp)
                    .align(Alignment.TopEnd)
                    .background(colorTheme.buttonColor.opacity(20f), CircleShape)
                    .padding(2.Sdp)
                    .onClick {
                        onValueChange("")
                    },
                contentDescription = null
            )
        }
    }
    SpaceF()
    NativeAdCard(
        holder = nativeAdHolder,
        modifier = Modifier
            .fillMaxWidth()
            .paddingVertical(10.Sdp)
            .clip(RoundedCornerShape(10.Sdp))
            .background(white),
        loading = { isLoading ->
            if (LocalInspectionMode.current) {
                ShimmerMediumCtaTopView(isShowShimmer = true)
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

@Composable
fun ColumnScope.InputQrPhone(
    viewModel: CreateViewModel = viewModel(),
) {
    val valueInput by viewModel.createQrPhone.collectAsStateWithLifecycle("")
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)

    val nativeAdHolder = rememberNativeAdPreload(
        tag = NativePlacement.NativeMediumAll.tag,
        enabled = NativePlacement.NativeMediumAll.canShowAds,
        fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        nativeAdHolder.request()
    }

    InputQrPhoneContent(
        valueInput = valueInput,
        onValueChange = { viewModel.setInputPhone(it) },
        theme = theme,
        nativeAdHolder = nativeAdHolder
    )
}

private val phoneAccentColor = Color(0xFF22C55E)

@Composable
fun ColumnScope.InputQrPhoneContent(
    valueInput: String,
    onValueChange: (String) -> Unit,
    theme: ColorTheme,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    CreateFormHeader(
        icon = R.drawable.ic_create_phone,
        title = stringResource(R.string.phone),
        subtitle = stringResource(R.string.phone_subtitle),
        accentColor = phoneAccentColor
    )
    FormField(
        labelRes = R.string.phone_number,
        value = valueInput,
        colorTheme = theme,
        keyboardType = KeyboardType.Phone,
        leadingIcon = R.drawable.ic_form_phone,
        cornerRadius = 14.Sdp,
    ) {
        if (it.length < 30) {
            onValueChange(it)
        }
    }
    SpaceF()
    NativeAdCard(
        holder = nativeAdHolder,
        modifier = Modifier
            .fillMaxWidth()
            .paddingVertical(10.Sdp)
            .clip(RoundedCornerShape(10.Sdp))
            .background(white),
        loading = { isLoading ->
            if (LocalInspectionMode.current) {
                ShimmerMediumCtaTopView(isShowShimmer = true)
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

@Composable
fun ColumnScope.InputQrText(
    viewModel: CreateViewModel = viewModel(),
) {

    val nativeAdHolder = rememberNativeAdPreload(
        tag = NativePlacement.NativeMediumAll.tag,
        enabled = NativePlacement.NativeMediumAll.canShowAds,
        fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        nativeAdHolder.request()
    }
    val valueInput by viewModel.createQrText.collectAsStateWithLifecycle("")

    InputQrTextContent(
        valueInput = valueInput,
        onValueChange = { viewModel.setInputTextQr(it) },
        nativeAdHolder = nativeAdHolder
    )
}

private val textAccentColor = Color(0xFF6B7280)

@Composable
fun ColumnScope. InputQrTextContent(
    valueInput: String,
    onValueChange: (String) -> Unit,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    val colorTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp}
    CreateFormHeader(
        icon = R.drawable.ic_create_text,
        title = stringResource(R.string.text),
        subtitle = stringResource(R.string.text_subtitle),
        accentColor = textAccentColor
    )
    TextView(
        stringResource(R.string.text), modifier = Modifier
            .paddingTop(12.Sdp)
            .fillMaxWidth(),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )
    Box(
        Modifier
            .paddingTop(6.Sdp)
            .fillMaxWidth()
            .wrapContentHeight()
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(14.Sdp)
            )
            .background(white, RoundedCornerShape(14.Sdp))
            .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
            .padding(4.Sdp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            IconView(
                item = R.drawable.ic_form_note,
                tint = colorTheme.buttonColor,
                icSize = 18,
                modifier = Modifier
                    .paddingTop(10.Sdp)
                    .paddingHorizontal(8.Sdp)
            )
            InputValue(
                valueInput,
                hint = stringResource(R.string.enter_text_here),
                hintColor = lightGrey,
                textAlignment = TextAlign.Justify,
                boxModifier = Modifier
                    .weight(1f)
                    .paddingVertical(2.Sdp)
                    .wrapContentHeight()
                    .defaultMinSize(minHeight = 120.Sdp),
            ) {
                if (it.length > 1000) {
                    return@InputValue
                }
                onValueChange(it)
            }
        }
        if (valueInput.isNotEmpty()) {
            Image(
                imageVector = Icons.Default.Close,
                modifier = Modifier
                    .size(18.Sdp)
                    .align(Alignment.TopEnd)
                    .background(colorTheme.buttonColor.opacity(20f), CircleShape)
                    .padding(4.Sdp)
                    .onClick {
                        onValueChange("")
                    },
                contentDescription = null
            )
        }
    }
    SpaceF()
    NativeAdCard(
        holder = nativeAdHolder,
        modifier = Modifier
            .fillMaxWidth()
            .paddingVertical(10.Sdp)
            .clip(RoundedCornerShape(10.Sdp))
            .background(white),
        loading = { isLoading ->
            if (LocalInspectionMode.current) {
                ShimmerMediumCtaTopView(isShowShimmer = true)
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
