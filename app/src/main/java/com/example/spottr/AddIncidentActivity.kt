package com.example.spottr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spottr.databinding.ActivityAddIncidentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class AddIncidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIncidentBinding


    fun formatDateForApi(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // or whatever format your DatePicker uses
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            inputDate // fallback if already formatted
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddIncidentBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnReportIncident.setOnClickListener {
            val location = binding.location.text.toString()
            val rawDate = binding.dateTime.text.toString()
            val dateTime = formatDateForApi(rawDate)
            val description = binding.description.text.toString()


            if (location.isEmpty() || dateTime.isEmpty() || description.isEmpty()) {

                Toast.makeText(this, "Please complete all required fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Get coordinates from address
            val geocoder = Geocoder(this)
            val results = geocoder.getFromLocationName(location, 1)
            if (results.isNullOrEmpty()) {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lat = results[0].latitude
            val lng = results[0].longitude

            val incident =
                Incident(location, dateTime, description,  lat, lng)

            RetrofitClient.instance.reportIncident(incident).enqueue(object : retrofit2.Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddIncidentActivity, "Incident reported successfully!", Toast.LENGTH_SHORT).show()

                        // Show local notification
                        NotificationHelper.showNotification(
                            this@AddIncidentActivity,
                            "New Incident Reported",
                            "A new issue has been reported at $location"
                        )
                    } else {
                        Toast.makeText(this@AddIncidentActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse?>, t: Throwable) {
                    Toast.makeText(this@AddIncidentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    t.printStackTrace()
                }
            })


            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
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