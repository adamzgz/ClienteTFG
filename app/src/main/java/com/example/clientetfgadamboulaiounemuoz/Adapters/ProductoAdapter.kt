import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.example.clientetfgadamboulaiounemuoz.R
import com.squareup.picasso.Picasso
import android.view.ContextMenu
import android.view.Menu

class ProductoAdapter(private val context: Context, private val productos: List<Producto>) : BaseAdapter() {

    override fun getCount(): Int = productos.size

    override fun getItem(position: Int): Any = productos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.producto_adapter, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val producto = productos[position]
        viewHolder.bind(producto)

        // Registrar la vista para mostrar el menú contextual
        view.setOnCreateContextMenuListener(viewHolder)

        return view
    }

    inner class ViewHolder(view: View) : View.OnCreateContextMenuListener {
        private val nombreTextView = view.findViewById<TextView>(R.id.nombreTextView)
        private val descripcionTextView = view.findViewById<TextView>(R.id.descripcionTextView)
        private val precioTextView = view.findViewById<TextView>(R.id.precioTextView)
        private val imagenImageView = view.findViewById<ImageView>(R.id.imagenImageView)

        private var currentProducto: Producto? = null

        fun bind(producto: Producto) {
            currentProducto = producto
            nombreTextView.text = producto.nombre
            descripcionTextView.text = producto.descripcion
            precioTextView.text = "${producto.precio} €"

            val imageUrl = "${URL.BASE_URL}/img_productos/${producto.imagen}"
            Picasso.get().load(imageUrl).placeholder(R.drawable.logo).error(R.drawable.noimage).into(imagenImageView)
        }
        fun getCurrentProducto(): Producto? {
            return currentProducto
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {

        }
    }
}
