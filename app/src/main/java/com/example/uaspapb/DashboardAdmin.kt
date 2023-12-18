package com.example.uaspapb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspapb.databinding.ActivityDashboardAdminBinding
import com.example.uaspapb.databinding.ActivityDashboardUserBinding
import com.example.uaspapb.databinding.FragmentFilmBinding
import com.google.firebase.firestore.FirebaseFirestore

class DashboardAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")
    private val filmListLiveData: MutableLiveData<List<Film>> by lazy {
        MutableLiveData<List<Film>>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            btnAdd.setOnClickListener {
                val intentToFormFilm = Intent(this@DashboardAdmin, FormFilm::class.java)
                startActivity(intentToFormFilm)
            }
        }
        observeFilms()
        getAllFilms()
    }
    private fun getAllFilms() {
        observeFilmsChanges()
    }
    private fun observeFilms() {
        filmListLiveData.observe(this) { film ->
            val adapterFilm = FilmAdapter(film.toMutableList()) { film ->
                Toast.makeText(this, "You clicked on ${film.nama_film} ", Toast.LENGTH_SHORT).show()
            }
            binding.rvFilm.apply {
                adapter = adapterFilm
            }
        }
    }
    private fun observeFilmsChanges() {
        filmCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val budgets = snapshots?.toObjects(Film::class.java)
            if (budgets != null) {
                filmListLiveData.postValue(budgets)
            }
        }
    }
}