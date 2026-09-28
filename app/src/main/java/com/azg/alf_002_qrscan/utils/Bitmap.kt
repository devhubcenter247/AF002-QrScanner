package com.azg.alf_002_qrscan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

suspend fun renderComposableToBitmap(
    context: Context,
    widthPx: Int,
    heightPx: Int,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) {
    val composeView = ComposeView(context).apply {
        layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
        setContent { content() }
        val wSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY)
        val hSpec = View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
        measure(wSpec, hSpec)
        layout(0, 0, widthPx, heightPx)
    }
    composeView.drawToBitmap()
}

fun String.toBitmap(): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun Bitmap.withWhitePadding(paddingPx: Int): Bitmap {
    if (paddingPx <= 0) return this
    val paddedBitmap = Bitmap.createBitmap(
        width + paddingPx * 2,
        height + paddingPx * 2,
        config ?: Bitmap.Config.ARGB_8888
    )
    Canvas(paddedBitmap).apply {
        drawColor(Color.WHITE)
        drawBitmap(this@withWhitePadding, paddingPx.toFloat(), paddingPx.toFloat(), null)
    }
    return paddedBitmap
}

fun File.toBase64(): String {
    FileInputStream(this).use {
        val byte = it.readBytes()
        return Base64.encodeToString(byte, Base64.NO_WRAP)
    }
}
// From drawable resource
fun encodeResourceToBase64(context: Context, resId: Int): String? {
    return try {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// From file path
fun encodeFileToBase64(path: String): String? {
    return try {
        val bitmap = BitmapFactory.decodeFile(path)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// From online URL
suspend fun downloadUrlAsBase64(url: String): String? = withContext(Dispatchers.IO) {
    try {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connect()
        val bitmap = BitmapFactory.decodeStream(conn.inputStream)
        conn.disconnect()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
/**
 * Encode a drawable resource to Base64 after resizing.
 * @param maxDimPx the maximum of (width, height) after scaling (keeps aspect)
 */
fun encodeResourceToBase64(
    context: Context,
    resId: Int,
    maxDimPx: Int = 512,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 85,
    maxSizeKB: Int? = null // optional size cap
): String? = runCatching {
    decodeResourceScaled(context, resId, maxDimPx)?.let { bmp ->
        encodeBitmapToBase64(bmp, format, quality, maxSizeKB)
            .also { bmp.recycle() }
    }
}.getOrNull()
/** Encode a file path image to Base64 after resizing. */
fun encodeFileToBase64(
    path: String,
    maxDimPx: Int = 512,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 85,
    maxSizeKB: Int? = null
): String? = runCatching {
    decodeFileScaled(path, maxDimPx)?.let { bmp ->
        val rotated = applyExifOrientationIfNeeded(path, bmp)
        if (rotated !== bmp) bmp.recycle()
        encodeBitmapToBase64(rotated, format, quality, maxSizeKB)
            .also { rotated.recycle() }
    }
}.getOrNull()
/** Download an image (public URL), resize, then encode to Base64. */
suspend fun downloadUrlAsBase64(
    url: String,
    maxDimPx: Int = 512,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP_LOSSY,
    quality: Int = 85,
    maxSizeKB: Int? = null
): String? = withContext(Dispatchers.IO) {
    runCatching {
        val bytes = downloadBytes(url) ?: return@runCatching null
        decodeBytesScaled(bytes, maxDimPx)?.let { bmp ->
            encodeBitmapToBase64(bmp, format, quality, maxSizeKB)
                .also { bmp.recycle() }
        }
    }.getOrNull()
}
// ------------- Core helpers --------------------------------------------------
private fun decodeResourceScaled(
    context: Context,
    resId: Int,
    maxDimPx: Int
): Bitmap? {
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeResource(context.resources, resId, opts)
    val sample = computeInSampleSize(opts.outWidth, opts.outHeight, maxDimPx)
    val opts2 = BitmapFactory.Options().apply { inSampleSize = sample }
    val sampled = BitmapFactory.decodeResource(context.resources, resId, opts2) ?: return null
    return scaleDownIfNeeded(sampled, maxDimPx)
}

private fun decodeFileScaled(path: String, maxDimPx: Int): Bitmap? {
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, opts)
    val sample = computeInSampleSize(opts.outWidth, opts.outHeight, maxDimPx)
    val opts2 = BitmapFactory.Options().apply { inSampleSize = sample }
    val sampled = BitmapFactory.decodeFile(path, opts2) ?: return null
    return scaleDownIfNeeded(sampled, maxDimPx)
}

private fun decodeBytesScaled(bytes: ByteArray, maxDimPx: Int): Bitmap? {
    val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
    val sample = computeInSampleSize(opts.outWidth, opts.outHeight, maxDimPx)
    val opts2 = BitmapFactory.Options().apply { inSampleSize = sample }
    val sampled = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts2) ?: return null
    return scaleDownIfNeeded(sampled, maxDimPx)
}
/** inSampleSize so that min(width, height) ≤ maxDimPx and memory stays low. */
private fun computeInSampleSize(w: Int, h: Int, maxDimPx: Int): Int {
    if (w <= 0 || h <= 0) return 1
    var sample = 1
    val maxSide = max(w, h)
    while (maxSide / (sample * 2) >= maxDimPx) {
        sample *= 2
    }
    return sample
}
/** If still larger than maxDimPx after sampling, scale precisely. */
private fun scaleDownIfNeeded(sampled: Bitmap, maxDimPx: Int): Bitmap {
    val w = sampled.width
    val h = sampled.height
    val maxSide = max(w, h)
    if (maxSide <= maxDimPx) return sampled
    val ratio = maxDimPx.toFloat() / maxSide.toFloat()
    val dstW = ceil(w * ratio).toInt().coerceAtLeast(1)
    val dstH = ceil(h * ratio).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(sampled, dstW, dstH, true)
    if (scaled !== sampled) sampled.recycle()
    return scaled
}
/** Encode bitmap to Base64, optionally shrinking quality to fit maxSizeKB. */
private fun encodeBitmapToBase64(
    bitmap: Bitmap,
    format: Bitmap.CompressFormat,
    quality: Int,
    maxSizeKB: Int?
): String {
    // Optional quality shrink loop to cap size
    val baos = ByteArrayOutputStream()
    var q = quality.coerceIn(0, 100)
    fun compressToStream(): Int {
        baos.reset()
        bitmap.compress(format, q, baos)
        return baos.size()
    }
    compressToStream()
    if (maxSizeKB != null) {
        val limit = max(1, maxSizeKB) * 1024
        // Reduce quality in steps until under limit or q <= 40
        while (baos.size() > limit && q > 40) {
            q = (q * 0.85f).toInt().coerceAtLeast(40)
            compressToStream()
        }
    }
    val bytes = baos.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
// ------------- Extras --------------------------------------------------------
/** Apply EXIF orientation for camera images loaded from file. */
private fun applyExifOrientationIfNeeded(path: String, src: Bitmap): Bitmap {
    return runCatching {
        val exif = ExifInterface(path)
        val ori = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val matrix = Matrix()
        when (ori) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            else -> return src
        }
        val rotated = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
        rotated
    }.getOrDefault(src)
}
/** Simple byte downloader for public URLs. */
private fun downloadBytes(url: String): ByteArray? {
    var conn: HttpURLConnection? = null
    return try {
        conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 15_000
            instanceFollowRedirects = true
        }
        conn.connect()
        if (conn.responseCode !in 200..299) return null
        conn.inputStream.use { it.readBytes() }
    } catch (_: Exception) {
        null
    } finally {
        conn?.disconnect()
    }
}

fun Uri.uriToBase64(context: Context): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        val bytes = inputStream?.readBytes() ?: return ""
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}
/** Decode full-size (cẩn thận OOM với ảnh lớn) */
fun Uri.decodeUriToBitmap(context: Context): Bitmap? = try {
    if (Build.VERSION.SDK_INT >= 28) {
        val src = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(src) { decoder, _, _ ->
            decoder.isMutableRequired = true
        }
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }
} catch (e: Exception) {
    e.printStackTrace(); null
}
/** Decode theo kích thước mong muốn (tránh OOM) */
fun Uri.decodeScaledBitmap(
    context: Context,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? = try {
    context.contentResolver.openInputStream(this)?.use { input ->
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(BufferedInputStream(input), null, opts)
        val sample = calculateInSampleSize(opts.outWidth, opts.outHeight, reqWidth, reqHeight)
        context.contentResolver.openInputStream(this)?.use { input2 ->
            val opts2 = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            BitmapFactory.decodeStream(BufferedInputStream(input2), null, opts2)
        }
    }
} catch (e: Exception) {
    e.printStackTrace(); null
}

private fun calculateInSampleSize(
    width: Int,
    height: Int,
    reqWidth: Int,
    reqHeight: Int
): Int {
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        var halfHeight = height / 2
        var halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= reqHeight &&
            (halfWidth / inSampleSize) >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

enum class ScaleType {
    WIDTH,      // Scale based on width
    HEIGHT,     // Scale based on height
    MIN,        // Scale based on smaller side
    MAX,        // Scale based on larger side
    BOTH        // Force width = height = targetSize
}

fun Bitmap.scaleBitmap(
    targetSize: Int,
    scaleType: ScaleType = ScaleType.BOTH
): Bitmap {
    val width = this.width
    val height = this.height

    val (newWidth, newHeight) = when (scaleType) {
        ScaleType.WIDTH -> {
            val scale = targetSize.toFloat() / width
            Pair(targetSize, (height * scale).toInt())
        }
        ScaleType.HEIGHT -> {
            val scale = targetSize.toFloat() / height
            Pair((width * scale).toInt(), targetSize)
        }
        ScaleType.MIN -> {
            val minSide = min(width, height)
            val scale = targetSize.toFloat() / minSide
            Pair((width * scale).toInt(), (height * scale).toInt())
        }
        ScaleType.MAX -> {
            val maxSide = max(width, height)
            val scale = targetSize.toFloat() / maxSide
            Pair((width * scale).toInt(), (height * scale).toInt())
        }
        ScaleType.BOTH -> {
            // Force square resize (e.g., for thumbnails)
            Pair(targetSize, targetSize)
        }
    }

    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}
