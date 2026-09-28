package com.azg.alf_002_qrscan.ui.main.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.CanvasHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.interstitial.InterstitialAdConfig
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerMediumCtaTopView
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerSmallCtaBotView

import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.color2121218
import com.azg.alf_002_qrscan.style.fontFamily
import com.azg.alf_002_qrscan.style.fontPadding
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.heightPercentDp
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.ui.main.MainActivity
import com.azg.alf_002_qrscan.ui.main.create.CreateBarcodeActivity
import com.azg.alf_002_qrscan.ui.main.create.InputDataActivity
import com.azg.alf_002_qrscan.utils.BarcodeType
import com.azg.alf_002_qrscan.utils.Constant
import com.azg.alf_002_qrscan.viewmodel.HomeViewModel
import com.azg.alf_002_qrscan.viewmodel.fakeDataHistoryDao
import com.azg.alf_002_qrscan.widget.color86869c
import com.create.custom.qr.QrType
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

@Preview(showBackground = true, name = "SettingScreen", device = PIXEL_4_XL)
@Composable
fun CreatePreview() {
    AppViewTheme {
        val nav = rememberNavController()
        val fakeActivity = MainActivity()
        val vm = remember { object : HomeViewModel(AppRepository(fakeDataHistoryDao)) {} }

        AppViewTheme {
            Surface(color = Color.White) {
                CreateScreen(
                    currentTheme = ColorTheme.BLUE,
                    activity = fakeActivity,
                    onBack = { /* no-op */ }
                )
            }
        }
    }
}

@Composable
fun CreateScreen(
    currentTheme: ColorTheme,
    activity: MainActivity,
    onQuestion: () -> Unit = {},
    nativeAdHolder: NativeAdPreloadHolder? = null,
    onBack: () -> Unit
) {
    var isCreateQr by remember {
        mutableStateOf(true)
    }
    var isCreateBarcode by remember {
        mutableStateOf(false)
    }
    BoxParentContent(
        onBack = {
            onBack.invoke()
        },
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.bgColor)
            .statusBarPadding()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .paddingHorizontal(12.Sdp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    TextView(
                        R.string.create,
                        font = R.font.rb_600,
                        color = black,
                        textSize = 16.Ssp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    SpaceH(4)
                    TextView(
                        R.string.create_subtitle,
                        font = R.font.rb_400,
                        color = color86869c,
                        textSize = 11.Ssp,
                        modifier = Modifier.fillMaxWidth(0.6f),
                        textAlign = TextAlign.Start
                    )
                }
                SpaceW(12.Sdp)
                Box(
                    Modifier
                        .size(32.Sdp)
                        .neonState(
                            true,
                            outerRadius = 4.Sdp,
                            outerSpread = 1.Sdp,
                            innerRadius = 4.Sdp,
                            colors = listOf(
                                currentTheme.buttonColor.opacity(30f),
                                currentTheme.buttonColor.opacity(30f)
                            ),
                            shape = CircleShape
                        )
                        .background(white, CircleShape)
                        .onClick { onQuestion() },
                    contentAlignment = Alignment.Center
                ) {
                    IconView(
                        item = R.drawable.ic_help_circle,
                        tint = currentTheme.buttonColor,
                        icSize = 18
                    )
                }
            }
            SpaceH(20)

            Row(
                Modifier
                    .fillMaxWidth()
                    .neonState(
                        true,
                        outerRadius = 2.Sdp,
                        outerSpread = 0.Sdp,
                        innerRadius = 4.Sdp,
                        colors = listOf(
                            black.opacity(5f),
                            black.opacity(5f)
                        ),
                        shape = CircleShape
                    )
                    .clip(RoundedCornerShape(100.Sdp))
                    .background(white)

                    .padding(4.Sdp),
                horizontalArrangement = Arrangement.spacedBy(4.Sdp)
            ) {
                ItemHeaderCreate(
                    isCreateQr,
                    label = R.string.qr_code,
                    icon = R.drawable.ic_qrcode_scan,
                    currentTheme,
                    Modifier.weight(1f)
                ) {
                    isCreateQr = true
                    isCreateBarcode = false
                }
                ItemHeaderCreate(
                    isCreateBarcode,
                    label = R.string.bar_code,
                    icon = R.drawable.ic_barcode_scan,
                    currentTheme,
                    Modifier.weight(1f)
                ) {
                    isCreateBarcode = true
                    isCreateQr = false
                }
            }
            if (isCreateQr && !isCreateBarcode) {
                CreateQrScreen(activity, currentTheme)
            }
            if (!isCreateQr && isCreateBarcode) {
                CreateBarcodeScreen(activity, currentTheme)
            }
        }
    }
}

