package com.azg.alf_002_qrscan.ui.main.scan

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.contentIfTrue
import com.azg.alf_002_qrscan.style.paddingTop
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
import com.azg.alf_002_qrscan.utils.QRUtils
import com.create.custom.qr.QrType
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerSmallCtaBotView
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.ScanType
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.borderTop
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.sdp.ssp.android.Sdp

interface ScanResultAction {
    fun onBack() {}
    fun onCopy() {}
    fun onSave() {}
    fun onShare() {}
    fun onRename() {}
    fun onFavorite() {}
    fun onDelete() {}
    fun onOpenBrowser(url: String) {}
    fun onCall(phone: String) {}
    fun onSendEmail(email: String) {}
    fun onSendMessage(phone: String) {}
    fun onAddContact() {}
    fun onConnectWifi() {}
    fun onOpenMap(latitude: String, longitude: String) {}
    fun onAddEvent() {}
    fun onSearch() {}
}

@Composable
private fun rememberResultCta(historyApp: HistoryApp, action: ScanResultAction): ResultQuickAction =
    remember(historyApp.type, historyApp.content) {
        when (historyApp.type) {
            QrType.Website -> ResultQuickAction(R.string.open_link, R.drawable.ic_app_search) {
                action.onOpenBrowser(historyApp.content)
            }

            QrType.Phone -> ResultQuickAction(R.string.call, R.drawable.ic_app_call) {
                action.onCall(historyApp.content)
            }

            QrType.Email -> ResultQuickAction(R.string.send_email, R.drawable.ic_app_email) {
                action.onSendEmail(historyApp.content)
            }

            QrType.Message -> ResultQuickAction(R.string.send_sms, R.drawable.ic_app_message) {
                action.onSendMessage(historyApp.content)
            }

            QrType.Contact -> ResultQuickAction(R.string.add_contact, R.drawable.ic_app_call) {
                action.onAddContact()
            }

            QrType.Wifi -> ResultQuickAction(R.string.connect, R.drawable.ic_app_wifi) {
                action.onConnectWifi()
            }

            QrType.Location -> ResultQuickAction(R.string.open_map, R.drawable.ic_app_location) {
                action.onOpenMap("", "")
            }

            QrType.Event -> ResultQuickAction(R.string.add_event, R.drawable.ic_calendar_clock) {
                action.onAddEvent()
            }

            else -> ResultQuickAction(R.string.search_on_web, R.drawable.ic_app_search) {
                action.onSearch()
            }
        }
    }

