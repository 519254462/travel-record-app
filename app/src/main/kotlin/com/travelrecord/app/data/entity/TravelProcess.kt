package com.travelrecord.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Travel process entity representing a single event or activity during travel
 * Each process is linked to a specific travel record
 */
@Entity(
    tableName = "travel_processes",
    foreignKeys = [
        ForeignKey(
            entity = TravelRecord::class,
            parentColumns = ["id"],
            childColumns = ["travelRecordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["travelRecordId"])]
)
@Parcelize
data class TravelProcess(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val travelRecordId: String,
    val description: String,
    val timestamp: Long,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Validates that the travel process has all required fields
     */
    fun isValid(): Boolean {
        return travelRecordId.isNotBlank() && 
               description.isNotBlank() && 
               timestamp > 0
    }
    
    /**
     * Returns a copy with updated description
     */
    fun withDescription(newDescription: String): TravelProcess {
        return copy(description = newDescription)
    }
    
    /**
     * Returns a copy with updated timestamp
     */
    fun withTimestamp(newTimestamp: Long): TravelProcess {
        return copy(timestamp = newTimestamp)
    }
}