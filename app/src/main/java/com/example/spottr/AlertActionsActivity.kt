package com.example.spottr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }
}
