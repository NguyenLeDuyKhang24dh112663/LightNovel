package com.example.lightnovel

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReadHistoryFragment : Fragment(R.layout.fragment_read_history) {

    private lateinit var db: databaseHelper
    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = databaseHelper(requireContext())
        rvHistory = view.findViewById(R.id.rvHistory)
        tvEmpty = view.findViewById(R.id.tvEmptyHistory)

        rvHistory.layoutManager = GridLayoutManager(requireContext(), 2)
        
        loadHistory()
    }

    private fun loadHistory() {
        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", null)

        if (username != null) {
            val historyList = db.getReadHistory(username)
            if (historyList.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                rvHistory.visibility = View.GONE
            } else {
                tvEmpty.visibility = View.GONE
                rvHistory.visibility = View.VISIBLE
                rvHistory.adapter = TruyenAdapter(historyList) { truyen ->
                    val bundle = Bundle().apply {
                        putInt("id", truyen.id)
                        putString("title", truyen.title)
                        putInt("image", truyen.imageRes)
                        putString("author", truyen.author)
                        putString("description", truyen.description)
                    }
                    val detailFragment = NovelDetailFragment()
                    detailFragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.flSectionsLayout, detailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }
}
