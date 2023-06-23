package com.example.musicstats.fragments.spotify_fragments.trackFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicstats.R
import com.example.musicstats.adapters.spotifyTrackAdapter.SpotifyTrackAdapter
import com.example.musicstats.adapters.spotifyTrackAdapter.SpotifyTrackData
import com.example.musicstats.datamodels.RefreshToken

import com.example.musicstats.datamodels.SpotifyTrack
import com.example.musicstats.datamodels.SpotifyTrackModel
import com.example.musicstats.network.SpotifyApi
import com.example.musicstats.network.SpotifyAuthApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private var BASE_AUTH_URL = "https://accounts.spotify.com/"

@Suppress("NAME_SHADOWING")
class TracksHalfYearFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracks_half_year, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().applicationContext.getSharedPreferences(
            "tokenSharedPreferences",
            Context.MODE_PRIVATE
        )
        var accessToken = sharedPref.getString("accessToken", null)


        //check if accessToken exists or if it is expired
        if (accessToken != null) {
            Log.d("TRACKS", accessToken.toString())

            val timeStamp = sharedPref.getLong("timestamp", 0)
            val expiresIn = sharedPref.getInt("expiresIn", 0)
            val refreshToken = sharedPref.getString("refreshToken", " ") ?: ""

            val currentTime = System.currentTimeMillis() / 1000

            //checking logic with logger
            Log.d("TRACKS", "CURRENT TIME: $currentTime")
            Log.d("TRACKS", "TIMESTAMP: $timeStamp")
            Log.d("TRACKS", "REFRESH TOKEN: $refreshToken")
            Log.d("TRACKS", (currentTime - timeStamp).toString())


            if (currentTime - timeStamp > expiresIn.toLong()) {
                //token expired get new token and save it in SharedPreferences
                Log.d("TRACKS", "token expired! sending refresh token request")


                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_AUTH_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val spotifyAuthApi = retrofit.create(SpotifyAuthApi::class.java)

                val refreshTokenCall: Call<RefreshToken> = spotifyAuthApi
                    .getRefreshToken(refreshToken = refreshToken)


                refreshTokenCall.enqueue(object : Callback<RefreshToken> {
                    override fun onResponse(
                        call: Call<RefreshToken>,
                        response: Response<RefreshToken>
                    ) {
                        Log.d("TRACKS", "Refresh token success")
                        Log.d(
                            "TRACKS",
                            "New access token:" + response.body()?.accessToken.orEmpty()
                        )
                        //update the refreshed token in shared preferences

                        val sharedPrefEditor = sharedPref.edit()
                        accessToken = response.body()?.accessToken
                        sharedPrefEditor.putString(
                            "accessToken",
                            response.body()?.accessToken.toString()
                        )
                        sharedPrefEditor.putString("tokenType", response.body()?.tokenType)
                        sharedPrefEditor.putString("scope", response.body()?.scope)

                        val expiresIn = response.body()?.expiresIn

                        if (expiresIn != null) {
                            sharedPrefEditor.putInt("expiresIn", expiresIn)
                        }

                        val timestamp = System.currentTimeMillis() / 1000
                        sharedPrefEditor.putLong("timestamp", timestamp)
                        sharedPrefEditor.apply()

                        Log.d("TRACKS", "Successfully written!")
                    }

                    override fun onFailure(call: Call<RefreshToken>, t: Throwable) {
                        Log.d("TRACKS", "Refresh token failure")
                    }
                })

            }
        } else {
            Log.d("TRACKS", "fail")
        }

        //get data
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val spotifyApi = retrofit.create(SpotifyApi::class.java)

        if (accessToken != null) {
            Log.d("TRACKS", "in if branch")

            try {
                val monthlyTracksResponse: Call<SpotifyTrackModel> = spotifyApi.getTracks(
                    accessToken = "Bearer " + accessToken.toString(),
                    type = "tracks",
                    timeRange = "medium_term",
                    offset = 0
                )

                monthlyTracksResponse.enqueue(object : Callback<SpotifyTrackModel> {
                    override fun onResponse(
                        call: Call<SpotifyTrackModel>,
                        response: Response<SpotifyTrackModel>
                    ) {
                        val tracks: List<SpotifyTrack> = response.body()?.items ?: emptyList()


                        //getView()?.findViewById<TextView>(R.id.monthlyTracksText)?.text = "SUCCESS"
                        //make a recycler view with tracks: SpotifyTrackAdapter, track_list_item, SpotifyTrackData

                        val tracksForAdapter: ArrayList<SpotifyTrackData> = ArrayList()

                        for (track in tracks) {
                            val trackForAdapter = SpotifyTrackData(
                                trackTitleImage = track.album.images[0].url,
                                trackTitle = track.name,
                                trackArtist = track.artists[0].name,
                                trackAlbum = track.album.name,
                                trackRanking = "Ranking: " + (tracks.indexOf(track)+1).toString()
                            )
                            tracksForAdapter.add(trackForAdapter)
                            Log.d("TRACKS_6", trackForAdapter.toString())
                        }

                        try {
                            val recyclerView =
                                view.findViewById<RecyclerView>(R.id.recyclerViewHalfYearTracks)
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            recyclerView.hasFixedSize()

                            recyclerView.adapter = SpotifyTrackAdapter(tracksForAdapter)
                        } catch (e: Exception) {
                            Log.d("TRACKS", e.toString())
                        }


                    }

                    override fun onFailure(call: Call<SpotifyTrackModel>, t: Throwable) {
                        Log.d("TRACKS", "wrong call")
                    }
                })

            } catch (e: java.lang.Exception) {
                Log.d("TRACKS", e.toString())
            }

        }

    }

}