package com.example.lightnovel

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class SigninActivity : AppCompatActivity() {
    private lateinit var db: AccountDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        db = AccountDatabaseHelper(this)

        val edtSurname = findViewById<TextInputEditText>(R.id.edtSurname)
        val edtFirstName = findViewById<TextInputEditText>(R.id.edtFirstName)
        val edtUser = findViewById<TextInputEditText>(R.id.edtUser)
        val edtEmail = findViewById<TextInputEditText>(R.id.edtEmail)
        val edtPhone = findViewById<TextInputEditText>(R.id.edtPhoneNum)
        val edtDoB = findViewById<TextInputEditText>(R.id.edtDoB)
        val edtGender = findViewById<AutoCompleteTextView>(R.id.edtGender)
        val edtPass = findViewById<TextInputEditText>(R.id.edtPassword)
        val edtConfirm = findViewById<TextInputEditText>(R.id.edtConfirmPass)
        val tilConfirm = findViewById<TextInputLayout>(R.id.tilConfirmPass)
        val btnSignUp = findViewById<Button>(R.id.button5)

        // Date of Birth Picker
        edtDoB.isFocusable = false
        edtDoB.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                edtDoB.setText(date)
            }, year, month, day)
            datePickerDialog.show()
        }

        // Gender Dropdown
        val genders = arrayOf("Nam", "Nữ", "Khác")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genders)
        edtGender.setAdapter(adapter)
        edtGender.setOnClickListener {
            edtGender.showDropDown()
        }

        // Real-time confirmation check
        edtConfirm.addTextChangedListener {
            val pass = edtPass.text.toString()
            val confirm = it.toString()
            if (confirm != pass) {
                tilConfirm.error = "Mật khẩu không khớp"
            } else {
                tilConfirm.error = null
            }
        }

        btnSignUp.setOnClickListener {
            val user = edtUser.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val pass = edtPass.text.toString().trim()
            val confirm = edtConfirm.text.toString().trim()
            val dob = edtDoB.text.toString().trim()
            val gender = edtGender.text.toString().trim()

            if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || dob.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ các trường bắt buộc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password Validation: > 8 digits, at least a letter & a number
            if (pass.length <= 8) {
                Toast.makeText(this, "Mật khẩu phải dài hơn 8 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!pass.any { it.isDigit() } || !pass.any { it.isLetter() }) {
                Toast.makeText(this, "Mật khẩu phải chứa ít nhất 1 chữ cái và 1 chữ số", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirm) {
                tilConfirm.error = "Mật khẩu không khớp"
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.checkUser(user, email)) {
                Toast.makeText(this, "Tên người dùng hoặc Email đã tồn tại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val account = Account(
                surname = edtSurname.text.toString(),
                firstName = edtFirstName.text.toString(),
                username = user,
                email = email,
                phone = edtPhone.text.toString(),
                dob = dob,
                gender = gender,
                password = pass
            )

            val success = db.insertAccount(account)
            if (success != -1L) {
                val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("username", user)
                    apply()
                }

                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        val tvLogin = findViewById<TextView>(R.id.tvLogIn)
        val btnBack = findViewById<ImageView>(R.id.ivBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
