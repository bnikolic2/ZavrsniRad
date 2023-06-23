package com.example.musicstats.network

import com.example.musicstats.datamodels.*
import retrofit2.Call
import retrofit2.http.*

    private var BASE_URL = "https://api.spotify.com"

interface SpotifyApi {

    /**
     * Retrieves the user's top tracks for a specified time range and type.
     *
     * @param accessToken the user's Spotify access token, in the format "Bearer <AccessToken>"
     * @param type the type of the user's top tracks ("artists" or "tracks")
     * @param timeRange the time range of the user's top tracks ("short_term", "medium_term", or "long_term")
     * @param limit the maximum number of items to return (1 to 50)
     * @param offset the index of the first item to return (default is 0)
     */
    @GET("/v1/me/top/{type}")
    fun getTracks  (
        @Header("Authorization") accessToken: String,
        @Path("type") type: String = "tracks",
        @Query("time_range") timeRange: String = "short_term",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Call<SpotifyTrackModel>


    /**
     *Retrieves the user's top artists from Spotify based on specified criteria.
     * @param accessToken the authorization token used to authenticate the request
     * @param type the type of entity to retrieve, defaults to "artists"
     * @param timeRange the time range to consider for the top artists, defaults to "short_term", can be "medium_term", "long_term"
     * @param limit the maximum number of results to return, defaults to 50
     * @param offset the index of the first result to return, defaults to 0
     */
    @GET("/v1/me/top/{type}")
    fun getArtists  (
        @Header("Authorization") accessToken: String,
        @Path("type") type: String = "artists",
        @Query("time_range") timeRange: String = "short_term",
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Call<SpotifyArtistModel>

    /**
     * Get a list of the songs saved in the current Spotify user's 'Your Music' library.
     * @param accessToken the authorization token used to authenticate the request
     * @param limit The maximum number of items to return. Default: 20. Minimum: 1. Maximum: 50.
     * @param offset The index of the first item to return. Default: 0 (the first item). Use with limit to get the next set of items.
     */
    @GET("/v1/me/tracks")
    fun getSavedTracks(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Call<SavedTracksResponse>

    /**
     * Get Spotify catalog information about albums, artists, playlists, tracks, shows, episodes or audiobooks that match a keyword string
     * @param q Your search query. You can narrow down your search using field filters. The available filters are album, artist, track, year, upc, tag:hipster, tag:new, isrc, and genre. Each field filter only applies to certain result types.
     * @param type A comma-separated list of item types to search across. Search results include hits from all the specified item types. For example: q=abacab&type=album,track returns both albums and tracks matching "abacab".
     * @param limit The maximum number of results to return in each item type. (range 0-50)
     * @param offset The index of the first result to return. Use with limit to get the next page of search results. (range 0-1000)
    */
    @GET("/v1/search")
    fun getTrack(
        @Header("Authorization") accessToken: String,
        @Query("q") searchFilter: String,
        @Query("type") type:String = "track",
        @Query("limit") limit:Int=1,
        @Query("offset") offset: Int=0
    ): Call<SearchTrackResponse>

    /**
     * Get Spotify catalog information about albums, artists, playlists, tracks, shows, episodes or audiobooks that match a keyword string
     * @param id Id of the track.
     * @param type A comma-separated list of item types to search across. Search results include hits from all the specified item types. For example: q=abacab&type=album,track returns both albums and tracks matching "abacab".
     * @param limit The maximum number of results to return in each item type. (range 0-50)
     * @param offset The index of the first result to return. Use with limit to get the next page of search results. (range 0-1000)
     */
    @GET("/v1/search")
    fun getTrackById(
        @Header("Authorization") accessToken: String,
        @Query("id") id: String,
        @Query("type") type:String = "track",
        @Query("limit") limit:Int=1,
        @Query("offset") offset: Int=0
    ): Call<SearchTrackResponse>

    /** Get spotify recommendations for given information
     * @param limit Limit on returned tracks: 1-100
     * @param market Market on which we search the tracks
     * @param artistIds Comma separated list of Spotify IDs for seed artists. Up to 5 seed values
     * @param seedGenres Comma separated list of any genres in the set of available genre seeds. Up to 5 seed values
     * @param trackIds Comma separated list of Spoitfy IDs for a seed track. Up to 5 seed values
     */
    @GET("/v1/recommendations")
    fun getSpotifyRecommendations(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int = 10,
        @Query("market") market: String =  "US",
        @Query("seed_artists") artistIds: String,
        @Query("seed_genres") seedGenres: String,
        @Query("seed_tracks") trackIds: String
    ): Call<SpotifyRecommendationsResponse>

}