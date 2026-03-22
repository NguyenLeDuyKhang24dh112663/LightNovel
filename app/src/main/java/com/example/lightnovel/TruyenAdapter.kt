package com.example.lightnovel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TruyenAdapter (
    private val list: List<Truyen>,
    private val onClick: (Truyen) -> Unit
) : RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder>() {

    class TruyenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgTruyen: ImageView = itemView.findViewById(R.id.imgTruyen)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TruyenViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_novel, parent, false)
        return TruyenViewHolder(view)
    }

    override fun onBindViewHolder(holder: TruyenViewHolder, position: Int) {
        val truyen = list[position]

        holder.txtTitle.text = truyen.title
        // Enable marquee scrolling
        holder.txtTitle.isSelected = true

        holder.imgTruyen.setImageResource(truyen.imageRes)

        holder.itemView.setOnClickListener {
            onClick(truyen)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
