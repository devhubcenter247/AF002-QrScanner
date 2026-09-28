package com.azg.alf_002_qrscan.ui.main.create.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
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
import com.azg.alf_002_qrscan.model.BallShape
import com.azg.alf_002_qrscan.model.CornerShape
import com.azg.alf_002_qrscan.model.PixelShape
import com.azg.alf_002_qrscan.model.QrQuality
import com.azg.alf_002_qrscan.model.QrTemplate
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.ColumnParentContent
import com.azg.alf_002_qrscan.style.CustomSwitch
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.VerScrollView
import com.azg.alf_002_qrscan.style.adjustLevel
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.disabled
import com.azg.alf_002_qrscan.style.disabledText
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.lineColor
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.style.widthPercent
import com.azg.alf_002_qrscan.style.widthPercentDp
import com.azg.alf_002_qrscan.ui.component.ColorPickerDialog
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.CustomColorItem
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.viewmodel.EditQrViewModel
import com.azg.alf_002_qrscan.viewmodel.fakeDataHistoryDao
import com.lib.ads.application.ads.helper.banner.BannerAdCard
import com.lib.ads.application.ads.helper.banner.DefaultBannerAdLoading
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditQrScreen(
    viewModel: EditQrViewModel = koinViewModel(),
    contentEdit: String,
    modifier: Modifier = Modifier,
    onSave: (Bitmap?) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val appTheme by rememberSharePreferenceWithLifecycle(ColorTheme.BLUE) { AppPreferences.themeApp }
    val qrVectorBitmap by viewModel.qrValueBitmap.collectAsStateWithLifecycle(null)

    val foregroundColor by viewModel.foregroundColor.collectAsStateWithLifecycle()
    val backgroundColor by viewModel.backgroundColor.collectAsStateWithLifecycle()
    val useGradient by viewModel.useGradient.collectAsStateWithLifecycle()
    val gradientStartColor by viewModel.gradientStartColor.collectAsStateWithLifecycle()
    val gradientEndColor by viewModel.gradientEndColor.collectAsStateWithLifecycle()
    val useBackgroundGradient by viewModel.useBackgroundGradient.collectAsStateWithLifecycle()
    val backgroundGradientStartColor by viewModel.backgroundGradientStartColor.collectAsStateWithLifecycle()
    val backgroundGradientEndColor by viewModel.backgroundGradientEndColor.collectAsStateWithLifecycle()
    val pixelShape by viewModel.pixelShape.collectAsStateWithLifecycle()
    val cornerShape by viewModel.cornerShape.collectAsStateWithLifecycle()
    val ballShape by viewModel.ballShape.collectAsStateWithLifecycle()
    val selectedLogoRes by viewModel.selectedLogoRes.collectAsStateWithLifecycle()
    val qrQuality by viewModel.qrQuality.collectAsStateWithLifecycle()
    var bitmapQrData by remember(qrVectorBitmap) {
        mutableStateOf(qrVectorBitmap)
    }
    var showColorPicker by remember { mutableStateOf(false) }
    var colorPickerType by remember { mutableStateOf<ColorPickerType?>(null) }
    var colorPickerInitialColor by remember { mutableStateOf(Color.Black) }
    LaunchedEffect(contentEdit) {
        viewModel.generateQrData(contentEdit, context)
    }
    val bannerAdContent =rememberBannerAdHolder(
        placement = BannerPlacement.BannerAll,
    )
    DisposableEffect(bannerAdContent) {
        bannerAdContent.request()
        onDispose {
            bannerAdContent.cancel()
        }
    }
    val onPickColor: (ColorPickerType, Color) -> Unit = { type, initial ->
        colorPickerType = type
        colorPickerInitialColor = initial
        showColorPicker = true
    }

    ColorPickerDialog(
        isShow = showColorPicker,
        initialColor = colorPickerInitialColor,
        theme = appTheme,
        onDismiss = { showColorPicker = false },
        onColorSelected = { color ->
            when (colorPickerType) {
                ColorPickerType.FOREGROUND -> viewModel.setForegroundColor(color)
                ColorPickerType.BACKGROUND -> viewModel.setBackgroundColor(color)
                ColorPickerType.GRADIENT_START -> viewModel.setGradientStartColor(color)
                ColorPickerType.GRADIENT_END -> viewModel.setGradientEndColor(color)
                ColorPickerType.BG_GRADIENT_START -> viewModel.setBackgroundGradientStartColor(color)
                ColorPickerType.BG_GRADIENT_END -> viewModel.setBackgroundGradientEndColor(color)
                null -> {}
            }
        }
    )

    ColumnParentContent(onBack = {
        onBack()
    }, modifier = modifier.fillMaxSize())
    {
        EditQrTopBar(onBack = onBack)

        EditQrPreview(bitmapQrData)

        VerScrollView(
            Modifier
                .paddingTop(10.Sdp)
                .weight(1f)
                .fillMaxWidth(1f)
        )
        {
            EditQrOptionsContent(
                viewModel = viewModel,
                appTheme = appTheme,
                foregroundColor = foregroundColor,
                backgroundColor = backgroundColor,
                useGradient = useGradient,
                gradientStartColor = gradientStartColor,
                gradientEndColor = gradientEndColor,
                useBackgroundGradient = useBackgroundGradient,
                backgroundGradientStartColor = backgroundGradientStartColor,
                backgroundGradientEndColor = backgroundGradientEndColor,
                pixelShape = pixelShape,
                cornerShape = cornerShape,
                ballShape = ballShape,
                selectedLogoRes = selectedLogoRes,
                qrQuality = qrQuality,
                onPickColor = onPickColor
            )
        }

        EditQrSaveButton(
            enabled = qrVectorBitmap != null,
            theme = appTheme,
            onClick = { onSave(qrVectorBitmap) }
        )
        BannerAdCard(
            holder = bannerAdContent ,
            loading = {
                DefaultBannerAdLoading()
            }
        )
    }
}

