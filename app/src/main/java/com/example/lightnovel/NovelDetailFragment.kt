package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class NovelDetailFragment : Fragment(R.layout.fragment_novel_detail) {

    private lateinit var viewModel: TruyenViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)

        val novelId = arguments?.getInt("id") ?: -1
        val title = arguments?.getString("title")
        val image = arguments?.getInt("image")
        val author = arguments?.getString("author")

        val tvTitle = view.findViewById<TextView>(R.id.txtTen)
        val img = view.findViewById<ImageView>(R.id.imgDetail)
        val tvAuthor = view.findViewById<TextView>(R.id.txtTacGia)
        val btnFav = view.findViewById<ImageView>(R.id.btnFavorite)

        tvTitle.text = title
        tvAuthor.text = "Tác giả: $author"
        image?.let { img.setImageResource(it) }

        // Observe the list to get the latest favorite status for this novel
        viewModel.products.observe(viewLifecycleOwner) { list ->
            val currentNovel = list.find { it.id == novelId }
            currentNovel?.let {
                if (it.isFavorite) {
                    btnFav.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
        }

        btnFav.setOnClickListener {
            if (novelId != -1) {
                viewModel.toggleFavorite(novelId)
            }
        }
    }
}
