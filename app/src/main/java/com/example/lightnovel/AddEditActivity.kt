package com.example.lightnovel

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddEditActivity : AppCompatActivity() {
    private lateinit var db: databaseHelper
    private var novelId = 0
    private var allGenres = ArrayList<TheLoai>()
    private var selectedGenreIds = ArrayList<Int>()

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
        
        // Tải toàn bộ thể loại từ database
        allGenres = db.getAllGenresWithIds()

        val etTtl = findViewById<EditText>(R.id.etTitle)
        val etAu = findViewById<EditText>(R.id.etAuthor)
        val etImg = findViewById<EditText>(R.id.etImgLink)
        val etDesc = findViewById<EditText>(R.id.etDesc)
        val tvSelectedGenres = findViewById<TextView>(R.id.tvSelectedGenres)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val bundle = intent.extras
        if (bundle != null) {
            novelId = bundle.getInt("id", 0)
            if (novelId != 0) {
                etTtl.setText(bundle.getString("Tên truyện"))
                etAu.setText(bundle.getString("Tác giả"))
                val imgValue = bundle.get("Ảnh")
                etImg.setText(imgValue?.toString() ?: "")
                etDesc.setText(bundle.getString("Mô tả"))
                
                // Lấy các thể loại hiện tại của truyện từ database
                val currentGenres = db.getGenresForNovel(novelId)
                selectedGenreIds.clear()
                currentGenres.forEach { selectedGenreIds.add(it.id) }
                updateGenreText(tvSelectedGenres)
            }
        }

        // Sự kiện click để chọn thể loại bằng Dialog
        tvSelectedGenres.setOnClickListener {
            showGenreSelectionDialog(tvSelectedGenres)
        }

        btnSave.setOnClickListener {
            val title = etTtl.text.toString().trim()
            val author = etAu.text.toString().trim()
            val imgStr = etImg.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            var resId = resources.getIdentifier(imgStr, "drawable", packageName)
            if (resId == 0 && imgStr.isNotEmpty()) {
                try { resId = imgStr.toInt() } catch (e: Exception) {}
            }

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
                db.insertNovel(novel, selectedGenreIds)
            } else {
                db.updateNovel(novel, selectedGenreIds).toLong()
            }

            if (result != -1L) {
                Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showGenreSelectionDialog(tvSelectedGenres: TextView) {
        val genreNames = allGenres.map { it.name }.toTypedArray()
        val checkedItems = BooleanArray(allGenres.size) { index ->
            selectedGenreIds.contains(allGenres[index].id)
        }

        AlertDialog.Builder(this)
            .setTitle("Chọn thể loại")
            .setMultiChoiceItems(genreNames, checkedItems) { _, which, isChecked ->
                val genreId = allGenres[which].id
                if (isChecked) {
                    if (!selectedGenreIds.contains(genreId)) selectedGenreIds.add(genreId)
                } else {
                    selectedGenreIds.remove(genreId)
                }
            }
            .setPositiveButton("Xong") { _, _ ->
                updateGenreText(tvSelectedGenres)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun updateGenreText(textView: TextView) {
        if (selectedGenreIds.isEmpty()) {
            textView.text = "Chưa chọn thể loại nào"
        } else {
            val selectedNames = allGenres.filter { selectedGenreIds.contains(it.id) }.map { it.name }
            textView.text = selectedNames.joinToString(", ")
        }
    }
}
