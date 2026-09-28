package com.azg.alf_002_qrscan.ui.main.scan

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Environment
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.ads.wrapper.native.NativePlacement
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.base.BaseActivity

import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.database.model.ScanType
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.DialogDeleteConfirm
import com.azg.alf_002_qrscan.ui.component.DialogRenameQr
import com.azg.alf_002_qrscan.utils.QRUtils
import com.azg.alf_002_qrscan.utils.decodeLegacyQrUnicodeEscapes
import com.azg.alf_002_qrscan.utils.saveBitmapToStorage
import com.azg.alf_002_qrscan.utils.shareFileBitmap
import com.create.custom.qr.QrType
import com.google.zxing.BarcodeFormat
import com.lib.ads.application.ads.helper.adnative.provider.rememberNativeAdPreload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.File

class ScanResultActivity : BaseActivity() {
    companion object {
        const val EXTRA_HISTORY_APP = "extra_history_app"
        const val EXTRA_QR_BITMAP_PATH = "extra_qr_bitmap_path"
    }

    private val repository: AppRepository by inject()
    private var historyApp by mutableStateOf(HistoryApp.Empty)
    private var qrBitmap by mutableStateOf<Bitmap?>(null)
    private var isShowDeleteDialog by mutableStateOf(false)
    private var isShowRenameDialog by mutableStateOf(false)
    override fun initialize() {
        val parcelable = intent.getParcelableExtra<HistoryApp>(EXTRA_HISTORY_APP)
        if (parcelable != null) {
            val normalizedParcelable = parcelable.copy(
                content = parcelable.content.decodeLegacyQrUnicodeEscapes()
            )
            if (normalizedParcelable.scanType == ScanType.BARCODE) {
                historyApp = if (parcelable.type != QrType.Barcode) {
                    normalizedParcelable.copy(type = QrType.Barcode).also {
                        lifecycleScope.launch {
                            repository.updateType(QrType.Barcode, parcelable.hisId)
                        }
                    }
                } else {
                    normalizedParcelable
                }
            } else {
                val detectedType = QRUtils.detectQrType(normalizedParcelable.content)
                historyApp = if (detectedType != parcelable.type &&
                    detectedType != QrType.Text
                ) {
                    normalizedParcelable.copy(type = detectedType).also {
                        lifecycleScope.launch {
                            repository.updateType(detectedType, parcelable.hisId)
                        }
                    }
                } else if (parcelable.type == QrType.Demo || parcelable.type == QrType.Text) {
                    normalizedParcelable.copy(type = detectedType)
                } else {
                    normalizedParcelable
                }
            }
        }
        val bitmapPath = intent.getStringExtra(EXTRA_QR_BITMAP_PATH)
        if (bitmapPath != null) {
            val file = File(bitmapPath)
            if (file.exists()) {
                qrBitmap = BitmapFactory.decodeFile(bitmapPath)
            }
        }
        if (qrBitmap == null && historyApp.content.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val format = if (historyApp.scanType == ScanType.BARCODE) {
                    BarcodeFormat.CODE_128
                } else {
                    BarcodeFormat.QR_CODE
                }
                val generatedBitmap = QRUtils.generateBarcode(historyApp.content, format)
                withContext(Dispatchers.Main) {
                    qrBitmap = generatedBitmap
                }
            }
        }
    }

    @Composable
    override fun BoxScope.ContentView() {
        val appTheme by AppPreferences.themeApp.flow.collectAsStateWithLifecycle(ColorTheme.BLUE)

        DialogDeleteConfirm(
            isShowDialog = isShowDeleteDialog,
            onDismiss = { isShowDeleteDialog = false },
            onConfirm = { deleteHistory() }
        )
        DialogRenameQr(
            isShowDialog = isShowRenameDialog,
            initialName = historyApp.qrName,
            theme = appTheme,
            onDismiss = { isShowRenameDialog = false },
            onConfirm = { renameHistory(it) }
        )
        val nativeAdHolder = rememberNativeAdPreload(
            tag = NativePlacement.NativeSmallAll.tag,
            enabled = NativePlacement.NativeSmallAll.canShowAds,
            fallbackAdUnitIds = NativePlacement.NativeSmallAll.listId,
            autoReloadOnResume = true,
        )
        LaunchedEffect(nativeAdHolder) {
            nativeAdHolder.request()
        }

        ScanResultScreen(
            historyApp = historyApp,
            qrBitmap = qrBitmap,
            nativeAdHolder = nativeAdHolder,
            action = object : ScanResultAction {
                override fun onBack() {
                    finish()
                }

                override fun onCopy() {
                    copyToClipboard(historyApp.content)
                }

                override fun onSave() {
                    saveScanBitmap()
                }

                override fun onShare() {
                    shareScanBitmap()
                }

                override fun onRename() {
                    isShowRenameDialog = true
                }

                override fun onFavorite() {
                    toggleFavorite()
                }

                override fun onDelete() {
                    isShowDeleteDialog = true
                }

                override fun onOpenBrowser(url: String) {
                    openUrl(url)
                }

                override fun onCall(phone: String) {
                    callPhone(phone)
                }

                override fun onSendEmail(email: String) {
                    sendEmail(email)
                }

                override fun onSendMessage(phone: String) {
                    sendSms(phone)
                }

                override fun onAddContact() {
                    addContact()
                }

                override fun onConnectWifi() {
                    connectWifi()
                }

                override fun onOpenMap(latitude: String, longitude: String) {
                    openMap()
                }

                override fun onAddEvent() {
                    addEvent()
                }

                override fun onSearch() {
                    searchOnWeb(historyApp.content)
                }
            }
        )
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR Content", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, R.string.copy_success, Toast.LENGTH_SHORT).show()
    }

    private fun shareContent(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivitySafely(
            Intent.createChooser(shareIntent, getString(R.string.share_via))
        )
    }

    private suspend fun ensureScanBitmap(): Bitmap? {
        qrBitmap?.let { return it }
        if (historyApp.content.isBlank()) return null

        val format = if (historyApp.scanType == ScanType.BARCODE) {
            BarcodeFormat.CODE_128
        } else {
            BarcodeFormat.QR_CODE
        }
        return withContext(Dispatchers.IO) {
            QRUtils.generateBarcode(historyApp.content, format)
        }?.also { generated ->
            qrBitmap = generated
        }
    }

    private fun shareScanBitmap() {
        lifecycleScope.launch {
            val bitmap = ensureScanBitmap()
            if (bitmap != null) {
                shareFileBitmap("qr_share.png", bitmap)
            } else {
                Toast.makeText(
                    this@ScanResultActivity,
                    R.string.qr_generation_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveScanBitmap() {
        lifecycleScope.launch {
            val bitmap = ensureScanBitmap()
            if (bitmap == null) {
                Toast.makeText(
                    this@ScanResultActivity,
                    R.string.cancel_save_file_now,
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            val prefix = if (historyApp.scanType == ScanType.BARCODE) "Barcode-Scan" else "Qr-Scan"
            val savedUri = withContext(Dispatchers.IO) {
                saveBitmapToStorage(
                    context = this@ScanResultActivity.applicationContext,
                    bitmap = bitmap,
                    destFileName = "${prefix}_${System.currentTimeMillis()}.png",
                    format = Bitmap.CompressFormat.PNG,
                    environment = Environment.DIRECTORY_PICTURES,
                    folderName = getString(R.string.app_name)
                )
            }
            Toast.makeText(
                this@ScanResultActivity,
                if (savedUri != null) R.string.save_file_success else R.string.cancel_save_file_now,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun toggleFavorite() {
        lifecycleScope.launch {
            historyApp = historyApp.copy(isFavorite = !historyApp.isFavorite)
            repository.updateFavorite(historyApp)
        }
    }

    private fun renameHistory(name: String) {
        lifecycleScope.launch {
            historyApp = historyApp.copy(qrName = name)
            repository.updateName(name, historyApp.hisId)
        }
    }

    private fun deleteHistory() {
        lifecycleScope.launch {
            repository.deleteHistory(historyApp)
            finish()
        }
    }

    private fun startActivitySafely(
        intent: Intent,
        errorMessageRes: Int = R.string.faild_connect_to
    ) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, errorMessageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl(url: String) {
        try {
            var finalUrl = url.trim()
            if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                finalUrl = "https://$finalUrl"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callPhone(phone: String) {
        try {
            val phoneNumber = QRUtils.parsePhone(phone)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(emailContent: String) {
        try {
            val emailModel = QRUtils.parseEmail(emailContent)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailModel.email))
                putExtra(Intent.EXTRA_SUBJECT, emailModel.subject)
                putExtra(Intent.EXTRA_TEXT, emailModel.body)
            }
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms(smsContent: String) {
        try {
            val smsModel = QRUtils.parseSms(smsContent)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${smsModel.phone}")
                putExtra("sms_body", smsModel.message)
            }
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addContact() {
        try {
            val contact = QRUtils.parseContact(historyApp.content)
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
                putExtra(ContactsContract.Intents.Insert.PHONE, contact.phone)
                putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
                putExtra(ContactsContract.Intents.Insert.COMPANY, contact.company)
                putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.jobTitle)
                putExtra(ContactsContract.Intents.Insert.NOTES, contact.note)
                putExtra(ContactsContract.Intents.Insert.POSTAL, contact.address)
            }
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresPermission(Manifest.permission.CHANGE_WIFI_STATE)
    private fun connectWifi() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CHANGE_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val wifi = QRUtils.parseWifi(historyApp.content)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val suggestion = WifiNetworkSuggestion.Builder()
                    .setSsid(wifi.name)
                    .apply {
                        if (wifi.pass.isNotEmpty()) {
                            setWpa2Passphrase(wifi.pass)
                        }
                    }
                    .build()
                val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val status = wifiManager.addNetworkSuggestions(listOf(suggestion))
                if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                    Toast.makeText(
                        this,
                        "${getString(R.string.attemp_value)} ${wifi.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "${getString(R.string.faild_connect_to)} ${wifi.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivitySafely(intent)
                Toast.makeText(
                    this,
                    "${getString(R.string.attemp_value)} ${wifi.name}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMap() {
        try {
            val location = QRUtils.parseLocation(historyApp.content)
            val geoUri =
                "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addEvent() {
        try {
            val event = QRUtils.parseEvent(historyApp.content)
            val startTimeMs = event.parseDateTime(event.startTime).time
            val endTimeMs = event.parseDateTime(event.endTime).time
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.eventName)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeMs)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMs)
            }
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchOnWeb(query: String) {
        try {
            val searchQuery = Uri.encode(query)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/search?q=$searchQuery")
            )
            startActivitySafely(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, R.string.faild_connect_to, Toast.LENGTH_SHORT).show()
        }
    }
}
