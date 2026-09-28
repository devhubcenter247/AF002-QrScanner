package com.dongbv.alf_03_iptv.base

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

fun Context.copyToClipboard(text: String, label: String = "Copy",callback: ()-> Unit = {}) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    callback()
}
fun Context.getFromClipboard(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = clipboard.primaryClip
    return if (clip != null && clip.itemCount > 0) {
        clip.getItemAt(0).text?.toString()
    } else null
}
fun Context.hasClipboardText(): Boolean {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return clipboard.hasPrimaryClip() && clipboard.primaryClipDescription?.hasMimeType("text/plain") == true
}
fun Context.clearClipboard() {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        clipboard.clearPrimaryClip()
    } else {
        val clip = ClipData.newPlainText("", "")
        clipboard.setPrimaryClip(clip)
    }
}
