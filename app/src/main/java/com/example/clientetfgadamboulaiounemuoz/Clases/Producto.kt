package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal

data class Producto(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val idCategoria: Int,
    val imagen: String
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL // URL base hardcoded

        fun crearProducto(token: String, producto: Producto, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/productos"
            val json = Gson().toJson(producto)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback(response.isSuccessful)
                }
            })
        }

        fun borrarProducto(token: String, id: Int, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/productos/$id"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .delete()
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback(response.isSuccessful)
                }
            })
        }

        fun modificarProducto(token: String, id: Int, nuevoProducto: Producto, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/productos/$id"
            val json = Gson().toJson(nuevoProducto)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .put(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    callback(response.isSuccessful)
                }
            })
        }

        fun obtenerProductos(token: String, callback: (List<Producto>?) -> Unit) {
            val url = "$BASE_URL/productos"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val productos = if (response.isSuccessful) {
                        Gson().fromJson(responseBody, Array<Producto>::class.java)?.toList()
                    } else {
                        null
                    }
                    callback(productos)
                }
            })
        }

        fun obtenerProductoPorId(token: String, id: Int, callback: (Producto?) -> Unit) {
            val url = "$BASE_URL/productos/$id"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    val producto = if (response.isSuccessful) {
                        Gson().fromJson(responseBody, Producto::class.java)
                    } else {
                        null
                    }
                    callback(producto)
                }
            })
        }
    }
}