@Composable
private fun EditQrTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .paddingHorizontal(12.Sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        IconView(
            item = R.drawable.ic_app_back,
            tint = black,
            icSize = 24
        ) {
            onBack()
        }

        TextView(
            text = stringResource(R.string.edit_qr),
            font = R.font.rb_600,
            color = black,
            textSize = 16.Ssp
        )

        Box(Modifier.size(28.Sdp))
    }
}

@Composable
private fun EditQrPreview(bitmapQrData: Bitmap?) {
    Box(
        Modifier
            .fillMaxWidth(1f),
        contentAlignment = Alignment.Center
    )
    {
        bitmapQrData?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(widthPercentDp(50f))
                    .background(
                        white,
                        RoundedCornerShape(10.Sdp)
                    )
                    .clip(RoundedCornerShape(10.Sdp))
            )
        } ?: run {
            Box(
                Modifier
                    .widthPercent(60f)
                    .aspectRatio(5 / 3f)
                    .background(
                        white,
                        RoundedCornerShape(10.Sdp)
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
}

@Composable
private fun EditQrOptionsContent(
    viewModel: EditQrViewModel,
    appTheme: ColorTheme,
    foregroundColor: Color,
    backgroundColor: Color,
    useGradient: Boolean,
    gradientStartColor: Color,
    gradientEndColor: Color,
    useBackgroundGradient: Boolean,
    backgroundGradientStartColor: Color,
    backgroundGradientEndColor: Color,
    pixelShape: PixelShape,
    cornerShape: CornerShape,
    ballShape: BallShape,
    selectedLogoRes: Int?,
    qrQuality: QrQuality,
    onPickColor: (ColorPickerType, Color) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .paddingHorizontal(12.Sdp)
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.Sdp)
    ) {
        TemplatesSection(theme = appTheme, onSelect = viewModel::applyTemplate)

        GradientToggleSection(
            label = stringResource(R.string.use_gradient),
            checked = useGradient,
            theme = appTheme,
            onCheckedChange = viewModel::setUseGradient
        )

        SolidColorSection(
            visible = !useGradient,
            title = stringResource(R.string.foreground_color),
            theme = appTheme,
            colors = viewModel.listForegroundColor,
            selected = foregroundColor,
            onPick = { onPickColor(ColorPickerType.FOREGROUND, foregroundColor) },
            onSelect = viewModel::setForegroundColor
        )

        GradientColorSection(
            visible = useGradient,
            startTitle = stringResource(R.string.gradient_start_color),
            endTitle = stringResource(R.string.gradient_end_color),
            theme = appTheme,
            colors = viewModel.listForegroundColor,
            startColor = gradientStartColor,
            endColor = gradientEndColor,
            onPickStart = { onPickColor(ColorPickerType.GRADIENT_START, gradientStartColor) },
            onPickEnd = { onPickColor(ColorPickerType.GRADIENT_END, gradientEndColor) },
            onSelectStart = viewModel::setGradientStartColor,
            onSelectEnd = viewModel::setGradientEndColor
        )

        GradientToggleSection(
            label = stringResource(R.string.use_background_gradient),
            checked = useBackgroundGradient,
            theme = appTheme,
            onCheckedChange = viewModel::setUseBackgroundGradient
        )

        SolidColorSection(
            visible = !useBackgroundGradient,
            title = stringResource(R.string.background_color),
            theme = appTheme,
            colors = viewModel.listBackgroundColor,
            selected = backgroundColor,
            onPick = { onPickColor(ColorPickerType.BACKGROUND, backgroundColor) },
            onSelect = viewModel::setBackgroundColor
        )

        GradientColorSection(
            visible = useBackgroundGradient,
            startTitle = stringResource(R.string.background_gradient_start_color),
            endTitle = stringResource(R.string.background_gradient_end_color),
            theme = appTheme,
            colors = viewModel.listBackgroundColor,
            startColor = backgroundGradientStartColor,
            endColor = backgroundGradientEndColor,
            onPickStart = { onPickColor(ColorPickerType.BG_GRADIENT_START, backgroundGradientStartColor) },
            onPickEnd = { onPickColor(ColorPickerType.BG_GRADIENT_END, backgroundGradientEndColor) },
            onSelectStart = viewModel::setBackgroundGradientStartColor,
            onSelectEnd = viewModel::setBackgroundGradientEndColor
        )

        PixelShapeSection(selected = pixelShape, theme = appTheme, onSelect = viewModel::setPixelShape)
        CornerShapeSection(selected = cornerShape, theme = appTheme, onSelect = viewModel::setCornerShape)
        BallShapeSection(selected = ballShape, theme = appTheme, onSelect = viewModel::setBallShape)

        LogoSection(
            theme = appTheme,
            selectedLogoRes = selectedLogoRes,
            listIconPaths = viewModel.listIconPaths,
            onSelect = viewModel::setLogoRes
        )

        QualitySection(
            qrQuality = qrQuality,
            theme = appTheme,
            hasLogo = selectedLogoRes != null,
            onSelect = viewModel::setQrQuality
        )
    }
}

