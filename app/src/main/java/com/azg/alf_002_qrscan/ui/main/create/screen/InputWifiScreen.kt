package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeAdByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.NativeShimmerByLayout
import com.azg.alf_002_qrscan.ads.wrapper.native.view.ShimmerMediumCtaTopView
import com.azg.alf_002_qrscan.model.LocationModel
import com.azg.alf_002_qrscan.model.WifiModel
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.gray
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
@Preview(name = "PreviewInputLocation", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewLocation() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrLocationContent(
                locationValue = LocationModel(),
                onLocationValueChange = {},
                theme = ColorTheme.BLUE
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputWifi", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewWifi() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrWifiContent(
                wifiValue = WifiModel(),
                onWifiValueChange = {},
                theme = ColorTheme.BLUE
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrWifi(
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
    val wifiValue by viewModel.wifiModel.collectAsStateWithLifecycle()
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)

    InputQrWifiContent(
        wifiValue = wifiValue,
        onWifiValueChange = { viewModel.setWifiModel(it) },
        theme = theme,
        nativeAdHolder = nativeAdHolder
    )
}

private val wifiAccentColor = Color(0xFFF59E0B)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrWifiContent(
    wifiValue: WifiModel,
    onWifiValueChange: (WifiModel) -> Unit,
    theme: ColorTheme,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    val securityOptions = listOf(
        "WPA/WPA2",
        "WPA3",
        "WEP",
        "No_password"
    )
    var expanded by remember { mutableStateOf(false) }

    CreateFormHeader(
        icon = R.drawable.ic_create_wifi,
        title = stringResource(R.string.wifi),
        subtitle = stringResource(R.string.wifi_subtitle),
        accentColor = wifiAccentColor
    )
    // WIFI NAME
    FormField(
        labelRes = R.string.wifi_name,
        value = wifiValue.name,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_wifi,
        cornerRadius = 14.Sdp,
    ) { text ->
        onWifiValueChange(wifiValue.copy(name = text))
    }
    FormField(
        labelRes = R.string.password,
        value = wifiValue.pass,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_lock,
        cornerRadius = 14.Sdp,
    ) { text ->
        onWifiValueChange(wifiValue.copy(pass = text))
    }
    TextView(
        text = stringResource(R.string.security).trim(),
        modifier = Modifier
            .fillMaxWidth()
            .paddingTop(12.Sdp),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )

    Box(
        modifier = Modifier
            .paddingTop(6.Sdp)
            .clip(RoundedCornerShape(14.Sdp))
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(theme.buttonColor.opacity(10f), theme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(14.Sdp)
            )
            .background(white, RoundedCornerShape(14.Sdp))
            .border(1.Sdp, theme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
            .fillMaxWidth(), contentAlignment = Alignment.CenterStart
    ) {
        val currentSecurity = when {
            wifiValue.security.isBlank() -> stringResource(R.string.select_security)
            wifiValue.security == "NOPASS" -> stringResource(R.string.no_password)
            else -> wifiValue.security
        }

        Row(
            modifier = Modifier
                .paddingHorizontal(8.Sdp)
                .paddingVertical(14.Sdp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_setting_privacy,
                tint = theme.buttonColor,
                icSize = 18,
                modifier = Modifier.paddingHorizontal(8.Sdp)
            )
            TextView(
                text = currentSecurity,
                textSize = 13.Ssp,
                font = R.font.rb_400,
                modifier = Modifier.weight(1f)
            )
            IconView(
                item = R.drawable.ic_arrow_down,
                tint = gray,
                icSize = 18,
                modifier = Modifier.paddingHorizontal(8.Sdp)
            )
        }

        DropdownMenu(
            properties = PopupProperties(dismissOnClickOutside = true),
            expanded = expanded,
            modifier = Modifier.fillMaxWidth(0.92f),
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(10.Sdp),
            containerColor = white,
        ) {
            Column(Modifier.padding(horizontal = 12.Sdp)) {
                securityOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            val securityValue = if (option == "No_password") "NOPASS" else option
                            onWifiValueChange(
                                wifiValue.copy(
                                    security = securityValue,
                                    pass = if (securityValue == "NOPASS") "" else wifiValue.pass
                                )
                            )
                            expanded = false
                        },
                        text = {
                            Text(
                                text = if (option == "No_password")
                                    stringResource(R.string.no_password)
                                else option
                            )
                        }
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }) { }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrLocation(
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
    val locationValue by viewModel.locationModel.collectAsStateWithLifecycle()
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)

    InputQrLocationContent(
        locationValue = locationValue,
        onLocationValueChange = { viewModel.setLocationModel(it) },
        theme = theme,
        nativeAdHolder = nativeAdHolder
    )
}

private val locationAccentColor = Color(0xFF14B8A6)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrLocationContent(
    locationValue: LocationModel,
    onLocationValueChange: (LocationModel) -> Unit,
    theme: ColorTheme,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {

    fun String.isValidLongitudeInput(): Boolean {
        if (isEmpty()) return true
        if (!matches(Regex("-?\\d*\\.?\\d*")))
            return false
        val value = toDoubleOrNull() ?: return true
        return value in -180.0..180.0
    }

    fun String.isValidLatitudeInput(): Boolean {
        if (isEmpty()) return true
        if (!matches(Regex("-?\\d*\\.?\\d*")))
            return false
        val value = toDoubleOrNull() ?: return true
        return value in -90.0..90.0
    }

    CreateFormHeader(
        icon = R.drawable.ic_create_location,
        title = stringResource(R.string.location),
        subtitle = stringResource(R.string.location_subtitle),
        accentColor = locationAccentColor
    )

    FormField(
        labelRes = R.string.label,
        value = locationValue.label,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_tag,
        cornerRadius = 14.Sdp,
    ) {
        onLocationValueChange(locationValue.copy(label = it))
    }
    FormField(
        labelRes = R.string.longtitude,
        value = locationValue.longitude,
        colorTheme = theme,
        keyboardType = KeyboardType.Number,
        leadingIcon = R.drawable.ic_form_compass,
        cornerRadius = 14.Sdp,
    ) { newText ->
        if (newText.length <= 30 && newText.isValidLongitudeInput()) {
            onLocationValueChange(
                locationValue.copy(longitude = newText)
            )
        }
    }

    FormField(
        labelRes = R.string.latitude,
        value = locationValue.latitude,
        colorTheme = theme,
        keyboardType = KeyboardType.Number,
        leadingIcon = R.drawable.ic_form_compass,
        cornerRadius = 14.Sdp,
    ) { newText ->
        if (newText.length <= 30 && newText.isValidLatitudeInput()) {
            onLocationValueChange(
                locationValue.copy(latitude = newText)
            )
        }
    }

    FormField(
        labelRes = R.string.query,
        value = locationValue.query,
        colorTheme = theme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_search,
        cornerRadius = 14.Sdp,
    ) {
        if (it.length < 150) {
            onLocationValueChange(
                locationValue.copy(query = it)
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
