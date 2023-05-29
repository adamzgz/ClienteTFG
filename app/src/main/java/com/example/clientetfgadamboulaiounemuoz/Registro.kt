package com.example.clientetfgadamboulaiounemuoz

import com.example.clientetfgadamboulaiounemuoz.Clases.Usuario
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
            buttonRegistrarse.isEnabled = false
            val nombre = editTextNombre.text.toString()
            val direccion = editTextDireccion.text.toString()
            val telefono = editTextTelefono.text.toString()
            val email = editTextEmail.text.toString()
            val contraseña = editTextContraseña.text.toString()

            // Asegurar que todos los campos estén completos
            if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || contraseña.isEmpty()) {
                showToast("Todos los campos son obligatorios")
                return@setOnClickListener
            }

            // Validar el formato de email
            val regexEmail = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
            if (!email.matches(regexEmail)) {
                showToast("Formato de email inválido")
                return@setOnClickListener
            }

            // Validar el formato de contraseña
            val regexContraseña = "^(?=.*[A-Z])(?=.*[0-9]).+\$".toRegex()
            if (!contraseña.matches(regexContraseña)) {
                showToast("La contraseña debe contener al menos una letra mayúscula y un número")
                return@setOnClickListener
            }

            // Validar el formato de telefono
            if (telefono.length < 10) {
                showToast("El teléfono debe tener al menos 10 dígitos")
                return@setOnClickListener
            }

            val usuario = Usuario(nombre, direccion, telefono, email, contraseña)

            usuario.registrar(usuario) { success ->
                if (success) {
                    // Registro exitoso
                    finish() // Cerrar la actividad de registro y volver a la actividad de inicio de sesión
                } else {
                    // Error en el registro
                    showToast("Error en el registro")
                    buttonRegistrarse.isEnabled = true
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
