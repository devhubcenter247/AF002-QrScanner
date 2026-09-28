package com.azg.alf_002_qrscan.database

import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.HistoryApp
import com.create.custom.qr.QrType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(
    private val historyDao: HistoryDao
) {
    suspend fun insert(yourEntity: HistoryApp) {
        historyDao.insert(yourEntity)
    }

    suspend fun insertWithId(history: HistoryApp): Long {
        return historyDao.insertWithId(history)
    }


    suspend fun deleteHistory(item: HistoryApp) {
        historyDao.deleteHistory(item.hisId)
    }

    suspend fun removeHistory(item: Int) = historyDao.deleteHistory(item)
    suspend fun findHistoryById(toInt: Int) = historyDao.findHistoryById(toInt)
    suspend fun updateFavorite(value: HistoryApp) {
        historyDao.updateFavorite(value.isFavorite,value.hisId)
    }

    suspend fun findIdByPath(path: String): Int = historyDao.findIdByPath(path)
    suspend fun toggleFavorite(app: HistoryApp) = withContext(Dispatchers.IO) {
        val updateType= !app.isFavorite
        historyDao.updateFavorite(updateType,app.hisId)
    }

    suspend fun updateFavorite(value: Boolean, hisId: Int) = withContext(Dispatchers.IO) {
        historyDao.updateFavorite(value, hisId)
    }

    suspend fun deleteHistory(hisId: Int) = withContext(Dispatchers.IO) {
        historyDao.deleteHistory(hisId)
    }

    suspend fun updateName(name: String, hisId: Int) = withContext(Dispatchers.IO) {
        historyDao.updateName(name, hisId)
    }

    suspend fun updateType(type: QrType, hisId: Int) = withContext(Dispatchers.IO) {
        historyDao.updateType(type, hisId)
    }

    suspend fun updateScanImage(hisId: Int, scanImage: String) = withContext(Dispatchers.IO) {
        historyDao.updateImageHistory(hisId, scanImage)
    }

    val allData: Flow<List<HistoryApp>> = historyDao.getAllData()

    val allHistoryScan :Flow<List<HistoryApp>> = historyDao.getListBySort(CreateType.SCAN)
    val allHistoryCreate :Flow<List<HistoryApp>> = historyDao.getListBySort(CreateType.CREATE)
    
    val allFavoriteCreate :Flow<List<HistoryApp>> = historyDao.getListFavorite(CreateType.CREATE)
    val allFavoriteScan :Flow<List<HistoryApp>> = historyDao.getAllFavorite()

    fun getByBatchId(batchId: String): Flow<List<HistoryApp>> = historyDao.getByBatchId(batchId)
}