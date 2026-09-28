package com.azg.alf_002_qrscan.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.database.model.ScanType
import com.azg.alf_002_qrscan.model.ContactModel
import com.azg.alf_002_qrscan.model.EmailModel
import com.azg.alf_002_qrscan.model.EventModel
import com.azg.alf_002_qrscan.model.LocationModel
import com.azg.alf_002_qrscan.model.MessageModel
import com.azg.alf_002_qrscan.model.WifiModel
import com.azg.alf_002_qrscan.model.isValidEmail
import com.azg.alf_002_qrscan.model.isValidPhoneNumber
import com.azg.alf_002_qrscan.model.toQrString
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.ifNotEmpty
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.utils.BarcodeType
import com.azg.alf_002_qrscan.utils.BarcodeUtils
import com.azg.alf_002_qrscan.utils.isWithinQrContentLimit
import com.azg.alf_002_qrscan.utils.limitToQrContent
import com.azg.alf_002_qrscan.utils.saveBitmapToInternalStorage
import com.create.custom.qr.QrErrorCorrectionLevel
import com.create.custom.qr.QrType
import com.create.custom.qr.vector.QrCodeDrawable
import com.create.custom.qr.vector.createQrVectorOptions
import com.create.custom.qr.vector.style.QrVectorBallShape
import com.create.custom.qr.vector.style.QrVectorColor
import com.create.custom.qr.vector.style.QrVectorFrameShape
import com.create.custom.qr.vector.style.QrVectorPixelShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class CreateViewModel(
    private val repo: AppRepository
) : ViewModel() {
    private val _colorTheme = MutableStateFlow(ColorTheme.DENIM)
    val colorTheme = _colorTheme.asStateFlow()
    private val _isEnableCreateState = MutableStateFlow(false)
    val isEnableCreate = _isEnableCreateState.asStateFlow()

    init {
        viewModelScope.launch {
            AppPreferences.themeApp.flow.collect {
                _colorTheme.value = it
            }
        }
    }

    private val options by lazy {
        createQrVectorOptions {
            padding = .1f
            fourthEyeEnabled = false
            errorCorrectionLevel = QrErrorCorrectionLevel.High
            background {
                color = QrVectorColor.Solid(white.toArgb())
            }
            colors {
                dark = QrVectorColor
                    .Solid(black.toArgb())
            }
            shapes {
                darkPixel = QrVectorPixelShape.Default
                ball = QrVectorBallShape.Default
                frame = QrVectorFrameShape.Default
            }
        }
    }
    private val _qrValueBitmap = MutableStateFlow<Bitmap?>(null)
    val qrValueBitmap = _qrValueBitmap.asStateFlow()

    // Store the current history ID for updating after edit
    private val _currentHistoryId = MutableStateFlow<Int?>(null)
    val currentHistoryId = _currentHistoryId.asStateFlow()
    private val isGeneratingQr = AtomicBoolean(false)

    fun generateQr(
        input: String,
        context: Context,
        qrType: QrType,
        onComplete: (Boolean) -> Unit
    ) {
        if (input.isBlank() || !input.isWithinQrContentLimit()) {
            onComplete(false)
            return
        }
        if (!isGeneratingQr.compareAndSet(false, true)) return

        viewModelScope.launch(Dispatchers.IO) {
            val success = try {
                QrCodeDrawable(
                    data = { input },
                    options = options,
                    charset = Charsets.UTF_8
                ).toBitmap(1024, 1024).also { bitmap ->
                    val saveImage = saveBitmapToInternalStorage(
                        context = context.applicationContext,
                        bitmap = bitmap,
                        baseFileName = "Save_Image_${System.currentTimeMillis()}"
                    )
                    _qrValueBitmap.value = bitmap
                    val history = HistoryApp(
                        scanType = ScanType.QRCODE,
                        content = input,
                        scanImage = saveImage?.absolutePath ?: "",
                        type = qrType,
                        qrName = "",
                        createType = CreateType.CREATE,
                        isFavorite = false,
                        isDelete = false,
                    )
                    val insertedId = repo.insertWithId(history)
                    _currentHistoryId.value = insertedId.toInt()
                }
                true
            } catch (e: Exception) {
                Log.e("CreateViewModel", "Could not generate QR code", e)
                false
            } finally {
                isGeneratingQr.set(false)
            }
            withContext(Dispatchers.Main) {
                onComplete(success)
            }
        }
    }

    fun deleteHistory(historyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteHistory(historyId)
        }
    }

    private val phoneRegex = Regex("^\\+?[0-9]{7,15}\$")
    val urlRegex = Regex(
        pattern = "^(https?|ftp)://[\\w.-]+(?:\\.[\\w.-]+)+[/#?]?.*\$",
        option = RegexOption.IGNORE_CASE
    )
    // create text
    private val _createQrText = MutableStateFlow("")
    val createQrText = _createQrText.asStateFlow()

    fun setInputTextQr(value: String) {
        val limitedValue = value.limitToQrContent()
        _createQrText.value = limitedValue
        _isEnableCreateState.value = limitedValue.isNotBlank()
    }
    // create phone
    private val _createQrPhone = MutableStateFlow("")
    val createQrPhone = _createQrPhone.asStateFlow()

    fun setInputPhone(value: String) {
        _createQrPhone.value = value
        _isEnableCreateState.value = value.isNotBlank() && value.matches(phoneRegex)
    }
    // create Ulr
    // create phone
    private val _createQrUrl = MutableStateFlow("")
    val createQrUrl = _createQrUrl.asStateFlow()

    fun setInputUrl(value: String) {
        val limitedValue = value.limitToQrContent()
        _createQrUrl.value = limitedValue
        _isEnableCreateState.value = limitedValue.isNotBlank() && limitedValue.matches(urlRegex)
    }

    private val _emailCreateModel = MutableStateFlow<EmailModel>(EmailModel())
    val emailModel = _emailCreateModel.asStateFlow()
    fun setEmailModel(emailModel: EmailModel) {
        if (!emailModel.toQrString().isWithinQrContentLimit()) return
        _emailCreateModel.value = emailModel
        validateForm()
    }

    private fun validateForm() {
        val current = _emailCreateModel.value
        _isEnableCreateState.value = current.email.isValidEmail() &&
                current.subject.isNotBlank() &&
                current.body.isNotBlank() &&
                current.cc.all { it.isValidEmail() } &&
                current.toQrString().isWithinQrContentLimit()
    }

    private val _emailMessageModel = MutableStateFlow<MessageModel>(MessageModel())
    val messageModel = _emailMessageModel.asStateFlow()
    fun setMessageModel(messageModel: MessageModel) {
        if (!messageModel.toQrString().isWithinQrContentLimit()) return
        _emailMessageModel.value = messageModel
        _isEnableCreateState.value = messageModel.phone.isValidPhoneNumber() &&
                messageModel.subject.isNotBlank() &&
                messageModel.message.isNotBlank()
    }

    private val _contactModel = MutableStateFlow(ContactModel())
    val contactModel = _contactModel.asStateFlow()
    fun setContactModel(contact: ContactModel) {
        if (!contact.toQrString().isWithinQrContentLimit()) return
        _contactModel.value = contact
        _isEnableCreateState.value =
            contact.name.isNotEmpty() && contact.phone.ifNotEmpty({ phone ->
                phone.isValidPhoneNumber()
            }, {
                true
            }) && contact.name.ifNotEmpty({ name ->
                name.length >= 2
            }, {
                false
            }) && contact.email.ifNotEmpty({ email ->
                email.isValidEmail()
            }, {
                true
            })
    }

    private val _locationModel = MutableStateFlow(LocationModel())
    val locationModel = _locationModel.asStateFlow()

    fun setLocationModel(model: LocationModel) {
        _locationModel.value = model
        _isEnableCreateState.value = isValidLatLng(model)
    }

    private fun isValidLatLng(model: LocationModel): Boolean {
        val lat = model.latitude.toDoubleOrNull()
        val lng = model.longitude.toDoubleOrNull()
        return lat != null && lng != null &&
                lat in -90.0..90.0 &&
                lng in -180.0..180.0
    }

    private val _wifiModel = MutableStateFlow(WifiModel())
    val wifiModel = _wifiModel.asStateFlow()
    fun setWifiModel(model: WifiModel) {
        if (!model.toQrString().isWithinQrContentLimit()) return
        _wifiModel.value = model
        _isEnableCreateState.value = isValidWifi(model)
    }

    private fun isValidWifi(model: WifiModel): Boolean {
        if (model.name.isBlank()) return false
        if (model.security != "NOPASS" && model.pass.isBlank()) return false
        return true
    }

    private val _eventModel = MutableStateFlow(EventModel())
    val eventModel = _eventModel.asStateFlow()
    fun setEventModel(model: EventModel) {
        if (!model.toQrString().isWithinQrContentLimit()) return
        _eventModel.value = model
        _isEnableCreateState.value =
            model.eventName.isNotEmpty() && model.startTime.isNotEmpty() && model.endTime.isNotEmpty()
    }

    // Barcode creation
    private val _barcodeBitmap = MutableStateFlow<Bitmap?>(null)
    val barcodeBitmap = _barcodeBitmap.asStateFlow()

    private val _barcodeError = MutableStateFlow<String?>(null)
    val barcodeError = _barcodeError.asStateFlow()
    val barcodeValue = MutableStateFlow(Pair("",""))

    fun createBarcodeValue(
        value: String,
        barcodeType: BarcodeType,
        context: Context,
        qrName: String = "",
        callback: (Bitmap?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Validate input
                val (isValid, errorMessage) = BarcodeUtils.validateBarcodeInput(value, barcodeType)
                if (!isValid) {
                    withContext(Dispatchers.Main) {
                        _barcodeError.value = errorMessage
                        callback(null)
                    }
                    return@launch
                }

                // Prepare value with checksum if needed
                val preparedValue = BarcodeUtils.prepareBarcodeValue(value, barcodeType)

                // Create barcode bitmap
                val bitmap = BarcodeUtils.createBarcode(preparedValue, barcodeType.zxingFormat)

                if (bitmap != null) {
                    // Save to internal storage
                    val saveImage = saveBitmapToInternalStorage(
                        context = context.applicationContext,
                        bitmap = bitmap,
                        baseFileName = "Barcode_${System.currentTimeMillis()}"
                    )
                    barcodeValue.value = Pair(preparedValue,barcodeType.name)
                    // Save to database
                    val history = HistoryApp(
                        scanType = ScanType.BARCODE,
                        content = preparedValue,
                        scanImage = saveImage?.absolutePath ?: "",
                        type = QrType.Barcode,
                        qrName = qrName,
                        createType = CreateType.CREATE,
                        isFavorite = false,
                        isDelete = false
                    )
                    // Use insertWithId to get the ID for later updates
                    val insertedId = repo.insertWithId(history)
                    _currentHistoryId.value = insertedId.toInt()

                    withContext(Dispatchers.Main) {
                        _barcodeBitmap.value = bitmap
                        _barcodeError.value = null
                        callback(bitmap)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _barcodeError.value = "Failed to create barcode"
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _barcodeError.value = e.message ?: "Unknown error"
                    callback(null)
                }
            }
        }
    }

    fun validateBarcodeInput(input: String, barcodeType: BarcodeType): Pair<Boolean, String?> {
        return BarcodeUtils.validateBarcodeInput(input, barcodeType)
    }

    fun clearBarcodeError() {
        _barcodeError.value = null
    }
}
