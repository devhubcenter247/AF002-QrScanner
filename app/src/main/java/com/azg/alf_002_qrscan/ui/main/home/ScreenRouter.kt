package com.azg.alf_002_qrscan.ui.main.home

object ScreenRouter {
    sealed class MainScreen(var route: String) {
        data object CreateScreen : MainScreen("createScreen")
        data object FavoriteScreen : MainScreen("favoriteScreen")
        data object ScannerScreen : MainScreen("scannerScreen")
        data object HistoryScreen : MainScreen("historyScreen")
        data object SettingScreen : MainScreen("settingScreen")
        data object ScanResultScreen : MainScreen("ScanResultScreen")
    }
}