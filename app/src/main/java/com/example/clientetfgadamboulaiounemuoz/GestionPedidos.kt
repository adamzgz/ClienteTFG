package com.example.clientetfgadamboulaiounemuoz

import Pedido.Companion.obtenerPedidos
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.PedidoAdapter

class GestionPedidos : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE = 1
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_pedidos)
        val recyclerView = findViewById<RecyclerView>(R.id.pedidosRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

            actualizarListaPedidos()

    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            actualizarListaPedidos()
        }
    }
    private fun actualizarListaPedidos() {
        val token = getTokenFromSharedPreferences()
        if (token != null) {
            obtenerPedidos(token) { pedidos ->
                runOnUiThread {
                    if (pedidos != null) {
                        val adapter = PedidoAdapter(this, pedidos)
                        findViewById<RecyclerView>(R.id.pedidosRecyclerView).adapter = adapter
                    } else {
                        // Maneja el error o el caso vacío
                    }
                }
            }
        }
    }

}