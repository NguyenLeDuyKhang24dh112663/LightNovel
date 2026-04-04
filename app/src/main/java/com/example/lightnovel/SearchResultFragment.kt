package com.example.lightnovel

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchResultFragment : Fragment(R.layout.fragment_search_result) {

    private lateinit var viewModel: TruyenViewModel
    private var searchQuery: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)
        searchQuery = arguments?.getString("query") ?: ""

        val rvSearchResults = view.findViewById<RecyclerView>(R.id.rvSearchResults)
        val tvNoResults = view.findViewById<TextView>(R.id.tvNoResults)
        val tvSearchTitle = view.findViewById<TextView>(R.id.tvSearchTitle)

        tvSearchTitle.text = "Kết quả cho: '$searchQuery'"

        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())

        viewModel.products.observe(viewLifecycleOwner) { list ->
            // Lọc theo từ khóa
            val filteredList = list.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.author.contains(searchQuery, ignoreCase = true)
            }

            if (filteredList.isEmpty()) {
                rvSearchResults.visibility = View.GONE
                tvNoResults.visibility = View.VISIBLE
            } else {
                rvSearchResults.visibility = View.VISIBLE
                tvNoResults.visibility = View.GONE

                // FavoriteAdapter
                rvSearchResults.adapter = FavoriteAdapter(requireContext(), filteredList)
            }
        }
    }
}