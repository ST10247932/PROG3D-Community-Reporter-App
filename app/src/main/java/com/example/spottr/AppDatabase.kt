package com.example.spottr

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [IncidentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incidentDAO(): IncidentDAO

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "incident_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}