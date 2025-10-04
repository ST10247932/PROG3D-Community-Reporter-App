package com.example.spottr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.spottr.R

class HomeActivity : AppCompatActivity() {

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
                R.id.nav_profile -> { /* TODO: Load Profile */ }
            }
            true
        }
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
