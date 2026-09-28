package com.azg.alf_002_qrscan.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.PhonePreview
import com.azg.alf_002_qrscan.style.gray
import com.azg.alf_002_qrscan.style.lightGrey
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.paddingBottom
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.red
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.main.home.ScreenRouter
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp

private val BottomNavigationFont = FontFamily(Font(R.font.rb_500))

fun getIconByThem(themeApp: ColorTheme,bottomNavItems: BottomNavItems): Pair<Int,Int>{
    return when(bottomNavItems){
        BottomNavItems.HOME -> {
            when(themeApp){
                ColorTheme.BLUE -> R.drawable.hom_enb_1 to R.drawable.hom_dis_1
                ColorTheme.PINK -> R.drawable.hom_enb_1_2 to R.drawable.hom_dis_1
                ColorTheme.GREEN -> R.drawable.hom_enb_1_3 to R.drawable.hom_dis_1
                else -> R.drawable.hom_enb_1_4 to R.drawable.hom_dis_1
            }
        }
        BottomNavItems.FAVORITE -> {
            when(themeApp){
                ColorTheme.BLUE -> R.drawable.hom_enb_2 to R.drawable.hom_dis_2
                ColorTheme.PINK -> R.drawable.hom_enb_2_2 to R.drawable.hom_dis_2
                ColorTheme.GREEN -> R.drawable.hom_enb_2_3 to R.drawable.hom_dis_2
                else -> R.drawable.hom_enb_2_4 to R.drawable.hom_dis_2
            }
        }
        BottomNavItems.HISTORY -> {
            when(themeApp){
                ColorTheme.BLUE -> R.drawable.hom_enb_3 to R.drawable.hom_dis_3
                ColorTheme.PINK -> R.drawable.hom_enb_3_2 to R.drawable.hom_dis_3
                ColorTheme.GREEN -> R.drawable.hom_enb_3_3 to R.drawable.hom_dis_3
                else -> R.drawable.hom_enb_3_4 to R.drawable.hom_dis_3
            }
        }
        else -> {
           when (themeApp) {
               ColorTheme.BLUE -> R.drawable.hom_enb_4 to R.drawable.hom_dis_4
               ColorTheme.PINK -> R.drawable.hom_enb_4_2 to R.drawable.hom_dis_4
               ColorTheme.GREEN -> R.drawable.hom_enb_4_3 to R.drawable.hom_dis_4
               else -> R.drawable.hom_enb_4_4 to R.drawable.hom_dis_4
           }
        }
    }
}
enum class BottomNavItems(
    val index: Int,
    val route: String,
    val title: Int,
) {
    HOME(
        index = 0,
        route = ScreenRouter.MainScreen.CreateScreen.route,
        title = R.string.create,
    ),
    FAVORITE(
        index = 1,
        route = ScreenRouter.MainScreen.FavoriteScreen.route,
        title = R.string.favorite,
    ),
    HISTORY(
        index = 3,
        route = ScreenRouter.MainScreen.HistoryScreen.route,
        title = R.string.history,
    ),
    SETTING(
        index = 4,
        route = ScreenRouter.MainScreen.SettingScreen.route,
        title = R.string.setting,
    ),
}

