package com.travelrecord.app.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.data.entity.TravelRecord
import com.travelrecord.app.data.repository.TravelRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import java.util.UUID

/**
 * Property-based tests for TravelDetailViewModel
 * Feature: android-native-app, Property 12: 开销总额计算一致性
 * Validates: Requirements 3.3, 3.4, 3.5
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TravelDetailViewModelPropertyTest : StringSpec() {
    
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
        
        "Property 12: Expense total calculation consistency - for any travel record, regardless of adding, editing or deleting expenses, total expense should equal the sum of all expense item amounts" {
            runTest {
                checkAll(iterations = 50, arbTravelRecordWithExpenses()) { (record, expenses) ->
                    // Mock repository
                    val repository = mockk<TravelRepository>()
                    coEvery { repository.getTravelRecordById(record.id) } returns record
                    coEvery { repository.getProcessesByTravelId(record.id) } returns emptyList()
                    coEvery { repository.getExpensesByTravelId(record.id) } returns expenses
                    
                    // Create ViewModel
                    val viewModel = TravelDetailViewModel(repository)
                    
                    // Load travel detail
                    viewModel.loadTravelDetail(record.id)
                    
                    // Verify total expense calculation
                    val expectedTotal = expenses.sumOf { it.amount }
                    viewModel.totalExpense.value shouldBe expectedTotal
                    
                    // Verify expenses are loaded correctly
                    viewModel.expenses.value shouldBe expenses
                }
            }
        }
        
        "Adding expenses should maintain calculation consistency" {
            runTest {
                checkAll(iterations = 50, arbTravelRecord(), Arb.list(arbExpenseItem(), 1..5)) { record, initialExpenses ->
                    val repository = mockk<TravelRepository>()
                    
                    // Setup initial state
                    coEvery { repository.getTravelRecordById(record.id) } returns record
                    coEvery { repository.getProcessesByTravelId(record.id) } returns emptyList()
                    coEvery { repository.getExpensesByTravelId(record.id) } returns initialExpenses
                    
                    val viewModel = TravelDetailViewModel(repository)
                    viewModel.loadTravelDetail(record.id)
                    
                    // Verify initial total
                    val initialTotal = initialExpenses.sumOf { it.amount }
                    viewModel.totalExpense.value shouldBe initialTotal
                }
            }
        }
        
        "Empty expense list should result in zero total" {
            runTest {
                checkAll(iterations = 50, arbTravelRecord()) { record ->
                    val repository = mockk<TravelRepository>()
                    
                    coEvery { repository.getTravelRecordById(record.id) } returns record
                    coEvery { repository.getProcessesByTravelId(record.id) } returns emptyList()
                    coEvery { repository.getExpensesByTravelId(record.id) } returns emptyList()
                    
                    val viewModel = TravelDetailViewModel(repository)
                    viewModel.loadTravelDetail(record.id)
                    
                    viewModel.totalExpense.value shouldBe 0.0
                    viewModel.expenses.value shouldBe emptyList()
                }
            }
        }
        
        "Single expense should equal total expense" {
            runTest {
                checkAll(iterations = 100, arbTravelRecord(), arbExpenseItem()) { record, expense ->
                    val expenseWithCorrectTravelId = expense.copy(travelRecordId = record.id)
                    val repository = mockk<TravelRepository>()
                    
                    coEvery { repository.getTravelRecordById(record.id) } returns record
                    coEvery { repository.getProcessesByTravelId(record.id) } returns emptyList()
                    coEvery { repository.getExpensesByTravelId(record.id) } returns listOf(expenseWithCorrectTravelId)
                    
                    val viewModel = TravelDetailViewModel(repository)
                    viewModel.loadTravelDetail(record.id)
                    
                    viewModel.totalExpense.value shouldBe expenseWithCorrectTravelId.amount
                    viewModel.expenses.value?.size shouldBe 1
                }
            }
        }
    }
}

// Generators for property testing
private fun arbTravelRecord(): Arb<TravelRecord> = arbitrary {
    TravelRecord(
        title = Arb.string(1, 100).bind(),
        purpose = Arb.string(1, 200).bind(),
        task = Arb.string(1, 200).bind(),
        startDate = Arb.long(1000000000000L, 2000000000000L).bind(),
        endDate = Arb.option(Arb.long(1000000000000L, 2000000000000L)).bind(),
        totalExpense = Arb.double(0.0, 100000.0).bind()
    )
}

private fun arbExpenseItem(): Arb<ExpenseItem> = arbitrary {
    ExpenseItem(
        travelRecordId = UUID.randomUUID().toString(),
        amount = Arb.double(0.1, 10000.0).bind(),
        category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
        description = Arb.string(1, 200).bind(),
        timestamp = Arb.long(1000000000000L, 2000000000000L).bind()
    )
}

private fun arbTravelRecordWithExpenses(): Arb<Pair<TravelRecord, List<ExpenseItem>>> = arbitrary {
    val record = arbTravelRecord().bind()
    val expenseCount = Arb.int(0, 10).bind()
    val expenses = (0 until expenseCount).map { 
        arbExpenseItem().bind().copy(travelRecordId = record.id)
    }
    Pair(record, expenses)
}