@Composable
private fun TemplatesSection(theme: ColorTheme, onSelect: (QrTemplate) -> Unit) {
    SectionCard(colorTheme = theme) {
        SectionLabel(stringResource(R.string.qr_templates))
        SpaceH(8.Sdp)
        LazyRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.Sdp)
        ) {
            itemsIndexed(QrTemplate.entries) { _, template ->
                TemplateChip(template = template, onSelect = onSelect)
            }
        }
    }
}

@Composable
private fun GradientToggleSection(
    label: String,
    checked: Boolean,
    theme: ColorTheme,
    onCheckedChange: (Boolean) -> Unit
) {
    SectionCard(colorTheme = theme) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextView(
                text = label,
                font = Font(R.font.rb_500),
                color = black
            )

            CustomSwitch(
                checked, theme.buttonColor, onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .width(38.Sdp)
                    .height(20.Sdp)
            )
        }
    }
}

@Composable
private fun SolidColorSection(
    visible: Boolean,
    title: String,
    theme: ColorTheme,
    colors: List<Color>,
    selected: Color,
    onPick: () -> Unit,
    onSelect: (Color) -> Unit
) {
    AnimatedVisibility(visible = visible) {
        SectionCard(colorTheme = theme) {
            SectionLabel(title)
            SpaceH(8)
            LazyRow(Modifier.fillMaxWidth()) {
                item {
                    CustomColorItem(theme = theme, onClick = onPick)
                    SpaceW(8)
                }
                itemsIndexed(colors) { _, value ->
                    ColorItem(
                        color = value,
                        isSelected = selected == value
                    ) { onSelect(value) }
                    SpaceW(8)
                }
            }
        }
    }
}

