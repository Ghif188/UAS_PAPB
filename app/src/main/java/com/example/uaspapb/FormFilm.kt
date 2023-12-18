package com.example.uaspapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.uaspapb.databinding.ActivityFormFilmBinding
import com.google.firebase.firestore.FirebaseFirestore

class FormFilm : AppCompatActivity() {
    private lateinit var binding: ActivityFormFilmBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("film")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFormFilmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val genreList = resources.getStringArray(com.example.uaspapb.R.array.genre)

        with(binding){
            val genreAdapter = ArrayAdapter(this@FormFilm,
                android.R.layout.simple_spinner_dropdown_item,
                genreList)
            genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genre.adapter = genreAdapter

            btnSave.setOnClickListener {
                val film = namaFilm.text.toString()
                val director = namaDirector.text.toString()
                val genreFilm = genre.selectedItem.toString()
                val rateFilm = rate.text.toString()
                val sinopsisFilm = sinopsis.text.toString()
                val data = Film(nama_film = film, director = director, genre = genreFilm, rate = rateFilm, desc = sinopsisFilm)
                addFilm(data)
            }
        }
    }
    private fun addFilm(film: Film) {
        budgetCollectionRef.add(film)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                film.id = createdBudgetId
                documentReference.set(film)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating budget ID: ", it)
                    }
                val intentToAdmin = Intent(this, DashboardAdmin::class.java)
                startActivity(intentToAdmin)
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding budget: ", it)
            }
    }
}