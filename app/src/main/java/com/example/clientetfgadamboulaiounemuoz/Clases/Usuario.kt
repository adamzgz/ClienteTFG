import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

data class Usuario(
    val id : Int? = null,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    var email: String,
    var contraseña: String,
    val imagen: String? = null
)
{
    constructor() : this(null, "", "", "", "", "")
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

        fun login(usuario: Usuario, callback: (Boolean, String, String) -> Unit) {
            val url = "${URL.BASE_URL}/auth/login"
            val json = Gson().toJson(usuario)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false, "", "")
                }
                override fun onResponse(call: Call, response: Response) {
                    val success = response.isSuccessful
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

        fun crearUsuario(token: String, usuarioDto: UsuarioDto, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/crear"
            val json = Gson().toJson(usuarioDto)
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

        fun eliminarUsuario(token: String, id: Int, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/eliminar/$id"
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

        fun actualizarUsuario(token: String, id: Int, usuarioDto: UsuarioDto, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/actualizar"
            val json = Gson().toJson(usuarioDto)
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
        fun obtenerUsuario(token: String, callback: (Boolean, Usuario?) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/obtener"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        val usuario = Gson().fromJson(responseBody, Usuario::class.java)
                        callback(true, usuario)
                    } else {
                        callback(false, null)
                    }
                }
            })
        }
        fun obtenerTodosLosUsuarios(token: String, callback: (Boolean, List<Usuario>?) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/todos"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .get()
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(false, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        val usuarios = Gson().fromJson(responseBody, Array<Usuario>::class.java).toList()
                        callback(true, usuarios)
                    } else {
                        callback(false, null)
                    }
                }
            })
        }
        // Función para deshabilitar una cuenta específica (solo para administradores)
        fun deshabilitarCuentaAdmin(token: String, id: Int, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/deshabilitar/admin/$id"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .post("{}".toRequestBody("application/json".toMediaType()))  // Podrías no necesitar un cuerpo en este POST
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

        // Función para que el usuario pueda deshabilitar su propia cuenta
        fun deshabilitarMiCuenta(token: String, callback: (Boolean) -> Unit) {
            val url = "$BASE_URL/secure/usuarios/deshabilitar"
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .post("{}".toRequestBody("application/json".toMediaType()))  // Podrías no necesitar un cuerpo en este POST
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
