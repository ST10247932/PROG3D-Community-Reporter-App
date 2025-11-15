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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spottr.databinding.ActivityAddIncidentBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class AddIncidentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIncidentBinding

    private val incidentViewModel: IncidentViewModel by viewModels()

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
            var lat: Double
            var lng: Double

            try {
                // Attempt to get coordinates from the address
                val results = geocoder.getFromLocationName(location, 1)

                if (results.isNullOrEmpty()) {
                    Toast.makeText(this, "Address not found. Please check the location.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                lat = results[0].latitude
                lng = results[0].longitude

            } catch (e: IOException) {
                // This block will now run when offline, preventing the crash.
                Toast.makeText(this, "Offline: Could not verify address. Saving incident with default coordinates.", Toast.LENGTH_LONG).show()
                // You MUST provide fallback coordinates, otherwise the incident is useless.
                // 0.0, 0.0 is a common default. The real address is still saved.
                lat = 0.0
                lng = 0.0
            }


            // 2. THIS IS THE CRITICAL CHANGE
            // Instead of calling Retrofit directly, call the ViewModel function.
            // The ViewModel will handle everything else.
            incidentViewModel.addNewIncident(
                location = location,
                dateTime = dateTime,
                description = description,
                lat = lat,
                lng = lng
            )

            // 3. Inform the user and navigate away.
            // The repository will handle the saving and syncing in the background.
            Toast.makeText(this, "Incident submitted! It will be synced when ready.", Toast.LENGTH_LONG).show()

            // Navigate back to the home screen after submission.
            val intent = Intent(this, HomeActivity::class.java)
            // Flags to clear the back stack so the user doesn't return to the add form.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Finish this activity

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