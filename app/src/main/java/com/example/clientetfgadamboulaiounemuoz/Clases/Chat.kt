package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Chat(
    val id_pedido: Int,
    val estado: String
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL
        private const val ENDPOINT_CHATS = "$BASE_URL/chats"
        private const val ENDPOINT_CREAR_CHAT = "$ENDPOINT_CHATS/crear"
        private const val ENDPOINT_ELIMINAR_CHAT = "$ENDPOINT_CHATS/eliminar/{id}"
        private const val ENDPOINT_ACTUALIZAR_CHAT = "$ENDPOINT_CHATS/actualizar/{id}"

        fun crearChat(chat: Chat, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_CREAR_CHAT
            val json = Gson().toJson(chat)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de chat a: $url")

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

        fun obtenerChats(callback: (List<Chat>?) -> Unit) {
            val url = ENDPOINT_CHATS
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener chats a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val chats = Gson().fromJson(responseBody, Array<Chat>::class.java)?.toList()
                    println("Respuesta recibida. Success = $success, Chats = $chats")
                    callback(chats)
                }
            })
        }

        fun obtenerChat(id: Int, callback: (Chat?) -> Unit) {
            val url = ENDPOINT_CHATS.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener chat a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val chat = Gson().fromJson(responseBody, Chat::class.java)
                    println("Respuesta recibida. Success = $success, Chat = $chat")
                    callback(chat)
                }
            })
        }

        fun actualizarEstadoChat(id: Int, nuevoEstado: String, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ACTUALIZAR_CHAT.replace("{id}", id.toString())
            val requestBody = JSONObject().apply {
                put("estado", nuevoEstado)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de chat a: $url")

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

        fun borrarChat(id: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ELIMINAR_CHAT.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            println("Enviando solicitud de borrado de chat a: $url")

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
