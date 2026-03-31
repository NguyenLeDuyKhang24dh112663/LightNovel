package com.example.lightnovel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CmtAdapter(
    private val list: MutableList<Comment>,
    private val onEditClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit,
    private val currentUsername: String?
) : RecyclerView.Adapter<CmtAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cmt, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCmtname: TextView = view.findViewById(R.id.tvCmtname)
        val tvCmttime: TextView = view.findViewById(R.id.tvCmttime)
        val tvCmtcontent: TextView = view.findViewById(R.id.tvCmtcontent)
        val ibDelete: ImageButton = view.findViewById(R.id.ibDeleteCmt)
        val ibEdit: ImageButton = view.findViewById(R.id.ibEditCmt)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvCmtname.text = item.username
        holder.tvCmttime.text = item.getFormattedDate()
        holder.tvCmtcontent.text = item.content

        // Chỉ cho phép sửa/xóa nếu là comment của chính người dùng đó
        if (item.username == currentUsername) {
            holder.ibDelete.visibility = View.VISIBLE
            holder.ibEdit.visibility = View.VISIBLE
        } else {
            holder.ibDelete.visibility = View.GONE
            holder.ibEdit.visibility = View.GONE
        }

        holder.ibEdit.setOnClickListener { onEditClick(item) }
        holder.ibDelete.setOnClickListener { onDeleteClick(item) }
    }
}
