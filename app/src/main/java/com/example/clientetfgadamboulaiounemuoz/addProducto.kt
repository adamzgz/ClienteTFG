package com.example.clientetfgadamboulaiounemuoz

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class addProducto : AppCompatActivity() {
    private val SELECT_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)

        val spinnerCategorias = findViewById<Spinner>(R.id.spinnerCategoria)

        // Obtener categorías y configurar el adaptador del Spinner
        Categoria.obtenerCategorias { categorias ->
            categorias?.let {
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, it.map { categoria -> categoria.nombre })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategorias.adapter = adapter
            }
        }

        findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST)
        }

        findViewById<Button>(R.id.btnEnviarProducto).setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.imageViewProducto)
            imageView.drawable?.let {
                enviarImagenAapi((imageView.drawable as BitmapDrawable).bitmap)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            findViewById<ImageView>(R.id.imageViewProducto).setImageURI(imageUri)
        }
    }

    private fun enviarImagenAapi(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArrayOutputStream.toByteArray())

        // Asume que "imagen_producto.jpg" es el nombre de tu archivo en el servidor. Esto puede ser cambiado según tus necesidades.
        val filePart = MultipartBody.Part.createFormData("uploaded_file", "imagen_producto.jpg", requestBody)

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(filePart)
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:80/img_productos")
            .post(multipartBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Maneja el error
                println("Error al enviar la imagen: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Maneja la respuesta del servidor
                println("Respuesta recibida. Código = ${response.code}")
            }
        })
    }
}
