package com.azg.alf_002_qrscan.ui.main.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.CustomSwitch
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.VerScrollView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.fontFamily
import com.azg.alf_002_qrscan.style.fontPadding
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.RatingDialog
import com.azg.alf_002_qrscan.ui.language.LanguageItem
import com.azg.alf_002_qrscan.ui.language.getLanguageArray
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

class FakeActivity : BaseActivity() {
    override fun initialize() = Unit

    @Composable
    override fun BoxScope.ContentView() {

    }

}

@Preview(showBackground = true, name = "SettingScreen")
@Composable
private fun SettingScreenPreview() {
    val fakeActivity = FakeActivity()
    AppViewTheme {
        Surface(color = Color.White) {
            SettingScreen(
                colorTheme = ColorTheme.BLUE,
                activity = fakeActivity,
                gotoLangSetting = { /* no-op in preview */ },
                onBack = { /* no-op */ }
            )
        }
    }
}

@Preview(name = "ScanSetting")
@Composable
private fun ScanSettingPreview() {
    AppViewTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            ScanSetting(
                colorTheme = ColorTheme.BLUE,
            ) {}
        }
    }
}

@Preview(name = "SettingItem")
@Composable
private fun SettingItemPreview() {
    AppViewTheme {
        SettingItem(
            icon = R.drawable.ic_setting_info,
            label = R.string.about_us,
            action = { }
        )
    }
}

@Composable
fun SettingScreen(
    colorTheme: ColorTheme,
    activity: BaseActivity,
    isRateApp: Boolean = false,
    currentLanguage: LanguageItem? = null,
    statusBarHeight: Dp = 0.dp,
    gotoLangSetting: () -> Unit = {},
    onHelp:()-> Unit = {},
    onIap: () -> Unit = {},
    gotoSettingTheme: () -> Unit = {},
    gotoAboutUs: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val selectedLanguage by remember {
        mutableStateOf(currentLanguage ?: getLanguageArray()[0])
    }
    var isShowRating by remember {
        mutableStateOf(false)
    }
    ColumnParentContent(
        onBack = {
            onBack.invoke()
        }, modifier = Modifier
            .fillMaxSize()
            .background(colorTheme.bgColor)
            .statusBarPadding()
    ) {

        TextView(
            R.string.setting,
            font = R.font.rb_600,
            color = black,
            textSize = 16.Ssp,
            modifier = Modifier.fillMaxWidth().paddingHorizontal(12.Sdp),
            textAlign = TextAlign.Start
        )
        VerScrollView(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {

            Column(
                Modifier
                    .fillMaxWidth(1f)
                    .paddingTop(statusBarHeight)
            )
            {
                SpaceH(24)
                TextView(
                    stringResource(R.string.general),
                    font = R.font.rb_700,
                    color = black,
                    textSize = 12.Ssp
                )
                SpaceH(8)
                ScanSetting(colorTheme) {
                    gotoSettingTheme.invoke()
                }
                SpaceH(16)
                TextView(
                    stringResource(R.string.more),
                    font = R.font.rb_700,
                    color = black,
                    textSize = 16.Ssp
                )
                SpaceH(8)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(white, shape = RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    LangSettingItem(selectedLanguage, colorTheme) {
                        gotoLangSetting.invoke()
                    }
                    SettingItem(R.drawable.ic_sparkle, R.string.premium) {
                        onIap.invoke()
                    }
                    if (!isRateApp) {
                        SettingItem(R.drawable.ic_setting_rate, R.string.rate_us) {
                            isShowRating = true
                        }
                    }
                    SettingItem(R.drawable.ic_help_circle, R.string.help) {
                        onHelp()
                    }
                    SettingItem(R.drawable.ic_setting_info, R.string.about_us) {
                        gotoAboutUs.invoke()
                    }
                }
            }
        }
    }
    RatingDialog(activity, theme = colorTheme, isShowRating, { isShowRating = false }, {
        isShowRating = false
        AppPreferences.isRatingApp.value = true
    }) {
    }
}

@Composable
fun ScanSetting(
    colorTheme: ColorTheme,
    action: () -> Unit
) {
    val isEnableVibrate by rememberSharePreferenceWithLifecycle(true) { AppPreferences.isEnableVibrate }
    val isEnableSound by rememberSharePreferenceWithLifecycle(true) { AppPreferences.isEnableSound }
    Column(
        Modifier
            .fillMaxWidth()
            .background(white, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        ThemeSettingItem(colorTheme) {
            action.invoke()
        }
        Box(Modifier.padding(vertical = 12.dp)) {
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                IconView(
                    item = R.drawable.ic_setting_volume,
                    tint = colorTheme.buttonColor,
                    icSize = 22,
                    modifier = Modifier
                )
                SpaceW(12)
                TextView(
                    text = stringResource(R.string.sound),
                    font = R.font.rb_400,
                    textSize = 12.Ssp,
                    color = black
                )
                Spacer(Modifier.weight(1f))
                CustomSwitch(isEnableSound, colorTheme.buttonColor, onCheckedChange = {
                    AppPreferences.isEnableSound.value = it
                }, modifier = Modifier
                    .width(40.Sdp)
                    .height(20.Sdp))
            }
        }
        Box(Modifier.padding(vertical = 12.dp)) {
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                IconView(
                    item = R.drawable.ic_setting_vibrate,
                    tint = colorTheme.buttonColor,
                    modifier = Modifier,
                    icSize = 22,
                )
                SpaceW(12)
                TextView(
                    text = stringResource(R.string.vibrate),
                    font = R.font.rb_400,
                    textSize = 12.Ssp,
                    color = black
                )
                Spacer(Modifier.weight(1f))
                CustomSwitch(isEnableVibrate, colorTheme.buttonColor, onCheckedChange = {
                    AppPreferences.isEnableVibrate.value = it
                }, modifier = Modifier
                    .width(40.Sdp)
                    .height(20.Sdp))
            }
        }
    }
}

