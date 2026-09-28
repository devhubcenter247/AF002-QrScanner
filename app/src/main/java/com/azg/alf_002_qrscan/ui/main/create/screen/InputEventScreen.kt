package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
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
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.DateTimeOption
import com.azg.alf_002_qrscan.ui.component.DateTimePickerDialog
import com.azg.alf_002_qrscan.ui.component.DateTimePickerMode
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.azg.alf_002_qrscan.model.EventModel
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.main.create.CreateFormHeader
import com.azg.alf_002_qrscan.ui.main.create.FormField
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputWifi", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewCalendar() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrEventContent(
                eventValue = EventModel(),
                onEventValueChange = {},
                theme = ColorTheme.BLUE
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrEvent(
    viewModel: CreateViewModel = viewModel(),
) {

    val nativeAdHolder = rememberNativeAdPreload(
        enabled = NativePlacement.NativeMediumAll.canShowAds,
        tag = NativePlacement.NativeMediumAll.tag,
        fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        nativeAdHolder.request()
    }
    val eventValue by viewModel.eventModel.collectAsStateWithLifecycle()
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)

    InputQrEventContent(
        eventValue = eventValue,
        onEventValueChange = { viewModel.setEventModel(it) },
        theme = theme,
        nativeAdHolder = nativeAdHolder
    )
}

