package com.example.musicstats

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.musicstats.adapters.trackRecommendationAdapter.TrackRecommendationAdapter
import com.example.musicstats.adapters.trackRecommendationAdapter.TrackRecommendationData
import com.example.musicstats.datamodels.*
import com.example.musicstats.network.SpotifyApi
import com.example.musicstats.network.SpotifyAuthApi
import com.google.android.material.imageview.ShapeableImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongFound : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_found)

        val title = intent.getStringExtra("title")
        val artist = intent.getStringExtra("artist")
        var imageUrl = intent.getStringExtra("imageUrl")

        val titleHolder = findViewById<TextView>(R.id.foundSongTitle)
        val artistHolder = findViewById<TextView>(R.id.foundSongArtist)
        val imageHolder = findViewById<ShapeableImageView>(R.id.foundSongImage)

        titleHolder.text = "Song name: " + title.toString()
        artistHolder.text = "Artist name: " + artist.toString()
        imageHolder.load(imageUrl)

         getTrackPlaybackAndReccomendations(title.toString(), artist.toString())
    }

    private fun getTrackPlaybackAndReccomendations(title: String, artist: String){
        val sharedPref = applicationContext.getSharedPreferences("tokenSharedPreferences", Context.MODE_PRIVATE)
        var accessToken = sharedPref.getString("accessToken", null)
        if (accessToken != null) {
            Log.d("LastFM", accessToken.toString())

            val timeStamp = sharedPref.getLong("timestamp", 0)
            val expiresIn = sharedPref.getInt("expiresIn", 0)
            val refreshToken = sharedPref.getString("refreshToken", " ") ?: ""

            val currentTime = System.currentTimeMillis() / 1000

            if (currentTime - timeStamp > expiresIn.toLong()) {
                //token expired get new token and save it in SharedPreferences
                Log.d("LastFM", "token expired! sending refresh token request")


                val retrofit = Retrofit.Builder()
                    .baseUrl("https://accounts.spotify.com/")
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

                        Log.d("LastFM", "Successfully written!")
                    }

                    override fun onFailure(call: Call<RefreshToken>, t: Throwable) {
                        Log.d("LastFm", "Refresh token failure")
                    }
                })

            }
        } else {
            Log.d("LastFm", "fail")
        }


        val spotifyApi = Retrofit.Builder()
            .baseUrl("https://api.spotify.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApi::class.java)

        val trackInfoResponse: Call<SearchTrackResponse> = spotifyApi.getTrack(
            accessToken = "Bearer " + accessToken,
            searchFilter = "artist:$artist track:$title"
        )

        trackInfoResponse.enqueue(object: Callback<SearchTrackResponse>{
            override fun onResponse(
                call: Call<SearchTrackResponse>,
                response: Response<SearchTrackResponse>
            ) {

                Log.d("SONGFOUND", "Success")
                try {
                    val id = response.body()?.tracks?.items?.get(0)?.id
                    val url = response.body()?.tracks?.items?.get(0)?.external_urls?.spotify
                    findViewById<Button>(R.id.foundSongListen).setOnClickListener {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        applicationContext.startActivity(i)
                    }

                    //get recommended songs and put them in recyclerviewer
                    val recommendations: Call<SpotifyRecommendationsResponse> =
                        spotifyApi.getSpotifyRecommendations(
                            accessToken = "Bearer " + accessToken,
                            trackIds = id.toString(),
                            seedGenres = "",
                            artistIds = ""
                        )

                    recommendations.enqueue(object : Callback<SpotifyRecommendationsResponse> {
                        override fun onResponse(
                            call: Call<SpotifyRecommendationsResponse>,
                            response: Response<SpotifyRecommendationsResponse>
                        ) {

                            val recyclerView =
                                findViewById<RecyclerView>(R.id.foundSongRecyclerView)
                            recyclerView.layoutManager = LinearLayoutManager(
                                applicationContext,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            val tracksForAdapter: ArrayList<TrackRecommendationData> = ArrayList()

                            //add tracks in arraylist
                            val recommendedTracks =
                                response.body()?.tracks as List<TrackRecommendation>

                            for (track in recommendedTracks) {
                                val tr = TrackRecommendationData(
                                    recommendedTrackId = track.id,
                                    recommendedTrackImage = track.album.images[0].url,
                                    recommendedTrackName = track.name,
                                    recommendedTrackArtist = track.artists[0].name,
                                    recommendedTrackDuration = "",
                                    recommendedTrackLink = ""
                                )
                                tracksForAdapter.add(tr)
                            }

                            recyclerView.adapter = TrackRecommendationAdapter(tracksForAdapter)
                            val adapter = recyclerView.adapter as TrackRecommendationAdapter

                            //update recycler viewer
                            for (track in adapter.trackList) {
                                val searchTrack: Call<SearchTrackResponse> = spotifyApi.getTrack(
                                    accessToken = "Bearer " + accessToken,
                                    searchFilter = "artist:${track.recommendedTrackArtist} track:${track.recommendedTrackName}"
                                )

                                searchTrack.enqueue(object : Callback<SearchTrackResponse> {
                                    override fun onResponse(
                                        call: Call<SearchTrackResponse>,
                                        response: Response<SearchTrackResponse>
                                    ) {
                                        try {
                                            val trackResponseSize =
                                                response?.body()?.tracks?.items?.size as Int
                                            if (trackResponseSize > 0) {
                                                val trackInfo =
                                                    response?.body()?.tracks?.items?.get(0) as TrackSearch
                                                val i = adapter.trackList.indexOf(track)
                                                track.recommendedTrackName = trackInfo.name
                                                track.recommendedTrackArtist =
                                                    trackInfo.artists[0].name
                                                track.recommendedTrackDuration =
                                                    trackInfo.album.release_date
                                                track.recommendedTrackLink =
                                                    trackInfo.external_urls.spotify
                                                adapter.notifyItemChanged(i)

                                            }
                                        } catch (e: java.lang.Exception) {
                                            Log.d("SONGFOUND", "ERROR")
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<SearchTrackResponse>,
                                        t: Throwable
                                    ) {
                                        Log.d("SONGFOUND", "Recommended info failed")
                                    }

                                })
                            }
                        }

                        override fun onFailure(
                            call: Call<SpotifyRecommendationsResponse>,
                            t: Throwable
                        ) {
                            Log.d("SONGFOUND", "Reccomendations failed")
                        }

                    })

                }catch (e: java.lang.Exception){
                    findViewById<Button>(R.id.foundSongListen).setOnClickListener {
                        Toast.makeText(applicationContext, "No playback available", Toast.LENGTH_SHORT).show()
                    }
                }
                }
                override fun onFailure(call: Call<SearchTrackResponse>, t: Throwable) {
                    Log.d("SONGFOUND", "Fail")
                }

        })
    }

}