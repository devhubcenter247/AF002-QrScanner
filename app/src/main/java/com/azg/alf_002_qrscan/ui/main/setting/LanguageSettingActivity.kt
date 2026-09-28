package com.azg.alf_002_qrscan.ui.main.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.LocateManager
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.LanguageItem
import com.azg.alf_002_qrscan.ui.language.LanguageItem
import com.azg.alf_002_qrscan.ui.language.getLanguageArray
import com.azg.alf_002_qrscan.ui.main.MainActivity
import com.azg.alf_002_qrscan.widget.mainColor
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

class LanguageSettingActivity : BaseActivity() {
    override fun initialize() = Unit

        @Composable
    override fun BoxScope.ContentView() {
        val languages = remember { getLanguageArray().toList() }
        var selected by rememberSaveable {
            mutableStateOf(getLanguageArray().find { it ->
                it.code == (AppPreferences.languageCode.value)
            } ?: getLanguageArray()[0])
        }
        val theme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)

        BoxParentContent(onBack = { finish() }) {
            LanguageSettingScreen(
                languages = languages,
                selected = selected,
                theme = theme,
                modifier = Modifier
                    .background(theme.bgColor)
                    .fillMaxSize(1f)
                    .statusBarPadding(),
                onSelect = { selected = it },
                onConfirm = {
                    val code = selected?.code ?: "en"
                    LocateManager.cachedAppLanguage = code
                    AppPreferences.setLocale(code)
                    launchAndClearTask<MainActivity>()
                }, onBack = {
                    finish()
                }
            )
        }
    }
}

@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL, showBackground = true)
@Composable
fun GreetingPreview() {
    val listLanguages = getLanguageArray()
    AppViewTheme {
        LanguageSettingScreen(
            listLanguages, listLanguages[0], modifier =
                Modifier
                    .paddingTop(6.Sdp)
                    .background(white), onSelect = {}, onConfirm = {}, onBack = {}
        )
    }
}

@Composable
fun LanguageSettingScreen(
      languages: List<LanguageItem>,
      selected: LanguageItem?,
      theme: ColorTheme = ColorTheme.BLUE,
      @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
      onSelect: (LanguageItem) -> Unit,
      onConfirm: () -> Unit,
      onBack: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        listState.animateScrollToItem(languages.indexOf(selected))
    }
    Column(modifier) {
        Row(
            Modifier.padding(horizontal = 12.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconView(
                item = R.drawable.ic_app_back,
                icSize = 28, tint = mainColor,
                contentDescription = stringResource(R.string.cd_back),
            ) {
                onBack()
            }
            SpaceW(4.Sdp)
            TextView(
                text = R.string.language,
                font = R.font.rb_600,
                textSize = 16.Ssp,
                color = black
            )
            Spacer(Modifier.weight(1f))
            IconView(
                item = R.drawable.ic_app_check,
                icSize = 28,
                tint = mainColor,
                contentDescription = stringResource(R.string.cd_next),
            ) {
                if (selected != null) onConfirm()
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.Sdp)
                .paddingTop(12.Sdp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            this.items(
                items = languages,
                key = { it.code }
            ) { item ->
                LanguageItem(currentLanguage = selected, theme = theme, item = item) { chosen ->
                    onSelect(chosen)
                }
            }
            item {
                SpaceH(10.Sdp)
            }
        }
    }
}
