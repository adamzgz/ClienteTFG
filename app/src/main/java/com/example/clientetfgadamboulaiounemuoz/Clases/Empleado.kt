package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Empleado(
    val id_usuario: Int,
    val rol: String
) {
    companion object {
        private const val BASE_URL = "http://tu-servidor.com/api"
        private const val ENDPOINT_EMPLEADOS = "$BASE_URL/empleados"
        private const val ENDPOINT_CREAR_EMPLEADO = "$ENDPOINT_EMPLEADOS/crear"
        private const val ENDPOINT_BORRAR_EMPLEADO = "$ENDPOINT_EMPLEADOS/borrar/{id}"
        private const val ENDPOINT_ACTUALIZAR_EMPLEADO = "$ENDPOINT_EMPLEADOS/actualizar/{id}"
        private const val ENDPOINT_OBTENER_EMPLEADO = "$ENDPOINT_EMPLEADOS/{id}"
        private const val ENDPOINT_OBTENER_EMPLEADOS = ENDPOINT_EMPLEADOS

        fun crearEmpleado(empleado: Empleado, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_CREAR_EMPLEADO
            val json = Gson().toJson(empleado)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de empleado a: $url")

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

        fun borrarEmpleado(id: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_BORRAR_EMPLEADO.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            println("Enviando solicitud de borrado de empleado a: $url")

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

        fun modificarEmpleado(id: Int, nuevoRol: String, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ACTUALIZAR_EMPLEADO.replace("{id}", id.toString())
            val requestBody = JSONObject().apply {
                put("rol", nuevoRol)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de empleado a: $url")

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

        fun obtenerEmpleado(id: Int): Empleado? {
            val url = ENDPOINT_OBTENER_EMPLEADO.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener empleado a: $url")

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            return if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Gson().fromJson(responseBody, Empleado::class.java)
            } else {
                null
            }
        }

        fun obtenerEmpleados(): List<Empleado> {
            val url = ENDPOINT_OBTENER_EMPLEADOS
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener empleados a: $url")

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            return if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val listType = object : TypeToken<List<Empleado>>() {}.type
                Gson().fromJson(responseBody, listType)
            } else {
                emptyList()
            }
        }
    }
}
