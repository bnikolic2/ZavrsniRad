package com.example.musicstats.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.musicstats.R
import com.example.musicstats.fragments.spotify_fragments.FragmentSpotifyArtists
import com.example.musicstats.fragments.spotify_fragments.FragmentSpotifyTracks


class SpotifyFragment : Fragment() {
    private var isTracksFragmentActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spotify, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwitchingFragments(view)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    private fun setupSwitchingFragments(view: View) {

        val trackFragment = FragmentSpotifyTracks()
        val artistsFragment = FragmentSpotifyArtists()


        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.spotifyFragmentChildFragmentContainer, trackFragment)
        transaction.add(R.id.spotifyFragmentChildFragmentContainer, artistsFragment)
        transaction.hide(artistsFragment)
        transaction.show(trackFragment)
        transaction.commit()

        val switchFragmentsButton = view.findViewById<Button>(R.id.spotifySwitchTracksArtists)
        switchFragmentsButton.setOnClickListener {


            if (isTracksFragmentActive) {
                childFragmentManager.beginTransaction().apply {
                    hide(trackFragment)
                    show(artistsFragment)
                    switchFragmentsButton.setText(R.string.tracks)
                    commit()
                }
                isTracksFragmentActive = false
            } else {
                childFragmentManager.beginTransaction().apply {
                    hide(artistsFragment)
                    show(trackFragment)
                    switchFragmentsButton.setText(R.string.artists)
                    commit()
                }
                isTracksFragmentActive = true
            }

        }
    }

}