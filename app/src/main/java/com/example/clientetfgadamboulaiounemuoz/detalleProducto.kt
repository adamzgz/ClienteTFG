package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.squareup.picasso.Picasso

class detalleProducto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        // Obtener referencias a las vistas donde mostrarás los detalles del producto
        val imagenImageView: ImageView = findViewById(R.id.productImage)  // Suponiendo que tienes un ImageView con este ID
        val nombreTextView: TextView = findViewById(R.id.productTitle)  // Suponiendo que tienes un TextView con este ID
        val descripcionTextView: TextView = findViewById(R.id.productDescription)
        val precioTextView : TextView = findViewById(R.id.productPrice)// Suponiendo que tienes un TextView con este ID

        // Obtener el ID del producto del Intent
        val productoId = intent.getIntExtra("productoId", -1)

        if (productoId != -1) {
            val token = getToken()  // Asume que tienes un método para obtener el token

            // Llamada para obtener el producto por su ID
            Producto.obtenerProductoPorId(token, productoId) { producto ->
                if (producto != null) {
                    // Actualizar la UI con los detalles del producto
                    runOnUiThread {
                        val imageUrl = "${URL.BASE_URL}/img_productos/${producto.imagen}"
                        Picasso.get().load(imageUrl).placeholder(R.drawable.logo).error(R.drawable.noimage).into(imagenImageView)

                        nombreTextView.text = producto.nombre
                        descripcionTextView.text = producto.descripcion
                        precioTextView.text = String.format("%.2f€", producto.precio)
                    }
                }
            }
        }
    }

    // Aquí deberías tener tu método para obtener el token, si es necesario.
    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }

}
