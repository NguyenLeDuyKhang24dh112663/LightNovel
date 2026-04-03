package com.example.lightnovel

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReadingFragment : Fragment() {

    private lateinit var db: databaseHelper
    private lateinit var rvChapters: RecyclerView
    private lateinit var layoutChapterList: LinearLayout
    private lateinit var layoutChapterContent: LinearLayout
    private lateinit var tvContent: TextView
    private lateinit var tvCurrentChapterTitle: TextView
    private lateinit var scrollViewContent: NestedScrollView
    
    private var novelId: Int = -1
    private var novelTitle: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_reading, container, false)

        db = databaseHelper(requireContext())
        
        // Nhận dữ liệu
        novelId = arguments?.getInt("id") ?: -1
        novelTitle = arguments?.getString("title") ?: "Đang đọc"
        val chapterNumberToLoad = arguments?.getInt("chapter_number", -1) ?: -1
        val isContinue = arguments?.getBoolean("continue", false) ?: false

        // Ánh xạ View
        layoutChapterList = view.findViewById(R.id.layoutChapterList)
        layoutChapterContent = view.findViewById(R.id.layoutChapterContent)
        rvChapters = view.findViewById(R.id.rvChaptersList)
        tvContent = view.findViewById(R.id.tvContent)
        tvCurrentChapterTitle = view.findViewById(R.id.tvCurrentChapterTitle)
        scrollViewContent = view.findViewById(R.id.scrollViewContent)
        
        val tvNovelTitle = view.findViewById<TextView>(R.id.tvReadingNovelTitle)
        tvNovelTitle.text = "Chương của: $novelTitle"

        val btnBackToDetail = view.findViewById<ImageButton>(R.id.btnBackToDetail)
        val btnBackToList = view.findViewById<ImageView>(R.id.ivBack)
        val btnTop = view.findViewById<Button>(R.id.btnMoveToTop)
        val btnBottom = view.findViewById<Button>(R.id.btnMoveToBottom)

        // Setup RecyclerView danh sách chương
        rvChapters.layoutManager = LinearLayoutManager(requireContext())
        
        val chapters = db.getChaptersForNovel(novelId)
        val adapter = ReadingChapterListAdapter(requireContext(), chapters) { chapter ->
            showChapterContent(chapter)
        }
        rvChapters.adapter = adapter

        // Kiểm tra xem có phải nhấn "Đọc tiếp" không
        if (isContinue && chapterNumberToLoad != -1) {
            val chapter = chapters.find { it.number == chapterNumberToLoad }
            if (chapter != null) {
                showChapterContent(chapter)
            }
        }

        // Sự kiện nút bấm
        btnBackToDetail.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnBackToList.setOnClickListener {
            showChapterList()
        }

        btnTop.setOnClickListener {
            scrollViewContent.fullScroll(View.FOCUS_UP)
        }

        btnBottom.setOnClickListener {
            scrollViewContent.fullScroll(View.FOCUS_DOWN)
        }

        return view
    }

    private fun showChapterContent(chapter: Chuong) {
        layoutChapterList.visibility = View.GONE
        layoutChapterContent.visibility = View.VISIBLE
        
        tvCurrentChapterTitle.text = "Chương ${chapter.number}: ${chapter.title}"
        tvContent.text = chapter.content
        
        // Lưu lịch sử đọc khi mở chương
        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)
        if (username != null) {
            db.addToHistory(username, novelId, chapter.number)
        }
        
        // Cuộn lên đầu khi mở chương mới
        scrollViewContent.scrollTo(0, 0)
    }

    private fun showChapterList() {
        layoutChapterList.visibility = View.VISIBLE
        layoutChapterContent.visibility = View.GONE
    }
}
