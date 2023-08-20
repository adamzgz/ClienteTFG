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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_categoria)

        val btnAccionCategoria = findViewById<Button>(R.id.btnCrearCategoria)

        // Verificar si hay un intent con una categoría para editar
        currentCategoria = intent.getSerializableExtra("categoria") as? Categoria
        if (currentCategoria != null) {
            isEditing = true
            findViewById<EditText>(R.id.editTextCategoriaNombre).setText(currentCategoria!!.nombre)
            btnAccionCategoria.text = "Actualizar"
        }

        btnAccionCategoria.setOnClickListener {
            if (validarFormulario()) {
                if (isEditing) {
                    confirmarActualizacion()
                } else {
                    crearCategoria()
                }
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
                        findViewById<Button>(R.id.btnCrearCategoria).isEnabled = true
                    }
                }
            }
        }
    }

    private fun actualizarCategoria() {
        val nombre = findViewById<EditText>(R.id.editTextCategoriaNombre).text.toString()

        getTokenFromSharedPreferences()?.let { token ->
            currentCategoria?.let { categoria ->
                Categoria.actualizarCategoria(token, categoria.id, nombre) { isSuccess ->
                    runOnUiThread {
                        if (isSuccess) {
                            showToastMessage("Categoría actualizada con éxito!")
                            finish() // Cierra la actividad si la categoría se actualizó con éxito
                        } else {
                            showToastMessage("Error al actualizar la categoría.")
                            findViewById<Button>(R.id.btnCrearCategoria).isEnabled = true
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
