package com.example.clientetfgadamboulaiounemuoz

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class Vista_gestion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_gestion)


        findViewById<Button>(R.id.btnAnadirProducto).setOnClickListener {
            val intent = Intent(this, addProducto::class.java) // Crea un Intent para iniciar addProducto
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAnadirCategoria).setOnClickListener {
            val intent = Intent(this, addCategoria::class.java) // Crea un Intent para iniciar addProducto
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnVerCategorias).setOnClickListener {
            val intent = Intent(this, ListadoCategorias::class.java) // Crea un Intent para iniciar addProducto
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnGestionProductos).setOnClickListener {
            val intent = Intent(this, ListadoProductoGestion::class.java) // Crea un Intent para iniciar addProducto
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Limpiar SharedPreferences
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("token")
            remove("rol")
            apply()
        }

        // Iniciar actividad de inicio de sesión
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()  // Finaliza la actividad actual para que el usuario no pueda regresar a ella con el botón de retroceso.
    }
}
