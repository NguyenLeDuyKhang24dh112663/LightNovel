package com.example.lightnovel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminsActivity : AppCompatActivity(), TruyenAdminAdapter.OnItemClickListener {

    private lateinit var db: databaseHelper
    private lateinit var adapter: TruyenAdminAdapter
    private lateinit var novels: ArrayList<Truyen>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admins)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = databaseHelper(this)
        novels = db.getAllNovels()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TruyenAdminAdapter(this, novels, this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        novels.clear()
        novels.addAll(db.getAllNovels())
        adapter.notifyDataSetChanged()
    }

    override fun onEdit(novel: Truyen) {
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", novel.id)
        intent.putExtra("Tên truyện", novel.title)
        intent.putExtra("Tác giả", novel.author)
        intent.putExtra("Ảnh", novel.imageRes)
        startActivity(intent)
    }

    override fun onDelete(novel: Truyen) {
        db.deleteNovel(novel.id)
        refreshData()
    }
}