package com.azg.alf_002_qrscan.viewmodel

import com.azg.alf_002_qrscan.database.HistoryDao
import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.azg.alf_002_qrscan.database.model.ScanType
import com.create.custom.qr.QrType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Test data for different QR types
val fakeHistoryList = listOf(
    // Website QR codes
    HistoryApp(
        hisId = 1,
        scanTime = System.currentTimeMillis() - 86400000, // 1 day ago
        scanType = ScanType.QRCODE,
        content = "https://www.google.com",
        type = QrType.Website,
        qrName = "Google",
        createType = CreateType.SCAN,
        isFavorite = true
    ),
    HistoryApp(
        hisId = 2,
        scanTime = System.currentTimeMillis() - 172800000, // 2 days ago
        scanType = ScanType.QRCODE,
        content = "https://github.com/android",
        type = QrType.Website,
        qrName = "GitHub Android",
        createType = CreateType.CREATE,
        isFavorite = false
    ),
    // WiFi QR code
    HistoryApp(
        hisId = 3,
        scanTime = System.currentTimeMillis() - 259200000, // 3 days ago
        scanType = ScanType.QRCODE,
        content = "WIFI:T:WPA;S:MyNetwork;P:password123;;",
        type = QrType.Wifi,
        qrName = "Home WiFi",
        createType = CreateType.CREATE,
        isFavorite = true
    ),
    // Contact QR code
    HistoryApp(
        hisId = 4,
        scanTime = System.currentTimeMillis() - 345600000, // 4 days ago
        scanType = ScanType.QRCODE,
        content = "BEGIN:VCARD\nVERSION:3.0\nN:Doe;John\nFN:John Doe\nTEL:+1234567890\nEMAIL:john.doe@email.com\nEND:VCARD",
        type = QrType.Contact,
        qrName = "John Doe Contact",
        createType = CreateType.SCAN,
        isFavorite = false
    ),
    // Email QR code
    HistoryApp(
        hisId = 5,
        scanTime = System.currentTimeMillis() - 432000000, // 5 days ago
        scanType = ScanType.QRCODE,
        content = "mailto:support@example.com?subject=Help&body=I need assistance",
        type = QrType.Email,
        qrName = "Support Email",
        createType = CreateType.CREATE,
        isFavorite = false
    ),
    // Phone QR code
    HistoryApp(
        hisId = 6,
        scanTime = System.currentTimeMillis() - 518400000, // 6 days ago
        scanType = ScanType.QRCODE,
        content = "tel:+1234567890",
        type = QrType.Phone,
        qrName = "Office Phone",
        createType = CreateType.SCAN,
        isFavorite = true
    ),
    // SMS/Message QR code
    HistoryApp(
        hisId = 7,
        scanTime = System.currentTimeMillis() - 604800000, // 7 days ago
        scanType = ScanType.QRCODE,
        content = "sms:+1234567890?body=Hello from QR",
        type = QrType.Message,
        qrName = "Quick Message",
        createType = CreateType.CREATE,
        isFavorite = false
    ),
    // Location QR code
    HistoryApp(
        hisId = 8,
        scanTime = System.currentTimeMillis() - 691200000, // 8 days ago
        scanType = ScanType.QRCODE,
        content = "geo:37.7749,-122.4194?q=San Francisco",
        type = QrType.Location,
        qrName = "San Francisco",
        createType = CreateType.SCAN,
        isFavorite = false
    ),
    // Text QR code
    HistoryApp(
        hisId = 9,
        scanTime = System.currentTimeMillis() - 777600000, // 9 days ago
        scanType = ScanType.QRCODE,
        content = "Hello, this is a simple text QR code for testing purposes.",
        type = QrType.Text,
        qrName = "Sample Text",
        createType = CreateType.CREATE,
        isFavorite = false
    ),
    // Event QR code
    HistoryApp(
        hisId = 10,
        scanTime = System.currentTimeMillis() - 864000000, // 10 days ago
        scanType = ScanType.QRCODE,
        content = "BEGIN:VEVENT\nSUMMARY:Team Meeting\nDTSTART:20250115T100000\nDTEND:20250115T110000\nLOCATION:Conference Room A\nEND:VEVENT",
        type = QrType.Event,
        qrName = "Team Meeting",
        createType = CreateType.CREATE,
        isFavorite = true
    ),
    // Barcode
    HistoryApp(
        hisId = 11,
        scanTime = System.currentTimeMillis() - 950400000, // 11 days ago
        scanType = ScanType.BARCODE,
        content = "5901234123457",
        type = QrType.Barcode,
        qrName = "Product Barcode",
        createType = CreateType.SCAN,
        isFavorite = false
    ),
    // Address QR code
    HistoryApp(
        hisId = 12,
        scanTime = System.currentTimeMillis() - 1036800000, // 12 days ago
        scanType = ScanType.QRCODE,
        content = "123 Main Street, New York, NY 10001, USA",
        type = QrType.Address,
        qrName = "Office Address",
        createType = CreateType.CREATE,
        isFavorite = false
    )
)

