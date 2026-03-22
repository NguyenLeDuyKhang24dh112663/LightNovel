package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Context
import kotlin.apply


class ReadingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    private var chuong = 1
    private var vitri = 0
    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_reading, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(requireContext())

        recyclerView.layoutManager = layoutManager

        // 🔥 Nhận dữ liệu
        chuong = arguments?.getInt("chuong") ?: 1
        vitri = arguments?.getInt("vitri") ?: 0

        // 🔥 Fake data
        val list = List(100) { "Chương $chuong - dòng ${it + 1}" }

// Dùng ReadingAdapter thay vì TruyenAdapter
        recyclerView.adapter = ReadingAdapter(list)

        // 🔥 Scroll lại
        recyclerView.post {
            recyclerView.scrollToPosition(vitri)
        }

        // 🔥 Lấy vị trí khi scroll
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                currentPosition = layoutManager.findFirstVisibleItemPosition()
            }
        })

        return view
    }

    // 🔥 Lưu vị trí
    override fun onPause() {
        super.onPause()

        val pref = requireContext()
            .getSharedPreferences("READING", Context.MODE_PRIVATE)

        pref.edit().apply {
            putInt("chuong", chuong)
            putInt("vitri", currentPosition)
            apply()
        }
    }
}