package com.azg.alf_002_qrscan.ui.main

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.inappupdate.AppUpdateWrapper
import com.app.inappupdate.InAppUpdateManager
import com.app.inappupdate.UpdateDialog
import com.app.inappupdate.UpdateDialogState
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.banner.BannerPlacement
import com.azg.alf_002_qrscan.ads.wrapper.banner.createBannerAdConfig
import com.azg.alf_002_qrscan.ads.wrapper.interstitial.InterstitialAdConfig
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.remote.remoteConfig
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.DialogQuitApp
import com.azg.alf_002_qrscan.ui.iap.IapActivity
import com.azg.alf_002_qrscan.ui.language.getLanguageArray
import com.azg.alf_002_qrscan.ui.main.home.CreateScreen
import com.azg.alf_002_qrscan.ui.main.home.FavoriteScreen
import com.azg.alf_002_qrscan.ui.main.home.HistoryScreen
import com.azg.alf_002_qrscan.ui.main.home.ScanQrScreen
import com.azg.alf_002_qrscan.ui.main.home.ScreenRouter
import com.azg.alf_002_qrscan.ui.main.home.SettingScreen
import com.azg.alf_002_qrscan.ui.main.setting.AboutUsActivity
import com.azg.alf_002_qrscan.ui.main.setting.HelpActivity
import com.azg.alf_002_qrscan.ui.main.setting.LanguageSettingActivity
import com.azg.alf_002_qrscan.ui.main.setting.SelectThemeActivity
import com.azg.alf_002_qrscan.viewmodel.HomeViewModel
import com.azg.alf_002_qrscan.widget.BottomNavigationBar
import com.azg.alf_002_qrscan.widget.animComposable
import com.azg.alf_002_qrscan.widget.navigationTo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.lib.ads.application.ads.helper.banner.BannerAdCard
import com.lib.ads.application.ads.helper.banner.BannerAdConfig
import com.lib.ads.application.ads.helper.banner.DefaultBannerAdLoading
import com.lib.ads.application.ads.helper.banner.createBannerAdHolder
import com.sdp.ssp.android.Sdp
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {
    override fun initialize() {
        lifecycle.addObserver(updateManager)
        updateManager.registerLauncher(updateLauncher)
        updateManager.checkForUpdate(
            remoteConfig.buildUpdateConfig(
                defaultTitle = getString(R.string.new_ver_avaliable),
                defaultMessage = getString(R.string.please_update_to_use),
                defaultUpdateText = getString(R.string.update_now),
                defaultDismissText = getString(R.string.latter),
            )
        )
        AppPreferences.isFinishFirstFlow = true
        bannerAdConfig.request()
        InterstitialAdConfig.loadInterAll(this)
    }

    private val viewModel: HomeViewModel by inject()
    private val updateManager by lazy {
        InAppUpdateManager(
            appUpdateWrapper = AppUpdateWrapper(AppUpdateManagerFactory.create(this))
        )
    }
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        updateManager.onUpdateFlowResult(result.resultCode)
    }

    private val bannerAdConfig by lazy {
        createBannerAdConfig(
            this@MainActivity,
            placement = BannerPlacement.BannerAll,
            lifecycleOwner = this@MainActivity
        )
    }

    @Composable
    override fun BoxScope.ContentView() {
        var isShowQuitDialog by rememberSaveable { mutableStateOf(false) }
        val navController = rememberNavController()
        val currentTheme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)
        val isRateApp by AppPreferences.isRatingApp.flow.collectAsStateWithLifecycle(false)
        val currentLanguageCode by AppPreferences.languageCode.flow.collectAsStateWithLifecycle(
            AppPreferences.languageCode.value
        )
        val currentLanguage by remember {
            derivedStateOf {
                getLanguageArray().find { it.code == currentLanguageCode } ?: getLanguageArray()[0]
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth(1f)
                    .weight(1f), contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.BottomCenter
                )
                {
                    NavHost(
                        modifier = Modifier.matchParentSize(),
                        navController = navController,
                        startDestination = ScreenRouter.MainScreen.CreateScreen.route
                    ) {
                        animComposable(ScreenRouter.MainScreen.CreateScreen.route) {
                            CreateScreen(
                                currentTheme = currentTheme,
                                activity = this@MainActivity,
                                onQuestion = { launchActivity<HelpActivity>() }
                            ) { isShowQuitDialog = true }
                        }
                        animComposable(ScreenRouter.MainScreen.FavoriteScreen.route) {
                            FavoriteScreen(
                                viewModel = viewModel,
                                activity = this@MainActivity,

                                ) { isShowQuitDialog = true }
                        }
                        animComposable(ScreenRouter.MainScreen.ScannerScreen.route) {
                            ScanQrScreen(
                                activity = this@MainActivity,
                                theme = currentTheme,
                            ) { isShowQuitDialog = true }
                        }
                        animComposable(ScreenRouter.MainScreen.HistoryScreen.route) {
                            HistoryScreen(
                                viewModel = viewModel,
                                activity = this@MainActivity,

                                ) { isShowQuitDialog = true }
                        }
                        animComposable(ScreenRouter.MainScreen.SettingScreen.route) {
                            SettingScreen(
                                colorTheme = currentTheme,
                                isRateApp = isRateApp,
                                onHelp = {
                                    launchActivity<HelpActivity>()
                                },
                                onIap = {
                                    launchActivity<IapActivity>()
                                },
                                currentLanguage = currentLanguage,
                                activity = this@MainActivity,
                                gotoSettingTheme = {
                                    launchActivity<SelectThemeActivity>()
                                },
                                gotoLangSetting = {
                                    launchActivity<LanguageSettingActivity>()
                                },
                                gotoAboutUs = {
                                    launchActivity<AboutUsActivity>()
                                },
                                onBack = { isShowQuitDialog = true }
                            )
                        }
                    }
                }


                BottomNavigationBar(
                    navHostController = navController, currentTheme, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    navController.navigationTo(route = ScreenRouter.MainScreen.ScannerScreen.route)
                }
            }
            BannerAdCard(
                holder = bannerAdConfig,
                loading = {
                    DefaultBannerAdLoading()
                }
            )
        }


        DialogQuitApp(
            isShowDialog = isShowQuitDialog,
            currentTheme,
            onDismiss = { isShowQuitDialog = false }
        ) {
            finishAffinity()
        }
        val dialogState by updateManager.dialogState.collectAsStateWithLifecycle()
        if (dialogState is UpdateDialogState.Visible) {
            UpdateDialog(
                state = dialogState as UpdateDialogState.Visible,
                onUpdate = { updateManager.startUpdateFlow() },
                onDismiss = { updateManager.dismissDialog() }
            )
        }
    }

}

