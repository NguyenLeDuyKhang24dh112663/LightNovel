package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteSectionFragment : Fragment(R.layout.fragment_favorite_section) {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var viewModel: TruyenViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)

        // Set up edge-to-edge padding for the fragment's root view
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvFavorites = view.findViewById(R.id.rvFavorites)
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())

        // Observe the viewmodel to get ONLY the favorite novels
        viewModel.products.observe(viewLifecycleOwner) { list ->
            val favoritesList = list.filter { it.isFavorite }
            adapter = FavoriteAdapter(requireContext(), favoritesList)
            rvFavorites.adapter = adapter
        }
    }
}