@Composable
private fun GradientColorSection(
    visible: Boolean,
    startTitle: String,
    endTitle: String,
    theme: ColorTheme,
    colors: List<Color>,
    startColor: Color,
    endColor: Color,
    onPickStart: () -> Unit,
    onPickEnd: () -> Unit,
    onSelectStart: (Color) -> Unit,
    onSelectEnd: (Color) -> Unit
) {
    AnimatedVisibility(visible = visible) {
        SectionCard(colorTheme = theme) {
            SectionLabel(startTitle)
            SpaceH(8)
            LazyRow(Modifier.fillMaxWidth()) {
                item {
                    CustomColorItem(theme = theme, onClick = onPickStart)
                    SpaceW(8)
                }
                itemsIndexed(colors) { _, value ->
                    ColorItem(
                        color = value,
                        isSelected = startColor == value
                    ) { onSelectStart(value) }
                    SpaceW(8)
                }
            }

            SpaceH(12)
            SectionLabel(endTitle)
            SpaceH(8)
            LazyRow(Modifier.fillMaxWidth()) {
                item {
                    CustomColorItem(theme = theme, onClick = onPickEnd)
                    SpaceW(8)
                }
                itemsIndexed(colors) { _, value ->
                    ColorItem(
                        color = value,
                        isSelected = endColor == value
                    ) { onSelectEnd(value) }
                    SpaceW(8)
                }
            }
        }
    }
}

@Composable
private fun PixelShapeSection(
    selected: PixelShape,
    theme: ColorTheme,
    onSelect: (PixelShape) -> Unit
) {
    SectionCard(colorTheme = theme) {
        ShapeSection(
            title = stringResource(R.string.pixel_shape),
            theme = theme,
            options = listOf(
                ShapeOption(
                    stringResource(R.string.shape_default),
                    R.drawable.ic_shape_grid,
                    selected == PixelShape.DEFAULT
                ) { onSelect(PixelShape.DEFAULT) },
                ShapeOption(
                    stringResource(R.string.shape_round),
                    R.drawable.ic_shape_round,
                    selected == PixelShape.ROUND
                ) { onSelect(PixelShape.ROUND) },
                ShapeOption(
                    stringResource(R.string.shape_square),
                    R.drawable.ic_shape_square,
                    selected == PixelShape.SQUARE
                ) { onSelect(PixelShape.SQUARE) },
                ShapeOption(
                    stringResource(R.string.shape_star),
                    R.drawable.ic_shape_star,
                    selected == PixelShape.STAR
                ) { onSelect(PixelShape.STAR) },
                ShapeOption(
                    stringResource(R.string.shape_rhombus),
                    R.drawable.ic_shape_rhombus,
                    selected == PixelShape.RHOMBUS
                ) { onSelect(PixelShape.RHOMBUS) },
                ShapeOption(
                    stringResource(R.string.shape_heart),
                    R.drawable.ic_shape_heart,
                    selected == PixelShape.HEART
                ) { onSelect(PixelShape.HEART) },
            )
        )
    }
}

@Composable
private fun CornerShapeSection(
    selected: CornerShape,
    theme: ColorTheme,
    onSelect: (CornerShape) -> Unit
) {
    SectionCard(colorTheme = theme) {
        ShapeSection(
            title = stringResource(R.string.corner_shape),
            theme = theme,
            options = listOf(
                ShapeOption(
                    stringResource(R.string.shape_default),
                    R.drawable.ic_shape_grid,
                    selected == CornerShape.DEFAULT
                ) { onSelect(CornerShape.DEFAULT) },
                ShapeOption(
                    stringResource(R.string.shape_round),
                    R.drawable.ic_shape_round,
                    selected == CornerShape.ROUND
                ) { onSelect(CornerShape.ROUND) },
                ShapeOption(
                    stringResource(R.string.shape_square),
                    R.drawable.ic_shape_square,
                    selected == CornerShape.SQUARE
                ) { onSelect(CornerShape.SQUARE) },
                ShapeOption(
                    stringResource(R.string.shape_star),
                    R.drawable.ic_shape_star,
                    selected == CornerShape.STAR
                ) { onSelect(CornerShape.STAR) },
                ShapeOption(
                    stringResource(R.string.shape_rhombus),
                    R.drawable.ic_shape_rhombus,
                    selected == CornerShape.RHOMBUS
                ) { onSelect(CornerShape.RHOMBUS) },
                ShapeOption(
                    stringResource(R.string.shape_heart),
                    R.drawable.ic_shape_heart,
                    selected == CornerShape.HEART
                ) { onSelect(CornerShape.HEART) },
            )
        )
    }
}

