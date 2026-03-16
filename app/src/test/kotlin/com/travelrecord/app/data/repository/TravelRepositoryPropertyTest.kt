package com.travelrecord.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelrecord.app.data.database.TravelDatabase
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.data.entity.TravelProcess
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
import java.util.UUID

/**
 * Property-based tests for TravelRepository
 * Feature: android-native-app, Property 4: 级联删除完整性
 * Validates: Requirements 1.4
 */
@RunWith(AndroidJUnit4::class)
class TravelRepositoryPropertyTest : StringSpec() {
    
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
        "Property 4: Cascade delete completeness - for any travel record, after deleting that record, all associated process records and expense records should also be deleted" {
            runBlocking {
                checkAll(iterations = 50, arbTravelRecordWithData()) { (record, processes, expenses) ->
                    // Insert travel record with associated data
                    repository.insertTravelRecordWithProcessesAndExpenses(record, processes, expenses)
                    
                    // Verify data exists
                    val retrievedRecord = repository.getTravelRecordById(record.id)
                    retrievedRecord shouldNotBe null
                    
                    val retrievedProcesses = repository.getProcessesByTravelId(record.id)
                    retrievedProcesses.size shouldBe processes.size
                    
                    val retrievedExpenses = repository.getExpensesByTravelId(record.id)
                    retrievedExpenses.size shouldBe expenses.size
                    
                    // Delete the travel record
                    val deleteResult = repository.deleteTravelRecordWithAllData(record.id)
                    deleteResult shouldBe 1
                    
                    // Verify all associated data is deleted
                    val recordAfterDelete = repository.getTravelRecordById(record.id)
                    recordAfterDelete shouldBe null
                    
                    val processesAfterDelete = repository.getProcessesByTravelId(record.id)
                    processesAfterDelete.size shouldBe 0
                    
                    val expensesAfterDelete = repository.getExpensesByTravelId(record.id)
                    expensesAfterDelete.size shouldBe 0
                }
            }
        }
        
        "Repository should maintain data consistency across operations" {
            runBlocking {
                checkAll(iterations = 50, arbValidTravelRecord()) { record ->
                    // Insert record
                    val insertResult = repository.insertTravelRecord(record)
                    insertResult shouldNotBe -1L
                    
                    // Verify it can be retrieved
                    val retrieved = repository.getTravelRecordById(record.id)
                    retrieved shouldNotBe null
                    retrieved!!.id shouldBe record.id
                    
                    // Update record
                    val updatedRecord = record.copy(title = "Updated Title").withUpdatedTimestamp()
                    val updateResult = repository.updateTravelRecord(updatedRecord)
                    updateResult shouldBe 1
                    
                    // Verify update
                    val afterUpdate = repository.getTravelRecordById(record.id)
                    afterUpdate shouldNotBe null
                    afterUpdate!!.title shouldBe "Updated Title"
                    
                    // Clean up
                    repository.deleteTravelRecord(record.id)
                }
            }
        }
    }
}

// Generators for property testing
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

private fun arbTravelProcess(travelRecordId: String): Arb<TravelProcess> = arbitrary {
    TravelProcess(
        travelRecordId = travelRecordId,
        description = Arb.string(1, 500).bind(),
        timestamp = Arb.long(1000000000000L, 2000000000000L).bind()
    )
}

private fun arbExpenseItem(travelRecordId: String): Arb<ExpenseItem> = arbitrary {
    ExpenseItem(
        travelRecordId = travelRecordId,
        amount = Arb.double(0.1, 10000.0).bind(),
        category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
        description = Arb.string(1, 200).bind(),
        timestamp = Arb.long(1000000000000L, 2000000000000L).bind()
    )
}

private fun arbTravelRecordWithData(): Arb<Triple<TravelRecord, List<TravelProcess>, List<ExpenseItem>>> = arbitrary {
    val record = arbValidTravelRecord().bind()
    val processCount = Arb.int(0, 5).bind()
    val expenseCount = Arb.int(0, 5).bind()
    
    val processes = (0 until processCount).map { arbTravelProcess(record.id).bind() }
    val expenses = (0 until expenseCount).map { arbExpenseItem(record.id).bind() }
    
    Triple(record, processes, expenses)
}