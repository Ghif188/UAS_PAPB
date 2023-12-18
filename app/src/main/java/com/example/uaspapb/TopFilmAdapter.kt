package com.example.uaspapb

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.uaspapb.databinding.ListFilmBinding
import com.example.uaspapb.databinding.ListTopFilmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.Serializable

class TopFilmAdapter(private val context: Context, private val listDisaster: List<Film>, private val onClickDisaster: OnClickDisaster) :
    RecyclerView.Adapter<TopFilmAdapter.ItemDisasterViewHolder>() {
    inner class ItemDisasterViewHolder(private val binding: ListTopFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Film) {
            with(binding) {
                if (data.poster.isNotEmpty()){
                    Picasso.get().load(data.poster).into(posterImage)
                }
                namaFilm.text = data.nama_film
                genre.text = data.genre
                rate.text = data.rate
                itemView.setOnClickListener{
                    onClickDisaster(data)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ItemDisasterViewHolder {
        val binding = ListTopFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemDisasterViewHolder(binding)
    }
    override fun getItemCount(): Int = listDisaster.size
    override fun onBindViewHolder(holder: ItemDisasterViewHolder, position: Int) {
        holder.bind(listDisaster[position])
    }
}
