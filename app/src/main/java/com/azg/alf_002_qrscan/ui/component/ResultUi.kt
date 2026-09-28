package com.azg.alf_002_qrscan.ui.component

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.fontOf
import com.azg.alf_002_qrscan.style.fontPadding
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingEnd
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingStart
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.style.widthPercent
import com.azg.alf_002_qrscan.style.widthPercentDp
import com.azg.alf_002_qrscan.widget.color86869c
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

/**
 * "Icon + label above value + copy button" card used for single-value QR types
 * (Text/Website/Phone) on both the Create Result and Scan Result screens.
 */
@Composable
fun ResultPrimaryValueCard(
    @DrawableRes icon: Int,
    accentColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    theme: ColorTheme,
) {
    Row(
        modifier
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(theme.buttonColor.opacity(10f), theme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(14.Sdp)
            )
            .background(white, RoundedCornerShape(14.Sdp))
            .padding(12.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(24.Sdp)
                .clip(CircleShape)
                .background(accentColor.opacity(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            IconView(item = icon, tint = accentColor, icSize = 18)
        }
        SpaceW(10.Sdp)
        Column(Modifier.weight(1f)) {
            TextView(
                text = label,
                font = R.font.rb_500,
                color = Color.Black,
                textSize = 13.Ssp
            )
            SpaceH(2.Sdp)
            TextView(
                text = value,
                font = R.font.rb_400,
                color = color86869c,
                textSize = 12.Ssp
            )
        }
    }
}

/**
 * "Icon circle + label" action button used in the Copy/Save/Share (and type-specific CTA)
 * rows on both the Create Result and Scan Result screens.
 */
@Composable
fun ButtonAction(
    @StringRes label: Int,
    @DrawableRes iconRes: Int,
    color: Color = Color.White,
    modifier: Modifier = Modifier,
    action: () -> Unit = {},
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            tint = color,
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = modifier
                .clip(CircleShape)
                .clickable(true) {
                    action()
                }
                .padding(8.Sdp)
        )
        TextView(
            text = stringResource(label),
            font = R.font.rb_400,
            textAlign = TextAlign.Center,
            textSize = 11.Ssp,
            color = black,
            modifier = Modifier
                .paddingTop(6.Sdp)
                .wrapContentSize()
        )
    }
}

/**
 * Shared top bar for the Create/Scan result screens: back button, title, and a
 * trailing slot for the screen-specific header action buttons.
 */
@Composable
fun ResultTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .paddingHorizontal(12.Sdp)
            .paddingBottom(8.Sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconView(item = R.drawable.ic_app_back, tint = black, icSize = 20, onclick = onBack)
        TextView(
            text = title,
            font = R.font.rb_600,
            color = black,
            textSize = 16.Ssp,
            modifier = Modifier
                .weight(1f)
                .paddingStart(8.Sdp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.Sdp), content = actions)
    }
}

/**
 * Circular neon-glow icon button used for header actions (favorite/edit/delete)
 * on both result screens.
 */
@Composable
fun ResultHeaderActionButton(
    @DrawableRes icon: Int,
    tint: Color,
    theme: ColorTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier
            .size(32.Sdp)
            .neonState(
                true,
                outerRadius = 4.Sdp,
                outerSpread = 1.Sdp,
                innerRadius = 4.Sdp,
                colors = listOf(theme.buttonColor.opacity(30f), theme.buttonColor.opacity(30f)),
                shape = CircleShape
            )
            .background(white, CircleShape)
            .onClick { onClick() },
        contentAlignment = Alignment.Center
    ) {
        IconView(item = icon, tint = tint, icSize = 18)
    }
}

/**
 * QR/barcode preview image (or a "no QR" placeholder) shown at the top of both
 * result screens.
 */
