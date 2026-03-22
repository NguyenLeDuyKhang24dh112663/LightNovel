package com.example.lightnovel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.content.Intent

class Comment_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comment)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarCmt)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Bình luận"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Gắn RecyclerView
        val rvComments = findViewById<RecyclerView>(R.id.rvCmts)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val btnBack = findViewById<Button>(R.id.btnBack)


        val adapter = CmtAdapter(fakecmt.comments)
        rvComments.layoutManager = LinearLayoutManager(this)
        rvComments.adapter = CmtAdapter(fakecmt.comments)

        btnSend.setOnClickListener {
            val newComment = etComment.text.toString().trim()
            if (newComment.isNotEmpty()) {
                fakecmt.comments.add(Comment("Bạn", newComment, "Now"))
                adapter.notifyItemInserted(fakecmt.comments.size - 1)
                rvComments.scrollToPosition(fakecmt.comments.size - 1)
                etComment.text.clear()
            }


            }
        btnBack.setOnClickListener {
           finish()
        }
    }
}