package com.example.uaspapb

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uaspapb.databinding.ListFilmUserBinding
import com.example.uaspapb.databinding.ListTopFilmBinding
import com.squareup.picasso.Picasso

class FilmUserAdapter(private val context: Context, private val listDisaster: List<Film>, private val onClickDisaster: OnClickDisaster) :
    RecyclerView.Adapter<FilmUserAdapter.ItemDisasterViewHolder>() {
    inner class ItemDisasterViewHolder(private val binding: ListFilmUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Film) {
            with(binding) {
                if (data.poster.isNotEmpty()){
                    Picasso.get().load(data.poster).into(posterImage)
                }
                namaFilm.text = data.nama_film
                genre.text = data.genre
                rate.text = data.rate
                namaDirector.text = data.director
                itemView.setOnClickListener{
                    onClickDisaster(data)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ItemDisasterViewHolder {
        val binding = ListFilmUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemDisasterViewHolder(binding)
    }
    override fun getItemCount(): Int = listDisaster.size
    override fun onBindViewHolder(holder: ItemDisasterViewHolder, position: Int) {
        holder.bind(listDisaster[position])
    }
}
