import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Usuario(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val contraseña: String,
    val imagen: String? = null
) {
    companion object {
        private const val BASE_URL = URL.BASE_URL

        fun registrar(usuario: Usuario, callback: (Boolean) -> Unit) {
            val url = "${URL.BASE_URL}/auth/register"
            val json = Gson().toJson(usuario)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            println("Enviando solicitud de registro a: $url")
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

        fun login(usuario: Usuario, callback: (Boolean, String, String) -> Unit) {
            val url = "${URL.BASE_URL}/auth/login"
            val json = Gson().toJson(usuario)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            println("Enviando solicitud de inicio de sesión a: $url")
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error al enviar solicitud: ${e.message}")
                    callback(false, "", "")
                }
                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
                    println("Respuesta recibida. Success = $success")
                    if (success) {
                        val responseBody = response.body?.string() ?: ""
                        val responseObject = JSONObject(responseBody)
                        val token = responseObject.getString("token")
                        val role = responseObject.getString("role")
                        callback(success, token, role)
                    } else {
                        callback(false, "", "")
                    }
                }
            })
        }

        fun crearUsuario(usuarioDto: UsuarioDto): Boolean {
            val url = "$BASE_URL/usuarios/crear"
            val json = Gson().toJson(usuarioDto)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            println("Enviando solicitud de creación de usuario a: $url")
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            return response.isSuccessful
        }

        fun eliminarUsuario(id: Int): Boolean {
            val url = "$BASE_URL/usuarios/eliminar/$id"
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()
            println("Enviando solicitud de eliminación de usuario a: $url")
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            return response.isSuccessful
        }

        fun actualizarUsuario(id: Int, usuarioDto: UsuarioDto): Boolean {
            val url = "$BASE_URL/usuarios/actualizar/$id"
            val json = Gson().toJson(usuarioDto)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()
            println("Enviando solicitud de actualización de usuario a: $url")
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            return response.isSuccessful
        }
    }

    data class UsuarioDto(
        val nombre: String,
        val direccion: String,
        val telefono: String,
        val email: String,
        val contraseña: String,
        val imagen: String? = null
    )
}
