package com.travelrecord.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelrecord.app.data.database.TravelDatabase
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
 * Property-based tests for data persistence
 * Feature: android-native-app, Property 13: 数据持久化即时性
 * Validates: Requirements 4.1
 */
@RunWith(AndroidJUnit4::class)
class DataPersistencePropertyTest : StringSpec() {
    
    private lateinit var database: TravelDatabase
    private lateinit var repository: TravelRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TravelDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = TravelRepositoryImpl(
            database.travelRecordDao(),
            database.travelProcessDao(),
            database.expenseItemDao()
        )
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    init {
        "Property 13: Data persistence immediacy - for any user input data, save operation should immediately write data to local storage" {
            runBlocking {
                checkAll(iterations = 100, arbValidTravelRecord()) { record ->
                    // Save the data
                    val saveResult = repository.insertTravelRecord(record)
                    saveResult shouldNotBe -1L
                    
                    // Immediately verify data is persisted (no delay)
                    val retrievedRecord = repository.getTravelRecordById(record.id)
                    retrievedRecord shouldNotBe null
                    retrievedRecord!!.id shouldBe record.id
                    retrievedRecord.title shouldBe record.title
                    retrievedRecord.purpose shouldBe record.purpose
                    retrievedRecord.task shouldBe record.task
                    retrievedRecord.startDate shouldBe record.startDate
                    retrievedRecord.endDate shouldBe record.endDate
                    retrievedRecord.totalExpense shouldBe record.totalExpense
                    
                    // Clean up
                    repository.deleteTravelRecord(record.id)
                }
            }
        }
        
        "Data updates should be immediately persisted" {
            runBlocking {
                checkAll(iterations = 100, arbValidTravelRecord(), Arb.string(1, 100)) { originalRecord, newTitle ->
                    // Insert original record
                    repository.insertTravelRecord(originalRecord)
                    
                    // Update the record
                    val updatedRecord = originalRecord.copy(title = newTitle).withUpdatedTimestamp()
                    val updateResult = repository.updateTravelRecord(updatedRecord)
                    updateResult shouldBe 1
                    
                    // Immediately verify update is persisted
                    val retrievedRecord = repository.getTravelRecordById(originalRecord.id)
                    retrievedRecord shouldNotBe null
                    retrievedRecord!!.title shouldBe newTitle
                    retrievedRecord.updatedAt shouldNotBe originalRecord.updatedAt
                    
                    // Clean up
                    repository.deleteTravelRecord(originalRecord.id)
                }
            }
        }
        
        "Data deletions should be immediately effective" {
            runBlocking {
                checkAll(iterations = 100, arbValidTravelRecord()) { record ->
                    // Insert record
                    repository.insertTravelRecord(record)
                    
                    // Verify it exists
                    val beforeDelete = repository.getTravelRecordById(record.id)
                    beforeDelete shouldNotBe null
                    
                    // Delete record
                    val deleteResult = repository.deleteTravelRecord(record.id)
                    deleteResult shouldBe 1
                    
                    // Immediately verify deletion is effective
                    val afterDelete = repository.getTravelRecordById(record.id)
                    afterDelete shouldBe null
                }
            }
        }
        
        "Batch operations should persist all data immediately" {
            runBlocking {
                checkAll(iterations = 50, Arb.list(arbValidTravelRecord(), 2..5)) { records ->
                    // Insert all records
                    records.forEach { record ->
                        repository.insertTravelRecord(record)
                    }
                    
                    // Immediately verify all records are persisted
                    val allRecords = repository.getAllTravelRecords()
                    allRecords.size shouldBe records.size
                    
                    records.forEach { originalRecord ->
                        val found = allRecords.find { it.id == originalRecord.id }
                        found shouldNotBe null
                        found!!.title shouldBe originalRecord.title
                        found.purpose shouldBe originalRecord.purpose
                    }
                    
                    // Clean up
                    repository.clearAllData()
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
        totalExpense = Arb.double(0.0, 100000.0).bind()
    )
}