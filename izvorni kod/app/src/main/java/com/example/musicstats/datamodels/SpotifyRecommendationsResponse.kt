package com.example.musicstats.datamodels

data class SpotifyRecommendationsResponse(
    val seeds: List<Seed>,
    val tracks: List<TrackRecommendation>
)

data class Seed(
    val afterFilteringSize: Int,
    val afterRelinkingSize: Int,
    val href: String?,
    val id: String,
    val initialPoolSize: Int,
    val type: String
)

data class TrackRecommendation(
    val album: AlbumRecommendation,
    val artists: List<ArtistRecommendation>,
    val availableMarkets: List<String>,
    val discNumber: Int,
    val durationMs: Int,
    val explicit: Boolean,
    val externalIds: Map<String, String>,
    val externalUrls: ExternalUrlsRecommendation,
    val href: String,
    val id: String,
    val isLocal: Boolean,
    val name: String,
    val popularity: Int,
    val previewUrl: String?,
    val trackNumber: Int,
    val type: String,
    val uri: String
)

data class ExternalUrlsRecommendation(
    val spotify: String
)

data class AlbumRecommendation(
    val albumType: String,
    val artists: List<ArtistRecommendation>,
    val availableMarkets: List<String>,
    val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    val images: List<ImageRecommendation>,
    val name: String,
    val releaseDate: String,
    val releaseDatePrecision: String,
    val totalTracks: Int,
    val type: String,
    val uri: String
)

data class ArtistRecommendation(
    val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

data class ImageRecommendation(
    val height: Int,
    val url: String,
    val width: Int
)
