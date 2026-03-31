package com.example.lightnovel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReadingChapterListAdapter(
    private val context: Context,
    private val chapters: List<Chuong>,
    private val onChapterClick: (Chuong) -> Unit
) : RecyclerView.Adapter<ReadingChapterListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvChapterItemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_reading_chapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapter = chapters[position]
        holder.tvName.text = "Chương ${chapter.number}: ${chapter.title}"
        holder.itemView.setOnClickListener { onChapterClick(chapter) }
    }

    override fun getItemCount() = chapters.size
}
