package com.azg.alf_002_qrscan.ui.main.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.toastShort
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.disabled
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectThemeActivity : BaseActivity() {
    override fun initialize() = Unit

    @Composable
    override fun BoxScope.ContentView() {
        val colorSelect by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)
        val scope = rememberCoroutineScope()
        BoxParentContent(
            onBack = { finish() },
            modifier = Modifier
                .fillMaxSize()
                .background(colorSelect.bgColor)
                .statusBarPadding()
        ) {
            SelectThemeScreen(
                initialTheme = colorSelect,
                onBack = { finish() },
                onSave = { chosen ->
                    AppPreferences.themeApp.value = chosen
                    toastShort(getString(R.string.applying_theme_success))
                    scope.launch(Dispatchers.Main) {
                        delay(1200L)
                        finish()
                    }
                }
            )
        }
    }
}

@Composable
fun SelectThemeScreen(
    initialTheme: ColorTheme,
    onBack: () -> Unit,
    onSave: (ColorTheme) -> Unit,
) {
    var colorSelect by remember(initialTheme) { mutableStateOf(initialTheme) }

    Column(
        Modifier
            .fillMaxSize()
            .paddingHorizontal(16.Sdp)
            .statusBarPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onBack)
        Box(
            Modifier
                .fillMaxWidth()
                .background(color = white, shape = RoundedCornerShape(20.dp))
                .padding(vertical = 32.Sdp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(220.Sdp)
                    .clip(CircleShape)
                    .background(colorSelect.buttonColor.opacity(0.08f))
            )
            IconView(
                item = R.drawable.ic_sparkle,
                tint = colorSelect.buttonColor.opacity(0.5f),
                icSize = 14,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.Sdp, top = 36.Sdp)
            )
            IconView(
                item = R.drawable.ic_sparkle,
                tint = colorSelect.buttonColor.opacity(0.5f),
                icSize = 12,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.Sdp, top = 60.Sdp)
            )
            IconView(
                item = R.drawable.ic_sparkle,
                tint = colorSelect.buttonColor.opacity(0.5f),
                icSize = 10,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 30.Sdp, bottom = 50.Sdp)
            )
            ThemeView(colorSelect)
        }

        SpaceH(16)

        Column(
            Modifier
                .fillMaxWidth()
                .background(color = white, shape = RoundedCornerShape(20.dp))
                .padding(16.Sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextView(
                R.string.highlight_color,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                font = R.font.rb_500,
                textSize = 14.Ssp,
                color = black
            )

            SpaceH(16)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ColorTheme.entries.forEach { theme ->
                    ThemeOption(
                        theme = theme,
                        selected = (theme == colorSelect),
                        onSelect = { colorSelect = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        SpaceF()

        SaveButton(
            colorSelect = colorSelect,
            onClick = { onSave(colorSelect) }
        )

        SpaceH(16)
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .paddingBottom(12.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconView(
            item = R.drawable.ic_app_back,
            tint = black,
            icSize = 26,
            modifier = Modifier.size(30.Sdp)
        ) { onBack() }
        Spacer(modifier = Modifier.width(4.Sdp))
        TextView(
            text = stringResource(R.string.color_theme),
            font = R.font.rb_600,
            color = black,
            textSize = 16.Ssp
        )
    }
}

@Composable
private fun ThemeOption(
    theme: ColorTheme,
    selected: Boolean,
    onSelect: (ColorTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeName by remember {
        mutableIntStateOf(
            when (theme) {
                ColorTheme.PINK -> R.string.theme_2
                ColorTheme.GREEN -> R.string.theme_3
                ColorTheme.DENIM -> R.string.theme_4
                else -> R.string.theme_1
            }
        )
    }
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorDot(
            selected = selected,
            color = theme.buttonColor,
            onClick = { onSelect(theme) }
        )
        SpaceH(8.Sdp)
        TextView(
            text = stringResource(themeName),
            font = if (selected) R.font.rb_600 else R.font.rb_400,
            color = if (selected) theme.buttonColor else black,
            textSize = 12.Ssp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ColorDot(
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(46.Sdp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                Modifier
                    .size(42.Sdp)
                    .clip(CircleShape)
                    .border(2.Sdp, color, CircleShape)
            )
        }
        Box(
            Modifier
                .size(32.Sdp)
                .clip(CircleShape)
                .background(color)
                .clickable(onClick = onClick)
        )
        if (selected) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .size(16.Sdp)
                    .clip(CircleShape)
                    .background(color)
                    .border(2.Sdp, white, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconView(item = R.drawable.ic_app_check, tint = white, icSize = 10)
            }
        }
    }
}

@Composable
private fun SaveButton(
    colorSelect: ColorTheme,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(46.Sdp)
            .clip(RoundedCornerShape(50))
            .background(colorSelect.buttonColor)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconView(item = R.drawable.ic_app_save, tint = colorSelect.colorText, icSize = 18)
        Spacer(modifier = Modifier.width(8.Sdp))
        TextView(
            text = stringResource(R.string.save),
            font = Font(R.font.rb_600),
            textAlign = TextAlign.Center,
            color = colorSelect.colorText,
            textSize = 15.Ssp
        )
    }
}

@Composable
private fun ThemeView(colorSelect: ColorTheme) {
    val outerShape = remember { RoundedCornerShape(16.dp) }
    val cardShape = remember { RoundedCornerShape(12.dp) }
    val dividerColor = disabled
    val boxGray = remember { Color(0xFFB8B8BE) }

    Box(
        Modifier
            .size(100.dp, 175.dp)
            .padding(2.dp)
            .border(2.5.dp, colorSelect.buttonColor, outerShape)
    ) {
        Column(
            Modifier
                .padding(4.dp)
                .fillMaxSize()
                .background(white, cardShape)
                .border(1.5.dp, dividerColor, cardShape),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Color(0xFFF2F2F7),
                        shape = cardShape.copy(
                            bottomStart = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .width(45.dp)
                        .height(10.dp)
                        .background(boxGray, RoundedCornerShape(3.dp))
                )
                SpaceH(5.dp)
                Box(
                    Modifier
                        .width(30.dp)
                        .height(10.dp)
                        .background(boxGray, RoundedCornerShape(5.dp))
                )
            }

            SpaceH(12)

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(5) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .background(boxGray, CircleShape)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Box(
                Modifier
                    .width(50.dp)
                    .height(10.dp)
                    .background(colorSelect.buttonColor, RoundedCornerShape(3.dp))
            )

            SpaceH(18)
        }
    }
}

@Preview(showBackground = true, name = "SelectTheme - BLUE")
@Composable
private fun SelectThemePreviewBlue() {
    Box(
        Modifier
            .fillMaxSize()
            .background(ColorTheme.BLUE.bgColor)

    ) {
        SelectThemeScreen(
            initialTheme = ColorTheme.BLUE,
            onBack = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true, name = "SelectTheme - PINK")
@Composable
private fun SelectThemePreviewPink() {
    Box(
        Modifier
            .fillMaxSize()
            .background(ColorTheme.PINK.bgColor)
    ) {
        SelectThemeScreen(
            initialTheme = ColorTheme.PINK,
            onBack = {},
            onSave = {}
        )
    }
}