@Composable
fun ScanResultScreen(
    historyApp: HistoryApp,
    qrBitmap: Bitmap? = null,
    nativeAdHolder: NativeAdPreloadHolder? = null,
    action: ScanResultAction
) {
    val appTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp }
    val cta = rememberResultCta(historyApp, action)

    ColumnParentContent(
        onBack = {
            action.onBack()
        }, modifier = Modifier
            .fillMaxSize(1f)
            .background(appTheme.bgColor)
            .statusBarPadding()
    ) {
        ResultTopBar(
            title = historyApp.qrName.ifEmpty { stringResource(R.string.scan_result) },
            onBack = { action.onBack() },
            modifier = Modifier
        ) {
            ResultHeaderActionButton(
                icon = R.drawable.ic_app_edit,
                tint = appTheme.buttonColor,
                theme = appTheme
            ) { action.onRename() }
            ResultHeaderActionButton(
                icon = R.drawable.ic_shape_star,
                tint = if (historyApp.isFavorite) Color(0xFF0066FF) else gray,
                theme = appTheme
            ) { action.onFavorite() }
            ResultHeaderActionButton(
                icon = R.drawable.ic_app_delete,
                tint = appTheme.buttonColor,
                theme = appTheme
            ) { action.onDelete() }
        }

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultQrPreview(
                bitmap = qrBitmap,
                colorTheme = appTheme,
                isBarcode = historyApp.type == QrType.Barcode
            )

            Column(
                Modifier
                    .paddingTop(12.Sdp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                val typeInfo = getQrTypeInfo(historyApp.type)
                when (historyApp.type) {
                    QrType.Text, QrType.Website, QrType.Phone -> {

                        ResultPrimaryValueCard(
                            icon = typeInfo.icon,
                            theme = appTheme,
                            accentColor = typeInfo.accentColor,
                            label = stringResource(typeInfo.title),
                            value = historyApp.content,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight(),
                        )
                    }

                    else -> ItemContentScan(
                        historyApp = historyApp,
                        accentColor = typeInfo.accentColor,
                        icon = typeInfo.icon,
                        theme = appTheme,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .wrapContentHeight()
                    )
                }
            }
        }

        Column(
            Modifier
                .background(white, RoundedCornerShape(topStart = 20.Sdp, topEnd = 20.Sdp))
                .paddingTop(12.Sdp)
                .fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResultQuickActionsRow(
                theme = appTheme,
                actions = listOf(
                    ResultQuickAction(R.string.copy, R.drawable.ic_app_copy) { action.onCopy() },
                    ResultQuickAction(R.string.save, R.drawable.ic_app_save) { action.onSave() },
                    ResultQuickAction(R.string.share, R.drawable.ic_app_share) { action.onShare() },
                )
            )

            ResultPrimaryCtaButton(
                label = cta.label,
                icon = cta.icon,
                theme = appTheme
            ) { cta.onClick() }

            NativeAdCard(
                holder = nativeAdHolder,
                modifier = Modifier
                    .borderTop(1.Sdp, lightGrey)
                    .fillMaxWidth()
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

@Composable
fun ItemContentScan(
    historyApp: HistoryApp,
    theme: ColorTheme,
    modifier: Modifier = Modifier,
    accentColor: Color,
    icon: Int
) {
    ResultContentCard(theme = theme, modifier = modifier) {
        ResultInfoRow(
            accentColor = accentColor,
            icon = icon,
            title = stringResource(R.string.type),
            isTypeHeader = true,
            content = getQrTypeLabel(historyApp.type),
            modifier = Modifier.fillMaxWidth()
        )

        // Show content based on type with detailed parsing
        when (historyApp.type) {
            QrType.Email -> {
                val emailModel = remember(historyApp.content) {
                    QRUtils.parseEmail(historyApp.content)
                }
                contentIfTrue(predicate = { emailModel.email.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.email),
                        content = emailModel.email,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                ResultInfoRow(
                    stringResource(R.string.subject),
                    content = emailModel.subject,
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                ResultInfoRow(
                    stringResource(R.string.body),
                    content = emailModel.body,
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                contentIfTrue(predicate = { emailModel.cc.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.cc),
                        content = emailModel.cc.joinToString(", "),
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Contact -> {
                val contactModel = remember(historyApp.content) {
                    QRUtils.parseContact(historyApp.content)
                }
                contentIfTrue(predicate = { contactModel.name.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.name),
                        content = contactModel.name,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.phone.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.phone_number),
                        content = contactModel.phone,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.email.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.email),
                        content = contactModel.email,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.company.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.company),
                        content = contactModel.company,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.jobTitle.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.job_title),
                        content = contactModel.jobTitle,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.address.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.address),
                        content = contactModel.address,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { contactModel.note.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.note),
                        content = contactModel.note,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Wifi -> {
                val wifiModel = remember(historyApp.content) {
                    QRUtils.parseWifi(historyApp.content)
                }
                ResultInfoRow(
                    stringResource(R.string.wifi_name),
                    content = wifiModel.name,
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                contentIfTrue(predicate = { wifiModel.pass.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.password),
                        content = wifiModel.pass,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                ResultInfoRow(
                    stringResource(R.string.security),
                    content = when {
                        wifiModel.security.isBlank() -> stringResource(R.string.no_password)
                        wifiModel.security == "NOPASS" -> stringResource(R.string.no_password)
                        else -> wifiModel.security
                    },
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
            }

            QrType.Location -> {
                val locationModel = remember(historyApp.content) {
                    QRUtils.parseLocation(historyApp.content)
                }
                contentIfTrue(predicate = { locationModel.label.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.label),
                        content = locationModel.label,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { locationModel.latitude.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.latitude),
                        content = locationModel.latitude,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { locationModel.longitude.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.longtitude),
                        content = locationModel.longitude,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { locationModel.query.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.query),
                        content = locationModel.query,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Event -> {
                val eventModel = remember(historyApp.content) {
                    QRUtils.parseEvent(historyApp.content)
                }
                contentIfTrue(predicate = { eventModel.eventName.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.event_name),
                        content = eventModel.eventName,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { eventModel.startTime.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.start_date),
                        content = eventModel.startTime,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { eventModel.endTime.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.end_date),
                        content = eventModel.endTime,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { eventModel.location.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.location),
                        content = eventModel.location,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
                contentIfTrue(predicate = { eventModel.description.isNotEmpty() }) {
                    ResultInfoRow(
                        stringResource(R.string.description),
                        content = eventModel.description,
                        accentColor = accentColor,
                        icon = icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.Sdp),
                    )
                }
            }

            QrType.Message -> {
                val messageModel = remember(historyApp.content) {
                    QRUtils.parseSms(historyApp.content)
                }
                ResultInfoRow(
                    stringResource(R.string.phone_number),
                    content = messageModel.phone,
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
                ResultInfoRow(
                    stringResource(R.string.message),
                    content = messageModel.message,
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
            }

            QrType.Phone -> {
                val phoneNumber = remember(historyApp.content) {
                    QRUtils.parsePhone(historyApp.content)
                }
                ResultInfoRow(
                    stringResource(R.string.phone_number),
                    content = phoneNumber, accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
            }

            QrType.Website -> {
                ResultInfoRow(
                    stringResource(R.string.url),
                    content = historyApp.content,   accentColor = accentColor,
                    icon = icon,

                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
            }

            else -> {
                // Default: show raw content
                ResultInfoRow(
                    stringResource(R.string.content),
                    content = historyApp.content,
                    accentColor = accentColor,
                    icon = icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .paddingTop(8.Sdp),
                )
            }
        }

        // Show scan time if available
        if (historyApp.scanTime > 0) {
            ResultInfoRow(
                stringResource(R.string.scan_time),
                content = formatScanTime(historyApp.scanTime),
                accentColor = accentColor,
                icon = icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingTop(8.Sdp),
            )
        }
    }
}

@Composable
private fun getQrTypeLabel(type: QrType): String {
    return when (type) {
        QrType.Website -> stringResource(R.string.url)
        QrType.Wifi -> stringResource(R.string.wifi)
        QrType.Email -> stringResource(R.string.email)
        QrType.Contact -> stringResource(R.string.contact)
        QrType.Text -> stringResource(R.string.text)
        QrType.Phone -> stringResource(R.string.phone)
        QrType.Message -> stringResource(R.string.message)
        QrType.Location -> stringResource(R.string.location)
        QrType.Event -> stringResource(R.string.event)
        QrType.Barcode -> stringResource(R.string.barcode)
        else -> stringResource(R.string.text)
    }
}

private fun formatScanTime(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

@Preview(showBackground = true, name = "ScanResultScreen - Website")
@Composable
private fun ScanResultScreenPreview() {
    AppViewTheme {
        Surface {
            ScanResultScreen(
                historyApp = HistoryApp(
                    type = QrType.Website,
                    content = "https://google.com",
                    qrName = "Google",
                    scanType = ScanType.QRCODE,
                    createType = CreateType.SCAN
                ),
                action = object : ScanResultAction {}
            )
        }
    }
}

@Preview(showBackground = true, name = "ScanResultScreen - Contact")
@Composable
private fun ScanResultContactPreview() {
    AppViewTheme {
        Surface {
            ScanResultScreen(
                historyApp = HistoryApp(
                    type = QrType.Contact,
                    content = "BEGIN:VCARD\nFN:John Doe\nTEL:+1234567890\nEMAIL:john@example.com\nEND:VCARD",
                    qrName = "John Doe",
                    scanType = ScanType.QRCODE,
                    createType = CreateType.SCAN
                ),
                action = object : ScanResultAction {}
            )
        }
    }
}

@Preview(showBackground = true, name = "ScanResultScreen - Wifi")
@Composable
private fun ScanResultWifiPreview() {
    AppViewTheme {
        Surface {
            ScanResultScreen(
                historyApp = HistoryApp(
                    type = QrType.Wifi,
                    content = "WIFI:T:WPA;S:MyNetwork;P:password123;;",
                    qrName = "MyNetwork",
                    scanType = ScanType.QRCODE,
                    createType = CreateType.SCAN
                ),
                action = object : ScanResultAction {}
            )
        }
    }
}
