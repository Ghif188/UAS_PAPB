package com.example.uaspapb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.uaspapb.databinding.ActivityDashboardUserBinding

class DashboardUser : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(FilmFragment())

        binding.bottomNavbar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_film->replaceFragment(FilmFragment())
                R.id.nav_favorite->replaceFragment(Favorite())
                R.id.nav_profile->replaceFragment(Profile())
                else->{}
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}