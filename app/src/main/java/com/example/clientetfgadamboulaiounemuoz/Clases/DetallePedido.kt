package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class DetallePedido(
    val id: Int? = null,
    val idPedido: Int,
    val idProducto: Int,
    var cantidad: Int
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL
        private const val ENDPOINT_DETALLES_PEDIDOS = "$BASE_URL/secure/detalles-pedidos"
        private const val ENDPOINT_INSERTAR_DETALLE_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS"
        private const val ENDPOINT_BORRAR_DETALLE_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS/{id}"
        private const val ENDPOINT_ACTUALIZAR_DETALLE_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS/{id}"
        private const val ENDPOINT_OBTENER_DETALLES_POR_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS/{idPedido}"

        fun insertarDetallePedido(token: String, detallePedido: DetallePedido, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_INSERTAR_DETALLE_PEDIDO
            val json = Gson().toJson(detallePedido)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .post(requestBody)
                .build()

            println("Enviando solicitud de inserción de detalle de pedido a: $url")

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

        fun borrarDetallePedido(token: String, id: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_BORRAR_DETALLE_PEDIDO.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .delete()
                .build()

            println("Enviando solicitud de borrado de detalle de pedido a: $url")

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

        fun modificarDetallePedido(token: String, id: Int, nuevaCantidad: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ACTUALIZAR_DETALLE_PEDIDO.replace("{id}", id.toString())
            val requestBody = JSONObject().apply {
                put("cantidad", nuevaCantidad)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de detalle de pedido a: $url")

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
        fun obtenerDetallesPorPedido(token: String, idPedido: Int, callback: (List<DetallePedido>?) -> Unit) {
            val url = ENDPOINT_OBTENER_DETALLES_POR_PEDIDO.replace("{idPedido}", idPedido.toString())
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            println("Enviando solicitud para obtener detalles de pedido a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        val detalles = Gson().fromJson(responseBody, Array<DetallePedido>::class.java).toList()
                        println("Detalles de pedido obtenidos con éxito.")
                        callback(detalles)
                    } else {
                        println("No se pudo obtener los detalles del pedido. Código de respuesta: ${response.code}")
                        callback(null)
                    }
                }
            })
        }
    }
}
