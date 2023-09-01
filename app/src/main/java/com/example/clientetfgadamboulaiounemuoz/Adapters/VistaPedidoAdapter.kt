package com.example.clientetfgadamboulaiounemuoz.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido
import com.example.clientetfgadamboulaiounemuoz.R
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.squareup.picasso.Picasso

class VistaPedidoAdapter(private val context: Context, private val listaDetallePedido: MutableList<DetallePedido>) : RecyclerView.Adapter<VistaPedidoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vistaPedidoImage: ImageView = itemView.findViewById(R.id.vistaPedidoImage)
        val vistaPedidoName: TextView = itemView.findViewById(R.id.vistaPedidoName)
        val vistaPedidoCategory: TextView = itemView.findViewById(R.id.vistaPedidoCategory)
        val vistaPedidoPrice: TextView = itemView.findViewById(R.id.vistaPedidoPrice)
        val vistaPedidoQuantity: TextView = itemView.findViewById(R.id.vistaPedidoQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vista_pedido_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detallePedido = listaDetallePedido[position]

        holder.vistaPedidoQuantity.text = "Cantidad: ${detallePedido.cantidad}"

        Producto.obtenerProductoPorId(getToken(), detallePedido.idProducto) { producto ->
            (context as AppCompatActivity).runOnUiThread {
                holder.vistaPedidoName.text = producto?.nombre
                holder.vistaPedidoPrice.text = String.format("%.2f€", producto?.precio)
                val imageUrl = "${URL.BASE_URL}/img_productos/${producto?.imagen}"
                Picasso.get().load(imageUrl).placeholder(R.drawable.logo).error(R.drawable.noimage).into(holder.vistaPedidoImage)
            }
            Categoria.obtenerCategoria(getToken(), producto?.idCategoria ?: 0) { categoria ->
                (context as AppCompatActivity).runOnUiThread {
                    holder.vistaPedidoCategory.text = categoria?.nombre
                }
            }
        }
    }

    override fun getItemCount() = listaDetallePedido.size

    private fun getToken(): String {
        val sharedPreferences = context.getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
}
