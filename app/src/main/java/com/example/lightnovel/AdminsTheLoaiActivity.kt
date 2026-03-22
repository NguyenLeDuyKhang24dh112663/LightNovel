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

class AdminsTheLoaiActivity : AppCompatActivity(), TheLoaiAdminAdapter.OnItemClickListener {

    private lateinit var db: databaseHelper
    private lateinit var adapter: TheLoaiAdminAdapter
    private lateinit var genres: ArrayList<TheLoai>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admins_the_loai)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = databaseHelper(this)
        genres = db.getAllGenresWithIds()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TheLoaiAdminAdapter(this, genres, this)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, TheLoaiAddEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        genres.clear()
        genres.addAll(db.getAllGenresWithIds())
        adapter.notifyDataSetChanged()
    }

    override fun onEdit(genre: TheLoai) {
        val intent = Intent(this, TheLoaiAddEditActivity::class.java)
        intent.putExtra("id", genre.id)
        intent.putExtra("name", genre.name)
        startActivity(intent)
    }

    override fun onDelete(genre: TheLoai) {
        db.deleteGenre(genre.id)
        refreshData()
    }
}
