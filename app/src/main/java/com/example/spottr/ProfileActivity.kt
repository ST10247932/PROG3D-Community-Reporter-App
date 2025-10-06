package com.example.spottr

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvName: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        tvName = findViewById(R.id.tvName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        btnUpdate = findViewById(R.id.btnUpdate)
        val btnSettings: ImageView = findViewById(R.id.btnSettings)


        val user = auth.currentUser

        if (user != null) {
            // Set email directly from Auth
            etEmail.setText(user.email)

            // Load full user data from Firestore
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val firstName = doc.getString("firstName") ?: ""
                        val lastName = doc.getString("lastName") ?: ""
                        val fullName = "$firstName $lastName".trim()
                        tvName.text = if (fullName.isNotEmpty()) fullName else "Unnamed User"

                        etPhone.setText(doc.getString("phone") ?: "")
                        etAddress.setText(doc.getString("address") ?: "")
                    } else {
                        tvName.text = "User Profile"
                    }
                }
                .addOnFailureListener {
                    tvName.text = "Error loading profile"
                }
        }

        // Update user profile
        btnUpdate.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (user != null) {
                val updates = mutableMapOf<String, Any>()

                if (phone.isNotEmpty()) updates["phone"] = phone
                else updates["phone"] = FieldValue.delete()

                if (address.isNotEmpty()) updates["address"] = address
                else updates["address"] = FieldValue.delete()

                if (email.isNotEmpty()) updates["email"] = email
                else updates["email"] = FieldValue.delete()

                db.collection("users").document(user.uid)
                    .update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        // Settings button
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