@Composable
private fun BallShapeSection(
    selected: BallShape,
    theme: ColorTheme,
    onSelect: (BallShape) -> Unit
) {
    SectionCard(colorTheme = theme) {
        ShapeSection(
            title = stringResource(R.string.ball_shape),
            theme = theme,
            options = listOf(
                ShapeOption(
                    stringResource(R.string.shape_default),
                    R.drawable.ic_shape_grid,
                    selected == BallShape.DEFAULT
                ) { onSelect(BallShape.DEFAULT) },
                ShapeOption(
                    stringResource(R.string.shape_round),
                    R.drawable.ic_shape_round,
                    selected == BallShape.ROUND
                ) { onSelect(BallShape.ROUND) },
                ShapeOption(
                    stringResource(R.string.shape_square),
                    R.drawable.ic_shape_square,
                    selected == BallShape.SQUARE
                ) { onSelect(BallShape.SQUARE) },
                ShapeOption(
                    stringResource(R.string.shape_rhombus),
                    R.drawable.ic_shape_rhombus,
                    selected == BallShape.RHOMBUS
                ) { onSelect(BallShape.RHOMBUS) },
                ShapeOption(
                    stringResource(R.string.shape_heart),
                    R.drawable.ic_shape_heart,
                    selected == BallShape.HEART
                ) { onSelect(BallShape.HEART) },
            )
        )
    }
}

@Composable
private fun LogoSection(
    theme: ColorTheme,
    selectedLogoRes: Int?,
    listIconPaths: List<Int>,
    onSelect: (Int?) -> Unit
) {
    SectionCard(colorTheme = theme) {
        SectionLabel(stringResource(R.string.logo))
        SpaceH(8)
        LazyRow(Modifier.fillMaxWidth()) {
            item {
                LogoItem(
                    isSelected = selectedLogoRes == null,
                    onSelect = { onSelect(null) }
                ) {
                    IconView(
                        item = R.drawable.ic_vector_default,
                        icSize = 16,
                        tint = gray,
                        modifier = Modifier.padding(4.Sdp)
                    )
                }
                SpaceW(8)
            }
            itemsIndexed(listIconPaths) { _, value ->
                LogoItem(
                    isSelected = selectedLogoRes == value,
                    onSelect = { onSelect(value) }
                ) {
                    Image(
                        painter = painterResource(value),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentDescription = ""
                    )
                }
                SpaceW(8)
            }
        }
    }
}

