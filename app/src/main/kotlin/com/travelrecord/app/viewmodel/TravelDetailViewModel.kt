package com.travelrecord.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.data.entity.TravelProcess
import com.travelrecord.app.data.entity.TravelRecord
import com.travelrecord.app.data.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel for travel record detail screen
 * Manages travel record details, processes, and expenses
 */
class TravelDetailViewModel(
    private val repository: TravelRepository
) : ViewModel() {
    
    // LiveData for travel record
    private val _travelRecord = MutableLiveData<TravelRecord?>()
    val travelRecord: LiveData<TravelRecord?> = _travelRecord
    
    // LiveData for travel processes
    private val _processes = MutableLiveData<List<TravelProcess>>()
    val processes: LiveData<List<TravelProcess>> = _processes
    
    // LiveData for expense items
    private val _expenses = MutableLiveData<List<ExpenseItem>>()
    val expenses: LiveData<List<ExpenseItem>> = _expenses
    
    // LiveData for loading states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _isProcessLoading = MutableLiveData<Boolean>()
    val isProcessLoading: LiveData<Boolean> = _isProcessLoading
    
    private val _isExpenseLoading = MutableLiveData<Boolean>()
    val isExpenseLoading: LiveData<Boolean> = _isExpenseLoading
    
    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // LiveData for success messages
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    // LiveData for total expense
    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense
    
    // Current travel record ID
    private var currentTravelId: String? = null
    
    /**
     * Load travel record details by ID
     */
    fun loadTravelDetail(id: String) {
        currentTravelId = id
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Load travel record
                val record = repository.getTravelRecordById(id)
                _travelRecord.value = record
                
                if (record != null) {
                    // Load processes and expenses
                    loadProcesses(id)
                    loadExpenses(id)
                } else {
                    _errorMessage.value = "旅游记录不存在"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "加载旅游详情失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Load travel processes for current record
     */
    private suspend fun loadProcesses(travelId: String) {
        try {
            _isProcessLoading.value = true
            val processes = repository.getProcessesByTravelId(travelId)
            _processes.value = processes
        } catch (e: Exception) {
            _errorMessage.value = "加载过程记录失败: ${e.message}"
            _processes.value = emptyList()
        } finally {
            _isProcessLoading.value = false
        }
    }
    
    /**
     * Load expense items for current record
     */
    private suspend fun loadExpenses(travelId: String) {
        try {
            _isExpenseLoading.value = true
            val expenses = repository.getExpensesByTravelId(travelId)
            _expenses.value = expenses
            
            // Calculate total expense
            val total = expenses.sumOf { it.amount }
            _totalExpense.value = total
            
        } catch (e: Exception) {
            _errorMessage.value = "加载开销记录失败: ${e.message}"
            _expenses.value = emptyList()
            _totalExpense.value = 0.0
        } finally {
            _isExpenseLoading.value = false
        }
    }
    
    /**
     * Add a new travel process
     */
    fun addProcess(description: String) {
        val travelId = currentTravelId ?: return
        
        if (description.isBlank()) {
            _errorMessage.value = "过程描述不能为空"
            return
        }
        
        viewModelScope.launch {
            try {
                val process = TravelProcess(
                    travelRecordId = travelId,
                    description = description,
                    timestamp = System.currentTimeMillis()
                )
                
                val result = repository.insertTravelProcess(process)
                if (result > 0) {
                    _successMessage.value = "过程记录添加成功"
                    loadProcesses(travelId)
                } else {
                    _errorMessage.value = "添加过程记录失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "添加过程记录失败: ${e.message}"
            }
        }
    }
    
    /**
     * Update a travel process
     */
    fun updateProcess(process: TravelProcess) {
        viewModelScope.launch {
            try {
                val result = repository.updateTravelProcess(process)
                if (result > 0) {
                    _successMessage.value = "过程记录更新成功"
                    currentTravelId?.let { loadProcesses(it) }
                } else {
                    _errorMessage.value = "更新过程记录失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "更新过程记录失败: ${e.message}"
            }
        }
    }
    
    /**
     * Delete a travel process
     */
    fun deleteProcess(processId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteTravelProcess(processId)
                if (result > 0) {
                    _successMessage.value = "过程记录删除成功"
                    currentTravelId?.let { loadProcesses(it) }
                } else {
                    _errorMessage.value = "删除过程记录失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "删除过程记录失败: ${e.message}"
            }
        }
    }
    
    /**
     * Add a new expense item
     */
    fun addExpense(amount: Double, category: String, description: String) {
        val travelId = currentTravelId ?: return
        
        if (amount <= 0) {
            _errorMessage.value = "金额必须大于0"
            return
        }
        
        if (category.isBlank()) {
            _errorMessage.value = "类别不能为空"
            return
        }
        
        if (description.isBlank()) {
            _errorMessage.value = "描述不能为空"
            return
        }
        
        viewModelScope.launch {
            try {
                val expense = ExpenseItem(
                    travelRecordId = travelId,
                    amount = amount,
                    category = category,
                    description = description,
                    timestamp = System.currentTimeMillis()
                )
                
                val result = repository.insertExpenseItem(expense)
                if (result > 0) {
                    _successMessage.value = "开销记录添加成功"
                    loadExpenses(travelId)
                    // Reload travel record to get updated total expense
                    loadTravelDetail(travelId)
                } else {
                    _errorMessage.value = "添加开销记录失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "添加开销记录失败: ${e.message}"
            }
        }
    }
    
    /**
     * Update an expense item
     */
    fun updateExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            try {
                val result = repository.updateExpenseItem(expense)
                if (result > 0) {
                    _successMessage.value = "开销记录更新成功"
                    currentTravelId?.let { 
                        loadExpenses(it)
                        // Reload travel record to get updated total expense
                        loadTravelDetail(it)
                    }
                } else {
                    _errorMessage.value = "更新开销记录失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "更新开销记录失败: ${e.message}"
            }
        }
    }
    
    /**
     * Delete an expense item
     */
    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteExpenseItem(expenseId)
                if (result > 0) {
                    _successMessage.value = "开销记录删除成功"
                    currentTravelId?.let { 
                        loadExpenses(it)
                        // Reload travel record to get updated total expense
                        loadTravelDetail(it)
                    }
                } else {
                    _errorMessage.value = "删除开销记录失败"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "删除开销记录失败: ${e.message}"
            }
        }
    }
    
    /**
     * Get processes as Flow for reactive updates
     */
    fun getProcessesFlow(travelId: String): Flow<List<TravelProcess>> {
        return repository.getProcessesByTravelIdFlow(travelId)
    }
    
    /**
     * Get expenses as Flow for reactive updates
     */
    fun getExpensesFlow(travelId: String): Flow<List<ExpenseItem>> {
        return repository.getExpensesByTravelIdFlow(travelId)
    }
    
    /**
     * Refresh all data
     */
    fun refresh() {
        currentTravelId?.let { loadTravelDetail(it) }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Clear success message
     */
    fun clearSuccess() {
        _successMessage.value = null
    }
    
    /**
     * Get current travel record ID
     */
    fun getCurrentTravelId(): String? {
        return currentTravelId
    }
}