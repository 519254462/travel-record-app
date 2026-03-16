package com.travelrecord.app.data.repository

import androidx.lifecycle.LiveData
import com.travelrecord.app.data.dao.ExpenseItemDao
import com.travelrecord.app.data.dao.TravelProcessDao
import com.travelrecord.app.data.dao.TravelRecordDao
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.data.entity.TravelProcess
import com.travelrecord.app.data.entity.TravelRecord
import kotlinx.coroutines.flow.Flow
/**
 * Implementation of TravelRepository interface
 * Coordinates data operations across multiple DAOs
 */
class TravelRepositoryImpl constructor(
    private val travelRecordDao: TravelRecordDao,
    private val travelProcessDao: TravelProcessDao,
    private val expenseItemDao: ExpenseItemDao
) : TravelRepository {
    
    // Travel Record operations
    override suspend fun getAllTravelRecords(): List<TravelRecord> {
        return travelRecordDao.getAllTravelRecords()
    }
    
    override fun getAllTravelRecordsLiveData(): LiveData<List<TravelRecord>> {
        return travelRecordDao.getAllTravelRecordsLiveData()
    }
    
    override fun getAllTravelRecordsFlow(): Flow<List<TravelRecord>> {
        return travelRecordDao.getAllTravelRecordsFlow()
    }
    
    override suspend fun getTravelRecordById(id: String): TravelRecord? {
        return travelRecordDao.getTravelRecordById(id)
    }
    
    override fun getTravelRecordByIdLiveData(id: String): LiveData<TravelRecord?> {
        return travelRecordDao.getTravelRecordByIdLiveData(id)
    }
    
    override suspend fun searchTravelRecords(query: String): List<TravelRecord> {
        return travelRecordDao.searchTravelRecords(query)
    }
    
    override suspend fun getTravelRecordsByDateRange(startDate: Long, endDate: Long): List<TravelRecord> {
        return travelRecordDao.getTravelRecordsByDateRange(startDate, endDate)
    }
    
    override suspend fun insertTravelRecord(record: TravelRecord): Long {
        return travelRecordDao.insertTravelRecord(record)
    }
    
    override suspend fun updateTravelRecord(record: TravelRecord): Int {
        return travelRecordDao.updateTravelRecord(record)
    }
    
    override suspend fun deleteTravelRecord(id: String): Int {
        return travelRecordDao.deleteTravelRecordById(id)
    }
    
    override suspend fun getTravelRecordCount(): Int {
        return travelRecordDao.getTravelRecordCount()
    }
    
    override suspend fun getTotalExpenseSum(): Double {
        return travelRecordDao.getTotalExpenseSum() ?: 0.0
    }
    
    // Travel Process operations
    override suspend fun getProcessesByTravelId(travelId: String): List<TravelProcess> {
        return travelProcessDao.getProcessesByTravelId(travelId)
    }
    
    override fun getProcessesByTravelIdLiveData(travelId: String): LiveData<List<TravelProcess>> {
        return travelProcessDao.getProcessesByTravelIdLiveData(travelId)
    }
    
    override fun getProcessesByTravelIdFlow(travelId: String): Flow<List<TravelProcess>> {
        return travelProcessDao.getProcessesByTravelIdFlow(travelId)
    }
    
    override suspend fun getTravelProcessById(id: String): TravelProcess? {
        return travelProcessDao.getTravelProcessById(id)
    }
    
    override suspend fun searchTravelProcesses(query: String): List<TravelProcess> {
        return travelProcessDao.searchTravelProcesses(query)
    }
    
    override suspend fun insertTravelProcess(process: TravelProcess): Long {
        return travelProcessDao.insertTravelProcess(process)
    }
    
    override suspend fun updateTravelProcess(process: TravelProcess): Int {
        return travelProcessDao.updateTravelProcess(process)
    }
    
    override suspend fun deleteTravelProcess(id: String): Int {
        return travelProcessDao.deleteTravelProcessById(id)
    }
    
    override suspend fun getProcessCountByTravelId(travelId: String): Int {
        return travelProcessDao.getProcessCountByTravelId(travelId)
    }
    
    // Expense Item operations
    override suspend fun getExpensesByTravelId(travelId: String): List<ExpenseItem> {
        return expenseItemDao.getExpensesByTravelId(travelId)
    }
    
    override fun getExpensesByTravelIdLiveData(travelId: String): LiveData<List<ExpenseItem>> {
        return expenseItemDao.getExpensesByTravelIdLiveData(travelId)
    }
    
    override fun getExpensesByTravelIdFlow(travelId: String): Flow<List<ExpenseItem>> {
        return expenseItemDao.getExpensesByTravelIdFlow(travelId)
    }
    
    override suspend fun getExpenseItemById(id: String): ExpenseItem? {
        return expenseItemDao.getExpenseItemById(id)
    }
    
    override suspend fun getExpensesByCategory(travelId: String, category: String): List<ExpenseItem> {
        return expenseItemDao.getExpensesByCategory(travelId, category)
    }
    
    override suspend fun searchExpenseItems(query: String): List<ExpenseItem> {
        return expenseItemDao.searchExpenseItems(query)
    }
    
    override suspend fun insertExpenseItem(expense: ExpenseItem): Long {
        val result = expenseItemDao.insertExpenseItem(expense)
        // Update travel record total expense
        updateTravelRecordTotalExpense(expense.travelRecordId)
        return result
    }
    
    override suspend fun updateExpenseItem(expense: ExpenseItem): Int {
        val result = expenseItemDao.updateExpenseItem(expense)
        // Update travel record total expense
        updateTravelRecordTotalExpense(expense.travelRecordId)
        return result
    }
    
    override suspend fun deleteExpenseItem(id: String): Int {
        val expense = expenseItemDao.getExpenseItemById(id)
        val result = expenseItemDao.deleteExpenseItemById(id)
        // Update travel record total expense if expense was found
        expense?.let { updateTravelRecordTotalExpense(it.travelRecordId) }
        return result
    }
    
    override suspend fun getTotalExpenseByTravelId(travelId: String): Double {
        return expenseItemDao.getTotalExpenseByTravelId(travelId) ?: 0.0
    }
    
    override suspend fun getExpenseCountByTravelId(travelId: String): Int {
        return expenseItemDao.getExpenseCountByTravelId(travelId)
    }
    
    override suspend fun getAllCategories(): List<String> {
        return expenseItemDao.getAllCategories()
    }
    
    // Composite operations
    override suspend fun insertTravelRecordWithProcessesAndExpenses(
        record: TravelRecord,
        processes: List<TravelProcess>,
        expenses: List<ExpenseItem>
    ): Long {
        val recordId = travelRecordDao.insertTravelRecord(record)
        
        if (processes.isNotEmpty()) {
            travelProcessDao.insertTravelProcesses(processes)
        }
        
        if (expenses.isNotEmpty()) {
            expenseItemDao.insertExpenseItems(expenses)
            // Update total expense
            updateTravelRecordTotalExpense(record.id)
        }
        
        return recordId
    }
    
    override suspend fun deleteTravelRecordWithAllData(id: String): Int {
        // Foreign key constraints will automatically delete related processes and expenses
        return travelRecordDao.deleteTravelRecordById(id)
    }
    
    override suspend fun updateTravelRecordTotalExpense(id: String): Int {
        val totalExpense = getTotalExpenseByTravelId(id)
        val currentTime = System.currentTimeMillis()
        return travelRecordDao.updateTotalExpense(id, totalExpense, currentTime)
    }
    
    override suspend fun exportTravelData(): List<TravelRecord> {
        return travelRecordDao.getAllTravelRecords()
    }
    
    override suspend fun importTravelData(records: List<TravelRecord>): Int {
        val insertedIds = travelRecordDao.insertTravelRecords(records)
        return insertedIds.size
    }
    
    override suspend fun clearAllData(): Int {
        var deletedCount = 0
        deletedCount += travelRecordDao.deleteAllTravelRecords()
        deletedCount += travelProcessDao.deleteAllTravelProcesses()
        deletedCount += expenseItemDao.deleteAllExpenseItems()
        return deletedCount
    }
}