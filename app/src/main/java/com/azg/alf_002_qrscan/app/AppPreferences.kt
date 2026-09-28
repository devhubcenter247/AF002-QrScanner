package com.azg.alf_002_qrscan.app

import com.azg.alf_002_qrscan.ui.component.ColorTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext


object AppPreferences: KoinComponent {
    private val sharedPreference: SharedPreference by  inject()
    var languageCode =  sharedPreference.observable("languageCode", "en")
        private set
    var isFinishFirstFlow by sharedPreference.boolean("isFinishFirstFlow", false)
    var firstOpenApp by sharedPreference.int("firstOpenApp", 0)
    fun isUfo(): Boolean {
        return firstOpenApp == 1
    }

    val themeApp = sharedPreference.observable("themeApp", ColorTheme.BLUE)
    val isRatingApp = sharedPreference.observable("isRatingApp", false)
    val isEnableSound = sharedPreference.observable("isEnableSound", false)
    val isEnableVibrate = sharedPreference.observable("isEnableVibrate", false)

    fun setLocale(language: String = "en") {
        languageCode.value = language
    }
}
