package com.example.clientetfgadamboulaiounemuoz.Adapters

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.R

class CategoriaAdapter(private var categorias: List<Categoria>, private val onItemLongClicked: (Categoria) -> Unit) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    fun updateCategorias(newCategorias: List<Categoria>) {
        categorias = newCategorias
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categoria_adapter, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.bind(categoria)
        holder.itemView.setOnLongClickListener {
            onItemLongClicked(categoria)
            false
        }
    }

    override fun getItemCount() = categorias.size

    inner class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {

        private val tvCategoriaId: TextView = itemView.findViewById(R.id.tvCategoriaId)
        private val tvCategoriaName: TextView = itemView.findViewById(R.id.tvCategoriaName)

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        fun bind(categoria: Categoria) {
            tvCategoriaId.text = categoria.id.toString()
            tvCategoriaName.text = categoria.nombre
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, R.id.edit_categoria, 0, "Editar categoría")
            menu?.add(Menu.NONE, R.id.delete_categoria, 1, "Eliminar categoría")
        }
    }
}
