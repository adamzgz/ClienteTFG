package com.example.clientetfgadamboulaiounemuoz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class addProducto : AppCompatActivity() {
    private val SELECT_IMAGE_REQUEST = 1
    private var selectedImageFileName: String? = null

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)

        val spinnerCategorias = findViewById<Spinner>(R.id.spinnerCategoria)
        val token = getTokenFromSharedPreferences()
        print("el token es $token")
        token?.let {
            Categoria.obtenerCategorias(it) { categorias ->
                runOnUiThread {
                    categorias?.let {
                        println(categorias.size)
                        val adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            it.map { categoria -> categoria.nombre }
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerCategorias.adapter = adapter
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        findViewById<Button>(R.id.btnEnviarProducto).setOnClickListener {
            if (validarFormulario()) {
                val imageView = findViewById<ImageView>(R.id.imageViewProducto)
                crearProducto()
                imageView.drawable?.let {
                    enviarImagenAapi((imageView.drawable as BitmapDrawable).bitmap, selectedImageFileName ?: "default.jpg")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            findViewById<ImageView>(R.id.imageViewProducto).setImageURI(imageUri)
            selectedImageFileName = getFileName(imageUri)
        }
    }

    private fun getFileName(uri: Uri?): String? {
        var result: String? = null
        if (uri?.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri?.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

    private fun enviarImagenAapi(bitmap: Bitmap, fileName: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArrayOutputStream.toByteArray())
        val filePart = MultipartBody.Part.createFormData("uploaded_file", fileName, requestBody)

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(filePart)
            .build()

        val request = Request.Builder()
            .url(URL.Companion.BASE_URL + "/secure/add_img_productos")
            .header("Authorization", "Bearer ${getTokenFromSharedPreferences()}")
            .post(multipartBody)
            .build()

        val btnEnviarProducto = findViewById<Button>(R.id.btnEnviarProducto)
        btnEnviarProducto.isEnabled = false // Deshabilita el botón mientras espera respuesta

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    println("Error al enviar la imagen: ${e.message}")
                    btnEnviarProducto.isEnabled = true // Habilita el botón si hubo un error
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    println("Respuesta recibida. Código = ${response.code}")
                    if (response.isSuccessful) {
                        finish() // Cierra la actividad si todo fue exitoso
                    } else {
                        btnEnviarProducto.isEnabled = true // Habilita el botón si la respuesta no fue exitosa
                    }
                }
            }
        })
    }

    private fun crearProducto() {
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val descripcion = findViewById<EditText>(R.id.editTextDescripcion).text.toString()
        val precio = findViewById<EditText>(R.id.editTextPrecio).text.toString().toDouble()
        val categoria = findViewById<Spinner>(R.id.spinnerCategoria).selectedItem.toString()
        val imagen = selectedImageFileName ?: "imagen_producto.jpg"
        val idCategoria = 1

        val producto = Producto(nombre, descripcion, precio, idCategoria, imagen)

        val btnEnviarProducto = findViewById<Button>(R.id.btnEnviarProducto)
        btnEnviarProducto.isEnabled = false // Deshabilita el botón mientras espera respuesta

        getTokenFromSharedPreferences()?.let { token ->
            Producto.crearProducto(token, producto) { isSuccess ->
                runOnUiThread {
                    if (isSuccess) {
                        println("Producto creado con éxito!")
                        finish() // Cierra la actividad si el producto se creó con éxito
                    } else {
                        println("Error al crear el producto.")
                        btnEnviarProducto.isEnabled = true // Habilita el botón si hubo un error
                    }
                }
            }
        }
    }

    private fun validarFormulario(): Boolean {
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val descripcion = findViewById<EditText>(R.id.editTextDescripcion).text.toString()
        val precioStr = findViewById<EditText>(R.id.editTextPrecio).text.toString()

        if (nombre.isEmpty()) {
            showToastMessage("El nombre no puede estar vacío.")
            return false
        }
        if (descripcion.isEmpty()) {
            showToastMessage("La descripción no puede estar vacía.")
            return false
        }
        if (precioStr.isEmpty()) {
            showToastMessage("El precio no puede estar vacío.")
            return false
        }

        return true
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
