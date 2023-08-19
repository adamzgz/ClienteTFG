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

        fun crearProducto(producto: Producto, token: String, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/productos" // URL para crear producto
            val json = Gson().toJson(producto)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Agrega el token en el encabezado
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de producto a: $url")

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

        fun borrarProducto(id: Int, token: String, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/productos/$id" // URL para borrar producto
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Agrega el token en el encabezado
                .delete()
                .build()

            println("Enviando solicitud de borrado de producto a: $url")

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

        fun modificarProducto(id: Int, nuevoProducto: Producto, token: String, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/productos/$id" // URL para modificar producto
            val json = Gson().toJson(nuevoProducto)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Agrega el token en el encabezado
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de producto a: $url")

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

        fun obtenerProductos(token: String, callback: (List<Producto>?) -> Unit) {
            val url = "$BASE_URL/productos" // URL para obtener productos
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Agrega el token en el encabezado
                .get()
                .build()

            println("Enviando solicitud para obtener productos a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val productos = Gson().fromJson(responseBody, Array<Producto>::class.java)?.toList()
                    println("Respuesta recibida. Success = $success, Productos = $productos")
                    callback(productos)
                }
            })
        }

        fun obtenerProductoPorId(id: Int, token: String, callback: (Producto?) -> Unit) {
            val url = "$BASE_URL/productos/$id" // URL para obtener un producto específico por ID
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Agrega el token en el encabezado
                .get()
                .build()

            println("Enviando solicitud para obtener el producto con ID $id a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val producto = if (success) {
                        Gson().fromJson(responseBody, Producto::class.java)
                    } else {
                        null
                    }
                    println("Respuesta recibida. Success = $success, Producto = $producto")
                    callback(producto)
                }
            })
        }
    }
}
