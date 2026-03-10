package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NovelDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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