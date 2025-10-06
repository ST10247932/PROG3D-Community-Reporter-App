package com.example.spottr

import android.content.Intent
import android.os.Bundle
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
import retrofit2.Call

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()

    companion object {
        val tempIncidents = mutableListOf<Incident>()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadReportsOnMap()
    }

    private fun loadReportsOnMap() {
        RetrofitClient.instance.getIncidents().enqueue(object : retrofit2.Callback<List<Incident>> {
            override fun onResponse(call: Call<List<Incident>>, response: retrofit2.Response<List<Incident>>) {
                if (response.isSuccessful) {
                    val reports = response.body() ?: emptyList()
                    for (report in reports) {
                        val loc = LatLng(report.lat, report.lng)
                        mMap.addMarker(MarkerOptions().position(loc).title(report.description))
                    }
                }
            }
            override fun onFailure(call: Call<List<Incident>>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Failed to load reports", Toast.LENGTH_SHORT).show()
            }
        })

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