@Composable
private fun QualitySection(
    qrQuality: QrQuality,
    theme: ColorTheme,
    hasLogo: Boolean,
    onSelect: (QrQuality) -> Unit
) {
    SectionCard(colorTheme = theme) {
        SectionLabel(stringResource(R.string.qr_quality))
        SpaceH(12.Sdp)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.Sdp)
        ) {
            QrQuality.entries.forEach { quality ->
                QualityOption(
                    text = stringResource(quality.label),
                    isSelected = qrQuality == quality,
                    theme = theme,
                    modifier = Modifier.weight(1f)
                ) { onSelect(quality) }
            }
        }
        if (hasLogo) {
            SpaceH(8.Sdp)
            TextView(
                text = stringResource(R.string.qr_quality_logo_hint),
                font = Font(R.font.rb_400),
                color = gray,
                textSize = 11.Ssp,
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EditQrSaveButton(
    enabled: Boolean,
    theme: ColorTheme,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .paddingTop(10.Sdp)
            .paddingBottom(16.Sdp)
            .fillMaxWidth()
            .paddingHorizontal(12.Sdp)
            .background(
                if (enabled) theme.buttonColor else disabled,
                RoundedCornerShape(50)
            )
            .onClick { if (enabled) onClick() }
            .padding(14.Sdp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        IconView(
            item = R.drawable.ic_app_save,
            tint = if (enabled) theme.colorText else disabledText,
            icSize = 18
        )
        SpaceW(8.Sdp)
        TextView(
            text = stringResource(R.string.save),
            font = R.font.rb_600,
            color = if (enabled) theme.colorText else disabledText,
            textSize = 15.Ssp
        )
    }
}

@Composable
private fun SectionCard(colorTheme: ColorTheme = ColorTheme.BLUE, content: @Composable ColumnScope.() -> Unit) {

    Column(
        Modifier
            .fillMaxWidth()
            .neonState(
                true,
                outerRadius = 5.Sdp,
                colors = listOf(colorTheme.buttonColor.opacity(10f), colorTheme.buttonColor.opacity(10f)),
                shape = RoundedCornerShape(12.Sdp)
            )
            .background(white, RoundedCornerShape(12.Sdp))
            .padding(12.Sdp),
        content = content
    )
}

@Composable
private fun SectionLabel(text: String) {
    TextView(
        text,
        modifier = Modifier.fillMaxWidth(),
        font = Font(R.font.rb_500),
        contentAlignment = Alignment.CenterStart,
        color = black,
        textSize = 12.Ssp,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun TemplateChip(template: QrTemplate, onSelect: (QrTemplate) -> Unit) {
    Column(
        modifier = Modifier
            .width(64.Sdp)
            .onClick { onSelect(template) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(37.Sdp)
                .clip(RoundedCornerShape(14.Sdp))
                .background(template.backgroundColor)
                .border(1.dp, lineColor, RoundedCornerShape(14.Sdp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(24.Sdp)
                    .clip(RoundedCornerShape(8.Sdp))
                    .then(
                        if (template.useGradient) {
                            Modifier.background(
                                Brush.linearGradient(
                                    listOf(template.gradientStartColor, template.gradientEndColor)
                                )
                            )
                        } else {
                            Modifier.background(template.foregroundColor)
                        }
                    )
            )
        }
        SpaceH(6.Sdp)
        TextView(
            text = stringResource(template.label),
            font = Font(R.font.rb_500),
            color = black,
            textSize = 11.Ssp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LogoItem(
    isSelected: Boolean,
    onSelect: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(Modifier.size(40.dp)) {
        Box(
            Modifier
                .size(40.dp)
                .background(white, RoundedCornerShape(12.dp))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) black else lineColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .onClick { onSelect() },
            contentAlignment = Alignment.Center,
            content = content
        )
        if (isSelected) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = -4.dp)
                    .size(16.dp)
                    .background(black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconView(
                    item = Icons.Rounded.CheckCircle,
                    tint = white,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

private data class ShapeOption(
    val label: String,
    @DrawableRes val icon: Int,
    val isSelected: Boolean,
    val onSelect: () -> Unit
)

@Composable
private fun ShapeSection(
    title: String,
    theme: ColorTheme,
    options: List<ShapeOption>
) {
    SectionLabel(title)
    SpaceH(8)

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            ShapeButton(
                text = option.label,
                icon = option.icon,
                isSelected = option.isSelected,
                theme = theme
            ) { option.onSelect() }
        }
    }
}

@Composable
private fun ShapeButton(
    text: String,
    @DrawableRes icon: Int,
    isSelected: Boolean,
    theme: ColorTheme,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                if (isSelected) theme.buttonColor else white,
                RoundedCornerShape(50)
            )
            .border(
                1.dp,
                if (isSelected) theme.buttonColor else lineColor,
                RoundedCornerShape(50)
            )
            .onClick { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconView(
            item = icon,
            tint = if (isSelected) white else black,
            icSize = 14
        )
        SpaceW(6.dp)
        TextView(
            text = text,
            color = if (isSelected) white else black,
            font = Font(R.font.rb_500),
            textSize = 13.Ssp
        )
    }
}

@Composable
private fun QualityOption(
    text: String,
    isSelected: Boolean,
    theme: ColorTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) theme.buttonColor else white,
                RoundedCornerShape(50)
            )
            .border(
                1.dp,
                if (isSelected) theme.buttonColor else lineColor,
                RoundedCornerShape(50)
            )
            .onClick { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        TextView(
            text = text,
            color = if (isSelected) white else black,
            font = Font(R.font.rb_500),
            textSize = 13.Ssp
        )
    }
}

@Composable
private fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, shape = RoundedCornerShape(12.dp))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) black else color.adjustLevel(1),
                    shape = RoundedCornerShape(12.dp)
                )
                .onClick { onSelect() }
        )

        if (isSelected) {
            IconView(
                item = Icons.Rounded.CheckCircle,
                modifier = Modifier.size(24.dp),
                tint = if (color == white) black else white
            )
        }
    }
}

