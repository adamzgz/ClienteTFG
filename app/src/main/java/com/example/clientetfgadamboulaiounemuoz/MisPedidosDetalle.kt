package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.VistaPedidoAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto.Companion.obtenerProductoPorId

class MisPedidosDetalle : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var vistaPedidoAdapter: VistaPedidoAdapter
    private val detallePedidos: MutableList<DetallePedido> = mutableListOf()
    private var totalPrice: Double = 0.0


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
                if (detalles != null) {
                    // Reset total price
                    totalPrice = 0.0

                    // Loop through each detallePedido to get Product ID and calculate total
                    for (detalle in detalles) {
                        obtenerProductoPorId(token, detalle.idProducto) { producto ->
                            if (producto != null) {
                                runOnUiThread {
                                    // Accumulate total price
                                    totalPrice += producto.precio * detalle.cantidad

                                    // Update UI, assume totalTextView is a TextView showing total price
                                    val totalTextView: TextView = findViewById(R.id.totalTextView)
                                    totalTextView.text = String.format("Total: %.2f€", totalPrice)
                                }
                            }
                        }
                    }

                    runOnUiThread {
                        detallePedidos.clear()
                        detallePedidos.addAll(detalles)
                        vistaPedidoAdapter.notifyDataSetChanged()
                    }
                } else {
                    println("Error al obtener detalles del pedido.")
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