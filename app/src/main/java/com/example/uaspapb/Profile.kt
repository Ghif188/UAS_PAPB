package com.example.uaspapb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspapb.databinding.FragmentProfileBinding
import com.example.uaspapb.databinding.ListFilmUserBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentProfileBinding
    private var db = FirebaseFirestore.getInstance().collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        getData()
        binding.btnLogout.setOnClickListener {
            val intentToLogin = Intent(activity, MainActivity::class.java)
            val sharedPreferences = activity?.getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
            with(sharedPreferences!!.edit()) {
                putString("token", null)
                putString("role", null)
                commit()
            }
            val sharedPref = context?.getSharedPreferences("account_data", Context.MODE_PRIVATE)
            val token = sharedPref?.getString("token", "")
            Log.d("Profile", token.toString())
            startActivity(intentToLogin)
        }
        return binding.root
    }

    private fun getData(){
        val sharedPref = context?.getSharedPreferences("account_data", Context.MODE_PRIVATE)
        val token = sharedPref?.getString("token", "")
        if (token != ""){
            val docRef = db.document(token!!)
            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val email = documentSnapshot.getString("email")
                        val username = documentSnapshot.getString("username")
                        binding.email.text = email
                        binding.username.text = username
                    } else {
                        // Dokumen tidak ditemukan
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("HELLO", exception.toString())
                }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}