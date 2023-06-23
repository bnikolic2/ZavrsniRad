package com.example.musicstats.datamodels

data class SearchTrackResponse(
    val tracks: Tracks
)

data class Tracks(
    val href: String,
    val items: List<TrackSearch>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)

data class TrackSearch(
    val album: Album,
    val artists: List<ArtistSearch>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrlsSearch,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String
)

data class Album(
    val album_type: String,
    val artists: List<ArtistSearch>,
    val available_markets: List<String>,
    val external_urls: ExternalUrlsSearch,
    val href: String,
    val id: String,
    val images: List<ImageSearch>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val type: String,
    val uri: String
)

data class ArtistSearch(
    val external_urls: ExternalUrlsSearch,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

data class ExternalIds(
    val isrc: String
)

data class ExternalUrlsSearch(
    val spotify: String
)

data class ImageSearch(
    val height: Int?,
    val url: String,
    val width: Int?
)

