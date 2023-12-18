package com.example.uaspapb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.uaspapb.databinding.ActivityDashboardAdminBinding
import com.example.uaspapb.databinding.FragmentFilmBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilmFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentFilmBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")
    private val filmListLiveData: MutableLiveData<List<Film>> by lazy {
        MutableLiveData<List<Film>>()
    }
    private val topFilmListLiveData: MutableLiveData<List<Film>> by lazy {
        MutableLiveData<List<Film>>()
    }
    private lateinit var roomFilmDao: FilmDao
    private lateinit var roomFilmDatabase: FilmRoomDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        roomFilmDatabase = FilmRoomDatabase.getDatabase(requireContext())!!
        roomFilmDao = roomFilmDatabase.filmDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilmBinding.inflate(inflater, container, false)
        val genreList = resources.getStringArray(R.array.genre)
        val genreWithDefault = mutableListOf("Semua Genre")
        genreWithDefault.addAll(genreList)
        val genreAdapter = ArrayAdapter(requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            genreWithDefault)
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genre.adapter = genreAdapter
        binding.genre.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Aksi yang akan diambil ketika item terpilih berubah
                val selectedItem = genreWithDefault[position]
                searchFilm(selectedItem)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Metode ini diimplementasikan ketika tidak ada item yang dipilih (jarang digunakan)
            }
        }
        observeFilms()
        getAllFilms()
        return binding.root
    }
    private fun searchFilm(genre: String){
        Log.d("HELLO", genre)
        if (genre == "Semua Genre"){
            observeFilmsChanges()
        }else{
            filmCollectionRef.whereEqualTo("genre", genre)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }
                    val films = querySnapshot?.toObjects(Film::class.java)
                    if (films != null) {
                        filmListLiveData.postValue(films)
                    }
                }
        }
    }

    private fun getAllFilms() {
        observeFilmsChanges()
        topFilm()
    }
    private fun observeFilms() {
        filmListLiveData.observe(requireActivity()) { film ->
            val adapterFilm = FilmUserAdapter(requireActivity(), film.toMutableList()) { film ->
                val intentToDetailFilm = Intent(activity, DetailFilm::class.java)
                intentToDetailFilm.putExtra("id", film.id)
                startActivity(intentToDetailFilm)
            }
            binding.rvFilm.apply {
                adapter = adapterFilm
                layoutManager = GridLayoutManager(requireActivity(), 2)
            }
        }
        topFilmListLiveData.observe(requireActivity()) { film ->
            val adapterFilm = TopFilmAdapter(requireActivity(), film.toMutableList()) { film ->
                val intentToDetailFilm = Intent(activity, DetailFilm::class.java)
                intentToDetailFilm.putExtra("id", film.id)
                startActivity(intentToDetailFilm)
            }
            Log.d("filmtop", film.toString())
            binding.rvTopFilm.apply {
                adapter = adapterFilm
                layoutManager = GridLayoutManager(requireActivity(), 1)
            }
        }
    }
    private fun observeFilmsChanges() {
        filmCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val films = snapshots?.toObjects(Film::class.java)
            if (films != null) {
                filmListLiveData.postValue(films)
                saveMoviesToRoom(films)
            }
        }
    }
    private fun topFilm(){
        filmCollectionRef.orderBy("rate", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val budgets = querySnapshot?.toObjects(Film::class.java)
                if (budgets != null) {
                    topFilmListLiveData.postValue(budgets)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", exception.toString())
            }
    }
    private fun saveMoviesToRoom(roomMovies: List<Film>) {
        // Simpan data ke Room
        lifecycleScope.launch(Dispatchers.IO) {
            // Hapus semua data di Room terlebih dahulu
            roomFilmDao.deleteAll()
            roomFilmDao.insertAll(roomMovies)

            // convert list movie room ke list movie
            var roomMoviesGet:List<Film> = roomFilmDao.getAllFilm()
            Log.d("HALO", "$roomMoviesGet")
            filmListLiveData.postValue(roomMoviesGet)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Film.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}