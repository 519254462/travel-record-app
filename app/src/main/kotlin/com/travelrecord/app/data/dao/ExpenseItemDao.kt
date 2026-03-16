package com.travelrecord.app.data.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.travelrecord.app.data.entity.ExpenseItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ExpenseItem entity
 * Provides methods to interact with expense_items table
 */
@Dao
interface ExpenseItemDao {
    
    /**
     * Get all expense items for a specific travel record, ordered by timestamp
     */
    @Query("SELECT * FROM expense_items WHERE travelRecordId = :travelRecordId ORDER BY timestamp DESC")
    suspend fun getExpensesByTravelId(travelRecordId: String): List<ExpenseItem>
    
    /**
     * Get all expense items for a specific travel record as LiveData
     */
    @Query("SELECT * FROM expense_items WHERE travelRecordId = :travelRecordId ORDER BY timestamp DESC")
    fun getExpensesByTravelIdLiveData(travelRecordId: String): LiveData<List<ExpenseItem>>
    
    /**
     * Get all expense items for a specific travel record as Flow
     */
    @Query("SELECT * FROM expense_items WHERE travelRecordId = :travelRecordId ORDER BY timestamp DESC")
    fun getExpensesByTravelIdFlow(travelRecordId: String): Flow<List<ExpenseItem>>
    
    /**
     * Get a specific expense item by ID
     */
    @Query("SELECT * FROM expense_items WHERE id = :id")
    suspend fun getExpenseItemById(id: String): ExpenseItem?
    
    /**
     * Get all expense items ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM expense_items ORDER BY timestamp DESC")
    suspend fun getAllExpenseItems(): List<ExpenseItem>
    
    /**
     * Get expense items by category for a specific travel record
     */
    @Query("SELECT * FROM expense_items WHERE travelRecordId = :travelRecordId AND category = :category ORDER BY timestamp DESC")
    suspend fun getExpensesByCategory(travelRecordId: String, category: String): List<ExpenseItem>
    
    /**
     * Get expense items within an amount range for a specific travel record
     */
    @Query("SELECT * FROM expense_items WHERE travelRecordId = :travelRecordId AND amount >= :minAmount AND amount <= :maxAmount ORDER BY amount DESC")
    suspend fun getExpensesByAmountRange(travelRecordId: String, minAmount: Double, maxAmount: Double): List<ExpenseItem>
    
    /**
     * Search expense items by description
     */
    @Query("SELECT * FROM expense_items WHERE description LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun searchExpenseItems(query: String): List<ExpenseItem>
    
    /**
     * Get total expense amount for a specific travel record
     */
    @Query("SELECT SUM(amount) FROM expense_items WHERE travelRecordId = :travelRecordId")
    suspend fun getTotalExpenseByTravelId(travelRecordId: String): Double?
    
    /**
     * Get expense count for a specific travel record
     */
    @Query("SELECT COUNT(*) FROM expense_items WHERE travelRecordId = :travelRecordId")
    suspend fun getExpenseCountByTravelId(travelRecordId: String): Int
    
    /**
     * Get expense summary by category for a specific travel record
     * Note: Room doesn't support Map return type directly
     * Use getExpensesByCategory instead and calculate in code
     */
    // @Query("SELECT category, SUM(amount) as total FROM expense_items WHERE travelRecordId = :travelRecordId GROUP BY category ORDER BY total DESC")
    // suspend fun getExpenseSummaryByCategory(travelRecordId: String): Map<String, Double>
    
    /**
     * Get all distinct categories used in expenses
     */
    @Query("SELECT DISTINCT category FROM expense_items ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
    
    /**
     * Insert a new expense item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseItem(expenseItem: ExpenseItem): Long
    
    /**
     * Insert multiple expense items
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseItems(expenseItems: List<ExpenseItem>): List<Long>
    
    /**
     * Update an existing expense item
     */
    @Update
    suspend fun updateExpenseItem(expenseItem: ExpenseItem): Int
    
    /**
     * Update multiple expense items
     */
    @Update
    suspend fun updateExpenseItems(expenseItems: List<ExpenseItem>): Int
    
    /**
     * Delete an expense item by ID
     */
    @Query("DELETE FROM expense_items WHERE id = :id")
    suspend fun deleteExpenseItemById(id: String): Int
    
    /**
     * Delete an expense item entity
     */
    @Delete
    suspend fun deleteExpenseItem(expenseItem: ExpenseItem): Int
    
    /**
     * Delete all expense items for a specific travel record
     */
    @Query("DELETE FROM expense_items WHERE travelRecordId = :travelRecordId")
    suspend fun deleteExpensesByTravelId(travelRecordId: String): Int
    
    /**
     * Delete all expense items
     */
    @Query("DELETE FROM expense_items")
    suspend fun deleteAllExpenseItems(): Int
}