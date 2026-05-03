package com.klodit.almizan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.klodit.almizan.data.tender.TenderRepository
import com.klodit.almizan.model.tender.Tender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TenderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TenderRepository()

    private val _tenders          = MutableStateFlow<List<Tender>>(emptyList())
    val tenders: StateFlow<List<Tender>> = _tenders

    private val _isLoading        = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error            = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchTenders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value     = null
            repository.fetchTenders()
                .onSuccess { _tenders.value = it }
                .onFailure { _error.value   = it.message }
            _isLoading.value = false
        }
    }
}