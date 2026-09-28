@file:Suppress("DEPRECATION")

package com.azg.alf_002_qrscan.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URLConnection

fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
    val directory = context.filesDir
    val file = File(directory, "$fileName.png")

    return try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        file.absolutePath
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun shareImages(context: Context, imagePaths: MutableList<String>) {
    val imageUris = ArrayList<Uri>()

    for (path in imagePaths) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        imageUris.add(uri)
    }
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share images via"))
}

fun Activity.bitmapFromUri(uri: Uri): Bitmap? {
    return try {
        val inputStream = this.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream).also {
            inputStream?.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Bitmap.resizeWidth(newWidth: Int): Bitmap {
    val aspectRatio = this.height.toFloat() / this.width
    val targetHeight = (newWidth * aspectRatio).toInt()
    return this.scale(newWidth, targetHeight)
}

fun Bitmap.resizeHeight(newHeight: Int): Bitmap {
    val aspectRatio = this.width.toFloat() / this.height
    val targetWidth = (newHeight * aspectRatio).toInt()
    return this.scale(targetWidth, newHeight)
}

fun Bitmap.resize(newWidth: Int, newHeight: Int): Bitmap {
    return this.scale(newWidth, newHeight)
}

fun Activity.openFromAssets(filePath: String, callback: ((Bitmap?) -> Unit)) {
    try {
        val inputStream = this.assets.open(filePath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        callback.invoke(bitmap)
        inputStream.close()
    } catch (e: IOException) {
        callback.invoke(null)
        e.printStackTrace()
    }
}

suspend fun saveBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    baseFileName: String,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100,
    subDir: String? = null   // nếu muốn: "images", "qr", v.v.
): File?  = withContext(Dispatchers.IO){
    return@withContext try {
        val ext = when (format) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.WEBP, Bitmap.CompressFormat.WEBP_LOSSY, Bitmap.CompressFormat.WEBP_LOSSLESS -> "webp"
            else -> "png"
        }
        val baseDir = if (subDir.isNullOrBlank()) {
            context.filesDir
        } else {
            File(context.filesDir, subDir).apply {
                if (!exists()) mkdirs()
            }
        }
        val fileName = if (baseFileName.endsWith(".$ext")) {
            baseFileName
        } else {
            "$baseFileName.$ext"
        }

        val dstFile = File(baseDir, fileName)

        FileOutputStream(dstFile).use { out ->
            if (!bitmap.compress(format, quality, out)) {
                throw IOException("Bitmap.compress failed")
            }
        }

        dstFile
    } catch (e: Exception) {
        Log.e("Saver", "saveBitmapToInternalStorage error", e)
        null
    }
}

suspend fun saveBitmapToCache(
    context: Context,
    bitmap: Bitmap,
    baseFileName: String,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100,
    subDir: String? = null
): File? = withContext(Dispatchers.IO){
    return@withContext try {
        val ext = when (format) {
            Bitmap.CompressFormat.JPEG -> "jpg"
            Bitmap.CompressFormat.WEBP, Bitmap.CompressFormat.WEBP_LOSSY, Bitmap.CompressFormat.WEBP_LOSSLESS -> "webp"
            else -> "png"
        }
        val baseDir = if (subDir.isNullOrBlank()) {
            context.cacheDir
        } else {
            File(context.cacheDir, subDir).apply {
                if (!exists()) mkdirs()
            }
        }
        val fileName = if (baseFileName.endsWith(".$ext")) {
            baseFileName
        } else {
            "$baseFileName.$ext"
        }
        val dstFile = File(baseDir, fileName)
        FileOutputStream(dstFile).use { out ->
            if (!bitmap.compress(format, quality, out)) {
                throw IOException("Bitmap.compress failed")
            }
        }
        dstFile
    } catch (e: Exception) {
        Log.e("Saver", "saveBitmapToCache error", e)
        null
    }
}


fun moveImageToGallery(
    activity: Activity,
    cacheFilePath: String,
    albumName: String
): Uri? {
    val cacheFile = File(cacheFilePath)
    if (!cacheFile.exists()) return null
    val contentResolver = activity.contentResolver
    val ext = cacheFile.extension.lowercase()
    val mime = when (ext) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        else -> "image/*"
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, cacheFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, mime)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/$albumName"
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        try {
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                contentResolver.openOutputStream(uri)?.use { out ->
                    cacheFile.inputStream().use { it.copyTo(out) }
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)
            }
            uri
        } catch (e: Exception) {
            null
        }
    } else {
        try {
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val targetDir = File(picturesDir, albumName).apply { if (!exists()) mkdirs() }
            val newFile = File(targetDir, cacheFile.name)
            cacheFile.copyTo(newFile, overwrite = true)
            val uri = Uri.fromFile(newFile)
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(newFile.absolutePath),
                arrayOf(mime),
                null
            )
            uri
        } catch (e: IOException) {
            null
        }
    }
}

