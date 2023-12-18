package com.example.uaspapb

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import com.example.uaspapb.databinding.ActivityFormFilmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File

class FormFilm : AppCompatActivity() {
    private lateinit var binding: ActivityFormFilmBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    private var imageUri: Uri? = null
    private var imageLink = ""
    val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            showSelectedImage(imageUri)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFormFilmBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            val genreList = resources.getStringArray(R.array.genre)
            val genreAdapter = ArrayAdapter(this@FormFilm,
                android.R.layout.simple_spinner_dropdown_item,
                genreList)
            genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genre.adapter = genreAdapter
            if (intent.hasExtra("dataDetail")) {
                val dataDetail = intent.extras?.get("dataDetail") as Film
                Picasso.get().load(dataDetail.poster).into(binding.posterImage)
                binding.posterImage.visibility = View.VISIBLE
                binding.txtBtnPoster.text = "Ubah Poster"
                binding.namaFilm.setText(dataDetail.nama_film)
                binding.namaDirector.setText(dataDetail.nama_film)
                val initialValuePosition = genreAdapter.getPosition(dataDetail.genre)
                binding.genre.setSelection(initialValuePosition)
                binding.rate.setText(dataDetail.rate)
                binding.sinopsis.setText(dataDetail.desc)
                if (imageLink == null){
                    btnSave.setOnClickListener {
                        val film = binding.namaFilm.text.toString()
                        val director = binding.namaDirector.text.toString()
                        val genreFilm = binding.genre.selectedItem.toString()
                        val rateFilm = binding.rate.text.toString()
                        val sinopsisFilm = binding.sinopsis.text.toString()
                        val data = Film(nama_film = film, director = director, genre = genreFilm, rate = rateFilm, desc = sinopsisFilm)
                        updateFilm(data)
                    }
                } else {
                    btnSave.setOnClickListener {
                        addPoster(imageUri!!)
                    }
                }
            } else {
                btnSave.setOnClickListener {
                    addPoster(imageUri!!)
                }
            }
            btnBack.setOnClickListener {
                val intentToListFilm = Intent(this@FormFilm, DashboardAdmin::class.java)
                startActivity(intentToListFilm)
            }
            btnUploadImg.setOnClickListener{
                getContent.launch("image/*")
            }
        }
    }
    private fun addFilm(film: Film) {
        filmCollectionRef.add(film)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                film.id = createdBudgetId
                documentReference.set(film)
                    .addOnFailureListener {
                        Log.d("FormFilm", "Error updating film ID: ", it)
                    }
                val intentToAdmin = Intent(this, DashboardAdmin::class.java)
                startActivity(intentToAdmin)
            }
            .addOnFailureListener {
                Log.d("FormFilm", "Error adding budget: ", it)
            }
    }

    private fun addPoster(uri: Uri){
        imageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageLink = uri.toString()
                    val film = binding.namaFilm.text.toString()
                    val director = binding.namaDirector.text.toString()
                    val genreFilm = binding.genre.selectedItem.toString()
                    val rateFilm = binding.rate.text.toString()
                    val sinopsisFilm = binding.sinopsis.text.toString()
                    val data = Film(nama_film = film, director = director, genre = genreFilm, rate = rateFilm, desc = sinopsisFilm, poster = imageLink)
                    if (intent.hasExtra("dataDetail")){
                        val dataDetail = intent.extras?.get("dataDetail") as Film
                        data.id = dataDetail.id
                        updateFilm(data)
                    } else {
                        addFilm(data)
                    }
                    Log.d("HALO",uri.toString())
                    // Lakukan sesuatu dengan URL unduhan gambar
                }
            }
            .addOnFailureListener {
                Log.d("FormFilm", "Error adding poster: ", it)
            }
    }
    private fun showSelectedImage(uri: Uri?) {
        binding.posterImage.setImageURI(uri)
        binding.posterImage.visibility = View.VISIBLE
        binding.txtBtnPoster.text = "Ubah Poster"
    }
    private fun updateFilm(film: Film) {
        filmCollectionRef.document(film.id).set(film)
            .addOnCompleteListener {
                Log.d("Edit Data", "cek")
                val intentToAdmin = Intent(this, DashboardAdmin::class.java)
                startActivity(intentToAdmin)
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error updating budget: ", it)
            }
    }
}