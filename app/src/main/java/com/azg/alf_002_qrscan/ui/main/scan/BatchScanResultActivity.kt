package com.azg.alf_002_qrscan.ui.main.scan

import android.content.Intent
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import org.koin.android.ext.android.inject

class BatchScanResultActivity : BaseActivity() {
    companion object {
        const val EXTRA_BATCH_ID = "extra_batch_id"
    }

    private val repository: AppRepository by inject()
    private var batchId = ""

    override fun initialize() {
        batchId = intent.getStringExtra(EXTRA_BATCH_ID).orEmpty()
        if (batchId.isEmpty()) finish()
    }

    @Composable
    override fun BoxScope.ContentView() {
        val batchResultsFlow = remember(batchId) { repository.getByBatchId(batchId) }
        val batchResults = batchResultsFlow.collectAsStateWithLifecycle(emptyList()).value
        val nativeAdHolder = rememberNativeAdPreload(
            enabled = NativePlacement.NativeMediumAll.canShowAds,
            tag = NativePlacement.NativeMediumAll.tag,
            fallbackAdUnitIds = NativePlacement.NativeMediumAll.listId,
            autoReloadOnResume = true,
        )
        LaunchedEffect(nativeAdHolder) {
            nativeAdHolder.request()
        }
        BatchScanResultScreen(
            nativeAdHolder = nativeAdHolder,
            results = batchResults,
            onBack = ::finish,
            onItemClick = ::openScanResult
        )
    }

    private fun openScanResult(item: HistoryApp) {
        startActivity(
            Intent(this, ScanResultActivity::class.java).apply {
                putExtra(ScanResultActivity.EXTRA_HISTORY_APP, item)
                if (item.scanImage.isNotEmpty()) {
                    putExtra(ScanResultActivity.EXTRA_QR_BITMAP_PATH, item.scanImage)
                }
            }
        )
    }
}