fun Activity.openFromAssets(filePath: String): Bitmap? {
    try {
        val inputStream = this.assets.open(filePath)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        return bitmap
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun copyToClipBoard(context: Context, value: String) {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copy Value", value)
    clipboard.setPrimaryClip(clip)
}

fun saveImageToGallery(
    activity: Activity,
    bitmap: Bitmap,
    fileName: String,
    folderName: String = "Data"
): Uri? {
    val cr = activity.contentResolver
    val baseName = fileName.substringBeforeLast('.')
    val ext = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
    val (format, mime, finalName) = when (ext) {
        "jpg", "jpeg" -> Triple(
            Bitmap.CompressFormat.JPEG,
            "image/jpeg",
            if (ext == "jpg") "$baseName.jpg" else "$baseName.jpeg"
        )
        "webp" -> Triple(Bitmap.CompressFormat.WEBP, "image/webp", "$baseName.webp")
        else -> Triple(Bitmap.CompressFormat.PNG, "image/png", "$baseName.png")
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, finalName)
            put(MediaStore.Images.Media.MIME_TYPE, mime)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}${File.separator}$folderName"
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        var uri: Uri? = null
        try {
            uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                cr.openOutputStream(uri)?.use { out ->
                    val quality = if (format == Bitmap.CompressFormat.JPEG) 95 else 100
                    if (!bitmap.compress(format, quality, out)) {
                        throw IOException("Bitmap.compress returned false")
                    }
                } ?: throw IOException("Failed to open output stream")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                cr.update(uri, values, null, null)
            }
            uri
        } catch (_: Exception) {
            if (uri != null) runCatching { cr.delete(uri, null, null) }
            null
        }
    } else {
        try {
            @Suppress("DEPRECATION")
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val targetDir = File(picturesDir, folderName).apply { if (!exists()) mkdirs() }
            val imageFile = File(targetDir, finalName)
            FileOutputStream(imageFile).use { out ->
                val quality = if (format == Bitmap.CompressFormat.JPEG) 95 else 100
                if (!bitmap.compress(format, quality, out)) {
                    throw IOException("Bitmap.compress returned false")
                }
            }
            val uri = Uri.fromFile(imageFile)
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(imageFile.absolutePath),
                arrayOf(mime),
                null
            )
            uri
        } catch (_: Exception) {
            null
        }
    }
}

private fun guessMime(fileName: String, fallback: String = "application/octet-stream"): String {
    val mime = URLConnection.guessContentTypeFromName(fileName)
    return mime ?: when (fileName.substringAfterLast('.', "").lowercase()) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        "json" -> "application/json"
        "csv" -> "text/csv"
        "zip" -> "application/zip"
        "apk" -> "application/vnd.android.package-archive"
        "mp4" -> "video/mp4"
        "mp3" -> "audio/mpeg"
        else -> fallback
    }
}

private fun ensureExt(fileName: String, desiredExt: String): String {
    val name = fileName.substringBeforeLast('.')
    val ext = fileName.substringAfterLast('.', "")
    return if (ext.equals(desiredExt, ignoreCase = true)) fileName else "$name.$desiredExt"
}

