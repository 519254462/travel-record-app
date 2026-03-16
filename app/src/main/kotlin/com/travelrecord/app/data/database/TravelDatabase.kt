package com.travelrecord.app.data.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.travelrecord.app.data.dao.ExpenseItemDao
import com.travelrecord.app.data.dao.TravelProcessDao
import com.travelrecord.app.data.dao.TravelRecordDao
import com.travelrecord.app.data.entity.ExpenseItem
import com.travelrecord.app.data.entity.TravelProcess
import com.travelrecord.app.data.entity.TravelRecord
import java.util.Date

/**
 * Room database for Travel Record application
 * Contains all entities and provides access to DAOs
 */
@Database(
    entities = [
        TravelRecord::class,
        TravelProcess::class,
        ExpenseItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {
    
    // Abstract methods to get DAOs
    abstract fun travelRecordDao(): TravelRecordDao
    abstract fun travelProcessDao(): TravelProcessDao
    abstract fun expenseItemDao(): ExpenseItemDao
    
    companion object {
        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: TravelDatabase? = null
        
        private const val DATABASE_NAME = "travel_database"
        
        /**
         * Get database instance using singleton pattern
         */
        fun getDatabase(context: Context): TravelDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration() // For development only
                    .build()
                INSTANCE = instance
                // Return instance
                instance
            }
        }
        
        /**
         * Close database and clear instance
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
        
        /**
         * Database callback for initialization
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database created for the first time
                // You can add initial data here if needed
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Database opened
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON")
            }
        }
        
        // Future migrations can be added here
        // Example migration from version 1 to 2:
        /*
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic here
            }
        }
        */
    }
}

/**
 * Type converters for Room database
 * Handles conversion of complex types to/from database storage
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}