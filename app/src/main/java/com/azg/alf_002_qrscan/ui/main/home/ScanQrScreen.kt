package com.azg.alf_002_qrscan.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.azg.alf_002_qrscan.R
import com.azg.alf_002_qrscan.app.AppPreferences
import com.azg.alf_002_qrscan.app.toastShort
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.database.model.ScanType
import com.azg.alf_002_qrscan.style.AppViewTheme
import com.azg.alf_002_qrscan.style.BoxParentContent
import com.azg.alf_002_qrscan.style.IconView
import com.azg.alf_002_qrscan.style.SpaceF
import com.azg.alf_002_qrscan.style.SpaceH
import com.azg.alf_002_qrscan.style.SpaceW
import com.azg.alf_002_qrscan.style.TextView
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.fontFamily
import com.azg.alf_002_qrscan.style.heightPercentDp
import com.azg.alf_002_qrscan.style.onClick
import com.azg.alf_002_qrscan.style.opacity
import com.azg.alf_002_qrscan.style.paddingHorizontal
import com.azg.alf_002_qrscan.style.paddingTop
import com.azg.alf_002_qrscan.style.paddingVertical
import com.azg.alf_002_qrscan.style.statusBarPadding
import com.azg.alf_002_qrscan.style.white
import com.azg.alf_002_qrscan.ui.component.ColorTheme
import com.azg.alf_002_qrscan.ui.component.getQrTypeInfo
import com.azg.alf_002_qrscan.ui.component.neonState
import com.azg.alf_002_qrscan.ui.main.MainActivity
import com.azg.alf_002_qrscan.ui.main.scan.BatchScanResultActivity
import com.azg.alf_002_qrscan.ui.main.scan.ScanResultActivity
import com.azg.alf_002_qrscan.utils.QRUtils
import com.azg.alf_002_qrscan.utils.decodeLegacyQrUnicodeEscapes
import com.azg.alf_002_qrscan.widget.BottomNavItems
import com.azg.alf_002_qrscan.widget.BottomNavigationBarContent
import com.create.custom.qr.QrType
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.sdp.ssp.android.Sdp
import com.sdp.ssp.android.Ssp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

enum class FlashMode { On, Off }

data class CameraState(
    val minZoom: Float = 1f,
    val maxZoom: Float = 5f,
    val hasFlashUnit: Boolean = false,
    val isStreaming: Boolean = false
)

