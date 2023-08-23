import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate

data class Pedido(
    val idPedido: Int?,
    val id_cliente: Int,
    val estado: String,
    val fecha: String
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL // URL base hardcoded

        fun comprobarPedidoEnProceso(token: String, callback: (Pedido?) -> Unit) {
            val url = "$BASE_URL/secure/pedidos/enProceso"

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Pasando el token en el header para autenticación
                .get()
                .build()

            println("Enviando solicitud para comprobar pedido en proceso a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val pedido = Gson().fromJson(responseBody, Pedido::class.java)
                        println("Pedido en proceso encontrado: $pedido")
                        callback(pedido)
                    } else {
                        // Aquí se maneja el caso cuando no hay un pedido en proceso y necesitas crear uno
                        println("No se encontró un pedido en proceso para el cliente.")
                        crearPedidoEnProceso(token) { nuevoPedido ->
                            if(nuevoPedido != null) {
                                println("Se creó un nuevo pedido en proceso: $nuevoPedido")
                                callback(nuevoPedido)
                            } else {
                                println("Error al crear el nuevo pedido.")
                                callback(null)
                            }
                        }
                    }
                }
            })
        }

        fun crearPedidoEnProceso(token: String, callback: (Pedido?) -> Unit) {
            val url = "$BASE_URL/secure/pedidos"

            val requestBody = JSONObject().apply {
                put("estado", Pedido.EstadoPedido.EN_PROCESO.name)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Pasando el token en el header para autenticación
                .post(requestBody)
                .build()

            println("Enviando solicitud para crear pedido en proceso a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val nuevoPedido = Gson().fromJson(responseBody, Pedido::class.java)
                        callback(nuevoPedido)
                    } else {
                        println("Error al crear el nuevo pedido.")
                        callback(null)
                    }
                }
            })
        }


        fun borrarPedido(id: Int, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/pedidos/eliminar/$id" // URL para borrar pedido
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            println("Enviando solicitud de borrado de pedido a: $url")

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

        fun modificarPedido(id: Int, nuevoEstado: EstadoPedido, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/pedidos/actualizar/$id" // URL para modificar pedido
            val requestBody = JSONObject().apply {
                put("estado", nuevoEstado.name)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de pedido a: $url")

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

        fun obtenerPedidos(callback: (List<Pedido>?) -> Unit) {
            val url = "$BASE_URL/secure/pedidos" // URL para obtener pedidos
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener pedidos a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val pedidos = Gson().fromJson(responseBody, Array<Pedido>::class.java)?.toList()
                    println("Respuesta recibida. Success = $success, Pedidos = $pedidos")
                    callback(pedidos)
                }
            })
        }


    }
    enum class EstadoPedido {
        PENDIENTE,   // pedido realizado por el cliente
        ENVIADO,     // pedido enviado
        ENTREGADO,   // pedido entregado
        EN_PROCESO   // en el carrito
    }
}
