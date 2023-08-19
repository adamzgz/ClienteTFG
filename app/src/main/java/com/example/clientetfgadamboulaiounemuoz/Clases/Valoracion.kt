import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class Valoracion(
    val id_cliente: Int,
    val id_producto: Int,
    val puntuacion: Int,
    val comentario: String?,
    val fecha: String?
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL

        fun crearValoracion(valoracion: Valoracion, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/valoraciones"
            val json = Gson().toJson(valoracion)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            println("Enviando solicitud de creación de valoración a: $url")

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

        fun borrarValoracion(id: Int, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/valoraciones/$id"
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            println("Enviando solicitud de borrado de valoración a: $url")

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

        fun modificarValoracion(id: Int, nuevaValoracion: Valoracion, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/valoraciones/$id"
            val json = Gson().toJson(nuevaValoracion)

            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            println("Enviando solicitud de modificación de valoración a: $url")

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

        fun obtenerPuntuacionMediaPorProducto(idProducto: Int, callback: (Double?) -> Unit) {
            val url = "$BASE_URL/valoraciones/puntuacion-media/$idProducto"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener puntuación media por producto a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    val responseBody = response.body?.string()
                    val puntuacionMedia = responseBody?.toDoubleOrNull()
                    println("Respuesta recibida. Success = $success, Puntuación media = $puntuacionMedia")
                    callback(puntuacionMedia)
                }
            })
        }

        fun obtenerValoracionesPorProducto(idProducto: Int, callback: (List<Valoracion>?) -> Unit) {
            val url = "$BASE_URL/valoraciones/valoracionesProducto/$idProducto"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            println("Enviando solicitud para obtener valoraciones del producto a: $url")

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    if (success) {
                        val responseBody = response.body?.string()
                        val listType = object : TypeToken<List<Valoracion>>() {}.type
                        val valoraciones = Gson().fromJson<List<Valoracion>>(responseBody, listType)
                        callback(valoraciones)
                    } else {
                        println("Respuesta no exitosa al obtener valoraciones del producto")
                        callback(null)
                    }
                }
            })
        }
    }
}
