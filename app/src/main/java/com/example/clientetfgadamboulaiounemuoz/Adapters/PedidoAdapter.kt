package com.example.clientetfgadamboulaiounemuoz.Adapters

import Pedido
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.*
import java.text.SimpleDateFormat
import java.util.Locale

class PedidoAdapter(private val context: Context, private val pedidos: List<Pedido>) :
    RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    companion object {
        const val REQUEST_CODE = 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.pedido_adapter, parent, false)
        return PedidoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.tvIdPedido.text = "ID Pedido: ${pedido.idPedido}"
        holder.tvEstado.text = "Estado: ${pedido.estado}"
        println(pedido.fechaPedido)
        // Verificar si la fecha no es nula antes de intentar formatearla
        if (pedido.fechaPedido != null) {
            try {
                val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val targetFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = originalFormat.parse(pedido.fechaPedido)
                println("LA FECHA DEL PEDIDO ES " + pedido.fechaPedido)
                val formattedDate = targetFormat.format(date)
                holder.tvFecha.text = "Fecha: $formattedDate"
            } catch (e: Exception) {
                // Si hay un error en la conversión, muestra la fecha original
                holder.tvFecha.text = "Fecha: ${pedido.fechaPedido}"
            }
        } else {
            holder.tvFecha.text = "Fecha: N/A"
        }

        holder.btnVerPedido.setOnClickListener {
            val intent = when {
                context is MisPedidos -> Intent(context, MisPedidosDetalle::class.java)
                context is GestionPedidos -> Intent(context, GestionPedidosDetalle::class.java)
                else -> throw IllegalArgumentException("Actividad desconocida")
            }
            intent.putExtra("idPedido", pedido.idPedido)
            if (context is GestionPedidos) {
                context.startActivityForResult(intent, REQUEST_CODE)
            } else {
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdPedido: TextView = itemView.findViewById(R.id.tvIdPedido)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val btnVerPedido: Button = itemView.findViewById(R.id.btnVerPedido)
    }
}
