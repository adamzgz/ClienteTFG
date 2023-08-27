package com.example.clientetfgadamboulaiounemuoz.Adapters

import Usuario
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientetfgadamboulaiounemuoz.R


class UsuarioAdapter(private var usuarios: List<Usuario>) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.userIdTextView)
        val nombre: TextView = itemView.findViewById(R.id.userNameTextView)
        val direccion: TextView = itemView.findViewById(R.id.userAddressTextView)
        val telefono: TextView = itemView.findViewById(R.id.userPhoneTextView)
        val email: TextView = itemView.findViewById(R.id.userEmailTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.usuario_adapter, parent, false)
        return UsuarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.id.text = usuario.id.toString()
        holder.nombre.text = usuario.nombre
        holder.direccion.text = usuario.direccion
        holder.telefono.text = usuario.telefono
        holder.email.text = usuario.email
    }

    override fun getItemCount() = usuarios.size

    fun updateUsuarios(newUsuarios: List<Usuario>) {
        this.usuarios = newUsuarios
        notifyDataSetChanged()
    }
}
