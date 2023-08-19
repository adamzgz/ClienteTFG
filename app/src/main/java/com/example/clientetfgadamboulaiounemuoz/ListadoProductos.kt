package com.example.clientetfgadamboulaiounemuoz

import Producto
import ProductoAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class ListadoProductos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var categoriasSpinner: Spinner
    private lateinit var adapter: ProductoAdapter
    private var productos: List<Producto> = emptyList()
    private var categorias: List<Categoria> = listOf(Categoria(id = -1, nombre = "Todas"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_productos)

        listView = findViewById(R.id.productListView)
        categoriasSpinner = findViewById(R.id.categoriaSpinner)

        adapter = ProductoAdapter(this, productos)
        listView.adapter = adapter

        obtenerProductos()
        obtenerCategorias()

        categoriasSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val categoriaSeleccionada = categorias[position]
                filtrarProductosPorCategoria(categoriaSeleccionada)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se hace nada aquí
            }
        }
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

    private fun obtenerCategorias() {
        Categoria.obtenerCategorias { categoriasFromAPI ->
            if (categoriasFromAPI != null) {
                this.categorias = listOf(Categoria(id = -1, nombre = "Todas")) + categoriasFromAPI
                cargarCategoriasAlSpinner()
            } else {
                println("Error al cargar las categorías desde el servidor")
            }
        }
    }

    private fun cargarCategoriasAlSpinner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias.map { it.nombre })
        categoriasSpinner.adapter = spinnerAdapter
    }

    private fun filtrarProductosPorCategoria(categoria: Categoria) {
        if (categoria.id == -1) {  // Categoría "Todas"
            adapter = ProductoAdapter(this, productos)
        } else {
            val productosFiltrados = productos.filter { it.idCategoria == categoria.id }
            adapter = ProductoAdapter(this, productosFiltrados)
        }
        listView.adapter = adapter
    }
}
