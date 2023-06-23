package com.example.musicstats.adapters.trackRecommendationAdapter

data class TrackRecommendationData(
    val recommendedTrackId: String?,
    var recommendedTrackImage: String,
    var recommendedTrackName: String,
    var recommendedTrackArtist : String,
    var recommendedTrackDuration: String,
    var recommendedTrackLink: String
)
