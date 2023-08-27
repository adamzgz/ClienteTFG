import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.io.Serializable
import java.time.LocalDate

data class Pedido(
    val idPedido: Int?,
    val id_cliente: Int,
    val estado: String,
    val fechaPedido: String?
) : Serializable {
    companion object {
        private const val BASE_URL = URL.BASE_URL // URL base hardcoded

        fun comprobarPedidoEnProceso(token: String, callback: (Pedido?) -> Unit, fallback: (() -> Unit)? = null) {
            val url = "$BASE_URL/secure/pedidos/enProceso"

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
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
                        println("No se encontró un pedido en proceso para el cliente.")
                        crearPedidoEnProceso(token) { nuevoPedido ->
                            if(nuevoPedido != null) {
                                println("Se creó un nuevo pedido en proceso: $nuevoPedido")
                                callback(nuevoPedido)
                            } else {
                                println("Error al crear el nuevo pedido.")
                                fallback?.invoke()  // Invoca la función de fallback si existe
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

        fun modificarPedido(token: String, id: Int, nuevoEstado: EstadoPedido, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/pedidos/$id" // URL para modificar pedido
            val requestBody = JSONObject().apply {
                put("estado", nuevoEstado.name)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Pasando el token en el header para autenticación
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


         fun obtenerPedidos(token: String, callback: (List<Pedido>?) -> Unit) {
            val url = "$BASE_URL/secure/pedidos"
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
                    val pedidos = Gson().fromJson(responseBody, Array<Pedido>::class.java)?.toList()
                    callback(pedidos)
                }
            })
        }
        // Añadir esta función a tu clase Pedido
        fun obtenerPedidoPorId(token: String, idPedido: Int, callback: (Pedido?) -> Unit) {
            val url = "$BASE_URL/secure/pedidos/$idPedido" // URL para obtener un pedido específico

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Pasando el token en el header para autenticación
                .get()
                .build()

            println("Enviando solicitud para obtener el pedido con ID $idPedido a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val pedido = Gson().fromJson(responseBody, Pedido::class.java)
                        println("Pedido obtenido exitosamente: $pedido")
                        callback(pedido)
                    } else {
                        println("No se pudo obtener el pedido con ID $idPedido.")
                        callback(null)
                    }
                }
            })
        }
        fun obtenerPedidosPorCliente(token: String, callback: (List<Pedido>?) -> Unit) {
            val url = "$BASE_URL/secure/pedidos/pedidosCliente" // Aquí se cambia la URL para usar el nuevo endpoint

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token") // Pasando el token en el header para autenticación
                .get()
                .build()

            println("Enviando solicitud para obtener los pedidos del cliente a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        println("Respuesta recibida: $responseBody")
                        val pedidos = Gson().fromJson(responseBody, Array<Pedido>::class.java)?.toList()
                        println("Pedidos del cliente obtenidos exitosamente: $pedidos")
                        callback(pedidos)
                    } else {
                        println("No se pudo obtener los pedidos del cliente.")
                        callback(null)
                    }
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
