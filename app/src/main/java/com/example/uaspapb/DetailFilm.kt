package com.example.uaspapb

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.uaspapb.databinding.ActivityDetailFilmBinding
import com.example.uaspapb.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailFilm : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFilmBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailFilmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            if (intent.hasExtra("id")) {
                val idFilm = intent.extras?.getString("id")
                getData(idFilm!!)
            }
            btnBack.setOnClickListener {
                val intentToDashboard = Intent(this@DetailFilm, DashboardUser::class.java)
                startActivity(intentToDashboard)
            }
        }
    }
    private fun getData(id: String){
        val docRef = filmCollectionRef.document(id)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val desc = documentSnapshot.getString("desc")
                    val director = documentSnapshot.getString("director")
                    val poster = documentSnapshot.getString("poster")
                    val rate = documentSnapshot.getString("rate")
                    val genre = documentSnapshot.getString("genre")
                    val nama_film = documentSnapshot.getString("nama_film")
                    binding.genre.text = genre
                    binding.rate.text = rate
                    binding.namaDirector.text = director
                    binding.judul.text = nama_film
                    binding.desc.text = desc
                    Picasso.get().load(poster).into(binding.posterImage)
                } else {

                }
            }
            .addOnFailureListener { exception ->
                Log.d("HELLO", exception.toString())
            }
    }
}