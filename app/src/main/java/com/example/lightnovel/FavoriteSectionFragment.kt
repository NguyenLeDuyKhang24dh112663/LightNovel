package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavoriteSectionFragment : Fragment(R.layout.fragment_favorite_section) {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var rvFavorites: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private var favorites: List<Favorites> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up edge-to-edge padding for the fragment's root view
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvFavorites = view.findViewById(R.id.rvFavorites)

        favorites = listOf(
            Favorites("Nhà giả kimmmmmm heheheheheheheh", "Paulo Coelho", R.drawable.nha_gia_kim),

            Favorites("Novel 1","khang", R.drawable.ic_launcher_background),
            Favorites("Novel 2", "kiệt",R.drawable.ic_launcher_background),
            Favorites("Novel 3", "thịnh",R.drawable.ic_launcher_background),
            Favorites("Sự im lặng của bầy cừu", "Thomas Harris", R.drawable.suimlangcuabaycuu),
            Favorites("Sự im lặng của bầy cừu ha ha hja ha ha ha ha ha", "Thomas Harris", R.drawable.suimlangcuabaycuu),

        )

        // Initialize with your data (currently empty)
        adapter = FavoriteAdapter(requireContext(), favorites)
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.adapter = adapter


    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteSectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
