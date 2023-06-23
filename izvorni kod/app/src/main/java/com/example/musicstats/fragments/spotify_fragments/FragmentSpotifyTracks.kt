package com.example.musicstats.fragments.spotify_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.musicstats.R
import com.example.musicstats.fragments.spotify_fragments.trackFragments.AnnualTracksFragment
import com.example.musicstats.fragments.spotify_fragments.trackFragments.MonthlyTracksFragment
import com.example.musicstats.fragments.spotify_fragments.trackFragments.TracksHalfYearFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class FragmentSpotifyTracks : Fragment() {

    private inner class MyPagerAdapter(fm: FragmentManager, lc: Lifecycle) :
        FragmentStateAdapter(fm, lc) {

        override fun getItemCount(): Int {
            return 3 // replace with the number of pages in your ViewPager
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MonthlyTracksFragment()
                1 -> TracksHalfYearFragment()
                2 -> AnnualTracksFragment()
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
        val view = inflater.inflate(R.layout.fragment_spotify_tracks, container, false)
        val viewPager: ViewPager2 = view.findViewById(R.id.trackViewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.spotifyTracksTabLayout)
        viewPager.adapter = MyPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "Month"
                1 -> tab.text = "6 Months"
                2 -> tab.text = "Year"
            }
        }.attach()
        return view
    }

}