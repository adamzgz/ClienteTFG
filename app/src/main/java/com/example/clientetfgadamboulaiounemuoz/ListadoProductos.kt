package com.example.clientetfgadamboulaiounemuoz

import Producto
import ProductoAdapter
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListadoProductos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ProductoAdapter
    private var productos: List<Producto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_productos)

        listView = findViewById(R.id.productListView)
        adapter = ProductoAdapter(this, productos)
        listView.adapter = adapter
        agregarProductoFicticio()
    }

    private fun obtenerProductos() {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        Producto.obtenerProductos(token!!) { productos ->
            if (productos != null) {
                this.productos = productos
                adapter = ProductoAdapter(this, productos)
                listView.adapter = adapter
            } else {
                println("Error al cargar los productos desde el servidor")
            }
        }
    }


    private fun obtenerProductosDeLaAPI(): List<Producto> {
        // Aquí implementas la lógica para obtener los productos desde tu API
        // o cualquier otra fuente de datos
        // Devuelve una lista de productos obtenidos

        // Ejemplo de lista de productos ficticia
        val productos: List<Producto> = listOf(
            Producto("Producto 1", "Descripción del producto 1", 10.0, 5, 1, "imagen1.png"),
            Producto("Producto 2", "Descripción del producto 2", 20.0, 8, 2, "imagen2.png"),
            Producto("Producto 3", "Descripción del producto 3", 15.0, 3, 3, "imagen3.png"),
            Producto("Producto 4", "Descripción del producto 4", 12.0, 6, 4, "imagen4.png")
        )

        return productos
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
                obtenerProductos()
            } else {
                // Manejar el caso de error al crear el producto ficticio en el servidor
            }
        }
    }

}
