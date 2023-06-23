package com.example.musicstats.network

import com.example.musicstats.datamodels.SimilarTracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//private var BASE_URL = "http://ws.audioscrobbler.com/2.0"
private var LastFmApiKey = "54871728bc5d544cf83e01edbcfbd8dd"

interface LastFmApi {


    /**
     *Performs a GET request to retrieve a list of similar tracks to the given track and artist names.
     *@param method The method name to call in the Last.fm API. Default is "track.getsimilar".
     *@param format The format of the response from the Last.fm API. Default is "json".
     *@param apiKey The API key to use for authentication with the Last.fm API. Default is the LastFmApiKey constant.
     *@param trackName The name of the track for which similar tracks will be retrieved.
     *@param artistName The name of the artist for which similar tracks will be retrieved.
     *@param autoCorrect Flag to enable auto-correction of errors in the track and artist names. Default is "1".
     */
    @GET("/2.0/")
    fun getSimilarTracks(
        @Query("method") method: String = "track.getsimilar",
        @Query("artist") artistName: String,
        @Query("track") trackName: String,
        @Query("format") format: String = "json",
        @Query("api_key") apiKey: String = LastFmApiKey,
        @Query("autocorrect") autoCorrect: String = "1",
        @Query("limit") numberOfTracks: String = "10"
    ): Call<SimilarTracksResponse>

}