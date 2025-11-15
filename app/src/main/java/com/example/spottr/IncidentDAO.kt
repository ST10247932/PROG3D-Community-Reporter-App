package com.example.spottr

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDAO {

    // It returns a Flow, which will automatically update the UI when data changes.
    @Query("SELECT * FROM offline_incidents ORDER BY id DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Query("SELECT * FROM offline_incidents WHERE isSynced = 0")
    suspend fun getUnsyncedIncidents(): List<IncidentEntity>

    @Query("UPDATE offline_incidents SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Delete
    suspend fun delete(incident: IncidentEntity)

    @Query("DELETE FROM offline_incidents WHERE isSynced = 1")
    suspend fun clearSyncedIncidents()
}