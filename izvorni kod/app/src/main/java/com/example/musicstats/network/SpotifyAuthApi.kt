package com.example.musicstats.network

import android.util.Base64
import android.util.Log
import com.example.musicstats.datamodels.AccessToken
import com.example.musicstats.datamodels.RefreshToken
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

private var BASE_URL = "https://accounts.spotify.com/"
private var clientId = "d776bb40f3ef46acbb287a8b994232db"
private var clientSecret = "bc2ad6df766c4aa5963ff2723ee850a4"
private var redirectUri = "musicstats://callback"
private var grantType = "authorization_code"

private val encodedIdAndSecret = encode(clientId+":"+ clientSecret)




private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private fun encode(str: String): String{
    Log.d("API Response", "Encoded: ${Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)}")
    return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
}

interface SpotifyAuthApi {

    @FormUrlEncoded
    @POST("/api/token")
    fun getAccessToken(
        @Header("Authorization") authorization: String = "Basic $encodedIdAndSecret",
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded",
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Query("scope") scope: String
    ): Call<AccessToken>

    @FormUrlEncoded
    @POST("/api/token")
    fun getRefreshToken(
        @Header("Authorization") authorization: String = "Basic $encodedIdAndSecret",
        @Header("Content-type") contentType: String = "application/x-www-form-urlencoded",
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String
    ): Call<RefreshToken>

}

