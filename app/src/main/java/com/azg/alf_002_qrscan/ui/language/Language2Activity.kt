package com.azg.alf_002_qrscan.ui.language

import com.azg.alf_002_qrscan.ads.AdPreloadManager
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement

class Language2Activity : LanguageActivity() {
    override fun nativePlacement(): NativePlacement = NativePlacement.Language2
    override fun initialize() {
        super.initialize()
        AdPreloadManager.preloadAdConfig(this@Language2Activity, NativePlacement.OnboardingFull, 1)
    }
}
