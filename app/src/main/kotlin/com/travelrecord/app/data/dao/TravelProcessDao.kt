package com.travelrecord.app.data.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.travelrecord.app.data.entity.TravelProcess
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for TravelProcess entity
 * Provides methods to interact with travel_processes table
 */
@Dao
interface TravelProcessDao {
    
    /**
     * Get all travel processes for a specific travel record, ordered by timestamp
     */
    @Query("SELECT * FROM travel_processes WHERE travelRecordId = :travelRecordId ORDER BY timestamp ASC")
    suspend fun getProcessesByTravelId(travelRecordId: String): List<TravelProcess>
    
    /**
     * Get all travel processes for a specific travel record as LiveData
     */
    @Query("SELECT * FROM travel_processes WHERE travelRecordId = :travelRecordId ORDER BY timestamp ASC")
    fun getProcessesByTravelIdLiveData(travelRecordId: String): LiveData<List<TravelProcess>>
    
    /**
     * Get all travel processes for a specific travel record as Flow
     */
    @Query("SELECT * FROM travel_processes WHERE travelRecordId = :travelRecordId ORDER BY timestamp ASC")
    fun getProcessesByTravelIdFlow(travelRecordId: String): Flow<List<TravelProcess>>
    
    /**
     * Get a specific travel process by ID
     */
    @Query("SELECT * FROM travel_processes WHERE id = :id")
    suspend fun getTravelProcessById(id: String): TravelProcess?
    
    /**
     * Get all travel processes ordered by timestamp (newest first)
     */
    @Query("SELECT * FROM travel_processes ORDER BY timestamp DESC")
    suspend fun getAllTravelProcesses(): List<TravelProcess>
    
    /**
     * Search travel processes by description
     */
    @Query("SELECT * FROM travel_processes WHERE description LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun searchTravelProcesses(query: String): List<TravelProcess>
    
    /**
     * Get travel processes within a time range for a specific travel record
     */
    @Query("SELECT * FROM travel_processes WHERE travelRecordId = :travelRecordId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getProcessesByTimeRange(travelRecordId: String, startTime: Long, endTime: Long): List<TravelProcess>
    
    /**
     * Get count of processes for a specific travel record
     */
    @Query("SELECT COUNT(*) FROM travel_processes WHERE travelRecordId = :travelRecordId")
    suspend fun getProcessCountByTravelId(travelRecordId: String): Int
    
    /**
     * Insert a new travel process
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelProcess(travelProcess: TravelProcess): Long
    
    /**
     * Insert multiple travel processes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelProcesses(travelProcesses: List<TravelProcess>): List<Long>
    
    /**
     * Update an existing travel process
     */
    @Update
    suspend fun updateTravelProcess(travelProcess: TravelProcess): Int
    
    /**
     * Update multiple travel processes
     */
    @Update
    suspend fun updateTravelProcesses(travelProcesses: List<TravelProcess>): Int
    
    /**
     * Delete a travel process by ID
     */
    @Query("DELETE FROM travel_processes WHERE id = :id")
    suspend fun deleteTravelProcessById(id: String): Int
    
    /**
     * Delete a travel process entity
     */
    @Delete
    suspend fun deleteTravelProcess(travelProcess: TravelProcess): Int
    
    /**
     * Delete all travel processes for a specific travel record
     */
    @Query("DELETE FROM travel_processes WHERE travelRecordId = :travelRecordId")
    suspend fun deleteProcessesByTravelId(travelRecordId: String): Int
    
    /**
     * Delete all travel processes
     */
    @Query("DELETE FROM travel_processes")
    suspend fun deleteAllTravelProcesses(): Int
}