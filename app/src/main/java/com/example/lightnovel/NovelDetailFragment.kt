package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class NovelDetailFragment : Fragment(R.layout.fragment_novel_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title")
        val image = arguments?.getInt("image")
        val author = arguments?.getString("author")

        val tvTitle = view.findViewById<TextView>(R.id.txtTen)
        val img = view.findViewById<ImageView>(R.id.imgDetail)
        val tvAuthor = view.findViewById<TextView>(R.id.txtTacGia)

        tvTitle.text = title
        tvAuthor.text = "Tác giả: $author"

        image?.let {
            img.setImageResource(it)
        }
    }
}