fun saveFileToStorage(
    context: Context,
    sourcePath: String,
    destFileName: String,
    environment: String = Environment.DIRECTORY_DOWNLOADS,
    folderName: String = "Downloads"
): Uri? {
    val src = File(sourcePath)
    if (!src.exists()) {
        Log.e("Saver", "Source file does not exist: $sourcePath")
        return null
    }
    val fileName = destFileName.ifBlank { src.name }
    val mime = guessMime(fileName)
    val cr = context.contentResolver

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileUsingMediaStore(context, src, fileName, mime, environment, folderName)
        } else {
            saveFileLegacy(context, src, fileName, mime, environment, folderName)
        }
    } catch (e: Exception) {
        Log.e("Saver", "saveFileToStorage error", e)
        null
    }
}
@RequiresApi(Build.VERSION_CODES.Q)
private fun saveFileUsingMediaStore(
    context: Context,
    src: File,
    fileName: String,
    mime: String,
    environment: String,
    folderName: String
): Uri? {
    val cr = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mime)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${environment}/$folderName")
        put(MediaStore.MediaColumns.IS_PENDING, 1)

        put(MediaStore.MediaColumns.SIZE, src.length())

        put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000)
    }
    val contentUri = when (environment) {
        Environment.DIRECTORY_DOWNLOADS -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        Environment.DIRECTORY_PICTURES -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        Environment.DIRECTORY_DOCUMENTS -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        Environment.DIRECTORY_MOVIES -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        Environment.DIRECTORY_MUSIC -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> when {
            mime.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mime.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            mime.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }
    }
    var uri: Uri? = null
    try {
        uri = cr.insert(contentUri, values)
        if (uri != null) {
            cr.openOutputStream(uri)?.use { out ->
                FileInputStream(src).use { it.copyTo(out) }
            } ?: throw IOException("OpenOutputStream failed")


            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            cr.update(uri, values, null, null)

            Log.d("Saver", "File saved successfully: $uri")
            return uri
        }
    } catch (e: Exception) {
        Log.e("Saver", "saveFileUsingMediaStore error", e)

        uri?.let { runCatching { cr.delete(it, null, null) } }
    }
    return null
}
@Suppress("DEPRECATION")
private fun saveFileLegacy(
    context: Context,
    src: File,
    fileName: String,
    mime: String,
    environment: String,
    folderName: String
): Uri? {
    try {
        val baseDir = when (environment) {
            Environment.DIRECTORY_DOWNLOADS -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            Environment.DIRECTORY_PICTURES -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            Environment.DIRECTORY_DOCUMENTS -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            )
            Environment.DIRECTORY_MOVIES -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            )
            Environment.DIRECTORY_MUSIC -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            else -> when {
                mime.startsWith("image/") -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                mime.startsWith("video/") -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES
                )
                mime.startsWith("audio/") -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC
                )
                else -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }
        }
        val dir = File(baseDir, folderName).apply {
            if (!exists()) mkdirs()
        }
        val dst = File(dir, fileName)


        src.copyTo(dst, overwrite = true)


        MediaScannerConnection.scanFile(context, arrayOf(dst.absolutePath), arrayOf(mime), null)

        Log.d("Saver", "File saved successfully to: ${dst.absolutePath}")
        return Uri.fromFile(dst)
    } catch (e: Exception) {
        Log.e("Saver", "saveFileLegacy error", e)
        return null
    }
}


