package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerSmallCtaBotView
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.ConstraintParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.contentIfTrue
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.ResultContentCard
import com.azg.alf_002_qrscan.ui.component.ResultHeaderActionButton
import com.azg.alf_002_qrscan.ui.component.ResultInfoRow
import com.azg.alf_002_qrscan.ui.component.ResultPrimaryCtaButton
import com.azg.alf_002_qrscan.ui.component.ResultPrimaryValueCard
import com.azg.alf_002_qrscan.ui.component.ResultQrPreview
import com.azg.alf_002_qrscan.ui.component.ResultQuickAction
import com.azg.alf_002_qrscan.ui.component.ResultQuickActionsRow
import com.azg.alf_002_qrscan.ui.component.ResultTopBar
import com.azg.alf_002_qrscan.ui.component.getQrTypeInfo
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.azg.alf_002_qrscan.viewmodel.fakeDataHistoryDao
import com.create.custom.qr.QrType
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import com.azg.alf_002_qrscan.ads.wrapper.banner.BannerPlacement
import com.azg.alf_002_qrscan.ads.wrapper.banner.rememberBannerAdHolder
import com.azg.alf_002_qrscan.ads.wrapper.reward.RewardPlacement
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.borderTop
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.paddingEnd
import com.lib.ads.application.ads.helper.banner.BannerAdCard
import com.lib.ads.application.ads.helper.banner.DefaultBannerAdLoading
import org.koin.compose.viewmodel.koinViewModel
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

interface ResultAction {
    fun onBack() {}
    fun onCopy() {}
    fun onShare(bitmap: Bitmap?) {}
    fun onSave(bitmap: Bitmap?) {}
    fun onEdit() {}
    fun onDelete() {}
    fun onSearch() {}
    fun onOpenBrowser(url: String) {}
    fun onCall(phone: String) {}
    fun onSendEmail() {}
    fun onSendMessage() {}
    fun onAddContact() {}
    fun onConnectWifi() {}
    fun onOpenMap() {}
    fun onAddEvent() {}
}

@Composable
private fun rememberCreateResultCta(
    qrType: QrType,
    viewModel: CreateViewModel,
    action: ResultAction
): ResultQuickAction {
    val url by viewModel.createQrUrl.collectAsStateWithLifecycle()
    val phone by viewModel.createQrPhone.collectAsStateWithLifecycle()
    return remember(qrType, url, phone) {
        when (qrType) {
            QrType.Website -> ResultQuickAction(R.string.open_link, R.drawable.ic_app_search) {
                action.onOpenBrowser(url)
            }

            QrType.Phone -> ResultQuickAction(R.string.call, R.drawable.ic_app_call) {
                action.onCall(phone)
            }

            QrType.Email -> ResultQuickAction(R.string.send_email, R.drawable.ic_app_email) {
                action.onSendEmail()
            }

            QrType.Message -> ResultQuickAction(R.string.send_sms, R.drawable.ic_app_message) {
                action.onSendMessage()
            }

            QrType.Contact -> ResultQuickAction(R.string.add_contact, R.drawable.ic_app_call) {
                action.onAddContact()
            }

            QrType.Wifi -> ResultQuickAction(R.string.connect, R.drawable.ic_app_wifi) {
                action.onConnectWifi()
            }

            QrType.Location -> ResultQuickAction(R.string.open_map, R.drawable.ic_app_location) {
                action.onOpenMap()
            }

            QrType.Event -> ResultQuickAction(R.string.add_event, R.drawable.ic_calendar_clock) {
                action.onAddEvent()
            }

            else -> ResultQuickAction(R.string.search_on_web, R.drawable.ic_app_search) {
                action.onSearch()
            }
        }
    }
}

private val successGreen = Color(0xFF22C55E)

