package com.azg.alf_002_qrscan.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.banner.BannerPlacement
import com.azg.alf_002_qrscan.ads.wrapper.banner.rememberBannerAdHolder
import com.azg.alf_002_qrscan.ads.wrapper.interstitial.InterstitialAdConfig
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.database.model.ScanType
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.BoxParentContent
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
import com.azg.alf_002_qrscan.style.widthPercent
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.DialogDeleteConfirm
import com.azg.alf_002_qrscan.ui.component.DialogRenameQr
import com.azg.alf_002_qrscan.ui.component.QrTypeInfo
import com.azg.alf_002_qrscan.ui.component.convertTimeToDate
import com.azg.alf_002_qrscan.ui.component.getQrTypeInfo
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.ui.main.MainActivity
import com.azg.alf_002_qrscan.ui.main.scan.ScanResultActivity
import com.azg.alf_002_qrscan.utils.QRUtils
import com.azg.alf_002_qrscan.utils.saveBitmapToStorage
import com.azg.alf_002_qrscan.utils.shareFileBitmap
import com.azg.alf_002_qrscan.viewmodel.HomeViewModel
import com.azg.alf_002_qrscan.viewmodel.fakeDataHistoryDao
import com.azg.alf_002_qrscan.widget.color86869c
import com.create.custom.qr.QrType
import com.google.zxing.BarcodeFormat
import com.lib.ads.application.ads.helper.banner.BannerAdCard
import com.lib.ads.application.ads.helper.banner.DefaultBannerAdLoading
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val BorderColor = Color(0xFFE0E0E0)
private val HintColor = Color(0xFF9E9E9E)
private val SubTextColor = Color(0xFF3A4753)
private val ArrowColor = Color(0xFF757575)
private val FavoriteBlue = Color(0xFF0066FF)

enum class FilterType(@StringRes val title: Int, val qrType: QrType?) {
    ALL(R.string.all_types, null),
    TEXT(R.string.text, QrType.Text),
    WEBSITE(R.string.url, QrType.Website),
    WIFI(R.string.wifi, QrType.Wifi),
    EMAIL(R.string.email, QrType.Email),
    CONTACT(R.string.contact, QrType.Contact),
    PHONE(R.string.phone, QrType.Phone),
    MESSAGE(R.string.sms, QrType.Message),
    LOCATION(R.string.location, QrType.Location),
    EVENT(R.string.event, QrType.Event),
    BARCODE(R.string.barcode, QrType.Barcode)
}

enum class HistoryDateGroup(@StringRes val label: Int) {
    TODAY(R.string.date_group_today),
    YESTERDAY(R.string.date_group_yesterday),
    THIS_WEEK(R.string.date_group_this_week),
    OLDER(R.string.date_group_older)
}

private fun HistoryApp.dateGroup(): HistoryDateGroup {
    val itemDate = java.time.Instant.ofEpochMilli(scanTime)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
    val today = java.time.LocalDate.now()
    val daysAgo = java.time.temporal.ChronoUnit.DAYS.between(itemDate, today)
    return when {
        daysAgo <= 0 -> HistoryDateGroup.TODAY
        daysAgo == 1L -> HistoryDateGroup.YESTERDAY
        daysAgo in 2..6 -> HistoryDateGroup.THIS_WEEK
        else -> HistoryDateGroup.OLDER
    }
}

private fun List<HistoryApp>.groupByDate(): List<Pair<HistoryDateGroup, List<HistoryApp>>> {
    val grouped = groupBy { it.dateGroup() }
    return HistoryDateGroup.entries.mapNotNull { group -> grouped[group]?.let { group to it } }
}

private suspend fun HistoryApp.toHistoryBitmap(): Bitmap? {
    scanImage.takeIf { it.isNotBlank() }?.let { path ->
        val file = File(path)
        if (file.exists()) {
            withContext(Dispatchers.IO) {
                BitmapFactory.decodeFile(path)
            }?.let { return it }
        }
    }

    if (content.isBlank()) return null

    val format = if (scanType == ScanType.BARCODE || type == QrType.Barcode) {
        BarcodeFormat.CODE_128
    } else {
        BarcodeFormat.QR_CODE
    }
    return QRUtils.generateBarcode(content, format)
}