fun saveBitmapToStorage(
    context: Context,
    bitmap: Bitmap,
    destFileName: String,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100,
    environment: String = Environment.DIRECTORY_PICTURES,
    folderName: String = "MyApp"
): Uri? {
    var mime = guessMime(destFileName)
    if (mime == "application/octet-stream") {
        mime = when (format) {
            Bitmap.CompressFormat.JPEG -> "image/jpeg"
            Bitmap.CompressFormat.WEBP, Bitmap.CompressFormat.WEBP_LOSSY, Bitmap.CompressFormat.WEBP_LOSSLESS -> "image/webp"
            else -> "image/png"
        }
    }

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveBitmapUsingMediaStore(
                context = context,
                bitmap = bitmap,
                fileName = destFileName,
                mime = mime,
                format = format,
                quality = quality,
                environment = environment,
                folderName = folderName
            )
        } else {
            saveBitmapLegacy(
                context = context,
                bitmap = bitmap,
                fileName = destFileName,
                mime = mime,
                format = format,
                quality = quality,
                environment = environment,
                folderName = folderName
            )
        }
    } catch (e: Exception) {
        Log.e("Saver", "saveBitmapToStorage error", e)
        null
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun saveBitmapUsingMediaStore(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
    mime: String,
    format: Bitmap.CompressFormat,
    quality: Int,
    environment: String,
    folderName: String
): Uri? {
    val cr = context.contentResolver

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mime)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "$environment/$folderName")
        put(MediaStore.MediaColumns.IS_PENDING, 1)
        put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis() / 1000)
    }

    val contentUri = when (environment) {
        Environment.DIRECTORY_DOWNLOADS -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        Environment.DIRECTORY_PICTURES -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        Environment.DIRECTORY_DOCUMENTS -> MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        Environment.DIRECTORY_MOVIES -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        Environment.DIRECTORY_MUSIC -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> when {
            mime.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mime.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            mime.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }
    }

    var uri: Uri? = null
    return try {
        uri = cr.insert(contentUri, values)
        if (uri != null) {
            cr.openOutputStream(uri)?.use { out ->
                if (!bitmap.compress(format, quality, out)) {
                    throw IOException("Bitmap.compress failed")
                }
            } ?: throw IOException("OpenOutputStream failed")

            // Clear pending
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            cr.update(uri, values, null, null)

            Log.d("Saver", "Bitmap saved successfully: $uri")
            uri
        } else {
            null
        }
    } catch (e: Exception) {
        Log.e("Saver", "saveBitmapUsingMediaStore error", e)
        uri?.let { runCatching { cr.delete(it, null, null) } }
        null
    }
}

@Suppress("DEPRECATION")
private fun saveBitmapLegacy(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
    mime: String,
    format: Bitmap.CompressFormat,
    quality: Int,
    environment: String,
    folderName: String
): Uri? {
    return try {
        val baseDir = when (environment) {
            Environment.DIRECTORY_DOWNLOADS -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            Environment.DIRECTORY_PICTURES -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            Environment.DIRECTORY_DOCUMENTS -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            )
            Environment.DIRECTORY_MOVIES -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            )
            Environment.DIRECTORY_MUSIC -> Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC
            )
            else -> when {
                mime.startsWith("image/") -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                mime.startsWith("video/") -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES
                )
                mime.startsWith("audio/") -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC
                )
                else -> Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
            }
        }

        val dir = File(baseDir, folderName).apply {
            if (!exists()) mkdirs()
        }
        val dst = File(dir, fileName)

        FileOutputStream(dst).use { out ->
            if (!bitmap.compress(format, quality, out)) {
                throw IOException("Bitmap.compress failed")
            }
        }

        MediaScannerConnection.scanFile(
            context,
            arrayOf(dst.absolutePath),
            arrayOf(mime),
            null
        )

        Log.d("Saver", "Bitmap saved successfully to: ${dst.absolutePath}")
        Uri.fromFile(dst)
    } catch (e: Exception) {
        Log.e("Saver", "saveBitmapLegacy error", e)
        null
    }
}


