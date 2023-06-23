package com.example.musicstats.network

import com.example.musicstats.datamodels.ShazamDetectResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

private var BASE_URL = "https://shazam.p.rapidapi.com"
private var RAPID_API_KEY = "107945d118mshf6df34d3b35a1ffp160dbdjsn6ca51a72b15b"
private var RAPID_API_HOST = "shazam.p.rapidapi.com"

interface ShazamApi {

    /**
     * Trying to find the track with given audio input
     *
     * @param data base64string of byte[] that generated from raw data
     *
     */

    @POST("/songs/v2/detect")
    fun detectAudio(
        @Header("content-type") contentType: String = "text/plain",
        @Header("X-RapidAPI-Key") apiKey: String = RAPID_API_KEY,
        @Header("X-RapidAPI-Host") host: String = RAPID_API_HOST,
        @Body data: RequestBody
    ):Call<ShazamDetectResponse>
}