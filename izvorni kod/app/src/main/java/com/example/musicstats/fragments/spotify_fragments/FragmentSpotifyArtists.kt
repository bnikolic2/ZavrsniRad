package com.example.musicstats.fragments.spotify_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.musicstats.R
import com.example.musicstats.fragments.spotify_fragments.artistFragment.AnnualTopArtistsFragment
import com.example.musicstats.fragments.spotify_fragments.artistFragment.HalfYearTopArtistsFragment
import com.example.musicstats.fragments.spotify_fragments.artistFragment.MonthTopArtistsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class FragmentSpotifyArtists : Fragment() {

    private inner class MyPagerAdapter(fm: FragmentManager, lc: Lifecycle ): FragmentStateAdapter(fm, lc){
        override fun getItemCount(): Int {
            return 3 //number of pages in ViewPager
        }

        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> MonthTopArtistsFragment()
                1 -> HalfYearTopArtistsFragment()
                2 -> AnnualTopArtistsFragment()
                else -> throw IllegalArgumentException("Invalid position $position")
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_spotify_artists, container,false)
        val viewPager: ViewPager2 = view.findViewById(R.id.artistViewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.spotifyArtistsTabLayout)

        viewPager.adapter = MyPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager){tab, position ->
            when(position){
                0 -> tab.text = "Month"
                1 -> tab.text = " 6 Months"
                2 -> tab.text = "Year"
            }
        }.attach()

        return view
    }

}