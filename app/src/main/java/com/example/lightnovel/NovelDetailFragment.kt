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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)

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
        val btnStart = view.findViewById<TextView>(R.id.btnStart)
        val btnContinue = view.findViewById<TextView>(R.id.btnContinue)
        val btnShare = view.findViewById<TextView>(R.id.btnShare)
        val btnCmts = view.findViewById<TextView>(R.id.btnCmts)



        tvTitle.text = title
        tvAuthor.text = "Tác giả: $author"
        
        // Show default text if description is null or empty
        tvDescription.text = if (description.isNullOrEmpty()) "Không có mô tả." else description

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

        btnStart.setOnClickListener {
            val fragment = ReadingFragment()
            val bundle = Bundle()

            bundle.putInt("chuong", 1)
            bundle.putInt("vitri", 0)

            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.flSectionsLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

        // 🔥 Đọc tiếp
        btnContinue.setOnClickListener {
            val pref = requireContext()
                .getSharedPreferences("READING", Context.MODE_PRIVATE)

            val chuong = pref.getInt("chuong", 1)
            val vitri = pref.getInt("vitri", 0)

            val fragment = ReadingFragment()
            val bundle = Bundle()

            bundle.putInt("chuong", chuong)
            bundle.putInt("vitri", vitri)

            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.flSectionsLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

        // Nút Share
        btnShare.setOnClickListener {
            val shareText = "📖 Mình đang đọc $title của tác giả $author trên Gấu Truyện!\nBạn cũng thử đọc đi nhé 😄"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Chia sẻ truyện qua..."))
        }

        btnCmts.setOnClickListener {
            val intent = Intent(requireContext(), Comment_Activity::class.java)
            startActivity(intent)
        }
    }
}
