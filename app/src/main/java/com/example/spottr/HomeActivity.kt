package com.example.spottr

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.spottr.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Optional: set default selected item
        bottomNavigationView.selectedItemId = R.id.nav_home

        // Handle navigation clicks (you can replace fragments here later)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { /* TODO: Load Home Fragment */ }
                R.id.nav_search -> { /* TODO: Load Search Fragment */ }
                R.id.nav_add -> { /* TODO: Open Add Screen */ }
                R.id.nav_alert -> { /* TODO: Load Alerts */ }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java))}
            }
            true
        }

        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnReport = findViewById<Button>(R.id.btnReport)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        btnReport.setOnClickListener {
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            val address = etAddress.text.toString().trim()
            val desc = etDescription.text.toString().trim()

            if (address.isNotEmpty() && desc.isNotEmpty()) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val results = geocoder.getFromLocationName(address, 1)
                Log.d("MapsActivity", "Results: $results")

                if (!results.isNullOrEmpty()) {
                    val lat = results[0].latitude
                    val lng = results[0].longitude

                    val report = Report(address, desc, lat, lng)
                    db.collection("reports").add(report)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Report added successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to add report: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }


                    val loc = LatLng(lat, lng)
                    mMap.addMarker(MarkerOptions().position(loc).title(desc))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))

                    etAddress.text.clear()
                    etDescription.text.clear()
                } else {
                    etAddress.error = "Address not found"
                }
            } else {
                if (address.isEmpty()) etAddress.error = "Enter address"
                if (desc.isEmpty()) etDescription.error = "Enter description"
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadReports()
    }

    private fun loadReports() {
        db.collection("reports").get().addOnSuccessListener { docs ->
            for (doc in docs) {
                val report = doc.toObject(Report::class.java)
                val loc = LatLng(report.lat, report.lng)
                mMap.addMarker(MarkerOptions().position(loc).title(report.description))
            }
        }
    }
}
