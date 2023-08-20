package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Categoria(
    val id: Int,
    val nombre: String
) : java.io.Serializable
{
    companion object {
        private const val BASE_URL = URL.BASE_URL
        private const val ENDPOINT_CATEGORIAS = "$BASE_URL/secure/categorias"

        fun crearCategoria(token: String, categoria: Categoria, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_CATEGORIAS

            val categoriaParaEnviar = JSONObject()
                .put("nombre", categoria.nombre)
                .toString()

            val requestBody = categoriaParaEnviar.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", "Bearer $token")
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

        fun obtenerCategorias(token: String, callback: (List<Categoria>?) -> Unit) {
            val url = ENDPOINT_CATEGORIAS
            val request = Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer $token")
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

        fun obtenerCategoria(token: String, id: Int, callback: (Categoria?) -> Unit) {
            val url = "$ENDPOINT_CATEGORIAS/$id"
            val request = Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer $token")
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

        fun actualizarCategoria(token: String, id: Int, nuevoNombre: String, callback: (Boolean) -> Unit) {
            val url = "$ENDPOINT_CATEGORIAS/$id"
            val requestBody = JSONObject().apply {
                put("nombre", nuevoNombre)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .header("Authorization", "Bearer $token")
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

        fun eliminarCategoria(token: String, id: Int, callback: (Boolean) -> Unit) {
            val url = "$ENDPOINT_CATEGORIAS/$id"
            val request = Request.Builder()
                .url(url)
                .delete()
                .header("Authorization", "Bearer $token")
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
