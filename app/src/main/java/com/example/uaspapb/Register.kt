package com.example.uaspapb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspapb.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 */
class Register : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRegisterBinding

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        binding.btnRegister.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            val data = User(username = username, email = email, password = password, role = "user")
            if (username == null || email == null || password == null || confirmPassword == null){
                if (password != confirmPassword){
                    Toast.makeText(activity, "Gagal membuat akun, password konfirmasi tidak sama",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Gagal membuat akun, harap inputan diisikan semua.",
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                if (password != confirmPassword){
                    Toast.makeText(activity, "Gagal membuat akun, password konfirmasi tidak sama",
                        Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                Log.d("Halo", task.result.toString())
                                val user = auth.currentUser
                                val userId = user?.uid
                                if (userId != null) {
                                    firestore.collection("users").document(userId)
                                        .set(data)
                                        .addOnSuccessListener {
                                            val sharedPreferences = activity?.getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
                                            with(sharedPreferences!!.edit()) {
                                                putString("token", userId)
                                                putString("role", "user")
                                                commit()
                                            }
                                            val intentToDashboardUser = Intent(activity, DashboardUser::class.java)
                                            startActivity(intentToDashboardUser)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("Firestore", "Gagal menyimpan data pengguna", e)
                                        }
                                }
                            } else {
                                Toast.makeText(activity, "Gagal membuat akun.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(activity, "Gagal mendaftar: ${e.message}", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment Register.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Register().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}