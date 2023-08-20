package com.example.clientetfgadamboulaiounemuoz

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.CategoriaAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria

class ListadoCategorias : AppCompatActivity() {

    private lateinit var categoriasAdapter: CategoriaAdapter
    private var categorias: List<Categoria> = listOf()
    private lateinit var selectedCategoria: Categoria  // Categoría seleccionada a través del menú contextual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_categorias)

        val categoriasRecyclerView = findViewById<RecyclerView>(R.id.categoriasRecyclerView)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        categoriasAdapter = CategoriaAdapter(categorias) { categoria ->
            // Callback para asignar la categoría seleccionada
            selectedCategoria = categoria
        }
        categoriasRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListadoCategorias)
            adapter = categoriasAdapter
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                val filteredCategorias = categorias.filter { it.nombre.contains(query, ignoreCase = true) }
                categoriasAdapter.updateCategorias(filteredCategorias)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadCategorias()
        registerForContextMenu(categoriasRecyclerView)
    }

    private fun loadCategorias() {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        Categoria.obtenerCategorias(token!!) { categoriasFromAPI ->
            categorias = categoriasFromAPI ?: listOf()
            runOnUiThread {
                categoriasAdapter.updateCategorias(categorias)
            }
        }
    }

    // Registrar el menú contextual para el RecyclerView
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(0, v.id, 0, "Editar categoría")
        menu.add(0, v.id, 1, "Eliminar categoría")
    }

    // Acciones al seleccionar una opción del menú contextual
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.title) {
            "Editar categoría" -> {
                val intent = Intent(this, addCategoria::class.java)
                intent.putExtra("categoriaSeleccionada", selectedCategoria)
                startActivity(intent)
            }
            "Eliminar categoría" -> {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que quieres eliminar esta categoría?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        eliminarCategoria(selectedCategoria)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun eliminarCategoria(categoria: Categoria) {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: ""

        Categoria.eliminarCategoria(token, categoria.id) { success ->
            if (success) {
                // Actualizar la lista de categorías tras la eliminación
                loadCategorias()
            } else {
                // Mostrar mensaje de error
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Ocurrió un error al eliminar la categoría.")
                    .setPositiveButton("Aceptar", null)
                    .show()
            }
        }
    }
}
