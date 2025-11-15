package com.example.spottr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * AndroidViewModel is used to get the application context, which the repository needs.
 */
class IncidentViewModel(application: Application) : AndroidViewModel(application) {

    // Create a single instance of the repository for this ViewModel.
    // Initialize the repository.
    private val repository: IncidentRepository = IncidentRepository(application)

    // Expose the Flow of incidents for the UI to observe.
    val allIncidents: Flow<List<IncidentEntity>> = repository.allIncidents

    init {
        // Refresh the data from the network when the ViewModel is first created.
        refreshDataFromServer()
    }

    // A public function to allow the UI to request a data refresh (e.g., for pull-to-refresh).
    fun refreshDataFromServer() {
        viewModelScope.launch {
            repository.refreshIncidents()
        }
    }

    /**
     * This is the function you will call from your UI (e.g., from a button's onClick).
     * It launches a coroutine in the ViewModel's own scope to safely call the repository's suspend function.
     */
    fun addNewIncident(
        location: String,
        dateTime: String,
        description: String,
        lat: Double,
        lng: Double
    ) {
        // Use viewModelScope.launch to run this on a background thread.
        viewModelScope.launch {
            val newIncident = IncidentEntity(
                // id is auto-generated, so we don't set it here.
                location = location,
                dateTime = dateTime,
                description = description,
                lat = lat,
                lng = lng,
                isSynced = false // Always default to not synced; repository will update it.
            )
            // This is the call to the repository function.
            repository.addIncident(newIncident)
        }
    }
}
