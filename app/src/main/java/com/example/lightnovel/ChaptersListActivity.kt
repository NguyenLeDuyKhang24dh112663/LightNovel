package com.example.lightnovel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChaptersListActivity : AppCompatActivity() {
    private lateinit var db: databaseHelper
    private lateinit var adapter: ChapterAdapter
    private var novelId: Int = -1
    private var novelName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters_list)

        db = databaseHelper(this)
        novelId = intent.getIntExtra("novel_id", -1)
        novelName = intent.getStringExtra("novel_name") ?: ""

        findViewById<TextView>(R.id.tvNovelName).text = "Chương của: $novelName"

        val rvChapters = findViewById<RecyclerView>(R.id.rvChapters)
        rvChapters.layoutManager = LinearLayoutManager(this)

        val btnAdd = findViewById<Button>(R.id.btnAddChapter)
        btnAdd.text = "Thêm chương mới vào '$novelName'"
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddEditChapterActivity::class.java).apply {
                putExtra("novel_id", novelId)
                putExtra("novel_name", novelName)
            }
            startActivity(intent)
        }

        loadChapters()
    }

    private fun loadChapters() {
        val chapters = db.getChaptersForNovel(novelId)
        adapter = ChapterAdapter(this, chapters, object : ChapterAdapter.OnChapterClickListener {
            override fun onEdit(chapter: Chuong) {
                val intent = Intent(this@ChaptersListActivity, AddEditChapterActivity::class.java).apply {
                    putExtra("novel_id", novelId)
                    putExtra("novel_name", novelName)
                    putExtra("chapter_id", chapter.id)
                    putExtra("chapter_number", chapter.number)
                    putExtra("chapter_title", chapter.title)
                    putExtra("chapter_content", chapter.content)
                }
                startActivity(intent)
            }

            override fun onDelete(chapter: Chuong) {
                db.deleteChapter(chapter.id)
                loadChapters()
            }
        })
        findViewById<RecyclerView>(R.id.rvChapters).adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadChapters()
    }
}
