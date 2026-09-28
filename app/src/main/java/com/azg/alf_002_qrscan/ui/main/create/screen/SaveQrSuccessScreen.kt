package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.banner.BannerPlacement
import com.azg.alf_002_qrscan.ads.wrapper.banner.rememberBannerAdHolder
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingStart
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.style.widthPercent
import com.azg.alf_002_qrscan.style.widthPercentDp
import com.azg.alf_002_qrscan.ui.component.ButtonAction
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.viewmodel.EditQrViewModel
import com.azg.alf_002_qrscan.viewmodel.fakeDataHistoryDao
import com.azg.alf_002_qrscan.widget.color86869c
import com.lib.ads.application.ads.helper.banner.BannerAdCard
import com.lib.ads.application.ads.helper.banner.DefaultBannerAdLoading
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import org.koin.compose.viewmodel.koinViewModel

private val successGreen = Color(0xFF22C55E)

interface SaveQrAction {
    fun onBack() {}
    fun onGoToHome() {}
    fun onContinueEdit() {}
    fun onCopy() {}
    fun onShare(bitmap: Bitmap?) {}
    fun onSave(bitmap: Bitmap?) {}
}

@Composable
fun SaveQrSuccessScreen(
    viewModel: EditQrViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    action: SaveQrAction
) {
    val appTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp }
    val qrVectorBitmap by viewModel.qrValueBitmap.collectAsStateWithLifecycle()
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
    }, modifier = modifier
        .fillMaxSize()
        .background(appTheme.bgColor)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .paddingHorizontal(12.Sdp)
                .paddingBottom(8.Sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier
                    .size(28.Sdp)
                    .clip(RoundedCornerShape(12.Sdp))
                    .onClick { action.onBack() },
                contentAlignment = Alignment.Center
            ) {
                IconView(item = R.drawable.ic_app_back, tint = black, icSize = 20)
            }

            TextView(
                text = stringResource(R.string.save_qr),
                font = R.font.rb_600,
                color = black,
                textSize = 16.Ssp
            )

            Box(Modifier.size(28.Sdp))
        }

        Column(
            Modifier
                .fillMaxWidth(1f)
                .paddingTop(4.Sdp)
                .paddingVertical(12.Sdp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            Box(
                Modifier.size(80.Sdp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(80.Sdp)
                        .clip(CircleShape)
                        .background(successGreen.opacity(0.12f))
                )
                Box(
                    Modifier
                        .size(56.Sdp)
                        .clip(CircleShape)
                        .background(successGreen),
                    contentAlignment = Alignment.Center
                ) {
                    IconView(item = R.drawable.ic_app_check, tint = white, icSize = 26)
                }
                IconView(
                    item = R.drawable.ic_sparkle,
                    tint = successGreen.opacity(0.6f),
                    icSize = 12,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                IconView(
                    item = R.drawable.ic_sparkle,
                    tint = appTheme.buttonColor.opacity(0.6f),
                    icSize = 10,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
                IconView(
                    item = R.drawable.ic_sparkle,
                    tint = successGreen.opacity(0.6f),
                    icSize = 10,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .size(6.Sdp)
                        .clip(CircleShape)
                        .background(appTheme.buttonColor.opacity(0.6f))
                )
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .size(6.Sdp)
                        .clip(CircleShape)
                        .background(successGreen.opacity(0.6f))
                )
            }

            SpaceH(12)
            TextView(
                text = stringResource(R.string.save_qr_successfully),
                font = R.font.rb_700,
                textSize = 17.Ssp,
                color = black,
                textAlign = TextAlign.Center
            )
            SpaceH(4)
            TextView(
                text = stringResource(R.string.save_qr_ready_message),
                font = R.font.rb_400,
                textSize = 12.Ssp,
                color = color86869c,
                textAlign = TextAlign.Center,
                modifier = Modifier.paddingHorizontal(24.Sdp)
            )

            SpaceH(20)
            qrVectorBitmap?.asImageBitmap()?.let {
                Box(
                    Modifier
                        .background(white, RoundedCornerShape(20.Sdp))
                        .padding(14.Sdp)
                ) {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(widthPercentDp(50f))
                            .border(1.Sdp, lightGrey.opacity(0.5f), RoundedCornerShape(10.Sdp))
                            .padding(6.Sdp)
                            .clip(RoundedCornerShape(6.Sdp))
                    )
                }
            } ?: run {
                Box(
                    modifier
                        .widthPercent(60f)
                        .aspectRatio(5 / 3f)
                        .neonState(
                            true,
                            outerRadius = 5.Sdp,
                            colors = listOf(
                                appTheme.buttonColor.opacity(10f),
                                appTheme.buttonColor.opacity(10f)
                            ),
                            shape = RoundedCornerShape(14.Sdp)
                        )
                        .background(white, RoundedCornerShape(14.Sdp))
                        .border(
                            1.Sdp,
                            appTheme.buttonColor.opacity(20f),
                            RoundedCornerShape(14.Sdp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_qr_code),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                }
            }
        }
        Row(
            Modifier
                .paddingVertical(12.Sdp)
                .fillMaxWidth()
                .paddingHorizontal(24.Sdp)
                .background(white, RoundedCornerShape(20.Sdp))
                .paddingVertical(8.Sdp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            ButtonAction(
                R.string.copy,
                R.drawable.ic_app_copy,
                color = appTheme.buttonColor,
                modifier = Modifier
                    .size(42.Sdp)
                    .background(color = appTheme.buttonColor.opacity(0.12f), CircleShape)
            ) {
                action.onCopy()
            }
            Box(
                Modifier
                    .width(1.Sdp)
                    .height(40.Sdp)
                    .background(lightGrey.opacity(0.5f))
            )
            ButtonAction(
                R.string.save,
                R.drawable.ic_app_save,
                color = appTheme.buttonColor,
                modifier = Modifier
                    .size(42.Sdp)
                    .background(color = appTheme.buttonColor.opacity(0.12f), CircleShape)
            ) {
                action.onSave(bitmap = qrVectorBitmap)
            }
            Box(
                Modifier
                    .width(1.Sdp)
                    .height(40.Sdp)
                    .background(lightGrey.opacity(0.5f))
            )
            ButtonAction(
                R.string.share,
                R.drawable.ic_app_share,
                color = appTheme.buttonColor,
                modifier = Modifier
                    .size(42.Sdp)
                    .background(color = appTheme.buttonColor.opacity(0.12f), CircleShape)
            ) {
                action.onShare(bitmap = qrVectorBitmap)
            }
        }

        Column(
            Modifier

                .background(white, RoundedCornerShape(topStart = 20.Sdp, topEnd = 20.Sdp))
                .paddingVertical(12.Sdp)
                .paddingHorizontal(12.Sdp)
                .fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = appTheme.buttonColor, CircleShape)
                    .clip(CircleShape)
                    .padding(10.Sdp)
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        tint = appTheme.colorText,
                        painter = painterResource(R.drawable.ic_app_home),
                        contentDescription = null,
                        modifier = Modifier.size(22.Sdp)
                    )
                    TextView(
                        text = stringResource(R.string.go_to_home),
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        textSize = 12.Ssp,
                        color = appTheme.colorText,
                        modifier = Modifier.paddingStart(8.Sdp)
                    )
                }
                Box(
                    Modifier
                        .matchParentSize()
                        .onClick {
                            action.onGoToHome()
                        })
            }
            SpaceH(8)
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = white, CircleShape)
                    .border(1.dp, appTheme.buttonColor, CircleShape)
                    .clip(CircleShape)
                    .padding(10.Sdp)
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        tint = appTheme.buttonColor,
                        painter = painterResource(R.drawable.ic_app_edit),
                        contentDescription = null,
                        modifier = Modifier.size(22.Sdp)
                    )
                    TextView(
                        text = stringResource(R.string.continue_edit),
                        font = R.font.rb_500,
                        textAlign = TextAlign.Center,
                        textSize = 12.Ssp,
                        color = appTheme.buttonColor,
                        modifier = Modifier.paddingStart(8.Sdp)
                    )
                }
                Box(
                    Modifier
                        .matchParentSize()
                        .onClick {
                            action.onContinueEdit()
                        })
            }
        }
        BannerAdCard(
            holder = bannerAdContent ,
            loading = {
                DefaultBannerAdLoading()
            }
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "SaveQrSuccessScreen")
@Composable
private fun SaveQrSuccessScreenPreview() {
    AppViewTheme {
        Surface {
            val vm = remember { EditQrViewModel(AppRepository(fakeDataHistoryDao)) }
            SaveQrSuccessScreen(
                viewModel = vm,
                action = object : SaveQrAction {}
            )
        }
    }
}
