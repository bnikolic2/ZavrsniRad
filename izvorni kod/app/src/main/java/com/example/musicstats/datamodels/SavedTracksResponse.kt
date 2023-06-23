package com.example.musicstats.datamodels

data class SavedTracksResponse(
    val href: String,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int,
    val items: List<SpotifyShowItem>
)

data class SpotifyShowItem(
    val added_at: String,
    val track: SpotifySavedTrack
)

data class SpotifySavedTrack(
    val album: SpotifyTrackAlbum,
    val artists: List<SpotifySavedArtist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_ids: SpotifyExternalIds,
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val is_playable: Boolean,
    val linked_from: SpotifyLinkedFrom?,
    val restrictions: SpotifyRestrictions?,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String,
    val is_local: Boolean
)

data class SpotifyTrackAlbum(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val images: List<SpotifySavedImage>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val restrictions: SpotifyRestrictions?,
    val type: String,
    val uri: String,
    val copyrights: List<SpotifyCopyright>,
    val external_ids: SpotifyExternalIds,
    val genres: List<String>,
    val label: String,
    val popularity: Int,
    val album_group: String,
    val artists: List<SpotifySavedArtist>
)

data class SpotifySavedArtist(
    val external_urls: SpotifyExternalUrls,
    val followers: SpotifyFollowers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<SpotifySavedImage>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

data class SpotifyExternalIds(
    val isrc: String?,
    val ean: String?,
    val upc: String?
)

data class SpotifyExternalUrls(
    val spotify: String
)

data class SpotifySavedImage(
    val url: String,
    val height: Int,
    val width: Int
)

data class SpotifyRestrictions(
    val reason: String
)

data class SpotifyLinkedFrom(
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)

data class SpotifyFollowers(
    val href: String?,
    val total: Int
)

data class SpotifyCopyright(
    val text: String,
    val type: String
)

