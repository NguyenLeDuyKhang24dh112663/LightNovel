package com.example.lightnovel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditChapterActivity : AppCompatActivity() {
    private lateinit var db: databaseHelper
    private var novelId: Int = -1
    private var novelName: String = ""
    private var chapterId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_chapter)

        db = databaseHelper(this)

        novelId = intent.getIntExtra("novel_id", -1)
        novelName = intent.getStringExtra("novel_name") ?: ""
        chapterId = intent.getIntExtra("chapter_id", -1)

        if (novelId == -1) {
            Toast.makeText(this, "Không tìm thấy ID truyện!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val tvHeader = findViewById<TextView>(R.id.tvNovelNameHeader)
        val etNumber = findViewById<EditText>(R.id.etChapterNumber)
        val etTitle = findViewById<EditText>(R.id.etChapterTitle)
        val etContent = findViewById<EditText>(R.id.etChapterContent)
        val btnSave = findViewById<Button>(R.id.btnSaveChapter)

        if (chapterId != -1) {
            tvHeader.text = "Sửa chương của: $novelName"
            btnSave.text = "Lưu thay đổi cho '$novelName'"
            
            etNumber.setText(intent.getIntExtra("chapter_number", 0).toString())
            etTitle.setText(intent.getStringExtra("chapter_title"))
            etContent.setText(intent.getStringExtra("chapter_content"))
        } else {
            tvHeader.text = "Thêm chương mới vào '$novelName'"
            btnSave.text = "Thêm chương mới vào '$novelName'"
        }

        btnSave.setOnClickListener {
            val numberStr = etNumber.text.toString().trim()
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (numberStr.isEmpty() || title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val number = numberStr.toIntOrNull()
            if (number == null) {
                Toast.makeText(this, "Số chương phải là một số!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = if (chapterId == -1) {
                db.insertChapter(novelId, number, title, content)
            } else {
                db.updateChapter(chapterId, number, title, content).toLong()
            }

            if (result != -1L) {
                Toast.makeText(this, "Thành công!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi lưu dữ liệu!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
