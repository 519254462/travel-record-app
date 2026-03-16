package com.travelrecord.app.data.entity

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.util.UUID

/**
 * Property-based tests for TravelProcess entity
 */
class TravelProcessPropertyTest : StringSpec({
    
    "Travel process ID uniqueness should be guaranteed" {
        checkAll(iterations = 100, Arb.list(arbTravelProcess(), 2..10)) { processes ->
            val ids = processes.map { it.id }
            ids.size shouldBe ids.toSet().size
            
            for (i in processes.indices) {
                for (j in i + 1 until processes.size) {
                    processes[i].id shouldNotBe processes[j].id
                }
            }
        }
    }
    
    "Travel process validation should work correctly for valid inputs" {
        checkAll(iterations = 100, arbValidTravelProcess()) { process ->
            process.isValid() shouldBe true
        }
    }
    
    "Travel process validation should reject invalid inputs" {
        checkAll(iterations = 100, arbInvalidTravelProcess()) { process ->
            process.isValid() shouldBe false
        }
    }
    
    "Description update should preserve other fields" {
        checkAll(iterations = 100, arbTravelProcess(), Arb.string(1, 500)) { process, newDescription ->
            val updated = process.withDescription(newDescription)
            
            updated.id shouldBe process.id
            updated.travelRecordId shouldBe process.travelRecordId
            updated.timestamp shouldBe process.timestamp
            updated.createdAt shouldBe process.createdAt
            updated.description shouldBe newDescription
        }
    }
    
    "Timestamp update should preserve other fields" {
        checkAll(iterations = 100, arbTravelProcess(), Arb.long(1, Long.MAX_VALUE)) { process, newTimestamp ->
            val updated = process.withTimestamp(newTimestamp)
            
            updated.id shouldBe process.id
            updated.travelRecordId shouldBe process.travelRecordId
            updated.description shouldBe process.description
            updated.createdAt shouldBe process.createdAt
            updated.timestamp shouldBe newTimestamp
        }
    }
})

private fun arbTravelProcess(): Arb<TravelProcess> = arbitrary {
    TravelProcess(
        travelRecordId = UUID.randomUUID().toString(),
        description = Arb.string(1, 500).bind(),
        timestamp = Arb.long(1000000000000L, 2000000000000L).bind()
    )
}

private fun arbValidTravelProcess(): Arb<TravelProcess> = arbitrary {
    TravelProcess(
        travelRecordId = UUID.randomUUID().toString(),
        description = Arb.string(1, 500).bind(),
        timestamp = Arb.long(1, Long.MAX_VALUE).bind()
    )
}

private fun arbInvalidTravelProcess(): Arb<TravelProcess> = Arb.choice(
    // Empty travel record ID
    arbitrary {
        TravelProcess(
            travelRecordId = "",
            description = Arb.string(1, 500).bind(),
            timestamp = Arb.long(1, Long.MAX_VALUE).bind()
        )
    },
    // Empty description
    arbitrary {
        TravelProcess(
            travelRecordId = UUID.randomUUID().toString(),
            description = "",
            timestamp = Arb.long(1, Long.MAX_VALUE).bind()
        )
    },
    // Invalid timestamp
    arbitrary {
        TravelProcess(
            travelRecordId = UUID.randomUUID().toString(),
            description = Arb.string(1, 500).bind(),
            timestamp = 0L
        )
    }
)