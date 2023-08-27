package com.example.clientetfgadamboulaiounemuoz

import Pedido.Companion.obtenerPedidosPorCliente
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.PedidoAdapter

class MisPedidos : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos)

        // Obtener token desde SharedPreferences
        val token = getTokenFromSharedPreferences()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMisPedidos)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener la lista de pedidos
        if (token != null) {
            // Usar la función obtenerPedidosPorCliente aquí
            obtenerPedidosPorCliente(token) { pedidos ->
                runOnUiThread {
                    if (pedidos != null) {
                        // Configurar el adaptador
                        val adapter = PedidoAdapter(this@MisPedidos, pedidos)
                        recyclerView.adapter = adapter
                    } else {
                        // Opcionalmente, manejar el caso en el que no hay pedidos o ha ocurrido un error.
                    }
                }
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
}

