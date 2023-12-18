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
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.Serializable

typealias OnClickDisaster = (Film) -> Unit
class FilmAdapter(private val context: Context, private val listDisaster: List<Film>, private val onClickDisaster: OnClickDisaster) :
    RecyclerView.Adapter<FilmAdapter.ItemDisasterViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val filmCollectionRef = firestore.collection("film")
    inner class ItemDisasterViewHolder(private val binding: ListFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Film) {
            with(binding) {
                if (data.poster.isNotEmpty()){
                    Picasso.get().load(data.poster).into(image)
                }
                namaFilm.text = data.nama_film
                namaDirector.text = data.director
                genre.text = data.genre
                rate.text = data.rate
                btnEdit.setOnClickListener {
                    val dataDetail = Film(data.id, data.nama_film, data.genre, data.desc, data.director, data.rate, data.poster)
                    val intentToFormFilm = Intent(context, FormFilm::class.java)
                    intentToFormFilm.putExtra("dataDetail", dataDetail as Serializable)
                    context.startActivity(intentToFormFilm)
                }
                btnDelete.setOnClickListener {
                    popupHapus(data)
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
    private fun popupHapus(film: Film) {
        val dialog = Dialog(context)
        val inflaterDialog = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.logout_popup)
        val window = dialog.window
        val layoutParams = WindowManager.LayoutParams()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams
        dialog.show()
        val btnBatal = dialog.findViewById<TextView>(R.id.btn_batal_hapus)
        val btnClose = dialog.findViewById<TextView>(R.id.btn_close_dialog)
        val btnHapus = dialog.findViewById<TextView>(R.id.btn_hapus)
        btnClose.setOnClickListener {
            dialog.cancel()
        }
        btnBatal.setOnClickListener {
            dialog.cancel()
        }
        btnHapus.setOnClickListener {
            deleteBudget(film)
            dialog.cancel()
        }
    }
    private fun deleteBudget(film: Film) {
        if (film.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: budget ID is empty!")
            return
        }
        filmCollectionRef.document(film.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting budget: ", it)
            }
    }
}