class Bar1Shape(
    private val bumpCenterX: Float? = null,
    private val bumpRadius: Dp = 40.dp,
    private val bumpHeight: Dp = 28.dp,
    private val shoulderWidth: Dp = 22.dp,
    private val cornerRadius: Dp = 26.dp,
    private val minBodyHeight: Dp = 12.dp,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Generic(createPath(size, density))
    }

    private fun createPath(
        size: Size,
        density: Density,
    ): Path = with(density) {
        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) {
            return@with Path()
        }
        val bodyTop = bumpHeight
            .toPx()
            .coerceIn(0f, (height - minBodyHeight.toPx()).coerceAtLeast(0f))

        val bodyHeight = height - bodyTop

        val corner = cornerRadius
            .toPx()
            .coerceAtMost(bodyHeight / 2f)

        val radius = bumpRadius
            .toPx()
            .coerceAtMost(width * 0.5f)

        val shoulder = shoulderWidth
            .toPx()
            .coerceAtMost(width * 0.5f)

        val minimumCenterX = corner + radius + shoulder
        val maximumCenterX = width - corner - radius - shoulder
        val requestedCenterX = bumpCenterX ?: (width / 2f)

        val centerX = if (minimumCenterX <= maximumCenterX) {
            requestedCenterX.coerceIn(
                minimumValue = minimumCenterX,
                maximumValue = maximumCenterX,
            )
        } else {
            width / 2f
        }

        val shoulderStart = centerX - radius - shoulder
        val shoulderEnd = centerX + radius + shoulder
        val bumpControlWeight = 0.6f

        Path().apply {
            moveTo(0f, height)

            lineTo(0f, bodyTop + corner)

            cubicTo(
                0f,
                bodyTop + corner * 0.45f,
                corner * 0.45f,
                bodyTop,
                corner,
                bodyTop,
            )

            lineTo(shoulderStart*1.1f, bodyTop)
            cubicTo(
                shoulderStart*1.05f + (centerX - shoulderStart) * bumpControlWeight,
                bodyTop,
                centerX - radius * bumpControlWeight ,
                0f,
                centerX,
                -2f,
            )

            cubicTo(
                centerX + radius * bumpControlWeight,
                0f,
                shoulderEnd - (shoulderEnd - centerX) * bumpControlWeight,
                bodyTop,
                shoulderEnd*.9f,
                bodyTop,
            )

            lineTo(width - corner, bodyTop)

            cubicTo(
                width - corner * 0.45f,
                bodyTop,
                width,
                bodyTop + corner * 0.45f,
                width,
                bodyTop + corner,
            )

            lineTo(width, height - corner)

            cubicTo(
                width,
                height - corner * 0.45f,
                width - corner * 0.45f,
                height,
                width - corner,
                height,
            )

            lineTo(corner, height)

            cubicTo(
                corner * 0.45f,
                height,
                0f,
                height - corner * 0.45f,
                0f,
                height - corner,
            )

            close()
        }
    }
}

@Composable
fun BottomNavigationBar(
    navHostController: NavHostController,
    themeApp: ColorTheme,
    modifier: Modifier = Modifier,
    scanButtonSize: Dp = 58.Sdp,
    onScanClick: () -> Unit = {},
) {
    val navBackStackEntry by navHostController.currentBackStackEntryFlow
        .collectAsStateWithLifecycle(
            initialValue = navHostController.currentBackStackEntry,
        )

    val currentDestination = navBackStackEntry?.destination

    val selectedItem = remember(currentDestination) {
        BottomNavItems.entries.firstOrNull { item ->
            currentDestination.isSelected(item.route)
        }
    }
    BottomNavigationBarContent(
        selectedItem = selectedItem,
        themeApp = themeApp,
        modifier = modifier,
        scanButtonSize = scanButtonSize,
        onScanClick = onScanClick,
        onItemClick = { item ->
            if (!currentDestination.isSelected(item.route)) {
                navHostController.navigationTo(item.route)
            }
        },
    )
}

