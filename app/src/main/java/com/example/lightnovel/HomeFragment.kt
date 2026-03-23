package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: TruyenViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)

        // ===== Dropdown Thể loại (Exposed Dropdown Menu) =====
        val autoCompleteGenres = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteGenres)
        
        updateGenresAdapter(autoCompleteGenres)

        autoCompleteGenres.setOnItemClickListener { parent, _, position, _ ->
            val selectedGenre = parent.getItemAtPosition(position) as String
            viewModel.filterByGenre(selectedGenre)
        }
        
        // Thêm click listener để đảm bảo dropdown hiện ra khi click vào
        autoCompleteGenres.setOnClickListener {
            autoCompleteGenres.showDropDown()
        }

        // ===== RecyclerView Truyện =====
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.products.observe(viewLifecycleOwner) { list ->
            recyclerView.adapter = TruyenAdapter(list) { truyen ->
                viewModel.select(truyen)
                val bundle = Bundle()
                bundle.putInt("id", truyen.id)
                bundle.putString("title", truyen.title)
                bundle.putInt("image", truyen.imageRes)
                bundle.putString("author", truyen.author)
                bundle.putString("description", truyen.description)

                val detailFragment = NovelDetailFragment()
                detailFragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.flSectionsLayout, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // ===== Banner ViewPager2 =====
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerBanner)

        val bannerList = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        val bannerAdapter = BannerAdapter(bannerList)
        viewPager.adapter = bannerAdapter
    }

    private fun updateGenresAdapter(autoCompleteGenres: AutoCompleteTextView) {
        val genres = viewModel.getAllGenres()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genres)
        autoCompleteGenres.setAdapter(adapter)
        
        // Quan trọng: Sử dụng filter = false khi đặt text ban đầu để tránh việc 
        // AutoCompleteTextView lọc mất các item khác trong danh sách dropdown.
        val currentText = autoCompleteGenres.text.toString()
        if (currentText.isNotEmpty()) {
            autoCompleteGenres.setText(currentText, false)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFromDb()
        // Cập nhật lại danh sách thể loại từ database khi quay lại màn hình này
        view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteGenres)?.let {
            updateGenresAdapter(it)
        }
    }
}
