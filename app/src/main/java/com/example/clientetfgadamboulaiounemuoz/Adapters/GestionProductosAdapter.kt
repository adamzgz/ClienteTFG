package com.example.clientetfgadamboulaiounemuoz.Adapters

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.example.clientetfgadamboulaiounemuoz.R
import com.squareup.picasso.Picasso

class GestionProductosAdapter(
    private val context: Context,
    private var productos: List<Producto>,
    private val onItemLongClicked: (Producto) -> Unit
) : RecyclerView.Adapter<GestionProductosAdapter.ProductoViewHolder>() {

    // Actualizar los productos y notificar al adapter
    fun updateProductos(newProductos: List<Producto>) {
        productos = newProductos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_gestion_productos, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto)
        holder.itemView.setOnLongClickListener {
            onItemLongClicked(producto)
            false
        }
    }

    override fun getItemCount() = productos.size

    inner class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val imgProducto: ImageView = view.findViewById(R.id.imagenProducto)
        val txtNombre: TextView = view.findViewById(R.id.nombreProducto)
        val txtPrecio: TextView = view.findViewById(R.id.precioProducto)
        val txtCategoria: TextView = view.findViewById(R.id.nombreCategoria)

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        fun bind(producto: Producto) {
            // Cargar imagen del producto
            val imageUrl = "${URL.BASE_URL}/img_productos/${producto.imagen}"
            Picasso.get().load(imageUrl).error(R.drawable.noimage).into(imgProducto)

            txtNombre.text = producto.nombre + " con el id " + producto.id
            txtPrecio.text = "${producto.precio} €"

            // Obtener nombre de la categoría
            val token = getTokenFromSharedPreferences()
            Categoria.obtenerCategoria(token, producto.idCategoria) { categoria ->
                txtCategoria.text = categoria?.nombre ?: "Desconocido"
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.edit_producto, 0, "Editar producto")
            menu?.add(Menu.NONE, R.id.delete_producto, 1, "Eliminar producto")
        }

    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = context.getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }
}
