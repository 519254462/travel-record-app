package com.travelrecord.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Expense item entity representing a single expense during travel
 * Each expense is linked to a specific travel record
 */
@Entity(
    tableName = "expense_items",
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
data class ExpenseItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val travelRecordId: String,
    val amount: Double,
    val category: String,
    val description: String,
    val timestamp: Long,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Validates that the expense item has all required fields
     */
    fun isValid(): Boolean {
        return travelRecordId.isNotBlank() && 
               amount > 0.0 && 
               category.isNotBlank() && 
               description.isNotBlank() && 
               timestamp > 0
    }
    
    /**
     * Returns a copy with updated amount
     */
    fun withAmount(newAmount: Double): ExpenseItem {
        return copy(amount = newAmount)
    }
    
    /**
     * Returns a copy with updated category
     */
    fun withCategory(newCategory: String): ExpenseItem {
        return copy(category = newCategory)
    }
    
    /**
     * Returns a copy with updated description
     */
    fun withDescription(newDescription: String): ExpenseItem {
        return copy(description = newDescription)
    }
    
    companion object {
        // Common expense categories
        const val CATEGORY_TRANSPORTATION = "交通"
        const val CATEGORY_ACCOMMODATION = "住宿"
        const val CATEGORY_FOOD = "餐饮"
        const val CATEGORY_ENTERTAINMENT = "娱乐"
        const val CATEGORY_SHOPPING = "购物"
        const val CATEGORY_OTHER = "其他"
        
        fun getDefaultCategories(): List<String> {
            return listOf(
                CATEGORY_TRANSPORTATION,
                CATEGORY_ACCOMMODATION,
                CATEGORY_FOOD,
                CATEGORY_ENTERTAINMENT,
                CATEGORY_SHOPPING,
                CATEGORY_OTHER
            )
        }
    }
}