package com.azg.alf_002_qrscan.ui.component

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.TextView
import com.sdp.ssp.android.Sdp
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.borderBottom
import com.azg.alf_002_qrscan.style.disabled
import com.azg.alf_002_qrscan.style.disabledText
import com.azg.alf_002_qrscan.style.fontFamily
import com.azg.alf_002_qrscan.style.fontPadding
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingEnd
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingStart
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.language.LanguageItem
import com.azg.alf_002_qrscan.widget.grdStart
import com.sdp.ssp.android.Ssp

@Preview(device = PIXEL_4_XL, showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun PreviewUnit() {
    var value by remember { mutableStateOf("Error dsjksjdksjd") }
    val isError = value.length > 10
    AppViewTheme {
        Column(Modifier.fillMaxWidth()) {
            MonthYearSelect(Modifier.fillMaxWidth()) {}
            SpaceH(16)
            HeaderCreate("Create", R.drawable.ic_app_back)
            ButtonCreate(true)
        }
    }
}

@Composable
fun LanguageItem(
      currentLanguage: LanguageItem? = null,
      theme: ColorTheme = ColorTheme.BLUE,
      item: LanguageItem,
      @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
      callback: (LanguageItem) -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .border(
                width = 1.Sdp,
                color = if (item != currentLanguage) Color(0xFFF2F2F7).opacity(50f) else theme.buttonColor,
                shape = RoundedCornerShape(8.Sdp)
            )
            .background(white.opacity(60f), shape = RoundedCornerShape(12.Sdp))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(item.flagId),
                contentDescription = item.code,
                modifier = Modifier
                    .size(24.Sdp)
                    .border(1.dp, color = gray.opacity(0.2f), shape = CircleShape)
            )
            TextView(
                text = item.name,
                font = R.font.rb_400,
                textSize = 12.Ssp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )

            Icon(
                painter = painterResource(if (currentLanguage == item) R.drawable.ic_language_selected else R.drawable.ic_language_unselected),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (currentLanguage == item) theme.buttonColor else LocalContentColor.current
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .onClick { callback(item) })
    }
}

@Composable
fun ButtonCreate(
      isEnable: Boolean = false,
      theme: ColorTheme = ColorTheme.DENIM,
      modifier: Modifier = Modifier,
      onClick: () -> Unit = {}
) {
    TextView(
        text = stringResource(R.string.create),
        modifier = modifier.then(
            Modifier
                .wrapContentHeight()
                .background(
                    if (isEnable) theme.buttonColor else disabled, CircleShape
                )
                .padding(14.Sdp)
        ),
        textAlign = TextAlign.Center,
        textSize = 12.Ssp,
        font = R.font.rb_600,
        color = if (isEnable) theme.colorText else disabledText,
        singleLine = true,
        txtStyle = TextStyle.Default.fontPadding(false)
    ) {
        if (isEnable) {
            onClick.invoke()
        }
    }
}

@Composable
fun ButtonSave(
      isEnable: Boolean = false,
      theme: ColorTheme = ColorTheme.DENIM,
      modifier: Modifier = Modifier,
      onClick: () -> Unit = {}
) {
    TextView(
        text = stringResource(R.string.save),
        modifier = modifier.then(
            Modifier
                .wrapContentHeight()
                .background(
                    if (isEnable) theme.buttonColor else disabled, CircleShape
                )
                .padding(12.Sdp)
        ),
        textAlign = TextAlign.Center,
        textSize = 12.Ssp,
        font = R.font.rb_600,
        color = if (isEnable) theme.colorText else disabledText,
        singleLine = true,
        txtStyle = TextStyle.Default.fontPadding(false)
    ) {
        if (isEnable) {
            onClick.invoke()
        }
    }
}

@Composable
fun AppHeader(
    header: String,
    @DrawableRes lefIcon: Int,
    modifier: Modifier = Modifier,
    leftClick: () -> Unit = {},
    rightContent: @Composable RowScope.() -> Unit = { Box(Modifier.size(26.Sdp)) }
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarPadding()
            .paddingHorizontal(12.Sdp)
            .paddingBottom(8.Sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconView(
            item = lefIcon,
            modifier = Modifier.size(26.Sdp),
            contentDescription = stringResource(R.string.cd_back)
        ) {
            leftClick.invoke()
        }

        Text(
            text = header,
            style = headerScreen,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 16.Ssp
        )

        rightContent()
    }
}

