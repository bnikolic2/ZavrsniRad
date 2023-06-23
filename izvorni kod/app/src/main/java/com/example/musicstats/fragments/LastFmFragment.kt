@file:Suppress("NAME_SHADOWING")

package com.example.musicstats.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicstats.R
import com.example.musicstats.adapters.trackRecommendationAdapter.TrackRecommendationAdapter
import com.example.musicstats.adapters.trackRecommendationAdapter.TrackRecommendationData
import com.example.musicstats.datamodels.*
import com.example.musicstats.network.LastFmApi
import com.example.musicstats.network.SpotifyApi
import com.example.musicstats.network.SpotifyAuthApi
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random


class LastFmFragment : Fragment() {

    private var numberOfTries = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lastfm, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //getSimilarTracks("Maroon", "Taylor Swift")
        getSavedTracksForUser(context as Context, object : SavedTracksCallback {
            override fun onSuccess(tracks: List<SpotifyShowItem>) {
                for (track in tracks) {
                    Log.d(
                        "LIKED SONGS IN LIBRARY",
                        track.track.name + "-" + track.track.artists[0].name
                    )
                    getSimilarTracks(track.track.name, track.track.artists[0].name,
                        object : SimilarTracksCallback {
                            override fun onSuccess(
                                basedOnTrackName: String,
                                basedOnArtistName: String,
                                tracks: MutableList<Track>
                            ) {
                                val trackName = track.track.name
                                val artistName = track.track.artists[0].name

                                Log.d(
                                    "Similar Tracks",
                                    track.track.name + " - " + track.track.artists[0].name
                                )

                                //LastFm tracks put in the spotify recommendation
                                for (track in tracks) {
                                    Log.d("Similar Tracks", track.name + " - " + track.artist.name)
                                    //calling spotify recommendations
                                    getSpotifyRecommendations(
                                        basedOnTrackName,
                                        basedOnArtistName,
                                        track,
                                        requireContext(),
                                        object : SpotifyRecommendationsCallback {
                                            override fun onSuccess(
                                                basedOnTrackName: String,
                                                basedOnArtistName: String,
                                                tracks: List<TrackRecommendation>
                                            ) {
                                                Log.d(
                                                    "Success spotify recom",
                                                    "--------------------------------------------"
                                                )
                                                Log.d(
                                                    "Success spotify recom",
                                                    "$basedOnTrackName --- $basedOnArtistName"
                                                )

                                                //make a recycler viewer
                                                val number = Random.nextInt(1, 4)

                                                val textView = when (number) {
                                                    1 -> view.findViewById<TextView>(R.id.textViewRec1)
                                                    2 -> view.findViewById<TextView>(R.id.textViewRec2)
                                                    3 -> view.findViewById<TextView>(R.id.textViewRec3)
                                                    else -> view.findViewById<TextView>(R.id.textViewRec1)
                                                }

                                                val recyclerView = when (number) {
                                                    1 -> view.findViewById<RecyclerView>(R.id.recyclerViewRec1)
                                                    2 -> view.findViewById<RecyclerView>(R.id.recyclerViewRec2)
                                                    3 -> view.findViewById<RecyclerView>(R.id.recyclerViewRec3)
                                                    else -> view.findViewById<RecyclerView>(R.id.recyclerViewRec1)
                                                }
                                                val textView1 =
                                                    view.findViewById<TextView>(R.id.textViewRec1)
                                                val textView2 =
                                                    view.findViewById<TextView>(R.id.textViewRec2)
                                                val textView3 =
                                                    view.findViewById<TextView>(R.id.textViewRec3)

                                                if (textView1.text == "Loading..."
                                                    || textView2.text == "Loading..."
                                                    || textView3.text == "Loading..."
                                                ) {
                                                    if (textView1.text != "Based on: ${basedOnTrackName} by ${basedOnArtistName}"
                                                        && textView2.text != "Based on: ${basedOnTrackName} by ${basedOnArtistName}"
                                                        && textView3.text != "Based on: ${basedOnTrackName} by ${basedOnArtistName}"
                                                    ) {
                                                        textView.text =
                                                            "Based on: ${basedOnTrackName} by ${basedOnArtistName}"
                                                        recyclerView.layoutManager =
                                                            LinearLayoutManager(
                                                                context,
                                                                LinearLayoutManager.HORIZONTAL,
                                                                false
                                                            )

                                                        val tracksForAdapter: ArrayList<TrackRecommendationData> =
                                                            ArrayList()

                                                        for (tr in tracks) {
                                                            Log.d(
                                                                "Success spotify recom",
                                                                "${tr.name} -- ${
                                                                    tr.artists.joinToString(
                                                                        ","
                                                                    )
                                                                }"
                                                            )
                                                            val track = TrackRecommendationData(
                                                                recommendedTrackId = tr.id,
                                                                recommendedTrackImage = tr.album.images[0].url,
                                                                recommendedTrackName = tr.name,
                                                                recommendedTrackArtist = tr.artists[0].name,
                                                                recommendedTrackDuration = "",
                                                                recommendedTrackLink = ""
                                                            )
                                                            tracksForAdapter.add(track)
                                                        }

                                                        recyclerView.adapter =
                                                            TrackRecommendationAdapter(
                                                                tracksForAdapter
                                                            )
                                                        val adapter =
                                                            recyclerView.adapter as TrackRecommendationAdapter

                                                        //update recycler viewer
                                                        for (track in adapter.trackList) {
                                                            getTrackInfoAdapter(basedOnTrackName,
                                                                basedOnArtistName,
                                                                track.recommendedTrackName,
                                                                track.recommendedTrackArtist,
                                                                requireContext(),
                                                                adapter,
                                                                object :
                                                                    SpotifyTrackInfoWithAdapterCallback {
                                                                    override fun onSuccess(
                                                                        adapter: TrackRecommendationAdapter,
                                                                        track: TrackSearch
                                                                    ) {
                                                                        for (tr in adapter.trackList) {
                                                                            if (tr.recommendedTrackId == track.id) {
                                                                                val i =
                                                                                    adapter.trackList.indexOf(
                                                                                        tr
                                                                                    )
                                                                                adapter.trackList.get(
                                                                                    i
                                                                                ).recommendedTrackName =
                                                                                    track.name
                                                                                adapter.trackList.get(
                                                                                    i
                                                                                ).recommendedTrackArtist =
                                                                                    track.artists[0].name
                                                                                adapter.trackList.get(
                                                                                    i
                                                                                ).recommendedTrackImage =
                                                                                    track.album.images[0].url
                                                                                adapter.trackList.get(
                                                                                    i
                                                                                ).recommendedTrackDuration =
                                                                                    track.album.release_date
                                                                                adapter.trackList.get(
                                                                                    i
                                                                                ).recommendedTrackLink =
                                                                                    track.external_urls.spotify
                                                                                adapter.notifyItemChanged(
                                                                                    i
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    override fun onFailure(error: Throwable) {
                                                                        Log.d(
                                                                            "Updating adapter",
                                                                            "FAIL"
                                                                        )
                                                                    }

                                                                })
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onFailure(error: Throwable) {
                                                Log.d("Success spotify recom", "FAIL")
                                            }

                                        })
                                }
                            }


                            override fun onFaliure(error: Throwable) {
                                Log.d("Saved Tracks", error.message.toString())
                            }
                        })
                }
            }

            override fun onFailure(error: Throwable) {
                Log.d("Saved Tracks", "ERROR")
            }
        }
        )


    }

}


private interface SavedTracksCallback {
    fun onSuccess(tracks: List<SpotifyShowItem>)
    fun onFailure(error: Throwable)
}

private interface SimilarTracksCallback {
    fun onSuccess(basedOnTrackName: String, basedOnArtistName: String, tracks: MutableList<Track>)
    fun onFaliure(error: Throwable)
}

private interface SpotifyRecommendationsCallback {
    fun onSuccess(
        basedOnTrackName: String,
        basedOnArtistName: String,
        tracks: List<TrackRecommendation>
    )

    fun onFailure(error: Throwable)
}

private interface SpotifyTrackInfoCallback {
    fun onSuccess(basedOnTrackName: String, basedOnArtistName: String, track: TrackSearch)
    fun onFailure(error: Throwable)
}

private interface SpotifyTrackInfoWithAdapterCallback{
    fun onSuccess(adapter: TrackRecommendationAdapter, track: TrackSearch)
    fun onFailure(error: Throwable)
}

private fun getSpotifyRecommendations(
    basedOnTrackName: String,
    basedOnArtistName: String,
    track: Track,
    context: Context,
    callback: SpotifyRecommendationsCallback
) {
    val sharedPref = context.getSharedPreferences(
        "tokenSharedPreferences",
        Context.MODE_PRIVATE
    )
    var accessToken = sharedPref.getString("accessToken", null)

    val BASE_AUTH_URL = "https://accounts.spotify.com/"
    val BASE_URL = "https://api.spotify.com"

    //check if accessTokenExpired, refresh token accordingly
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

    //get recommended tracks
    val spotifyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyApi::class.java)

    //Get Spotify Track info ID!!!!
    getTrackInfo(
        basedOnTrackName,
        basedOnArtistName,
        track.name,
        track.artist.name,
        context,
        object : SpotifyTrackInfoCallback {
            override fun onSuccess(
                basedOnTrackName: String,
                basedOnArtistName: String,
                track: TrackSearch
            ) {
                Log.d("SIMILAR TRACK INFO", "--------------------------------------")
                Log.d("SIMILAR TRACK INFO", "$basedOnTrackName -- $basedOnArtistName")
                Log.d("SIMILAR TRACK INFO", "${track.id}")
                Log.d("SIMILAR TRACK INFO", "${track.artists[0].id}")

                val spotifyRecommendations: Call<SpotifyRecommendationsResponse> =
                    spotifyApi.getSpotifyRecommendations(
                        accessToken = "Bearer " + accessToken.toString(),
                        artistIds = track.artists[0].id,
                        seedGenres = "",
                        trackIds = track.id
                    )

                spotifyRecommendations.enqueue(object : Callback<SpotifyRecommendationsResponse> {
                    override fun onResponse(
                        call: Call<SpotifyRecommendationsResponse>,
                        response: Response<SpotifyRecommendationsResponse>
                    ) {
                        val tracks: MutableList<TrackRecommendation> = mutableListOf()
                        Log.d("Spotify Recomm", "-------------------------------------------")
                        Log.d("Spotify Recomm", basedOnTrackName + " -- " + basedOnArtistName)
                        Log.d("Spotify Recomm", response.body().toString())
                        try {
                            callback.onSuccess(
                                basedOnTrackName,
                                basedOnArtistName,
                                response.body()!!.tracks
                            )
                        } catch (e: java.lang.Exception) {
                            Log.d("Spotify Recomm", "FAIL")
                        }

                    }

                    override fun onFailure(
                        call: Call<SpotifyRecommendationsResponse>,
                        t: Throwable
                    ) {
                        Log.d("Spotify Recomm", "FAIL")
                    }

                })
            }

            override fun onFailure(error: Throwable) {
                Log.d("SIMILAR TRACK INFO", "FAIL 2")
            }

        })

}

private fun getTrackInfo(
    basedOnTrackName: String,
    basedOnArtistName: String,
    trackName: String,
    artistName: String,
    context: Context,
    callback: SpotifyTrackInfoCallback
) {
    val sharedPref = context.getSharedPreferences(
        "tokenSharedPreferences",
        Context.MODE_PRIVATE
    )
    var accessToken = sharedPref.getString("accessToken", null)

    val BASE_AUTH_URL = "https://accounts.spotify.com/"
    val BASE_URL = "https://api.spotify.com"

    //check if accessTokenExpired, refresh token accordingly
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
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyApi::class.java)

    val trackInfoResponse: Call<SearchTrackResponse> = spotifyApi.getTrack(
        accessToken = "Bearer " + accessToken,
        searchFilter = "artist:$artistName track:$trackName"
    )

    trackInfoResponse.enqueue(object : Callback<SearchTrackResponse> {
        override fun onResponse(
            call: Call<SearchTrackResponse>,
            response: Response<SearchTrackResponse>
        ) {
            Log.d("SIMILAR TRACK INFO", "SUCCESS")

            try {
                val trackResponseSize = response?.body()?.tracks?.items?.size as Int
                if (trackResponseSize > 0) {
                    val trackInfo = response?.body()?.tracks?.items?.get(0) as TrackSearch
                    Log.d("Sim Track Info", trackInfo.toString())

                    callback.onSuccess(basedOnTrackName, basedOnArtistName, trackInfo)
                }
            } catch (e: java.lang.Exception) {
                Log.d("Sim Track Info", "ERROR")
            }


        }

        override fun onFailure(call: Call<SearchTrackResponse>, t: Throwable) {
            Log.d("SIMILAR TRACK INFO", "FAIL")
            callback.onFailure(t)
        }

    })

}

private fun getTrackInfoAdapter(
    basedOnTrackName: String,
    basedOnArtistName: String,
    trackName: String,
    artistName: String,
    context: Context,
    adapter: TrackRecommendationAdapter,
    callback: SpotifyTrackInfoWithAdapterCallback
) {
    val sharedPref = context.getSharedPreferences(
        "tokenSharedPreferences",
        Context.MODE_PRIVATE
    )
    var accessToken = sharedPref.getString("accessToken", null)

    val BASE_AUTH_URL = "https://accounts.spotify.com/"
    val BASE_URL = "https://api.spotify.com"

    //check if accessTokenExpired, refresh token accordingly
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
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyApi::class.java)

    val trackInfoResponse: Call<SearchTrackResponse> = spotifyApi.getTrack(
        accessToken = "Bearer " + accessToken,
        searchFilter = "artist:$artistName track:$trackName"
    )

    trackInfoResponse.enqueue(object : Callback<SearchTrackResponse> {
        override fun onResponse(
            call: Call<SearchTrackResponse>,
            response: Response<SearchTrackResponse>
        ) {
            Log.d("SIMILAR TRACK INFO", "SUCCESS")

            try {
                val trackResponseSize = response?.body()?.tracks?.items?.size as Int
                if (trackResponseSize > 0) {
                    val trackInfo = response?.body()?.tracks?.items?.get(0) as TrackSearch
                    Log.d("Sim Track Info", trackInfo.toString())

                    callback.onSuccess(adapter, trackInfo)
                }
            } catch (e: java.lang.Exception) {
                Log.d("Sim Track Info", "ERROR")
            }


        }

        override fun onFailure(call: Call<SearchTrackResponse>, t: Throwable) {
            Log.d("SIMILAR TRACK INFO", "FAIL")
            callback.onFailure(t)
        }

    })

}

private fun getSavedTracksForUser(context: Context, callback: SavedTracksCallback) {
    val sharedPref = context.applicationContext.getSharedPreferences(
        "tokenSharedPreferences",
        Context.MODE_PRIVATE
    )
    var accessToken = sharedPref.getString("accessToken", null)

    val BASE_AUTH_URL = "https://accounts.spotify.com/"
    val BASE_URL = "https://api.spotify.com"

    //check if accessTokenExpired, refresh token accordingly
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
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyApi::class.java)

    val savedTracksResponse: Call<SavedTracksResponse> = spotifyApi.getSavedTracks(
        accessToken = "Bearer " + accessToken.toString(),
        limit = 20,
        offset = 0
    )

    val tracksForRecomenadation: MutableList<Pair<String, String>> = mutableListOf()
    savedTracksResponse.enqueue(object : Callback<SavedTracksResponse> {
        override fun onResponse(
            call: Call<SavedTracksResponse>,
            response: Response<SavedTracksResponse>
        ) {
            Log.d("Saved Tracks", "CALLED SUCCESS")
            val tracks = response.body()?.items as List<SpotifyShowItem>

            callback.onSuccess(tracks)

        }

        override fun onFailure(call: Call<SavedTracksResponse>, t: Throwable) {
            Log.d("Saved Tracks", "CALLED FAIL")
            Log.d("Saved Tracks", t.message.toString())

            callback.onFailure(t)
        }
    })

}


private fun getSimilarTracks(
    trackName: String,
    artistName: String,
    callback: SimilarTracksCallback
) {

    val BASE_URL = "https://ws.audioscrobbler.com/"
    val lastFmApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build().create(LastFmApi::class.java)

    val similarTracksResponse: Call<SimilarTracksResponse> = lastFmApi.getSimilarTracks(
        trackName = trackName,
        artistName = artistName,
        numberOfTracks = "5"
    )

    similarTracksResponse.enqueue(object : Callback<SimilarTracksResponse> {
        override fun onResponse(
            call: Call<SimilarTracksResponse>,
            response: Response<SimilarTracksResponse>
        ) {
            val similarTracks: List<Track> =
                response.body()?.similartracks?.track as List<Track>

            val tracksToShow: MutableList<Track> = mutableListOf()
            for (track in similarTracks) {
                tracksToShow.add(track)
            }

            callback.onSuccess(trackName, artistName, tracksToShow)
        }

        override fun onFailure(call: Call<SimilarTracksResponse>, t: Throwable) {
            callback.onFaliure(t)

        }
    })

}

