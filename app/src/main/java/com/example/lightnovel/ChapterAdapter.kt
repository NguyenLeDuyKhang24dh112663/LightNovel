package com.example.lightnovel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChapterAdapter(
    private val context: Context,
    private val chapters: ArrayList<Chuong>,
    private val listener: OnChapterClickListener
) : RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder>() {

    interface OnChapterClickListener {
        fun onEdit(chapter: Chuong)
        fun onDelete(chapter: Chuong)
    }

    inner class ChapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNo: TextView = view.findViewById(R.id.tvChapterNo)
        val tvName: TextView = view.findViewById(R.id.tvChapterName)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chuong_cua_truyen, parent, false)
        return ChapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.tvNo.text = "Chương số: ${chapter.number}"
        holder.tvName.text = "Tên chương: ${chapter.title}"

        holder.btnEdit.setOnClickListener { listener.onEdit(chapter) }
        holder.btnDelete.setOnClickListener { listener.onDelete(chapter) }
    }

    override fun getItemCount() = chapters.size
}
