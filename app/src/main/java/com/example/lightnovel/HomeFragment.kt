package com.example.lightnovel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== RecyclerView Truyện =====
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val list = listOf(
            Truyen("Đấu la đại lục", "Oda", R.drawable.daula),
            Truyen("Thần điêu đại hiệp", "Tác giả A", R.drawable.thandieu),
            Truyen("One Piece", "Rumiko", R.drawable.img),
            Truyen("Conan", "Tác giả B", R.drawable.banner1),
            Truyen("Đấu la", "Oda", R.drawable.daula),
            Truyen("Tomiya", "Tác giả A", R.drawable.thandieu),
            Truyen("Inuyasha", "Rumiko", R.drawable.img),
            Truyen("Luyện Sắc", "Tác giả B", R.drawable.banner1)
        )

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = TruyenAdapter(list) { truyen ->
            // Tạo bundle để gửi dữ liệu
            val bundle = Bundle()
            bundle.putString("title", truyen.title)
            bundle.putInt("image", truyen.image)
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