private val eventAccentColor = Color(0xFFF97316)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrEventContent(
    eventValue: EventModel,
    onEventValueChange: (EventModel) -> Unit,
    theme: ColorTheme,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    CreateFormHeader(
        icon = R.drawable.ic_create_calendar,
        title = stringResource(R.string.event),
        subtitle = stringResource(R.string.event_subtitle),
        accentColor = eventAccentColor
    )

    FormField(
        labelRes = R.string.event_name,
        value = eventValue.eventName,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_calendar,
        cornerRadius = 14.Sdp,
    ) { text ->
        onEventValueChange(eventValue.copy(eventName = text))
    }
    TextView(
        stringResource(R.string.start_date),
        modifier = Modifier
            .paddingTop(12.Sdp)
            .fillMaxWidth(),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )

    Row(
        modifier = Modifier
            .paddingTop(6.Sdp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .neonState(
                    true,
                    outerRadius = 5.Sdp,
                    colors = listOf(theme.buttonColor.opacity(10f), theme.buttonColor.opacity(10f)),
                    shape = RoundedCornerShape(14.Sdp)
                )
                .background(white, RoundedCornerShape(14.Sdp))
                .border(1.Sdp, theme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
                .height(46.Sdp)
                .clickable { showStartDatePicker = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_form_calendar,
                tint = theme.buttonColor,
                icSize = 16,
                modifier = Modifier.paddingHorizontal(8.Sdp)
            )
            Text(
                text = eventValue.startTime,
                textAlign = TextAlign.Justify,
                color = if (eventValue.startTime.isEmpty()) lightGrey else black,
                fontSize = if (eventValue.startTime.isEmpty()) 10.Ssp else 12.Ssp,
                modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
            )
        }
        // Date picker button
        IconButton(
            onClick = { showStartDatePicker = true },
            modifier = Modifier
                .padding(start = 8.Sdp)
                .size(44.Sdp)
                .background(
                    color = theme.buttonColor,
                    shape = RoundedCornerShape(14.Sdp)
                ),
            enabled = true
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar_clock),
                contentDescription = stringResource(R.string.select_date),
                tint = theme.colorText,
                modifier = Modifier.size(18.Sdp)
            )
        }
    }
    // End Date Section
    TextView(
        stringResource(R.string.end_date),
        modifier = Modifier
            .paddingTop(12.Sdp)
            .fillMaxWidth(),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )

    Row(
        modifier = Modifier
            .paddingTop(6.Sdp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date display field
        Row(
            modifier = Modifier
                .weight(1f)
                .neonState(
                    true,
                    outerRadius = 5.Sdp,
                    colors = listOf(theme.buttonColor.opacity(10f), theme.buttonColor.opacity(10f)),
                    shape = RoundedCornerShape(14.Sdp)
                )
                .background(white, RoundedCornerShape(14.Sdp))
                .border(1.Sdp, theme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
                .height(46.Sdp)
                .clickable { showEndDatePicker = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_form_calendar,
                tint = theme.buttonColor,
                icSize = 16,
                modifier = Modifier.paddingHorizontal(8.Sdp)
            )
            Text(
                text = eventValue.endTime,
                textAlign = TextAlign.Justify,
                color = if (eventValue.endTime.isEmpty()) lightGrey else black,
                fontSize = if (eventValue.endTime.isEmpty()) 10.Ssp else 12.Ssp,
                modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
            )
        }
        // Date picker button
        IconButton(
            onClick = { showEndDatePicker = true },
            modifier = Modifier
                .padding(start = 8.Sdp)
                .size(44.Sdp)
                .background(
                    color = theme.buttonColor,
                    shape = RoundedCornerShape(14.Sdp)
                ),
            enabled = true
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar_clock),
                contentDescription = stringResource(R.string.select_date),
                tint = theme.colorText,
                modifier = Modifier.size(18.Sdp)
            )
        }
    }

    FormField(
        labelRes = R.string.location,
        value = eventValue.location,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_location,
        cornerRadius = 14.Sdp,
    ) { text ->
        onEventValueChange(eventValue.copy(location = text))
    }
    FormField(
        labelRes = R.string.description,
        value = eventValue.description,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_note,
        cornerRadius = 14.Sdp,
    ) { text ->
        onEventValueChange(eventValue.copy(description = text))
    }

    if (showStartDatePicker) {
        DateTimePickerDialog(
            onDismiss = { showStartDatePicker = false },
            onDateTimeSelected = { dt ->
                Log.d("DateTimeSelect", "TimeStart:$dt")
                onEventValueChange(
                    eventValue.copy(
                        startTime = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    )
                )
                showStartDatePicker = false
            },
            options = DateTimeOption(
                mode = DateTimePickerMode.DateTime,
                title = stringResource(R.string.select_start_date),
                dateLabel = stringResource(R.string.select_date),
                timeLabel = stringResource(R.string.select_time),
                cancelText = stringResource(R.string.cancel),
                okText = stringResource(R.string.ok),
                minuteLabel = stringResource(R.string.minute),
                hourLabel = stringResource(R.string.hour),
                monthLabel = stringResource(R.string.month_),
                yearLabel = stringResource(R.string.year),
                is24Hour = true,
                minuteInterval = 5
            )
        )
    }

    if (showEndDatePicker) {
        val localDateTime = eventValue.startTime.takeIf { it.isNotEmpty() }?.let {
            runCatching {
                LocalDateTime.parse(
                    it,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                )
            }.getOrNull()
        } ?: LocalDateTime.now()

        DateTimePickerDialog(
            onDismiss = { showEndDatePicker = false },
            onDateTimeSelected = { dt ->
                onEventValueChange(
                    eventValue.copy(
                        endTime = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    )
                )
                showEndDatePicker = false
            },
            options = DateTimeOption(
                minDate = localDateTime.toLocalDate(),
                mode = DateTimePickerMode.DateTime,
                title = stringResource(R.string.select_end_date),
                dateLabel = stringResource(R.string.select_date),
                timeLabel = stringResource(R.string.select_time),
                cancelText = stringResource(R.string.cancel),
                okText = stringResource(R.string.ok),
                minuteLabel = stringResource(R.string.minute),
                hourLabel = stringResource(R.string.hour),
                monthLabel = stringResource(R.string.month_),
                yearLabel = stringResource(R.string.year),
                is24Hour = true,
                minuteInterval = 5
            )
        )
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
