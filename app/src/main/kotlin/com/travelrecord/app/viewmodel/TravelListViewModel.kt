package com.travelrecord.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelrecord.app.data.entity.TravelRecord
import com.travelrecord.app.data.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel for travel records list screen
 * Manages travel records data and UI state for the list view
 */
class TravelListViewModel(
    private val repository: TravelRepository
) : ViewModel() {
    
    // LiveData for travel records list
    private val _travelRecords = MutableLiveData<List<TravelRecord>>()
    val travelRecords: LiveData<List<TravelRecord>> = _travelRecords
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // LiveData for search query
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    // LiveData for total expense sum
    private val _totalExpenseSum = MutableLiveData<Double>()
    val totalExpenseSum: LiveData<Double> = _totalExpenseSum
    
    // LiveData for record count
    private val _recordCount = MutableLiveData<Int>()
    val recordCount: LiveData<Int> = _recordCount
    
    // Flow for reactive data updates
    val travelRecordsFlow: Flow<List<TravelRecord>> = repository.getAllTravelRecordsFlow()
    
    init {
        loadTravelRecords()
    }
    
    /**
     * Load all travel records from repository
     */
    fun loadTravelRecords() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val records = repository.getAllTravelRecords()
                _travelRecords.value = records
                
                // Update statistics
                updateStatistics()
                
            } catch (e: Exception) {
                _errorMessage.value = "加载旅游记录失败: ${e.message}"
                _travelRecords.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Search travel records by query
     */
    fun searchTravelRecords(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _searchQuery.value = query
                
                val records = if (query.isBlank()) {
                    repository.getAllTravelRecords()
                } else {
                    repository.searchTravelRecords(query)
                }
                
                _travelRecords.value = records
                
            } catch (e: Exception) {
                _errorMessage.value = "搜索失败: ${e.message}"
                _travelRecords.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Get travel records by date range
     */
    fun getTravelRecordsByDateRange(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val records = repository.getTravelRecordsByDateRange(startDate, endDate)
                _travelRecords.value = records
                
            } catch (e: Exception) {
                _errorMessage.value = "按日期筛选失败: ${e.message}"
                _travelRecords.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Delete a travel record by ID
     */
    fun deleteTravelRecord(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val result = repository.deleteTravelRecordWithAllData(id)
                if (result > 0) {
                    // Reload records after successful deletion
                    loadTravelRecords()
                } else {
                    _errorMessage.value = "删除失败：记录不存在"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "删除失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear search and show all records
     */
    fun clearSearch() {
        _searchQuery.value = ""
        loadTravelRecords()
    }
    
    /**
     * Refresh data
     */
    fun refresh() {
        loadTravelRecords()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Update statistics (total expense sum and record count)
     */
    private suspend fun updateStatistics() {
        try {
            val totalSum = repository.getTotalExpenseSum()
            val count = repository.getTravelRecordCount()
            
            _totalExpenseSum.value = totalSum
            _recordCount.value = count
            
        } catch (e: Exception) {
            // Statistics update failure shouldn't affect main functionality
            _totalExpenseSum.value = 0.0
            _recordCount.value = 0
        }
    }
    
    /**
     * Get current search query
     */
    fun getCurrentSearchQuery(): String {
        return _searchQuery.value ?: ""
    }
    
    /**
     * Check if currently searching
     */
    fun isSearching(): Boolean {
        return !getCurrentSearchQuery().isBlank()
    }
}