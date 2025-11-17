
package com.example.spottr
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.spottr.LoginActivity
import com.example.spottr.R
import com.example.spottr.RegisterActivity
import android.content.res.Configuration
import android.widget.PopupMenu
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.concurrent.Executor

class StartActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnSignup)
        val btnLanguage = findViewById<Button>(R.id.btnLanguage)

        // --- Biometric Logic Setup ---
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                    goToHomeScreen()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If user cancels, we'll go to the standard login page
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                        goToLoginActivity()
                    } else {
                        Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Spottr")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()


        btnLogin.setOnClickListener {
            // Check if user has enabled biometric login previously
            val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isBiometricEnabled = sharedPref.getBoolean("biometric_enabled", false)

            if (isBiometricEnabled) {
                // If enabled, trigger the biometric prompt
                biometricPrompt.authenticate(promptInfo)
            } else {
                // If not enabled, go to the normal LoginActivity
                goToLoginActivity()
            }
        }

        // Navigate to RegisterActivity
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Language selection
        btnLanguage.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add("English")
            popup.menu.add("Afrikaans")
            popup.menu.add("Zulu")

            popup.setOnMenuItemClickListener { item ->
                when (item.title.toString()) {
                    "English" -> setLocale("en")
                    "Afrikaans" -> setLocale("af")
                    "Zulu" -> setLocale("zu")
                }
                true
            }
            popup.show()
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // restart activity to apply language
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun goToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close StartupActivity
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