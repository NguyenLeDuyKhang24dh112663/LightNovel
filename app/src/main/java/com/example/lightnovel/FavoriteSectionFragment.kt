package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteSectionFragment : Fragment() {

    private var rvFavorites: RecyclerView? = null
    private var tvEmpty: TextView? = null
    private lateinit var adapter: FavoriteAdapter
    private lateinit var viewModel: TruyenViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        return if (isLoggedIn) {
            inflater.inflate(R.layout.fragment_favorite_section, container, false)
        } else {
            inflater.inflate(R.layout.fragment_not_logged_in, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)

            // Set up edge-to-edge padding for the fragment's root view
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            rvFavorites = view.findViewById(R.id.rvFavorites)
            tvEmpty = view.findViewById(R.id.tvEmptyFavorites)
            rvFavorites?.layoutManager = LinearLayoutManager(requireContext())

            // Observe the viewmodel to get ONLY the favorite novels
            viewModel.products.observe(viewLifecycleOwner) { list ->
                val favoritesList = list.filter { it.isFavorite }

                if (favoritesList.isEmpty()) {
                    rvFavorites?.visibility = View.GONE
                    tvEmpty?.visibility = View.VISIBLE
                } else {
                    rvFavorites?.visibility = View.VISIBLE
                    tvEmpty?.visibility = View.GONE
                    adapter = FavoriteAdapter(requireContext(), favoritesList)
                    rvFavorites?.adapter = adapter
                }
            }
        } else {
            // Setup listeners for Not Logged In view
            view.findViewById<Button>(R.id.button).setOnClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            view.findViewById<Button>(R.id.button4).setOnClickListener {
                startActivity(Intent(requireContext(), SigninActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            viewModel.refreshFromDb()
        }
    }
}
