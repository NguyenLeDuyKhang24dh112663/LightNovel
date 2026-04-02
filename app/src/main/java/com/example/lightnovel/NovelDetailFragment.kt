package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.widget.Toolbar

class NovelDetailFragment : Fragment(R.layout.fragment_novel_detail) {

    private lateinit var viewModel: TruyenViewModel
    private lateinit var db: databaseHelper
    private var isDescriptionExpanded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)
        db = databaseHelper(requireContext())

        val novelId = arguments?.getInt("id") ?: -1
        val title = arguments?.getString("title")
        val image = arguments?.getInt("image")
        val description = arguments?.getString("description") ?: ""
        val author = arguments?.getString("author")

        val toolbar = view.findViewById<Toolbar>(R.id.toolbarDetail)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

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
        
        setupExpandableDescription(tvDescription, description)

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

    private fun setupExpandableDescription(textView: TextView, fullText: String) {
        val limit = 200
        if (fullText.length <= limit) {
            textView.text = fullText
            return
        }

        val updateText = {
            val displayText = if (isDescriptionExpanded) {
                "$fullText  Thu gọn"
            } else {
                "${fullText.substring(0, limit)}... Đọc thêm"
            }

            val spannableString = SpannableString(displayText)
            val suffix = if (isDescriptionExpanded) "Thu gọn" else "Đọc thêm"
            val startIndex = displayText.lastIndexOf(suffix)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    isDescriptionExpanded = !isDescriptionExpanded
                    setupExpandableDescription(textView, fullText)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
                    ds.isFakeBoldText = true
                }
            }

            spannableString.setSpan(clickableSpan, startIndex, displayText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.text = spannableString
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
        
        updateText()
    }
}
