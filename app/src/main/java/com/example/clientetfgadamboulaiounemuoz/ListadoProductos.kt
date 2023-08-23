package com.example.clientetfgadamboulaiounemuoz

import ProductoAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto

class ListadoProductos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var categoriasSpinner: Spinner
    private lateinit var adapter: ProductoAdapter
    private var productos: List<Producto> = emptyList()
    private var categorias: List<Categoria> = listOf(Categoria(id = -1, nombre = "Todas"))
    private lateinit var searchEditText: EditText


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
        searchEditText = findViewById(R.id.searchEditText)

        // Agregar el TextWatcher al EditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario hacer nada aquí
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario hacer nada aquí
            }

            override fun afterTextChanged(s: Editable?) {
                filtrarProductosPorNombre(s.toString())
            }
        })
        registerForContextMenu(listView)

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
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("token")
            remove("rol")  // Si utilizas "rol" en esta clase. Si no, puedes eliminar esta línea.
            apply()
        }

        // Iniciar actividad de inicio de sesión
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()  // Finaliza la actividad actual
    }

    private fun obtenerProductos() {
        val token = getToken()

        Producto.obtenerProductos(token) { productosResult ->
            if (productosResult != null) {
                this.productos = productosResult
                adapter = ProductoAdapter(this, productosResult)
                runOnUiThread {
                    listView.adapter = adapter
                }
            } else {
                println("Error al cargar los productos desde el servidor")
            }
        }

    }

    private fun obtenerCategorias() {
        val token = getToken()

        Categoria.obtenerCategorias(token) { categoriasResult ->
            if (categoriasResult != null) {
                // Añade la opción "Todas" y las categorías obtenidas del servidor
                this.categorias = listOf(Categoria(id = -1, nombre = "Todas")) + categoriasResult
                cargarCategoriasAlSpinner()
            } else {
                println("Error al cargar las categorías desde el servidor")
            }
        }
    }


    private fun cargarCategoriasAlSpinner() {
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias.map { it.nombre })
        runOnUiThread {
            categoriasSpinner.adapter = spinnerAdapter
        }
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

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
    private fun filtrarProductosPorNombre(query: String) {
        val productosFiltrados = if (query.isEmpty()) {
            productos
        } else {
            productos.filter { producto ->
                producto.nombre.contains(query, ignoreCase = true)
            }
        }
        adapter = ProductoAdapter(this, productosFiltrados)
        listView.adapter = adapter
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.menuInfo is AdapterView.AdapterContextMenuInfo) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            val adapter = listView.adapter as ProductoAdapter
            val viewHolder = listView.getChildAt(info.position - listView.firstVisiblePosition)?.tag as? ProductoAdapter.ViewHolder
            val productoSeleccionado = viewHolder?.getCurrentProducto()
            println("entra")
            return when (item.itemId) {
                R.id.add_carrito -> {
                    productoSeleccionado?.let {
                        agregarProductoAlCarrito(it.id)
                    }
                    true
                }
                else -> super.onContextItemSelected(item)
            }
        } else {
            // Log the issue or handle it appropriately
            return super.onContextItemSelected(item)
        }
    }

    private fun agregarProductoAlCarrito(idProducto: Int) {

        // Comprobar si hay un pedido en proceso
        Pedido.comprobarPedidoEnProceso(getToken()) { pedidoEnProceso ->
            if (pedidoEnProceso != null) {
                // Si hay un pedido en proceso, añadir el producto al pedido existente
                val detalle = DetallePedido(idPedido = pedidoEnProceso.idPedido!!, idProducto = idProducto, cantidad = 1)
                DetallePedido.insertarDetallePedido(getToken(), detalle) { success ->
                    if (success) {
                        println("Producto añadido al carrito exitosamente.")
                        // Actualizar la interfaz del usuario o mostrar un mensaje si es necesario
                    } else {
                        println("Error al añadir el producto al carrito.")
                        // Manejar el error y notificar al usuario
                    }
                }
            } else {
                println("Error al obtener el pedido en proceso.")
                // Manejar el error y notificar al usuario
            }
        }
    }
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.add_carrito, menu) // Replace 'your_context_menu' with your actual context menu XML filename
    }



}
