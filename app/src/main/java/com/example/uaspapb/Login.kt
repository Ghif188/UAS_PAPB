package com.example.uaspapb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.uaspapb.databinding.FragmentLoginBinding
import com.example.uaspapb.databinding.FragmentRegisterBinding
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLoginBinding

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance().collection("users")
        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email == "" || password == ""){
                if (email == ""){
                    binding.txtEmail.visibility = View.VISIBLE
                } else {
                    binding.txtEmail.visibility = View.GONE
                }
                if (password == ""){
                    binding.txtPassword.visibility = View.VISIBLE
                } else {
                    binding.txtPassword.visibility = View.GONE
                }
            } else {
                if (email != ""){
                    binding.txtEmail.visibility = View.GONE
                }
                if (password != ""){
                    binding.txtPassword.visibility = View.GONE
                }
                auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login berhasil
                            val user = auth.currentUser
                            val docRef = db.document(user!!.uid)
                            docRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        // Dokumen ditemukan
                                        val role = documentSnapshot.getString("role")
                                        if (role == "admin"){
                                            val intentToAdmin = Intent(activity, DashboardAdmin::class.java)
                                            startActivity(intentToAdmin)
                                        } else {
                                            val intentToUser = Intent(activity, DashboardUser::class.java)
                                            startActivity(intentToUser)
                                        }
                                        // Lakukan sesuatu dengan nilai kolom
                                    } else {
                                        // Dokumen tidak ditemukan
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Gagal mengambil data
                                    // Handle kesalahan di sini
                                }
                            // Lanjutkan dengan tindakan setelah login berhasil, misalnya pindah ke layar utama
                        } else {
                            // Gagal login
                            Toast.makeText(activity, "Gagal login. Periksa kembali email dan password Anda.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}