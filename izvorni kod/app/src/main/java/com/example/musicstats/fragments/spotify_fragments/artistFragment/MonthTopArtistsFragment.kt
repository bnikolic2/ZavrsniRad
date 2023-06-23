package com.example.musicstats.fragments.spotify_fragments.artistFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicstats.R
import com.example.musicstats.adapters.spotifyArtistAdapter.SpotifyArtistAdapter
import com.example.musicstats.adapters.spotifyArtistAdapter.SpotifyArtistData
import com.example.musicstats.datamodels.Artist
import com.example.musicstats.datamodels.ArtistSearch
import com.example.musicstats.datamodels.RefreshToken
import com.example.musicstats.datamodels.SpotifyArtistModel
import com.example.musicstats.network.SpotifyApi
import com.example.musicstats.network.SpotifyAuthApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private var BASE_AUTH_URL = "https://accounts.spotify.com/"

@Suppress("NAME_SHADOWING")
class MonthTopArtistsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_month_top_artists, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().applicationContext.getSharedPreferences(
            "tokenSharedPreferences",
            Context.MODE_PRIVATE
        )
        var accessToken = sharedPref.getString("accessToken", null)

        //check if accessTokenExpired, refresh token accordingly
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
            Log.d("ARTISTS", "fail")
        }

        val spotifyApi = Retrofit.Builder()
            .baseUrl("https://api.spotify.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApi::class.java)

        if(accessToken != null){
            Log.d("ARTISTS", "in if branch")

            try{

                val artistResponse: Call<SpotifyArtistModel> = spotifyApi.getArtists(
                    accessToken = "Bearer " + accessToken.toString(),
                    type = "artists",
                    timeRange = "short_term",
                    offset = 0
                )

                artistResponse.enqueue(object: Callback<SpotifyArtistModel>{
                    override fun onResponse(
                        call: Call<SpotifyArtistModel>,
                        response: Response<SpotifyArtistModel>
                    ) {
                        val artists: List<Artist> = response.body()?.items ?: emptyList()
                        //make a recycler view with tracks: SpotifyArtistAdapter, artist_list_item, SpotifyArtistData

                        val artistsForAdapter: ArrayList<SpotifyArtistData> = ArrayList()

                        for (artist in artists){

                            val artistForAdapter = SpotifyArtistData(
                                artistImage = artist.images[0].url,
                                artistName = artist.name,
                                artistGenres = "Genres: ${artist.genres.joinToString(separator = ", ")}",
                                artistPopularity = "Popularity: " + artist.popularity.toString(),
                                artistRanking = "Ranking: " + (artists.indexOf(artist)+1).toString()
                            )
                            artistsForAdapter.add(artistForAdapter)
                            Log.d("ARTIST", artistForAdapter.toString())
                        }

                        try{
                            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMonthArtists)
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            recyclerView.hasFixedSize()

                            recyclerView.adapter = SpotifyArtistAdapter(artistsForAdapter)
                        }catch(e: java.lang.Exception){
                            Log.d("ARTIST", e.toString())
                        }

                    }

                    override fun onFailure(call: Call<SpotifyArtistModel>, t: Throwable) {
                        Log.d("ARTISTS", "wrong call")
                    }

                })


            }catch(e:java.lang.Exception){
                Log.d("ARTISTS", e.toString())
            }
        }

    }

}