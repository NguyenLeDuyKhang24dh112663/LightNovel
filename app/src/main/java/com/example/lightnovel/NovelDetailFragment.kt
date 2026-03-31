package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class NovelDetailFragment : Fragment(R.layout.fragment_novel_detail) {

    private lateinit var viewModel: TruyenViewModel
    private lateinit var db: databaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)
        db = databaseHelper(requireContext())

        val novelId = arguments?.getInt("id") ?: -1
        val title = arguments?.getString("title")
        val image = arguments?.getInt("image")
        val description = arguments?.getString("description")
        val author = arguments?.getString("author")

        val tvTitle = view.findViewById<TextView>(R.id.txtTen)
        val img = view.findViewById<ImageView>(R.id.imgDetail)
        val tvDescription = view.findViewById<TextView>(R.id.txtMoTa)
        val tvAuthor = view.findViewById<TextView>(R.id.txtTacGia)
        val btnFav = view.findViewById<ImageView>(R.id.btnFavorite)
        val btnViewChapters = view.findViewById<TextView>(R.id.btnViewChapters)
        val btnContinue = view.findViewById<TextView>(R.id.btnContinue)
        val btnShare = view.findViewById<TextView>(R.id.btnShare)
        val btnCmts = view.findViewById<TextView>(R.id.btnCmts)

        tvTitle.text = title
        tvAuthor.text = "Tác giả: $author"
        
        tvDescription.text = if (description.isNullOrEmpty()) "Không có mô tả." else description

        image?.let { img.setImageResource(it) }

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

        btnViewChapters.setOnClickListener {
            if (novelId != -1) {
                val fragment = ReadingFragment()
                val bundle = Bundle().apply {
                    putInt("id", novelId)
                    putString("title", title)
                }
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.flSectionsLayout, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnContinue.setOnClickListener {
            if (novelId != -1) {
                val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                val username = sharedPref.getString("username", null)
                
                var lastChapter = 1
                if (username != null) {
                    lastChapter = db.getLastReadChapter(username, novelId)
                }

                val fragment = ReadingFragment()
                val bundle = Bundle().apply {
                    putInt("id", novelId)
                    putString("title", title)
                    putInt("chapter_number", lastChapter)
                    putBoolean("continue", true)
                }
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.flSectionsLayout, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnShare.setOnClickListener {
            val shareText = "📖 Mình đang đọc $title của tác giả $author trên Gấu Truyện!\nBạn cũng thử đọc đi nhé 😄"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Chia sẻ truyện qua..."))
        }

        btnCmts.setOnClickListener {
            if (novelId != -1) {
                val intent = Intent(requireContext(), Comment_Activity::class.java).apply {
                    putExtra("novel_id", novelId)
                    putExtra("novel_title", title)
                }
                startActivity(intent)
            }
        }
    }
}
