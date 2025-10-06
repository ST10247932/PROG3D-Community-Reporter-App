
package com.example.spottr
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.spottr.LoginActivity
import com.example.spottr.R
import com.example.spottr.RegisterActivity


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        // Find buttons by ID
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnSignup)

        // Navigate to LoginActivity
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Navigate to RegisterActivity
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
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