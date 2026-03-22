package com.example.lightnovel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TheLoaiAddEditActivity : AppCompatActivity() {
    private lateinit var db: databaseHelper
    private var genreId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_the_loai_add_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = databaseHelper(this)

        val etGenreName = findViewById<EditText>(R.id.etGenreName)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val bundle = intent.extras
        if (bundle != null) {
            genreId = bundle.getInt("id", 0)
            if (genreId != 0) {
                etGenreName.setText(bundle.getString("name"))
            }
        }

        btnSave.setOnClickListener {
            val name = etGenreName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thể loại", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = if (genreId == 0) {
                db.insertGenre(name)
            } else {
                db.updateGenre(genreId, name).toLong()
            }

            if (result != -1L) {
                Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi lưu dữ liệu (Tên có thể đã tồn tại)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
