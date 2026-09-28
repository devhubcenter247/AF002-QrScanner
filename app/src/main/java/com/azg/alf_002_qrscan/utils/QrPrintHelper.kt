package com.azg.alf_002_qrscan.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.print.pdf.PrintedPdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Number of QR copies laid out per A4 page. Grid dimensions are derived from the
 * count (e.g. 4 -> 2x2, 9 -> 3x3) rather than hardcoded per option.
 */
enum class QrPrintLayout(val itemsPerPage: Int) {
    ONE(1),
    FOUR(4),
    NINE(9),
    SIXTEEN(16)
}

fun printQrCode(context: Activity, qrBitmap: Bitmap, layout: QrPrintLayout, jobName: String) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val adapter = QrPrintDocumentAdapter(context, qrBitmap, layout.itemsPerPage, jobName)
    val attributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(PrintAttributes.Resolution("qr_print", "QR Print", 300, 300))
        .setMinMargins(PrintAttributes.Margins(200, 200, 200, 200))
        .build()
    printManager.print(jobName, adapter, attributes)
}

private class QrPrintDocumentAdapter(
    private val context: Context,
    private val qrBitmap: Bitmap,
    private val itemsPerPage: Int,
    private val jobName: String
) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null
    private var pageWidth = 0
    private var pageHeight = 0

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        pdfDocument = PrintedPdfDocument(context, newAttributes)
        val resolution = newAttributes.resolution
        val mediaSize = newAttributes.mediaSize
        val hDpi = resolution?.horizontalDpi ?: 300
        val vDpi = resolution?.verticalDpi ?: 300
        pageWidth = ((mediaSize?.widthMils ?: 8267) * hDpi) / 1000
        pageHeight = ((mediaSize?.heightMils ?: 11692) * vDpi) / 1000

        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val info = PrintDocumentInfo.Builder(jobName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)
            .build()
        callback.onLayoutFinished(info, oldAttributes != newAttributes)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        val document = pdfDocument ?: return
        val page = document.startPage(0)
        drawQrGrid(page.canvas)
        document.finishPage(page)

        try {
            FileOutputStream(destination.fileDescriptor).use { out ->
                document.writeTo(out)
            }
        } catch (e: IOException) {
            callback.onWriteFailed(e.message)
            return
        } finally {
            document.close()
            pdfDocument = null
        }
        callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
    }

    private fun drawQrGrid(canvas: android.graphics.Canvas) {
        val columns = ceil(sqrt(itemsPerPage.toDouble())).toInt().coerceAtLeast(1)
        val rows = ceil(itemsPerPage.toDouble() / columns).toInt().coerceAtLeast(1)
        val margin = pageWidth * 0.04f
        val cellWidth = (pageWidth - margin * 2) / columns
        val cellHeight = (pageHeight - margin * 2) / rows
        val qrSize = min(cellWidth, cellHeight) * 0.8f

        repeat(itemsPerPage) { index ->
            val col = index % columns
            val row = index / columns
            val cellLeft = margin + col * cellWidth
            val cellTop = margin + row * cellHeight
            val qrLeft = cellLeft + (cellWidth - qrSize) / 2
            val qrTop = cellTop + (cellHeight - qrSize) / 2
            val destRect = RectF(qrLeft, qrTop, qrLeft + qrSize, qrTop + qrSize)
            canvas.drawBitmap(qrBitmap, null, destRect, null)
        }
    }
}
