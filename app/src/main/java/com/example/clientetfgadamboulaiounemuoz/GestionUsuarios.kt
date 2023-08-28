package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.UsuarioAdapter
import Usuario
import android.widget.Toast

class GestionUsuarios : AppCompatActivity() {
    lateinit var usuarioAdapter: UsuarioAdapter
    private var selectedPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuarioAdapter = UsuarioAdapter(emptyList())
        recyclerView.adapter = usuarioAdapter

        // Register RecyclerView for context menu
        registerForContextMenu(recyclerView)

        val token = getToken()

        Usuario.obtenerTodosLosUsuarios(token) { success, usuarios ->
            if (success && usuarios != null) {
                runOnUiThread {
                    usuarioAdapter.updateUsuarios(usuarios)
                }
            }
        }

        usuarioAdapter.itemLongClickListener = { view, position ->
            selectedPosition = position
            openContextMenu(view)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.deshabilitar_usuario, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deshabilitar_usuario) {
            val position = selectedPosition
            if (position != null) {
                val usuario = usuarioAdapter.usuarios[position]
                val token = getToken()

                Usuario.deshabilitarCuentaAdmin(token, usuario.id ?: 0) { success ->
                    if (success) {
                        runOnUiThread {
                            usuarioAdapter.usuarios = usuarioAdapter.usuarios.filter { it.id != usuario.id }
                            usuarioAdapter.notifyDataSetChanged()
                            showToast("Usuario deshabilitado con éxito")
                        }
                    } else {
                        showToast("Error al deshabilitar usuario")
                    }
                }
            }
            return true
        }
        return super.onContextItemSelected(item)
    }


    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

}
