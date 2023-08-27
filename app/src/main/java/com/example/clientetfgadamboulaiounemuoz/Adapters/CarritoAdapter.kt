package com.example.clientetfgadamboulaiounemuoz.Adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.DetallePedido
import com.example.clientetfgadamboulaiounemuoz.R
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.squareup.picasso.Picasso

class CarritoAdapter(private val context: Context, private val listaDetallePedido: MutableList<DetallePedido>) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    private val handler = Handler()
    private val updateDelay: Long = 1000  // 1-second delay

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantityEditText: EditText = itemView.findViewById(R.id.productQuantity)
        val sumButton: Button = itemView.findViewById(R.id.sumButton)
        val subtractButton: Button = itemView.findViewById(R.id.subtractButton)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carrito_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detallePedido = listaDetallePedido[position]
        println("DetallePedido en posición $position: $detallePedido")

        holder.productQuantityEditText.setText(detallePedido.cantidad.toString())

        holder.sumButton.setOnClickListener {
            updateProductQuantity(holder, detallePedido, 1)
        }

        holder.subtractButton.setOnClickListener {
            updateProductQuantity(holder, detallePedido, -1)
        }

        Producto.obtenerProductoPorId(getToken(), detallePedido.idProducto) { producto ->
            (context as AppCompatActivity).runOnUiThread {
                println("Producto obtenido: $producto")
                holder.productName.text = producto?.nombre
                holder.productPrice.text = producto?.precio.toString()
                holder.productQuantityEditText.setText(detallePedido.cantidad.toString())
                val imageUrl = "${URL.BASE_URL}/img_productos/${producto?.imagen}"
                Picasso.get().load(imageUrl).placeholder(R.drawable.logo).error(R.drawable.noimage).into(holder.productImage)
            }
            Categoria.obtenerCategoria(getToken(), producto?.idCategoria ?: 0) { categoria ->
                (context as AppCompatActivity).runOnUiThread {
                    holder.productCategory.text = categoria?.nombre
                }
            }
        }

        holder.deleteIcon.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar DetallePedido")
                .setMessage("¿Estás seguro de que deseas eliminar este DetallePedido?")
                .setPositiveButton("Sí") { _, _ ->
                    val id = detallePedido.id // Make sure 'id' is correctly set in the 'DetallePedido' object
                    if (id != null) {
                        DetallePedido.borrarDetallePedido(getToken(), id) { success ->
                            (context as AppCompatActivity).runOnUiThread {
                                if (success) {
                                    println("DetallePedido eliminado con éxito del servidor.")
                                    listaDetallePedido.removeAt(position)
                                    notifyItemRemoved(position)
                                } else {
                                    println("Error al eliminar el DetallePedido del servidor.")
                                }
                            }
                        }

                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
        holder.productQuantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                val newQuantity = s.toString().toIntOrNull()

                if (newQuantity != null) {
                    if (newQuantity >= 1) {
                        detallePedido.cantidad = newQuantity
                        handler.removeCallbacksAndMessages(null)  // Remove any pending tasks
                        handler.postDelayed({
                            notifyBackend(holder, detallePedido)
                        }, updateDelay)
                    } else {
                        holder.productQuantityEditText.setText("1")  // Establece el valor de 1 si el usuario intenta ingresar un valor menor
                        Toast.makeText(context, "La cantidad no puede ser inferior a 1", Toast.LENGTH_SHORT).show()
                        detallePedido.cantidad = 1
                        handler.removeCallbacksAndMessages(null)  // Remove any pending tasks
                        handler.postDelayed({
                            notifyBackend(holder, detallePedido)
                        }, updateDelay)
                    }
                } else {
                    holder.productQuantityEditText.setText("1")  // Establece el valor de 1 si el campo está vacío o es inválido
                    Toast.makeText(context, "La cantidad no puede estar vacía o ser inválida", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun updateProductQuantity(holder: ViewHolder, detallePedido: DetallePedido, delta: Int) {
        var quantity = holder.productQuantityEditText.text.toString().toInt() + delta
        if (quantity >= 1) {
            holder.productQuantityEditText.setText(quantity.toString())
            detallePedido.cantidad = quantity

            handler.removeCallbacksAndMessages(null)  // Remove any pending tasks
            handler.postDelayed({
                notifyBackend(holder, detallePedido)
            }, updateDelay)
        }
    }

    private fun notifyBackend(holder: ViewHolder, detallePedido: DetallePedido) {
        Pedido.comprobarPedidoEnProceso(getToken(),
            { pedidoEnProceso ->  // Callback cuando el pedido se encuentra o se crea
                if (pedidoEnProceso != null) {
                    val detalle = DetallePedido(id = detallePedido.id, idPedido = pedidoEnProceso.idPedido!!, idProducto = detallePedido.idProducto, cantidad = detallePedido.cantidad)
                    DetallePedido.modificarDetallePedido(getToken(), detallePedido.id!!, detallePedido.cantidad) { success ->
                        if (success) {
                            println("Producto quantity successfully updated in cart.")
                        } else {
                            println("Error updating product quantity in cart.")
                        }
                    }
                } else {
                    println("Error fetching the in-process order.")
                }
            },
            {  // Fallback cuando falla la creación del pedido
                println("Error al crear un nuevo pedido, intenta nuevamente.")
                // Puedes intentar llamar a notifyBackend nuevamente o manejar el error de alguna otra manera
            }
        )
    }


    override fun getItemCount() = listaDetallePedido.size

    private fun getToken(): String {
        val sharedPreferences = context.getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
}
