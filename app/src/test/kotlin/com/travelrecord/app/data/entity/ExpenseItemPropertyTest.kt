package com.travelrecord.app.data.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.util.UUID

/**
 * Property-based tests for ExpenseItem entity
 */
class ExpenseItemPropertyTest : StringSpec({
    
    "Expense item ID uniqueness should be guaranteed" {
        checkAll(iterations = 100, Arb.list(arbExpenseItem(), 2..10)) { expenses ->
            val ids = expenses.map { it.id }
            ids.size shouldBe ids.toSet().size
            
            for (i in expenses.indices) {
                for (j in i + 1 until expenses.size) {
                    expenses[i].id shouldNotBe expenses[j].id
                }
            }
        }
    }
    
    "Expense item validation should work correctly for valid inputs" {
        checkAll(iterations = 100, arbValidExpenseItem()) { expense ->
            expense.isValid() shouldBe true
        }
    }
    
    "Expense item validation should reject invalid inputs" {
        checkAll(iterations = 100, arbInvalidExpenseItem()) { expense ->
            expense.isValid() shouldBe false
        }
    }
    
    "Amount update should preserve other fields" {
        checkAll(iterations = 100, arbExpenseItem(), Arb.double(0.1, 10000.0)) { expense, newAmount ->
            val updated = expense.withAmount(newAmount)
            
            updated.id shouldBe expense.id
            updated.travelRecordId shouldBe expense.travelRecordId
            updated.category shouldBe expense.category
            updated.description shouldBe expense.description
            updated.timestamp shouldBe expense.timestamp
            updated.createdAt shouldBe expense.createdAt
            updated.amount shouldBe newAmount
        }
    }
    
    "Category update should preserve other fields" {
        checkAll(iterations = 100, arbExpenseItem(), Arb.element(ExpenseItem.getDefaultCategories())) { expense, newCategory ->
            val updated = expense.withCategory(newCategory)
            
            updated.id shouldBe expense.id
            updated.travelRecordId shouldBe expense.travelRecordId
            updated.amount shouldBe expense.amount
            updated.description shouldBe expense.description
            updated.timestamp shouldBe expense.timestamp
            updated.createdAt shouldBe expense.createdAt
            updated.category shouldBe newCategory
        }
    }
    
    "Description update should preserve other fields" {
        checkAll(iterations = 100, arbExpenseItem(), Arb.string(1, 200)) { expense, newDescription ->
            val updated = expense.withDescription(newDescription)
            
            updated.id shouldBe expense.id
            updated.travelRecordId shouldBe expense.travelRecordId
            updated.amount shouldBe expense.amount
            updated.category shouldBe expense.category
            updated.timestamp shouldBe expense.timestamp
            updated.createdAt shouldBe expense.createdAt
            updated.description shouldBe newDescription
        }
    }
})

private fun arbExpenseItem(): Arb<ExpenseItem> = arbitrary {
    ExpenseItem(
        travelRecordId = UUID.randomUUID().toString(),
        amount = Arb.double(0.1, 10000.0).bind(),
        category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
        description = Arb.string(1, 200).bind(),
        timestamp = Arb.long(1000000000000L, 2000000000000L).bind()
    )
}

private fun arbValidExpenseItem(): Arb<ExpenseItem> = arbitrary {
    ExpenseItem(
        travelRecordId = UUID.randomUUID().toString(),
        amount = Arb.double(0.01, Double.MAX_VALUE).bind(),
        category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
        description = Arb.string(1, 200).bind(),
        timestamp = Arb.long(1, Long.MAX_VALUE).bind()
    )
}

private fun arbInvalidExpenseItem(): Arb<ExpenseItem> = Arb.choice(
    // Empty travel record ID
    arbitrary {
        ExpenseItem(
            travelRecordId = "",
            amount = Arb.double(0.01, 10000.0).bind(),
            category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
            description = Arb.string(1, 200).bind(),
            timestamp = Arb.long(1, Long.MAX_VALUE).bind()
        )
    },
    // Zero or negative amount
    arbitrary {
        ExpenseItem(
            travelRecordId = UUID.randomUUID().toString(),
            amount = Arb.double(Double.MIN_VALUE, 0.0).bind(),
            category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
            description = Arb.string(1, 200).bind(),
            timestamp = Arb.long(1, Long.MAX_VALUE).bind()
        )
    },
    // Empty category
    arbitrary {
        ExpenseItem(
            travelRecordId = UUID.randomUUID().toString(),
            amount = Arb.double(0.01, 10000.0).bind(),
            category = "",
            description = Arb.string(1, 200).bind(),
            timestamp = Arb.long(1, Long.MAX_VALUE).bind()
        )
    },
    // Empty description
    arbitrary {
        ExpenseItem(
            travelRecordId = UUID.randomUUID().toString(),
            amount = Arb.double(0.01, 10000.0).bind(),
            category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
            description = "",
            timestamp = Arb.long(1, Long.MAX_VALUE).bind()
        )
    },
    // Invalid timestamp
    arbitrary {
        ExpenseItem(
            travelRecordId = UUID.randomUUID().toString(),
            amount = Arb.double(0.01, 10000.0).bind(),
            category = Arb.element(ExpenseItem.getDefaultCategories()).bind(),
            description = Arb.string(1, 200).bind(),
            timestamp = 0L
        )
    }
)