@Composable
private fun PreviewScreenBlock(title: String, tint: Color) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.White)
                .padding(24.dp)
        ) {
            androidx.compose.material3.Text(text = title, color = tint)
        }
    }
}

@Composable
private fun MainScreenPreviewScaffold(theme: ColorTheme) {
    val navController = rememberNavController()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            NavHost(
                modifier = Modifier.matchParentSize(),
                navController = navController,
                startDestination = ScreenRouter.MainScreen.ScannerScreen.route
            ) {
                composable(ScreenRouter.MainScreen.CreateScreen.route) {
                    PreviewScreenBlock("Create Screen", theme.buttonColor)
                }
                composable(ScreenRouter.MainScreen.FavoriteScreen.route) {
                    PreviewScreenBlock("Favorite Screen", theme.buttonColor)
                }
                composable(ScreenRouter.MainScreen.ScannerScreen.route) {
                    PreviewScreenBlock("Scanner Screen", theme.buttonColor)
                }
                composable(ScreenRouter.MainScreen.HistoryScreen.route) {
                    PreviewScreenBlock("History Screen", theme.buttonColor)
                }
                composable(ScreenRouter.MainScreen.SettingScreen.route) {
                    PreviewScreenBlock("Setting Screen", theme.buttonColor)
                }
            }
        }
        BottomNavigationBar(
            navHostController = navController, theme, modifier = Modifier
                .fillMaxWidth()

        )
    }
}

@Preview(name = "Main (BLUE)", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
private fun MainPreviewBlue() {
    AppViewTheme {
        Surface { MainScreenPreviewScaffold(theme = ColorTheme.BLUE) }
    }
}

@Preview(name = "Main (PINK)", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
private fun MainPreviewPink() {
    AppViewTheme {
        Surface { MainScreenPreviewScaffold(theme = ColorTheme.PINK) }
    }
}

@Preview(name = "Main (GREEN)", showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
private fun MainPreviewGreen() {
    AppViewTheme {
        Surface { MainScreenPreviewScaffold(theme = ColorTheme.GREEN) }
    }
}