private val repository: AppRepository by inject(AppRepository::class.java)
private const val BATCH_DUPLICATE_WINDOW_MS = 3_000L
private const val BATCH_PREVIEW_DURATION_MS = 1_500L
private const val BATCH_SCAN_COOLDOWN_MS = 1_200L
private const val PICK_IMAGE_CLICK_COOLDOWN_MS = 1_000L
private const val ANALYZER_FRAME_INTERVAL_MS = 350L

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ScanQrScreen(
    activity: MainActivity,
    theme: ColorTheme = ColorTheme.BLUE,
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasRequestedPermission by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var flashMode by remember { mutableStateOf(FlashMode.Off) }
    var isScanning by remember { mutableStateOf(true) }
    var zoomRatio by remember { mutableFloatStateOf(1f) }
    var cameraState by remember { mutableStateOf(CameraState()) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val scanResultLock = remember { AtomicBoolean(false) }

    var batchModeEnabled by remember { mutableStateOf(false) }
    var batchId by remember { mutableStateOf<String?>(null) }
    val batchResults = remember { mutableStateListOf<HistoryApp>() }
    var batchPreviewItem by remember { mutableStateOf<HistoryApp?>(null) }
    val recentBatchScans = remember { mutableMapOf<String, Long>() }

    LaunchedEffect(batchPreviewItem) {
        if (batchPreviewItem != null) {
            delay(BATCH_PREVIEW_DURATION_MS)
            batchPreviewItem = null
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        hasRequestedPermission = true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasCameraPermission = ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (!scanResultLock.get()) isScanning = true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val galleryScanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
        )
    }
    DisposableEffect(Unit) {
        onDispose { galleryScanner.close() }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scanQRCodeFromUri(context, uri, galleryScanner, scope) { barcode ->
                if (barcode == null) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.no_results_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@scanQRCodeFromUri
                }
                if (!isScanning) return@scanQRCodeFromUri
                val now = System.currentTimeMillis()
                if (batchModeEnabled && barcode.isRecentBatchDuplicate(
                        recentBatchScans = recentBatchScans,
                        now = now
                    )
                ) {
                    return@scanQRCodeFromUri
                }
                isScanning = false
                scope.launch {
                    val saved = handleScanResult(
                        context = context,
                        barcode = barcode,
                        scanResultLock = scanResultLock,
                        batchId = batchId.takeIf { batchModeEnabled },
                        generatePreviewImage = shouldGeneratePreviewImageForScan(
                            batchModeEnabled = batchModeEnabled,
                            barcode = barcode
                        )
                    )
                    if (saved == null) {
                        isScanning = true
                    } else if (batchModeEnabled) {
                        batchResults.add(saved)
                        batchPreviewItem = saved
                        recentBatchScans[saved.content] = System.currentTimeMillis()
                        delay(BATCH_SCAN_COOLDOWN_MS)
                        isScanning = true
                    } else {
                        val intent = Intent(context, ScanResultActivity::class.java).apply {
                            putExtra(ScanResultActivity.EXTRA_HISTORY_APP, saved)
                            if (saved.scanImage.isNotEmpty()) {
                                putExtra(
                                    ScanResultActivity.EXTRA_QR_BITMAP_PATH,
                                    saved.scanImage
                                )
                            }
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    ScanQrScreenContent(
        hasCameraPermission = hasCameraPermission,
        theme = theme,
        flashMode = flashMode,
        zoomRatio = zoomRatio,
        cameraState = cameraState,
        hasRequestedPermission = hasRequestedPermission,
        shouldShowRationale = activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA),
        batchModeEnabled = batchModeEnabled,
        scannedCount = batchResults.size,
        batchPreviewItem = batchPreviewItem,
        onBatchResultsClick = {
            batchId?.let { currentBatchId ->
                activity.startActivity(
                    Intent(activity, BatchScanResultActivity::class.java).apply {
                        putExtra(BatchScanResultActivity.EXTRA_BATCH_ID, currentBatchId)
                    }
                )
            }
        },
        onBack = onBack,
        onZoomChange = { newZoom ->
            zoomRatio = newZoom
            cameraControl?.setZoomRatio(newZoom)
        },
        onFlashToggle = {
            val cam = camera ?: return@ScanQrScreenContent
            if (!cam.cameraInfo.hasFlashUnit() || lensFacing != CameraSelector.LENS_FACING_BACK) return@ScanQrScreenContent
            val turnOn = flashMode == FlashMode.Off
            try {
                cam.cameraControl.enableTorch(turnOn)
                flashMode = if (turnOn) FlashMode.On else FlashMode.Off
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        onPickImage = {
            galleryLauncher.launch("image/*")
        },
        onCameraSwitch = {
            camera?.cameraControl?.enableTorch(false)
            flashMode = FlashMode.Off
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else CameraSelector.LENS_FACING_BACK
        },
        onPermissionRequest = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        },
        onOpenSettings = {
            activity.openAppSettings()
        },
        onBatchModeToggle = batchToggle@{
            // Keep the current batch alive until its in-flight database insert completes.
            if (scanResultLock.get()) return@batchToggle
            if (batchModeEnabled) {
                batchModeEnabled = false
                if (batchResults.isNotEmpty()) {
                    batchId?.let { completedBatchId ->
                        activity.startActivity(
                            Intent(activity, BatchScanResultActivity::class.java).apply {
                                putExtra(BatchScanResultActivity.EXTRA_BATCH_ID, completedBatchId)
                            }
                        )
                    }
                } else {
                    context.toastShort(context.getString(R.string.no_scanned_codes))
                }
                batchId = null
                batchResults.clear()
                batchPreviewItem = null
                recentBatchScans.clear()
                isScanning = true
            } else {
                batchModeEnabled = true
                batchId = System.currentTimeMillis().toString()
                batchResults.clear()
                recentBatchScans.clear()
                isScanning = true
            }
        },
        cameraPreview = { overlay ->
            CameraPreviewWithScanner(
                lensFacing = lensFacing,
                flashMode = flashMode,
                zoomRatio = zoomRatio,
                isScanning = isScanning,
                onCameraStateChanged = { state -> cameraState = state },
                onCameraControlAvailable = { control -> cameraControl = control },
                onCameraReady = { cam -> camera = cam },
                onBarcodesDetected = detected@{ barcodes, _ ->
                    if (!isScanning) return@detected
                    val now = System.currentTimeMillis()
                    val barcode = barcodes.firstOrNull { candidate ->
                        !candidate.rawValue.isNullOrEmpty() &&
                                (!batchModeEnabled || !candidate.isRecentBatchDuplicate(
                                    recentBatchScans = recentBatchScans,
                                    now = now
                                ))
                    } ?: return@detected
                    isScanning = false
                    scope.launch {
                        val saved = handleScanResult(
                            context = context,
                            barcode = barcode,
                            scanResultLock = scanResultLock,
                            batchId = batchId.takeIf { batchModeEnabled },
                            generatePreviewImage = shouldGeneratePreviewImageForScan(
                                batchModeEnabled = batchModeEnabled,
                                barcode = barcode
                            )
                        )
                        if (saved == null) {
                            isScanning = true
                        } else if (batchModeEnabled) {
                            batchResults.add(saved)
                            batchPreviewItem = saved
                            recentBatchScans[saved.content] = System.currentTimeMillis()
                            delay(BATCH_SCAN_COOLDOWN_MS)
                            isScanning = true
                        } else {
                            val intent = Intent(context, ScanResultActivity::class.java).apply {
                                putExtra(ScanResultActivity.EXTRA_HISTORY_APP, saved)
                                if (saved.scanImage.isNotEmpty()) {
                                    putExtra(
                                        ScanResultActivity.EXTRA_QR_BITMAP_PATH,
                                        saved.scanImage
                                    )
                                }
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            ) {
                overlay()
            }
        }
    )
}

interface ScannerListener {
    fun onScanResults(barcodes: List<Barcode>, qrBitmap: Bitmap?)
    fun onScanFailed() {}
}

class QrScanAnalyzer(
    private val scanner: BarcodeScanner,
    private val isScanning: AtomicBoolean,
    private val listener: ScannerListener
) : ImageAnalysis.Analyzer {

    private val isProcessing = AtomicBoolean(false)
    private val lastAnalyzeTime = AtomicLong(0L)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        val lastTime = lastAnalyzeTime.get()
        if (
            !isScanning.get() ||
            now - lastTime < ANALYZER_FRAME_INTERVAL_MS ||
            !isProcessing.compareAndSet(false, true)
        ) {
            imageProxy.close()
            return
        }
        lastAnalyzeTime.set(now)

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            isProcessing.set(false)
            imageProxy.close()
            return
        }

        try {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val imageWidth = inputImage.width.toFloat()
            val imageHeight = inputImage.height.toFloat()

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    val validBarcodes = barcodes.filter {
                        !it.rawValue.isNullOrEmpty() && it.isInsideScanArea(imageWidth, imageHeight)
                    }
                    if (validBarcodes.isNotEmpty() && isScanning.get()) {
                        // The consumer owns duplicate filtering because batch mode may need
                        // to skip an old code and accept another code from the same frame.
                        listener.onScanResults(validBarcodes, null)
                    }
                }
                .addOnFailureListener { listener.onScanFailed() }
                .addOnCompleteListener {
                    isProcessing.set(false)
                    imageProxy.close()
                }
        } catch (e: Exception) {
            Log.e("QrScanAnalyzer", "Error analyzing image", e)
            isProcessing.set(false)
            imageProxy.close()
        }
    }
}

private fun Barcode.isInsideScanArea(imageWidth: Float, imageHeight: Float): Boolean {
    val box = boundingBox ?: return true
    val frameSize = minOf(imageWidth, imageHeight) * 0.7f
    val frameLeft = (imageWidth - frameSize) / 2f
    val frameTop = (imageHeight - frameSize) / 2.5f
    val frameRight = frameLeft + frameSize
    val frameBottom = frameTop + frameSize
    val centerX = box.exactCenterX()
    val centerY = box.exactCenterY()
    return centerX in frameLeft..frameRight && centerY in frameTop..frameBottom
}

private fun shouldGeneratePreviewImageForScan(
    batchModeEnabled: Boolean,
    barcode: Barcode
): Boolean {
    return !batchModeEnabled || QRUtils.formatBarcode(barcode) != QRUtils.FormatUtils.QR_CODE
}

@Composable
fun CameraPreviewWithScanner(
    lensFacing: Int,
    flashMode: FlashMode,
    zoomRatio: Float,
    isScanning: Boolean,
    onCameraStateChanged: (CameraState) -> Unit,
    onCameraControlAvailable: (CameraControl) -> Unit,
    onCameraReady: (Camera) -> Unit,
    onBarcodesDetected: (List<Barcode>, Bitmap?) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val isScanningAtomic = remember { AtomicBoolean(isScanning) }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    androidx.compose.runtime.SideEffect {
        isScanningAtomic.set(isScanning)
    }

    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        )
    }
    DisposableEffect(Unit) {
        onDispose { scanner.close() }
    }
    DisposableEffect(analysisExecutor) {
        onDispose { analysisExecutor.shutdown() }
    }

    DisposableEffect(lensFacing) {
        var currentCamera: Camera? = null
        val mainExecutor = ContextCompat.getMainExecutor(context)
        val analysisResolutionSelector = ResolutionSelector.Builder()
            .setResolutionStrategy(
                ResolutionStrategy(
                    android.util.Size(1280, 720),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
            .build()
        val imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(analysisResolutionSelector)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }
            val analyzer = QrScanAnalyzer(
                scanner = scanner,
                isScanning = isScanningAtomic,
                listener = object : ScannerListener {
                    override fun onScanResults(
                        barcodes: List<Barcode>,
                        qrBitmap: Bitmap?
                    ) {
                        onBarcodesDetected(barcodes, qrBitmap)
                    }

                    override fun onScanFailed() {
                        Log.e("QrScanAnalyzer", "Scan failed")
                    }
                }
            )
            imageAnalysis.setAnalyzer(analysisExecutor, analyzer)

            val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            try {
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
                currentCamera = camera
                onCameraControlAvailable(camera.cameraControl)
                onCameraReady(camera)

                try {
                    if (camera.cameraInfo.hasFlashUnit() && lensFacing == CameraSelector.LENS_FACING_BACK) {
                        camera.cameraControl.enableTorch(flashMode == FlashMode.On)
                    } else {
                        camera.cameraControl.enableTorch(false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                var lastCameraState: CameraState? = null
                val zoomObserver =
                    androidx.lifecycle.Observer<androidx.camera.core.ZoomState> { zoomState ->
                        val nextCameraState = CameraState(
                            minZoom = zoomState.minZoomRatio,
                            maxZoom = zoomState.maxZoomRatio,
                            isStreaming = true,
                            hasFlashUnit = camera.cameraInfo.hasFlashUnit()
                        )
                        if (lastCameraState != nextCameraState) {
                            lastCameraState = nextCameraState
                            onCameraStateChanged(nextCameraState)
                        }
                    }
                camera.cameraInfo.zoomState.observe(lifecycleOwner, zoomObserver)
                camera.cameraControl.setZoomRatio(zoomRatio)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, mainExecutor)

        onDispose {
            imageAnalysis.clearAnalyzer()
            currentCamera?.cameraInfo?.zoomState?.removeObservers(lifecycleOwner)
            cameraProviderFuture.get().unbindAll()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { previewView })
        content()
    }
}

private fun scanQRCodeFromUri(
    context: Context,
    uri: Uri,
    scanner: BarcodeScanner,
    scope: CoroutineScope,
    onResult: (Barcode?) -> Unit
) {
    scope.launch(Dispatchers.Default) {
        try {
            val inputImage = InputImage.fromFilePath(context, uri)
            scanner.process(inputImage)
                .addOnSuccessListener(ContextCompat.getMainExecutor(context)) { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        onResult(barcodes.firstOrNull())
                    } else {
                        // Retry with preprocessed image for styled QR codes
                        // (gradients, custom shapes reduce contrast)
                        scanWithPreprocessing(context, uri, scanner, scope, onResult)
                    }
                }
                .addOnFailureListener(ContextCompat.getMainExecutor(context)) {
                    Log.e("ScanQrScreen", "MLKit scan failed", it)
                    onResult(null)
                }
        } catch (e: Exception) {
            Log.e("ScanQrScreen", "Error scanning QR from URI", e)
            withContext(Dispatchers.Main) {
                onResult(null)
            }
        }
    }
}

/**
 * Preprocess the image to grayscale with high contrast before scanning.
 * This helps MLKit detect QR codes that use gradients or custom shapes.
 */
private fun scanWithPreprocessing(
    context: Context,
    uri: Uri,
    scanner: BarcodeScanner,
    scope: CoroutineScope,
    onResult: (Barcode?) -> Unit
) {
    scope.launch(Dispatchers.Default) {
        try {
            val sourceBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)
            } ?: run {
                withContext(Dispatchers.Main) { onResult(null) }
                return@launch
            }

            // Convert to high-contrast grayscale
            val grayscale = createBitmap(sourceBitmap.width, sourceBitmap.height)
            val canvas = android.graphics.Canvas(grayscale)
            val paint = android.graphics.Paint()

            val contrastScale = 1.5f
            val contrastShift = -(128f * (contrastScale - 1f))
            val colorMatrix = ColorMatrix().apply {
                setSaturation(0f)
                val contrastMatrix = ColorMatrix(
                    floatArrayOf(
                        contrastScale, 0f, 0f, 0f, contrastShift,
                        0f, contrastScale, 0f, 0f, contrastShift,
                        0f, 0f, contrastScale, 0f, contrastShift,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                postConcat(contrastMatrix)
            }
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)
            sourceBitmap.recycle()

            val processedImage = InputImage.fromBitmap(grayscale, 0)
            scanner.process(processedImage)
                .addOnSuccessListener(ContextCompat.getMainExecutor(context)) { barcodes ->
                    grayscale.recycle()
                    onResult(barcodes.firstOrNull())
                }
                .addOnFailureListener(ContextCompat.getMainExecutor(context)) {
                    grayscale.recycle()
                    Log.e("ScanQrScreen", "MLKit preprocessed scan failed", it)
                    onResult(null)
                }
        } catch (e: Exception) {
            Log.e("ScanQrScreen", "Error preprocessing QR image", e)
            withContext(Dispatchers.Main) {
                onResult(null)
            }
        }
    }
}

private fun Barcode.isRecentBatchDuplicate(
    recentBatchScans: Map<String, Long>,
    now: Long
): Boolean {
    val value = rawValue?.decodeLegacyQrUnicodeEscapes() ?: return false
    val lastScanTime = recentBatchScans[value] ?: return false
    return now - lastScanTime < BATCH_DUPLICATE_WINDOW_MS
}

private suspend fun handleScanResult(
    context: Context,
    barcode: Barcode,
    scanResultLock: AtomicBoolean,
    batchId: String? = null,
    generatePreviewImage: Boolean = true,
): HistoryApp? {
    if (!scanResultLock.compareAndSet(false, true)) {
        Log.d("ScanQrScreen", "handleScanResult blocked - already processing")
        return null
    }

    return try {
        if (AppPreferences.isEnableVibrate.value) vibrate(context)
        if (AppPreferences.isEnableSound.value) playBeep()

        val content = barcode.rawValue
            ?.takeIf { it.isNotEmpty() }
            ?.decodeLegacyQrUnicodeEscapes()
            ?: return null
        val format = QRUtils.formatBarcode(barcode)
        val valueType = QRUtils.valueTypeBarcode(barcode)
        val scanType =
            if (format == QRUtils.FormatUtils.QR_CODE) ScanType.QRCODE else ScanType.BARCODE
        val qrType = if (scanType == ScanType.BARCODE) {
            QrType.Barcode
        } else {
            QRUtils.detectQrType(content).takeUnless { it == QrType.Text }
                ?: mapValueTypeToQrType(valueType)
        }
        val historyApp = HistoryApp(
            content = content,
            type = qrType,
            scanType = scanType,
            createType = CreateType.SCAN,
            scanTime = System.currentTimeMillis(),
            batchId = batchId
        )

        withContext(Dispatchers.IO) {
            var bitmapPath: String? = null
            var generatedBitmap: Bitmap? = null
            try {
                if (generatePreviewImage) {
                    val barcodeFormat = QRUtils.formatNameToBarcodeFormat(format)
                    val bitmap = QRUtils.generateBarcode(content, barcodeFormat)
                    generatedBitmap = bitmap
                    if (bitmap != null) {
                        val file =
                            java.io.File(context.filesDir, "scan_${System.currentTimeMillis()}.png")
                        java.io.FileOutputStream(file).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }
                        bitmapPath = file.absolutePath
                    }
                }
            } catch (e: Exception) {
                Log.e("ScanQrScreen", "Could not create scan preview", e)
            } finally {
                generatedBitmap?.takeIf { !it.isRecycled }?.recycle()
            }

            val historyWithImage = historyApp.copy(scanImage = bitmapPath ?: "")
            try {
                val insertedId = repository.insertWithId(historyWithImage)
                historyWithImage.copy(hisId = insertedId.toInt())
            } catch (e: Exception) {
                bitmapPath?.let { java.io.File(it).delete() }
                throw e
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e("ScanQrScreen", "Could not save scan result", e)
        null
    } finally {
        scanResultLock.set(false)
    }
}

private fun mapValueTypeToQrType(valueType: String): QrType {
    return when (valueType) {
        QRUtils.TypeUtils.URL -> QrType.Website
        QRUtils.TypeUtils.WIFI -> QrType.Wifi
        QRUtils.TypeUtils.EMAIL -> QrType.Email
        QRUtils.TypeUtils.CONTACT_INFO -> QrType.Contact
        QRUtils.TypeUtils.PHONE -> QrType.Phone
        QRUtils.TypeUtils.SMS -> QrType.Message
        QRUtils.TypeUtils.GEO -> QrType.Location
        QRUtils.TypeUtils.CALENDAR_EVENT -> QrType.Event
        else -> QrType.Text
    }
}

@Suppress("DEPRECATION")
private fun vibrate(context: Context) {
    try {
        val vibrator =
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                100L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private object BeepPlayer {
    fun playBeep() {
        var toneGenerator: ToneGenerator? = null
        try {
            val generator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGenerator = generator
            generator.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    generator.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 200)
        } catch (e: Throwable) {
            Log.e("ScanQrScreen", "Error playing beep", e)
            try {
                toneGenerator?.release()
            } catch (_: Exception) {
            }
        }
    }
}

private fun playBeep() {
    BeepPlayer.playBeep()
}

@Composable
private fun ScanOverlay(
    modifier: Modifier = Modifier,
    frameColor: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "scan_line"
    )

    val lineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "line_progress"
    )

    Canvas(
        modifier = modifier
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val frameSize = canvasWidth * 0.60f

        val frameLeft = (canvasWidth - frameSize) / 2f
        val frameTop = canvasHeight * 0.27f

        val frameRight = frameLeft + frameSize
        val frameBottom = frameTop + frameSize

        val cornerRadius = 24.dp.toPx()

        val overlayPath = Path().apply {
            addRect(
                Rect(
                    left = 0f,
                    top = 0f,
                    right = canvasWidth,
                    bottom = canvasHeight
                )
            )
            addRoundRect(
                RoundRect(
                    left = frameLeft,
                    top = frameTop,
                    right = frameRight,
                    bottom = frameBottom,
                    radiusX = cornerRadius,
                    radiusY = cornerRadius
                )
            )

            fillType = PathFillType.EvenOdd
        }

        drawPath(
            path = overlayPath,
            color = Color.Black.copy(alpha = 0.45f)
        )

        val gridLines = 10

        val gridColor = Color.White.copy(
            alpha = 0.08f
        )

        val gridStrokeWidth = 0.5.dp.toPx()

        for (i in 1 until gridLines) {

            val offset = frameSize / gridLines * i
            drawLine(
                color = gridColor,
                start = Offset(
                    x = frameLeft + offset,
                    y = frameTop
                ),
                end = Offset(
                    x = frameLeft + offset,
                    y = frameBottom
                ),
                strokeWidth = gridStrokeWidth
            )
            drawLine(
                color = gridColor,
                start = Offset(
                    x = frameLeft,
                    y = frameTop + offset
                ),
                end = Offset(
                    x = frameRight,
                    y = frameTop + offset
                ),
                strokeWidth = gridStrokeWidth
            )
        }
        val lineY =
            frameTop + frameSize * lineProgress

        val lineBrush = Brush.horizontalGradient(
            colors = listOf(
                frameColor.copy(alpha = 0f),
                frameColor.copy(alpha = 0.5f),
                frameColor,
                frameColor.copy(alpha = 0.5f),
                frameColor.copy(alpha = 0f)
            ),
            startX = frameLeft,
            endX = frameRight
        )

        // Glow scan line
        drawLine(
            brush = lineBrush,
            start = Offset(
                x = frameLeft,
                y = lineY
            ),
            end = Offset(
                x = frameRight,
                y = lineY
            ),
            strokeWidth = 7.dp.toPx(),
            alpha = 0.18f
        )

        // Main scan line
        drawLine(
            brush = lineBrush,
            start = Offset(
                x = frameLeft,
                y = lineY
            ),
            end = Offset(
                x = frameRight,
                y = lineY
            ),
            strokeWidth = 2.dp.toPx()
        )

        val centerX =
            frameLeft + frameSize / 2f

        drawCircle(
            color = frameColor.copy(alpha = 0.2f),
            radius = 10.dp.toPx(),
            center = Offset(
                centerX,
                lineY
            )
        )

        drawCircle(
            color = frameColor.copy(alpha = 0.4f),
            radius = 6.dp.toPx(),
            center = Offset(
                centerX,
                lineY
            )
        )

        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = Offset(
                centerX,
                lineY
            )
        )
        val cornerLength =
            frameSize * 0.16f

        val strokeWidth =
            4.dp.toPx()

        val glowStrokeWidth =
            10.dp.toPx()

        val glowColor =
            frameColor.copy(alpha = 0.20f)
        drawArc(
            color = glowColor,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameLeft,
                frameTop
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = glowStrokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = frameColor,
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameLeft,
                frameTop
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameLeft,
                frameTop + cornerRadius
            ),
            end = Offset(
                frameLeft,
                frameTop + cornerLength
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameLeft,
                frameTop + cornerRadius
            ),
            end = Offset(
                frameLeft,
                frameTop + cornerLength
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameLeft + cornerRadius,
                frameTop
            ),
            end = Offset(
                frameLeft + cornerLength,
                frameTop
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameLeft + cornerRadius,
                frameTop
            ),
            end = Offset(
                frameLeft + cornerLength,
                frameTop
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        
        // TOP RIGHT
        

        drawArc(
            color = glowColor,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameRight - cornerRadius * 2,
                frameTop
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = glowStrokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = frameColor,
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameRight - cornerRadius * 2,
                frameTop
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameRight,
                frameTop + cornerRadius
            ),
            end = Offset(
                frameRight,
                frameTop + cornerLength
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameRight,
                frameTop + cornerRadius
            ),
            end = Offset(
                frameRight,
                frameTop + cornerLength
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameRight - cornerRadius,
                frameTop
            ),
            end = Offset(
                frameRight - cornerLength,
                frameTop
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameRight - cornerRadius,
                frameTop
            ),
            end = Offset(
                frameRight - cornerLength,
                frameTop
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        
        // BOTTOM LEFT
        

        drawArc(
            color = glowColor,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameLeft,
                frameBottom - cornerRadius * 2
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = glowStrokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = frameColor,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameLeft,
                frameBottom - cornerRadius * 2
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameLeft,
                frameBottom - cornerRadius
            ),
            end = Offset(
                frameLeft,
                frameBottom - cornerLength
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameLeft,
                frameBottom - cornerRadius
            ),
            end = Offset(
                frameLeft,
                frameBottom - cornerLength
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameLeft + cornerRadius,
                frameBottom
            ),
            end = Offset(
                frameLeft + cornerLength,
                frameBottom
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameLeft + cornerRadius,
                frameBottom
            ),
            end = Offset(
                frameLeft + cornerLength,
                frameBottom
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        
        // BOTTOM RIGHT
        

        drawArc(
            color = glowColor,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameRight - cornerRadius * 2,
                frameBottom - cornerRadius * 2
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = glowStrokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = frameColor,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(
                frameRight - cornerRadius * 2,
                frameBottom - cornerRadius * 2
            ),
            size = Size(
                cornerRadius * 2,
                cornerRadius * 2
            ),
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameRight,
                frameBottom - cornerRadius
            ),
            end = Offset(
                frameRight,
                frameBottom - cornerLength
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameRight,
                frameBottom - cornerRadius
            ),
            end = Offset(
                frameRight,
                frameBottom - cornerLength
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = glowColor,
            start = Offset(
                frameRight - cornerRadius,
                frameBottom
            ),
            end = Offset(
                frameRight - cornerLength,
                frameBottom
            ),
            strokeWidth = glowStrokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = frameColor,
            start = Offset(
                frameRight - cornerRadius,
                frameBottom
            ),
            end = Offset(
                frameRight - cornerLength,
                frameBottom
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ScanQrScreenContent(
    hasCameraPermission: Boolean,
    theme: ColorTheme,
    flashMode: FlashMode,
    zoomRatio: Float,
    cameraState: CameraState,
    hasRequestedPermission: Boolean,
    shouldShowRationale: Boolean,
    batchModeEnabled: Boolean = false,
    scannedCount: Int = 0,
    batchPreviewItem: HistoryApp? = null,
    onBack: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onFlashToggle: () -> Unit,
    onPickImage: () -> Unit,
    onCameraSwitch: () -> Unit,
    onPermissionRequest: () -> Unit,
    onOpenSettings: () -> Unit,
    onBatchModeToggle: () -> Unit = {},
    onBatchResultsClick: () -> Unit = {},
    cameraPreview: @Composable (@Composable () -> Unit) -> Unit
) {
    var isPickImageLocked by remember { mutableStateOf(false) }

    LaunchedEffect(isPickImageLocked) {
        if (isPickImageLocked) {
            delay(PICK_IMAGE_CLICK_COOLDOWN_MS)
            isPickImageLocked = false
        }
    }

    BoxParentContent(modifier = Modifier.background(black), onBack = { onBack.invoke() }) {
        if (hasCameraPermission) {
            cameraPreview {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    ScanOverlay(
                        modifier = Modifier.fillMaxSize(),
                        frameColor = Color(0xFF0066FF)
                    )

                    val frameTop = maxHeight * 0.27f
                    val frameBottom = frameTop + maxWidth * 0.60f
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .paddingTop(frameBottom + 16.Sdp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(white.opacity(0.1f), RoundedCornerShape(50))
                                .padding(horizontal = 12.Sdp, vertical = 6.Sdp),
                            horizontalArrangement = Arrangement.spacedBy(6.Sdp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_app_check),
                                contentDescription = null,
                                tint = white,
                                modifier = Modifier.size(14.Sdp)
                            )
                            TextView(
                                text = "Supports QR & Barcode",
                                textSize = 10.Ssp,
                                color = white,
                                font = R.font.rb_500
                            )
                        }
                    }

                    if (batchModeEnabled) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .paddingTop(frameTop - 48.Sdp)
                                .background(Color(0xFF0066FF), RoundedCornerShape(50))
                                .onClick(
                                    enable = scannedCount > 0,
                                    onClick = onBatchResultsClick
                                )
                                .padding(horizontal = 12.Sdp, vertical = 7.Sdp),
                            horizontalArrangement = Arrangement.spacedBy(6.Sdp)
                        ) {
                            IconView(
                                item = R.drawable.ic_batch_scan,
                                tint = white,
                                icSize = 14
                            )
                            TextView(
                                text = stringResource(
                                    R.string.scanned_codes_count,
                                    scannedCount
                                ),
                                textSize = 11.Ssp,
                                color = white,
                                font = R.font.rb_600
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = stringResource(R.string.view_batch_results),
                                tint = white,
                                modifier = Modifier.size(16.Sdp)
                            )
                        }
                    }
                }
                 Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .statusBarPadding()
                         .paddingHorizontal(16.Sdp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {


                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TextView(
                                text = stringResource(R.string.scan_code),
                                textSize = 16.Ssp,
                                color = white,
                                font = R.font.rb_600,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(1f)
                            )
                            TextView(
                                text = stringResource(R.string.place_the_code_inside_the_frame_to_scan),
                                textSize = 11.Ssp,
                                color = white.opacity(0.7f),
                                font = R.font.rb_400,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(1f)
                            )
                        }
                    }
                     SpaceF()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.Sdp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (batchPreviewItem != null) {
                            BatchScanPreviewCard(item = batchPreviewItem)
                            SpaceH(12.Sdp)
                        }

                        ZoomControls(
                            minZoom = cameraState.minZoom,
                            maxZoom = cameraState.maxZoom,
                            zoomLevel = zoomRatio,
                            onZoomChange = onZoomChange
                        )

                        SpaceH(8.Sdp)
                        Row(
                            Modifier
                                .padding(horizontal = 16.Sdp)
                                .fillMaxWidth()
                                .background(
                                    color = black.opacity(0.3f),
                                    shape = RoundedCornerShape(24.Sdp)
                                )
                                .border(1.dp, white.opacity(0.1f), RoundedCornerShape(24.Sdp))
                                .padding(vertical = 8.Sdp).paddingHorizontal(6.Sdp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScannerActionItem(
                                icon = if (flashMode == FlashMode.Off) R.drawable.ic_view_offlash else R.drawable.ic_view_onflash,
                                label = "Flash",
                                onClick = onFlashToggle
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.Sdp)
                                    .background(white.opacity(0.1f))
                            )

                            ScannerActionItem(
                                icon = R.drawable.ic_view_picture,
                                label = "Gallery",
                                enabled = !isPickImageLocked,
                                onClick = {
                                    if (isPickImageLocked) return@ScannerActionItem
                                    isPickImageLocked = true
                                    onPickImage()
                                }
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.Sdp)
                                    .background(white.opacity(0.1f))
                            )

                            ScannerActionItem(
                                icon = R.drawable.ic_view_camera,
                                label = "Flip Camera",
                                onClick = onCameraSwitch
                            )

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.Sdp)
                                    .background(white.opacity(0.1f))
                            )

                            ScannerActionItem(
                                icon = if (batchModeEnabled) R.drawable.ic_view_batch_on else R.drawable.ic_view_batch_off,
                                label = stringResource(R.string.batch_scan),
                                onClick = onBatchModeToggle
                            )
                        }

                        SpaceH(heightPercentDp(8f))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_app_camera),
                        contentDescription = "camera",
                        tint = white,
                        modifier = Modifier
                            .size(68.Sdp)
                            .background(
                                color = white.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                            .padding(18.Sdp)
                    )
                    TextView(
                        text = stringResource(R.string.camera_permission_message),
                        textSize = 11.Ssp,
                        color = white,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .paddingVertical(10.Sdp)
                            .paddingHorizontal(18.Sdp)
                    )
                    TextView(
                        text = stringResource(R.string.open_settings),
                        textSize = 12.Ssp,
                        color = white,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .paddingTop(18.Sdp)
                            .background(theme.buttonColor, CircleShape)
                            .padding(12.Sdp)
                            .fillMaxWidth(0.5f)
                            .wrapContentHeight()
                    ) {
                        if (shouldShowRationale || !hasRequestedPermission) {
                            onPermissionRequest()
                        } else {
                            onOpenSettings()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchScanPreviewCard(item: HistoryApp) {
    val typeInfo = remember(item.type) { getQrTypeInfo(item.type) }
    Row(
        Modifier
            .paddingHorizontal(24.Sdp)
            .fillMaxWidth()
            .background(black.opacity(0.55f), RoundedCornerShape(16.Sdp))
            .border(1.dp, white.opacity(0.1f), RoundedCornerShape(16.Sdp))
            .padding(12.Sdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(36.Sdp)
                .clip(RoundedCornerShape(10.Sdp))
                .background(typeInfo.accentColor.opacity(0.2f)),
            contentAlignment = Alignment.Center
        ) {
            IconView(item = typeInfo.icon, tint = typeInfo.accentColor, icSize = 18)
        }
        SpaceW(10.Sdp)
        Column(Modifier.weight(1f)) {
            TextView(
                text = stringResource(typeInfo.title),
                textSize = 11.Ssp,
                color = white,
                font = R.font.rb_600
            )
            TextView(
                text = item.content,
                textSize = 11.Ssp,
                color = white.opacity(0.7f),
                font = R.font.rb_400,
                maxLine = 1
            )
        }
        IconView(item = R.drawable.ic_app_check, tint = Color(0xFF22C55E), icSize = 18)
    }
}

@Composable
private fun ScannerActionItem(
    @DrawableRes icon: Int,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.55f)
            .onClick(enable = enabled) { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.Sdp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(42.Sdp)
        )
        TextView(
            text = label,
            textSize = 10.Ssp,
            color = white,
            font = R.font.rb_500
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
private fun ScanQrScreenPreview() {
    AppViewTheme {
        Box(Modifier.fillMaxSize()) {
            ScanQrScreenContent(
                hasCameraPermission = true,
                theme = ColorTheme.BLUE,
                flashMode = FlashMode.Off,
                zoomRatio = 1f,
                cameraState = CameraState(
                    minZoom = 1f,
                    maxZoom = 5f,
                    hasFlashUnit = true,
                    isStreaming = true
                ),
                hasRequestedPermission = true,
                shouldShowRationale = false,
                onBack = {},
                onZoomChange = {},
                onFlashToggle = {},
                onPickImage = {},
                onCameraSwitch = {},
                onPermissionRequest = {},
                onOpenSettings = {},
                cameraPreview = { overlay ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        overlay()
                    }
                })
            BottomNavigationBarContent(
                selectedItem = BottomNavItems.HOME,
                themeApp = ColorTheme.BLUE,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@ComposePreview(showBackground = true, name = "No Permission")
@Composable
private fun ScanQrScreenNoPermissionPreview() {
    AppViewTheme {
        Surface {
            Box(Modifier.fillMaxSize()) {
                ScanQrScreenContent(
                    hasCameraPermission = false,
                    theme = ColorTheme.BLUE,
                    flashMode = FlashMode.Off,
                    zoomRatio = 1f,
                    cameraState = CameraState(),
                    hasRequestedPermission = false,
                    shouldShowRationale = false,
                    onBack = {},
                    onZoomChange = {},
                    onFlashToggle = {},
                    onPickImage = {},
                    onCameraSwitch = {},
                    onPermissionRequest = {},
                    onOpenSettings = {},
                    cameraPreview = {}
                )
                BottomNavigationBarContent(
                    selectedItem = BottomNavItems.HOME,
                    themeApp = ColorTheme.BLUE,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun ZoomControls(
    minZoom: Float,
    maxZoom: Float,
    zoomLevel: Float,
    onZoomChange: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.Sdp)
    ) {
// Minus button
        Box(
            modifier = Modifier
                .size(40.Sdp)
                .background(white.opacity(0.1f), CircleShape)
                .onClick { onZoomChange((zoomLevel - 0.5f).coerceIn(minZoom, maxZoom)) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_round_remove),
                tint = white,
                modifier = Modifier.size(20.Sdp),
                contentDescription = null
            )
        }

// Pill
        Row(
            modifier = Modifier
                .background(white.opacity(0.1f), RoundedCornerShape(50))
                .padding(4.Sdp),
            horizontalArrangement = Arrangement.spacedBy(4.Sdp)
        ) {
            listOf(1f, 2f, 3f).forEach { level ->
                val selected = if (level == 1f) zoomLevel < 1.5f
                else if (level == 2f) zoomLevel >= 1.5f && zoomLevel < 2.5f
                else zoomLevel >= 2.5f

                Box(
                    modifier = Modifier
                        .background(
                            if (selected) Color(0xFF0066FF) else Color.Transparent,
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 16.Sdp, vertical = 8.Sdp)
                        .onClick { onZoomChange(level) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${level.toInt()}x",
                        color = white,
                        fontSize = 12.sp,
                        style = TextStyle.Default.fontFamily(R.font.rb_600)
                    )
                }
            }
        }

// Plus button
        Box(
            modifier = Modifier
                .size(40.Sdp)
                .background(white.opacity(0.1f), CircleShape)
                .onClick { onZoomChange((zoomLevel + 0.5f).coerceIn(minZoom, maxZoom)) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                tint = white,
                modifier = Modifier.size(20.Sdp),
                contentDescription = null
            )
        }
    }
}