@Composable
fun BottomNavigationBarContent(
    selectedItem: BottomNavItems?,
    themeApp: ColorTheme,
    modifier: Modifier = Modifier,
    barHeight: Dp = 62.Sdp,
    bumpHeight: Dp = 20.Sdp,
    scanButtonSize: Dp = 56.Sdp,
    onItemClick: (BottomNavItems) -> Unit = {},
    onScanClick: () -> Unit = {},
) {
    val safeBumpHeight = bumpHeight.coerceAtLeast(0.dp)

    val totalHeight = barHeight + safeBumpHeight
    val barShape = remember(
        safeBumpHeight,
        scanButtonSize,
    ) {
        Bar1Shape(
            bumpRadius = scanButtonSize / 2 + 16.Sdp,
            bumpHeight = safeBumpHeight,
            shoulderWidth = 20.Sdp,
            cornerRadius = 8.Sdp,
            minBodyHeight = 12.Sdp,
        )
    }
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight)
                .shadow(
                    elevation = 8.Sdp,
                    shape = barShape,
                    clip = false,
                    ambientColor = themeApp.buttonColor.copy(alpha = 0.06f),
                    spotColor = themeApp.buttonColor.copy(alpha = 0.4f),
                )
                .background(
                    color = white,
                    shape = barShape,
                )
                .border(
                    width = 1.Sdp,
                    color = themeApp.buttonColor.copy(alpha = 0.15f),
                    shape = barShape,
                ).paddingTop(22.Sdp).paddingBottom(2.Sdp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavigationItem(
                item = BottomNavItems.HOME,
                selected = selectedItem == BottomNavItems.HOME,
                themeApp = themeApp,
                onClick = {
                    onItemClick(BottomNavItems.HOME)
                },
            )

            BottomNavigationItem(
                item = BottomNavItems.FAVORITE,
                selected = selectedItem == BottomNavItems.FAVORITE,
                themeApp = themeApp,
                onClick = {
                    onItemClick(BottomNavItems.FAVORITE)
                },
            )

            Spacer(modifier = Modifier.width(scanButtonSize))

            BottomNavigationItem(
                item = BottomNavItems.HISTORY,
                selected = selectedItem == BottomNavItems.HISTORY,
                themeApp = themeApp,
                onClick = {
                    onItemClick(BottomNavItems.HISTORY)
                },
            )

            BottomNavigationItem(
                item = BottomNavItems.SETTING,
                selected = selectedItem == BottomNavItems.SETTING,
                themeApp = themeApp,
                onClick = {
                    onItemClick(BottomNavItems.SETTING)
                },
            )
        }

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 12.Sdp,
                    shape = CircleShape,
                    ambientColor = themeApp.buttonColor.copy(alpha = 0.18f),
                    spotColor = themeApp.buttonColor.copy(alpha = 0.1f),
                )
                .clip(CircleShape)
                .background(
                    color = white,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(
                    when (themeApp) {
                        ColorTheme.BLUE -> R.drawable.ic_scan_view_1
                        ColorTheme.PINK -> R.drawable.ic_scan_view_2
                        ColorTheme.GREEN -> R.drawable.ic_scan_view_3
                        else -> R.drawable.ic_scan_view_4
                    }
                ),
                contentDescription = stringResource(R.string.scanner),
                modifier = Modifier
                    .size(scanButtonSize)
                    .shadow(
                        elevation = 10.Sdp,
                        shape = CircleShape,
                        clip = false,
                        ambientColor = themeApp.buttonColor.copy(alpha = 0.22f),
                        spotColor = themeApp.buttonColor.copy(alpha = 0.25f),
                    )
                    .onClick {
                        onScanClick()
                    }
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    item: BottomNavItems,
    selected: Boolean,
    themeApp: ColorTheme,
    onClick: () -> Unit,
) {
    val tintColor = if (selected) themeApp.buttonColor else gray

    val backgroundColor = if (selected) {
        themeApp.buttonColor.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val icon by remember (selected,item){
        if(selected){
            mutableIntStateOf(getIconByThem(themeApp,item).first)
        } else{
            mutableIntStateOf(getIconByThem(themeApp,item).second)
        }

    }

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.Sdp, vertical = 8.Sdp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .paddingVertical(3.Sdp)
            .onClick(
                enable = !selected,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = stringResource(item.title),
                tint = tintColor,
                modifier = Modifier.size(20.Sdp),
            )

            Spacer(
                modifier = Modifier.height(2.Sdp),
            )

            Text(
                text = stringResource(item.title),
                maxLines = 1,
                fontSize = 10.Ssp,
                fontWeight = FontWeight.Medium,
                fontFamily = BottomNavigationFont,
                color = tintColor,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )
        }
    }
}

private fun NavDestination?.isSelected(
    route: String,
): Boolean {
    return this?.hierarchy?.any { destination ->
        destination.route == route
    } == true
}

@Preview(
    name = "Bottom Navigation - Create",
    showBackground = true,
    backgroundColor = 0xFFF5F8FC,
    widthDp = 390,
    heightDp = 130,
)
@Composable
private fun BottomNavigationCreatePreview() {
    AppViewTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp,
                ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            BottomNavigationBarContent(
                selectedItem = BottomNavItems.HOME,
                themeApp = ColorTheme.BLUE,
            )
        }
    }
}

@PhonePreview
@Composable
private fun BottomNavigationFavoritePreview() {
    AppViewTheme {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 12.dp,
                    vertical = 10.dp,
                ),
        ) {
            BottomNavigationBarContent(
                selectedItem = BottomNavItems.FAVORITE,
                themeApp = ColorTheme.PINK,
            )
            BottomNavigationBarContent(
                selectedItem = BottomNavItems.HISTORY,
                themeApp = ColorTheme.GREEN,
            )
            BottomNavigationBarContent(
                selectedItem = BottomNavItems.SETTING,
                themeApp = ColorTheme.DENIM,
            )
        }
    }
}
