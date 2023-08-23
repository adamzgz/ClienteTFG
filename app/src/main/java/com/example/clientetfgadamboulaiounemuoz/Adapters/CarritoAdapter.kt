package com.example.clientetfgadamboulaiounemuoz.Adapters

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido
import com.example.clientetfgadamboulaiounemuoz.R
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.squareup.picasso.Picasso

class CarritoAdapter(private val context: Context, private val listaDetallePedido: List<DetallePedido>) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    private val handler = Handler()
    private val updateDelay: Long = 1000  // Retraso de 1 segundo

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantityEditText: EditText = itemView.findViewById(R.id.productQuantity)
        val sumButton: Button = itemView.findViewById(R.id.sumButton)
        val subtractButton: Button = itemView.findViewById(R.id.subtractButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carrito_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detallePedido = listaDetallePedido[position]

        holder.productQuantityEditText.setText(detallePedido.cantidad.toString())

        holder.sumButton.setOnClickListener {
            updateProductQuantity(holder, detallePedido, 1)
        }

        holder.subtractButton.setOnClickListener {
            updateProductQuantity(holder, detallePedido, -1)
        }

        Producto.obtenerProductoPorId(getToken(), detallePedido.idProducto) { producto ->
            holder.productName.text = producto?.nombre
            holder.productPrice.text = producto?.precio.toString()
            holder.productQuantityEditText.setText(detallePedido.cantidad.toString())

            Categoria.obtenerCategoria(getToken(), producto?.idCategoria ?: 0) { categoria ->
                holder.productCategory.text = categoria?.nombre
            }

            val imageUrl = "${URL.BASE_URL}/img_productos/${producto?.imagen}"
            Picasso.get().load(imageUrl).placeholder(R.drawable.logo).error(R.drawable.noimage).into(holder.productImage)
        }
    }

    private fun updateProductQuantity(holder: ViewHolder, detallePedido: DetallePedido, delta: Int) {
        var quantity = holder.productQuantityEditText.text.toString().toInt() + delta
        if (quantity >= 1) {
            holder.productQuantityEditText.setText(quantity.toString())
            detallePedido.cantidad = quantity

            handler.removeCallbacksAndMessages(null)  // Eliminar cualquier tarea pendiente
            handler.postDelayed({
                notifyBackend(holder, detallePedido)
            }, updateDelay)
        }
    }

    private fun notifyBackend(holder: ViewHolder, detallePedido: DetallePedido) {
        Pedido.comprobarPedidoEnProceso(getToken()) { pedidoEnProceso ->
            if (pedidoEnProceso != null) {
                val detalle = DetallePedido(idPedido = pedidoEnProceso.idPedido!!, idProducto = detallePedido.idProducto, cantidad = detallePedido.cantidad)
                DetallePedido.insertarDetallePedido(getToken(), detalle) { success ->
                    if (success) {
                        println("Cantidad del producto actualizada exitosamente en el carrito.")
                    } else {
                        println("Error al actualizar la cantidad del producto en el carrito.")
                    }
                }
            } else {
                println("Error al obtener el pedido en proceso.")
            }
        }
    }

    override fun getItemCount() = listaDetallePedido.size

    private fun getToken(): String {
        val sharedPreferences = context.getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
}
