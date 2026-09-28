package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.model.ContactModel
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.main.create.CreateFormHeader
import com.azg.alf_002_qrscan.ui.main.create.FormField
import com.azg.alf_002_qrscan.ui.main.create.FormMultilineField
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.sdp.ssp.android.Sdp

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputContact", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewContact() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()

                .background(ColorTheme.BLUE.bgColor)
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrContactContent(
                contactValue = ContactModel(),
                onContactValueChange = {}
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputQrContact(
    viewModel: CreateViewModel = viewModel(),
) {
    val contactValue by viewModel.contactModel.collectAsStateWithLifecycle()
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)

    val nativeAdHolder = rememberNativeAdPreload(
        enabled = NativePlacement.NativeMediumAll.canShowAds,
        tag = NativePlacement.NativeMediumAll.tag,
        fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
        autoReloadOnResume = true,
    )
    LaunchedEffect(nativeAdHolder) {
        nativeAdHolder.request()
    }

    InputQrContactContent(
        contactValue = contactValue,
        colorTheme = theme,
        onContactValueChange = { viewModel.setContactModel(it) },
        nativeAdHolder = nativeAdHolder
    )
}

private val contactAccentColor = Color(0xFF3B82F6)
private val contactFieldCornerRadius = 14.Sdp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputQrContactContent(
    contactValue: ContactModel,
    colorTheme: ColorTheme = ColorTheme.BLUE,
    onContactValueChange: (ContactModel) -> Unit,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    CreateFormHeader(
        icon = R.drawable.ic_create_contact,
        title = stringResource(R.string.contact),
        subtitle = stringResource(R.string.contact_subtitle),
        accentColor = contactAccentColor
    )

    FormField(
        labelRes = R.string.name,
        value = contactValue.name,
        keyboardType = KeyboardType.Text,
        colorTheme = colorTheme,
        leadingIcon = R.drawable.ic_form_person,
        cornerRadius = contactFieldCornerRadius,
    ) {
        onContactValueChange(contactValue.copy(name = it))
    }
    FormField(
        labelRes = R.string.phone_number,
        value = contactValue.phone,
        colorTheme = colorTheme,
        keyboardType = KeyboardType.Phone,
        leadingIcon = R.drawable.ic_form_phone,
        cornerRadius = contactFieldCornerRadius,
    ) {
        if (it.length < 30) {
            onContactValueChange(
                contactValue.copy(phone = it)
            )
        }
    }
//
//    NativeAdCard(
//        holder = nativeAdHolder,
//        modifier = Modifier
//            .fillMaxWidth()
//            .paddingVertical(10.Sdp)
//            .clip(RoundedCornerShape(10.Sdp))
//            .background(white),
//        loading = { isLoading ->
//            if (LocalInspectionMode.current) {
//                ShimmerMediumCtaTopView(isShowShimmer = true)
//            } else {
//                NativeShimmerByLayout(
//                    NativePlacement.NativeMediumAll,
//                    isLoading,
//                    Modifier.fillMaxWidth()
//                )
//            }
//        },
//        nativeView = { ad ->
//            NativeAdByLayout(NativePlacement.NativeMediumAll, ad)
//        },
//    )

    FormField(
        labelRes = R.string.email,
        value = contactValue.email,
        keyboardType = KeyboardType.Email,
        colorTheme = colorTheme,
        leadingIcon = R.drawable.ic_form_email,
        cornerRadius = contactFieldCornerRadius,
    ) {
        onContactValueChange(
            contactValue.copy(email = it)
        )
    }

    FormField(
        labelRes = R.string.company,
        value = contactValue.company,
        colorTheme = colorTheme,
        keyboardType = KeyboardType.Text,
        leadingIcon = R.drawable.ic_form_company,
        cornerRadius = contactFieldCornerRadius,
    ) {
        onContactValueChange(
            contactValue.copy(company = it)
        )
    }

    FormField(
        labelRes = R.string.job_title,
        value = contactValue.jobTitle,
        keyboardType = KeyboardType.Text,
        colorTheme = colorTheme,
        leadingIcon = R.drawable.ic_form_job,
        cornerRadius = contactFieldCornerRadius,
    ) {
        if (it.length < 300) {
            onContactValueChange(
                contactValue.copy(jobTitle = it)
            )
        }
    }

    FormField(
        labelRes = R.string.address,
        value = contactValue.address,
        keyboardType = KeyboardType.Text,
        colorTheme = colorTheme,
        leadingIcon = R.drawable.ic_form_location,
        cornerRadius = contactFieldCornerRadius,
    ) {
        if (it.length < 300) {
            onContactValueChange(
                contactValue.copy(address = it)
            )
        }
    }

    FormMultilineField(
        labelRes = R.string.note,
        value = contactValue.note,
        leadingIcon = R.drawable.ic_form_note,
        cornerRadius = contactFieldCornerRadius,
        height = 120.Sdp,
    ) {
        if (it.length < 300) {
            onContactValueChange(
                contactValue.copy(note = it)
            )
        }
    }
}
