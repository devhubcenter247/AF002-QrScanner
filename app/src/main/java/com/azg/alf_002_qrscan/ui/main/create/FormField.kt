package com.azg.alf_002_qrscan.ui.main.create

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.rememberSharePreferenceWithLifecycle
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.InputValue
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingStart
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.primary
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.widget.color86869c
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

/**
 * Shared "icon + big title + subtitle" banner used at the top of every QR/Barcode create form.
 */
@Composable
fun CreateFormHeader(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .paddingTop(2.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(38.Sdp)
                .clip(RoundedCornerShape(16.Sdp))
                .background(accentColor.opacity(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(icon),
                modifier = Modifier
                    .size(40.Sdp)
                    .clip(RoundedCornerShape(12.Sdp)),
                contentDescription = null
            )
        }
        SpaceW(14.Sdp)
        Column {
            TextView(
                title,
                textSize = 14.Ssp,
                color = black,
                font = R.font.rb_600,
                modifier = Modifier.fillMaxWidth(1f)
            )
            TextView(
                subtitle,
                modifier = Modifier
                    .paddingTop(2.Sdp)
                    .fillMaxWidth(),
                textSize = 11.Ssp,
                color = color86869c,
                font = R.font.rb_400,
            )
        }
    }
}

@Composable
fun FormField(
    @StringRes labelRes: Int,
    value: String,
    colorTheme: ColorTheme,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    @DrawableRes leadingIcon: Int? = null,
    cornerRadius: Dp = 8.Sdp,
    onValueChange: (String) -> Unit,
) {
    TextView(
        stringResource(labelRes).trim(),
        modifier = Modifier
            .fillMaxWidth()
            .paddingTop(12.Sdp),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )
    Row(
        modifier = Modifier
            .paddingTop(6.Sdp)
            .fillMaxWidth()
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(white, RoundedCornerShape(cornerRadius))
            .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(cornerRadius))
            .paddingHorizontal(4.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {
            IconView(
                item = it,
                tint = colorTheme.buttonColor,
                icSize = 18,
                modifier = Modifier.paddingStart(8.Sdp)
            )
        }
        InputValue(
            value,
            hint = stringResource(R.string.enter_text_here),
            hintColor = lightGrey,
            textAlignment = TextAlign.Justify,
            textSize = 12.Ssp,
            maxLines = if (singleLine) 1 else 1000,
            singleLine = singleLine,
            keyboardType = keyboardType,
            boxModifier = Modifier
                .weight(1f)
                .paddingVertical(8.Sdp)
                .wrapContentHeight(),
            onValueChange = onValueChange
        )
    }
}

/**
 * Shared "label + multi-line text box" field for longer content (notes/subject/body/description).
 * Mirrors [FormField] but with a fixed [height] and a top-anchored leading icon.
 */
@Composable
fun FormMultilineField(
    @StringRes labelRes: Int,
    value: String,
    @DrawableRes leadingIcon: Int? = null,
    cornerRadius: Dp = 8.Sdp,
    height: Dp = 120.Sdp,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit,
) {
    val colorTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp}
    TextView(
        stringResource(labelRes).trim(),
        modifier = Modifier
            .fillMaxWidth()
            .paddingTop(12.Sdp),
        textSize = 13.Ssp,
        font = R.font.rb_400
    )
    Row(
        modifier = Modifier
            .paddingTop(6.Sdp)
            .fillMaxWidth()
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(white, RoundedCornerShape(cornerRadius))
            .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(cornerRadius))
            .paddingHorizontal(4.Sdp)
            .height(height),
    ) {
        leadingIcon?.let {
            IconView(
                item = it,
                tint = colorTheme.buttonColor,
                icSize = 18,
                modifier = Modifier
                    .paddingTop(10.Sdp)
                    .paddingHorizontal(8.Sdp)
            )
        }
        InputValue(
            value,
            hint = stringResource(R.string.enter_text_here),
            hintColor = lightGrey,
            textAlignment = TextAlign.Justify,
            singleLine = false,
            textSize = 12.Ssp,
            keyboardType = keyboardType,
            verticalAlignment = Alignment.Top,
            boxModifier = Modifier
                .weight(1f)
                .paddingVertical(2.Sdp)
                .fillMaxWidth()
                .wrapContentHeight(),
            onValueChange = onValueChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateFormHeaderPreview() {
    AppViewTheme {
        CreateFormHeader(
            icon = R.drawable.ic_create_url,
            title = "URL",
            subtitle = "Create a QR code for a website link",
            accentColor = primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FormFieldPreview() {
    AppViewTheme {
        Column(Modifier.paddingHorizontal(16.Sdp)) {
            FormField(
                labelRes = R.string.text,
                value = "Sample text content",
                colorTheme = ColorTheme.BLUE,
                leadingIcon = R.drawable.ic_create_text,
                onValueChange = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormMultilineFieldPreview() {
    AppViewTheme {
        Column(Modifier.paddingHorizontal(16.Sdp)) {
            FormMultilineField(
                labelRes = R.string.text,
                value = "This is a sample text for multiline field.",
                leadingIcon = R.drawable.ic_create_text,
                onValueChange = {}
            )
        }
    }
}
