package com.travelrecord.app.data.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.travelrecord.app.data.entity.TravelRecord
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for TravelRecord entity
 * Provides methods to interact with travel_records table
 */
@Dao
interface TravelRecordDao {
    
    /**
     * Get all travel records ordered by creation date (newest first)
     */
    @Query("SELECT * FROM travel_records ORDER BY createdAt DESC")
    suspend fun getAllTravelRecords(): List<TravelRecord>
    
    /**
     * Get all travel records as LiveData for UI observation
     */
    @Query("SELECT * FROM travel_records ORDER BY createdAt DESC")
    fun getAllTravelRecordsLiveData(): LiveData<List<TravelRecord>>
    
    /**
     * Get all travel records as Flow for reactive programming
     */
    @Query("SELECT * FROM travel_records ORDER BY createdAt DESC")
    fun getAllTravelRecordsFlow(): Flow<List<TravelRecord>>
    
    /**
     * Get a specific travel record by ID
     */
    @Query("SELECT * FROM travel_records WHERE id = :id")
    suspend fun getTravelRecordById(id: String): TravelRecord?
    
    /**
     * Get a specific travel record by ID as LiveData
     */
    @Query("SELECT * FROM travel_records WHERE id = :id")
    fun getTravelRecordByIdLiveData(id: String): LiveData<TravelRecord?>
    
    /**
     * Search travel records by title or purpose
     */
    @Query("SELECT * FROM travel_records WHERE title LIKE '%' || :query || '%' OR purpose LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    suspend fun searchTravelRecords(query: String): List<TravelRecord>
    
    /**
     * Get travel records within a date range
     */
    @Query("SELECT * FROM travel_records WHERE startDate >= :startDate AND startDate <= :endDate ORDER BY startDate ASC")
    suspend fun getTravelRecordsByDateRange(startDate: Long, endDate: Long): List<TravelRecord>
    
    /**
     * Get travel records with total expense greater than specified amount
     */
    @Query("SELECT * FROM travel_records WHERE totalExpense >= :minExpense ORDER BY totalExpense DESC")
    suspend fun getTravelRecordsByMinExpense(minExpense: Double): List<TravelRecord>
    
    /**
     * Insert a new travel record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelRecord(travelRecord: TravelRecord): Long
    
    /**
     * Insert multiple travel records
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelRecords(travelRecords: List<TravelRecord>): List<Long>
    
    /**
     * Update an existing travel record
     */
    @Update
    suspend fun updateTravelRecord(travelRecord: TravelRecord): Int
    
    /**
     * Update multiple travel records
     */
    @Update
    suspend fun updateTravelRecords(travelRecords: List<TravelRecord>): Int
    
    /**
     * Delete a travel record by ID
     */
    @Query("DELETE FROM travel_records WHERE id = :id")
    suspend fun deleteTravelRecordById(id: String): Int
    
    /**
     * Delete a travel record entity
     */
    @Delete
    suspend fun deleteTravelRecord(travelRecord: TravelRecord): Int
    
    /**
     * Delete all travel records
     */
    @Query("DELETE FROM travel_records")
    suspend fun deleteAllTravelRecords(): Int
    
    /**
     * Get count of all travel records
     */
    @Query("SELECT COUNT(*) FROM travel_records")
    suspend fun getTravelRecordCount(): Int
    
    /**
     * Get total expense sum across all travel records
     */
    @Query("SELECT SUM(totalExpense) FROM travel_records")
    suspend fun getTotalExpenseSum(): Double?
    
    /**
     * Update total expense for a specific travel record
     */
    @Query("UPDATE travel_records SET totalExpense = :totalExpense, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTotalExpense(id: String, totalExpense: Double, updatedAt: Long): Int
}