package com.azg.alf_002_qrscan.style

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azg.alf_002_qrscan.R
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

fun String.format(): String {
    return this.replace("\n", "").trim()
}

fun String.checkValue(): Boolean {
    return this.replace("", "").replace("\n", "").isNotEmpty()
}

fun String.maxLength(length: Int): Boolean {
    return this.replace("\n", "").length < (length)
}

fun String.minLength(length: Int): Boolean {
    return this.replace("\n", "").length >= (length)
}

fun String.isEmail(): Boolean {
    return this.isNotEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun fontOf(@FontRes vararg fonts: Int): FontFamily =
    FontFamily(*fonts.map { resId -> Font(resId) }.toTypedArray())

fun fontPadding(isPadding: Boolean) = PlatformTextStyle(includeFontPadding = isPadding)

@Composable
fun InputValue(
    value: String,
    hint: String = "",
    maxLines: Int = 1000,
    color: Color = Color.Black,
    hintColor: Color = darkGray,
    font: Font = Font(R.font.rb_400),
    textSize: TextUnit = 16.sp,
    style: TextStyle = TextStyle.Default,
    @SuppressLint("ModifierParameter") boxModifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = keyboardType,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showCursor: Boolean = true,
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (String) -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = boxModifier
            .verticalScroll(rememberScrollState())
            .clickable(
                indication = null,
                interactionSource = interaction
            ) { focusRequester.requestFocus() },
        horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = modifier,
            cursorBrush = SolidColor(if (showCursor) color else Color.Transparent),
            textStyle = style.copy(
                color = color,
                fontSize = textSize,
                fontFamily = FontFamily(font),
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.Sdp), contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = TextStyle(
                                color = hintColor,
                                fontSize = textSize,
                                fontFamily = FontFamily(font)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
@Composable
fun InputValue(
    value: String,
    hint: String = "",
    font: Font = Font(R.font.rb_400),
    @SuppressLint("ModifierParameter") boxModifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    maxLines: Int = 1000,
    hintColor: Color = darkGray,
    color: Color = black,
    textAlignment: TextAlign = TextAlign.Start,
    singleLine: Boolean = false,
    textSize: TextUnit = 16.sp,
    textStyle: TextStyle = TextStyle.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    showCursor: Boolean = true,
    autoFocus: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onValueChange: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val interaction = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current
    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    Row(
        modifier = boxModifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = interaction
            ) { focusRequester.requestFocus() },
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        BasicTextField(
            value = value,
            singleLine = singleLine,
            onValueChange = { onValueChange(it) },
            modifier = modifier.focusRequester(focusRequester),
            cursorBrush = SolidColor(if (showCursor) color else Color.Transparent),
            textStyle = textStyle.copy(
                color = color,
                fontSize = textSize,
                textAlign = textAlignment,
                fontFamily = FontFamily(font),
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            maxLines = maxLines,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = TextStyle(
                                color = hintColor,
                                fontSize = textSize,
                                fontFamily = FontFamily(font),
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 12.Ssp,
    color: Color? = null,
    font: Font? = null,
    maxLine: Int = 100,
    textAlign: TextAlign? = TextAlign.Start,
    contentAlignment: Alignment,
    onclick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(interactionSource, null) { onclick?.invoke() },
        contentAlignment = contentAlignment
    ) {
        Text(
            text = text,
            fontSize = textSize,
            textAlign = textAlign,
            maxLines = maxLine,
            color = color ?: Color.Black,
            fontFamily = FontFamily(
                font ?: Font(R.font.rb_400)
            ),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}

@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 12.Ssp,
    color: Color? = null,
    font: Font? = null,
    maxLine: Int = 100,
    minLine: Int = 1,
    singleLine: Boolean = false,
    textAlign: TextAlign? = TextAlign.Start,
    onclick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val clickableModifier = if (onclick != null) Modifier.clickable(interactionSource, null) { onclick.invoke() } else Modifier
    Text(
        text = text,
        fontSize = textSize,
        textAlign = textAlign,
        maxLines = maxLine,
        minLines = minLine,
        overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
        color = color ?: black,
        fontFamily = FontFamily(
            font ?: Font(
                R.font.rb_400
            )
        ),
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
        modifier = modifier.then(clickableModifier)
    )
}

@Composable
fun TextView(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 12.Ssp,
    color: Color = Color.Black,
    font: Int = R.font.rb_400,
    maxLine: Int = 100,
    minLine: Int = 1,
    singleLine: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    onclick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        text = stringResource(text),
        fontSize = textSize,
        textAlign = textAlign,
        maxLines = maxLine,
        minLines = minLine,
        overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
        color = color ?: black,
        fontFamily = FontFamily(
            Font(
                font
            )
        ),
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ), textAlign = textAlign
        ),
        modifier = modifier.clickable(interactionSource, null) {
            onclick.invoke()
        }
    )
}
@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 12.Ssp,
    color: Color = Color.Black,
    font: Int = R.font.rb_400,
    maxLine: Int = 100,
    minLine: Int = 1,
    singleLine: Boolean = false,
    txtStyle: TextStyle = TextStyle(),
    textAlign: TextAlign? = TextAlign.Start,
    onclick: (() -> Unit)? = null
) = Text(
    text = text,
    fontSize = textSize,
    textAlign = textAlign,
    maxLines = if (singleLine) 1 else maxLine,
    overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
    color = color,
    minLines = minLine,
    fontFamily = FontFamily(Font(font)),
    style = txtStyle.copy(
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
    ),
    modifier = if(onclick!= null) modifier.onClick { onclick.invoke() } else modifier
)

@Composable
fun TextStyle.fontPadding(enabled: Boolean): TextStyle {
    return this.copy(
        platformStyle = PlatformTextStyle(includeFontPadding = enabled)
    )
}

@Composable
fun TextStyle.fontFamily(@FontRes fontId: Int): TextStyle {
    return this.copy(
        fontFamily = FontFamily(Font(fontId))
    )
}

@Stable
class ComposeTextStyle {
    private val b = AnnotatedString.Builder()

    fun append(
        text: String,
        style: FontStyle? = null,
        weight: FontWeight? = null,
        color: Color = Color.Black,
        underline: Boolean = false,
        strike: Boolean = false,
        bg: Color? = null,
        fontSizeSp: Float? = null,
        fontFamily: FontFamily? = null,
        onClick: (() -> Unit)? = null
    ) = apply {
        if (text.isEmpty()) return@apply
        val start = b.length
        val deco = buildList {
            if (underline) add(TextDecoration.Underline)
            if (strike) add(TextDecoration.LineThrough)
        }.let { if (it.isEmpty()) null else TextDecoration.combine(it) }

        b.pushStyle(
            SpanStyle(
                color = color,
                fontStyle = style,
                fontWeight = weight,
                textDecoration = deco,
                background = bg ?: Color.Transparent,
                fontSize = (fontSizeSp ?: 0f).takeIf { it > 0f }?.sp ?: TextUnit.Unspecified,
                fontFamily = fontFamily
            )
        )
        b.append(text)
        b.pop()
        val end = b.length
        if (onClick != null && end > start) {
            b.addLink(
                LinkAnnotation.Clickable(
                    tag = "click",
                    styles = null,
                    linkInteractionListener = object : LinkInteractionListener {
                        override fun onClick(link: LinkAnnotation) {
                            onClick.invoke()
                        }
                    }
                ),
                start,
                end
            )
        }
    }

    fun bold(
        text: String,
        color: Color = Color.Black,
        underline: Boolean = false,
        strike: Boolean = false,
        bg: Color? = null,
        fontSizeSp: Float? = null,
        fontFamily: FontFamily? = null,
        onClick: (() -> Unit)? = null
    ) = append(
        text, style = null, weight = FontWeight.Bold, color = color,
        underline = underline, strike = strike, bg = bg, fontSizeSp = fontSizeSp,
        fontFamily = fontFamily, onClick = onClick
    )

    fun italic(
        text: String,
        color: Color = Color.Black,
        underline: Boolean = false,
        strike: Boolean = false,
        bg: Color? = null,
        fontSizeSp: Float? = null,
        fontFamily: FontFamily? = null,
        onClick: (() -> Unit)? = null
    ) = append(
        text, style = FontStyle.Italic, weight = null, color = color,
        underline = underline, strike = strike, bg = bg, fontSizeSp = fontSizeSp,
        fontFamily = fontFamily, onClick = onClick
    )

    fun normal(
        text: String,
        color: Color = Color.Black,
        underline: Boolean = false,
        strike: Boolean = false,
        bg: Color? = null,
        fontSizeSp: Float? = null,
        fontFamily: FontFamily? = null,
        onClick: (() -> Unit)? = null
    ) = append(
        text, style = null, weight = null, color = color,
        underline = underline, strike = strike, bg = bg, fontSizeSp = fontSizeSp,
        fontFamily = fontFamily, onClick = onClick
    )

    fun build(): AnnotatedString = b.toAnnotatedString()
}