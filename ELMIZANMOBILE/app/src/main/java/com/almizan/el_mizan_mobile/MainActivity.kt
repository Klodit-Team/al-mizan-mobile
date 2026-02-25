
package com.almizan.el_mizan_mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    // Exemple d'identifiants corrects (pour test)
    private val correctEmail = "test@exemple.com"
    private val correctPassword = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(email == correctEmail && password == correctPassword) {
                Toast.makeText(this, "Connexion réussie ✅", Toast.LENGTH_SHORT).show()
                // Ici tu peux naviguer vers HomeActivity par exemple
            } else {
                Toast.makeText(this, "Email ou mot de passe incorrect ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }
}