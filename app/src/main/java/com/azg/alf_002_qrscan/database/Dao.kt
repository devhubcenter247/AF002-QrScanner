package com.azg.alf_002_qrscan.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.azg.alf_002_qrscan.database.model.CreateType
import com.azg.alf_002_qrscan.database.model.HistoryApp
import kotlinx.coroutines.flow.Flow
@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HistoryApp)
    @Query("UPDATE HistoryApp SET isDelete = 1 WHERE hisId = :hisId")
    suspend fun softDelete(hisId: Int)

    @Query("SELECT * FROM HistoryApp WHERE isDelete = 0 ORDER BY scanTime DESC")
    fun getAllData(): Flow<List<HistoryApp>>

    @Query("SELECT * FROM HistoryApp WHERE isDelete = 0 AND createType = :createType ORDER BY scanTime DESC")
    fun getListBySort(createType: CreateType): Flow<List<HistoryApp>>
    
    @Query("SELECT * FROM HistoryApp WHERE isDelete = 0 AND createType = :createType AND isFavorite=1 ORDER BY scanTime DESC")
    fun getListFavorite(createType: CreateType): Flow<List<HistoryApp>>

    @Query("SELECT * FROM HistoryApp WHERE isDelete = 0 AND isFavorite=1 ORDER BY scanTime DESC")
    fun getAllFavorite(): Flow<List<HistoryApp>>
    @Query("SELECT * FROM HistoryApp WHERE scanTime LIKE :date")
    fun getHistoryAppByDateFlow(date: String): Flow<List<HistoryApp>> // New: flow version

    @Query("SELECT * FROM HistoryApp WHERE hisId = :id")
    fun findHistoryByIdFlow(id: Int): Flow<HistoryApp> // New: observe one record

    @Query("SELECT COUNT(*) FROM HistoryApp")
    fun getTotalScanFlow(): Flow<Int>

    @Query("SELECT * FROM HistoryApp WHERE scanTime LIKE :date")
    suspend fun getHistoryAppByDate(date: String): MutableList<HistoryApp>

    @Query("SELECT COUNT(*) FROM HistoryApp")
    suspend fun getTotalScan(): Int

    @Query("DELETE FROM HistoryApp WHERE hisId = :hisId")
    suspend fun deleteHistory(hisId: Int): Int

    @Insert
    suspend fun insertWithId(history: HistoryApp): Long

    @Query("UPDATE HistoryApp SET qrName = :currentName WHERE hisId = :saveQrHistory")
    suspend fun updateName(currentName: String, saveQrHistory: Int)

    @Query("UPDATE HistoryApp SET scanImage = :scanImage WHERE hisId = :hisId")
    suspend fun updateImageHistory(hisId: Int, scanImage: String)

    @Query("SELECT * FROM HistoryApp WHERE hisId = :toInt")
    suspend fun findHistoryById(toInt: Int): HistoryApp

    @Query("UPDATE HistoryApp SET isFavorite=:value WHERE hisId = :hisId")
    suspend fun updateFavorite(value: Boolean, hisId: Int)
    @Query("SELECT hisId FROM HistoryApp WHERE scanImage = :path")
    suspend fun findIdByPath(path: String): Int

    @Query("UPDATE HistoryApp SET type = :type WHERE hisId = :hisId")
    suspend fun updateType(type: com.create.custom.qr.QrType, hisId: Int)

    @Query("SELECT * FROM HistoryApp WHERE batchId = :batchId AND isDelete = 0 ORDER BY scanTime DESC")
    fun getByBatchId(batchId: String): Flow<List<HistoryApp>>
}


