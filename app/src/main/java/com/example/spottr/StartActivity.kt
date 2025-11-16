
package com.example.spottr
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.spottr.LoginActivity
import com.example.spottr.R
import com.example.spottr.RegisterActivity
import android.content.res.Configuration
import android.widget.PopupMenu
import java.util.Locale

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnSignup)
        val btnLanguage = findViewById<Button>(R.id.btnLanguage)

        // Navigate to LoginActivity
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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