@Composable
fun LangSettingItem(
    currentLanguage: LanguageItem,
    colorTheme: ColorTheme = ColorTheme.BLUE,
    action: () -> Unit
) {
    Box(Modifier.padding(vertical = 12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_setting_lang,
                modifier = Modifier,
                icSize = 22,
            )
            SpaceW(12)
            TextView(
                text = R.string.language,
                font = R.font.rb_400,
                textSize = 12.Ssp,
                color = black
            )
            Spacer(Modifier.weight(1f))
            TextView(
                text = currentLanguage.name,
                font = R.font.rb_500,
                textSize = 12.Ssp,
                color = colorTheme.buttonColor
            )
            SpaceW(8)
            IconView(
                item = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                tint = colorTheme.buttonColor,
                modifier = Modifier,
                icSize = 22,
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .onClick { action.invoke() })
    }
}

@Composable
fun ThemeSettingItem(colorTheme: ColorTheme, action: () -> Unit) {
    val themeName by remember(colorTheme) {
        mutableIntStateOf(
            when (colorTheme) {
                ColorTheme.PINK -> R.string.theme_2
                ColorTheme.GREEN -> R.string.theme_3
                ColorTheme.DENIM -> R.string.theme_4
                else -> R.string.theme_1
            }
        )
    }
    Box(Modifier.padding(vertical = 12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_setting_theme,
                icSize = 22,
                modifier = Modifier, tint = colorTheme.buttonColor
            )
            SpaceW(12)
            TextView(
                text = R.string.color_theme,
                font = R.font.rb_400,
                textSize = 12.Ssp,
                color = black
            )
            Spacer(Modifier.weight(1f))
            TextView(
                text = stringResource(themeName),
                font = R.font.rb_500,
                textSize = 12.Ssp,
                color = colorTheme.buttonColor
            )
            SpaceW(8)
            IconView(
                item = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                tint = colorTheme.buttonColor,
                icSize = 22,
                modifier = Modifier
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .onClick { action.invoke() })
    }
}

@Composable
fun SettingItem(@DrawableRes icon: Int, @StringRes label: Int, action: () -> Unit) {
    Box(Modifier.padding(vertical = 12.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = icon,
                modifier = Modifier,
                icSize = 22,
            )
            SpaceW(12)
            TextView(
                text = stringResource(label),
                txtStyle = TextStyle.Default.fontFamily(R.font.rb_500).fontPadding(false).copy(
                    color = black, fontSize = 12.Ssp
                )
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .onClick {
                    action.invoke()
                })
    }
}
