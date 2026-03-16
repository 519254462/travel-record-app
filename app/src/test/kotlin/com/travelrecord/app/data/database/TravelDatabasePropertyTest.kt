package com.travelrecord.app.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelrecord.app.data.entity.TravelRecord
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

/**
 * Property-based tests for database operations
 * Feature: android-native-app, Property 1: 旅游记录创建完整性
 * Validates: Requirements 1.1
 */
@RunWith(AndroidJUnit4::class)
class TravelDatabasePropertyTest : StringSpec() {
    
    private lateinit var database: TravelDatabase
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TravelDatabase::class.java
        ).allowMainThreadQueries().build()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    init {
        "Property 1: Travel record creation completeness - for any valid travel record data (time, purpose, task), after creating the record it should be retrievable with all input information intact" {
            runBlocking {
                checkAll(iterations = 100, arbValidTravelRecord()) { record ->
                    // Insert the record
                    val insertedId = database.travelRecordDao().insertTravelRecord(record)
                    insertedId shouldNotBe -1L
                    
                    // Retrieve the record
                    val retrievedRecord = database.travelRecordDao().getTravelRecordById(record.id)
                    
                    // Verify all fields are preserved
                    retrievedRecord shouldNotBe null
                    retrievedRecord!!.id shouldBe record.id
                    retrievedRecord.title shouldBe record.title
                    retrievedRecord.purpose shouldBe record.purpose
                    retrievedRecord.task shouldBe record.task
                    retrievedRecord.startDate shouldBe record.startDate
                    retrievedRecord.endDate shouldBe record.endDate
                    retrievedRecord.totalExpense shouldBe record.totalExpense
                    retrievedRecord.createdAt shouldBe record.createdAt
                    retrievedRecord.updatedAt shouldBe record.updatedAt
                    
                    // Clean up for next iteration
                    database.travelRecordDao().deleteTravelRecordById(record.id)
                }
            }
        }
        
        "Database should maintain data integrity across multiple operations" {
            runBlocking {
                checkAll(iterations = 50, Arb.list(arbValidTravelRecord(), 1..5)) { records ->
                    // Insert all records
                    val insertedIds = database.travelRecordDao().insertTravelRecords(records)
                    insertedIds.size shouldBe records.size
                    
                    // Verify all records can be retrieved
                    val allRecords = database.travelRecordDao().getAllTravelRecords()
                    allRecords.size shouldBe records.size
                    
                    // Verify each record exists and has correct data
                    records.forEach { originalRecord ->
                        val retrievedRecord = database.travelRecordDao().getTravelRecordById(originalRecord.id)
                        retrievedRecord shouldNotBe null
                        retrievedRecord!!.title shouldBe originalRecord.title
                        retrievedRecord.purpose shouldBe originalRecord.purpose
                        retrievedRecord.task shouldBe originalRecord.task
                    }
                    
                    // Clean up
                    database.travelRecordDao().deleteAllTravelRecords()
                }
            }
        }
        
        "Update operations should preserve record identity while changing specified fields" {
            runBlocking {
                checkAll(iterations = 100, arbValidTravelRecord(), Arb.string(1, 100)) { originalRecord, newTitle ->
                    // Insert original record
                    database.travelRecordDao().insertTravelRecord(originalRecord)
                    
                    // Update the record
                    val updatedRecord = originalRecord.copy(title = newTitle).withUpdatedTimestamp()
                    val updateResult = database.travelRecordDao().updateTravelRecord(updatedRecord)
                    updateResult shouldBe 1
                    
                    // Retrieve and verify
                    val retrievedRecord = database.travelRecordDao().getTravelRecordById(originalRecord.id)
                    retrievedRecord shouldNotBe null
                    retrievedRecord!!.id shouldBe originalRecord.id
                    retrievedRecord.title shouldBe newTitle
                    retrievedRecord.purpose shouldBe originalRecord.purpose
                    retrievedRecord.task shouldBe originalRecord.task
                    retrievedRecord.startDate shouldBe originalRecord.startDate
                    retrievedRecord.endDate shouldBe originalRecord.endDate
                    retrievedRecord.totalExpense shouldBe originalRecord.totalExpense
                    retrievedRecord.createdAt shouldBe originalRecord.createdAt
                    retrievedRecord.updatedAt shouldNotBe originalRecord.updatedAt
                    
                    // Clean up
                    database.travelRecordDao().deleteTravelRecordById(originalRecord.id)
                }
            }
        }
        
        "Delete operations should completely remove records and return correct count" {
            runBlocking {
                checkAll(iterations = 100, arbValidTravelRecord()) { record ->
                    // Insert record
                    database.travelRecordDao().insertTravelRecord(record)
                    
                    // Verify it exists
                    val beforeDelete = database.travelRecordDao().getTravelRecordById(record.id)
                    beforeDelete shouldNotBe null
                    
                    // Delete record
                    val deleteResult = database.travelRecordDao().deleteTravelRecordById(record.id)
                    deleteResult shouldBe 1
                    
                    // Verify it's gone
                    val afterDelete = database.travelRecordDao().getTravelRecordById(record.id)
                    afterDelete shouldBe null
                    
                    // Verify count is correct
                    val count = database.travelRecordDao().getTravelRecordCount()
                    count shouldBe 0
                }
            }
        }
    }
}

// Generator for valid travel records
private fun arbValidTravelRecord(): Arb<TravelRecord> = arbitrary {
    TravelRecord(
        title = Arb.string(1, 100).bind(),
        purpose = Arb.string(1, 200).bind(),
        task = Arb.string(1, 200).bind(),
        startDate = Arb.long(1000000000000L, 2000000000000L).bind(),
        endDate = Arb.option(Arb.long(1000000000000L, 2000000000000L)).bind(),
        totalExpense = Arb.double(0.0, 100000.0).bind(),
        createdAt = Arb.long(1000000000000L, 2000000000000L).bind(),
        updatedAt = Arb.long(1000000000000L, 2000000000000L).bind()
    )
}