@Composable
fun SimpleBackTopBar(@StringRes title: Int, modifier: Modifier = Modifier, onBack: () -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .paddingHorizontal(8.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconView(
            item = R.drawable.ic_app_back,
            modifier = Modifier.size(32.Sdp),
            icSize = 24,
            tint = black,
            contentDescription = stringResource(R.string.cd_back)
        ) { onBack() }

        TextView(
            text = title,
            font = R.font.rb_600,
            color = black,
            textSize = 16.Ssp
        )
    }
}

@Composable
fun HeaderCreate(
      header: String,
      @DrawableRes lefIcon: Int,
      @DrawableRes rightIcon: Int? = null,
      modifier: Modifier = Modifier,
      rightAction: (() -> Unit)? = null,
      leftClick: () -> Unit = {},
) {
    AppHeader(header = header, lefIcon = lefIcon, modifier = modifier, leftClick = leftClick) {
        rightIcon?.let {
            IconView(
                item = rightIcon, modifier = Modifier.size(26.Sdp)
            ) {
                rightAction?.invoke()
            }
        } ?: run {
            Box(Modifier.size(26.Sdp))
        }
    }
}

@Composable
fun HeaderCreateResult(
      header: String,
      @DrawableRes lefIcon: Int,
      modifier: Modifier = Modifier,
      onEdit: () -> Unit = {},
      onDelete: () -> Unit = {},
      leftClick: () -> Unit = {},
) {
    AppHeader(header = header, lefIcon = lefIcon, modifier = modifier, leftClick = leftClick) {
        IconView(
            item = R.drawable.ic_app_edit,
            modifier = Modifier
                .paddingEnd(10.Sdp)
                .size(26.Sdp),
            contentDescription = stringResource(R.string.cd_edit)
        ) {
            onEdit.invoke()
        }

        IconView(
            item = R.drawable.ic_app_delete,
            modifier = Modifier.size(26.Sdp),
            contentDescription = stringResource(R.string.cd_delete)
        ) {
            onDelete.invoke()
        }
    }
}

@Composable
fun HeaderScanResult(
      header: String,
      @DrawableRes lefIcon: Int,
      isFavorite: Boolean = false,
      modifier: Modifier = Modifier,
      onFavorite: () -> Unit = {},
      onDelete: () -> Unit = {},
      leftClick: () -> Unit = {},
) {
    AppHeader(header = header, lefIcon = lefIcon, modifier = modifier, leftClick = leftClick) {
        IconView(
            item = if (isFavorite) R.drawable.ic_app_favorite_minus else R.drawable.ic_app_favorite_add,
            modifier = Modifier
                .paddingEnd(10.Sdp)
                .size(26.Sdp),
            contentDescription = stringResource(if (isFavorite) R.string.cd_favorite_remove else R.string.cd_favorite_add)
        ) {
            onFavorite.invoke()
        }

        IconView(
            item = R.drawable.ic_app_delete,
            modifier = Modifier.size(26.Sdp),
            contentDescription = stringResource(R.string.cd_delete)
        ) {
            onDelete.invoke()
        }
    }
}

@SuppressLint("ResourceType")
@Composable
fun MonthYearSelect(
      modifier: Modifier = Modifier, isMonth: Boolean = true, isMonthSelect: (Boolean) -> Unit = {}
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(0.45f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.Sdp))
                .padding(vertical = 8.Sdp)
                .weight(0.55f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .paddingEnd(3.Sdp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(if (isMonth) R.drawable.ic_language_selected else R.drawable.ic_language_unselected),
                        contentDescription = null,
                        Modifier
                            .size(24.dp)
                            .onClick {
                                isMonthSelect.invoke(true)
                            })
                    Text(
                        text = stringResource(R.string.month_),
                        fontSize = 11.Ssp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = TextStyle.Default.fontFamily(R.font.rb_500),
                        color = black,
                        modifier = Modifier
                            .weight(1f)
                            .paddingStart(3.Sdp),
                    )
                }
                Row(
                    Modifier
                        .paddingStart(3.Sdp)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(if (!isMonth) R.drawable.ic_language_selected else R.drawable.ic_language_unselected),
                        contentDescription = null,
                        Modifier
                            .size(24.dp)
                            .onClick {
                                isMonthSelect.invoke(false)
                            })
                    Text(
                        text = stringResource(R.string.year),
                        fontSize = 11.Ssp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = TextStyle.Default.fontFamily(R.font.rb_500),
                        color = black,
                        modifier = Modifier
                            .weight(1f)
                            .paddingStart(3.Sdp)
                    )
                }
            }
        }
    }
}
