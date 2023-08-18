package com.example.clientetfgadamboulaiounemuoz.Adapters

import Valoracion
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RatingBar
import android.widget.TextView
import com.example.clientetfgadamboulaiounemuoz.R

class ValoracionAdapter(private val context: Context, private val valoraciones: List<ValoracionDto>) : BaseAdapter() {

    override fun getCount(): Int = valoraciones.size

    override fun getItem(position: Int): Any = valoraciones[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.valoracion_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val valoracion = valoraciones[position]

        viewHolder.bind(valoracion, context) // Pasamos el context como parámetro

        return view
    }

    private class ViewHolder(view: View) {
        private val ratingBarValoracion = view.findViewById<RatingBar>(R.id.ratingBarValoracion)
        private val tvComentario = view.findViewById<TextView>(R.id.tvComentario)
        private val tvFechaValoracion = view.findViewById<TextView>(R.id.tvFechaValoracion)

        fun bind(valoracion: Valoracion, context: Context) { // Añadimos context como parámetro
            ratingBarValoracion.rating = valoracion.puntuacion.toFloat()
            tvComentario.text = valoracion.comentario ?: context.getString(R.string.sin_comentario)
            tvFechaValoracion.text = "Fecha de valoración: ${valoracion.fecha_valoracion}"
        }
    }
}
