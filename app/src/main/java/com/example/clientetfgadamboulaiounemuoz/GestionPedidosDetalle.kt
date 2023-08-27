package com.example.clientetfgadamboulaiounemuoz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Adapters.VistaPedidoAdapter
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido

class GestionPedidosDetalle : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var vistaPedidoAdapter: VistaPedidoAdapter
    private val detallePedidos: MutableList<DetallePedido> = mutableListOf()
    private lateinit var buttonEnviarPedido: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_pedidos_detalle)

        recyclerView = findViewById(R.id.gestionVistaPedidoRecyclerView)
        vistaPedidoAdapter = VistaPedidoAdapter(this, detallePedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = vistaPedidoAdapter

        buttonEnviarPedido = findViewById(R.id.enviarPedidoButton)

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

                        // Comprobar estado del pedido
                        Pedido.obtenerPedidoPorId(token, idPedido) { pedido ->
                            if (pedido != null) {
                                if (pedido.estado == Pedido.EstadoPedido.PENDIENTE.name) {
                                    buttonEnviarPedido.visibility = View.VISIBLE
                                    buttonEnviarPedido.setOnClickListener {
                                        enviarPedido(token, idPedido)
                                    }
                                } else {
                                    buttonEnviarPedido.visibility = View.INVISIBLE
                                }
                            } else {
                                println("Error al obtener el estado del pedido.")
                            }
                        }
                    } else {
                        println("Error al obtener detalles del pedido.")
                    }
                }
            }
        } else {
            println("Token no disponible.")
        }
    }

    private fun enviarPedido(token: String, idPedido: Int) {
        Pedido.modificarPedido(token, idPedido, Pedido.EstadoPedido.ENVIADO) { success ->
            runOnUiThread {
                if (success) {
                    buttonEnviarPedido.visibility = View.INVISIBLE
                    println("Pedido enviado exitosamente.")
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    println("Error al enviar el pedido.")
                }
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }
}