package com.example.lightnovel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CmtAdapter(private val list: List<Comment>) :
    RecyclerView.Adapter<CmtAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cmt, parent, false)
        return ViewHolder(view)
    }

        // Kho dữ liệu tạm thời -> loai lại trang web thì nó có sẵn để hiển thị
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val image: ImageView = view.findViewById(R.id.imgBanner)
        val tvCmtname: TextView = view.findViewById(R.id.tvCmtname)
            val tvCmttime: TextView = view.findViewById(R.id.tvCmttime)
            val tvCmtcontent: TextView = view.findViewById(R.id.tvCmtcontent)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(khoDuLieu: ViewHolder, position: Int) {
        val item = list[position]

        khoDuLieu.tvCmtname.text = item.name
        khoDuLieu.tvCmttime.text = item.day
        khoDuLieu.tvCmtcontent.text = item.content

    }

}