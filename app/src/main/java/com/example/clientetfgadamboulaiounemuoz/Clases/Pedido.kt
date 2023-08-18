import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Pedido(
    val id_cliente: Int,
    val estado: String
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL // URL base hardcoded

        fun crearPedido(pedido: Pedido, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/pedidos" // URL para crear pedido
            val json = Gson().toJson(pedido)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de pedido a: $url")

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

        fun borrarPedido(id: Int, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/pedidos/eliminar/$id" // URL para borrar pedido
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

        fun modificarPedido(id: Int, nuevoEstado: String, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/pedidos/actualizar/$id" // URL para modificar pedido
            val requestBody = JSONObject().apply {
                put("estado", nuevoEstado)
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
            val url = "$BASE_URL/pedidos" // URL para obtener pedidos
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
}
