package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.UsuarioAdapter

class GestionUsuarios : AppCompatActivity() {
    lateinit var usuarioAdapter: UsuarioAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        usuarioAdapter = UsuarioAdapter(emptyList())
        recyclerView.adapter = usuarioAdapter

        val token = getToken()

        Usuario.obtenerTodosLosUsuarios(token) { success, usuarios ->
            if (success && usuarios != null) {
                runOnUiThread {
                    usuarioAdapter.updateUsuarios(usuarios)
                }
            }
        }
    }
    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
}