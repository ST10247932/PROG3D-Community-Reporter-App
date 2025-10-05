package com.example.spottr

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class LocationSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_settings)

        val locationSwitch = findViewById<Switch>(R.id.switchLocation)
        val systemSettingsButton = findViewById<Button>(R.id.btnSystemSettings)

        systemSettingsButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            // save toggle state to preferences
        }
    }
}