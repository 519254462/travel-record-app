package com.travelrecord.app.data.repository

import androidx.lifecycle.LiveData
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.data.entity.TravelProcess
import com.travelrecord.app.data.entity.TravelRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for travel-related data operations
 * Provides a clean API for data access across the application
 */
interface TravelRepository {
    
    // Travel Record operations
    suspend fun getAllTravelRecords(): List<TravelRecord>
    fun getAllTravelRecordsLiveData(): LiveData<List<TravelRecord>>
    fun getAllTravelRecordsFlow(): Flow<List<TravelRecord>>
    suspend fun getTravelRecordById(id: String): TravelRecord?
    fun getTravelRecordByIdLiveData(id: String): LiveData<TravelRecord?>
    suspend fun searchTravelRecords(query: String): List<TravelRecord>
    suspend fun getTravelRecordsByDateRange(startDate: Long, endDate: Long): List<TravelRecord>
    suspend fun insertTravelRecord(record: TravelRecord): Long
    suspend fun updateTravelRecord(record: TravelRecord): Int
    suspend fun deleteTravelRecord(id: String): Int
    suspend fun getTravelRecordCount(): Int
    suspend fun getTotalExpenseSum(): Double
    
    // Travel Process operations
    suspend fun getProcessesByTravelId(travelId: String): List<TravelProcess>
    fun getProcessesByTravelIdLiveData(travelId: String): LiveData<List<TravelProcess>>
    fun getProcessesByTravelIdFlow(travelId: String): Flow<List<TravelProcess>>
    suspend fun getTravelProcessById(id: String): TravelProcess?
    suspend fun searchTravelProcesses(query: String): List<TravelProcess>
    suspend fun insertTravelProcess(process: TravelProcess): Long
    suspend fun updateTravelProcess(process: TravelProcess): Int
    suspend fun deleteTravelProcess(id: String): Int
    suspend fun getProcessCountByTravelId(travelId: String): Int
    
    // Expense Item operations
    suspend fun getExpensesByTravelId(travelId: String): List<ExpenseItem>
    fun getExpensesByTravelIdLiveData(travelId: String): LiveData<List<ExpenseItem>>
    fun getExpensesByTravelIdFlow(travelId: String): Flow<List<ExpenseItem>>
    suspend fun getExpenseItemById(id: String): ExpenseItem?
    suspend fun getExpensesByCategory(travelId: String, category: String): List<ExpenseItem>
    suspend fun searchExpenseItems(query: String): List<ExpenseItem>
    suspend fun insertExpenseItem(expense: ExpenseItem): Long
    suspend fun updateExpenseItem(expense: ExpenseItem): Int
    suspend fun deleteExpenseItem(id: String): Int
    suspend fun getTotalExpenseByTravelId(travelId: String): Double
    suspend fun getExpenseCountByTravelId(travelId: String): Int
    suspend fun getAllCategories(): List<String>
    
    // Composite operations
    suspend fun insertTravelRecordWithProcessesAndExpenses(
        record: TravelRecord,
        processes: List<TravelProcess> = emptyList(),
        expenses: List<ExpenseItem> = emptyList()
    ): Long
    
    suspend fun deleteTravelRecordWithAllData(id: String): Int
    suspend fun updateTravelRecordTotalExpense(id: String): Int
    suspend fun exportTravelData(): List<TravelRecord>
    suspend fun importTravelData(records: List<TravelRecord>): Int
    suspend fun clearAllData(): Int
}