@Composable
fun ResultQrPreview(
    bitmap: Bitmap?,
    isBarcode: Boolean,
    colorTheme : ColorTheme = ColorTheme.BLUE,
    modifier: Modifier = Modifier
) {
    bitmap?.asImageBitmap()?.let {
        if (isBarcode) {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = modifier
                    .width(widthPercentDp(60f))
                    .aspectRatio(5 / 2.75f)
                    .neonState(
                        true,
                        outerRadius = 5.Sdp,
                        colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                        shape = RoundedCornerShape(14.Sdp)
                    )
                    .clip(RoundedCornerShape(14.Sdp))
                    .background(white, RoundedCornerShape(14.Sdp))
                    .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
                    .padding(4.Sdp)
                    .paddingVertical(3.Sdp)

            )
        } else {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(widthPercentDp(50f))
                    .neonState(
                        true,
                        outerRadius = 5.Sdp,
                        colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                        shape = RoundedCornerShape(14.Sdp)
                    )
                    .clip(RoundedCornerShape(14.Sdp))
                    .background(white, RoundedCornerShape(14.Sdp))
                    .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp))
                    .padding(4.Sdp)

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
                    colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                    shape = RoundedCornerShape(14.Sdp)
                )
                .background(white, RoundedCornerShape(14.Sdp))
                .border(1.Sdp, colorTheme.buttonColor.opacity(20f), RoundedCornerShape(14.Sdp)),
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

/**
 * White, neon-bordered card that wraps the type-specific detail rows on both
 * result screens.
 */
@Composable
fun ResultContentCard(
    theme: ColorTheme,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .paddingTop(12.Sdp)
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(theme.buttonColor.opacity(10f), theme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(14.Sdp)
            )
            .background(white, RoundedCornerShape(14.Sdp))
            .padding(12.Sdp)
            .heightIn(max = 150.Sdp)
            .verticalScroll(rememberScrollState()),
        content = content
    )
}

/**
 * "Label : value" row used inside [ResultContentCard] on both result screens.
 */
@Composable
fun ResultInfoRow(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    isTypeHeader: Boolean = false,
    accentColor: Color,
    icon: Int
) {
    Row(modifier = modifier.fillMaxWidth(1f)) {
        if(isTypeHeader){
            Box(
                Modifier
                    .paddingEnd(6.Sdp)
                    .size(16.Sdp)
                    .clip(CircleShape)
                    .background(accentColor.opacity(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                IconView(item = icon, tint = accentColor, icSize = 14)
            }
        }

        Text(
            text = title,
            style = TextStyle(
                platformStyle = fontPadding(false),
                textAlign = TextAlign.Start,
                color = black,
                fontFamily = fontOf(R.font.rb_500),
                fontSize = 11.Ssp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(if(isTypeHeader)  0.225f else 0.32f)
        )
        Text(
            text = ":",
            style = TextStyle(
                platformStyle = fontPadding(false),
                textAlign = TextAlign.Start,
                color = black,
                fontFamily = fontOf(R.font.rb_500),
                fontSize = 11.Ssp,
            ),
            modifier = Modifier.wrapContentSize(Alignment.TopCenter)
        )
        Text(
            text = content,
            style = TextStyle(
                platformStyle = fontPadding(false),
                textAlign = TextAlign.Start,
                color = black,
                fontFamily = fontOf(R.font.rb_400),
                fontSize = 11.Ssp
            ),
            modifier = Modifier
                .paddingStart(10.Sdp)
                .weight(0.68f)
        )
    }
}

/**
 * Full-width pill CTA button (e.g. "Open link", "Call", "Connect") shown below
 * the quick-action icons on both result screens.
 */
@Composable
fun ResultPrimaryCtaButton(
    @StringRes label: Int,
    @DrawableRes icon: Int,
    theme: ColorTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .paddingVertical(12.Sdp)
            .fillMaxWidth(0.75f)
            .background(color = theme.buttonColor, CircleShape)
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(10.Sdp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            tint = theme.colorText,
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(22.Sdp)
        )
        TextView(
            text = stringResource(label),
            font = R.font.rb_500,
            textAlign = TextAlign.Center,
            textSize = 12.Ssp,
            color = theme.colorText,
            modifier = Modifier
                .paddingStart(8.Sdp)
                .wrapContentSize()
        )
    }
}

/** A single copy/save/share/etc quick-action icon in [ResultQuickActionsRow]. */
data class ResultQuickAction(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    val onClick: () -> Unit
)

/**
 * Row of circular quick-action icons (copy/save/share/...) shown above the
 * primary CTA button on both result screens.
 */
@Composable
fun ResultQuickActionsRow(
    theme: ColorTheme,
    actions: List<ResultQuickAction>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .paddingHorizontal(24.Sdp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        actions.forEach { action ->
            ButtonAction(
                action.label,
                action.icon,
                color = theme.buttonColor,
                modifier = Modifier
                    .size(38.Sdp)
                    .background(color = theme.buttonColor.opacity(0.12f), CircleShape)
            ) {
                action.onClick()
            }
        }
    }
}
