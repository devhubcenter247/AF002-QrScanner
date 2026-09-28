package com.azg.alf_002_qrscan.ui.main.setting

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.BuildConfig
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.base.BaseActivity

import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.VerScrollView
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.SimpleBackTopBar
import androidx.core.net.toUri
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

class AboutUsActivity : BaseActivity() {
    override fun initialize() = Unit

    @Composable
    override fun BoxScope.ContentView() {
        val colorTheme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)

        BoxParentContent(
            onBack = { finish() },
            modifier = Modifier
                .fillMaxSize()
                .background(colorTheme.bgColor)
                .statusBarPadding()
        ) {
            AboutUsScreen(
                colorTheme = colorTheme,
                onBack = { finish() },
                onPrivacyPolicy = { openUrl(PRIVACY_POLICY_URL) },
                onTermsOfService = { openUrl(TERMS_OF_SERVICE_URL) },
                onShareApp = { shareApp() }
            )
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${getString(R.string.check_out_this_amazing_app)}$PLAY_STORE_URL$packageName"
                )
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PRIVACY_POLICY_URL = "https://docs.google.com/document/d/e/2PACX-1vR4SqEYaVWrldLT2p8ExbJC_LkgBTjSYTuS818aZXRHSj80nUaz-dat0_vf_QbJqg/pub"
        private const val TERMS_OF_SERVICE_URL = "https://docs.google.com/document/d/e/2PACX-1vTZexOUdGMrvfmr8vjNmReU_M9qQOa6TAjpAk6zwlbVg_bjdxmTw5yQGAREBZGM1g/pub"
        private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="
    }
}

@Composable
fun AboutUsScreen(
    colorTheme: ColorTheme,
    onBack: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsOfService: () -> Unit,
    onShareApp: () -> Unit
) {
    val context: Context = LocalContext.current
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AboutTopBar(onBack)

        VerScrollView(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            SpaceH(32)

            // App Icon
            Image(
                painter = painterResource(R.drawable.img_logo_splash),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(100.Sdp)
                    .clip(RoundedCornerShape(10.Sdp))
                    .align(Alignment.CenterHorizontally)
            )

            SpaceH(16)

            // App Name
            TextView(
                text = stringResource(R.string.app_name),
                font = R.font.rb_700,
                color = black,
                textSize = 16.Ssp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SpaceH(8)

            // Version
            TextView(
                text = stringResource(R.string.version_format, BuildConfig.VERSION_NAME),
                font = R.font.rb_400,
                color = gray,
                textSize = 12.Ssp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SpaceH(32)

            // Menu Items
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(white, shape = RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                AboutMenuItem(
                    icon = R.drawable.ic_setting_privacy,
                    label = R.string.privacy_policy,
                    colorTheme = colorTheme,
                    onClick = onPrivacyPolicy
                )

                AboutMenuItem(
                    icon = R.drawable.ic_setting_terms,
                    label = R.string.term_of_service,
                    colorTheme = colorTheme,
                    onClick = onTermsOfService
                )

                AboutMenuItem(
                    icon = R.drawable.ic_setting_share,
                    label = R.string.share_app,
                    colorTheme = colorTheme,
                    onClick = onShareApp
                )
            }

            SpaceH(32)

            // Copyright
            TextView(
                text = stringResource(R.string.copyright).replace("%s",stringResource(R.string.app_name)),
                font = R.font.rb_400,
                color = gray,
                textSize = 10.Ssp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SpaceH(16)
        }
    }
}

@Composable
private fun AboutTopBar(onBack: () -> Unit) {
    SimpleBackTopBar(title = R.string.about_us, onBack = onBack)
}

@Composable
private fun AboutMenuItem(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    colorTheme: ColorTheme,
    onClick: () -> Unit
) {
    Box(Modifier.padding(vertical = 12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = icon,
                tint = colorTheme.buttonColor,
                modifier = Modifier
            )
            SpaceW(12)
            TextView(
                text = stringResource(label),
                font = R.font.rb_400,
                textSize = 12.Ssp,
                color = black
            )
            Spacer(Modifier.weight(1f))
            IconView(
                item = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                tint = black,
                modifier = Modifier
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .onClick { onClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutUsScreenPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(ColorTheme.BLUE.bgColor)
    ) {
        AboutUsScreen(
            colorTheme = ColorTheme.BLUE,
            onBack = {},
            onPrivacyPolicy = {},
            onTermsOfService = {},
            onShareApp = {}
        )
    }
}