private fun guessMime(fileName: String): String {
    val extension = fileName.substringAfterLast('.', "").lowercase()
    return when (extension) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "bmp" -> "image/bmp"
        "webp" -> "image/webp"
        "heic", "heif" -> "image/heif"
        "ico" -> "image/x-icon"
        "svg" -> "image/svg+xml"
        "tiff", "tif" -> "image/tiff"
        "mp4" -> "video/mp4"
        "avi" -> "video/x-msvideo"
        "mkv" -> "video/x-matroska"
        "mov" -> "video/quicktime"
        "wmv" -> "video/x-ms-wmv"
        "flv" -> "video/x-flv"
        "webm" -> "video/webm"
        "m4v" -> "video/x-m4v"
        "3gp" -> "video/3gpp"
        "ts" -> "video/mp2t"
        "mpeg", "mpg" -> "video/mpeg"
        "mp3" -> "audio/mpeg"
        "wav" -> "audio/wav"
        "ogg" -> "audio/ogg"
        "flac" -> "audio/flac"
        "aac" -> "audio/aac"
        "m4a" -> "audio/mp4"
        "wma" -> "audio/x-ms-wma"
        "amr" -> "audio/amr"
        "aiff", "aif" -> "audio/aiff"
        "pdf" -> "application/pdf"
        "doc" -> "application/msword"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        "xls" -> "application/vnd.ms-excel"
        "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        "ppt" -> "application/vnd.ms-powerpoint"
        "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        "txt" -> "text/plain"
        "rtf" -> "application/rtf"
        "csv" -> "text/csv"
        "html", "htm" -> "text/html"
        "xml" -> "application/xml"
        "json" -> "application/json"
        "zip" -> "application/zip"
        "rar" -> "application/vnd.rar"
        "7z" -> "application/x-7z-compressed"
        "tar" -> "application/x-tar"
        "gz" -> "application/gzip"
        "apk" -> "application/vnd.android.package-archive"
        "exe" -> "application/x-msdownload"
        "dmg" -> "application/x-apple-diskimage"
        "ttf" -> "font/ttf"
        "otf" -> "font/otf"
        "woff" -> "font/woff"
        "woff2" -> "font/woff2"
        "js" -> "application/javascript"
        "css" -> "text/css"
        "java" -> "text/x-java-source"
        "kt" -> "text/x-kotlin"
        "py" -> "text/x-python"
        "php" -> "application/x-httpd-php"
        "c", "cpp", "h" -> "text/x-c"
        "swift" -> "text/x-swift"
        "db" -> "application/x-sqlite3"
        "sql" -> "application/sql"
        "epub" -> "application/epub+zip"
        "mobi" -> "application/x-mobipocket-ebook"
        "pem", "crt", "cer" -> "application/x-x509-ca-cert"
        "p12", "pfx" -> "application/x-pkcs12"
        "key" -> "application/x-pem-file"
        "ini", "conf", "cfg" -> "text/plain"
        "yaml", "yml" -> "application/x-yaml"
        "ai" -> "application/postscript"
        "eps" -> "application/postscript"
        "ps" -> "application/postscript"
        "obj" -> "model/obj"
        "stl" -> "model/stl"
        "fbx" -> "application/octet-stream"
        "blend" -> "application/x-blender"
        else -> "application/octet-stream"
    }
}

fun getSuggestedDirectoryForFile(fileName: String): String {
    val mime = guessMime(fileName)
    return when {
        mime.startsWith("image/") -> Environment.DIRECTORY_PICTURES
        mime.startsWith("video/") -> Environment.DIRECTORY_MOVIES
        mime.startsWith("audio/") -> Environment.DIRECTORY_MUSIC
        mime.startsWith("application/") || mime.startsWith("text/") -> Environment.DIRECTORY_DOCUMENTS
        else -> Environment.DIRECTORY_DOWNLOADS
    }
}

fun saveBytesToDownloads(
    context: Context,
    bytes: ByteArray,
    fileName: String,
    environment: String = Environment.DIRECTORY_DOWNLOADS,
    folderName: String = "Downloads"
): Uri? = ByteArrayInputStream(bytes).use {
    saveStreamToDownloads(context, it, environment = environment, fileName, folderName)
}

