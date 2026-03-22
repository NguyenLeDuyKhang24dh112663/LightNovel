package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtUser = findViewById<EditText>(R.id.edtUser)
        val edtPass = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.button5)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        val btnBack = findViewById<ImageView>(R.id.ivBack)

        val db = AccountDatabaseHelper(this)

        btnLogin.setOnClickListener {
            val user = edtUser.text.toString().trim()
            val pass = edtPass.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.login(user, pass)) {
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("username", user)
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvSignIn.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
}
