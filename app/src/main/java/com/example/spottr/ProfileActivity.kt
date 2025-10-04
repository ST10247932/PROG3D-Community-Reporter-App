package com.example.spottr

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Views
        tvName = findViewById(R.id.tvName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnBack = findViewById(R.id.btnBack)

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

        // Back button
        btnBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