fun saveStreamToDownloads(
    context: Context,
    input: InputStream,
    environment: String = Environment.DIRECTORY_DOWNLOADS,
    fileName: String,
    folderName: String = "Downloads"
): Uri? {
    val mime = guessMime(fileName)
    val cr = context.contentResolver

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mime)
            put(MediaStore.Downloads.RELATIVE_PATH, "${environment}${File.separator}$folderName")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        var uri: Uri? = null
        try {
            uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                cr.openOutputStream(uri)?.use { out -> input.copyTo(out) }
                    ?: throw IOException("OpenOutputStream failed")
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                cr.update(uri, values, null, null)
            }
            uri
        } catch (e: Exception) {
            Log.e("Saver", "saveStreamToDownloads Q+ error", e)
            if (uri != null) runCatching { cr.delete(uri, null, null) }
            null
        }
    } else {
        try {
            @Suppress("DEPRECATION")
            val base =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val dir = File(base, folderName).apply { if (!exists()) mkdirs() }
            val dst = File(dir, fileName)
            FileOutputStream(dst).use { out -> input.copyTo(out) }
            val uri = Uri.fromFile(dst)
            MediaScannerConnection.scanFile(context, arrayOf(dst.absolutePath), arrayOf(mime), null)
            uri
        } catch (e: Exception) {
            Log.e("Saver", "saveStreamToDownloads pre-Q error", e)
            null
        }
    }
}


private fun Context.getMediaStorePath(uri: Uri): String {
    return try {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val projection = arrayOf(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    MediaStore.MediaColumns.DISPLAY_NAME
                )
                var path = ""

                contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val relativePath =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH))
                        val fileName =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                        path =
                            "${Environment.getExternalStorageDirectory()}/${relativePath}${fileName}"
                    }
                }
                path
            }
            else -> {
                uri.getRealPathFromUriLegacy(this) ?: ""
            }
        }
    } catch (e: Exception) {
        Log.e("FileUtils", "Error getting media path", e)
        ""
    }
}

private fun Uri.getRealPathFromUriLegacy(context: Context): String? {
    val uri = this
    return try {
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        val type = split[0]

                        if ("primary".equals(type, ignoreCase = true) && split.size > 1) {
                            "${Environment.getExternalStorageDirectory()}/${split[1]}"
                        } else {
                            null
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        try {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                id.toLong()
                            )
                            getDataColumn(context, contentUri, null, null)
                        } catch (e: NumberFormatException) {
                            // Handle raw file downloads
                            getDataColumn(context, uri, null, null)
                        }
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":")
                        if (split.size >= 2) {
                            val type = split[0]
                            val contentUri = when (type) {
                                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                else -> null
                            }
                            contentUri?.let {
                                getDataColumn(context, it, "_id=?", arrayOf(split[1]))
                            }
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                getDataColumn(context, uri, null, null)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                uri.path
            }
            else -> null
        }
    } catch (e: Exception) {
        Log.e("UriHelper", "Error getting real path from URI: $uri", e)
        null
    }
}

private fun getDataColumn(
    context: Context,
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    val column = "_data"
    val projection = arrayOf(column)

    return try {
        context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(column))
                } else null
            }
    } catch (e: Exception) {
        Log.e("FileUtils", "Error getting data column", e)
        null
    }
}

private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

object ShareThrottler {
    private var lastTs = 0L
    private const val GAP = 800L
    fun allow(): Boolean {
        val now = SystemClock.elapsedRealtime()
        return if (now - lastTs > GAP) {
            lastTs = now; true
        } else false
    }
}

fun Activity.shareFileWithPath(path: String, mime: String = "image/*") {
    if (!ShareThrottler.allow()) return
    val file = File(path)
    if (!file.exists()) return
    val uri = FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        file
    )
    val share = Intent(Intent.ACTION_SEND).apply {
        type = mime
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(share, "Share File"))
}

fun loadBitmapFromAssets(context: Context, fileName: String): Bitmap? {
    return try {
        val inputStream = context.assets.open(fileName)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}
@Throws(IOException::class)
fun getBitmapFromAssets(context: Context, fileName: String?): Bitmap {
    val assetManager = context.assets
    val istr = assetManager.open(fileName!!)
    val bitmap = BitmapFactory.decodeStream(istr)
    istr.close()
    return bitmap
}

fun shareFilesWithPaths(context: Context, paths: List<String>) {
    val uris = paths.mapNotNull { path ->
        val file = File(path)
        if (file.exists()) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } else {
            null
        }
    }

    if (uris.isNotEmpty()) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            type = "*/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Files"))
    } else {
        Toast.makeText(context, "No valid files to share", Toast.LENGTH_SHORT).show()
    }
}

