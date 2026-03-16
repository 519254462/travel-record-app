package com.travelrecord.app.data.entity

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for TravelRecord entity
 */
class TravelRecordTest {
    
    @Test
    fun `valid travel record should pass validation`() {
        val record = TravelRecord(
            title = "Test Travel",
            purpose = "Business",
            task = "Meeting",
            startDate = System.currentTimeMillis(),
            totalExpense = 100.0
        )
        
        assertTrue("Valid record should pass validation", record.isValid())
    }
    
    @Test
    fun `travel record with empty title should fail validation`() {
        val record = TravelRecord(
            title = "",
            purpose = "Business",
            task = "Meeting",
            startDate = System.currentTimeMillis(),
            totalExpense = 100.0
        )
        
        assertFalse("Record with empty title should fail validation", record.isValid())
    }
    
    @Test
    fun `travel record with negative expense should fail validation`() {
        val record = TravelRecord(
            title = "Test Travel",
            purpose = "Business",
            task = "Meeting",
            startDate = System.currentTimeMillis(),
            totalExpense = -100.0
        )
        
        assertFalse("Record with negative expense should fail validation", record.isValid())
    }
    
    @Test
    fun `withTotalExpense should update expense and timestamp`() {
        val originalRecord = TravelRecord(
            title = "Test Travel",
            purpose = "Business",
            task = "Meeting",
            startDate = System.currentTimeMillis(),
            totalExpense = 100.0
        )
        
        val updatedRecord = originalRecord.withTotalExpense(200.0)
        
        assertEquals("Total expense should be updated", 200.0, updatedRecord.totalExpense, 0.01)
        assertNotEquals("Updated timestamp should be different", originalRecord.updatedAt, updatedRecord.updatedAt)
        assertEquals("Other fields should remain same", originalRecord.title, updatedRecord.title)
    }
}