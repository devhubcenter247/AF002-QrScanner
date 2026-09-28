package com.azg.alf_002_qrscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azg.alf_002_qrscan.database.AppRepository
import com.azg.alf_002_qrscan.database.model.HistoryApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class HomeViewModel(private val appRepository: AppRepository) : ViewModel() {

    val listQrHistory: StateFlow<List<HistoryApp>> = appRepository.allData.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val listQrFavorite: StateFlow<List<HistoryApp>> = appRepository.allFavoriteScan.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Alias for history list
    val historyList: StateFlow<List<HistoryApp>> = listQrHistory

    // Flag to indicate history change
    var isChangeHistory: Boolean = false

    fun addToFavorite(item: HistoryApp) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.updateFavorite(true, item.hisId)
        }
    }

    fun removeFavorite(hisId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.updateFavorite(false, hisId)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteHistory(hisId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.deleteHistory(hisId)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun updateHistoryName(name: String, hisId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.updateName(name, hisId)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}