package com.azg.alf_002_qrscan.ui.language

import com.azg.alf_002_qrscan.ads.AdPreloadManager
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement

class Language1Activity : LanguageActivity() {
    override fun nativePlacement(): NativePlacement = NativePlacement.Language1
    override fun initialize() {
        super.initialize()
        AdPreloadManager.preloadAdConfig(this@Language1Activity, NativePlacement.Onboarding, 3)
    }
}