@Composable
fun ColumnScope.CreateBarcodeScreen(activity: MainActivity, currentTheme: ColorTheme) {
    val isInspectionMode = LocalInspectionMode.current
    val canShowAd = if (isInspectionMode) true else NativePlacement.NativeSmallAll.canShowAds
    val nativeAdHolder = rememberNativeAdPreload(
        tag = NativePlacement.NativeSmallAll.tag,
        enabled = canShowAd,
        fallbackAdUnitIds = if (isInspectionMode) emptyList() else NativePlacement.NativeSmallAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        if (!isInspectionMode) {
            nativeAdHolder.request()
        }
    }

    CreateBarcodeContent(
        currentTheme = currentTheme,
        canShowAd = canShowAd,
        nativeAdHolder = nativeAdHolder,
        onBarcodeTypeClick = { barcodeType ->
            InterstitialAdConfig.showInterAll(activity) {
                activity.launchActivity<CreateBarcodeActivity>(
                    Constant.BARCODE_TYPE to barcodeType
                )
            }
        }
    )
}

@Composable
fun ColumnScope.CreateBarcodeContent(
    currentTheme: ColorTheme,
    canShowAd: Boolean,
    nativeAdHolder: NativeAdPreloadHolder?,
    onBarcodeTypeClick: (BarcodeType) -> Unit
) {
    Column(
        Modifier
            .paddingTop(12.Sdp)
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconView(item = R.drawable.ic_sparkle, tint = currentTheme.buttonColor, icSize = 16)
            SpaceW(6.Sdp)
            TextView(
                R.string.quick_create,
                font = R.font.rb_600,
                color = black,
                textSize = 14.Ssp
            )
        }
        SpaceH(12.Sdp)

        val barcodeTypes = listOf(
            BarcodeType.AZTEC,
            BarcodeType.PDF_417,
            BarcodeType.DATA_MATRIX,
            BarcodeType.EAN_8,
            BarcodeType.EAN_13,
            BarcodeType.UPC_A,
            BarcodeType.UPC_E,
            BarcodeType.CODE_39,
            BarcodeType.CODE_93,
            BarcodeType.CODE_128,
            BarcodeType.CODABAR,
            BarcodeType.ITF,
        )
        barcodeTypes.chunked(3).forEachIndexed { index, row ->
            if (index > 0) SpaceH(10.Sdp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.Sdp)) {
                row.forEach { type ->
                    ItemCreateBarCode(
                        icon = type.iconRes,
                        label = type.titleRes,
                        colorTheme = currentTheme,
                        accentColor = type.accentColor,
                        modifier = Modifier.weight(1f)
                    ) { onBarcodeTypeClick(type) }
                }
            }
            if (index == 0) {
                if (canShowAd) {
                    SpaceH(10.Sdp)
                    NativeAdCard(
                        holder = nativeAdHolder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.Sdp))
                            .background(white),
                        loading = { isLoading ->
                            if (LocalInspectionMode.current) {
                                ShimmerSmallCtaBotView(isShowShimmer = true)
                            } else {
                                NativeShimmerByLayout(
                                    NativePlacement.NativeSmallAll,
                                    isLoading,
                                    Modifier.fillMaxWidth()
                                )
                            }
                        },
                        nativeView = { ad ->
                            NativeAdByLayout(NativePlacement.NativeSmallAll, ad)
                        },
                    )
                }
            }
        }
        SpaceH(heightPercentDp(10f))
    }
}

private val BarcodeType.accentColor: Color
    get() = when (this) {
        BarcodeType.AZTEC -> Color(0xFF8B5CF6)
        BarcodeType.PDF_417 -> Color(0xFFEF4444)
        BarcodeType.DATA_MATRIX -> Color(0xFF14B8A6)
        BarcodeType.EAN_8 -> Color(0xFF22C55E)
        BarcodeType.EAN_13 -> Color(0xFF3B82F6)
        BarcodeType.UPC_A -> Color(0xFF6366F1)
        BarcodeType.UPC_E -> Color(0xFFEC4899)
        BarcodeType.CODE_39 -> Color(0xFFF59E0B)
        BarcodeType.CODE_93 -> Color(0xFFA16207)
        BarcodeType.CODE_128 -> Color(0xFF6B7280)
        BarcodeType.CODABAR -> Color(0xFF10B981)
        BarcodeType.ITF -> Color(0xFF4F46E5)
        BarcodeType.OTHER -> Color(0xFF6B7280)
    }

