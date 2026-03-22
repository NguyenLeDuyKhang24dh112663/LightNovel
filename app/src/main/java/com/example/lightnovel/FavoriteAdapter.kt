package com.example.lightnovel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter (
    private val context: Context,
    private val favs: List<Truyen>
): RecyclerView.Adapter<FavoriteAdapter.FavoritesVH>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent,false)
        return FavoritesVH(v)
    }

    override fun onBindViewHolder(
        holder: FavoritesVH,
        position: Int
    ) {
        val truyen = favs[position]
        holder.imgFavNovel.setImageResource(truyen.imageRes)
        holder.tvName.text = truyen.title
        holder.tvAuthor.text = truyen.author

        // Enable marquee scrolling
        holder.tvName.isSelected = true
        holder.tvAuthor.isSelected = true

        holder.btnRead.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", truyen.id)
            bundle.putString("title", truyen.title)
            bundle.putInt("image", truyen.imageRes)
            bundle.putString("author", truyen.author)
            bundle.putString("description", truyen.description)

            val detailFragment = NovelDetailFragment()
            detailFragment.arguments = bundle

            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.flSectionsLayout, detailFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = favs.size

    inner class FavoritesVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val imgFavNovel: ImageView = itemView.findViewById(R.id.imgFavNovel)
        val tvName: TextView = itemView.findViewById(R.id.tvNovelName)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthorName)
        val btnRead: Button = itemView.findViewById(R.id.btnRead)
    }
}