fun Context.shareFileBitmap(fileName: String, bitmapCreate: Bitmap?) {
    bitmapCreate?.let { bitmap ->
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, fileName)
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val fileUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                file
            )
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/png"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            startActivity(Intent.createChooser(shareIntent, "Share Photo"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun Uri.getPathFromUri(context: Context): String? {
    return when (this.scheme) {
        "file" -> {
            path
        }
        "content" -> {
            try {
                when {
                    isGooglePhotosUri(this) -> {
                        this.lastPathSegment
                    }
                    isDownloadsDocument(this) -> {
                        getDownloadsPath(context, this)
                    }
                    isMediaDocument(this) -> {
                        getMediaPath(context, this)
                    }
                    else -> {
                        getContentPath(context, this)
                    }
                }
            } catch (e: Exception) {
                Log.e("FileUtils", "Error getting path from URI", e)
                null
            }
        }
        else -> null
    }
}

private fun getDownloadsPath(context: Context, uri: Uri): String? {
    return try {
        val id = DocumentsContract.getDocumentId(uri)
        when {
            id.startsWith("raw:") -> {
                val rawPath = id.substring(4)
                if (File(rawPath).exists()) rawPath else null
            }
            id.startsWith("msf:") -> {
                getContentPath(context, uri)
            }
            else -> {
                try {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id.toLong()
                    )
                    getContentPath(context, contentUri)
                } catch (e: NumberFormatException) {
                    getContentPath(context, uri)
                }
            }
        }
    } catch (e: Exception) {
        Log.e("UriHelper", "Error getting downloads path for URI: $uri", e)
        getContentPath(context, uri)
    }
}

private fun getContentPath(context: Context, uri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        cursor = context.contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME),
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val dataIndex = it.getColumnIndex(MediaStore.MediaColumns.DATA)
                if (dataIndex != -1) {
                    val path = it.getString(dataIndex)
                    if (path != null && File(path).exists()) {
                        return path
                    }
                }
                val displayNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    val displayName = it.getString(displayNameIndex)
                    if (displayName != null) {
                        val possiblePaths = arrayOf(
                            "${Environment.getExternalStorageDirectory()}/Download/$displayName",
                            "${Environment.getExternalStorageDirectory()}/Downloads/$displayName",
                            "/storage/emulated/0/Download/$displayName",
                            "/storage/emulated/0/Downloads/$displayName"
                        )

                        possiblePaths.forEach { path ->
                            if (File(path).exists()) {
                                return path
                            }
                        }
                    }
                }
            }
        }
        null
    } catch (e: Exception) {
        Log.e("UriHelper", "Error in getContentPath", e)
        null
    } finally {
        cursor?.close()
    }
}

private fun getMediaPath(context: Context, uri: Uri): String? {
    val id = DocumentsContract.getDocumentId(uri)
    val split = id.split(":")
    val type = split[0]
    val contentUri = when (type.lowercase()) {
        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        else -> null
    }

    return contentUri?.let {
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        getContentPath(context, it, selection, selectionArgs)
    }
}

private fun getContentPath(
    context: Context,
    uri: Uri,
    selection: String? = null,
    selectionArgs: Array<String>? = null
): String? {
    return context.contentResolver.query(
        uri,
        arrayOf(MediaStore.Images.Media.DATA),
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.getString(columnIndex)
        } else null
    }
}

private fun copyFileToTemp(context: Context, uri: Uri): String? {
    return try {
        val tempFile = File(
            context.cacheDir,
            "temp_${System.currentTimeMillis()}_${uri.lastPathSegment}"
        )
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile.absolutePath
    } catch (e: Exception) {
        Log.e("FileUtils", "Error copying file", e)
        null
    }
}

private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}
private fun getMimeType(path: String): String {
    return MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path))
        ?: "application/octet-stream"
}

private fun Cursor.getStringOrNull(columnIndex: Int): String? {
    return try {
        getString(columnIndex)
    } catch (e: Exception) {
        null
    }
}

private fun Cursor.getLongOrNull(columnIndex: Int): Long? {
    return try {
        getLong(columnIndex)
    } catch (e: Exception) {
        null
    }
}