private enum class ColorPickerType {
    FOREGROUND,
    BACKGROUND,
    GRADIENT_START,
    GRADIENT_END,
    BG_GRADIENT_START,
    BG_GRADIENT_END
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "EditQrScreen")
@Composable
fun EditQrScreenPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            val vm = remember { EditQrViewModel(AppRepository(fakeDataHistoryDao)) }
            EditQrScreen(
                viewModel = vm,
                contentEdit = "https://google.com"
            )
        }
    }
}

@Preview(showBackground = true, name = "EditQrTopBar")
@Composable
private fun EditQrTopBarPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            EditQrTopBar(onBack = {})
        }
    }
}

@Preview(showBackground = true, name = "EditQrPreview - empty")
@Composable
private fun EditQrPreviewEmptyPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            EditQrPreview(bitmapQrData = null)
        }
    }
}

@Preview(showBackground = true, name = "TemplatesSection")
@Composable
private fun TemplatesSectionPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(Modifier.padding(12.Sdp)) {
                TemplatesSection(theme = ColorTheme.BLUE, onSelect = {})
            }
        }
    }
}

@Preview(showBackground = true, name = "GradientToggleSection")
@Composable
private fun GradientToggleSectionPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(Modifier.padding(12.Sdp)) {
                GradientToggleSection(
                    label = "Use gradient",
                    checked = true,
                    theme = ColorTheme.BLUE,
                    onCheckedChange = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "SolidColorSection")
@Composable
private fun SolidColorSectionPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(Modifier.padding(12.Sdp)) {
                SolidColorSection(
                    visible = true,
                    title = "Foreground color",
                    theme = ColorTheme.BLUE,
                    colors = listOf(black, white, ColorTheme.BLUE.buttonColor),
                    selected = black,
                    onPick = {},
                    onSelect = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GradientColorSection")
@Composable
private fun GradientColorSectionPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(Modifier.padding(12.Sdp)) {
                GradientColorSection(
                    visible = true,
                    startTitle = "Gradient start",
                    endTitle = "Gradient end",
                    theme = ColorTheme.BLUE,
                    colors = listOf(black, white, ColorTheme.BLUE.buttonColor),
                    startColor = black,
                    endColor = white,
                    onPickStart = {},
                    onPickEnd = {},
                    onSelectStart = {},
                    onSelectEnd = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "ShapeSections")
@Composable
private fun ShapeSectionsPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(
                Modifier.padding(12.Sdp),
                verticalArrangement = Arrangement.spacedBy(10.Sdp)
            ) {
                PixelShapeSection(selected = PixelShape.ROUND, theme = ColorTheme.BLUE, onSelect = {})
                CornerShapeSection(selected = CornerShape.SQUARE, theme = ColorTheme.BLUE, onSelect = {})
                BallShapeSection(selected = BallShape.HEART, theme = ColorTheme.BLUE, onSelect = {})
            }
        }
    }
}

@Preview(showBackground = true, name = "LogoSection")
@Composable
private fun LogoSectionPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(Modifier.padding(12.Sdp)) {
                LogoSection(
                    theme = ColorTheme.BLUE,
                    selectedLogoRes = null,
                    listIconPaths = emptyList(),
                    onSelect = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "QualitySection")
@Composable
private fun QualitySectionPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            Column(Modifier.padding(12.Sdp)) {
                QualitySection(
                    qrQuality = QrQuality.STANDARD,
                    theme = ColorTheme.BLUE,
                    hasLogo = true,
                    onSelect = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "EditQrSaveButton")
@Composable
private fun EditQrSaveButtonPreview() {
    AppViewTheme {
        Surface(color = ColorTheme.BLUE.bgColor) {
            EditQrSaveButton(enabled = true, theme = ColorTheme.BLUE, onClick = {})
        }
    }
}