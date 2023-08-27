package com.example.clientetfgadamboulaiounemuoz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.CarritoAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido


class CarritoCompra : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnComprar: Button
    private lateinit var carritoAdapter: CarritoAdapter
    private val detallePedidos: MutableList<DetallePedido> = mutableListOf()
    private lateinit var btnMisPedidos: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito_compra)

        recyclerView = findViewById(R.id.recyclerViewCarrito)
        btnComprar = findViewById(R.id.btnComprar)

        carritoAdapter = CarritoAdapter(this, detallePedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = carritoAdapter

        // Carga los datos del pedido y detalles
        cargarPedidoYDetalles()

        btnComprar.setOnClickListener {
            val token = getTokenFromSharedPreferences()
            if (token != null && token.isNotBlank()) {
                Pedido.comprobarPedidoEnProceso(token,
                    { pedidoEnProceso ->
                        val idPedido = pedidoEnProceso?.idPedido ?: return@comprobarPedidoEnProceso
                        Pedido.modificarPedido(token, idPedido, Pedido.EstadoPedido.PENDIENTE) { success ->
                            runOnUiThread {
                                if (success) {
                                    println("Estado del pedido actualizado a PENDIENTE.")
                                    detallePedidos.clear()
                                    carritoAdapter.notifyDataSetChanged()
                                    updateComprarButtonState()
                                } else {
                                    println("Error al actualizar el estado del pedido.")
                                }
                            }
                        }
                    },
                    {
                        println("Error: No se pudo crear un pedido en proceso.")
                    }
                )
            } else {
                println("Token no disponible.")
            }
        }
        btnMisPedidos = findViewById(R.id.btnMisPedidos)
        btnMisPedidos.setOnClickListener {
            val intent = Intent(this, MisPedidos::class.java)
            startActivity(intent)
        }



    }

    private fun cargarPedidoYDetalles() {
        val token = getTokenFromSharedPreferences()
        if (token != null && token.isNotBlank()) {
            Pedido.comprobarPedidoEnProceso(token,
                { pedido ->
                    val idPedido = pedido?.idPedido ?: return@comprobarPedidoEnProceso
                    DetallePedido.obtenerDetallesPorPedido(token, idPedido) { detalles ->
                        runOnUiThread {
                            if (detalles != null) {
                                detallePedidos.clear()
                                detallePedidos.addAll(detalles)
                                carritoAdapter.notifyDataSetChanged()  // Actualizar el RecyclerView
                                updateComprarButtonState()  // Actualizar el estado del botón Comprar
                            } else {
                                println("Error al obtener detalles del pedido.")
                            }
                        }
                    }
                },
                {
                    runOnUiThread {
                        println("Error: No se pudo crear un pedido en proceso.")
                    }
                }
            )
        } else {
            runOnUiThread {
                println("Token no disponible.")
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    private fun updateComprarButtonState() {
        btnComprar.isEnabled = detallePedidos.isNotEmpty()
    }
}
