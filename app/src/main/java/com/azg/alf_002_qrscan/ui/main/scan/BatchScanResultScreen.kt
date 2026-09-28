package com.azg.alf_002_qrscan.ui.main.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerMediaLeftComposeView
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.ResultTopBar
import com.azg.alf_002_qrscan.ui.component.convertTimeToDate
import com.azg.alf_002_qrscan.ui.component.getQrTypeInfo
import com.create.custom.qr.QrType
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

@Composable
fun BatchScanResultScreen(
    nativeAdHolder: NativeAdPreloadHolder? = null,
    results: List<HistoryApp>,
    onBack: () -> Unit,
    onItemClick: (HistoryApp) -> Unit,
) {
    val appTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) {
        AppPreferences.themeApp
    }

    ColumnParentContent(
        onBack = onBack,
        modifier = Modifier
            .fillMaxSize()
            .background(appTheme.bgColor)
            .statusBarPadding()
    ) {
        ResultTopBar(
            title = stringResource(R.string.batch_scan_result),
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .paddingHorizontal(12.Sdp),
            verticalArrangement = Arrangement.spacedBy(10.Sdp)
        ) {
            item(key = "batch_summary") {
                BatchSummaryCard(count = results.size, theme = appTheme)
            }

            if (results.isEmpty()) {
                item(key = "batch_empty") {
                    BatchEmptyState(theme = appTheme)
                }
            } else {
                items(
                    items = results,
                    key = { item -> item.hisId }
                ) { item ->
                    BatchResultItem(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }

            item(key = "batch_bottom_space") { SpaceH(24.Sdp) }
        }
        NativeAdCard(
            holder = nativeAdHolder,
            modifier = Modifier
                .fillMaxWidth(),
            loading = { isLoading ->
                if (LocalInspectionMode.current) {
                    ShimmerMediaLeftComposeView(isShowShimmer = true)
                } else {
                    NativeShimmerByLayout(
                        NativePlacement.NativeMediumAll,
                        isLoading,
                        Modifier.fillMaxWidth()
                    )
                }
            },
            nativeView = { ad ->
                if (!LocalInspectionMode.current) {
                    NativeAdByLayout(NativePlacement.NativeMediumAll, ad)
                }
            },
        )
    }
}

@Composable
private fun BatchSummaryCard(count: Int, theme: ColorTheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(theme.buttonColor.opacity(0.12f), RoundedCornerShape(14.Sdp))
            .padding(horizontal = 14.Sdp, vertical = 12.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.Sdp)
                .background(theme.buttonColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconView(item = R.drawable.ic_batch_scan, tint = white, icSize = 18)
        }
        SpaceW(10.Sdp)
        TextView(
            text = stringResource(R.string.batch_scan_summary, count),
            color = black,
            textSize = 14.Ssp,
            font = R.font.rb_600
        )
    }
}

@Composable
private fun BatchResultItem(item: HistoryApp, onClick: () -> Unit) {
    val typeInfo = remember(item.type) { getQrTypeInfo(item.type) }
    val formattedDate = remember(item.scanTime) {
        item.scanTime.toString().convertTimeToDate()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(14.Sdp))
            .onClick(onClick = onClick)
            .padding(12.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.Sdp)
                .background(typeInfo.accentColor.opacity(0.12f), RoundedCornerShape(12.Sdp)),
            contentAlignment = Alignment.Center
        ) {
            IconView(item = typeInfo.icon, tint = typeInfo.accentColor, icSize = 22)
        }
        SpaceW(12.Sdp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.Sdp)
        ) {
            TextView(
                text = item.qrName.ifEmpty { stringResource(typeInfo.title) },
                color = black,
                textSize = 13.Ssp,
                font = R.font.rb_600
            )
            androidx.compose.material3.Text(
                text = item.content,
                color = black.opacity(0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 11.Ssp
            )
            TextView(
                text = formattedDate,
                color = black.opacity(0.5f),
                textSize = 10.Ssp,
                font = R.font.rb_400
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = black.opacity(0.35f),
            modifier = Modifier.size(22.Sdp)
        )
    }
}

@Composable
private fun BatchEmptyState(theme: ColorTheme) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.Sdp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.Sdp)
    ) {
        IconView(item = R.drawable.ic_batch_scan, tint = theme.buttonColor, icSize = 48)
        TextView(
            text = stringResource(R.string.no_scanned_codes),
            color = black.opacity(0.6f),
            textSize = 12.Ssp,
            font = R.font.rb_500
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BatchScanResultPreview() {
    AppViewTheme {
        BatchScanResultScreen(
            results = listOf(
                HistoryApp(
                    hisId = 1,
                    content = "https://example.com",
                    type = QrType.Website,
                    createType = CreateType.SCAN
                ),
                HistoryApp(
                    hisId = 2,
                    content = "Sample text",
                    type = QrType.Text,
                    createType = CreateType.SCAN
                )
            ),
            onBack = {},
            onItemClick = {},
        )
    }
}
