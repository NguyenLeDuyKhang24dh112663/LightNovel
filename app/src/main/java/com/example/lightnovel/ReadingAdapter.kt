package com.example.lightnovel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReadingAdapter(
    private val list: List<String>
) : RecyclerView.Adapter<ReadingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLine: TextView = itemView.findViewById(R.id.txtLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reading_line, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtLine.text = list[position]
    }

    override fun getItemCount(): Int = list.size
}