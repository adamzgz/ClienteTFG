package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


data class Usuario(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val contraseña: String
) {
    fun registrar(usuario: Usuario, callback: (Boolean) -> Unit) {
        val url = URL.REGISTRO // Access the URL from the companion object URL
        val json = Gson().toJson(usuario)

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        println("Enviando solicitud de registro a: $url") // Agregar log

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error al enviar solicitud: ${e.message}") // Agregar log
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val success = response.isSuccessful
                println("Respuesta recibida. Success = $success") // Agregar log
                callback(success)
            }
        })
    }
    fun login(usuario: Usuario, callback: (Boolean, String) -> Unit) {
        val url = URL.LOGIN
        val json = Gson().toJson(usuario)

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        println("Enviando solicitud de inicio de sesión a: $url")

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error al enviar solicitud: ${e.message}")
                callback(false, "")
            }

            override fun onResponse(call: Call, response: Response) {
                val success = response.isSuccessful
                println("Respuesta recibida. Success = $success")

                val token = if(success) {
                    val responseBody = response.body?.string() ?: ""
                    val responseObject = JSONObject(responseBody)
                    responseObject.getString("token")
                } else {
                    ""
                }

                callback(success, token)
            }
        })
    }




}