@Composable
fun ColumnScope.CreateQrScreen(activity: MainActivity, currentTheme: ColorTheme) {
    val isInspectionMode = LocalInspectionMode.current

    val canShowAd = if (isInspectionMode) true else NativePlacement.NativeSmallAll.canShowAds
    val nativeAdHolder = rememberNativeAdPreload(
        tag = NativePlacement.NativeSmallAll.tag,
        enabled = canShowAd,
        fallbackAdUnitIds = if (isInspectionMode) emptyList() else NativePlacement.NativeSmallAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        if (!isInspectionMode) {
            nativeAdHolder.request()
        }
    }
    Column(
        Modifier
            .paddingTop(12.Sdp)
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconView(item = R.drawable.ic_sparkle, tint = currentTheme.buttonColor, icSize = 16)
            SpaceW(6.Sdp)
            TextView(
                R.string.quick_create,
                font = R.font.rb_600,
                color = black,
                textSize = 14.Ssp
            )
        }
        SpaceH(12.Sdp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.Sdp)) {
            ItemCreateQr(
                icon = R.drawable.ic_create_text,
                label = R.string.text,
                colorTheme = currentTheme,
                accentColor = colorText,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Text
                    )
                }
            }
            ItemCreateQr(
                icon = R.drawable.ic_create_url,
                label = R.string.url,
                colorTheme = currentTheme,
                accentColor = colorUrl,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Website
                    )
                }
            }
            ItemCreateQr(
                icon = R.drawable.ic_create_email,
                label = R.string.email,
                colorTheme = currentTheme,
                accentColor = colorEmail,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Email
                    )
                }
            }
        }
        if (canShowAd) {
            NativeAdCard(
                holder = nativeAdHolder,
                modifier = Modifier
                    .paddingVertical(10.Sdp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.Sdp))
                    .background(white),
                loading = { isLoading ->
                    NativeShimmerByLayout(
                        NativePlacement.NativeMediumAll,
                        isLoading,
                        Modifier.fillMaxWidth()
                    )
                },
                nativeView = { ad ->
                    NativeAdByLayout(NativePlacement.NativeMediumAll, ad)
                },
            )
        } else {
            SpaceH(10.Sdp)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.Sdp)) {
            ItemCreateQr(
                icon = R.drawable.ic_create_message,
                label = R.string.sms,
                colorTheme = currentTheme,
                accentColor = colorSms,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Message
                    )
                }
            }
            ItemCreateQr(
                icon = R.drawable.ic_create_phone,
                label = R.string.phone,
                colorTheme = currentTheme,
                accentColor = colorPhone,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Phone
                    )
                }
            }
            ItemCreateQr(
                icon = R.drawable.ic_create_contact,
                label = R.string.contact,
                colorTheme = currentTheme,
                accentColor = colorContact,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Contact
                    )
                }
            }
        }
        SpaceH(10.Sdp)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.Sdp)) {
            ItemCreateQr(
                icon = R.drawable.ic_create_calendar,
                label = R.string.event,
                colorTheme = currentTheme,
                accentColor = colorEvent,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Event
                    )
                }
            }
            ItemCreateQr(
                icon = R.drawable.ic_create_location,
                label = R.string.location,
                colorTheme = currentTheme,
                accentColor = colorLocation,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Location
                    )
                }
            }
            ItemCreateQr(
                icon = R.drawable.ic_create_wifi,
                label = R.string.wifi,
                colorTheme = currentTheme,
                accentColor = colorWifi,
                modifier = Modifier.weight(1f)
            ) {
                InterstitialAdConfig.showInterAll(activity) {
                    activity.launchActivity<InputDataActivity>(
                        Constant.CREATE_TYPE to QrType.Wifi
                    )
                }
            }
        }
        SpaceH(heightPercentDp(10f))
    }
}

private val colorText = Color(0xFF6B7280)
private val colorUrl = Color(0xFFEF4444)
private val colorEmail = Color(0xFF3B82F6)
private val colorSms = Color(0xFF8B5CF6)
private val colorPhone = Color(0xFF22C55E)
private val colorContact = Color(0xFF2563EB)
private val colorEvent = Color(0xFFF97316)
private val colorLocation = Color(0xFF14B8A6)
private val colorWifi = Color(0xFFF59E0B)

