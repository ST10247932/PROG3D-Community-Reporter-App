package com.example.spottr

import android.content.Context
import android.location.Geocoder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repo = IncidentRepository(applicationContext)
        val geocoder = Geocoder(applicationContext)

        // Fetch all incidents that are marked as not synced.
        val unsyncedIncidents = repo.getUnsyncedIncidents()

        if (unsyncedIncidents.isEmpty()) {
            return Result.success() // Nothing to do.
        }

        return try {
            withContext(Dispatchers.IO) {
                for (incidentEntity in unsyncedIncidents) {
                    var entityToSync = incidentEntity

                    // --- LOGIC TO FIX COORDINATES ---
                    // Check if the incident has fallback coordinates.
                    if (incidentEntity.lat == 0.0 && incidentEntity.lng == 0.0) {
                        try {
                            // Try to get the real coordinates now that we are online.
                            val results = geocoder.getFromLocationName(incidentEntity.location, 1)
                            if (!results.isNullOrEmpty()) {
                                // If successful, create a copy of the entity with the correct coordinates.
                                entityToSync = incidentEntity.copy(
                                    lat = results[0].latitude,
                                    lng = results[0].longitude
                                )
                            }
                        } catch (e: IOException) {
                            // Geocoder failed again, proceed with fallback coordinates for now.
                            e.printStackTrace()
                        }
                    }
                    // 1. UPLOAD the data to Firestore.
                    val isSuccess = repo.syncSingleIncident(entityToSync)

                    // 2. If the upload was successful, DELETE the local copy.
                    if (isSuccess) {
                        repo.deleteIncident(incidentEntity) // Delete the original offline record
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If any part of the sync fails, retry the whole worker later.
            Result.retry()
        }
    }
}
