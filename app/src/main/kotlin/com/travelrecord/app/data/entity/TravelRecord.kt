package com.travelrecord.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Travel record entity representing a single travel experience
 * Contains basic information about the travel including time, purpose, and expenses
 */
@Entity(tableName = "travel_records")
@Parcelize
data class TravelRecord(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val purpose: String,
    val task: String,
    val startDate: Long,
    val endDate: Long? = null,
    val totalExpense: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Validates that the travel record has all required fields
     */
    fun isValid(): Boolean {
        return title.isNotBlank() && 
               purpose.isNotBlank() && 
               task.isNotBlank() && 
               startDate > 0 &&
               totalExpense >= 0.0
    }
    
    /**
     * Returns a copy with updated timestamp
     */
    fun withUpdatedTimestamp(): TravelRecord {
        return copy(updatedAt = System.currentTimeMillis())
    }
    
    /**
     * Returns a copy with updated total expense
     */
    fun withTotalExpense(newTotal: Double): TravelRecord {
        return copy(
            totalExpense = newTotal,
            updatedAt = System.currentTimeMillis()
        )
    }
}