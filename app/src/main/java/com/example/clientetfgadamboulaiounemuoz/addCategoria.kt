package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria

class addCategoria : AppCompatActivity() {

    private var isEditing: Boolean = false
    private var currentCategoria: Categoria? = null
    private lateinit var btnAccionCategoria: Button // Define btnAccionCategoria a nivel de clase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_categoria)

        btnAccionCategoria = findViewById(R.id.btnCrearCategoria)

        initializeView()
        handleButtonAction()
    }

    private fun initializeView() {
        currentCategoria = intent.getSerializableExtra("categoriaSeleccionada") as? Categoria
        println(currentCategoria?.nombre)
        if (currentCategoria != null) {
            isEditing = true
            findViewById<EditText>(R.id.editTextCategoriaNombre).setText(currentCategoria!!.nombre)
            btnAccionCategoria.text = "Actualizar"
        }
    }

    private fun handleButtonAction() {
        btnAccionCategoria.setOnClickListener {
            btnAccionCategoria.isEnabled = false // Desactivar botón al iniciar operación
            if (validarFormulario()) {
                if (isEditing) {
                    confirmarActualizacion()
                } else {
                    crearCategoria()
                }
            } else {
                btnAccionCategoria.isEnabled = true // Reactivar botón si validación falla
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    private fun crearCategoria() {
        val nombre = findViewById<EditText>(R.id.editTextCategoriaNombre).text.toString()
        val categoria = Categoria(0, nombre)

        getTokenFromSharedPreferences()?.let { token ->
            Categoria.crearCategoria(token, categoria) { isSuccess ->
                runOnUiThread {
                    if (isSuccess) {
                        showToastMessage("Categoría creada con éxito!")
                        finish() // Cierra la actividad si la categoría se creó con éxito
                    } else {
                        showToastMessage("Error al crear la categoría.")
                        btnAccionCategoria.isEnabled = true
                    }
                }
            }
        }
    }

    private fun actualizarCategoria() {
        val nombre = findViewById<EditText>(R.id.editTextCategoriaNombre).text.toString()
        currentCategoria!!.nombre = nombre
        getTokenFromSharedPreferences()?.let { token ->
            currentCategoria?.let { categoria ->
                Categoria.actualizarCategoria(token, categoria) { isSuccess ->
                    runOnUiThread {
                        if (isSuccess) {
                            showToastMessage("Categoría actualizada con éxito!")
                            finish() // Cierra la actividad si la categoría se actualizó con éxito
                        } else {
                            showToastMessage("Error al actualizar la categoría.")
                            btnAccionCategoria.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun confirmarActualizacion() {
        val nombre = findViewById<EditText>(R.id.editTextCategoriaNombre).text.toString()
        AlertDialog.Builder(this)
            .setTitle("Confirmar actualización")
            .setMessage("¿Deseas modificar la categoría con ID ${currentCategoria?.id} y nombre ${currentCategoria?.nombre} a nombre $nombre?")
            .setPositiveButton("Sí") { _, _ ->
                actualizarCategoria()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun validarFormulario(): Boolean {
        val nombre = findViewById<EditText>(R.id.editTextCategoriaNombre).text.toString()

        if (nombre.isEmpty()) {
            showToastMessage("El nombre de la categoría no puede estar vacío.")
            return false
        }

        return true
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
