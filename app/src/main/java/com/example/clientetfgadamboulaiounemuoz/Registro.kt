package com.example.clientetfgadamboulaiounemuoz

import Usuario
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Registro : AppCompatActivity() {
    private lateinit var editTextNombre: EditText
    private lateinit var editTextDireccion: EditText
    private lateinit var editTextTelefono: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextContraseña: EditText
    private lateinit var buttonRegistrarse: Button
    private lateinit var buttonDeshabilitarCuenta: Button
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Initialize views
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDireccion = findViewById(R.id.editTextDireccion)
        editTextTelefono = findViewById(R.id.editTextTelefono)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextContraseña = findViewById(R.id.editTextContraseña)
        buttonRegistrarse = findViewById(R.id.buttonRegistrarse)
        buttonDeshabilitarCuenta = findViewById(R.id.buttonDeshabilitarCuenta)

        // Check if in edit mode
        isEditMode = intent.getBooleanExtra("EDITAR_PERFIL", false)

        val token = getToken()

        if (isEditMode) {
            buttonRegistrarse.text = "Actualizar Usuario"

            buttonDeshabilitarCuenta.visibility = View.VISIBLE
            buttonDeshabilitarCuenta.isEnabled = true

            Usuario.obtenerUsuario(token) { success, usuario ->
                if (success && usuario != null) {
                    editTextNombre.setText(usuario.nombre)
                    editTextDireccion.setText(usuario.direccion)
                    editTextTelefono.setText(usuario.telefono)
                    editTextEmail.setText(usuario.email)
                    editTextContraseña.setText(usuario.contraseña)
                } else {
                    showToast("Error al cargar usuario")
                }
            }

            buttonDeshabilitarCuenta.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Deshabilitar cuenta")
                    .setMessage("¿Estás seguro de que quieres deshabilitar tu cuenta?")
                    .setPositiveButton("Sí") { _, _ ->
                        Usuario.deshabilitarMiCuenta(token) { success ->
                            if (success) {
                                clearToken()
                                showToast("Cuenta deshabilitada con éxito")
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                showToast("No se pudo deshabilitar la cuenta")
                            }
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        } else {
            buttonDeshabilitarCuenta.visibility = View.GONE
            buttonDeshabilitarCuenta.isEnabled = false
        }

        buttonRegistrarse.setOnClickListener {
            val usuario = Usuario(
                null,
                editTextNombre.text.toString(),
                editTextDireccion.text.toString(),
                editTextTelefono.text.toString(),
                editTextEmail.text.toString(),
                editTextContraseña.text.toString()
            )

            if (isEditMode) {
                AlertDialog.Builder(this)
                    .setTitle("Actualizar perfil")
                    .setMessage("¿Estás seguro de que quieres actualizar tu perfil?")
                    .setPositiveButton("Sí") { _, _ ->
                        Usuario.actualizarUsuario(token, usuario.id ?: 0, Usuario.UsuarioDto(
                            usuario.nombre,
                            usuario.direccion,
                            usuario.telefono,
                            usuario.email,
                            usuario.contraseña,
                            usuario.imagen
                        )) { success ->
                            if (success) {
                                finish()
                            } else {
                                showToast("Error al actualizar")
                            }
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Usuario.registrar(usuario) { success ->
                    if (success) {
                        finish()
                    } else {
                        showToast("Error en el registro")
                    }
                }
            }
        }
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }

    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("token").apply()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
