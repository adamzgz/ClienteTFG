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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.clientetfgadamboulaiounemuoz.API.URL
import com.example.clientetfgadamboulaiounemuoz.Clases.Categoria
import com.example.clientetfgadamboulaiounemuoz.Clases.Producto
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class addProducto : AppCompatActivity() {

    private val SELECT_IMAGE_REQUEST = 1
    private var selectedImageFileName: String? = null
    private var isEditMode = false
    private var currentProducto: Producto? = null

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("com.example.clientetfgadamboulaiounemuoz", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)

        currentProducto = intent.getSerializableExtra("productoSeleccionado") as Producto?
        val btnEnviarProducto = findViewById<Button>(R.id.btnEnviarProducto)

        if (currentProducto != null) {
            isEditMode = true
            populateProductoData(currentProducto!!)
            btnEnviarProducto.text = "Editar Producto"
        }

        val spinnerCategorias = findViewById<Spinner>(R.id.spinnerCategoria)
        val token = getTokenFromSharedPreferences()
        token?.let {
            Categoria.obtenerCategorias(it) { categorias ->
                runOnUiThread {
                    categorias?.let {
                        val adapter = ArrayAdapter<Categoria>(
                            this,
                            android.R.layout.simple_spinner_item,
                            categorias
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

        btnEnviarProducto.setOnClickListener {
            if (validarFormulario()) {
                val imageView = findViewById<ImageView>(R.id.imageViewProducto)
                if (selectedImageFileName != null) {
                    enviarImagenAapi((imageView.drawable as BitmapDrawable).bitmap, selectedImageFileName!!) { isSuccessful ->
                        if (isSuccessful) {
                            if (isEditMode) {
                                println("EditarProducto")
                                editarProducto() // Aquí usarás selectedImageFileName como producto.imagen
                            } else {
                                println("crearProducto")
                                crearProducto() // Aquí usarás selectedImageFileName como producto.imagen
                            }
                        } else {
                            println("Error al cargar la imagen.")
                        }
                    }
                } else if (isEditMode) {
                    editarProducto() // Puedes conservar la lógica aquí si estás en modo de edición pero la imagen no ha cambiado
                } else {
                    mostrarDialogoSinImagen()
                }
            }
        }
    }

    private fun populateProductoData(producto: Producto) {
        findViewById<EditText>(R.id.editTextNombre).setText(producto.nombre)
        findViewById<EditText>(R.id.editTextDescripcion).setText(producto.descripcion)
        findViewById<EditText>(R.id.editTextPrecio).setText(producto.precio.toString())

        val imageUrl = "${URL.BASE_URL}/img_productos/${producto.imagen}"
        Picasso.get().load(imageUrl).into(findViewById<ImageView>(R.id.imageViewProducto))

        selectedImageFileName = producto.imagen
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

    private fun enviarImagenAapi(bitmap: Bitmap, fileName: String, callback: (Boolean) -> Unit) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArrayOutputStream.toByteArray())
        val filePart = MultipartBody.Part.createFormData("uploaded_file", fileName, requestBody)

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(filePart)
            .build()

        val token = getTokenFromSharedPreferences()  // Asume que tienes una función que recupera el token desde SharedPreferences

        val request = Request.Builder()
            .url(URL.Companion.BASE_URL + "/secure/add_img_productos")
            .header("Authorization", "Bearer $token")  // Aquí se añade el token
            .post(multipartBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }


    private fun validarFormulario(): Boolean {
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val descripcion = findViewById<EditText>(R.id.editTextDescripcion).text.toString()
        val precio = findViewById<EditText>(R.id.editTextPrecio).text.toString()

        if (nombre.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun crearProducto() {
        val nombre = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val descripcion = findViewById<EditText>(R.id.editTextDescripcion).text.toString()
        val precio = findViewById<EditText>(R.id.editTextPrecio).text.toString().toDouble()
        val idCategoria = (findViewById<Spinner>(R.id.spinnerCategoria).selectedItem as Categoria).id

        val nuevoProducto = Producto(
            id = 0,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio,
            idCategoria = idCategoria,
            imagen = selectedImageFileName!! // Usar el nombre del archivo de la imagen como producto.imagen
        )

        val token = getTokenFromSharedPreferences()

        token?.let {
            Producto.crearProducto(it, nuevoProducto) { isSuccess ->
                runOnUiThread {
                    if (isSuccess) {  // Cambiado de nuevoProducto != null a isSuccess
                        Toast.makeText(this, "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun editarProducto() {
        val producto = Producto(
            id = currentProducto!!.id,
            nombre = findViewById<EditText>(R.id.editTextNombre).text.toString(),
            descripcion = findViewById<EditText>(R.id.editTextDescripcion).text.toString(),
            precio = findViewById<EditText>(R.id.editTextPrecio).text.toString().toDouble(),
            idCategoria = (findViewById<Spinner>(R.id.spinnerCategoria).selectedItem as Categoria).id,
            imagen = selectedImageFileName!! // Usar el nombre del archivo de la imagen como producto.imagen
        )

        getTokenFromSharedPreferences()?.let { token ->
            Producto.modificarProducto(token, producto.id, producto) { isSuccess ->
                runOnUiThread {
                    if (isSuccess) {
                        println("Producto editado con éxito!")

                        // Sólo ahora enviamos la imagen, si hay una nueva seleccionada.
                        val imageView = findViewById<ImageView>(R.id.imageViewProducto)
                        if (selectedImageFileName != null) {
                            enviarImagenAapi((imageView.drawable as BitmapDrawable).bitmap, selectedImageFileName!!) { isSuccessful ->
                                if (isSuccessful) {
                                    println("Imagen cargada con éxito!")
                                } else {
                                    println("Error al cargar la imagen.")
                                }
                            }
                        }

                        finish() // Podrías mover esto dentro del callback de enviarImagenAapi si sólo quieres finalizar cuando la imagen también se haya cargado con éxito.
                    } else {
                        println("Error al editar el producto.")
                    }
                }
            }
        }
    }


    private fun convertBitmapToFile(bitmap: Bitmap, fileName: String): File {
            // Aquí conviertes tu bitmap a un archivo. Puedes almacenarlo temporalmente.
            val file = File(externalCacheDir, fileName)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            return file
        }


        private fun mostrarDialogoSinImagen() {
            AlertDialog.Builder(this)
                .setTitle("Imagen faltante")
                .setMessage("Por favor, selecciona una imagen para el producto antes de continuar.")
                .setPositiveButton("Ok") { _, _ -> }
                .show()
        }
    }

