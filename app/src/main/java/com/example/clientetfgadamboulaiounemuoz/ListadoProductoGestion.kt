package com.example.clientetfgadamboulaiounemuoz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.GestionProductosAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import android.app.AlertDialog
import android.view.ContextMenu
import android.view.View

class ListadoProductoGestion : AppCompatActivity() {

    lateinit var productosRecyclerView: RecyclerView
    lateinit var selectedProducto: Producto  // Producto seleccionado a través del menú contextual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_producto_gestion)
        println("VISTA LISTADO PRODUCTO GESTION")
        // 1. Inicializar tu RecyclerView
        productosRecyclerView = findViewById(R.id.productosRecyclerView)

        // 2. Definir y establecer un LayoutManager
        productosRecyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Hacer la solicitud para obtener los productos

            cargarProductos()


    }

    private fun cargarProductos() {
        val token = getTokenFromSharedPreferences()
        Producto.obtenerProductos(token!!) { productos ->
            runOnUiThread {
                if (productos != null) {
                    // 4. Establecer el Adapter con los productos obtenidos y la función de callback
                    val adapter = GestionProductosAdapter(this, productos) { producto ->
                        // Callback para asignar el producto seleccionado
                        selectedProducto = producto
                    }
                    productosRecyclerView.adapter = adapter
                    registerForContextMenu(productosRecyclerView)
                } else {
                    // Aquí puedes manejar errores o mostrar un mensaje si no hay productos
                }
            }
        }
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
        val intent = Intent(this, addProducto::class.java)  // Asumiendo que tienes una actividad llamada AddProductoActivity
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
                    // Aquí puedes mostrar un mensaje de confirmación de eliminación si lo deseas
                }
            } else {
                runOnUiThread {
                    // Aquí puedes manejar el error o mostrar un mensaje de fallo
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
