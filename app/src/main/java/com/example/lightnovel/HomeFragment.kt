package com.example.lightnovel

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: TruyenViewModel
    private lateinit var viewPager: ViewPager2
    private val sliderHandler = Handler(Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

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
        
        autoCompleteGenres.setOnClickListener {
            autoCompleteGenres.showDropDown()
        }

        // ===== RecyclerView Truyện =====
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.products.observe(viewLifecycleOwner) { list ->
            recyclerView.adapter = TruyenAdapter(list) { truyen ->
                navigateToDetail(truyen)
            }

            // ===== Banner ViewPager2 (Ngẫu nhiên 5 truyện) =====
            if (list.isNotEmpty()) {
                setupBanner(list)
            }
        }

        // Nút điều hướng banner thủ công
        view.findViewById<ImageButton>(R.id.btnBannerLeft).setOnClickListener {
            val current = viewPager.currentItem
            if (current > 0) viewPager.currentItem = current - 1
        }

        view.findViewById<ImageButton>(R.id.btnBannerRight).setOnClickListener {
            val current = viewPager.currentItem
            val count = viewPager.adapter?.itemCount ?: 0
            if (current < count - 1) viewPager.currentItem = current + 1
        }
    }

    private fun setupBanner(fullList: List<Truyen>) {
        if (!isAdded) return
        viewPager = requireView().findViewById(R.id.viewPagerBanner)
        
        // Lấy ngẫu nhiên tối đa 5 truyện làm banner
        val randomNovels = fullList.shuffled().take(5)

        val bannerAdapter = BannerAdapter(randomNovels) { truyen ->
            navigateToDetail(truyen)
        }
        viewPager.adapter = bannerAdapter

        // Tự động cuộn mỗi 3 giây
        sliderRunnable = Runnable {
            if (!isAdded) return@Runnable
            val nextItem = if (viewPager.currentItem == randomNovels.size - 1) 0 else viewPager.currentItem + 1
            viewPager.currentItem = nextItem
            sliderHandler.postDelayed(sliderRunnable, 3000)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    private fun navigateToDetail(truyen: Truyen) {
        viewModel.select(truyen)
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

    private fun updateGenresAdapter(autoCompleteGenres: AutoCompleteTextView) {
        val genres = viewModel.getAllGenres()
        // Sử dụng layout tùy chỉnh item_dropdown_genre để hiển thị font chữ Montserrat
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_genre, genres)
        autoCompleteGenres.setAdapter(adapter)
        
        val currentText = autoCompleteGenres.text.toString()
        if (currentText.isNotEmpty()) {
            autoCompleteGenres.setText(currentText, false)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFromDb()
        view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteGenres)?.let {
            updateGenresAdapter(it)
        }
        if (::sliderRunnable.isInitialized) {
            sliderHandler.postDelayed(sliderRunnable, 3000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sliderRunnable.isInitialized) {
            sliderHandler.removeCallbacks(sliderRunnable)
        }
    }
}
