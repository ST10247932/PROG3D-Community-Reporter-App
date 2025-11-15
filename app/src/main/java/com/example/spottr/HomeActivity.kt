package com.example.spottr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import retrofit2.Call

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val incidentViewModel: IncidentViewModel by viewModels()
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    companion object {
        val tempIncidents = mutableListOf<Incident>()
    }

    // This contract handles the permission request flow for us.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Move the camera to the user's location.
                moveCameraToUserLocation()
            } else {
                // Permission denied. Show a message and default to a wide view.
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(43.6, -79.3), 5f)) // Default view
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)) }
                R.id.nav_search -> { startActivity(Intent(this, GraphActivity::class.java)) }
                R.id.nav_add -> { startActivity(Intent(this, AddIncidentActivity::class.java)) }
                R.id.nav_alert -> { startActivity(Intent(this, AlertActionsActivity::class.java)) }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)) }
            }
            true
        }



        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        // When the user returns to this screen, trigger a refresh.
        // This ensures that any changes from the background SyncWorker are reflected.
        incidentViewModel.refreshDataFromServer()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkLocationPermission() // <-- Start the location flow
        observeIncidents()
    }

    // --- NEW: Function to check and request permissions ---
    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                moveCameraToUserLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Optional: Explain to the user why you need the permission.
                // For this example, we'll just ask directly.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                // Directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // --- NEW: Function to get location and move camera ---
    private fun moveCameraToUserLocation() {
        // Double-check permission before accessing location.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true // Shows the blue "my location" dot
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Location found, move camera.
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
                    } else {
                        // Location is null, might be disabled on the device.
                        Toast.makeText(this, "Could not get current location. Please enable GPS.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    /**
     * This function observes the ViewModel's Flow.
     * Whenever the data in the local Room database changes, this will automatically
     * receive the new list and update the map markers.
     */
    private fun observeIncidents() {
        // Use lifecycleScope to automatically manage the coroutine's lifecycle.
        lifecycleScope.launch {
            incidentViewModel.allIncidents.collect { incidents ->
                // The 'collect' block will re-run whenever the 'incidents' list changes.
                updateMapMarkers(incidents)
            }
        }
    }


    //This function clears the map and adds a fresh set of markers.
    private fun updateMapMarkers(incidents: List<IncidentEntity>) {
        // Clear any old markers from the map first.
        mMap.clear()

        if (incidents.isNotEmpty()) {
            for (incident in incidents) {
                val location = LatLng(incident.lat, incident.lng)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(incident.description)
                )
            }
        }
    }
}
/*
References:

Android Developers, 2025. Android Developer Documentation. [online]
Available at: https://developer.android.com
[Accessed 6 October 2025].

Firebase, 2025. Firebase Documentation – Build apps fast, without managing infrastructure. [online]
Available at: https://firebase.google.com/docs
[Accessed 6 October 2025].
*/