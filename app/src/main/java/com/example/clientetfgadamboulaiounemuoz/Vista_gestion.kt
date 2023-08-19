package com.example.clientetfgadamboulaiounemuoz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Vista_gestion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_gestion)

        // Agregar un listener al botón btnAnadirProducto
        findViewById<Button>(R.id.btnAnadirProducto).setOnClickListener {
            val intent = Intent(this, addProducto::class.java) // Crea un Intent para iniciar addProducto
            startActivity(intent) // Inicia addProducto
        }
    }
}
