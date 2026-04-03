package com.example.lightnovel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Comment_Activity : AppCompatActivity() {
    private lateinit var db: databaseHelper
    private lateinit var adapter: CmtAdapter
    private val commentList = mutableListOf<Comment>()
    private var novelId: Int = -1
    private var currentUsername: String? = null
    
    private var editingComment: Comment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comment)

        db = databaseHelper(this)
        novelId = intent.getIntExtra("novel_id", -1)
        val novelTitle = intent.getStringExtra("novel_title") ?: "Bình luận"

        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        currentUsername = sharedPref.getString("username", null)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarCmt)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Bình luận về '$novelTitle'"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvComments = findViewById<RecyclerView>(R.id.rvCmts)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val btnCancelEdit = findViewById<Button>(R.id.btnCancelEdit)

        // Load comments
        loadComments()

        adapter = CmtAdapter(
            commentList,
            onEditClick = { comment -> 
                editingComment = comment
                etComment.setText(comment.content)
                etComment.requestFocus()
                btnSend.text = "Lưu"
                btnCancelEdit.visibility = View.VISIBLE
            },
            onDeleteClick = { comment -> showDeleteConfirmDialog(comment) },
            currentUsername = currentUsername
        )
        rvComments.layoutManager = LinearLayoutManager(this)
        rvComments.adapter = adapter

        btnSend.setOnClickListener {
            val content = etComment.text.toString().trim()
            if (content.isNotEmpty() && novelId != -1) {
                if (editingComment == null) {
                    // Thêm mới
                    val username = currentUsername ?: "Anonymous"
                    val success = db.insertComment(novelId, username, content)
                    if (success != -1L) {
                        loadComments()
                        adapter.notifyDataSetChanged()
                        rvComments.scrollToPosition(0)
                        etComment.text.clear()
                    }
                } else { 
                    // Cập nhật
                    val result = db.updateComment(editingComment!!.id, content)
                    if (result > 0) {
                        loadComments()
                        adapter.notifyDataSetChanged()
                        editingComment = null
                        btnSend.text = "Gửi"
                        btnCancelEdit.visibility = View.GONE
                        etComment.text.clear()
                        Toast.makeText(this, "Đã cập nhật bình luận", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnCancelEdit.setOnClickListener {
            editingComment = null
            etComment.text.clear()
            btnSend.text = "Gửi"
            btnCancelEdit.visibility = View.GONE
        }

    }

    private fun loadComments() {
        if (novelId != -1) {
            commentList.clear()
            commentList.addAll(db.getCommentsForNovel(novelId))
        }
    }

    private fun showDeleteConfirmDialog(comment: Comment) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = builder.create()
        
        // Làm cho background của dialog mặc định trở nên trong suốt để thấy bo góc của CardView
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDelete)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDelete)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)

        tvMessage.text = "Bạn có chắc chắn muốn xóa bình luận này không?"

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val result = db.deleteComment(comment.id)
            if (result > 0) {
                loadComments()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Đã xóa bình luận", Toast.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
