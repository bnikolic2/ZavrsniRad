package com.example.musicstats.datamodels

data class SimilarTracksResponse(
    val similartracks: SimilarTracks
)

data class SimilarTracks(
    val track: List<Track>
)

data class Track(
    val name: String,
    val playcount: Int,
    val mbid: String,
    val match: Double,
    val url: String,
    val streamable: Streamable,
    val duration: Int,
    val artist: ArtistLastFm,
    val image: List<ImageLastFm>
)

data class Streamable(
    val text: String,
    val fulltrack: String
)

data class ArtistLastFm(
    val name: String,
    val mbid: String,
    val url: String
)

data class ImageLastFm(
    val text: String,
    val size: String
)

