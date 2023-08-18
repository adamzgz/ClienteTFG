import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.clientetfgadamboulaiounemuoz.R

class ProductoAdapter(private val context: Context, private val productos: List<Producto>) : BaseAdapter() {

    override fun getCount(): Int {
        return productos.size
    }

    override fun getItem(position: Int): Any {
        return productos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

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

        return view
    }

    private class ViewHolder(view: View) {
        private val nombreTextView = view.findViewById<TextView>(R.id.nombreTextView)
        private val descripcionTextView = view.findViewById<TextView>(R.id.descripcionTextView)
        private val precioTextView = view.findViewById<TextView>(R.id.precioTextView)
        private val stockTextView = view.findViewById<TextView>(R.id.stockTextView)
        private val categoriaTextView = view.findViewById<TextView>(R.id.categoriaTextView)
        private val imagenImageView = view.findViewById<ImageView>(R.id.imagenImageView)

        fun bind(producto: Producto) {
            nombreTextView.text = producto.nombre
            descripcionTextView.text = producto.descripcion
            precioTextView.text = producto.precio.toString()
            stockTextView.text = producto.stock.toString()
            categoriaTextView.text = producto.idCategoria.toString()


            imagenImageView.setImageResource(R.drawable.logo)
        }
    }
}