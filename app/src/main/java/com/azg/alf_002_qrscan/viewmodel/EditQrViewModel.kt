package com.azg.alf_002_qrscan.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.model.BallShape
import com.azg.alf_002_qrscan.model.CornerShape
import com.azg.alf_002_qrscan.model.PixelShape
import com.azg.alf_002_qrscan.model.QrQuality
import com.azg.alf_002_qrscan.model.QrTemplate
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.white
import com.create.custom.qr.QrErrorCorrectionLevel
import com.create.custom.qr.vector.QrCodeDrawable
import com.create.custom.qr.vector.createQrVectorOptions
import com.create.custom.qr.vector.style.QrVectorBallShape
import com.create.custom.qr.vector.style.QrVectorColor
import com.create.custom.qr.vector.style.QrVectorFrameShape
import com.create.custom.qr.vector.style.QrVectorLogoPadding
import com.create.custom.qr.vector.style.QrVectorLogoShape
import com.create.custom.qr.vector.style.QrVectorPixelShape
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.utils.isWithinQrContentLimit
import com.azg.alf_002_qrscan.utils.saveBitmapToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditQrViewModel(
    private val repository: AppRepository
) : ViewModel() {
    val listIconPaths = mutableStateListOf(
        R.drawable.img_social_facebook,
        R.drawable.img_social_messenger,
        R.drawable.img_social_spotify,
        R.drawable.img_social_paypal,
        R.drawable.img_social_instagram,
        R.drawable.img_social_youtube,
        R.drawable.img_social_tik_tok,
        R.drawable.img_social_netflix,
        R.drawable.img_social_whatsapp,
        R.drawable.img_social_line,
        R.drawable.img_social_snapchat,
        R.drawable.img_social_discord,
        R.drawable.img_social_telegram,
        R.drawable.img_social_wechat,
        R.drawable.img_social_google,
        R.drawable.img_social_skype,
        R.drawable.img_social_vimeo,
        R.drawable.img_social_twitter,
        R.drawable.img_social_linkedin,
        R.drawable.img_social_tumblr,
        R.drawable.img_social_baidu,
        R.drawable.img_social_tinder
    )
    val listForegroundColor = mutableStateListOf(
        Color(0xFFBF0000),
        Color(0xFFD27E00),
        Color(0xFFD2BD00),
        Color(0xFF39B200),
        Color(0xFF00A8A8),
        Color(0xFF043084),
        Color(0xFF4C0484),
        Color(0xFF84046F),
        Color(0xFF7D002D),
        Color(0xFF000000),
        Color(0xFF1E88E5),
        Color(0xFF00897B),
        Color(0xFF6A1B9A),
        Color(0xFFF4511E),
        Color(0xFF2E7D32),
        Color(0xFF5D4037),
    )
    val listBackgroundColor = mutableStateListOf(
        Color(0xFFffcccc),
        Color(0xFFFFEBCC),
        Color(0xFFFFFACC),
        Color(0xFFDCFFCC),
        Color(0xFFccffff),
        Color(0xFFCCDDFF),
        Color(0xFFE9CCFF),
        Color(0xFFFFCCF7),
        Color(0xFFFFCCDE),
        Color(0xFFFFFFFF),
        Color(0xFFE3F2FD),
        Color(0xFFE0F2F1),
        Color(0xFFF3E5F5),
        Color(0xFFFFE0B2),
        Color(0xFFE8F5E9),
        Color(0xFFD7CCC8),
    )
    // QR Options State
    private val _foregroundColor = MutableStateFlow(black)
    val foregroundColor = _foregroundColor.asStateFlow()
    private val _backgroundColor = MutableStateFlow(white)
    val backgroundColor = _backgroundColor.asStateFlow()
    private val _useGradient = MutableStateFlow(false)
    val useGradient = _useGradient.asStateFlow()
    private val _gradientStartColor = MutableStateFlow(Color(0xFF1E88E5))
    val gradientStartColor = _gradientStartColor.asStateFlow()
    private val _gradientEndColor = MutableStateFlow(Color(0xFF6A1B9A))
    val gradientEndColor = _gradientEndColor.asStateFlow()
    private val _useBackgroundGradient = MutableStateFlow(false)
    val useBackgroundGradient = _useBackgroundGradient.asStateFlow()
    private val _backgroundGradientStartColor = MutableStateFlow(Color(0xFFE3F2FD))
    val backgroundGradientStartColor = _backgroundGradientStartColor.asStateFlow()
    private val _backgroundGradientEndColor = MutableStateFlow(Color(0xFFF3E5F5))
    val backgroundGradientEndColor = _backgroundGradientEndColor.asStateFlow()
    private val _pixelShape = MutableStateFlow(PixelShape.DEFAULT)
    val pixelShape = _pixelShape.asStateFlow()
    private val _cornerShape = MutableStateFlow(CornerShape.DEFAULT)
    val cornerShape = _cornerShape.asStateFlow()
    private val _ballShape = MutableStateFlow(BallShape.DEFAULT)
    val ballShape = _ballShape.asStateFlow()
    private val _selectedLogoRes = MutableStateFlow<Int?>(null)
    val selectedLogoRes = _selectedLogoRes.asStateFlow()
    private val _qrQuality = MutableStateFlow(QrQuality.STANDARD)
    val qrQuality = _qrQuality.asStateFlow()
    private var _bitmapCreator = MutableStateFlow<Bitmap?>(null)
    val qrValueBitmap = _bitmapCreator.asStateFlow()
    private var currentQrContent = ""
    private var storedContext: Context? = null

    fun setForegroundColor(color: Color) {
        _foregroundColor.value = color
        regenerateQr()
    }

    fun setBackgroundColor(color: Color) {
        _backgroundColor.value = color
        regenerateQr()
    }

    fun setUseGradient(enabled: Boolean) {
        _useGradient.value = enabled
        regenerateQr()
    }

    fun setGradientStartColor(color: Color) {
        _gradientStartColor.value = color
        regenerateQr()
    }

    fun setGradientEndColor(color: Color) {
        _gradientEndColor.value = color
        regenerateQr()
    }

    fun setUseBackgroundGradient(enabled: Boolean) {
        _useBackgroundGradient.value = enabled
        regenerateQr()
    }

    fun setBackgroundGradientStartColor(color: Color) {
        _backgroundGradientStartColor.value = color
        regenerateQr()
    }

    fun setBackgroundGradientEndColor(color: Color) {
        _backgroundGradientEndColor.value = color
        regenerateQr()
    }

    fun setPixelShape(shape: PixelShape) {
        _pixelShape.value = shape
        regenerateQr()
    }

    fun setCornerShape(shape: CornerShape) {
        _cornerShape.value = shape
        regenerateQr()
    }
    fun setBallShape(shape: BallShape) {
        _ballShape.value = shape
        regenerateQr()
    }

    fun setLogoRes(@DrawableRes logoRes: Int?) {
        _selectedLogoRes.value = logoRes
        // A logo eats into the QR's visual data area, so bump error correction up to
        // High automatically so the code stays scannable. Never auto-downgrades back
        // to Standard when the logo is removed - higher redundancy is never harmful.
        if (logoRes != null && _qrQuality.value == QrQuality.STANDARD) {
            _qrQuality.value = QrQuality.HIGH
        }
        regenerateQr()
    }

    fun setQrQuality(quality: QrQuality) {
        _qrQuality.value = quality
        regenerateQr()
    }

    /**
     * Applies every property bundled in [template] in one shot and regenerates the QR
     * once, instead of going through the individual setters (each of which would
     * otherwise trigger its own redundant bitmap re-render).
     */
    fun applyTemplate(template: QrTemplate) {
        _foregroundColor.value = template.foregroundColor
        _backgroundColor.value = template.backgroundColor
        _useGradient.value = template.useGradient
        _gradientStartColor.value = template.gradientStartColor
        _gradientEndColor.value = template.gradientEndColor
        _useBackgroundGradient.value = false
        _pixelShape.value = template.pixelShape
        _cornerShape.value = template.cornerShape
        _ballShape.value = template.ballShape
        regenerateQr()
    }
    private fun buildOptions(context: Context? = null): com.create.custom.qr.vector.QrVectorOptions {
        return createQrVectorOptions {
            padding = .1f
            // Fourth eye is intentionally disabled: it overwrites real data/alignment
            // modules in the bottom-right corner with a decorative shape unrelated to
            // the encoded bits, which can push small/medium QR codes past their
            // error-correction budget and make them unscannable.
            fourthEyeEnabled = false
            errorCorrectionLevel = _qrQuality.value.errorCorrectionLevel
            // Background
            background {
                color = if (_useBackgroundGradient.value) {
                    QrVectorColor.LinearGradient(
                        colors = listOf(
                            0f to _backgroundGradientStartColor.value.toArgb(),
                            1f to _backgroundGradientEndColor.value.toArgb()
                        ),
                        orientation = QrVectorColor.LinearGradient.Orientation.LeftDiagonal
                    )
                } else {
                    QrVectorColor.Solid(_backgroundColor.value.toArgb())
                }
            }
            _selectedLogoRes.value?.let { resId ->
                // Logo
                logo {
                    size = .25f
                    padding = QrVectorLogoPadding.Natural(.2f)
                    shape = QrVectorLogoShape.Circle
                    context?.let { ctx ->
                        drawable = ContextCompat.getDrawable(ctx, resId)
                    }
                }
            }
            // Foreground color / gradient
            colors {
                dark = if (_useGradient.value) {
                    QrVectorColor.LinearGradient(
                        colors = listOf(
                            0f to _gradientStartColor.value.toArgb(),
                            1f to _gradientEndColor.value.toArgb()
                        ),
                        orientation = QrVectorColor.LinearGradient.Orientation.LeftDiagonal
                    )
                } else {
                    QrVectorColor.Solid(_foregroundColor.value.toArgb())
                }
            }
            // Shapes
            shapes {
                darkPixel = when (_pixelShape.value) {
                    PixelShape.ROUND -> QrVectorPixelShape.RoundCorners(.5f)
                    PixelShape.SQUARE -> QrVectorPixelShape.Circle(1f)
                    PixelShape.STAR -> QrVectorPixelShape.Star
                    PixelShape.RHOMBUS -> QrVectorPixelShape.Rhombus(1f)
                    PixelShape.HEART -> QrVectorPixelShape.Heart
                    else -> QrVectorPixelShape.Default
                }
                ball = when (_ballShape.value) {
                    BallShape.ROUND -> QrVectorBallShape.RoundCorners(.5f)
                    BallShape.SQUARE -> QrVectorBallShape.Circle(1f)
                    BallShape.RHOMBUS -> QrVectorBallShape.Rhombus(1f)
                    BallShape.HEART -> QrVectorBallShape.Heart
                    else -> QrVectorBallShape.Default
                }
                frame = when (_cornerShape.value) {
                    CornerShape.ROUND -> QrVectorFrameShape.RoundCorners(.25f)
                    CornerShape.SQUARE -> QrVectorFrameShape.Rect(7)
                    CornerShape.STAR -> QrVectorFrameShape.AsPixelShape(QrVectorPixelShape.Star, 7)
                    CornerShape.RHOMBUS -> QrVectorFrameShape.AsPixelShape(QrVectorPixelShape.Rhombus(1f), 7)
                    CornerShape.HEART -> QrVectorFrameShape.AsPixelShape(QrVectorPixelShape.Heart, 7)
                    else -> QrVectorFrameShape.Default
                }
            }
        }
    }

    private fun regenerateQr() {
        if (currentQrContent.isNotEmpty()) {
            generateQrData(currentQrContent, storedContext)
        }
    }

    fun generateQrData(value: String, context: Context?) {
        currentQrContent = value
        if (context != null) {
            storedContext = context
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (!value.isWithinQrContentLimit()) {
                _bitmapCreator.value = null
                return@launch
            }
            try {
                val options = buildOptions(storedContext)
                _bitmapCreator.value = QrCodeDrawable(
                    data = { value },
                    options = options,
                    charset = Charsets.UTF_8
                ).toBitmap(1024, 1024)
            } catch (e: Exception) {
                Log.e("EditQrViewModel", "Could not generate QR code", e)
                _bitmapCreator.value = null
            }
        }
    }

    /**
     * Save the current QR bitmap to internal storage and update the database record
     * @param context Android context
     * @param historyId The ID of the history record to update
     * @param onSuccess Callback with the saved file path when successful
     * @param onError Callback when an error occurs
     */
    fun saveAndUpdateDatabase(
        context: Context,
        historyId: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val bitmap = _bitmapCreator.value
        if (bitmap == null) {
            onError("No QR code to save")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Save bitmap to internal storage
                val savedFile = saveBitmapToInternalStorage(
                    context = context.applicationContext,
                    bitmap = bitmap,
                    baseFileName = "Edit_QR_${System.currentTimeMillis()}"
                )

                if (savedFile != null) {
                    // Update database with new image path
                    repository.updateScanImage(historyId, savedFile.absolutePath)

                    withContext(Dispatchers.Main) {
                        onSuccess(savedFile.absolutePath)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Failed to save QR image")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Unknown error")
                }
            }
        }
    }
}
