package com.example.clientetfgadamboulaiounemuoz

import Producto
import Producto.Companion.obtenerProductos
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class Vista_gestion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_gestion)
        agregarProductoFicticio()
    }
    private fun agregarProductoFicticio() {
        val productoFicticio = Producto(
            "Producto ficticio",
            "Descripción del producto ficticio",
            9.99,
            10,
            1,
            "imagen_producto_ficticio.png"
        )

        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        Log.i("tokenprefs",token!!)
        Producto.crearProducto(productoFicticio, token!!) { success ->
            if (success) {
                // Producto ficticio creado exitosamente en el servidor
                // Actualizar la lista de productos y el adaptador
            } else {
                // Manejar el caso de error al crear el producto ficticio en el servidor
            }
        }
    }

}