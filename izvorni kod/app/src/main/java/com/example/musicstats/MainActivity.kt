package com.example.musicstats

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.musicstats.databinding.ActivityMainBinding
import com.example.musicstats.fragments.LastFmFragment
import com.example.musicstats.fragments.ShazamFragment
import com.example.musicstats.fragments.SpotifyFragment
import com.example.musicstats.fragments.spotify_fragments.FragmentAuthorize
import com.google.android.material.bottomnavigation.BottomNavigationView




class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var sharedPref = this?.applicationContext?.getSharedPreferences("tokenSharedPreferences", Context.MODE_PRIVATE)
        var accessTokenSpotify = sharedPref?.getString("accessToken", null)

        Log.d("RECREATING CALL", accessTokenSpotify.toString())
        if(accessTokenSpotify == null) {
            loadFragment(FragmentAuthorize())
            bottomNav = findViewById(R.id.bottomNav)
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.spotify -> {
                        loadFragment(FragmentAuthorize())
                        true
                    }
                    R.id.lastfm -> {
                        loadFragment(FragmentAuthorize())
                        true
                    }
                    R.id.shazam -> {
                        loadFragment(FragmentAuthorize())
                        true
                    }
                    else -> true
                }
            }
        }else{
            loadFragment(SpotifyFragment())
            bottomNav = findViewById(R.id.bottomNav)
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.spotify -> {
                        loadFragment(SpotifyFragment())
                        true
                    }
                    R.id.lastfm -> {
                        loadFragment(LastFmFragment())
                        true
                    }
                    R.id.shazam -> {
                        loadFragment(ShazamFragment())
                        true
                    }
                    else -> true
                }
            }
        }
    }


    //makes Spotify fragment in bottom navigation the default view in application
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }



}