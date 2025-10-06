package com.example.spottr

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)) }
                R.id.nav_search -> { /* TODO */ }
                R.id.nav_add -> { startActivity(Intent(this, AddIncidentActivity::class.java)) }
                R.id.nav_alert -> { startActivity(Intent(this, AlertActionsActivity::class.java)) }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)) }
            }
            true
        }

        val listView = findViewById<ListView>(R.id.settingsList)


        val settingsItems = listOf(
            "Customize Alert Actions",
            "Privacy Settings",
            "Location Settings",
            "Incident History"
        )

        val data = ArrayList<HashMap<String, String>>()
        for (item in settingsItems) {
            val map = HashMap<String, String>()
            map["title"] = item
            data.add(map)
        }

        val adapter = SimpleAdapter(
            this,
            data,
            R.layout.item_setting,
            arrayOf("title"),
            intArrayOf(R.id.settingTitle)
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (settingsItems[position]) {
                "Customize Alert Actions" ->
                    startActivity(Intent(this, AlertActionsActivity::class.java))

                "Privacy Settings" ->
                    startActivity(Intent(this, PrivacyPolicyActivity::class.java))

                "Location Settings" ->
                    startActivity(Intent(this, LocationSettingsActivity::class.java))

                "Incident History" ->
                    startActivity(Intent(this, IncidentHistoryActivity::class.java))
            }
        }
    }
}
