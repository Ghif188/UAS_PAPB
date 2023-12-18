package com.example.uaspapb

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uaspapb.databinding.ListFilmBinding

typealias OnClickDisaster = (Film) -> Unit
class FilmAdapter(private val listDisaster: List<Film>, private val onClickDisaster: OnClickDisaster) :
    RecyclerView.Adapter<FilmAdapter.ItemDisasterViewHolder>() {
    inner class ItemDisasterViewHolder(private val binding: ListFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Film) {
            with(binding) {
                namaFilm.text = data.nama_film
                namaDirector.text = data.director
                genre.text = data.genre
                rate.text = data.rate
                itemView.setOnClickListener {
                    onClickDisaster(data)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ItemDisasterViewHolder {
        val binding = ListFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemDisasterViewHolder(binding)
    }
    override fun getItemCount(): Int = listDisaster.size
    override fun onBindViewHolder(holder: ItemDisasterViewHolder, position: Int) {
        holder.bind(listDisaster[position])
    }
}
