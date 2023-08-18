package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Mensaje(
    val id_chat: Int,
    val id_usuario: Int,
    val mensaje: String
) {
    /*fun enviarMensaje(mensaje: Mensaje, callback: (Boolean) -> Unit) {
        val url = URL.ENVIAR_MENSAJE
        val json = Gson().toJson(mensaje)

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        println("Enviando solicitud de envío de mensaje a: $url")

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

    fun borrarMensaje(id: Int, callback: (Boolean) -> Unit) {
        val url = URL.BASE + "mensajes/$id"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        println("Enviando solicitud de borrado de mensaje a: $url")

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

    fun modificarMensaje(id: Int, nuevoMensaje: String, callback: (Boolean) -> Unit) {
        val url = URL.BASE + "mensajes/$id"
        val requestBody = JSONObject().apply {
            put("mensaje", nuevoMensaje)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        println("Enviando solicitud de modificación de mensaje a: $url")

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
    }*/
}
