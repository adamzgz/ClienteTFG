package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.GestionProductosAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.EditText

class ListadoProductoGestion : AppCompatActivity() {

    lateinit var productosRecyclerView: RecyclerView
    lateinit var searchEditText: EditText
    lateinit var selectedProducto: Producto  // Producto seleccionado a través del menú contextual
    var originalProductosList: List<Producto> = emptyList()
    var filteredProductosList: List<Producto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_producto_gestion)
        println("VISTA LISTADO PRODUCTO GESTION")

        // 1. Inicializar tu RecyclerView y EditText
        productosRecyclerView = findViewById(R.id.productosRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        // 2. Definir y establecer un LayoutManager
        productosRecyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Hacer la solicitud para obtener los productos
        cargarProductos()

        // 4. Configurar el TextWatcher para filtrar productos
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filteredProductosList = originalProductosList.filter {
                    it.nombre.contains(s.toString(), ignoreCase = true)
                }
                updateRecyclerView()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun cargarProductos() {
        val token = getTokenFromSharedPreferences()
        Producto.obtenerProductos(token!!) { productos ->
            runOnUiThread {
                if (productos != null) {
                    originalProductosList = productos
                    filteredProductosList = productos // Inicialmente, ambas listas serán iguales
                    updateRecyclerView()
                } else {
                    // Aquí puedes manejar errores o mostrar un mensaje si no hay productos
                }
            }
        }
    }

    private fun updateRecyclerView() {
        val adapter = GestionProductosAdapter(this, filteredProductosList) { producto ->
            selectedProducto = producto
        }
        productosRecyclerView.adapter = adapter
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_producto -> {
                editarProducto()
            }
            R.id.delete_producto -> {
                confirmarEliminacion()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun editarProducto() {
        val intent = Intent(this, addProducto::class.java)
        intent.putExtra("productoSeleccionado", selectedProducto)
        startActivity(intent)
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar este producto?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto() {
        val token = getTokenFromSharedPreferences()!!
        Producto.borrarProducto(token, selectedProducto.id) { success ->
            if (success) {
                runOnUiThread {
                    cargarProductos()
                }
            } else {
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Ocurrió un error al eliminar el producto.")
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }
}
