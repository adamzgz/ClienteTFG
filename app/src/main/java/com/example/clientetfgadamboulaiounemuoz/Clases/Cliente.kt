package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Cliente(
    val id_usuario: Int,
    val vip: Boolean
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL
        private const val ENDPOINT_CLIENTES = "$BASE_URL/clientes"
        private const val ENDPOINT_CREAR_CLIENTE = "$ENDPOINT_CLIENTES"
        private const val ENDPOINT_ELIMINAR_CLIENTE = "$ENDPOINT_CLIENTES/{id}"
        private const val ENDPOINT_ACTUALIZAR_CLIENTE = "$ENDPOINT_CLIENTES/{id}"

        fun crearCliente(cliente: Cliente, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_CREAR_CLIENTE
            val json = Gson().toJson(cliente)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de cliente a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    println("Respuesta recibida. Success = $success")
                    callback(success)
                }
            })
        }

        fun obtenerClientes(callback: (List<Cliente>?) -> Unit) {
            val url = ENDPOINT_CLIENTES
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener clientes a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val clientes = Gson().fromJson(responseBody, Array<Cliente>::class.java)?.toList()
                    println("Respuesta recibida. Success = $success, Clientes = $clientes")
                    callback(clientes)
                }
            })
        }

        fun obtenerCliente(id: Int, callback: (Cliente?) -> Unit) {
            val url = "$ENDPOINT_CLIENTES/$id"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener cliente a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val cliente = Gson().fromJson(responseBody, Cliente::class.java)
                    println("Respuesta recibida. Success = $success, Cliente = $cliente")
                    callback(cliente)
                }
            })
        }

        fun actualizarCliente(id: Int, nuevoIdUsuario: Int, nuevoVip: Boolean, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ACTUALIZAR_CLIENTE.replace("{id}", id.toString())
            val requestBody = JSONObject().apply {
                put("id_usuario", nuevoIdUsuario)
                put("vip", nuevoVip)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de cliente a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    println("Respuesta recibida. Success = $success")
                    callback(success)
                }
            })
        }

        fun eliminarCliente(id: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ELIMINAR_CLIENTE.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            println("Enviando solicitud de borrado de cliente a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    println("Respuesta recibida. Success = $success")
                    callback(success)
                }
            })
        }
    }
}