@Composable
fun CreateResultScreen(
    viewModel: CreateViewModel = koinViewModel(),
    qrType: QrType = QrType.Text,
    modifier: Modifier = Modifier,
    action: ResultAction
) {
    val appTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp }
    val qrVectorBitmap by viewModel.qrValueBitmap.collectAsStateWithLifecycle()
    val barcodeBitmap by viewModel.barcodeBitmap.collectAsStateWithLifecycle()
    val displayBitmap = if (qrType == QrType.Barcode) barcodeBitmap else qrVectorBitmap
    val cta = rememberCreateResultCta(qrType, viewModel, action)
    val bannerAdContent =rememberBannerAdHolder(
        placement = BannerPlacement.BannerAll,
    )
    DisposableEffect(bannerAdContent) {
        bannerAdContent.request()
        onDispose {
            bannerAdContent.cancel()
        }
    }
    ColumnParentContent(onBack = {
        action.onBack()
    }, modifier = modifier.fillMaxSize(1f)) {

        ResultTopBar(
            title = stringResource(R.string.create_result),
            onBack = { action.onBack() },
            modifier = Modifier
        ) {
            if (qrType != QrType.Barcode) {
                EditQrAdActionButton(
                    icon = R.drawable.ic_app_edit,
                    tint = appTheme.buttonColor,
                    theme = appTheme
                ) { action.onEdit() }
            }
            ResultHeaderActionButton(
                icon = R.drawable.ic_app_delete,
                tint = appTheme.buttonColor,
                theme = appTheme
            ) { action.onDelete() }
        }

        Column(
            Modifier
                .fillMaxWidth(1f)
                .weight(1f)
                .paddingVertical(4.Sdp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            ResultQrPreview(
                bitmap = displayBitmap,
                colorTheme = appTheme,
                isBarcode = qrType == QrType.Barcode
            )

            Column(
                Modifier
                    .weight(1f)
                    .paddingTop(12.Sdp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (qrType) {
                    QrType.Text -> {
                        val typeInfo = getQrTypeInfo(qrType)
                        ResultPrimaryValueCard(
                            theme = appTheme,
                            icon = typeInfo.icon,
                            accentColor = typeInfo.accentColor,
                            label = stringResource(typeInfo.title),
                            value = viewModel.createQrText.collectAsState().value,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight(),
                        )
                    }

                    QrType.Website -> {
                        val typeInfo = getQrTypeInfo(qrType)
                        ResultPrimaryValueCard(
                            theme = appTheme,
                            icon = typeInfo.icon,
                            accentColor = typeInfo.accentColor,
                            label = stringResource(typeInfo.title),
                            value = viewModel.createQrUrl.collectAsState().value,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight(),
                        )
                    }

                    QrType.Phone -> {
                        val typeInfo = getQrTypeInfo(qrType)
                        ResultPrimaryValueCard(
                            theme = appTheme,
                            icon = typeInfo.icon,
                            accentColor = typeInfo.accentColor,
                            label = stringResource(typeInfo.title),
                            value = viewModel.createQrPhone.collectAsState().value,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight()
                        )
                    }

                    else -> ItemContentCreate(
                        colorTheme = appTheme,
                        qrType, viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .wrapContentHeight()
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .paddingHorizontal(24.Sdp)
                .paddingVertical(8.Sdp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            IconView(item = R.drawable.ic_sparkle, tint = successGreen.opacity(0.5f), icSize = 14)
            SpaceW(8.Sdp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(20.Sdp)
                            .clip(CircleShape)
                            .background(successGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        IconView(item = R.drawable.ic_app_check, tint = white, icSize = 12)
                    }
                    SpaceW(6.Sdp)
                    TextView(
                        text = stringResource(R.string.created_successfully),
                        font = R.font.rb_600,
                        color = black,
                        textSize = 12.Ssp
                    )
                }
                SpaceH(2.Sdp)
                TextView(
                    text = stringResource(R.string.qr_ready_to_use),
                    font = R.font.rb_400,
                    color = gray,
                    textSize = 11.Ssp
                )
            }
            SpaceW(8.Sdp)
            IconView(item = R.drawable.ic_sparkle, tint = successGreen.opacity(0.5f), icSize = 14)
        }
        Column(
            Modifier
                .background(white, RoundedCornerShape(topStart = 20.Sdp, topEnd = 20.Sdp))
                .paddingTop(12.Sdp)
                .fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            ResultQuickActionsRow(
                theme = appTheme,
                actions = listOf(
                    ResultQuickAction(R.string.copy, R.drawable.ic_app_copy) { action.onCopy() },
                    ResultQuickAction(R.string.save, R.drawable.ic_app_save) {
                        action.onSave(bitmap = displayBitmap)
                    },
                    ResultQuickAction(R.string.share, R.drawable.ic_app_share) {
                        action.onShare(bitmap = displayBitmap)
                    },
                )
            )

            ResultPrimaryCtaButton(
                label = cta.label,
                icon = cta.icon,
                theme = appTheme
            ) { cta.onClick() }
        }
        BannerAdCard(
            holder = bannerAdContent ,
            modifier =  Modifier.fillMaxWidth().borderTop(1.Sdp, lightGrey),
            loading = {
                DefaultBannerAdLoading()
            }
        )
    }
}

@Composable
private fun EditQrAdActionButton(
    icon: Int,
    tint: Color,
    theme: ColorTheme,
    onClick: () -> Unit
) {
    val isRewardEnable = if(LocalInspectionMode.current){
        true
    } else {
        RewardPlacement.RewardEdit.canShowAds
    }
    Box(
        modifier = Modifier.size(width = 42.Sdp, height = 36.Sdp)
    ) {
        ResultHeaderActionButton(
            icon = icon,
            tint = tint,
            theme = theme,
            modifier = Modifier.align(Alignment.BottomStart)
        ) { onClick() }
        if(isRewardEnable){
            TextView(
                text = stringResource(R.string.ad_label),
                modifier = Modifier
                    .paddingTop(4.Sdp)
                    .paddingEnd(2.Sdp)
                    .align(Alignment.TopEnd)
                    .background(Color(0xFFFFC107), RoundedCornerShape(8.Sdp))
                    .paddingHorizontal(4.Sdp)
                    .paddingVertical(1.Sdp),
                textSize = 7.Ssp,
                color = black,
                font = Font(R.font.rb_700),
                maxLine = 1,
                singleLine = true
            )
        }
    }
}

@Composable
fun ItemContentCreate(
    colorTheme: ColorTheme,
    qrType: QrType = QrType.Text,
    viewModel: CreateViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val typeInfo = getQrTypeInfo(qrType)
    ResultContentCard(theme = colorTheme, modifier = modifier) {
        when (qrType) {
            QrType.Location -> {
                val contactModel = viewModel.locationModel.collectAsState().value
                contentIfTrue(predicate = { contactModel.label.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.label),
                        content = contactModel.label,
                        accentColor = typeInfo.accentColor,
                        isTypeHeader = true,
                        icon = typeInfo.icon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                contentIfTrue(predicate = { contactModel.longitude.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.longtitude),
                        content = contactModel.longitude,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.latitude.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.latitude),
                        content = contactModel.latitude,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.query.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.query),
                        content = contactModel.query,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Message -> {
                val contactModel = viewModel.messageModel.collectAsState().value
                contentIfTrue(predicate = { contactModel.phone.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.phone_number),
                        content = contactModel.phone,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                contentIfTrue(predicate = { contactModel.subject.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.subject),
                        content = contactModel.subject,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.message.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.message),
                        content = contactModel.message,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Email -> {
                val contactModel = viewModel.emailModel.collectAsState().value
                contentIfTrue(predicate = { contactModel.email.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.email),
                        content = contactModel.email,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                ResultInfoRow(
                    stringResource(R.string.subject),
                    content = contactModel.subject,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                ResultInfoRow(
                    stringResource(R.string.body),
                    content = contactModel.body,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                contentIfTrue(predicate = { contactModel.cc.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.cc),
                        content = contactModel.cc.joinToString(", "),
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Contact -> {
                val contactModel = viewModel.contactModel.collectAsState().value
                contentIfTrue(predicate = { contactModel.name.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.name),
                        content = contactModel.name,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.phone.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.phone_number),
                        content = contactModel.phone,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.email.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.email),
                        content = contactModel.email,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.jobTitle.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.job_title),
                        content = contactModel.jobTitle,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.address.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.address),
                        content = contactModel.address,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.note.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.note),
                        content = contactModel.note,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Event -> {
                val contactModel = viewModel.eventModel.collectAsState().value
                contentIfTrue(predicate = { contactModel.eventName.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.event_name),
                        content = contactModel.eventName,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                contentIfTrue(predicate = { contactModel.startTime.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.start_date),
                        content = contactModel.startTime,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.endTime.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.end_date),
                        content = contactModel.endTime,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.location.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.location),
                        content = contactModel.location,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.description.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.description),
                        content = contactModel.description,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Wifi -> {
                val wifiModel = viewModel.wifiModel.collectAsState().value
                ResultInfoRow(
                    stringResource(R.string.wifi_name),
                    content = wifiModel.name,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier.fillMaxWidth(),
                )
                ResultInfoRow(
                    stringResource(R.string.password),
                    content = wifiModel.pass,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                ResultInfoRow(
                    stringResource(R.string.security),
                    content = when {
                        wifiModel.security.isBlank() -> stringResource(R.string.select_security)
                        wifiModel.security == "NOPASS" -> stringResource(R.string.no_password)
                        else -> wifiModel.security
                    },
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
            }

            QrType.Website -> {
                val content = viewModel.createQrUrl.collectAsState().value
                ResultInfoRow(
                    stringResource(R.string.url),
                    content = content,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            QrType.Phone -> {
                val content = viewModel.createQrPhone.collectAsState().value
                ResultInfoRow(
                    stringResource(R.string.phone_number),
                    content = content,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            QrType.Barcode -> {
                val barcodeValue = viewModel.barcodeValue.collectAsState().value
                ResultInfoRow(
                    stringResource(R.string.type),
                    content = stringResource(R.string.barcode) + " ${
                        barcodeValue.second.replace(
                            "_",
                            "-"
                        )
                    }",
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier.fillMaxWidth(),
                )
                ResultInfoRow(
                    stringResource(R.string.barcode_content),
                    content = barcodeValue.first,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            else -> {
                val content = viewModel.createQrText.collectAsState().value
                ResultInfoRow(
                    stringResource(R.string.text),
                    content = content,
                    accentColor = typeInfo.accentColor,
                    icon = typeInfo.icon,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "CreateResultScreen - Text")
@Composable
private fun CreateResultScreenPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            val vm = remember { CreateViewModel(AppRepository(fakeDataHistoryDao)) }
            CreateResultScreen(
                viewModel = vm,
                qrType = QrType.Text,
                action = object : ResultAction {}
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "CreateResultScreen - Website")
@Composable
private fun CreateResultWebsitePreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            val vm = remember { CreateViewModel(AppRepository(fakeDataHistoryDao)) }
            CreateResultScreen(
                viewModel = vm,
                qrType = QrType.Website,
                action = object : ResultAction {}
            )
        }
    }
}