val fakeDataHistoryDao = object : HistoryDao {
    private val dataList = fakeHistoryList.toMutableList()

    override suspend fun insert(history: HistoryApp) {
        dataList.add(history)
    }

    override suspend fun softDelete(hisId: Int) {
        dataList.find { it.hisId == hisId }?.isDelete = true
    }

    override fun getAllData(): Flow<List<HistoryApp>> =
        flowOf(dataList.filter { !it.isDelete })

    override fun getListBySort(createType: CreateType): Flow<List<HistoryApp>> =
        flowOf(dataList.filter { it.createType == createType && !it.isDelete })

    override fun getListFavorite(createType: CreateType): Flow<List<HistoryApp>> =
        flowOf(dataList.filter { it.createType == createType && it.isFavorite && !it.isDelete })

    override fun getAllFavorite(): Flow<List<HistoryApp>> = flowOf(fakeHistoryList)

    override fun getHistoryAppByDateFlow(date: String): Flow<List<HistoryApp>> =
        flowOf(dataList.filter { !it.isDelete })

    override fun findHistoryByIdFlow(id: Int): Flow<HistoryApp> =
        flowOf(dataList.find { it.hisId == id } ?: HistoryApp())

    override fun getTotalScanFlow(): Flow<Int> =
        flowOf(dataList.count { it.createType == CreateType.SCAN && !it.isDelete })

    override suspend fun getHistoryAppByDate(date: String): MutableList<HistoryApp> =
        dataList.filter { !it.isDelete }.toMutableList()

    override suspend fun getTotalScan(): Int =
        dataList.count { it.createType == CreateType.SCAN && !it.isDelete }

    override suspend fun deleteHistory(hisId: Int): Int {
        val index = dataList.indexOfFirst { it.hisId == hisId }
        return if (index >= 0) {
            dataList.removeAt(index)
            1
        } else 0
    }

    override suspend fun insertWithId(history: HistoryApp): Long {
        val newId = (dataList.maxOfOrNull { it.hisId } ?: 0) + 1
        dataList.add(history.copy(hisId = newId))
        return newId.toLong()
    }

    override suspend fun updateName(currentName: String, saveQrHistory: Int) {
        dataList.find { it.hisId == saveQrHistory }?.qrName = currentName
    }

    override suspend fun updateImageHistory(hisId: Int, scanImage: String) {
        dataList.find { it.hisId == hisId }?.scanImage = scanImage
    }

    override suspend fun findHistoryById(toInt: Int): HistoryApp =
        dataList.find { it.hisId == toInt } ?: HistoryApp()

    override suspend fun updateFavorite(value: Boolean, hisId: Int) {
        dataList.find { it.hisId == hisId }?.isFavorite = value
    }

    override suspend fun findIdByPath(path: String): Int =
        dataList.find { it.scanImage == path }?.hisId ?: -1

    override suspend fun updateType(type: QrType, hisId: Int) = Unit

    override fun getByBatchId(batchId: String): Flow<List<HistoryApp>> =
        flowOf(dataList.filter { it.batchId == batchId && !it.isDelete })
}