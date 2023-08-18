package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class EmpleadoChat(
    val id_empleado: Int,
    val id_chat: Int
) {
    /*fun insertarEmpleadoChat(empleadoChat: EmpleadoChat, callback: (Boolean) -> Unit) {
        val url = URL.INSERTAR_EMPLEADO_CHAT
        val json = Gson().toJson(empleadoChat)

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        println("Enviando solicitud de inserción de empleado chat a: $url")

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

    fun borrarEmpleadoChat(id: Int, callback: (Boolean) -> Unit) {
        val url = URL.BASE + "empleados-chats/$id"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        println("Enviando solicitud de borrado de empleado chat a: $url")

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

    fun modificarEmpleadoChat(id: Int, nuevoIdEmpleado: Int, callback: (Boolean) -> Unit) {
        val url = URL.BASE + "empleados-chats/$id"
        val requestBody = JSONObject().apply {
            put("id_empleado", nuevoIdEmpleado)
        }.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        println("Enviando solicitud de modificación de empleado chat a: $url")

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
