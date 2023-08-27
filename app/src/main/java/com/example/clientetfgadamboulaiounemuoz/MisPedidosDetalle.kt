package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.VistaPedidoAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido

class MisPedidosDetalle : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var vistaPedidoAdapter: VistaPedidoAdapter
    private val detallePedidos: MutableList<DetallePedido> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos_detalle)

        recyclerView = findViewById(R.id.vistaPedidoRecyclerView)
        vistaPedidoAdapter = VistaPedidoAdapter(this, detallePedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = vistaPedidoAdapter

        // Obtener idPedido desde el Intent
        val idPedido = intent.getIntExtra("idPedido", 0)

        if (idPedido != 0) {
            cargarPedidoYDetalles(idPedido)
        }
    }

    private fun cargarPedidoYDetalles(idPedido: Int) {
        val token = getTokenFromSharedPreferences()
        if (token != null && token.isNotBlank()) {
            DetallePedido.obtenerDetallesPorPedido(token, idPedido) { detalles ->
                runOnUiThread {
                    if (detalles != null) {
                        detallePedidos.clear()
                        detallePedidos.addAll(detalles)
                        vistaPedidoAdapter.notifyDataSetChanged()
                    } else {
                        println("Error al obtener detalles del pedido.")
                    }
                }
            }
        } else {
            println("Token no disponible.")
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
}