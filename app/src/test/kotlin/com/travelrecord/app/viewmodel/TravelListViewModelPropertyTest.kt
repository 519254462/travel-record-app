package com.travelrecord.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.travelrecord.app.data.entity.TravelRecord
import com.travelrecord.app.data.repository.TravelRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Rule

/**
 * Property-based tests for TravelListViewModel
 * Feature: android-native-app, Property 2: 旅游记录显示完整性
 * Validates: Requirements 1.2
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TravelListViewModelPropertyTest : StringSpec() {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = UnconfinedTestDispatcher()
    
    init {
        beforeTest {
            Dispatchers.setMain(testDispatcher)
        }
        
        afterTest {
            Dispatchers.resetMain()
        }
        
        "Property 2: Travel record display completeness - for any stored travel record, when displayed it should include all required field information (title, purpose, task, time, expense)" {
            runTest {
                checkAll(iterations = 100, Arb.list(arbValidTravelRecord(), 1..10)) { records ->
                    // Mock repository
                    val repository = mockk<TravelRepository>()
                    coEvery { repository.getAllTravelRecords() } returns records
                    coEvery { repository.getAllTravelRecordsFlow() } returns flowOf(records)
                    coEvery { repository.getTotalExpenseSum() } returns records.sumOf { it.totalExpense }
                    coEvery { repository.getTravelRecordCount() } returns records.size
                    
                    // Create ViewModel
                    val viewModel = TravelListViewModel(repository)
                    
                    // Verify all records are displayed with complete information
                    val displayedRecords = viewModel.travelRecords.value
                    displayedRecords shouldNotBe null
                    displayedRecords!!.size shouldBe records.size
                    
                    // Verify each record contains all required fields
                    displayedRecords.forEachIndexed { index, displayedRecord ->
                        val originalRecord = records[index]
                        
                        // Verify all required fields are present and not empty/null
                        displayedRecord.id shouldBe originalRecord.id
                        displayedRecord.title shouldBe originalRecord.title
                        displayedRecord.title.isNotBlank() shouldBe true
                        
                        displayedRecord.purpose shouldBe originalRecord.purpose
                        displayedRecord.purpose.isNotBlank() shouldBe true
                        
                        displayedRecord.task shouldBe originalRecord.task
                        displayedRecord.task.isNotBlank() shouldBe true
                        
                        displayedRecord.startDate shouldBe originalRecord.startDate
                        displayedRecord.startDate shouldNotBe 0L
                        
                        displayedRecord.totalExpense shouldBe originalRecord.totalExpense
                        displayedRecord.totalExpense shouldNotBe null
                    }
                }
            }
        }
        
        "Display should preserve record order and completeness" {
            runTest {
                checkAll(iterations = 50, Arb.list(arbValidTravelRecord(), 2..5)) { records ->
                    val repository = mockk<TravelRepository>()
                    coEvery { repository.getAllTravelRecords() } returns records
                    coEvery { repository.getAllTravelRecordsFlow() } returns flowOf(records)
                    coEvery { repository.getTotalExpenseSum() } returns records.sumOf { it.totalExpense }
                    coEvery { repository.getTravelRecordCount() } returns records.size
                    
                    val viewModel = TravelListViewModel(repository)
                    
                    val displayedRecords = viewModel.travelRecords.value
                    displayedRecords shouldNotBe null
                    displayedRecords!!.size shouldBe records.size
                    
                    // Verify order is preserved
                    displayedRecords.forEachIndexed { index, displayedRecord ->
                        displayedRecord.id shouldBe records[index].id
                    }
                }
            }
        }
        
        "Search results should maintain display completeness" {
            runTest {
                checkAll(iterations = 50, Arb.list(arbValidTravelRecord(), 1..5), Arb.string(1, 10)) { records, searchQuery ->
                    val filteredRecords = records.filter { 
                        it.title.contains(searchQuery, ignoreCase = true) || 
                        it.purpose.contains(searchQuery, ignoreCase = true) 
                    }
                    
                    val repository = mockk<TravelRepository>()
                    coEvery { repository.getAllTravelRecords() } returns records
                    coEvery { repository.getAllTravelRecordsFlow() } returns flowOf(records)
                    coEvery { repository.searchTravelRecords(searchQuery) } returns filteredRecords
                    coEvery { repository.getTotalExpenseSum() } returns records.sumOf { it.totalExpense }
                    coEvery { repository.getTravelRecordCount() } returns records.size
                    
                    val viewModel = TravelListViewModel(repository)
                    viewModel.searchTravelRecords(searchQuery)
                    
                    val searchResults = viewModel.travelRecords.value
                    searchResults shouldNotBe null
                    searchResults!!.size shouldBe filteredRecords.size
                    
                    // Verify each search result has complete information
                    searchResults.forEach { result ->
                        result.title.isNotBlank() shouldBe true
                        result.purpose.isNotBlank() shouldBe true
                        result.task.isNotBlank() shouldBe true
                        result.startDate shouldNotBe 0L
                        result.totalExpense shouldNotBe null
                    }
                }
            }
        }
        
        "Empty list should be handled correctly" {
            runTest {
                val repository = mockk<TravelRepository>()
                coEvery { repository.getAllTravelRecords() } returns emptyList()
                coEvery { repository.getAllTravelRecordsFlow() } returns flowOf(emptyList())
                coEvery { repository.getTotalExpenseSum() } returns 0.0
                coEvery { repository.getTravelRecordCount() } returns 0
                
                val viewModel = TravelListViewModel(repository)
                
                viewModel.travelRecords.value shouldBe emptyList()
                viewModel.recordCount.value shouldBe 0
                viewModel.totalExpenseSum.value shouldBe 0.0
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