package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TruyenAdminAdapter(
    private val context: Context,
    private val novels: ArrayList<Truyen>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TruyenAdminAdapter.TruyenAdminViewHolder>() {

    private val db = databaseHelper(context)

    interface OnItemClickListener {
        fun onEdit(novel: Truyen)
        fun onDelete(novel: Truyen)
    }

    inner class TruyenAdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTtl: TextView = view.findViewById(R.id.tvTitle)
        val tvAu: TextView = view.findViewById(R.id.tvAuthor)
        val tvGenre: TextView = view.findViewById(R.id.tvGenre)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val ivNovelImg: ImageView = view.findViewById(R.id.ivNovelImg)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnChapter: Button = view.findViewById(R.id.btnChapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruyenAdminViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_truyen_trong_admin, parent, false)
        return TruyenAdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: TruyenAdminViewHolder, position: Int) {
        val novel = novels[position]
        holder.tvTtl.text = "Tên: ${novel.title}"
        holder.tvAu.text = "Tác giả: ${novel.author}"
        
        val genres = db.getGenresForNovel(novel.id)
        val genreNames = if (genres.isNotEmpty()) {
            genres.joinToString(", ") { it.name }
        } else {
            "Chưa có thể loại"
        }
        holder.tvGenre.text = "Thể loại: $genreNames"
        
        val descText = if (novel.description.isNullOrEmpty()) "null" else novel.description
        holder.tvDesc.text = "Mô tả: $descText"


        if (novel.imageRes != 0) {
            holder.ivNovelImg.setImageResource(novel.imageRes)
        } else {
            holder.ivNovelImg.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.btnEdit.setOnClickListener { listener.onEdit(novel) }
        holder.btnDelete.setOnClickListener { listener.onDelete(novel) }
        
        holder.btnChapter.setOnClickListener {
            val intent = Intent(context, ChaptersListActivity::class.java).apply {
                putExtra("novel_id", novel.id)
                putExtra("novel_name", novel.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = novels.size

}
