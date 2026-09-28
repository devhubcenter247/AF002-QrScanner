package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
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
import com.azg.alf_002_qrscan.model.EmailModel
import com.azg.alf_002_qrscan.model.MessageModel
import com.azg.alf_002_qrscan.model.isValidEmail
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
import com.azg.alf_002_qrscan.ui.main.create.FormMultilineField
import com.azg.alf_002_qrscan.viewmodel.CreateViewModel
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdCard
import com.lib.ads.application.ads.helper.adnative.provider.NativeAdPreloadHolder
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputEmail", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewEmail() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrEmailContent(
                emailValue = EmailModel(),
                onEmailValueChange = {},
                theme = ColorTheme.BLUE
            )
        }
    }
}
@SuppressLint("ViewModelConstructorInComposable")
@Preview(name = "PreviewInputMessage", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun InputDataPreviewMess() {
    AppViewTheme {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 12.Sdp)
        ) {
            InputQrMessageContent(
                messageModel = MessageModel(),
                onMessageValueChange = {},
                theme = ColorTheme.BLUE
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrMessage(
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
    val messageModel by viewModel.messageModel.collectAsStateWithLifecycle()
    val theme by viewModel.colorTheme.collectAsStateWithLifecycle(ColorTheme.BLUE)

    InputQrMessageContent(
        messageModel = messageModel,
        onMessageValueChange = { viewModel.setMessageModel(it) },
        theme = theme,
        nativeAdHolder = nativeAdHolder
    )
}

private val messageAccentColor = Color(0xFF8B5CF6)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrMessageContent(
    messageModel: MessageModel,
    onMessageValueChange: (MessageModel) -> Unit,
    theme: ColorTheme,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {

    CreateFormHeader(
        icon = R.drawable.ic_create_message,
        title = stringResource(R.string.message),
        subtitle = stringResource(R.string.message_subtitle),
        accentColor = messageAccentColor
    )

    FormField(
        labelRes = R.string.phone_number,
        value = messageModel.phone,
        colorTheme = theme,
        keyboardType = KeyboardType.Phone,
        leadingIcon = R.drawable.ic_form_phone,
        cornerRadius = 14.Sdp,
    ) {
        if (it.length < 30) {
            onMessageValueChange(
                messageModel.copy(
                    phone = it
                )
            )
        }
    }
    FormMultilineField(
        labelRes = R.string.subject,
        value = messageModel.subject,
        leadingIcon = R.drawable.ic_form_note,
        cornerRadius = 14.Sdp,
        height = 60.Sdp,
    ) {
        if (it.length < 200) {
            onMessageValueChange(
                messageModel.copy(
                    subject = it
                )
            )
        }
    }
    FormMultilineField(
        labelRes = R.string.body,
        value = messageModel.message,
        leadingIcon = R.drawable.ic_form_message,
        cornerRadius = 14.Sdp,
        height = 120.Sdp,
    ) {
        onMessageValueChange(
            messageModel.copy(
                message = it
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.InputQrEmail(
    viewModel: CreateViewModel = koinViewModel(),
) {
    val emailValue by viewModel.emailModel.collectAsStateWithLifecycle()
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
    InputQrEmailContent(
        emailValue = emailValue,
        onEmailValueChange = { viewModel.setEmailModel(it) },
        theme = theme,
        nativeAdHolder = nativeAdHolder
    )
}

private val emailAccentColor = Color(0xFF3B82F6)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InputQrEmailContent(
    emailValue: EmailModel,
    onEmailValueChange: (EmailModel) -> Unit,
    theme: ColorTheme,
    nativeAdHolder: NativeAdPreloadHolder? = null,
) {
    var currentCc by remember { mutableStateOf("") }
    CreateFormHeader(
        icon = R.drawable.ic_create_email,
        title = stringResource(R.string.email),
        subtitle = stringResource(R.string.email_subtitle),
        accentColor = emailAccentColor
    )

    FormField(
        labelRes = R.string.email,
        value = emailValue.email,
        colorTheme = theme,
        keyboardType = KeyboardType.Email,
        leadingIcon = R.drawable.ic_form_email,
        cornerRadius = 14.Sdp,
    ) {
        if (it.length < 100) {
            onEmailValueChange(
                emailValue.copy(
                    email = it
                )
            )
        }
    }
    FormMultilineField(
        labelRes = R.string.subject,
        value = emailValue.subject,
        leadingIcon = R.drawable.ic_form_note,
        cornerRadius = 14.Sdp,
        height = 60.Sdp,
    ) {
        if (it.length < 200) {
            onEmailValueChange(
                emailValue.copy(
                    subject = it
                )
            )
        }
    }
    FormMultilineField(
        labelRes = R.string.body,
        value = emailValue.body,
        leadingIcon = R.drawable.ic_form_message,
        cornerRadius = 14.Sdp,
        height = 120.Sdp,
    ) {
        onEmailValueChange(
            emailValue.copy(
                body = it
            )
        )
    }
    if(!LocalInspectionMode.current){
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
    TextView(
        stringResource(R.string.cc), modifier = Modifier
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
    )
    {
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
                .paddingHorizontal(4.Sdp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_form_email,
                tint = theme.buttonColor,
                icSize = 18,
                modifier = Modifier.paddingHorizontal(8.Sdp).paddingVertical(14.Sdp)
            )
            InputValue(
                currentCc,
                hint = stringResource(R.string.add_cc_email),
                hintColor = lightGrey,
                textAlignment = TextAlign.Justify,
                keyboardType = KeyboardType.Email,
                boxModifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
            ) {
                currentCc = it
            }
        }
        IconButton(
            onClick = {
                if (currentCc.isValidEmail()) {
                    val updatedCc = emailValue.cc + currentCc
                    onEmailValueChange(
                        emailValue.copy(
                            cc = updatedCc
                        )
                    )
                    currentCc = ""
                }
            },
            modifier = Modifier
                .padding(start = 8.Sdp)
                .size(42.Sdp)
                .background(
                    color = if (currentCc.isValidEmail()) theme.buttonColor else lightGrey,
                    shape = RoundedCornerShape(14.Sdp)
                ),
            enabled = currentCc.isValidEmail()
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.add_cc),
                tint = Color.White,
                modifier = Modifier.size(20.Sdp)
            )
        }
    }
    Box(
        Modifier
            .paddingTop(8.Sdp)
            .fillMaxWidth()
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(theme.buttonColor.opacity(10f), theme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(14.Sdp)
            )
            .background(white, RoundedCornerShape(14.Sdp))
            .border(1.Sdp, theme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
    )
    {
        if (emailValue.cc.isEmpty()) {
            TextView(
                text = stringResource(R.string.no_cc_added),
                modifier = Modifier
                    .padding(8.Sdp)
                    .paddingVertical(8.Sdp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = lightGrey,
                textSize = 13.Ssp,
                font = R.font.rb_400
            )
        } else {
            FlowRow(
                modifier = Modifier
                    .paddingTop(8.Sdp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .paddingHorizontal(10.Sdp),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Top
            ) {
                emailValue.cc.forEach { ccEmail ->
                    ChipItem(
                        email = ccEmail,
                        theme = theme,
                        onRemove = {
                            val updatedCc = emailValue.cc.filter { it != ccEmail }
                            onEmailValueChange(
                                emailValue.copy(
                                    cc = updatedCc
                                )
                            )
                        }
                    )
                }
            }
        }
    }
    // CC Count
    Text(
        text = "${emailValue.cc.size} ${stringResource(R.string.cc_recipients)}",
        modifier = Modifier
            .padding(top = 4.Sdp)
            .fillMaxWidth(),
        textAlign = TextAlign.End,
        color = lightGrey,
        fontSize = 12.Ssp
    )
}
@Composable
fun ChipItem(
    email: String,
    theme: ColorTheme,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(end = 8.Sdp, bottom = 8.Sdp)
            .wrapContentSize()
            .background(
                color = theme.buttonColor.opacity(10f), shape = RoundedCornerShape(16.Sdp)
            )
            .border(1.Sdp, theme.buttonColor.opacity(20f), RoundedCornerShape(16.Sdp))
    ) {
        Row(
            modifier = Modifier.padding(start = 12.Sdp, end = 8.Sdp, top = 4.Sdp, bottom = 4.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = email,
                color = Color.Black,
                fontSize = 14.Ssp,
                maxLines = 1,
                modifier = Modifier.padding(end = 8.Sdp)
            )
            Image(
                imageVector = Icons.Default.Close,
                modifier = Modifier
                    .size(18.Sdp)
                    .background(theme.buttonColor.opacity(20f), CircleShape)
                    .padding(2.Sdp)
                    .onClick {
                        onRemove.invoke()
                    },
                contentDescription = null
            )
        }
    }
}