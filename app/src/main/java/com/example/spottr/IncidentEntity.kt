package com.example.spottr

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location: String,
    val dateTime: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val isSynced: Boolean = false
)
