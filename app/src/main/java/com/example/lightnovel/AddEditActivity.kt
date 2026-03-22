package com.example.lightnovel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddEditActivity : AppCompatActivity() {
    private lateinit var db: databaseHelper
    private var novelId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = databaseHelper(this)

        val etTtl = findViewById<EditText>(R.id.etTitle)
        val etAu = findViewById<EditText>(R.id.etAuthor)
        val etImg = findViewById<EditText>(R.id.etImgLink)
        val etDesc = findViewById<EditText>(R.id.etDesc)

        val btnSave = findViewById<Button>(R.id.btnSave)

        val bundle = intent.extras
        if (bundle != null) {
            novelId = bundle.getInt("id", 0)
            if (novelId != 0) {
                etTtl.setText(bundle.getString("Tên truyện"))
                etAu.setText(bundle.getString("Tác giả"))
                etImg.setText(bundle.getInt("Ảnh").toString())
                etDesc.setText(bundle.getString("Mô tả"))
            }
        }

        btnSave.setOnClickListener {
            val title = etTtl.text.toString().trim()
            val author = etAu.text.toString().trim()
            val imgStr = etImg.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            val resId = resources.getIdentifier(imgStr, "drawable", packageName)

            if (title.isEmpty() || author.isEmpty() || imgStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novel = Truyen(
                id = novelId,
                title = title,
                author = author,
                imageRes = resId,
                description = if (desc.isEmpty()) null else desc,
                isFavorite = false
            )

            val result = if (novelId == 0) {
                db.insertNovel(novel)
            } else {
                db.updateNovels(novel).toLong()
            }

            if (result != -1L) {
                Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
