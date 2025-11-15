package com.example.spottr

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.spottr.RetrofitClient
import com.example.spottr.AppDatabase
import com.example.spottr.Incident
import com.example.spottr.IncidentEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.awaitResponse
import java.io.IOException

class IncidentRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).incidentDAO()
    private val appContext = context.applicationContext
    private val api = RetrofitClient.instance

    // This Flow is the "Single Source of Truth". The UI will observe this.
    val allIncidents: Flow<List<IncidentEntity>> = dao.getAllIncidents()


    private fun mapEntityToNetwork(entity: IncidentEntity): Incident {
        return Incident(
            location = entity.location,
            dateTime = entity.dateTime,
            description = entity.description,
            lat = entity.lat,
            lng = entity.lng
        )
    }

    // Main entry point
    // --- NEW, SIMPLIFIED addIncident ---
    /**
     * This function now ONLY saves the incident to the local database.
     * It marks it as unsynced, making it the SyncWorker's job to handle all uploads.
     * This is the correct and most reliable offline-first pattern.
     */
    suspend fun addIncident(incidentEntity: IncidentEntity) {
        dao.insertIncident(incidentEntity.copy(isSynced = false))

        triggerSyncWorker()
    }

    // --- NEW HELPER FUNCTION ---
    private fun triggerSyncWorker() {
        // Define constraints: the worker needs a network connection to run.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create a one-time request that runs as soon as constraints are met.
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        // Enqueue the work. If the user is online, it will run almost immediately.
        // If they are offline, it will wait until the network is available.
        WorkManager.getInstance(appContext).enqueue(syncRequest)
    }

    /**
     * Refreshes data from the network.
     * This version uses onConflictStrategy in the DAO to update existing entries,
     * which is cleaner than manual checks.
     */
    // A function to refresh data from the network and update the local database.
    suspend fun refreshIncidents() {
        if (isOnline()) {
            try {
                // This prevents duplicates but keeps new, unsynced incidents.
                dao.clearSyncedIncidents()

                val networkIncidents = api.getIncidents().awaitResponse().body() ?: emptyList()
                // Map all network incidents to local entities marked as 'synced'
                val syncedEntities = networkIncidents.map { networkIncident ->
                    IncidentEntity(
                        // We must provide an ID. If your API doesn't provide one that can be used
                        // as a primary key, we rely on the location+dateTime to be unique.
                        // Room's REPLACE strategy will use the primary key to update.
                        location = networkIncident.location,
                        dateTime = networkIncident.dateTime,
                        description = networkIncident.description,
                        lat = networkIncident.lat,
                        lng = networkIncident.lng,
                        isSynced = true
                    )
                }
                // Let Room handle the insertion and replacement of old synced data.
                // Unsynced data will remain untouched unless a network item
                // has the exact same primary key (unlikely for new offline items).
                syncedEntities.forEach { dao.insertIncident(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // This function for the worker to get the list
    suspend fun getUnsyncedIncidents(): List<IncidentEntity> {
        return dao.getUnsyncedIncidents()
    }

    suspend fun deleteIncident(incident: IncidentEntity) {
        dao.delete(incident)
    }

    // Add this function to sync one item at a time
    suspend fun syncSingleIncident(incidentEntity: IncidentEntity): Boolean {
        if (isOnline()) {
            return try {
                val incidentForApi = mapEntityToNetwork(incidentEntity)
                val response = api.reportIncident(incidentForApi).awaitResponse()

                // If successful, return true.
                // We no longer call markAsSynced here.
                response.isSuccessful

            } catch (e: Exception) {
                // If any error occurs during the network call, return false.
                e.printStackTrace()
                false
            }
        } else {
            // If offline, the sync cannot succeed.
            return false
        }
    }


    private fun isOnline(): Boolean {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
