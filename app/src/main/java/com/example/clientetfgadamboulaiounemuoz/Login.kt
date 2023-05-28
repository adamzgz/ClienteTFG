package com.example.clientetfgadamboulaiounemuoz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registerButton: Button = findViewById(R.id.buttonRegister)
        registerButton.setOnClickListener(CommonOnClickListener(this, Registro::class.java))
    }

    class CommonOnClickListener(private val context: AppCompatActivity, private val targetActivity: Class<*>) :
        View.OnClickListener {

        override fun onClick(view: View) {
            val intent = Intent(context, targetActivity)
            context.startActivity(intent)
        }
    }
}
