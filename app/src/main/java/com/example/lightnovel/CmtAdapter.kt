package com.example.lightnovel

import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CmtAdapter(
    private val list: MutableList<Comment>,
    private val onEditClick: (Comment) -> Unit,
    private val onDeleteClick: (Comment) -> Unit,
    private val currentUsername: String?
) : RecyclerView.Adapter<CmtAdapter.ViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

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

        setupExpandableText(holder.tvCmtcontent, item.content, position, 100)

        // Hiển thị nút sửa và xóa dựa trên người dùng hiện tại

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

    private fun setupExpandableText(textView: TextView, fullText: String, position: Int, limit: Int) {
        if (fullText.length <= limit) {
            textView.text = fullText
            return
        }

        val isExpanded = expandedPositions.contains(position)
        val displayText = if (isExpanded) {
            "$fullText  Thu gọn"
        } else {
            "${fullText.substring(0, limit)}... Đọc thêm"
        }

        val spannableString = SpannableString(displayText)
        val suffix = if (isExpanded) "Thu gọn" else "Đọc thêm"
        val startIndex = displayText.lastIndexOf(suffix)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (isExpanded) {
                    expandedPositions.remove(position)
                } else {
                    expandedPositions.add(position)
                }
                notifyItemChanged(position)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(textView.context, android.R.color.holo_orange_dark)
                ds.isFakeBoldText = true
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, displayText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
