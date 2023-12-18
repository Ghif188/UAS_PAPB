package com.example.uaspapb

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
    private val channelId = "TEST_NOTIFIKASI_2023"
    private val notifId = 909

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
        val sharedPreferences = requireActivity().getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
        val tokenFromPrefs = sharedPreferences.getString("token", null)
        val roleFromPrefs = sharedPreferences.getString("role", null)
        if (tokenFromPrefs != null && roleFromPrefs != null){
            if (roleFromPrefs == "admin"){
                val intentToAdmin = Intent(activity, DashboardAdmin::class.java)
                startActivity(intentToAdmin)
            } else {
                val intentToUser = Intent(activity, DashboardUser::class.java)
                startActivity(intentToUser)
            }
        }
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
                            val user = auth.currentUser
                            val docRef = db.document(user!!.uid)
                            docRef.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val role = documentSnapshot.getString("role")
                                        val username = documentSnapshot.getString("username")
                                        if (role == "admin"){
                                            val intentToAdmin = Intent(activity, DashboardAdmin::class.java)
                                            startActivity(intentToAdmin)
                                        } else {
                                            val intentToUser = Intent(activity, DashboardUser::class.java)
                                            startActivity(intentToUser)
                                        }
                                        val sharedPreferences = activity?.getSharedPreferences("account_data", AppCompatActivity.MODE_PRIVATE)
                                        with(sharedPreferences!!.edit()) {
                                            putString("token", user.uid)
                                            putString("role", role)
                                            commit()
                                        }
                                        notifikasi(username!!)
                                    } else {

                                    }
                                }
                                .addOnFailureListener { exception ->
                                }
                        } else {
                            Toast.makeText(activity, "Gagal login. Periksa kembali email dan password Anda.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        return binding.root
    }
    private fun notifikasi(username: String){
        val notifImage = BitmapFactory.decodeResource(resources, R.drawable.ic_logo)
        val notifManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        }
        else {
            0
        }
        val intentToLogin = Intent(activity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            activity,
            0,
            intentToLogin,
            flag
        )
        val builder = NotificationCompat.Builder(requireActivity(), channelId)
            .setSmallIcon(R.drawable.ic_back)
            .setContentTitle("Movee")
            .setContentText("Selamat Datang ${username}, Silahkan klik notifikasi ini untuk menjalajahi aplikasi MOVEE..") // Isi pesan bebas
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(notifImage)
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(
                channelId,
                "Movee",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            with(notifManager) {
                createNotificationChannel(notifChannel)
                notify(notifId, builder.build())
            }
        }
        else {
            notifManager.notify(notifId, builder.build())
        }
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