@Composable
private fun DateGroupHeader(group: HistoryDateGroup, modifier: Modifier = Modifier) {
    TextView(
        text = stringResource(group.label),
        font = R.font.rb_500,
        color = color86869c,
        textSize = 13.Ssp,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun HistoryScreen(
    viewModel: HomeViewModel,
    activity: MainActivity,
    currentTheme: ColorTheme = ColorTheme.BLUE,
    onBack: () -> Unit = {}
) {
    val historyList by viewModel.historyList.collectAsState(initial = emptyList())
    HistoryLikeScreen(
        viewModel = viewModel,
        activity = activity,
        currentTheme = currentTheme,
        onBack = onBack,
        titleRes = R.string.history,
        searchHint = stringResource(R.string.search_hint),
        emptyRes = R.string.no_history,
        list = historyList
    )
}

@Composable
fun FavoriteScreen(
    viewModel: HomeViewModel,
    activity: MainActivity,
    currentTheme: ColorTheme = ColorTheme.BLUE,
    onBack: () -> Unit = {}
) {
    val favoriteList by viewModel.listQrFavorite.collectAsState(initial = emptyList())
    HistoryLikeScreen(
        viewModel = viewModel,
        activity = activity,
        currentTheme = currentTheme,
        onBack = onBack,
        titleRes = R.string.favorite,
        searchHint = stringResource(R.string.search_favorites_hint),
        emptyRes = R.string.no_favorites,
        list = favoriteList
    )
}

@Composable
private fun HistoryLikeScreen(
    viewModel: HomeViewModel,
    activity: MainActivity,
    currentTheme: ColorTheme,
    onBack: () -> Unit,
    @StringRes titleRes: Int,
    searchHint: String,
    @StringRes emptyRes: Int,
    list: List<HistoryApp>
) {
    var isShowDialogDelete by remember { mutableStateOf(false) }
    var isShowRenameDialog by remember { mutableStateOf(false) }
    var saveQrHistory by remember { mutableStateOf<HistoryApp?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var isFilterExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val filteredList by remember(list, searchQuery, selectedFilter) {
        derivedStateOf {
            if (searchQuery.isEmpty() && selectedFilter.qrType == null) {
                list
            } else {
                list.filter { item ->
                    val matchesSearch = searchQuery.isEmpty() ||
                            item.qrName.contains(searchQuery, ignoreCase = true) ||
                            item.content.contains(searchQuery, ignoreCase = true)
                    val matchesFilter =
                        selectedFilter.qrType == null || item.type == selectedFilter.qrType
                    matchesSearch && matchesFilter
                }
            }
        }
    }

    DialogDeleteConfirm(
        isShowDialog = isShowDialogDelete,
        onDismiss = { isShowDialogDelete = false },
        onConfirm = {
            saveQrHistory?.let { viewModel.deleteHistory(it.hisId) }
            saveQrHistory = null
        }
    )
    DialogRenameQr(
        isShowDialog = isShowRenameDialog,
        initialName = saveQrHistory?.qrName.orEmpty(),
        theme = currentTheme,
        onDismiss = {
            isShowRenameDialog = false
            saveQrHistory = null
        },
        onConfirm = { name ->
            saveQrHistory?.let { viewModel.updateHistoryName(name, it.hisId) }
        }
    )

    BoxParentContent(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.bgColor)
            .statusBarPadding(),
        onBack = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .paddingHorizontal(12.Sdp)
        ) {
            TextView(
                titleRes,
                font = R.font.rb_600,
                color = black,
                textSize = 16.Ssp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            SpaceH(12)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    hint = searchHint,
                    modifier = Modifier.weight(1f),
                    currentTheme = currentTheme
                )
                FilterDropdown(
                    selectedFilter = selectedFilter,
                    isExpanded = isFilterExpanded,
                    onExpandedChange = { isFilterExpanded = it },
                    onFilterSelected = {
                        selectedFilter = it
                        isFilterExpanded = false
                    },
                    currentTheme = currentTheme
                )
            }

            SpaceH(12)
            if (filteredList.isEmpty()) {
                EmptyState(isListEmpty = list.isEmpty(), emptyRes = emptyRes)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var globalIndex = 0
                    filteredList.groupByDate().forEach { (group, items) ->
                        item(
                            key = "history_group_${group.name}",
                            contentType = "date_header"
                        ) {
                            DateGroupHeader(group)
                        }

                        items.forEach { history ->
                            item(
                                key = "history_${history.hisId}",
                                contentType = "history"
                            ) {
                                HistoryItem(
                                    item = history,
                                    onFavoriteClick = { isFavorite ->
                                        if (isFavorite) {
                                            viewModel.addToFavorite(history)
                                        } else {
                                            viewModel.removeFavorite(history.hisId) {}
                                        }
                                    },
                                    onShare = {
                                        coroutineScope.launch {
                                            val bitmap = history.toHistoryBitmap()
                                            if (bitmap != null) {
                                                activity.shareFileBitmap(
                                                    "qr_history_share_${history.hisId}_${System.currentTimeMillis()}.png",
                                                    bitmap
                                                )
                                            } else {
                                                Toast.makeText(
                                                    activity,
                                                    R.string.qr_generation_failed,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onSave = {
                                        coroutineScope.launch {
                                            val bitmap = history.toHistoryBitmap()
                                            if (bitmap != null) {
                                                val uri = withContext(Dispatchers.IO) {
                                                    saveBitmapToStorage(
                                                        context = activity.applicationContext,
                                                        bitmap = bitmap,
                                                        destFileName = "QR-History_${history.hisId}_${System.currentTimeMillis()}.png",
                                                        environment = Environment.DIRECTORY_PICTURES,
                                                        folderName = "QR Scanner"
                                                    )
                                                }
                                                Toast.makeText(
                                                    activity,
                                                    if (uri != null) R.string.save_file_success else R.string.qr_generation_failed,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    activity,
                                                    R.string.qr_generation_failed,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onDelete = {
                                        isShowDialogDelete = true
                                        saveQrHistory = history
                                    },
                                    onRename = {
                                        saveQrHistory = history
                                        isShowRenameDialog = true
                                    },
                                    onUnFavorite = {
                                        viewModel.removeFavorite(history.hisId) {}
                                    },
                                    onAddFavorite = {
                                        viewModel.addToFavorite(history)
                                    },
                                    onViewItem = {
                                        InterstitialAdConfig.showInterAll(activity) {
                                            activity.startActivity(
                                                Intent(
                                                    activity,
                                                    ScanResultActivity::class.java
                                                ).apply {
                                                    putExtra(
                                                        ScanResultActivity.EXTRA_HISTORY_APP,
                                                        history
                                                    )

                                                    if (history.scanImage.isNotEmpty()) {
                                                        putExtra(
                                                            ScanResultActivity.EXTRA_QR_BITMAP_PATH,
                                                            history.scanImage
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                )
                            }

                            val shouldShowAd = (globalIndex in 3..9 && globalIndex % 3 == 0)
                            if (shouldShowAd) {
                                item(
                                    key = "history_ad_after_${history.hisId}",
                                    contentType = "native_ad"
                                ) {
                                    HistoryNativeAdItem()
                                }
                            }
                            globalIndex++
                        }
                    }

                    item(
                        key = "history_bottom_space",
                        contentType = "spacing"
                    ) {
                        SpaceH(48.Sdp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(isListEmpty: Boolean, @StringRes emptyRes: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_app_empty),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            SpaceH(16)
            TextView(
                text = stringResource(if (isListEmpty) emptyRes else R.string.no_results_found),
                font = R.font.rb_400,
                textSize = 12.Ssp,
                color = SubTextColor
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    currentTheme: ColorTheme = ColorTheme.BLUE
) {
    Box(
        modifier = modifier
            .height(42.Sdp)
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(
                    currentTheme.buttonColor.opacity(10f),
                    currentTheme.buttonColor.opacity(10f)
                ),
                shape = RoundedCornerShape(12.Sdp)
            )
            .background(white, RoundedCornerShape(12.Sdp))
            .border(1.Sdp, currentTheme.buttonColor.opacity(20f), RoundedCornerShape(12.Sdp))
            .padding(horizontal = 12.Sdp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_app_search),
                contentDescription = null,
                tint = HintColor,
                modifier = Modifier.size(20.dp)
            )
            SpaceW(8)
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = hint,
                        color = HintColor,
                        style = TextStyle(fontSize = 12.Ssp)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(color = black, fontSize = 12.Ssp),
                    cursorBrush = SolidColor(currentTheme.buttonColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.ic_app_close),
                    contentDescription = null,
                    tint = HintColor,
                    modifier = Modifier
                        .size(20.dp)
                        .onClick { onQueryChange("") }
                )
            }
        }
    }
}

@Composable
fun FilterDropdown(
    selectedFilter: FilterType,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onFilterSelected: (FilterType) -> Unit,
    currentTheme: ColorTheme = ColorTheme.BLUE
) {
    Box {
        Row(
            modifier = Modifier
                .height(42.Sdp)
                .widthPercent(28f)
                .neonState(
                    true,
                    outerRadius = 5.Sdp,
                    colors = listOf(
                        currentTheme.buttonColor.opacity(10f),
                        currentTheme.buttonColor.opacity(10f)
                    ),
                    shape = RoundedCornerShape(12.Sdp)
                )
                .background(white, RoundedCornerShape(12.Sdp))
                .border(1.Sdp, currentTheme.buttonColor.opacity(20f), RoundedCornerShape(12.Sdp))
                .padding(horizontal = 12.Sdp)
                .onClick { onExpandedChange(!isExpanded) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_app_filter),
                contentDescription = null,
                tint = currentTheme.buttonColor,
                modifier = Modifier.size(20.dp)
            )
            SpaceW(6)
            Text(
                text = stringResource(selectedFilter.title),
                color = black,
                style = TextStyle(fontSize = 12.Ssp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            SpaceW(4)
            Icon(
                painter = painterResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = ArrowColor,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .background(white, RoundedCornerShape(8.Sdp))
                .wrapContentWidth(),
            shape = RoundedCornerShape(8.Sdp)
        ) {
            FilterType.entries.forEach { filter ->
                val isSelected = filter == selectedFilter
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_app_check),
                                    contentDescription = null,
                                    tint = currentTheme.buttonColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                SpaceW(8)
                            }
                            Text(
                                text = stringResource(filter.title),
                                color = if (isSelected) currentTheme.buttonColor else black
                            )
                        }
                    },
                    onClick = { onFilterSelected(filter) }
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    item: HistoryApp,
    onFavoriteClick: (Boolean) -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    onUnFavorite: () -> Unit,
    onAddFavorite: () -> Unit,
    onViewItem: () -> Unit,
) {
    val typeInfo = remember(item.type) {
        getQrTypeInfo(item.type)
    }

    val formattedDate = remember(item.scanTime) {
        item.scanTime.toString().convertTimeToDate()
    }

    var expanded by remember(item.hisId) {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(14.Sdp))
            .padding(10.Sdp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TypeIconTile(typeInfo)

                    SpaceW(12)

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextView(
                            text = item.qrName.ifEmpty {
                                stringResource(typeInfo.title)
                            },
                            font = R.font.rb_500,
                            textSize = 13.Ssp,
                            color = black,
                            maxLine = 2
                        )

                        SpaceH(6)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TypeBadge(typeInfo)
                            SpaceW(6)
                            TextView(
                                text = "· $formattedDate",
                                font = R.font.rb_400,
                                textSize = 10.Ssp,
                                color = SubTextColor,
                                maxLine = 1
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .onClick {
                            onViewItem()
                        }
                )
            }

            Box(
                modifier = Modifier
                    .size(32.Sdp)
                    .clip(RoundedCornerShape(10.Sdp))
                    .border(
                        1.5.dp,
                        if (item.isFavorite) FavoriteBlue else BorderColor,
                        RoundedCornerShape(10.Sdp)
                    )
                    .onClick { onFavoriteClick(!item.isFavorite) },
                contentAlignment = Alignment.Center
            ) {
                IconView(
                    item = R.drawable.ic_shape_star,
                    tint = if (item.isFavorite) FavoriteBlue else HintColor,
                    icSize = 18,
                    contentDescription = stringResource(
                        if (item.isFavorite) R.string.cd_favorite_remove else R.string.cd_favorite_add
                    )
                )
            }

            SpaceW(8.dp)

            Box {
                IconView(
                    item = R.drawable.ic_more_vert,
                    icSize = 28,
                    modifier = Modifier.size(32.dp),
                    contentDescription = stringResource(R.string.cd_more_options)
                ) {
                    expanded = true
                }

                if (expanded) {
                    HistoryItemMenu(
                        isFavorite = item.isFavorite,
                        onDismiss = { expanded = false },
                        onShare = onShare,
                        onSave = onSave,
                        onFavoriteToggle = if (item.isFavorite) {
                            onUnFavorite
                        } else {
                            onAddFavorite
                        },
                        onRename = onRename,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryNativeAdItem() {
    val bannerAdConfig = rememberBannerAdHolder(
        placement = BannerPlacement.BannerAll
    )
    DisposableEffect(bannerAdConfig) {
        bannerAdConfig.request()
        onDispose {
            bannerAdConfig.cancel()
        }
    }

    BannerAdCard(
        holder = bannerAdConfig,
        modifier = Modifier
            .fillMaxWidth()
            .background(white)
            .clip(RoundedCornerShape(10.Sdp)),
        loading = {
            DefaultBannerAdLoading(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.Sdp)
            )
        }
    )
}

@Composable
fun TypeIconTile(typeInfo: QrTypeInfo, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.Sdp)
            .clip(RoundedCornerShape(12.Sdp))
            .background(typeInfo.accentColor.opacity(0.12f)),
        contentAlignment = Alignment.Center
    ) {
        IconView(item = typeInfo.icon, icSize = 22)
    }
}

@Composable
fun TypeBadge(typeInfo: QrTypeInfo, modifier: Modifier = Modifier) {
    TextView(
        text = stringResource(typeInfo.title),
        modifier = modifier
            .clip(RoundedCornerShape(6.Sdp))
            .background(typeInfo.accentColor.opacity(0.12f))
            .padding(horizontal = 6.Sdp, vertical = 2.Sdp),
        font = R.font.rb_500,
        textSize = 11.Ssp,
        color = typeInfo.accentColor
    )
}

@Composable
private fun HistoryItemMenu(
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .widthPercent(40f)
            .background(white, RoundedCornerShape(8.Sdp)),
        shape = RoundedCornerShape(8.Sdp)
    ) {
        MenuItemRow(R.drawable.ic_app_share, R.string.share) {
            onDismiss()
            onShare()
        }
        MenuItemRow(R.drawable.ic_app_save, R.string.save_to_gallery) {
            onDismiss()
            onSave()
        }
        MenuItemRow(R.drawable.ic_app_edit, R.string.rename_qr) {
            onDismiss()
            onRename()
        }
        MenuItemRow(
            icon = if (isFavorite) R.drawable.ic_app_favorite_minus else R.drawable.ic_app_favorite_add,
            text = if (isFavorite) R.string.remove_from_favorite else R.string.add_to_favorite
        ) {
            onDismiss()
            onFavoriteToggle()
        }
        MenuItemRow(R.drawable.ic_app_delete, R.string.delete, tint = Color.Red) {
            onDismiss()
            onDelete()
        }
    }
}

@Composable
private fun MenuItemRow(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    tint: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
                SpaceW(8)
                Text(
                    stringResource(text),
                    color = tint.takeIf { it != Color.Unspecified } ?: Color.Unspecified)
            }
        },
        onClick = onClick
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "FavoriteScreen")
@Composable
private fun FavoriteScreenPreview() {
    AppViewTheme {
        Surface(color = Color.White) {
            val vm = remember { HomeViewModel(AppRepository(fakeDataHistoryDao)) }
            FavoriteScreen(
                viewModel = vm,
                activity = MainActivity(),
                currentTheme = ColorTheme.BLUE
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "HistoryScreen")
@Composable
private fun HistoryScreenPreview() {
    AppViewTheme {
        Surface(color = Color.White) {
            val vm = remember { HomeViewModel(AppRepository(fakeDataHistoryDao)) }
            HistoryScreen(
                viewModel = vm,
                activity = MainActivity(),

                currentTheme = ColorTheme.BLUE
            )
        }
    }
}
