package com.travelrecord.app.data.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

/**
 * Property-based tests for TravelRecord entity
 * Feature: android-native-app, Property 5: ID唯一性保证
 * Validates: Requirements 1.5
 */
class TravelRecordPropertyTest : StringSpec({
    
    "Property 5: ID uniqueness guarantee - for any created travel records, their IDs should be different from all other record IDs in the system" {
        checkAll(iterations = 100, Arb.list(arbTravelRecord(), 2..10)) { records ->
            // Extract all IDs from the generated records
            val ids = records.map { it.id }
            
            // Verify all IDs are unique
            ids.size shouldBe ids.toSet().size
            
            // Verify no two records have the same ID
            for (i in records.indices) {
                for (j in i + 1 until records.size) {
                    records[i].id shouldNotBe records[j].id
                }
            }
        }
    }
    
    "Travel record validation should work correctly for all valid inputs" {
        checkAll(iterations = 100, arbValidTravelRecord()) { record ->
            record.isValid() shouldBe true
        }
    }
    
    "Travel record validation should reject invalid inputs" {
        checkAll(iterations = 100, arbInvalidTravelRecord()) { record ->
            record.isValid() shouldBe false
        }
    }
    
    "Total expense update should preserve other fields" {
        checkAll(iterations = 100, arbTravelRecord(), Arb.double(0.0, 10000.0)) { record, newExpense ->
            val updated = record.withTotalExpense(newExpense)
            
            updated.id shouldBe record.id
            updated.title shouldBe record.title
            updated.purpose shouldBe record.purpose
            updated.task shouldBe record.task
            updated.startDate shouldBe record.startDate
            updated.endDate shouldBe record.endDate
            updated.createdAt shouldBe record.createdAt
            updated.totalExpense shouldBe newExpense
            updated.updatedAt shouldNotBe record.updatedAt
        }
    }
})

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

private fun arbValidTravelRecord(): Arb<TravelRecord> = arbitrary {
    TravelRecord(
        title = Arb.string(1, 100).bind(),
        purpose = Arb.string(1, 200).bind(),
        task = Arb.string(1, 200).bind(),
        startDate = Arb.long(1, Long.MAX_VALUE).bind(),
        endDate = Arb.option(Arb.long(1, Long.MAX_VALUE)).bind(),
        totalExpense = Arb.double(0.0, Double.MAX_VALUE).bind()
    )
}

private fun arbInvalidTravelRecord(): Arb<TravelRecord> = Arb.choice(
    // Empty title
    arbitrary {
        TravelRecord(
            title = "",
            purpose = Arb.string(1, 200).bind(),
            task = Arb.string(1, 200).bind(),
            startDate = Arb.long(1, Long.MAX_VALUE).bind(),
            totalExpense = Arb.double(0.0, Double.MAX_VALUE).bind()
        )
    },
    // Empty purpose
    arbitrary {
        TravelRecord(
            title = Arb.string(1, 100).bind(),
            purpose = "",
            task = Arb.string(1, 200).bind(),
            startDate = Arb.long(1, Long.MAX_VALUE).bind(),
            totalExpense = Arb.double(0.0, Double.MAX_VALUE).bind()
        )
    },
    // Empty task
    arbitrary {
        TravelRecord(
            title = Arb.string(1, 100).bind(),
            purpose = Arb.string(1, 200).bind(),
            task = "",
            startDate = Arb.long(1, Long.MAX_VALUE).bind(),
            totalExpense = Arb.double(0.0, Double.MAX_VALUE).bind()
        )
    },
    // Invalid start date
    arbitrary {
        TravelRecord(
            title = Arb.string(1, 100).bind(),
            purpose = Arb.string(1, 200).bind(),
            task = Arb.string(1, 200).bind(),
            startDate = 0L,
            totalExpense = Arb.double(0.0, Double.MAX_VALUE).bind()
        )
    },
    // Negative total expense
    arbitrary {
        TravelRecord(
            title = Arb.string(1, 100).bind(),
            purpose = Arb.string(1, 200).bind(),
            task = Arb.string(1, 200).bind(),
            startDate = Arb.long(1, Long.MAX_VALUE).bind(),
            totalExpense = Arb.double(Double.MIN_VALUE, -0.1).bind()
        )
    }
)