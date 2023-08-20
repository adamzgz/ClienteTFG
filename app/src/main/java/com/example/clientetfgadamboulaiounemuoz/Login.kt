package com.example.clientetfgadamboulaiounemuoz

import Usuario
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput: EditText = findViewById(R.id.editTextUsername)
        val passwordInput: EditText = findViewById(R.id.editTextPassword)
        val loginButton: Button = findViewById(R.id.buttonLogin)
        val registerButton: Button = findViewById(R.id.buttonRegister)

        navigateIfLoggedIn()

        registerButton.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val usuario = Usuario("", "", "", username, password)
            Usuario.login(usuario) { success, token, role ->
                if (success) {
                    saveCredentialsToSharedPreferences(token, role)
                    Log.i("token", token)

                    navigateBasedOnRole(role)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@Login, "Inicio de sesión fallido", Toast.LENGTH_SHORT).show()
                        loginButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun navigateIfLoggedIn() {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val rol = sharedPreferences.getString("role", "")

        if (!token.isNullOrEmpty()) {
            navigateBasedOnRole(rol)
        }
    }

    private fun navigateBasedOnRole(role: String?) {
        val intent = when (role) {
            "Cliente" -> Intent(this@Login, ListadoProductos::class.java)
            "Empleado" -> Intent(this@Login, Vista_gestion::class.java)
            else -> null
        }
        intent?.let {
            startActivity(it)
            finish()
        } ?: run {
            Toast.makeText(this@Login, "Rol del usuario desconocido: $role", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCredentialsToSharedPreferences(token: String, role: String) {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("token", token)
            putString("role", role)
            apply()
        }
    }
}
