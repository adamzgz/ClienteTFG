package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clientetfgadamboulaiounemuoz.Clases.Usuario

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameInput: EditText = findViewById(R.id.editTextUsername)
        val passwordInput: EditText = findViewById(R.id.editTextPassword)
        val loginButton: Button = findViewById(R.id.buttonLogin)
        val registerButton: Button = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            loginButton.isEnabled = false
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val usuario = Usuario("", "", "", username, password)
            usuario.login(usuario) { success, token ->
                if (success) {
                    val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("token", token)
                        apply()
                    }

                    val intent = Intent(this@Login, ListadoProductos::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@Login, "Inicio de sesión fallido", Toast.LENGTH_SHORT)
                            .show()
                        loginButton.isEnabled = true
                    }
                }
            }
        }
    }
}
