package com.example.spottr

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val listView = findViewById<ListView>(R.id.settingsList)
        val backButton = findViewById<ImageView>(R.id.backButton)

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

        backButton.setOnClickListener { finish() }
    }
}