@Composable
fun ItemCreateQr(
    @DrawableRes icon: Int = 0,
    @StringRes label: Int = 0,
    colorTheme: ColorTheme = ColorTheme.BLUE,
    accentColor: Color = colorTheme.buttonColor,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {}
) {
    Column(
        modifier
            .shadow(
                elevation = 5.Sdp,
                shape = RoundedCornerShape(16.Sdp),
                ambientColor = accentColor.opacity(0.5f),
                spotColor = accentColor.opacity(0.55f)
            )
            .clip(RoundedCornerShape(16.Sdp))
            .background(white)
            .onClick { onSelect.invoke() }
            .padding(vertical = 16.Sdp, horizontal = 6.Sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(48.Sdp)
                .clip(RoundedCornerShape(14.Sdp))
                .background(accentColor.opacity(0.18f)),
            contentAlignment = Alignment.Center
        ) {
            IconView(
                item = icon,
                tint = Color.Unspecified,
                icSize = 36
            )
        }
        SpaceH(8.Sdp)
        TextView(
            text = stringResource(label),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            txtStyle = TextStyle.Default.fontFamily(R.font.rb_500).fontPadding(false)
                .copy(
                    color = black,
                    fontSize = 12.Ssp
                )
        )
    }
}


@Composable
fun ItemCreateBarCode(
    @DrawableRes icon: Int = 0,
    @StringRes label: Int = 0,
    colorTheme: ColorTheme = ColorTheme.BLUE,
    accentColor: Color = colorTheme.buttonColor,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {}
) {
    Column(
        modifier
            .shadow(
                elevation = 3.Sdp,
                shape = RoundedCornerShape(16.Sdp),
                ambientColor = accentColor.opacity(0.25f),
                spotColor = accentColor.opacity(0.25f)
            )
            .clip(RoundedCornerShape(16.Sdp))
            .background(white)
            .onClick { onSelect.invoke() }
            .padding(vertical = 16.Sdp, horizontal = 6.Sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(48.Sdp)
                .clip(RoundedCornerShape(14.Sdp))
                .background(accentColor.opacity(0.14f)),
            contentAlignment = Alignment.Center
        ) {
            IconView(
                item = icon,
                tint = Color.Unspecified,
                icSize = 22,
                modifier = Modifier.clip(RoundedCornerShape(20))
            )
            IconView(
                item = R.drawable.ic_sparkle,
                tint = Color.White,
                icSize = 16,
                modifier = Modifier
                    .padding(3.Sdp)
                    .align(Alignment.TopEnd)
            )
        }
        SpaceH(8.Sdp)
        TextView(
            text = stringResource(label),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            txtStyle = TextStyle.Default.fontFamily(R.font.rb_500).fontPadding(false)
                .copy(
                    color = black,
                    fontSize = 12.Ssp
                )
        )
    }
}

@Composable
fun ItemHeaderCreate(
    isSelected: Boolean,
    @StringRes label: Int = 0,
    @DrawableRes icon: Int = 0,
    currentTheme: ColorTheme = ColorTheme.BLUE,
    modifier: Modifier = Modifier,
    action: (Boolean) -> Unit
) {
    Row(
        modifier
            .then(
                if (isSelected) Modifier.shadow(
                    elevation = 6.Sdp,
                    shape = RoundedCornerShape(100.Sdp),
                    ambientColor = currentTheme.buttonColor.opacity(0.4f),
                    spotColor = currentTheme.buttonColor.opacity(0.4f)
                ) else Modifier
            )
            .clip(RoundedCornerShape(100.Sdp))
            .background(if (isSelected) currentTheme.buttonColor else Color.Transparent)
            .onClick { action(true) }
            .paddingVertical(11.Sdp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconView(
            item = icon,
            tint = if (isSelected) white else gray,
            icSize = 16
        )
        SpaceW(6.Sdp)
        TextView(
            stringResource(label),
            font = R.font.rb_600,
            color = if (isSelected) white else gray,
            textSize = 13.Ssp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateBarcodePreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                CreateBarcodeContent(
                    currentTheme = ColorTheme.BLUE,
                    canShowAd = true,
                    nativeAdHolder = null,
                    onBarcodeTypeClick = {}
                )
            }
        }
    }
}
