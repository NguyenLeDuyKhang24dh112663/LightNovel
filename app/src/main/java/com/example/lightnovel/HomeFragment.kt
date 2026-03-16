package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: TruyenViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TruyenViewModel::class.java)

        // ===== RecyclerView Truyện =====
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Initialize data if empty
        if (viewModel.products.value == null) {
            val list = listOf(
                Truyen(1,"Đấu la đại lục", "Oda", R.drawable.daula),
                Truyen(2,"Thần điêu đại hiệp", "Tác giả A", R.drawable.thandieu),
                Truyen(3,"One Piece", "Rumiko", R.drawable.img),
                Truyen(4,"Conan", "Tác giả B", R.drawable.banner1),
                Truyen(5,"Đấu la", "Oda", R.drawable.daula),
                Truyen(6,"Tomiya", "Tác giả A", R.drawable.thandieu),
                Truyen(7,"Inuyasha", "Rumiko", R.drawable.img),
                Truyen(8,"Luyện Sắc", "Tác giả B", R.drawable.banner1)
            )
            viewModel.setProducts(list)
        }

        viewModel.products.observe(viewLifecycleOwner) { list ->
            recyclerView.adapter = TruyenAdapter(list) { truyen ->
                viewModel.select(truyen)
                // Tạo bundle để gửi dữ liệu
                val bundle = Bundle()
                bundle.putInt("id", truyen.id)
                bundle.putString("title", truyen.title)
                bundle.putInt("image", truyen.imageRes)
                bundle.putString("author", truyen.author)

                // Tạo fragment mới
                val detailFragment = NovelDetailFragment()
                detailFragment.arguments = bundle

                // Mở trang mới
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
}
