package com.example.clientetfgadamboulaiounemuoz

import Usuario
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Registro : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextContraseña: EditText
    private lateinit var buttonRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextContraseña = findViewById(R.id.editTextContraseña)
        buttonRegistrarse = findViewById(R.id.buttonRegistrarse)

        // Configurar el click listener del botón Registrarse
        buttonRegistrarse.setOnClickListener {
            val nombre = editTextNombre.text.toString()
            val direccion = editTextDireccion.text.toString()
            val telefono = editTextTelefono.text.toString()
            val email = editTextEmail.text.toString()
            val contraseña = editTextContraseña.text.toString()

            val usuario = Usuario(nombre, direccion, telefono, email, contraseña)

            usuario.registrar(usuario) { success ->
                if (success) {
                    // Registro exitoso
                    finish() // Cerrar la actividad de registro y volver a la actividad de inicio de sesión
                } else {
                    // Error en el registro
                    showToast("Error en el registro")
                }
            }
        }

    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
