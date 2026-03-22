package com.example.lightnovel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TheLoaiAdminAdapter(
    private val context: Context,
    private val genreList: ArrayList<TheLoai>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TheLoaiAdminAdapter.GenreViewHolder>() {

    interface OnItemClickListener {
        fun onEdit(genre: TheLoai)
        fun onDelete(genre: TheLoai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_truyen_trong_admin, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genreList[position]
        holder.tvName.text = "Thể loại: ${genre.name}"

        holder.btnEdit.setOnClickListener { listener.onEdit(genre) }
        holder.btnDelete.setOnClickListener { listener.onDelete(genre) }
    }

    override fun getItemCount(): Int = genreList.size

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvTitle)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }
}
