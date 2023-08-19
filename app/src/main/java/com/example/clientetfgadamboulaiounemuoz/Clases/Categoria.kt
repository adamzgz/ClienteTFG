package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Categoria(
    val id: Int,  // Añadimos el ID de la categoría
    val nombre: String
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL
        private const val ENDPOINT_CATEGORIAS = "$BASE_URL/secure/categorias"

        fun crearCategoria(categoria: Categoria, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_CATEGORIAS

            // Asumimos que al crear una categoría no necesitas enviar el ID, ya que
            // el backend debería generarlo automáticamente. Entonces creamos un objeto sin el ID para enviar.
            val categoriaParaEnviar = JSONObject()
                .put("nombre", categoria.nombre)
                .toString()

            val requestBody = categoriaParaEnviar.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de categoría a: $url")

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

        fun obtenerCategorias(callback: (List<Categoria>?) -> Unit) {
            val url = ENDPOINT_CATEGORIAS
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener categorías a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val categorias = Gson().fromJson(responseBody, Array<Categoria>::class.java)?.toList()
                    println("Respuesta recibida. Success = $success, Categorías = $categorias")
                    callback(categorias)
                }
            })
        }

        fun obtenerCategoria(id: Int, callback: (Categoria?) -> Unit) {
            val url = "$ENDPOINT_CATEGORIAS/$id"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener categoría a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val categoria = Gson().fromJson(responseBody, Categoria::class.java)
                    println("Respuesta recibida. Success = $success, Categoría = $categoria")
                    callback(categoria)
                }
            })
        }

        fun actualizarCategoria(id: Int, nuevoNombre: String, callback: (Boolean) -> Unit) {
            val url = "$ENDPOINT_CATEGORIAS/$id"
            val requestBody = JSONObject().apply {
                put("nombre", nuevoNombre)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de categoría a: $url")

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

        fun eliminarCategoria(id: Int, callback: (Boolean) -> Unit) {
            val url = "$ENDPOINT_CATEGORIAS/$id"
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            println("Enviando solicitud de borrado de categoría a: $url")

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
