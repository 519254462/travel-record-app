package com.travelrecord.app

import android.app.Application
import com.travelrecord.app.data.database.TravelDatabase
import com.travelrecord.app.data.repository.TravelRepository
import com.travelrecord.app.data.repository.TravelRepositoryImpl

/**
 * Application class for Travel Record app
 * Provides application-wide dependencies and initialization
 */
class TravelRecordApplication : Application() {
    
    // Database instance - lazy initialization
    val database by lazy { TravelDatabase.getDatabase(this) }
    
    // Repository instance - lazy initialization
    val repository by lazy { TravelRepositoryImpl(database.travelRecordDao(), database.travelProcessDao(), database.expenseItemDao()) }
    
    override fun onCreate() {
        super.onCreate()
    }
}