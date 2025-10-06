package com.example.spottr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlertActionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var alertAdapter: AlertAdapter

    private val alertList = mutableListOf(
        AlertItem("Push Notifications", true, "Social"),
        AlertItem("Email Alerts", false, "Promotions"),
        AlertItem("SMS Alerts", false, "Security"),
        AlertItem("In-App Alerts", true, "General")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_actions)

        recyclerView = findViewById(R.id.recyclerAlerts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        alertAdapter = AlertAdapter(this, alertList)
        recyclerView.adapter = alertAdapter

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
    }
}
