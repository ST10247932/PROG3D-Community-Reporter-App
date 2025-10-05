package com.example.spottr

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_settings)

        val checkAgree = findViewById<CheckBox>(R.id.checkAgree)
        val btnAccept = findViewById<Button>(R.id.btnAccept)

        btnAccept.setOnClickListener {
            if (checkAgree.isChecked) {
                Toast.makeText(this, "Privacy Policy Accepted ✅", Toast.LENGTH_SHORT).show()
                // TODO: Proceed to next screen or save acceptance
                finish()
            } else {
                Toast.makeText(this, "You must agree to continue.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
