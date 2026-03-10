package com.example.lightnovel

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter (
    private val context: Context,
    private val favs: List<Favorites>
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
        val fav = favs[position]
        holder.imgFavNovel.setImageResource(fav.novelImg)
        holder.tvName.text=fav.name
        holder.tvAuthor.text=fav.author

        // Enable marquee scrolling
        holder.tvName.isSelected = true
        holder.tvAuthor.isSelected = true

        holder.btnRead.setOnClickListener {
            val intent = Intent(context, NovelDetailFragment::class.java)
            intent.putExtra("name", fav.name)
            context.startActivity(intent)
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