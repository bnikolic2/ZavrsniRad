package com.example.musicstats.datamodels

import com.google.gson.annotations.SerializedName

data class SpotifyTrackModel(
    @SerializedName("items")
        val items: List<SpotifyTrack>,
    @SerializedName("total")
        val total: Int,
    @SerializedName("limit")
        val limit: Int,
    @SerializedName("offset")
        val offset: Int,
    @SerializedName("previous")
        val previous: String?,
    @SerializedName("next")
        val next: String?
)

data class SpotifyTrack(
    @SerializedName("id")
        val id: String,
    @SerializedName("name")
        val name: String,
    @SerializedName("album")
        val album: SpotifyAlbum,
    @SerializedName("artists")
        val artists: List<SpotifyArtist>
)

data class SpotifyAlbum(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("images")
        val images: List<SpotifyImage>
)

data class SpotifyArtist(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
)

data class SpotifyImage(
        @SerializedName("url")
        val url: String,
        @SerializedName("width")
        val width: Int?,
        @SerializedName("height")
        val height: Int?
)