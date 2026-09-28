package com.azg.alf_002_qrscan.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.create.custom.qr.QrType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "HistoryApp")
data class HistoryApp(
    @PrimaryKey(autoGenerate = true)
    var hisId: Int = 0,
    var scanTime: Long = System.currentTimeMillis(),
    var scanType: ScanType = ScanType.QRCODE,
    var content: String = "",
    var scanImage: String = "",
    var type: QrType = QrType.Text,
    var qrName: String = "",
    var createType: CreateType = CreateType.CREATE,
    var isFavorite: Boolean = false,
    var isDelete: Boolean = false,
    var batchId: String? = null,
) : Parcelable {
    companion object {
        val Empty = HistoryApp(
            type = QrType.Demo,
            content = "46464616164561261"
        )
    }
}
