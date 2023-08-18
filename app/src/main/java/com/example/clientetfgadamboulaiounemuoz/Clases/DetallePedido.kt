package com.example.clientetfgadamboulaiounemuoz.Clases

import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class DetallePedido(
    val id_pedido: Int,
    val id_producto: Int,
    val cantidad: Int
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL
        private const val ENDPOINT_DETALLES_PEDIDOS = "$BASE_URL/detalles-pedidos"
        private const val ENDPOINT_INSERTAR_DETALLE_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS/insertar"
        private const val ENDPOINT_BORRAR_DETALLE_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS/borrar/{id}"
        private const val ENDPOINT_ACTUALIZAR_DETALLE_PEDIDO = "$ENDPOINT_DETALLES_PEDIDOS/actualizar/{id}"

        fun insertarDetallePedido(detallePedido: DetallePedido, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_INSERTAR_DETALLE_PEDIDO
            val json = Gson().toJson(detallePedido)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
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

        fun borrarDetallePedido(id: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_BORRAR_DETALLE_PEDIDO.replace("{id}", id.toString())
            val request = Request.Builder()
                .url(url)
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

        fun modificarDetallePedido(id: Int, nuevaCantidad: Int, callback: (Boolean) -> Unit) {
            val url = ENDPOINT_ACTUALIZAR_DETALLE_PEDIDO.replace("{id}", id.toString())
            val requestBody = JSONObject().apply {
                put("cantidad", nuevaCantidad)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
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
    }
}
