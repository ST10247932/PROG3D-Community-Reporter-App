package com.example.spottr

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    companion object {
        val tempReports = mutableListOf<Report>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { /* Do nothing */ }
                R.id.nav_search -> { /* TODO */ }
                R.id.nav_add -> { /* TODO */ }
                R.id.nav_alert -> { /* TODO */ }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)) }
            }
            true
        }

        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnReport = findViewById<Button>(R.id.btnReport)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


        // Report button
        btnReport.setOnClickListener {
            val address = etAddress.text.toString().trim()
            val desc = etDescription.text.toString().trim()

            if (address.isNotEmpty() && desc.isNotEmpty()) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val results = geocoder.getFromLocationName(address, 1)
                Log.d("HomeActivity", "Geocoder results: $results")

                if (!results.isNullOrEmpty()) {
                    val lat = results[0].latitude
                    val lng = results[0].longitude

                    val report = Report(address, desc, lat, lng)

                    // Save to Firestore
                    db.collection("reports").add(report)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Report saved to Firestore!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save report: ${e.message}", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }

                    // Add to in-memory list
                    tempReports.add(report)

                    // Update map
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
        loadReportsOnMap()
    }

    private fun loadReportsOnMap() {
        db.collection("reports").get().addOnSuccessListener { docs ->
            for (doc in docs) {
                val report = doc.toObject(Report::class.java)
                val loc = LatLng(report.lat, report.lng)
                mMap.addMarker(MarkerOptions().position(loc).title(report.description))
            }
        }
    }
}
